package es.iesjandula.reaktor.firebase_server.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import es.iesjandula.reaktor.firebase_server.repository.IAplicacionRepository;
import es.iesjandula.reaktor.firebase_server.repository.INotificacionCalendarRepository;
import es.iesjandula.reaktor.firebase_server.repository.INotificacionEmailRepository;
import es.iesjandula.reaktor.firebase_server.repository.INotificacionWebRepository;

@RestController
@RequestMapping("/notifications_manager")
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
	public ResponseEntity<?> obtenerResumen(@RequestHeader("nombre") String nombre, @RequestHeader("client_id") String clientId)
	{
		
		try {
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
}
