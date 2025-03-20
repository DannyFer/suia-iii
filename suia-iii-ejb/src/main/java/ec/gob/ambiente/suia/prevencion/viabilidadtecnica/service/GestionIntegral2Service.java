package ec.gob.ambiente.suia.prevencion.viabilidadtecnica.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.EstudioViabilidadTecnica;
import ec.gob.ambiente.suia.domain.GestionIntegral2;
import ec.gob.ambiente.suia.domain.ViabilidadTecnicaMaterialesDiagnostico_2;

@Stateless
public class GestionIntegral2Service {
		
	@EJB
	private CrudServiceBean crudServiceBean;
	
	private static final Logger LOG = Logger.getLogger(GestionIntegral2Service.class);
	
	
	public void guardar(GestionIntegral2 gestionIntegral2, EstudioViabilidadTecnica estudioViabilidadTecnica){
		try {
			   crudServiceBean.saveOrUpdate(estudioViabilidadTecnica);
			   gestionIntegral2.setEstudioViabilidadTecnica(estudioViabilidadTecnica);
			   crudServiceBean.saveOrUpdate(gestionIntegral2);

			  } catch (Exception e) {
			   LOG.error("Error al guardar gestionIntegral2.", e);
			   e.printStackTrace();
			  }
		}
	 
	public void guardaViabilidadTecnicaMaterialesDiagnostico(ViabilidadTecnicaMaterialesDiagnostico_2 viabilidadTecnicaMaterialesDiagnostico){
		try {
			   crudServiceBean.saveOrUpdate(viabilidadTecnicaMaterialesDiagnostico);			 
			  } catch (Exception e) {
			   LOG.error("Error al guardar gestionIntegral2.", e);
			  }
		} 
	
	public ViabilidadTecnicaMaterialesDiagnostico_2 cargaViabilidadTecnicaMaterialesDiagnostico	(Integer idGestionIntegral, Integer idMaterialesDiagnostico) {
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("idGestionIntegral2", idGestionIntegral);
		params.put("idMaterialesDiagnostico", idMaterialesDiagnostico);
		@SuppressWarnings("unchecked")
		List<ViabilidadTecnicaMaterialesDiagnostico_2> tmp = (List<ViabilidadTecnicaMaterialesDiagnostico_2>) crudServiceBean.findByNamedQuery(ViabilidadTecnicaMaterialesDiagnostico_2.GET_DETALLE_MATERIALES_2, params);
		if(tmp.size()>0){
			return tmp.get(0);
		}else{
			return new ViabilidadTecnicaMaterialesDiagnostico_2();
		}
		}
	
		
	@SuppressWarnings("unchecked")
	public GestionIntegral2 cargarGestionIntegral(Integer idProceso) {
		Map<String, Object> params = new HashMap<String, Object>();		
		params.put("p_procesoId", idProceso);
		List<GestionIntegral2> listarDiagnosticoFactibilidadAnexo = (List<GestionIntegral2>) crudServiceBean.findByNamedQuery(
				GestionIntegral2.OBTENER_ESTUDIO_VIABILIDAD_POR_PROCESO, params);
		return listarDiagnosticoFactibilidadAnexo.get(0);
	}
	
}
