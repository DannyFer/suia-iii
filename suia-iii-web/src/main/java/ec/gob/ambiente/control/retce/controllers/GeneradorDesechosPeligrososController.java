package ec.gob.ambiente.control.retce.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import lombok.Getter;
import lombok.Setter;

import org.jbpm.process.audit.ProcessInstanceLog;
import org.kie.api.task.model.TaskSummary;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.control.retce.beans.DeclaracionGeneradorRetceBean;
import ec.gob.ambiente.rcoa.digitalizacion.facade.AutorizacionAdministrativaAmbientalFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.ProyectoAsociadoDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.model.AutorizacionAdministrativaAmbiental;
import ec.gob.ambiente.rcoa.digitalizacion.model.ProyectoAsociadoDigitalizacion;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.PuntoRecuperacionRgdRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosProyectosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.PuntoRecuperacionRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosProyectosRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosRcoa;
import ec.gob.ambiente.retce.model.DetalleCatalogoGeneral;
import ec.gob.ambiente.retce.model.GeneradorDesechosPeligrososRetce;
import ec.gob.ambiente.retce.model.InformacionProyecto;
import ec.gob.ambiente.retce.services.DetalleCatalogoGeneralFacade;
import ec.gob.ambiente.retce.services.GeneradorDesechosPeligrososFacade;
import ec.gob.ambiente.retce.services.InformacionProyectoFacade;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.comparator.OrdenarTareaPorEstadoComparator;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.GeneradorDesechosPeligrosos;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.PuntoRecuperacion;
import ec.gob.ambiente.suia.domain.RegistroGeneradorDesechosAsociado;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.ResumeTarea;
import ec.gob.ambiente.suia.dto.Tarea;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.ConvertidorObjetosDominioUtil;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.webservicesclientes.facade.JbpmSuiaCustomServicesFacade;

@ManagedBean
@ViewScoped
public class GeneradorDesechosPeligrososController {
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{declaracionGeneradorRetceBean}")
	private DeclaracionGeneradorRetceBean declaracionGeneradorRetceBean;
	
	@EJB
	private GeneradorDesechosPeligrososFacade generadorDesechosPeligrososFacade;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private DetalleCatalogoGeneralFacade detalleCatalogoGeneralFacade;
	
	@EJB
	private InformacionProyectoFacade informacionProyectoFacade;
	
	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;
	
	@EJB
	private DocumentosFacade documentosFacade;

	@EJB
	private ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade;
	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	@EJB
	private PuntoRecuperacionRgdRcoaFacade puntoRecuperacionRgdRcoaFacade;
	
	/*************************************  Digitalizacion ***************************************************************/
	@EJB
	private AutorizacionAdministrativaAmbientalFacade autorizacionAdministrativaAmbientalFacade;
	@EJB
	private ProyectoAsociadoDigitalizacionFacade proyectoAsociadoDigitalizacionFacade;
	
	/*************************************  RCOA *************************************************/
	@EJB
	private RegistroGeneradorDesechosProyectosRcoaFacade registroGeneradorDesechosProyectosRcoaFacade;
	
	@Getter
	@Setter
	private List<RegistroGeneradorDesechosRcoa> listaGeneradoresRcoa;
	
	@Getter
	@Setter
	private RegistroGeneradorDesechosRcoa generadorSeleccionadoRcoa;
	
	/************************************************* fin Rcoa *********************************************/
	
	@EJB
	private AreaFacade areaFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@Getter
	@Setter
	private List<GeneradorDesechosPeligrosos> listaGeneradores;
	
	@Getter
	@Setter
	private List<DesechoPeligroso> listaDesechosGenerador;
	
	@Getter
	@Setter
	private List<GeneradorDesechosPeligrososRetce> listaDeclaracionesRgd;
	
	@Getter
	@Setter
	private InformacionProyecto informacionProyecto;
	
	@Getter
	@Setter
	private GeneradorDesechosPeligrosos generadorSeleccionado;
	
	@Getter
	@Setter
	private GeneradorDesechosPeligrososRetce generadorDesechosRetce;
	
