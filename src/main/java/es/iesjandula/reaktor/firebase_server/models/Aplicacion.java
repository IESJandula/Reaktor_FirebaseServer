package es.iesjandula.reaktor.firebase_server.models;

import java.util.Arrays;
import java.util.List;

import es.iesjandula.reaktor.base.utils.BaseConstants;
import es.iesjandula.reaktor.firebase_server.utils.Constants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad para las aplicaciones/servicios clientes (como PrintersClient)
 * que interactúan con FirebaseServer.
 */
@Entity
@Table(name = "aplicacion")
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

    /**
     * Constructor por defecto
     */
    public Aplicacion()
    {
        super();
    }

    /**
     * Constructor con parámetros
     * @param clientId
     * @param nombre
     * @param roles
     */
    public Aplicacion(String clientId, String nombre, String roles)
    {
        this.clientId = clientId;
        this.nombre = nombre;
        this.roles = roles;
    }
    
    /**
     * Getter para el atributo clientId
     * @return el clientId
     */
    public String getClientId()
    {
        return this.clientId;
    }

    /**
     * Getter para el atributo nombre
     * @return el nombre
     */
    public String getNombre()
    {
        return this.nombre;
    }

    /**
     * Getter para el atributo roles
     * @return los roles
     */
    public String getRoles()
    {
        return this.roles;
    }

    /**
     * Setter para el atributo clientId
     * @param clientId el clientId
     */
    public void setClientId(String clientId)
    {
        this.clientId = clientId;
    }

    /**
     * Setter para el atributo nombre
     * @param nombre el nombre
     */
    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    /**
     * Setter para el atributo roles
     * @param roles los roles
     */
    public void setRoles(String roles)
    {
        this.roles = roles;
    }

    /**
     * Getter para el atributo rolesList
     * @return lista de roles deserializada
     */
    public List<String> getRolesList()
    {
        return Arrays.asList(this.roles.split(BaseConstants.STRING_COMA)) ;
    }

    /**
     * Setter para el atributo rolesList
     * Setter para establecer los roles desde una lista.
     * 
     * @param rolesList lista de roles
     */
    public void setRolesList(List<String> rolesList)
    {
        this.roles = null;

        if (rolesList != null && !rolesList.isEmpty())
        {
            StringBuilder rolesStringBuilder = new StringBuilder();

            for (int i = 0; i < rolesList.size(); i++)
            {
                // Añadimos el rol
                rolesStringBuilder.append(rolesList.get(i));

                // Si no es el último, añadimos una coma
                if (i < rolesList.size() - 1)
                {
                    rolesStringBuilder.append(BaseConstants.STRING_COMA);
                }
            }

            // Convierte el StringBuilder a cadena
            this.roles = rolesStringBuilder.toString();
        }
    }
}
