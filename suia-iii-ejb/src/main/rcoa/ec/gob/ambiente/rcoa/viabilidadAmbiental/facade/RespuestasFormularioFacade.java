package ec.gob.ambiente.rcoa.viabilidadAmbiental.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.RespuestaFormularioSnap;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class RespuestasFormularioFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	@SuppressWarnings("unchecked")
	public RespuestaFormularioSnap getPorTipoProyectoViabilidadPregunta(Integer idViabilidad, Integer idPregunta) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idProyectoViabilidad", idViabilidad);
		parameters.put("idPregunta", idPregunta);

		try {
			List<RespuestaFormularioSnap> lista = (List<RespuestaFormularioSnap>) crudServiceBean
					.findByNamedQuery(RespuestaFormularioSnap.GET_POR_TIPOPROYECTOVIABILIDAD_PREGUNTA, parameters);

			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista.get(0);
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public RespuestaFormularioSnap getPorViabilidadPregunta(Integer idViabilidad, Integer idPregunta, Integer idInforme) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idViabilidad", idViabilidad);
		parameters.put("idPregunta", idPregunta);
		parameters.put("idInforme", idInforme);

		try {
			List<RespuestaFormularioSnap> lista = (List<RespuestaFormularioSnap>) crudServiceBean
					.findByNamedQuery(RespuestaFormularioSnap.GET_POR_VIABILIDAD_PREGUNTA, parameters);

			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista.get(0);
			}
		} catch (Exception e) {
			return null;
		}
	}

	public RespuestaFormularioSnap guardar(RespuestaFormularioSnap respuesta) {
		return crudServiceBean.saveOrUpdate(respuesta);
	}
	
}
