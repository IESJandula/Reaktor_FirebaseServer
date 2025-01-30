package es.iesjandula.reaktor.firebase_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Francisco Manuel Benítez Chico
 */
@SpringBootApplication
@ComponentScan(basePackages = {"es.iesjandula"})
public class ReaktorFirebaseServerApplication
{
	public static void main(String[] args)
	{
		SpringApplication.run(ReaktorFirebaseServerApplication.class, args);
	}
}
