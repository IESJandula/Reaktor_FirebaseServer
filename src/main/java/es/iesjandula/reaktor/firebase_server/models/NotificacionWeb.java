package es.iesjandula.reaktor.firebase_server.models;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
	
	private LocalDate fechaCreacion ;
	
	private String texto ;
	
	private LocalDate fechaFin ;
	
	private String roles ;
	
	private int nivel ;
	
	private LocalDate fechaInicio ;
	
	private String titulo ;
	
	@ManyToOne
	@JoinColumn(name = "aplicacion_client_id", nullable = false)
	private Aplicacion aplicacion ;
	
	@OneToMany(mappedBy = "notificacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WebNotificacionPara> paraUsuarios;

    // Relación con usuarios en COPIA (CC)
    @OneToMany(mappedBy = "notificacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WebNotificacionCopia> copiaUsuarios;

    // Relación con usuarios en COPIA OCULTA (CCO)
    @OneToMany(mappedBy = "notificacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WebNotificacionCopiaOculta> copiaOcultaUsuarios;
	
}
