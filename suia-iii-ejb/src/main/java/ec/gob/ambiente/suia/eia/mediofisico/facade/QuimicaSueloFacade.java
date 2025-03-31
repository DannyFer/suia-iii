package ec.gob.ambiente.suia.eia.mediofisico.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;




import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CoordenadaGeneral;
import ec.gob.ambiente.suia.domain.EiaOpciones;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.QuimicaSuelo;
import ec.gob.ambiente.suia.eia.descripcion.proyecto.facade.EiaOpcionesFacade;
import ec.gob.ambiente.suia.eia.mediofisico.service.QuimicaSueloService;
import ec.gob.ambiente.suia.exceptions.ServiceException;

@Stateless
public class QuimicaSueloFacade {
	@EJB
	private QuimicaSueloService quimicaSueloService;
	@EJB
	private CrudServiceBean crudServiceBean;
        @EJB
        private EiaOpcionesFacade eiaOpcionesFacade;
	public List<QuimicaSuelo> quimicaXEiaId(Integer eiaId) {
		return quimicaSueloService.quimicaXEiaId(eiaId);
	}

	public void guardar(List<QuimicaSuelo> listaQuimicaSuelo,List<QuimicaSuelo> quimicaBorradas, EstudioImpactoAmbiental es, EiaOpciones eiaOpciones)  throws ServiceException{
		try{
                    eiaOpcionesFacade.guardar(es, eiaOpciones);
		for(QuimicaSuelo quimicaSuelo:listaQuimicaSuelo)
		{
			quimicaSuelo.setIdLaboratorio(quimicaSuelo.getLaboratorio().getId());
			quimicaSuelo.setIdNormativa(quimicaSuelo.getNormativa().getId());
			if(quimicaSuelo.getUsoSuelo()!=null)
			quimicaSuelo.setIdUso(quimicaSuelo.getUsoSuelo().getId());
			quimicaSuelo.setIdParametro(quimicaSuelo.getParametro().getId());
			CoordenadaGeneral coordenadaGeneral=quimicaSuelo.getCoordenadaGeneral();
			quimicaSuelo=crudServiceBean.saveOrUpdate(quimicaSuelo);
			coordenadaGeneral.setIdTable(quimicaSuelo.getId());
			coordenadaGeneral.setNombreTabla(QuimicaSuelo.class.getSimpleName());
			crudServiceBean.saveOrUpdate(coordenadaGeneral);
		}
		for (QuimicaSuelo quimicaSuelo : quimicaBorradas) {
			crudServiceBean.saveOrUpdate(quimicaSuelo);
			quimicaSuelo.getCoordenadaGeneral().setEstado(false);
			crudServiceBean.saveOrUpdate(quimicaSuelo.getCoordenadaGeneral());
		}
		}catch (Exception e) {
			throw new ServiceException("Error al guardar caracteristicas quimicas del suelo");
		}
}

}
