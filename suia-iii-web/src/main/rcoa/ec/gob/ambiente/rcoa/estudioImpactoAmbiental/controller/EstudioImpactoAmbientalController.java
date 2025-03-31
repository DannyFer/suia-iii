package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.kie.api.task.model.TaskSummary;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.dto.EntityPmaEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.AclaracionProrrogaEsIAFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.CatalogoFasesCoaFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.DocumentosImpactoEstudioFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.EquipoApoyoProyectoEIACoaFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.InformacionProyectoEIACoaFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.InformeTecnicoEsIAFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.ObservacionesEsIAFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.OficioPronunciamientoEsIAFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.PlanManejoAmbientalEsIAFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.PlanManejoEsIAObservacionesFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.ProrrogaModificacionEstudioFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.ProyectoFasesCoaFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.AclaracionProrrogaEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.CatalogoFasesCoa;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.DetallePlanManejoEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.DocumentoEstudioImpacto;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformacionProyectoEia;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformeTecnicoEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.ObservacionesEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.OficioPronunciamientoEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.PlanManejoAmbientalEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.PlanManejoEsIAObservaciones;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.ProgramaPlanManejoEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.ProrrogaModificacionEstudio;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.ProyectoFasesEiaCoa;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaAmbientalCoaCiuuBloquesFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaAmbientalConcesionesMinerasFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaAmbientalCoaCiuuBloques;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaAmbientalConcesionesMineras;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.model.SubActividades;
import ec.gob.ambiente.rcoa.utils.DiasHabilesUtil;
import ec.gob.ambiente.suia.administracion.facade.FeriadosFacade;
import ec.gob.ambiente.suia.administracion.service.AreaService;
import ec.gob.ambiente.suia.bandeja.BandejaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.domain.Holiday;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.CodigoTasa;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.EntityAdjunto;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.recaudaciones.controllers.GenerarNUTController;
import ec.gob.ambiente.suia.recaudaciones.facade.NumeroUnicoTransaccionalFacade;
import ec.gob.ambiente.suia.recaudaciones.facade.SolicitudTramiteFacade;
import ec.gob.ambiente.suia.recaudaciones.facade.TarifasFacade;
import ec.gob.ambiente.suia.recaudaciones.facade.TarifasNUTFacade;
import ec.gob.ambiente.suia.recaudaciones.model.Cuentas;
import ec.gob.ambiente.suia.recaudaciones.model.EstadosNut;
import ec.gob.ambiente.suia.recaudaciones.model.NumeroUnicoTransaccional;
import ec.gob.ambiente.suia.recaudaciones.model.SolicitudUsuario;
import ec.gob.ambiente.suia.recaudaciones.model.Tarifas;
import ec.gob.ambiente.suia.recaudaciones.model.TarifasNUT;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class EstudioImpactoAmbientalController {

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @Getter
    @Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
	@EJB
	private DocumentosImpactoEstudioFacade documentosFacade;
	@EJB
	private InformacionProyectoEIACoaFacade informacionProyectoEIACoaFacade;
	@EJB
	private CatalogoFasesCoaFacade catalogoFasesCoaFacade;
	@EJB
	private ProyectoFasesCoaFacade proyectoFasesCoaFacade;
	@EJB
	private ProyectoLicenciaAmbientalCoaCiuuBloquesFacade proyectoLicenciaAmbientalCoaCiuuBloquesFacade;
	@EJB
	private ProyectoLicenciaAmbientalConcesionesMinerasFacade proyectoLicenciaAmbientalConcesionesMinerasFacade;
	@EJB
	private BandejaFacade bandejaFacade;
	@EJB
	private AclaracionProrrogaEsIAFacade aclaracionProrrogaEsIAFacade;
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private InformeTecnicoEsIAFacade informeTecnicoEsIAFacade;
	@EJB
	private EquipoApoyoProyectoEIACoaFacade equipoApoyoProyectoEIACoaFacade;
	@EJB
    private ObservacionesEsIAFacade observacionesEsIAFacade;
	@EJB
    private ProrrogaModificacionEstudioFacade prorrogaModificacionEstudioFacade;
	@EJB
    private FeriadosFacade feriadosFacade;
	@EJB
	private NumeroUnicoTransaccionalFacade numeroUnicoTransaccionalFacade;
	@EJB
	private DocumentosImpactoEstudioFacade documentosEstudioFacade;
	@EJB
	private SolicitudTramiteFacade solicitudTramiteFacade;
	@EJB
	private TarifasNUTFacade tarifasNUTFacade;
	@EJB
	private SecuenciasFacade secuenciasFacade;
	@EJB
	private TarifasFacade tarifasFacade;
	@EJB
	private OficioPronunciamientoEsIAFacade oficioPronunciamientoEsIAFacade;
	@EJB
	private PlanManejoAmbientalEsIAFacade planManejoAmbientalEsIAFacade;
	@EJB
	private PlanManejoEsIAObservacionesFacade planManejoEsIAObservacionesFacade;
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	@EJB
    private AreaService areaService;
	@EJB
	private DiasHabilesUtil diasHabilesUtil;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;

	@Getter
	@Setter
	private List<ProyectoLicenciaAmbientalCoaCiuuBloques> listaActividadBloques;
	
	@Getter
	@Setter
	private List<ProyectoLicenciaAmbientalConcesionesMineras> listaActividadConcesiones;
	
	@Getter
	@Setter
	private List<DocumentoEstudioImpacto> listaDocumentosAdjuntos;
	
	@Getter
	@Setter
	private List<DocumentoEstudioImpacto> documentosNUT;
	
	@Getter
	@Setter
	private List<CatalogoFasesCoa> listaFasesPorSector;
	
	@Getter
	@Setter
	private List<ProyectoFasesEiaCoa> listaFasesProyecto;
	
	@Getter
	@Setter
	private List<EntityPmaEsIA> listaPlanManejo;
	
	@Getter
	@Setter
	private List<DocumentoEstudioImpacto> listaDocumentoPlantillaEliminar, listaDocumentoPlantillaEliminarAux;
	
	@Getter
	@Setter
	private List<ProgramaPlanManejoEsIA> listaProgramasSubPlan, listaProgramasEliminarAux, listaProgramasEliminar;
	
	@Getter
	@Setter
	private DocumentoEstudioImpacto documentoAdjunto;
	
	@Getter
    @Setter
	private InformacionProyectoEia informacionProyectoEia;
	
	@Getter
    @Setter
	private ProrrogaModificacionEstudio datosProrrogaModificacion;

	@Getter
    @Setter
    private TipoSector sector;

	@Getter
    @Setter
    private String paginaActiva, titulo="", nombreClaseObservaciones, mensajeSubPlanesObservados, detalleObservacionPma, campoObservacionPma, nombreClaseObservacionesPma;
	
	@Getter
	@Setter
	private List<String> listaFasesSectorId, listaFasesProyectoGuardadas;
	
	@Getter
	@Setter
	private List<EntityAdjunto> listaDocumentosPorPagina;
	
	@Getter
	@Setter
	private DocumentoEstudioImpacto documentoJustificacionProrroga, documentoPlantillaPma;
	
	@Getter
	@Setter
	private AclaracionProrrogaEsIA aclaracionProrrogaEsIA;
	
	@Getter
	@Setter
	private EntityPmaEsIA planSeleccionado;

	@Getter
    @Setter
	private boolean validarDatos, habilitadoIngreso, mostrarObservaciones, editarObservaciones, habilitarSiguiente, esRevisionEstudio, mostrarResumenObservaciones;
	
	@Getter
    @Setter
	private boolean mostrarSiguiente=true, btnRegresar, mostrarFases;

	@Getter
    @Setter
	private boolean pagAnalisis, pagDemanda, pagDiagnostico, pagInventario, pagIdentificacion, pagRiesgos, pagImpacto, pagCiclo, pagPlan,observaciones_viables=false;

    private Map<String, Object> variables;
    
    private Map<String, List<DocumentoEstudioImpacto>> listadoDocumentosEIA = new HashMap<String, List<DocumentoEstudioImpacto>>();
	private String tramite, tipoDocumento, mensajeFilaIncompleta;
	
	@Getter
    @Setter
	private Integer numeroRevision, diasRestantesModificacion, maxDiasProrroga, diasPorRevision, indexPma, indexSubsanacion;
	
	@Getter
    @Setter
	private Integer idClaseObservaciones, idClaseObsPma;
	
	@Getter
    @Setter
	private Boolean requiereReunion, tieneProrroga, generarNUT, requiereIngresoPlan;
	
	@Getter
    @Setter
	private Double costoPrograma = 0.0, costoTotalPma = 0.0;
	
	@Getter
	@Setter
	private String zonaCamaronera;
	
	@Getter
	@Setter
	private Boolean esActividadCamaronera = false;
	
	@Getter
	@Setter
	private DocumentoEstudioImpacto documentoDocCamaroneraPlaya, documentoDocCamaroneraAlta;
	
	@PostConstruct
	private void init(){
		try{
			paginaActiva="EIA_ALCANCE_CICLO_VIDA";
			btnRegresar = false;
			mostrarFases = false;
			listaFasesSectorId = new ArrayList<String>();
			listaFasesProyectoGuardadas = new ArrayList<String>();
			listaDocumentosPorPagina = new ArrayList<EntityAdjunto>();
			listaActividadBloques = new ArrayList<ProyectoLicenciaAmbientalCoaCiuuBloques>();
			listaActividadConcesiones = new ArrayList<ProyectoLicenciaAmbientalConcesionesMineras>();
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
			//variable para el flujo de aprobacion final deternima si tiene observacinoes economicamente viables
			if(variables.get(Constantes.VARIABLE_OBSERVACIONES_VIABLES) != null){
				observaciones_viables=Boolean.parseBoolean(variables.get(Constantes.VARIABLE_OBSERVACIONES_VIABLES).toString());	
			}

			requiereIngresoPlan = false;
			if(variables.get("requiereIngresoPlan") != null){
				requiereIngresoPlan=Boolean.parseBoolean(variables.get("requiereIngresoPlan").toString());	
			}
			
			proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
			informacionProyectoEia = informacionProyectoEIACoaFacade.obtenerInformacionProyectoEIAPorProyecto(proyectoLicenciaCoa);
			if(informacionProyectoEia == null){
				JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
				return;
			}
			listaDocumentosAdjuntos = new ArrayList<DocumentoEstudioImpacto>();
			listaFasesPorSector = new ArrayList<CatalogoFasesCoa>();
			if(loginBean.getUsuario().getId() == null){
				JsfUtil.redirectTo("/start.jsf");
				return;
			}
			
			//cuando se visualiza el estudio desde otros procesos no se puede utilizar la variable requiereIngresoPlan
			//se debe consultar si hay informacion de PMA en base de datos
			if(!bandejaTareasBean.getTarea().getProcessId().contains("rcoa.Estudio")) {
				requiereIngresoPlan = getExisteInfoSubplanes();
			}

			if(bandejaTareasBean.getTarea().getTaskName().toUpperCase().contains("REVISAR")
					|| bandejaTareasBean.getTarea().getTaskName().toUpperCase().contains("CONSOLIDADO")){
				String tarea = bandejaFacade.getNombreTarea(JsfUtil.getCurrentTask().getTaskId());
				btnRegresar = true;
				esRevisionEstudio = true;
				validarDatos = true;
				habilitadoIngreso=false;
				mostrarObservaciones=true;
				editarObservaciones=true;
				habilitarSiguiente = true;
				if(tarea.equals("revisarEsIAIngresado")){
					mostrarObservaciones=false;
					editarObservaciones=false;
				}
				Integer idClaseObservaciones =(Integer)(JsfUtil.devolverObjetoSession(InformeTecnicoEsIA.class.getSimpleName()));
				if(idClaseObservaciones != null) {
					this.idClaseObservaciones = informacionProyectoEia.getId();
					InformeTecnicoEsIA informeTecnico = informeTecnicoEsIAFacade.obtenerPorId(idClaseObservaciones);
					
					nombreClaseObservaciones = InformeTecnicoEsIA.class.getSimpleName() + "_" + informeTecnico.getUsuarioCreacion() + "_" + informeTecnico.getTipoInforme();
					nombreClaseObservacionesPma =  InformeTecnicoEsIA.class.getSimpleName() + "_" + informeTecnico.getUsuarioCreacion() + "_" + informeTecnico.getTipoInforme();
					if(!informeTecnico.getTipoInforme().equals(InformeTecnicoEsIA.apoyo)) {
						nombreClaseObservaciones = InformeTecnicoEsIA.class.getSimpleName() + "_" + informeTecnico.getTipoInforme() + ";" 
													+ InformeTecnicoEsIA.class.getSimpleName() + "_" + informeTecnico.getUsuarioCreacion() + "_" + informeTecnico.getTipoInforme();
						
						nombreClaseObservacionesPma =  InformeTecnicoEsIA.class.getSimpleName() + "_" + informeTecnico.getTipoInforme();
					}
					JsfUtil.cargarObjetoSession(InformeTecnicoEsIA.class.getSimpleName(), null);
				} else {
					//JsfUtil.redirectTo("/start.jsf");
					//return;
				}
			}else{
				esRevisionEstudio = false;
				validarDatos = true;
				habilitadoIngreso =true;
				mostrarObservaciones=false;
				editarObservaciones=false;
				habilitarSiguiente = false;
			}
	        FacesContext ctx = FacesContext.getCurrentInstance();
	        String urls = ctx.getViewRoot().getViewId();
	        if(urls.contains("verProyectoEIAR")){
				validarDatos = false;
				habilitadoIngreso =false;
				mostrarObservaciones=false;
				editarObservaciones=true;
				btnRegresar = true;
	        }
	        
	        //si esta para aprobacion final pero no tiene observacines economicamente viables no debe permitir mostrar observaciones 
	   			if(!observaciones_viables  &&  bandejaTareasBean.getTarea().getProcessId().contains(Constantes.RCOA_ESTUDIO_APROBACION_FINAL)){
	   				mostrarObservaciones=false;
	   			} 
	        
			// obtengo el sector
			List<ProyectoLicenciaCuaCiuu>  listaactividades = proyectoLicenciaCuaCiuuFacade.actividadesPorProyecto(proyectoLicenciaCoa);
			for (ProyectoLicenciaCuaCiuu objActividad : listaactividades) {
				if(objActividad.getPrimario()){
					sector = objActividad.getCatalogoCIUU().getTipoSector();
					if(sector.getId().equals(TipoSector.TIPO_SECTOR_ELECTRICO)
							|| sector.getId().equals(TipoSector.TIPO_SECTOR_MINERIA)
							|| sector.getId().equals(TipoSector.TIPO_SECTOR_HIDROCARBUROS)){
						listaFasesPorSector	= catalogoFasesCoaFacade.obtenerFasesPorSector(sector.getId());
						mostrarFases = true;
					}
					if(sector.getId().equals(TipoSector.TIPO_SECTOR_MINERIA)){
						listaActividadConcesiones = proyectoLicenciaAmbientalConcesionesMinerasFacade.cargarConcesiones(proyectoLicenciaCoa);
					}
					if(sector.getId().equals(TipoSector.TIPO_SECTOR_HIDROCARBUROS)){
						listaActividadBloques = proyectoLicenciaAmbientalCoaCiuuBloquesFacade.cargarBloques(proyectoLicenciaCoa);
					}
					
					break;
				}
			}
			// obtengo las fase del proyecto
			cargarFasesProyecto();
			// cargo los archivo subidos
		    tipoDocumento = "EIA_ALCANCE_CICLO_VIDA";
        	listaDocumentosAdjuntos = documentosFacade.documentoXTablaIdXIdDocLista(informacionProyectoEia.getId(), InformacionProyectoEia.class.getSimpleName(), TipoDocumentoSistema.EIA_ALCANCE_CICLO_VIDA);
	        listadoDocumentosEIA.put(tipoDocumento, listaDocumentosAdjuntos);
	        agregarListaDocumentos(tipoDocumento);
        	tipoDocumento = "EIA_ANALISIS_ALTERNATIVAS";
        	listaDocumentosAdjuntos = documentosFacade.documentoXTablaIdXIdDocLista(informacionProyectoEia.getId(), InformacionProyectoEia.class.getSimpleName(), TipoDocumentoSistema.EIA_ANALISIS_ALTERNATIVAS);
	        listadoDocumentosEIA.put(tipoDocumento, listaDocumentosAdjuntos);
        	agregarListaDocumentos(tipoDocumento);
	        tipoDocumento = "EIA_DEMANDA_RECURSOS_NATURALES";
	        listaDocumentosAdjuntos = documentosFacade.documentoXTablaIdXIdDocLista(informacionProyectoEia.getId(), InformacionProyectoEia.class.getSimpleName(), TipoDocumentoSistema.EIA_DEMANDA_RECURSOS_NATURALES);
	        listadoDocumentosEIA.put(tipoDocumento, listaDocumentosAdjuntos);
	        agregarListaDocumentos(tipoDocumento);
	        tipoDocumento = "EIA_DIAGNOSTICO_AMBIENTAL";
	        listaDocumentosAdjuntos = documentosFacade.documentoXTablaIdXIdDocLista(informacionProyectoEia.getId(), InformacionProyectoEia.class.getSimpleName(), TipoDocumentoSistema.EIA_DIAGNOSTICO_AMBIENTAL);
		    listadoDocumentosEIA.put(tipoDocumento, listaDocumentosAdjuntos);
		    agregarListaDocumentos(tipoDocumento);
	        tipoDocumento = "EIA_INVENTARIO_FORESTAL";
	        listaDocumentosAdjuntos = documentosFacade.documentoXTablaIdXIdDocLista(informacionProyectoEia.getId(), InformacionProyectoEia.class.getSimpleName(), TipoDocumentoSistema.EIA_INVENTARIO_FORESTAL);
		    listadoDocumentosEIA.put(tipoDocumento, listaDocumentosAdjuntos);
		    agregarListaDocumentos(tipoDocumento);
	        tipoDocumento = "EIA_IDENTIFICACION_DETERMINACION_AREAS";
	        listaDocumentosAdjuntos = documentosFacade.documentoXTablaIdXIdDocLista(informacionProyectoEia.getId(), InformacionProyectoEia.class.getSimpleName(), TipoDocumentoSistema.EIA_IDENTIFICACION_DETERMINACION_AREAS);
		    listadoDocumentosEIA.put(tipoDocumento, listaDocumentosAdjuntos);
		    agregarListaDocumentos(tipoDocumento);
	        tipoDocumento = "EIA_ANALISIS_RIESGOS";
	        listaDocumentosAdjuntos = documentosFacade.documentoXTablaIdXIdDocLista(informacionProyectoEia.getId(), InformacionProyectoEia.class.getSimpleName(), TipoDocumentoSistema.EIA_ANALISIS_RIESGOS);
		    listadoDocumentosEIA.put(tipoDocumento, listaDocumentosAdjuntos);
		    agregarListaDocumentos(tipoDocumento);
	        tipoDocumento = "EIA_EVALUACION_IMPACTOS_SOCIAMBIENTALES";
	        listaDocumentosAdjuntos = documentosFacade.documentoXTablaIdXIdDocLista(informacionProyectoEia.getId(), InformacionProyectoEia.class.getSimpleName(), TipoDocumentoSistema.EIA_EVALUACION_IMPACTOS_SOCIAMBIENTALES);
		    listadoDocumentosEIA.put(tipoDocumento, listaDocumentosAdjuntos);
		    agregarListaDocumentos(tipoDocumento);
	        tipoDocumento = "EIA_PLAN_MANEJO_AMBIENTAL_Y_SUB_PLANES";
	        listaDocumentosAdjuntos = documentosFacade.documentoXTablaIdXIdDocLista(informacionProyectoEia.getId(), InformacionProyectoEia.class.getSimpleName(), TipoDocumentoSistema.EIA_PLAN_MANEJO_AMBIENTAL_Y_SUB_PLANES);
		    listadoDocumentosEIA.put(tipoDocumento, listaDocumentosAdjuntos);
		    agregarListaDocumentos(tipoDocumento);
	        tipoDocumento = "EIA_ANEXOS";
	        listaDocumentosAdjuntos = documentosFacade.documentoXTablaIdXIdDocLista(informacionProyectoEia.getId(), InformacionProyectoEia.class.getSimpleName(), TipoDocumentoSistema.EIA_ANEXOS);
		    listadoDocumentosEIA.put(tipoDocumento, listaDocumentosAdjuntos);
		    agregarListaDocumentos(tipoDocumento);
		    tipoDocumento = "EIA_ALCANCE_CICLO_VIDA";
        	listaDocumentosAdjuntos = documentosFacade.documentoXTablaIdXIdDocLista(informacionProyectoEia.getId(), InformacionProyectoEia.class.getSimpleName(), TipoDocumentoSistema.EIA_ALCANCE_CICLO_VIDA);
        	
        	cargarDatosModificacion();
        	
        	if(requiereIngresoPlan) {
        		indexPma = -1;
        		indexSubsanacion = -1;
        		getInfoSubplanes();
        	}
        	
        	numeroRevision = 0;
        	String revision = (String) variables.get("numeroRevision");
			if(revision != null) {
				numeroRevision = Integer.parseInt(revision);
			}
			
			if(bandejaTareasBean.getTarea().getTaskName().toUpperCase().contains("INGRESAR")){
				boolean validar = false;
				 if(proyectoLicenciaCoa.getInterecaBosqueProtector() || proyectoLicenciaCoa.getInterecaSnap() || proyectoLicenciaCoa.getInterecaPatrimonioForestal()){
					 validar = false;
				 }else
					 validar = true;
			
				 if(validar){
					 validarUbicacionCamaroneras();
				 }	
			}
			
			validarActividadCamaronera();
			
			if(esActividadCamaronera){
				if(zonaCamaronera != null && zonaCamaronera.equals("MIXTA")){
					documentoDocCamaroneraAlta = documentosFacade.documentoXTablaIdXIdDoc(informacionProyectoEia.getId(), "RA_CONCESION_MINERA_ALTA", TipoDocumentoSistema.RCOA_DOCUMENTO_TITULO_CONCESION_CAMARONERA);
					documentoDocCamaroneraPlaya = documentosFacade.documentoXTablaIdXIdDoc(informacionProyectoEia.getId(), "RA_CONCESION_MINERA_PLAYA", TipoDocumentoSistema.RCOA_DOCUMENTO_TITULO_CONCESION_CAMARONERA);
										
				}else if(zonaCamaronera != null && zonaCamaronera.equals("ALTA")){
					documentoDocCamaroneraAlta = documentosFacade.documentoXTablaIdXIdDoc(informacionProyectoEia.getId(), "RA_CONCESION_MINERA_ALTA", TipoDocumentoSistema.RCOA_DOCUMENTO_TITULO_CONCESION_CAMARONERA);
				}else if(zonaCamaronera != null){
					documentoDocCamaroneraPlaya = documentosFacade.documentoXTablaIdXIdDoc(informacionProyectoEia.getId(), "RA_CONCESION_MINERA_PLAYA", TipoDocumentoSistema.RCOA_DOCUMENTO_TITULO_CONCESION_CAMARONERA);
				}
				if(zonaCamaronera == null){
					esActividadCamaronera = false;
				}
				if(documentoDocCamaroneraAlta == null && documentoDocCamaroneraPlaya == null){
					esActividadCamaronera = false;
				}
			}
			
			
		}catch(Exception e){
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
	}
	
	public void agregarListaDocumentos(String clave){
		if(listaDocumentosAdjuntos != null && listaDocumentosAdjuntos.size() > 0){
			EntityAdjunto objAdjunto = new EntityAdjunto();
			objAdjunto.setNombre(getEtiqueta(clave));
			objAdjunto.setExtension(clave);
			listaDocumentosPorPagina.add(objAdjunto);
		}
	}
	
	public void desabilitarMenupopup(){
		pagAnalisis=false;
		pagDemanda=false;
		pagDiagnostico=false;
		pagInventario=false;
		pagIdentificacion=false;
		pagRiesgos=false;
		pagImpacto=false;
		pagCiclo=false;
		pagPlan=false;
	}
	
	public void verDocumentos(EntityAdjunto tipoDocumentos){
		String indice = paginaActiva;
		paginaActiva = tipoDocumentos.getExtension();
		cargarDocumentosLista(new DocumentoEstudioImpacto());
		titulo = tipoDocumentos.getNombre();
		paginaActiva = indice;
	}
	
	public String getEtiqueta(String key){
		String clave = key.length() == 0? paginaActiva: key;
		switch (clave) {
		case "EIA_ANALISIS_ALTERNATIVAS":
			titulo = "Análisis de las alternativas de las actividades del proyecto";
			break;
		case "EIA_DEMANDA_RECURSOS_NATURALES":
			titulo = "Demanda de recursos naturales por parte del proyecto";
			break;
		case "EIA_DIAGNOSTICO_AMBIENTAL":
			titulo = "Diagnóstico ambiental de línea base";
			break;
		case "EIA_INVENTARIO_FORESTAL":
			titulo = "Inventario forestal";
			break;
		case "EIA_IDENTIFICACION_DETERMINACION_AREAS":
			titulo = "Identificación y determinación de áreas de influencia y áreas sensibles";
			break;
		case "EIA_ANALISIS_RIESGOS":
			titulo = "Análisis de riesgos";
			break;
		case "EIA_EVALUACION_IMPACTOS_SOCIAMBIENTALES":
			titulo = "Evaluación de impactos socioambientales";
			break;
		case "EIA_PLAN_MANEJO_AMBIENTAL_Y_SUB_PLANES":
			titulo = "Plan de manejo ambiental";
			break;
		case "EIA_CRONOGRAMA_VALORADO":
			titulo = "Cronograma Valorado del Plan de Manejo Ambiental";
			break;
		case "EIA_ANEXOS":
			titulo = "Anexos";
			break;
		case "EIA_ALCANCE_CICLO_VIDA":
			titulo = "Alcance, ciclo de vida y descripción detallada del proyecto";
			break;

		default:
			break;
		}
		return titulo;
	}
	
	public void activarIndice(String  indice){
		indexPma = -1;
		indexSubsanacion = -1;
		paginaActiva = indice;
		cargarDocumentosLista(new DocumentoEstudioImpacto());
		desabilitarMenupopup();
		if(paginaActiva.equals("EIA_ANEXOS")){
			for (Map.Entry<String, List<DocumentoEstudioImpacto>> entry : listadoDocumentosEIA.entrySet()) {
				if("EIA_ALCANCE_CICLO_VIDA".equals(entry.getKey()) && entry.getValue().size() == 0 ){
					pagCiclo=true;
				}
				if("EIA_ANALISIS_ALTERNATIVAS".equals(entry.getKey()) && entry.getValue().size() == 0 ){
					pagAnalisis= true;
				}
				if("EIA_DEMANDA_RECURSOS_NATURALES".equals(entry.getKey()) && entry.getValue().size() == 0 ){
					pagDemanda=true;
				}
				if("EIA_DIAGNOSTICO_AMBIENTAL".equals(entry.getKey()) && entry.getValue().size() == 0 ){
					pagDiagnostico=true;
				}
				if("EIA_INVENTARIO_FORESTAL".equals(entry.getKey()) && entry.getValue().size() == 0 ){
					pagInventario=true;
				}
				if("EIA_IDENTIFICACION_DETERMINACION_AREAS".equals(entry.getKey()) && entry.getValue().size() == 0 ){
					pagIdentificacion=true;
				}
				if("EIA_ANALISIS_RIESGOS".equals(entry.getKey()) && entry.getValue().size() == 0 ){
					pagRiesgos=true;
				}
				if("EIA_EVALUACION_IMPACTOS_SOCIAMBIENTALES".equals(entry.getKey()) && entry.getValue().size() == 0 ){
					pagImpacto=true;
				}
				if("EIA_PLAN_MANEJO_AMBIENTAL_Y_SUB_PLANES".equals(entry.getKey()) && !requiereIngresoPlan && entry.getValue().size() == 0 ){
					pagPlan=true;
				}
				if("EIA_PLAN_MANEJO_AMBIENTAL_Y_SUB_PLANES".equals(entry.getKey()) && requiereIngresoPlan && !validarDatosPma(false)){
					pagPlan=true;
				}
			}

			if((sector.getId().equals(TipoSector.TIPO_SECTOR_HIDROCARBUROS) || sector.getId().equals(TipoSector.TIPO_SECTOR_MINERIA) || sector.getId().equals(TipoSector.TIPO_SECTOR_ELECTRICO)) && (listaFasesSectorId == null || listaFasesSectorId.size() == 0 || listaFasesProyectoGuardadas.size() == 0)){
				pagCiclo=true;
			}
			if(pagAnalisis || pagDemanda || pagDiagnostico || pagInventario || pagIdentificacion || pagRiesgos || pagImpacto || pagCiclo || pagPlan){
	            RequestContext context = RequestContext.getCurrentInstance();
	            context.update(":formDialog:dlgPasoFaltante");
	            context.execute("PF('dlgPasoFaltante').show();");
			}
			mostrarSiguiente=false;
		}else{
			mostrarSiguiente=true;
		}
		if(!esRevisionEstudio)
			habilitarSiguiente = false;
	}
	
	public void siguiente(){
		switch (paginaActiva) {
		case "EIA_ALCANCE_CICLO_VIDA":
			paginaActiva = "EIA_ANALISIS_ALTERNATIVAS";
			break;
		case "EIA_ANALISIS_ALTERNATIVAS":
			paginaActiva = "EIA_DEMANDA_RECURSOS_NATURALES";
			break;
		case "EIA_DEMANDA_RECURSOS_NATURALES":
			paginaActiva = "EIA_DIAGNOSTICO_AMBIENTAL";
			break;
		case "EIA_DIAGNOSTICO_AMBIENTAL":
			paginaActiva = "EIA_INVENTARIO_FORESTAL";
			break;
		case "EIA_INVENTARIO_FORESTAL":
			paginaActiva = "EIA_IDENTIFICACION_DETERMINACION_AREAS";
			break;
		case "EIA_IDENTIFICACION_DETERMINACION_AREAS":
			paginaActiva = "EIA_ANALISIS_RIESGOS";
			break;
		case "EIA_ANALISIS_RIESGOS":
			paginaActiva = "EIA_EVALUACION_IMPACTOS_SOCIAMBIENTALES";
			break;
		case "EIA_EVALUACION_IMPACTOS_SOCIAMBIENTALES":
			paginaActiva = "EIA_PLAN_MANEJO_AMBIENTAL_Y_SUB_PLANES";
			break;
		case "EIA_PLAN_MANEJO_AMBIENTAL_Y_SUB_PLANES":
			paginaActiva = (requiereIngresoPlan) ? "EIA_CRONOGRAMA_VALORADO" : "EIA_ANEXOS";
			break;
		case "EIA_CRONOGRAMA_VALORADO":
			paginaActiva = "EIA_ANEXOS";
			break;
		}
		activarIndice(paginaActiva);
	}

	
	public void anterior(){
		switch (paginaActiva) {
		case "EIA_ALCANCE_CICLO_VIDA":
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			break;
		case "EIA_ANALISIS_ALTERNATIVAS":
			paginaActiva = "EIA_ALCANCE_CICLO_VIDA";
			break;
		case "EIA_DEMANDA_RECURSOS_NATURALES":
			paginaActiva = "EIA_ANALISIS_ALTERNATIVAS";
			break;
		case "EIA_DIAGNOSTICO_AMBIENTAL":
			paginaActiva = "EIA_DEMANDA_RECURSOS_NATURALES";
			break;
		case "EIA_INVENTARIO_FORESTAL":
			paginaActiva = "EIA_DIAGNOSTICO_AMBIENTAL";
			break;
		case "EIA_IDENTIFICACION_DETERMINACION_AREAS":
			paginaActiva = "EIA_INVENTARIO_FORESTAL";
			break;
		case "EIA_ANALISIS_RIESGOS":
			paginaActiva = "EIA_IDENTIFICACION_DETERMINACION_AREAS";
			break;
		case "EIA_EVALUACION_IMPACTOS_SOCIAMBIENTALES":
			paginaActiva = "EIA_ANALISIS_RIESGOS";
			break;
		case "EIA_PLAN_MANEJO_AMBIENTAL_Y_SUB_PLANES":
			paginaActiva = "EIA_EVALUACION_IMPACTOS_SOCIAMBIENTALES";
			break;
		case "EIA_CRONOGRAMA_VALORADO":
			paginaActiva = "EIA_PLAN_MANEJO_AMBIENTAL_Y_SUB_PLANES";
			break;
		case "EIA_ANEXOS":
			paginaActiva = (requiereIngresoPlan) ? "EIA_CRONOGRAMA_VALORADO" : "EIA_PLAN_MANEJO_AMBIENTAL_Y_SUB_PLANES";
			break;
		}
		activarIndice(paginaActiva);
	}
	public void cargarDocumentosLista(DocumentoEstudioImpacto objDocumento ){
		for (Map.Entry<String, List<DocumentoEstudioImpacto>> entry : listadoDocumentosEIA.entrySet()) {
			if(paginaActiva.equals(entry.getKey())){
				   listaDocumentosAdjuntos = entry.getValue();
				   cargarArchivoTmp(objDocumento, entry.getKey());
				   break;
			    }
			}
		
	}
	
	public void cargarArchivoTmp(DocumentoEstudioImpacto objDocumento, String key ){
		   if(objDocumento != null && objDocumento.getContenidoDocumento() != null){
			   listaDocumentosAdjuntos.add(objDocumento);
			   listadoDocumentosEIA.put(key, listaDocumentosAdjuntos);
		   }
	}
	
	 // Documento 
    public void asignarDocumentoEIA(FileUploadEvent file) {	
        String[] split=file.getFile().getContentType().split("/");
        String extension = "."+split[split.length-1];
        documentoAdjunto = new DocumentoEstudioImpacto();
        documentoAdjunto.setNombre(file.getFile().getFileName());
        documentoAdjunto.setMime(file.getFile().getContentType());
        documentoAdjunto.setContenidoDocumento(file.getFile().getContents());
        documentoAdjunto.setExtesion(extension);
        documentoAdjunto.setNombreTabla(DocumentoEstudioImpacto.class.getSimpleName());
    }
    public void agregarAdjunto(){
		try{
	        // Ingreso a alfresco de documento manifiesto recepcion
	        if (documentoAdjunto.getContenidoDocumento() != null) {
	        	cargarDocumentosLista(documentoAdjunto);
	        }
	        documentoAdjunto = new DocumentoEstudioImpacto();
		} catch (Exception e) {
			JsfUtil.addMessageError("Error al guardar los adjuntos del manifiesto transporte. Por favor comuníquese con Mesa de Ayuda");
		}
	}
    
    public void cargarFasesProyecto(){

		listaFasesProyecto = proyectoFasesCoaFacade.obtenerInformacionProyectoEIAPorProyecto(informacionProyectoEia);
		for (ProyectoFasesEiaCoa objFasesPro : listaFasesProyecto) {
			listaFasesProyectoGuardadas.add(objFasesPro.getFaseSector().getId().toString());
			listaFasesSectorId.add(objFasesPro.getFaseSector().getId().toString());
		}
    }
    
    public boolean validarDatos(){
		boolean valido = true;
		
		if(requiereIngresoPlan) {
			if(paginaActiva.equals("EIA_PLAN_MANEJO_AMBIENTAL_Y_SUB_PLANES")) {
				valido = validarDatosPma(true);
			} else if(paginaActiva.equals("EIA_CRONOGRAMA_VALORADO")) {
				valido = true;
			} else if(listaDocumentosAdjuntos == null ||  listaDocumentosAdjuntos.size() == 0){
				JsfUtil.addMessageError("Debe adjuntar por lo menos un documento.");
				valido = false;
			}
		} else if(listaDocumentosAdjuntos == null ||  listaDocumentosAdjuntos.size() == 0){
			JsfUtil.addMessageError("Debe adjuntar por lo menos un documento.");
			valido = false;
		}
		
		if(paginaActiva.equals("EIA_ALCANCE_CICLO_VIDA") && (sector.getId().equals(TipoSector.TIPO_SECTOR_HIDROCARBUROS) || sector.getId().equals(TipoSector.TIPO_SECTOR_MINERIA) || sector.getId().equals(TipoSector.TIPO_SECTOR_ELECTRICO))) {
			if(listaFasesSectorId == null || listaFasesSectorId.size() == 0 ){
				JsfUtil.addMessageError("El campo fases del proyecto es requerido.");
				valido = false;
			}
		}
		
		return valido;
	}
    
    public boolean validarDatosEnviar(){
		boolean valido = true;
		if(!requiereIngresoPlan && (listaDocumentosAdjuntos == null ||  listaDocumentosAdjuntos.size() == 0)){
			JsfUtil.addMessageError("Debe adjuntar por lo menos un documento.");
			valido = false;
		}
		if((sector.getId().equals(TipoSector.TIPO_SECTOR_HIDROCARBUROS) || sector.getId().equals(TipoSector.TIPO_SECTOR_MINERIA) || sector.getId().equals(TipoSector.TIPO_SECTOR_ELECTRICO)) && (listaFasesSectorId == null || listaFasesSectorId.size() == 0 || listaFasesProyectoGuardadas.size() == 0)){
			JsfUtil.addMessageError("El campo fases del proyecto es requerido.");
			valido = false;
		}
		return valido;
	}

	public void guardar(Boolean mostrarMensaje) throws Exception{
		Boolean guardarDetallePlan = false;
		
		validarDatos = false;
		TipoDocumentoSistema objTipoDocumento=null;
		if(validarDatos()){
			switch (paginaActiva) {
			case "EIA_ALCANCE_CICLO_VIDA":
				if(sector.getId().equals(TipoSector.TIPO_SECTOR_ELECTRICO)
						|| sector.getId().equals(TipoSector.TIPO_SECTOR_MINERIA)
						|| sector.getId().equals(TipoSector.TIPO_SECTOR_HIDROCARBUROS)){

					if(listaFasesSectorId.size() == 0){
						JsfUtil.addMessageError("El campo fases del proyecto es requerido.");
						return;
					}
				}
				objTipoDocumento = TipoDocumentoSistema.EIA_ALCANCE_CICLO_VIDA;
				// elimino las fases que estaban guardada y fueron eliminadas las fases seleccionadas
				proyectoFasesCoaFacade.eliminarFasesProyecto(listaFasesSectorId, listaFasesProyectoGuardadas, informacionProyectoEia.getId() );
				if(listaFasesSectorId.size() > 0){
					for (String objFaseId : listaFasesSectorId) {
						if(objFaseId != "0" && !listaFasesProyectoGuardadas.contains(objFaseId)){
							ProyectoFasesEiaCoa objFaseProyecto = new ProyectoFasesEiaCoa();
							CatalogoFasesCoa objFase = catalogoFasesCoaFacade.obtenerFasesPorId(Integer.valueOf(objFaseId));
							objFaseProyecto.setEstado(true);
							objFaseProyecto.setInformacionProyectoEia(informacionProyectoEia);
							objFaseProyecto.setFaseSector(objFase);
							proyectoFasesCoaFacade.guardar(objFaseProyecto);
						}
					}
					cargarFasesProyecto();
				}
				break;
			case "EIA_ANALISIS_ALTERNATIVAS":
				objTipoDocumento = TipoDocumentoSistema.EIA_ANALISIS_ALTERNATIVAS;
				break;
			case "EIA_DEMANDA_RECURSOS_NATURALES":
				objTipoDocumento = TipoDocumentoSistema.EIA_DEMANDA_RECURSOS_NATURALES;
				break;
			case "EIA_DIAGNOSTICO_AMBIENTAL":
				objTipoDocumento = TipoDocumentoSistema.EIA_DIAGNOSTICO_AMBIENTAL;
				break;
			case "EIA_INVENTARIO_FORESTAL":
				objTipoDocumento = TipoDocumentoSistema.EIA_INVENTARIO_FORESTAL;
				break;
			case "EIA_IDENTIFICACION_DETERMINACION_AREAS":
				objTipoDocumento = TipoDocumentoSistema.EIA_IDENTIFICACION_DETERMINACION_AREAS;
				break;
			case "EIA_ANALISIS_RIESGOS":
				objTipoDocumento = TipoDocumentoSistema.EIA_ANALISIS_RIESGOS;
				break;
			case "EIA_EVALUACION_IMPACTOS_SOCIAMBIENTALES":
				objTipoDocumento = TipoDocumentoSistema.EIA_EVALUACION_IMPACTOS_SOCIAMBIENTALES;
				break;
			case "EIA_PLAN_MANEJO_AMBIENTAL_Y_SUB_PLANES":
				objTipoDocumento = TipoDocumentoSistema.EIA_PLAN_MANEJO_AMBIENTAL_Y_SUB_PLANES;
				guardarDetallePlan = requiereIngresoPlan;
			break;
			case "EIA_ANEXOS":
				objTipoDocumento = TipoDocumentoSistema.EIA_ANEXOS;
				break;
			default:
				objTipoDocumento = TipoDocumentoSistema.EIA_ALCANCE_CICLO_VIDA;
				break;
			}
			
			if(!guardarDetallePlan) {
				for (DocumentoEstudioImpacto objDocumentoEIA : listaDocumentosAdjuntos) {
					if(objDocumentoEIA.getId() == null && objDocumentoEIA.getContenidoDocumento() != null){
						DocumentoEstudioImpacto documento = documentosFacade.ingresarDocumento(objDocumentoEIA, informacionProyectoEia.getId(), proyectoLicenciaCoa.getCodigoUnicoAmbiental(), objTipoDocumento, objDocumentoEIA.getNombre(), InformacionProyectoEia.class.getSimpleName(), bandejaTareasBean.getProcessId());
						int index = listaDocumentosAdjuntos.indexOf(objDocumentoEIA);
						listaDocumentosAdjuntos.set(index, documento);
					}
				}
				validarDatos = true;
				habilitarSiguiente = true;
				
				if(mostrarMensaje)
					JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			} else {
				guardarPlanManejo();
				
				validarDatos = true;
				habilitarSiguiente = true;
				indexPma = -1;
				indexSubsanacion = -1;
				RequestContext.getCurrentInstance().update(":#{p:component('acdPlan')}");
				
				if(mostrarMensaje)
					JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			}
		} else {
			if(paginaActiva.equals("EIA_PLAN_MANEJO_AMBIENTAL_Y_SUB_PLANES") && requiereIngresoPlan) {
				guardarPlanManejo();
			}
		}
	}
	
	public void guardarInformacion() throws Exception{
		Boolean guardarDetallePlan = false;
		
		validarDatos = false;
		TipoDocumentoSistema objTipoDocumento=null;
		// elimino las fases que estaban guardada y fueron eliminadas las fases seleccionadas
		proyectoFasesCoaFacade.eliminarFasesProyecto(listaFasesSectorId, listaFasesProyectoGuardadas, informacionProyectoEia.getId() );
		if(listaFasesSectorId.size() > 0){
			for (String objFaseId : listaFasesSectorId) {
				if(objFaseId != "0" && !listaFasesProyectoGuardadas.contains(objFaseId)){
					ProyectoFasesEiaCoa objFaseProyecto = new ProyectoFasesEiaCoa();
					CatalogoFasesCoa objFase = catalogoFasesCoaFacade.obtenerFasesPorId(Integer.valueOf(objFaseId));
					objFaseProyecto.setEstado(true);
					objFaseProyecto.setInformacionProyectoEia(informacionProyectoEia);
					objFaseProyecto.setFaseSector(objFase);
					proyectoFasesCoaFacade.guardar(objFaseProyecto);
				}
			}
			cargarFasesProyecto();
		}		
		for (Map.Entry<String, List<DocumentoEstudioImpacto>> entry : listadoDocumentosEIA.entrySet()) {
		   listaDocumentosAdjuntos = entry.getValue();
		   switch (entry.getKey()) {
				case "EIA_ALCANCE_CICLO_VIDA":
					objTipoDocumento = TipoDocumentoSistema.EIA_ALCANCE_CICLO_VIDA;
					break;
				case "EIA_ANALISIS_ALTERNATIVAS":
					objTipoDocumento = TipoDocumentoSistema.EIA_ANALISIS_ALTERNATIVAS;
					break;
				case "EIA_DEMANDA_RECURSOS_NATURALES":
					objTipoDocumento = TipoDocumentoSistema.EIA_DEMANDA_RECURSOS_NATURALES;
					break;
				case "EIA_DIAGNOSTICO_AMBIENTAL":
					objTipoDocumento = TipoDocumentoSistema.EIA_DIAGNOSTICO_AMBIENTAL;
					break;
				case "EIA_INVENTARIO_FORESTAL":
					objTipoDocumento = TipoDocumentoSistema.EIA_INVENTARIO_FORESTAL;
					break;
				case "EIA_IDENTIFICACION_DETERMINACION_AREAS":
					objTipoDocumento = TipoDocumentoSistema.EIA_IDENTIFICACION_DETERMINACION_AREAS;
					break;
				case "EIA_ANALISIS_RIESGOS":
					objTipoDocumento = TipoDocumentoSistema.EIA_ANALISIS_RIESGOS;
					break;
				case "EIA_EVALUACION_IMPACTOS_SOCIAMBIENTALES":
					objTipoDocumento = TipoDocumentoSistema.EIA_EVALUACION_IMPACTOS_SOCIAMBIENTALES;
					break;
				case "EIA_PLAN_MANEJO_AMBIENTAL_Y_SUB_PLANES":
					objTipoDocumento = TipoDocumentoSistema.EIA_PLAN_MANEJO_AMBIENTAL_Y_SUB_PLANES;
					guardarDetallePlan = requiereIngresoPlan;
				break;
				case "EIA_ANEXOS":
					objTipoDocumento = TipoDocumentoSistema.EIA_ANEXOS;
					break;
				default:
					objTipoDocumento = TipoDocumentoSistema.EIA_ALCANCE_CICLO_VIDA;
					break;
				}
		   
			   if(!guardarDetallePlan) {
				   for (DocumentoEstudioImpacto objDocumentoEIA : listaDocumentosAdjuntos) {
						if(objDocumentoEIA.getId() == null && objDocumentoEIA.getContenidoDocumento() != null){
							documentosFacade.ingresarDocumento(objDocumentoEIA, informacionProyectoEia.getId(), proyectoLicenciaCoa.getCodigoUnicoAmbiental(), objTipoDocumento, objDocumentoEIA.getNombre(), InformacionProyectoEia.class.getSimpleName(), bandejaTareasBean.getProcessId());
						}
					}
			   } else {
					guardarPlanManejo();
			   }
				
			}
			validarDatos = true;
			habilitarSiguiente = true;
	}
	
	public void completarTarea(){
		try {
			if(validarDatosEnviar()){
				guardar(false);
				//guardo la informacion que no se guardo en cada seccion
				guardarInformacion();
				if(validarDatos){
					informacionProyectoEia.setFechaEnvioEstudio(new Date());;
					informacionProyectoEIACoaFacade.guardar(informacionProyectoEia);
					
					Boolean existeNormativaPago = Constantes.getPropertyAsBoolean("rcoa.esia.existe.normativa.pago");
					
					Map<String, Object> parametros = new HashMap<>();
					parametros.put("existeNormativaPago", existeNormativaPago);
					parametros.put("totalRevisiones", 4);
					
					procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
					
					procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(),bandejaTareasBean.getTarea().getTaskId(),bandejaTareasBean.getTarea().getProcessInstanceId(), null);
					JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
					JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
				}
			}
		} catch (JbpmException e) {					
			e.printStackTrace();
		}catch (Exception e) {					
			e.printStackTrace();
		}
	}
	public void eliminarAdjunto(DocumentoEstudioImpacto objDocumentoEIA ){
		if(objDocumentoEIA != null ){
			listaDocumentosAdjuntos.remove(objDocumentoEIA);
			if(objDocumentoEIA.getId() != null){
				objDocumentoEIA.setEstado(false);
				documentosFacade.guardar(objDocumentoEIA);
			}
		}
	}
	
	public void uploadDocumento(FileUploadEvent file) {	
        String[] split=file.getFile().getContentType().split("/");
        String extension = "."+split[split.length-1];
        documentoJustificacionProrroga = new DocumentoEstudioImpacto();
        documentoJustificacionProrroga.setNombre(file.getFile().getFileName());
        documentoJustificacionProrroga.setMime(file.getFile().getContentType());
        documentoJustificacionProrroga.setContenidoDocumento(file.getFile().getContents());
        documentoJustificacionProrroga.setExtesion(extension);
        documentoJustificacionProrroga.setNombreTabla("JustificacionProrroga");
        documentoJustificacionProrroga.setIdTable(informacionProyectoEia.getId());
        documentoJustificacionProrroga.setIdProceso(bandejaTareasBean.getProcessId());
    }
	
	public void cargarDatosModificacion() {
		try{
			numeroRevision = 0;
			generarNUT = true;

			String revision = (String) variables.get("numeroRevision");
			if(revision != null && !esRevisionEstudio) {
				numeroRevision = Integer.parseInt(revision);
				
				aclaracionProrrogaEsIA = aclaracionProrrogaEsIAFacade.getPorEstudio(informacionProyectoEia.getId());
				
				if(aclaracionProrrogaEsIA == null) {
					aclaracionProrrogaEsIA = new AclaracionProrrogaEsIA();
					aclaracionProrrogaEsIA.setIdEstudio(informacionProyectoEia.getId());
				}
				
				if(aclaracionProrrogaEsIA.getRequiereProrroga() != null && aclaracionProrrogaEsIA.getRequiereProrroga())
					tieneProrroga = true;
				
				recuperarDiasFaltantes();
				
				List<DocumentoEstudioImpacto> documentos = documentosFacade.documentoXTablaIdXIdDocLista(informacionProyectoEia.getId(),
						"JustificacionProrroga", TipoDocumentoSistema.EIA_JUSTIFICACION_PRORROGA);
				if(documentos.size() > 0) {
					documentoJustificacionProrroga = documentos.get(0);
				}
				
				mostrarResumenObservaciones = true;
				
				maxDiasProrroga = 30;
				
				documentosNUT = new ArrayList<>();
				List<NumeroUnicoTransaccional> listNUTXTramite = numeroUnicoTransaccionalFacade.listNUTActivoPorTramite(proyectoLicenciaCoa.getCodigoUnicoAmbiental());
				if (listNUTXTramite != null && listNUTXTramite.size() > 0) {
					for (NumeroUnicoTransaccional nut : listNUTXTramite) {
						List<DocumentoEstudioImpacto> comprobantes = documentosEstudioFacade
								.documentoXTablaIdXIdDocLista(
										nut.getSolicitudUsuario().getId(),
										"NUT RECAUDACIONES",
										TipoDocumentoSistema.RECAUDACIONES_NUT_PAGOS);

						if (comprobantes.size() > 0) {
							documentosNUT.addAll(comprobantes);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Error al cargar datos");
		}
	}
	
	public void recuperarDiasFaltantes() throws Exception {
		diasRestantesModificacion = 0;
		
		datosProrrogaModificacion = prorrogaModificacionEstudioFacade.getPorEstudio(informacionProyectoEia.getId());
		if(datosProrrogaModificacion != null) {
			Date fechaInicio = new Date();
			Date fechaFin = datosProrrogaModificacion.getFechaFinModificacion();
			if(datosProrrogaModificacion.getFechaFinModificacionProrroga() != null)
				fechaFin = datosProrrogaModificacion.getFechaFinModificacionProrroga();
			
			Integer diasFeriados = feriadosFacade.getDiasFeriadosNacionalesPorRangoFechas(fechaInicio, fechaFin);
			
			diasRestantesModificacion = getDiasLaborables(fechaInicio, fechaFin, diasFeriados);
			
			diasPorRevision = datosProrrogaModificacion.getNumeroDiasPorRevision();
		}
	}
	
	public Integer getDiasLaborables(Date fechaInicioC, Date fechaFinC, Integer diasFeriados) throws ParseException {
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaInicio = formatter.parse(formatter.format(fechaInicioC));
		Date fechaFin = formatter.parse(formatter.format(fechaFinC));
		
		Calendar fechaInicial = Calendar.getInstance();
		fechaInicial.setTime(fechaInicio);
		
		Calendar fechaFinal = Calendar.getInstance();
		fechaFinal.setTime(fechaFin);
		
		Integer diffDays = 0;
		// mientras la fecha inicial sea menor o igual que la fecha final contar dias
		while (fechaInicial.before(fechaFinal) || fechaInicial.equals(fechaFinal)) {
			if (fechaInicial.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY
					 && fechaInicial.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
				// si no es sabado o domingo
				diffDays++;
			}
			//incremento la fecha inicial
			fechaInicial.add(Calendar.DATE, 1);
		}
		
		if(diasFeriados > 0)
			diffDays = diffDays - diasFeriados;
			
		return diffDays;
	}
	
	public void calcularDiasProrroga() throws Exception {
		if(aclaracionProrrogaEsIA.getDiasProrroga() != null && aclaracionProrrogaEsIA.getDiasProrroga() > 0) {
			Date fechaFinMod = datosProrrogaModificacion.getFechaFinModificacion();
			Date fechaInicioProrroga = diasHabilesUtil.recuperarFechaHabil(fechaFinMod, 1, true);//se agrega 1 dia porque la prórroga inicia mañana o el siguiente dia habil
			Date fechaFinProrroga = diasHabilesUtil.recuperarFechaHabil(fechaFinMod, aclaracionProrrogaEsIA.getDiasProrroga(), false);
			
			datosProrrogaModificacion.setFechaSolicitudProrroga(fechaInicioProrroga);
			datosProrrogaModificacion.setNumeroDiasProrroga(aclaracionProrrogaEsIA.getDiasProrroga());
			datosProrrogaModificacion.setFechaFinModificacionProrroga(fechaFinProrroga);
		}
	}
	
	public void enviarSolicitudReunion() {
		try {
			
			if(aclaracionProrrogaEsIA.getRequiereReunion() == null) {
				JsfUtil.addMessageError("El campo '¿Desea mantener una reunión aclaratoria?' es requerido.");
				return;
			}
			
			if(aclaracionProrrogaEsIA.getRequiereReunion()) {
				String usuario = (String) variables.get("tecnicoResponsable");
				Usuario tecnicoResponsable = usuarioFacade.buscarUsuario(usuario);
				
				Map<String, Object> parametros = new HashMap<>();
				
				if(tieneProrroga == null || !tieneProrroga) {
					aclaracionProrrogaEsIA.setRequiereProrroga(null);
					aclaracionProrrogaEsIA.setDiasProrroga(null);
				}
				
				aclaracionProrrogaEsIAFacade.guardar(aclaracionProrrogaEsIA);
	
				parametros.put("finalizoModificacionEstudio", false);
				parametros.put("deseaProrroga", !aclaracionProrrogaEsIA.getRequiereReunion());
				
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
	
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
				
				// enviar notificacion tecnico
				Object[] parametrosCorreoTecnicos = new Object[] {tecnicoResponsable.getPersona().getNombre(),
						proyectoLicenciaCoa.getNombreProyecto(), proyectoLicenciaCoa.getCodigoUnicoAmbiental() };
				String notificacion = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
								"bodyNotificacionSolicitudReunion", parametrosCorreoTecnicos);
	
				Email.sendEmail(tecnicoResponsable, "Regularización Ambiental Nacional", notificacion, tramite, loginBean.getUsuario());
			} else {
				if(tieneProrroga == null || !tieneProrroga) {
					aclaracionProrrogaEsIA.setRequiereProrroga(null);
					aclaracionProrrogaEsIA.setDiasProrroga(null);
				}
				
				aclaracionProrrogaEsIAFacade.guardar(aclaracionProrrogaEsIA);
			}

			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);

		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}
	}
	
	public void enviarSolicitudProrroga() {
		try {
			
			if(aclaracionProrrogaEsIA.getRequiereProrroga() == null) {
				JsfUtil.addMessageError("El campo '¿Desea obtener una prórroga?' es requerido.");
				return;
			}
			
			if(aclaracionProrrogaEsIA.getRequiereProrroga()) {
				if(documentoJustificacionProrroga == null) {
					JsfUtil.addMessageError("El campo 'Adjuntar documento de justificación' es requerido.");
					return;
				}
				
				Map<String, Object> parametros = new HashMap<>();
				
				if(documentoJustificacionProrroga.getId() == null && documentoJustificacionProrroga.getContenidoDocumento() != null){
					documentoJustificacionProrroga = documentosFacade.guardarDocumentoAlfrescoCA(proyectoLicenciaCoa.getCodigoUnicoAmbiental(), 
							"ESTUDIO_AMBIENTAL", documentoJustificacionProrroga, TipoDocumentoSistema.EIA_JUSTIFICACION_PRORROGA);
					if(documentoJustificacionProrroga.getId() == null) {
						JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
						return;
					}
				}
				
				calcularDiasProrroga();
				
				aclaracionProrrogaEsIAFacade.guardar(aclaracionProrrogaEsIA);
				prorrogaModificacionEstudioFacade.guardar(datosProrrogaModificacion);
	
				parametros.put("finalizoModificacionEstudio", false);
				parametros.put("deseaProrroga", true);
				
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getProcessInstanceId(), parametros);
	
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);
			} else 
				aclaracionProrrogaEsIAFacade.guardar(aclaracionProrrogaEsIA);

			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);

		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}
	}
	
	public void enviarModificacion() {
		try {
			//guardo la informacion que no se guardo en cada seccion
			
			if(numeroRevision == 1){				
				informacionProyectoEia.setFechaSegundoEnvio(new Date());
			}else if(numeroRevision == 2){
				informacionProyectoEia.setFechaTercerEnvio(new Date());
			}
			informacionProyectoEIACoaFacade.guardar(informacionProyectoEia);
			
			guardarInformacion();
			
			//MarielaGuano Actualización CI validar si tiene una solicitud de actualizacion de CI aprobada
			if(proyectoLicenciaCoa.getEstadoActualizacionCertInterseccion().equals(2)) {
				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('dlgInformativo').show();");
				habilitarSiguiente = false;
				return;
			}
			
			Integer numeroRevisionFinal = numeroRevision + 1;
			if(numeroRevisionFinal >= 4) {
				numeroRevisionFinal = 3;
			}
			
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("finalizoModificacionEstudio", true);
			parametros.put("emitioRespuesta", true);
			parametros.put("numeroRevision", numeroRevisionFinal);
			parametros.put("esPrimeraRevision", false);
			parametros.put("totalRevisiones", 4); //se actualiza variable para solventar problemas en back
			
			Map<String, Object> infoInterseccion = recuperarInfoInterseccion();
			if(infoInterseccion.size() > 0)
				parametros.putAll(infoInterseccion);
			
			if(JsfUtil.getLoggedUser() == null || bandejaTareasBean.getTarea()  == null) {
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
				return;
			}
			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);
			
			List<TaskSummary> listaTareas=procesoFacade.getTaskReservedInProgress(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			if(listaTareas != null && listaTareas.size() > 0) {				
				for (TaskSummary tarea : listaTareas) {
					String nombretarea = bandejaFacade.getNombreTarea(tarea.getId());
					if(nombretarea.equals("registrarFechaReunionAclaratoria")) {
						String usuario = (String) variables.get("tecnicoResponsable");
						Usuario tecnicoResponsable = usuarioFacade.buscarUsuario(usuario);
						
						procesoFacade.aprobarTarea(tecnicoResponsable, tarea.getId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);
						
						List<TaskSummary> listaTareasActas = procesoFacade.getTaskReservedInProgress(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
						if(listaTareasActas != null && listaTareas.size() > 0) {				
							for (TaskSummary item : listaTareasActas) {
								String nuevoNombreTarea = bandejaFacade.getNombreTarea(item.getId());
								if(nuevoNombreTarea.equals("subirActaReunion")) {									
									procesoFacade.aprobarTarea(tecnicoResponsable, item.getId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);
									break;
								}
							}
						}
						
						break;
					} else if(nombretarea.equals("subirActaReunion")) {	
						String usuario = (String) variables.get("tecnicoResponsable");
						Usuario tecnicoResponsable = usuarioFacade.buscarUsuario(usuario);
						
						procesoFacade.aprobarTarea(tecnicoResponsable, tarea.getId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);
						break;
					}
				}
			}
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Map<String, Object> recuperarInfoInterseccion() {
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		Boolean requierePronunciamiento = false;
		
		InformeTecnicoEsIA informeTecnico = informeTecnicoEsIAFacade.obtenerPorEstudioTipoInforme(informacionProyectoEia, InformeTecnicoEsIA.snap);
		if(informeTecnico != null && informeTecnico.getTipoPronunciamiento().equals(2)){ //si es observado se dispara la tarea
			parametros.put("requierePronunciamientoSnap", true);
			requierePronunciamiento = true;
		} else
			parametros.put("requierePronunciamientoSnap", false);

		InformeTecnicoEsIA informeTecnicoForestal = informeTecnicoEsIAFacade.obtenerPorEstudioTipoInforme(informacionProyectoEia, InformeTecnicoEsIA.forestal);
		if(informeTecnicoForestal != null && informeTecnicoForestal.getTipoPronunciamiento().equals(2)){ //si es observado se dispara la tarea
			parametros.put("requierePronunciamientoForestal", true);
			requierePronunciamiento = true;
		} else
			parametros.put("requierePronunciamientoForestal", false);

		InformeTecnicoEsIA informeTecnicoInventario = informeTecnicoEsIAFacade.obtenerPorEstudioTipoInforme(informacionProyectoEia, InformeTecnicoEsIA.forestalInventario);
		if(informeTecnicoInventario != null && informeTecnicoInventario.getTipoPronunciamiento().equals(2)){ //si es observado se dispara la tarea
			parametros.put("requierePronunciamientoInventario", true);
			requierePronunciamiento = true;
		} else
			parametros.put("requierePronunciamientoInventario", false);
		
		parametros.put("requierePronunciamientoPatrimonio", requierePronunciamiento);
		
		return parametros;
	}

	public List<ObservacionesEsIA> getObservacionesTecnicoSeccion() {
		String seccion = "";
		switch (paginaActiva) {
		case "EIA_ALCANCE_CICLO_VIDA":
			seccion = "DescripcionProyectoEIA";
			break;
		case "EIA_ANALISIS_ALTERNATIVAS":
			seccion = "AnalisisAlternativasProyectoEIA";
			break;
		case "EIA_DEMANDA_RECURSOS_NATURALES":
			seccion = "DemandaRecursosProyectoEIA";
			break;
		case "EIA_DIAGNOSTICO_AMBIENTAL":
			seccion = "DiagnosticoAmbientalProyectoEIA";
			break;
		case "EIA_INVENTARIO_FORESTAL":
			seccion = "InventarioForestalProyectoEIA";
			break;
		case "EIA_IDENTIFICACION_DETERMINACION_AREAS":
			seccion = "AreasInfluenciaProyectoEIA";
			break;
		case "EIA_ANALISIS_RIESGOS":
			seccion = "AnalisisRiesgosProyectoEIA";
			break;
		case "EIA_EVALUACION_IMPACTOS_SOCIAMBIENTALES":
			seccion = "EvaluacionImpactosProyectoEIA";
			break;
		case "EIA_PLAN_MANEJO_AMBIENTAL_Y_SUB_PLANES":
			seccion = "PlanManejoAmbientalProyectoEIA";
			break;
		case "EIA_ANEXOS":
			seccion = "AnexosProyectoEIA";
			break;
		}
		
		List<ObservacionesEsIA> observacionesSeccion = new ArrayList<>();
		try {
			List<ObservacionesEsIA> observaciones = observacionesEsIAFacade.listarPorIdClaseSeccionNoCorregidas(informacionProyectoEia.getId(),seccion);
			if (observaciones != null)
				observacionesSeccion.addAll(observaciones);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return observacionesSeccion;
	}
	
	public void generarNut() throws Exception {

		List<NumeroUnicoTransaccional> listNUTXTramite = new ArrayList<NumeroUnicoTransaccional>();

		String codigoTramite = tramite;

		listNUTXTramite = numeroUnicoTransaccionalFacade.listNUTActivoPorTramite(codigoTramite);
		if (listNUTXTramite != null && listNUTXTramite.size() > 0) {
			for (NumeroUnicoTransaccional nut : listNUTXTramite) {
				if (nut.getEstadosNut().getId() == 5) {
					nut.setNutFechaActivacion(new Date());
					nut.setNutFechaDesactivacion(JsfUtil.sumarDiasAFecha(
							new Date(), 3));
					nut.setEstadosNut(new EstadosNut(2));
					numeroUnicoTransaccionalFacade.guardarNUT(nut);
					JsfUtil.addMessageWarning("Su trámite está caducado, se activo nuevamente por 3 días, por favor revisar.");
				}
			}
			JsfUtil.addMessageWarning("Usted ya tiene generado un Número Único de Trámite, por favor descargue en documentos del trámite.");
			return;
		}

		Tarifas tarifa = tarifasFacade.buscarTarifasPorCodigo(CodigoTasa.REVISION_ESTUDIO_IMPACTO_AMBIENTAL.getDescripcion());
		if (tarifa == null) {
			JsfUtil.addMessageError("Ocurrió un error al generar el NUT. Por favor comunicarse con mesa de ayuda.");
			return;
		}

		SolicitudUsuario solicitudUsuario= new SolicitudUsuario();
		solicitudUsuario.setUsuario(loginBean.getUsuario());
		solicitudUsuario.setSolicitudDescripcion("Pagos por el trámite: "+codigoTramite);
		solicitudUsuario.setSolicitudCodigo(JsfUtil.getBean(GenerarNUTController.class).generarCodigoSolicitud());
		solicitudTramiteFacade.guardarSolicitudUsuario(solicitudUsuario);
		
		Integer numeroDocumento=1;
		
		NumeroUnicoTransaccional numeroUnicoTransaccional;
		numeroUnicoTransaccional = new NumeroUnicoTransaccional();
		numeroUnicoTransaccional.setNutCodigo(secuenciasFacade.getNextValueDedicateSequence("NUT_CODIGO", 10));
		numeroUnicoTransaccional.setNutFechaActivacion(new Date());
		numeroUnicoTransaccional.setEstadosNut(new EstadosNut(2));
		numeroUnicoTransaccional.setNutFechaDesactivacion(JsfUtil.sumarDiasAFecha(new Date(), 3));
		numeroUnicoTransaccional.setSolicitudUsuario(solicitudUsuario);
		numeroUnicoTransaccional.setCuentas(new Cuentas(1));
		numeroUnicoTransaccional.setNutValor(informacionProyectoEia.getValorAPagar());
		numeroUnicoTransaccional.setNutCodigoProyecto(codigoTramite);
		numeroUnicoTransaccionalFacade.guardarNUT(numeroUnicoTransaccional);
		
		TarifasNUT tarifasNUT= new TarifasNUT();
		tarifasNUT.setNumeroUnicoTransaccional(numeroUnicoTransaccional);
		tarifasNUT.setTarifas(tarifa);
		tarifasNUT.setCantidad(1);
		tarifasNUT.setValorUnitario(informacionProyectoEia.getValorAPagar());
		tarifasNUTFacade.guardarTarifasNUT(tarifasNUT);
		
		JsfUtil.getBean(GenerarNUTController.class).setSolicitudUsuario(solicitudUsuario);
		byte[] contenidoDocumento = JsfUtil.getBean(GenerarNUTController.class).generarDocumentoNutRcoa(numeroUnicoTransaccional, solicitudUsuario, numeroDocumento);
		
		DocumentoEstudioImpacto documentoPago = new DocumentoEstudioImpacto();
		documentoPago.setContenidoDocumento(contenidoDocumento);
		documentoPago.setMime("application/pdf");
		documentoPago.setIdTable(solicitudUsuario.getId());
		documentoPago.setNombreTabla("NUT RECAUDACIONES");
		documentoPago.setNombre("ComprobantePago" + numeroDocumento + ".pdf");
		documentoPago.setExtesion(".pdf");
		documentoPago = documentosEstudioFacade
				.guardarDocumentoAlfrescoSinProyecto(
						numeroUnicoTransaccional.getNutCodigoProyecto() != null ? numeroUnicoTransaccional
								.getNutCodigoProyecto() : solicitudUsuario
								.getSolicitudCodigo(), "RECAUDACIONES", null,
						documentoPago,
						TipoDocumentoSistema.RECAUDACIONES_NUT_PAGOS);
		
		documentosNUT = new ArrayList<>();
		documentosNUT.add(documentoPago);
		
		List<String>listaArchivos= new ArrayList<String>();
		listaArchivos=JsfUtil.getBean(GenerarNUTController.class).getListPathArchivos();
		JsfUtil.getBean(GenerarNUTController.class).enviarNotificacionPago(solicitudUsuario.getUsuario(),codigoTramite,listaArchivos);
		JsfUtil.addMessageInfo("Se ha enviado un correo electrónico con los comprobantes para realizar el pago.");

	}
	
	public StreamedContent descargar(DocumentoEstudioImpacto documento) {
		DefaultStreamedContent content = null;
		try {
			if (documento.getAlfrescoId() != null) {
				byte[] documentoContent = documentosEstudioFacade
						.descargar(documento.getAlfrescoId());

				content = new DefaultStreamedContent(new ByteArrayInputStream(
						documentoContent));
				content.setName(documento.getNombre());
			} else if(documento.getContenidoDocumento() != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documento.getContenidoDocumento()));
				content.setName(documento.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	
	public StreamedContent descargarJustificacionProrroga() {
		DocumentoEstudioImpacto documento = documentoJustificacionProrroga;
		DefaultStreamedContent content = null;
		try {
			if (documento.getAlfrescoId() != null) {
				byte[] documentoContent = documentosEstudioFacade
						.descargar(documento.getAlfrescoId());

				content = new DefaultStreamedContent(new ByteArrayInputStream(
						documentoContent));
				content.setName(documento.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}

	public void regresar() {
		try {
			String url = JsfUtil.getCurrentTask().getTaskSummary().getDescription();
			 JsfUtil.redirectTo(url);
		} catch (Exception e) {
		}
	}
	
	public StreamedContent descargarOficio(){
		DefaultStreamedContent content = null;
		try {
			DocumentoEstudioImpacto documento = new DocumentoEstudioImpacto();
			InformeTecnicoEsIA informeTecnico = informeTecnicoEsIAFacade.obtenerPorEstudioTipoInforme(informacionProyectoEia, InformeTecnicoEsIA.consolidado);
			OficioPronunciamientoEsIA oficioPronunciamiento = oficioPronunciamientoEsIAFacade.getPorEstudioInforme(informacionProyectoEia.getId(), informeTecnico.getId());
			
			List<DocumentoEstudioImpacto> listaDocumentosInt = documentosFacade.documentoXTablaIdXIdDocLista(oficioPronunciamiento.getId(), OficioPronunciamientoEsIA.class.getSimpleName(), TipoDocumentoSistema.EIA_OFICIO_APROBACION_CONSOLIDADO);
			if (listaDocumentosInt.size() > 0) 
				documento = listaDocumentosInt.get(0);
			
			if (documento.getAlfrescoId() != null) {
				byte[] documentoContent = documentosEstudioFacade
						.descargar(documento.getAlfrescoId());

				content = new DefaultStreamedContent(new ByteArrayInputStream(
						documentoContent), documento.getMime());
				content.setName(documento.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public StreamedContent descargarInformeTecnicoConsolidado(){
		DefaultStreamedContent content = null;
		try {
			DocumentoEstudioImpacto documento = new DocumentoEstudioImpacto();
			InformeTecnicoEsIA informeTecnico = informeTecnicoEsIAFacade.obtenerPorEstudioTipoInforme(informacionProyectoEia, InformeTecnicoEsIA.consolidado);
			
			List<DocumentoEstudioImpacto> listaDocumentosInt = documentosFacade.documentoXTablaIdXIdDocLista(informeTecnico.getId(), InformeTecnicoEsIA.class.getSimpleName(), TipoDocumentoSistema.EIA_INFORME_TECNICO_RCOA);
			if (listaDocumentosInt.size() > 0) 
				documento = listaDocumentosInt.get(0);
			
			if (documento.getAlfrescoId() != null) {
				byte[] documentoContent = documentosEstudioFacade
						.descargar(documento.getAlfrescoId());

				content = new DefaultStreamedContent(new ByteArrayInputStream(
						documentoContent), documento.getMime());
				content.setName(documento.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}

	//INICIO AUTOMATIZACION PLAN DE MANEJO
	public StreamedContent getPlantillaPlan() throws Exception {
		DefaultStreamedContent content = null;
		try {
    		byte[] plantillaPmi = documentosFacade.descargarDocumentoPorNombre("Anexo01_EsIA_Plantilla plan manejo ambiental.xlsx");
        
            if (plantillaPmi != null) {
                content = new DefaultStreamedContent(new ByteArrayInputStream(plantillaPmi));
                content.setName("Plantilla plan manejo ambiental.xlsx");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }
	
	public void getInfoSubplanes () {
		try {

			listaDocumentoPlantillaEliminar = new ArrayList<>();
			listaDocumentoPlantillaEliminarAux = new ArrayList<>();
			listaProgramasEliminar = new ArrayList<>();
			listaProgramasEliminarAux = new ArrayList<>();
			
			listaPlanManejo = planManejoAmbientalEsIAFacade.obtenerSubplanes(informacionProyectoEia.getId(), habilitadoIngreso);
			
			calcularCostoPmi();
			
			if(esRevisionEstudio) {
				if(mostrarObservaciones) {
					for (EntityPmaEsIA item : listaPlanManejo) {
						item.setListaObservacionesSubPlan(getObservacionesPma(false, item.getSubplan().getCodigo(), false, true));
						
						PlanManejoEsIAObservaciones planObservacion = planManejoEsIAObservacionesFacade.getPorSubPlanNombreObservacion(item.getPlanManejo().getId(), nombreClaseObservacionesPma);
						if(item.getListaObservacionesSubPlan().size() > 0 && planObservacion.getId() == null) {
							planObservacion.setTieneObservaciones(true);
						}
						item.getPlanManejo().setPlanManejoObservacion(planObservacion);
					}
				}
			} else {
				if(numeroRevision > 0) {					
					String planesObservados = "<ul>";
					for (EntityPmaEsIA item : listaPlanManejo) {
						if(item.getPlanManejo().getTieneObservaciones() != null 
								&& item.getPlanManejo().getTieneObservaciones()) {
							item.setListaObservacionesSubPlan(getObservacionesPma(true, item.getSubplan().getCodigo(), false, false));
							
							List<ObservacionesEsIA> observacionesPendientes = getObservacionesPma(true, item.getSubplan().getCodigo(), true, false);
							if(observacionesPendientes != null && observacionesPendientes.size() > 0) {
								planesObservados += "<li>" + item.getSubplan().getNombre() + "</li>";
							}
						}
					}
					planesObservados += "</ul>";
					
					Object[] params = new Object[] {planesObservados};
					mensajeSubPlanesObservados = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
									"mensajeEsIASubsanarObservacionesPma", params);
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void calcularCostoPmi() {
		costoTotalPma = 0.0;
		
		for (EntityPmaEsIA item : listaPlanManejo) {
			List<EntityPmaEsIA> listaSubsanaciones = item.getPlanManejo().getListaSubsanacionesSubPlan();			
			if(listaSubsanaciones != null && listaSubsanaciones.size() > 0) {
				EntityPmaEsIA itemSub = listaSubsanaciones.get(listaSubsanaciones.size() - 1);
				item.setPlanManejoCronograma(itemSub.getPlanManejo());
				if(itemSub.getPlanManejo() != null && itemSub.getPlanManejo().getCostoSubplan() != null) {
					costoTotalPma = costoTotalPma + itemSub.getPlanManejo().getCostoSubplan();
				} 
			} else {
				item.setPlanManejoCronograma(item.getPlanManejo());
				if(item.getPlanManejo() != null && item.getPlanManejo().getCostoSubplan() != null) {
					costoTotalPma = costoTotalPma + item.getPlanManejo().getCostoSubplan();
				} 
			}
		}
	}
	
	public void setIndiceTabActivo(EntityPmaEsIA plan) {
		int index = -1;
		for (EntityPmaEsIA pma : listaPlanManejo) {
			index += 1;
			if(pma.getSubplan().getId().equals(plan.getSubplan().getId())) {
				break;
			}
		}
		indexPma = index;
	}
	
	public void seleccionarIngresoSubplan(EntityPmaEsIA plan) {
		setIndiceTabActivo(plan);
	}
	
	public void setIndiceTabActivoSubsanacion(EntityPmaEsIA plan) {
		int index = -1;
		if(numeroRevision > 0) {
			for (EntityPmaEsIA pma : listaPlanManejo) {
				if(pma.getSubplan().getId().equals(plan.getSubplan().getId())) {
					if(pma.getPlanManejo().getListaSubsanacionesSubPlan() != null) {
						for (EntityPmaEsIA item : pma.getPlanManejo().getListaSubsanacionesSubPlan()) {
							index += 1;
							if(item.getPlanManejo().equals(plan.getPlanManejo())) {
								break;
							}
						}
					}
					break;
				}
			}
		}
		
		indexSubsanacion = index;
	}
	
	public void handleFileUploadPlan(final FileUploadEvent event) throws IOException {
		
		if(listaProgramasSubPlan != null) {
			listaProgramasEliminarAux.addAll(listaProgramasSubPlan);
			
			if(documentoPlantillaPma != null) {
				listaDocumentoPlantillaEliminarAux.add(documentoPlantillaPma);
			}
		}

    	listaProgramasSubPlan = new ArrayList<>();
    	documentoPlantillaPma = null;
    	costoPrograma = 0.0;
    	mensajeFilaIncompleta = "";
    	
    	int rows = 0;
    	String codigoDetalle = null;
    	
    	List<String> nombreColumnas = new ArrayList<>();
    	ProgramaPlanManejoEsIA nuevoPrograma = new ProgramaPlanManejoEsIA();
    	List<DetallePlanManejoEsIA> listaDetallePmaPrograma = new ArrayList<>();
    	
    	XSSFWorkbook libroPma = null;
    	try {
    		UploadedFile uploadedFile = event.getFile();
			libroPma = new XSSFWorkbook(uploadedFile.getInputstream());
    		Sheet sheet = libroPma.getSheetAt(0);
    		Iterator<Row> rowIterator = sheet.iterator();
    		
    		while (rowIterator.hasNext()) {
    			Row row = rowIterator.next();                
    			boolean isEmptyRow = true;
    			for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
    				Cell cell = row.getCell(cellNum);
    				if (cell != null && cell.getCellTypeEnum() != CellType.BLANK && StringUtils.isNotBlank(cell.toString())) {
    					isEmptyRow = false;
    				}
    			}
    			if (isEmptyRow)
    				break;
    			
    			if(rows == 0) {
    				for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
        				Cell cell = row.getCell(cellNum);
        				nombreColumnas.add(cell.toString());
        			}
    			}
    			
    			if (rows > 0) {                    
    				DetallePlanManejoEsIA detalle = mapearData(row, nombreColumnas, rows);
    				
    				if(codigoDetalle == null) {
    					codigoDetalle = detalle.getCodigo();
    					
    					nuevoPrograma = new ProgramaPlanManejoEsIA();
    					nuevoPrograma.setNombre(detalle.getNombrePrograma());
    					
    					listaDetallePmaPrograma = new ArrayList<>();
    				}
    				
    				if(codigoDetalle.equals(detalle.getCodigo())) {
    					costoPrograma = costoPrograma + detalle.getCosto();
    					listaDetallePmaPrograma.add(detalle);
    				} else {
    					nuevoPrograma.setListaDetallePlan(listaDetallePmaPrograma);
    					nuevoPrograma.setCostoPrograma(costoPrograma);
    					listaProgramasSubPlan.add(nuevoPrograma);
    					
    					codigoDetalle = detalle.getCodigo();
    					
    					costoPrograma = 0.0;
    					nuevoPrograma = new ProgramaPlanManejoEsIA();
    					listaDetallePmaPrograma = new ArrayList<>();
    					
    					costoPrograma = costoPrograma + detalle.getCosto();
    					nuevoPrograma.setNombre(detalle.getNombrePrograma());
    					listaDetallePmaPrograma.add(detalle);
    				}
    			}
    			rows++;
    		}
    		
    		if(listaDetallePmaPrograma.size() > 0) {
    			nuevoPrograma.setListaDetallePlan(listaDetallePmaPrograma);
    			nuevoPrograma.setCostoPrograma(costoPrograma);
    			listaProgramasSubPlan.add(nuevoPrograma);
    		}
    		System.out.println("total programas cargados " + listaProgramasSubPlan.size());
    		
    		libroPma.close();
    		
    		if (!mensajeFilaIncompleta.equals("")) {
	            throw new RuntimeException("_blank_cell");
	        }
    		
    		if(rows > 1) {
    			documentoPlantillaPma = new DocumentoEstudioImpacto();

        		String[] split = event.getFile().getContentType().split("/");
        		String extension = "." + split[split.length - 1];
        		documentoPlantillaPma = new DocumentoEstudioImpacto();
        		documentoPlantillaPma.setNombre(event.getFile().getFileName());
        		documentoPlantillaPma.setMime(event.getFile().getContentType());
        		documentoPlantillaPma.setContenidoDocumento(event.getFile().getContents());
        		documentoPlantillaPma.setExtesion(extension);
    		} else {
    			JsfUtil.addMessageError("Ingrese al menos una fila de datos");
    		}
    		
		} catch (Exception e) {
			listaDetallePmaPrograma = new ArrayList<>();
			listaProgramasSubPlan = new ArrayList<ProgramaPlanManejoEsIA>();
			
	    	nuevoPrograma.setListaDetallePlan(listaDetallePmaPrograma);
	    	nuevoPrograma.setCostoPrograma(null);
	    	
			e.printStackTrace();
			String mensaje = "Error en la lectura del archivo";
			if(e.getMessage().equals("_blank_cell")) {
				mensaje = mensajeFilaIncompleta;
			} else if(e.getMessage().equals("_format_number_cell")) {
				mensaje = "El costo debe ser un valor numérico";
			} else if(e.getMessage().equals("_value_number_cell")) {
				mensaje = "El valor de la columna Costo Estimado no puede ser menor a USD 1";
			}
			
			JsfUtil.addMessageError(mensaje);
		} finally {
	        libroPma.close();
	    }
    }
	
	public DetallePlanManejoEsIA mapearData(Row row, List<String> nombreColumnas, int rows) {
		DetallePlanManejoEsIA detalle = new DetallePlanManejoEsIA();
		
		if (nombreColumnas.size() < 12) {
			throw new RuntimeException("Archivo no válido");
		}
		
		for (int i=0; i<row.getLastCellNum(); i++) {
	        Cell cell = row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
	        
	        if (cell == null) {
	        	String mensajeVacio = "No existe información en la columna " + nombreColumnas.get(i) + " y fila " + rows + ".";
	        	mensajeFilaIncompleta += (mensajeFilaIncompleta.equals("")) ? mensajeVacio : "<br />" + mensajeVacio ;
	        } else {
	        	String valorCelda = cell.toString();
		        
		        switch (i) {
				case 0:
					detalle.setCodigo(valorCelda);
					break;
				case 1:
					detalle.setNombrePrograma(valorCelda);
					break;
				case 2:
					detalle.setEtapa(valorCelda);
					break;
				case 3:
					detalle.setActividad(valorCelda);
					break;
				case 4:
					detalle.setAspectoAmbiental(valorCelda);
					break;
				case 5:
					detalle.setImpacto(valorCelda);
					break;
				case 6:
					detalle.setMedidas(valorCelda);
					break;
				case 7:
					detalle.setIndicadores(valorCelda);
					break;
				case 8:
					detalle.setMediosVerificacion(valorCelda);
					break;
				case 9:
					detalle.setPlazo(valorCelda);
					break;
				case 10:
					detalle.setFrecuencia(valorCelda);
					break;
				case 11:
					try {
						Double.valueOf(valorCelda.replace("," , "."));
					} catch (Exception e) {
						throw new RuntimeException("_format_number_cell");
					}
					Double costo = Double.valueOf(valorCelda.replace("," , "."));
					
					if (costo < 1) {
						throw new RuntimeException(
								"_value_number_cell");
					}
					
					detalle.setCosto(costo);
					break;
				default:
					break;
				}
	        }
	    }
		
		if(detalle.getCosto() == null) {
			detalle.setCosto(0.0);
		}
		
		return detalle;
	}
	
	public void nuevoDetalleSubPlan(EntityPmaEsIA plan) {
		try {
			habilitarSiguiente = false;
			
			if(plan.getListaProgramas() == null) {
				documentoPlantillaPma = null;
				listaProgramasSubPlan = null;
			} else {
				documentoPlantillaPma = plan.getPlanManejo().getPlantillaSubPlan();
				listaProgramasSubPlan = plan.getListaProgramas();
			}
			
			planSeleccionado = plan;
			setIndiceTabActivo(plan);
			setIndiceTabActivoSubsanacion(plan);
			
			if(planSeleccionado.getListaProgramas() == null) {
				planSeleccionado.setListaProgramas(new ArrayList<ProgramaPlanManejoEsIA>());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void validateDetalleSubPlan(FacesContext context, UIComponent validate, Object value) throws RemoteException {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		if(planSeleccionado.getSubplan().getRequerido()) {
			if(listaProgramasSubPlan == null || listaProgramasSubPlan.size() == 0) {
				errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe adjuntar la Plantilla correspondiente al Sub Plan", null));
			}
		} else {
			if(planSeleccionado.getPlanManejo().getIngresoDetalle() != null 
					&& planSeleccionado.getPlanManejo().getIngresoDetalle()) {
				if(listaProgramasSubPlan == null || listaProgramasSubPlan.size() == 0) {
					errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe adjuntar la Plantilla correspondiente al Sub Plan", null));
				}
			} 
		}
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public void aceptarDetalleSubPlan() {
		planSeleccionado.setListaProgramas(listaProgramasSubPlan);
		planSeleccionado.getPlanManejo().setPlantillaSubPlan(documentoPlantillaPma);
		
		listaProgramasEliminar.addAll(listaProgramasEliminarAux);
		listaDocumentoPlantillaEliminar.addAll(listaDocumentoPlantillaEliminarAux);
		
		listaProgramasSubPlan = null;
		costoPrograma = null;
		documentoPlantillaPma = null;
		listaDocumentoPlantillaEliminarAux = new ArrayList<>();
		
		calcularCostoPlan(planSeleccionado);
		calcularCostoPmi();
	}
	
	public void calcularCostoPlan(EntityPmaEsIA itemSubplan) {
		if(itemSubplan != null && itemSubplan.getListaProgramas() != null 
				&& itemSubplan.getListaProgramas().size() > 0) {
			Double costoSubPlan = 0.0;
			
			for (ProgramaPlanManejoEsIA programa : itemSubplan.getListaProgramas()) {
				Double costoPrograma = 0.0;
				if(programa.getCostoPrograma() != null) {
					costoPrograma = programa.getCostoPrograma();
				}
				
				costoSubPlan = costoSubPlan + costoPrograma;
			}
			
			itemSubplan.getPlanManejo().setCostoSubplan(costoSubPlan);
		}
	}
	
	public void eliminarPlantillaSubPlan(EntityPmaEsIA plan) {
		if(plan.getListaProgramas() != null) {
			listaProgramasEliminar.addAll(plan.getListaProgramas());
			
			listaDocumentoPlantillaEliminar.add(plan.getPlanManejo().getPlantillaSubPlan());
		}
		
		plan.getPlanManejo().setCostoSubplan(null);
		plan.setListaProgramas(null);
		plan.getPlanManejo().setPlantillaSubPlan(null);
		
		setIndiceTabActivo(plan);
		setIndiceTabActivoSubsanacion(plan);
		
		planSeleccionado = new EntityPmaEsIA();
		
		calcularCostoPmi();
		
		habilitarSiguiente = false;
	}

	public void agregarAnexoSubPlan(EntityPmaEsIA plan) {
		try {
			habilitarSiguiente = false;
			planSeleccionado = plan;
			
			setIndiceTabActivo(plan);
			setIndiceTabActivoSubsanacion(plan);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void uploadAnexoSubPlan(FileUploadEvent file) {	
        String[] split=file.getFile().getContentType().split("/");
        String extension = "."+split[split.length-1];
        DocumentoEstudioImpacto documentoAnexo = new DocumentoEstudioImpacto();
        documentoAnexo.setNombre(file.getFile().getFileName());
        documentoAnexo.setMime(file.getFile().getContentType());
        documentoAnexo.setContenidoDocumento(file.getFile().getContents());
        documentoAnexo.setExtesion(extension);
        documentoAnexo.setNombreTabla("PMA_AnexoSubPlan");
        
        planSeleccionado.getPlanManejo().getListaAnexosSubPlan().add(documentoAnexo);
    }

    public void eliminarAnexoSubPlan(EntityPmaEsIA plan, DocumentoEstudioImpacto documentoAnexo) {
		try {
			listaDocumentoPlantillaEliminar.add(documentoAnexo);

			plan.getPlanManejo().getListaAnexosSubPlan().remove(documentoAnexo);
			
			setIndiceTabActivo(plan);
			
			planSeleccionado = new EntityPmaEsIA();
			habilitarSiguiente = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Boolean validarDatosPma(Boolean showMensaje) {
		Boolean valido = true;
		Integer nroPlanError = 0;
		
		for(EntityPmaEsIA plan : listaPlanManejo) {
			valido = validarPlan(plan);
			
			if(!valido) {
				if(showMensaje) {
					JsfUtil.addMessageError("Es indispensable el ingreso de información del " + plan.getSubplan().getNombre());
				}
				nroPlanError++;
			}
			
			if(habilitadoIngreso && plan.getPlanManejo().getListaSubsanacionesSubPlan() != null 
					&& plan.getPlanManejo().getListaSubsanacionesSubPlan().size() > 0 ) {
				List<EntityPmaEsIA> listaSubsanaciones = plan.getPlanManejo().getListaSubsanacionesSubPlan();
				EntityPmaEsIA planSub = listaSubsanaciones.get(listaSubsanaciones.size() - 1);
				valido = validarPlan(planSub);
				
				if(!valido) {
					if(showMensaje) {
						JsfUtil.addMessageError("Es indispensable el ingreso de información del " + plan.getSubplan().getNombre() );
					}
					nroPlanError++;
				}
			}
			
			
		}
		
		return (nroPlanError.equals(0)) ? true : false;
	}
	
	public Boolean validarPlan(EntityPmaEsIA plan) {
		Boolean valido = true;
		Boolean validarDetalle = false;
		
		PlanManejoAmbientalEsIA planManejo = plan.getPlanManejo();
		
		if(!plan.getSubplan().getRequerido()) {
			if(planManejo.getIngresoDetalle() == null) {
				valido = false;
			} else {
				if(planManejo.getIngresoDetalle()) {
					validarDetalle = true;
				} else {
					if(planManejo.getJustificacion() == null || planManejo.getJustificacion().isEmpty()) {
						valido = false;
					}
				}
			}
		}
		
		if(plan.getSubplan().getRequerido() || validarDetalle) {
			if(plan.getListaProgramas() == null || plan.getListaProgramas().size() == 0) {
				valido = false;
			} else {
				for (ProgramaPlanManejoEsIA programa : plan.getListaProgramas()) {
					if(programa.getId() == null && (programa.getListaDetallePlan() == null || programa.getListaDetallePlan().size() == 0)) {
						valido = false;
					}
					
					if(programa.getCostoPrograma() == null) {
						valido = false;
					}
				}
			}
		}
		
		return valido;
	}
	
	public void guardarPlanManejo() throws Exception {
		indexPma = -1;
		indexSubsanacion = -1;
		
		for(EntityPmaEsIA itemPma : listaPlanManejo) {
			List<EntityPmaEsIA> listaSubsanaciones = itemPma.getPlanManejo().getListaSubsanacionesSubPlan();
			
			guardarDetallePlanManejo(itemPma);
			
			if(listaSubsanaciones != null && listaSubsanaciones.size() > 0) {
				
				itemPma.getPlanManejo().setRegistroHabilitado(false);
				itemPma.setPlanManejo(planManejoAmbientalEsIAFacade.guardarPlan(itemPma.getPlanManejo()));
				
				for(EntityPmaEsIA itemPmaSub : listaSubsanaciones) {
					itemPmaSub.getPlanManejo().setIdSubPlanPadre(itemPma.getPlanManejo().getId());
					guardarDetallePlanManejo(itemPmaSub);
				}
			}
		}
		
		for(ProgramaPlanManejoEsIA item : listaProgramasEliminar) {
			if(item.getId() != null) {
				item.setEstado(false);
				planManejoAmbientalEsIAFacade.eliminarPrograma(item);
			}
		}		
		
		for(DocumentoEstudioImpacto item : listaDocumentoPlantillaEliminar) {
			if(item != null && item.getId() != null) {
				item.setEstado(false);
				documentosFacade.guardar(item);
			}
		}
		
		listaProgramasEliminar = new ArrayList<>();
		listaDocumentoPlantillaEliminar = new ArrayList<>();
	}
	
	public void guardarDetallePlanManejo(EntityPmaEsIA itemPma) throws Exception {
		Boolean guardarDetalle = false;
		
		if(!itemPma.getSubplan().getRequerido()) {
			if(itemPma.getPlanManejo().getIngresoDetalle() != null && itemPma.getPlanManejo().getIngresoDetalle()){
				guardarDetalle = true;
				
				itemPma.getPlanManejo().setJustificacion(null);
			} else {
				itemPma.getPlanManejo().setIdEstudio(informacionProyectoEia.getId());
				itemPma.getPlanManejo().setIdSubplan(itemPma.getSubplan().getId());
				itemPma.setPlanManejo(planManejoAmbientalEsIAFacade.guardarPlan(itemPma.getPlanManejo()));
				
				//eliminar programas y detalles cuando se cambia a la opción NO desea ingresar el subplan
				if(itemPma.getPlanManejo().getId() != null && itemPma.getListaProgramas() != null) {
					for(ProgramaPlanManejoEsIA item : itemPma.getListaProgramas()) {
						if(item.getId() != null) {
							item.setEstado(false);
							planManejoAmbientalEsIAFacade.eliminarPrograma(item);
						}
					}
				}
			}
		}
		
		if(itemPma.getSubplan().getRequerido() || guardarDetalle) {
			itemPma.getPlanManejo().setIdEstudio(informacionProyectoEia.getId());
			itemPma.getPlanManejo().setIdSubplan(itemPma.getSubplan().getId());			
			itemPma.setPlanManejo(planManejoAmbientalEsIAFacade.guardarPlan(itemPma.getPlanManejo()));
			Integer idNuevoPlan =  itemPma.getPlanManejo().getId();
			
			if(itemPma.getListaProgramas() != null && itemPma.getListaProgramas().size() > 0) {
				if(itemPma.getListaProgramas().get(0).getId() == null) {
					for(ProgramaPlanManejoEsIA programa : itemPma.getListaProgramas()) {
						programa.setIdPlanManejo(idNuevoPlan);
						guardarProgramaPma(programa, itemPma.getSubplan().getNombre());
					}
				}
			}
			
			DocumentoEstudioImpacto plantillaPlan = itemPma.getPlanManejo().getPlantillaSubPlan();
			List<DocumentoEstudioImpacto> listaAnexos = itemPma.getPlanManejo().getListaAnexosSubPlan();
			
			if(listaAnexos != null && listaAnexos.size() > 0) {
				List<DocumentoEstudioImpacto> listaAnexosAux = new ArrayList<>();
				for (DocumentoEstudioImpacto anexo : listaAnexos) {
					if(anexo.getId() == null) {
						anexo.setIdTable(idNuevoPlan);
						
						anexo = documentosFacade.guardarDocumentoAlfrescoCA(proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
								"ESTUDIO_IMPACTO_AMBIENTAL", anexo, TipoDocumentoSistema.EIA_ANEXO_SUB_PLAN_PMA);
					} 
					
					listaAnexosAux.add(anexo);
				}
				
				itemPma.getPlanManejo().setListaAnexosSubPlan(listaAnexosAux);
			}
			
			if(plantillaPlan != null) {
				if(plantillaPlan.getId() == null) {
					plantillaPlan.setNombreTabla("PMA_" + itemPma.getSubplan().getNombre());
					plantillaPlan.setIdTable(idNuevoPlan);
					
					DocumentoEstudioImpacto docPlantilla = documentosFacade.guardarDocumentoAlfrescoCA(proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
							"ESTUDIO_IMPACTO_AMBIENTAL", plantillaPlan, TipoDocumentoSistema.EIA_PLANTILLA_DETALLE_PMA);
					
					itemPma.getPlanManejo().setPlantillaSubPlan(docPlantilla);
				}
			}
			
		}
	}
	
	public void guardarProgramaPma(ProgramaPlanManejoEsIA programa, String nombreSubPlan) throws Exception {

		List<DetallePlanManejoEsIA> listaDetalles = programa.getListaDetallePlan();
		
		programa = planManejoAmbientalEsIAFacade.guardarPrograma(programa);
		
		if(listaDetalles != null) {
			for(DetallePlanManejoEsIA detalle : listaDetalles) {
				detalle.setIdPrograma(programa.getId());
				detalle = planManejoAmbientalEsIAFacade.guardarPlanDetalle(detalle);
			}
			
			programa.setListaDetallePlan(listaDetalles);
		}
	}
	
	public void siguienteValidarPma() throws ServiceException {
		for (EntityPmaEsIA itemPma : listaPlanManejo) {
			PlanManejoEsIAObservaciones planObservacion =  itemPma.getPlanManejo().getPlanManejoObservacion();
			
			planManejoAmbientalEsIAFacade.guardarPlan(itemPma.getPlanManejo());
			
			if(mostrarObservaciones && planObservacion != null) {
				planObservacion.setIdPlanManejo(itemPma.getPlanManejo().getId());
				planObservacion.setNombreClaseObservacion(nombreClaseObservacionesPma);
				planObservacion.setUsuarioRegistro(loginBean.getUsuario());
				planManejoEsIAObservacionesFacade.guardar(planObservacion);
			}
		}
		
		Boolean avanzar = true;
		
		if(mostrarObservaciones) {
			for(EntityPmaEsIA itemPma : listaPlanManejo) {
				if(itemPma.getPlanManejo().getPlanManejoObservacion().getTieneObservaciones() != null 
						&& itemPma.getPlanManejo().getPlanManejoObservacion().getTieneObservaciones()) {
					List<ObservacionesEsIA> observaciones = null;
					if (numeroRevision > 1) {
						observaciones = getObservacionesPma(false, itemPma.getSubplan().getCodigo(), false, false);
					} else {
						observaciones = getObservacionesPma(false, itemPma.getSubplan().getCodigo(), true, false);
					}
					 
					if (observaciones == null || observaciones.size() == 0) {
						avanzar = false;
						JsfUtil.addMessageError("Por favor ingrese al menos una observación en el " + itemPma.getSubplan().getNombre());
					}
				} 
				
				if(itemPma.getPlanManejo().getListaSubsanacionesSubPlan() != null && itemPma.getPlanManejo().getListaSubsanacionesSubPlan().size() > 0) {
					
				}
			}
		}
		
		if (avanzar) {
			if(mostrarObservaciones) {
				for (EntityPmaEsIA itemPma : listaPlanManejo) {
					if(!itemPma.getPlanManejo().getPlanManejoObservacion().getTieneObservaciones() 
							&& itemPma.getListaObservacionesSubPlan() != null 
							&& itemPma.getListaObservacionesSubPlan().size() > 0) { //borra las observaciones q existan si se cambio que el plan no tiene observaciones
						for (ObservacionesEsIA obj : itemPma.getListaObservacionesSubPlan()) {
							if (obj.getId() != null) {
					            obj.setEstado(false);
					            observacionesEsIAFacade.guardar(obj);
					        } 
						}
						
						itemPma.setListaObservacionesSubPlan(new ArrayList<ObservacionesEsIA>());
					}
				}
			}
			
			siguiente();
		}
	}
	
	public void onTabChange(TabChangeEvent event) {
		planSeleccionado = (EntityPmaEsIA) event.getData();
    }
	
	public List<ObservacionesEsIA> getObservacionesPma(Boolean all, String seccion, Boolean soloPendientes, Boolean editar) throws ServiceException {
		List<ObservacionesEsIA> observacionesSeccion = new ArrayList<>();
		List<ObservacionesEsIA> observaciones = null;
		
		if(all) {
			if(soloPendientes) {
				observaciones = observacionesEsIAFacade.listarPorIdClaseSeccionNoCorregidas(informacionProyectoEia.getId(), seccion);
			} else {
				observaciones = observacionesEsIAFacade.listarPorIdClaseSeccion(informacionProyectoEia.getId(), seccion);
			}
		} else {
			if(soloPendientes) {
				observaciones = observacionesEsIAFacade.listarPorIdClaseNombreClaseSeccionNoCorregidas(informacionProyectoEia.getId(), nombreClaseObservacionesPma, seccion);
			} else {
				 List<String> listaNombreClases = Arrays.asList(nombreClaseObservaciones.split(";"));
				observaciones = observacionesEsIAFacade.listarPorIdClaseNombreClase(informacionProyectoEia.getId(), listaNombreClases, seccion);
			}
		}

		if(observaciones != null) {
			if(editar) {
				for (ObservacionesEsIA ob : observaciones) {
	            	if (ob.getIdUsuario().equals(JsfUtil.getLoggedUser().getId())) {
	                    ob.setDisabled(false);
	                }
	                else{
	                    ob.setDisabled(false);
	                }
	            }
			}
			
			observacionesSeccion.addAll(observaciones);
		}
		
		return observacionesSeccion;
	}

	public void agregarObservacionPma(EntityPmaEsIA plan) {
		planSeleccionado = plan;
		
		ObservacionesEsIA ob = new ObservacionesEsIA();
		ob.setCampo("");
		ob.setDescripcion("");
		ob.setIdClase(new Integer(informacionProyectoEia.getId()));
		ob.setNombreClase(nombreClaseObservacionesPma);
		ob.setSeccionFormulario(plan.getSubplan().getCodigo());
		ob.setUsuario(JsfUtil.getLoggedUser());
		ob.setFechaRegistro(new Date());
		ob.setDisabled(false);
		ob.setIdTarea(bandejaTareasBean.getTarea().getTaskId());
		
		planSeleccionado.getListaObservacionesSubPlan().add(ob);
		
		RequestContext.getCurrentInstance().update(":#{p:component('acdPlan')}");
	}

	public void guardarObservacionPma(EntityPmaEsIA plan) {
        try {
        	planSeleccionado = plan;
        	
        	for(ObservacionesEsIA obs : plan.getListaObservacionesSubPlan()){
        		
        		PolicyFactory policy = (new HtmlPolicyBuilder().allowElements("table", "tr", "td", "th").allowAttributes("style").globally()).toFactory();
        		policy = policy.and(Sanitizers.FORMATTING).and(Sanitizers.BLOCKS).and(Sanitizers.IMAGES).and(Sanitizers.LINKS);
        		
        		String safeHtml = policy.sanitize(obs.getDescripcion());
        		obs.setDescripcion(safeHtml);
        	}

			observacionesEsIAFacade.guardar(plan.getListaObservacionesSubPlan());
			
			planManejoAmbientalEsIAFacade.guardarPlan(planSeleccionado.getPlanManejo());
			
			PlanManejoEsIAObservaciones planObservacion =  planSeleccionado.getPlanManejo().getPlanManejoObservacion();
			if(planObservacion != null) {
				planObservacion.setIdPlanManejo(planSeleccionado.getPlanManejo().getId());
				planObservacion.setNombreClaseObservacion(nombreClaseObservacionesPma);
				planObservacion.setUsuarioRegistro(loginBean.getUsuario());
				planManejoEsIAObservacionesFacade.guardar(planObservacion);
			}

            JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
            
            RequestContext.getCurrentInstance().update(":#{p:component('acdPlan')}");
            
        } catch (Exception e) {
        	e.printStackTrace();
            JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO + " " + e.getMessage());
        } 
    }
    
    public void eliminarObservacionPma(EntityPmaEsIA plan, ObservacionesEsIA obj) {
    	try{
	        if (obj.getId() != null) {
	            obj.setEstado(false);
	            observacionesEsIAFacade.guardar(obj);
	        }
	        
	        plan.getListaObservacionesSubPlan().remove(obj);
	        
    	} catch (Exception e) {
        	e.printStackTrace();
            JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO + " " + e.getMessage());
        } 
    }
    
    public void verDetalleObservacionPma(ObservacionesEsIA obj) {
    	campoObservacionPma = obj.getCampo();
    	detalleObservacionPma = obj.getDescripcion();
    	
    	RequestContext context = RequestContext.getCurrentInstance();
        context.update(":formDialogAdjunto:dlgVerObservacionSubPlan");
        context.execute("PF('dlgVerObservacionSubPlan').show();");
    	
    }
	//FIN AUTOMATIZACION PLAN DE MANEJO
    
	private void validarUbicacionCamaroneras(){
		try {
			
			Calendar fechaAcuerdo= Calendar.getInstance();
	    	fechaAcuerdo.set(Calendar.YEAR, 2023);
	    	fechaAcuerdo.set(Calendar.MONTH, 10);
	    	fechaAcuerdo.set(Calendar.DATE, 7);
	    		    	
	    	Calendar fechaProyecto = Calendar.getInstance();
	    	fechaProyecto.setTime(proyectoLicenciaCoa.getFechaCreacion());
			
	    	if(fechaProyecto.after(fechaAcuerdo) || fechaProyecto.equals(fechaAcuerdo)){
	    		if(proyectoLicenciaCoa.getZona_camaronera() != null && proyectoLicenciaCoa.getZona_camaronera().equals("ALTA")){
	    			
	    			List<ProyectoLicenciaCuaCiuu> listaActividadesCiuu = new ArrayList<ProyectoLicenciaCuaCiuu>();
	    			listaActividadesCiuu = proyectoLicenciaCuaCiuuFacade.actividadesPorProyecto(proyectoLicenciaCoa);
	    			
	    			for(ProyectoLicenciaCuaCiuu actividad : listaActividadesCiuu){
	    				if(actividad.getOrderJerarquia() == 1){
	    					
	    					if(Constantes.getActividadesCamaroneras().contains(actividad.getCatalogoCIUU().getCodigo())){
	    						SubActividades subActividad1=actividad.getSubActividad();
	    						if(subActividad1.getSubActividades().getRequiereValidacionCoordenadas().equals(1)){
	    							
	    							UbicacionesGeografica ubicacionPrincipal = proyectoLicenciaCoaUbicacionFacade.ubicacionPrincipal(proyectoLicenciaCoa).getUbicacionesGeografica();
						  								    							
	    							UbicacionesGeografica provincia=ubicacionPrincipal.getUbicacionesGeografica().getUbicacionesGeografica();
	    							UbicacionesGeografica canton=ubicacionPrincipal.getUbicacionesGeografica();	
	    							
	    							if(provincia.getId()==15) {
	    								if(canton.getNombre().equals("GUAYAQUIL")){
	    									proyectoLicenciaCoa.setAreaResponsable(ubicacionPrincipal.getEnteAcreditado());							
    									}	    									
	    							}	    							
	    							if(provincia.getId()==28){
	    								proyectoLicenciaCoa.setAreaResponsable(areaService.getAreaGadProvincial(3, ubicacionPrincipal.getUbicacionesGeografica().getUbicacionesGeografica()));	    								
	    							}
	    							
	    							proyectoLicenciaCoa=proyectoLicenciaCoaFacade.guardar(proyectoLicenciaCoa);	    							
	    						}
	    					}
	    				}
	    			}
				}
	    	}			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void validarActividadCamaronera(){
		try {
			
			List<ProyectoLicenciaCuaCiuu> listaActividadesCiuu = new ArrayList<ProyectoLicenciaCuaCiuu>();
			listaActividadesCiuu = proyectoLicenciaCuaCiuuFacade.actividadesPorProyecto(proyectoLicenciaCoa);
			
			for(ProyectoLicenciaCuaCiuu actividad : listaActividadesCiuu){
				
				if(Constantes.getActividadesCamaroneras().contains(actividad.getCatalogoCIUU().getCodigo())){
					SubActividades subActividad1=actividad.getSubActividad();
					if(subActividad1.getSubActividades().getRequiereValidacionCoordenadas().equals(1)){
						esActividadCamaronera = true;	
						setZonaCamaronera(proyectoLicenciaCoa.getZona_camaronera());
					}					
				}				
			}						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public StreamedContent descargarDocConcesionAlta() throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			
			if (documentoDocCamaroneraAlta != null && documentoDocCamaroneraAlta.getAlfrescoId() != null) {
				documentoContent = documentosFacade.descargar(documentoDocCamaroneraAlta.getAlfrescoId());
			} else if (documentoDocCamaroneraAlta.getContenidoDocumento() != null) {
				documentoContent = documentoDocCamaroneraAlta.getContenidoDocumento();
			}
			
			if (documentoDocCamaroneraAlta != null && documentoDocCamaroneraAlta.getNombre() != null
					&& documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documentoDocCamaroneraAlta.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public StreamedContent descargarDocConcesionPlaya() throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			
			if (documentoDocCamaroneraPlaya != null && documentoDocCamaroneraPlaya.getAlfrescoId() != null) {
				documentoContent = documentosFacade.descargar(documentoDocCamaroneraPlaya.getAlfrescoId());
			} else if (documentoDocCamaroneraPlaya.getContenidoDocumento() != null) {
				documentoContent = documentoDocCamaroneraPlaya.getContenidoDocumento();
			}
			
			if (documentoDocCamaroneraPlaya != null && documentoDocCamaroneraPlaya.getNombre() != null
					&& documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documentoDocCamaroneraPlaya.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public String getFechaInicioProrroga() throws Exception {
		if(aclaracionProrrogaEsIA.getDiasProrroga() != null && aclaracionProrrogaEsIA.getDiasProrroga() > 0) {
			Date fechaFinMod = datosProrrogaModificacion.getFechaFinModificacion();
			Date fechaInicioProrroga = diasHabilesUtil.recuperarFechaHabil(fechaFinMod, 1, true);//se agrega 1 dia porque la prórroga inicia mañana o el siguiente dia habil
			
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			return formatter.format(fechaInicioProrroga);
		}
		
		return null;
	}
	
	public Boolean verMensajeProrroga() {
		Boolean verMensaje = false;
		
		if(tieneProrroga != null && tieneProrroga) {
			if(datosProrrogaModificacion.getFechaFinModificacionProrroga() != null) {
				verMensaje = true;
			}
		}
		
		return verMensaje;
	}
	
	public Boolean getExisteInfoSubplanes() throws Exception {
		Boolean existePma = false;
		List<PlanManejoAmbientalEsIA> listaPlanManejo = planManejoAmbientalEsIAFacade.obtenerPlanManejoEsia(informacionProyectoEia.getId());

		if (listaPlanManejo != null && listaPlanManejo.size() > 0) {
			existePma = true;
		}

		return existePma;
	}
}