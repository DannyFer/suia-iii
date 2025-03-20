package ec.gob.ambiente.suia.prevencion.viabilidadtecnica.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.EstudioViabilidadTecnica;
import ec.gob.ambiente.suia.domain.EstudioViabilidadTecnicaDiagnostico;

@Stateless
public class DisenoDefinitivoVTService {

	/**
	 * 
	 */

	@EJB
	private CrudServiceBean crudServiceBean;

	public void guardar(EstudioViabilidadTecnica estudio,
			EstudioViabilidadTecnicaDiagnostico diagnostico) {
		crudServiceBean.saveOrUpdate(estudio);
		diagnostico.setEstudioViabilidadTecnica(estudio);

		crudServiceBean.saveOrUpdate(diagnostico);

	}
	
	@SuppressWarnings("unchecked")
	public EstudioViabilidadTecnicaDiagnostico cargarDiagnosticoFactibilidad(Integer idProceso) {
		  Map<String, Object> params = new HashMap<String, Object>();
		  params.put("p_procesoId", idProceso);
		  List<EstudioViabilidadTecnicaDiagnostico> listarDiagnosticoFactibilidadAnexo = (List<EstudioViabilidadTecnicaDiagnostico>) crudServiceBean.findByNamedQuery(
		    EstudioViabilidadTecnicaDiagnostico.OBTENER_ESTUDIO_VIABILIDAD_POR_PROCESO, params);
		  return listarDiagnosticoFactibilidadAnexo.get(0);
		  
	}
}
