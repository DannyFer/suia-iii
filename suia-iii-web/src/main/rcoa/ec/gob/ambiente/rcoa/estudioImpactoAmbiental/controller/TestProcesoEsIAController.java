package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.facade.NotificacionDiagnosticoAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.TestEsIAFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ViabilidadCoaFacade;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class TestProcesoEsIAController {
	
	private static final Logger LOG = Logger.getLogger(TestProcesoEsIAController.class);
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private AreaFacade areaFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	
	@EJB
	private OrganizacionFacade organizacionFacade;
	
	@EJB
	private ViabilidadCoaFacade viabilidadCoaFacade;
	
	@EJB
	private NotificacionDiagnosticoAmbientalFacade notificacionDiagnosticoAmbientalFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
	private TestEsIAFacade testEsIAFacade;

	@Getter
	@Setter
	private Integer idProceso, procesoAnterior, procesoNuevo;
	
	@Getter
	@Setter
	private String tramite, nombreVariable, valorVariable;
	
	public boolean iniciarProceso(ProyectoLicenciaCoa proyecto){
		try {
			Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
			parametros.put("operador", JsfUtil.getLoggedUser().getNombre());
			parametros.put("idProyecto", proyecto.getId());
			parametros.put("tramite", proyecto.getCodigoUnicoAmbiental());
			parametros.put("tecnicoResponsable", "1722742903"); //debe venir del u_tecnicoResponsable de preliminar
			
			
			
			
			procesoFacade.iniciarProceso(JsfUtil.getLoggedUser(), Constantes.RCOA_ESTUDIO_IMPACTO_AMBIENTAL_v2, proyecto.getCodigoUnicoAmbiental(), parametros);
			
			return true;
		} catch (Exception e) {
			JsfUtil.addMessageError("Ha ocurrido un error. Por favor comuníquese con Mesa de Ayuda.");
			LOG.error(e.getMessage()+" "+e.getCause().getMessage());
			return false;
		}
	}
	
	public void aprobarTarea(Integer idTarea, Integer idProceso) throws JbpmException {
		procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), new Long(idProceso), null);

		procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), new Long(idTarea), new Long(idProceso), null);
	}
	
	public void aprobarTareaPago(Integer idTarea, Integer idProceso) throws JbpmException {
		Map<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("numeroRevision", 1); // al realizar el pago se envía a la primera revisión del tecnico
		parametros.put("esPrimeraRevision", true);
		parametros.put("esPlantaCentral", true);
		
		parametros.put("requierePronunciamientoPatrimonio", false);
		
		parametros.put("requierePronunciamientoSnap", false);
		parametros.put("requierePronunciamientoForestal", false);
		parametros.put("requierePronunciamientoInventario", false);
		
		procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), new Long(idProceso), parametros);

		procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), new Long(idTarea), new Long(idProceso), null);
	}
	
	
	public void aprobarTareaRevision(Integer idTarea, Integer idProceso) throws JbpmException {
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		parametros.put("requiereEquipoMultidiciplinario", true);
		parametros.put("requiereInspeccion", false);
		parametros.put("esPlantaCentral", true);
		
		parametros.put("tecnicoBosques", "1722742903");
		parametros.put("requierePronunciamientoForestal", true);
		parametros.put("requierePronunciamientoSnap", false);
		parametros.put("requierePronunciamientoInventario", false);
		parametros.put("requiereTecnicosApoyo", false);
		parametros.put("esGad", false);
		
		procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), new Long(idProceso), parametros);

		procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), new Long(idTarea), new Long(idProceso), null);
	}
	
	public void aprobarTareaEnviarInforme(Integer idTarea, Integer idProceso) throws JbpmException {
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		parametros.put("coordinadorBosques",  "1722742903");
		
		procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), new Long(idProceso), parametros);

		procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), new Long(idTarea), new Long(idProceso), null);
	}
	
	public void aprobarTareaInformeAceptar(Integer idTarea, Integer idProceso) throws JbpmException {
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		parametros.put("requierePronunciamientoPatrimonio", false);
		parametros.put("coordinadorBosques",  "1722742903");
		parametros.put("directorBosques",  "1722742903");
		
		parametros.put("observacionesBosquesInterseccion", false);
		
		procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), new Long(idProceso), parametros);

		procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), new Long(idTarea), new Long(idProceso), null);
	}
	
	public void aprobarTareaInformeObservar(Integer idTarea, Integer idProceso) throws JbpmException {
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		parametros.put("coordinadorBosques",  "1722742903");
		parametros.put("directorBosques",  "1722742903");
		
		parametros.put("observacionesBosquesInterseccion", true);
		
		procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), new Long(idProceso), parametros);

		procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), new Long(idTarea), new Long(idProceso), null);
	}
	
	public void notificarDiagnostico() {
		notificacionDiagnosticoAmbientalFacade.obtenerTareasFirma();
	}


	public boolean actualizarVariableInicioViabilidad(){
		try {
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("intersecaSnapForestIntanManglar", false);
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(),new Long(idProceso), parametros);
			
			JsfUtil.addMessageInfo("Actualización ejecutada correctamente");
			
			idProceso = null;

			return true;
		} catch (Exception e) {
			JsfUtil.addMessageError("Ha ocurrido un error. Por favor comuníquese con Mesa de Ayuda.");
			LOG.error(e.getMessage()+" "+e.getCause().getMessage());
			return false;
		}
	}
	
	
	public boolean iniciarBack(){
		try {
			
			ProyectoLicenciaCoa proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
			proyectoLicenciaCoa.setEstadoRegistro(false);
			proyectoLicenciaCoa=proyectoLicenciaCoaFacade.guardar(proyectoLicenciaCoa);
			
			return true;
		} catch (Exception e) {
			JsfUtil.addMessageError("Ha ocurrido un error. Por favor comuníquese con Mesa de Ayuda.");
			LOG.error(e.getMessage()+" "+e.getCause().getMessage());
			return false;
		}
	}
	
	public void igualarProcesos() {
		testEsIAFacade.igualarProcesoBack(new Long(procesoAnterior), new Long(procesoNuevo));
	}
	
	public void actualizarVariable() {
		try {
			Map<String, Object> parametros = new HashMap<String, Object>();
			
			Integer valor = Integer.parseInt(valorVariable);
			parametros.put(nombreVariable, valor);
			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), new Long(idProceso), parametros);
			
			JsfUtil.addMessageInfo("Actualización ejecutada correctamente");
			
			idProceso = null;
			valorVariable = null;
			nombreVariable = null;
			
		} catch (Exception e) {
			JsfUtil.addMessageError("Ha ocurrido un error. Por favor comuníquese con Mesa de Ayuda.");
			LOG.error(e.getMessage()+" "+e.getCause().getMessage());
		}
		

	}
}
