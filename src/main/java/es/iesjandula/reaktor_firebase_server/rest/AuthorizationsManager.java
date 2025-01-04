package es.iesjandula.reaktor_firebase_server.rest;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

import es.iesjandula.reaktor.base.utils.BaseConstants;
import es.iesjandula.reaktor_firebase_server.models.Usuario;
import es.iesjandula.reaktor_firebase_server.repository.IUsuarioRepository;
import es.iesjandula.reaktor_firebase_server.utils.Constants;
import es.iesjandula.reaktor_firebase_server.utils.FirebaseServerException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/firebase/auth")
public class AuthorizationsManager
{
	@Autowired
	private IUsuarioRepository usuarioRepository ;
	
	/**
	 * Autoriza el acceso del usuario e inserta en la colección de Firebase en caso de que no exista
	 * @return la lista de impresoras
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/user")
	public ResponseEntity<?> autorizarUsuarioEnLogin(@RequestHeader("Authorization") String authorizationHeader)
	{
		try
		{
		    // Eliminamos el prefijo "Bearer " del encabezado de autorización para obtener el token JWT limpio
		    String token = authorizationHeader.replace("Bearer ", "") ;
			
			// Verificamos el token con Firebase
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token) ;
            
            // Obtenemos el email
            String email = decodedToken.getEmail() ;
            
            // Buscamos el usuario en BBDD y lo devolvemos con 200
            return ResponseEntity.ok().body(this.autorizarUsuarioEnLoginBuscarUsuarioEnBBDD(email)) ;
		}
        catch (FirebaseServerException firebaseServerException)
        {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(firebaseServerException.getBodyExceptionMessage()) ;
        }
        catch (Exception exception)
        {
	        FirebaseServerException printersServerException = 
	        		new FirebaseServerException(BaseConstants.ERR_GENERIC_EXCEPTION_CODE, 
	        									BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "autorizarUsuarioEnLogin",
											    exception) ;

			log.error(BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "autorizarUsuarioEnLogin", printersServerException) ;
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
}
