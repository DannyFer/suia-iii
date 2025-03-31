package ec.gob.ambiente.suia.tipocatalogo.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.TipoCatalogo;

@Stateless
public class TipoCatalogoService {
@EJB
private CrudServiceBean crudServiceBean;
@SuppressWarnings("unchecked")
public List<TipoCatalogo> obtenerTipoCatalogoXCodigo( String codigo) throws Exception{
	List<TipoCatalogo> catalogos = null;
	try {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("p_codigo", codigo);
		catalogos = (List<TipoCatalogo>) crudServiceBean
				.findByNamedQuery("TipoCatalogo.tipoCatalogoXCodigo",
						parameters);
	} catch (Exception e) {
		throw new Exception();
	}
	
	return catalogos;
}

}
