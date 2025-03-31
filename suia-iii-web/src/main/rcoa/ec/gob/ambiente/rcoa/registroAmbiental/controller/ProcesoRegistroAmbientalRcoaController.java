package ec.gob.ambiente.rcoa.registroAmbiental.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.RegistroAmbientalRcoa;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class ProcesoRegistroAmbientalRcoaController {

	private static final Logger LOG = Logger.getLogger(ProcesoRegistroAmbientalRcoaController.class);
	
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;

	private String tramite;
	
	private boolean iniciarProceso(String tramite, Integer idproyecto, String documentoEspecialista){
		try {
			Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
			parametros.put("operador", JsfUtil.getLoggedUser().getNombre());
			parametros.put("tramite", tramite);	
			parametros.put("idProyecto", idproyecto);
			parametros.put("tienePago", true);
			parametros.put("esCertificadoRegistro", true);
			parametros.put("coberturaVegetal", true);
			parametros.put("u_existeInventarioForestal", true);
		
			procesoFacade.iniciarProceso(JsfUtil.getLoggedUser(), Constantes.RCOA_REGISTRO_AMBIENTAL,tramite, parametros);			
			
			return true;
		} catch (Exception e) {
			JsfUtil.addMessageError("Ha ocurrido un error. Por favor comuníquese con Mesa de Ayuda.");
			LOG.error(e.getMessage()+" "+e.getCause().getMessage());
			return false;
		}	
	}
	
	private boolean iniciarProcesoEIA(String tramite, Integer idproyecto, String documentoEspecialista){
		try {
			Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
			parametros.put("operador", JsfUtil.getLoggedUser().getNombre());
			parametros.put("tramite", tramite);	
			parametros.put("idProyecto", idproyecto);
		
			procesoFacade.iniciarProceso(JsfUtil.getLoggedUser(), Constantes.RCOA_ESTUDIO_IMPACTO_AMBIENTAL,tramite, parametros);			
			
			return true;
		} catch (Exception e) {
			JsfUtil.addMessageError("Ha ocurrido un error. Por favor comuníquese con Mesa de Ayuda.");
			LOG.error(e.getMessage()+" "+e.getCause().getMessage());
			return false;
		}	
	}

	public boolean iniciarProceso(RegistroAmbientalRcoa responsableProyecto){
		//tramite = "MAE-RA-2020-415418";
		tramite = "MAE-RA-2020-415746";
		/*MAE-RA-2020-415474
		MAE-RA-2020-415481
		MAE-RA-2020-415478
		MAE-RA-2020-415481
		MAE-RA-2020-415415*/
		proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
		return iniciarProcesoEIA(proyecto.getCodigoUnicoAmbiental(),proyecto.getId(), proyecto.getUsuarioCreacion());
	}
	
}
