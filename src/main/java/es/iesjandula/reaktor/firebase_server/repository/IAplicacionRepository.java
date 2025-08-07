package es.iesjandula.reaktor.firebase_server.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import es.iesjandula.reaktor.firebase_server.models.Aplicacion;

/**
 * Repositorio para acceder a las aplicaciones/servicios clientes.
 */
public interface IAplicacionRepository extends JpaRepository<Aplicacion, String>
{

	Optional<Aplicacion> findByClientIdAndNombre(String clientId, String nombre) ;
	
}

