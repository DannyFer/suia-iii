package ec.gob.ambiente.suia.eia.mediofisico.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CoordenadaGeneral;
import ec.gob.ambiente.suia.domain.FisicoMecanicaSuelo;
import ec.gob.ambiente.suia.eia.descripcion.proyecto.facade.EiaOpcionesFacade;
import ec.gob.ambiente.suia.eia.mediofisico.service.FisicoMecanicaService;
import ec.gob.ambiente.suia.exceptions.ServiceException;

@Stateless
public class FisicoMecanicaFacade {
    @EJB
    private CrudServiceBean crudServiceBean;
    @EJB
    private FisicoMecanicaService fisicoMecanicaService;
    @EJB
    private EiaOpcionesFacade eiaOpcionesFacade;

    public void guardarFisicoMecanica(List<FisicoMecanicaSuelo> listaFisicoMecanica, List<FisicoMecanicaSuelo> fisicoMecanicasBorras) throws ServiceException {
        try {

            for (FisicoMecanicaSuelo fisico : listaFisicoMecanica) {
                CoordenadaGeneral coordenadaGeneral = fisico.getCoordenadaGeneral();
                fisico = crudServiceBean.saveOrUpdate(fisico);
                coordenadaGeneral.setIdTable(fisico.getId());
                coordenadaGeneral.setNombreTabla(FisicoMecanicaSuelo.class.getSimpleName());
                crudServiceBean.saveOrUpdate(coordenadaGeneral);
            }
            for (FisicoMecanicaSuelo fisicoMecanicaSuelo : fisicoMecanicasBorras) {
                crudServiceBean.delete(fisicoMecanicaSuelo);
                fisicoMecanicaSuelo.getCoordenadaGeneral().setEstado(false);
                crudServiceBean.delete(fisicoMecanicaSuelo.getCoordenadaGeneral());
            }

        } catch (Exception e) {
            throw new ServiceException("Error al guardar fisico mecanica");
        }
    }

    public List<FisicoMecanicaSuelo> fisoMecanicaXEiaId(Integer eiaId) {
        return fisicoMecanicaService.fisoMecanicaXEiaId(eiaId);
    }
}
