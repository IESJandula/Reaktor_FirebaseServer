package es.iesjandula.reaktor.firebase_server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import es.iesjandula.reaktor.firebase_server.dto.DtoInfoUsuario;
import es.iesjandula.reaktor.firebase_server.models.Usuario;

/**
 * @author Francisco Manuel Benítez Chico
 */
@Repository
public interface IUsuarioRepository extends JpaRepository<Usuario, String>
{
	@Query("SELECT new es.iesjandula.reaktor.firebase_server.dto.DtoInfoUsuario(u.email, u.nombre, u.apellidos) "   +
		   "FROM Usuario u")
	List<DtoInfoUsuario> obtenerInfoUsuarios() ;
}
