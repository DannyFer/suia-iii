package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.TipoObservacion;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class TipoObservacionFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	@SuppressWarnings("unchecked")
	public List<TipoObservacion> listaObservaciones(){
		List<TipoObservacion> lista = new ArrayList<>();
		try {
			
			lista = (List<TipoObservacion>)  crudServiceBean.findAll(TipoObservacion.class);
			
			return lista;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}

}
