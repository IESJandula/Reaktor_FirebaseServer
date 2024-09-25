package es.iesjandula.reaktor_firebase_server.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus ;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.cloud.FirestoreClient;

import es.iesjandula.base.base_server.utils.BaseServerConstants;
import es.iesjandula.reaktor_firebase_server.models.Usuario;
import es.iesjandula.reaktor_firebase_server.repository.IUsuarioRepository;
import es.iesjandula.reaktor_firebase_server.utils.Constants;
import es.iesjandula.reaktor_firebase_server.utils.FirebaseServerException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/firebase/users")
public class UsersRest
{
	@Autowired
	private IUsuarioRepository usuarioRepository ;
	
    @PreAuthorize("hasRole('" + BaseServerConstants.ROLE_ADMINISTRADOR + "')")
	@RequestMapping(method = RequestMethod.POST, value = "/import")
    public ResponseEntity<?> importarUsuarios(@RequestParam("file") MultipartFile file)
	{
        try
        {
            // Validamos que el archivo no esté vacío
            if (file.isEmpty())
            {
    	    	String errorString = "El fichero con los usuarios está vacío" ;
    	    	
    	    	log.error(errorString) ;
    	    	throw new FirebaseServerException(Constants.ERR_USERS_IMPORTS, errorString) ;
            }

            // Procesamos el archivo CSV
            this.importarUsuariosProcesarCsv(file) ;
            
            // Borramos todos los documentos de la colección de usuarios
            this.importarUsuariosBorrarDocumentosColeccion() ;

            // Devolvemos el 200
            return ResponseEntity.ok().build() ;
        }
        catch (FirebaseServerException firebaseServerException)
        {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(firebaseServerException.getBodyExceptionMessage()) ;
        }
        catch (Exception exception)
        {
	        FirebaseServerException printersServerException = 
	        		new FirebaseServerException(BaseServerConstants.ERR_GENERIC_EXCEPTION_CODE, 
	        									BaseServerConstants.ERR_GENERIC_EXCEPTION_MSG + "importarUsuarios",
											    exception) ;

			log.error(BaseServerConstants.ERR_GENERIC_EXCEPTION_MSG + "importarUsuarios", printersServerException) ;
			return ResponseEntity.status(500).body(printersServerException.getBodyExceptionMessage()) ;
        }
    }

	/**
	 * @param file multipart file
	 * @throws FirebaseServerException con un error
	 */
	private void importarUsuariosProcesarCsv(MultipartFile file) throws FirebaseServerException
	{
		InputStreamReader inputStreamReader = null ;
		BufferedReader bufferedReader 		= null ;
		
		try
		{
			inputStreamReader = new InputStreamReader(file.getInputStream()) ;
			bufferedReader = new BufferedReader(inputStreamReader) ;
			
			// Nos saltamos la primera línea de cabecera
			String linea = bufferedReader.readLine() ;
			
			// Ahora sí leemos la primera línea
			linea = bufferedReader.readLine() ;
			
			// Iteramos mientras haya líneas
			while (linea != null)
			{
				// Obtenemos la instancia del usuario a partir de la línea procesada
				Usuario usuario = this.importarUsuariosProcesarCsvProcesarLinea(linea) ;
				
				// Guardamos el usuario
				this.usuarioRepository.saveAndFlush(usuario) ;
				
				// Logueamos
				log.info("Usuario insertado: " + usuario) ;
				
				// Ahora sí leemos la primera línea
				linea = bufferedReader.readLine() ;
			}
		}
		catch (IOException ioException)
		{
			String errorString = "Error en la lectura del fichero CSV" ;
			
			log.error(errorString, ioException) ;
			throw new FirebaseServerException(Constants.ERR_USERS_IMPORTS, errorString, ioException) ;
		}
		finally
		{
			this.importarUsuariosProcesarCsvCierreFlujos(inputStreamReader, bufferedReader) ;
		}
	}
	
