package ec.gob.ambiente.suia.prevencion.viabilidadtecnica.service;
//object create cls_mba
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.EstudioViabilidadTecnicaDiagnostico;
import ec.gob.ambiente.suia.domain.ViabilidadTecnicaMaterialesDiagnostico;

@Stateless
public class DiagnosticoFactibilidadAnexoVTService {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	public void guardaDiagnosticoFactibilidadAnexo(EstudioViabilidadTecnicaDiagnostico estudioViabilidadTecnicaDiagnostico) throws Exception {
		crudServiceBean.saveOrUpdate(estudioViabilidadTecnicaDiagnostico);
	}
	
	
	public EstudioViabilidadTecnicaDiagnostico cargarDiagnosticoFactibilidadAnexo(Integer idProceso) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("p_procesoId", idProceso);
		List<EstudioViabilidadTecnicaDiagnostico> listarDiagnosticoFactibilidadAnexo = (List<EstudioViabilidadTecnicaDiagnostico>) crudServiceBean.findByNamedQuery(
				EstudioViabilidadTecnicaDiagnostico.OBTENER_ESTUDIO_VIABILIDAD_POR_PROCESO, params);
		return listarDiagnosticoFactibilidadAnexo.get(0);
		
	}
	
	public void guardaViabilidadTecnicaMaterialesDiagnostico(ViabilidadTecnicaMaterialesDiagnostico viabilidadTecnicaMaterialesDiagnostico) throws Exception {
		crudServiceBean.saveOrUpdate(viabilidadTecnicaMaterialesDiagnostico);
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public ViabilidadTecnicaMaterialesDiagnostico cargaViabilidadTecnicaMaterialesDiagnostico
														(Integer idEstudioViabilidadTecnicaDiagnostico, Integer idMaterialesDiagnostico) {
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("idEstudioViabilidadTecnicaDiagnostico", idEstudioViabilidadTecnicaDiagnostico);
		params.put("idViabilidadTecnicaTipoMateriales", idMaterialesDiagnostico);
		
		return (ViabilidadTecnicaMaterialesDiagnostico) crudServiceBean.findByNamedQuery(ViabilidadTecnicaMaterialesDiagnostico.GET_DETALLE_MATERIALES, params).get(0);
	}
}
