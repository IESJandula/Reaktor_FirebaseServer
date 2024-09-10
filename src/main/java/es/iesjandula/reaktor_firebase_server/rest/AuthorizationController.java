package es.iesjandula.reaktor_firebase_server.rest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;

import es.iesjandula.reaktor_firebase_server.utils.Constants;
import es.iesjandula.reaktor_firebase_server.utils.FirebaseServerException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/firebase")
public class AuthorizationController
{
	@Value("${reaktor.privateKeyFile}")
	private String privateKeyFile ;
	
    @RequestMapping(method = RequestMethod.POST, value = "/getCustomToken")
    public ResponseEntity<?> obtenerTokenPersonalizado(@RequestHeader(name = "uid") String uid)
    {
        try
        {
            // Verificamos si el UID existe en Firebase Authentication
            UserRecord userRecord = FirebaseAuth.getInstance().getUser(uid) ;
            if (userRecord == null)
            {
                String errorString = "El UID " + uid + " no existe en Firebase" ;
                
                log.error(errorString, errorString) ;
        		throw new FirebaseServerException(Constants.ERR_UID_USER_NOT_EXISTS_IN_FIREBASE, errorString) ;
            }

            // Obtenemos datos adicionales del usuario desde Firestore
            Firestore firestore = FirestoreClient.getFirestore() ;
            DocumentSnapshot document = firestore.collection(Constants.COLLECTION_NAME_USUARIOS).document(uid).get().get();

            // Comprobamos que el fichero existe
            if (!document.exists())
            {
                String errorString = "El documento del usuario con " + uid + " no existe en la colección de Firebase" ;
                
                log.error(errorString, errorString) ;
        		throw new FirebaseServerException(Constants.ERR_USER_NOT_EXISTS_IN_COLLECTION, errorString) ;
            }

            // Obtenemos la información de cada campo en el mapa
            Map<String, Object> userData = document.getData() ;

            // Creamos claims personalizados basados en la información obtenida
            Map<String, Object> customClaims = new HashMap<String, Object>() ;
            
            customClaims.put(Constants.COLLECTION_USUARIOS_ATTRIBUTE_EMAIL, 	userData.get(Constants.COLLECTION_USUARIOS_ATTRIBUTE_EMAIL));
            customClaims.put(Constants.COLLECTION_USUARIOS_ATTRIBUTE_NOMBRE, 	userData.get(Constants.COLLECTION_USUARIOS_ATTRIBUTE_NOMBRE));
            customClaims.put(Constants.COLLECTION_USUARIOS_ATTRIBUTE_APELLIDOS, userData.get(Constants.COLLECTION_USUARIOS_ATTRIBUTE_APELLIDOS));
            customClaims.put(Constants.COLLECTION_USUARIOS_ATTRIBUTE_ROLES, 	userData.get(Constants.COLLECTION_USUARIOS_ATTRIBUTE_ROLES));

            // Firmamos el JWT con la clave privada
            String tokenJwt = Jwts.builder().subject(uid)
						                    .claims(customClaims)
						                    .signWith(this.obtenerClavePrivada(), Jwts.SIG.RS256)
						                    .compact() ;

	        // Devolvemos el resultado
	        return ResponseEntity.ok().body(tokenJwt) ;

        }
        catch (FirebaseServerException firebaseServerServerException)
        {
			return ResponseEntity.status(500).body(firebaseServerServerException.getBodyExceptionMessage()) ;
        }
	    catch (Exception exception) 
	    {
	    	FirebaseServerException firebaseServerException = 
	    			new FirebaseServerException(Constants.ERR_GENERIC_EXCEPTION_CODE, 
 												Constants.ERR_GENERIC_EXCEPTION_MSG + "obtenerTokenPersonalizado",
 												exception) ;
	        
			log.error(Constants.ERR_GENERIC_EXCEPTION_MSG + "obtenerTokenPersonalizado", firebaseServerException) ;
			return ResponseEntity.status(500).body(firebaseServerException.getBodyExceptionMessage()) ;
	    }
    }
    
	/**
	 * @return clave privada de nuestra aplicación
	 * @throws FirebaseServerException con un error
	 */
    private PrivateKey obtenerClavePrivada() throws FirebaseServerException
    {
		try
		{
			// Obtenemos el contenido del fichero
			String privateKeyContent = new String(Files.readAllBytes(Paths.get(this.privateKeyFile))) ;
			
	        // Reemplazamos los saltos de líneas por nada, quitamos el BEGIN y END
	        privateKeyContent = privateKeyContent.replaceAll("\\n", "")
	        									 .replaceAll("\\r", "")
								                 .replace("-----BEGIN PRIVATE KEY-----", "")
								                 .replace("-----END PRIVATE KEY-----", "") ;
	        
		     // Crea una instancia de KeyFactory para el algoritmo RSA
		     // KeyFactory proporciona métodos para convertir claves de una representación a otra
		     KeyFactory keyFactory = KeyFactory.getInstance("RSA") ;
		
		     // Decodifica el contenido de la clave privada desde Base64 a su forma binaria original
		     // PKCS8EncodedKeySpec representa la especificación de una clave privada codificada en formato PKCS #8
		     PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyContent)) ;
		
		     // Genera una instancia de PrivateKey a partir de la especificación de clave privada (keySpec)
		     // Utiliza el KeyFactory configurado para el algoritmo RSA para convertir la especificación a una clave privada real
		     return keyFactory.generatePrivate(keySpec) ;
		} 
		catch (IOException ioException)
		{
			String errorString = "IOException mientras se cargaba el fichero con la clave privada" ;
			
			log.error(errorString, ioException) ;
			throw new FirebaseServerException(Constants.ERR_GETTING_PRIVATE_KEY, errorString, ioException) ;
		}
		catch (InvalidKeySpecException invalidKeySpecException)
		{
			String errorString = "InvalidKeySpecException mientras se cargaba el fichero con la clave privada" ;
			
			log.error(errorString, invalidKeySpecException) ;
			throw new FirebaseServerException(Constants.ERR_GETTING_PRIVATE_KEY, errorString, invalidKeySpecException) ;
		}
		catch (NoSuchAlgorithmException noSuchAlgorithmException)
		{
			String errorString = "NoSuchAlgorithmException mientras se cargaba el fichero con la clave privada" ;
			
			log.error(errorString, noSuchAlgorithmException) ;
			throw new FirebaseServerException(Constants.ERR_GETTING_PRIVATE_KEY, errorString, noSuchAlgorithmException) ;
		}
    }
}

