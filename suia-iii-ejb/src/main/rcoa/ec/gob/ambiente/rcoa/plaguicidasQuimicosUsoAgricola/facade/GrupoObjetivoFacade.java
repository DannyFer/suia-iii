package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.GrupoObjetivo;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class GrupoObjetivoFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public GrupoObjetivo guardar(GrupoObjetivo grupo){
		return crudServiceBean.saveOrUpdate(grupo);
	}

	@SuppressWarnings("unchecked")
	public List<GrupoObjetivo> listaGruposActivos(){
		List<GrupoObjetivo> lista = new ArrayList<>();
		
		lista = (ArrayList<GrupoObjetivo>) crudServiceBean
					.findByNamedQuery(GrupoObjetivo.GET_ACTIVOS, null);

		return lista;
	}

}
