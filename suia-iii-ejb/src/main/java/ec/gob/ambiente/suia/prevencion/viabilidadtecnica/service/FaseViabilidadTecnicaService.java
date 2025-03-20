package ec.gob.ambiente.suia.prevencion.viabilidadtecnica.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.FaseViabilidadTecnica;

@Stateless
public class FaseViabilidadTecnicaService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

private static final Logger LOG = Logger.getLogger(FaseViabilidadTecnicaService.class);
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	@SuppressWarnings("unchecked")
	public List<FaseViabilidadTecnica> obtenerLista(String grupo){
		List<FaseViabilidadTecnica> listaFaseViabilidadTecnicas  = new ArrayList<FaseViabilidadTecnica>();
		try {
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("grupo", grupo);
			listaFaseViabilidadTecnicas = (List<FaseViabilidadTecnica>) crudServiceBean.findByNamedQuery(FaseViabilidadTecnica.LISTAR_POR_GRUPO, params);

			
			return listaFaseViabilidadTecnicas;
		} catch (Exception e) {
			LOG.error("Error al  consultar FaseViabilidadTecnica.", e);
		}
		return  listaFaseViabilidadTecnicas;

	}
	
}
