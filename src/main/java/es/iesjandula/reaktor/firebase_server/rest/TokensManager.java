package es.iesjandula.reaktor.firebase_server.rest;

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
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

import es.iesjandula.reaktor.base.utils.BaseConstants;
import es.iesjandula.reaktor.firebase_server.models.Aplicacion;
import es.iesjandula.reaktor.firebase_server.models.Usuario;
import es.iesjandula.reaktor.firebase_server.repository.IAplicacionRepository;
import es.iesjandula.reaktor.firebase_server.repository.IUsuarioRepository;
import es.iesjandula.reaktor.firebase_server.utils.Constants;
import es.iesjandula.reaktor.firebase_server.utils.FirebaseServerException;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/firebase/token")
public class TokensManager
{
	/**
	 * Logger of the class
	 */
	private static final Logger log = LoggerFactory.getLogger(TokensManager.class);

	@Value("${reaktor.privateKeyFile}")
	private String privateKeyFile ;
	
	@Autowired
	private IUsuarioRepository usuarioRepository ;
	
	@Autowired
	private IAplicacionRepository aplicacionRepository ;
	
    @RequestMapping(method = RequestMethod.POST, value = "/user")
    public ResponseEntity<?> obtenerTokenPersonalizadoUsuario(@RequestHeader("Authorization") String authorizationHeader)
    {
        try
        {
		    // Eliminamos el prefijo "Bearer " del encabezado de autorización para obtener el token JWT limpio
		    String token = authorizationHeader.replace("Bearer ", "") ;
			
			// Verificamos el token con Firebase
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token) ;
        	
            // Verificamos si el decode token existe en Firebase Authentication
            if (decodedToken == null)
            {
                String errorString = "El Token " + decodedToken + " no existe en Firebase" ;
                
                log.error(errorString, errorString) ;
        		throw new FirebaseServerException(Constants.ERR_USER_AUTHORIZATION, errorString) ;
            }
            
            Optional<Usuario> optionalUsuario = this.usuarioRepository.findById(decodedToken.getEmail()) ;

            // Comprobamos que el usuario existe
            if (!optionalUsuario.isPresent())
            {
    			String errorString = "El usuario " + decodedToken.getEmail() + " no está dado de alta. " +
    								 "¿Estás seguro que lo hiciste con el dominio g.educaand.es o hablaste con el TDE?" ;
    			
    			log.error(errorString) ;
    			throw new FirebaseServerException(Constants.ERR_USER_AUTHORIZATION, errorString) ;  
            }

            // Obtenemos la información del usuario
            Usuario usuario = optionalUsuario.get() ;

            // Creamos claims personalizados basados en la información obtenida
            Map<String, Object> customClaims = new HashMap<String, Object>() ;
            
            customClaims.put(BaseConstants.JWT_ATTR_USUARIOS_ATTRIBUTE_EMAIL, 	         usuario.getEmail()) ;
            customClaims.put(BaseConstants.JWT_ATTR_USUARIOS_ATTRIBUTE_NOMBRE, 	         usuario.getNombre()) ;
            customClaims.put(BaseConstants.JWT_ATTR_USUARIOS_ATTRIBUTE_APELLIDOS,        usuario.getApellidos()) ;
            customClaims.put(BaseConstants.JWT_ATTR_USUARIOS_ATTRIBUTE_DEPARTAMENTO,     usuario.getDepartamento()) ;
			customClaims.put(BaseConstants.JWT_ATTR_USUARIOS_ATTRIBUTE_FECHA_NACIMIENTO, usuario.getFechaNacimiento()) ;
            customClaims.put(BaseConstants.JWT_ATTR_USUARIOS_ATTRIBUTE_ROLES, 	         usuario.getRolesList()) ;

            // Firmamos el JWT con la clave privada
            String tokenJwt = Jwts.builder().subject(decodedToken.getUid())
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
 												Constants.ERR_GENERIC_EXCEPTION_MSG + "obtenerTokenPersonalizadoUsuario",
 												exception) ;
	        
			log.error(Constants.ERR_GENERIC_EXCEPTION_MSG + "obtenerTokenPersonalizadoUsuario", firebaseServerException) ;
			return ResponseEntity.status(500).body(firebaseServerException.getBodyExceptionMessage()) ;
	    }
    }
    
    @RequestMapping(method = RequestMethod.POST, value = "/app")
    public ResponseEntity<?> obtenerTokenPersonalizadoAplicacion(@RequestHeader("X-CLIENT-ID") String clientId)
    {
        try
        {
            // Buscamos la aplicación por clientId
            Optional<Aplicacion> optionalAplicacion = this.aplicacionRepository.findById(clientId) ;
            
            // Comprobamos que la aplicación existe
            if (!optionalAplicacion.isPresent())
            {
                String errorString = "La aplicación con " + clientId + " no existe en la BBDD" ;
                
                log.error(errorString, errorString) ;
        		throw new FirebaseServerException(Constants.ERR_APPS_AUTHORIZATION, errorString) ;
            }
            
            // Obtenemos la información de la aplicación
            Aplicacion aplicacion = optionalAplicacion.get() ;

            // Creamos claims personalizados basados en la información obtenida
            Map<String, Object> customClaims = new HashMap<String, Object>() ;
            
            customClaims.put(BaseConstants.JWT_ATTR_APLICACIONES_ATTRIBUTE_NOMBRE, aplicacion.getNombre()) ;
            customClaims.put(BaseConstants.JWT_ATTR_APLICACIONES_ATTRIBUTE_ROLES, aplicacion.getRolesList()) ;

            // Firmamos el JWT con la clave privada
            String tokenJwt = Jwts.builder().subject(clientId)
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
 												Constants.ERR_GENERIC_EXCEPTION_MSG + "obtenerTokenPersonalizadoAplicacion",
 												exception) ;
	        
			log.error(Constants.ERR_GENERIC_EXCEPTION_MSG + "obtenerTokenPersonalizadoAplicacion", firebaseServerException) ;
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

