package es.iesjandula.reaktor.firebase_server.models;

import java.util.Arrays;
import java.util.List;

import es.iesjandula.reaktor.base.utils.BaseConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * @author Francisco Manuel Benítez Chico
 */
@Entity
@Table(name = "usuario")
public class Usuario
{
	/** Atributo - Email */
	@Id
	private String email ;
	
	/** Atributo - Nombre */
	@Column
	private String nombre ;
	
	/** Atributo - Apellidos */
	@Column
	private String apellidos ;
	
	/** Atributo - Lista de roles */
	@Column
	private String roles ;

	/** Atributo - Departamento */
	@Column
	private String departamento ;

	/** Atributo - Fecha de nacimiento */
	@Column
	private String fechaNacimiento ;

	/**
	 * Constructor por defecto
	 */
	public Usuario()
	{
		super();
	}
	
	/**
	 * Constructor con parámetros
	 * @param email
	 * @param nombre
	 * @param apellidos
	 * @param roles
	 * @param departamento
	 */
	public Usuario(String email, String nombre, String apellidos, String roles, String departamento, String fechaNacimiento)
	{
		this.email           = email ;
		this.nombre          = nombre ;
		this.apellidos       = apellidos ;
		this.roles 			 = roles ;
		this.departamento    = departamento ;
		this.fechaNacimiento = fechaNacimiento ;
	}		

	/**
	 * Getter para el atributo email
	 * @return el email
	 */
	public String getEmail()
	{
		return this.email;
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
	 * Getter para el atributo apellidos
	 * @return los apellidos
	 */
	public String getApellidos()
	{
		return this.apellidos;
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
	 * Getter para el atributo departamento
	 * @return el departamento
	 */
	public String getDepartamento()
	{
		return this.departamento;
	}

	/**
	 * Getter para el atributo fecha de nacimiento
	 * @return la fecha de nacimiento
	 */
	public String getFechaNacimiento()
	{
		return this.fechaNacimiento;
	}
	/**
	 * Setter para el atributo email
	 * @param email el email
	 */
	public void setEmail(String email)
	{
		this.email = email;
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
	 * Setter para el atributo apellidos
	 * @param apellidos los apellidos
	 */
	public void setApellidos(String apellidos)
	{
		this.apellidos = apellidos;
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
	 * Setter para el atributo departamento
	 * @param departamento el departamento
	 */
	public void setDepartamento(String departamento)
	{
		this.departamento = departamento;
	}

	/**
	 * Setter para el atributo fecha de nacimiento
	 * @param fechaNacimiento la fecha de nacimiento
	 */
	public void setFechaNacimiento(String fechaNacimiento)
	{
		this.fechaNacimiento = fechaNacimiento;
	}

    /**
     * @return lista de roles deserializada
     */
    public List<String> getRolesList()
    {
        return Arrays.asList(this.roles.split(BaseConstants.STRING_COMA)) ;
    }

    /**
     * Setter para establecer los roles desde una lista
     * 
     * @param rolesList lista de roles
     */
    public void setRolesList(List<String> rolesList)
    {
    	this.roles = null ;
    	
        if (rolesList != null && !rolesList.isEmpty())
        {

	        StringBuilder rolesStringBuilder = new StringBuilder();
	
	        for (int i = 0 ; i < rolesList.size() ; i++)
	        {
	        	// Añadimos el role 
	            rolesStringBuilder.append(rolesList.get(i)) ;
	            
	            // Si no es el último, añadimos una coma
	            if (i < rolesList.size() - 1)
	            {
	                rolesStringBuilder.append(BaseConstants.STRING_COMA) ;
	            }
	        }
	
	        // Convierte el StringBuilder a cadena
	        this.roles = rolesStringBuilder.toString() ;
        }
    }
}

