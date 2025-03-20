package ec.gob.ambiente.suia.prevencion.categoria1.facade;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CertificadoAmbientalInformeForestalFormaCoordenada;

@Stateless
public class InformeInspeccionForestalFormaCAFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	
	private static final Logger LOG = Logger.getLogger(InformeInspeccionForestalFormaCAFacade.class);	
			
	public void guardarCoordenada(List<CertificadoAmbientalInformeForestalFormaCoordenada> coordenadas) {
		crudServiceBean.saveOrUpdate(coordenadas);
	}

	@SuppressWarnings("unchecked")
	public CertificadoAmbientalInformeForestalFormaCoordenada getCoordenadasPorIdInformeForestal(int idInformeForestal) {
		Map<String, Object> parameters = new ConcurrentHashMap<String, Object>();
		parameters.put("idInformeForestal", idInformeForestal);

		List<CertificadoAmbientalInformeForestalFormaCoordenada> result = (List<CertificadoAmbientalInformeForestalFormaCoordenada>) crudServiceBean.findByNamedQuery(CertificadoAmbientalInformeForestalFormaCoordenada.FIND_BY_REPORT,parameters);
		if (result != null && !result.isEmpty()) {
			return result.get(0);
		}
		return null;
	}    
    
    
	
	
}