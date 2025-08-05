package es.iesjandula.reaktor.firebase_server.models;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notificacion_calendar")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class NotificacionCalendar 
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id ;
	
	private LocalDate fecha_envio ;
	
	private String titulo ;
	
	private Date fecha_inicio ;
	
	private Date fecha_fin ;
	
	@ManyToOne
	@JoinColumn(name = "client_id", nullable = false)
	private Aplicacion aplicacion ;
	
	@ManyToMany
    @JoinTable(
        name = "invitados_calendar",
        joinColumns = @JoinColumn(name = "calendar_id"),
        inverseJoinColumns = @JoinColumn(name = "usuario_email")
    )
    private List<Usuario> usuarios;
	
}
