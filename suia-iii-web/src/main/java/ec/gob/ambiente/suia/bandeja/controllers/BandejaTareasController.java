
package ec.gob.ambiente.suia.bandeja.controllers;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.kie.api.task.model.TaskSummary;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.integracion.facade.IntegracionFacade;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.digitalizacion.facade.AutorizacionAdministrativaAmbientalFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.InformacionProyectoEIACoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.facade.RegistroAmbientalCoaFacade;
import ec.gob.ambiente.rcoa.facade.TareaFirmaMasivaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.ProyectoPlaguicidasFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.ProyectoPlaguicidas;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.FeriadosFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.BandejaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.AreaUsuario;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.Holiday;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.dto.ProyectoCustom;
import ec.gob.ambiente.suia.dto.TaskSummaryCustom;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.integracion.bean.ContenidoExterno;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.recaudaciones.facade.ProyectosConPagoSinNutFacade;
import ec.gob.ambiente.suia.recaudaciones.model.ProyectosConPagoSinNut;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class BandejaTareasController implements Serializable {

	private static final long serialVersionUID = 5103814376528187233L;

	private static final Logger LOG = Logger.getLogger(BandejaTareasController.class);

	@EJB
	private BandejaFacade bandejaFacade;

	@EJB
	private ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade;

	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;

	@EJB
	private OrganizacionFacade organizacionFacade;

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;

	@EJB
	private TareaFirmaMasivaFacade tareaFirmaMasivaFacade;

	@EJB
	private InformacionProyectoEIACoaFacade informacionProyectoEIACoaFacade;

	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	@EJB
	private ProyectosConPagoSinNutFacade proyectosConPagoSinNutFacade;
	@EJB
	private ProyectoPlaguicidasFacade proyectoPlaguicidasFacade;
	@EJB
	private UsuarioFacade usuariosFacade;
	@EJB
	private AutorizacionAdministrativaAmbientalFacade autorizacionAdministrativaAmbientalFacade;
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private RegistroAmbientalCoaFacade registroAmbientalCoaFacade;

	// Cris F: aumento de ejb para contactos
	@EJB
	private ContactoFacade contactoFacade;

	// Fin CF

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;

	@Getter
	@Setter
	public boolean bloquear = false, bloquearBypass = false, bloquearRegistroAmbiental = false;

	@Getter
	@Setter
	public boolean operador = false;

	@Getter
	@Setter
	public boolean tecnico = false;

	@Getter
	@Setter
	boolean bloquearEnte = false, bloquearPorRolTmp;

	@Getter
	@Setter
	private LazyDataModel<TaskSummaryCustom> listarTareasPaginador;

	@Getter
	@Setter
	private Integer terminos;

	@Getter
	@Setter
	List<TaskSummaryCustom> listaSelectTareas;

	@Getter
	@Setter
	private Boolean mostrarFirmaMasiva;

	@EJB
	private FeriadosFacade feriadosFacade;

	@Getter
	@Setter
	private List<Holiday> listHoliday = new ArrayList<Holiday>();

	public void startTask(TaskSummaryCustom taskSummaryCustom) throws ParseException, JbpmException {
		bandejaTareasBean.setVerObservacionesPlaguicidas(false);
		bloquearEnte = false;
		if(JsfUtil.getLoggedUser().getRolUsuarios() == null || JsfUtil.getLoggedUser().getRolUsuarios().size() == 0 ){
			JsfUtil.addMessageError("No se puede iniciar la tarea, no tiene un rol asignado");
			return;
		}
		bloquearPorRolTmp=Usuario.isUserInRole(JsfUtil.getLoggedUser(),	"TEMP AUTORIDAD AMBIENTAL");
		// si tiene rol temporal no puede iniciar la tarea
		if(bloquearPorRolTmp){
			RequestContext.getCurrentInstance().execute("PF('bloqueoConRolTmp').show();");
			return;
		}
		ProyectoLicenciamientoAmbiental proyecto = new ProyectoLicenciamientoAmbiental();
		if (!taskSummaryCustom.getProcessName().equals("Aprobacion Requisitos Tecnicos Gestion de Desechos")
				&& !taskSummaryCustom.getProcessName()
						.equals("Registro de Generador de Residuos y Desechos Peligrosos yo Especiales")
				&& !taskSummaryCustom.getProcessName()
						.equals("Registro de generador de desechos especiales y peligrosos")
				&& !taskSummaryCustom.getProcessName()
						.equals("Digitalizacion de Proyectos")
				&& (taskSummaryCustom.getProcedure().toString().contains("MAE-RA-")
						|| taskSummaryCustom.getProcedure().toString().contains("MAAE-RA-")
						|| taskSummaryCustom.getProcedure().toString().contains("MAATE-RA-"))) {
			proyecto = proyectoLicenciaAmbientalFacade
					.getProyectoPorCodigo(taskSummaryCustom.getProcedure().toString());
//		if(proyecto!= null && proyecto.getAreaResponsable().getTipoArea().getId()==3 &&(proyecto.getAreaResponsable().getId()==584 || proyecto.getAreaResponsable().getId()==580 
//				|| proyecto.getAreaResponsable().getId()==577 || proyecto.getAreaResponsable().getId()==576 || proyecto.getAreaResponsable().getId()==579)){
//	if (proyecto.getAreaResponsable().getHabilitarArea()==null){
			if (proyecto != null && proyecto.getAreaResponsable().getHabilitarArea() == null && proyecto != null
					&& proyecto.getAreaResponsable().getTipoArea().getId() == 3) {
				bloquearEnte = false;
//		proyecto.getAreaResponsable().getArea().setHabilitarArea(false);
			}
			if (proyecto != null && proyecto.getAreaResponsable().getTipoArea().getId() == 3
					&& (proyecto.getAreaResponsable().getHabilitarArea() == true)) {

				bloquearEnte = false;
			} else if (proyecto != null && proyecto.getAreaResponsable().getTipoArea().getId() == 3) {
				bloquearEnte = true;
			} else {
				bloquearEnte = false;
			}
		}
		if (bloquearEnte) {
			// JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			RequestContext.getCurrentInstance().execute("PF('bloqueoEntes').show();");
			return;
		}

		Date fechabloqueo = null;
		Date fechaproyecto = null;
		boolean bloquear = false;
		if (JsfUtil.getLoggedUser().getListaAreaUsuario() == null
				|| JsfUtil.getLoggedUser().getListaAreaUsuario().size() == 0) {
			operador = true;
			setOperador(true);
			tecnico = false;
			setTecnico(false);
		} else {
			Boolean esProponente = false;
			for (AreaUsuario areaUser : JsfUtil.getLoggedUser().getListaAreaUsuario()) {
				if (areaUser.getArea().getAreaName().contains("PROPONENTE")) {
					operador = true;
					setOperador(true);
					tecnico = false;
					setTecnico(false);

					esProponente = true;
					break;
				}
			}

			if (!esProponente) {
				operador = false;
				setOperador(false);
				tecnico = true;
				setTecnico(true);
			}
		}
		
		if (taskSummaryCustom.getProcedure().toString().contains("MAE-RA-") 
			|| taskSummaryCustom.getProcedure().toString().contains("MAAE-RA-")
			|| taskSummaryCustom.getProcedure().toString().contains("MAATE-RA-")) {
			if (proyecto != null && proyecto.getCodigo() == null) {
				proyecto = proyectoLicenciaAmbientalFacade
						.getProyectoPorCodigo(taskSummaryCustom.getProcedure().toString());
			}
			ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental = proyecto;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			fechabloqueo = sdf.parse(Constantes.getFechaBloqueoRegistroFisico());
			if (proyectoLicenciamientoAmbiental == null) {
				bloquear = false;
			} else {
				fechaproyecto = sdf.parse(proyectoLicenciamientoAmbiental.getFechaRegistro().toString());
				bloquear = true;
			}
		} else {
			bloquear = false;
		}

		if (taskSummaryCustom.getProcessId().contains("(Desconocido)")) {
			taskSummaryCustom.setProcessNameHuman("");
		}
		boolean fechaAntes = false;
		if (bloquear) {
			if (fechaproyecto.after(fechabloqueo)) {
				fechaAntes = true;
			}
		}

//		if (encontrarActividadesProyecto(taskSummaryCustom)) {
//			RequestContext.getCurrentInstance().execute("PF('mensajeMineria').show();");
//			return;
//		}

		if ((taskSummaryCustom.getTaskName().contains("pex_revision_pago")
				|| taskSummaryCustom.getTaskName().contains("pex_nuevo_facilitador")
				|| taskSummaryCustom.getTaskName().contains("pex_correcion_pago")
				|| taskSummaryCustom.getTaskName().contains("pex_asignacion_f")
				|| taskSummaryCustom.getTaskName().contains("pex_aceptacion_f")
				|| taskSummaryCustom.getTaskName().contains("pex_informe_vp")
				|| taskSummaryCustom.getTaskName().contains("pex_revision_informe_vp")
				|| taskSummaryCustom.getTaskName().contains("pex_verificacion_facilitadores")
				|| taskSummaryCustom.getTaskName().contains("pex_participacion_social")
				|| taskSummaryCustom.getTaskName().contains("pex_revision_ps")
				|| taskSummaryCustom.getTaskName().contains("cat_iv_asignarTareaEstudios")
				|| taskSummaryCustom.getTaskName().contains("cat_iv_pronunciamientoEstudios")
				|| taskSummaryCustom.getTaskName().contains("cat_iv_revisionEstudios")
				|| taskSummaryCustom.getTaskName().contains("cat_iv_corregirEstudios")
				|| taskSummaryCustom.getTaskName().contains("cat_iv_firmaOficioObservacionesEstudios")
				|| taskSummaryCustom.getTaskName().contains("cat_iv_revisionOficioObservacionesEstudios")
				|| taskSummaryCustom.getTaskName().contains("cat_iv_pronunciamientoFavorable")
				|| taskSummaryCustom.getTaskName().contains("cat_iv_firmaPronunciamientoFavorable")
				|| taskSummaryCustom.getTaskName().contains("cat_iv_pagoEstudios")
				|| taskSummaryCustom.getTaskName().contains("cat_iv_revisionPagoEstudios")
				|| taskSummaryCustom.getTaskName().contains("cat_iv_corregirPagoEstudios")
				|| taskSummaryCustom.getTaskName().contains("cat_iv_borradorLicencia")
				|| taskSummaryCustom.getTaskName().contains("cat_iv_juridico")
				|| taskSummaryCustom.getTaskName().contains("cat_iv_despacho")
				|| taskSummaryCustom.getTaskName().contains("cat_iv_ministra")
				|| taskSummaryCustom.getTaskName().contains("cat_iv_direccionProvincial")
				|| taskSummaryCustom.getTaskName().contains("cat_iv_firmaMinistra")
				|| taskSummaryCustom.getTaskName().contains("cat_iv_firmaDirector")
				|| taskSummaryCustom.getTaskName().contains("cat_iv_juridico_registro_permiso")
				|| taskSummaryCustom.getTaskName().contains("cat_iv_Pre_visulizacion")
				|| taskSummaryCustom.getTaskName().contains("cat_iv_impresionAprobacionEstudios")) && fechaAntes) {
			setBloquear(true);
			bloquear = true;
		} else if ((!(taskSummaryCustom.getProcessId().equals("Hydrocarbons.Inclusion")
				|| taskSummaryCustom.getProcessId().equals("Hydrocarbons.TDR")
				|| taskSummaryCustom.getProcessId().equals("Hydrocarbons.TDREnte")))
				&& (taskSummaryCustom.getProcessNameHuman().equals("Participación Social")
						|| taskSummaryCustom.getProcessNameHuman().equals("Estudios de Impacto Ambiental"))
				&& (fechaproyecto.after(fechabloqueo)) && bloquear) {
			if (fechaproyecto.after(fechabloqueo)) {
				setBloquear(true);
				bloquear = true;
			} else {
				setBloquear(false);
				bloquear = false;
			}

		} else if (taskSummaryCustom.getProcessNameHuman().equals("Evaluacion Social")
				&& fechaproyecto.after(fechabloqueo) && bloquear) {
			if (fechaproyecto.after(fechabloqueo)) {
				setBloquear(true);
				bloquear = true;
			} else {
				setBloquear(false);
				bloquear = false;
			}
		} else if (((!taskSummaryCustom.getTaskNameHuman().equals("Descargar TDR")
				&& taskSummaryCustom.getProcessNameHuman().equals("Licencia Ambiental")))
				&& (fechaproyecto.after(fechabloqueo)) && bloquear) {
			if (fechaproyecto.after(fechabloqueo)) {
				setBloquear(true);
				bloquear = true;
			} else {
				setBloquear(false);
				bloquear = false;
			}
		} else {
			setBloquear(false);
			bloquear = false;
			operador = false;
			setOperador(false);
			tecnico = false;
			setTecnico(false);
			taskSummaryCustom.setProcessVariables(bandejaFacade
					.getProcessVariables(taskSummaryCustom.getProcessInstanceId(), JsfUtil.getLoggedUser()));

			switch (taskSummaryCustom.getSourceType()) {
			case TaskSummaryCustom.SOURCE_TYPE_INTERNAL:

				bandejaTareasBean.setTarea(taskSummaryCustom);
				bandejaTareasBean.setProcessId(taskSummaryCustom.getProcessInstanceId());

				if (proyecto != null && proyecto.getCodigo() == null) {
					if (!taskSummaryCustom.getProcessId().toUpperCase().contains("RCOA"))
						recuperarProyecto(taskSummaryCustom.getProjectId());
					else
						recuperarProyectoRcoa(taskSummaryCustom);
				} else {
					Map<String, Object> variables = taskSummaryCustom.getProcessVariables();
					String vieneRcoa = "";
					try {
						vieneRcoa = variables.get("vieneRcoa").toString();
					} catch (Exception e) {
					}

					if (taskSummaryCustom.getProcessId().toUpperCase().contains("RCOA") || vieneRcoa.equals("true"))
						recuperarProyectoRcoa(taskSummaryCustom);
					else
						proyectosBean.setProyecto(proyecto);

					// suspender ejecución de ppc bypass por solicitud Ticket#10351907
					if (taskSummaryCustom.getProcessId().equals("rcoa.ProcesoParticipacionCiudadanaBypass")) {
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

						Date fechaSuspensionBypass = sdf.parse(Constantes.getFechaSuspensionPpcBypass());
						Date fechaProyecto = sdf
								.parse(proyectosBean.getProyectoRcoa().getFechaGeneracionCua().toString());

						if (fechaProyecto.compareTo(fechaSuspensionBypass) >= 0) {
							setBloquearBypass(true);
							bloquear = true;
							RequestContext.getCurrentInstance().execute("PF('bloqueoBypass').show();");
							return;
						}
					}
					
					//bloquear registros ambientales para ejecución de PPC fisico. Ticket#10418702
//					if (taskSummaryCustom.getProcessId().equals("rcoa.RegistroAmbiental") 
//							&& taskSummaryCustom.getTaskName().equals("Realizar pago de tasa")) {
//						SimpleDateFormat formatoFechaS = new SimpleDateFormat("yyyy-MM-dd");
//						Date fechaFechaBloqueoRegistro = formatoFechaS.parse(Constantes.getFechaBloqueoRegistroAmbientalPpcFisico());
//						Date fechaProyecto = formatoFechaS.parse(proyectosBean.getProyectoRcoa().getFechaGeneracionCua().toString());
//
//						if (fechaProyecto.compareTo(fechaFechaBloqueoRegistro) >= 0) {
//							ProyectoLicenciaCuaCiuu proyectoCiuuPrincipal = proyectoLicenciaCuaCiuuFacade.actividadPrincipal(proyectosBean.getProyectoRcoa());
//							CatalogoCIUU actividadPrincipal = proyectoCiuuPrincipal.getCatalogoCIUU();
//							if (actividadPrincipal.getTipoSector().getId().equals(TipoSector.TIPO_SECTOR_HIDROCARBUROS) ||
//									actividadPrincipal.getTipoSector().getId().equals(TipoSector.TIPO_SECTOR_MINERIA)) {
//								setBloquearRegistroAmbiental(true);
//								bloquear = true;
//								RequestContext.getCurrentInstance().execute("PF('bloqueoRegistroAmbientalPpc').show();");
//								return;
//							}
//						}
//					}
					
					// si es un RgdRcoa de un proyecto del suia
					if (proyecto != null && proyecto.getId() != null && taskSummaryCustom.getProcessId()
							.equals("rcoa.RegistrodeGeneradordeDesechosPeligrososyEspeciales")) {
						proyectosBean.setProyecto(proyecto);
						proyectosBean.setProyectoRcoa(null);
					}
				}

				// String ruc = taskSummaryCustom.getProcessVariables().get("proponente")!= null
				// ? (String)taskSummaryCustom.getProcessVariables().get("proponente") : null;
				// //vear: 28/03/75: quitado para colocar la linea de abajo, para que salga
				// correctamente el proponente si es un organizacion
				String ruc = taskSummaryCustom.getProcessVariables().get("sujetoControl") != null
						? (String) taskSummaryCustom.getProcessVariables().get("sujetoControl")
						: null;
				if (ruc == null)
					ruc = taskSummaryCustom.getTaskSummary().getCreatedBy().getId();

				cargarProponente(taskSummaryCustom.getProjectId() != null, ruc, taskSummaryCustom);
				cargarDatosTramite();
				String urlPagos = existeProyecto();
				
				//verifico si se trata de un proyecto de Registro Ambiental de mineria o Hidrocarburos sin PPC para iniciar el proceso de Registro ambiental con PPC
				String urlRegistroAmbientalPPC = iniciarNuevoFlujoRAPPC();
				
				if(!urlPagos.isEmpty())
					JsfUtil.redirectTo(urlPagos);
				else if(!urlRegistroAmbientalPPC.isEmpty())
					JsfUtil.redirectTo(urlRegistroAmbientalPPC);
				else if (proyectosBean.getProyectoRcoa() != null)
					JsfUtil.redirectTo(taskSummaryCustom.getTaskSummary().getDescription());
				else if (proyectosBean.getProyecto() == null)
					JsfUtil.redirectTo(taskSummaryCustom.getTaskSummary().getDescription());
				else if (!proyectosBean.getProyecto().getAreaResponsable().getBloqueado())
					JsfUtil.redirectTo(taskSummaryCustom.getTaskSummary().getDescription());
				else
					JsfUtil.addMessageError("No se puede iniciar la tarea");

				break;

			case TaskSummaryCustom.SOURCE_TYPE_EXTERNAL_SUIA:
			case TaskSummaryCustom.SOURCE_TYPE_EXTERNAL_HYDROCARBONS:

				JsfUtil.getBean(ContenidoExterno.class).executeAction(taskSummaryCustom,
						IntegracionFacade.IntegrationActions.iniciar_tarea);

				break;
			}
		}

	}
	
	/***
	 *  validacion para finalizar el flujo e iniciar el nuevo flujo de registro ambiental con PPC
	 * @throws JbpmException 
	 */
	private String iniciarNuevoFlujoRAPPC() throws JbpmException{
		String url="";
		//si es un proyecto de registro ambiental y no esta en la tarea de firma inicio un nuevo flujo en la nueva version
		if(bandejaTareasBean.getTarea().getProcessId().equals(Constantes.RCOA_REGISTRO_AMBIENTAL)
				&& !bandejaTareasBean.getTarea().getTaskName().equals("Firmar electronicamente resolucion ambiental")){
			
			/**
			 * Fecha que se toma en cuanta para ingresar a ppc de un proyecto en trámite
			 */
			Calendar fechaProyecto = Calendar.getInstance();
			fechaProyecto.set(Calendar.YEAR, 2021);
			fechaProyecto.set(Calendar.MONTH, 9);
			fechaProyecto.set(Calendar.DATE, 12);
			
			if(proyectosBean.getProyectoRcoa().getFechaCreacion().before(fechaProyecto.getTime())){
				return url;
			}
			
			ProyectoLicenciaCuaCiuu actividadprincipal = proyectoLicenciaCuaCiuuFacade.actividadPrincipal(proyectosBean.getProyectoRcoa());
			if(actividadprincipal != null && actividadprincipal.getId() != null){
				if(actividadprincipal.getCatalogoCIUU().getTipoSector().getId().equals(TipoSector.TIPO_SECTOR_HIDROCARBUROS)
						|| actividadprincipal.getCatalogoCIUU().getTipoSector().getId().equals(TipoSector.TIPO_SECTOR_MINERIA)){
					// desactivo el flujo actual
					autorizacionAdministrativaAmbientalFacade.desactivarInstanciaRegistroambiental(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
					Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
					parametros.put("initiator", proyectosBean.getProyectoRcoa().getUsuario().getNombre());
					parametros.put("operador", proyectosBean.getProyectoRcoa().getUsuario().getNombre());
					parametros.put("tramite", proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental());
					parametros.put("idProyecto", proyectosBean.getProyectoRcoa().getId());
					//inicio el nuevo flujo de registro ambiental con PPC
					Long processintanceId = procesoFacade.iniciarProceso(JsfUtil.getLoggedUser(), Constantes.RCOA_REGISTRO_AMBIENTAL_PPC, proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental(), parametros);
					//habilito el ingreso de informacion en el registro ambiental
					if (bandejaTareasBean.getTarea().getTaskName().equals("Realizar pago de tasa")) {
							registroAmbientalCoaFacade.activarIngresoRegistroAmbiental(proyectosBean.getProyectoRcoa().getId());
					}
					//obtengo la tarea de pago generada
					TaskSummary tareaActual = procesoFacade.getCurrenTask(JsfUtil.getLoggedUser(), processintanceId);
					if(tareaActual != null){
						bandejaTareasBean.setProcessId(processintanceId);
						TaskSummaryCustom tarea = new TaskSummaryCustom();
						tarea.setTaskSummary(tareaActual);
						tarea.setTaskName(tareaActual.getName());
						tarea.setProcessName("Registro Ambiental con PPC para Consulta Ambiental");
						tarea.setProcessId(tareaActual.getProcessId());
						tarea.setProcessInstanceId(tareaActual.getProcessInstanceId());
						tarea.setTaskId(tareaActual.getId());
						tarea.setProcedure(proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental());
						tarea.setActivationDate(tareaActual.getActivationTime().toString());
						bandejaTareasBean.setTarea(tarea);
						url = tareaActual.getDescription();
					}
				}
			}	
		}
		return url;
	}
	/***
	 *  Fin registro ambiental con PPC
	 */

	public String viewProject(TaskSummaryCustom taskSummaryCustom) {
		switch (taskSummaryCustom.getSourceType()) {
		case TaskSummaryCustom.SOURCE_TYPE_INTERNAL:

			try {
				ProyectoLicenciamientoAmbiental proyecto = proyectoLicenciamientoAmbientalFacade
						.buscarProyectosLicenciamientoAmbientalPorId(
								Integer.parseInt(taskSummaryCustom.getProjectId()));
				proyectosBean.setProyectoToShow(proyecto);
				return JsfUtil.actionNavigateTo("/proyectos/resumenProyecto.jsf");
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				return null;
			}

		case TaskSummaryCustom.SOURCE_TYPE_EXTERNAL_SUIA:
		case TaskSummaryCustom.SOURCE_TYPE_EXTERNAL_HYDROCARBONS:
			ProyectoCustom proyectoCustom = new ProyectoCustom(taskSummaryCustom);
			JsfUtil.getBean(ContenidoExterno.class).executeAction(proyectoCustom,
					IntegracionFacade.IntegrationActions.mostrar_dashboard);

			break;
		}

		return "";
	}

	/*
	 * public String seleccionar(ProyectoCustom proyectoCustom) { switch
	 * (proyectoCustom.getSourceType()) { case ProyectoCustom.SOURCE_TYPE_INTERNAL:
	 * 
	 * try { ProyectoLicenciamientoAmbiental proyecto =
	 * proyectoLicenciamientoAmbientalFacade
	 * .buscarProyectosLicenciamientoAmbientalPorId(Integer.parseInt(proyectoCustom.
	 * getId())); setProyectoToShow(proyecto); return
	 * JsfUtil.actionNavigateTo("/proyectos/resumenProyecto.jsf"); } catch
	 * (Exception e) { LOG.error(e.getMessage(),e);
	 * JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION); return
	 * null; }
	 * 
	 * case ProyectoCustom.SOURCE_TYPE_EXTERNAL_HIDROCARBUROS: case
	 * ProyectoCustom.SOURCE_TYPE_EXTERNAL_SUIA:
	 * 
	 * JsfUtil.getBean(ContenidoExterno.class).executeAction(proyectoCustom,
	 * IntegracionFacade.IntegrationActions.mostrar_dashboard);
	 * 
	 * break; }
	 * 
	 * return null; }
	 *
	 * 
	 */
	@PostConstruct
	public void init() {
		try {
			bandejaTareasBean.setTarea(null);
//			bandejaTareasBean.addTareas(bandejaFacade.getTasksByUser(JsfUtil.getLoggedUser().getNombre(),
//					JsfUtil.getLoggedUser()));
			listarTareasPaginador = new LazyDataModelTareas(JsfUtil.getLoggedUser().getNombre(),
					JsfUtil.getLoggedUser());
//			terminos y condiciones

			String operathor = JsfUtil.getLoggedUser().getRolUsuarios().toString();

			if (operathor.contains("sujeto de control")) {
				operador = true;
			}

			FacesContext context = FacesContext.getCurrentInstance();
			HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
			HttpSession httpSession = request.getSession(false);
			terminos = (Integer) httpSession.getAttribute("terminos");

			mostrarFirmaMasiva = true;
			if (Usuario.isUserInRole(JsfUtil.getLoggedUser(), "sujeto de control"))
				mostrarFirmaMasiva = false;
		} catch (Exception e) {
			LOG.error("Error en bandeja de tareas", e);
			JsfUtil.addMessageError(e.getMessage());
		}
	}

	public int diasRestantes(TaskSummaryCustom tarea) throws ParseException, ServiceException {
		try {
			Date fechaActual = new Date();
			SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			SimpleDateFormat formatoFecha1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			String standarFormat1;
			Date diasRestantes = formatoFecha.parse(tarea.getActivationDate());
			standarFormat1 = formatoFecha.format(diasRestantes);
			String fechaActivaActual = formatoFecha.format(fechaActual);
			Date fechaInicial = formatoFecha.parse(standarFormat1);
			Date fechaFinal = formatoFecha.parse(fechaActivaActual);

			int dias = (int) ((fechaFinal.getTime() - fechaInicial.getTime()) / 86400000);
			dias++;
			listHoliday = feriadosFacade.listarFeriados();
			int contador = 0;
			int contadorFinDeSemana = 0;
			for (Holiday holiday : listHoliday) {
				Date hol = holiday.getFechaInicio();
				String holi = formatoFecha.format(hol);
				Date feriado = formatoFecha.parse(holi);
//			la busqueda despues de fecha inicial y antes de fecha final
				if ((feriado.after(fechaInicial)) && (feriado.before(fechaFinal))) {
					contador++;
				}
			}
			Calendar cal = Calendar.getInstance();
			Calendar calFin = Calendar.getInstance();
			formatoFecha.format(fechaInicial);
			cal = formatoFecha.getCalendar();
			formatoFecha1.format(fechaFinal);
			calFin = formatoFecha1.getCalendar();

			while (cal.before(calFin) || cal.equals(calFin)) {

				if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
						|| cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
					// se aumentan los dÃ­as de diferencia entre min y max
					contadorFinDeSemana++;
				}
				// se suma 1 dia para hacer la validaciÃ³n del siguiente dia.
				cal.add(Calendar.DATE, 1);
			}

			dias = (dias - contador - contadorFinDeSemana);
			dias = 90 - dias;

			if (dias <= 0) {
				dias = 0;
			}
			terminos = dias;
			return dias;

		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return terminos;

	}

	private boolean recuperarProyecto(String idProyecto) {
		try {
			proyectosBean.setProyecto(null);
			proyectosBean.setProponente(null);
			if (idProyecto != null && !idProyecto.isEmpty()) {
				proyectosBean
						.setProyecto(proyectoLicenciaAmbientalFacade.getProyectoPorId(Integer.valueOf(idProyecto)));
				return true;
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al cargar la información del proyecto.");
		}
		return false;
	}

	private void cargarProponente(boolean noVoluntario, String ruc, TaskSummaryCustom taskSummaryCustom) {
		try {
			UbicacionesGeografica ubicacionesGeografica = null;
			Organizacion organizacion = null;

			if (noVoluntario) {
				Usuario userTramite = new Usuario();
				try {
					ProyectoLicenciamientoAmbiental proyecto = proyectoLicenciaAmbientalFacade
							.getProyectoPorCodigo(taskSummaryCustom.getProcedure());
					userTramite = proyecto.getUsuario();
				} catch (Exception e) {
					userTramite = null;
				}
				if (null == userTramite) {
					ProyectoLicenciaCoa proyectoLicenciaCoa = proyectoLicenciaCoaFacade
							.buscarProyecto(taskSummaryCustom.getProcedure());
					userTramite = proyectoLicenciaCoa.getUsuario();
				}
				if ("rcoa.RegistroSustanciasQuimicasImportacion".equals(taskSummaryCustom.getProcessId())) {
					ProyectoLicenciaCoa proyectoLicenciaCoa = proyectoLicenciaCoaFacade
							.buscarProyectoPorId(Integer.valueOf(taskSummaryCustom.getProjectId()));
					userTramite = proyectoLicenciaCoa.getUsuario();
				} else if (Constantes.RCOA_PROCESO_ACTUALIZACION_ETIQUETADO_PLAGUICIDAS.equals(taskSummaryCustom.getProcessId())) {
					ProyectoPlaguicidas proyectoPlaguicidas = proyectoPlaguicidasFacade.getPorCodigoProyecto(taskSummaryCustom.getProcedure());
					userTramite = usuariosFacade.buscarUsuario(proyectoPlaguicidas.getUsuarioCreacion());
					
					String tarea = bandejaFacade.getNombreTarea(JsfUtil.getCurrentTask().getTaskId());
					if (tarea.equals("elaborarInformeTecnicoOficio")) {
						bandejaTareasBean.setVerObservacionesPlaguicidas(true);
					}
				}

				if (userTramite != null && userTramite.getPersona() != null) {
					if (userTramite.getPersona().getIdNacionalidad() != null
							&& userTramite.getNombre().length() <= 10) {
						ubicacionesGeografica = ubicacionGeograficaFacade
								.ubicacionCompleta(userTramite.getPersona().getIdUbicacionGeografica());
						proyectosBean.setProponente(userTramite.getPersona().getNombre());

						/**
						 * Cris F: aumento de contactos
						 */
						getEmailPersona(userTramite.getPersona());

					} else {
						organizacion = organizacionFacade.buscarPorPersona(userTramite.getPersona(),
								userTramite.getNombre());

						if (organizacion == null) {
							ubicacionesGeografica = ubicacionGeograficaFacade
									.ubicacionCompleta(userTramite.getPersona().getIdUbicacionGeografica());
							proyectosBean.setProponente(userTramite.getPersona().getNombre());

							/**
							 * Cris F: aumento de contactos
							 */

							getEmailPersona(userTramite.getPersona());

						} else {
							proyectosBean.setProponente(organizacion.getNombre());
							ubicacionesGeografica = ubicacionGeograficaFacade
									.ubicacionCompleta(organizacion.getIdUbicacionGeografica());

							/**
							 * Cris F: aumento de contactos
							 */
							getEmailOrganizacion(organizacion);
						}
					}
					proyectosBean.getUbicacionProponente().clear();
					proyectosBean.getUbicacionProponente().add(ubicacionesGeografica);

				}
			} else {

				organizacion = organizacionFacade.buscarPorRuc(ruc);
				if (organizacion != null) {
					proyectosBean.setProponente(organizacion.getNombre());
					ubicacionesGeografica = ubicacionGeograficaFacade
							.ubicacionCompleta(organizacion.getIdUbicacionGeografica());

					proyectosBean.getUbicacionProponente().clear();
					proyectosBean.getUbicacionProponente().add(ubicacionesGeografica);
				}

			}

		} catch (Exception e) {
			LOG.error("Error: No se puede cargar la información del proponente: ", e);
			JsfUtil.addMessageError("Ocurrió un error al cargar la información del proponente.");
		}
	}

	private void cargarDatosTramite() {
		String resolverClass = bandejaTareasBean.getTarea().getProcedureResolverClass();
		if (resolverClass != null && !resolverClass.isEmpty())
			bandejaTareasBean.initHtmlPanelGridTramite();
		else
			bandejaTareasBean.initResolverTramite();
	}

	/**
	 * Cris F: métodos para obtener correo y teléfono del proponente
	 */
	public void getEmailPersona(Persona persona) {
		try {

			String correo = "";
			String telefono = "";
			String celular = "";

			List<Contacto> listaContactosPersona = contactoFacade.buscarPorPersona(persona);

			for (Contacto contacto : listaContactosPersona) {
				if (contacto.getFormasContacto().getId() == FormasContacto.EMAIL) {
					correo = contacto.getValor();
				}

				if (contacto.getFormasContacto().getId() == FormasContacto.TELEFONO) {
					telefono = contacto.getValor();
				}

				if (contacto.getFormasContacto().getId() == FormasContacto.CELULAR) {
					celular = contacto.getValor();
				}
			}

			proyectosBean.setCorreo(correo);
			proyectosBean.setNumeroTelefonico(telefono);
			proyectosBean.setCelular(celular);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getEmailOrganizacion(Organizacion organizacion) {
		try {
			String correo = "";
			String telefono = "";
			String celular = "";

			List<Contacto> listaContactosOrganizacion = contactoFacade.buscarPorOrganizacion(organizacion);

			for (Contacto contacto : listaContactosOrganizacion) {
				if (contacto.getFormasContacto().getId() == FormasContacto.EMAIL) {
					correo = contacto.getValor();
				}

				if (contacto.getFormasContacto().getId() == FormasContacto.TELEFONO) {
					telefono = contacto.getValor();
				}

				if (contacto.getFormasContacto().getId() == FormasContacto.CELULAR) {
					celular = contacto.getValor();
				}
			}

			proyectosBean.setCorreo(correo);
			proyectosBean.setNumeroTelefonico(telefono);
			proyectosBean.setCelular(celular);
			if (organizacion.getPersona() != null)
				proyectosBean.setRepresentanteLegal(organizacion.getPersona().getNombre());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean recuperarProyectoRcoa(TaskSummaryCustom taskSummaryCustom) {
		try {
			String idProyecto = taskSummaryCustom.getProjectId();
			proyectosBean.setProyecto(null);
			proyectosBean.setProyectoRcoa(null);
			proyectosBean.setProponente(null);
			if (idProyecto != null && !idProyecto.isEmpty()) {
				proyectosBean
						.setProyectoRcoa(proyectoLicenciaCoaFacade.buscarProyectoPorId((Integer.valueOf(idProyecto))));

				bandejaTareasBean.setTramiteDiagnosticoAmbiental(false);
				if (taskSummaryCustom.getProcessId().equals("rcoa.RegistroPreliminar")
						&& taskSummaryCustom.getTaskName().contains("Revisar informacion")
						&& taskSummaryCustom.getTaskSummary().getDescription().contains("/revisionPreliminar/"))
					bandejaTareasBean.setTramiteDiagnosticoAmbiental(true);

				return true;
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al cargar la información del proyecto.");
		}
		return false;
	}

	public void cerrarTerminos() {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		HttpSession httpSession = request.getSession(false);
		httpSession.setAttribute("terminos", 0);
		terminos = 0;
	}

	public void enviarParaFirma() {
		try {
			bandejaTareasBean.setProcessId(0L);
			bandejaTareasBean.setTareasFirmaMasiva(listaSelectTareas);

			JsfUtil.redirectTo("/pages/rcoa/firmaMasiva/firmaElectronicaMasiva.jsf");
		} catch (Exception e) {
			JsfUtil.addMessageError(e.getMessage());
		}
	}

	public Boolean habilitarFirmaMasiva(TaskSummaryCustom tarea) {
		Boolean resultado = false;
		if (tarea != null)
			resultado = tareaFirmaMasivaFacade.esTareaFirmaMasiva(tarea.getProcessId(), tarea.getTaskName());

		return !resultado;
	}

	public void limpiarFirma() {
		listaSelectTareas = null;

		RequestContext.getCurrentInstance().update("tabview");
	}

	public String getMensajeSuspensionBypass() throws ServiceException {
		String notificacion = mensajeNotificacionFacade
				.recuperarValorMensajeNotificacionSD("bodyNotificacionSupensionPpcBypass");
		return notificacion;
	}

	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;

	public boolean encontrarActividadesProyecto(TaskSummaryCustom taskSummaryCustom) {
		try {
			taskSummaryCustom.setProcessVariables(bandejaFacade
					.getProcessVariables(taskSummaryCustom.getProcessInstanceId(), JsfUtil.getLoggedUser()));
			recuperarProyectoRcoa(taskSummaryCustom);

			boolean existeActividad = false;
			if (proyectosBean.getProyectoRcoa() != null && proyectosBean.getProyectoRcoa().getId() != null) {

				SimpleDateFormat formatofecha = new SimpleDateFormat("yyyy-MM-dd");

				Date fecha = formatofecha.parse("2021-12-01");

				if (proyectosBean.getProyectoRcoa().getFechaCreacion().before(fecha)) {
					return false;
				}

				if (proyectosBean.getProyectoRcoa().getCategorizacion() != 2) {
					return false;
				}

				List<ProyectoLicenciaCuaCiuu> lista = proyectoLicenciaCuaCiuuFacade
						.actividadesPorProyecto(proyectosBean.getProyectoRcoa());

				for (ProyectoLicenciaCuaCiuu ciiu1 : lista) {
					if (ciiu1.getCatalogoCIUU().getCodigo().equals("B0990.01")
							|| ciiu1.getCatalogoCIUU().getCodigo().equals("B0990.01.01")
							|| ciiu1.getCatalogoCIUU().getCodigo().equals("B0990.02")
							|| ciiu1.getCatalogoCIUU().getCodigo().equals("B0990.02.01")
							|| ciiu1.getCatalogoCIUU().getCodigo().equals("B0990.09")
							|| ciiu1.getCatalogoCIUU().getCodigo().equals("B0990.09.01")) {

						existeActividad = true;
						break;
					}
				}

			} else {
				return false;
			}

			return existeActividad;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public String existeProyecto(){
		String url="";
		boolean esPago=false;
		//verifico si es una tarea de pago con nut
		switch(bandejaTareasBean.getTarea().getProcessName()){
		case "Registro de Generador de Residuos y Desechos Peligrosos yo Especiales":
		case "Declaracion Sustancias Quimicas":
		case "Proceso de Participacion Ciudadana":
		case "Registro Ambiental":
			if(bandejaTareasBean.getTarea().getTaskName().toLowerCase().contains("pago")){
				esPago=true;
			}
			break;
		case "Registro Ambiental con PPC para Consulta Ambiental":
			if(bandejaTareasBean.getTarea().getTaskName().toLowerCase().contains("pago")){
				esPago=true;
			}
			break;
		case "Resolucion Licencia Ambiental":
			if(bandejaTareasBean.getTarea().getTaskName().toLowerCase().contains("valor")){
				esPago=true;
			}
			break;
		}
		try{
			// si es tarea de pago con nut busco si el proyecto esta registrado para que no pague con nut
			if(esPago){
				ProyectosConPagoSinNut objProyectoNoNut = proyectosConPagoSinNutFacade.buscarNUTPorProyectoPorUsuario(bandejaTareasBean.getProcessId(), JsfUtil.getLoggedUser());
		        if(objProyectoNoNut == null){
			        // si no debe pagar con la version anterio verifico si tiene un pago liberado para RGD
		    		switch(bandejaTareasBean.getTarea().getProcessName()){
		    		case "Registro de Generador de Residuos y Desechos Peligrosos yo Especiales":
		    		case "Registro Ambiental":
		    			String codigoTramite = (proyectosBean.getProyecto() != null && proyectosBean.getProyecto().getCodigo() != null) ? proyectosBean.getProyecto().getCodigo() : (proyectosBean.getProyectoRcoa() != null && proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental() != null) ?proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental() : "";  
			        	objProyectoNoNut = proyectosConPagoSinNutFacade.buscarPorProyectoPorUsuarioPagoLiberado(codigoTramite, JsfUtil.getLoggedUser(), 0L);
		    			break;
		    		case "Registro Ambiental con PPC para Consulta Ambiental":
		    			String codigoTramiteRegistro = (proyectosBean.getProyecto() != null && proyectosBean.getProyecto().getCodigo() != null) ? proyectosBean.getProyecto().getCodigo() : (proyectosBean.getProyectoRcoa() != null && proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental() != null) ?proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental() : "";  
			        	objProyectoNoNut = proyectosConPagoSinNutFacade.buscarPorProyectoPorUsuarioPagoLiberado(codigoTramiteRegistro, JsfUtil.getLoggedUser(), 0L);
		    			break;
		    		}
		        }
		        if(objProyectoNoNut != null && objProyectoNoNut.getId() != null)
		        	url = mostrarVersionAnterior();
			}
		}catch(Exception e){
			
		}
		return url;
	}

	
	public String mostrarVersionAnterior() throws ParseException{
		String urlPagina="";
		switch(bandejaTareasBean.getTarea().getProcessName()){
		case "Registro de Generador de Residuos y Desechos Peligrosos yo Especiales":
			if(bandejaTareasBean.getTarea().getTaskName().toLowerCase().contains("pago")){
				urlPagina = bandejaTareasBean.getTarea().getTaskSummary().getDescription().replace("realizarPagosrgd.", "realizarPagosrgdV1.");
			}
			break;
		case "Declaracion Sustancias Quimicas":
			if(bandejaTareasBean.getTarea().getTaskName().toLowerCase().contains("pago")){
				urlPagina = bandejaTareasBean.getTarea().getTaskSummary().getDescription().replace("realizarPago.", "realizarPagoV1.");
			}
			break;
		case "Proceso de Participacion Ciudadana":
			if(bandejaTareasBean.getTarea().getTaskName().toLowerCase().contains("pago")){
				urlPagina = bandejaTareasBean.getTarea().getTaskSummary().getDescription().replace("realizarPagoFacilitadores.", "realizarPagoFacilitadoresV1.");
			}
			break;
		case "Resolucion Licencia Ambiental":
			if(bandejaTareasBean.getTarea().getTaskName().toLowerCase().contains("valor")){
				urlPagina = bandejaTareasBean.getTarea().getTaskSummary().getDescription().replace("operadorCostoProyecto.", "operadorCostoProyectoV1.");
			}
			break;
		case "Registro Ambiental":
			if(bandejaTareasBean.getTarea().getTaskName().toLowerCase().contains("pago")){
				urlPagina = bandejaTareasBean.getTarea().getTaskSummary().getDescription().replace("pagoRegistroAmbientalCoa.", "pagoRegistroAmbientalCoaV1.");
			}
			break;
		case "Registro Ambiental con PPC para Consulta Ambiental":
			if(bandejaTareasBean.getTarea().getTaskName().toLowerCase().contains("pago")){
				urlPagina = bandejaTareasBean.getTarea().getTaskSummary().getDescription().replace("pagoRegistroAmbientalCoa.", "pagoRegistroAmbientalCoaV1.");
			}
			break;
		}
		return urlPagina;
	}
	
	public String getMensajeBloqueoRegistroAmbiental() throws ServiceException {
		String notificacion = mensajeNotificacionFacade
				.recuperarValorMensajeNotificacionSD("mensajeBloqueoRegistroAmbiental");
		return notificacion;
	}
	
	public void cargarInfoProponente(Usuario userTramite) {
		try {
			UbicacionesGeografica ubicacionesGeografica = null;
			Organizacion organizacion = null;
			
			if (userTramite != null && userTramite.getPersona() != null) {
				if (userTramite.getPersona().getIdNacionalidad() != null
						&& userTramite.getNombre().length() <= 10) {
					ubicacionesGeografica = ubicacionGeograficaFacade.ubicacionCompleta(userTramite.getPersona().getIdUbicacionGeografica());
					proyectosBean.setProponente(userTramite.getPersona().getNombre());

					getEmailPersona(userTramite.getPersona());
				} else {
					organizacion = organizacionFacade.buscarPorPersona(userTramite.getPersona(), userTramite.getNombre());

					if (organizacion == null) {
						ubicacionesGeografica = ubicacionGeograficaFacade.ubicacionCompleta(userTramite.getPersona().getIdUbicacionGeografica());
						proyectosBean.setProponente(userTramite.getPersona().getNombre());

						getEmailPersona(userTramite.getPersona());
					} else {
						proyectosBean.setProponente(organizacion.getNombre());
						ubicacionesGeografica = ubicacionGeograficaFacade.ubicacionCompleta(organizacion.getIdUbicacionGeografica());
						
						getEmailOrganizacion(organizacion);
					}
				}
				proyectosBean.getUbicacionProponente().clear();
				proyectosBean.getUbicacionProponente().add(ubicacionesGeografica);
			}
		} catch (Exception e) {
			LOG.error("Error: No se puede cargar la información del proponente: ", e);
			JsfUtil.addMessageError("Ocurrió un error al cargar la información del proponente.");
		}
	}
	
}
