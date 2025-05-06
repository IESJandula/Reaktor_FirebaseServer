package es.iesjandula.reaktor.firebase_server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.iesjandula.reaktor.base.security.models.DtoUsuarioBase;
import es.iesjandula.reaktor.firebase_server.models.Usuario;

/**
 * @author Francisco Manuel Benítez Chico
 */
@Repository
public interface IUsuarioRepository extends JpaRepository<Usuario, String>
{
	@Query("SELECT new es.iesjandula.reaktor.base.security.models.DtoUsuarioBase(u.email, u.nombre, u.apellidos, u.departamento) "   +
		   "FROM Usuario u")
	List<DtoUsuarioBase> obtenerInfoUsuarios() ;
	
	
	@Query("SELECT new es.iesjandula.reaktor.base.security.models.DtoUsuarioBase(u.email, u.nombre, u.apellidos, u.departamento) "   +
			   "FROM Usuario u where u.email = :email")
	DtoUsuarioBase obtenerInfoUsuario(@Param("email") String email);
}
