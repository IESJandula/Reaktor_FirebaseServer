package es.iesjandula.reaktor.firebase_server.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import es.iesjandula.reaktor.base.security.models.DtoUsuarioBase;
import es.iesjandula.reaktor.base.utils.BaseConstants;
import es.iesjandula.reaktor.firebase_server.repository.IUsuarioRepository;
import es.iesjandula.reaktor.firebase_server.utils.FirebaseServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/firebase/queries")
public class QueriesManager
{
	/**
	 * Logger of the class
	 */
	private static final Logger log = LoggerFactory.getLogger(QueriesManager.class);

	@Autowired
	private IUsuarioRepository usuarioRepository ;
	
    @PreAuthorize("hasAnyRole('" + BaseConstants.ROLE_ADMINISTRADOR + "', '" + BaseConstants.ROLE_DIRECCION + "', '" + BaseConstants.ROLE_APLICACION_NOTIFICACIONES + "')")
	@RequestMapping(method = RequestMethod.GET, value = "/users")
    public ResponseEntity<?> obtenerInfoUsuarios()
	{
        try
        {
        	// Obtenemos la información de los usuarios
			List<DtoUsuarioBase> dtoUsuarioBase = this.usuarioRepository.obtenerInfoUsuarios() ;
            
            // Devolvemos el 200
            return ResponseEntity.ok().body(dtoUsuarioBase) ;
        }
        catch (Exception exception)
        {
	        FirebaseServerException printersServerException = 
	        		new FirebaseServerException(BaseConstants.ERR_GENERIC_EXCEPTION_CODE, 
	        									BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "obtenerInfoUsuarios",
											    exception) ;

			log.error(BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "obtenerInfoUsuarios", printersServerException) ;
			return ResponseEntity.status(500).body(printersServerException.getBodyExceptionMessage()) ;
        }
    }
    
    @PreAuthorize("hasAnyRole('" + BaseConstants.ROLE_ADMINISTRADOR + "', '" + BaseConstants.ROLE_DIRECCION + "')")
	@RequestMapping(method = RequestMethod.GET, value = "/user")
    public ResponseEntity<?> obtenerInfoUsuario(@RequestHeader(value = "email") String email)
	{
        try
        {
        	// Logging de entrada al método
        	log.info("Se ha pedido información del usuario con email {}", email) ;
        	
        	// Obtenemos la información de los usuarios
			DtoUsuarioBase dtoUsuarioBase = this.usuarioRepository.obtenerInfoUsuario(email) ;
            
            // Devolvemos el 200
            return ResponseEntity.ok().body(dtoUsuarioBase) ;
        }
        catch (Exception exception)
        {
	        FirebaseServerException printersServerException = 
	        		new FirebaseServerException(BaseConstants.ERR_GENERIC_EXCEPTION_CODE, 
	        									BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "obtenerInfoUsuarios",
											    exception) ;

			log.error(BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "obtenerInfoUsuarios", printersServerException) ;
			return ResponseEntity.status(500).body(printersServerException.getBodyExceptionMessage()) ;
        }
    }
}
