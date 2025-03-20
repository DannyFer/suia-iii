package ec.gob.ambiente.suia.eia.mediofisico.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CalidadAire;
import ec.gob.ambiente.suia.domain.CoordenadaGeneral;
import ec.gob.ambiente.suia.domain.EiaOpciones;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.eia.descripcion.proyecto.facade.EiaOpcionesFacade;
import ec.gob.ambiente.suia.eia.mediofisico.service.CalidadAireService;
import ec.gob.ambiente.suia.exceptions.ServiceException;

@Stateless
public class CalidadAireFacade {
	@EJB
	private CalidadAireService calidadAireService;
	@EJB
	private CrudServiceBean crudServiceBean;
        @EJB
        private EiaOpcionesFacade eiaOpcionesFacade;
	public List<CalidadAire> calidadAireXEiaId(Integer eiaId) {
		return calidadAireService.calidadAireXEiaId(eiaId);
	}
	public void guardar(List<CalidadAire> listaCalidadAire,List<CalidadAire> aireBorradas,
                EstudioImpactoAmbiental es, EiaOpciones eiaOpciones) throws ServiceException {
	try{
            eiaOpcionesFacade.guardar(es, eiaOpciones);
		for(CalidadAire calidadAire:listaCalidadAire)
		{
			calidadAire.setIdLaboratorio(calidadAire.getLaboratorio().getId());
			calidadAire.setIdParametro(calidadAire.getParametro().getId());
			CoordenadaGeneral coordenadaGeneral=calidadAire.getCoordenadaGeneral();
			calidadAire=crudServiceBean.saveOrUpdate(calidadAire);
			coordenadaGeneral.setIdTable(calidadAire.getId());
			coordenadaGeneral.setNombreTabla(CalidadAire.class.getSimpleName());
			crudServiceBean.saveOrUpdate(coordenadaGeneral);
		}
		for (CalidadAire calidadAire : aireBorradas) {
			crudServiceBean.saveOrUpdate(calidadAire);
			calidadAire.getCoordenadaGeneral().setEstado(false);
			crudServiceBean.saveOrUpdate(calidadAire.getCoordenadaGeneral());
		}
	}catch(Exception ex){
		throw new ServiceException("Error al guardar calidad del aire");
	}
}
}
