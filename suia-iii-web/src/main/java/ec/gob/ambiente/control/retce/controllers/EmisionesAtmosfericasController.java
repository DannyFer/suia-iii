package ec.gob.ambiente.control.retce.controllers;

import index.Intersecado_capa;
import index.Intersecado_coordenada;
import index.Intersecado_entrada;
import index.Intersecado_resultado;
import index.Reproyeccion_entrada;
import index.Reproyeccion_resultado;
import index.SVA_Reproyeccion_IntersecadoPortTypeProxy;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import javax.servlet.ServletContext;
import javax.xml.ws.WebServiceException;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;
import org.primefaces.component.wizard.Wizard;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

import ec.gob.ambiente.cliente.ConsultaRucCedula;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.mapa.webservices.GenerarMapaImagenWS_Service;
import ec.gob.ambiente.mapa.webservices.GenerarMapaWSService;
import ec.gob.ambiente.mapa.webservices.ResponseImagen;
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
import ec.gob.ambiente.retce.model.FaseRetce;
import ec.gob.ambiente.retce.model.FuenteFijaCombustion;
import ec.gob.ambiente.retce.model.InformacionProyecto;
import ec.gob.ambiente.retce.model.LimiteMaximoPermitido;
import ec.gob.ambiente.retce.model.OficioPronunciamientoRetce;
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
import ec.gob.ambiente.retce.services.FaseRetceFacade;
import ec.gob.ambiente.retce.services.FuenteFijaCombustionFacade;
import ec.gob.ambiente.retce.services.InformacionProyectoFacade;
import ec.gob.ambiente.retce.services.LimiteMaximoPermitidoFacade;
import ec.gob.ambiente.retce.services.OficioRetceFacade;
import ec.gob.ambiente.retce.services.SubstanciasRetceFacade;
import ec.gob.ambiente.retce.services.TecnicoResponsableFacade;
import ec.gob.ambiente.retce.services.TipoCombustibleRetceFacade;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comun.bean.WizardBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.prevencion.planemergente.service.TipoSectorFaseService;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilFichaMineria;
import ec.gov.sri.wsconsultacontribuyente.ContribuyenteCompleto;

@ManagedBean
@ViewScoped
public class EmisionesAtmosfericasController {
	
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
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private AreaFacade areaFacade;
	@EJB
	private ContactoFacade contactoFacade;
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	@EJB
	private OficioRetceFacade oficioRetceFacade;
	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;
	@EJB
	private FaseRetceFacade faseRetceFacade;
	
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
	private Documento oficioAprobacionDoc, documentoJustificacionEstadoFuente, documentoInformeLaboratorio, 
						documentoRespaldoSustancias, documentoJustificativo, 
						oficioAprobacionDocHistorico, documentoJustificativoHistorico, 
						documentoJustificacionEstadoFuenteHistorico;
	
	@Getter
	@Setter
	private EmisionesAtmosfericas emisionesAtmosfericas, emisionesAtmosfericasOriginal;
	
	@Getter
	@Setter
	private DetalleEmisionesAtmosfericas detalleEmision, detalleEmisionOriginal;
	
	@Getter
	@Setter
	private FuenteFijaCombustion fuenteFijaCombustion;
	
	@Getter
	@Setter
	private List<FuenteFijaCombustion> listaFuentesFijasCombustion;
	
	@Getter
	@Setter
	private List<DetalleEmisionesAtmosfericas> listaDetalleEmisionesAtmosfericas, listaNuevaDetalleEmisionesAtmosfericas;
	
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
	private List<FaseRetce> listaFaseRetce;
	
	@Getter
	@Setter 
	private FaseRetce faseRetce;
	
	@Getter
	@Setter
	private Integer idFuente, idTipoCombustible, idAutorizacion,
			idMetodoEstimacionParametro, idEstadoFuenteDetalleCatalogo,
			idUnidadPotencia, idFrecuenciaMonitoreo, idTipoFuncionamiento,idFase,
			idUnidadCombustible, idTipoFuenteRetce, idCatalogoSusRetce, idMetodoEstimacionSustancia;
	
	@Getter
	@Setter
	private List<DetalleCatalogoGeneral> listaCatalogoEstadoFuente, listaCatalogoTipoFuncionamiento, listaCatalogoConstantes;
	
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
	private List<DatoObtenidoMedicion> listaDatosObtenidos, listaDatosObtenidosTotal, listaDatosTotalAux, listaDatosObtenidosAux;
	
	@Getter
	@Setter
	private DatoObtenidoMedicion datoObtenido;
	
	@Getter
	@Setter
	private List<CatalogoMetodoEstimacion> listaMetodoEstimacion;
	
	@Getter
	@Setter
	private boolean mostrarEstadoFuente = true, deshabilitarTipoCombustible = false, habiliatFinalizar=false, mostrarLaboratorioFuentes;
	
	@Getter
    @Setter
    private boolean permitirContinuar, editarFuente=true;
	
	@Getter
    @Setter
    private boolean guardado, masReportes=false;
	
	@Getter
	@Setter
	private InformacionProyecto informacionProyecto;
	
	@Getter
	@Setter
	private List<FacilidadesMonitoreoEmisiones> listaFacilidadesMonitoreoEmisiones;
	
	private String mesLista[] = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
	
	@Getter
	@Setter
	private List<String> listaMeses;
	
	@Getter
	@Setter List<Integer> listaAnios;
	
	@Getter
	@Setter
	private String mesInicio, mesFin;
	
	@Getter
	@Setter
	private Integer anioInicio, anioFin;
	
	@Getter
	@Setter
	private List<DatosLaboratorio> listaLaboratorios, listaLaboratoriosEliminados;
	
	@Getter
	@Setter
	private DatosLaboratorio datoLaboratorio;
	
	@Getter
	@Setter
	private List<SubstanciasRetce> listaSustanciasRetce, listaSustanciasRetceEliminadas;
	
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
	private List<EmisionesAtmosfericas> listaEmisionesAtmosfericas, listaEmisionesAtmosfericasHistorial;
	
	@Getter       
    private String nombreUsuario,representanteLegal;  
	
	@Getter
	private boolean verFormulario, nuevoReporte;
	
	@Setter
	@Getter
	private UploadedFile fileOficioAprobacion;
	
	@Getter
	@Setter
	private CatalogoMetodoEstimacion metodoEstimacion;
	
	@Getter
	@Setter
	private List<Documento> listaDocumentosLaboratorio, listaDocumentosLaboratoriosEliminados;
	
	//variables de flujo
	private String tramite;
	
	private Map<String, Object> variables;
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@Getter
	@Setter
	private boolean existeObservaciones = false, mostrarHoras = false;
	
	@Getter
	@Setter
	private Integer numeroObservacion;
	
	//fin variables flujo
	
	@Getter
	@Setter
	private String nombreOperador, cedulaOperador, mensajeResponsabilidad;
	
	
	@Getter
	@Setter
	private Documento documento,imagenCoordenadas;
	
	private String sector;
	
	private boolean ingresoDetalle;
	
	@Getter
	@Setter
	private boolean mostrarLimites,mostrarHME=false;
	
	@Getter
	@Setter
	private Boolean excedeLimiteFuente,limiteValidar=false;
	
	@Getter
	@Setter
    private String documentoTipo1, documentoTipo2, documentoTipo3, documentoTipo4, documentoTipo5;
	
	private GenerarMapaImagenWS_Service wsMapas;
	
	@PostConstruct
	public void init(){
		try {
			
			Integer idInformacionBasica =(Integer)(JsfUtil.devolverObjetoSession(InformacionProyecto.class.getSimpleName()));
			if(idInformacionBasica!=null){
				informacionProyecto=informacionProyectoFacade.findById(idInformacionBasica);
				JsfUtil.cargarObjetoSession(InformacionProyecto.class.getSimpleName(), null);
				numeroObservacion = 0;
			}else{
				variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
				tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
				
				if(tramite != null){
					listaEmisionesAtmosfericas = emisionesAtmosfericasFacade.findByEmision(tramite);
					informacionProyecto = listaEmisionesAtmosfericas.get(0).getInformacionProyecto();
					existeObservaciones = true;
					numeroObservacion = Integer.valueOf(variables.get("numero_observaciones").toString());
				}else{
					numeroObservacion = 0;
				}
			}
												
			emisionesAtmosfericas = new EmisionesAtmosfericas();
			listaEmisionesAtmosfericas = new ArrayList<EmisionesAtmosfericas>();
			buscarDatosOperador();
			MensajeResponsabilidadRetceController mensajeResponsabilidadRetceController = JsfUtil.getBean(MensajeResponsabilidadRetceController.class);
			mensajeResponsabilidad = mensajeResponsabilidadRetceController.mensajeResponsabilidadRetce(informacionProyecto.getUsuarioCreacion());
			listaNuevaDetalleEmisionesAtmosfericas = new ArrayList<DetalleEmisionesAtmosfericas>();
			listaDetalleEmisionesAtmosfericas = new ArrayList<DetalleEmisionesAtmosfericas>();	
			if(!existeObservaciones){
				listaEmisionesAtmosfericas = emisionesAtmosfericasFacade.findByInformacionProyecto(informacionProyecto);
				
				if(listaEmisionesAtmosfericas != null && !listaEmisionesAtmosfericas.isEmpty()){
					
					for(EmisionesAtmosfericas emision : listaEmisionesAtmosfericas){
						OficioPronunciamientoRetce oficio = oficioRetceFacade.getOficio(emision.getCodigo(), TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_EMISION_ATMOSFERICA);
						
						if(oficio != null){
							List<Documento> documentos = documentosFacade.documentoXTablaIdXIdDoc(oficio.getId(),
				            		"OficioEmisionesAtmosfericas", TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_EMISION_ATMOSFERICA);
				            if (documentos.size() > 0) {
				                documento = documentos.get(0);
				                emision.setOficio(documento);
				            }
						}
					}										
				}			
			}else{				
				listaEmisionesAtmosfericas = emisionesAtmosfericasFacade.findByEmision(tramite);
				informacionProyecto = listaEmisionesAtmosfericas.get(0).getInformacionProyecto();				
			}
			
			sector = informacionProyecto.getTipoSector().getNombre();
			mostrarHME= (informacionProyecto.getTipoSector().getNombre().contains("Otros")) ? false:true ;
			listaFaseRetce=new ArrayList<FaseRetce>();
			listaFaseRetce=faseRetceFacade.findByTipoSector(informacionProyecto.getTipoSector());
			imagenCoordenadas = new Documento();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	public void validarDocumentoAprobacionMonitoreo(FacesContext context, UIComponent validate, Object value) {
		validarDocumento(oficioAprobacionDoc,"Debe adjuntar Oficio de aprobación del punto de monitoreo.");
	}
	
	public void validarDocumentoJustificacionEstadoFuente(FacesContext context, UIComponent validate, Object value) {
		validarDocumento(documentoJustificacionEstadoFuente,"Debe adjuntar el documento de justificación.");
	}
	
	private void validarDocumento(Documento documento,String mensaje) {
		List<FacesMessage> errorMessages = new ArrayList<>();		
		if (documento==null || documento.getContenidoDocumento()==null) {
			errorMessages.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							mensaje, null));
		}		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}

	public void guardarReporte(){
		nuevoReporte=false;
		if(emisionesAtmosfericas.getId() != null){
			return;
		}
		emisionesAtmosfericas.setInformacionProyecto(informacionProyecto);
		emisionesAtmosfericasFacade.save(emisionesAtmosfericas, loginBean.getUsuario());
		for (DetalleEmisionesAtmosfericas objDetalle : listaNuevaDetalleEmisionesAtmosfericas) {
			objDetalle.setDetalleOriginalId(objDetalle.getId());
			detalleEmisionesAtmosfericasFacade.save(objDetalle, loginBean.getUsuario());
			// creo el nuevo detalle para el Nuevo reporte
			objDetalle.setId(null);
			objDetalle.setConsumoCombustible(null);
			objDetalle.setCatalogoUnidadesCombustible(null);
			objDetalle.setHorasFuncionamiento(null);
			objDetalle.setEstadoFuenteDetalleCatalogo(null);
			objDetalle.setTipoOperacionDetalleCatalogo(null);
			objDetalle.setVelocidadSalida(null);
			objDetalle.setFechaFinMonitoreo(null);
			objDetalle.setFechaInicioMonitoreo(null);
			objDetalle.setEmisionesAtmosfericas(emisionesAtmosfericas);
			detalleEmisionesAtmosfericasFacade.save(objDetalle, loginBean.getUsuario());
		}
		ocultarFormulario();
	}
	
	public void cancelarReporte(){
		nuevoReporte=false;
		listaNuevaDetalleEmisionesAtmosfericas = new ArrayList<DetalleEmisionesAtmosfericas>();
	}
	
	public void agregarReporte(){
		nuevoReporte=true;
		emisionesAtmosfericas = new EmisionesAtmosfericas();
		EmisionesAtmosfericas emisionesAtmosfericasAux = new EmisionesAtmosfericas();
		listaNuevaDetalleEmisionesAtmosfericas = new ArrayList<DetalleEmisionesAtmosfericas>();
		// busco el primer reporte de las emisiones atmosfericas
		List<EmisionesAtmosfericas> listaEmisionesAtmosfericasAux = emisionesAtmosfericasFacade.findByInformacionProyecto(informacionProyecto);
		if(listaEmisionesAtmosfericasAux != null && listaEmisionesAtmosfericasAux.size() > 0){
			for (EmisionesAtmosfericas objEmisiones : listaEmisionesAtmosfericasAux) {
				emisionesAtmosfericasAux = objEmisiones;
			}
			listaNuevaDetalleEmisionesAtmosfericas = detalleEmisionesAtmosfericasFacade.findByEmisionesAtmosfericas(emisionesAtmosfericasAux);	
		}else{
			agregarDetalle(emisionesAtmosfericas);
			nuevoReporte=false;
		}
	}
	
