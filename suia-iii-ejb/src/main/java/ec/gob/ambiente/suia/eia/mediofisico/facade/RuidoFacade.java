package ec.gob.ambiente.suia.eia.mediofisico.facade;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CoordenadaGeneral;
import ec.gob.ambiente.suia.domain.EiaOpciones;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.Ruido;
import ec.gob.ambiente.suia.eia.descripcion.proyecto.facade.EiaOpcionesFacade;
import ec.gob.ambiente.suia.eia.mediofisico.service.RuidoService;
import ec.gob.ambiente.suia.exceptions.ServiceException;

@Stateless
public class RuidoFacade {
	@EJB
	CrudServiceBean crudServiceBean;
	@EJB
	private RuidoService ruidoService;
        @EJB
        private EiaOpcionesFacade eiaOpcionesFacade;
        
	public void guardarRuido(List<Ruido> listaRuido,List<Ruido> ruidoBorradas,
                EstudioImpactoAmbiental es, EiaOpciones eiaOpciones) throws ServiceException{
		try{
                    eiaOpcionesFacade.guardar(es, eiaOpciones);
		for(Ruido ruido: listaRuido)
		{
			ruido.setIdLaboratorio(ruido.getLaboratorio().getId());
			CoordenadaGeneral coordenadaGeneral=ruido.getCoordenadaGeneral();
			ruido=crudServiceBean.saveOrUpdate(ruido);
			coordenadaGeneral.setIdTable(ruido.getId());
			coordenadaGeneral.setNombreTabla(Ruido.class.getSimpleName());
			crudServiceBean.saveOrUpdate(coordenadaGeneral);
		}
		for (Ruido ruido : ruidoBorradas) {
			crudServiceBean.saveOrUpdate(ruido);
			ruido.getCoordenadaGeneral().setEstado(false);
			crudServiceBean.saveOrUpdate(ruido.getCoordenadaGeneral());
		}
	}catch(Exception e){
			throw new ServiceException("Error al guardar caracteristicas de ruido.");
		}
}

	public List<Ruido> ruidoXEiaId(Integer eiaId) {
		return ruidoService.ruidoXEiaId(eiaId);
	}
}
