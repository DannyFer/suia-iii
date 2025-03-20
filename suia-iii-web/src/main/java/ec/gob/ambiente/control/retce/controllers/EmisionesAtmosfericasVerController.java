package ec.gob.ambiente.control.retce.controllers;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.cliente.ConsultaRucCedula;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.retce.model.AutorizacionEmisiones;
import ec.gob.ambiente.retce.model.CatalogoFacilidadesMonitoreo;
import ec.gob.ambiente.retce.model.CatalogoFrecuenciaMonitoreo;
import ec.gob.ambiente.retce.model.CatalogoMetodoEstimacion;
import ec.gob.ambiente.retce.model.CatalogoSustanciasRetce;
import ec.gob.ambiente.retce.model.CatalogoUnidades;
import ec.gob.ambiente.retce.model.DatoObtenidoMedicion;
import ec.gob.ambiente.retce.model.DatosLaboratorio;
import ec.gob.ambiente.retce.model.DetalleCatalogoGeneral;
import ec.gob.ambiente.retce.model.DetalleEmisionesAtmosfericas;
import ec.gob.ambiente.retce.model.EmisionesAtmosfericas;
import ec.gob.ambiente.retce.model.FacilidadesMonitoreoEmisiones;
import ec.gob.ambiente.retce.model.FuenteFijaCombustion;
import ec.gob.ambiente.retce.model.InformacionProyecto;
import ec.gob.ambiente.retce.model.LimiteMaximoPermitido;
import ec.gob.ambiente.retce.model.SubstanciasRetce;
import ec.gob.ambiente.retce.model.TecnicoResponsable;
import ec.gob.ambiente.retce.model.TipoCombustibleRetce;
import ec.gob.ambiente.retce.services.AutorizacionEmisionesFacade;
import ec.gob.ambiente.retce.services.CatalogoFacilidadesMonitoreoFacade;
import ec.gob.ambiente.retce.services.CatalogoFrecuenciaMonitoreoFacade;
import ec.gob.ambiente.retce.services.CatalogoMetodoEstimacionFacade;
import ec.gob.ambiente.retce.services.CatalogoSustanciasRetceFacade;
import ec.gob.ambiente.retce.services.CatalogoUnidadesFacade;
import ec.gob.ambiente.retce.services.DatoObtenidoMedicionFacade;
import ec.gob.ambiente.retce.services.DatosLaboratorioFacade;
import ec.gob.ambiente.retce.services.DetalleCatalogoGeneralFacade;
import ec.gob.ambiente.retce.services.DetalleEmisionesAtmosfericasFacade;
import ec.gob.ambiente.retce.services.EmisionesAtmosfericasFacade;
import ec.gob.ambiente.retce.services.FacilidadesMonitoreoEmisionesFacade;
import ec.gob.ambiente.retce.services.FuenteFijaCombustionFacade;
import ec.gob.ambiente.retce.services.InformacionProyectoFacade;
import ec.gob.ambiente.retce.services.LimiteMaximoPermitidoFacade;
import ec.gob.ambiente.retce.services.SubstanciasRetceFacade;
import ec.gob.ambiente.retce.services.TecnicoResponsableFacade;
import ec.gob.ambiente.retce.services.TipoCombustibleRetceFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comun.bean.WizardBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.planemergente.service.TipoSectorFaseService;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gov.sri.wsconsultacontribuyente.ContribuyenteCompleto;

@ManagedBean
@ViewScoped
public class EmisionesAtmosfericasVerController {
	private static final Logger LOG = Logger.getLogger(InformacionBasicaController.class);
	private final ConsultaRucCedula consultaRucCedula = new ConsultaRucCedula(Constantes.getUrlWsRegistroCivilSri());
	
	@EJB
	private FuenteFijaCombustionFacade fuenteFijaFacade;
	@EJB
	private TipoSectorFaseService tipoSectorFacade;
	@EJB
	private TipoCombustibleRetceFacade tipoCombustibleFacade;
	@EJB
	private CatalogoUnidadesFacade catalogoUnidadesFacade;
	@EJB
	private DetalleCatalogoGeneralFacade detalleCatalogoGeneralFacade;
	@EJB
	private CatalogoFacilidadesMonitoreoFacade catalogoFacilidadesMonitoreoFacade;
	@EJB
	private AutorizacionEmisionesFacade autorizacionEmisionesFacade;
	@EJB
	private CatalogoFrecuenciaMonitoreoFacade catalogoFrecuenciaMonitoreoFacade;
	@EJB
	private LimiteMaximoPermitidoFacade limiteMaximoPermitidoFacade;
	@EJB
	private CatalogoMetodoEstimacionFacade catalogoMetodoEstimacionFacade;
	@EJB
	private EmisionesAtmosfericasFacade emisionesAtmosfericasFacade;
	@EJB
	private DetalleEmisionesAtmosfericasFacade detalleEmisionesAtmosfericasFacade;
	@EJB
	private FacilidadesMonitoreoEmisionesFacade facilidadesMonitoreoEmisionesFacade;
	@EJB
	private DocumentosFacade documentosFacade;
	@EJB
	private InformacionProyectoFacade informacionProyectoFacade;
	@EJB
	private DatoObtenidoMedicionFacade datoObtenidoMedicionFacade;
	@EJB
	private DatosLaboratorioFacade datosLaboratorioFacade;
	@EJB
	private SubstanciasRetceFacade sustanciaRetceFacade;
	@EJB
	private CatalogoSustanciasRetceFacade catalogoSustanciasRetceFacade;
	@EJB
	private TecnicoResponsableFacade tecnicoResponsableFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private UsuarioFacade usuarioFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@ManagedProperty(value = "#{wizardBean}")
	@Getter
	@Setter
	private WizardBean wizardBean;

	@Getter
	@Setter
	private Documento oficioAprobacionDoc, documentoInformeLaboratorio, 
	documentoRespaldoSustancias, documentoJustificacionEstadoFuente, documentoJustificativo;
	
	@Getter
	@Setter
	private EmisionesAtmosfericas emisionesAtmosfericas;

	@Getter
	@Setter
	private DetalleEmisionesAtmosfericas detalleEmision;

	@Getter
	@Setter
	private FuenteFijaCombustion fuenteFijaCombustion;

	@Getter
	@Setter
	private List<FuenteFijaCombustion> listaFuentesFijasCombustion;

	@Getter
	@Setter
	private List<DetalleEmisionesAtmosfericas> listaDetalleEmisionesAtmosfericas, listaDetalleEmisionesAtmosfericasEliminadas;

	@Getter
	@Setter
	private List<TipoCombustibleRetce> listaTipoCombustible;

	@Getter
	@Setter
	private List<CatalogoUnidades> listaUnidadesPotencia, listaUnidadesCombustible;

	@Getter
	@Setter
	private List<DetalleCatalogoGeneral> listaCatalogoChimenea;

	@Getter
	@Setter
	private List<CatalogoFacilidadesMonitoreo> listaFacilidadesMonitoreo;

	@Getter
	@Setter
	private List<AutorizacionEmisiones> listaAutorizacionesEmisiones;

	@Getter
	@Setter
	private Integer idFuente, idTipoCombustible, idAutorizacion, idMetodoEstimacionParametro, idEstadoFuenteDetalleCatalogo,
			idUnidadPotencia, idFrecuenciaMonitoreo, idTipoFuncionamiento, idUnidadCombustible, idTipoFuenteRetce, idCatalogoSusRetce,
			idMetodoEstimacionSustancia;

	@Getter
	@Setter
	private List<DetalleCatalogoGeneral> listaCatalogoEstadoFuente, listaCatalogoTipoFuncionamiento; 