	/**
	 * Borramos todos los documentos de la colección
	 * @throws FirebaseServerException con un error
	 */
	private void importarUsuariosBorrarDocumentosColeccion() throws FirebaseServerException
	{
		Firestore firestore = FirestoreClient.getFirestore() ;
		
        // Obtenemos la referencia a la colección 'usuarios'
        CollectionReference usuariosCollection = firestore.collection(BaseServerConstants.COLLECTION_NAME_USUARIOS) ;

        // Obtenemos todos los documentos de la colección
        ApiFuture<QuerySnapshot> future = usuariosCollection.get() ;

        try
        {
	        // Obtenemos todos los documentos
	        List<QueryDocumentSnapshot> documents = future.get().getDocuments() ;
	
	        // Borramos uno a uno cada documento
	        for (QueryDocumentSnapshot document : documents)
	        {
	            document.getReference().delete() ;
	        }
	        
	        // Logueamos
	        log.info("Se han borrado todos los elementos de la colección") ;
		}
		catch (ExecutionException executionException)
		{
			String errorString = "ExecutionException cuando se trató de borrar todos los documentos de la colección usuarios" ;
			
			log.error(errorString, executionException) ;
			throw new FirebaseServerException(Constants.ERR_USERS_IMPORTS, errorString, executionException) ;
		}
		catch (InterruptedException interruptedException)
		{
			String errorString = "InterruptedException cuando se trató de borrar todos los documentos de la colección usuarios" ;
			
			log.error(errorString, interruptedException) ;
			throw new FirebaseServerException(Constants.ERR_USERS_IMPORTS, errorString, interruptedException) ;
		}
	}

	/**
	 * @param linea linea a parsear
	 * @return una instancia del usuario con la línea parseada
	 * @throws FirebaseServerException con un error
	 */
	private Usuario importarUsuariosProcesarCsvProcesarLinea(String linea) throws FirebaseServerException
	{
		String[] fields = linea.split(",") ;
		
		if (fields.length != 4)
		{
			String errorString = "Hay una fila del CSV que no posee el número de elementos esperados: " + linea ;
			
			log.error(errorString) ;
			throw new FirebaseServerException(Constants.ERR_USERS_IMPORTS, errorString) ;
		}
		
		// Parseamos cada uno de los campos
		String email 	   = fields[0] ;
		String nombre 	   = fields[1] ;
		String apellidos   = fields[2] ;
		List<String> roles = Arrays.asList(fields[3].split("\\|")) ;
		
		// Validamos roles del usuario
		for (String role : roles)
		{
			// Si el role no existe, devolvemos un error
			if (!BaseServerConstants.ROLES_LIST.contains(role))
			{
				String errorString = "Rol inválido en el CSV: " + role ;
				
				log.error(errorString) ;
				throw new FirebaseServerException(Constants.ERR_USERS_IMPORTS, errorString) ;
			}
		}
		
		// Creamos una instancia del usuario y seteamos los campos
		Usuario usuario = new Usuario() ;
		
		usuario.setEmail(email) ;
		usuario.setNombre(nombre) ;
		usuario.setApellidos(apellidos) ;
		usuario.setRolesList(roles) ;
		
		// Devolvemos una instancia del usuario
		return usuario ;
	}
	
	/**
	 * @param inputStreamReader input stream reader
	 * @param bufferedReader buffered reader
	 * @throws FirebaseServerException con un error
	 */
	private void importarUsuariosProcesarCsvCierreFlujos(InputStreamReader inputStreamReader, BufferedReader bufferedReader) 
				 throws FirebaseServerException
	{
		if (bufferedReader != null)
		{
			try
			{
				bufferedReader.close() ;
			}
			catch (IOException ioException)
			{
				String errorString = "Error en el cierre del flujo bufferedReader en la lectura del fichero CSV" ;
				
				log.error(errorString, ioException) ;
				throw new FirebaseServerException(Constants.ERR_USERS_IMPORTS, errorString, ioException) ;
			}
		}
		
		if (inputStreamReader != null)
		{
			try
			{
				inputStreamReader.close() ;
			}
			catch (IOException ioException)
			{
				String errorString = "Error en el cierre del flujo inputStreamReader en la lectura del fichero CSV" ;
				
				log.error(errorString, ioException) ;
				throw new FirebaseServerException(Constants.ERR_USERS_IMPORTS, errorString, ioException) ;
			}
		}
	}
	
