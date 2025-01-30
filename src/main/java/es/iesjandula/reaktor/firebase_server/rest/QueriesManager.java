package es.iesjandula.reaktor.firebase_server.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.iesjandula.reaktor.base.utils.BaseConstants;
import es.iesjandula.reaktor.firebase_server.dto.DtoInfoUsuario;
import es.iesjandula.reaktor.firebase_server.repository.IUsuarioRepository;
import es.iesjandula.reaktor.firebase_server.utils.FirebaseServerException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/firebase/queries")
public class QueriesManager
{
	@Autowired
	private IUsuarioRepository usuarioRepository ;
	
    @PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@RequestMapping(method = RequestMethod.GET, value = "/users")
    public ResponseEntity<?> obtenerInfoUsuarios()
	{
        try
        {
        	// Obtenemos la información de los usuarios
			List<DtoInfoUsuario> dtoInfoUsuario = this.usuarioRepository.obtenerInfoUsuarios() ;
            
            // Devolvemos el 200
            return ResponseEntity.ok().body(dtoInfoUsuario) ;
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
    
	@RequestMapping(method = RequestMethod.GET, value = "/user")
    public ResponseEntity<?> obtenerInfoUsuario(@RequestParam(value = "email") String email)
	{
        try
        {
        	// Obtenemos la información de los usuarios
			DtoInfoUsuario dtoInfoUsuario = this.usuarioRepository.obtenerInfoUsuario(email) ;
            System.out.println(dtoInfoUsuario);
            // Devolvemos el 200
            return ResponseEntity.ok().body(dtoInfoUsuario) ;
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
