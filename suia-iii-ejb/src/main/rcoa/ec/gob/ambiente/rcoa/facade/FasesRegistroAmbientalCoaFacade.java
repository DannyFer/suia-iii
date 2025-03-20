package ec.gob.ambiente.rcoa.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.model.FasesRegistroAmbiental;
import ec.gob.ambiente.rcoa.model.RegistroAmbientalRcoa;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class FasesRegistroAmbientalCoaFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
	public FasesRegistroAmbiental guardar(FasesRegistroAmbiental faseRegistro) {        
    	return crudServiceBean.saveOrUpdate(faseRegistro);        
	}
	
	@SuppressWarnings("unchecked")
	public List<FasesRegistroAmbiental> obtenerfasesPorRegistroAmbiental(RegistroAmbientalRcoa registroAmbiental)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("registroAmbientalId", registroAmbiental.getId());
		
		List<FasesRegistroAmbiental> lista = (List<FasesRegistroAmbiental>) crudServiceBean
					.findByNamedQuery(
							FasesRegistroAmbiental.GET_FASES_POR_REGISTROAMBIENTAL,
							parameters);
		if(lista.size() > 0 ){
			return lista;
		}
		return  new ArrayList<>();
	}

}
