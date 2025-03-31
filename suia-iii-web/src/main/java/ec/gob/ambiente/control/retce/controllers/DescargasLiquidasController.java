package ec.gob.ambiente.control.retce.controllers;

import index.Intersecado_capa;
import index.Intersecado_coordenada;
import index.Intersecado_entrada;
import index.Intersecado_resultado;
import index.Reproyeccion_entrada;
import index.Reproyeccion_resultado;
import index.SVA_Reproyeccion_IntersecadoPortTypeProxy;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
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
import javax.xml.ws.WebServiceException;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.component.wizard.Wizard;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.cliente.ConsultaRucCedula;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.mapa.webservices.GenerarMapaImagenWS_Service;
import ec.gob.ambiente.mapa.webservices.GenerarMapaWSService;
import ec.gob.ambiente.mapa.webservices.ResponseCertificado;
import ec.gob.ambiente.mapa.webservices.ResponseImagen;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.retce.model.CaracteristicasPuntoMonitoreo;
import ec.gob.ambiente.retce.model.CaracteristicasPuntoMonitoreoTabla;
import ec.gob.ambiente.retce.model.CatalogoFrecuenciaMonitoreo;
import ec.gob.ambiente.retce.model.CatalogoMetodoEstimacion;
import ec.gob.ambiente.retce.model.CatalogoSustanciasRetce;
import ec.gob.ambiente.retce.model.CatalogoTipoCuerpoReceptor;
import ec.gob.ambiente.retce.model.CatalogoTipoCuerpoReceptorCaracteristicasPunto;
import ec.gob.ambiente.retce.model.CatalogoTipoDescarga;
import ec.gob.ambiente.retce.model.CatalogoTratamientoAguas;
import ec.gob.ambiente.retce.model.DatoObtenidoMedicionDescargas;
import ec.gob.ambiente.retce.model.DatosLaboratorio;
import ec.gob.ambiente.retce.model.DescargasLiquidas;
import ec.gob.ambiente.retce.model.DetalleDescargasLiquidas;
import ec.gob.ambiente.retce.model.DetalleDescargasLiquidasTratamientoAguas;
import ec.gob.ambiente.retce.model.DetalleEmisionesAtmosfericas;
import ec.gob.ambiente.retce.model.EmisionesAtmosfericas;
import ec.gob.ambiente.retce.model.FaseRetce;
import ec.gob.ambiente.retce.model.FuenteFijaCombustion;
import ec.gob.ambiente.retce.model.InformacionProyecto;
import ec.gob.ambiente.retce.model.LugarDescarga;
import ec.gob.ambiente.retce.model.OficioPronunciamientoRetce;
import ec.gob.ambiente.retce.model.ParametrosTablas;
import ec.gob.ambiente.retce.model.SubstanciasRetce;
import ec.gob.ambiente.retce.model.TecnicoResponsable;
import ec.gob.ambiente.retce.model.TipoMuestra;
import ec.gob.ambiente.retce.services.CaracteristicasPuntoMonitoreoFacade;
import ec.gob.ambiente.retce.services.CatalogoFrecuenciaMonitoreoFacade;
import ec.gob.ambiente.retce.services.CatalogoMetodoEstimacionFacade;
import ec.gob.ambiente.retce.services.CatalogoSustanciasRetceFacade;
import ec.gob.ambiente.retce.services.CatalogoTipoCuerpoCaracteristicasFacade;
import ec.gob.ambiente.retce.services.CatalogoTipoCuerpoReceptorFacade;
import ec.gob.ambiente.retce.services.CatalogoTipoDescargaFacade;
import ec.gob.ambiente.retce.services.CatalogoTratamientoAguasFacade;
import ec.gob.ambiente.retce.services.DatoObtenidoMedicionDescargasFacade;
import ec.gob.ambiente.retce.services.DatosLaboratorioFacade;
import ec.gob.ambiente.retce.services.DescargasLiquidasFacade;
import ec.gob.ambiente.retce.services.DetalleDescargasLiquidasFacade;
import ec.gob.ambiente.retce.services.DetalleDescargasTratamientoFacade;
import ec.gob.ambiente.retce.services.FaseRetceFacade;
import ec.gob.ambiente.retce.services.InformacionProyectoFacade;
import ec.gob.ambiente.retce.services.LugarDescargaFacade;
import ec.gob.ambiente.retce.services.OficioRetceFacade;
import ec.gob.ambiente.retce.services.ParametrosDescargasLiquidasFacade;
import ec.gob.ambiente.retce.services.SubstanciasRetceFacade;
import ec.gob.ambiente.retce.services.TecnicoResponsableFacade;
import ec.gob.ambiente.retce.services.TipoMuestraFacade;
import ec.gob.ambiente.retce.services.TipoSectorFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.bandeja.controllers.BandejaTareasController;
import ec.gob.ambiente.suia.comun.bean.WizardBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.ModalidadGestionDesechos;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.TaskSummaryCustom;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.registrocivil.consultacedula.Cedula;
import ec.gov.sri.wsconsultacontribuyente.ContribuyenteCompleto;

@ManagedBean
@ViewScoped
public class DescargasLiquidasController {
	
	private static final Logger LOG = Logger.getLogger(DescargasLiquidasController.class);
	
	/*BEANs*/
	@Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
	
	@ManagedProperty(value = "#{wizardBean}")
	@Getter
	@Setter
	private WizardBean wizardBean;
    
    /*EJBs*/
	@EJB
	private CaracteristicasPuntoMonitoreoFacade caracteristicasPuntoMonitoreoFacade;
	
	@EJB
	private CatalogoFrecuenciaMonitoreoFacade catalogoFrecuenciaMonitoreoFacade;
	
	@EJB
	private CatalogoMetodoEstimacionFacade catalogoMetodoEstimacionFacade;
	
	@EJB
	private CatalogoSustanciasRetceFacade catalogoSustanciasRetceFacade;
	
	@EJB
	private CatalogoTipoCuerpoReceptorFacade catalogoTipoCuerpoReceptorFacade;
	
	@EJB
	private CatalogoTipoDescargaFacade catalogoTipoDescargaFacade;
	
	@EJB
	private CatalogoTratamientoAguasFacade catalogoTratamientoAguasFacade;
	
	@EJB
	private DatoObtenidoMedicionDescargasFacade datoObtenidoMedicionDescargasFacade;
	
	@EJB
	private DatosLaboratorioFacade datosLaboratorioFacade;
	
	@EJB
    private DescargasLiquidasFacade descargasLiquidasFacade;
	
	@EJB
    private DetalleDescargasLiquidasFacade detalleDescargasLiquidasFacade;
	
	@EJB
    private DocumentosFacade documentosFacade;
    
    @EJB
    private InformacionProyectoFacade informacionProyectoFacade;
    
    @EJB
	private OficioRetceFacade oficioRetceFacade;
    
    @EJB
    private OrganizacionFacade organizacionFacade;
    
    @EJB
	private ParametrosDescargasLiquidasFacade parametrosDescargasLiquidasFacade;
    
    @EJB
	private ProcesoFacade procesoFacade;
    
    @EJB
	private SubstanciasRetceFacade sustanciasRetceFacade;
    
    @EJB
    private TecnicoResponsableFacade tecnicoResponsableFacade;
    
    @EJB
	private TipoMuestraFacade tipoMuestraFacade;
    
    @EJB
    private UsuarioFacade usuarioFacade; 
    
    @EJB
    private CatalogoTipoCuerpoCaracteristicasFacade catalogoTipoCuerpoCaracteristicasFacade;
    
    @EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;
    
    @EJB
	private DetalleDescargasTratamientoFacade detalleDescargasTratamientoFacade;
    
    @EJB
    private TipoSectorFacade tipoSectorRetceFacade;
    
    @EJB
    private LugarDescargaFacade lugarDescargaFacade;
    
	@EJB
	private FaseRetceFacade faseRetceFacade;
    /*List*/    
    @Getter
	private List<CaracteristicasPuntoMonitoreo> caracteristicasPuntoMonitoreoList;
    
    @Getter
	private List<CaracteristicasPuntoMonitoreoTabla> caracteristicasPuntoMonitoreoTablaList;
    
    @Getter
	private List<CatalogoFrecuenciaMonitoreo> catalogoFrecuenciaMonitoreoList;
    
    @Getter
	private List<CatalogoMetodoEstimacion> catalogoMetodoEstimacionList;
    
    @Getter
	private List<CatalogoSustanciasRetce> catalogoSustanciasRetceList;
    
    @Getter
	private List<CatalogoTipoCuerpoReceptor> catalogoTipoCuerpoReceptorList;
    
    @Getter
	private List<CatalogoTipoDescarga> catalogoTipoDescargaList;
    
    @Getter
	private List<CatalogoTratamientoAguas> catalogoTratamientoAguasList;
    
    @Getter
    @Setter
	private List<DetalleDescargasLiquidasTratamientoAguas> descargaTramientoList;
    
    @Getter
    @Setter
	private List<LugarDescarga> lugarDescargaList;
    
    private GenerarMapaImagenWS_Service wsMapas;
        
	private List<DatoObtenidoMedicionDescargas> datoObtenidoList,datoObtenidoEliminadosList,datoObtenidoHitoricoEliminadosList;
	
