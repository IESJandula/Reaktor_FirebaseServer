package es.iesjandula.reaktor.firebase_server.config;

import java.io.FileInputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class FirebaseConfig
{
	@Value("${reaktor.googleCredentialsFile}")
	private String googleCredentialsFile ;
	
    @Bean
    public FirebaseApp initializeFirebase() throws IOException
    {
        // Cargar las credenciales de Firebase desde el archivo en el sistema de archivos
        FileInputStream serviceAccount = new FileInputStream(this.googleCredentialsFile) ;
        
    	// Google Credentials desde fichero
    	GoogleCredentials googleCredentials = GoogleCredentials.fromStream(serviceAccount) ;
    	
    	// Seteamos Google Credentials en Firebase Options
    	FirebaseOptions firebaseOptions = FirebaseOptions.builder()
    													 .setCredentials(googleCredentials)
    													 .build() ;
    	
    	// Creamos una Firebase App con las opciones anteriores
    	return FirebaseApp.initializeApp(firebaseOptions) ;
    }
}
