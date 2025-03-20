package ec.gob.ambiente.suia.facilitador.facade;

import ec.gob.ambiente.suia.domain.FacilitadorProyecto;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.facilitador.service.FacilitadorProyectoServiceBean;

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
public class FacilitadorProyectoFacade {
    @EJB
    private FacilitadorProyectoServiceBean facilitadorProyectoServiceBean;


    public FacilitadorProyecto guardar(FacilitadorProyecto facilitadorProyecto) {
        return facilitadorProyectoServiceBean.guardar(facilitadorProyecto);

    }
    public FacilitadorProyecto guardarNoExiste(FacilitadorProyecto facilitadorProyecto) {
        return facilitadorProyectoServiceBean.guardarNoExiste(facilitadorProyecto);

    }


    public List<FacilitadorProyecto> listarFacilitadoresProyecto(ProyectoLicenciamientoAmbiental proyecto) {
        return facilitadorProyectoServiceBean.listarFacilitadoresProyecto(proyecto);
    }

    public  FacilitadorProyecto  obtenerFacilitadorResponsable(ProyectoLicenciamientoAmbiental proyecto) throws ServiceException {
        return facilitadorProyectoServiceBean.obtenerFacilitadorResponsable(proyecto);
    }
}
