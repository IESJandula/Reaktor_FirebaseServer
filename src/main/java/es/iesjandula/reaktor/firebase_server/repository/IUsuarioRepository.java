package es.iesjandula.reaktor.firebase_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.iesjandula.reaktor.firebase_server.models.Usuario;

/**
 * @author Francisco Manuel Benítez Chico
 */
@Repository
public interface IUsuarioRepository extends JpaRepository<Usuario, String>
{

}