	public List<DatoObtenidoMedicionDescargas> getDatoObtenidoList(CaracteristicasPuntoMonitoreoTabla tabla){
		List<DatoObtenidoMedicionDescargas> lista=new ArrayList<DatoObtenidoMedicionDescargas>();
		for (DatoObtenidoMedicionDescargas dato : datoObtenidoList) {
			if(dato.getParametrosTablas().getTabla().getId().intValue()==tabla.getId().intValue())
			lista.add(dato);
		}		
		return lista;
	}
	public List<DatoObtenidoMedicionDescargas> getDatoObtenidoHistoricoEliminadosList(CaracteristicasPuntoMonitoreoTabla tabla){
		List<DatoObtenidoMedicionDescargas> lista=new ArrayList<DatoObtenidoMedicionDescargas>();
		/*for (DatoObtenidoMedicionDescargas dato : datoObtenidoHitoricoEliminadosList) {
			if(dato.getParametrosTablas().getTabla().getId().intValue()==tabla.getId().intValue())
			lista.add(dato);
		}*/
		return lista;
	}
    
    @Getter
	private List<DatosLaboratorio> datosLaboratorioList,datosLaboratorioEliminadosList,datosLaboratorioHistoricoEliminadosList;
    
    
    private List<Documento> datosLaboratorioEliminadosDocumentosList;
    
	@Getter
	private List<DescargasLiquidas> descargasLiquidasList;
	
	@Getter
	private List<DetalleDescargasLiquidas> detalleDescargasLiquidasList, listaNuevaDetalleDescargas;
		
	private List<ParametrosTablas> parametrosTablasList;
	public List<ParametrosTablas> getParametrosTablasList()
	{
		
		List<ParametrosTablas> lista=new ArrayList<ParametrosTablas>();
		if(parametrosTablasList!=null && !parametrosTablasList.isEmpty() && caracteristicasPuntoMonitoreoTabla!=null && caracteristicasPuntoMonitoreoTabla.getId()!=null)		
		for (ParametrosTablas parametro : parametrosTablasList) {
			if(parametro.getTabla().getId().intValue()==caracteristicasPuntoMonitoreoTabla.getId().intValue())
				lista.add(parametro);
		}
		return lista;
	}
	
	@Getter
	private List<TipoMuestra> tipoMuestraList;
	
	@Getter
	private List<String> anioList,mesList;
	
	@Getter
	private List<SubstanciasRetce> sustanciasRetceList,sustanciasRetceEliminadosList,sustanciasRetceHistoricoEliminadosList;
	
			
	/*Object*/
	private CaracteristicasPuntoMonitoreoTabla caracteristicasPuntoMonitoreoTabla;
	
	private final ConsultaRucCedula consultaRucCedula = new ConsultaRucCedula(Constantes.getUrlWsRegistroCivilSri());
		
	@Setter
	@Getter
	private InformacionProyecto informacionProyecto;
	
	@Setter
	@Getter
	private TecnicoResponsable tecnicoResponsable;
	
	@Setter
	@Getter
	private DatoObtenidoMedicionDescargas datoObtenido;
	
	@Setter
	@Getter
	private DatosLaboratorio datosLaboratorio;
	
	@Setter
	@Getter
	private DescargasLiquidas descargasLiquidas;
	
	@Setter
	@Getter
	private DetalleDescargasLiquidas detalleDescargasLiquidas;
		
	@Setter
	@Getter
	private Documento adjuntoAprobacionMonitoreo,adjuntoJustificacion,informeLaboratorio,registroLaboratorio,cadenaCustodia,protocoloMuestro,documentoAdicional,imagenCoordenadas;
	
	private Map<String, Object> variables;
	
	@Setter
	@Getter
	private SubstanciasRetce sustanciasRetce;
	
	@Setter
	@Getter
	private UploadedFile fileAdjuntoAprobacionMonitoreo,fileAdjuntoJustificacion,fileAdjuntoInformeLaboratorio;
		
	/*Boolean*/
	private boolean actualizaAprobacionMonitoreo,actualizaJustificacion;
	
    @Getter       
    private boolean verFormulario,editarLaboratorio,nuevoReporte;
    
    @Getter       
    private boolean habilitarCorreccion=false;
    
    @Getter
    @Setter
    private boolean habilitarIngreso=false, habilitarAgregar, editarFuente=true, habilitaFinalizar=false, masReportes=false;
    
    @Getter       
    private boolean habilitarObservaciones=false;
    
    @Getter       
    private boolean editarObservaciones=false;
    
    /*Integer*/
    private Integer numeroObservaciones=0;  
    
	/*String*/
	@Getter
	@Setter       
    private String nombreUsuario,representanteLegal, mensajeResponsabilidad;

    
    @Setter
	@Getter
	private String nombreAdjuntoAprobacionMonitoreo,nombreAdjuntoJustificacion,medicionDesdeAnio,medicionDesdeMes,medicionHastaAnio,medicionHastaMes;
    
    private String nombreTarea=null;
    
    	@Getter
	@Setter
	private List<CatalogoTipoCuerpoReceptorCaracteristicasPunto>catalogoTipoCuerpoReceptorCaracteristicasPuntolist;
    	
	@Getter
	@Setter
	private List<CatalogoTratamientoAguas> catalogoTratamientoListSelected;
	
	@Getter
	@Setter
    private boolean verOtroTratamiento = false, verOtroLugar=false,mostrarHME=false ;
	
	@Getter
	@Setter
    private String documentoTipo1, documentoTipo2, documentoTipo3, documentoTipo4, documentoTipo5;
	
	@Getter
	@Setter
	private TipoSector tipoSector;
	
	@Getter
	@Setter 
	private List<FaseRetce> listaFaseRetce;
	
	@Getter
	@Setter 
	private FaseRetce faseRetce;
	
	@Getter
	@Setter 
	private Integer idLaboratorio;
	
	@PostConstruct
	public void init() 
	{
		cargarDatosIniciales();
		
		Integer idInformacionBasica =(Integer)(JsfUtil.devolverObjetoSession(InformacionProyecto.class.getSimpleName()));
		if(idInformacionBasica!=null){
			informacionProyecto=informacionProyectoFacade.findById(idInformacionBasica);
			JsfUtil.cargarObjetoSession(InformacionProyecto.class.getSimpleName(), null);
			buscarDatosOperador();
			cargarListaDescargas();		
			if(descargasLiquidasList.isEmpty()){
				verFormulario=true;
				editarFuente=false;
				detalleDescargasLiquidas.setCodigoPuntoMonitoreo(generarCodigoMonitoreo(1));
			}
			habilitarIngreso=true;
			habilitarAgregar=true;
			MensajeResponsabilidadRetceController mensajeResponsabilidadRetceController = JsfUtil.getBean(MensajeResponsabilidadRetceController.class);
			mensajeResponsabilidad = mensajeResponsabilidadRetceController.mensajeResponsabilidadRetce(informacionProyecto.getUsuarioCreacion());
		}else if(bandejaTareasBean.getTarea()!=null){
			cargarDatosTarea();
			habilitarObservaciones=true;
			habilitarAgregar=false;
			editarObservaciones=bandejaTareasBean.getTarea().getTaskName().toUpperCase().contains("ELABORAR");//ELABORAR INFORME
			//JsfUtil.cargarObjetoSession(InformacionProyecto.class.getSimpleName(), null);
		}else{
			return;
		}
		
		catalogoFrecuenciaMonitoreoList=informacionProyecto.getFaseRetce()!=null?catalogoFrecuenciaMonitoreoFacade.findByName(informacionProyecto.getFaseRetce().getFrecuencia()):catalogoFrecuenciaMonitoreoFacade.findAll();
		caracteristicasPuntoMonitoreoList=caracteristicasPuntoMonitoreoFacade.findBySector(informacionProyecto.getTipoSector());
		lugarDescargaList= new ArrayList<LugarDescarga>();
		lugarDescargaList=lugarDescargaFacade.findAll();
		
		mostrarHME= (informacionProyecto.getTipoSector().getNombre().contains("Otros")) ? false:true;
		listaFaseRetce=new ArrayList<FaseRetce>();
		listaFaseRetce=faseRetceFacade.findByTipoSector(informacionProyecto.getTipoSector());
	}

	public void guardarReporte(){
		nuevoReporte=false;
		if(descargasLiquidas.getId() != null){
			return;
		}
		descargasLiquidas.setInformacionProyecto(informacionProyecto);
		descargasLiquidasFacade.guardar(descargasLiquidas,JsfUtil.getLoggedUser(),0);
		for (DetalleDescargasLiquidas objDetalle : listaNuevaDetalleDescargas) {
			objDetalle.setDetalleOriginalId(objDetalle.getId());
			detalleDescargasLiquidasFacade.save(objDetalle, loginBean.getUsuario());
			// creo el nuevo detalle para el Nuevo reporte
			objDetalle.setId(null);
			objDetalle.setCatalogoTipoDescarga(null);
			objDetalle.setCaudalMedido(null);
			objDetalle.setFechaCreacion(new Date());
			objDetalle.setFechaModificacion(null);
			objDetalle.setHorasDescargaDia(null);
			objDetalle.setNumeroObservacion(0);
			objDetalle.setTipoMuestra(null);
			objDetalle.setVolumenDescarga(null);
			objDetalle.setDescargasLiquidas(descargasLiquidas);
			detalleDescargasLiquidasFacade.save(objDetalle, loginBean.getUsuario());
		}
		descargasLiquidasFacade.guardar(descargasLiquidas,JsfUtil.getLoggedUser(),0);
		cargarListaDescargas();
		ocultarFormulario();
	}
	