	@Getter
	@Setter
	private List<String> facilidadesMonitoreoSeleccionadas;

	@Getter
	@Setter
	private List<CatalogoFrecuenciaMonitoreo> listaCatalogoFrecuenciaMonitoreo;

	@Getter
	@Setter
	private List<LimiteMaximoPermitido> listaLimitesMaximoPermitidos;

	@Getter
	@Setter
	private List<DatoObtenidoMedicion> listaDatosObtenidos, listaDatosObtenidosTotal, listaDatosTotalAux, listaDatosObtenidosAux,
		listaHistorialDatosObtenidos, listaHistorialDatosObtenidosEliminados;

	@Getter
	@Setter
	private DatoObtenidoMedicion datoObtenido;

	@Getter
	@Setter
	private List<CatalogoMetodoEstimacion> listaMetodoEstimacion;

	@Getter
	@Setter
	private boolean mostrarEstadoFuente = true,	deshabilitarTipoCombustible = false;

	@Getter
	@Setter
	private boolean permitirContinuar;

	@Getter
	@Setter
	private boolean guardado;

	@Getter
	@Setter
	private InformacionProyecto informacionProyecto;

	@Getter
	@Setter
	private List<FacilidadesMonitoreoEmisiones> listaFacilidadesMonitoreoEmisiones;

	@Getter
	@Setter
	private String mesInicio, mesFin;

	@Getter
	@Setter
	private Integer anioInicio, anioFin;

	@Getter
	@Setter
	private List<DatosLaboratorio> listaLaboratorios, listaLaboratoriosEliminados, listaLaboratoriosHistorial;

	@Getter
	@Setter
	private DatosLaboratorio datoLaboratorio;

	@Getter
	@Setter
	private List<SubstanciasRetce> listaSustanciasRetce, listaSustanciasRetceEliminadas, listaSustanciasRetceHistorial;

	@Getter
	@Setter
	private List<CatalogoSustanciasRetce> listaCatalogoSustanciasRetce;

	@Getter
	@Setter
	private SubstanciasRetce sustanciaRetceIngresada;

	@Getter
	@Setter
	private boolean mostrarLaboratorios, mostrarAutorizacion;

	@Setter
	@Getter
	private TecnicoResponsable tecnicoResponsable;

	@Getter
	@Setter
	private List<EmisionesAtmosfericas> listaEmisionesAtmosfericas, listaHistorialEmisionesAtmosfericas;

	@Getter
	private String nombreUsuario, representanteLegal, rucCedula;

	@Getter
	private boolean verFormulario;
	
	@Getter
	@Setter
	private String nombreTabla;
	
	@Getter
	@Setter
	private List<Documento> listaDocumentosLaboratorio;
	
	@Getter
	@Setter
	private Boolean habilitarObservaciones, observacionesSoloLectura;
	
	private Map<String, Object> variables;
	    
	@EJB
	private ProcesoFacade procesoFacade;

	private String tramite;
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@Getter
	@Setter
	private Integer numeroObservacion;
	
	@Getter
	@Setter
	private List<DetalleEmisionesAtmosfericas> listaHistorialCaracteristicasFuente, listaHistorialFuncionamientoFuente, 
	listaHistorialChimenea, listaHistorialUbicacion, listaHistorialEstadoFuente, listaHistorialAutorizacion;
	
	@Getter
	@Setter
	private List<FacilidadesMonitoreoEmisiones> listaFacilidadesHistorial;
	
	@Getter
	@Setter
	private List<DatoObtenidoMedicion> listaIndividualHistorial;
	
	@Getter
	@Setter
	private List<Documento> listaDocumentosLaboratorioEliminados, listaDocumentosJustificativoHistorial, listaDocumentosJustificacionEstadoFuenteHistorial;
	
	@Getter
	@Setter
	private List<TecnicoResponsable> listaTecnicosHistorial;
	
	@Getter
	@Setter
	private boolean mostrarLimites = true,mostrarHoras=false,mostrarFase=false , documentoAdicionalLabora=false;
	
	@Getter
	@Setter
    private String documentoTipo1, documentoTipo2, documentoTipo3, documentoTipo4, documentoTipo5;
	
	@Getter
	@Setter 
	private Integer idLaboratorio;
	
	@Setter
	@Getter
	private Documento informeLaboratorio,registroLaboratorio,cadenaCustodia,protocoloMuestro,documentoAdicional;

