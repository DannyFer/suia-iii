package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.Cultivo;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class CultivoFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public Cultivo guardar(Cultivo cultivo){
		return crudServiceBean.saveOrUpdate(cultivo);
	}
	
	@SuppressWarnings("unchecked")
	public List<Cultivo> listaCultivos(){
		List<Cultivo> lista = new ArrayList<>();
		try {
			
			lista = (ArrayList<Cultivo>) crudServiceBean
					.findByNamedQuery(Cultivo.GET_CULTIVOS, null);
			
			return lista;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	

}
