package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.Plaga;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class PlagaFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;

	public Plaga guardar(Plaga plaga){
		return crudServiceBean.saveOrUpdate(plaga);
	}
	
	@SuppressWarnings("unchecked")
	public List<Plaga> listaPlagas(){
		List<Plaga> lista = new ArrayList<>();
		try {
			
			lista = (ArrayList<Plaga>) crudServiceBean
					.findByNamedQuery(Plaga.GET_PLAGAS, null);
			return lista;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}

}