	public void cancelarReporte(){
		nuevoReporte=false;
		listaNuevaDetalleDescargas = new ArrayList<DetalleDescargasLiquidas>();
	}
	
	public void agregarReporte(){
		nuevoReporte=true;
		medicionDesdeAnio=null;
		medicionDesdeMes=null;
		medicionHastaAnio=null;
		medicionHastaMes=null;
		descargasLiquidas = new DescargasLiquidas();
		DescargasLiquidas descargasLiquidasAux = new DescargasLiquidas();
		listaNuevaDetalleDescargas = new ArrayList<DetalleDescargasLiquidas>();
		// busco el primer reporte de las emisiones atmosfericas
		List<DescargasLiquidas> listaDescargasLiquidasAux = descargasLiquidasFacade.findByInformacionProyecto(informacionProyecto);
		if(listaDescargasLiquidasAux != null && listaDescargasLiquidasAux.size() > 0){
			for (DescargasLiquidas objDescargas : listaDescargasLiquidasAux) {
				descargasLiquidasAux = objDescargas;
			}
			listaNuevaDetalleDescargas = detalleDescargasLiquidasFacade.findByDescargaLiquida(descargasLiquidasAux);	
		}else{
			agregarDetalle(descargasLiquidas);
			nuevoReporte=false;
			editarFuente=false;
		}
	}
	
	private void cargarDatosIniciales(){
		adjuntoAprobacionMonitoreo=new Documento();
		adjuntoJustificacion=new Documento();
		caracteristicasPuntoMonitoreoTabla=new CaracteristicasPuntoMonitoreoTabla();
		datoObtenido=new DatoObtenidoMedicionDescargas();
		datosLaboratorio=new DatosLaboratorio();		
		descargasLiquidas=new DescargasLiquidas();
		detalleDescargasLiquidas=new DetalleDescargasLiquidas();descargasLiquidas.setFechaReporte(new Date());
		sustanciasRetce=new SubstanciasRetce();
		tecnicoResponsable=new TecnicoResponsable();
						
		caracteristicasPuntoMonitoreoList=new ArrayList<CaracteristicasPuntoMonitoreo>();
		catalogoFrecuenciaMonitoreoList=new ArrayList<CatalogoFrecuenciaMonitoreo>();
		catalogoMetodoEstimacionList=new ArrayList<CatalogoMetodoEstimacion>();
		catalogoSustanciasRetceList=new ArrayList<CatalogoSustanciasRetce>();
		catalogoTipoCuerpoReceptorList=new ArrayList<CatalogoTipoCuerpoReceptor>();
		catalogoTipoDescargaList=new ArrayList<CatalogoTipoDescarga>();
		catalogoTratamientoAguasList=new ArrayList<CatalogoTratamientoAguas>();
		datoObtenidoList=new ArrayList<DatoObtenidoMedicionDescargas>();
		datoObtenidoEliminadosList=new ArrayList<DatoObtenidoMedicionDescargas>();
		datoObtenidoHitoricoEliminadosList=new ArrayList<DatoObtenidoMedicionDescargas>();
		datosLaboratorioList=new ArrayList<DatosLaboratorio>();
		datosLaboratorioEliminadosList=new ArrayList<DatosLaboratorio>();
		datosLaboratorioHistoricoEliminadosList=new ArrayList<DatosLaboratorio>();
		datosLaboratorioEliminadosDocumentosList=new ArrayList<Documento>();
		descargasLiquidasList=new ArrayList<DescargasLiquidas>();
		detalleDescargasLiquidasList=new ArrayList<DetalleDescargasLiquidas>();
		parametrosTablasList=new ArrayList<ParametrosTablas>();
		sustanciasRetceList=new ArrayList<SubstanciasRetce>();
		sustanciasRetceEliminadosList=new ArrayList<SubstanciasRetce>();
		sustanciasRetceHistoricoEliminadosList=new ArrayList<SubstanciasRetce>();
		tipoMuestraList=new ArrayList<TipoMuestra>();
		listaNuevaDetalleDescargas = new ArrayList<DetalleDescargasLiquidas>();
		anioList=new ArrayList<String>();
		mesList=new ArrayList<String>();
		catalogoTratamientoListSelected = new ArrayList<>();
		
		editarLaboratorio=false;
		medicionDesdeAnio=null;medicionDesdeMes=null;medicionHastaAnio=null;medicionDesdeAnio=null;
				
		catalogoMetodoEstimacionList=catalogoMetodoEstimacionFacade.findAll();
		catalogoSustanciasRetceList=catalogoSustanciasRetceFacade.findAll();
		catalogoTipoCuerpoReceptorList=catalogoTipoCuerpoReceptorFacade.findAll();
		catalogoTipoDescargaList=catalogoTipoDescargaFacade.findAll();
		catalogoTratamientoAguasList=catalogoTratamientoAguasFacade.findAll();			
		tipoMuestraList=tipoMuestraFacade.findAll();
		imagenCoordenadas = new Documento();
		
		cargarListaAniosMeses();
	}
	
	private void cargarListaAniosMeses(){	
		mesList=new ArrayList<String>( Arrays.asList("enero","febrero","marzo","abril","mayo","junio","julio","agosto","septiembre","octubre","noviembre","diciembre"));		
				
		int anioActual=JsfUtil.getCurrentYear();		
		for(int i=(anioActual-10);i<=(anioActual+5);i++)
			anioList.add(String.valueOf(i));

	}
			
	private void cargarDatosTarea(){
		if(bandejaTareasBean.getTarea()!=null){
			nombreTarea=bandejaTareasBean.getTarea()!=null?bandejaTareasBean.getTarea().getTaskName().toUpperCase():null;
			String tramite=bandejaTareasBean.getTarea().getProcedure();			
			descargasLiquidas=descargasLiquidasFacade.findByCodigo(tramite);
			informacionProyecto=descargasLiquidas.getInformacionProyecto();
			buscarDatosOperador();
			descargasLiquidasList=new ArrayList<DescargasLiquidas>();
			descargasLiquidasList.add(descargasLiquidas);
			seleccionarDescargas(descargasLiquidas);
			try {
				variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
				numeroObservaciones=variables.containsKey("numero_observaciones")?(Integer.valueOf((String)variables.get("numero_observaciones"))):0;
			} catch (JbpmException e) {
				LOG.error("Error al recuperar variable numero_observaciones "+e.getCause()+" "+e.getMessage());
			}
		}		
		habilitarListener(descargasLiquidas);
	}
	
	private void habilitarListener(DescargasLiquidas descargas){
		if(!JsfUtil.getLoggedUser().getNombre().contains(descargas.getUsuarioCreacion())){
			habilitarIngreso=false;
			return;
		}		
		if(descargas.getEnviado()){
			habilitarObservaciones=true;	
 	    	
 	    	if(nombreTarea==null){ 	    		
 	    		habilitarIngreso=false;
 	    		habilitarCorreccion=false;
 	    	}else if(nombreTarea.contains("INGRESAR")){
 	    		habilitarIngreso=true;
 	    		habilitarCorreccion=true; 	    		
 	    	}else{
 	    		habilitarIngreso=false;
 	    		habilitarCorreccion=false;
 	    	}	    		
		}else{
			habilitarIngreso=true;
		}
	}
	
	private void cargarListaDescargas(){		
		descargasLiquidasList=descargasLiquidasFacade.findByInformacionProyecto(informacionProyecto);
		for (DescargasLiquidas descargas : descargasLiquidasList) {
			cargarDocumentos(descargas);
		}
	}
	
	private void cargarDocumentos(DescargasLiquidas descargas){
		descargas.setAdjuntoJustificacion(documentosFacade.documentoXTablaIdXIdDocUnico(descargas.getId(),DescargasLiquidas.class.getSimpleName(),TipoDocumentoSistema.JUSTIFICACION_DESCARGA_LIQUIDA));		
		OficioPronunciamientoRetce oficio =oficioRetceFacade.getOficio(descargas.getCodigo(),TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_DESCARGA_LIQUIDA);
		if(oficio != null){
			descargas.setOficioPronunciamiento(documentosFacade.documentoXTablaIdXIdDocUnico(oficio.getId(),OficioPronunciamientoRetce.class.getSimpleName(),TipoDocumentoSistema.OFICIO_PRONUNCIAMIENTO_DESCARGA_LIQUIDA));
		}		
	}
	