	@PostConstruct
	public void init() {
		try {
	
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);		
			
			Object numObservacion = variables.get("numero_observaciones");
			
			if(numObservacion != null){
				numeroObservacion = Integer.valueOf(numObservacion.toString());
			}else{
				numeroObservacion = 0;
			}

			emisionesAtmosfericas = new EmisionesAtmosfericas();
			listaEmisionesAtmosfericas = new ArrayList<EmisionesAtmosfericas>();			
			
			emisionesAtmosfericas = emisionesAtmosfericasFacade.findByCodigo(tramite);
			informacionProyecto = informacionProyectoFacade.findById(emisionesAtmosfericas.getInformacionProyecto().getId());
			seleccionarEmision(emisionesAtmosfericas);
			buscarDatosOperador();
			
			if(!isTecnicoAutenticado()){
				this.observacionesSoloLectura = true;
				this.habilitarObservaciones = false;
			}else{
				this.observacionesSoloLectura = false;
				this.habilitarObservaciones = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void buscarDatosOperador() {
		rucCedula = emisionesAtmosfericas.getUsuarioCreacion();
		Usuario user = usuarioFacade.buscarUsuario(emisionesAtmosfericas.getUsuarioCreacion());
		Organizacion orga = null;
		try {
			orga = organizacionFacade.buscarPorRuc(user.getNombre());
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		if (orga != null) {
			nombreUsuario = orga.getNombre();
			representanteLegal = orga.getPersona().getNombre();
		} else {
			nombreUsuario = user.getPersona().getNombre();
		}
	}

	
	public void seleccionarEmision(EmisionesAtmosfericas emision) {
		this.emisionesAtmosfericas = emision;
		listaDetalleEmisionesAtmosfericas = detalleEmisionesAtmosfericasFacade.findByEmisionesAtmosfericas(emisionesAtmosfericas);
		listaDetalleEmisionesAtmosfericasEliminadas = new ArrayList<DetalleEmisionesAtmosfericas>();
		
		List<DetalleEmisionesAtmosfericas> listaDetalleEmisionesAtmosfericasBdd = detalleEmisionesAtmosfericasFacade.findByEmisionesAtmosfericasBdd(emisionesAtmosfericas);
		
		if(listaDetalleEmisionesAtmosfericas != null && !listaDetalleEmisionesAtmosfericas.isEmpty()){
			for(DetalleEmisionesAtmosfericas detalle : listaDetalleEmisionesAtmosfericas){
				if(detalle.getNumeroObservacion() != null && detalle.getNumeroObservacion().equals(numeroObservacion)){
					detalle.setNuevoEnModificacion(true);
					detalle.setRegistroModificado(false);
				}else{
					List<DetalleEmisionesAtmosfericas> listaHistoricos = detalleEmisionesAtmosfericasFacade.findByIdHistory(detalle.getId(), numeroObservacion);
					if(listaHistoricos != null && !listaHistoricos.isEmpty()){
						detalle.setRegistroModificado(true);
						detalle.setNuevoEnModificacion(false);
					}else{
						detalle.setRegistroModificado(false);
					}
				}				
			}
		}
		
		if(listaDetalleEmisionesAtmosfericasBdd != null && !listaDetalleEmisionesAtmosfericasBdd.isEmpty()){
			
			for(DetalleEmisionesAtmosfericas detalleEliminado : listaDetalleEmisionesAtmosfericasBdd){
				Comparator<DetalleEmisionesAtmosfericas> c = new Comparator<DetalleEmisionesAtmosfericas>() {
					
					@Override
					public int compare(DetalleEmisionesAtmosfericas o1, DetalleEmisionesAtmosfericas o2) {						
						return o1.getId().compareTo(o2.getId());
					}
				};				
				
				Collections.sort(listaDetalleEmisionesAtmosfericas, c);
				
				int index = Collections.binarySearch(listaDetalleEmisionesAtmosfericas, new DetalleEmisionesAtmosfericas(detalleEliminado.getIdRegistroOriginal()), c);
				
				if(index < 0){
					listaDetalleEmisionesAtmosfericasEliminadas.add(detalleEliminado);
				}				
			}			
		}else{
			listaDetalleEmisionesAtmosfericasEliminadas = new ArrayList<DetalleEmisionesAtmosfericas>();
		}
	}

	public String btnAtras() {
		@SuppressWarnings("unused")
		String currentStep = wizardBean.getCurrentStep();
		return null;
	}

	public String btnSiguiente() throws CmisAlfrescoException {

		@SuppressWarnings("unused")
		String currentStep = wizardBean.getCurrentStep();
		return null;
	}

	private void cargarDatos() {
		try {			
			listaFacilidadesMonitoreo = catalogoFacilidadesMonitoreoFacade.findAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void agregarDetalle(EmisionesAtmosfericas emision){
		verFormulario=true;		
		cargarDatosIniciales();	
		this.emisionesAtmosfericas=emision!=null?emision:new EmisionesAtmosfericas();	
		
		if(emisionesAtmosfericas != null && emisionesAtmosfericas.getId() != null){
			listaDetalleEmisionesAtmosfericas = detalleEmisionesAtmosfericasFacade.findByEmisionesAtmosfericas(emisionesAtmosfericas);			
			
		}		
	}
	
	private void cargarDatosIniciales(){
				
		fuenteFijaCombustion = new FuenteFijaCombustion();				
		detalleEmision = new DetalleEmisionesAtmosfericas();			
		fuenteFijaCombustion = new FuenteFijaCombustion();					
		listaLimitesMaximoPermitidos = new ArrayList<LimiteMaximoPermitido>();
		listaDatosObtenidos = new ArrayList<DatoObtenidoMedicion>();
		listaMetodoEstimacion = new ArrayList<CatalogoMetodoEstimacion>();
		listaDatosObtenidosTotal = new ArrayList<DatoObtenidoMedicion>();
		listaDatosTotalAux = new ArrayList<DatoObtenidoMedicion>();
		facilidadesMonitoreoSeleccionadas = new ArrayList<String>();
		listaFacilidadesMonitoreoEmisiones = new ArrayList<FacilidadesMonitoreoEmisiones>();
		listaLaboratorios = new ArrayList<DatosLaboratorio>();
		datoLaboratorio = new DatosLaboratorio();
		
		listaSustanciasRetce = new ArrayList<SubstanciasRetce>();
		sustanciaRetceIngresada = new SubstanciasRetce();
		listaSustanciasRetce = new ArrayList<SubstanciasRetce>();
		tecnicoResponsable = new TecnicoResponsable();	
		
		cargarDatos();			
	}
	
	public void editarDetalle(DetalleEmisionesAtmosfericas detalleEmision){
		cargarDatosIniciales();
		this.detalleEmision = detalleEmision;
		cargarDatosDetalleEmision();
	}
	
	private void cargarDatosDetalleEmision() {
		try {
			verFormulario = true;
			
			try {
				List<Documento> documentos = documentosFacade.documentoXTablaIdXIdDoc(emisionesAtmosfericas.getId(),
						EmisionesAtmosfericas.class.getSimpleName(), TipoDocumentoSistema.JUSTIFICATIVO_EMISION_ATMOSFERICA);				
				
				if(documentos != null){
					documentoJustificativo = documentos.get(0);
				}
				
				
				listaDocumentosJustificativoHistorial = new ArrayList<Documento>();
				
				for(Documento documento: documentos){
					if(documento.getIdHistorico() != null){
						listaDocumentosJustificativoHistorial.add(documento);
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (detalleEmision.getDocumentoOficioAprobacion() != null) {
				oficioAprobacionDoc = documentosFacade.buscarDocumentoPorId(detalleEmision.getDocumentoOficioAprobacion().getId());				
			}

			idFuente = detalleEmision.getFuenteFijaCombustion().getId();
						
			idUnidadPotencia = detalleEmision.getCatalogoUnidadesPotencia().getId();

			if (detalleEmision.getTipoCombustible() != null) {
				idTipoCombustible = detalleEmision.getTipoCombustible().getId();	
				nombreTabla = detalleEmision.getTipoCombustible().getNombreTabla();
				
				if(detalleEmision.getTipoCombustible().getTieneLimitesMaximos() != null && 
						!detalleEmision.getTipoCombustible().getTieneLimitesMaximos()){
					mostrarLimites = false;
				}else{
					mostrarLimites = true;
				}
				
			}else{
				nombreTabla = detalleEmision.getFuenteFijaCombustion().getNombreTabla();
			}

			listaFacilidadesMonitoreoEmisiones = facilidadesMonitoreoEmisionesFacade.findByDetail(detalleEmision);
			if (listaFacilidadesMonitoreoEmisiones != null	&& !listaFacilidadesMonitoreoEmisiones.isEmpty()) {
				for (FacilidadesMonitoreoEmisiones facilidadMonitoreo : listaFacilidadesMonitoreoEmisiones) {
					facilidadesMonitoreoSeleccionadas.add(facilidadMonitoreo.getCatalogoFacilidadesMonitoreo().getId().toString());
				}
			}

			if (detalleEmision.getCatalogoUnidadesCombustible() != null) {
				//solo mostrar en un text el nombre de la unidad 
				idUnidadCombustible = detalleEmision.getCatalogoUnidadesCombustible().getId();
			}
			if (detalleEmision.getHorasFuncionamiento() != null) {  
				mostrarHoras=true; 
			}
			if (detalleEmision.getFaseRetce() != null) {   
				mostrarFase=true; 
			}
			if (detalleEmision.getEstadoFuenteDetalleCatalogo() != null) {
				idEstadoFuenteDetalleCatalogo = detalleEmision.getEstadoFuenteDetalleCatalogo().getId();
				if(detalleEmision.getEstadoFuenteDetalleCatalogo().getDescripcion().equals("Inactiva")){
					mostrarEstadoFuente = false;
					
					List<Documento> documentos = documentosFacade.documentoXTablaIdXIdDoc(detalleEmision.getId(),
									EmisionesAtmosfericas.class.getSimpleName(),
									TipoDocumentoSistema.DOCUMENTO_JUSTIFICACION_ESTADO_FUENTE);
					
					listaDocumentosJustificacionEstadoFuenteHistorial = new ArrayList<Documento>();
					if(documentos != null && !documentos.isEmpty()){						
						documentoJustificacionEstadoFuente = documentos.get(0);
						
						for(Documento documento : documentos){
							if(documento.getIdHistorico() != null){
								listaDocumentosJustificacionEstadoFuenteHistorial.add(documento);
							}							
						}
						
					}					
				}
				else{
					mostrarEstadoFuente = true;
					List<Documento> documentos = documentosFacade.documentoXTablaIdXIdDoc(detalleEmision.getId(),
							EmisionesAtmosfericas.class.getSimpleName(),
							TipoDocumentoSistema.DOCUMENTO_JUSTIFICACION_ESTADO_FUENTE);
			
					listaDocumentosJustificacionEstadoFuenteHistorial = new ArrayList<Documento>();
					if(documentos != null && !documentos.isEmpty()){
				
						documentoJustificacionEstadoFuente = documentos.get(0);
				
						for(Documento documento : documentos){
							if(documento.getIdHistorico() != null){
								listaDocumentosJustificacionEstadoFuenteHistorial.add(documento);
							}							
						}
				
					}	
				}
			}
			if (detalleEmision.getTipoOperacionDetalleCatalogo() != null) {
				idTipoFuncionamiento = detalleEmision.getTipoOperacionDetalleCatalogo().getId();
			}
			if (detalleEmision.getFrecuenciaMonitoreo() != null) {
				idFrecuenciaMonitoreo = detalleEmision.getFrecuenciaMonitoreo().getId();
			}

			if (detalleEmision.getFechaInicioMonitoreo() != null) {
				String string = detalleEmision.getFechaInicioMonitoreo();
				String[] parts = string.split("-");
				mesInicio = parts[0];
				anioInicio = Integer.valueOf(parts[1]);
			}

			if (detalleEmision.getFechaFinMonitoreo() != null) {
				String string = detalleEmision.getFechaFinMonitoreo();
				String[] parts = string.split("-");
				mesFin = parts[0];
				anioFin = Integer.valueOf(parts[1]);
			}
			// DATOS NORMATIVA VIGENTE
			if (detalleEmision.getAutorizacionEmisiones() != null) {
				idAutorizacion = detalleEmision.getAutorizacionEmisiones().getId();				
			}
			
			listaDatosObtenidosTotal = datoObtenidoMedicionFacade.findByEmisionAtmosferica(detalleEmision);
			
			if(listaDatosObtenidosTotal != null && !listaDatosObtenidosTotal.isEmpty()){
				for(DatoObtenidoMedicion dato : listaDatosObtenidosTotal){
					if(dato.getLimiteMaximoPermitido().getValor().equals(0)){
						dato.setCumple("Cumple LMP");
					}else{
						if(dato.getValorCorregido() > dato.getLimiteMaximoPermitido().getValor()){
							dato.setCumple("No Cumple LMP");
						}else{
							dato.setCumple("Cumple LMP");
						}
					}					
				}
			}
			

			// DATOS DE LABORATORIO

			if (detalleEmision.getId() != null) {				
				
				listaLaboratorios = datosLaboratorioFacade.findByDetalleEmisiones(detalleEmision.getEmisionesAtmosfericas());

				if (listaLaboratorios != null && !listaLaboratorios.isEmpty()) {
					for (DatosLaboratorio dato : listaLaboratorios) {
						
						dato.setListaDocumentosLaboratorios(new ArrayList<Documento>());
						
						List<Documento> documentos = documentosFacade.documentoXTablaIdXIdDoc(dato.getId(),
										EmisionesAtmosfericas.class.getSimpleName(),
										TipoDocumentoSistema.TIPO_DOCUMENTO_INFORME_LABORATORIO);

						if (documentos != null && !documentos.isEmpty()){														
							dato.setListaDocumentosLaboratorios(documentos);														
						}
						
					}
				}

				// SUSTANCIAS RETCE
				listaSustanciasRetce = sustanciaRetceFacade.findByDetalleEmision(detalleEmision);

				if (listaSustanciasRetce != null && !listaSustanciasRetce.isEmpty()) {
					for (SubstanciasRetce sustancia : listaSustanciasRetce) {
						sustancia.setFuenteFija(detalleEmision.getFuenteFijaCombustion().getFuente());
						sustancia.setCodigoPuntoMonitoreo(detalleEmision.getCodigoPuntoMonitoreo());
					}					
				}
			}

			// tecnico responsable
			if (detalleEmision.getEmisionesAtmosfericas().getTecnicoResponsable() != null) {
				tecnicoResponsable = detalleEmision.getEmisionesAtmosfericas().getTecnicoResponsable();
			}
			
			//historial
			obtenerHistorial1();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<Documento> obtenerListaActuales(List<Documento> documentos){
		List<Documento> documentosActuales = new ArrayList<Documento>();
		if(documentos != null && !documentos.isEmpty()){
			for(Documento doc : documentos){
				if(doc.getIdHistorico() == null){
					documentosActuales.add(doc);
				}
			}	
		}																		
		return documentosActuales;		
	}
	
	public List<Documento> obtenerListaEliminados(List<Documento> documentos){
		List<Documento> documentosActuales = new ArrayList<Documento>();
		if(documentos != null && !documentos.isEmpty()){
			for(Documento doc : documentos){
				if(doc.getIdHistorico() != null){
					documentosActuales.add(doc);
				}
			}	
		}																		
		return documentosActuales;		
	}
	
	public void obtenerLaboratorioHistorial(List<DatosLaboratorio> laboratorio){
		listaLaboratoriosHistorial = new ArrayList<DatosLaboratorio>();
		listaLaboratoriosHistorial.addAll(laboratorio);
	}
		
	//obtiene el documento para descagar	
	public StreamedContent getDocumentoDownload(Documento documento){		
		try {
			if (documento != null && documento.getContenidoDocumento()!=null) {
				StreamedContent streamedContent = new DefaultStreamedContent(new ByteArrayInputStream(documento.getContenidoDocumento()),
						documento.getMime(), documento.getNombre());
				return streamedContent;
			}
		} catch (Exception e) {			
			e.printStackTrace();
		}
		return null;
	}
	
	public StreamedContent getDocumentoOficioAprobacion() throws CmisAlfrescoException{
		
		if (detalleEmision.getDocumentoOficioAprobacion() != null) {
			byte[] content = documentosFacade.descargar(oficioAprobacionDoc.getIdAlfresco());
			oficioAprobacionDoc.setContenidoDocumento(content);
		}
		
		return getDocumentoDownload(oficioAprobacionDoc);		
	}
	
	public StreamedContent obtenerDocumentoJustificacionEstadoFuente() throws CmisAlfrescoException{
		if(documentoJustificacionEstadoFuente != null){
			byte[] content = documentosFacade.descargar(documentoJustificacionEstadoFuente.getIdAlfresco());
			documentoJustificacionEstadoFuente.setContenidoDocumento(content);
		}
		return getDocumentoDownload(documentoJustificacionEstadoFuente);
	}
	
	
	public void cargarInformacionLaboratorio(DatosLaboratorio laboratorio){
		try {
			setDatoLaboratorio(laboratorio);
			idLaboratorio=datoLaboratorio.getId();
			setListaDocumentosLaboratorio(laboratorio.getListaDocumentosLaboratorios());
			/**
			 * verificar si existe documento adicional en documentacion de laboratorio
			 */
			documentoAdicional=documentosFacade.documentoXTablaIdXIdDocUnico(idLaboratorio,DatosLaboratorio.class.getSimpleName(),TipoDocumentoSistema.RETCE_ADICIONAL_LABORATORIO);
			documentoAdicionalLabora=(documentoAdicional!=null) ? true :  false ;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public StreamedContent getDocumentoInformeLaboratorio(Documento documento) throws CmisAlfrescoException{
		if(documento != null){
			byte[] content = documentosFacade.descargar(documento.getIdAlfresco());
			documento.setContenidoDocumento(content);
		}
		return getDocumentoDownload(documento);
	}
	
	public StreamedContent getDocumentoRespaldoLaboratorio(Documento documento) throws CmisAlfrescoException{
		if(documento != null){
			byte[] content = documentosFacade.descargar(documento.getIdAlfresco());
			documento.setContenidoDocumento(content);
		}
		return getDocumentoDownload(documento);
	}
	
	public StreamedContent obtenerDocumentoJustificativo() throws CmisAlfrescoException{
		if(documentoJustificativo != null){
			byte[] content = documentosFacade.descargar(documentoJustificativo.getIdAlfresco());
			documentoJustificativo.setContenidoDocumento(content);
		}
		return getDocumentoDownload(documentoJustificativo);
	}
	
	public void cerrar(){
		verFormulario=false;	
	}
	
	public boolean isTecnicoAutenticado() {
		boolean isTecnico = false;
		
		if (JsfUtil.getCurrentTask().getVariable("usuario_tecnico") != null) {
			String pinTecnico = JsfUtil.getCurrentTask().getVariable("usuario_tecnico").toString();
			if (pinTecnico != null && JsfUtil.getLoggedUser().getPin() != null && JsfUtil.getLoggedUser().getPin().equals(pinTecnico))
				isTecnico = true;
			else			
				isTecnico = false;			
		}
		
		
		return isTecnico;
	}
	
	public String getClassName() {
		return EmisionesAtmosfericas.class.getSimpleName();
	}

	private void obtenerHistorial1(){
		try {
			listaHistorialCaracteristicasFuente = new ArrayList<DetalleEmisionesAtmosfericas>();
			listaHistorialFuncionamientoFuente = new ArrayList<DetalleEmisionesAtmosfericas>();
			listaHistorialChimenea = new ArrayList<DetalleEmisionesAtmosfericas>();
			listaHistorialUbicacion = new ArrayList<DetalleEmisionesAtmosfericas>();
			listaHistorialEstadoFuente = new ArrayList<DetalleEmisionesAtmosfericas>();
			listaHistorialEmisionesAtmosfericas = new ArrayList<EmisionesAtmosfericas>();
			listaHistorialAutorizacion = new ArrayList<DetalleEmisionesAtmosfericas>();
			
			List<DetalleEmisionesAtmosfericas> listaHistorialCaracteristicasFuenteAux = new ArrayList<DetalleEmisionesAtmosfericas>();
			List<DetalleEmisionesAtmosfericas> listaHistorialFuncionamientoFuenteAux = new ArrayList<DetalleEmisionesAtmosfericas>();
			List<DetalleEmisionesAtmosfericas> listaHistorialEstadoFuenteAux = new ArrayList<DetalleEmisionesAtmosfericas>();	
			List<DetalleEmisionesAtmosfericas> listaHistorialChimeneaAux = new ArrayList<DetalleEmisionesAtmosfericas>();
			List<DetalleEmisionesAtmosfericas> listaHistorialUbicacionAux = new ArrayList<DetalleEmisionesAtmosfericas>();
			List<DetalleEmisionesAtmosfericas> listaHistorialAutorizacionAux = new ArrayList<DetalleEmisionesAtmosfericas>();
			
			List<DetalleEmisionesAtmosfericas> listaDetallesGeneralHistorico = detalleEmisionesAtmosfericasFacade.findByIdIdRegistroOriginal(detalleEmision.getId());
			
			if(listaDetallesGeneralHistorico != null && !listaDetallesGeneralHistorico.isEmpty()){
				for(DetalleEmisionesAtmosfericas detalleH : listaDetallesGeneralHistorico){
					
					//primera muestra de datos
					if(detalleEmision.getCodigoPuntoMonitoreo().equals(detalleH.getCodigoPuntoMonitoreo()) && 
							detalleEmision.getDocumentoOficioAprobacion().equals(detalleH.getDocumentoOficioAprobacion()) && 
							detalleEmision.getFuenteFijaCombustion().equals(detalleH.getFuenteFijaCombustion())){
						//iguales
					}else{
						listaHistorialCaracteristicasFuenteAux.add(detalleH);
					}										
					
					//segunda muestra de datos
					if(detalleEmision.getPotencia().equals(detalleH.getPotencia()) && 
							detalleEmision.getCatalogoUnidadesPotencia().equals(detalleH.getCatalogoUnidadesPotencia()) &&
							detalleEmision.getMarca().equals(detalleH.getMarca()) && 
							detalleEmision.getNumeroSerie().equals(detalleH.getNumeroSerie()) && 
							detalleEmision.getTipoCombustible().equals(detalleH.getTipoCombustible()) &&
							detalleEmision.getNumeroDuctosChimenea().equals(detalleH.getNumeroDuctosChimenea()) 
							){
						//iguales						
					}else{						
						listaHistorialFuncionamientoFuenteAux.add(detalleH);
					}
					
					//tercera muestra de datos
					if(detalleEmision.getAlturaLongitudChimenea().equals(detalleH.getAlturaLongitudChimenea()) && 
							detalleEmision.getDiametroChimenea().equals(detalleH.getDiametroChimenea()) && 
							detalleEmision.getUltimaPerturbacion().equals(detalleH.getUltimaPerturbacion()) && 
							detalleEmision.getCuspideChimenea().equals(detalleH.getCuspideChimenea())){
						//iguales
					}else{
						listaHistorialChimeneaAux.add(detalleH);
					}
					
					//cuarta muestra de datos
					if(detalleEmision.getCoordenadaX().equals(detalleH.getCoordenadaX()) && 
							detalleEmision.getCoordenadaY().equals(detalleH.getCoordenadaY()) && 
							detalleEmision.getLugarPuntoMuestreo().equals(detalleH.getLugarPuntoMuestreo())){
						//iguales
					}else{
						listaHistorialUbicacionAux.add(detalleH);
					}
								
					//quinta muestra de datos
					//SE TIENE QUE HACER EL HISTORIAL SI ES INACTIVA O ACTIVA PARA OBTENER TANTO DATOS SI ES ACTIVA COMO DATOS SI ES INACTIVA
					//Activa
					if(detalleH.getEstadoFuenteDetalleCatalogo().equals(detalleEmision.getEstadoFuenteDetalleCatalogo())){
						//iguales
						if(detalleH.getEstadoFuenteDetalleCatalogo().getDescripcion().equals("Activa")){
							if(detalleH.getConsumoCombustible().equals(detalleEmision.getConsumoCombustible()) && 
									detalleH.getCatalogoUnidadesCombustible().equals(detalleEmision.getCatalogoUnidadesCombustible()) && 
									detalleH.getHorasFuncionamiento().equals(detalleEmision.getHorasFuncionamiento()) && 
									detalleH.getTipoOperacionDetalleCatalogo().equals(detalleEmision.getTipoOperacionDetalleCatalogo()) && 
									detalleH.getVelocidadSalida().equals(detalleEmision.getVelocidadSalida())){
								//iguales
							}else{
								listaHistorialEstadoFuenteAux.add(detalleH);
							}							
						}else{
							if(detalleH.getJustificacion().equals(detalleEmision.getJustificacion())){
								//iguales									
							}else{
								listaHistorialEstadoFuenteAux.add(detalleH);
							}
						}						
					}else{						
						listaHistorialEstadoFuenteAux.add(detalleH);
					}	
					
					
					
					//Autorizacion
					if((detalleH.getAutorizacionEmisiones() == null && detalleEmision.getAutorizacionEmisiones() == null) ||
						(detalleH.getAutorizacionEmisiones() != null && detalleEmision.getAutorizacionEmisiones() != null && 
							detalleH.getAutorizacionEmisiones().equals(detalleEmision.getAutorizacionEmisiones()))){
						//iguales
					}else{
						listaHistorialAutorizacionAux.add(detalleH);
					}
					
				}				
			}
			
			//////////////////////////////////////////////////////////////////////////////////////////////////////////
			//listaHistorialEstadoFuente
			List<DetalleEmisionesAtmosfericas> listaCaracteristicasFuenteEstado = new ArrayList<DetalleEmisionesAtmosfericas>();
			for(DetalleEmisionesAtmosfericas detalleE : listaHistorialEstadoFuenteAux){
				if(listaCaracteristicasFuenteEstado.isEmpty()){
					listaCaracteristicasFuenteEstado.add(detalleE);
				}else{
					for(int i= 0; i < listaCaracteristicasFuenteEstado.size(); i++){
						DetalleEmisionesAtmosfericas detalleC = listaCaracteristicasFuenteEstado.get(i);
						if((detalleE.getConsumoCombustible() == null && detalleC.getConsumoCombustible() == null) ||
								(detalleE.getConsumoCombustible() != null && detalleC.getConsumoCombustible() != null && 
								detalleE.getConsumoCombustible().equals(detalleC.getConsumoCombustible())) && 
								((detalleE.getCatalogoUnidadesCombustible() == null && detalleC.getCatalogoUnidadesCombustible() == null) ||
								(detalleE.getCatalogoUnidadesCombustible() != null && detalleC.getCatalogoUnidadesCombustible() != null && 
								detalleE.getCatalogoUnidadesCombustible().equals(detalleC.getCatalogoUnidadesCombustible()))) && 
								((detalleE.getHorasFuncionamiento() == null && detalleC.getHorasFuncionamiento() == null) ||
								(detalleE.getHorasFuncionamiento() != null && detalleC.getHorasFuncionamiento() != null && 
								detalleE.getHorasFuncionamiento().equals(detalleC.getHorasFuncionamiento())))){
								break;
						}else{
							listaCaracteristicasFuenteEstado.add(detalleE);
						}
					}
				}
			}
			
			listaHistorialEstadoFuente.addAll(listaCaracteristicasFuenteEstado);
			
			//-----------------------------------------------------------------------------------------------------------------//
			//listaHistorialFuncionamientoFuenteAux
			List<DetalleEmisionesAtmosfericas> listaHistorialFuncionamientoFuenteComp = new ArrayList<DetalleEmisionesAtmosfericas>();
			
			for(DetalleEmisionesAtmosfericas detalleE : listaHistorialFuncionamientoFuenteAux){
				if(listaHistorialFuncionamientoFuenteComp.isEmpty()){
					listaHistorialFuncionamientoFuenteComp.add(detalleE);
				}else{
					for(int i = 0; i < listaHistorialFuncionamientoFuenteComp.size(); i++){
						DetalleEmisionesAtmosfericas detalleC = listaHistorialFuncionamientoFuenteComp.get(i);
						if(detalleE.getPotencia().equals(detalleC.getPotencia()) && 
								detalleE.getCatalogoUnidadesPotencia().equals(detalleC.getCatalogoUnidadesPotencia()) &&
								detalleE.getMarca().equals(detalleC.getMarca()) && 
								detalleE.getNumeroSerie().equals(detalleC.getNumeroSerie()) && 
								detalleE.getTipoCombustible().equals(detalleC.getTipoCombustible()) &&
								detalleE.getNumeroDuctosChimenea().equals(detalleC.getNumeroDuctosChimenea()) 
								){
							break;						
						}else{						
							listaHistorialFuncionamientoFuenteComp.add(detalleE);
						}						
					}
				}
			}
			
			listaHistorialFuncionamientoFuente.addAll(listaHistorialFuncionamientoFuenteComp);
			
			//-----------------------------------------------------------------------------------------------------------------//
			//listaHistorialCaracteristicasFuente 
			List<DetalleEmisionesAtmosfericas> listaCaracteristicasFuenteCar = new ArrayList<DetalleEmisionesAtmosfericas>();
			for(DetalleEmisionesAtmosfericas detalleC : listaHistorialCaracteristicasFuenteAux){
				if(listaCaracteristicasFuenteCar.isEmpty()){
					listaCaracteristicasFuenteCar.add(detalleC);
				}else{
					for(int i = 0; i< listaCaracteristicasFuenteCar.size(); i ++){
						DetalleEmisionesAtmosfericas detalleE = listaCaracteristicasFuenteCar.get(i);
						if(detalleEmision.getCodigoPuntoMonitoreo().equals(detalleE.getCodigoPuntoMonitoreo()) && 
								detalleEmision.getDocumentoOficioAprobacion().equals(detalleE.getDocumentoOficioAprobacion()) && 
								detalleEmision.getFuenteFijaCombustion().equals(detalleE.getFuenteFijaCombustion())){
							break;
						}else{
							listaCaracteristicasFuenteCar.add(detalleC);
						}										
					}
				}
			}
			
			listaHistorialCaracteristicasFuente.addAll(listaCaracteristicasFuenteCar);
			//-----------------------------------------------------------------------------------------------------------------//
			List<DetalleEmisionesAtmosfericas> listaHistorialChimeneaCar = new ArrayList<DetalleEmisionesAtmosfericas>();
			for(DetalleEmisionesAtmosfericas detalleC : listaHistorialChimeneaAux){
				if(listaHistorialChimeneaCar.isEmpty()){
					listaHistorialChimeneaCar.add(detalleC);
				}else{
					for(int i = 0; i < listaHistorialChimeneaCar.size(); i++){
						DetalleEmisionesAtmosfericas detalleE = listaHistorialChimeneaCar.get(i);
						
						if(detalleC.getAlturaLongitudChimenea().equals(detalleE.getAlturaLongitudChimenea()) && 
								detalleC.getDiametroChimenea().equals(detalleE.getDiametroChimenea()) && 
								detalleC.getUltimaPerturbacion().equals(detalleE.getUltimaPerturbacion()) && 
								detalleC.getCuspideChimenea().equals(detalleE.getCuspideChimenea())){
							break;
						}else{
							listaHistorialChimeneaCar.add(detalleC);
						}						
					}
				}
			}
			listaHistorialChimenea.addAll(listaHistorialChimeneaCar);
			
			//----------------------------------------------------------------------------------------------------------------//
			List<DetalleEmisionesAtmosfericas> listaHistorialUbicacionCar = new ArrayList<DetalleEmisionesAtmosfericas>();
			for(DetalleEmisionesAtmosfericas detalleC : listaHistorialUbicacionAux){
				if(listaHistorialUbicacionCar.isEmpty()){
					listaHistorialUbicacionCar.add(detalleC);
				}else{
					for(int i = 0; i < listaHistorialUbicacionCar.size(); i++){
						DetalleEmisionesAtmosfericas detalleE = listaHistorialUbicacionCar.get(i);
						
						if(detalleC.getCoordenadaX().equals(detalleE.getCoordenadaX()) && 
								detalleC.getCoordenadaY().equals(detalleE.getCoordenadaY()) && 
								detalleC.getLugarPuntoMuestreo().equals(detalleE.getLugarPuntoMuestreo())){
							break;
						}else{
							listaHistorialUbicacionCar.add(detalleC);
						}						
					}
				}
			}
			
			listaHistorialUbicacion.addAll(listaHistorialUbicacionCar);
			
			//-----------------------------------------------------------------------------------------------------------------//
			List<DetalleEmisionesAtmosfericas> listaHistorialAutorizacionCar = new ArrayList<DetalleEmisionesAtmosfericas>();
			for(DetalleEmisionesAtmosfericas detalleC : listaHistorialAutorizacionAux){
				if(listaHistorialAutorizacionCar.isEmpty()){
					listaHistorialAutorizacionCar.add(detalleC);
				}else{
					for(int i = 0; i < listaHistorialAutorizacionCar.size(); i++){
						DetalleEmisionesAtmosfericas detalleE = listaHistorialAutorizacionCar.get(i);
						
						if((detalleC.getAutorizacionEmisiones() == null && detalleE.getAutorizacionEmisiones() == null) ||
								(detalleC.getAutorizacionEmisiones() != null && detalleE.getAutorizacionEmisiones() != null && 
										detalleC.getFrecuenciaMonitoreo().equals(detalleE.getFrecuenciaMonitoreo()) && 
										detalleC.getFechaInicioMonitoreo().equals(detalleE.getFechaInicioMonitoreo()) && 
										detalleC.getFechaFinMonitoreo().equals(detalleE.getFechaFinMonitoreo()) &&
										detalleC.getAutorizacionEmisiones().equals(detalleE.getAutorizacionEmisiones()))){
							break;
						}else{
							listaHistorialAutorizacionCar.add(detalleC);
						}						
					}
				}
			}
			listaHistorialAutorizacion.addAll(listaHistorialAutorizacionCar);
			
			
			////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			
			//facilidades de monitoreo
			List<FacilidadesMonitoreoEmisiones> listaFacilidadesH = facilidadesMonitoreoEmisionesFacade.findDetalleH(detalleEmision);
			
			if(listaFacilidadesH == null){
				listaFacilidadesHistorial = new ArrayList<FacilidadesMonitoreoEmisiones>();
			}else{
				listaFacilidadesHistorial = new ArrayList<FacilidadesMonitoreoEmisiones>();
				
				if(detalleEmision.getNumeroObservacion() == null || detalleEmision.getNumeroObservacion() < numeroObservacion){
					for(FacilidadesMonitoreoEmisiones facilidades : listaFacilidadesH){					
						if(facilidades.getIdRegistroOriginal() == null && facilidades.getNumeroObservacion() != null && 
								facilidades.getNumeroObservacion().equals(numeroObservacion)){
							facilidades.setNuevoEnModificacion(true);
							listaFacilidadesHistorial.add(facilidades);
						}else if(facilidades.getIdRegistroOriginal() != null){
							listaFacilidadesHistorial.add(facilidades);
						}
					}
				}			
			}
			
			//Emisiones Atmosfericas
			
			//Datos obtenidos
			listaHistorialDatosObtenidos = new ArrayList<DatoObtenidoMedicion>();
			listaHistorialDatosObtenidosEliminados = new ArrayList<DatoObtenidoMedicion>();
			List<DatoObtenidoMedicion> listaDatosObtenidosBdd = datoObtenidoMedicionFacade.findByDetalleEmisionAtmosfericaTotal(detalleEmision);
							
			if(listaDatosObtenidosBdd != null && !listaDatosObtenidosBdd.isEmpty()){
				for(DatoObtenidoMedicion dato : listaDatosObtenidosBdd){
					
					Comparator<DatoObtenidoMedicion> c = new Comparator<DatoObtenidoMedicion>() {
						
						@Override
						public int compare(DatoObtenidoMedicion o1, DatoObtenidoMedicion o2) {							
							return o1.getId().compareTo(o2.getId());
						}
					};
					
					Collections.sort(listaDatosObtenidosTotal, c);
					
					int index = Collections.binarySearch(listaDatosObtenidosTotal, new DatoObtenidoMedicion(dato.getIdRegistroOriginal()), c);
					
					if(index >= 0){
						listaHistorialDatosObtenidos.add(dato);
						listaDatosObtenidosTotal.get(index).setRegistroModificado(true);
					}else{
						listaHistorialDatosObtenidosEliminados.add(dato);
					}
				}
				
				if(listaHistorialDatosObtenidosEliminados != null && !listaHistorialDatosObtenidosEliminados.isEmpty()){
					for(DatoObtenidoMedicion dato : listaHistorialDatosObtenidosEliminados){
						if(dato.getLimiteMaximoPermitido().getValor().equals(0)){
							dato.setCumple("Cumple LMP");
						}else{
							if(dato.getValorCorregido() > dato.getLimiteMaximoPermitido().getValor()){
								dato.setCumple("No Cumple LMP");
							}else{
								dato.setCumple("Cumple LMP");
							}
						}
					}
				}				
			}else{
				listaHistorialDatosObtenidos = new ArrayList<DatoObtenidoMedicion>();
				listaHistorialDatosObtenidosEliminados = new ArrayList<DatoObtenidoMedicion>();
			}
			
			for(DatoObtenidoMedicion datoActual : listaDatosObtenidosTotal){
				if(datoActual.getNumeroObservacion() != null && 
						datoActual.getNumeroObservacion().equals(numeroObservacion)){
					
					datoActual.setNuevoEnModificacion(true);
				}
			}		
			
			
			//LABORATORIOS
			List<DatosLaboratorio> laboratoriosBdd = datosLaboratorioFacade.findByEmisionHistorial(detalleEmision.getEmisionesAtmosfericas());
			List<DatosLaboratorio> laboratoriosBddAux = new ArrayList<DatosLaboratorio>();
			listaLaboratoriosEliminados = new ArrayList<DatosLaboratorio>();
			if(laboratoriosBdd == null){
				listaLaboratoriosEliminados = new ArrayList<DatosLaboratorio>();
			}else{
				laboratoriosBddAux.addAll(laboratoriosBdd);
				for(DatosLaboratorio lab : listaLaboratorios){
					for(DatosLaboratorio labE : laboratoriosBdd){
						if(lab.getId().equals(labE.getIdRegistroOriginal())){
							laboratoriosBddAux.remove(labE);
						}
					}
				}
				
				if(laboratoriosBddAux.isEmpty()){
					listaLaboratoriosEliminados = new ArrayList<DatosLaboratorio>();
				}else
					listaLaboratoriosEliminados.addAll(laboratoriosBddAux);
			}
			
			for(DatosLaboratorio dato : listaLaboratoriosEliminados){
				dato.setListaDocumentosLaboratorios(new ArrayList<Documento>());
				
				List<Documento> documentos = documentosFacade.documentoXTablaIdXIdDoc(dato.getIdRegistroOriginal(),
								EmisionesAtmosfericas.class.getSimpleName(),
								TipoDocumentoSistema.TIPO_DOCUMENTO_INFORME_LABORATORIO);

				if (documentos != null && !documentos.isEmpty()){														
					dato.setListaDocumentosLaboratorios(documentos);														
				}
			}
			
			//SUSTANCIAS RETCE
			List<SubstanciasRetce> sustanciasBdd = sustanciaRetceFacade.findByDetalleEmisionHistorial(detalleEmision);
			
			List<SubstanciasRetce> sustanciasBddAux = new ArrayList<SubstanciasRetce>();
			listaSustanciasRetceEliminadas = new ArrayList<SubstanciasRetce>();
			
			if(sustanciasBdd != null && !sustanciasBdd.isEmpty()){
				sustanciasBddAux.addAll(sustanciasBdd);
				
				for(SubstanciasRetce sustancia : listaSustanciasRetce){
					for(SubstanciasRetce sustanciaH : sustanciasBdd){
						if(sustancia.getId().equals(sustanciaH.getIdRegistroOriginal())){
							sustanciasBddAux.remove(sustanciaH);
						}
					}
				}
				
				if(!sustanciasBddAux.isEmpty()){
					listaSustanciasRetceEliminadas.addAll(sustanciasBddAux);
				}
			}	
			
			//TECNICO QUE REPORTA
			//findHistoricoByNumeroRevision
			listaTecnicosHistorial = new ArrayList<TecnicoResponsable>();
			
			for(int i = 1; i <= numeroObservacion; i++){
				TecnicoResponsable tecnicoHis = tecnicoResponsableFacade.findByIdRegistroOriginal(tecnicoResponsable.getId(), i);
				if(tecnicoHis != null){
					listaTecnicosHistorial.add(tecnicoHis);
				}				
			}						
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public StreamedContent getDocumento(Documento documento) throws CmisAlfrescoException{
		if(documento != null){
			byte[] content = documentosFacade.descargar(documento.getIdAlfresco());
			documento.setContenidoDocumento(content);
		}
		return getDocumentoDownload(documento);
	}
	

	
	public void obtenerDato(DatoObtenidoMedicion datoHistorial){	
		listaIndividualHistorial = new ArrayList<DatoObtenidoMedicion>();
		for(DatoObtenidoMedicion dato : listaHistorialDatosObtenidos){
			if(dato.getIdRegistroOriginal().equals(datoHistorial.getId())){
				if(dato.getLimiteMaximoPermitido().getValor().equals(0)){
					dato.setCumple("Cumple LMP");
				}else{
					if(dato.getValorCorregido() > dato.getLimiteMaximoPermitido().getValor()){
						dato.setCumple("No Cumple LMP");
					}else{
						dato.setCumple("Cumple LMP");
					}
				}
				
				listaIndividualHistorial.add(dato);
			}
		}		
	}
	
	public void obtenerDocLabEliminado(List<Documento> documentos){
		listaDocumentosLaboratorioEliminados = new ArrayList<Documento>();
		listaDocumentosLaboratorioEliminados.addAll(documentos);
	}
	
	public void obtenerSustanciasRecteHis(List<SubstanciasRetce> sustanciasRetce){
		listaSustanciasRetceHistorial = new ArrayList<SubstanciasRetce>();
		listaSustanciasRetceHistorial.addAll(sustanciasRetce);
	}
	
	public void obtenerSustanciasEliminadas(){
		try {			
			listaSustanciasRetceHistorial = new ArrayList<SubstanciasRetce>();
			listaSustanciasRetceHistorial.addAll(listaSustanciasRetceEliminadas);	
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	public void eliminarLaboratorio(DatosLaboratorio laboratorio){
		if(listaSustanciasRetce != null && !listaSustanciasRetce.isEmpty()){
			List<SubstanciasRetce> listaAux = new ArrayList<SubstanciasRetce>();
			listaAux.addAll(listaSustanciasRetce);
			for(SubstanciasRetce sustancia : listaAux){
				if(sustancia.getDatosLaboratorio() != null){
					if(sustancia.getDatosLaboratorio().equals(laboratorio)){
						listaSustanciasRetceEliminadas.add(sustancia);
						listaSustanciasRetce.remove(sustancia);
					}
				}
			}
		}		
		
		listaLaboratoriosEliminados.add(laboratorio);
		listaLaboratorios.remove(laboratorio);
	}
	public void validarLaboratorioListener(){
		
		String cedulaRuc=datoLaboratorio.getRuc();		
		datoLaboratorio=new DatosLaboratorio();		
		if(cedulaRuc.length()==13 && JsfUtil.validarCedulaORUC(cedulaRuc))
		{
			for (DatosLaboratorio labIngresado : listaLaboratorios) {
				if(labIngresado.getRuc().compareTo(cedulaRuc)==0)
				{
					JsfUtil.addMessageWarning("Laboratorio ya ingresado");
					return;
				}
			}
			
			try {
				Organizacion orga=organizacionFacade.buscarPorRuc(cedulaRuc);
				if(orga!=null)
				{
					datoLaboratorio.setRuc(orga.getRuc());
					datoLaboratorio.setNombre(orga.getNombre());
					return;
				}
				
				ContribuyenteCompleto contribuyenteCompleto = consultaRucCedula
		                .obtenerPorRucSRI(
		                        Constantes.USUARIO_WS_MAE_SRI_RC,
		                        Constantes.PASSWORD_WS_MAE_SRI_RC,
		                        cedulaRuc);
				if(contribuyenteCompleto!=null)
				{
					datoLaboratorio.setRuc(contribuyenteCompleto.getNumeroRuc());
					datoLaboratorio.setNombre(contribuyenteCompleto.getRazonSocial());
									
				}
				else
				{
					JsfUtil.addMessageError("Error al validar Ruc, Servicio Web No Disponible");
					return;
				}
			} catch (Exception e) {
				JsfUtil.addMessageError("Error al validar Ruc");
				LOG.error(e.getMessage());
				return;
			}
		}
		
		if(datoLaboratorio.getRuc()==null)
		{
			JsfUtil.addMessageError("Ruc no válido");
		}		
	}
	public StreamedContent getDocumentoInformeLaboratorio() {
		try {
			informeLaboratorio=documentosFacade.documentoXTablaIdXIdDocUnico(idLaboratorio,DatosLaboratorio.class.getSimpleName(),TipoDocumentoSistema.TIPO_DOCUMENTO_INFORME_LABORATORIO); 
			if(informeLaboratorio!=null)
				informeLaboratorio.setContenidoDocumento(documentosFacade.descargar(informeLaboratorio.getIdAlfresco()));
			else
				informeLaboratorio=new Documento();
			
		} catch (Exception e) {
			LOG.error(e);
		}
		return UtilDocumento.getStreamedContent(informeLaboratorio);
	}
	public StreamedContent getDocumentoRegistroLaboratorio() {
		try {
			registroLaboratorio=documentosFacade.documentoXTablaIdXIdDocUnico(idLaboratorio,DatosLaboratorio.class.getSimpleName(),TipoDocumentoSistema.RETCE_SAE_LABORATORIO); 
			if(registroLaboratorio!=null)
				informeLaboratorio.setContenidoDocumento(documentosFacade.descargar(registroLaboratorio.getIdAlfresco()));
			else
				registroLaboratorio=new Documento();
			
		} catch (Exception e) {
			LOG.error(e);
		}
		return UtilDocumento.getStreamedContent(registroLaboratorio);
	}
	public StreamedContent getDocumentoCadenaCustodia() {
		try {
			cadenaCustodia=documentosFacade.documentoXTablaIdXIdDocUnico(idLaboratorio,DatosLaboratorio.class.getSimpleName(),TipoDocumentoSistema.RETCE_CADENA_CUSTODIA); 
			if(cadenaCustodia!=null)
				cadenaCustodia.setContenidoDocumento(documentosFacade.descargar(cadenaCustodia.getIdAlfresco()));
			else
				cadenaCustodia=new Documento();
			
		} catch (Exception e) {
			LOG.error(e);
		}		
		return UtilDocumento.getStreamedContent(cadenaCustodia);
	}
	public StreamedContent getDocumentoProtocoloMuestro() {
		try {
			protocoloMuestro=documentosFacade.documentoXTablaIdXIdDocUnico(idLaboratorio,DatosLaboratorio.class.getSimpleName(),TipoDocumentoSistema.RETCE_PROTOCOLO_MUESTREO); 
			if(protocoloMuestro!=null)
				protocoloMuestro.setContenidoDocumento(documentosFacade.descargar(protocoloMuestro.getIdAlfresco()));
			else
				protocoloMuestro=new Documento();
			
		} catch (Exception e) {
			LOG.error(e);
		}		
		return UtilDocumento.getStreamedContent(protocoloMuestro);
	}
	public StreamedContent getDocumentoAdicional() {
		try {
			documentoAdicional=documentosFacade.documentoXTablaIdXIdDocUnico(idLaboratorio,DatosLaboratorio.class.getSimpleName(),TipoDocumentoSistema.RETCE_ADICIONAL_LABORATORIO); 
			if(documentoAdicional!=null)
				documentoAdicional.setContenidoDocumento(documentosFacade.descargar(documentoAdicional.getIdAlfresco()));
			else
				documentoAdicional=new Documento();
			
		} catch (Exception e) {
			LOG.error(e);
		}
		return UtilDocumento.getStreamedContent(documentoAdicional);
	}
}
