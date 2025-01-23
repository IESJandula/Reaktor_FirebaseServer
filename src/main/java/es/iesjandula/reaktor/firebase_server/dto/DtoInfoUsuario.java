package es.iesjandula.reaktor.firebase_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Francisco Manuel Benítez Chico
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DtoInfoUsuario
{
	/** Atributo - Email */
	private String email ;
	
	/** Atributo - Nombre */
	private String nombre ;
	
	/** Atributo - Apellidos */
	private String apellidos ;
}
