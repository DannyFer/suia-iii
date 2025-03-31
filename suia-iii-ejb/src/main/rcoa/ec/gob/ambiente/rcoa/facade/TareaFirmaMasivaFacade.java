package ec.gob.ambiente.rcoa.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.model.TareaFirmaMasiva;
import ec.gob.ambiente.suia.administracion.facade.FlujosCategoriaFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Flujo;


@Stateless
public class TareaFirmaMasivaFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private SecuenciasFacade secuenciasFacade;
	
	@EJB
	private FlujosCategoriaFacade flujosCategoriaFacade;

	@SuppressWarnings("unchecked")
	public Boolean esTareaFirmaMasiva(String keyFlujo, String nombreTarea) {
		

		try {
			Flujo flujo = flujosCategoriaFacade.getFlujoPorIdProceso(keyFlujo);
			
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("idFlujo", flujo.getId());
			parameters.put("nombreTarea", nombreTarea);
			
			List<TareaFirmaMasiva> lista = (List<TareaFirmaMasiva>) crudServiceBean
					.findByNamedQuery(TareaFirmaMasiva.GET_POR_FLUJO_TAREA, parameters);

			if (lista == null || lista.isEmpty()) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			return false;
		}	
	}
	
	@SuppressWarnings("unchecked")
	public TareaFirmaMasiva obtenerPorFlujoTarea(Integer idFlujo, String nombreTarea) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idFlujo", idFlujo);
		parameters.put("nombreTarea", nombreTarea);

		try {
			List<TareaFirmaMasiva> lista = (List<TareaFirmaMasiva>) crudServiceBean
					.findByNamedQuery(TareaFirmaMasiva.GET_POR_FLUJO_TAREA, parameters);

			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista.get(0);
			}
		} catch (Exception e) {
			return null;
		}	
	}

}
