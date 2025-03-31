package ec.gob.ambiente.suia.prevencion.viabilidadtecnica.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.EstudioViabilidadTecnica;
import ec.gob.ambiente.suia.domain.GestionIntegral2;
import ec.gob.ambiente.suia.domain.ViabilidadTecnicaMaterialesDiagnostico_2;
import ec.gob.ambiente.suia.domain.ViabilidadTecnicaTipoMateriales;
import ec.gob.ambiente.suia.prevencion.viabilidadtecnica.service.GestionIntegral2Service;


@Stateless
public class GestionIntegral2Facade {
	
	
	@EJB
	private GestionIntegral2Service gestionIntegral2Service;
	
	
	@EJB
	 private CrudServiceBean crudServiceBean;
	
	public void guardarGestionIntegral2(GestionIntegral2 gestionIntegral2, EstudioViabilidadTecnica estudioViabilidadTecnica) 
	{
		gestionIntegral2Service.guardar(gestionIntegral2, estudioViabilidadTecnica);
	}
	

	 public void guardaViabilidadTecnicaMaterialesDiagnostico(ViabilidadTecnicaMaterialesDiagnostico_2 viabilidadTecnicaMaterialesDiagnostico) throws Exception {
		 gestionIntegral2Service.guardaViabilidadTecnicaMaterialesDiagnostico(viabilidadTecnicaMaterialesDiagnostico);
	 }
	

	 public ViabilidadTecnicaMaterialesDiagnostico_2 cargaViabilidadTecnicaMaterialesDiagnostico(Integer idGestionIntegral, Integer idMaterialesDiagnostico) {
		 return gestionIntegral2Service.cargaViabilidadTecnicaMaterialesDiagnostico(idGestionIntegral, idMaterialesDiagnostico);
	 }

	 public GestionIntegral2 cargarGestionIntegral(Integer idGestionIntegral) throws Exception {
			return gestionIntegral2Service.cargarGestionIntegral(idGestionIntegral);
	}
	 

	 @SuppressWarnings("unchecked")
	 public List<ViabilidadTecnicaTipoMateriales> datosViabilidadTecnicaTipoMateriales() {
			return (List<ViabilidadTecnicaTipoMateriales>) crudServiceBean.findAll(ViabilidadTecnicaTipoMateriales.class);
	}
}