	/**
	 * Autoriza el acceso del usuario e inserta en la colección de Firebase en caso de que no exista
	 * @return la lista de impresoras
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/authorization")
	public ResponseEntity<?> autorizarUsuarioEnLogin(@RequestHeader("Authorization") String authorizationHeader)
	{
		try
		{
		    // Eliminamos el prefijo "Bearer " del encabezado de autorización para obtener el token JWT limpio
		    String token = authorizationHeader.replace("Bearer ", "") ;
			
			// Verificamos el token con Firebase
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token) ;
            
            // Obtenemos el UID y el email
            String uid   = decodedToken.getUid() ;  
            String email = decodedToken.getEmail() ;
            
            // Buscamos el usuario en BBDD
            Usuario usuario = this.autorizarUsuarioEnLoginBuscarUsuarioEnBBDD(email);
            
            // Verificamos si existe el usuario en Firebase
            this.autorizarUsuarioEnLoginVerificarSiExisteEnFirebase(uid, email, usuario) ;
			
			// Respondemos con un 200 de que todo ha ido bien
			return ResponseEntity.ok().body(usuario) ;
		}
        catch (FirebaseServerException firebaseServerException)
        {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(firebaseServerException.getBodyExceptionMessage()) ;
        }
        catch (Exception exception)
        {
	        FirebaseServerException printersServerException = 
	        		new FirebaseServerException(BaseServerConstants.ERR_GENERIC_EXCEPTION_CODE, 
	        									BaseServerConstants.ERR_GENERIC_EXCEPTION_MSG + "autorizarUsuarioEnLogin",
											    exception) ;

			log.error(BaseServerConstants.ERR_GENERIC_EXCEPTION_MSG + "autorizarUsuarioEnLogin", printersServerException) ;
			return ResponseEntity.status(500).body(printersServerException.getBodyExceptionMessage()) ;
        }
	}

	/**
	 * @param email email del usuario logueado
	 * @return usuario almacenado en BBDD
	 * @throws FirebaseServerException
	 */
	private Usuario autorizarUsuarioEnLoginBuscarUsuarioEnBBDD(String email) throws FirebaseServerException
	{
		// Buscamos al usuario por el email
		Optional<Usuario> optionalUsuario = this.usuarioRepository.findById(email) ;
		
		// Si el usuario no está presente en la BBDD, enviamos un error
		if (!optionalUsuario.isPresent())
		{
			String errorString = "El usuario no está dado de alta. ¿Estás seguro que lo hiciste con el dominio g.educaand.es?" ;
			
			log.error(errorString) ;
			throw new FirebaseServerException(Constants.ERR_USER_AUTHORIZATION, errorString) ;            	
		}
		
		// Obtenemos y devolvemos el usuario de la BBDD
		return optionalUsuario.get() ;
	}

	/**
	 * @param uid uid del usuario logueado
	 * @param email email del usuario logueado
	 * @param usuario información del usuario tomada de BBDD
	 * @throws FirebaseServerException con un error
	 */
	private void autorizarUsuarioEnLoginVerificarSiExisteEnFirebase(String uid, String email, Usuario usuario) throws FirebaseServerException
	{
		try
		{
			// Accedemos a Firestore Database
	        Firestore firestore = FirestoreClient.getFirestore() ;
	        
	        DocumentReference documentReference = firestore.collection(BaseServerConstants.COLLECTION_NAME_USUARIOS).document(uid) ;
	
	        // Verificamos si el documento ya existe en la colección de Firebase
	        ApiFuture<DocumentSnapshot> apiFuture = documentReference.get() ;
	        DocumentSnapshot document = apiFuture.get();
	
	        if (!document.exists())
	        {
	            // El usuario no existe en Firebase, lo creamos
	            Map<String, Object> documentoUsuario = new HashMap<String, Object>() ;
	            
	            documentoUsuario.put(BaseServerConstants.COLLECTION_USUARIOS_ATTRIBUTE_EMAIL, 	  email) ;
	            documentoUsuario.put(BaseServerConstants.COLLECTION_USUARIOS_ATTRIBUTE_NOMBRE,    usuario.getNombre()) ;
	            documentoUsuario.put(BaseServerConstants.COLLECTION_USUARIOS_ATTRIBUTE_APELLIDOS, usuario.getApellidos()) ;
	            documentoUsuario.put(BaseServerConstants.COLLECTION_USUARIOS_ATTRIBUTE_ROLES, 	  usuario.getRolesList()) ;
	
	            // Añadimos el documento a la colección 'usuarios'
	            ApiFuture<WriteResult> result = documentReference.set(documentoUsuario) ;
	            
	            // Logueamos la creación del usuario
	            log.info("Usuario creado en Firebase con UID: " + uid + ", usuario: " + usuario + ", en: " + result.get().getUpdateTime()) ;
	        }
		}
		catch (ExecutionException executionException)
		{
			String errorString = "ExecutionException cuando se trató de insertar el usuario en la colección" ;
			
			log.error(errorString, executionException) ;
			throw new FirebaseServerException(Constants.ERR_USER_AUTHORIZATION, errorString, executionException) ;
		}
		catch (InterruptedException interruptedException)
		{
			String errorString = "InterruptedException cuando se trató de insertar el usuario en la colección" ;
			
			log.error(errorString, interruptedException) ;
			throw new FirebaseServerException(Constants.ERR_USER_AUTHORIZATION, errorString, interruptedException) ;
		}
	}
}