	@Getter
	@Setter
	private List<Integer> listaAnios;
	
	@Getter
	@Setter
	private Boolean agregarNuevo, reporteHabilitado;
	
	@Getter
	@Setter
	private String inicioReporte, finReporte;
	
	@Getter
	@Setter
	private Date fechaRGD;
	
	@Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyecto;

	@PostConstruct
	public void init() {
		try {

			Integer idInformacionBasica =(Integer)(JsfUtil.devolverObjetoSession(InformacionProyecto.class.getSimpleName()));
			if(idInformacionBasica!=null)
			{
				informacionProyecto=informacionProyectoFacade.findById(idInformacionBasica);
				//verifico si es un proyecto anterior y de un enete acreditado
				if(informacionProyecto != null && informacionProyecto.getId() != null && informacionProyecto.getAreaSeguimiento() != null && informacionProyecto.getAreaSeguimiento().getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_EA)){
					ProyectoLicenciamientoAmbiental proyectoSuia = proyectoLicenciamientoAmbientalFacade.buscarProyectoPorCodigoCompleto(informacionProyecto.getCodigo());
					if(proyectoSuia != null && proyectoSuia.getId() != null){
						// si es proyectos suia busco la direccion zonal en base a la ubicacion geografica
						if(proyectoSuia.getProyectoUbicacionesGeograficas() != null && proyectoSuia.getProyectoUbicacionesGeograficas().size() > 0){
							UbicacionesGeografica ubicacion = proyectoSuia.getProyectoUbicacionesGeograficas().get(0).getUbicacionesGeografica();
							Area areaTramite = ubicacion.getUbicacionesGeografica().getAreaCoordinacionZonal();
							informacionProyecto.setAreaSeguimiento(areaTramite);
						}
					}
				}
				JsfUtil.cargarObjetoSession(InformacionProyecto.class.getSimpleName(), null);
			}else{
				return;
			}
			
			listaDeclaracionesRgd = generadorDesechosPeligrososFacade.getRgdRetceByInformacionProyecto(idInformacionBasica);
			
			validarFechaReporte();
			
			agregarNuevo = false;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void validarFechaReporte() throws ParseException {
		DetalleCatalogoGeneral periodoRegistro = detalleCatalogoGeneralFacade.findByCodigo("periodo.generador");
		
		Calendar fecha = Calendar.getInstance();
		String fechaActualString = fecha.get(Calendar.DAY_OF_MONTH) + "/" + (fecha.get(Calendar.MONTH) + 1)  + "/" + fecha.get(Calendar.YEAR);
		Date fechaActual = new SimpleDateFormat("dd/MM/yyyy").parse(fechaActualString);
		
		Integer anioActual = JsfUtil.getYearFromDate(fechaActual);
		String inicioString = periodoRegistro.getParametro() + "/" + anioActual;
		String finString = periodoRegistro.getParametro2() + "/" + anioActual;
		Date fechaInicio = new SimpleDateFormat("dd/MM/yyyy").parse(inicioString);
		Date fechaFin = new SimpleDateFormat("dd/MM/yyyy").parse(finString);
		
		if(fechaActual.compareTo(fechaInicio) >= 0 && fechaActual.compareTo(fechaFin) <= 0) 
			reporteHabilitado = true;
		else
			reporteHabilitado = false;

		inicioReporte = JsfUtil.devuelveDiaMesEnLetras(fechaInicio);
		finReporte = JsfUtil.devuelveDiaMesEnLetras(fechaFin);
	}
	
	public void getGeneradoresAprobados(List<GeneradorDesechosPeligrosos> listaGeneradoresResult){
		try {
			for (GeneradorDesechosPeligrosos generador: listaGeneradoresResult) {
				List<ProcessInstanceLog> procesosTramite = procesoFacade
						.getProcessInstancesLogsVariableValue(loginBean.getUsuario(),
								"numeroSolicitud", generador.getSolicitud());
				
				if (procesosTramite.size() > 0) {
					for (ProcessInstanceLog processLog : procesosTramite) {
						if (processLog.getProcessId().equals(
										Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS)) {
							Long processInstanceId = processLog.getProcessInstanceId();
		
							TaskSummary tareaActual = procesoFacade.getCurrenTask(loginBean.getUsuario(),
									processInstanceId);
							
							Boolean procesoAprobado = false;
							String fechaAprobacion = obtenerFechaAprobacion(tareaActual, processInstanceId);
							if(fechaAprobacion != null) {
								procesoAprobado = true;
								generador.setFecha(fechaAprobacion);
							}							
							
							if(procesoAprobado && !listaGeneradores.contains(generador)){
								listaGeneradores.add(generador);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String obtenerFechaAprobacion(TaskSummary tareaActual, Long processInstanceId) {
		Date fechaAprobacion = null;
		DateFormat formatoFecha = DateFormat.getDateInstance(DateFormat.MEDIUM, new Locale("es"));
		try{
			List<Tarea> tareas = new ArrayList<Tarea>();
			
			if (tareaActual != null) {
				List<TaskSummary> tareasFlujo = procesoFacade.getTaskBySelectFlow(loginBean.getUsuario(), processInstanceId);
				
				if (tareasFlujo.size() > 1) {
					for (TaskSummary tareaSummary : tareasFlujo) {
						Tarea tarea = new Tarea();
						ConvertidorObjetosDominioUtil.convertirTaskSummaryATarea(tareaSummary, tarea);
						if (!tareas.contains(tarea))
							tareas.add(tarea);
					}
				}
			} else {
				List<ResumeTarea> resumeTareas = BeanLocator.getInstance(JbpmSuiaCustomServicesFacade.class).getResumenTareas(processInstanceId);
				for (ResumeTarea resumeTarea : resumeTareas) {
					Tarea tarea = new Tarea();
					if (!(resumeTarea.getStatus().equals("Exited") || resumeTarea.getStatus().equals("Created") || resumeTarea.getStatus().equals("Ready"))){
					ConvertidorObjetosDominioUtil.convertirBamTaskSummaryATarea(resumeTarea, tarea);
					if(!tareas.contains(tarea))
						tareas.add(tarea);
					}
				}
			}
			
			Collections.sort(tareas, new OrdenarTareaPorEstadoComparator());
			
			for (Tarea tarea : tareas) {
			    if ((tarea.getNombre().equals("Firmar pronunciamiento favorable con registro de generador") || 
			         tarea.getNombre().equals("Firmar y enviar el oficio de pronunciamiento y el permiso RGD")) 
			         && tarea.getEstado().equals("Completada")) {
			        fechaAprobacion = tarea.getFechaFin();
			        break;
			    } else if (tarea.getNombre().equals("Ingresar datos del registro") && tarea.getEstado().equals("Completada")) {
			        fechaAprobacion = informacionProyecto.getFechaEmision();
			        break;
			    }
			}
		this.fechaRGD = fechaAprobacion;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return (fechaAprobacion == null) ? null : formatoFecha.format(fechaAprobacion);
	}

	public void seleccionarRgd(){		
		listaDesechosGenerador = generadorDesechosPeligrososFacade.getDesechosByGenerador(generadorSeleccionado.getId());
	}
	
	private void cargarListaAnios() {
		Date nuevaFecha = new Date();
		Integer i= JsfUtil.getYearFromDate(nuevaFecha);
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.YEAR, -1);
		nuevaFecha = cal.getTime();
		listaAnios = new ArrayList<Integer>();

		for ( i=2019; i<= JsfUtil.getYearFromDate(nuevaFecha); i++){
			listaAnios.add(i);
		}
	}
	
	
	public void validateSeleccionProyecto(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		if(generadorSeleccionado == null && generadorSeleccionadoRcoa == null ){
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Debe seleccionar el proyecto de registro de generador de desechos peligrosos.", null));
		}
		// proyecto RGD SUIA
		if(generadorSeleccionado != null){
			GeneradorDesechosPeligrososRetce generadorRetce = generadorDesechosPeligrososFacade.getRgdRetce(generadorSeleccionado.getId(),
							generadorDesechosRetce.getAnioDeclaracion());
			if(generadorRetce != null){
				generadorDesechosRetce = generadorRetce;
				
				if(generadorDesechosRetce.getRegistroFinalizado()){
					errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Usted ya generó el reporte para el RGDP y año seleccionado.", null));
				}
			}
		}
		// proyecto RGD  RCOA
		if(generadorSeleccionadoRcoa != null){
			GeneradorDesechosPeligrososRetce generadorRetce = generadorDesechosPeligrososFacade.getRgdRetce(generadorSeleccionadoRcoa.getId(),
							generadorDesechosRetce.getAnioDeclaracion());
			if(generadorRetce != null){
				generadorDesechosRetce = generadorRetce;
				
				if(generadorDesechosRetce.getRegistroFinalizado()){
					errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Usted ya generó el reporte para el RGDP y año seleccionado.", null));
				}
			}
		}
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public String iniciarReporte() {
		try{
			//para actualizar el área a las OT. si es un tramite nuevo o uno previamente registrado
			ProyectoLicenciamientoAmbiental proyectoLicencia = proyectoLicenciamientoAmbientalFacade.buscarProyectoPorCodigoCompleto(informacionProyecto.getCodigo());
			ProyectoLicenciaCoa proyectoRcoa = proyectoLicenciaCoaFacade.buscarProyecto(informacionProyecto.getCodigo());
			AutorizacionAdministrativaAmbiental proyectoDigital = autorizacionAdministrativaAmbientalFacade.obtenerAAAPorCodigoProyecto(informacionProyecto.getCodigo());
			UbicacionesGeografica ubicacion = (proyectoLicencia != null) ? proyectoLicencia.getProyectoUbicacionesGeograficas().get(0).getUbicacionesGeografica() 
					: (proyectoRcoa.getCodigoUnicoAmbiental() != null) ? proyectoRcoa.getAreaResponsable().getUbicacionesGeografica() 
							: (proyectoDigital.getCodigoDigitalizacion() != null) ? proyectoDigital.getAreaEmisora().getUbicacionesGeografica() : null;	
			Area areaRGD = (ubicacion != null && ubicacion.getUbicacionesGeografica() != null &&  ubicacion.getUbicacionesGeografica().getAreaCoordinacionZonal() != null) ? ubicacion.getUbicacionesGeografica().getAreaCoordinacionZonal() : informacionProyecto.getAreaSeguimiento();
			Area areaResponsable = (generadorDesechosRetce.getId() != null)? areaFacade.getAreaWithOutFilters(generadorDesechosRetce.getIdArea()) : null;
			
			if((generadorDesechosRetce.getId() == null && areaRGD.getTipoArea().getSiglas().equals("DP")) || (generadorDesechosRetce.getId() != null && areaResponsable != null && areaResponsable.getTipoArea().getSiglas().equals("DP")) || (generadorDesechosRetce.getId() == null && areaRGD.getTipoArea().getSiglas().equals("OT"))) {
				if(generadorSeleccionado != null){
					List<PuntoRecuperacion> puntosRecuperacion = registroGeneradorDesechosFacade.buscarPuntoRecuperacion(generadorSeleccionado);
					if(puntosRecuperacion.size() > 0)
						areaResponsable = areaFacade.getAreaCoordinacionZonal(puntosRecuperacion.get(0).getUbicacionesGeografica().getUbicacionesGeografica());
					else 
						areaResponsable = null;
				}else if(generadorSeleccionadoRcoa != null){
					List<PuntoRecuperacionRgdRcoa> puntosRecuperacionRcoa = puntoRecuperacionRgdRcoaFacade.buscarPorRgd(generadorSeleccionadoRcoa.getId());
					if(puntosRecuperacionRcoa.size() > 0){
						if(puntosRecuperacionRcoa.get(0).getUbicacionesGeografica().getUbicacionesGeografica().getCodificacionInec().contains("200"))
							areaResponsable = areaFacade.getArea(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS);
						else
							areaResponsable = areaFacade.getAreaCoordinacionZonal(puntosRecuperacionRcoa.get(0).getUbicacionesGeografica().getUbicacionesGeografica());
					}else{ 
						areaResponsable = null;
					}
				}
			} else if(generadorDesechosRetce.getId() == null)
				areaResponsable = areaRGD;
			
			
			if(areaResponsable == null) {
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				return null;
			}
				
			if(generadorDesechosRetce.getId() == null){
				generadorDesechosRetce.setCodigoGenerador(generadorDesechosPeligrososFacade.generarCodigoTramite());
				generadorDesechosRetce.setInformacionProyecto(informacionProyecto);
				if(generadorSeleccionado != null && generadorSeleccionado.getId() != null){
					generadorDesechosRetce.setIdGeneradorDesechosPeligrosos(generadorSeleccionado.getId());
					generadorDesechosRetce.setCodigoGeneradorDesechosPeligrosos(generadorSeleccionado.getSolicitud());
					generadorDesechosRetce.setFechaAprobacionRgd(new SimpleDateFormat("dd-MMM-yyyy", new Locale("es","ES")).parse(generadorSeleccionado.getFecha()));
				}
				if(generadorSeleccionadoRcoa != null && generadorSeleccionadoRcoa.getId() != null){
					generadorDesechosRetce.setIdGeneradorDesechosPeligrososRcoa(generadorSeleccionadoRcoa.getId());
					generadorDesechosRetce.setCodigoGeneradorDesechosPeligrosos(generadorSeleccionadoRcoa.getCodigo());
					generadorDesechosRetce.setFechaAprobacionRgd(generadorSeleccionadoRcoa.getFecha() == null ? new SimpleDateFormat("dd-MMM-yyyy", new Locale("es", "ES")).parse(generadorSeleccionadoRcoa.getFecha()) : fechaRGD);
				}
				generadorDesechosRetce.setIdArea(areaResponsable.getId());
				generadorDesechosRetce.setRegistroFinalizado(false);
				
				generadorDesechosPeligrososFacade.guardarRgdRetce(generadorDesechosRetce);
			} else {
				if(!areaResponsable.getId().equals(generadorDesechosRetce.getIdArea())) {
					generadorDesechosRetce.setIdArea(areaResponsable.getId());
					generadorDesechosPeligrososFacade.guardarRgdRetce(generadorDesechosRetce);
				}
			}
			
			declaracionGeneradorRetceBean.init();
			declaracionGeneradorRetceBean.setGeneradorDesechosRetce(generadorDesechosRetce);
			declaracionGeneradorRetceBean.setListaDesechosGenerador(listaDesechosGenerador);
			
			return JsfUtil.actionNavigateTo("/control/retce/generadorDesechos/generadorDesechosPeligrosos.jsf");
		} catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			return null;
		}
	}
	
	public void agregarDeclaracion() {
		cargarGeneradores();
		
		cargarListaAnios();
		
		generadorDesechosRetce = new GeneradorDesechosPeligrososRetce();
		generadorSeleccionado = null;
		agregarNuevo = true;
	}
	
	public void cargarGeneradores() {
		try{
			List<GeneradorDesechosPeligrosos> registrosGeneradores = new ArrayList<>();
			listaGeneradoresRcoa = new ArrayList<RegistroGeneradorDesechosRcoa>();
			if(informacionProyecto.getEsEmisionFisica()){
				//buscar asociados
				List<RegistroGeneradorDesechosAsociado> generadoresAsociados = generadorDesechosPeligrososFacade.getGeneradoresAsociadosLicenciaFisica(informacionProyecto.getNumeroResolucion());
				if(generadoresAsociados != null && generadoresAsociados.size() > 0){
					for (RegistroGeneradorDesechosAsociado registroGeneradorDesechosAsociado : generadoresAsociados) {
						GeneradorDesechosPeligrosos generadorDesechoAsoc = registroGeneradorDesechosFacade.getRGD(registroGeneradorDesechosAsociado.getCodigoDesecho());
						if (generadorDesechoAsoc != null)
							registrosGeneradores.add(generadorDesechoAsoc);
					}
				}
			} else {
				registrosGeneradores = generadorDesechosPeligrososFacade.getByProyectoLicenciaAmbiental(informacionProyecto.getCodigo());
				if(registrosGeneradores == null || registrosGeneradores.size() == 0){
					registrosGeneradores = generadorDesechosPeligrososFacade.getByProyectoRcoa(informacionProyecto.getCodigo());	
				}
				List<RegistroGeneradorDesechosProyectosRcoa> listaGeneradorRcoa = registroGeneradorDesechosProyectosRcoaFacade.buscarPorCodigoProyectoRcoa(informacionProyecto.getCodigo());
				if(listaGeneradorRcoa == null || listaGeneradorRcoa.size() == 0){
					// verifico si es un proyecto del suia_iii
					ProyectoLicenciamientoAmbiental proyecto=proyectoLicenciaAmbientalFacade.getProyectoPorCodigo(informacionProyecto.getCodigo());
					if(proyecto != null && proyecto.getId() != null){
						listaGeneradorRcoa = registroGeneradorDesechosProyectosRcoaFacade.buscarPorProyectoSuia(proyecto.getId());
					}
					if(listaGeneradorRcoa == null || listaGeneradorRcoa.size() == 0){
						// verifico si es un proyecto de digitalizacion
						AutorizacionAdministrativaAmbiental autorizacionAdministrativa = autorizacionAdministrativaAmbientalFacade.obtenerAAAPorCodigoProyecto(informacionProyecto.getCodigo());
						if(autorizacionAdministrativa != null && autorizacionAdministrativa.getId() != null){
							listaGeneradorRcoa = registroGeneradorDesechosProyectosRcoaFacade.buscarPorTipoProyecto(autorizacionAdministrativa.getId(), "digitalizacion");
							// si no tiene RGD AAA busco si tiene un RGD vinculado en digitalizacion
							if(listaGeneradorRcoa == null || listaGeneradorRcoa.size() == 0){
								List<ProyectoAsociadoDigitalizacion> listaProyectosAsociados = proyectoAsociadoDigitalizacionFacade.buscarProyectosAsociados(autorizacionAdministrativa.getId());
								for (ProyectoAsociadoDigitalizacion proyectoAsociado : listaProyectosAsociados) {
									if(proyectoAsociado.getTipoProyecto() != null && proyectoAsociado.getTipoProyecto().toString().equals("4")){
										GeneradorDesechosPeligrosos objGenerador = registroGeneradorDesechosFacade.get(proyectoAsociado.getProyectoAsociadoId());
										if (objGenerador != null)
											registrosGeneradores.add(objGenerador);
										break;
									}
								}
							}
						}
					}
				}
				if(listaGeneradorRcoa != null && listaGeneradorRcoa.size() > 0){
					getGeneradoresAprobadosRcoa(listaGeneradorRcoa);
				}
			}

			listaGeneradores = new ArrayList<>();
			if(registrosGeneradores != null && registrosGeneradores.size() > 0)
				getGeneradoresAprobados(registrosGeneradores);
		}catch(Exception ex){
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public String verInfoReporte(GeneradorDesechosPeligrososRetce generador, Integer accion) {
		if(accion == 0){
			cargarGeneradores();
			
			cargarListaAnios();
			
			generadorDesechosRetce = generador;
			for (GeneradorDesechosPeligrosos generadorDesechos : listaGeneradores) {
				if(generadorDesechos.getId().equals(generador.getIdGeneradorDesechosPeligrosos())){
					generadorSeleccionado = generadorDesechos;
					seleccionarRgd();
					break;
				}
			}
			// para rgd de proyecto rcoa
			for (RegistroGeneradorDesechosRcoa generadorDesechos : listaGeneradoresRcoa) {
				if(generadorDesechos.getId().equals(generador.getIdGeneradorDesechosPeligrososRcoa())){
					generadorSeleccionadoRcoa = generadorDesechos;
					seleccionarRgdRcoa();
					break;
				}
			}
			agregarNuevo = true;
		} else {
			JsfUtil.cargarObjetoSession(GeneradorDesechosPeligrososRetce.class.getSimpleName(), generador.getId());
			return JsfUtil.actionNavigateTo("/control/retce/generadorDesechos/generadorDesechosPeligrososVer.jsf");
		}
		
		return null;
	}
	
	public void eliminarConsumo(GeneradorDesechosPeligrososRetce generador) {
		try{
			generador.setEstado(false);
			generadorDesechosPeligrososFacade.eliminarGenerador(generador);
			
			listaDeclaracionesRgd.remove(generador);
			
			JsfUtil.addMessageInfo("La eliminación se realizó satisfactoriamente.");
			
		} catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public void cerrarNuevoGenerador() {
		agregarNuevo = false;
	}
	
	public StreamedContent descargar(GeneradorDesechosPeligrososRetce declaracion) throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			Documento documento = null;
			
			List<Documento> oficios = documentosFacade.documentoXTablaIdXIdDoc(
					declaracion.getId(), "OficioPronunciamientoRetceGenerador",
					 TipoDocumentoSistema.OFICIO_APROBACION_GENERADOR);
			if (oficios.size() > 0) {
				documento =  oficios.get(0);
				if (documento != null && documento.getIdAlfresco() != null) {
					documentoContent = documentosFacade.descargar(documento.getIdAlfresco());
				} else if (documento.getContenidoDocumento() != null) {
					documentoContent = documento.getContenidoDocumento();
				}
			}
			
			if (documento != null && documento.getNombre() != null
					&& documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documento.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void validarData() {
		if (informacionProyecto == null) {
			JsfUtil.redirectTo("/control/retce/informacionBasica.jsf");
		}
	}
	
	/*********************************************************** RCOA *************************************************/
	

	public void getGeneradoresAprobadosRcoa(List<RegistroGeneradorDesechosProyectosRcoa> listaGeneradoresResult){
		try {
			for (RegistroGeneradorDesechosProyectosRcoa generador: listaGeneradoresResult) {
				if (generador.getRegistroGeneradorDesechosRcoa() != null){
					List<ProcessInstanceLog> procesosTramite = procesoFacade
							.getProcessInstancesLogsVariableValue(loginBean.getUsuario(),
									"tramite", informacionProyecto.getCodigo());
					boolean tieneRgdBpm = false;
					if (procesosTramite.size() > 0) {
						for (ProcessInstanceLog processLog : procesosTramite) {
							if (processLog.getProcessId().equals(Constantes.RCOA_REGISTRO_GENERADOR_DESECHOS)) {
								Long processInstanceId = processLog.getProcessInstanceId();
								tieneRgdBpm = true;
								TaskSummary tareaActual = procesoFacade.getCurrenTask(loginBean.getUsuario(), processInstanceId);
								Boolean procesoAprobado = false;
								String fechaAprobacion = obtenerFechaAprobacion(tareaActual, processInstanceId);
								if(fechaAprobacion != null) {
									procesoAprobado = true;
									generador.getRegistroGeneradorDesechosRcoa().setFecha(fechaAprobacion);
									generador.getRegistroGeneradorDesechosRcoa().setAreaResponsable(informacionProyecto.getAreaSeguimiento());
								}
								if(procesoAprobado && !listaGeneradoresRcoa.contains(generador)){
									listaGeneradoresRcoa.add(generador.getRegistroGeneradorDesechosRcoa());
								}
							}
						}
						
						if(!tieneRgdBpm && generador.getProyectoLicenciaCoa() != null && generador.getProyectoLicenciaCoa().getCodigoRgdAsociado() != null){
							listaGeneradoresRcoa.add(generador.getRegistroGeneradorDesechosRcoa());
							Date fechaAprobacion = informacionProyecto.getFechaEmision();
							generador.getRegistroGeneradorDesechosRcoa().setFecha(new SimpleDateFormat("dd-MMM-yyyy").format(fechaAprobacion));
							this.fechaRGD = fechaAprobacion;
						}	
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void seleccionarRgdRcoa(){		
		listaDesechosGenerador = generadorDesechosPeligrososFacade.getDesechosByGeneradorRcoa(generadorSeleccionadoRcoa.getId());
	}
	/***********************************************************************************************************/
}