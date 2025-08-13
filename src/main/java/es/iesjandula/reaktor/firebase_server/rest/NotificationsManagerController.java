package es.iesjandula.reaktor.firebase_server.rest;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import es.iesjandula.reaktor.base.utils.BaseConstants;
import es.iesjandula.reaktor.firebase_server.dtos.NotificacionesEnviadasDto;
import es.iesjandula.reaktor.firebase_server.models.Aplicacion;
import es.iesjandula.reaktor.firebase_server.repository.IAplicacionRepository;
import es.iesjandula.reaktor.firebase_server.repository.INotificacionCalendarRepository;
import es.iesjandula.reaktor.firebase_server.repository.INotificacionEmailRepository;
import es.iesjandula.reaktor.firebase_server.repository.INotificacionWebRepository;
import es.iesjandula.reaktor.firebase_server.utils.FirebaseServerException;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/notifications_manager")
@Slf4j
public class NotificationsManagerController 
{

	@Autowired
	private IAplicacionRepository aplicacionRepository ;
	
	@Autowired
	private INotificacionCalendarRepository notificacionCalendarRepository ;
	
	@Autowired 
	private INotificacionEmailRepository notificacionEmailRepository ;
	
	@Autowired
	private INotificacionWebRepository notificacionWebRepository ;
	
	@RequestMapping(method = RequestMethod.GET, value = "notificacionesEnviadas")
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_APLICACION + "')")
	public ResponseEntity<?> obtenerResumen(@RequestHeader("nombre") String nombre, @RequestHeader("client_id") String clientId)
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
			
			LocalDate hoy = LocalDate.now() ;
			
			int calendarHoy = notificacionCalendarRepository.countByAplicacionAndFechaCreacion(aplicacion, hoy) ;
			int emailHoy = notificacionEmailRepository.countByAplicacionAndFechaCreacion(aplicacion, hoy) ;
			int webHoy = notificacionWebRepository.countByAplicacionAndFechaCreacion(aplicacion, hoy) ;
			
			NotificacionesEnviadasDto notificacionesEnviadasDto = new NotificacionesEnviadasDto(
				aplicacion.getClientId(),
				aplicacion.getNombre(),
				calendarHoy,
				emailHoy,
				webHoy,
				aplicacion.getNotif_max_calendar(),
				aplicacion.getNotif_max_email(),
				aplicacion.getNotif_hoy_web()
			) ;
			
			log.info("Notificaciones recibidas correctamente") ;
			return ResponseEntity.status(200).body(notificacionesEnviadasDto) ;
		} catch (Exception e) 
		{
			String errorMessage = "Error inesperado al procesar la solicitud." ;
			log.error(errorMessage, e) ;
			FirebaseServerException firebaseServerException = new FirebaseServerException(500, errorMessage, e) ;
			return ResponseEntity.status(500).body(firebaseServerException.getBodyExceptionMessage()) ;
		}
		
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/actualizarMaximos")
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	public ResponseEntity<?> configurarMaximosNotificaciones(
			@RequestHeader("client_id") String clientId,
            @RequestHeader("nombre") String nombre,
            @RequestHeader("max_web") int maxWeb,
            @RequestHeader("max_email") int maxEmail,
            @RequestHeader("max_calendar") int maxCalendar)
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
			
			aplicacion.setNotif_max_calendar(maxCalendar) ;
			aplicacion.setNotif_max_email(maxEmail) ;
			aplicacion.setNotif_max_web(maxWeb) ;
			
			aplicacionRepository.saveAndFlush(aplicacion) ;
			
			log.info("Máximos de notificaciones actualizados para la aplicación: {}", nombre) ;
			return ResponseEntity.status(204).body("Máximos de notificaciones actualizados para la aplicación") ;
			
		} catch (Exception e) 
		{
			String errorMessage = "Error inesperado al actualizar máximos de aplicaciones" ;
			log.error(errorMessage, e) ;
			FirebaseServerException firebaseServerException = new FirebaseServerException(500, errorMessage, e) ;
			return ResponseEntity.status(500).body(firebaseServerException.getBodyExceptionMessage()) ;
		}
		
	}
	
}
