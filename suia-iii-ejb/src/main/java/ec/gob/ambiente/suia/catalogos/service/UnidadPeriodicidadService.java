package ec.gob.ambiente.suia.catalogos.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.UnidadPeriodicidad;

@Stateless
public class UnidadPeriodicidadService {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public UnidadPeriodicidad unidadFrecuencia(int id) {
		return crudServiceBean.find(UnidadPeriodicidad.class, id);
	}
	
	@SuppressWarnings("unchecked")
	public List<UnidadPeriodicidad> listaUnidadesPeriodicidad() throws Exception {
		return (List<UnidadPeriodicidad>) crudServiceBean.findAll(UnidadPeriodicidad.class);
	}

}
