package ec.gob.ambiente.suia.facilitador.facade;

import ec.gob.ambiente.suia.domain.FacilitadorProyectoLog;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.facilitador.service.FacilitadorProyectoLogServiceBean;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

/**
 * *
 * <p/>
 * <b> Facade para las operaciones con los Facilitadores</b>
 *
 * @author frank torres rodriguez
 * @version Revision: 1.0
 *          <p>
 *          [Autor: frank torres rodriguez, Fecha: 01/09/2015]
 *          </p>
 */
@Stateless
public class FacilitadorProyectoLogFacade {
    @EJB
    private FacilitadorProyectoLogServiceBean facilitadorProyectoLogServiceBean;

    public Integer buscarRechazos(Usuario usuario) {
        return facilitadorProyectoLogServiceBean.buscarRechazos(usuario);
    }

    public FacilitadorProyectoLog guardar(FacilitadorProyectoLog facilitadorProyectoLog) {
        return facilitadorProyectoLogServiceBean.guardar(facilitadorProyectoLog);

    }


    public List<FacilitadorProyectoLog> listarFacilitadoresRechazadosProyecto(ProyectoLicenciamientoAmbiental proyecto) {
        return facilitadorProyectoLogServiceBean.listarFacilitadoresRechazadosProyecto(proyecto);
    }
}
