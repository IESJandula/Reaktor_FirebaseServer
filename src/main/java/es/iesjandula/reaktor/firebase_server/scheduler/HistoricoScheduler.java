package es.iesjandula.reaktor.firebase_server.scheduler;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import es.iesjandula.reaktor.firebase_server.models.Aplicacion;
import es.iesjandula.reaktor.firebase_server.models.HistoricoAplicacion;
import es.iesjandula.reaktor.firebase_server.repository.IAplicacionRepository;
import es.iesjandula.reaktor.firebase_server.repository.IHistoricoAplicacionRepository;

@Component
public class HistoricoScheduler {

    @Autowired
    private IAplicacionRepository aplicacionRepository;

    @Autowired
    private IHistoricoAplicacionRepository historicoAplicacionRepository;

    @Scheduled(cron = "0 0 0 * * ?") // todos los días a las 00:00
    public void generarHistorico() {
        List<Aplicacion> aplicaciones = aplicacionRepository.findAll();
        LocalDate ayer = LocalDate.now().minusDays(1);

        for (Aplicacion app : aplicaciones) {
            for (String tipo : List.of("Calendar", "Email", "Web")) {
                HistoricoAplicacion historico = new HistoricoAplicacion();
                historico.setNombre(app.getNombre());
                historico.setFecha(ayer);
                historico.setNotificaciones(app.getNotif_hoy()); // simplificado
                historico.setTipo(tipo);

                historicoAplicacionRepository.save(historico);
            }

            app.setNotif_hoy(0);
            aplicacionRepository.save(app);
        }
    }
}
