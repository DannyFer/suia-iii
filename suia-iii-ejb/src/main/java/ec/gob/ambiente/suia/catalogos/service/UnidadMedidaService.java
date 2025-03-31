package ec.gob.ambiente.suia.catalogos.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.UnidadMedida;

@Stateless
public class UnidadMedidaService {

	@EJB
	private CrudServiceBean crudServiceBean;
	

	public UnidadMedida unidadMedida(int id) {
		return crudServiceBean.find(UnidadMedida.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<UnidadMedida> listaUnidadesMedida() throws Exception {
		return (List<UnidadMedida>) crudServiceBean.findAll(UnidadMedida.class);
	}
	@SuppressWarnings("unchecked")
	public List<UnidadMedida> listaUnidadesMedidaTipo(String tipo) throws Exception {
		List<UnidadMedida> listaUnidades = (List<UnidadMedida>) crudServiceBean
	            .getEntityManager()
	            .createQuery("From UnidadMedida c where c.estado = true and c.tipo =:tipo").setParameter("tipo", tipo).getResultList();
		if (listaUnidades != null && !listaUnidades.isEmpty()) {
			return listaUnidades;
		} else {
			return null;
		}
	}
	 @SuppressWarnings("unchecked")
	    public List<UnidadMedida> listaUnidadesMedidaTipo020() throws Exception {
	        List<UnidadMedida> listaUnidades = (List<UnidadMedida>) crudServiceBean
	                .getEntityManager()
	                .createQuery("From UnidadMedida c where c.estado = true and c.nombre ='Toneladas'").getResultList();
	        if (listaUnidades != null && !listaUnidades.isEmpty()) {
	            return listaUnidades;
	        } else {
	            return null;
	        }
	    }    
}