	private void buscarDatosOperador()	 
	{
		Usuario user=loginBean.getUsuario();
		Organizacion orga=null;
		try {
			orga = organizacionFacade.buscarPorRuc(user.getNombre());
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		
		if(orga!=null){			
			nombreUsuario=orga.getNombre();
			representanteLegal=orga.getPersona().getNombre();
		}else{		
			nombreUsuario=user.getPersona().getNombre();
		}		
	}
	
	
			
	public void validarLaboratorioListener()
	{		
		String cedulaRuc=datosLaboratorio.getRuc();		
		if(!editarLaboratorio){
			datosLaboratorio=new DatosLaboratorio();
			datosLaboratorio.setListaDocumentosLaboratorios(new ArrayList<Documento>());
		}					
		
		if(cedulaRuc.length()==13 && JsfUtil.validarCedulaORUC(cedulaRuc)){
			for (DatosLaboratorio labIngresado : datosLaboratorioList) {
				if(!labIngresado.equals(datosLaboratorio) && labIngresado.getRuc().compareTo(cedulaRuc)==0)
				{
					JsfUtil.addMessageWarning("Laboratorio ya ingresado");
					return;
				}
			}
			
			try {
				Organizacion orga=organizacionFacade.buscarPorRuc(cedulaRuc);
				if(orga!=null)
				{
					datosLaboratorio.setRuc(orga.getRuc());
					datosLaboratorio.setNombre(orga.getNombre());
					return;
				}
				
				ContribuyenteCompleto contribuyenteCompleto = consultaRucCedula
		                .obtenerPorRucSRI(
		                        Constantes.USUARIO_WS_MAE_SRI_RC,
		                        Constantes.PASSWORD_WS_MAE_SRI_RC,
		                        cedulaRuc);
				if(contribuyenteCompleto!=null)
				{
					datosLaboratorio.setRuc(contribuyenteCompleto.getNumeroRuc());
					datosLaboratorio.setNombre(contribuyenteCompleto.getRazonSocial());
									
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
		
		if(datosLaboratorio.getRuc()==null)
		{
			JsfUtil.addMessageError("Ruc no válido");
		}		
	}
	
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
	
	public boolean validarCorreoListener()
	{
		if(JsfUtil.validarMail(tecnicoResponsable.getCorreo())){
			return true;
		}else{
			JsfUtil.addMessageError("Debe Ingresar un Correo Válido del Técnico Responsable");
			tecnicoResponsable.setCorreo(null);
			return false;
		}
	}
	
	public void validarPeriodoMedicionListener(){		
		if(medicionDesdeAnio==null || medicionDesdeMes==null || medicionHastaAnio==null || medicionHastaMes==null)
			return;
		
		int mesDesde= mesList.indexOf(medicionDesdeMes);
		int meshasta= mesList.indexOf(medicionHastaMes);			
		int fechaDesde=Integer.valueOf(medicionDesdeAnio)*100+mesDesde;
		int fechaHasta=Integer.valueOf(medicionHastaAnio)*100+meshasta;
			
		if(fechaDesde > fechaHasta){
			JsfUtil.addMessageWarning("El período de medición (hasta) no debe ser menor al período de medición (Desde)");
			medicionHastaMes=null;
			medicionHastaAnio=null;
		}
	}
	
	public void caracteristicasMonitoreoListener(){
		datoObtenidoList=datoObtenidoMedicionDescargasFacade.findByDescarga(detalleDescargasLiquidas);
		caracteristicasPuntoMonitoreoTablaList=new ArrayList<CaracteristicasPuntoMonitoreoTabla>();
		parametrosTablasList=new ArrayList<ParametrosTablas>();
		if(detalleDescargasLiquidas.getCaracteristicasPuntoMonitoreo()!=null && detalleDescargasLiquidas.getCatalogoTipoCuerpoReceptorCaracteristicasPunto() != null){
			//caracteristicasPuntoMonitoreoTablaList=caracteristicasPuntoMonitoreoFacade.findTablaByCaracteristica(detalleDescargasLiquidas.getCaracteristicasPuntoMonitoreo());
			caracteristicasPuntoMonitoreoTablaList=caracteristicasPuntoMonitoreoFacade.findTablaByCaracteristicaPorTipoCuerpo(detalleDescargasLiquidas.getCaracteristicasPuntoMonitoreo(), detalleDescargasLiquidas.getCatalogoTipoCuerpoReceptorCaracteristicasPunto().getCatalogoTipoCuerpoReceptor());
			for (CaracteristicasPuntoMonitoreoTabla tabla : caracteristicasPuntoMonitoreoTablaList) {
				parametrosTablasList.addAll(parametrosDescargasLiquidasFacade.findParametrosByTabla(tabla));
			}			
		}
		if(detalleDescargasLiquidas.getCaracteristicasPuntoMonitoreo() != null){
			catalogoTipoCuerpoReceptorCaracteristicasPuntolist=catalogoTipoCuerpoCaracteristicasFacade.findAll(detalleDescargasLiquidas.getCaracteristicasPuntoMonitoreo());
		}
	}
	
	public Date getFechaActual(){
		return new Date();
	}
	
	public void crearDatosLaboratorio(){
		datosLaboratorio=new DatosLaboratorio();
		datosLaboratorio.setListaDocumentosLaboratorios(new ArrayList<Documento>());
		editarLaboratorio=false;
		
		documentoTipo1 = null;
		documentoTipo2 = null;
		documentoTipo3 = null;
		documentoTipo4 = null;
		documentoTipo5 = null;
	}
	
	public void agregarDatosLaboratorio(){
		if(!datosLaboratorioList.contains(datosLaboratorio))
			datosLaboratorioList.add(datosLaboratorio);
		editarLaboratorio=false;
	}
	
	public void editarDatosLaboratorio(DatosLaboratorio item) throws Exception{
		datosLaboratorio=item;
		editarLaboratorio=true;
		idLaboratorio=datosLaboratorio.getId();
		if(datosLaboratorio.getListaDocumentosLaboratorios()==null)
		{
			if(datosLaboratorio.getId()==null)
				datosLaboratorio.setListaDocumentosLaboratorios(new ArrayList<Documento>());
			else {
				List<Integer> listaTipos = new ArrayList<Integer>(); 
		    	listaTipos.add(TipoDocumentoSistema.TIPO_DOCUMENTO_INFORME_LABORATORIO.getIdTipoDocumento());
		    	listaTipos.add(TipoDocumentoSistema.RETCE_SAE_LABORATORIO.getIdTipoDocumento());
		    	listaTipos.add(TipoDocumentoSistema.RETCE_CADENA_CUSTODIA.getIdTipoDocumento());
		    	listaTipos.add(TipoDocumentoSistema.RETCE_PROTOCOLO_MUESTREO.getIdTipoDocumento());
		    	listaTipos.add(TipoDocumentoSistema.RETCE_ADICIONAL_LABORATORIO.getIdTipoDocumento());
		    	
		    	datosLaboratorio.setListaDocumentosLaboratorios(documentosFacade.recuperarDocumentosPorTipo(datosLaboratorio.getId(),
		    			DatosLaboratorio.class.getSimpleName(), listaTipos));
			}
		}
		//informeLaboratorio,registroLaboratorio,cadenaCustodia,protocoloMuestro,documentoAdicional
		informeLaboratorio	=new Documento();
		registroLaboratorio	=new Documento();
		cadenaCustodia		=new Documento();
		protocoloMuestro	=new Documento();
		documentoAdicional	=new Documento();
		for (Documento doc : datosLaboratorio.getListaDocumentosLaboratorios()) {
			Integer idTipodoc = doc.getTipoDocumento().getId();
			
			switch (idTipodoc) {
			case 4211:
				documentoTipo1 = doc.getNombre();
				informeLaboratorio=doc;
				System.out.println(documentoTipo1);
				break;
			case 4236:
				documentoTipo2 = doc.getNombre();
				registroLaboratorio=doc;
				break;
			case 4237:
				documentoTipo3 = doc.getNombre();
				cadenaCustodia=doc;
				break;
			case 4238:
				documentoTipo4 = doc.getNombre();
				protocoloMuestro=doc;
				break;
			case 4239:
				documentoTipo5 = doc.getNombre();
				documentoAdicional=doc;
				break;
			}
		}
	}
	
	public void eliminarDatosLaboratorio(DatosLaboratorio item){
		if(item.getId()!=null)
			datosLaboratorioEliminadosList.add(item);
		datosLaboratorioList.remove(item);		
	}
	
	public void eliminarDocumentoLaboratorio(Documento item){
		if(item.getId()!=null)
			datosLaboratorioEliminadosDocumentosList.add(item);
		datosLaboratorio.getListaDocumentosLaboratorios().remove(item);		
	}
	
	//verificar si toca borrar
	public void crearSustancias(){
		sustanciasRetce=new SubstanciasRetce();
	}
	
	public void agregarSustancias(){
		sustanciasRetce.setDetalleDescargasLiquidas(detalleDescargasLiquidas);
		if(!sustanciasRetceList.contains(sustanciasRetce))
			sustanciasRetceList.add(sustanciasRetce);		
	}
	
	public void editarSustancias(SubstanciasRetce item){
		sustanciasRetce=item;		
	}
	
	public void eliminarSustancias(SubstanciasRetce item){
		if(item.getId()!=null)
			sustanciasRetceEliminadosList.add(item);
		sustanciasRetceList.remove(item);
	}
	
	public void crearDatoObtenido(CaracteristicasPuntoMonitoreoTabla caracteristicasPuntoMonitoreoTabla){
		datoObtenido=new DatoObtenidoMedicionDescargas();
		datoObtenido.setMetodoEstimacion(catalogoMetodoEstimacionFacade.findById(1));
		this.caracteristicasPuntoMonitoreoTabla=caracteristicasPuntoMonitoreoTabla;
	}
	
	public void agregarDatoObtenido(){
		if(!datoObtenidoList.contains(datoObtenido))
			datoObtenidoList.add(datoObtenido);		
	}
	
	public void editarDatoObtenido(DatoObtenidoMedicionDescargas item){
		datoObtenido=item;
		caracteristicasPuntoMonitoreoTabla=item.getParametrosTablas().getTabla();
	}
	
	public void eliminarDatoObtenido(DatoObtenidoMedicionDescargas item){
		if(item.getId()!=null)
			datoObtenidoEliminadosList.add(item);
		datoObtenidoList.remove(item);
		
		datoObtenido=new DatoObtenidoMedicionDescargas();
	}	
	
	public void fileUploadAprobacionMonitoreo(FileUploadEvent event) {
		fileAdjuntoAprobacionMonitoreo = event.getFile();
		setAdjuntoAprobacionMonitoreo(UtilDocumento.generateDocumentPDFFromUpload(fileAdjuntoAprobacionMonitoreo.getContents(),fileAdjuntoAprobacionMonitoreo.getFileName()));
		actualizaAprobacionMonitoreo=true;
	}
	
	public void fileUploadJustificacion(FileUploadEvent event) {
		fileAdjuntoJustificacion = event.getFile();
		setAdjuntoJustificacion(UtilDocumento.generateDocumentPDFFromUpload(fileAdjuntoJustificacion.getContents(),fileAdjuntoJustificacion.getFileName()));
		actualizaJustificacion=true;
	}
	
	public void fileUploadInformeLaboratorio(FileUploadEvent event) {
		Integer tipoDocumento = Integer.parseInt(event.getComponent().getAttributes().get("tipoDocumento").toString());
		Documento documentoLoad = null;
		
		fileAdjuntoInformeLaboratorio = event.getFile();
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
		for(int i=0; i< datosLaboratorio.getListaDocumentosLaboratorios().size(); i++){
			Documento doc = datosLaboratorio.getListaDocumentosLaboratorios().get(i);
			if(doc.getTipoDocumento().equals(tipoDoc)) {
				docsEliminar.add(i);
			}
		}
		
		for (Integer index : docsEliminar) {
			datosLaboratorio.getListaDocumentosLaboratorios().remove(index);
		}
		
		datosLaboratorio.getListaDocumentosLaboratorios().add(documentoLoad);
	}
	
	public StreamedContent getDocumentoAdjuntoAprobacionMonitoreo() {
		return UtilDocumento.getStreamedContent(adjuntoAprobacionMonitoreo);
	}
	
	public StreamedContent getDocumentoAdjuntoJustificacion() {
		return UtilDocumento.getStreamedContent(adjuntoJustificacion);
	}
	
	public StreamedContent getStreamedContentDocumento(Documento documento) {
		try {
			if(documento.getContenidoDocumento()==null && documento.getId()!=null)
				documento.setContenidoDocumento(documentosFacade.descargar(documento.getIdAlfresco()));
			return UtilDocumento.getStreamedContent(documento);
		} catch (CmisAlfrescoException e) {
			LOG.error(e);
			e.printStackTrace();
			return null;
		}		
	}
		
	public String verCodigosMonitoreo(DescargasLiquidas descargas){
		String codigos="<ol>";		
		for (DetalleDescargasLiquidas detalle : detalleDescargasLiquidasFacade.findByDescargaLiquida(descargas)) {
			codigos+="<li>"+detalle.getCodigoPuntoMonitoreo()+"</li>";
		}		
		return codigos+"<ol>";
	}
	
	public void seleccionarDescargas(DescargasLiquidas descargas){
		descargasLiquidas=descargas;
		detalleDescargasLiquidasList=detalleDescargasLiquidasFacade.findByDescargaLiquida(descargasLiquidas);
		cargarDocumentos(descargas);
		habilitarListener(descargas);
	}
	
	private String generarCodigoMonitoreo(int index){
		String sequenceString=String.valueOf(index);
		for (int i = sequenceString.length(); i < 4; i++) {
			sequenceString = "0" + sequenceString;
		}
		return "DL"+sequenceString;
	}
	
	public void agregarDetalle(DescargasLiquidas descargas){
		verFormulario=true;		
		habilitarIngreso=true;
		medicionDesdeAnio=null;
		medicionDesdeMes=null;
		medicionHastaAnio=null;
		medicionHastaMes=null;
		this.descargasLiquidas=descargas!=null?descargas:new DescargasLiquidas();
		detalleDescargasLiquidas=new DetalleDescargasLiquidas();
		editarFuente=false;
		adjuntoAprobacionMonitoreo=new Documento();
		try {
			if(descargasLiquidas != null && descargasLiquidas.getId() != null){
				caracteristicasMonitoreoListener();
			}
			/*if(descargasLiquidas != null && descargasLiquidas.getId() != null){
				adjuntoAprobacionMonitoreo=documentosFacade.documentoXTablaIdXIdDocUnico(descargasLiquidas.getId(),DetalleDescargasLiquidas.class.getSimpleName(),TipoDocumentoSistema.TIPO_DOCUMENTO_OFICIO_DE_APROBACION_PUNTO_MONITOREO);
				if(adjuntoAprobacionMonitoreo!=null)
					adjuntoAprobacionMonitoreo.setContenidoDocumento(documentosFacade.descargar(adjuntoAprobacionMonitoreo.getIdAlfresco()));
				else
					adjuntoAprobacionMonitoreo=new Documento();
			}*/
		} catch (Exception e) {
			LOG.error(e);
		}
		
		//Generar Codigo de punto de Monitoreo
		if(detalleDescargasLiquidas.getCodigoPuntoMonitoreo()==null){
			List<DetalleDescargasLiquidas> detList=detalleDescargasLiquidasFacade.findByDescargaLiquida(descargasLiquidas);
			int sec=detList.size();
			String sequenceString=generarCodigoMonitoreo(++sec);
			
			List<String> codigosIngresados=new ArrayList<String>();
			for (DetalleDescargasLiquidas det : detList){
					codigosIngresados.add(det.getCodigoPuntoMonitoreo());
			}
			while(codigosIngresados.contains(sequenceString)){				
				sequenceString=generarCodigoMonitoreo(++sec);
			}
			detalleDescargasLiquidas.setCodigoPuntoMonitoreo(sequenceString);
		}
		
		if(detalleDescargasLiquidas.getFechaInicioMonitoreo()!=null){
			medicionDesdeAnio=detalleDescargasLiquidas.getFechaInicioMonitoreo().substring(0,4);
			medicionDesdeMes=detalleDescargasLiquidas.getFechaInicioMonitoreo().replace(medicionDesdeAnio+"-","");
		}
		if(detalleDescargasLiquidas.getFechaFinMonitoreo()!=null){
			medicionHastaAnio=detalleDescargasLiquidas.getFechaFinMonitoreo().substring(0,4);
			medicionHastaMes=detalleDescargasLiquidas.getFechaFinMonitoreo().replace(medicionHastaAnio+"-","");
		}
	}
	
	public void ocultarFormulario(){
		verFormulario=false;
		nuevoReporte =false;
		masReportes=false;
		if(descargasLiquidas != null && descargasLiquidas.getId() != null)
			habilitarListener(descargasLiquidas);
		seleccionarDescargas(descargasLiquidas);
		wizardBean.setCurrentStep("pasoCaracteristicas");
		Wizard wizard = (Wizard) FacesContext.getCurrentInstance().getViewRoot().findComponent("form:wizardDescargas");
		wizard.setStep("pasoCaracteristicas");
		RequestContext.getCurrentInstance().update("form:wizardDescargas");
	}
	
	public void editarDetalle(DetalleDescargasLiquidas detalle){		
		detalleDescargasLiquidas=detalle;
		habilitaFinalizar=false;
		if(detalleDescargasLiquidas.getDetalleOriginalId() != null){
			editarFuente = !detalleDescargasLiquidas.getId().equals(detalleDescargasLiquidas.getDetalleOriginalId());
		}else
			editarFuente = false;
		descargasLiquidas=descargasLiquidasFacade.findById(detalleDescargasLiquidas.getDescargasLiquidas().getId());
		informacionProyecto=descargasLiquidas.getInformacionProyecto();
		tecnicoResponsable=descargasLiquidas.getTecnicoResponsable()!=null?tecnicoResponsableFacade.findById(descargasLiquidas.getTecnicoResponsable().getId()):new TecnicoResponsable();
		datosLaboratorioList=datosLaboratorioFacade.findByDescarga(descargasLiquidas);
		//datosLaboratorioHistoricoEliminadosList=datosLaboratorioFacade.findByDescargaHistoricoEliminados(descargasLiquidas);
		sustanciasRetceList=sustanciasRetceFacade.findByDetalleDescarga(detalleDescargasLiquidas);
		sustanciasRetceHistoricoEliminadosList=sustanciasRetceFacade.findByDetalleDescargaHistoricoEliminados(detalleDescargasLiquidas);
		caracteristicasMonitoreoListener();
		datoObtenidoList=datoObtenidoMedicionDescargasFacade.findByDescarga(detalleDescargasLiquidas);
		datoObtenidoEliminadosList=new ArrayList<DatoObtenidoMedicionDescargas>();
		datoObtenidoHitoricoEliminadosList=datoObtenidoMedicionDescargasFacade.findByDescargaHistoricoEliminados(detalleDescargasLiquidas);

		medicionDesdeAnio=null;medicionDesdeMes=null;medicionHastaAnio=null;medicionDesdeAnio=null;
		if(detalleDescargasLiquidas.getFechaInicioMonitoreo()!=null){
			medicionDesdeAnio=detalleDescargasLiquidas.getFechaInicioMonitoreo().substring(0,4);
			medicionDesdeMes=detalleDescargasLiquidas.getFechaInicioMonitoreo().replace(medicionDesdeAnio+"-","");
		}
		if(detalleDescargasLiquidas.getFechaFinMonitoreo()!=null){
			medicionHastaAnio=detalleDescargasLiquidas.getFechaFinMonitoreo().substring(0,4);
			medicionHastaMes=detalleDescargasLiquidas.getFechaFinMonitoreo().replace(medicionHastaAnio+"-","");
		}
		
		try {
			Integer idDetalle = detalleDescargasLiquidas.getDetalleOriginalId() == null ? detalleDescargasLiquidas.getId():detalleDescargasLiquidas.getDetalleOriginalId();
			adjuntoAprobacionMonitoreo=documentosFacade.documentoXTablaIdXIdDocUnico(idDetalle,DetalleDescargasLiquidas.class.getSimpleName(),TipoDocumentoSistema.TIPO_DOCUMENTO_OFICIO_DE_APROBACION_PUNTO_MONITOREO);
			if(adjuntoAprobacionMonitoreo!=null)
				adjuntoAprobacionMonitoreo.setContenidoDocumento(documentosFacade.descargar(adjuntoAprobacionMonitoreo.getIdAlfresco()));
			else
				adjuntoAprobacionMonitoreo=new Documento();
		} catch (Exception e) {
			LOG.error(e);
		}
		try {
			adjuntoJustificacion=documentosFacade.documentoXTablaIdXIdDocUnico(descargasLiquidas.getId(),DescargasLiquidas.class.getSimpleName(),TipoDocumentoSistema.JUSTIFICACION_DESCARGA_LIQUIDA);
			if(adjuntoJustificacion!=null)
				adjuntoJustificacion.setContenidoDocumento(documentosFacade.descargar(adjuntoJustificacion.getIdAlfresco()));
			else
				adjuntoJustificacion=new Documento();
		} catch (Exception e) {
			LOG.error(e);
		}
		/**
		 * descarga de asignacion de imgane de imagenes
		 */
		try {
			imagenCoordenadas=documentosFacade.documentoXTablaIdXIdDocUnico(descargasLiquidas.getId(),DescargasLiquidas.class.getSimpleName(),TipoDocumentoSistema.RETCE_IMAGEN_COORDENADAS);
			if(imagenCoordenadas!=null)
				imagenCoordenadas.setContenidoDocumento(documentosFacade.descargar(imagenCoordenadas.getIdAlfresco()));
			else
				imagenCoordenadas=new Documento();
		} catch (Exception e) {
			LOG.error(e);
		}

		List<DetalleDescargasLiquidasTratamientoAguas> listaDetalleTratamiento = detalleDescargasTratamientoFacade.findByIdDetalle(detalleDescargasLiquidas.getId());
		verOtroTratamiento = false;
		for (DetalleDescargasLiquidasTratamientoAguas tratamiento : listaDetalleTratamiento) {
			catalogoTratamientoListSelected.add(tratamiento.getCatalogoTratamientoAguas());
			
			if (tratamiento.getCatalogoTratamientoAguas().getDescripcion().equals("Otros")) {
				verOtroTratamiento = true;
			}
		}
		
		verFormulario=true;
		nuevoReporte =false;
		actualizaAprobacionMonitoreo=false;
		actualizaJustificacion=false;
	}
	
	public void eliminarDetalle(DetalleDescargasLiquidas detalle){
		Usuario userLogged=JsfUtil.getLoggedUser();	
		detalle.setEstado(false);
		detalleDescargasLiquidasFacade.save(detalle, userLogged);
		detalleDescargasLiquidasList.remove(detalle);
		descargasLiquidas.setEstado(false);
		descargasLiquidasFacade.guardar(descargasLiquidas, loginBean.getUsuario(),0);
		detalleDescargasLiquidasList.remove(detalle);
		descargasLiquidasList=descargasLiquidasFacade.findByInformacionProyecto(informacionProyecto);
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
	
	public void validarDocumentoAprobacionMonitoreo(FacesContext context, UIComponent validate, Object value) {
		validarDocumento(adjuntoAprobacionMonitoreo,"Debe adjuntar Oficio de aprobación del punto de monitoreo. PDF tamaño permitido 20Mb.");
	}
	
	public void validarDocumentoInformelaboratorio(FacesContext context, UIComponent validate, Object value) {

		List<FacesMessage> errorMessages = new ArrayList<>();
		
		if (documentoTipo1 == null) {
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Informe de monitoreo del Laboratorio es requerido", null));
		}
		
		if (documentoTipo2 == null) {
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Registro de SAE del Laboratorio es requerido", null));
		}
		
		if (documentoTipo3 == null) {
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cadena de custodia es requerido", null));
		}
		
		if (documentoTipo4 == null) {
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Protocolo de muestreo es requerido", null));
		}
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	private boolean validarCaracteristicasMedicion()
	{
		boolean validar=true;
		if(adjuntoAprobacionMonitoreo.getContenidoDocumento()==null){
			JsfUtil.addMessageError("Debe adjuntar Oficio de aprobación del punto de monitoreo");
			validar= false;
		}
		if(detalleDescargasLiquidas.getFechaInicioMonitoreo() ==null || detalleDescargasLiquidas.getFechaFinMonitoreo() ==null)
			validar= false;
		validar = validarCoordenadas(); 
		return validar;
	}
	
	private boolean validarRegistroMedicion(DetalleDescargasLiquidas detalle)
	{
		boolean validar=true;
		List<DatoObtenidoMedicionDescargas> datoDetalle = datoObtenidoMedicionDescargasFacade.findByDescarga(detalle);
		if(datoDetalle == null || datoDetalle.isEmpty()){
			JsfUtil.addMessageError("Debe ingresar al menos 1 dato obtenido en Registro de Mediciones de la medición "+detalle.getNumeroPuntoMonitoreo());
			validar= false;
		}
		return validar;
	}
	
	private boolean validarLaboratorios()
	{
		boolean validar=true;
		if(datosLaboratorioList.isEmpty()){
			JsfUtil.addMessageError("Debe ingresar al menos 1 laboratorio");
			validar= false;
		}
		return validar;
	}
	
	private boolean validarSustancias()
	{
		boolean validar=true;
		if(sustanciasRetceList.isEmpty()){
			//No es obligatorio
			//JsfUtil.addMessageError("Debe ingresar al menos 1 Sustancia RETCE");
			//validar= false;
		}
		return validar;
	}
	
	private boolean validarTecnico()
	{
		boolean validar=true;
		if(tecnicoResponsable.getNombre().isEmpty()){
			JsfUtil.addMessageError("Debe Ingresar la Información del Técnico Responsable");
			validar= false;
		}
		return validar;
	}
	
	private boolean validarDatos(){
		boolean validar=true;
		for (DetalleDescargasLiquidas detalle : detalleDescargasLiquidasList) {
			if(!validarCaracteristicasMedicion() || !validarRegistroMedicion(detalle) || !validarLaboratorios() || !validarSustancias() || !validarTecnico()){			
				validar= false;
			}
		}
		return validar;
	}
	
	private void guardarPuntoMonitoreo()
	{
		Usuario userLogged=JsfUtil.getLoggedUser();
		detalleDescargasLiquidas.setFechaInicioMonitoreo(medicionDesdeAnio+"-"+medicionDesdeMes);
		detalleDescargasLiquidas.setFechaFinMonitoreo(medicionHastaAnio+"-"+medicionHastaMes);
		descargasLiquidas.setInformacionProyecto(informacionProyecto);
		descargasLiquidasFacade.guardar(descargasLiquidas,userLogged,habilitarObservaciones?numeroObservaciones:0);
		detalleDescargasLiquidas.setDescargasLiquidas(descargasLiquidas);
		detalleDescargasLiquidasFacade.guardar(detalleDescargasLiquidas, userLogged,habilitarObservaciones?numeroObservaciones:0);		
		
		//guardar tratamiento
		List<CatalogoTratamientoAguas> catalogoTratamientoList = new ArrayList<>();
		catalogoTratamientoList.addAll(catalogoTratamientoListSelected);
		if(detalleDescargasLiquidas.getId() != null) {
			List<DetalleDescargasLiquidasTratamientoAguas> listaDetalleTratamiento = detalleDescargasTratamientoFacade.findByIdDetalle(detalleDescargasLiquidas.getId());
			if(!listaDetalleTratamiento.isEmpty() && listaDetalleTratamiento.size() > 0) {
				for (DetalleDescargasLiquidasTratamientoAguas tratamientoBase : listaDetalleTratamiento) {
					if(!catalogoTratamientoListSelected.contains(tratamientoBase.getCatalogoTratamientoAguas())) {
						tratamientoBase.setEstado(false);
						detalleDescargasTratamientoFacade.save(tratamientoBase, userLogged);
					} else {
						catalogoTratamientoList.remove(tratamientoBase.getCatalogoTratamientoAguas());
					}
				}
			}
		}
		for (CatalogoTratamientoAguas tratamiento : catalogoTratamientoList) {
			DetalleDescargasLiquidasTratamientoAguas detalleNuevo = new DetalleDescargasLiquidasTratamientoAguas();
			detalleNuevo.setDetalleDescargasLiquidas(detalleDescargasLiquidas);
			detalleNuevo.setCatalogoTratamientoAguas(tratamiento);
			
			if (tratamiento.getDescripcion().equals("Otros")) {
				detalleNuevo.setOtroTratamientoAgua(detalleDescargasLiquidas.getOtroTratamientoAgua());
			}
			
			detalleDescargasTratamientoFacade.save(detalleNuevo, userLogged);
		}
	}
	
	private void guardarCaracteristicasMedicion(){
		boolean validar=validarCaracteristicasMedicion();
		if(validar)
		{
			try {								
				guardarPuntoMonitoreo();
				
				if(actualizaAprobacionMonitoreo && adjuntoAprobacionMonitoreo.getContenidoDocumento()!=null){
					adjuntoAprobacionMonitoreo.setIdTable(detalleDescargasLiquidas.getId());
					adjuntoAprobacionMonitoreo.setNombreTabla(DetalleDescargasLiquidas.class.getSimpleName());
					adjuntoAprobacionMonitoreo.setDescripcion("Oficio de aprobación del punto de monitoreo");
					adjuntoAprobacionMonitoreo.setEstado(true);				
					documentosFacade.guardarDocumentoAlfrescoSinProyecto(descargasLiquidas.getCodigo(), "APROBACION_PUNTO_MONITOREO", detalleDescargasLiquidas.getId().longValue(), adjuntoAprobacionMonitoreo, TipoDocumentoSistema.TIPO_DOCUMENTO_OFICIO_DE_APROBACION_PUNTO_MONITOREO, null);
					actualizaAprobacionMonitoreo=false;
				}
				cargarListaDescargas();
    			seleccionarDescargas(descargasLiquidas);
			} catch (Exception e) {
				JsfUtil.addMessageError("Error al guardar Información. Por favor comuníquese con Mesa de Ayuda");
				LOG.error(e);
				validar=false;
			}
		}
		if(validar)
			JsfUtil.addMessageInfo("Información de Características de Mediciones guardada con éxito");
		
	}
		
	private void guardarRegistroMedicion(){
//		boolean validar=validarRegistroMedicion(detalleDescargasLiquidas);
		boolean validar=true;
		if(validar){
			try {
				if(!datoObtenidoList.isEmpty()){
					Usuario userLogged=JsfUtil.getLoggedUser();			
					guardarPuntoMonitoreo();
					
					
					for (DatoObtenidoMedicionDescargas dato : datoObtenidoList) {
						dato.setDetalleDescargasLiquidas(detalleDescargasLiquidas);
						datoObtenidoMedicionDescargasFacade.guardar(dato, userLogged,habilitarObservaciones?numeroObservaciones:0);
					}
					for (DatoObtenidoMedicionDescargas dato : datoObtenidoEliminadosList) {
						dato.setEstado(false);
						datoObtenidoMedicionDescargasFacade.guardar(dato, userLogged,habilitarObservaciones?numeroObservaciones:0);
					}
					
				}else{
					validar=false;
					JsfUtil.addMessageError("Demanda química de oxígeno requerido");
				}
			} catch (Exception e) {
				JsfUtil.addMessageError("Error al guardar Información. Por favor comuníquese con Mesa de Ayuda");
				e.printStackTrace();
				LOG.error(e);
				validar=false;
			}
		}
		if(validar)
			JsfUtil.addMessageInfo("Información de Datos Obtenidos guardada con éxito");
		
	}

	private void guardarLaboratorios(){		
		boolean validar=validarLaboratorios();
		if(validar){
			try {
				Usuario userLogged=JsfUtil.getLoggedUser();
				guardarPuntoMonitoreo();
				
				for (DatosLaboratorio laboratorio : datosLaboratorioList) {
					laboratorio.setDescargasLiquidas(descargasLiquidas);
					datosLaboratorioFacade.guardar(laboratorio, userLogged,habilitarObservaciones?numeroObservaciones:0);
					if(laboratorio.getListaDocumentosLaboratorios()!=null){
						for (Documento documentoLaboratorio : laboratorio.getListaDocumentosLaboratorios()) {
							TipoDocumentoSistema tipoDoc;
							Integer idTipodoc = documentoLaboratorio.getTipoDocumento().getId();
							
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
							
							if(documentoLaboratorio.getContenidoDocumento()!=null) {
								documentoLaboratorio.setIdTable(laboratorio.getId());
								documentoLaboratorio.setNombreTabla(DatosLaboratorio.class.getSimpleName());
								documentoLaboratorio.setDescripcion("Informe de monitoreo emitido por el laboratorio");
								documentoLaboratorio.setEstado(true);				
								documentosFacade.guardarDocumentoAlfrescoSinProyecto(descargasLiquidas.getCodigo(), "INFORMES_LABORATORIOS", laboratorio.getId().longValue(), documentoLaboratorio, tipoDoc, null);
							}
						}
					}						
					
				}
				for (DatosLaboratorio laboratorio : datosLaboratorioEliminadosList) {
					laboratorio.setEstado(false);
					datosLaboratorioFacade.guardar(laboratorio, userLogged,habilitarObservaciones?numeroObservaciones:0);
				}
				for (Documento documentoLaboratorio: datosLaboratorioEliminadosDocumentosList) {
					documentoLaboratorio.setEstado(false);				
					documentosFacade.actualizarDocumento(documentoLaboratorio);
				}
				datosLaboratorioEliminadosList.clear();
				datosLaboratorioEliminadosDocumentosList.clear();
				
			} catch (Exception e) {
				JsfUtil.addMessageError("Error al guardar Información. Por favor comuníquese con Mesa de Ayuda");
				LOG.error(e);
				validar=false;
			}
		}
		if(validar)
			JsfUtil.addMessageInfo("Información de Laboratorios guardada con éxito");
	}
	
	private void guardarSustancias(){		
		boolean validar=validarSustancias();
		if(validar){
			try {
				Usuario userLogged=JsfUtil.getLoggedUser();
				guardarPuntoMonitoreo();			
				
				for (SubstanciasRetce sustancia : sustanciasRetceList) {
					sustancia.setDetalleDescargasLiquidas(detalleDescargasLiquidas);
					sustanciasRetceFacade.guardar(sustancia, userLogged,habilitarObservaciones?numeroObservaciones:0);
				}
				for (SubstanciasRetce sustancia : sustanciasRetceEliminadosList) {
					sustancia.setEstado(false);
					sustanciasRetceFacade.guardar(sustancia, userLogged,habilitarObservaciones?numeroObservaciones:0);
				}			
			} catch (Exception e) {
				JsfUtil.addMessageError("Error al guardar Información. Por favor comuníquese con Mesa de Ayuda");
				LOG.error(e);
				validar=false;
			}
		}
		if(validar)
			JsfUtil.addMessageInfo("Información de Sustancias Retce guardada con éxito");
	}

	private void guardarTecnico(){
		boolean validar=validarTecnico();
		if(validar)
		{
			try {
				Usuario userLogged=JsfUtil.getLoggedUser();
				guardarPuntoMonitoreo();
				
				tecnicoResponsableFacade.guardar(tecnicoResponsable, userLogged,habilitarObservaciones?numeroObservaciones:0);
				descargasLiquidas.setTecnicoResponsable(tecnicoResponsable);
				descargasLiquidasFacade.guardar(descargasLiquidas, userLogged, habilitarObservaciones?numeroObservaciones:0);			
			} catch (Exception e) {
				JsfUtil.addMessageError("Error al guardar Información. Por favor comuníquese con Mesa de Ayuda");
				LOG.error(e);
				validar=false;
			}
		}
		if(validar)
			JsfUtil.addMessageInfo("Información de Técnico guardada con éxito");
	}
	
	private void guardarJustificacion(){		
		try {			
			guardarPuntoMonitoreo();
			
			if(actualizaJustificacion && adjuntoJustificacion.getContenidoDocumento()!=null){
				adjuntoJustificacion.setIdTable(descargasLiquidas.getId());
				adjuntoJustificacion.setNombreTabla(DescargasLiquidas.class.getSimpleName());
				adjuntoJustificacion.setDescripcion("Accion Correctiva de Descargas Liquidas");
				adjuntoJustificacion.setEstado(true);				
				documentosFacade.guardarDocumentoAlfrescoSinProyecto(descargasLiquidas.getCodigo(), "ACCION_CORRECTIVA", descargasLiquidas.getId().longValue(), adjuntoJustificacion, TipoDocumentoSistema.JUSTIFICACION_DESCARGA_LIQUIDA, null);
				actualizaJustificacion=false;
				JsfUtil.addMessageInfo("Información de Justificación guardada con éxito");
			}
			habilitaFinalizar=true;
		} catch (Exception e) {
			JsfUtil.addMessageError("Error al guardar Información. Por favor comuníquese con Mesa de Ayuda");
			e.printStackTrace();
			LOG.error(e);
		}
		
	}

	public void btnAtras(){
		masReportes=false;
		habilitaFinalizar=false;
	}
		
	public void guardar() {        
        String currentStep = wizardBean.getCurrentStep()==null?"pasoCaracteristicas":wizardBean.getCurrentStep();        
        habilitaFinalizar=false;
        if(habilitarIngreso)
        	switch (currentStep) {		
    		case "pasoCaracteristicas":
    			guardarCaracteristicasMedicion();
    			//caracteristicasMonitoreoListener();
    			if(detalleDescargasLiquidas.getCaracteristicasPuntoMonitoreo()!=null && detalleDescargasLiquidas.getCatalogoTipoCuerpoReceptorCaracteristicasPunto() != null){
    				caracteristicasPuntoMonitoreoTablaList=caracteristicasPuntoMonitoreoFacade.findTablaByCaracteristicaPorTipoCuerpo(detalleDescargasLiquidas.getCaracteristicasPuntoMonitoreo(), detalleDescargasLiquidas.getCatalogoTipoCuerpoReceptorCaracteristicasPunto().getCatalogoTipoCuerpoReceptor());
    				parametrosTablasList=new ArrayList<ParametrosTablas>();
    				for (CaracteristicasPuntoMonitoreoTabla tabla : caracteristicasPuntoMonitoreoTablaList) {
    					parametrosTablasList.addAll(parametrosDescargasLiquidasFacade.findParametrosByTabla(tabla));
    				}			
    			}
    			break;
    		case "pasoMediciones":
    			guardarRegistroMedicion();
    			break;
    		case "pasoLaboratorios":
    			guardarLaboratorios();
    			break;
    		case "pasoSustancias":
    			guardarSustancias();
    			break;
    		case "pasoTecnico":
    			guardarTecnico();
    			break;
    		case "pasoJustificacion":
    			guardarJustificacion();
    			break;
    		default:
    			break;
    		}
    }
	
	public void NuevoPuntoMonitoreo(){
			guardarRegistroMedicion();
			ocultarFormulario();
			verFormulario=true;
			editarFuente=false;
			agregarDetalle(descargasLiquidas);
	}
	
	public void enviar(){
		guardarJustificacion();
		if(!validarServicios())
		{	
			JsfUtil.addMessageWarning("Error al iniciar servicios, comuniquese con mesa de ayuda");
		}else{
			getImagenMapaAlfresco();
			if(validarDatos())
			{
				boolean operacionCorrecta=false;
				if(habilitarCorreccion){
					try {
						procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(),bandejaTareasBean.getTarea().getProcessInstanceId(), null);
						operacionCorrecta=true;
						nombreTarea=null;
					} catch (JbpmException e) {					
						e.printStackTrace();
					}
				}else{
					ProcesoRetceController procesoRetceController=JsfUtil.getBean(ProcesoRetceController.class);
					if(!descargasLiquidas.getEnviado()){
						if(procesoRetceController.iniciarProceso(descargasLiquidas)){
							operacionCorrecta=true;
						}
					}
					if(operacionCorrecta){
						descargasLiquidas.setEnviado(true);
						descargasLiquidasFacade.guardar(descargasLiquidas, JsfUtil.getLoggedUser(),habilitarObservaciones?numeroObservaciones:0);					
					}
				}
				
				if(operacionCorrecta){
					JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
//					JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
					ocultarFormulario();
					cargarListaDescargas();
				}

			}
		}
	}
	
	public void irTarea() throws JbpmException{
		TaskSummaryCustom tarea= bandejaTareasBean.getTarea();
		BandejaTareasController bandejaTareasController =JsfUtil.getBean(BandejaTareasController.class);
		try {
			bandejaTareasController.startTask(tarea);
		} catch (ParseException e) {			
			e.printStackTrace();
		}
	}
	
	//HISTORICOS
	@Getter
	private List<DescargasLiquidas> descargasListHistorico=new ArrayList<DescargasLiquidas>();
	@Getter
	private List<DetalleDescargasLiquidas> detalleDescargasListHistorico=new ArrayList<DetalleDescargasLiquidas>();
	public void detalleDescargasHistoricoListener(DetalleDescargasLiquidas item){
		this.detalleDescargasListHistorico=item.getHistorialLista();
		this.descargasListHistorico=descargasLiquidas.getHistorialLista();		
	}
	
	@Getter
	private List<DatoObtenidoMedicionDescargas> datosObtenidoListHistorico=new ArrayList<DatoObtenidoMedicionDescargas>();
	public void datoObtenidoHistoricoListener(DatoObtenidoMedicionDescargas item){
		this.datosObtenidoListHistorico=item.getHistorialLista();		
	}
	
	@Getter
	private List<DatosLaboratorio> datosLaboratorioListHistorico=new ArrayList<DatosLaboratorio>();
	public void datoLaboratorioHistoricoListener(DatosLaboratorio item){
		this.datosLaboratorioListHistorico=item.getHistorialLista();		
	}
	
	@Getter
	private List<SubstanciasRetce> sustanciasListHistorico=new ArrayList<SubstanciasRetce>();
	public void sustanciaHistoricoListener(SubstanciasRetce item){
		this.sustanciasListHistorico=item.getHistorialLista();		
	}
	
	@Getter
	private List<TecnicoResponsable> tecnicosListHistorico=new ArrayList<TecnicoResponsable>();
	public void tecnicoHistoricoListener(TecnicoResponsable item){
		this.tecnicosListHistorico=item.getHistorialLista();		
	}
	
	public void validarCaracteristicas(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();		
		
		//validar coordenada
		if(detalleDescargasLiquidas.getCoordenadaX() != null && detalleDescargasLiquidas.getCoordenadaY() != null) {
			String coordenadaX = detalleDescargasLiquidas.getCoordenadaX().toString().replace(",", ".");
			String coordenadaY = detalleDescargasLiquidas.getCoordenadaY().toString().replace(",", ".");
			
			String mensaje = JsfUtil.validarCoordenadaPunto17S(coordenadaX, coordenadaY, 
					informacionProyecto.getCodigoRetce());
			
			if(mensaje != null)
				errorMessages.add(new FacesMessage(
						FacesMessage.SEVERITY_ERROR, mensaje, null));
		}

		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public boolean validarCoordenadas() {
		List<FacesMessage> errorMessages = new ArrayList<>();
		boolean correcto = false;
		UbicacionesGeografica ubicacionFuente = new UbicacionesGeografica();
		detalleDescargasLiquidas.setUbicacionGeografica(ubicacionFuente);
		
		if (detalleDescargasLiquidas.getCoordenadaX() != null && detalleDescargasLiquidas.getCoordenadaY() != null) {
			
			
			String coordenadaX = detalleDescargasLiquidas.getCoordenadaX().toString().replace(",", ".");
			String coordenadaY = detalleDescargasLiquidas.getCoordenadaY().toString().replace(",", ".");

			// String mensaje = JsfUtil.validarCoordenadaPunto17S(coordenadaX,
			// coordenadaY, informacionProyecto.getCodigoRetce());
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
							detalleDescargasLiquidas.setUbicacionGeografica(ubicacionFuente);
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
	
	public void validarOtros() {
		verOtroTratamiento = false;
		for (CatalogoTratamientoAguas tratamiento : catalogoTratamientoListSelected) {
			if (tratamiento.getDescripcion().equals("Otros")) {
				verOtroTratamiento = true;
			}
		}
	}
	public void validarOtroLugar() {
		verOtroLugar = false;
			if (detalleDescargasLiquidas.getLugarDescarga().getDescripcion().equals("Otros")) {
				verOtroLugar = true;
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
	public void getImagenMapaAlfresco(){
		try {
			
			ResponseImagen resCer = new ResponseImagen();
			resCer=wsMapas.getGenerarMapaImagenWSPort().generarImagenMapaRetce(descargasLiquidas.getCodigo().toString(), 2);
			if(resCer.getWorkspaceAlfresco()==null)
			{
				JsfUtil.addMessageWarning("Error al generar la imagen de puntos de coordenadas");
			}
			else
			{
				//imagenCoordenadas
				TipoDocumento tipoDocumento = new TipoDocumento();
				tipoDocumento.setId(TipoDocumentoSistema.RETCE_IMAGEN_COORDENADAS.getIdTipoDocumento());
				imagenCoordenadas.setIdAlfresco(resCer.getWorkspaceAlfresco());
				imagenCoordenadas.setIdTable(descargasLiquidas.getId());
				imagenCoordenadas.setNombreTabla(DescargasLiquidas.class.getSimpleName());
				imagenCoordenadas.setDescripcion("mapa-puntosmonitoreo_"+descargasLiquidas.getCodigo());
				imagenCoordenadas.setNombre("mapa-puntosmonitoreo_"+descargasLiquidas.getCodigo());
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
