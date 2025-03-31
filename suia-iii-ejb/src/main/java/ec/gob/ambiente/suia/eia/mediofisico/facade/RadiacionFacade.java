package ec.gob.ambiente.suia.eia.mediofisico.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CoordenadaGeneral;
import ec.gob.ambiente.suia.domain.EiaOpciones;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.Radiacion;
import ec.gob.ambiente.suia.eia.descripcion.proyecto.facade.EiaOpcionesFacade;
import ec.gob.ambiente.suia.eia.mediofisico.service.RadiacionService;
import ec.gob.ambiente.suia.exceptions.ServiceException;

@Stateless
public class RadiacionFacade {
	@EJB
	private RadiacionService radiacionService;
	@EJB
	private CrudServiceBean crudServiceBean;
        @EJB
        private EiaOpcionesFacade eiaOpcionesFacade;

	public void guardarRadiacion(List<Radiacion> listaRadiaciones,List<Radiacion> radiacionesBorradas,
                EstudioImpactoAmbiental es, EiaOpciones eiaOpciones) throws ServiceException{
		try{
                    eiaOpcionesFacade.guardar(es, eiaOpciones);
		for(Radiacion radiacion:listaRadiaciones)
		{
			CoordenadaGeneral coordenadaGeneral=radiacion.getCoordenadaGeneral();
			radiacion=crudServiceBean.saveOrUpdate(radiacion);
			coordenadaGeneral.setIdTable(radiacion.getId());
			coordenadaGeneral.setNombreTabla(Radiacion.class.getSimpleName());
			crudServiceBean.saveOrUpdate(coordenadaGeneral);
		}
		for (Radiacion radiacion:radiacionesBorradas) {
			crudServiceBean.saveOrUpdate(radiacion);
			radiacion.getCoordenadaGeneral().setEstado(false);
			crudServiceBean.saveOrUpdate(radiacion.getCoordenadaGeneral());
		}
		}catch(Exception  e){
			throw new ServiceException("Error al guardar radiaciones");
		}
}

	public List<Radiacion> radiacionXEiaId(Integer eiaId) {
		return radiacionService.radiacionXEiaId(eiaId);
	}

}
