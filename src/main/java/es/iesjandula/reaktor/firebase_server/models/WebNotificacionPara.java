package es.iesjandula.reaktor.firebase_server.models;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "web_notificacion_para")
@AllArgsConstructor
@NoArgsConstructor
public class WebNotificacionPara 
{

	@EmbeddedId
	private NotificacionWebUsuarioId id ;
	
	@ManyToOne
    @MapsId("notificacionId")
    @JoinColumn(name = "notificacion_id")
    private NotificacionWeb notificacion;

    @ManyToOne
    @MapsId("usuarioEmail")
    @JoinColumn(name = "usuario_email")
    private Usuario usuario;
	
}
