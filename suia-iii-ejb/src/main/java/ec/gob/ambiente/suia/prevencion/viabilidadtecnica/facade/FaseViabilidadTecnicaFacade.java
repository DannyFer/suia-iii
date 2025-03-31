package ec.gob.ambiente.suia.prevencion.viabilidadtecnica.facade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import ec.gob.ambiente.suia.domain.FaseViabilidadTecnica;
import ec.gob.ambiente.suia.prevencion.viabilidadtecnica.service.FaseViabilidadTecnicaService;

@Stateless
public class FaseViabilidadTecnicaFacade implements Serializable {

@EJB
FaseViabilidadTecnicaService faseViabilidadTecnicaService;
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
private static final Logger LOG = Logger.getLogger(FaseViabilidadTecnicaFacade.class);
	

	
	public List<FaseViabilidadTecnica> obtenerLista(String grupo){
		List<FaseViabilidadTecnica> listaFaseViabilidadTecnicas  = new ArrayList<FaseViabilidadTecnica>();
		try {
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("grupo", grupo);
			listaFaseViabilidadTecnicas = faseViabilidadTecnicaService.obtenerLista(grupo);
			
			return listaFaseViabilidadTecnicas;
		} catch (Exception e) {
			LOG.error("Error al  consultar FaseViabilidadTecnica.", e);
		}
		return  listaFaseViabilidadTecnicas;

	}
	
}
