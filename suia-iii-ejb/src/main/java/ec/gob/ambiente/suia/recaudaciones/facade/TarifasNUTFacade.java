package ec.gob.ambiente.suia.recaudaciones.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.recaudaciones.model.TarifasNUT;

@Stateless
public class TarifasNUTFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public void guardarTarifasNUT(TarifasNUT tarifasNUT) throws ServiceException {
		crudServiceBean.saveOrUpdate(tarifasNUT);
	}
	
	@SuppressWarnings("unchecked")
	public List<TarifasNUT> buscarTarifasNUTPorNut(String nutCodigo) throws ServiceException {
		 
		Map<String, Object> params = new HashMap<String, Object>();
	    params.put("nutCodigo", nutCodigo);
	    List<TarifasNUT> result = (List<TarifasNUT>) crudServiceBean.findByNamedQuery(TarifasNUT.LISTAR_TARIFAS_NUT_POR_NUT,params);
	    if (result != null && !result.isEmpty()) {
			return result;
		}
	    return  null;
	}	
	
}