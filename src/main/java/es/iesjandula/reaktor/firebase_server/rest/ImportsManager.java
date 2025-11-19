package es.iesjandula.reaktor.firebase_server.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus ;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import es.iesjandula.reaktor.base.utils.BaseConstants;
import es.iesjandula.reaktor.firebase_server.models.Aplicacion;
import es.iesjandula.reaktor.firebase_server.models.Usuario;
import es.iesjandula.reaktor.firebase_server.repository.IAplicacionRepository;
import es.iesjandula.reaktor.firebase_server.repository.IUsuarioRepository;
import es.iesjandula.reaktor.firebase_server.utils.Constants;
import es.iesjandula.reaktor.firebase_server.utils.FirebaseServerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/firebase/imports")
public class ImportsManager
{
	/**
	 * Logger of the class
	 */
	private static final Logger log = LoggerFactory.getLogger(ImportsManager.class);

	@Autowired
	private IUsuarioRepository usuarioRepository ;
	
	@Autowired
	private IAplicacionRepository aplicacionRepository ;
	
    @PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@RequestMapping(method = RequestMethod.POST, value = "/users")
    public ResponseEntity<?> importarUsuarios(@RequestParam("file") MultipartFile file)
	{
        try
        {
            // Validamos que el archivo no esté vacío
            if (file.isEmpty())
            {
    	    	String errorString = "El fichero con los usuarios está vacío" ;
    	    	
    	    	log.error(errorString) ;
    	    	throw new FirebaseServerException(Constants.ERR_USERS_IMPORTS, errorString) ;
            }

            // Procesamos el archivo CSV
            this.importarUsuariosProcesarCsv(file) ;
            
            // Devolvemos el 200
            return ResponseEntity.ok().build() ;
        }
        catch (FirebaseServerException firebaseServerException)
        {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(firebaseServerException.getBodyExceptionMessage()) ;
        }
        catch (Exception exception)
        {
	        FirebaseServerException printersServerException = 
	        		new FirebaseServerException(BaseConstants.ERR_GENERIC_EXCEPTION_CODE, 
	        									BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "importarUsuarios",
											    exception) ;

			log.error(BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "importarUsuarios", printersServerException) ;
			return ResponseEntity.status(500).body(printersServerException.getBodyExceptionMessage()) ;
        }
    }

	/**
	 * @param file multipart file
	 * @throws FirebaseServerException con un error
	 */
	private void importarUsuariosProcesarCsv(MultipartFile file) throws FirebaseServerException
	{
		InputStreamReader inputStreamReader = null ;
		BufferedReader bufferedReader 		= null ;
		
		try
		{
			inputStreamReader = new InputStreamReader(file.getInputStream(), Charset.forName("UTF-8")) ;
			bufferedReader = new BufferedReader(inputStreamReader) ;

			// Borrar todos los profesores menos los administradores
			this.usuarioRepository.borrarProfesoresConRolNoAdministrador() ;
			
			// Nos saltamos la primera línea de cabecera
			String linea = bufferedReader.readLine() ;
			
			// Ahora sí leemos la primera línea
			linea = bufferedReader.readLine() ;
			
			// Iteramos mientras haya líneas
			while (linea != null)
			{
				// Obtenemos la instancia del usuario a partir de la línea procesada
				Usuario usuario = this.importarUsuariosProcesarCsvProcesarLinea(linea) ;
				
				// Guardamos el usuario
				this.usuarioRepository.saveAndFlush(usuario) ;
				
				// Logueamos
				log.info("Usuario insertado: " + usuario) ;
				
				// Ahora sí leemos la primera línea
				linea = bufferedReader.readLine() ;
			}
		}
		catch (IOException ioException)
		{
			String errorString = "Error en la lectura del fichero CSV" ;
			
			log.error(errorString, ioException) ;
			throw new FirebaseServerException(Constants.ERR_USERS_IMPORTS, errorString, ioException) ;
		}
		finally
		{
			this.cierreFlujos(inputStreamReader, bufferedReader) ;
		}
	}
	
	/**
	 * @param linea linea a parsear
	 * @return una instancia del usuario con la línea parseada
	 * @throws FirebaseServerException con un error
	 */
	private Usuario importarUsuariosProcesarCsvProcesarLinea(String linea) throws FirebaseServerException
	{
		String[] fields = linea.split(",") ;
		
		if (fields.length != 6)
		{
			String errorString = "Hay una fila del CSV que no posee el número de elementos esperados: " + linea ;
			
			log.error(errorString) ;
			throw new FirebaseServerException(Constants.ERR_USERS_IMPORTS, errorString) ;
		}
		
		// Parseamos cada uno de los campos
		String email 	       = fields[0] ;
		String nombre 	       = fields[1] ;
		String apellidos       = fields[2] ;
		String departamento    = fields[3] ;
		String fechaNacimiento = fields[4] ;
		List<String> roles     = Arrays.asList(fields[5].split("\\|")) ;
		
		// Validamos roles del usuario
		for (String role : roles)
		{
			// Si el role no existe, devolvemos un error
			if (!BaseConstants.ROLES_USUARIOS_LIST.contains(role))
			{
				String errorString = "Rol inválido en el CSV: " + role ;
				
				log.error(errorString) ;
				throw new FirebaseServerException(Constants.ERR_USERS_IMPORTS, errorString) ;
			}
		}
		
		// Creamos una instancia del usuario
		Usuario usuario = new Usuario() ;

		// Seteamos los campos
		usuario.setEmail(email) ;
		usuario.setNombre(nombre) ;
		usuario.setApellidos(apellidos) ;
		usuario.setDepartamento(departamento) ;
		usuario.setFechaNacimiento(fechaNacimiento) ;
		usuario.setRolesList(roles) ;

		// Devolvemos una instancia del usuario
		return usuario ;
	}
	
