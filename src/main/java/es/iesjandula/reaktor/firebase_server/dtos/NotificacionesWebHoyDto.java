package es.iesjandula.reaktor.firebase_server.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificacionesWebHoyDto 
{

	private Long id ;
	private String texto;
    private String nivel;
    private String imagen;
    private String fechaInicio;
    private String fechaFin;
    private String roles;
	
}
