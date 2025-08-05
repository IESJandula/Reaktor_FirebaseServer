package es.iesjandula.reaktor.firebase_server.models;

import java.sql.Date;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notificacion_web")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class NotificacionWeb 
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id ;
	
	private LocalDate fecha_envio ;
	
	private String texto ;
	
	private Date fecha_fin ;
	
	private String roles ;
	
	@ManyToOne
	@JoinColumn(name = "client_id", nullable = false)
	private Aplicacion aplicacion ;
	
}
