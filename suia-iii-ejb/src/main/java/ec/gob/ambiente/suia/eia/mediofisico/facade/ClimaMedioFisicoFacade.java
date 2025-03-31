package ec.gob.ambiente.suia.eia.mediofisico.facade;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.eia.descripcion.proyecto.facade.EiaOpcionesFacade;
import ec.gob.ambiente.suia.eia.mediofisico.service.ClimaMedioFisicoService;
import ec.gob.ambiente.suia.exceptions.ServiceException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class ClimaMedioFisicoFacade {
    @EJB
    private ClimaMedioFisicoService climaService;
    @EJB
    private CrudServiceBean crudServiceBean;
    @EJB
    private EiaOpcionesFacade eiaOpcionesFacade;

    public List<Clima> climaXEiaId(EstudioImpactoAmbiental estudioImpactoAmbiental) {
        return climaService.climaXEiaId(estudioImpactoAmbiental);
    }

    public void guardar(List<Clima> listaClima, List<Clima> climasBorrados) throws ServiceException {
        try {

            for (Clima clima : listaClima) {
                crudServiceBean.saveOrUpdate(clima);
            }
            for (Clima clima : climasBorrados) {
                crudServiceBean.saveOrUpdate(clima);
            }
        } catch (RuntimeException e) {
            throw new ServiceException("Error al guardar calidad del agua", e);
        }
    }

}
