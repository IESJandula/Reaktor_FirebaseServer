package es.iesjandula.reaktor.firebase_server.models;

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
@Table(name = "notificacion_email")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class NotificacionEmail 
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id ;
	
	private LocalDate fecha_envio ;
	
	private String asunto ;
	
	private String contenido ;
	
	@ManyToOne
	@JoinColumn(name = "client_id", nullable = false)
	private Aplicacion aplicacion ;
	
	@ManyToMany
    @JoinTable(name = "email_para", joinColumns = @JoinColumn(name = "email_id"),
               inverseJoinColumns = @JoinColumn(name = "usuario_email"))
    private List<Usuario> para;

    @ManyToMany
    @JoinTable(name = "email_copia", joinColumns = @JoinColumn(name = "email_id"),
               inverseJoinColumns = @JoinColumn(name = "usuario_email"))
    private List<Usuario> copia;

    @ManyToMany
    @JoinTable(name = "email_copia_oculta", joinColumns = @JoinColumn(name = "email_id"),
               inverseJoinColumns = @JoinColumn(name = "usuario_email"))
    private List<Usuario> copiaOculta;
	
}
