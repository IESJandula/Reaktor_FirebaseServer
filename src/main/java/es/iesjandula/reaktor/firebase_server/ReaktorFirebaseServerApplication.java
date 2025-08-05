package es.iesjandula.reaktor.firebase_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Francisco Manuel Benítez Chico
 */
@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"es.iesjandula"})
public class ReaktorFirebaseServerApplication
{
	public static void main(String[] args)
	{
		SpringApplication.run(ReaktorFirebaseServerApplication.class, args);
	}
}
