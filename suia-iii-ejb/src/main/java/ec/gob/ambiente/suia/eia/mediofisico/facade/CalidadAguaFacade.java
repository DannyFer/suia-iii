package ec.gob.ambiente.suia.eia.mediofisico.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CalidadAgua;
import ec.gob.ambiente.suia.domain.CoordenadaGeneral;
import ec.gob.ambiente.suia.domain.EiaOpciones;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.eia.descripcion.proyecto.facade.EiaOpcionesFacade;
import ec.gob.ambiente.suia.eia.mediofisico.service.CalidadAguaService;
import ec.gob.ambiente.suia.exceptions.ServiceException;

@Stateless
public class CalidadAguaFacade {
	@EJB
	private CalidadAguaService calidadAguaService;
	@EJB
	private CrudServiceBean crudServiceBean;
        @EJB
        private EiaOpcionesFacade eiaOpcionesFacade;

	public List<CalidadAgua> calidadAguaXEiaId(Integer eiaId) {
		return calidadAguaService.calidadAguaXEiaId(eiaId);
	}
	public void guardar(List<CalidadAgua> listaCalidadAgua,List<CalidadAgua> aguaBorradas,
                EstudioImpactoAmbiental es, EiaOpciones eiaOpciones) throws ServiceException{
	try{
            eiaOpcionesFacade.guardar(es, eiaOpciones);
		for(CalidadAgua calidadAgua: listaCalidadAgua)
		{
				calidadAgua.setIdLaboratorio(calidadAgua.getLaboratorio().getId());
				calidadAgua.setIdParametro(calidadAgua.getParametro().getId());
				CoordenadaGeneral coordenadaGeneral=calidadAgua.getCoordenadaGeneral();
				calidadAgua=crudServiceBean.saveOrUpdate(calidadAgua);
				coordenadaGeneral.setIdTable(calidadAgua.getId());
				coordenadaGeneral.setNombreTabla(CalidadAgua.class.getSimpleName());
				crudServiceBean.saveOrUpdate(coordenadaGeneral);
		}
		for (CalidadAgua calidadAgua : aguaBorradas) {
			crudServiceBean.saveOrUpdate(calidadAgua);
			calidadAgua.getCoordenadaGeneral().setEstado(false);
			crudServiceBean.saveOrUpdate(calidadAgua.getCoordenadaGeneral());
		}
		}catch(RuntimeException e){
			throw new ServiceException("Error al guardar calidad del agua",e);
		}
}

}
