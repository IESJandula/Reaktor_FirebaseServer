package es.iesjandula.reaktor.firebase_server.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificacionesWebHoyDto 
{

	private String texto ;
	private int nivel ;
	private String imagen ;
	
}
