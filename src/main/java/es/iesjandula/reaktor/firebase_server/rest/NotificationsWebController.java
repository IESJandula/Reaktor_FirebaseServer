package es.iesjandula.reaktor.firebase_server.rest;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import es.iesjandula.reaktor.base.utils.BaseConstants;
import es.iesjandula.reaktor.firebase_server.dtos.NotificacionesWebHoyDto;
import es.iesjandula.reaktor.firebase_server.models.Aplicacion;
import es.iesjandula.reaktor.firebase_server.models.NotificacionWeb;
import es.iesjandula.reaktor.firebase_server.repository.IAplicacionRepository;
import es.iesjandula.reaktor.firebase_server.repository.INotificacionWebRepository;
import es.iesjandula.reaktor.firebase_server.utils.FirebaseServerException;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/notifications_web")
@Slf4j
public class NotificationsWebController 
{
	
	@Autowired
	private IAplicacionRepository aplicacionRepository ;
	
	@Autowired
	private INotificacionWebRepository notificacionWebRepository ;

	@RequestMapping(method = RequestMethod.POST, value = "/crearNotificacionWeb")
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	public ResponseEntity<?> crearNotificacionWeb(
			@RequestHeader("client_id") String clientId,
            @RequestHeader("nombre") String nombre,
            @RequestHeader("fecha_inicio") String fechaInicio,
            @RequestHeader("fecha_fin") String fechaFin,
            @RequestHeader("roles") String roles,
            @RequestHeader("texto") String texto,
            @RequestHeader("nivel") int nivel,
            @RequestHeader(value = "imagen", required = false) String imagen,
            @RequestHeader("titulo") String titulo)
	{
		
		try 
		{
			
			Aplicacion aplicacion = aplicacionRepository.findByClientIdAndNombre(clientId, nombre) ;
			if (aplicacion == null) 
			{
				String errorMessage = "Aplicación no encontrada con ese client_id y nombre" ;
				log.error(errorMessage) ;
				throw new FirebaseServerException(400, errorMessage) ;
			}
			
			NotificacionWeb notificacionWeb = new NotificacionWeb() ;
			notificacionWeb.setAplicacion(aplicacion) ;
			notificacionWeb.setFechaCreacion(LocalDate.now()) ;
			notificacionWeb.setFechaInicio(LocalDate.parse(fechaInicio)) ;
			notificacionWeb.setFechaFin(LocalDate.parse(fechaFin)) ;
			notificacionWeb.setRoles(roles) ;
			notificacionWeb.setTexto(texto) ;
			notificacionWeb.setNivel(nivel) ;
			notificacionWeb.setTitulo(titulo) ;
			
			if (imagen != null) 
			{
				notificacionWeb.setTexto(notificacionWeb.getTexto() + "\n[Imagen: " + imagen + "]");
			}
			
			notificacionWebRepository.saveAndFlush(notificacionWeb) ;
			
			log.info("Notificación web creada correctamente") ;
			return ResponseEntity.status(204).body("Notificación web creada correctamente") ;
			
		} catch (Exception e) 
		{
			
			String errorMessage = "Error al crear la notificación web" ;
			log.error(errorMessage, e) ;
			FirebaseServerException firebaseServerException = new FirebaseServerException(204, errorMessage, e) ;
			return ResponseEntity.status(500).body(firebaseServerException.getBodyExceptionMessage()) ;
			
		}
		
		
		
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/obtenerNotificacionesHoy")
	public ResponseEntity<?> obtenerNotificacionHoy(@RequestHeader("usuario") String usuario)
	{
	
		try 
		{
		
			LocalDate hoy = LocalDate.now() ;
			
			List<NotificacionWeb> notificaciones = notificacionWebRepository.findByFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(hoy, hoy) ;
			
			List<NotificacionesWebHoyDto> resultado = notificaciones.stream()
					.map(n -> new NotificacionesWebHoyDto(
								n.getTexto(),
								n.getNivel(),
								extraerNombreImagen(n.getTexto())
							))
							.collect(Collectors.toList()) ;
			
			log.info("Notificaciones para el usuario {} encontradas: {}", usuario, resultado.size()) ;
			return ResponseEntity.status(200).body(resultado) ;
			
		} catch (Exception e) 
		{
			
			String errorMessage = "Error inesperado al obtener las notificaciones" ;
			log.error(errorMessage, e) ;
			FirebaseServerException firebaseServerException = new FirebaseServerException(500, errorMessage, e) ;
			return ResponseEntity.status(500).body(firebaseServerException.getBodyExceptionMessage()) ;
		
		}
		
	}
	
	// Método auxiliar para obtener nombre de imagen si está dentro del texto
    private String extraerNombreImagen(String texto) 
    {
        if (texto != null && texto.contains("[Imagen:")) 
        {
            return texto.substring(texto.indexOf("[Imagen:") + 8, texto.indexOf("]")).trim();
        }
        return null;
    }
	
}
