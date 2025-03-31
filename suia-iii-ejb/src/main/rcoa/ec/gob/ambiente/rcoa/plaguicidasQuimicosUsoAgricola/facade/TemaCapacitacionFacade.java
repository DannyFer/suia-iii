package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.TemaCapacitacion;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class TemaCapacitacionFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public TemaCapacitacion guardar(TemaCapacitacion tema){
		return crudServiceBean.saveOrUpdate(tema);
	}

	@SuppressWarnings("unchecked")
	public List<TemaCapacitacion> listaTemasPadre(){
		List<TemaCapacitacion> lista = new ArrayList<>();
		
		lista = (ArrayList<TemaCapacitacion>) crudServiceBean
					.findByNamedQuery(TemaCapacitacion.GET_PADRES, null);

		return lista;
	}

}