	public void agregarDetalle(EmisionesAtmosfericas emision){
		verFormulario=true;
		cargarDatosIniciales();
		if (detalleEmision == null || detalleEmision.getId() == null || detalleEmision.getDetalleOriginalId() == null )
			editarFuente = false;
		this.emisionesAtmosfericas=emision!=null?emision:new EmisionesAtmosfericas();
		
		listaDetalleEmisionesAtmosfericas = new ArrayList<DetalleEmisionesAtmosfericas>();
		
		if(emisionesAtmosfericas != null && emisionesAtmosfericas.getId() != null){
			listaLaboratorios = datosLaboratorioFacade.findByDetalleEmisiones(emisionesAtmosfericas);
			// tecnico responsable
			if (emisionesAtmosfericas.getTecnicoResponsable() != null) {
				tecnicoResponsable = emisionesAtmosfericas.getTecnicoResponsable();
			}
			//historial
			if(existeObservaciones){
				emisionesAtmosfericasOriginal = emisionesAtmosfericasFacade.findByIdOriginalObservacion(emisionesAtmosfericas.getId(), numeroObservacion);
			}
			
			listaDetalleEmisionesAtmosfericas = detalleEmisionesAtmosfericasFacade.findByEmisionesAtmosfericas(emisionesAtmosfericas);			
		}
		
		if(listaDetalleEmisionesAtmosfericas != null && !listaDetalleEmisionesAtmosfericas.isEmpty()){
			if(detalleEmision.getCodigoFuente() == null){
				
				String string = listaDetalleEmisionesAtmosfericas.get(0).getCodigoFuente();
				String[] parts = string.split("F");
				//String part1 = parts[0]; 
				String part2 = parts[1];
				
				Integer numeroSerie = Integer.valueOf(part2);				
				
				String valorSecuencia = String.valueOf(numeroSerie + 1);
				while (valorSecuencia.length() < 3)
					valorSecuencia = "0" + valorSecuencia;
				String codigoFuente="F"+valorSecuencia;	
				
				detalleEmision.setCodigoFuente(codigoFuente);
			}			
		}else{
			detalleEmision.setCodigoFuente("F001");
		}
		
		if(emisionesAtmosfericas != null){
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
			
			try {
				List<Documento> documentos = documentosFacade.documentoXTablaIdXIdDoc(
								emisionesAtmosfericas.getId(), EmisionesAtmosfericas.class.getSimpleName(),
								TipoDocumentoSistema.JUSTIFICATIVO_EMISION_ATMOSFERICA);

				if(documentos != null && !documentos.isEmpty()){
					documentoJustificativo = documentos.get(0);
					if (documentoJustificativo != null && documentoJustificativo.getIdAlfresco() != null) {
						byte[] contenido = documentosFacade.descargar(documentoJustificativo.getIdAlfresco());
						documentoJustificativo.setContenidoDocumento(contenido);
					}	
					
					if(existeObservaciones){
						documentoJustificativoHistorico = validarDocumentoHistorico(documentoJustificativo, documentos);
					}
				}else{
					documentoJustificativo = new Documento();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
	}
	
	private void cargarDatosIniciales(){
		listaFuentesFijasCombustion = new ArrayList<FuenteFijaCombustion>();				
		listaTipoCombustible = new ArrayList<TipoCombustibleRetce>();			
		fuenteFijaCombustion = new FuenteFijaCombustion();			
		listaUnidadesPotencia = new ArrayList<CatalogoUnidades>();			
		detalleEmision = new DetalleEmisionesAtmosfericas();			
		listaCatalogoChimenea = new ArrayList<DetalleCatalogoGeneral>();			
		listaFacilidadesMonitoreo = new ArrayList<CatalogoFacilidadesMonitoreo>();			
		listaAutorizacionesEmisiones = new ArrayList<AutorizacionEmisiones>();				
		listaCatalogoEstadoFuente = new ArrayList<DetalleCatalogoGeneral>();			
		listaUnidadesCombustible = new ArrayList<CatalogoUnidades>();			
		listaCatalogoTipoFuncionamiento = new ArrayList<DetalleCatalogoGeneral>();			
		listaCatalogoFrecuenciaMonitoreo = new ArrayList<CatalogoFrecuenciaMonitoreo>();			
		listaLimitesMaximoPermitidos = new ArrayList<LimiteMaximoPermitido>();
		listaDatosObtenidos = new ArrayList<DatoObtenidoMedicion>();
		listaMetodoEstimacion = new ArrayList<CatalogoMetodoEstimacion>();
		listaDatosObtenidosTotal = new ArrayList<DatoObtenidoMedicion>();
		listaDatosTotalAux = new ArrayList<DatoObtenidoMedicion>();
		facilidadesMonitoreoSeleccionadas = new ArrayList<String>();
		listaFacilidadesMonitoreoEmisiones = new ArrayList<FacilidadesMonitoreoEmisiones>();
		listaLaboratorios = new ArrayList<DatosLaboratorio>();
		datoLaboratorio = new DatosLaboratorio();
		listaLaboratoriosEliminados = new ArrayList<DatosLaboratorio>();
		listaSustanciasRetce = new ArrayList<SubstanciasRetce>();
		sustanciaRetceIngresada = new SubstanciasRetce();
		listaSustanciasRetce = new ArrayList<SubstanciasRetce>();
		listaSustanciasRetceEliminadas = new ArrayList<SubstanciasRetce>();
		tecnicoResponsable = new TecnicoResponsable();
		listaDatosObtenidosAux = new ArrayList<DatoObtenidoMedicion>();
		metodoEstimacion = new CatalogoMetodoEstimacion();
		listaCatalogoConstantes = new ArrayList<DetalleCatalogoGeneral>();
		listaDocumentosLaboratorio = new ArrayList<Documento>();
		listaDocumentosLaboratoriosEliminados = new ArrayList<Documento>();
		idAutorizacion = null;
		idCatalogoSusRetce = null;
		idEstadoFuenteDetalleCatalogo = null;
		idFrecuenciaMonitoreo = null;
		idFuente = null;
		idMetodoEstimacionParametro = null;
		idMetodoEstimacionSustancia = null;
		idTipoCombustible = null;
		idTipoFuenteRetce = null;
		idTipoFuncionamiento = null;
		idUnidadCombustible = null;
		idUnidadPotencia = null;
		anioInicio = null;
		anioFin = null;
		mesInicio = null;
		mesFin = null;
		setMostrarEstadoFuente(true);
		mostrarLaboratorioFuentes=false;
		oficioAprobacionDoc = new Documento();
		idFase=null;
		listaMeses = new ArrayList<String>();
		
		for(String mes : mesLista){
			listaMeses.add(mes);
		}
		
		listaAnios = new ArrayList<Integer>();
		cargarListaAnios();
		cargarDatos();			
	}
	

	private void inicializarVariables(){
		detalleEmision = new DetalleEmisionesAtmosfericas();
		facilidadesMonitoreoSeleccionadas = new ArrayList<String>();
		listaFacilidadesMonitoreoEmisiones = new ArrayList<FacilidadesMonitoreoEmisiones>();	
		idAutorizacion = null;
		idEstadoFuenteDetalleCatalogo = null;
		idFrecuenciaMonitoreo = null;
		idFase=null;
		idFuente = null;
		idMetodoEstimacionParametro = null;
		idMetodoEstimacionSustancia = null;
		idTipoCombustible = null;
		idTipoFuenteRetce = null;
		idTipoFuncionamiento = null;
		idUnidadCombustible = null;
		idUnidadPotencia = null;
		anioInicio = null;
		anioFin = null;
		mesInicio = null;
		mesFin = null;
		oficioAprobacionDoc = new Documento();			
	}
	
	public void guardarFuente(){
		try {
			btnSiguiente();
			ocultarFormulario();
			verFormulario=true;
			editarFuente=false;
			agregarDetalle(emisionesAtmosfericas);
			//inicializarVariables();
		} catch (CmisAlfrescoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void ocultarFormulario(){
		verFormulario=false;
		masReportes=false;
		nuevoReporte =false;
		if(existeObservaciones){
			listaEmisionesAtmosfericas = emisionesAtmosfericasFacade.findByEmision(emisionesAtmosfericas.getCodigo());
		}else{
			listaEmisionesAtmosfericas = emisionesAtmosfericasFacade.findByInformacionProyecto(informacionProyecto);
		}
		
		//listaDetalleEmisionesAtmosfericas = new ArrayList<DetalleEmisionesAtmosfericas>();
		//detalleEmision = new DetalleEmisionesAtmosfericas();
		if(emisionesAtmosfericas != null && emisionesAtmosfericas.getId() != null)
			seleccionarEmision(emisionesAtmosfericas);
		wizardBean.setCurrentStep("paso1");
		Wizard wizard = (Wizard) FacesContext.getCurrentInstance().getViewRoot().findComponent("form:wizardEmision");
		wizard.setStep("paso1");
		RequestContext.getCurrentInstance().update("form:wizardEmision");
	}
	 	 
	private void buscarDatosOperador() {
		Usuario user = loginBean.getUsuario();
		Organizacion orga = null;
		try {
			orga = organizacionFacade.buscarPorRuc(user.getNombre());
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		if (orga != null) {
			nombreUsuario = orga.getNombre();
			representanteLegal = orga.getPersona().getNombre();
			nombreOperador = representanteLegal;
			cedulaOperador = orga.getPersona().getPin();
		} else {
			nombreUsuario = user.getPersona().getNombre();
			nombreOperador = nombreUsuario;
			cedulaOperador = user.getPersona().getPin();
		}
	}
	
	
	private void cargarListaAnios() {
        Date nuevaFecha = new Date();
        Integer i= JsfUtil.getYearFromDate(nuevaFecha);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.YEAR, 1);
        nuevaFecha = cal.getTime();
        listaAnios = new ArrayList<Integer>();

        for ( i=2015; i<= JsfUtil.getYearFromDate(nuevaFecha); i++){
            listaAnios.add(i);
        }
    }
	
	public void seleccionarEmision(EmisionesAtmosfericas emision){
		this.emisionesAtmosfericas=emision;
		listaDetalleEmisionesAtmosfericas = detalleEmisionesAtmosfericasFacade.findByEmisionesAtmosfericas(emisionesAtmosfericas);
	}
	
	public String btnAtras() {
		masReportes=false;
        habiliatFinalizar=false;
        setGuardado(true);
        String currentStep = wizardBean.getCurrentStep();
        
        if (currentStep != null && currentStep.equals("paso4")){
        	datoLaboratorio = new DatosLaboratorio();
        	documentoInformeLaboratorio = new Documento();
        	listaDocumentosLaboratorio = new ArrayList<Documento>();
        }
        if (currentStep != null && currentStep.equals("paso5")){
        	sustanciaRetceIngresada = new SubstanciasRetce();	
        	documentoRespaldoSustancias = new Documento();
        	idMetodoEstimacionSustancia = null;
        	idCatalogoSusRetce = null;
        	mostrarLaboratorios = false;
        }
        
        return null;
    }
	
	 public String btnSiguiente() throws CmisAlfrescoException {
	        setGuardado(false);
	        String currentStep = wizardBean.getCurrentStep();
	        	       
	        guardar(false);
	        if (currentStep != null && currentStep.equals("paso4")){
	        	datoLaboratorio = new DatosLaboratorio();
	        	documentoInformeLaboratorio = new Documento();
	        	listaDocumentosLaboratorio = new ArrayList<Documento>();
	        }
	        if (currentStep != null && currentStep.equals("paso5")){
	        	sustanciaRetceIngresada = new SubstanciasRetce();	
	        	documentoRespaldoSustancias = new Documento();
	        	idMetodoEstimacionSustancia = null;
	        	idCatalogoSusRetce = null;
	        	mostrarLaboratorios = false;
	        }
	        return null;
	 }
	
	private void cargarDatos(){
		try {
			if(informacionProyecto.getTipoSector().getId() >= 3){
				listaFuentesFijasCombustion = fuenteFijaFacade.findByTipoSector("Otros Sectores");
			}else{
				listaFuentesFijasCombustion = fuenteFijaFacade.findByTipoSector(informacionProyecto.getTipoSector().getNombre());
			}			
								
			listaUnidadesPotencia = catalogoUnidadesFacade.findByParametro("Unidades Potencia");
			listaCatalogoChimenea = detalleCatalogoGeneralFacade.findByCatalogoGeneralString("emision.numero_chimeneas"); 
			listaFacilidadesMonitoreo = catalogoFacilidadesMonitoreoFacade.findAll();
//			listaAutorizacionesEmisiones = autorizacionEmisionesFacade.findAll();
			listaCatalogoEstadoFuente = detalleCatalogoGeneralFacade.findByCatalogoGeneralString("emision.estado_fuente");
			listaUnidadesCombustible = catalogoUnidadesFacade.findByParametro("Unidades Combustible");
			listaCatalogoTipoFuncionamiento = detalleCatalogoGeneralFacade.findByCatalogoGeneralString("emision.tipo_funcionamiento");
			listaMetodoEstimacion = catalogoMetodoEstimacionFacade.findAll();
			listaCatalogoSustanciasRetce = catalogoSustanciasRetceFacade.findAll();		
			//listaCatalogoFrecuenciaMonitoreo=informacionProyecto.getFaseRetce()!=null && informacionProyecto.getFaseRetce().getFrecuencia() != null ?catalogoFrecuenciaMonitoreoFacade.findByName(informacionProyecto.getFaseRetce().getFrecuencia()):catalogoFrecuenciaMonitoreoFacade.findAll();
			listaCatalogoFrecuenciaMonitoreo=catalogoFrecuenciaMonitoreoFacade.findAll();
			listaCatalogoConstantes = detalleCatalogoGeneralFacade.findByCatalogoGeneralString("emision.contante.formula");
			metodoEstimacion = catalogoMetodoEstimacionFacade.findByNombre("Medición Directa");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void editarDetalle(DetalleEmisionesAtmosfericas detalleEmision){
		cargarDatosIniciales();
		this.detalleEmision = detalleEmision;
		editarFuente = true;
        habiliatFinalizar=false;
		if(detalleEmision.getDetalleOriginalId() != null){
			editarFuente = !detalleEmision.getId().equals(detalleEmision.getDetalleOriginalId());
		}else
			editarFuente = false;
		cargarDatosDetalleEmision();
	}
	
	private void cargarDatosDetalleEmision() {
		try {
			verFormulario = true;
			ingresoDetalle = true;
			mostrarLimites = true;
			nuevoReporte=false;
			try {
				List<Documento> documentos = documentosFacade.documentoXTablaIdXIdDoc(
								emisionesAtmosfericas.getId(), EmisionesAtmosfericas.class.getSimpleName(),
								TipoDocumentoSistema.JUSTIFICATIVO_EMISION_ATMOSFERICA);

				if(documentos != null && !documentos.isEmpty()){
					documentoJustificativo = documentos.get(0);
					if (documentoJustificativo != null && documentoJustificativo.getIdAlfresco() != null) {
						byte[] contenido = documentosFacade.descargar(documentoJustificativo.getIdAlfresco());
						documentoJustificativo.setContenidoDocumento(contenido);
					}	
					
					if(existeObservaciones){
						documentoJustificativoHistorico = validarDocumentoHistorico(documentoJustificativo, documentos);
					}
				}else{
					documentoJustificativo = new Documento();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			

			if (detalleEmision.getDocumentoOficioAprobacion() != null) {
				oficioAprobacionDoc = documentosFacade.buscarDocumentoPorId(detalleEmision.getDocumentoOficioAprobacion().getId());
				if(oficioAprobacionDoc!=null)
					oficioAprobacionDoc.setContenidoDocumento(documentosFacade.descargar(oficioAprobacionDoc.getIdAlfresco()));
				else
					oficioAprobacionDoc=new Documento();
				if(existeObservaciones){
					oficioAprobacionDocHistorico = new Documento();
					setOficioAprobacionDoc(oficioAprobacionDoc);
				}
			}

			if(detalleEmision.getFuenteFijaCombustion() != null){
				idFuente = detalleEmision.getFuenteFijaCombustion().getId();
				if (idFuente != null) {
					tipoCombustiblesPorFuentes();
				}
			}
			if(detalleEmision.getCatalogoUnidadesPotencia() != null){
				idUnidadPotencia = detalleEmision.getCatalogoUnidadesPotencia().getId();
			}

			if (detalleEmision.getTipoCombustible() != null) {
				idTipoCombustible = detalleEmision.getTipoCombustible().getId();
				tipoCombustibleListener();				
			}

			listaFacilidadesMonitoreoEmisiones = facilidadesMonitoreoEmisionesFacade.findByDetail(detalleEmision);
			if (listaFacilidadesMonitoreoEmisiones != null
					&& !listaFacilidadesMonitoreoEmisiones.isEmpty()) {
				for (FacilidadesMonitoreoEmisiones facilidadMonitoreo : listaFacilidadesMonitoreoEmisiones) {
					facilidadesMonitoreoSeleccionadas.add(facilidadMonitoreo.getCatalogoFacilidadesMonitoreo().getId().toString());
				}
			}

			if (detalleEmision.getCatalogoUnidadesCombustible() != null) {
				idUnidadCombustible = detalleEmision.getCatalogoUnidadesCombustible().getId();
			}

			if (detalleEmision.getEstadoFuenteDetalleCatalogo() != null) {
				idEstadoFuenteDetalleCatalogo = detalleEmision.getEstadoFuenteDetalleCatalogo().getId();
				if(detalleEmision.getEstadoFuenteDetalleCatalogo().getDescripcion().equals("Inactiva")){
					mostrarEstadoFuente = false;
					
					List<Documento> documentos = documentosFacade.documentoXTablaIdXIdDoc(detalleEmision.getId(),
									EmisionesAtmosfericas.class.getSimpleName(),
									TipoDocumentoSistema.DOCUMENTO_JUSTIFICACION_ESTADO_FUENTE);
					
					if(documentos != null && !documentos.isEmpty()){
						documentoJustificacionEstadoFuente = documentos.get(0);
						if(documentoJustificacionEstadoFuente!=null)
							documentoJustificacionEstadoFuente.setContenidoDocumento(documentosFacade.descargar(documentoJustificacionEstadoFuente.getIdAlfresco()));
						else
							documentoJustificacionEstadoFuente=new Documento();
						if(existeObservaciones){
							documentoJustificacionEstadoFuenteHistorico = validarDocumentoHistorico(
									documentoJustificacionEstadoFuente,	documentos);
						}
					}
				}
				else
					mostrarEstadoFuente = true;
			}
			verificarFuentesActivas();
			if (detalleEmision.getTipoOperacionDetalleCatalogo() != null) {
				idTipoFuncionamiento = detalleEmision.getTipoOperacionDetalleCatalogo().getId();
			}
			if (detalleEmision.getFrecuenciaMonitoreo() != null) {
				idFrecuenciaMonitoreo = detalleEmision.getFrecuenciaMonitoreo().getId();
			}
			if (detalleEmision.getFaseRetce() != null) {
				idFase = detalleEmision.getFaseRetce().getId();
			}
			if (detalleEmision.getFechaInicioMonitoreo() != null) {
				String string = detalleEmision.getFechaInicioMonitoreo();
				String[] parts = string.split("-");
				mesInicio = parts[0];
				anioInicio = Integer.valueOf(parts[1]);
			}else{
				mesInicio = "";
				anioInicio = 0;
			}

			if (detalleEmision.getFechaFinMonitoreo() != null) {
				String string = detalleEmision.getFechaFinMonitoreo();
				String[] parts = string.split("-");
				mesFin = parts[0];
				anioFin = Integer.valueOf(parts[1]);
			}else{
				mesFin = "";
				anioFin = 0;
			}
			// DATOS NORMATIVA VIGENTE
			if(sector.equals("Hidrocarburos")){
				idAutorizacion = null;
			}else{				
				if (detalleEmision.getAutorizacionEmisiones() != null) {
					idAutorizacion = detalleEmision.getAutorizacionEmisiones().getId();
					cargarTablaParametros();
				}
			}			
			
			listaDatosObtenidosTotal = datoObtenidoMedicionFacade.findByEmisionAtmosferica(detalleEmision);
			
			metodoEstimacion = catalogoMetodoEstimacionFacade.findByNombre("Medición Directa");
			idMetodoEstimacionParametro = metodoEstimacion.getId();
			
			if(listaDatosObtenidosTotal == null || listaDatosObtenidosTotal.isEmpty()){
				if(mostrarAutorizacion){
					idAutorizacion = null;
					listaLimitesMaximoPermitidos = new ArrayList<LimiteMaximoPermitido>();
				}				
			}

			// DATOS DE LABORATORIO

			if (detalleEmision.getId() != null) {
				listaLaboratorios = datosLaboratorioFacade.findByDetalleEmisiones(detalleEmision.getEmisionesAtmosfericas());
				if (listaLaboratorios != null && !listaLaboratorios.isEmpty()) {
					for (DatosLaboratorio dato : listaLaboratorios) {
						
						dato.setListaDocumentosLaboratorios(new ArrayList<Documento>());
						
						List<Documento> documentos = documentosFacade
								.documentoXTablaIdXIdDoc(dato.getId(),
										EmisionesAtmosfericas.class.getSimpleName(),
										TipoDocumentoSistema.TIPO_DOCUMENTO_INFORME_LABORATORIO);

						if (documentos != null && !documentos.isEmpty()) {
							
							for(Documento documento : documentos){
								if(documento.getIdHistorico() == null){
									byte[] documentoContent = documentosFacade.descargar(documento.getIdAlfresco());
									
									documento.setContenidoDocumento(documentoContent);								
									dato.getListaDocumentosLaboratorios().add(documento);
								}								
							}							
						}
					}
				}

				// SUSTANCIAS RETCE
				listaSustanciasRetce = sustanciaRetceFacade.findByDetalleEmision(detalleEmision);

				if (listaSustanciasRetce != null && !listaSustanciasRetce.isEmpty()) {
					for (SubstanciasRetce sustancia : listaSustanciasRetce) {
						sustancia.setFuenteFija(fuenteFijaCombustion.getFuente());
						sustancia.setCodigoPuntoMonitoreo(detalleEmision.getCodigoPuntoMonitoreo());					
					}
				}
			}

			// tecnico responsable
			if (detalleEmision.getEmisionesAtmosfericas().getTecnicoResponsable() != null) {
				tecnicoResponsable = detalleEmision.getEmisionesAtmosfericas().getTecnicoResponsable();
			}
			
			if(detalleEmision.getUbicacionGeografica() == null || detalleEmision.getUbicacionGeografica().getId() == null) {
				validarCoordenadas();
			}
			
			ingresoDetalle = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Permite obtener los tipos de combustible si es el caso y setear la fuente fija de combustión en el objeto
	 */
	public void tipoCombustiblesPorFuentes(){
		try {
			mostrarHoras = false;
			fuenteFijaCombustion = fuenteFijaFacade.findById(idFuente);
			if(fuenteFijaCombustion.getFuente().toLowerCase().contains("motores de combusti")){
				mostrarHoras = true;
			}
			listaDatosObtenidos = new ArrayList<DatoObtenidoMedicion>();
			listaDatosObtenidosTotal = new ArrayList<DatoObtenidoMedicion>();			
			listaLimitesMaximoPermitidos = new ArrayList<LimiteMaximoPermitido>();
			
			listaTipoCombustible = tipoCombustibleFacade.findByFuenteFijaCombustion(fuenteFijaCombustion);
			
			detalleEmision.setFuenteFijaCombustion(fuenteFijaCombustion);
			
			//CODIGO PARA OBTENER LIMITES MAXIMOS
			listaLimitesMaximoPermitidos = limiteMaximoPermitidoFacade.findByFuenteFija(fuenteFijaCombustion);
			
			if(listaLimitesMaximoPermitidos != null && !listaLimitesMaximoPermitidos.isEmpty()){				
				deshabilitarTipoCombustible = true;
				mostrarAutorizacion = false;
				if(!ingresoDetalle){
					idTipoCombustible = null;
				}				
			}else{				
				mostrarAutorizacion = true;
				idAutorizacion = null;
				encontrarAutorizacionPorFuente(fuenteFijaCombustion);
				if(listaTipoCombustible == null || listaTipoCombustible.isEmpty()){
					deshabilitarTipoCombustible = true;
					idTipoCombustible = null;
				}else{
					deshabilitarTipoCombustible = false;
				}
			}
			if(!ingresoDetalle){
				detalleEmision.setTipoCombustible(null);
				detalleEmision.setAutorizacionEmisiones(null);
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void unidadPotenciaListener(){
		try {
			CatalogoUnidades unidadPotencia = catalogoUnidadesFacade.findById(idUnidadPotencia);
			detalleEmision.setCatalogoUnidadesPotencia(unidadPotencia);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void unidadFaseListener(){
		try {
			faseRetce=faseRetceFacade.findById(idFase);
			detalleEmision.setFaseRetce(faseRetce);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void tipoCombustibleListener(){
		try {
			TipoCombustibleRetce tipoCombustible = tipoCombustibleFacade.findById(idTipoCombustible);
			
			if(tipoCombustible != null && tipoCombustible.getDescripcion() != null && tipoCombustible.getDescripcion().equals("No aplica")){
				//fuente sin combustible
			}else{
				detalleEmision.setTipoCombustible(tipoCombustible);
				
				if(!ingresoDetalle)
					detalleEmision.setAutorizacionEmisiones(null);
				
				if(tipoCombustible.getTieneLimitesMaximos() != null && !tipoCombustible.getTieneLimitesMaximos()){
					mostrarLimites = false;
					mostrarAutorizacion = false;
					return;
				}else{
					mostrarLimites = true;
				}
				
				listaDatosObtenidos = new ArrayList<DatoObtenidoMedicion>();
				listaDatosObtenidosTotal = new ArrayList<DatoObtenidoMedicion>();
				
				listaLimitesMaximoPermitidos = limiteMaximoPermitidoFacade.findByCombustible(tipoCombustible);	
				if(listaLimitesMaximoPermitidos == null || listaLimitesMaximoPermitidos.isEmpty()){
					listaLimitesMaximoPermitidos = new ArrayList<LimiteMaximoPermitido>();
					mostrarAutorizacion = true;
					idAutorizacion = null;
					encontrarAutorizacionPorCombustible(tipoCombustible);
				}else{
					mostrarAutorizacion = false;
				}
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void unidadCombustibleListener(){
		try {
			CatalogoUnidades unidadCombustible = catalogoUnidadesFacade.findById(idUnidadCombustible);
			detalleEmision.setCatalogoUnidadesCombustible(unidadCombustible);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void tipoFuncionamientoListener(){
		try {
			DetalleCatalogoGeneral detalleTipoFuncionamiento = detalleCatalogoGeneralFacade.findById(idTipoFuncionamiento);
			detalleEmision.setTipoOperacionDetalleCatalogo(detalleTipoFuncionamiento);	
		} catch (Exception e) {
			e.printStackTrace();
		}			
	}
	
	public void frecuenciaMonitoreoListener(){
		try {
			CatalogoFrecuenciaMonitoreo frecuenciaMonitoreo = catalogoFrecuenciaMonitoreoFacade.findById(idFrecuenciaMonitoreo);
			detalleEmision.setFrecuenciaMonitoreo(frecuenciaMonitoreo);			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void catalogoSustanciaRetceListener(){
		try {
			CatalogoSustanciasRetce catalogoSustancia = catalogoSustanciasRetceFacade.findById(idCatalogoSusRetce);
			sustanciaRetceIngresada.setCatologSustanciasRetce(catalogoSustancia);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void metodoEstimacionSustanciaListener(){
		try {
			CatalogoMetodoEstimacion metodoEstimacion = catalogoMetodoEstimacionFacade.findById(idMetodoEstimacionSustancia);
			sustanciaRetceIngresada.setCatalogoMetodoEstimacion(metodoEstimacion);	
			
			if(metodoEstimacion.getDescripcion().equals("Medición Directa")){
				mostrarLaboratorios = true;
				documentoRespaldoSustancias = null;
				sustanciaRetceIngresada.setDocumento(null);
			}else{
				mostrarLaboratorios = false;
				sustanciaRetceIngresada.setDatosLaboratorio(null);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void validarPeriodoMedicionListener(){		
		if(anioInicio==null || mesInicio==null || anioFin==null || mesFin==null)
			return;
		
		int mesDesde= listaMeses.indexOf(mesInicio);
		int meshasta= listaMeses.indexOf(mesFin);			
		int fechaDesde=Integer.valueOf(anioInicio)*100+mesDesde;
		int fechaHasta=Integer.valueOf(anioFin)*100+meshasta;
			
		if(fechaDesde > fechaHasta){
			JsfUtil.addMessageWarning("Las fechas de Periodo de medición son incorrectas");
			mesFin=null;
			anioFin=null;
			detalleEmision.setFechaFinMonitoreo(null);
			return;
		}
				
		String fechaInicioMonitoreo = mesInicio + "-" + anioInicio.toString();
		detalleEmision.setFechaInicioMonitoreo(fechaInicioMonitoreo);

		String fechaFinMonitoreo = mesFin + "-" + anioFin.toString();
		detalleEmision.setFechaFinMonitoreo(fechaFinMonitoreo);
			
	}
	
	public void metodoEstimacionListener(){
		datoObtenido.setValorIngresado(null);
	}
		
	public void uploadListenerDocumentoOficioAprobacion(FileUploadEvent event) {
		oficioAprobacionDoc = this.uploadListener(event,EmisionesAtmosfericas.class, "pdf");
	}	 
	
	public void uploadListenerDocumentoEstadoFuente(FileUploadEvent event) {
		documentoJustificacionEstadoFuente = this.uploadListener(event,EmisionesAtmosfericas.class, "pdf");
	}
	
	public void uploadListenerDocumentoInformeLaboratorio(FileUploadEvent event){

		
		Integer tipoDocumento = Integer.parseInt(event.getComponent().getAttributes().get("tipoDocumento").toString());
		Documento documentoLoad = null;
		
		UploadedFile fileAdjuntoInformeLaboratorio = event.getFile();
		String tipoFile = event.getFile().getContentType();
		if(tipoFile.toLowerCase().contains("pdf")) {
			documentoLoad = UtilDocumento.generateDocumentPDFFromUpload(fileAdjuntoInformeLaboratorio.getContents(),fileAdjuntoInformeLaboratorio.getFileName());
		} else {
			documentoLoad = UtilDocumento.generateDocumentZipRarFromUpload(fileAdjuntoInformeLaboratorio.getContents(),fileAdjuntoInformeLaboratorio.getFileName());
		}
		
		
		TipoDocumento tipoDoc = new TipoDocumento();
		
		switch (tipoDocumento) {
		case 1:
	        tipoDoc.setId(TipoDocumentoSistema.TIPO_DOCUMENTO_INFORME_LABORATORIO.getIdTipoDocumento());
	        documentoLoad.setDescripcion("Informe de monitoreo emitido por el laboratorio");
	        documentoLoad.setTipoContenido("1");
	        documentoTipo1 = fileAdjuntoInformeLaboratorio.getFileName();
			break;
		case 2:
	        tipoDoc.setId(TipoDocumentoSistema.RETCE_SAE_LABORATORIO.getIdTipoDocumento());
	        documentoLoad.setDescripcion("Registro de SAE del laboratorio");
	        documentoLoad.setTipoContenido("2");
	        documentoTipo2 = fileAdjuntoInformeLaboratorio.getFileName();
			break;
		case 3:
	        tipoDoc.setId(TipoDocumentoSistema.RETCE_CADENA_CUSTODIA.getIdTipoDocumento());
	        documentoLoad.setDescripcion("Cadena de custodia del laboratorio");
	        documentoLoad.setTipoContenido("3");
	        documentoTipo3 = fileAdjuntoInformeLaboratorio.getFileName();
			break;
		case 4:
	        tipoDoc.setId(TipoDocumentoSistema.RETCE_PROTOCOLO_MUESTREO.getIdTipoDocumento());
	        documentoLoad.setDescripcion("Protocolo de muestreo del laboratorio");
	        documentoLoad.setTipoContenido("4");
	        documentoTipo4 = fileAdjuntoInformeLaboratorio.getFileName();
			break;
		case 5:
	        tipoDoc.setId(TipoDocumentoSistema.RETCE_ADICIONAL_LABORATORIO.getIdTipoDocumento());
	        documentoLoad.setDescripcion("Documento adicional del laboratorio");
	        documentoLoad.setTipoContenido("5");
	        documentoTipo5 = fileAdjuntoInformeLaboratorio.getFileName();
			break;

		default:
			break;
		}
		
		documentoLoad.setTipoDocumento(tipoDoc);
		
		List<Integer> docsEliminar = new ArrayList<Integer>();
		for(int i=0; i< listaDocumentosLaboratorio.size(); i++){
			Documento doc = listaDocumentosLaboratorio.get(i);
			if(doc.getTipoDocumento().equals(tipoDoc)) {
				docsEliminar.add(i);
			}
		}
		
		for (Integer index : docsEliminar) {
			listaDocumentosLaboratorio.remove(index);
		}
		
		listaDocumentosLaboratorio.add(documentoLoad);
	}
	
	public void uploadListenerDocumentoRespaldoSustancias(FileUploadEvent event){
		documentoRespaldoSustancias = this.uploadListener(event,EmisionesAtmosfericas.class, "pdf");
	}
	
	public void uploadListenerDocumentoJustificativo(FileUploadEvent event){
		documentoJustificativo = this.uploadListener(event,EmisionesAtmosfericas.class, "pdf");
	}
	 
	private Documento uploadListener(FileUploadEvent event, Class<?> clazz, String extension) {
		byte[] contenidoDocumento = event.getFile().getContents();
		Documento documento = crearDocumento(contenidoDocumento, clazz,	extension);
		documento.setNombre(event.getFile().getFileName());
		return documento;
	}

	public Documento crearDocumento(byte[] contenidoDocumento, Class<?> clazz, String extension) {
		Documento documento = new Documento();
		documento.setContenidoDocumento(contenidoDocumento);
		documento.setNombreTabla(clazz.getSimpleName());
		documento.setIdTable(0);
		documento.setExtesion("." + extension);

		documento.setMime(extension == "pdf" ? "application/pdf" : "application/vnd.ms-excel");
		return documento;
	}
	
	public void cargarTablaParametros(){
		try {					
						
			if(idAutorizacion != null){
				AutorizacionEmisiones autorizacion = autorizacionEmisionesFacade.findById(idAutorizacion);
				detalleEmision.setAutorizacionEmisiones(autorizacion);
				listaDatosObtenidos = new ArrayList<DatoObtenidoMedicion>();
				listaDatosObtenidosTotal = new ArrayList<DatoObtenidoMedicion>();				
				idMetodoEstimacionParametro = metodoEstimacion.getId();
				
				if(idTipoCombustible != null && detalleEmision.getTipoCombustible() != null && 
						!detalleEmision.getTipoCombustible().getDescripcion().equals("No aplica")){
				listaLimitesMaximoPermitidos = limiteMaximoPermitidoFacade
						.findByAutorizacionCombustible(detalleEmision.getAutorizacionEmisiones(), detalleEmision.getTipoCombustible());
				}else{
					listaLimitesMaximoPermitidos = limiteMaximoPermitidoFacade.findByAutorizacionFuenteFija(
									detalleEmision.getAutorizacionEmisiones(),
									detalleEmision.getFuenteFijaCombustion());
				}				
			}		
					
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * METODOS DE INGRESO DE DATOS PARA CALCULOS
	 * Se carga la lista de datos por parámetro
	 * @param limite
	 */
	public void cargarIngresoDatos(LimiteMaximoPermitido limite){
		try {
			listaDatosObtenidos = new ArrayList<DatoObtenidoMedicion>();
			listaDatosObtenidosAux = new ArrayList<DatoObtenidoMedicion>();
			listaDatosTotalAux = new ArrayList<DatoObtenidoMedicion>();
			datoObtenido = new DatoObtenidoMedicion();			
			datoObtenido.setLimiteMaximoPermitido(limite);
			idMetodoEstimacionParametro = metodoEstimacion.getId();
						
			listaDatosTotalAux.addAll(listaDatosObtenidosTotal);
			if(!listaDatosTotalAux.isEmpty()){
				
				for(DatoObtenidoMedicion dato : listaDatosTotalAux){
					if(dato.getLimiteMaximoPermitido().getParametro().equals(limite.getParametro()) && 
							((dato.getLimiteMaximoPermitido().getApplicacion() == null && limite.getApplicacion() == null) || 
							(dato.getLimiteMaximoPermitido().getApplicacion() != null && limite.getApplicacion() != null && 
								dato.getLimiteMaximoPermitido().getApplicacion().equals(limite.getApplicacion())))){
						listaDatosObtenidos.add(dato);
						datoObtenido = dato;
						listaDatosObtenidosTotal.remove(dato);
					}
				}
				
				listaDatosObtenidosAux.addAll(listaDatosObtenidos);
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	//TODO: MODIFICADO PARA MOSTRAR FORMATO 
	public String cargarReporte(LimiteMaximoPermitido limite){
		try {				
			if(!listaDatosObtenidosTotal.isEmpty()){				
				for(DatoObtenidoMedicion dato : listaDatosObtenidosTotal){
					if((dato.getLimiteMaximoPermitido().getParametro().equals(limite.getParametro())) && 
							((dato.getLimiteMaximoPermitido().getApplicacion() == null && limite.getApplicacion() == null) || 
							(dato.getLimiteMaximoPermitido().getApplicacion() != null && limite.getApplicacion() != null && 
								dato.getLimiteMaximoPermitido().getApplicacion().equals(limite.getApplicacion())))){
						String datoMostar="";	
						if(dato.getValorCorregido()<0){
								datoMostar=dato.getValorCorregido().toString();
							}else{
								String patron="#,###.0000000";
								DecimalFormat formato=new DecimalFormat(patron);
								 datoMostar= formato.format(dato.getValorCorregido());
							}
							 
						return datoMostar;
					}
				}
				return null;
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}	
	
	/**
	 * Se agrega datos individuales a la lista para guardar
	 */
	public void agregarDatoLista(){
		try {
			
			if(datoObtenido.getLimiteMaximoPermitido().getValor() > 0 ){
				if(datoObtenido.getLimiteMaximoPermitido().getValor() < datoObtenido.getValorIngresado()){
					JsfUtil.addMessageWarning("Superó el límite máximo permisible, incluir la acción correctiva implementada");
				}
			}
			DatoObtenidoMedicion datoAux = datoObtenido;
			
			datoObtenido.setCatalogoMetodoEstimacion(metodoEstimacion);
			
			if(!listaDatosObtenidos.contains(datoObtenido)){
								
				if(datoAux.getLimiteMaximoPermitido().isParametroCalculado()){
					Double datoCalculado = calculoCorrecionOxigeno(datoObtenido);
					datoObtenido.setValorCorregido(datoCalculado);
				}else{
					datoObtenido.setValorCorregido(datoAux.getValorIngresado());
				}
				listaDatosObtenidos.add(datoObtenido);
						
			}else{
				for(DatoObtenidoMedicion dato : listaDatosObtenidos){
					if(dato.getLimiteMaximoPermitido().isParametroCalculado()){
						Double datoCalculado = calculoCorrecionOxigeno(datoObtenido);
						datoObtenido.setValorCorregido(datoCalculado);
					}else{
						dato.setValorCorregido(dato.getValorIngresado());
					}
				}				
			}
			
			datoObtenido = new DatoObtenidoMedicion();
			datoObtenido.setValorFlujoGasSeco(datoAux.getValorFlujoGasSeco());
			datoObtenido.setValorPorcentajeOxigeno(datoAux.getValorPorcentajeOxigeno());
			datoObtenido.setValorTemperatura(datoAux.getValorTemperatura());
			datoObtenido.setLimiteMaximoPermitido(datoAux.getLimiteMaximoPermitido());
			
			RequestContext.getCurrentInstance().reset(":dialogform:metodoEstimacion");
			
			agregarTodos();
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	public void limpiar(){
		try {
			DatoObtenidoMedicion dato = datoObtenido;
			datoObtenido = new DatoObtenidoMedicion();
			if(!listaDatosObtenidos.isEmpty()){
				datoObtenido.setValorFlujoGasSeco(dato.getValorFlujoGasSeco());
				datoObtenido.setValorPorcentajeOxigeno(dato.getValorPorcentajeOxigeno());
				datoObtenido.setValorTemperatura(dato.getValorTemperatura());			
			}		
			datoObtenido.setLimiteMaximoPermitido(dato.getLimiteMaximoPermitido());			
			RequestContext.getCurrentInstance().reset(":dialogform:metodoEstimacion");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Double calculoCorrecionOxigeno(DatoObtenidoMedicion dato){		
		try {
			Integer constante = Integer.valueOf(listaCatalogoConstantes.get(0).getDescripcion());
			Double concentracion = dato.getValorIngresado() * dato.getLimiteMaximoPermitido().getParametro().getDensidad();
			dato.setValorConcentracion(concentracion);
			Double segundoValor = Double.valueOf(0);
			
			if(dato.getLimiteMaximoPermitido().getTipoCombustible() != null && dato.getLimiteMaximoPermitido().getTipoCombustible().getPorcentajeCorrecionOxigeno() != null){
				segundoValor = dato.getLimiteMaximoPermitido().getTipoCombustible().getPorcentajeCorrecionOxigeno();				
			}else{
				segundoValor = dato.getLimiteMaximoPermitido().getFuenteFijaCombustion().getPorcentajeCorrecion();
			}
			
			
			Double tercerValor = dato.getValorPorcentajeOxigeno();
			
			Double valor = dato.getValorConcentracion() * ((constante-segundoValor)/(constante-tercerValor));
			Double valorCalculado = (double)Math.round(valor * 10000000d) / 10000000d;
						
			dato.setPorcentajeOxigenoCorrecion(segundoValor);
			return valorCalculado;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Double.valueOf(0);
		
	}
	
	/*
	 * Se agrega todos los datos ingresados por parámetro
	 */
	public void agregarTodos(){
		
		if(!listaDatosObtenidos.isEmpty()){
			listaDatosObtenidosTotal.addAll(listaDatosObtenidos);
			RequestContext.getCurrentInstance().execute("PF('adicionarDatos').hide()");
		}else{
			JsfUtil.addMessageError("Debe ingresar los datos de medición");
		}		
	}
	
	public void cancelarModDatos(){
		listaDatosObtenidosTotal.addAll(listaDatosObtenidosAux);
		RequestContext.getCurrentInstance().execute("PF('adicionarDatos').hide()");
	}
	
	public void eliminarDato(DatoObtenidoMedicion dato){		
		listaDatosObtenidos.remove(dato);		
		if(listaDatosObtenidos.isEmpty()){
			datoObtenido.setValorFlujoGasSeco(null);
			datoObtenido.setValorPorcentajeOxigeno(null);
			datoObtenido.setValorTemperatura(null);			
		}
		datoObtenido.setLimiteMaximoPermitido(dato.getLimiteMaximoPermitido());
		RequestContext.getCurrentInstance().update(":dialogform:elementoContainer");
	}
	
	public void modificarDato(DatoObtenidoMedicion dato){
		try {
			idMetodoEstimacionParametro = dato.getCatalogoMetodoEstimacion().getId();
			setDatoObtenido(dato);	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//Fin metodos de medición
	
	/**
	 * para activar o desactivar los datos de la normativa y activar el ingreso de justificación	  
	 */
	public void estadoFuente(){
		DetalleCatalogoGeneral estadoFuente = new DetalleCatalogoGeneral();
		estadoFuente = detalleCatalogoGeneralFacade.findById(idEstadoFuenteDetalleCatalogo);		
		detalleEmision.setEstadoFuenteDetalleCatalogo(estadoFuente);
		
		if(estadoFuente.getDescripcion().equals("Inactiva")){
			mostrarEstadoFuente = false;
			//detalleEmision.setConsumoCombustible(null);
			//detalleEmision.setCatalogoUnidadesCombustible(null);
			//detalleEmision.setHorasFuncionamiento(null);
			detalleEmision.setTipoOperacionDetalleCatalogo(null);
			detalleEmision.setVelocidadSalida(null);
		
		}else{
			mostrarEstadoFuente = true;
			detalleEmision.setJustificacion(null);
			documentoJustificacionEstadoFuente = new Documento();
		}
		verificarFuentesActivas();
	}
	
	private void verificarFuentesActivas(){
		// si el estado es inactivo, verifico si hay una fuente activa
		if(!mostrarEstadoFuente){
			mostrarLaboratorioFuentes = false;
			for (DetalleEmisionesAtmosfericas objetodetalle : listaDetalleEmisionesAtmosfericas) {
				if(objetodetalle.getEstadoFuenteDetalleCatalogo()!=null){
					if(objetodetalle.getEstadoFuenteDetalleCatalogo().getDescripcion().equals("Activa") ){
						mostrarLaboratorioFuentes = true;
						break;
					}
				}
			}		
		}
	}
	
	public void agregarLaboratorio(){
		boolean guardar = false;
		if(datoLaboratorio.getRuc() == null || datoLaboratorio.getRuc().isEmpty()){
			JsfUtil.addMessageError("El campo RUC es requerido");
			return;
		}else if(datoLaboratorio.getNumeroRegistroSAE() == null || datoLaboratorio.getNumeroRegistroSAE().isEmpty()){
			JsfUtil.addMessageError("El campo N° de Registro de SAE es requerido");
			return;
		}else if(datoLaboratorio.getFechaVigenciaRegistro() == null){
			JsfUtil.addMessageError("El campo Vigencia del registro es requerido");
			return;
		}else if(datoLaboratorio.getMetodologia() == null || datoLaboratorio.getMetodologia().isEmpty()){
			JsfUtil.addMessageError("El campo Metodología es requerido");
			return;
		}else if (documentoTipo1 == null) {
			JsfUtil.addMessageError("Informe de monitoreo del Laboratorio es requerido");
			return;
		}else if (documentoTipo2 == null) {
			JsfUtil.addMessageError("Registro de SAE del Laboratorio es requerido");
			return;
		}else if (documentoTipo3 == null) {
			JsfUtil.addMessageError("Cadena de custodia es requerido");
			return;
		}else if (documentoTipo4 == null) {
			JsfUtil.addMessageError("Protocolo de muestreo es requerido");
			return;
		}else{
			guardar = true;
		}
			
		
		if(guardar){
			
			if(laboratorioModificado){			
				datoLaboratorio.setListaDocumentosLaboratorios(listaDocumentosLaboratorio);
				
				datoLaboratorio = new DatosLaboratorio();
				listaDocumentosLaboratorio = new ArrayList<Documento>();
			}else{
				boolean existe = false;
				
				if(!listaLaboratorios.isEmpty()){
					for(DatosLaboratorio laboratorio : listaLaboratorios){
						if(laboratorio.getRuc().equals(datoLaboratorio.getRuc())){
							existe = true;
							break;
						}
					}
				}			
				
				if(!existe){
					datoLaboratorio.setEmisionesAtmosfericas(detalleEmision.getEmisionesAtmosfericas());
					datoLaboratorio.setListaDocumentosLaboratorios(listaDocumentosLaboratorio);
					listaLaboratorios.add(datoLaboratorio);
					datoLaboratorio = new DatosLaboratorio();
					listaDocumentosLaboratorio = new ArrayList<Documento>();
				}else{
					JsfUtil.addMessageError("El laboratorio ya se encuentra ingresado");
				}
			}			
		}	
		laboratorioModificado = false;
		RequestContext.getCurrentInstance().execute("PF('adicionarLaboratorio').hide();");
	}
	
	/**
	 * Metodos de guardado del wizard
	 * @throws CmisAlfrescoException 
	 */
	public void guardar(boolean mostraMensaje) throws CmisAlfrescoException{
		try {
            String currentStep = wizardBean.getCurrentStep();
            habiliatFinalizar=true;
            if (currentStep == null || currentStep.equals("paso1")) {
                guardarRegistroPaso1();
                guardado = true;
            } else if (currentStep.equals("paso2")) {
                guardarRegistroPaso2();
            } else if (currentStep.equals("paso3")) {
            	mostraMensaje = guardarRegistroPaso3();
            	if(!mostraMensaje){
            		wizardBean.setCurrentStep("paso3");
            	}
            } else if (currentStep.equals("paso4")) {
                guardarRegistroPaso4();
                if(listaLaboratorios.isEmpty()){
                	JsfUtil.addMessageError("Ingrese laboratorios");
                	return;
                }
            } else if (currentStep.equals("paso5")) {
                guardarRegistroPaso5();
            } else if (currentStep.equals("paso6")) {
                guardarRegistroPaso6();
            } else if(currentStep.equals("paso7")){
            	mostraMensaje = guardarRegistroPaso7();
            }
            guardado = true;

        } catch (CmisAlfrescoException ae) {
            guardado = false;
            throw ae;
        } 
		catch (Exception e) {
            guardado = false;
            e.printStackTrace();
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
        }
		if(mostraMensaje)
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
	}
	
	private void guardarRegistroPaso1() throws ServiceException, CmisAlfrescoException{	
		if(!validarCoordenadas()){
			return;
		}
		emisionesAtmosfericas.setInformacionProyecto(informacionProyecto);
		emisionesAtmosfericas.setEstado(true);		
				
		emisionesAtmosfericasFacade.save(emisionesAtmosfericas, loginBean.getUsuario());
		
		Documento documentoOficio = new Documento();
				
		if(oficioAprobacionDoc != null && oficioAprobacionDoc.getId() == null && oficioAprobacionDoc.getContenidoDocumento()!=null){
			oficioAprobacionDoc.setIdTable(informacionProyecto.getId());
			oficioAprobacionDoc.setNombreTabla(EmisionesAtmosfericas.class.getSimpleName());
			oficioAprobacionDoc.setDescripcion("Oficio de aprobación del punto de monitoreo");
			oficioAprobacionDoc.setEstado(true);               
			documentoOficio = documentosFacade.guardarDocumentoAlfrescoSinProyecto(
							emisionesAtmosfericas.getCodigo(),
							"EMISIONES-ATMOSFERICAS",
							informacionProyecto.getId().longValue(),
							oficioAprobacionDoc,
							TipoDocumentoSistema.TIPO_DOCUMENTO_OFICIO_DE_APROBACION_PUNTO_MONITOREO,
							null);			
	    }
		
		if(existeObservaciones && detalleEmision.getId() == null){
			detalleEmision.setNumeroObservacion(numeroObservacion);
		}
		
		detalleEmision.setEmisionesAtmosfericas(emisionesAtmosfericas);
		detalleEmision.setEstado(true);
		if (documentoOficio != null && documentoOficio.getId() != null) {
			detalleEmision.setDocumentoOficioAprobacion(documentoOficio);
		}
		
		//Cambios para historial
		if(existeObservaciones){
			
			if(detalleEmision.getId() == null){
				detalleEmision.setNumeroObservacion(numeroObservacion);
			}else{
				List<DetalleEmisionesAtmosfericas> listaHistoricos = detalleEmisionesAtmosfericasFacade.findByIdHistory(detalleEmision.getId(), numeroObservacion);
				
				if(listaHistoricos == null || listaHistoricos.isEmpty()){
					
					if(detalleEmision.getNumeroObservacion() == null || detalleEmision.getNumeroObservacion() < numeroObservacion){
						detalleEmisionOriginal = detalleEmisionesAtmosfericasFacade.findById(detalleEmision.getId());
						
						//Considerar que si existe un cambio en tipo de combustible se debe guardar el historial de los datos
						//al igual que si existe cambio en fuente fija se debe guardar el combustible y los datos
						if(detalleEmisionOriginal.getCodigoPuntoMonitoreo().equals(detalleEmision.getCodigoPuntoMonitoreo()) && 
								detalleEmisionOriginal.getFuenteFijaCombustion().equals(detalleEmision.getFuenteFijaCombustion()) && 
								detalleEmisionOriginal.getPotencia().equals(detalleEmision.getPotencia()) && 
								detalleEmisionOriginal.getCatalogoUnidadesPotencia().equals(detalleEmision.getCatalogoUnidadesPotencia()) && 
								detalleEmisionOriginal.getMarca().equals(detalleEmision.getMarca()) && 
								detalleEmisionOriginal.getNumeroSerie().equals(detalleEmision.getNumeroSerie()) && 
								((detalleEmisionOriginal.getTipoCombustible() == null && detalleEmision.getTipoCombustible() == null) ||
								detalleEmisionOriginal.getTipoCombustible().equals(detalleEmision.getTipoCombustible())) && 
								detalleEmisionOriginal.getNumeroDuctosChimenea().equals(detalleEmision.getNumeroDuctosChimenea()) && 
								detalleEmisionOriginal.getAlturaLongitudChimenea().equals(detalleEmision.getAlturaLongitudChimenea()) && 
								detalleEmisionOriginal.getDiametroChimenea().equals(detalleEmision.getDiametroChimenea()) && 
								detalleEmisionOriginal.getUltimaPerturbacion().equals(detalleEmision.getUltimaPerturbacion()) && 
								detalleEmisionOriginal.getCuspideChimenea().equals(detalleEmision.getCuspideChimenea()) && 
								detalleEmisionOriginal.getDocumentoOficioAprobacion().equals(detalleEmision.getDocumentoOficioAprobacion())){
							
							//no existieron cambios				
							//validar para datos obtenidos segun el sector ya que pueden tener o no datos desde combustible o desde fuente				
						}else{
							
							DetalleEmisionesAtmosfericas detalleHistorial = (DetalleEmisionesAtmosfericas)SerializationUtils.clone(detalleEmisionOriginal);
							detalleHistorial.setId(null);
							detalleHistorial.setNumeroObservacion(numeroObservacion);
							detalleHistorial.setFechaCreacion(new Date());
							detalleHistorial.setFechaModificacion(null);
							detalleHistorial.setHistorial(true);
							detalleHistorial.setUsuarioModificacion(null);
							detalleHistorial.setIdRegistroOriginal(detalleEmision.getId());
							
							detalleEmisionesAtmosfericasFacade.save(detalleHistorial, loginBean.getUsuario());	
						}
					}
				}
			}							
		}
		//Fin historial
		
		detalleEmisionesAtmosfericasFacade.save(detalleEmision, loginBean.getUsuario());
		
		if(!facilidadesMonitoreoSeleccionadas.isEmpty()){
			List<String> facilidadesMonitoreoGuardar = new ArrayList<String>();
			List<FacilidadesMonitoreoEmisiones> facilidadesMonitoreoBdd = new ArrayList<FacilidadesMonitoreoEmisiones>();		
			
			facilidadesMonitoreoGuardar.addAll(facilidadesMonitoreoSeleccionadas);
			facilidadesMonitoreoBdd.addAll(listaFacilidadesMonitoreoEmisiones);
			
			for(FacilidadesMonitoreoEmisiones facilidad : listaFacilidadesMonitoreoEmisiones){
				for(String id : facilidadesMonitoreoSeleccionadas){
					if(facilidad.getCatalogoFacilidadesMonitoreo().getId().toString().equals(id)){
						facilidadesMonitoreoGuardar.remove(id);
						facilidadesMonitoreoBdd.remove(facilidad);
					}
				}
			}
			
			for(FacilidadesMonitoreoEmisiones facilidadMod : facilidadesMonitoreoBdd){
				
				//Historial FacilidadesMonitoreo
				if(existeObservaciones){
					
					if(facilidadMod.getNumeroObservacion() == null || facilidadMod.getNumeroObservacion() < numeroObservacion){
						FacilidadesMonitoreoEmisiones facilidadHis = (FacilidadesMonitoreoEmisiones) SerializationUtils.clone(facilidadMod);
						facilidadHis.setId(null);
						facilidadHis.setFechaCreacion(new Date());
						facilidadHis.setFechaModificacion(null);
						facilidadHis.setNumeroObservacion(numeroObservacion);
						facilidadHis.setHistorial(true);
						facilidadHis.setIdRegistroOriginal(facilidadMod.getId());
						
						facilidadesMonitoreoEmisionesFacade.save(facilidadHis, loginBean.getUsuario());
					}					
				}
				//fin historial
				
				facilidadMod.setEstado(false);
				facilidadesMonitoreoEmisionesFacade.save(facilidadMod, loginBean.getUsuario());
			}
			
			
			for(String index : facilidadesMonitoreoGuardar){		
				CatalogoFacilidadesMonitoreo catalogo = catalogoFacilidadesMonitoreoFacade.findById(Integer.valueOf(index));
				FacilidadesMonitoreoEmisiones facilidadEmision = new FacilidadesMonitoreoEmisiones();
				facilidadEmision.setCatalogoFacilidadesMonitoreo(catalogo);
				facilidadEmision.setDetalleEmisionesAtmosfericas(detalleEmision);
				facilidadEmision.setEstado(true);	
				
				if(existeObservaciones){
					facilidadEmision.setNumeroObservacion(numeroObservacion);
				}
				
				facilidadesMonitoreoEmisionesFacade.save(facilidadEmision, loginBean.getUsuario());
			}	
			
			listaFacilidadesMonitoreoEmisiones = facilidadesMonitoreoEmisionesFacade.findByDetail(detalleEmision);
		}
		//JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
	}
	
	private void guardarRegistroPaso2() throws ServiceException, CmisAlfrescoException{	
		try {
		
			if(existeObservaciones){
				List<EmisionesAtmosfericas> listaHistorial = emisionesAtmosfericasFacade.findHistoricos(emisionesAtmosfericas.getId());
				
				if(listaHistorial == null || listaHistorial.size() < numeroObservacion){
					EmisionesAtmosfericas emisionesBdd = emisionesAtmosfericasFacade.findById(emisionesAtmosfericas.getId());
					
					if(emisionesBdd.getNumeroObservacion() == null || emisionesAtmosfericas.getNumeroObservacion()< numeroObservacion){
							//guarda objeto
							EmisionesAtmosfericas emisionHistorial = (EmisionesAtmosfericas)SerializationUtils.clone(emisionesBdd);
							emisionHistorial.setId(null);
							emisionHistorial.setNumeroObservacion(numeroObservacion);
							emisionHistorial.setFechaCreacion(new Date());
							emisionHistorial.setFechaModificacion(null);
							emisionHistorial.setHistorial(true);
							emisionHistorial.setUsuarioModificacion(null);
							emisionHistorial.setIdRegistroOriginal(emisionesAtmosfericas.getId());
							emisionesAtmosfericasFacade.save(emisionHistorial, loginBean.getUsuario());
					}				
				}
			}			
					
		emisionesAtmosfericasFacade.save(emisionesAtmosfericas, loginBean.getUsuario());	
		
		if(existeObservaciones){
			//guardar historial del detalle
			
			List<DetalleEmisionesAtmosfericas> listaDetalleHistorial = detalleEmisionesAtmosfericasFacade.findByIdHistory(detalleEmision.getId(), numeroObservacion);
			
			if(listaDetalleHistorial == null || listaDetalleHistorial.isEmpty()){
				
				DetalleEmisionesAtmosfericas detalleBdd = detalleEmisionesAtmosfericasFacade.findById(detalleEmision.getId());
				
				if(detalleBdd != null  && (detalleBdd.getNumeroObservacion() == null || detalleBdd.getNumeroObservacion() < numeroObservacion)){
					if(detalleBdd.getCoordenadaX().equals(detalleEmision.getCoordenadaX()) && 
							detalleBdd.getCoordenadaY().equals(detalleEmision.getCoordenadaY()) &&
							detalleBdd.getLugarPuntoMuestreo().equals(detalleEmision.getLugarPuntoMuestreo()) && 
							detalleBdd.getEstadoFuenteDetalleCatalogo().equals(detalleEmision.getEstadoFuenteDetalleCatalogo()) && 
							((detalleBdd.getConsumoCombustible() == null && detalleEmision.getConsumoCombustible() == null) ||
							detalleBdd.getConsumoCombustible() != null && detalleEmision.getConsumoCombustible() != null && 
							detalleBdd.getConsumoCombustible().equals(detalleEmision.getConsumoCombustible())) && 
							((detalleBdd.getCatalogoUnidadesCombustible() == null && detalleEmision.getCatalogoUnidadesCombustible() == null) || 
							(detalleBdd.getCatalogoUnidadesCombustible() != null && detalleEmision.getCatalogoUnidadesCombustible() != null && 
							detalleBdd.getCatalogoUnidadesCombustible().equals(detalleEmision.getCatalogoUnidadesCombustible()))) && 
							((detalleBdd.getHorasFuncionamiento() == null && detalleEmision.getHorasFuncionamiento() == null) || 
							(detalleBdd.getHorasFuncionamiento() != null && detalleEmision.getHorasFuncionamiento() != null &&  
							detalleBdd.getHorasFuncionamiento().equals(detalleEmision.getHorasFuncionamiento()))) && 
							detalleBdd.getFechaInicioMonitoreo().equals(detalleEmision.getFechaInicioMonitoreo()) && 
							detalleBdd.getFechaFinMonitoreo().equals(detalleEmision.getFechaFinMonitoreo()) && 
							detalleBdd.getFrecuenciaMonitoreo().equals(detalleEmision.getFrecuenciaMonitoreo()) && 
							((detalleBdd.getTipoOperacionDetalleCatalogo() == null && detalleEmision.getTipoOperacionDetalleCatalogo() == null) ||
							(detalleBdd.getTipoOperacionDetalleCatalogo() != null && detalleEmision.getTipoOperacionDetalleCatalogo() != null && 
							detalleBdd.getTipoOperacionDetalleCatalogo().equals(detalleEmision.getTipoOperacionDetalleCatalogo()))) &&
							((detalleBdd.getVelocidadSalida() == null && detalleEmision.getTipoOperacionDetalleCatalogo() == null) || 
							(detalleBdd.getVelocidadSalida() != null && detalleEmision.getVelocidadSalida() != null && 
							detalleBdd.getVelocidadSalida().equals(detalleEmision.getVelocidadSalida()))) && 
							((detalleBdd.getJustificacion() == null && detalleEmision.getJustificacion() == null) ||
							(detalleBdd.getJustificacion() != null && detalleEmision.getJustificacion() != null && 
							detalleBdd.getJustificacion().equals(detalleEmision.getJustificacion())))
							){
						//son iguales
					}else{
						DetalleEmisionesAtmosfericas detalleHistorial = (DetalleEmisionesAtmosfericas)SerializationUtils.clone(detalleBdd);
						detalleHistorial.setId(null);
						detalleHistorial.setNumeroObservacion(numeroObservacion);
						detalleHistorial.setFechaCreacion(new Date());
						detalleHistorial.setFechaModificacion(null);
						detalleHistorial.setHistorial(true);
						detalleHistorial.setUsuarioModificacion(null);
						detalleHistorial.setIdRegistroOriginal(detalleEmision.getId());
						
						detalleEmisionesAtmosfericasFacade.save(detalleHistorial, loginBean.getUsuario());
						
					}	
				}					
			}	
		}
		
				
		if(detalleEmision.getEstadoFuenteDetalleCatalogo() != null){
			if(detalleEmision.getEstadoFuenteDetalleCatalogo().getDescripcion().equals("Inactiva")){	
				if(documentoJustificacionEstadoFuente != null && documentoJustificacionEstadoFuente.getId() == null){
					if (documentoJustificacionEstadoFuente != null && documentoJustificacionEstadoFuente.getContenidoDocumento() != null) {
						documentoJustificacionEstadoFuente.setIdTable(detalleEmision.getId());
						documentoJustificacionEstadoFuente.setNombreTabla(EmisionesAtmosfericas.class.getSimpleName());
						documentoJustificacionEstadoFuente.setDescripcion("Documento justificación fuente");
						documentoJustificacionEstadoFuente.setEstado(true);
						
						if(existeObservaciones && documentoJustificacionEstadoFuenteHistorico == null){
							documentoJustificacionEstadoFuente.setNumeroNotificacion(numeroObservacion);
						}
						
						Documento documento = documentosFacade.guardarDocumentoAlfrescoSinProyecto(emisionesAtmosfericas.getCodigo(),
								"EMISIONES-ATMOSFERICAS", detalleEmision.getId().longValue(),
								documentoJustificacionEstadoFuente, TipoDocumentoSistema.DOCUMENTO_JUSTIFICACION_ESTADO_FUENTE,
								null);
						
						if(existeObservaciones){						
								if(documentoJustificacionEstadoFuenteHistorico != null && documento.getId() != null){
									documentoJustificacionEstadoFuenteHistorico.setIdHistorico(documento.getId());
									documentoJustificacionEstadoFuenteHistorico.setNumeroNotificacion(numeroObservacion);
									documentosFacade.actualizarDocumento(documentoJustificacionEstadoFuenteHistorico);
								}		
							}						
						}else{
							JsfUtil.addMessageError("Adjunte un documento de justificación");
							return;
						}
					}/*else{
						JsfUtil.addMessageError("Seleccione el estado de la fuente");
						return;
					}*/	
				}
			}
			detalleEmisionesAtmosfericasFacade.save(detalleEmision, loginBean.getUsuario());
		//	JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
			
	private boolean guardarRegistroPaso3(){
		boolean correcto = true;
		try {
			if(existeObservaciones){
				List<DetalleEmisionesAtmosfericas> listaDetalleHistorial = detalleEmisionesAtmosfericasFacade.findByIdHistory(detalleEmision.getId(), numeroObservacion);
				
				if(listaDetalleHistorial == null || listaDetalleHistorial.isEmpty()){
					
					DetalleEmisionesAtmosfericas detalleBdd = detalleEmisionesAtmosfericasFacade.findById(detalleEmision.getId());
					
					if(detalleBdd.getNumeroObservacion() == null || detalleBdd.getNumeroObservacion() < numeroObservacion){
						if(detalleBdd.getAutorizacionEmisiones().equals(detalleEmision.getAutorizacionEmisiones())){
							//igual
						}else{
							DetalleEmisionesAtmosfericas detalleHistorial = (DetalleEmisionesAtmosfericas)SerializationUtils.clone(detalleBdd);
							detalleHistorial.setId(null);
							detalleHistorial.setNumeroObservacion(numeroObservacion);
							detalleHistorial.setFechaCreacion(new Date());
							detalleHistorial.setFechaModificacion(null);
							detalleHistorial.setHistorial(true);
							detalleHistorial.setUsuarioModificacion(null);
							detalleHistorial.setIdRegistroOriginal(detalleEmision.getId());
							
							detalleEmisionesAtmosfericasFacade.save(detalleHistorial, loginBean.getUsuario());
						}
					}			
				}
			}
			
			if(sector.equals("Hidrocarburos")){
				detalleEmision.setAutorizacionEmisiones(null);
			}

			detalleEmisionesAtmosfericasFacade.save(detalleEmision, loginBean.getUsuario());
					
			
			if(!listaDatosObtenidosTotal.isEmpty()){
				List<DatoObtenidoMedicion> listaDatosBdd = new ArrayList<DatoObtenidoMedicion>();
				List<DatoObtenidoMedicion> listaDatosBddAux = new ArrayList<DatoObtenidoMedicion>();
				
				listaDatosBdd = datoObtenidoMedicionFacade.findByEmisionAtmosferica(detalleEmision);
				listaDatosBddAux.addAll(listaDatosBdd);
				
				for(DatoObtenidoMedicion dato : listaDatosObtenidosTotal){
					if(dato.getId() != null){
						for(DatoObtenidoMedicion datoAux : listaDatosBdd){
							if(dato.getId().equals(datoAux.getId())){
								listaDatosBddAux.remove(datoAux);
							}
						}
					}
				}
								
				for(DatoObtenidoMedicion datoEliminar :listaDatosBddAux){
					
					if(existeObservaciones){
						if(datoEliminar.getNumeroObservacion() == null || datoEliminar.getNumeroObservacion() < numeroObservacion){
							DatoObtenidoMedicion datoHistorial = (DatoObtenidoMedicion)SerializationUtils.clone(datoEliminar);
							datoHistorial.setId(null);
							datoHistorial.setFechaModificacion(null);
							datoHistorial.setFechaCreacion(new Date());
							datoHistorial.setIdRegistroOriginal(datoEliminar.getId());
							datoHistorial.setHistorial(true);
							datoObtenidoMedicionFacade.save(datoHistorial, loginBean.getUsuario());
						}						
					}					

					datoEliminar.setDetalleEmisionesAtmosfericas(detalleEmision);
					datoEliminar.setEstado(false);					
					datoObtenidoMedicionFacade.save(datoEliminar, loginBean.getUsuario());
				}
				
				
				for(DatoObtenidoMedicion dato : listaDatosObtenidosTotal){
					if(existeObservaciones){
						if(dato.getId() == null){
							dato.setNumeroObservacion(numeroObservacion);
						}else{
							List<DatoObtenidoMedicion> listaHistorial = datoObtenidoMedicionFacade.findByIdRegistroOriginal(dato.getId());
							
							if(listaHistorial == null || listaHistorial.size() < numeroObservacion){
								if(dato.getNumeroObservacion() == null || dato.getNumeroObservacion() < numeroObservacion){
									
									DatoObtenidoMedicion datoBdd = datoObtenidoMedicionFacade.findById(dato.getId());
									
									if(datoBdd.getCatalogoMetodoEstimacion().equals(dato.getCatalogoMetodoEstimacion()) && 
										((datoBdd.getValorFlujoGasSeco() == null && dato.getValorFlujoGasSeco() == null) ||
										(datoBdd.getValorFlujoGasSeco() != null && dato.getValorFlujoGasSeco() != null && 
										datoBdd.getValorFlujoGasSeco().equals(dato.getValorFlujoGasSeco()))) && 
										((datoBdd.getValorPorcentajeOxigeno() == null && dato.getValorPorcentajeOxigeno() == null) || 
										(datoBdd.getValorPorcentajeOxigeno() != null && dato.getValorPorcentajeOxigeno() != null && 
										datoBdd.getValorPorcentajeOxigeno().equals(dato.getValorPorcentajeOxigeno()))) && 
										((datoBdd.getValorTemperatura() == null && dato.getValorTemperatura() == null) || 
										(datoBdd.getValorTemperatura() != null && dato.getValorTemperatura() != null && 
										datoBdd.getValorTemperatura().equals(dato.getValorTemperatura()))) && 
										((datoBdd.getValorIngresado() == null || dato.getValorIngresado() == null) ||
										(datoBdd.getValorIngresado() != null && dato.getValorIngresado() != null && 
										datoBdd.getValorIngresado().equals(dato.getValorIngresado())))									
										){
										//iguales
									}else{
										DatoObtenidoMedicion datoHistorial = (DatoObtenidoMedicion)SerializationUtils.clone(datoBdd);
										datoHistorial.setId(null);
										datoHistorial.setFechaModificacion(null);
										datoHistorial.setFechaCreacion(new Date());
										datoHistorial.setIdRegistroOriginal(dato.getId());
										datoHistorial.setNumeroObservacion(numeroObservacion);
										datoHistorial.setHistorial(true);
										datoObtenidoMedicionFacade.save(datoHistorial, loginBean.getUsuario());
									}										
								}	
							}											
						}
					}
					
					dato.setDetalleEmisionesAtmosfericas(detalleEmision);
					dato.setEstado(true);
					datoObtenidoMedicionFacade.save(dato, loginBean.getUsuario());
				}
				//JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			}else{
				if(mostrarLimites){
					correcto = false;
					JsfUtil.addMessageError("Ingrese valores en la tabla");
				}				
			}
		} catch (Exception e) {
			correcto = false;
			e.printStackTrace();
		}
		return correcto;
	}	
	
	private void guardarRegistroPaso4() {
		try {			
			if(listaLaboratorios != null && !listaLaboratorios.isEmpty()){
				for (DatosLaboratorio laboratorio : listaLaboratorios) {
					if(existeObservaciones){
						if(laboratorio.getId() == null){
							laboratorio.setNumeroObservacion(numeroObservacion);
						}else{
							DatosLaboratorio historial = datosLaboratorioFacade.getLaboratorioHistorialPorID(laboratorio.getId(), numeroObservacion);
							
							if(historial == null){
								
								if(laboratorio.getNumeroObservacion() == null || laboratorio.getNumeroObservacion() < numeroObservacion){
									
									DatosLaboratorio laboratorioBdd = datosLaboratorioFacade.findById(laboratorio.getId());
									
									if (laboratorioBdd.getRuc().equals(laboratorio.getRuc()) && 
										laboratorioBdd.getNombre().equals(laboratorio.getNombre()) && 
										laboratorioBdd.getNumeroRegistroSAE().equals(laboratorio.getNumeroRegistroSAE()) && 
										laboratorioBdd.getFechaVigenciaRegistro().equals(laboratorio.getFechaVigenciaRegistro())) {
										// son iguales
									} else {
										DatosLaboratorio laboratorioHistorial = (DatosLaboratorio)SerializationUtils.clone(laboratorioBdd);
										laboratorioHistorial.setId(null);
										laboratorioHistorial.setFechaCreacion(new Date());
										laboratorioHistorial.setFechaModificacion(null);
										laboratorioHistorial.setIdRegistroOriginal(laboratorio.getId());
										laboratorioHistorial.setHistorial(true);
										laboratorioHistorial.setUsuarioModificacion(null);
										laboratorioHistorial.setNumeroObservacion(numeroObservacion);
										
										datosLaboratorioFacade.saveLaboratorioEmisiones(laboratorioHistorial, loginBean.getUsuario());
									}
								}								
							}
						}
					}
					
					laboratorio.setEstado(true);
					DatosLaboratorio datosLab =  datosLaboratorioFacade.saveLaboratorioEmisiones(laboratorio, loginBean.getUsuario());

					if(laboratorio.getListaDocumentosLaboratorios() != null){
												
						//aqui se guarda los que quedan
						for(Documento documentoLab : laboratorio.getListaDocumentosLaboratorios()){
																					
							if (documentoLab.getId() == null && documentoLab.getContenidoDocumento() != null) {
								TipoDocumentoSistema tipoDoc;
								Integer idTipodoc = documentoLab.getTipoDocumento().getId();
								
								switch (idTipodoc) {
								case 4211:
									tipoDoc = TipoDocumentoSistema.TIPO_DOCUMENTO_INFORME_LABORATORIO;
									break;
								case 5901:
									tipoDoc = TipoDocumentoSistema.RETCE_SAE_LABORATORIO;
									break;
								case 5902:
									tipoDoc = TipoDocumentoSistema.RETCE_CADENA_CUSTODIA;
									break;
								case 5903:
									tipoDoc = TipoDocumentoSistema.RETCE_PROTOCOLO_MUESTREO;
									break;
								case 5904:
									tipoDoc = TipoDocumentoSistema.RETCE_ADICIONAL_LABORATORIO;
									break;
								default:
									tipoDoc = TipoDocumentoSistema.RETCE_ADICIONAL_LABORATORIO;
									break;
								}
								
								documentoLab.setIdTable(laboratorio.getId());
								documentoLab.setNombreTabla(DatosLaboratorio.class.getSimpleName());
								documentoLab.setEstado(true);				
								Documento doc = documentosFacade.guardarDocumentoAlfrescoSinProyecto(emisionesAtmosfericas.getCodigo(), "EMISIONES-ATMOSFERICAS", laboratorio.getId().longValue(), documentoLab, tipoDoc, null);
								
								if(existeObservaciones && documentoLab.getId() == null){
									doc.setNumeroNotificacion(numeroObservacion);
									documentosFacade.actualizarDocumento(doc);
								}	
								
								documentoLab.setId(doc.getId());
							}
						}		
					}					
				}
				
				if(listaDocumentosLaboratoriosEliminados != null && !listaDocumentosLaboratoriosEliminados.isEmpty()){
					for(Documento documento : listaDocumentosLaboratoriosEliminados){						
						if(documento.getId() != null){
							if(existeObservaciones){
								if(documento.getNumeroNotificacion() == null || documento.getNumeroNotificacion() < numeroObservacion){
									Documento documentoHistorial = (Documento)SerializationUtils.clone(documento);
									documentoHistorial.setId(null);
									documentoHistorial.setUsuarioModificacion(null);
									documentoHistorial.setNumeroNotificacion(numeroObservacion);
									documentoHistorial.setFechaCreacion(new Date());
									documentoHistorial.setFechaModificacion(new Date());
									documentoHistorial.setIdHistorico(documento.getId());
									
									documentosFacade.actualizarDocumento(documentoHistorial);
								}	
							}					
							
							documento.setEstado(false);
							documentosFacade.actualizarDocumento(documento);
						}					
					}
				}				
			}
			
			if(listaLaboratoriosEliminados != null && !listaLaboratoriosEliminados.isEmpty()){
				for(DatosLaboratorio labEliminado : listaLaboratoriosEliminados){
					if(labEliminado.getId() != null){
						
						if(existeObservaciones && (labEliminado.getNumeroObservacion() == null || labEliminado.getNumeroObservacion() < numeroObservacion)){
							DatosLaboratorio laboratorioHistorial = (DatosLaboratorio)SerializationUtils.clone(labEliminado);
							laboratorioHistorial.setId(null);
							laboratorioHistorial.setFechaCreacion(new Date());
							laboratorioHistorial.setFechaModificacion(null);
							laboratorioHistorial.setIdRegistroOriginal(labEliminado.getId());
							laboratorioHistorial.setHistorial(true);
							laboratorioHistorial.setNumeroObservacion(numeroObservacion);
							laboratorioHistorial.setUsuarioModificacion(null);
							
							datosLaboratorioFacade.saveLaboratorioEmisiones(laboratorioHistorial, loginBean.getUsuario());
						}						
						
						labEliminado.setEstado(false);
						datosLaboratorioFacade.save(labEliminado, loginBean.getUsuario());
					}
				}
				listaLaboratoriosEliminados = new ArrayList<DatosLaboratorio>();
				listaDocumentosLaboratoriosEliminados = new ArrayList<>();
			}
			//JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		} catch (Exception e) {
			e.printStackTrace();			
		}

	}
	
	private void guardarRegistroPaso5() {
		try {
			if(listaSustanciasRetce != null && !listaSustanciasRetce.isEmpty()){
				for(SubstanciasRetce sustancia : listaSustanciasRetce){
					
					if(sustancia.getDocumento() != null && sustancia.getDocumento().getId() == null && sustancia.getDocumento().getContenidoDocumento() != null){
												
						sustancia.getDocumento().setIdTable(detalleEmision.getId());
						sustancia.getDocumento().setNombreTabla(EmisionesAtmosfericas.class.getSimpleName());
						sustancia.getDocumento().setDescripcion("Documento de respaldo Laboratorio");
						sustancia.getDocumento().setEstado(true);
						Documento documento = documentosFacade.guardarDocumentoAlfrescoSinProyecto(emisionesAtmosfericas.getCodigo(),
								"EMISIONES-ATMOSFERICAS", detalleEmision.getId().longValue(),
								sustancia.getDocumento(), TipoDocumentoSistema.TIPO_DOCUMENTO_RESPALDO_LABORATORIO,
								null);
						
						if(existeObservaciones){
							documento.setNumeroNotificacion(numeroObservacion);
							documentosFacade.actualizarDocumento(documento);
						}	
						
						documento.setContenidoDocumento(sustancia.getDocumento().getContenidoDocumento());						
						sustancia.setDocumento(documento);
					}
					
					if(existeObservaciones){
						if(sustancia.getId() == null){
							sustancia.setNumeroObservacion(numeroObservacion);
						}else{
							
							if(sustancia.getNumeroObservacion() == null || sustancia.getNumeroObservacion() < numeroObservacion){
								SubstanciasRetce historial = sustanciaRetceFacade.getSustanciaHistorialPorID(sustancia.getId(), numeroObservacion);
								
								if(historial == null){
									SubstanciasRetce sustanciaBdd = sustanciaRetceFacade.findById(sustancia.getId());
									
									if(sustanciaBdd.getCatalogoMetodoEstimacion().equals(sustancia.getCatalogoMetodoEstimacion()) && 
										sustanciaBdd.getCatologSustanciasRetce().equals(sustancia.getCatologSustanciasRetce()) && 
										((sustanciaBdd.getDatosLaboratorio() == null && sustancia.getDatosLaboratorio() == null) ||
										(sustanciaBdd.getDatosLaboratorio() != null && sustancia.getDatosLaboratorio() != null && 
										sustanciaBdd.getDatosLaboratorio().equals(sustancia.getDatosLaboratorio()))) && 
										((sustanciaBdd.getDocumento() == null && sustancia.getDocumento() == null) || 
										(sustanciaBdd.getDocumento() != null && sustancia.getDocumento() != null && 
										sustanciaBdd.getDocumento().equals(sustancia.getDocumento()))) && 
										sustanciaBdd.getReporteToneladaAnio().equals(sustancia.getReporteToneladaAnio())
										){
										// iguales
									}else{
										
										SubstanciasRetce sustanciaHistorial =  (SubstanciasRetce) SerializationUtils.clone(sustanciaBdd);
										sustanciaHistorial.setId(null);
										sustanciaHistorial.setFechaCreacion(new Date());
										sustanciaHistorial.setFechaModificacion(null);
										sustanciaHistorial.setUsuarioModificacion(null);
										sustanciaHistorial.setHistorial(true);
										sustanciaHistorial.setIdRegistroOriginal(sustancia.getId());
										sustanciaHistorial.setNumeroObservacion(numeroObservacion);
										
										sustanciaRetceFacade.save(sustanciaHistorial, loginBean.getUsuario());
									}	
								}														
							}
						}
					}
										
					
					sustancia.setDetalleEmisionesAtmosfericas(detalleEmision);
					sustancia.setEstado(true);
					sustanciaRetceFacade.save(sustancia, loginBean.getUsuario());
				}				
			}
			
			if(listaSustanciasRetceEliminadas != null && !listaSustanciasRetceEliminadas.isEmpty()){
				for(SubstanciasRetce sustanciaEliminada : listaSustanciasRetceEliminadas){
					if(sustanciaEliminada.getId() != null){
						
						if(existeObservaciones){
							SubstanciasRetce historial = sustanciaRetceFacade.getSustanciaHistorialPorID(sustanciaEliminada.getId(), numeroObservacion);
							if(historial == null){
								SubstanciasRetce sustanciaHistorial = (SubstanciasRetce)SerializationUtils.clone(sustanciaEliminada);
								sustanciaHistorial.setId(null);
								sustanciaHistorial.setFechaCreacion(new Date());
								sustanciaHistorial.setFechaModificacion(null);
								sustanciaHistorial.setUsuarioModificacion(null);
								sustanciaHistorial.setHistorial(true);
								sustanciaHistorial.setIdRegistroOriginal(sustanciaEliminada.getId());
								sustanciaHistorial.setNumeroObservacion(numeroObservacion);
								
								sustanciaRetceFacade.save(sustanciaHistorial, loginBean.getUsuario());
							}
						}						
						
						sustanciaEliminada.setEstado(false);
						sustanciaRetceFacade.save(sustanciaEliminada, loginBean.getUsuario());
					}
				}
				
				listaSustanciasRetceEliminadas = new ArrayList<SubstanciasRetce>();
			}			
			//JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void guardarRegistroPaso6(){
		try {
			
			boolean cambioTecnico = false;
			
			if(existeObservaciones){
				TecnicoResponsable tecnicoHistorial = tecnicoResponsableFacade.findByIdRegistroOriginal(tecnicoResponsable.getId(), numeroObservacion);
				
				if(tecnicoHistorial == null && tecnicoResponsable.getId() != null){
					TecnicoResponsable tecnicoBdd = tecnicoResponsableFacade.findById(tecnicoResponsable.getId());
					
					if(tecnicoBdd.getIdentificador().equals(tecnicoResponsable.getIdentificador()) && 
							tecnicoBdd.getCorreo().equals(tecnicoResponsable.getCorreo()) && 
							((tecnicoBdd.getTelefono() == null && tecnicoResponsable.getTelefono() == null) ||
							(tecnicoBdd.getTelefono() != null && tecnicoResponsable.getTelefono() != null &&
							tecnicoBdd.getTelefono().equals(tecnicoResponsable.getTelefono()))) && 
							((tecnicoBdd.getCelular() == null && tecnicoResponsable.getCelular() == null) ||
							(tecnicoBdd.getCelular() != null && tecnicoResponsable.getCelular() != null && 
							tecnicoBdd.getCelular().equals(tecnicoResponsable.getCelular())))
							){
						//iguales
					}else{
						
						TecnicoResponsable tecnicoHist = (TecnicoResponsable)SerializationUtils.clone(tecnicoBdd);
						tecnicoHist.setId(null);
						tecnicoHist.setFechaCreacion(new Date());
						tecnicoHist.setFechaModificacion(null);
						tecnicoHist.setUsuarioModificacion(null);
						tecnicoHist.setHistorial(true);
						tecnicoHist.setIdRegistroOriginal(tecnicoResponsable.getId());
						tecnicoHist.setNumeroObservacion(numeroObservacion);
						
						tecnicoResponsableFacade.saveTecnico(tecnicoHist, loginBean.getUsuario());
						cambioTecnico = true;
					}					
				}
			}
						
			 TecnicoResponsable tecnico = tecnicoResponsableFacade.saveTecnico(tecnicoResponsable, loginBean.getUsuario());
			 
			 detalleEmision.getEmisionesAtmosfericas().setTecnicoResponsable(tecnico);			 
			 
			 if(cambioTecnico && existeObservaciones){
				 
				 List<DetalleEmisionesAtmosfericas> listaDetalleHistorial = detalleEmisionesAtmosfericasFacade.findByIdHistory(detalleEmision.getId(), numeroObservacion);
				 
				 if(listaDetalleHistorial == null || listaDetalleHistorial.isEmpty()){
					 DetalleEmisionesAtmosfericas detalleBdd = detalleEmisionesAtmosfericasFacade.findById(detalleEmision.getId());
					 
					 DetalleEmisionesAtmosfericas detalleHistorial = (DetalleEmisionesAtmosfericas)SerializationUtils.clone(detalleBdd);
					 detalleHistorial.setId(null);
					 detalleHistorial.setNumeroObservacion(numeroObservacion);
					 detalleHistorial.setFechaCreacion(new Date());
					 detalleHistorial.setFechaModificacion(null);
					 detalleHistorial.setHistorial(true);
					 detalleHistorial.setUsuarioModificacion(null);
					 detalleHistorial.setIdRegistroOriginal(detalleEmision.getId());
						
					 detalleEmisionesAtmosfericasFacade.save(detalleHistorial, loginBean.getUsuario());
				 }				 
			 }
			 detalleEmisionesAtmosfericasFacade.save(detalleEmision, loginBean.getUsuario());	
			 emisionesAtmosfericasFacade.save(detalleEmision.getEmisionesAtmosfericas(), loginBean.getUsuario());
			 //JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Boolean guardarRegistroPaso7(){
		try {
			/*if(listaDatosObtenidosTotal.isEmpty() && detalleEmision.getEstadoFuenteDetalleCatalogo().getDescripcion().equals("Activa")){
				JsfUtil.addMessageError("Ingrese datos en el paso 3");
				return;
			}
			
			if(listaLaboratorios.isEmpty() && detalleEmision.getEstadoFuenteDetalleCatalogo().getDescripcion().equals("Activa")){
				JsfUtil.addMessageError("Ingrese laboratorios en el paso 4");
				return;
			}*/
			
			if(documentoJustificativo != null && documentoJustificativo.getId() == null && documentoJustificativo.getContenidoDocumento() != null){
				documentoJustificativo.setIdTable(emisionesAtmosfericas.getId());
				documentoJustificativo.setNombreTabla(EmisionesAtmosfericas.class.getSimpleName());
				documentoJustificativo.setDescripcion("Justificativo Emision Atmosferica");
				documentoJustificativo.setEstado(true);
				documentoJustificativo = documentosFacade.guardarDocumentoAlfrescoSinProyecto(emisionesAtmosfericas.getCodigo(),
						"EMISIONES-ATMOSFERICAS", detalleEmision.getId().longValue(),
						documentoJustificativo, TipoDocumentoSistema.JUSTIFICATIVO_EMISION_ATMOSFERICA,
						null);
				
				if(existeObservaciones){
					if(documentoJustificativoHistorico != null && documento.getId() != null){
						documentoJustificativoHistorico.setIdHistorico(documento.getId());
						documentoJustificativoHistorico.setNumeroNotificacion(numeroObservacion);
						documentosFacade.actualizarDocumento(documentoJustificativoHistorico);
					}		
				}	
			}		

			if(limiteValidar) {
				if(documentoJustificativo == null || documentoJustificativo.getId() == null){
					JsfUtil.addMessageError("El documento acción correctiva es requerido");
					habiliatFinalizar = false;
					return false;
				}
			}
			habiliatFinalizar=true;
			//JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return true;
	}
	

	
	//Fin de metodos de guardado
	//TODO: ENVIAR FORMULARIOS
	private static final String[] TIPO_AREA={"","pc","dp","ea"};
	public void aceptar() throws JbpmException{
		try {
			if(!validarServicios())
			{	
				JsfUtil.addMessageWarning("Error al iniciar servicios, comuniquese con mesa de ayuda");
			}else{
			
			if(!guardarRegistroPaso7()) {
				return;
			}
			getImagenMapaAlfresco();
		
			boolean existeErro=false;
			
			if(mostrarLimites){
				if(listaDatosObtenidosTotal.isEmpty() && detalleEmision.getEstadoFuenteDetalleCatalogo().getDescripcion().equals("Activa")){
					JsfUtil.addMessageError("Ingrese datos en el paso 3");
					return;
				}
			}
			
			if(listaLaboratorios.isEmpty() && detalleEmision.getEstadoFuenteDetalleCatalogo().getDescripcion().equals("Activa")){
				JsfUtil.addMessageError("Ingrese laboratorios en el paso 4");
				return;
			}
			/*for (DetalleEmisionesAtmosfericas emisiones : listaDetalleEmisionesAtmosfericas) {
				if(emisiones.getEstadoFuenteDetalleCatalogo() == null ){
					existeErro=true;
					JsfUtil.addMessageError("Falta completar la información del punto de monitoreo "+emisiones.getCodigoPuntoMonitoreo());
				}else{
					if(emisiones.getEstadoFuenteDetalleCatalogo().getDescripcion().equals("Activa")){
						List<DatoObtenidoMedicion> listaDatosObtenidosAuxTotal = datoObtenidoMedicionFacade.findByEmisionAtmosferica(emisiones);
						if(listaDatosObtenidosAuxTotal.isEmpty()){
							existeErro=true;
							JsfUtil.addMessageError("Falta completar la información del punto de monitoreo "+emisiones.getCodigoPuntoMonitoreo());
						}
					}
				}
			}
			if(existeErro){
				return;
			}*/
			
			ProcesoRetceController procesoRetceController=JsfUtil.getBean(ProcesoRetceController.class);
			
			if(numeroObservacion == 0){
				if(procesoRetceController.iniciarProceso(emisionesAtmosfericas)){
					JsfUtil.addMessageInfo("Proyecto retce enviado");
					JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
				}else{
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_INICIAR_PROCESO);
					return;
				}
			}else{
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);
				Usuario usuarioCoordinador = new Usuario();
				String cedulaCoordinador=(String)variables.get("usuario_coordinador");				
				usuarioCoordinador = usuarioFacade.buscarUsuario(cedulaCoordinador);				
				
				String operador = "";
				if(JsfUtil.getLoggedUser().getNombre().length() == 13){
					Organizacion organizacion = organizacionFacade.buscarPorRuc(JsfUtil.getLoggedUser().getNombre());
					if(organizacion != null){
						operador = organizacion.getNombre();
					}else{
						operador = JsfUtil.getLoggedUser().getPersona().getNombre();
					}				
				}else{
					operador = JsfUtil.getLoggedUser().getPersona().getNombre();
				}
				String mensaje = mensajeNotificacionFacade.recuperarValorMensajeNotificacion("bodyNotificacionIngresoTramiteRetce", new Object[]{operador, tramite});
				Email.sendEmail(usuarioCoordinador, "Ingreso Trámite RETCE", mensaje,"",JsfUtil.getLoggedUser());
				
				JsfUtil.addMessageInfo("Proyecto retce enviado");
				JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
			}
			//hacer el historial
			emisionesAtmosfericas.setEnviado(true);
			emisionesAtmosfericas.setFechaReporte(new Date());
			emisionesAtmosfericasFacade.save(emisionesAtmosfericas, loginBean.getUsuario());
			ocultarFormulario(); 
			}  
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public Usuario getUsuarioCoordinador(){
		Usuario usuarioCoordinador = new Usuario();
		String roleKey="role.retce."+TIPO_AREA[emisionesAtmosfericas.getInformacionProyecto().getAreaSeguimiento().getTipoArea().getId()]+".coordinador.asignar.tarea";
		
		Area area = emisionesAtmosfericas.getInformacionProyecto().getAreaSeguimiento();
		TipoSector sector = emisionesAtmosfericas.getInformacionProyecto().getTipoSector();
		
		if(area.getTipoArea().getId()==1 &&(sector.getId()==1 || sector.getId()==2 || sector.getId()==4)){
			roleKey+=".sector."+sector.getId();
		}
						
		try {
			usuarioCoordinador = areaFacade.getUsuarioPorRolArea(roleKey,area);
			return usuarioCoordinador;
		} catch (Exception e) {
			LOG.error("No se ha encontrado usuario con en rol "+roleKey+" en el area "+area.getAreaName());		
			return null;
		}
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
	
	public void cancelarLaboratorio(){
		datoLaboratorio = new DatosLaboratorio();
		listaDocumentosLaboratorio = new ArrayList<Documento>();
		laboratorioModificado = false;
		documentoTipo1 = null;
		documentoTipo2 = null;
		documentoTipo3 = null;
		documentoTipo4 = null;
		documentoTipo5 = null;
		RequestContext.getCurrentInstance().execute("PF('adicionarLaboratorio').show()");
	}
		
	public StreamedContent getDocumentoSustancia(Documento documento){
		try {
			if (documento != null && documento.getId() != null) {
				List<Documento> documentos = documentosFacade.documentoXTablaIdXIdDoc(detalleEmision.getId(),
								EmisionesAtmosfericas.class.getSimpleName(),
								TipoDocumentoSistema.TIPO_DOCUMENTO_RESPALDO_LABORATORIO);

				if (documentos != null && !documentos.isEmpty()) {
					byte[] documentoContent = documentosFacade
							.descargarDocumentoAlfrescoQueryDocumentos(EmisionesAtmosfericas.class.getSimpleName(),
									detalleEmision.getId(),
									TipoDocumentoSistema.TIPO_DOCUMENTO_RESPALDO_LABORATORIO);

					Documento documentoEncontrado = documentos.get(0);
					documentoEncontrado.setContenidoDocumento(documentoContent);
					
					return getDocumentoInformeLaboratorioDownload(documentoEncontrado);
				}
			}else{
				return getDocumentoInformeLaboratorioDownload(documento);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
		
	public StreamedContent getDocumentoInformeLaboratorioDownload(Documento documento){		
		try {
			if (documento != null && documento.getContenidoDocumento()!=null) {
				StreamedContent streamedContent = new DefaultStreamedContent(
						new ByteArrayInputStream(
								documento.getContenidoDocumento()),
						documento.getMime(), documento.getNombre());
				return streamedContent;
			}
		} catch (Exception e) {			
			e.printStackTrace();
		}
		return null;
	}
	
	public StreamedContent getDocumentoJustificacionDownload(){
		return getDocumentoInformeLaboratorioDownload(documentoJustificacionEstadoFuente);
	}

	public StreamedContent getDocumentoAprobacion() {		
		try {
			if(oficioAprobacionDoc.getContenidoDocumento()==null && oficioAprobacionDoc.getId()!=null)
				oficioAprobacionDoc.setContenidoDocumento(documentosFacade.descargar(oficioAprobacionDoc.getIdAlfresco()));
		} catch (CmisAlfrescoException e) {
			e.printStackTrace();
		}
		return UtilDocumento.getStreamedContent(oficioAprobacionDoc);
	}
	
	boolean laboratorioModificado = false;
	public void cargarInformacionLaboratorio(DatosLaboratorio laboratorio){
		try {
			documentoTipo1 = null;
			documentoTipo2 = null;
			documentoTipo3 = null;
			documentoTipo4 = null;
			documentoTipo5 = null;
			laboratorioModificado = true;
			setDatoLaboratorio(laboratorio);			
			setListaDocumentosLaboratorio(laboratorio.getListaDocumentosLaboratorios());
			
			if(laboratorio.getListaDocumentosLaboratorios()==null || laboratorio.getListaDocumentosLaboratorios().size() == 0)
			{
				if(laboratorio.getId()==null)
					laboratorio.setListaDocumentosLaboratorios(new ArrayList<Documento>());
				else {
					List<Integer> listaTipos = new ArrayList<Integer>(); 
			    	listaTipos.add(TipoDocumentoSistema.TIPO_DOCUMENTO_INFORME_LABORATORIO.getIdTipoDocumento());
			    	listaTipos.add(TipoDocumentoSistema.RETCE_SAE_LABORATORIO.getIdTipoDocumento());
			    	listaTipos.add(TipoDocumentoSistema.RETCE_CADENA_CUSTODIA.getIdTipoDocumento());
			    	listaTipos.add(TipoDocumentoSistema.RETCE_PROTOCOLO_MUESTREO.getIdTipoDocumento());
			    	listaTipos.add(TipoDocumentoSistema.RETCE_ADICIONAL_LABORATORIO.getIdTipoDocumento());
			    	
			    	laboratorio.setListaDocumentosLaboratorios(documentosFacade.recuperarDocumentosPorTipo(laboratorio.getId(),
			    			DatosLaboratorio.class.getSimpleName(), listaTipos));
				}
			}
			
			for (Documento doc : laboratorio.getListaDocumentosLaboratorios()) {
				Integer idTipodoc = doc.getTipoDocumento().getId();
				
				switch (idTipodoc) {
				case 4211:
					documentoTipo1 = doc.getNombre();
					break;
				case 5901:
					documentoTipo2 = doc.getNombre();
					break;
				case 5902:
					documentoTipo3 = doc.getNombre();
					break;
				case 5903:
					documentoTipo4 = doc.getNombre();
					break;
				case 5904:
					documentoTipo5 = doc.getNombre();
					break;
				}
			}
			
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
	
	//SUSTANCIAS RETCE
	public void agregarSustancia(){
		boolean guardarSustancia = false;
		if(sustanciaRetceIngresada.getCatologSustanciasRetce() == null){
			JsfUtil.addMessageError("El campo Nombre de contaminante es requerido");
			return;
		}else if(sustanciaRetceIngresada.getCatalogoMetodoEstimacion() == null){
			JsfUtil.addMessageError("El campo método de estimación es requerido");
			return;
		}else if(sustanciaRetceIngresada.getReporteToneladaAnio() == null || 
				sustanciaRetceIngresada.getReporteToneladaAnio().equals(0.0)){
			JsfUtil.addMessageError("El campo Reporte es requerido");
			return;
		}else{			
			if(sustanciaRetceIngresada.getCatalogoMetodoEstimacion().getDescripcion().equals("Medición Directa")){
				if(sustanciaRetceIngresada.getDatosLaboratorio() == null){
					JsfUtil.addMessageError("El campo Escoja un laboratorio es requerido");
					return;
				}
			}else{
				if(!mostrarLaboratorios){
					if(sustanciaModificada){
						if(documentoRespaldoSustancias != null && documentoRespaldoSustancias.getId() != null){
							
						}else{
							if(documentoRespaldoSustancias == null || documentoRespaldoSustancias.getContenidoDocumento() == null){
								JsfUtil.addMessageError("Adjunte el documento de respaldo");
								return;
							}
						}						
					}else{
						if(documentoRespaldoSustancias == null || documentoRespaldoSustancias.getContenidoDocumento() == null){
							JsfUtil.addMessageError("Adjunte el documento de respaldo");
							return;
						}
					}					
				}
			}		
			guardarSustancia = true;
		}
								
		if(guardarSustancia){
			
			if(sustanciaModificada){
				
				if(documentoRespaldoSustancias != null && documentoRespaldoSustancias.getContenidoDocumento() != null){
					sustanciaRetceIngresada.setDocumento(documentoRespaldoSustancias);
				}
				
				sustanciaRetceIngresada = new SubstanciasRetce();
				documentoRespaldoSustancias = new Documento();
				idMetodoEstimacionSustancia = null;
				idCatalogoSusRetce = null;
				sustanciaModificada = false;
				
			}else{
				
				boolean existe = false;
				
				if(!listaSustanciasRetce.isEmpty()){
					for(SubstanciasRetce sustancia : listaSustanciasRetce){
						if(sustancia.getCatologSustanciasRetce().equals(sustanciaRetceIngresada.getCatologSustanciasRetce())){
							existe = true;
							break;
						}						
					}
				}			
				
				if(!existe){
					sustanciaRetceIngresada.setFuenteFija(detalleEmision.getFuenteFijaCombustion().getFuente());
					sustanciaRetceIngresada.setCodigoPuntoMonitoreo(detalleEmision.getCodigoPuntoMonitoreo());		
					
					if(documentoRespaldoSustancias != null && documentoRespaldoSustancias.getContenidoDocumento() != null){
						sustanciaRetceIngresada.setDocumento(documentoRespaldoSustancias);  
					}		
					
					listaSustanciasRetce.add(sustanciaRetceIngresada);
					
					sustanciaRetceIngresada = new SubstanciasRetce();
					documentoRespaldoSustancias = new Documento();
					idCatalogoSusRetce = null;
					idMetodoEstimacionSustancia = null;				
				}else{
					JsfUtil.addMessageError("La sustancia ya se encuentra ingresada.");
				}
			}
		}		
	}
	
	public void agregarLaboratorio(DatosLaboratorio laboratorioAsignado){
		sustanciaRetceIngresada.setDatosLaboratorio(laboratorioAsignado);
	}
	
	public void eliminarSustancia(SubstanciasRetce sustanciaEliminada){
		listaSustanciasRetce.remove(sustanciaEliminada);
		listaSustanciasRetceEliminadas.add(sustanciaEliminada);
	}
	
	public void cancelarSustancia(){
		sustanciaRetceIngresada = new SubstanciasRetce();
		documentoRespaldoSustancias = new Documento();
		idCatalogoSusRetce = null;
		idMetodoEstimacionSustancia = null;	
		sustanciaModificada = false;
	}
	
	boolean sustanciaModificada = false;
	public void cargarInformacionSustancia(SubstanciasRetce sustancia){
		try {
			sustanciaModificada = true;
			setSustanciaRetceIngresada(sustancia);	
			idCatalogoSusRetce = sustancia.getCatologSustanciasRetce().getId();
			idMetodoEstimacionSustancia = sustancia.getCatalogoMetodoEstimacion().getId();
						
			setDocumentoRespaldoSustancias(sustancia.getDocumento());
			
			if(sustancia.getCatalogoMetodoEstimacion().getDescripcion().equals("Medición Directa")){
				mostrarLaboratorios = true;
			}else{
				mostrarLaboratorios = false;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//INFORMACIÓN DEL TÉCNICO
	public void validarCedulaListener()
	{
		ListaContactosRetceController contactosController = JsfUtil.getBean(ListaContactosRetceController.class);
		String cedulaRuc=tecnicoResponsable.getIdentificador();
		tecnicoResponsable=contactosController.validarCedulaListener(cedulaRuc);
		if(tecnicoResponsable.getIdentificador()==null)
		{
			JsfUtil.addMessageError("Error en Cédula o Ruc no válido");
		}
	}
	
	public void eliminarDetalle(DetalleEmisionesAtmosfericas detalle){
		try {
			if(existeObservaciones){
				List<DetalleEmisionesAtmosfericas> listaDetalleHistorial = detalleEmisionesAtmosfericasFacade.findByIdHistory(detalle.getId(), numeroObservacion);
				 
				 if(listaDetalleHistorial == null || listaDetalleHistorial.isEmpty()){
					 DetalleEmisionesAtmosfericas detalleBdd = detalleEmisionesAtmosfericasFacade.findById(detalle.getId());
					 
					 DetalleEmisionesAtmosfericas detalleHistorial = (DetalleEmisionesAtmosfericas)SerializationUtils.clone(detalleBdd);
					 detalleHistorial.setId(null);
					 detalleHistorial.setNumeroObservacion(numeroObservacion);
					 detalleHistorial.setFechaCreacion(new Date());
					 detalleHistorial.setFechaModificacion(null);
					 detalleHistorial.setHistorial(true);
					 detalleHistorial.setUsuarioModificacion(null);
					 detalleHistorial.setIdRegistroOriginal(detalle.getId());
						
					 detalleEmisionesAtmosfericasFacade.save(detalleHistorial, loginBean.getUsuario());
				 }
			}
			
			detalle.setEstado(false);
			detalleEmisionesAtmosfericasFacade.save(detalle, loginBean.getUsuario());
			emisionesAtmosfericas.setEstado(false);
			emisionesAtmosfericasFacade.save(emisionesAtmosfericas, loginBean.getUsuario());
			listaEmisionesAtmosfericas = emisionesAtmosfericasFacade.findByInformacionProyecto(informacionProyecto);
			listaDetalleEmisionesAtmosfericas.remove(detalle);
			
		} catch (Exception e) {
			e.printStackTrace();
		}			
	}
	
	
	//metodos de documentos
	public void agregarDocumento(){
		try {
			
			if(documentoInformeLaboratorio != null && documentoInformeLaboratorio.getContenidoDocumento() != null){
				listaDocumentosLaboratorio.add(documentoInformeLaboratorio);
				documentoInformeLaboratorio = new Documento();
			}else{
				JsfUtil.addMessageError("Debe agregar un documento");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void eliminarDocumentoLaboratorio(Documento documento){
		listaDocumentosLaboratorio.remove(documento);
		listaDocumentosLaboratoriosEliminados.add(documento);
	}
	
	//Metodo historicos
	private Documento validarDocumentoHistorico(Documento documentoIngresado, List<Documento> documentosXEIA){
		try {
			List<Documento> documentosList = new ArrayList<>();
			for(Documento documento : documentosXEIA){
				if(documento.getIdHistorico() != null && 
						documento.getIdHistorico().equals(documentoIngresado.getId()) && 
						documento.getNumeroNotificacion().equals(numeroObservacion)){
					documentosList.add(documento);
				}
			}			
			
			if(documentosList != null && !documentosList.isEmpty()){		        
				return documentosList.get(0);
			}else{
				return documentoIngresado;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return documentoIngresado;
		}		
	}
	
	public StreamedContent getStream(Documento documento) throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (documento != null) {
			if (documento.getContenidoDocumento() == null) {
				documento.setContenidoDocumento(documentosFacade.descargar(documento.getIdAlfresco()));
			}
			content = new DefaultStreamedContent(new ByteArrayInputStream(documento.getContenidoDocumento()), "application/pdf");
			content.setName(documento.getNombre());
		}
		return content;

	}
	 
	 public boolean mostrarDetalle(){
		 if(existeObservaciones){
			 return true;
		 }else{
			 boolean mostrar = true;
			 if(emisionesAtmosfericas.getEnviado() != null && emisionesAtmosfericas.getEnviado()){
				 mostrar = false;
			 }else{
				 mostrar = true;
			 }
				
			 return mostrar;
		 }
	 }
	 
	 public boolean mostraAgregar(EmisionesAtmosfericas emision){
		 if(existeObservaciones){
			 return true;
		 }else{
			 boolean mostrar = true;
			 if(emision.getEnviado() != null && emision.getEnviado()){
				 mostrar = false;
			 }else
				 mostrar = true;
			 
			 return mostrar;
		 }		
	 }
	 
	 public String obtenerParametro(LimiteMaximoPermitido limite){
		 if(limite.getApplicacion() != null && !limite.getApplicacion().isEmpty()){
			 return limite.getParametro().getDescripcion() + " (" + limite.getApplicacion() + ")";
		 }else
			 return limite.getParametro().getDescripcion();
	 }
	 	 
	 private void encontrarAutorizacionPorFuente(FuenteFijaCombustion fuente){
		 try {
			 listaAutorizacionesEmisiones = new ArrayList<AutorizacionEmisiones>();
			 List<AutorizacionEmisiones> autorizacionesPorFuente = limiteMaximoPermitidoFacade.findBySoloFuenteFija(fuente);
			 
			 if(autorizacionesPorFuente != null && !autorizacionesPorFuente.isEmpty()){
				 listaAutorizacionesEmisiones.addAll(autorizacionesPorFuente);
			 }
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	 }
	 
	 private void encontrarAutorizacionPorCombustible(TipoCombustibleRetce combustible){
		 try {		
			 listaAutorizacionesEmisiones = new ArrayList<AutorizacionEmisiones>();
			 List<AutorizacionEmisiones> listaAutorizaciones = limiteMaximoPermitidoFacade.findBySoloCombustible(combustible);	
			 if(listaAutorizaciones != null && !listaAutorizaciones.isEmpty()){
				 listaAutorizacionesEmisiones.addAll(listaAutorizaciones);
			 }
			 
		} catch (Exception e) {
			e.printStackTrace();
		}		 
	 }

	 //Validacion de coordenas
	 public void validateRegistroMedicionesCoordenadas(FacesContext context, UIComponent validate, Object value) {
			List<FacesMessage> errorMessages = new ArrayList<>();			
			
			//validar coordenada
			if(detalleEmision.getCoordenadaX() != null && detalleEmision.getCoordenadaY() != null) {
				String coordenadaX = detalleEmision.getCoordenadaX().toString().replace(",", ".");
				String coordenadaY = detalleEmision.getCoordenadaY().toString().replace(",", ".");
				
				String mensaje = JsfUtil.validarCoordenadaPunto17S(coordenadaX, coordenadaY, emisionesAtmosfericas.getCodigo());
				
				if(mensaje != null)
					errorMessages.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR, mensaje, null));
			}
			
			
			if (!errorMessages.isEmpty())
				throw new ValidatorException(errorMessages);
	}

	 //Validacion de coordenas
		public boolean validarCoordenadas() {
			boolean correcto = false;
			if (detalleEmision.getCoordenadaX() != null && detalleEmision.getCoordenadaY() != null) {
				UbicacionesGeografica ubicacionFuente = new UbicacionesGeografica();
				String coordenadaX = detalleEmision.getCoordenadaX().toString().replace(",", ".");
				String coordenadaY = detalleEmision.getCoordenadaY().toString().replace(",", ".");
				String coordUbicacion = coordenadaX + " " + coordenadaY + " 17S";
				try {
					SVA_Reproyeccion_IntersecadoPortTypeProxy ws = new SVA_Reproyeccion_IntersecadoPortTypeProxy();
					Reproyeccion_resultado[] resultadoReproyeccion;
					Reproyeccion_entrada reproyeccion_entrada = new Reproyeccion_entrada(
							Constantes.getUserWebServicesSnap(),  informacionProyecto.getCodigoRetce(), "WGS84", coordUbicacion);
					resultadoReproyeccion = ws.reproyeccion(reproyeccion_entrada);
					if (resultadoReproyeccion[0].getCoordenada()[0].getError() == null) {
						String[] coordenadasReproyectadas = resultadoReproyeccion[0].getCoordenada()[0].getXy().split(" ");
						coordUbicacion = coordenadasReproyectadas[0] + " " + coordenadasReproyectadas[1];
						Intersecado_resultado[] resultadoInterseccion;
						Intersecado_entrada inter = new Intersecado_entrada(
								Constantes.getUserWebServicesSnap(),  informacionProyecto.getCodigoRetce(), "pu", coordUbicacion, "dp");
						resultadoInterseccion = ws.interseccion(inter);
						if (resultadoInterseccion[0].getInformacion().getError() != null) {
							JsfUtil.addMessageError(resultadoInterseccion[0].getInformacion().getError().toString());
							correcto = false;
						} else {
							for (Intersecado_capa intersecado_capa : resultadoInterseccion[0].getCapa()) {
								String capaNombre = intersecado_capa.getNombre();
								if (intersecado_capa.getError() != null) {
									JsfUtil.addMessageError(intersecado_capa.getError().toString());
									correcto = false;
								}
								Intersecado_coordenada[] intersecadoCoordenada = intersecado_capa.getCruce();
								if (intersecadoCoordenada != null) {
									for (Intersecado_coordenada intersecado_coordenad : intersecadoCoordenada) {
										if (capaNombre.equals("dpa")) {
											String parroquia = intersecado_coordenad.getObjeto();
											List<UbicacionesGeografica> lista = ubicacionGeograficaFacade.buscarUbicacionGeograficaPorParroquia(parroquia);
											ubicacionFuente = lista.get(0);
										}
									}
								}
							}
							if (ubicacionFuente != null && ubicacionFuente.getId() != null) {
								correcto = true;
								detalleEmision.setUbicacionGeografica(ubicacionFuente);
							}
						}
					} else {
						JsfUtil.addMessageError("Error en la reproyección de las coordenadas.");
					}
				} catch (Exception e) {
					JsfUtil.addMessageError("Error en la verificación de las coordenadas.");
					e.printStackTrace();
				}
			}
			return correcto;
		}
	 public StreamedContent generarPdfInformacion(EmisionesAtmosfericas emision){
			try{
				seleccionarEmision(emision);
				String datos = devolverDetalle(), nombreReporte = "reporte_emisiones_atmosfericas_"+emisionesAtmosfericas.getCodigo();
				File fileReporte = generarFichero("cadenaHtml", datos,nombreReporte , true, null);
				if(fileReporte == null){
					JsfUtil.addMessageError("Error no se pudo generar el documento con la informacion ingresada.");
					return null;
				}
				return new DefaultStreamedContent(new FileInputStream(fileReporte), "application/pdf", nombreReporte+".pdf");
			} catch (Exception e) {
				LOG.error(e, e);
				JsfUtil.addMessageError("Error al realizar la operación.");
			}
			return null;
		}
	 
	 public String devolverDetalle(){
		 StringBuilder tablaStandar = new StringBuilder();
		 if(listaDetalleEmisionesAtmosfericas == null || listaDetalleEmisionesAtmosfericas.size() == 0){
			 return "";
		 }
		 
		tablaStandar.append("<table align=\"left\" border=\"0\" cellpadding=\"1\" cellspacing=\"1\" style=\"width:95%\">");
		tablaStandar.append("<tbody><tr>");
		tablaStandar.append("<td colspan=\"7\" style=\"width: 100%; text-align: center\"> <div style=\"font-size: small;font-weight: bold;background-color: inherit;\">");
		tablaStandar.append("REGISTRO DE EMISIONES Y TRANSFERENCIA DE CONTAMINANTES EN EL ECUADOR");
		tablaStandar.append("<br/>&nbsp;</div></td>");
		tablaStandar.append("</tr>");
		tablaStandar.append("<tr>");
		tablaStandar.append("<td colspan=\"4\" style=\"width: 100%; text-align: center\"> <div style=\"font-size: small;font-weight: bold;background-color: inherit;\">");
		tablaStandar.append("REPORTE DE EMISIONES ATMOSFÉRICAS");
		tablaStandar.append("<br/>&nbsp;</div></td>");
		tablaStandar.append("</tr>");
		tablaStandar.append("</tbody></table><br/>");
		tablaStandar.append("<table align=\"left\" border=\"1\" cellpadding=\"1\" cellspacing=\"1\" style=\"width:95%\">");
		tablaStandar.append("<tr>");
		tablaStandar.append("<td style=\"width: 10%; text-align: left\"> <div style=\"font-size: small;font-weight: bold;background-color: inherit;\">");
		tablaStandar.append("Código Trámite");
		tablaStandar.append("<br/></div></td>");
		tablaStandar.append("<td style=\"width: 20%; text-align: left\"> <div style=\"font-size: small;font-weight: bold;background-color: inherit;\">");
		tablaStandar.append("Código del punto de monitoreo aprobado");
		tablaStandar.append("<br/></div></td>");
		tablaStandar.append("<td style=\"width: 30%; text-align: left\"> <div style=\"font-size: small;font-weight: bold;background-color: inherit;\">");
		tablaStandar.append("Fuente Fija de Combustión");
		tablaStandar.append("<br/></div></td>");
		tablaStandar.append("<td style=\"width: 10%; text-align: left\"> <div style=\"font-size: small;font-weight: bold;background-color: inherit;\">");
		tablaStandar.append("Estado de la fuente");
		tablaStandar.append("<br/></div></td>");
		tablaStandar.append("<td style=\"width: 10%; text-align: left\"> <div style=\"font-size: small;font-weight: bold;background-color: inherit;\">");
		tablaStandar.append("Número de la serie");
		tablaStandar.append("<br/></div></td>");
		tablaStandar.append("<td style=\"width: 10%; text-align: left\"> <div style=\"font-size: small;font-weight: bold;background-color: inherit;\">");
		tablaStandar.append("Locación");
		tablaStandar.append("<br/></div></td>");
		tablaStandar.append("<td style=\"width: 10%; text-align: left\"> <div style=\"font-size: small;font-weight: bold;background-color: inherit;\">");
		tablaStandar.append("Frecuencia de Monitoreo");
		tablaStandar.append("<br/></div></td>");
		tablaStandar.append("</tr>");

		 for (DetalleEmisionesAtmosfericas objDetalleAtmosferico: listaDetalleEmisionesAtmosfericas) {
				tablaStandar.append("<tr>");
				tablaStandar.append("<td style=\" text-align: left\"> <div style=\"font-size: small;background-color: inherit;\">");
				tablaStandar.append(objDetalleAtmosferico.getEmisionesAtmosfericas().getCodigo());
				tablaStandar.append("<br/></div></td>");
				tablaStandar.append("<td style=\"text-align: left\"> <div style=\"font-size: small;background-color: inherit;\">");
				tablaStandar.append(objDetalleAtmosferico.getCodigoPuntoMonitoreo());
				tablaStandar.append("<br/></div></td>");
				tablaStandar.append("<td style=\" text-align: left\"> <div style=\"font-size: small;background-color: inherit;\">");
				tablaStandar.append((objDetalleAtmosferico.getFuenteFijaCombustion() != null ?objDetalleAtmosferico.getFuenteFijaCombustion().getFuente():""));
				tablaStandar.append("<br/></div></td>");
				tablaStandar.append("<td style=\" text-align: left\"> <div style=\"font-size: small;background-color: inherit;\">");
				tablaStandar.append((objDetalleAtmosferico.getEstadoFuenteDetalleCatalogo() != null ? objDetalleAtmosferico.getEstadoFuenteDetalleCatalogo().getDescripcion():""));
				tablaStandar.append("<br/></div></td>");
				tablaStandar.append("<td style=\" text-align: left\"> <div style=\"font-size: small;background-color: inherit;\">");
				tablaStandar.append(objDetalleAtmosferico.getNumeroSerie());
				tablaStandar.append("<br/></div></td>");
				tablaStandar.append("<td style=\" text-align: left\"> <div style=\"font-size: small;background-color: inherit;\">");
				tablaStandar.append(objDetalleAtmosferico.getLugarPuntoMuestreo());
				tablaStandar.append("<br/></div></td>");
				tablaStandar.append("<td style=\" text-align: left\"> <div style=\"font-size: small;background-color: inherit;\">");
				tablaStandar.append((objDetalleAtmosferico.getFrecuenciaMonitoreo() != null ? objDetalleAtmosferico.getFrecuenciaMonitoreo().getDescripcion():""));
				tablaStandar.append("<br/></div></td>");
				tablaStandar.append("</tr>");
		}
		tablaStandar.append("</tbody></table><br/>");
		return tablaStandar.toString();
	 }
	 

	    public static File generarFichero(final String cadenaHtml,
	                                      final String entityReporte, final String nombreReporte,
	                                      final Boolean mostrarNumeracionPagina, Area area) {
	        Document document = null;
	        PdfWriter writer = null;
	        OutputStream fileOutputStream = null;
	        File file = null;
	        String archivo="";
	        try {
	            String buf = entityReporte;
	            file = File.createTempFile(nombreReporte, ".pdf");
	            //document = new Document(PageSize.A4, 36, 36, 100, 70);
	            document = new Document(PageSize.A4.rotate(), 36, 36, 80, 70);

	            fileOutputStream = new FileOutputStream(file);
	            writer = PdfWriter.getInstance(document, fileOutputStream);
	            writer.createXmpMetadata();
				writer.setPageEvent(new HeaderFichaAmbientalCoa(null, mostrarNumeracionPagina));
				
	            document.open();
	            
	            PdfContentByte cb = writer.getDirectContent();
	            archivo=nombreReporte+new Date().getTime()+".pdf";
	            createPdfHtml(buf, System.getProperty("java.io.tmpdir")+"/"+archivo);
				
	            PdfReader readerF = new PdfReader(System.getProperty("java.io.tmpdir")+"/"+archivo);
	            Integer totalPages = readerF.getNumberOfPages();
	            for (int i = 1; i <= totalPages; i++) {
	                PdfImportedPage page = writer.getImportedPage(readerF, i);
	                document.newPage();
	                cb.addTemplate(page, 0, 0);
	            }
	        } catch (Exception e) {

	        } finally {
	            if (document != null && document.isOpen()) {
	                document.close();                
	            }
	            try {

	                if (fileOutputStream != null) {
	                    fileOutputStream.close();
	                }
	            } catch (IOException e) {
	            }
	            if (writer != null && !writer.isCloseStream()) {
	                writer.close();
	            }
	        }
	        if (Constantes.getDocumentosBorrador()) {
	        	UtilFichaMineria.deleteFileTmp(nombreReporte+".pdf");
	            return file;
	        } else {
	        	UtilFichaMineria.deleteFileTmp(nombreReporte+".pdf");
	            return file;
	        }

	    }
	    
	    public static void createPdfHtml(String html, String destinoFile) throws IOException {
	    	com.itextpdf.kernel.geom.PageSize pageSize ;
	    	pageSize =  com.itextpdf.kernel.geom.PageSize.A4.rotate();
	        try(
	        		com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter(destinoFile);
	                PdfDocument pdfDoc = new PdfDocument(writer);
	                com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdfDoc);   )
	        {
	            pdfDoc.setTagged();
	            pdfDoc.setDefaultPageSize(pageSize);
	            //para el margen superior 70
	            document.setMargins(70, 36, 70, 36);
	            List<IElement> elements = HtmlConverter.convertToElements(html);
	            for (IElement element : elements)
	            {
	                if (element instanceof IBlockElement)
	                    document.add((IBlockElement) element);
	            }
	        }
	    }

	    static class HeaderFichaAmbientalCoa extends PdfPageEventHelper {

	        String[] params;
	        Boolean mostrarNumeracionPagina;
	        Boolean mostrarDireccionMae;
	        /**
	         * The template with the total number of pages.
	         */
	        PdfTemplate total;

	        public HeaderFichaAmbientalCoa() {
	        }

	        public HeaderFichaAmbientalCoa(String[] params,
	                                           Boolean mostrarNumeracionPagina) {
	            this.params = params;
	            this.mostrarNumeracionPagina = mostrarNumeracionPagina;
	        }

	        /**
	         * Creates the PdfTemplate that will hold the total number of pages.
	         *
	         * @see com.itextpdf.text.pdf.PdfPageEventHelper#onOpenDocument(com.itextpdf.text.pdf.PdfWriter,
	         * com.itextpdf.text.Document)
	         */
	        public void onOpenDocument(PdfWriter writer, Document document) {
	            total = writer.getDirectContent().createTemplate(30, 14);
	        }

	        public void onCloseDocument(PdfWriter writer, Document document) {
	            ColumnText.showTextAligned(total, Element.ALIGN_LEFT, new Phrase(
	                    String.valueOf(writer.getPageNumber() - 1)), 2, 2, 0);
	        }


	        public void onEndPage(PdfWriter writer, Document document) {
	            Image imghead = null;
	            Image imgbackground = null;
	            String nombre_logo ="";
	            try {
                    imghead = Image.getInstance(getRecursoImage("logo_mae.png"));
                    imghead.setAbsolutePosition(0, 0);
        			imghead.scalePercent(40);
        			imghead.setAlignment(Element.ALIGN_CENTER);
        			
        			imgbackground = Image.getInstance(getRecursoImage("escudoEcua.png"));
        			imgbackground.setAbsolutePosition(0, 220);
        			imgbackground.scalePercent(100);
        			document.add(imgbackground);

                    Image imgPie = Image.getInstance(getRecursoImage("pie.png"));
                    imgPie.setAbsolutePosition(0, 5);
                    imgPie.scalePercent(80);
        			document.add(imgPie);

	                PdfPTable tableHeader = new PdfPTable(4);
	                tableHeader.setWidths(new int[]{14, 10, 10, 14});
	                tableHeader.setTotalWidth(2700);
	                tableHeader.setLockedWidth(true);
	                tableHeader.getDefaultCell().setFixedHeight(20);
	                tableHeader.getDefaultCell().setBorder(Rectangle.NO_BORDER);
	                tableHeader.setHorizontalAlignment(Element.ALIGN_LEFT);
	                PdfPCell cellImagen = new PdfPCell(imghead, false);
	                if(imghead != null)
	                	cellImagen.addElement(imghead);
	                cellImagen.setBorder(Rectangle.NO_BORDER);
	                tableHeader.addCell(cellImagen);
	                tableHeader.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
	                PdfPCell cellVacia = new PdfPCell();
	                cellVacia.setBorder(Rectangle.NO_BORDER);
	                tableHeader.addCell(cellVacia);
	                tableHeader.addCell(cellVacia);
	                tableHeader.addCell(cellVacia);
	                //tableHeader.writeSelectedRows(0, -1, 34, 815,  writer.getDirectContent());
	                tableHeader.writeSelectedRows(0, -1, 34, 590,writer.getDirectContent());
	                if (params != null) {
	                    int top = 800;
	                    Font font = new Font(Font.FontFamily.HELVETICA, 8);
	                    font.setStyle(Font.BOLD);
	                    for (String s : params) {
	                        PdfPTable tableHeader1 = new PdfPTable(4);
	                        Phrase letra = new Phrase(s, font);
	                        tableHeader1.setWidths(new int[]{14, 10, 10, 14});
	                        tableHeader1.setTotalWidth(527);
	                        tableHeader1.setLockedWidth(true);
	                        tableHeader1.getDefaultCell().setFixedHeight(20);
	                        tableHeader1.getDefaultCell().setBorder(
	                                Rectangle.NO_BORDER);
	                        tableHeader1.getDefaultCell().setHorizontalAlignment(
	                                Element.ALIGN_RIGHT);

	                        tableHeader1.addCell(cellVacia);
	                        tableHeader1.addCell(cellVacia);
	                        tableHeader1.addCell(cellVacia);
	                        tableHeader1.addCell(letra);
	                        tableHeader1.writeSelectedRows(0, -1, 36, top,
	                                writer.getDirectContent());
	                        top -= 10;
	                    }
	                }


	            } catch (DocumentException de) {

	            } catch (MalformedURLException e) {
	                LOG.error(e, e);
	            } catch (IOException e) {
	                LOG.error(e, e);
	            }
	        }
	    }


	private static URL getRecursoImage(String nombreImagen) {
	    ServletContext servletContext = (ServletContext) FacesContext
		    .getCurrentInstance().getExternalContext().getContext();
	    try {
		return servletContext.getResource("/resources/images/"
		        + nombreImagen);
	    } catch (MalformedURLException e) {
		e.printStackTrace();
		return null;
	    }
	}
	//TODO: FUNCION PARA VALIDAR LOS LIMITES
	public Boolean validarValorLimite(LimiteMaximoPermitido limite){
		excedeLimiteFuente = false;
		try {
			listaDatosTotalAux = new ArrayList<DatoObtenidoMedicion>();						
			listaDatosTotalAux.addAll(listaDatosObtenidosTotal);
			
			if(!listaDatosTotalAux.isEmpty() && limite != null){
				for(DatoObtenidoMedicion dato : listaDatosTotalAux){
					if(dato.getLimiteMaximoPermitido().getParametro().equals(limite.getParametro()) && 
							((dato.getLimiteMaximoPermitido().getApplicacion() == null && limite.getApplicacion() == null) || 
							(dato.getLimiteMaximoPermitido().getApplicacion() != null && limite.getApplicacion() != null && 
								dato.getLimiteMaximoPermitido().getApplicacion().equals(limite.getApplicacion())))){
						if(dato.getLimiteMaximoPermitido().getValor() > 0 ){
							if(dato.getLimiteMaximoPermitido().getValor() < dato.getValorIngresado()){
								excedeLimiteFuente = true;
								limiteValidar=true;
								break;
							}
						}
					}
				}
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return excedeLimiteFuente;
	}
	public void getImagenMapaAlfresco(){
		try {
			
			ResponseImagen resCer = new ResponseImagen();
			resCer=wsMapas.getGenerarMapaImagenWSPort().generarImagenMapaRetce(emisionesAtmosfericas.getCodigo().toString(), 1);
			if(resCer.getWorkspaceAlfresco()==null)
			{
				JsfUtil.addMessageWarning("Error al generar la imagen de puntos de coordenadas");
			}
			else
			{
				//imagenCoordenadas
				TipoDocumento tipoDocumento = new TipoDocumento();
				tipoDocumento.setId(TipoDocumentoSistema.RETCE_IMAGEN_COORDENADAS.getIdTipoDocumento());
				imagenCoordenadas.setIdAlfresco(resCer.getWorkspaceAlfresco().toString());
				imagenCoordenadas.setIdTable(emisionesAtmosfericas.getId());
				imagenCoordenadas.setNombreTabla(EmisionesAtmosfericas.class.getSimpleName());
				imagenCoordenadas.setDescripcion("mapa-puntosmonitoreo_"+emisionesAtmosfericas.getCodigo());
				imagenCoordenadas.setNombre("mapa-puntosmonitoreo_"+emisionesAtmosfericas.getCodigo());
				imagenCoordenadas.setMime("image/jpg");
				imagenCoordenadas.setTipoDocumento(tipoDocumento);
				imagenCoordenadas.setExtesion(".jpg");
				imagenCoordenadas.setEstado(true);
				imagenCoordenadas=documentosFacade.actualizarDocumento(imagenCoordenadas);
			}
		}catch(Exception e){
			JsfUtil.addMessageWarning("Error al generar la imagen de puntos de coordenadas");
			LOG.error(e);
		}
	}
	public boolean validarServicios()
	{
		boolean estado=false;
		try {			
			wsMapas = new GenerarMapaImagenWS_Service(new URL(Constantes.getGenerarMapaImagenWS()));
			estado=true;
		} catch (WebServiceException e) {
			estado=false;
			e.printStackTrace();
			System.out.println("Servicio no disponible ---> "+Constantes.getGenerarMapaImagenWS());			
		} catch (MalformedURLException e) {
			estado=false;
		}
		return estado;
	}	
}