    @PreAuthorize("hasRole('" + BaseConstants.ROLE_ADMINISTRADOR + "')")
	@RequestMapping(method = RequestMethod.POST, value = "/apps")
    public ResponseEntity<?> importarApps(@RequestParam("file") MultipartFile file)
	{
        try
        {
            // Validamos que el archivo no esté vacío
            if (file.isEmpty())
            {
    	    	String errorString = "El fichero con las apps está vacío" ;
    	    	
    	    	log.error(errorString) ;
    	    	throw new FirebaseServerException(Constants.ERR_APPS_IMPORTS, errorString) ;
            }

            // Procesamos el archivo CSV
            this.importarAppsProcesarCsv(file) ;
            
            // Devolvemos el 200
            return ResponseEntity.ok().build() ;
        }
        catch (FirebaseServerException firebaseServerException)
        {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(firebaseServerException.getBodyExceptionMessage()) ;
        }
        catch (Exception exception)
        {
	        FirebaseServerException printersServerException = 
	        		new FirebaseServerException(BaseConstants.ERR_GENERIC_EXCEPTION_CODE, 
	        									BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "importarUsuarios",
											    exception) ;

			log.error(BaseConstants.ERR_GENERIC_EXCEPTION_MSG + "importarUsuarios", printersServerException) ;
			return ResponseEntity.status(500).body(printersServerException.getBodyExceptionMessage()) ;
        }
    }
    
	/**
	 * @param file multipart file
	 * @throws FirebaseServerException con un error
	 */
	private void importarAppsProcesarCsv(MultipartFile file) throws FirebaseServerException
	{
		InputStreamReader inputStreamReader = null ;
		BufferedReader bufferedReader 		= null ;
		
		try
		{
			inputStreamReader = new InputStreamReader(file.getInputStream(), Charset.forName("UTF-8")) ;
			bufferedReader = new BufferedReader(inputStreamReader) ;
			
			// Nos saltamos la primera línea de cabecera
			String linea = bufferedReader.readLine() ;
			
			// Ahora sí leemos la primera línea
			linea = bufferedReader.readLine() ;
			
			// Iteramos mientras haya líneas
			while (linea != null)
			{
				// Obtenemos la instancia de la aplicación a partir de la línea procesada
				Aplicacion aplicacion = this.importarAppsProcesarCsvProcesarLinea(linea) ;
				
				// Guardamos la aplicación
				this.aplicacionRepository.saveAndFlush(aplicacion) ;
				
				// Logueamos
				log.info("Aplicación insertada: " + aplicacion) ;
				
				// Ahora sí leemos la primera línea
				linea = bufferedReader.readLine() ;
			}
		}
		catch (IOException ioException)
		{
			String errorString = "Error en la lectura del fichero CSV" ;
			
			log.error(errorString, ioException) ;
			throw new FirebaseServerException(Constants.ERR_APPS_IMPORTS, errorString, ioException) ;
		}
		finally
		{
			this.cierreFlujos(inputStreamReader, bufferedReader) ;
		}
	}
	
	/**
	 * @param linea linea a parsear
	 * @return una instancia de la aplicación con la línea parseada
	 * @throws FirebaseServerException con un error
	 */
	private Aplicacion importarAppsProcesarCsvProcesarLinea(String linea) throws FirebaseServerException
	{
		String[] fields = linea.split(",") ;
		
		if (fields.length != 3)
		{
			String errorString = "Hay una fila del CSV que no posee el número de elementos esperados: " + linea ;
			
			log.error(errorString) ;
			throw new FirebaseServerException(Constants.ERR_APPS_IMPORTS, errorString) ;
		}
		
		// Parseamos cada uno de los campos
		String clientId 	= fields[0] ;
		String nombre   	= fields[1] ;
		List<String> roles  = Arrays.asList(fields[2].split("\\|")) ;
		
		// Validamos roles del usuario
		for (String role : roles)
		{
			// Si el role no existe, devolvemos un error
			if (!BaseConstants.ROLES_APPS_LIST.contains(role))
			{
				String errorString = "Rol inválido en el CSV: " + role ;
				
				log.error(errorString) ;
				throw new FirebaseServerException(Constants.ERR_APPS_IMPORTS, errorString) ;
			}
		}
		
		// Creamos una instancia de la aplicación y seteamos los campos
		Aplicacion aplicacion = new Aplicacion() ;
		
		aplicacion.setClientId(clientId) ;
		aplicacion.setNombre(nombre) ;
		aplicacion.setRolesList(roles) ;
		
		// Devolvemos una instancia de la aplicación
		return aplicacion ;
	}
    
	/**
	 * @param inputStreamReader input stream reader
	 * @param bufferedReader buffered reader
	 * @throws FirebaseServerException con un error
	 */
	private void cierreFlujos(InputStreamReader inputStreamReader, BufferedReader bufferedReader) throws FirebaseServerException
	{
		if (bufferedReader != null)
		{
			try
			{
				bufferedReader.close() ;
			}
			catch (IOException ioException)
			{
				String errorString = "Error en el cierre del flujo bufferedReader en la lectura del fichero CSV" ;
				
				log.error(errorString, ioException) ;
				throw new FirebaseServerException(Constants.ERR_USERS_IMPORTS, errorString, ioException) ;
			}
		}
		
		if (inputStreamReader != null)
		{
			try
			{
				inputStreamReader.close() ;
			}
			catch (IOException ioException)
			{
				String errorString = "Error en el cierre del flujo inputStreamReader en la lectura del fichero CSV" ;
				
				log.error(errorString, ioException) ;
				throw new FirebaseServerException(Constants.ERR_USERS_IMPORTS, errorString, ioException) ;
			}
		}
	}
}
