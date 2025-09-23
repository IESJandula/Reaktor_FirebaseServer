package es.iesjandula.reaktor.firebase_server.dtos;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NotificacionesWebHoyDto 
{
	private Long id ;
	private String texto;
    private String nivel;
    private String imagen;
    private String fechaInicio;
    private String horaInicio;
    private String fechaFin;
    private String horaFin;
    private String roles;
	
    /**
     * Constructor de la clase NotificacionesWebHoyDto
     * @param id id de la notificación
     * @param texto texto de la notificación
     * @param nivel nivel de la notificación
     * @param imagen imagen de la notificación
     * @param fechaInicio fecha de inicio de la notificación
     * @param horaInicio hora de inicio de la notificación
     * @param fechaFin fecha de fin de la notificación  
     * @param horaFin hora de fin de la notificación
     * @param roles roles de la notificación
     */
    public NotificacionesWebHoyDto(Long id, String texto, String nivel, String imagen, 
                                   LocalDate fechaInicio, LocalTime horaInicio, 
                                   LocalDate fechaFin, LocalTime horaFin, 
                                   String roles)
    {
        this.id          = id;
        this.texto       = texto;
        this.nivel       = nivel;
        this.imagen      = imagen;
        this.fechaInicio = fechaInicio.toString();
        this.horaInicio  = horaInicio.toString();
        this.fechaFin    = fechaFin.toString();
        this.horaFin     = horaFin.toString();
        this.roles       = roles;
    }
}
