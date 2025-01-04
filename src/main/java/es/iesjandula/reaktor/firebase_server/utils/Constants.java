package es.iesjandula.reaktor.firebase_server.utils;

/**
 * @author Francisco Manuel Benítez Chico
 */
public class Constants
{
	public static final String STRING_COMA = "," ;
	
	/*********************************************************/
	/*********************** Errores *************************/
	/*********************************************************/
	
	/** Error - Excepción genérica - Código */
	public static final int ERR_GENERIC_EXCEPTION_CODE 			  = 100 ;
	
	/** Error - Excepción genérica - Mensaje */
	public static final String ERR_GENERIC_EXCEPTION_MSG 		  = "Excepción genérica en " ;
	
	/** Error - Error mientras se obtenía la clave privada */
	public static final int ERR_GETTING_PRIVATE_KEY 			  = 101 ;
	
	/** Error - Error mientras se obtenían los credenciales de Google */
	public static final int ERR_GETTING_GOOGLE_CREDENTIALS        = 102 ;
	
	/** Error - Error el UID de usuario no existe */
	public static final int ERR_UID_USER_NOT_EXISTS_IN_FIREBASE   = 103 ;
	
	/** Error - Error el UID de usuario no existe */
	public static final int ERR_USER_NOT_EXISTS_IN_COLLECTION	  = 104 ;
	
	/** Error - Error en la importación de usuarios */
	public static final int ERR_USERS_IMPORTS					  = 105 ;
	
	/** Error - Error en la autorización del usuario en el login */
	public static final int ERR_USER_AUTHORIZATION				  = 106 ;
	
	/** Error - Error la APP no existe en la BBDD propia */
	public static final int ERR_APP_NOT_EXISTS_IN_DATABASE        = 107 ;
	
	/** Error - Error la APP no existe en la colección de Firebase */
	public static final int ERR_APP_NOT_EXISTS_IN_COLLECTION      = 108 ;
	
	/** Error - Error en la importación de aplicaciones */
	public static final int ERR_APPS_IMPORTS					  = 109 ;
	
	
}

