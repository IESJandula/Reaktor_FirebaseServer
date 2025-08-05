package es.iesjandula.reaktor.firebase_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.iesjandula.reaktor.firebase_server.models.HistoricoAplicacion;

public interface IHistoricoAplicacionRepository extends JpaRepository<HistoricoAplicacion, Long> {

}
