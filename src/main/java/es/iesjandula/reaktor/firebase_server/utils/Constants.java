package es.iesjandula.reaktor.firebase_server.utils;

/**
 * @author Francisco Manuel Benítez Chico
 */
public class Constants
{	
	/*********************************************************/
	/*********************** Errores *************************/
	/*********************************************************/
	
	/** Error - Excepción genérica - Código */
	public static final int ERR_GENERIC_EXCEPTION_CODE 			  = 100 ;
	
	/** Error - Excepción genérica - Mensaje */
	public static final String ERR_GENERIC_EXCEPTION_MSG 		  = "Excepción genérica en " ;
	
	/** Error - Error mientras se obtenía la clave privada */
	public static final int ERR_GETTING_PRIVATE_KEY 			  = 101 ;
	
	/** Error - Error en la importación de usuarios */
	public static final int ERR_USERS_IMPORTS					  = 102 ;
	
	/** Error - Error en la autorización del usuario en el login */
	public static final int ERR_USER_AUTHORIZATION				  = 103 ;
	
	/** Error - Error la APP no existe en la BBDD propia */
	public static final int ERR_APPS_AUTHORIZATION        		  = 104 ;
	
	/** Error - Error en la importación de aplicaciones */
	public static final int ERR_APPS_IMPORTS					  = 105 ;
}

