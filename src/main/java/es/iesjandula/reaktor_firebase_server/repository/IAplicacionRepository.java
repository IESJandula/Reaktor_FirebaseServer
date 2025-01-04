package es.iesjandula.reaktor_firebase_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import es.iesjandula.reaktor_firebase_server.models.Aplicacion;

/**
 * Repositorio para acceder a las aplicaciones/servicios clientes.
 */
public interface IAplicacionRepository extends JpaRepository<Aplicacion, String>
{

}

