package ec.gob.ambiente.suia.validaPago.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.configuration.service.ServiceBean;
import ec.gob.ambiente.suia.domain.PagoConfiguraciones;


@Stateless
public class PagoConfiguracionesFacade {
	
//	@EJB
//	private CrudServiceBean crudServiceBean;
	@EJB
	private ServiceBean serviceBean;
	
	@SuppressWarnings("unchecked")
	public List<PagoConfiguraciones> getPagoConfiguraciones() throws Exception {
		List<PagoConfiguraciones> result = null;
		result = (List<PagoConfiguraciones>)serviceBean.findAll(PagoConfiguraciones.class);
		
		return result;
	}

}