package ec.gob.ambiente.suia.fichatecnica.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.FichaTecnica;

@Stateless
public class FichaTecnicaService {
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public FichaTecnica obtenerFichaTecnica(Integer id){
		return crudServiceBean.find(FichaTecnica.class, id);
	}
	public List<FichaTecnica>  fichaTecnicaXEiaId(Integer eiaId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("eiaId", eiaId);
		
		@SuppressWarnings("unchecked")
		List<FichaTecnica> listaFicha = (List<FichaTecnica>) crudServiceBean
				.findByNamedQuery(FichaTecnica.LISTAR_POR_ID_EIA, params);
		return listaFicha;
	}

}
