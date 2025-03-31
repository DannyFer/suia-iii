package ec.gob.ambiente.suia.prevencion.viabilidadtecnica.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.EstudioViabilidadTecnicaDiagnostico;
import ec.gob.ambiente.suia.domain.ViabilidadTecnicaMaterialesDiagnostico;
import ec.gob.ambiente.suia.domain.ViabilidadTecnicaTipoMateriales;
import ec.gob.ambiente.suia.prevencion.viabilidadtecnica.service.DiagnosticoFactibilidadAnexoVTService;

@Stateless
public class DiagnosticoFactibilidadAnexoVTFacade {

	
	
	@EJB
	private DiagnosticoFactibilidadAnexoVTService diagnosticoFactibilidadAnexoVTService;
	@EJB
	private CrudServiceBean crudServiceBean;
	public void guardaDiagnosticoFactibilidadAnexo(EstudioViabilidadTecnicaDiagnostico estudioViabilidadTecnicaDiagnostico) throws Exception {
		diagnosticoFactibilidadAnexoVTService.guardaDiagnosticoFactibilidadAnexo(estudioViabilidadTecnicaDiagnostico);
	}
	public EstudioViabilidadTecnicaDiagnostico cargarDiagnosticoFactibilidadAnexo(Integer idEstudioViabilidadTecnica) throws Exception {
		return diagnosticoFactibilidadAnexoVTService.cargarDiagnosticoFactibilidadAnexo(idEstudioViabilidadTecnica);
	}
	public List<ViabilidadTecnicaTipoMateriales> datosViabilidadTecnicaTipoMateriales() {
		return (List<ViabilidadTecnicaTipoMateriales>) crudServiceBean.findAll(ViabilidadTecnicaTipoMateriales.class);
	}	
	public void guardaViabilidadTecnicaMaterialesDiagnostico(ViabilidadTecnicaMaterialesDiagnostico viabilidadTecnicaMaterialesDiagnostico) throws Exception {
		diagnosticoFactibilidadAnexoVTService.guardaViabilidadTecnicaMaterialesDiagnostico(viabilidadTecnicaMaterialesDiagnostico);
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public ViabilidadTecnicaMaterialesDiagnostico cargaViabilidadTecnicaMaterialesDiagnostico
														(Integer idEstudioViabilidadTecnicaDiagnostico, Integer idMaterialesDiagnostico) {
		
		return diagnosticoFactibilidadAnexoVTService.cargaViabilidadTecnicaMaterialesDiagnostico(idEstudioViabilidadTecnicaDiagnostico, idMaterialesDiagnostico);
		
	}
	
	
}