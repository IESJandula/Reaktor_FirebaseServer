package es.iesjandula.reaktor.firebase_server.models;

import java.util.Arrays;
import java.util.List;

import es.iesjandula.reaktor.firebase_server.utils.Constants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad para las aplicaciones/servicios clientes (como PrintersClient)
 * que interactúan con FirebaseServer.
 */
@Entity
@Table(name = "aplicacion")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Aplicacion
{

    /** Atributo - ID único de la aplicación */
    @Id
    private String clientId ;

    /** Atributo - Nombre descriptivo de la aplicación */
    @Column(nullable = false)
    private String nombre ;

    /** Atributo - Roles asociados a la aplicación */
    @Column
    private String roles ;
    
    private int notif_hoy_calendar ;
    
    private int notif_max_calendar ;
    
    private int notif_hoy_email ;
    
    private int notif_max_email ;
    
    private int notif_hoy_web ;
    
    private int notif_max_web ;
    
    @OneToMany(mappedBy = "aplicacion")
    private List<NotificacionCalendar> notificaciones_calendars ;
    
    @OneToMany(mappedBy = "aplicacion")
    private List<NotificacionEmail> notificaciones_emails ;
    
    @OneToMany(mappedBy = "aplicacion")
    private List<NotificacionWeb> notificaciones_webs ;

    /**
     * @return lista de roles deserializada
     */
    public List<String> getRolesList()
    {
        return Arrays.asList(this.roles.split(Constants.STRING_COMA)) ;
    }

    /**
     * Setter para establecer los roles desde una lista.
     * 
     * @param rolesList lista de roles
     */
    public void setRolesList(List<String> rolesList) {
        this.roles = null;

        if (rolesList != null && !rolesList.isEmpty()) {
            StringBuilder rolesStringBuilder = new StringBuilder();

            for (int i = 0; i < rolesList.size(); i++) {
                // Añadimos el rol
                rolesStringBuilder.append(rolesList.get(i));

                // Si no es el último, añadimos una coma
                if (i < rolesList.size() - 1) {
                    rolesStringBuilder.append(Constants.STRING_COMA);
                }
            }

            // Convierte el StringBuilder a cadena
            this.roles = rolesStringBuilder.toString();
        }
    }
}
