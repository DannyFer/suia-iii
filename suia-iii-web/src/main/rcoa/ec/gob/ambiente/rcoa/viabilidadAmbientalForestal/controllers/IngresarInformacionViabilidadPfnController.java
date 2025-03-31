package ec.gob.ambiente.rcoa.viabilidadAmbientalForestal.controllers;

import index.ContienePoligono_entrada;
import index.ContienePoligono_resultado;
import index.ContieneZona_entrada;
import index.ContieneZona_resultado;
import index.Intersecado_capa;
import index.Intersecado_coordenada;
import index.Intersecado_entrada;
import index.Intersecado_resultado;
import index.SVA_Reproyeccion_IntersecadoPortTypeProxy;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.imageio.ImageIO;
import javax.servlet.ServletContext;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import _154._1._180._10._8080.consultatitulossenescytwsv3.servicioconsultatitulo.GraduadoReporteDTO;
import _154._1._180._10._8080.consultatitulossenescytwsv3.servicioconsultatitulo.NivelTitulosDTO;

import com.itextpdf.io.IOException;
import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.cliente.ConsultaRucCedula;
import ec.gob.ambiente.rcoa.certificado.interseccion.facade.CertificadoInterseccionCoaFacade;
import ec.gob.ambiente.rcoa.enums.CatalogoTipoCoaEnum;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.CatalogoFasesCoaFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.CatalogoFasesCoa;
import ec.gob.ambiente.rcoa.facade.CapasCoaFacade;
import ec.gob.ambiente.rcoa.facade.CatalogoCoaFacade;
import ec.gob.ambiente.rcoa.facade.CoordenadasProyectoCoaFacade;
import ec.gob.ambiente.rcoa.facade.DetalleInterseccionProyectoAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaAmbientalCoaShapeFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.HigherClassificationFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.SpecieTaxaFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.model.HigherClassification;
import ec.gob.ambiente.rcoa.inventarioForestal.model.SpecieTaxa;
import ec.gob.ambiente.rcoa.model.CapasCoa;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.model.CoordenadasProyecto;
import ec.gob.ambiente.rcoa.model.DetalleInterseccionProyectoAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaAmbientalCoaShape;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.preliminar.contoller.CoordenadasRcoaBean;
import ec.gob.ambiente.rcoa.util.CoordendasPoligonos;
import ec.gob.ambiente.rcoa.util.UtilGenerarDocumentoViabilidadForestal;
import ec.gob.ambiente.rcoa.utils.stringTablasViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.controllers.UtilViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.AfectacionForestalFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.AnalisisResultadoForestalFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ConsultorForestalFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.CoordenadaSitioMuestralFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.DocumentosViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.EquipoConsultorForestalFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.EspeciesImportanciaForestalFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.EspeciesInformeForestalFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InformeFactibilidadForestalFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InterseccionViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.SitioMuestralFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.UbicacionSitioMuestralFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.UnidadHidrograficaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.AfectacionForestal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.AnalisisResultadoForestal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ConsultorForestal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.CoordenadaSitioMuestral;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.DocumentoViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.EquipoConsultorForestal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.EspeciesImportanciaForestal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.EspeciesInformeForestal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.InformeFactibilidadForestal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.InformeFactibilidadForestalEntity;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ProyectoTipoViabilidadCoa;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.SitioMuestral;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.UbicacionSitioMuestral;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.UnidadHidrografica;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ViabilidadCoa;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.BandejaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.TipoForma;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilFichaMineria;
import ec.gob.ambiente.suia.webservice.facade.AsignarTareaFacade;
import ec.gob.registrocivil.consultacedula.Cedula;

@ManagedBean
@ViewScoped
public class IngresarInformacionViabilidadPfnController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Logger LOG = Logger.getLogger(IngresarInformacionViabilidadPfnController.class);
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@Setter
	@Getter
	@ManagedProperty(value = "#{coordenadasRcoaBean}")
	private CoordenadasRcoaBean coordenadasRcoaBean;
			
	@EJB
	private DocumentosFacade documentosFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private ViabilidadCoaFacade viabilidadCoaFacade;
	@EJB
	private AreaFacade areaFacade;
	@EJB
    private ProcesoFacade procesoFacade;
	@EJB
	private DocumentosViabilidadFacade documentosViabilidadFacade;
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	@EJB
	private InterseccionViabilidadCoaFacade interseccionViabilidadCoaFacade;
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	@EJB
    private OrganizacionFacade organizacionFacade;
	@EJB
    private ContactoFacade contactoFacade;
	@EJB
	private ProyectoLicenciaAmbientalCoaShapeFacade proyectoLicenciaAmbientalCoaShapeFacade;
	@EJB
	private CoordenadasProyectoCoaFacade coordenadasProyectoCoaFacade;
	@EJB
    private AsignarTareaFacade asignarTareaFacade;
	@EJB
	private CertificadoInterseccionCoaFacade certificadoInterseccionCoaFacade;
	@EJB
	private CatalogoFasesCoaFacade catalogoFasesCoaFacade;
	@EJB
	private BandejaFacade bandejaFacade;
	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;
	@EJB
	private UbicacionGeograficaFacade ubicacionfacade;
	@EJB
	private DetalleInterseccionProyectoAmbientalFacade detalleInterseccionProyectoFacade;
	@EJB
	private HigherClassificationFacade higherClassificationFacade;
	@EJB
	private SpecieTaxaFacade specieTaxaFacade;
	@EJB
	private ConsultorForestalFacade consultorForestalFacade;
	@EJB
	private CatalogoCoaFacade catalogoCoaFacade;
	
	@Getter
	@Setter
	private InformeFactibilidadForestal informeFactivilidadForestal;
	
	@Getter
	@Setter
	private ViabilidadCoa viabilidadProyecto;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Getter
	@Setter
	private ProyectoTipoViabilidadCoa proyectoTipoViabilidadCoa;
	
	@Getter
	@Setter
	private Usuario operador;
	
	@Getter
	@Setter
	private Organizacion organizacion;
	
	@Getter
    @Setter
    private TipoSector sector;

	@Getter
	@Setter
	private List<CoordendasPoligonos> coordenadasImplantacion, coordenadasGeograficas;
	
	@Getter
	@Setter
	private List<UbicacionesGeografica> ubicacionProyecto;
	
	@Getter
	@Setter
	private List<CatalogoFasesCoa> listaFasesPorSector;
	
	@Getter
	@Setter
	private Integer idProyecto;
	
	@Getter
	@Setter
	private String nombreTarea;
	
	@Getter
	@Setter
	private Boolean guiaDescargada, mostrarFases;
	
	@Getter
	@Setter
	private String nombreOperador, representanteLegal, correo, direccion, telefono, interseccion;
	
	private Map<String, Object> variables;
	
	@Getter
	@Setter
	private List<DetalleInterseccionProyectoAmbiental> listaInterseccionesCobertura, listaInterseccionesEcosistema, listaInterseccionesConvenios;
	
	@Getter
	@Setter
	private List<DocumentoViabilidad> listaImagenesSatelite, listaImagenesSateliteEliminar;
	
	/**MODIFICAR CON EL VALOR CORRESPONDIENTE*/
	@Getter
	@Setter
	private List<String> listaConvenios;
	
	@Getter
	@Setter
	private List<UnidadHidrografica> listaInterseccionUHidrograficas, listaInterseccionUHidrograficasEliminar;
	
	@Getter
	@Setter
	private UnidadHidrografica unidadHidrografica;
	
	@Getter
	@Setter
	private DocumentoViabilidad documentoAdjunto, fotoArchivo, imagenSatelital;
	
	@Getter
	@Setter
	private List<CoordenadaSitioMuestral> listaCoordenadaSitioMuestral, listaCoordenadaSitioMuestralEliminar;
	
	@Getter
	@Setter
	private List<EspeciesInformeForestal> listaCaracterizacionCualitativaEspecies, listaCaracterizacionCualitativaEspeciesEliminar;
	
	@Getter
	@Setter
	private EspeciesInformeForestal especiesInformeForestal;
	
	@Getter
	@Setter
	private Integer nivelTaxonomia, nivelTaxonomiaAB, nivelTaxonomiaEspecieImportancia;
	
	@Getter
	@Setter
	private List<String> taxonomiaFamiliaList,taxonomiaGeneroList, taxonomiaEspecieList;
	
	@Setter
    @Getter
    private HigherClassification objFamilia = new HigherClassification();
	
	@Setter
    @Getter
    private HigherClassification objGenero = new HigherClassification();
	
	@Setter
    @Getter
    private SpecieTaxa objEspecie = new SpecieTaxa();
	
    @Setter
    @Getter
    private SpecieTaxa objEspecieOtro = new SpecieTaxa();
    
    @Setter
	@Getter
	private List<HigherClassification> listFamilia = new ArrayList<HigherClassification>();
    
    @Setter
	@Getter
	private List<HigherClassification> listFamiliaSelect = new ArrayList<HigherClassification>();
    
    @Setter
	@Getter
	private List<HigherClassification> listGenero = new ArrayList<HigherClassification>();

    @Setter
	@Getter
	private List<SpecieTaxa> listEspecie = new ArrayList<SpecieTaxa>();
    
    @Getter
    private boolean blockFamilia=true, blockGenero=true, blockEspecie=true;
    
    @Getter
	@Setter
	private List<DocumentoViabilidad> listaFotografiaCuantitativa, listaFotografiaCuantitativaEliminar;
    
    @Getter
    @Setter
    private DocumentoViabilidad fotografiaCuantitativa, fotografiaOficina;
    
    @Getter
   	@Setter
   	private List<DocumentoViabilidad> listaFotografiaOficina, listaFotografiaOficinaEliminar;
    
    @Getter
   	@Setter
   	private List<DocumentoViabilidad> listaFotoArchivo, listaFotoArchivoEliminar;
    
    @Getter
    @Setter
    private List<DocumentoViabilidad> listaDocumentoAdjunto, listaDocumentoAdjuntoEliminar;
    
    @Getter
    @Setter
    private String metodoRecoleccion;
            
    @Getter
    @Setter
    private Double totalDnR, totalDmR, totalIVI;
    
    @Getter
    @Setter
    private Double indiceShannon, indiceSimpson, exponencialShannon, inversoSimpson;
    
    private byte[] plantillaCoordenadas;
    
    @Getter
    @Setter
    private List<SitioMuestral> listaSitiosMuestrales;
    
	@Getter
    @Setter
	private Integer index;
	
	@Getter
	@Setter
	private Boolean apendiceCites, conservacionUICN, endemica, aprovechamientoCondicionado,mostrarListaConvenio=true,mostrarlistaUnidadesHidrograficas=true;
	
	@Getter
	@Setter
	private List<DocumentoViabilidad> listaDocumentosAnexos, listaDocumentosAnexosEliminar;
	
	@Getter
	@Setter
	private boolean editarSeleccionado, esCobertura2022;
	
	@Getter
	@Setter
	private List<String> coordenadasGeograficasProyecto;
	
	@Getter
	@Setter
	private List<EquipoConsultorForestal> listaEquipoConsultor, listaEquipoConsultorEliminar;
	
	@Getter
	@Setter
	private EquipoConsultorForestal consultorPrincipal, equipoConsultor;
	
	@Getter
	@Setter
	private CoordenadaSitioMuestral coordenadaSitio;
	
	@Getter
	@Setter
	private List<CatalogoGeneralCoa> listaEstadoConservacion, listaApendiceCITES;
	
	@Getter
	@Setter
	private List<SitioMuestral> listaSitiosIngresados;
	
	@Getter
	@Setter
	private List<EspeciesInformeForestal> listaEspeciesCenso, listaEspeciesCensoEliminar,listaEspecieGenerico;
	
	@Getter
	@Setter
	private EspeciesInformeForestal especiesInformeForestalCenso;
	
	@Getter
	@Setter
	private SitioMuestral sitioMuestralCenso;
	
	@Getter
	@Setter
	private List<EspeciesImportanciaForestal> listaEspeciesImportancia, listaEspeciesImportanciaEliminar;
	
	@Getter
	@Setter
	private EspeciesImportanciaForestal especiesInformeForestalImportancia;
	
	@Getter
	@Setter
	private List<String> listaValorImportancia;
	
	@Getter
	@Setter
    private List<CoordenadaSitioMuestral> coordenadasGeograficasAux = new ArrayList<>();
		
	@Getter
	@Setter
	private List<AnalisisResultadoForestal> listaIndiceValorImportancia, listaIndiceValorImportanciaEliminar;
	
	@Getter
	@Setter
	private SitioMuestral sitioMuestral;
	
	@Getter
	@Setter
	private List<SitioMuestral> listaSitioMuestral, listaSitioMuestralEliminar, listaSitioSumatoria, listaSitioMuestreo, listaSitioMuestreoEliminar, listaSitioMuestreoAux, listaSitioMuestralAux,listaSitioSumatoriaCenso;
	
	@Getter
	@Setter
	private List<EspeciesInformeForestal> listaEspecies, listaEspeciesEliminar, listaEspeciesMuestreo, listaEspeciesMuestreoEliminar;
	
	@Getter
	@Setter
	private EspeciesInformeForestal especiesInformeForestalMuestreo;
	
	@Getter
	@Setter
	private List<AfectacionForestal> listaAfectacionForestal, listaAfectacionForestalEliminar;
	
	@Getter
	@Setter
	private AfectacionForestal afectacionForestal;
	
	@Getter
	@Setter
	private List<String> listaIdEcosistemas, listaIdCoberturaVegetal, listaIdConvenio, listaIdUnidadHidrograficas;
	
	private final ConsultaRucCedula consultaRucCedula = new ConsultaRucCedula(Constantes.getUrlWsRegistroCivilSri());
	public static final Double FACTOR_FORMA = 0.7;
	
	@PostConstruct
	private void iniciar() {
		
		try {
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
	
			String idProyectoString=(String)variables.get("idProyecto");
			idProyecto = Integer.valueOf(idProyectoString);
			
			nombreTarea = bandejaFacade.getNombreTarea(JsfUtil.getCurrentTask().getTaskId());
			
			
			proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
			
			proyectoTipoViabilidadCoa = viabilidadCoaFacade.getTipoViabilidadPorProyecto(idProyecto, false);
			
			if(proyectoTipoViabilidadCoa == null) {
				proyectoTipoViabilidadCoa = new ProyectoTipoViabilidadCoa();
				proyectoTipoViabilidadCoa.setIdProyectoLicencia(idProyecto);
				proyectoTipoViabilidadCoa.setEsViabilidadSnap(false);
				viabilidadCoaFacade.guardarProyectoTipoViabilidadCoa(proyectoTipoViabilidadCoa);
			}
			
			viabilidadProyecto = viabilidadCoaFacade.getViabilidadPorTipoProyecto(proyectoTipoViabilidadCoa.getId());
			
			operador = proyectoLicenciaCoa.getUsuario();
			
			if(viabilidadProyecto != null && viabilidadProyecto.getId() != null) {
				informeFactivilidadForestal = informeFactibilidadForestalFacade.getInformePorViabilidad(viabilidadProyecto.getId());
			}
			
			if(informeFactivilidadForestal == null) {
				String marcoLegalPredefinido=catalogoCoaFacade.obtenerCatalogoPorId(Constantes.ID_MARCO_LEGAL).getValor();
				informeFactivilidadForestal = new InformeFactibilidadForestal();
				informeFactivilidadForestal.setMarcoLegal(marcoLegalPredefinido);
			}
			
			recuperarInfoOperador();
			
			cargarInformacion();
			sumatoriaCenso();
			
	        RequestContext context = RequestContext.getCurrentInstance();			
			if(listaInterseccionesConvenios.size()<=0) {
			    mostrarListaConvenio=false;
			    context.update("form:dtConvenios");
			}
			if(listaInterseccionUHidrograficas.size()<=0) {
				mostrarlistaUnidadesHidrograficas=false;
			   context.update("form:dtInterUHidrograficas");
			}			
			mostrarlistaUnidadesHidrograficas=false;	
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos RegistroForestalController.");
		}
	}
	
	public void validarTareaBpm() {
		switch (nombreTarea) {
		case "ingresarInformacionForestal":
			JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
					"/pages/rcoa/viabilidadAmbientalForestal/ingresarInformacion.jsf");
			break;
		case "ingresarAclaratoriaForestal":
			JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
					"/pages/rcoa/viabilidadAmbientalForestal/ingresarAclaratoria.jsf");
			break;
		default:
			break;
		}
	}
	
	public void recuperarInfoOperador() throws ServiceException {
		coordenadasGeograficas = new ArrayList<>();
		coordenadasImplantacion = new ArrayList<>();
		mostrarFases = false;

		organizacion = organizacionFacade.buscarPorRuc(operador.getNombre());
		
		if (organizacion != null) {
        	//Cuando el representante legal es otra organizacion
			Organizacion organizacionRep = organizacion;				
			while (organizacionRep.getPersona().getPin().length() == 13) {
				Organizacion orgaAux = organizacionFacade.buscarPorRuc(organizacionRep.getPersona().getPin());
				if (orgaAux != null) {
					organizacionRep = orgaAux;
				} else {
					break;
				}
			}
			
			representanteLegal = organizacionRep.getPersona().getNombre();
			nombreOperador = organizacion.getNombre();
		} else {
			representanteLegal = "";
			nombreOperador = JsfUtil.getLoggedUser().getPersona().getNombre();				
		}
		
		List<Contacto> listaContactos = null;
		correo = "";
		direccion = "";
		telefono = "";
		listaContactos = contactoFacade.buscarUsuarioNativeQuery(operador.getNombre());

        for (Contacto c : listaContactos) {
            if (correo.isEmpty() && c.getFormasContacto().getId().intValue() == FormasContacto.EMAIL) {
                correo = c.getValor();
            } else if (direccion.isEmpty() && c.getFormasContacto().getId().intValue() == FormasContacto.DIRECCION) {
                direccion = c.getValor();
            } else if (telefono.isEmpty() && c.getFormasContacto().getId().intValue() == FormasContacto.TELEFONO) {
                telefono = c.getValor();
            }
        }

		List<ProyectoLicenciaAmbientalCoaShape> formas = new ArrayList<ProyectoLicenciaAmbientalCoaShape>();
		coordenadasGeograficasProyecto = new ArrayList<String>();
		formas = proyectoLicenciaAmbientalCoaShapeFacade.buscarFormaGeograficaPorProyecto(proyectoLicenciaCoa, 2, 0);
		if (formas == null) {
			formas = new ArrayList<ProyectoLicenciaAmbientalCoaShape>();
		} else {
			for (ProyectoLicenciaAmbientalCoaShape forma : formas) {
				List<CoordenadasProyecto> coordenadasGeograficasGeo = coordenadasProyectoCoaFacade.buscarPorForma(forma);

				CoordendasPoligonos poligono = new CoordendasPoligonos();
				poligono.setCoordenadas(coordenadasGeograficasGeo);
				poligono.setTipoForma(forma.getTipoForma());
				coordenadasGeograficas.add(poligono);
				
				String arrayCoordenadas = "";
				for (CoordenadasProyecto rowCoordenadas : coordenadasGeograficasGeo) {
					arrayCoordenadas += (arrayCoordenadas == "") ? rowCoordenadas.getX().toString()+ " " + rowCoordenadas.getY().toString() 
							: "," + rowCoordenadas.getX().toString() + " " + rowCoordenadas.getY().toString();
				}
				coordenadasGeograficasProyecto.add(arrayCoordenadas);
			}
		}
		List<ProyectoLicenciaAmbientalCoaShape> formasImplantacion = proyectoLicenciaAmbientalCoaShapeFacade.buscarFormaGeograficaPorProyecto(proyectoLicenciaCoa, 1, 0);
		if (formasImplantacion == null) {
			formasImplantacion = new ArrayList<ProyectoLicenciaAmbientalCoaShape>();
		} else {
			for (ProyectoLicenciaAmbientalCoaShape forma : formasImplantacion) {
				List<CoordenadasProyecto> coordenadasGeograficasImplantacion = coordenadasProyectoCoaFacade.buscarPorForma(forma);
				
				CoordendasPoligonos poligono = new CoordendasPoligonos();
				poligono.setCoordenadas(coordenadasGeograficasImplantacion);
				poligono.setTipoForma(forma.getTipoForma());
				coordenadasImplantacion.add(poligono);
			}
		}
				
		ubicacionProyecto = proyectoLicenciaCoaUbicacionFacade.buscarPorProyecto(proyectoLicenciaCoa);
		
		interseccion = interseccionViabilidadCoaFacade.getInterseccionesForestal(proyectoLicenciaCoa.getId(), 2);
		
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
				break;
			}
		}		

		UbicacionesGeografica ubicacion = ubicacionGeograficaFacade.buscarPorId(proyectoLicenciaCoa.getIdCantonOficina());
		areaTramiteViabilidad = ubicacion.getAreaCoordinacionZonal();
	}
	
	public void cargarInformacion(){
		
		listaInterseccionesCobertura = new ArrayList<>();
		listaInterseccionesEcosistema = new ArrayList<>();
		listaInterseccionesConvenios = new ArrayList<DetalleInterseccionProyectoAmbiental>();
		listaImagenesSatelite = new ArrayList<DocumentoViabilidad>();
		listaImagenesSateliteEliminar = new ArrayList<DocumentoViabilidad>();
		listaFotografiaCuantitativa = new ArrayList<DocumentoViabilidad>();
		listaFotografiaCuantitativaEliminar = new ArrayList<DocumentoViabilidad>();
		listFamilia = obtenerListaFamilia();
		imagenSatelital = new DocumentoViabilidad();
		fotografiaCuantitativa = new DocumentoViabilidad();
		fotografiaOficina = new DocumentoViabilidad();
		listaFotografiaOficina = new ArrayList<DocumentoViabilidad>();
		listaFotografiaOficinaEliminar = new ArrayList<DocumentoViabilidad>();
		listaFotoArchivo = new ArrayList<DocumentoViabilidad>();
		listaFotoArchivoEliminar = new ArrayList<DocumentoViabilidad>();
		fotoArchivo = new DocumentoViabilidad();
		documentoAdjunto = new DocumentoViabilidad();
		listaDocumentoAdjunto = new ArrayList<DocumentoViabilidad>();
		listaDocumentoAdjuntoEliminar = new ArrayList<DocumentoViabilidad>();
		listaSitiosMuestrales = new ArrayList<SitioMuestral>();
		listaDocumentosAnexos = new ArrayList<DocumentoViabilidad>();
		listaDocumentosAnexosEliminar = new ArrayList<DocumentoViabilidad>();
		listaInterseccionUHidrograficas = new ArrayList<UnidadHidrografica>();
		unidadHidrografica = new UnidadHidrografica();
		listaInterseccionUHidrograficasEliminar = new ArrayList<UnidadHidrografica>();
		listaEquipoConsultor = new ArrayList<EquipoConsultorForestal>();
		listaEquipoConsultorEliminar = new ArrayList<EquipoConsultorForestal>();
		consultorPrincipal = new EquipoConsultorForestal();
		consultorPrincipal.setConsultor(new ConsultorForestal());
		equipoConsultor = new EquipoConsultorForestal();
		equipoConsultor.setConsultor(new ConsultorForestal());
		coordenadaSitio = new CoordenadaSitioMuestral();
		coordenadaSitio.setSitioMuestral(new SitioMuestral());
		listaCaracterizacionCualitativaEspecies = new ArrayList<EspeciesInformeForestal>();
		listaCaracterizacionCualitativaEspeciesEliminar = new ArrayList<EspeciesInformeForestal>();
		especiesInformeForestal = new EspeciesInformeForestal();
		listaApendiceCITES = new ArrayList<CatalogoGeneralCoa>();
		listaEstadoConservacion = new ArrayList<CatalogoGeneralCoa>();
		listaSitiosIngresados = new ArrayList<SitioMuestral>();
		listaSitioSumatoriaCenso = new ArrayList<SitioMuestral>();
		listaEspeciesCenso =  new ArrayList<EspeciesInformeForestal>();
		listaEspecieGenerico=new ArrayList<EspeciesInformeForestal>();
		listaEspeciesCensoEliminar = new ArrayList<EspeciesInformeForestal>();
		especiesInformeForestalCenso = new EspeciesInformeForestal();
		sitioMuestralCenso = new SitioMuestral();
		listaEspeciesImportancia = new ArrayList<EspeciesImportanciaForestal>();
		listaEspeciesImportanciaEliminar = new ArrayList<EspeciesImportanciaForestal>();
		especiesInformeForestalImportancia = new EspeciesImportanciaForestal();
		listaValorImportancia = new ArrayList<String>();
		listaIndiceValorImportancia = new ArrayList<AnalisisResultadoForestal>();
		listaIndiceValorImportanciaEliminar = new ArrayList<AnalisisResultadoForestal>();
		sitioMuestral = new SitioMuestral();
		listaSitioMuestral = new ArrayList<SitioMuestral>();
		listaSitioMuestreo = new ArrayList<SitioMuestral>();
		listaSitioMuestralEliminar = new ArrayList<SitioMuestral>();
		listaSitioMuestreoEliminar = new ArrayList<SitioMuestral>();
		listaEspecies = new ArrayList<EspeciesInformeForestal>();
		listaEspeciesEliminar = new ArrayList<EspeciesInformeForestal>();
		especiesInformeForestalMuestreo = new EspeciesInformeForestal();
		listaSitioSumatoria = new ArrayList<SitioMuestral>();
		listaAfectacionForestal = new ArrayList<AfectacionForestal>();
		listaAfectacionForestalEliminar = new ArrayList<AfectacionForestal>();
		afectacionForestal = new AfectacionForestal();
		listaIdCoberturaVegetal = new ArrayList<String>();
		listaIdConvenio = new ArrayList<String>();
		listaIdEcosistemas = new ArrayList<String>();
		listaIdUnidadHidrograficas = new ArrayList<String>();
		listaEspeciesMuestreo = new ArrayList<EspeciesInformeForestal>();
		listaEspeciesMuestreoEliminar = new ArrayList<EspeciesInformeForestal>();
		listaSitioMuestreoAux = new ArrayList<SitioMuestral>();
		listaSitioMuestralAux = new ArrayList<SitioMuestral>();
				
		esCobertura2022 = false;
		CapasCoa capaCobertura2022 = capasCoaFacade.getCapaByNombre(CapasCoa.NAME_COBERTURA_VEGETAL_2022);
		List<DetalleInterseccionProyectoAmbiental> listaInterseccionesCobertura2022 = detalleInterseccionProyectoFacade.getDetalleProyectoCapa(proyectoLicenciaCoa, capaCobertura2022.getId());
		if(listaInterseccionesCobertura2022 != null && listaInterseccionesCobertura2022.size() > 0) {
			listaInterseccionesCobertura = listaInterseccionesCobertura2022;
			esCobertura2022 = true;
		} else {
			listaInterseccionesCobertura = detalleInterseccionProyectoFacade.getDetalleProyectoCapa(proyectoLicenciaCoa, CapasCoa.ID_COBERTURA_VEGETAL_2018);
		}
		
		listaInterseccionesEcosistema = detalleInterseccionProyectoFacade.getDetalleProyectoCapa(proyectoLicenciaCoa, CapasCoa.ID_ECOSISTEMAS);
		listaInterseccionesConvenios = detalleInterseccionProyectoFacade.getDetalleProyectoCapa(proyectoLicenciaCoa, CapasCoa.ID_SOCIO_BOSQUE);
		CapasCoa capaRestauracion = capasCoaFacade.getCapaByNombre(CapasCoa.NAME_CAPA_RESTAURACION);
		List<DetalleInterseccionProyectoAmbiental> listaInterseccionesRestauracion = detalleInterseccionProyectoFacade.getDetalleProyectoCapa(proyectoLicenciaCoa, capaRestauracion.getId());
		if(listaInterseccionesRestauracion != null && listaInterseccionesRestauracion.size() > 0) {
			listaInterseccionesConvenios.addAll(listaInterseccionesRestauracion);
		}
		
		listaApendiceCITES = catalogoCoaFacade.obtenerCatalogo(CatalogoTipoCoaEnum.VAF_APENDICE_CITES);
		listaEstadoConservacion = catalogoCoaFacade.obtenerCatalogo(CatalogoTipoCoaEnum.VAF_ESTADO_CONSERVACION_UICN);
		index = 0;

		if(informeFactivilidadForestal != null && informeFactivilidadForestal.getId() != null) {
			cargarDatosInforme();
		}
	}
	
	public void finalizar() {
		try {
			UbicacionesGeografica ubicacion = ubicacionGeograficaFacade.buscarPorId(proyectoLicenciaCoa.getIdCantonOficina());
			Area areaTramite = ubicacion.getAreaCoordinacionZonal();
			
			if(areaTramite == null) {
				LOG.error("No se encontro área responsable para la Viabilidad Forestal del proyecto " + proyectoLicenciaCoa.getCodigoUnicoAmbiental());
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
				return;
			}

			Map<String, Object> parametros = new HashMap<>();
			
			String tipoRol = "role.va.pfn.tecnico.forestal";
			String rolTecnico = Constantes.getRoleAreaName(tipoRol);

			List<Usuario> listaTecnicosResponsables = asignarTareaFacade.getCargaLaboralPorUsuariosV2(rolTecnico, areaTramite.getAreaName());

			if(listaTecnicosResponsables==null || listaTecnicosResponsables.isEmpty()){
				LOG.error("No se encontro usuario " + rolTecnico + " en " + areaTramite.getAreaName());
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
				return;
			}
			
			generarInforme(false);
			
			DocumentoViabilidad informeFactibilidad = new DocumentoViabilidad();
	        informeFactibilidad.setId(null);
	        informeFactibilidad.setContenidoDocumento(informeFactivilidadForestal.getArchivoInforme());
	        informeFactibilidad.setNombre(informeFactivilidadForestal.getNombreReporte());
	        informeFactibilidad.setMime("application/pdf");
	        informeFactibilidad.setIdViabilidad(viabilidadProyecto.getId());
	        informeFactibilidad.setIdTipoDocumento(TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_INFORME_FACTIBILIDAD.getIdTipoDocumento());
	        
	        informeFactibilidad = documentosViabilidadFacade.guardarDocumentoProceso(
					proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
					"VIABILIDAD_AMBIENTAL", informeFactibilidad, 1, JsfUtil.getCurrentProcessInstanceId());
	        
	        if(informeFactibilidad == null || informeFactibilidad.getId() == null) {
	        	LOG.error("No se genero el informe de factibilidad el proyecto " + proyectoLicenciaCoa.getCodigoUnicoAmbiental());
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				return;
	        }

			Usuario tecnicoResponsable = listaTecnicosResponsables.get(0);
			parametros.put("tecnicoResponsable", tecnicoResponsable.getNombre()); 
			parametros.put("idViabilidad", viabilidadProyecto.getId());
			parametros.put("usaCobertura2022", esCobertura2022);
			parametros.put(Constantes.USUARIO_VISTA_MIS_PROCESOS, JsfUtil.getLoggedUser().getNombre());
			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			
			if(informeFactivilidadForestal.getCaracterizacionCobertura().equals(InformeFactibilidadForestal.caracterizacionCuantitativa)) {
				generarReporteTaxonomia();
			}
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al enviar la tarea.");
		}
	}
	
	public Integer recuperarNroZonales() throws ServiceException {
		Integer nroZonales = 0;
		
		ArrayList<Integer> listaProv = new ArrayList<Integer>();
		List<UbicacionesGeografica> ubicacionesProyecto = proyectoLicenciaCoaUbicacionFacade.buscarPorProyecto(proyectoLicenciaCoa);
		for (UbicacionesGeografica ubi : ubicacionesProyecto) {
			listaProv.add(ubi.getUbicacionesGeografica().getUbicacionesGeografica() == null ? 0 
					: ubi.getUbicacionesGeografica().getUbicacionesGeografica().getId());
		}

		Set<Integer> setProvincias = new HashSet<Integer>(listaProv);
		
		Integer nroProvincias = setProvincias.size();
		
		if(nroProvincias > 1) {
			ArrayList<Integer> listaZonales = new ArrayList<Integer>();
			for (int id : setProvincias) {
				UbicacionesGeografica ubicacion = ubicacionfacade.buscarPorId(id);
				if (ubicacion.getAreaCoordinacionZonal() != null)
					listaZonales.add(ubicacion.getAreaCoordinacionZonal().getId());
			}

			Set<Integer> setZonales = new HashSet<Integer>(listaZonales);
			
			nroZonales = setZonales.size();
		} else {
			nroZonales = 1;
		}

		return nroZonales;
	}
	
	//UNIDADES HIDROGRAFICAS
	public void limpiarUnidadHidrografica(){
		unidadHidrografica = new UnidadHidrografica();	
		editarSeleccionado = false;
	}
	
	public void aceptarUnidadHidrografica(){
		if(!editarSeleccionado){
			listaInterseccionUHidrograficas.add(unidadHidrografica);
			mostrarlistaUnidadesHidrograficas=true;
			RequestContext.getCurrentInstance().update("form:pnlUnidadeshidrograficas");
		}
		
		editarSeleccionado = false;
		
		unidadHidrografica = new UnidadHidrografica();
	}
	
	public void eliminarUnidadHidrografica(UnidadHidrografica unidad){
		if(unidad.getId() != null){
			listaInterseccionUHidrograficasEliminar.add(unidad);
		}
		listaInterseccionUHidrograficas.remove(unidad);
		mostrarlistaUnidadesHidrograficas=(listaInterseccionUHidrograficas.size()<=0)? false:true;
		RequestContext.getCurrentInstance().update("form:pnlUnidadeshidrograficas");

	}	
	
	public void editarUnidadHidrografica(UnidadHidrografica unidad){
		unidadHidrografica = unidad;
		editarSeleccionado = true;
	}
	
	public void validateUnidadHidrografica(FacesContext context, UIComponent validate, Object value) throws RemoteException {
		List<FacesMessage> errorMessages = new ArrayList<>();
		// validar coordenada
		if (unidadHidrografica.getCoordenadaX() != null && unidadHidrografica.getCoordenadaY() != null) {
			String coordenadaX = unidadHidrografica.getCoordenadaX().toString().replace(",", ".");
			String coordenadaY = unidadHidrografica.getCoordenadaY().toString().replace(",", ".");

			String mensaje = JsfUtil.validarCoordenadaPunto17S(coordenadaX,
					coordenadaY, proyectoLicenciaCoa.getCodigoUnicoAmbiental());

			if (mensaje != null) {
				errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,mensaje, null));
			} else {
				String coordenadaPunto = coordenadaX.toString() + " " + coordenadaY.toString();
				
				mensaje = validarCoordenadaArea(coordenadaPunto);
				
				if(mensaje != null) {
					errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, mensaje, null));
				}
			}
		}
		
		if(editarSeleccionado){
			List<UnidadHidrografica> listaUnidadesAux = new ArrayList<>();
			listaUnidadesAux.addAll(listaInterseccionUHidrograficas);
			listaUnidadesAux.remove(unidadHidrografica);
			
			for (UnidadHidrografica unidad : listaUnidadesAux) {
				if(unidad.getNombre().equals(unidadHidrografica.getNombre())) {
					errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El nombre de la unidad se encuentra repetido", null));
					break;
				}
			}
			
		}else{
			for(UnidadHidrografica unidad : listaInterseccionUHidrograficas){
				if(unidad.getNombre().equals(unidadHidrografica.getNombre())){
					errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El nombre de la unidad se encuentra repetido", null));
					break;
				}			
			}
		}

		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	//FIN undades hidrograficas
	
	//EQUIPO CONSULTOR
	public void validarConsultor(){
		consultorPrincipal = validarCedula(consultorPrincipal);
		if(consultorPrincipal.getConsultor().getCedula() != null && !consultorPrincipal.getConsultor().getCedula().isEmpty()){
			consultorPrincipal.setTipoConsultor(1);
		}		
	}
	
	public void aceptarEquipoConsultor(){		
		if(!editarSeleccionado){
			listaEquipoConsultor.add(equipoConsultor);
		}		
		equipoConsultor = new EquipoConsultorForestal();
		editarSeleccionado = false;
	}
	
	public void validarEquipoConsultor(){
		if(consultorPrincipal.getConsultor().getCedula() == null){
			JsfUtil.addMessageError("Debe ingresar la información del profesional principal");
			equipoConsultor.getConsultor().setCedula(null);
			return;
		}
		
		if(consultorPrincipal.getConsultor().getCedula().equals(equipoConsultor.getConsultor().getCedula())){
			JsfUtil.addMessageError("El número de cédula ingresado es del profesional principal");
			equipoConsultor.getConsultor().setCedula(null);
			return;
		}

		equipoConsultor = validarCedula(equipoConsultor);
		if(equipoConsultor.getConsultor().getCedula() != null && !equipoConsultor.getConsultor().getCedula().isEmpty()){
			equipoConsultor.setTipoConsultor(2);
		}		
	}
	
	public void abrirEquipoConsultor(){
		equipoConsultor = new EquipoConsultorForestal();
		equipoConsultor.setConsultor(new ConsultorForestal());
	}
	
	public void editarEquipoConsultor(EquipoConsultorForestal equipoEditar){
		editarSeleccionado = true;
		setEquipoConsultor(equipoEditar);
	}
	
	public void eliminarEquipoConsultor(EquipoConsultorForestal equipoEliminar){
		if(equipoEliminar.getId() != null){
			listaEquipoConsultorEliminar.add(equipoEliminar);
		}		
		listaEquipoConsultor.remove(equipoEliminar);		
	}
	
	public EquipoConsultorForestal validarCedula (EquipoConsultorForestal equipo){
		if(equipo.getConsultor().getCedula().length() != 10){
			JsfUtil.addMessageError("El numero de documento no corresponde a un numero de cédula.");
			equipo = new EquipoConsultorForestal();
			equipo.setConsultor(new ConsultorForestal());
			return equipo;
		}
		if(equipo.getConsultor().getCedula() == null || equipo.getConsultor().getCedula().isEmpty()){
			equipo = new EquipoConsultorForestal();
			equipo.setConsultor(new ConsultorForestal());
			JsfUtil.addMessageError("El numero de documento es obligatorio.");
			return equipo;
		}
		
		if(listaEquipoConsultor != null && listaEquipoConsultor.size() >= 0) {
			if(editarSeleccionado) {
				List<EquipoConsultorForestal> listaEquipoAux = new ArrayList<>();
				listaEquipoAux.addAll(listaEquipoConsultor);
				listaEquipoAux.remove(equipo);
				
				for (EquipoConsultorForestal equipoC : listaEquipoAux) {
					if(equipoC.getConsultor().getCedula().equals(equipo.getConsultor().getCedula())) {
						JsfUtil.addMessageError("El consultor ya ha sido ingresado.");
						
						equipo = new EquipoConsultorForestal();
						equipo.setConsultor(new ConsultorForestal());
						return equipo;
					}
				}
			} else {
				for (EquipoConsultorForestal equipoC : listaEquipoConsultor) {
					if(equipoC.getConsultor().getCedula().equals(equipo.getConsultor().getCedula())) {
						JsfUtil.addMessageError("El consultor ya ha sido ingresado.");
						
						equipo = new EquipoConsultorForestal();
						equipo.setConsultor(new ConsultorForestal());
						return equipo;
					}
				}
			}
		}
		
		try{
			ConsultorForestal objConsultor = consultorForestalFacade.getConsultorPorCedula(equipo.getConsultor().getCedula());
			
			if(objConsultor != null){
				equipo.setConsultor(objConsultor);
			}else{
				Cedula cedula = consultaRucCedula.obtenerPorCedulaRC(Constantes.USUARIO_WS_MAE_SRI_RC,
						Constantes.PASSWORD_WS_MAE_SRI_RC, equipo.getConsultor().getCedula());
				if (cedula != null && cedula.getError().equals(Constantes.NO_ERROR_WS_MAE_SRI_RC)) {					
					equipo.getConsultor().setNombres(cedula.getNombre());
					
					String numerosTitulos=null;
					String nombresTitulos=null;
			    	GraduadoReporteDTO titulos = consultaRucCedula.obtenerTitulo(equipo.getConsultor().getCedula());
					if(titulos != null && !titulos.getNiveltitulos().isEmpty()) {
			           for (NivelTitulosDTO titulo : titulos.getNiveltitulos()) {
			        	   numerosTitulos=numerosTitulos==null?"":numerosTitulos+";";
			        	   nombresTitulos=nombresTitulos==null?"":nombresTitulos+";";
			        	   numerosTitulos+=titulo.getTitulo().get(0).getNumeroRegistro();
			        	   nombresTitulos+=titulo.getTitulo().get(0).getNombreTitulo();        	   
			           }
			           
			           equipo.getConsultor().setTituloAcademico(nombresTitulos);
			           equipo.getConsultor().setRegistroSenecyt(numerosTitulos);
					}else{
						JsfUtil.addMessageInfo("No existen registros de títulos SENESCYT con el numero de cedula "+ equipo.getConsultor().getCedula());
						equipo = new EquipoConsultorForestal();
						equipo.setConsultor(new ConsultorForestal());
					}
										
				}else{
					JsfUtil.addMessageInfo("No se encontro un consultor con el numero de cedula "+ equipo.getConsultor().getCedula());
					equipo = new EquipoConsultorForestal();
					equipo.setConsultor(new ConsultorForestal());
				}
			}
			
		}catch(Exception e){
			JsfUtil.addMessageInfo("No se encontro un consultor con el numero de cedula "+ equipo.getConsultor().getCedula());
			equipo = new EquipoConsultorForestal();
			equipo.setConsultor(new ConsultorForestal());
		}
		
		return equipo;
	}
	//FIN equipo consultor
	
	public DocumentoViabilidad uploadListener(DocumentoViabilidad documento, FileUploadEvent event){
		String[] split = event.getFile().getContentType().split("/");
	    String extension = "."+split[split.length-1];
	    
	    String tipoDocumento = (split[0].equals("image")) ? "image/jpg" : "application/pdf";
	    
		byte[] contenidoDocumento = event.getFile().getContents();
		documento.setContenidoDocumento(contenidoDocumento);
		documento.setNombre(event.getFile().getFileName());
		documento.setExtension(extension);
		documento.setMime(tipoDocumento);
		documento.setNombreTabla(IngresarInformacionViabilidadPfnController.class.getSimpleName());	
		documento.setTipoUsuario(1);
		return documento;
	}
	
	public StreamedContent descargarFotografia(DocumentoViabilidad fotografia) throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			
			if(fotografia.getContenidoDocumento() != null) {
				documentoContent = fotografia.getContenidoDocumento();
			} else {
				documentoContent = documentosViabilidadFacade.descargar(fotografia.getIdAlfresco());
			}
			
			if (fotografia != null && documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(fotografia.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	//FOTOGRAFIA PROYECTO
	public void uploadListenerFotografia(FileUploadEvent file) {       
        fotoArchivo = new DocumentoViabilidad();
        fotoArchivo = uploadListener(fotoArchivo, file);
    }
	
	public void limpiarFotoArchivo(){
		fotoArchivo = new DocumentoViabilidad();		
	}
	
	public void aceptarFotoArchivo(){
		listaFotoArchivo.add(fotoArchivo);      
		fotoArchivo = new DocumentoViabilidad();
	}
	
	public void eliminarFotoArchivo(DocumentoViabilidad documento){
		if(documento.getId() != null){
			listaFotoArchivoEliminar.add(documento);
		}
		listaFotoArchivo.remove(documento);		
	}
	//FIN fotografia proyecto
	
	//ADJUNTO PROYECTO
	public void uploadListenerAdjunto(FileUploadEvent event) {		
		documentoAdjunto = new DocumentoViabilidad();
		documentoAdjunto = uploadListener(documentoAdjunto, event);
	}
	
	public void limpiarDocumentoAdjunto(){
		documentoAdjunto = new DocumentoViabilidad();		
	}
	
	public void aceptarDocumentoAdjunto(){
		listaDocumentoAdjunto.add(documentoAdjunto);      
		documentoAdjunto = new DocumentoViabilidad();
	}
	
	public void eliminarDocumentoAdjunto(DocumentoViabilidad documento){
		if(documento.getId() != null){
			listaDocumentoAdjuntoEliminar.add(documento);
		}
		listaDocumentoAdjunto.remove(documento);		
	}
	//FIN adjunto proyecto
	
	//FOTOGRAFIA SATELITAL
	public void uploadListenerImagenSatelital(FileUploadEvent file) {
        imagenSatelital = new DocumentoViabilidad(); 
        imagenSatelital = uploadListener(imagenSatelital, file);
    }
	
	public void limpiarImagenSatelital(){
		imagenSatelital = new DocumentoViabilidad();		
	}
	
	public void aceptarImagenSatelital(){		
		listaImagenesSatelite.add(imagenSatelital);      
		imagenSatelital = new DocumentoViabilidad();       
	}
	
	public void eliminarImagenSatelital(DocumentoViabilidad documento){
		if(documento.getId() != null){
			listaImagenesSateliteEliminar.add(documento);
		}
		listaImagenesSatelite.remove(documento);
		
	}
	//FIN fotografia satelital
	
	public void seleccionarCaracterizacion(){
		if(informeFactivilidadForestal.getCaracterizacionCobertura().equals(InformeFactibilidadForestal.caracterizacionCualitativa)){
			informeFactivilidadForestal.setFaseCampo(null);
			informeFactivilidadForestal.setFaseOficina(null);
			informeFactivilidadForestal.setAreaSitioMuestral(null);
			informeFactivilidadForestal.setInterpretacionAreaBasal(null);
			informeFactivilidadForestal.setInterpretacionEspeciesImportancia(null);			
		}else{
			informeFactivilidadForestal.setDescripcionMetodologia(null);			
		}
	}
	
	//CUALITATIVA
	public void abrirCoordenadasSitioMuestral(){
		editarSeleccionado = false;
		sitioMuestral = new SitioMuestral();
		sitioMuestral.setCoordenadaSitioMuestral(new CoordenadaSitioMuestral());
	}
	
	public void uploadListenerFotografiaCuantitativa(FileUploadEvent file) {
        fotografiaCuantitativa = new DocumentoViabilidad();
        fotografiaCuantitativa = uploadListener(fotografiaCuantitativa, file);
    }
	
	public void uploadListenerFotografiaOficina(FileUploadEvent file) {
        fotografiaOficina = new DocumentoViabilidad();
        fotografiaOficina = uploadListener(fotografiaOficina, file);
    }
	
	public void eliminarCaracterizacionCualitativa(SitioMuestral sitio){
		if(sitio.getId() != null){
			listaSitioMuestralEliminar.add(sitio);
		}	
		
		listaSitioMuestral.remove(sitio);
	}
	
	public void editarCaracterizacionCualitativa(SitioMuestral sitio){
		sitioMuestral = new SitioMuestral();
		sitioMuestral.setCoordenadaSitioMuestral(new CoordenadaSitioMuestral());
		
		editarSeleccionado = true;
		setSitioMuestral(sitio);
		
		listaSitioMuestralAux = new ArrayList<SitioMuestral>();
		listaSitioMuestralAux.addAll(listaSitioMuestral);
		listaSitioMuestralAux.remove(sitio);
	}
	
	public void aceptarCaracterizacionCualitativa() throws RemoteException{		
				
		if(!editarSeleccionado){		
			sitioMuestral.setListaCoordenadas(new ArrayList<CoordenadaSitioMuestral>());
			sitioMuestral.getListaCoordenadas().add(sitioMuestral.getCoordenadaSitioMuestral());
			
			listaSitioMuestral.add(sitioMuestral);			
		}
			editarSeleccionado = false;
			sitioMuestral = new SitioMuestral();
			sitioMuestral.setCoordenadaSitioMuestral(new CoordenadaSitioMuestral());
			
		 RequestContext context = RequestContext.getCurrentInstance();
         context.execute("PF('dlgCaracterizacionCualitativa').hide();");
	}
	
	
	public void validateCoordenadaSitioCualitativa(FacesContext context, UIComponent validate, Object value) throws RemoteException {		
		List<FacesMessage> errorMessages = new ArrayList<>();
		// validar coordenada
		if (sitioMuestral.getCoordenadaSitioMuestral().getCoordenadaX() != null && sitioMuestral.getCoordenadaSitioMuestral().getCoordenadaY() != null) {
			String coordenadaX = sitioMuestral.getCoordenadaSitioMuestral().getCoordenadaX().toString().replace(",", ".");
			String coordenadaY = sitioMuestral.getCoordenadaSitioMuestral().getCoordenadaY().toString().replace(",", ".");

			String mensaje = JsfUtil.validarCoordenadaPunto17S(coordenadaX,
					coordenadaY, proyectoLicenciaCoa.getCodigoUnicoAmbiental());

			if (mensaje != null) {
				errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,mensaje, null));
			} else {
				String coordenadaPunto = coordenadaX.toString() + " " + coordenadaY.toString();
				
				mensaje = validarCoordenadaArea(coordenadaPunto);
				
				if(mensaje != null) {
					errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, mensaje, null));
				}
			}
		}

		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}

	public void validateNombreSitio(FacesContext context, UIComponent validate, Object value) throws RemoteException {		
		
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		List<SitioMuestral> listaSitios = new ArrayList<SitioMuestral>();
		if(editarSeleccionado){
			listaSitios.addAll(listaSitioMuestralAux);
		}else{
			listaSitios.addAll(listaSitioMuestral);
		}		
		
		for(SitioMuestral sitioI : listaSitios){
			if(sitioI.getNombreSitio().equals(sitioMuestral.getNombreSitio())){
				errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El nombre del sitio se encuentra repetido", null));
				break;
			}			
		}
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public void validateNombreSitioMuestra(FacesContext context, UIComponent validate, Object value) throws RemoteException {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		List<SitioMuestral> listaSitios = new ArrayList<SitioMuestral>();
		if(editarSeleccionado){
			listaSitios.addAll(listaSitioMuestreoAux);
		}else{
			listaSitios.addAll(listaSitioMuestreo);
		}
		
		for(SitioMuestral sitioI : listaSitios){
			if(sitioI.getNombreSitio().equals(sitioMuestral.getNombreSitio())){
				errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El nombre del sitio se encuentra repetido", null));
				break;
			}
		}
		
		if(sitioMuestral.getUbicacionesGeograficas() == null || sitioMuestral.getUbicacionesGeograficas().size() == 0){
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe cargar el archivo de coordenadas", null));
		}
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	@Getter
    private UploadedFile uploadedFile;
	
	public void handleFileUpload(final FileUploadEvent event) {
		int rows = 0;
		
		sitioMuestral.setUbicacionesGeograficas(new ArrayList<UbicacionSitioMuestral>());
        sitioMuestral.setListaCoordenadas(new ArrayList<CoordenadaSitioMuestral>());
        varUbicacionAux = new HashMap<String,Double>();
        
		try {
			
			uploadedFile = event.getFile();
			Workbook workbook = new HSSFWorkbook(uploadedFile.getInputstream());
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            // cuento las filas
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                boolean isEmptyRow = true;
                for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
                    Cell cell = row.getCell(cellNum);
                    if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK
                            && StringUtils.isNotBlank(cell.toString())) {
                        isEmptyRow = false;
                    }
                }
                if (isEmptyRow)
                    break;
                
                if(rows > 0){
                	Iterator<Cell> cellIterator = row.cellIterator();
                	CoordenadaSitioMuestral coordenada = new CoordenadaSitioMuestral();
                	coordenada.setOrden((int) cellIterator.next().getNumericCellValue());
                	coordenada.setCoordenadaX(new Double(Double.valueOf(cellIterator.next().toString().replace("," , "."))));
                	coordenada.setCoordenadaY(new Double(Double.valueOf(cellIterator.next().toString().replace("," , "."))));
                	
                	sitioMuestral.getListaCoordenadas().add(coordenada);
                }
                
                rows++;
            }
            
            int num = sitioMuestral.getListaCoordenadas().size();
            TipoForma forma = new TipoForma();
            if(num > 1){            	
            	forma.setId(TipoForma.TIPO_FORMA_POLIGONO);
            	sitioMuestral.setTipoForma(forma);
            }else{
            	forma.setId(TipoForma.TIPO_FORMA_PUNTO);
            	sitioMuestral.setTipoForma(forma);
            }        
            
            validateCoordenadasMuestreo();
            
            
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	public void validateCoordenadasMuestreo() throws RemoteException {		
		
		//validar el archivo de coordenadas
		List<String> errorMessages = new ArrayList<>();
		List<String> listaCoordenadasValidar = new ArrayList<>();
		String coordenadasPoligono = "";
		for(CoordenadaSitioMuestral coordenada : sitioMuestral.getListaCoordenadas()){
			String coordenadaX = coordenada.getCoordenadaX().toString().replace(",", ".");
			String coordenadaY = coordenada.getCoordenadaY().toString().replace(",", ".");

			String mensaje = JsfUtil.validarCoordenadaPunto17S(coordenadaX,
					coordenadaY, proyectoLicenciaCoa.getCodigoUnicoAmbiental());
			
			if (mensaje != null) {
				errorMessages.add(mensaje);
				break;
			} else {
				String coordenadaPunto = coordenadaX.toString() + " " + coordenadaY.toString();
				listaCoordenadasValidar.add(coordenadaPunto);	
				
				coordenadasPoligono += (coordenadasPoligono == "") ? coordenadaX+" "+coordenadaY : ","+coordenadaX+" "+coordenadaY;
			}
		}
		
		if (errorMessages.isEmpty()){
			String mensaje = "";			
			if(sitioMuestral.getTipoForma().getId().equals(TipoForma.TIPO_FORMA_POLIGONO)){				
				mensaje = validarCoordenadaAreaMuestraPoligono(coordenadasPoligono,"po");
			}else{			
				mensaje = validarCoordenadaAreaMuestraPunto(listaCoordenadasValidar, "pu", coordenadasPoligono);
			}		
			
			if(mensaje != null) {
				errorMessages.add(mensaje);
			}
		}
		if (!errorMessages.isEmpty())
			JsfUtil.addMessageError(errorMessages);
	}
	
	public String validarCoordenadaAreaMuestraPunto(List<String> coordenadaPunto, String forma, String coordenada) throws RemoteException {
		
		String mensaje = null;

		List<String> listaCoordenadasValidar = new ArrayList<String>();
		listaCoordenadasValidar.addAll(coordenadaPunto);

		for (int i = 0; i < listaCoordenadasValidar.size(); i++) {
			for (int j = 0; j < coordenadasGeograficasProyecto.size(); j++) {
				ContienePoligono_entrada verificarGeoImpla = new ContienePoligono_entrada();
				verificarGeoImpla.setU(Constantes.getUserWebServicesSnap());
				verificarGeoImpla.setTipo(forma);
				verificarGeoImpla.setXy1(coordenadasGeograficasProyecto.get(j));
				verificarGeoImpla.setXy2(listaCoordenadasValidar.get(i));

				SVA_Reproyeccion_IntersecadoPortTypeProxy wsGeoImpl = new SVA_Reproyeccion_IntersecadoPortTypeProxy();
				ContienePoligono_resultado[] intRestGeoImpl = wsGeoImpl.contienePoligono(verificarGeoImpla);
				if (intRestGeoImpl[0].getInformacion().getError() != null) {
					mensaje = intRestGeoImpl[0].getInformacion().getError().toString();
				} else {
					if (intRestGeoImpl[0].getContienePoligono().getValor().equals("f")) {
						mensaje = "Las coordenadas están fuera del área geográfica";
						sitioMuestral.setUbicacionesGeograficas(new ArrayList<UbicacionSitioMuestral>());
					}else{
						ubicacionPunto(coordenada);
					}
				}
			}
		}
		
		return mensaje;
	}
	
	public void ubicacionPunto(String coordenadaPunto){
		try {
			
			SVA_Reproyeccion_IntersecadoPortTypeProxy ws = new SVA_Reproyeccion_IntersecadoPortTypeProxy();
			
			Intersecado_resultado[] resultadoInterseccion;
			Intersecado_entrada inter = new Intersecado_entrada(
					Constantes.getUserWebServicesSnap(), proyectoLicenciaCoa.getCodigoUnicoAmbiental() , "pu", coordenadaPunto, "dp");

			resultadoInterseccion = ws.interseccion(inter);

			if (resultadoInterseccion[0].getInformacion().getError() != null) {
				JsfUtil.addMessageError(resultadoInterseccion[0].getInformacion().getError().toString());
				
			} else {
				for (Intersecado_capa intersecado_capa : resultadoInterseccion[0].getCapa()) {
					String capaNombre = intersecado_capa.getNombre();
					if (intersecado_capa.getError() != null) {
						JsfUtil.addMessageError(intersecado_capa.getError().toString());
						
					}

					Intersecado_coordenada[] intersecadoCoordenada = intersecado_capa.getCruce();
					if (intersecadoCoordenada != null) {
						sitioMuestral.setUbicacionesGeograficas(new ArrayList<UbicacionSitioMuestral>());
						for (Intersecado_coordenada intersecado_coordenad : intersecadoCoordenada) {
							if (capaNombre.equals("dpa")) {
								String parroquia = intersecado_coordenad.getObjeto();
								List<UbicacionesGeografica> lista = ubicacionGeograficaFacade.buscarUbicacionGeograficaPorParroquia(parroquia);
								UbicacionSitioMuestral ubicacionSitio = new UbicacionSitioMuestral();
								ubicacionSitio.setUbicacionesGeografica(lista.get(0));
								sitioMuestral.getUbicacionesGeograficas().add(ubicacionSitio);
							}
						}
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	private HashMap<String, Double> varUbicacionArea= new HashMap<String,Double>();
	private HashMap<String, Double> varUbicacionAux = new HashMap<String,Double>();
	public String validarCoordenadaAreaMuestraPoligono(String coordenadaPunto, String tipo) throws RemoteException {
		
		String mensaje = null;

		SVA_Reproyeccion_IntersecadoPortTypeProxy ws = new SVA_Reproyeccion_IntersecadoPortTypeProxy();
		ws.setEndpoint(Constantes.getInterseccionesWS());

		Intersecado_entrada poligono = new Intersecado_entrada();
		poligono.setU(Constantes.getUserWebServicesSnap());
		poligono.setIsla(Constantes.SIGLAS_INSTITUCION + "-RA");
		poligono.setTog(tipo);
		poligono.setXy(coordenadaPunto);
		poligono.setShp("dp");
		Intersecado_resultado[] intRest;

		try {
			intRest = ws.interseccion(poligono);
			
			if (intRest[0].getInformacion().getError() != null) {
				JsfUtil.addMessageError(intRest[0].getInformacion().getError().toString());
			} else {
				Double areaPoligono = 0.0;
				
				Boolean intersecaProyecto = false;

				String coodenadasgeograficas = "";
				for (String coordenada : coordenadasGeograficasProyecto) {
					coodenadasgeograficas = coordenada;
					
					ContieneZona_entrada verificarGeoImpla = new ContieneZona_entrada(); 
					verificarGeoImpla.setU(Constantes.getUserWebServicesSnap());
					verificarGeoImpla.setXy1(coodenadasgeograficas);
					verificarGeoImpla.setXy2(coordenadaPunto);
					ContieneZona_resultado[] intRestGeoImpl;
					intRestGeoImpl = ws.contieneZona(verificarGeoImpla);
					if (intRestGeoImpl[0].getInformacion().getError() != null) {
						JsfUtil.addMessageError(intRestGeoImpl[0].getInformacion().getError().toString());
						break;
					} else{
						if (intRestGeoImpl[0].getContieneCapa().getValor().equals("f")) {
							intersecaProyecto = false;
						} else {
							areaPoligono = Double.valueOf(intRestGeoImpl[0].getValorArea().getArea());
							if(areaPoligono == null) {
			            		mensaje = "Error a recuperar la ubicación del proyecto";
			            	}
							            	
			            	Double areaPoligonoAux = areaPoligono/10000;            	
			            	
			            	recuperarParroquiasInterseccion(areaPoligonoAux, intRest);
			            	cargarUbicacionProyecto(varUbicacionAux);
			            	
			            	intersecaProyecto = true;
			            	break;
						}
					}
				}
				
				if(!intersecaProyecto) {
					mensaje = "Las coordenadas ingresadas no se encuentran dentro del área del proyecto";
				}
			}
		} catch (Exception e) {
			JsfUtil.addMessageError("Error inesperado, comuní­quese con mesa de ayuda");
			sitioMuestral.setListaCoordenadas(new ArrayList<CoordenadaSitioMuestral>());
		}
		return mensaje;
	}
	
	private void recuperarParroquiasInterseccion(Double areaPoligono, Intersecado_resultado[]intRest) throws Exception {
		String parroquia="";
        Double valorParroquia=0.0;
        Integer orden=2; 
        
        
		for (Intersecado_capa intersecado_capa : intRest[0].getCapa()) { 
			String capaNombre=intersecado_capa.getNombre();   
			if(intersecado_capa.getError()!=null){
				JsfUtil.addMessageError(intersecado_capa.getError().toString());
			}
			Intersecado_coordenada[] intersecadoCoordenada=intersecado_capa.getCruce();
			if (intersecadoCoordenada != null){

				for (Intersecado_coordenada intersecado_coordenad : intersecadoCoordenada) {
					if (intersecado_coordenad.getValor() != null) {
						valorParroquia = Double.valueOf(intersecado_coordenad.getValor());
					}
					if (valorParroquia >= 0) {
						if (capaNombre.equals("dpa")) {                							
							parroquia=intersecado_coordenad.getObjeto();
							Double areaValorParroquia = areaPoligono * valorParroquia; //calcula el área de interseccion de la parroquia
							
							if (varUbicacionArea.get(parroquia) != null) {
								varUbicacionArea.put(parroquia, areaValorParroquia + varUbicacionArea.get(parroquia)); // agrega el area al total de la parroquia, cuando son varios poligonos
							} else {
								varUbicacionArea.put(parroquia,areaValorParroquia);
							}
							
							if (varUbicacionAux.get(parroquia) != null) {
								if (valorParroquia >= varUbicacionAux.get(parroquia)) {
									varUbicacionAux.put(parroquia,orden.doubleValue());
								}
							} else {
								varUbicacionAux.put(parroquia,orden.doubleValue());
							}
						}
					}
					orden ++;
				}
			}
		}
	}
	
	public void cargarUbicacionProyecto(HashMap<String, Double> parroquia){
		
		sitioMuestral.setUbicacionesGeograficas(new ArrayList<UbicacionSitioMuestral>());

		Iterator<Entry<String, Double>> it = parroquia.entrySet().iterator();
		String inec="";
		
		while (it.hasNext()) {
			Map.Entry<String, Double> e = (Entry<String, Double>) it.next();
			inec  = e.getKey();
			if (!inec.equals("")) {
				List<UbicacionesGeografica> lista = ubicacionGeograficaFacade.buscarUbicacionGeograficaPorParroquia(inec);		
				for(UbicacionesGeografica ubicacion : lista){
					UbicacionSitioMuestral ubicacionSitio = new UbicacionSitioMuestral();
					ubicacionSitio.setUbicacionesGeografica(ubicacion);
					sitioMuestral.getUbicacionesGeograficas().add(ubicacionSitio);
				}				
			}
		}
	}
	
	public void desbloquearTaxonomia(EspeciesInformeForestal especie) {
		blockFamilia = true;
		blockGenero = true;
		blockEspecie = true;
		if (especie.getNivelTaxonomia().equals(2)) {
			blockFamilia = false;
			blockGenero = false;
			blockEspecie = false;
		}
		if (especie.getNivelTaxonomia().equals(1)) {
			blockFamilia = false;
			blockGenero = false;
		}
	}
	
	public void desbloquearTaxonomiaAB(EspeciesInformeForestal especie) {
		blockFamilia = true;
		blockGenero = true;
		blockEspecie = true;
		if (especie.getNivelTaxonomia().equals(2)) {
			blockFamilia = false;
			blockGenero = false;
			blockEspecie = false;
		}
		if (especie.getNivelTaxonomia().equals(1)) {
			blockFamilia = false;
			blockGenero = false;
		}
	}
	
	public void desbloquearTaxonomiaEI(EspeciesImportanciaForestal especie) {
    	blockFamilia=true;
		blockGenero=true;
		blockEspecie=true;
		if (especie.getNivelTaxonomia().equals(2)) {
			blockFamilia = false;
			blockGenero = false;
			blockEspecie = false;
		}
		if (especie.getNivelTaxonomia().equals(1)) {
			blockFamilia = false;
			blockGenero = false;
		}
    }
	
	public List<HigherClassification> obtenerListaFamilia() {
    	List<HigherClassification> familia = new ArrayList<HigherClassification>();
    	try {
			familia = higherClassificationFacade.getFamilia();
		} catch (ServiceException e) {
			JsfUtil.addMessageError("Error en consulta de familia");
            LOG.error(e);
		}
    	return familia;
    }
	
	public void obtenerListaGeneroPorFamilia(HigherClassification objFamilia) {
		listGenero = new ArrayList<HigherClassification>();
    	listGenero = higherClassificationFacade.getByFamilia(objFamilia.getId());
    	objGenero = new HigherClassification();
    	objEspecie = new SpecieTaxa();
    }
	
	public void obtenerListaEspecies(HigherClassification objGenero) {
		if(informeFactivilidadForestal.getCaracterizacionCobertura().equals(InformeFactibilidadForestal.caracterizacionCualitativa))
			validarEspecieCualitativa();

		listEspecie = new ArrayList<SpecieTaxa>();
    	listEspecie = specieTaxaFacade.getByGenero(objGenero.getId());
    	objEspecie = new SpecieTaxa();
    }
		
	public void aceptarFotografiaCuantitativa(){
		listaFotografiaCuantitativa.add(fotografiaCuantitativa);      
		fotografiaCuantitativa = new DocumentoViabilidad();
	}
	
	public void eliminarFotografiaCuantitativa(DocumentoViabilidad documento){
		if(documento.getId() != null){
			listaFotografiaCuantitativaEliminar.add(documento);
		}
		listaFotografiaCuantitativa.remove(documento);		
	}
	
	public void limpiarFotografiaCuantitativa(){
		fotografiaCuantitativa = new DocumentoViabilidad();		
	}
	
	public void aceptarFotografiaOficina(){
		listaFotografiaOficina.add(fotografiaOficina);      
		fotografiaOficina = new DocumentoViabilidad();
	}
	
	public void eliminarFotografiaOficina(DocumentoViabilidad documento){
		if(documento.getId() != null){
			listaFotografiaOficinaEliminar.add(documento);
		}
		listaFotografiaOficina.remove(documento);		
	}
	
	public void limpiarFotografiaOficina(){
		fotografiaOficina = new DocumentoViabilidad();		
	}	
	
	public void aceptarDocumentoAdjuntoAnexo(){
		listaDocumentosAnexos.add(documentoAdjunto);      
		documentoAdjunto = new DocumentoViabilidad();
	}
	
	public void borrarCaracterizacionCualitativa(){
		editarSeleccionado = false;
		sitioMuestral = new SitioMuestral();
	}
	
	public void editarCaracterizacionCualitativaEspecie(EspeciesInformeForestal especie){
		editarSeleccionado = true;
		setEspeciesInformeForestal(especie);
		
		desbloquearTaxonomia(especie);
		
		obtenerListaGeneroPorFamilia(especie.getFamiliaEspecie());
		obtenerListaEspecies(especie.getGeneroEspecie());
	}
	
	public void aceptarCaracterizacionCualitativaEspecies(){
		if(!editarSeleccionado){
			listaCaracterizacionCualitativaEspecies.add(especiesInformeForestal);
		}
		
		especiesInformeForestal = new EspeciesInformeForestal();	
		editarSeleccionado = false;
	}
	
	public void borrarCaracterizacionCualitativaEspecies(){
		especiesInformeForestal = new EspeciesInformeForestal();
		editarSeleccionado = false;
		nivelTaxonomia = null;
		
		blockFamilia = true;
		blockGenero = true;
		blockEspecie = true;
	}
	
	public void eliminarCaracterizacionCualitativaEspecie(EspeciesInformeForestal especie){
		if(especie.getId() != null){
			listaCaracterizacionCualitativaEspeciesEliminar.add(especie);
		}
		
		listaCaracterizacionCualitativaEspecies.remove(especie);
	}
	
	public void validarEspecieCualitativa() {		
		
		Boolean esRepetido = false;
		
		if(editarSeleccionado){
			List<EspeciesInformeForestal> listaUnidadesAux = new ArrayList<>();
			listaUnidadesAux.addAll(listaCaracterizacionCualitativaEspecies);
			listaUnidadesAux.remove(especiesInformeForestal);
			
			for (EspeciesInformeForestal item : listaUnidadesAux) {
				if(especiesInformeForestal.getNivelTaxonomia() == 1 && item.getNivelTaxonomia() == 1){ //genero
					if (item.getSitioMuestral().getNombreSitio().equals(especiesInformeForestal.getSitioMuestral().getNombreSitio())
							&& item.getGeneroEspecie().equals(especiesInformeForestal.getGeneroEspecie())) {
						esRepetido = true;
						break;
					}								
				}else if(especiesInformeForestal.getNivelTaxonomia() == 2 && item.getNivelTaxonomia() == 2){
					if(item.getSitioMuestral().getNombreSitio().equals(especiesInformeForestal.getSitioMuestral().getNombreSitio())
							&& item.getEspecie().equals(especiesInformeForestal.getEspecie())){
						esRepetido = true;
						break;
					}			
				}
			}
			
		}else{
			for(EspeciesInformeForestal item : listaCaracterizacionCualitativaEspecies){
				if(especiesInformeForestal.getNivelTaxonomia() == 1 && item.getNivelTaxonomia() == 1){
					if(item.getSitioMuestral().getNombreSitio().equals(especiesInformeForestal.getSitioMuestral().getNombreSitio())
							&& item.getGeneroEspecie().equals(especiesInformeForestal.getGeneroEspecie())){
						esRepetido = true;
						break;
					}					
				}else if(especiesInformeForestal.getNivelTaxonomia() == 2 && item.getNivelTaxonomia() == 2){
					if(item.getSitioMuestral().getNombreSitio().equals(especiesInformeForestal.getSitioMuestral().getNombreSitio())
							&& item.getEspecie().equals(especiesInformeForestal.getEspecie())){
						esRepetido = true;
						break;
					}
				}					
			}
		}
		
		if(esRepetido) {
			if(especiesInformeForestal.getNivelTaxonomia() == 1){
				especiesInformeForestal.setGeneroEspecie(null);
				JsfUtil.addMessageError("La especie ya ha sido registrada");
			}else{
				especiesInformeForestal.setEspecie(null);
				JsfUtil.addMessageError("La especie ya ha sido registrada");
			}
			
			
		}
	}
	
	public void limpiarCalculoBasal(){
		blockFamilia = true;
		blockGenero = true;
		blockEspecie = true;
		
		nivelTaxonomiaAB = null;
		especiesInformeForestalCenso = new EspeciesInformeForestal();
		
		RequestContext context = RequestContext.getCurrentInstance();
		context.update("formDialog:dlgAreaBasal");
	}
	
	public void editarCalculoBasal(EspeciesInformeForestal especie) {
		editarSeleccionado = true;
		setEspeciesInformeForestalCenso(especie);
		
		obtenerListaGeneroPorFamilia(especiesInformeForestalCenso.getFamiliaEspecie());
		obtenerListaEspecies(especiesInformeForestalCenso.getGeneroEspecie());
		
		desbloquearTaxonomiaAB(especie);
		sumatoriaCenso();
	}
	
	public void eliminarCalculoBasal(EspeciesInformeForestal calculo){
		if(calculo.getId() != null){
			listaEspeciesCensoEliminar.add(calculo);
		}
		
		listaEspeciesCenso.remove(calculo);

		listFamiliaSelect = new ArrayList<HigherClassification>();
		
		for(EspeciesInformeForestal especieIng : listaEspeciesCenso){
			if(!listFamiliaSelect.contains(especieIng.getFamiliaEspecie())){
				listFamiliaSelect.add(especieIng.getFamiliaEspecie());
			}			
		}
		
		List<EspeciesImportanciaForestal> listaEspeciesImportanciaAux = new ArrayList<EspeciesImportanciaForestal>();
		listaEspeciesImportanciaAux.addAll(listaEspeciesImportancia);
		for(EspeciesImportanciaForestal especieImp : listaEspeciesImportanciaAux){			
			if(!listFamiliaSelect.contains(especieImp.getFamiliaEspecie())){
				if(especieImp.getId() != null){
					listaEspeciesImportanciaEliminar.add(especieImp);					
				}	
				listaEspeciesImportancia.remove(especieImp);
			}
		}
		
		listaIndiceValorImportanciaEliminar.addAll(listaIndiceValorImportancia);
		listaIndiceValorImportancia = new ArrayList<>();
		
		if(listaEspeciesCenso.isEmpty()){
			sitioMuestralCenso.setSumatoriaAreaBasal(null);
			sitioMuestralCenso.setSumatoriaVolumenTotal(null);
			sitioMuestralCenso.setSumatoriaVolumenComercial(null);
			
			sitioMuestralCenso.setTotalDmr(null);
			sitioMuestralCenso.setTotalDnr(null);
			sitioMuestralCenso.setTotalIvi(null);
			sitioMuestralCenso.setIndiceShanon(null);
			sitioMuestralCenso.setIndiceSimpson(null);
			sitioMuestralCenso.setInversoSimpson(null);
			sitioMuestralCenso.setExponencialShannon(null);
		}else{
			calcularIndiceValorImportancia();	
		}
		sumatoriaCenso();
	}	
	
	public void calculoAreaBasal(){
		double area = (Math.PI/4)*Math.pow(especiesInformeForestalCenso.getAlturaDap(), 2);
		especiesInformeForestalCenso.setAreaBasal(area);
		
		calcularVolumenTotal();
		calcularVolumenComercial();
	}
	
	public void calcularVolumenTotal(){
		if (especiesInformeForestalCenso.getAreaBasal() != null
				&& especiesInformeForestalCenso.getAlturaTotal() != null) {
			double volumen = especiesInformeForestalCenso.getAreaBasal() * especiesInformeForestalCenso.getAlturaTotal() * FACTOR_FORMA;
			especiesInformeForestalCenso.setVolumenTotal(volumen);
		}
	}
	
	public void calcularVolumenComercial(){
		if (especiesInformeForestalCenso.getAreaBasal() != null
				&& especiesInformeForestalCenso.getAlturaComercial() != null) {
			double volumen = especiesInformeForestalCenso.getAreaBasal() * especiesInformeForestalCenso.getAlturaComercial() * FACTOR_FORMA;
			especiesInformeForestalCenso.setVolumenComercial(volumen);
		}
	}	
	
	public void aceptarAreaBasal(){
		if(!editarSeleccionado){
			listaEspeciesCenso.add(especiesInformeForestalCenso);			
		}
		
		listFamiliaSelect = new ArrayList<HigherClassification>();
		
		for(EspeciesInformeForestal especieIng : listaEspeciesCenso){
			if(!listFamiliaSelect.contains(especieIng.getFamiliaEspecie())){
				listFamiliaSelect.add(especieIng.getFamiliaEspecie());
			}			
		}

		editarSeleccionado = false;
		especiesInformeForestalCenso = new EspeciesInformeForestal();
		
		listaIndiceValorImportanciaEliminar.addAll(listaIndiceValorImportancia);
		listaIndiceValorImportancia = new ArrayList<>();
		

		sumatoriaCenso();
		
	}
	
	public void borrarEspecieImportancia(){
		especiesInformeForestalImportancia = new EspeciesImportanciaForestal();
		editarSeleccionado = false;
		
		blockFamilia = true;
		blockGenero = true;
		blockEspecie = true;
		nivelTaxonomiaEspecieImportancia = null;
		
		RequestContext context = RequestContext.getCurrentInstance();
		context.update("formDialog:dlgEspeciesImportanciaCenso");
	}
	
	public void editarEspecieImportancia(EspeciesImportanciaForestal especie){
		editarSeleccionado = true;
		setEspeciesInformeForestalImportancia(especie);
		
		obtenerListaGeneroPorFamilia(especie.getFamiliaEspecie());
		obtenerListaEspecies(especie.getGeneroEspecie());
		
		desbloquearTaxonomiaEI(especie);
	}
	
	public void eliminarEspecieImportancia(EspeciesImportanciaForestal especie){
		if(especie.getId() != null){
			listaEspeciesImportanciaEliminar.add(especie);
		}
		listaEspeciesImportancia.remove(especie);
	}
	
	public void agregarEspecieImportancia(){
		if(!editarSeleccionado){
			boolean repetido = false;
			for(EspeciesImportanciaForestal especie : listaEspeciesImportancia){
				if(especie.getEspecie() != null){
					if(especie.getEspecie().equals(especiesInformeForestalImportancia.getEspecie())){
						repetido = true;
						break;
					}
				}else{
					if(especie.getGeneroEspecie().equals(especiesInformeForestalImportancia.getGeneroEspecie())){
						repetido = true;
						break;
					}
				}
			}
			
			if(!repetido){
				listaEspeciesImportancia.add(especiesInformeForestalImportancia);
			}else{
				JsfUtil.addMessageError("Especie ya ingresada");
			}
		}
		
		especiesInformeForestalImportancia = new EspeciesImportanciaForestal();
		editarSeleccionado = false;
	}
	
	public void calcularIndiceValorImportancia(){
		try {
			
			if(listaEspeciesCenso.isEmpty()){
				JsfUtil.addMessageError("Debe ingresar especies en la Tabla de cáculo del área basal y volumen");
				return;
			}
			
			Double totalAreaBasal = 0.0;
			Double totalVolumenT = 0.0;
			Double totalVolumenC = 0.0;
			Integer totalIndividuos = listaEspeciesCenso.size();
			
			listaIndiceValorImportanciaEliminar.addAll(listaIndiceValorImportancia);
			listaIndiceValorImportancia = new ArrayList<AnalisisResultadoForestal>();
			for(EspeciesInformeForestal especie : listaEspeciesCenso){
				totalAreaBasal += especie.getAreaBasal();
				totalVolumenT += especie.getVolumenTotal();
				totalVolumenC += especie.getVolumenComercial();
			}
			
			for(EspeciesInformeForestal especie : listaEspeciesCenso){
				if(especie.getEspecie() != null){
					int numeroIndividuos = 0;
					double areaBasalEspecie = 0.0;
					boolean existe = false;
					for(AnalisisResultadoForestal specie : listaIndiceValorImportancia){
						if(specie.getEspecie() != null && especie.getEspecie().getSptaScientificName().equals(specie.getEspecie().getSptaScientificName())){
							existe = true;
							areaBasalEspecie = specie.getAreaBasal() + especie.getAreaBasal();					
							numeroIndividuos = specie.getFrecuencia() + 1;
							specie.setAreaBasal(areaBasalEspecie);
							specie.setFrecuencia(numeroIndividuos);
						}
					}
					
					if(!existe){
						AnalisisResultadoForestal specie = new AnalisisResultadoForestal();
						specie.setEspecie(especie.getEspecie());
						specie.setGeneroEspecie(especie.getGeneroEspecie());
						specie.setAreaBasal(especie.getAreaBasal());
						specie.setFrecuencia(1);
						specie.setNombreComun(especie.getNombreComun());
						listaIndiceValorImportancia.add(specie);
					}					
					
				}else{
					int numeroIndividuos = 0;
					double areaBasalEspecie = 0.0;
					boolean existe = false;
					for(AnalisisResultadoForestal specie : listaIndiceValorImportancia){
						if(specie.getEspecie() == null && especie.getGeneroEspecie().getHiclScientificName().equals(specie.getGeneroEspecie().getHiclScientificName())){
							existe = true;
							areaBasalEspecie = specie.getAreaBasal() + especie.getAreaBasal();					
							numeroIndividuos = specie.getFrecuencia() + 1;
							specie.setAreaBasal(areaBasalEspecie);
							specie.setFrecuencia(numeroIndividuos);
						}
					}
					
					if(!existe){
						AnalisisResultadoForestal specie = new AnalisisResultadoForestal();
						specie.setEspecie(especie.getEspecie());
						specie.setGeneroEspecie(especie.getGeneroEspecie());
						specie.setAreaBasal(especie.getAreaBasal());
						specie.setFrecuencia(1);
						specie.setNombreComun(especie.getNombreComun());
						listaIndiceValorImportancia.add(specie);
					}			
				}				
			}
			
			BigDecimal totalDnR = new BigDecimal(0.0000);
			double totalDmR = 0.0;
			double totalIVI = 0.0;
			double shannon = 0.0;
			double simpson = 0.0;
			
			for(AnalisisResultadoForestal calculo : listaIndiceValorImportancia){
				
				double frecuencia = calculo.getFrecuencia();
				double totalInd = totalIndividuos;
				
				double dnr = (frecuencia / totalInd) * 100;
				double dmr = (calculo.getAreaBasal()/totalAreaBasal) * 100;
				double ivi = (dnr + dmr)/2;
				
				calculo.setValorDmr(dmr);
				calculo.setValorDnr(dnr);
				calculo.setValorIvi(ivi);
				
				totalDnR = totalDnR.add(new BigDecimal(dnr));
				totalDmR += dmr;
				totalIVI += ivi;
				
				double proporcionInd = calculo.getFrecuencia() / totalInd;
				
				shannon += proporcionInd * (Math.log(proporcionInd));		
				
				simpson += Math.pow(proporcionInd, 2);
				
				calculo.setValorPi(proporcionInd);
				calculo.setValorLnPi(Math.log(proporcionInd));
				calculo.setValorPiCuadrado(Math.pow(proporcionInd, 2));					
			}
			
			sitioMuestralCenso.setSumatoriaAreaBasal(totalAreaBasal);
			sitioMuestralCenso.setSumatoriaVolumenTotal(totalVolumenT);
			sitioMuestralCenso.setSumatoriaVolumenComercial(totalVolumenC);
			
			sitioMuestralCenso.setTotalDmr(totalDmR);
			sitioMuestralCenso.setTotalDnr(totalDnR);
			sitioMuestralCenso.setTotalIvi(totalIVI);
			sitioMuestralCenso.setIndiceShanon(-shannon);
			
			double exponencial = Math.exp(sitioMuestralCenso.getIndiceShanon());
			
			sitioMuestralCenso.setExponencialShannon(exponencial);
			
			double indiceSimp = 1 - simpson;
			
			sitioMuestralCenso.setIndiceSimpson(indiceSimp);
			
			double inversoSimp = 1/sitioMuestralCenso.getIndiceSimpson();
			
			double inf = Double.POSITIVE_INFINITY;
			
			if(inversoSimp == inf){
				sitioMuestralCenso.setInversoSimpson(null);
			}else{
				sitioMuestralCenso.setInversoSimpson(inversoSimp);
			}
			
			
		} catch (Exception e) {
			LOG.error("Error en el cálculo del indice de valor de importancia");
		}
		
	}	
	
	public void abriUbicacionMuestral(){
		sitioMuestral = new SitioMuestral();
		sitioMuestral.setListaCoordenadas(new ArrayList<CoordenadaSitioMuestral>());
		sitioMuestral.setListaEspecieValorImportancia(new ArrayList<AnalisisResultadoForestal>());
		
		varUbicacionAux = new HashMap<String,Double>();
		
		editarSeleccionado = false;
	}
	
	public void aceptarSitiosMuestrales(){
		
		//poner en un solo string las coordenas como las ubicaciones
		if(!editarSeleccionado){			
			listaSitioMuestreo.add(sitioMuestral);			
		}
		
		double area = 0.0;
		for(SitioMuestral sitio : listaSitioMuestreo){
			String parroquia = "";
			String canton = "";
			String provincia = "";
			
			for(UbicacionSitioMuestral ubicacion : sitio.getUbicacionesGeograficas()){
				parroquia += parroquia.equals("") ? ubicacion.getUbicacionesGeografica().getNombre() : ", "
						+ ubicacion.getUbicacionesGeografica().getNombre();
				canton += canton.equals("") ? ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre() : ", "
						+ ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre();
				provincia += provincia.equals("") ? ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getUbicacionesGeografica().getNombre() : ", "
						+ ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getUbicacionesGeografica().getNombre();
			}
			
			sitio.setParroquia(parroquia);
			sitio.setCanton(canton);
			sitio.setProvincia(provincia);
			
			String coordenadasX = "";
			String coordenadasY = "";
			for(CoordenadaSitioMuestral coordenada : sitio.getListaCoordenadas()){				
				coordenadasX += coordenadasX.equals("") ? coordenada.getCoordenadaX() : ", " + coordenada.getCoordenadaX();
				coordenadasY += coordenadasY.equals("") ? coordenada.getCoordenadaY() : ", " + coordenada.getCoordenadaY();				
			}
			sitio.setCoordenadasX(coordenadasX);
			sitio.setCoordenadasY(coordenadasY);
			
			double areaSitio = sitio.getAncho() * sitio.getLargo();
			sitio.setAreaSitio(areaSitio);
			
			if(sitio.getAreaSitio() != null){
				area += sitio.getAreaSitio();
			}			
		}
		informeFactivilidadForestal.setAreaSitioMuestral(area);
		sitioMuestral = new SitioMuestral();
	}
	
	public void abrirEspeciesCalculoMuestreo(){
		especiesInformeForestalMuestreo = new EspeciesInformeForestal();
		editarSeleccionado = false;
		nivelTaxonomiaAB = null;

		blockFamilia = true;
		blockGenero = true;
		blockEspecie = true;
	}
	
	public void aceptarEspeciesCalculoMuestreo(){
		listaSitioSumatoria = new ArrayList<SitioMuestral>();
		if(!editarSeleccionado){
			listaEspeciesMuestreo.add(especiesInformeForestalMuestreo);
		}

		listFamiliaSelect = new ArrayList<HigherClassification>();
		
		for(EspeciesInformeForestal especieIng : listaEspeciesMuestreo){
			if(!listFamiliaSelect.contains(especieIng.getFamiliaEspecie())){
				listFamiliaSelect.add(especieIng.getFamiliaEspecie());
			}			
		}
		
		for(SitioMuestral sitioM : listaSitioMuestreo){
			if(sitioM.getNombreSitio().equals(especiesInformeForestalMuestreo.getSitioMuestral().getNombreSitio())){
				if(sitioM.getListaEspecieValorImportancia() != null){
					listaIndiceValorImportanciaEliminar.addAll(sitioM.getListaEspecieValorImportancia());
				}
				sitioM.setListaEspecieValorImportancia(new ArrayList<AnalisisResultadoForestal>());
				break;
			}
		}
		
		for(SitioMuestral sitioM : listaSitioMuestreo){
			double sumatoriaAreaBasal = 0.0;
			double sumatoriaVolumenTotal = 0.0;
			double sumatoriaVolumenComercial = 0.0;
			boolean existeSitio = false;
			for(EspeciesInformeForestal especie : listaEspeciesMuestreo){
				if(sitioM.getNombreSitio().equals(especie.getSitioMuestral().getNombreSitio())){
					existeSitio = true;
					sumatoriaAreaBasal += especie.getAreaBasal();	
					sumatoriaVolumenTotal += especie.getVolumenTotal();
					sumatoriaVolumenComercial += especie.getVolumenComercial();
				}
			}
			if(existeSitio){
				sitioM.setSumatoriaAreaBasal(sumatoriaAreaBasal);
				sitioM.setSumatoriaVolumenTotal(sumatoriaVolumenTotal);
				sitioM.setSumatoriaVolumenComercial(sumatoriaVolumenComercial);
				listaSitioSumatoria.add(sitioM);
			}			
		}

		List<EspeciesImportanciaForestal> listaEspeciesImportanciaAux = new ArrayList<EspeciesImportanciaForestal>();
		listaEspeciesImportanciaAux.addAll(listaEspeciesImportancia);
		for(EspeciesImportanciaForestal especieImp : listaEspeciesImportanciaAux){			
			if(!listFamiliaSelect.contains(especieImp.getFamiliaEspecie())){
				if(especieImp.getId() != null){
					listaEspeciesImportanciaEliminar.add(especieImp);
				}	
				listaEspeciesImportancia.remove(especieImp);
			}
		}
	}
	
	public void calculoAreaBasalM(){
		double area = (Math.PI/4)*Math.pow(especiesInformeForestalMuestreo.getAlturaDap(), 2);
		especiesInformeForestalMuestreo.setAreaBasal(area);
		
		if(especiesInformeForestalMuestreo.getAlturaTotal() != null){
			calcularVolumenComercialM();
		}
		if(especiesInformeForestalMuestreo.getAlturaComercial() != null){
			calcularVolumenTotalM();
		}		
	}
	
	public void calcularVolumenTotalM(){
		if (especiesInformeForestalMuestreo.getAreaBasal() != null
				&& especiesInformeForestalMuestreo.getAlturaTotal() != null) {
			double volumen = especiesInformeForestalMuestreo.getAreaBasal() * especiesInformeForestalMuestreo.getAlturaTotal() * FACTOR_FORMA;
			especiesInformeForestalMuestreo.setVolumenTotal(volumen);
		}
	}
	
	public void calcularVolumenComercialM(){
		if (especiesInformeForestalMuestreo.getAreaBasal() != null
				&& especiesInformeForestalMuestreo.getAlturaComercial() != null) {
			double volumen = especiesInformeForestalMuestreo.getAreaBasal() * especiesInformeForestalMuestreo.getAlturaComercial() * FACTOR_FORMA;
			especiesInformeForestalMuestreo.setVolumenComercial(volumen);
		}
	}
	
	public void editarCalculoBasalMuestreo(EspeciesInformeForestal calculo){
		editarSeleccionado = true;
		setEspeciesInformeForestalMuestreo(calculo);
		
		obtenerListaGeneroPorFamilia(calculo.getFamiliaEspecie());
		obtenerListaEspecies(calculo.getGeneroEspecie());
		
		desbloquearTaxonomiaAB(calculo);
	}
	
	public void eliminarCalculoBasalMuestreo(EspeciesInformeForestal calculo){
		if(calculo.getId() != null){
			listaEspeciesMuestreoEliminar.add(calculo);
		}
		
		listaEspeciesMuestreo.remove(calculo);

		listFamiliaSelect = new ArrayList<HigherClassification>();
		
		for(EspeciesInformeForestal especieIng : listaEspeciesMuestreo){
			if(!listFamiliaSelect.contains(especieIng.getFamiliaEspecie())){
				listFamiliaSelect.add(especieIng.getFamiliaEspecie());
			}			
		}
		
		for(SitioMuestral sitioM : listaSitioMuestreo){
			if(sitioM.getNombreSitio().equals(calculo.getSitioMuestral().getNombreSitio())){
				listaIndiceValorImportanciaEliminar.addAll(sitioM.getListaEspecieValorImportancia());
				sitioM.setListaEspecieValorImportancia(new ArrayList<AnalisisResultadoForestal>());
				sitioM.setTotalDmr(null);
				sitioM.setTotalDnr(null);
				sitioM.setTotalIvi(null);
				sitioM.setIndiceShanon(null);
				sitioM.setIndiceSimpson(null);
				sitioM.setExponencialShannon(null);
				sitioM.setInversoSimpson(null);
				break;
			}
		}
		
		listaSitioSumatoria = new ArrayList<SitioMuestral>();
		for(SitioMuestral sitioM : listaSitioMuestreo){
			double sumatoriaAreaBasal = 0.0;
			double sumatoriaVolumenTotal = 0.0;
			double sumatoriaVolumenComercial = 0.0;
			boolean existeSitio = false;
			for(EspeciesInformeForestal especie : listaEspeciesMuestreo){
				if(sitioM.getNombreSitio().equals(especie.getSitioMuestral().getNombreSitio())){
					existeSitio = true;
					sumatoriaAreaBasal += especie.getAreaBasal();	
					sumatoriaVolumenTotal += especie.getVolumenTotal();
					sumatoriaVolumenComercial += especie.getVolumenComercial();
				}
			}
			if(existeSitio){
				sitioM.setSumatoriaAreaBasal(sumatoriaAreaBasal);
				sitioM.setSumatoriaVolumenTotal(sumatoriaVolumenTotal);
				sitioM.setSumatoriaVolumenComercial(sumatoriaVolumenComercial);
				listaSitioSumatoria.add(sitioM);
			}			
		}

		List<EspeciesImportanciaForestal> listaEspeciesImportanciaAux = new ArrayList<EspeciesImportanciaForestal>();
		listaEspeciesImportanciaAux.addAll(listaEspeciesImportancia);
		for(EspeciesImportanciaForestal especieImp : listaEspeciesImportanciaAux){			
			if(!listFamiliaSelect.contains(especieImp.getFamiliaEspecie())){
				if(especieImp.getId() != null){
					listaEspeciesImportanciaEliminar.add(especieImp);					
				}	
				listaEspeciesImportancia.remove(especieImp);
			}
		}
	}
	
	public void calcularIndiceValorImportanciaMuestreo(SitioMuestral sitio){
		
		try {
			
			if(listaEspeciesMuestreo.isEmpty()){
				JsfUtil.addMessageError("Debe ingresar especies en la Tabla de cálculo del área basal y volumen");
				return;
			}
			
			List<EspeciesInformeForestal> listaEspeciesCalculo = new ArrayList<>();
			for(EspeciesInformeForestal especieSitio : listaEspeciesMuestreo){
				if(especieSitio.getSitioMuestral().equals(sitio)){
					listaEspeciesCalculo.add(especieSitio);
				}
			}

			if(listaEspeciesCalculo.isEmpty()){
				JsfUtil.addMessageError("Debe ingresar especies en la Tabla de cálculo del área basal y volumen");
				return;
			}
			
			Double totalAreaBasal = 0.0;
			Integer totalIndividuos = listaEspeciesCalculo.size();
			
			if(sitio.getListaEspecieValorImportancia() != null) {
				listaIndiceValorImportanciaEliminar.addAll(sitio.getListaEspecieValorImportancia());
			}
			sitio.setListaEspecieValorImportancia(new ArrayList<AnalisisResultadoForestal>());
			for(EspeciesInformeForestal especie : listaEspeciesCalculo){
				totalAreaBasal += especie.getAreaBasal();
			}
			
			for(EspeciesInformeForestal especie : listaEspeciesCalculo){
				if(especie.getEspecie() != null){
					int numeroIndividuos = 0;
					double areaBasalEspecie = 0.0;
					boolean existe = false;
					for(AnalisisResultadoForestal specie : sitio.getListaEspecieValorImportancia()){
						if(specie.getEspecie() != null && especie.getEspecie().getSptaScientificName().equals(specie.getEspecie().getSptaScientificName())){
							existe = true;
							areaBasalEspecie = specie.getAreaBasal() + especie.getAreaBasal();					
							numeroIndividuos = specie.getFrecuencia() + 1;
							specie.setAreaBasal(areaBasalEspecie);
							specie.setFrecuencia(numeroIndividuos);
						}
					}
					
					if(!existe){
						AnalisisResultadoForestal specie = new AnalisisResultadoForestal();
						specie.setEspecie(especie.getEspecie());						
						specie.setGeneroEspecie(especie.getGeneroEspecie());
						specie.setAreaBasal(especie.getAreaBasal());
						specie.setFrecuencia(1);
						specie.setNombreComun(especie.getNombreComun());
						sitio.getListaEspecieValorImportancia().add(specie);
					}					
					
				}else{
					int numeroIndividuos = 0;
					double areaBasalEspecie = 0.0;
					boolean existe = false;
					for(AnalisisResultadoForestal specie : sitio.getListaEspecieValorImportancia()){
						if(specie.getEspecie() == null && especie.getGeneroEspecie().getHiclScientificName().equals(specie.getGeneroEspecie().getHiclScientificName())){
							existe = true;
							areaBasalEspecie = specie.getAreaBasal() + especie.getAreaBasal();					
							numeroIndividuos = specie.getFrecuencia() + 1;
							specie.setAreaBasal(areaBasalEspecie);
							specie.setFrecuencia(numeroIndividuos);
						}
					}
					
					if(!existe){
						AnalisisResultadoForestal specie = new AnalisisResultadoForestal();
						specie.setEspecie(especie.getEspecie());						
						specie.setGeneroEspecie(especie.getGeneroEspecie());
						specie.setAreaBasal(especie.getAreaBasal());
						specie.setFrecuencia(1);
						specie.setNombreComun(especie.getNombreComun());
						sitio.getListaEspecieValorImportancia().add(specie);
					}			
				}				
			}
			
			double totalDnR = 0.0;
			double totalDmR = 0.0;
			double totalIVI = 0.0;
			double shannon = 0.0;
			double simpson = 0.0;
			
			for(AnalisisResultadoForestal calculo : sitio.getListaEspecieValorImportancia()){
				
				double frecuencia = calculo.getFrecuencia();
				double totalInd = totalIndividuos;
				
				double dnr = (frecuencia / totalInd) * 100;
				double dmr = (calculo.getAreaBasal()/totalAreaBasal) * 100;
				double ivi = (dnr + dmr)/2;
				
				calculo.setValorDmr(dmr);
				calculo.setValorDnr(dnr);
				calculo.setValorIvi(ivi);
				
				totalDnR +=dnr;
				totalDmR += dmr;
				totalIVI += ivi;
				
				double proporcionInd = calculo.getFrecuencia() / totalInd;
				
				shannon += proporcionInd * (Math.log(proporcionInd));		
				
				simpson += Math.pow(proporcionInd, 2);
				
				calculo.setValorPi(proporcionInd);
				calculo.setValorLnPi(Math.log(proporcionInd));
				calculo.setValorPiCuadrado(Math.pow(proporcionInd, 2));				
			}
			
			sitio.setTotalDmr(totalDmR);
			sitio.setTotalDnr(new BigDecimal(totalDnR));
			sitio.setTotalIvi(totalIVI);
			sitio.setIndiceShanon(-shannon);
			
			double exponencial = Math.exp(sitio.getIndiceShanon());
			
			sitio.setExponencialShannon(exponencial);
			
			double indiceSimp = 1 - simpson;
			
			sitio.setIndiceSimpson(indiceSimp);
			
			double inversoSimp = 1/sitio.getIndiceSimpson();
			
			double inf = Double.POSITIVE_INFINITY;
			
			if(inversoSimp == inf){
				sitio.setInversoSimpson(null);
			}else{
				sitio.setInversoSimpson(inversoSimp);
			}
			
			
		} catch (Exception e) {
			LOG.error("Error en el cálculo del indice de valor de importancia");
		}
		
	}
		
	public void editarSitioMuestralMuestreo(SitioMuestral sitio){
		editarSeleccionado = true;		
		setSitioMuestral(sitio);
		
		listaSitioMuestreoAux = new ArrayList<SitioMuestral>();
		listaSitioMuestreoAux.addAll(listaSitioMuestreo);
		listaSitioMuestreoAux.remove(sitio);
		
	}
	
	public void eliminarSitioMuestralMuestreo(SitioMuestral sitio){
		if(sitio.getId() != null){
			listaSitioMuestreoEliminar.add(sitio);
		}
		
		listaSitioMuestreo.remove(sitio);
		
		List<EspeciesInformeForestal> listaEspeciesAux = new ArrayList<EspeciesInformeForestal>();
		listaEspeciesAux.addAll(listaEspeciesMuestreo);
		for(EspeciesInformeForestal especie : listaEspeciesAux){
			if(especie.getSitioMuestral().equals(sitio)){
				if(especie.getId() != null){
					listaEspeciesMuestreoEliminar.add(especie);
				}
				listaEspeciesMuestreo.remove(especie);
			}
		}		
		
		listaSitioSumatoria = new ArrayList<SitioMuestral>();		
		
		for(SitioMuestral sitioM : listaSitioMuestreo){
			double sumatoriaAreaBasal = 0.0;
			double sumatoriaVolumenTotal = 0.0;
			double sumatoriaVolumenComercial = 0.0;
			boolean existeSitio = false;
			for(EspeciesInformeForestal especie : listaEspeciesMuestreo){
				if(sitioM.getNombreSitio().equals(especie.getSitioMuestral().getNombreSitio())){
					existeSitio = true;
					sumatoriaAreaBasal += especie.getAreaBasal();	
					sumatoriaVolumenTotal += especie.getVolumenTotal();
					sumatoriaVolumenComercial += especie.getVolumenComercial();
				}
			}
			if(existeSitio){
				sitioM.setSumatoriaAreaBasal(sumatoriaAreaBasal);
				sitioM.setSumatoriaVolumenTotal(sumatoriaVolumenTotal);
				sitioM.setSumatoriaVolumenComercial(sumatoriaVolumenComercial);
				listaSitioSumatoria.add(sitioM);
			}			
		}
		
		double areaAnterior = informeFactivilidadForestal.getAreaSitioMuestral();
		double areaActual = areaAnterior-sitio.getAreaSitio();		
		informeFactivilidadForestal.setAreaSitioMuestral(areaActual);
		
		listFamiliaSelect = new ArrayList<HigherClassification>();
		
		for(EspeciesInformeForestal especieIng : listaEspeciesMuestreo){
			if(!listFamiliaSelect.contains(especieIng.getFamiliaEspecie())){
				listFamiliaSelect.add(especieIng.getFamiliaEspecie());
			}			
		}
		
		List<EspeciesImportanciaForestal> listaEspeciesImportanciaAux = new ArrayList<EspeciesImportanciaForestal>();
		listaEspeciesImportanciaAux.addAll(listaEspeciesImportancia);
		for(EspeciesImportanciaForestal especieImp : listaEspeciesImportanciaAux){			
			if(!listFamiliaSelect.contains(especieImp.getFamiliaEspecie())){
				if(especieImp.getId() != null){
					listaEspeciesImportanciaEliminar.add(especieImp);					
				}	
				listaEspeciesImportancia.remove(especieImp);
			}
		}
	}
	
	public StreamedContent getPlantillaCoordenadas() throws Exception {	
		try {
            plantillaCoordenadas = documentosFacade.descargarDocumentoPorNombre(Constantes.PLANTILLA_COORDENADAS_VIABILIDAD_FORESTAL);        	
        	
        } catch (Exception e) {
        }		
        
        try {
            if (plantillaCoordenadas != null) {
            	
            	DefaultStreamedContent content = null;
                content = new DefaultStreamedContent(new ByteArrayInputStream(plantillaCoordenadas));
                content.setName(Constantes.PLANTILLA_COORDENADAS);
                return content;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }        
        return null;
        
    }
	
	public void eliminarAnexo(DocumentoViabilidad documento){
		if(documento.getId() != null){
			listaDocumentosAnexosEliminar.add(documento);
		}
		listaDocumentosAnexos.remove(documento);
		
	}
	
	public String validarCoordenadaArea(String coordenadaPunto) throws RemoteException {
		// Verificar si las coordenadas estan contenidas en el poligono de implantacion del proyecto
		String mensaje = null;
		Boolean intersecaProyecto = false;

		List<String> listaCoordenadasValidar = new ArrayList<String>();
		listaCoordenadasValidar.add(coordenadaPunto);

		for (int i = 0; i < listaCoordenadasValidar.size(); i++) {
			for (int j = 0; j < coordenadasGeograficasProyecto.size(); j++) {
				ContienePoligono_entrada verificarGeoImpla = new ContienePoligono_entrada();
				verificarGeoImpla.setU(Constantes.getUserWebServicesSnap());
				verificarGeoImpla.setTipo("pu");
				verificarGeoImpla.setXy1(coordenadasGeograficasProyecto.get(j));
				verificarGeoImpla.setXy2(listaCoordenadasValidar.get(i));

				SVA_Reproyeccion_IntersecadoPortTypeProxy wsGeoImpl = new SVA_Reproyeccion_IntersecadoPortTypeProxy();
				ContienePoligono_resultado[] intRestGeoImpl = wsGeoImpl.contienePoligono(verificarGeoImpla);
				if (intRestGeoImpl[0].getInformacion().getError() != null) {
					mensaje = intRestGeoImpl[0].getInformacion().getError().toString();
					break;
				} else {
					if (intRestGeoImpl[0].getContienePoligono().getValor().equals("f")) {
						intersecaProyecto = false;
					} else {
						intersecaProyecto = true;
						break;
					}
				}
			}
		}
		
		if(!intersecaProyecto) {
			mensaje = "Las coordenadas están fuera del área geográfica";
		}
		
		return mensaje;
	}

	public void cambiarTipoRecoleccionDatos(){
		if(!listaEspeciesImportancia.isEmpty()){
			for(EspeciesImportanciaForestal especie : listaEspeciesImportancia){
				if(especie.getId() != null){
					listaEspeciesImportanciaEliminar.add(especie);
				}
			}
			listaEspeciesImportancia = new ArrayList<EspeciesImportanciaForestal>();
		}	
	}
	
	public void abrirAfectacionForestal(){
		listaIdCoberturaVegetal = new ArrayList<String>();
		listaIdConvenio = new ArrayList<String>();
		listaIdEcosistemas = new ArrayList<String>();
		listaIdUnidadHidrograficas = new ArrayList<String>();
		editarSeleccionado = false;
		afectacionForestal = new AfectacionForestal();
	}
	
	public void editarAfectacionForestal(AfectacionForestal afectacion){
		editarSeleccionado = true;
		setAfectacionForestal(afectacion);
	}
	
	public void eliminarAfectacionForestal(AfectacionForestal afectacion){
		if(afectacion.getId() != null){
			listaAfectacionForestalEliminar.add(afectacion);
		}
		
		listaAfectacionForestal.remove(afectacion);
	}
	
	public void aceptarAfectacionForestal(){
		if(afectacionForestal.getListaIdCoberturaVegetal().size() > 0) {
			String cobertura = afectacionForestal.getListaIdCoberturaVegetal().toString(); 
			afectacionForestal.setTipoCoberturaVegetal(cobertura.substring(1, cobertura.length() -  1).replaceAll("\\s", ""));
			
			String nombre = "";
			for (String item : afectacionForestal.getListaIdCoberturaVegetal()) {
				Integer id = Integer.valueOf(item);
				for (DetalleInterseccionProyectoAmbiental detalle : listaInterseccionesCobertura) {
					if(id.equals(detalle.getId())) {
						nombre += (nombre == "") ? detalle.getNombreGeometria() : ", " + detalle.getNombreGeometria();
					}
				}
			}
			
			afectacionForestal.setNombresCoberturaVegetal(nombre);
		}

		if(afectacionForestal.getListaIdEcosistemas().size() > 0) {
			String convenio = afectacionForestal.getListaIdEcosistemas().toString(); 
			afectacionForestal.setTipoEcosistemas(convenio.substring(1, convenio.length() - 1).replaceAll("\\s", ""));

			String nombre = "";
			for (String item : afectacionForestal.getListaIdEcosistemas()) {
				Integer id = Integer.valueOf(item);
				for (DetalleInterseccionProyectoAmbiental detalle : listaInterseccionesEcosistema) {
					if(id.equals(detalle.getId())) {
						nombre += (nombre == "") ? detalle.getNombreGeometria() : ", " + detalle.getNombreGeometria();
					}
				}
			}
			
			afectacionForestal.setNombresEcosistemas(nombre);
		}

		if(afectacionForestal.getListaIdConvenio().size() > 0) {
			String convenio = afectacionForestal.getListaIdConvenio().toString(); 
			afectacionForestal.setTipoConvenio(convenio.substring(1, convenio.length() - 1).replaceAll("\\s", ""));

			String nombre = "";
			for (String item : afectacionForestal.getListaIdConvenio()) {
				Integer id = Integer.valueOf(item);
				for (DetalleInterseccionProyectoAmbiental detalle : listaInterseccionesConvenios) {
					if(id.equals(detalle.getId())) {
						nombre += (nombre == "") ? detalle.getNombreGeometria() : ", " + detalle.getNombreGeometria();
					}
				}
			}
			
			afectacionForestal.setNombresConvenio(nombre);
		}

		if(afectacionForestal.getListaIdUnidadHidrograficas().size() > 0) {
			String unidadHidro = afectacionForestal.getListaIdUnidadHidrograficas().toString(); 
			afectacionForestal.setTipoUnidadesHidrograficas(unidadHidro.substring(1, unidadHidro.length() -  1).replaceAll("\\s", ""));

			String nombre = "";
			for (String item : afectacionForestal.getListaIdUnidadHidrograficas()) {
				nombre += (nombre == "") ? item : ", " + item;
			}
			afectacionForestal.setNombresUnidadHidrograficas(nombre);
		}
		
		if(!editarSeleccionado){
			listaAfectacionForestal.add(afectacionForestal);
		}
		
		afectacionForestal = new AfectacionForestal();
		editarSeleccionado = false;
	}

	//***MIOS
	@EJB
	private PlantillaReporteFacade plantillaReporteFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private InformeFactibilidadForestalFacade informeFactibilidadForestalFacade;
	@EJB
	private UnidadHidrograficaFacade unidadHidrograficaFacade;
	@EJB
	private EquipoConsultorForestalFacade equipoConsultorForestalFacade;
	@EJB
	private SitioMuestralFacade sitioMuestralFacade;
	@EJB
	private CoordenadaSitioMuestralFacade coordenadaSitioMuestralFacade;
	@EJB
	private EspeciesInformeForestalFacade especiesInformeForestalFacade;
	@EJB
	private EspeciesImportanciaForestalFacade especiesImportanciaForestalFacade;
	@EJB
	private AnalisisResultadoForestalFacade analisisResultadoForestalFacade;
	@EJB
	private AfectacionForestalFacade afectacionForestalFacade;
	@EJB
	private UbicacionSitioMuestralFacade ubicacionSitioMuestralFacade;
	@EJB
	private CapasCoaFacade capasCoaFacade;
	
	@Getter
	@Setter
	private List<File> filesFotos;
	
	@Getter
	@Setter
	private List<AnalisisResultadoForestal> listaIndiceValorImportanciaCenso;
	
	@Getter
	@Setter
	private Area areaTramiteViabilidad;
	
	@Getter
	@Setter
	private boolean informeGuardado;
	
	public void visualizarInforme() {
		try {
			generarInforme(true);
			
			informeGuardado = false;
			
			RequestContext context = RequestContext.getCurrentInstance();
			context.update("formDialog:dlgInformeFact");
			context.execute("PF('dlgInformeFact').show();");
		} catch (Exception e) {
			informeGuardado = false;
			
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public StreamedContent getStreamInforme() throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		
		if (informeFactivilidadForestal.getArchivoInforme() != null) {
			content = new DefaultStreamedContent(new ByteArrayInputStream(informeFactivilidadForestal.getArchivoInforme()), "application/octet-stream");
			content.setName(informeFactivilidadForestal.getNombreReporte());
		}
		
		return content;
	}
	
	public void generarInforme(Boolean marcaAgua) throws Exception {
		filesFotos = new ArrayList<>();
		
		String nombreTipoInforme = "Informe factibilidad";
		if(informeFactivilidadForestal == null || informeFactivilidadForestal.getId() == null) {
			informeFactivilidadForestal = new InformeFactibilidadForestal();
		} 
		
		informeFactivilidadForestal.setNombreReporte(nombreTipoInforme + " " + UtilViabilidad.getFileNameEscaped(proyectoLicenciaCoa.getCodigoUnicoAmbiental().replace("/", "-")) + ".pdf");
		
		PlantillaReporte plantillaReporteInforme = plantillaReporteFacade.obtenerPlantillaReportePorCodigo(TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_INFORME_FACTIBILIDAD.getIdTipoDocumento(), "VIABILIDAD_AMBIENTAL_PFN_II_INFORME_FACTIBILIDAD");
		PlantillaReporte plantillaReporteCaratula = plantillaReporteFacade.obtenerPlantillaReportePorCodigo(TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_INFORME_FACTIBILIDAD.getIdTipoDocumento(), "VIABILIDAD_AMBIENTAL_PFN_II_INFORME_FACTIBILIDAD_CARATULA");
		PlantillaReporte plantillaReporteContenido = plantillaReporteFacade.obtenerPlantillaReportePorCodigo(TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_INFORME_FACTIBILIDAD.getIdTipoDocumento(), "VIABILIDAD_AMBIENTAL_PFN_II_INFORME_FACTIBILIDAD_CONTENIDO");
		
		InformeFactibilidadForestalEntity informeEntity = cargarDatosDocumento();
		InformeFactibilidadForestalEntity informeEntityCaratula = cargarDatosCaratula();
		
		String[] params = new String[1];
		params[0] = proyectoLicenciaCoa.getCodigoUnicoAmbiental();
		
		File informeCaratula = UtilGenerarDocumentoViabilidadForestal.generarFicheroBlanco(plantillaReporteCaratula.getHtmlPlantilla(), 
				informeFactivilidadForestal.getNombreReporte() + "_caratula", informeEntityCaratula);
		
		File informeContenido = UtilGenerarDocumentoViabilidadForestal.generarFicheroSinCabecera(plantillaReporteContenido.getHtmlPlantilla(), 
				informeFactivilidadForestal.getNombreReporte() + "_contenido", true, informeEntityCaratula, null, 0);
		
		File informePdfContenido = UtilGenerarDocumentoViabilidadForestal.generarFichero(plantillaReporteInforme.getHtmlPlantilla(), informeFactivilidadForestal.getNombreReporte(), 
				true, informeEntity, "<span style='color:red'>INGRESAR</span>", params, 1, true, true);
		
		List<File> listaFiles = new ArrayList<File>();
		listaFiles.add(informeCaratula);
		listaFiles.add(informeContenido);
		listaFiles.add(informePdfContenido);
		File informePdfAux = UtilFichaMineria.unirPdf(listaFiles, informeFactivilidadForestal.getNombreReporte()); 

		File informePdf = JsfUtil.fileMarcaAgua(informePdfAux, marcaAgua ? " - - BORRADOR - - " : " ", BaseColor.GRAY);

		Path path = Paths.get(informePdf.getAbsolutePath());
		informeFactivilidadForestal.setArchivoInforme(Files.readAllBytes(path));
		String reporteHtmlfinal = informeFactivilidadForestal.getNombreReporte().replace("/", "-");
		File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
		FileOutputStream file = new FileOutputStream(archivoFinal);
		file.write(informeFactivilidadForestal.getArchivoInforme());
		file.close();
		informeFactivilidadForestal.setInformePath(JsfUtil.devolverContexto("/reportesHtml/" + informeFactivilidadForestal.getNombreReporte()));
		
		if(filesFotos!= null && filesFotos.size() > 0) {
			for (File item : filesFotos) {
				item.delete();
			}
		}
	}
	
	public InformeFactibilidadForestalEntity cargarDatosCaratula() throws Exception {
		InformeFactibilidadForestalEntity informeEntity = new InformeFactibilidadForestalEntity();
		
		DateFormat formatoFechaCorta = new SimpleDateFormat("MM-yyyy");
		
		informeEntity.setFechaInforme(formatoFechaCorta.format(new Date()));
		informeEntity.setFotoLogo("<img src=\'" + getRecursoImage("logo_mae_header.png") + "\' height=\'50%\' width=\'50%\' ></img>");
		informeEntity.setNombreProyecto(proyectoLicenciaCoa.getNombreProyecto());
		
		return informeEntity;
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
	
	public InformeFactibilidadForestalEntity cargarDatosDocumento() throws Exception {
		
		List<String> infoOperador = usuarioFacade.recuperarNombreOperador(proyectoLicenciaCoa.getUsuario());
		
		String interseccionesProyecto = interseccionViabilidadCoaFacade.getInterseccionesForestal(proyectoLicenciaCoa.getId(), 2);
		
		InformeFactibilidadForestalEntity informeEntity = new InformeFactibilidadForestalEntity();
		
		informeEntity.setCedula(proyectoLicenciaCoa.getUsuario().getNombre());
		informeEntity.setNombreOperador(infoOperador.get(0));
		informeEntity.setRepresentanteLegal((infoOperador.get(1).equals("")) ? "N/A" : (infoOperador.get(1)));
		informeEntity.setDireccion(direccion);
		informeEntity.setEmail(correo);
		informeEntity.setTelefono(telefono);
		informeEntity.setCasilleroJudicial(informeFactivilidadForestal.getCasilleroJudicial());
		
		informeEntity.setNombreProyecto(proyectoLicenciaCoa.getNombreProyecto());
		
		informeEntity.setCoordenadasGeograficas(getTablaCoordenadas(coordenadasGeograficas, false));
		informeEntity.setCoordenadasImplantacion(getTablaCoordenadas(coordenadasImplantacion, false));
		informeEntity.setUbicaciones(getTablaUbicacionProyecto());
		
		informeEntity.setInterseca(interseccionesProyecto);
		
		informeEntity.setConveniosInterseca(getTablaConvenios());
		
		informeEntity.setUnidadesHidrograficas(getTablaUnidadesHidrograficas());
		
		informeEntity.setSectorProyecto(getSectorProyecto());
		
		informeEntity.setEquipoConsultor(getTablaEquipoConsultor());
		
		informeEntity.setAntecedentes(informeFactivilidadForestal.getAntecedentes());
		informeEntity.setMarcoLegal(informeFactivilidadForestal.getMarcoLegal());
		informeEntity.setObjetivo(informeFactivilidadForestal.getObjetivo());
		informeEntity.setDescripcionProyecto(informeFactivilidadForestal.getDescripcionProyecto());
		informeEntity.setFotosDescripcion(getTablaFotos(listaFotoArchivo));
		informeEntity.setDescripcionCobertura(informeFactivilidadForestal.getDescripcionCoberturaVegetal());
		informeEntity.setListaCobertura(getTablaCobertura());
		informeEntity.setDescripcionEcosistemas(informeFactivilidadForestal.getDescripcionEcosistemas());
		informeEntity.setListaEcosistemas(getTablaEcosistemas());
		
		if(informeFactivilidadForestal.getCaracterizacionCobertura() != null 
				&& informeFactivilidadForestal.getCaracterizacionCobertura().equals(InformeFactibilidadForestal.caracterizacionCualitativa)) {
			informeEntity.setDisplayCaracterizacionCualitativa("inline");
			informeEntity.setDescripcionCaracterizacionCualitativa(informeFactivilidadForestal.getDescripcionMetodologia());
			informeEntity.setListaUbicacionCualitativa(getTablaUbicacionSitios());
			informeEntity.setListaEspeciesCualitativa(getTablaEspeciesCualitativa());
		}
		
		if(informeFactivilidadForestal.getCaracterizacionCobertura() != null 
				&& informeFactivilidadForestal.getCaracterizacionCobertura().equals(InformeFactibilidadForestal.caracterizacionCuantitativa)) {
			informeEntity.setDisplayCaracterizacionCuantitativa("inline");
			informeEntity.setFaseCampo(informeFactivilidadForestal.getFaseCampo());
			informeEntity.setFotosCampo(getTablaFotos(listaFotografiaCuantitativa));
			informeEntity.setFaseOficina(informeFactivilidadForestal.getFaseOficina());
			informeEntity.setFotosOficina(getTablaFotos(listaFotografiaOficina));
			
			if(informeFactivilidadForestal.getRecoleccionDatos() != null 
					&& informeFactivilidadForestal.getRecoleccionDatos().equals(InformeFactibilidadForestal.recoleccionCenso)) {
				informeEntity.setDisplayResultadosCenso("inline");
				informeEntity.setUbicacionCenso(getTablaUbicacionCenso());
				informeEntity.setTablaAreaBasalCenso(stringTablasViabilidad.getTablaAreaBasalCenso( listaEspeciesCenso));
				informeEntity.setTablaSumatoriasCenso("");
				informeEntity.setInterpretacionAreaBasalCenso(informeFactivilidadForestal.getInterpretacionAreaBasal());
				informeEntity.setEspeciesImportanciaCenso(getTablaEspeciesImportanciaCensoMuestreo(listaEspeciesImportancia));
				informeEntity.setInterpretacionEspeciesImportanciaCenso(informeFactivilidadForestal.getInterpretacionEspeciesImportancia());
				informeEntity.setTablaIviCenso(getTablaIviCenso());
				informeEntity.setInterpretacionIviCenso(informeFactivilidadForestal.getInterpretacionValorImportancia());
				informeEntity.setInterpretacionShannonCenso(informeFactivilidadForestal.getInterpretacionShanonSimpson());
			}
			
			if(informeFactivilidadForestal.getRecoleccionDatos() != null 
					&& informeFactivilidadForestal.getRecoleccionDatos().equals(InformeFactibilidadForestal.recoleccionMuestreo)) {
				informeEntity.setDisplayResultadosMuestreo("inline");
				informeEntity.setUbicacionMuestreo(getTablaUbicacionMuestreo());
				informeEntity.setTotalAreaMuestral(informeFactivilidadForestal.getAreaSitioMuestral().toString());
				informeEntity.setTablaAreaBasalMuestreo(getTablaAreaBasalMuestreo());
				informeEntity.setTablaSumatoriasMuestreo(getTablaSumatoriasMuestreo());
				informeEntity.setInterpretacionAreaBasalMuestreo(informeFactivilidadForestal.getInterpretacionAreaBasal());
				informeEntity.setEspeciesImportanciaMuestreo(getTablaEspeciesImportanciaCensoMuestreo(listaEspeciesImportancia));
				informeEntity.setInterpretacionEspeciesImportanciaMuestreo(informeFactivilidadForestal.getInterpretacionEspeciesImportancia());
				informeEntity.setTablasIviMuestreo(getInfoIviPorSitiosMuestrales());
			}
		}
		
		List<String> listaAfectaciones = getTablaAfectacionForestal();
		informeEntity.setDetalleActividades(listaAfectaciones.get(0));
		informeEntity.setDetalleAfectaciones(listaAfectaciones.get(1));
		
		informeEntity.setConclusiones(informeFactivilidadForestal.getConclusiones());
		informeEntity.setRecomendaciones(informeFactivilidadForestal.getRecomendaciones());
		informeEntity.setBibliografia(informeFactivilidadForestal.getBibliografia());
		informeEntity.setAnexos(getTablaAnexos());
		
		return informeEntity;
	}
	
	public String getTablaCoordenadas(List<CoordendasPoligonos> listaCoordenadas, Boolean isHeader) {
		String tableCoordenadas = "<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 80%; border-collapse:collapse;font-size:10px;\" ";
		if(isHeader) {
			tableCoordenadas += "<tr><th colspan=\"4\">Ubicación del censo forestal</th></tr>";
			
		}
		String headerTableCoordenadas = "<tr><th style=\"text-align: center;\">Área Geográfica</th>"
				+"<th style=\"text-align: center;\">Shape</th>"
				+ "<th style=\"text-align: center;\">Coordenada X</th>"
				+ "<th style=\"text-align: center;\">Coordenada Y</th></tr>";
		
		String coordenadaGeografica = "";
		for(CoordendasPoligonos coordenadasPol : listaCoordenadas){
			coordenadaGeografica += tableCoordenadas;
			coordenadaGeografica += headerTableCoordenadas;
			
			if(coordenadasPol != null){
				for(CoordenadasProyecto coordenada : coordenadasPol.getCoordenadas()){
					String nroArea = (coordenada.getAreaGeografica() == null) ? "1" : coordenada.getAreaGeografica().toString();
					coordenadaGeografica += "<tr><td>" + nroArea + "</td><td>" + coordenada.getOrdenCoordenada() + "</td><td>" + coordenada.getX().toString() + "</td><td>" + coordenada.getY().toString() + "</td></tr>";
				}						
			}	
			coordenadaGeografica += "</table>";
		}
		
		return coordenadaGeografica;
	}
	
	public String getTablaUbicacionProyecto() {
        String ubicacionCompleta = "";
		
        ubicacionCompleta = "<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:10px;\">";
		ubicacionCompleta += "<tr><th>Provincia</th><th>Cantón</th><th>Parroquia</th></tr>";
		for (UbicacionesGeografica ubicacionActual : ubicacionProyecto) {
			ubicacionCompleta += "<tr>"
					+ "<td>" + ubicacionActual.getUbicacionesGeografica().getUbicacionesGeografica().getNombre() + "</td>"
					+ "<td>" + ubicacionActual.getUbicacionesGeografica().getNombre() + "</td>"
					+ "<td>" + ubicacionActual.getNombre() + "</td></tr>";
		}

		ubicacionCompleta += "</table>";
		
		return ubicacionCompleta;
	}
	
	public String getTablaConvenios() {
		StringBuilder stringBuilder = new StringBuilder();
		if(listaInterseccionesConvenios != null && !listaInterseccionesConvenios.isEmpty()) {
			stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:10px;\">");
			stringBuilder.append("<tr><th>Tipo de convenio</th><th>Código del convenio</th>"
					+ "<th>Nombre beneficiario</th>"
					+ "<th>Área total (ha)</th><th>Área intersección</th></tr>");
			for (DetalleInterseccionProyectoAmbiental item : listaInterseccionesConvenios) {
				stringBuilder.append("<tr>");
				stringBuilder.append("<td>" + item.getTipoConvenio() + "</td>");
				stringBuilder.append("<td>" + item.getCodigoConvenio() + "</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(item.getBeneficiarioConvenio());
				stringBuilder.append("</td>");
				stringBuilder.append("<td style=\"text-align: center;\">");
				//stringBuilder.append(item.getAreaTotalCapa());
				stringBuilder.append(String.format("%.2f", item.getPorcentajeAreaInterseccion() * proyectoLicenciaCoa.getSuperficie().doubleValue()));
				stringBuilder.append("</td>");
				stringBuilder.append("<td style=\"text-align: center;\">");
				stringBuilder.append((item.getPorcentajeAreaInterseccion() * 100) + "%");
				stringBuilder.append("</td>");
				stringBuilder.append("</tr>");
			}
			stringBuilder.append("</table>");
		}else
			stringBuilder.append("No se registran convenios para conservación o restauración ");
		return stringBuilder.toString();
	}
	
	public String getTablaUnidadesHidrograficas() {
		StringBuilder stringBuilder = new StringBuilder();
		if(listaInterseccionUHidrograficas != null && !listaInterseccionUHidrograficas.isEmpty()) {
			stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:10px;\">");
			stringBuilder.append("<tr><th>Nombre</th><th>Coordenada X</th><th>Coordenada Y</th><th>Elevación (m.s.n.m.)</th></tr>");
			
			for (UnidadHidrografica item : listaInterseccionUHidrograficas) {
				stringBuilder.append("<tr>");
				stringBuilder.append("<td style=\"text-align: center;\">" + item.getNombre() + "</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(item.getCoordenadaX());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(item.getCoordenadaY());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(item.getElevacion());
				stringBuilder.append("</td>");
				stringBuilder.append("</tr>");
			}
			stringBuilder.append("</table>");
		}else
			stringBuilder.append("No se registran unidades hidrográficas ");
		
		return stringBuilder.toString();
	}
	
	public String getSectorProyecto() {
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:10px;\">");
		stringBuilder.append("<tr>");
		stringBuilder.append("<th>Sector proyecto</th>");
		stringBuilder.append("<td>");
		stringBuilder.append(sector.getNombre());
		stringBuilder.append("</td>");
		stringBuilder.append("<th>Fase del proyecto</th>");
		stringBuilder.append("<td>");
		stringBuilder.append((informeFactivilidadForestal.getFaseProyecto() != null) ? informeFactivilidadForestal.getFaseProyecto() : "");
		stringBuilder.append("</td>");
		stringBuilder.append("</tr>");
		
		stringBuilder.append("</table>");
		
		return stringBuilder.toString();
	}
	
	public String getTablaEquipoConsultor() {
		StringBuilder stringBuilder = new StringBuilder();
		
		List<EquipoConsultorForestal> listaEquipoConsultorCompleta = new ArrayList<>();
		
		if(listaEquipoConsultor != null && listaEquipoConsultor.size() > 0) {
			listaEquipoConsultorCompleta.addAll(listaEquipoConsultor);
		}
		
		if(consultorPrincipal != null && consultorPrincipal.getConsultor() != null && consultorPrincipal.getConsultor().getCedula() != null) {
			listaEquipoConsultorCompleta.add(0, consultorPrincipal);
		}
		
		stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:10px;\">");
		stringBuilder.append("<tr><th>Cédula</th><th>Nombres y Apellidos</th><th>Tí­tulo académico</th><th>Registro de tí­tulo SENESCYT</th>"
				+ "<th>Correo electrónico</th><th>Teléfono</th></tr>");
		
		for (EquipoConsultorForestal item : listaEquipoConsultorCompleta) {
			stringBuilder.append("<tr>");
			stringBuilder.append("<td>" + item.getConsultor().getCedula() + "</td>");
			stringBuilder.append("<td>");
			stringBuilder.append(item.getConsultor().getNombres());
			stringBuilder.append("</td>");
			stringBuilder.append("<td>");
			stringBuilder.append(item.getConsultor().getTituloAcademico());
			stringBuilder.append("</td>");
			stringBuilder.append("<td>");
			stringBuilder.append(item.getConsultor().getRegistroSenecyt());
			stringBuilder.append("</td>");
			stringBuilder.append("<td>");
			stringBuilder.append(item.getCorreo());
			stringBuilder.append("</td>");
			stringBuilder.append("<td>");
			stringBuilder.append(item.getTelefono());
			stringBuilder.append("</td>");
			stringBuilder.append("</tr>");
		}
		stringBuilder.append("</table>");
		
		return stringBuilder.toString();
	}
	
	public String getTablaFotos(List<DocumentoViabilidad> listaFotos) throws Exception {
		StringBuilder stringBuilder = new StringBuilder();
		
		if(listaFotos != null && listaFotos.size() > 0) {
			
			String tamanioTabla = (listaFotos.size() >= 2) ? "width: 100%;" :  "width: 50%;";
			
			stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\""+tamanioTabla+" border-collapse:collapse;font-size:10px;\">");
			
			for (int i = 0; i < listaFotos.size(); i++) {
				stringBuilder.append("<tr>");
				StringBuilder stringBuilderDescripcion = new StringBuilder();
				for (int j = 0; j < 2; j++) {
					DocumentoViabilidad foto = null;
					if(i < listaFotos.size())
						foto = listaFotos.get(i);
					
					if(foto != null) {
						Timestamp timestamp = new Timestamp(System.currentTimeMillis());
						
						if(foto.getContenidoDocumento() == null) {
							foto.setContenidoDocumento(documentosViabilidadFacade.descargar(foto.getIdAlfresco()));
						}
						
						ByteArrayInputStream inputStreamFirma = new ByteArrayInputStream(foto.getContenidoDocumento());
						BufferedImage bImageFirma = ImageIO.read(inputStreamFirma);
						File imageFile = new File(timestamp.getTime() +"_" + foto.getNombre());
						ImageIO.write(bImageFirma, "jpg", imageFile);
						
						stringBuilder.append("<td style=\"text-align: center; width: 50%\">");
						stringBuilder.append("<img src=\'" + imageFile.getPath() + "\' height=\'160\' width=\'160\' ></img>");
						stringBuilder.append("</td>");
						
						stringBuilderDescripcion.append("<td style=\"text-align: justify;\">");
						stringBuilderDescripcion.append((i + 1) + ". " + foto.getDescripcionAnexo());
						stringBuilderDescripcion.append("</td>");
						
						filesFotos.add(imageFile);
					} else {
						if(listaFotos.size() >= 2) {
							stringBuilder.append("<td></td>");
							stringBuilderDescripcion.append("<td></td>");
						}
					}
					
					if(j==0)
						i++;
				}
				stringBuilder.append("</tr>");
				
				if(stringBuilderDescripcion != null) {
					stringBuilder.append("<tr>");
					stringBuilder.append(stringBuilderDescripcion.toString());
					stringBuilder.append("</tr>");
				}
			}
			
			stringBuilder.append("</table>");
		}
		
		return stringBuilder.toString();
	}
	
	public String getTablaCobertura() {
		StringBuilder stringBuilder = new StringBuilder();
		if(listaInterseccionesCobertura != null && !listaInterseccionesCobertura.isEmpty()) {
			stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:10px;\">");
			stringBuilder.append("<tr><th>Tipo de cobertura vegetal y uso de suelo</th><th>Superficie total de la cobertura vegetal y uso de suelo (ha)</th>"
					+ "<th>Porcentaje de intersección con el proyecto, obra o actividad</th></tr>");
			for (DetalleInterseccionProyectoAmbiental item : listaInterseccionesCobertura) {
				stringBuilder.append("<tr>");
				stringBuilder.append("<td>" + item.getNombreGeometria() + "</td>");
				stringBuilder.append("<td style=\"text-align: center;\">");
				stringBuilder.append(String.format("%.2f", item.getPorcentajeAreaInterseccion() * proyectoLicenciaCoa.getSuperficie().doubleValue()));
				stringBuilder.append("</td>");
				stringBuilder.append("<td style=\"text-align: center;\">");
				stringBuilder.append((item.getPorcentajeAreaInterseccion() * 100) + "%");
				stringBuilder.append("</td>");
				stringBuilder.append("</tr>");
			}
			stringBuilder.append("</table>");
		}
		
		return stringBuilder.toString();
	}
	
	public String getTablaEcosistemas() {
		StringBuilder stringBuilder = new StringBuilder();
		if(listaInterseccionesEcosistema != null && !listaInterseccionesEcosistema.isEmpty()) {
			stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:10px;\">");
			stringBuilder.append("<tr><th>Código del ecosistema</th>"
					+ "<th>Nombre del ecosistema</th>"
					+ "<th>Superficie total del ecosistema (ha)</th><th>Porcentaje de intersección con el proyecto, obra o actividad</th></tr>");
			for (DetalleInterseccionProyectoAmbiental item : listaInterseccionesEcosistema) {
				stringBuilder.append("<tr>");
				stringBuilder.append("<td>" + item.getCodigoConvenio() + "</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(item.getNombreGeometria());
				stringBuilder.append("</td>");
				stringBuilder.append("<td style=\"text-align: center;\">");
				stringBuilder.append(String.format("%.2f", item.getPorcentajeAreaInterseccion() * proyectoLicenciaCoa.getSuperficie().doubleValue()));
				stringBuilder.append("</td>");
				stringBuilder.append("<td style=\"text-align: center;\">");
				stringBuilder.append((item.getPorcentajeAreaInterseccion() * 100) + "%");
				stringBuilder.append("</td>");
				stringBuilder.append("</tr>");
			}
			stringBuilder.append("</table>");
		}
		
		return stringBuilder.toString();
	}
	
	public String getTablaUbicacionSitios() {
		StringBuilder stringBuilder = new StringBuilder();
		if(listaSitioMuestral != null && !listaSitioMuestral.isEmpty()) {
			stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:10px;\">");
			stringBuilder.append("<tr><th colspan=\"4\">Ubicación del sitio de referencia caracterización cualitativa</th></tr>");
			stringBuilder.append("<tr><th>Código o nombre del sitio</th><th>Coordenada X</th><th>Coordenada Y</th><th>Elevación (m.s.n.m.)</th></tr>");
			
			for (SitioMuestral item : listaSitioMuestral) {
				stringBuilder.append("<tr>");
				stringBuilder.append("<td style=\"text-align: center;\">" + item.getNombreSitio() + "</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(item.getCoordenadaSitioMuestral().getCoordenadaX());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(item.getCoordenadaSitioMuestral().getCoordenadaY());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(item.getElevacion());
				stringBuilder.append("</td>");
				stringBuilder.append("</tr>");
			}
			stringBuilder.append("</table>");
		}
		
		return stringBuilder.toString();
	}
	
	public String getTablaEspeciesCualitativa() {
		StringBuilder stringBuilder = new StringBuilder();
		if(listaCaracterizacionCualitativaEspecies != null && !listaCaracterizacionCualitativaEspecies.isEmpty()) {
			stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:10px;\">");
			stringBuilder.append("<tr><th colspan=\"9\">Registro cualitativo de especies del área de estudio</th></tr>");
			stringBuilder.append("<tr><th>Código o nombre del sitio</th><th>Familia</th><th>Género</th><th>Especie</th>"
					+ "<th>Frecuencia</th><th>Estado de conservación UICN</th><th>Apéndice CITES</th>"
					+ "<th>Aprovechamiento condicionado</th><th>Especies endémicas</th></tr>");
			for (EspeciesInformeForestal registro : listaCaracterizacionCualitativaEspecies) {
				stringBuilder.append("<tr>");
				stringBuilder.append("<td>");
				stringBuilder.append(registro.getSitioMuestral().getNombreSitio());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(registro.getFamiliaEspecie().getHiclScientificName());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(registro.getGeneroEspecie().getHiclScientificName());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append((registro.getEspecie() != null) ? registro.getEspecie().getSptaScientificName() : "");
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(registro.getFrecuencia());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(registro.getEstadoConservacion().getNombre());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(registro.getEstadoCites().getNombre());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append((registro.getAprovechamientoCondicionado() != null) ? (registro.getAprovechamientoCondicionado() ? "SI" : "NO") : "");
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append((registro.getEspecieEndemica() != null) ? (registro.getEspecieEndemica() ? "SI" : "NO") : "");
				stringBuilder.append("</td>");
				stringBuilder.append("</tr>");
			}
			stringBuilder.append("</table>");
		}
		
		return stringBuilder.toString();
	}
	
	public List<String> getTablaAfectacionForestal() {
		List<String> resultadoAfectacion = new ArrayList<>();
		
		StringBuilder stringBuilderActividades = new StringBuilder();
		StringBuilder stringBuilderAfectaciones = new StringBuilder();
		
		if(listaAfectacionForestal != null && !listaAfectacionForestal.isEmpty()) {
			stringBuilderActividades.append("<table border=\"1\" cellpadding=\"1\" cellspacing=\"0\" style=\"width: 700px; border-collapse:collapse;font-size:9px;\">");
			stringBuilderActividades.append("<tr><th rowspan=\"2\">Actividad</th>"
					+ "<th colspan=\"4\">Ubicación del proyecto de acuerdo a la Zonificación del BVP</th>"
					+ "<th rowspan=\"2\">Ecosistemas (afectados)</th>"
					+ "<th rowspan=\"2\">Cobertura Vegetal (afectados)</th>"
					+ "<th rowspan=\"2\">Convenios (afectados)</th>"
					+ "<th rowspan=\"2\">Unidad Hidrográfica (afectados)</th>"
					+ "<th colspan=\"4\">Especies Forestales</th>");
			stringBuilderActividades.append("<tr><th>ZPP</th>"
					+ "<th>ZMBN</th>" + "<th>ZR</th>" + "<th>ZOU</th>" 
					+ "<th>Estado de conservación UICN</th>"+ "<th>Apéndice CITES</th>"+ "<th>Aprovechamiento condicionado</th>"+ "<th>Especies endémicas</th>"
					+ "</tr>");
			
			stringBuilderAfectaciones.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:10px;\">");
			stringBuilderAfectaciones.append("<tr><th>Actividad</th>"
					+ "<th>Descripción de la posible afectación al Patrimonio Forestal Nacional</th>"
					+ "<th>Medida propuesta</th></tr>");
			for (AfectacionForestal item : listaAfectacionForestal) {
				stringBuilderActividades.append("<tr>");
				stringBuilderActividades.append("<td>" + item.getActividad() + "</td>");
				stringBuilderActividades.append("<td style=\"text-align: center;\">" + getTextoDesdeBoolean(item.getZppZonaProteccion(), 1) + "</td>");
				stringBuilderActividades.append("<td style=\"text-align: center;\">" + getTextoDesdeBoolean(item.getZmbnZonaManejoBosque(), 1) + "</td>");
				stringBuilderActividades.append("<td style=\"text-align: center;\">" + getTextoDesdeBoolean(item.getZrZonaRecuperacion(), 1) + "</td>");
				stringBuilderActividades.append("<td style=\"text-align: center;\">" + getTextoDesdeBoolean(item.getZouZonaOtrosUsos(), 1) + "</td>");
				stringBuilderActividades.append("<td>" + item.getNombresEcosistemas() + "</td>");
				stringBuilderActividades.append("<td>" + item.getNombresCoberturaVegetal() + "</td>");
				stringBuilderActividades.append("<td>" + item.getNombresConvenio() + "</td>");
				stringBuilderActividades.append("<td>" + item.getNombresUnidadHidrograficas() + "</td>");
				stringBuilderActividades.append("<td style=\"text-align: center;\">" + getTextoDesdeBoolean(item.getEsUicn(), 2) + "</td>");
				stringBuilderActividades.append("<td style=\"text-align: center;\">" + getTextoDesdeBoolean(item.getEsCites(), 2) + "</td>");
				stringBuilderActividades.append("<td style=\"text-align: center;\">" + getTextoDesdeBoolean(item.getEsAprovechamientoCondicionado(), 2) + "</td>");
				stringBuilderActividades.append("<td style=\"text-align: center;\">" + getTextoDesdeBoolean(item.getEsEndemica(), 2) + "</td>");
				stringBuilderActividades.append("</tr>");
				
				stringBuilderAfectaciones.append("<tr>");
				stringBuilderAfectaciones.append("<td>" + item.getActividad() + "</td>");
				stringBuilderAfectaciones.append("<td>");
				stringBuilderAfectaciones.append(item.getDescripcionAfectacion() != null ? item.getDescripcionAfectacion() : "");
				stringBuilderAfectaciones.append("</td>");
				stringBuilderAfectaciones.append("<td>");
				stringBuilderAfectaciones.append(item.getMedidaPropuesta() != null ? item.getMedidaPropuesta() : "");
				stringBuilderAfectaciones.append("</td>");
				stringBuilderAfectaciones.append("</tr>");
			}
			stringBuilderActividades.append("</table>");
			stringBuilderAfectaciones.append("</table>");
		}
		
		resultadoAfectacion.add(stringBuilderActividades.toString());
		resultadoAfectacion.add(stringBuilderAfectaciones.toString());
		
		return resultadoAfectacion;
	}
	
	public String getTablaUbicacionCenso() {
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append(getTablaCoordenadas(coordenadasGeograficas, true));
		stringBuilder.append("<br />");
		stringBuilder.append(getTablaUbicacionProyecto());
		
		return stringBuilder.toString();
	}
	/*
	public String getTablaAreaBasalCenso() {
		StringBuilder stringBuilder = new StringBuilder();
		if(listaEspeciesCenso != null && !listaEspeciesCenso.isEmpty()) {
			stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 700px; border-collapse:collapse;font-size:10px;\">");
			stringBuilder.append("<tr><th colspan=\"11\">Tabla de cálculo del área basal y volumen</th></tr>");
			stringBuilder.append("<tr><th rowspan=\"2\">N° de individuo</th><th colspan=\"3\">Taxonomí­a</th><th rowspan=\"2\">Nombre común</th>"
					+ "<th rowspan=\"2\">Diámetro de la altura del pecho DAP (m)</th>"
					+ "<th rowspan=\"2\">Altura Total HT (m)</th><th rowspan=\"2\">Altura comercial HC (m)</th><th rowspan=\"2\">Área basal AB (m2)</th>"
					+ "<th rowspan=\"2\">Volumen total Vt (m3)</th><th rowspan=\"2\">Volumen comercial Vc (m3)</th></tr>");
			
			stringBuilder.append("<tr><th>Familia</th><th>Género</th><th>Especie</th></tr>");
			
			int nro = 1;
			for (EspeciesInformeForestal registro : listaEspeciesCenso) {
				stringBuilder.append("<tr>");
				stringBuilder.append("<td>" + nro + "</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(registro.getFamiliaEspecie().getHiclScientificName());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(registro.getGeneroEspecie().getHiclScientificName());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append((registro.getEspecie() != null) ? registro.getEspecie().getSptaScientificName() : "");
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(registro.getNombreComun());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(registro.getAlturaDap());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(registro.getAlturaTotal());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(registro.getAlturaComercial());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(registro.getAreaBasal());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(registro.getVolumenTotal());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(registro.getVolumenComercial());
				stringBuilder.append("</td>");
				stringBuilder.append("</tr>");
				
				nro++;
			}
			stringBuilder.append("</table>");
		}
		
		return stringBuilder.toString();
	}
	*/
	public String getTablaEspeciesImportanciaCensoMuestreo(List<EspeciesImportanciaForestal> listaEspeciesImportancia) {
		StringBuilder stringBuilder = new StringBuilder();
		if(listaEspeciesImportancia != null && !listaEspeciesImportancia.isEmpty()) {
			stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:10px;\">");
			stringBuilder.append("<tr><th colspan=\"7\">Tabla de especies de importancia</th></tr>");
			stringBuilder.append("<tr><th>Familia</th><th>Género</th><th>Especie</th>"
					+ "<th>Estado de conservación UICN</th><th>Apéndice CITES</th>"
					+ "<th>Aprovechamiento condicionado</th><th>Especies endémicas</th></tr>");
			for (EspeciesImportanciaForestal registro : listaEspeciesImportancia) {
				stringBuilder.append("<tr>");
				stringBuilder.append("<td>");
				stringBuilder.append(registro.getFamiliaEspecie().getHiclScientificName());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(registro.getGeneroEspecie().getHiclScientificName());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append((registro.getEspecie() != null) ? registro.getEspecie().getSptaScientificName() : "");
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(registro.getEstadoConservacion().getNombre());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(registro.getEstadoCites().getNombre());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append((registro.getAprovechamientoCondicionado() != null) ? (registro.getAprovechamientoCondicionado() ? "SI" : "NO") : "");
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append((registro.getEspecieEndemica() != null) ? (registro.getEspecieEndemica() ? "SI" : "NO") : "");
				stringBuilder.append("</td>");
				stringBuilder.append("</tr>");
			}
			stringBuilder.append("</table>");
		}
		
		return stringBuilder.toString();
	}
	
	public String getTablaIviCenso() {
		StringBuilder stringBuilder = new StringBuilder();
		if(listaIndiceValorImportancia != null && !listaIndiceValorImportancia.isEmpty()) {
			stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 700px; border-collapse:collapse;font-size:10px;\">");
			stringBuilder.append("<tr><th colspan=\"9\">Tabla de índice de valor de importancia</th></tr>");
			stringBuilder.append("<tr><th>Familia</th><th>Género</th><th>Especie</th><th>Nombre común</th>"
					+ "<th>Frecuencia</th><th>Área basal AB (m2)</th>"
					+ "<th>DnR</th><th>DmR</th>"
					+ "<th>Índice de valor de importancia IVI (DnR + DmR) / 2</th></tr>");

			for (AnalisisResultadoForestal item : listaIndiceValorImportancia) {
				stringBuilder.append("<tr>");
				stringBuilder.append("<td style=\"font-size:8px;\">");
				stringBuilder.append(item.getGeneroEspecie().getHiclIdParent().getHiclScientificName());
				stringBuilder.append("</td>");
				stringBuilder.append("<td style=\"font-size:8px;\">");
				stringBuilder.append(item.getGeneroEspecie().getHiclScientificName());
				stringBuilder.append("</td>");
				stringBuilder.append("<td style=\"font-size:8px;\">");
				stringBuilder.append((item.getEspecie() != null) ? item.getEspecie().getSptaScientificName() : "");
				stringBuilder.append("</td>");
				stringBuilder.append("<td style=\"font-size:8px;\">");
				stringBuilder.append(item.getNombreComun());
				stringBuilder.append("</td>");
				stringBuilder.append("<td style=\"font-size:8px;\">");
				stringBuilder.append(item.getFrecuencia());
				stringBuilder.append("</td>");
				stringBuilder.append("<td style=\"font-size:8px;\">");
				stringBuilder.append(item.getAreaBasal());
				stringBuilder.append("</td>");
				stringBuilder.append("<td style=\"font-size:8px;\">");
				stringBuilder.append(item.getValorDnr());
				stringBuilder.append("</td>");
				stringBuilder.append("<td style=\"font-size:8px;\">");
				stringBuilder.append(item.getValorDmr());
				stringBuilder.append("</td>");
				stringBuilder.append("<td style=\"font-size:8px;\">");
				stringBuilder.append(item.getValorIvi());
				stringBuilder.append("</td>");
				stringBuilder.append("</tr>");
			}
			stringBuilder.append("</table>");
			
			stringBuilder.append("<br />");
			stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:10px;\">");
			stringBuilder.append("<tr><th>Total de DnR</th><td>" + sitioMuestralCenso.getTotalDnr() + "</td>"
					+ "<th>Total de DmR</th><td>" + sitioMuestralCenso.getTotalDmr() + "</td>"
					+ "<th>Total de IVI</th><td>" + sitioMuestralCenso.getTotalIvi() + "</td></tr>");
			stringBuilder.append("</table>");
			
			String valorInversoSimpson = (sitioMuestralCenso.getIndiceSimpson().equals(0.0)) ? "Indefinido" : sitioMuestralCenso.getInversoSimpson().toString();
			stringBuilder.append("<br />");
			stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:10px;\">");
			stringBuilder.append("<tr><th>Índice de Shannon</th><td>" + sitioMuestralCenso.getIndiceShanon() + "</td>"
					+ "<th>Índice de Simpson</th><td>" + sitioMuestralCenso.getIndiceSimpson() + "</td></tr>");
			stringBuilder.append("<tr><th>Exponencial de Shannon</th><td>" + sitioMuestralCenso.getExponencialShannon() + "</td>"
					+ "<th>Inverso de Simpson</th><td>" + valorInversoSimpson + "</td></tr>");
			stringBuilder.append("</table>");
		}
		
		return stringBuilder.toString();
	}
	
	public String getTablaUbicacionMuestreo() {
		StringBuilder stringBuilder = new StringBuilder();
		if(listaSitioMuestreo != null && !listaSitioMuestreo.isEmpty()) {
			stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 700px; border-collapse:collapse;font-size:10px;\">");
			stringBuilder.append("<tr><th colspan=\"10\">Ubicación de las unidades muestrales</th></tr>");
			stringBuilder.append("<tr><th rowspan=\"2\">Código de la Unidad Muestral (muestreo)</th><th rowspan=\"2\">Coordenada X</th>"
					+ "<th rowspan=\"2\">Coordenada Y</th><th rowspan=\"2\">Provincia</th><th rowspan=\"2\">Cantón</th><th rowspan=\"2\">Parroquia</th>"
					+ "<th rowspan=\"2\">Elevación (m.s.n.m.)</th>"
					+ "<th colspan=\"2\">Dimensiones de la Unidad Muestral(muestreo)</th><th rowspan=\"2\">Superficie de la Unidad Muestral</th></tr>");
			
			stringBuilder.append("<tr><th>Largo</th>"
					+ "<th>Ancho</th></tr>");
			
			for (SitioMuestral item : listaSitioMuestreo) {
				stringBuilder.append("<tr>");
				stringBuilder.append("<td>" + item.getNombreSitio() + "</td>");
				stringBuilder.append("<td>" + item.getCoordenadasX() + "</td>");
				stringBuilder.append("<td>" + item.getCoordenadasY() + "</td>");
				stringBuilder.append("<td>" + item.getProvincia() + "</td>");
				stringBuilder.append("<td>" + item.getCanton() + "</td>");
				stringBuilder.append("<td>" + item.getParroquia() + "</td>");
				stringBuilder.append("<td>" + item.getElevacion() + "</td>");
				stringBuilder.append("<td>" + item.getLargo() + "</td>");
				stringBuilder.append("<td>" + item.getAncho() + "</td>");
				stringBuilder.append("<td>" + item.getAreaSitio() + "</td>");
				
				stringBuilder.append("</tr>");
			}
			stringBuilder.append("</table>");
		}
		
		return stringBuilder.toString();
	}
	
	public String getTablaAreaBasalMuestreo() {
		StringBuilder stringBuilder = new StringBuilder();
		if(listaEspeciesMuestreo != null && !listaEspeciesMuestreo.isEmpty()) {
			stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 700px; border-collapse:collapse;font-size:8px;\">");
			stringBuilder.append("<tr><th colspan=\"12\">Tabla de cálculo del área basal y volumen</th></tr>");
			stringBuilder.append("<tr><th rowspan=\"2\">Código de la Unidad Muestral</th><th rowspan=\"2\">N° de individuo</th>"
					+ "<th colspan=\"3\">Taxonomí­a</th><th rowspan=\"2\">Nombre común</th>"
					+ "<th rowspan=\"2\">Diámetro de la altura del pecho DAP (m)</th>"
					+ "<th rowspan=\"2\">Altura Total HT (m)</th><th rowspan=\"2\">Altura comercial HC (m)</th><th rowspan=\"2\">Área basal AB (m2)</th>"
					+ "<th rowspan=\"2\">Volumen total Vt (m3)</th><th rowspan=\"2\">Volumen comercial Vc (m3)</th></tr>");
			
			stringBuilder.append("<tr><th>Familia</th><th>Género</th><th>Especie</th></tr>");
			
			int nro = 1;
			for (EspeciesInformeForestal registro : listaEspeciesMuestreo) {
				stringBuilder.append("<tr>");
				stringBuilder.append("<td>" + registro.getSitioMuestral().getNombreSitio() + "</td>");
				stringBuilder.append("<td>" + nro + "</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(registro.getFamiliaEspecie().getHiclScientificName());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(registro.getGeneroEspecie().getHiclScientificName());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append((registro.getEspecie() != null) ? registro.getEspecie().getSptaScientificName() : "");
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(registro.getNombreComun());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(registro.getAlturaDap());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(registro.getAlturaTotal());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(registro.getAlturaComercial());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(registro.getAreaBasal());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(registro.getVolumenTotal());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(registro.getVolumenComercial());
				stringBuilder.append("</td>");
				stringBuilder.append("</tr>");
				
				nro++;
			}
			stringBuilder.append("</table>");
		}
		
		return stringBuilder.toString();
	}
	
	
	public String getTablaSumatoriasMuestreo() {
		StringBuilder stringBuilder = new StringBuilder();
		listaSitioMuestral = sitioMuestralFacade.getSitiosPorInformeTipo(informeFactivilidadForestal.getId(), SitioMuestral.registroCuantitativoMuestreo );
		if(listaSitioMuestral != null && !listaSitioMuestral.isEmpty()) {
			stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:10px;\">");
			stringBuilder.append("<tr><th>Código de la Unidad Muestral</th><th>Sumatoria del área basal de cada sitio de muestreo</th>"
					+ "<th>Sumatoria del volumen total de cada sitio de muestreo</th>"
					+ "<th>Sumatoria del volumen comercial de cada sitio de muestreo</th></tr>");

			
			for (SitioMuestral item : listaSitioMuestral) {
				stringBuilder.append("<tr>");
				stringBuilder.append("<td>" + item.getNombreSitio() + "</td>");
				stringBuilder.append("<td>" + item.getSumatoriaAreaBasal() + "</td>");
				stringBuilder.append("<td>" + item.getSumatoriaVolumenTotal() + "</td>");
				stringBuilder.append("<td>" + item.getSumatoriaVolumenComercial() + "</td>");
				
				stringBuilder.append("</tr>");
			}
			stringBuilder.append("</table>");
		}
		
		return stringBuilder.toString();
	}
	
	public String getInfoIviPorSitiosMuestrales() {
		StringBuilder stringBuilder = new StringBuilder();
		
		if(listaSitioMuestreo != null && !listaSitioMuestreo.isEmpty()) {
			stringBuilder.append("Índice de valor de importancia de las unidades muestrales");
			
			int sitios = 0;
			for (SitioMuestral sitio : listaSitioMuestreo) {
				if(sitio.getListaEspecieValorImportancia() != null && !sitio.getListaEspecieValorImportancia().isEmpty()) {
					if(sitios  > 0) {
						stringBuilder.append("<br />");
					}
					
					stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 700px; border-collapse:collapse;font-size:10px;\">");
					stringBuilder.append("<tr><th colspan=\"9\">Tabla de índice de valor de importancia para " + sitio.getNombreSitio()+ "</th></tr>");
					stringBuilder.append("<tr><th>Familia</th><th>Género</th><th>Especie</th><th>Nombre común</th>"
							+ "<th>Frecuencia</th><th>Área basal AB (m2)</th>"
							+ "<th>DnR</th><th>DmR</th>"
							+ "<th>Índice de valor de importancia IVI (DnR + DmR) / 2</th></tr>");

					for (AnalisisResultadoForestal item : sitio.getListaEspecieValorImportancia()) {
						stringBuilder.append("<tr>");
						stringBuilder.append("<td>");
						stringBuilder.append(item.getGeneroEspecie().getHiclIdParent().getHiclScientificName());
						stringBuilder.append("</td>");
						stringBuilder.append("<td>");
						stringBuilder.append(item.getGeneroEspecie().getHiclScientificName());
						stringBuilder.append("</td>");
						stringBuilder.append("<td>");
						stringBuilder.append((item.getEspecie() != null) ? item.getEspecie().getSptaScientificName() : "");
						stringBuilder.append("</td>");
						stringBuilder.append("<td>");
						stringBuilder.append(item.getNombreComun());
						stringBuilder.append("</td>");
						stringBuilder.append("<td>");
						stringBuilder.append(item.getFrecuencia());
						stringBuilder.append("</td>");
						stringBuilder.append("<td>");
						stringBuilder.append(item.getAreaBasal());
						stringBuilder.append("</td>");
						stringBuilder.append("<td>");
						stringBuilder.append(item.getValorDnr());
						stringBuilder.append("</td>");
						stringBuilder.append("<td>");
						stringBuilder.append(item.getValorDmr());
						stringBuilder.append("</td>");
						stringBuilder.append("<td>");
						stringBuilder.append(item.getValorIvi());
						stringBuilder.append("</td>");
						stringBuilder.append("</tr>");
					}
					stringBuilder.append("</table>");
					
					stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 700px; border-collapse:collapse;font-size:10px;\">");
					stringBuilder.append("<tr><th>Total de DnR</th><td>" + sitio.getTotalDnr() + "</td>"
							+ "<th>Total de DmR</th><td>" + sitio.getTotalDmr() + "</td>"
							+ "<th>Total de IVI</th><td>" + sitio.getTotalIvi() + "</td></tr>");
					stringBuilder.append("</table>");
					
					String valorInversoSimpson = (sitio.getIndiceSimpson().equals(0.0)) ? "Indefinido" : sitio.getInversoSimpson().toString();
					stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 700px; border-collapse:collapse;font-size:10px;\">");
					stringBuilder.append("<tr><th>Índice de Shannon</th><td>" + sitio.getIndiceShanon() + "</td>"
							+ "<th>Índice de Simpson</th><td>" + sitio.getIndiceSimpson() + "</td></tr>");
					stringBuilder.append("<tr><th>Exponencial de Shannon</th><td>" + sitio.getExponencialShannon() + "</td>"
							+ "<th>Inverso de Simpson</th><td>" + valorInversoSimpson + "</td></tr>");
					stringBuilder.append("</table>");
					
					sitios++;
				}
				
			}
		}
		
		if(listaIndiceValorImportanciaCenso != null && !listaIndiceValorImportanciaCenso.isEmpty()) {
			
		}
		
		return stringBuilder.toString();
	}
	
	public String getTextoDesdeArray(String informacion) {
		String datos = "";
		if(informacion != null && !informacion.isEmpty()) {
			String[] infoArray = informacion.split(";");
			
			if(infoArray.length > 1) {
				for (int i = 0; i < infoArray.length; i++) {
					datos = (datos .equals("")) ? infoArray[i] : "<br />" + infoArray[i];
				}
			} else {
				datos = infoArray[0];
			}
		}
		
		return datos;
	}
	
	public String getTextoDesdeBoolean(Boolean opcion, Integer tipo) {
		String datos = "";
		
		if(opcion != null) {
			switch (tipo) {
			case 1:
				datos = (opcion) ? "X" : "";
				break;
			case 2:
				datos = (opcion) ? "SI" : "NO";
				break;
			default:
				break;
			}
		}
		
		return datos;
	}
	
	public String getTablaAnexos() throws Exception {
		StringBuilder stringBuilder = new StringBuilder();
		if(listaDocumentosAnexos != null && listaDocumentosAnexos.size() > 0) {
			
			String tamanioTabla = "width: 90%;";
			
			stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\""+tamanioTabla+" border-collapse:collapse;font-size:10px;\">");
			
			int totalAnexos = 0;
			for (int i = 0; i < listaDocumentosAnexos.size(); i++) {
				DocumentoViabilidad foto = listaDocumentosAnexos.get(i);
				
				if(foto.getMime().contains("jpg") && totalAnexos <= 10){
					Timestamp timestamp = new Timestamp(System.currentTimeMillis());
					
					if(foto.getContenidoDocumento() == null) {
						foto.setContenidoDocumento(documentosViabilidadFacade.descargar(foto.getIdAlfresco()));
					}
					
					ByteArrayInputStream inputStreamFirma = new ByteArrayInputStream(foto.getContenidoDocumento());
					BufferedImage bImageFirma = ImageIO.read(inputStreamFirma);
					File imageFile = new File(timestamp.getTime() +"_" + foto.getNombre());
					ImageIO.write(bImageFirma, "jpg", imageFile);
					
					stringBuilder.append("<tr>");
					stringBuilder.append("<td style=\"text-align: center;\">");
					stringBuilder.append("<img src=\'" + imageFile.getPath() + "\' height=\'50%\' width=\'50%\' ></img>");
					stringBuilder.append("</td>");
					stringBuilder.append("</tr>");
					
					stringBuilder.append("<tr>");
					stringBuilder.append("<td style=\"text-align: justify;\">");
					stringBuilder.append((i + 1) + ". " + foto.getDescripcionAnexo());
					stringBuilder.append("</td>");
					stringBuilder.append("</tr>");
					
					filesFotos.add(imageFile);
					totalAnexos++;
				}
			}
			
			if(totalAnexos > 0){
				stringBuilder.append("</table>");
			} else {
				stringBuilder = new StringBuilder();
			}
		}
		
		return stringBuilder.toString();
	}
	
	
	public void guardarInforme() {
		try {
			guardarDatosInforme();
			
			informeGuardado = true;
			
			RequestContext.getCurrentInstance().update("form");
			
			JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public void guardarDatosInforme() throws Exception {
		
		if (viabilidadProyecto == null) {
			viabilidadProyecto = new ViabilidadCoa();
			viabilidadProyecto.setIdProyectoLicencia(idProyecto);
			viabilidadProyecto.setIdProyectoTipoViabilidad(proyectoTipoViabilidadCoa.getId());
			viabilidadProyecto.setEsViabilidadSnap(false);
			viabilidadProyecto.setAreaResponsable(areaTramiteViabilidad);
			viabilidadProyecto.setViabilidadCompletada(false);
			viabilidadProyecto.setNroZonalesInterseccion(recuperarNroZonales());
			viabilidadProyecto.setTipoFlujoViabilidad(ViabilidadCoa.flujosIndependientes);

			viabilidadCoaFacade.guardar(viabilidadProyecto);
		} else {
			viabilidadProyecto.setAreaResponsable(areaTramiteViabilidad);
			viabilidadProyecto.setTipoFlujoViabilidad(ViabilidadCoa.flujosIndependientes);

			viabilidadCoaFacade.guardar(viabilidadProyecto);
		}
		
		informeFactivilidadForestal.setIdViabilidad(viabilidadProyecto.getId());

		informeFactibilidadForestalFacade.guardar(informeFactivilidadForestal);
		
		if(!listaInterseccionUHidrograficas.isEmpty()) {
			for (UnidadHidrografica unidad : listaInterseccionUHidrograficas) {
				unidad.setIdInformeFactibilidad(informeFactivilidadForestal.getId());
				unidadHidrograficaFacade.guardar(unidad);
			}
		}
		
		if(!listaInterseccionUHidrograficasEliminar.isEmpty()){
			for (UnidadHidrografica unidad : listaInterseccionUHidrograficasEliminar) {
				unidad.setEstado(false);
				unidadHidrograficaFacade.guardar(unidad);
			}
		}
		listaInterseccionUHidrograficasEliminar = new ArrayList<> ();
		
		if(consultorPrincipal != null && consultorPrincipal.getConsultor() != null && consultorPrincipal.getConsultor().getCedula() != null) {
			ConsultorForestal consultor =consultorPrincipal.getConsultor(); 
			if(consultor.getId() == null) {
				consultor = consultorForestalFacade.guardar(consultor);
			}
			
			consultorPrincipal.setConsultor(consultor);
			consultorPrincipal.setInformeFactibilidad(informeFactivilidadForestal);
			equipoConsultorForestalFacade.guardar(consultorPrincipal);
		}
		
		if(!listaEquipoConsultor.isEmpty()) {
			for (EquipoConsultorForestal item : listaEquipoConsultor) {
				if(item != null && item.getConsultor() != null && item.getConsultor().getCedula() != null) {
					ConsultorForestal consultor =item.getConsultor(); 
					if(consultor.getId() == null) {
						consultor = consultorForestalFacade.guardar(consultor);
					}
					
					item.setConsultor(consultor);
					item.setInformeFactibilidad(informeFactivilidadForestal);
					equipoConsultorForestalFacade.guardar(item);
				}
			}
		}
		
		if(!listaEquipoConsultorEliminar.isEmpty()){
			for (EquipoConsultorForestal item : listaEquipoConsultorEliminar) {
				item.setEstado(false);
				equipoConsultorForestalFacade.guardar(item);
			}
		}
		
		if(!listaFotoArchivo.isEmpty()) {
			for (DocumentoViabilidad fotoProyecto : listaFotoArchivo) {
				if(fotoProyecto.getId() == null) {
					fotoProyecto.setIdViabilidad(viabilidadProyecto.getId());
					fotoProyecto.setIdTipoDocumento(TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_FOTO_GENERAL_PROYECTO.getIdTipoDocumento());
					fotoProyecto = documentosViabilidadFacade.guardarDocumento(proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
							  "VIABILIDAD_AMBIENTAL", fotoProyecto, 1);
				}
			}
		}

		if(!listaFotoArchivoEliminar.isEmpty()){
			for (DocumentoViabilidad item : listaFotoArchivoEliminar) {
				item.setEstado(false);
				documentosViabilidadFacade.guardar(item);
			}
		}

		if(!listaDocumentoAdjunto.isEmpty()) {
			for (DocumentoViabilidad fotoProyecto : listaDocumentoAdjunto) {
				if(fotoProyecto.getId() == null) {
					fotoProyecto.setIdViabilidad(viabilidadProyecto.getId());
					fotoProyecto.setIdTipoDocumento(TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_ADJUNTO_GENERAL_PROYECTO.getIdTipoDocumento());
					fotoProyecto = documentosViabilidadFacade.guardarDocumento(proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
							  "VIABILIDAD_AMBIENTAL", fotoProyecto, 1);
				}
			}
		}

		if(!listaDocumentoAdjuntoEliminar.isEmpty()){
			for (DocumentoViabilidad item : listaDocumentoAdjuntoEliminar) {
				item.setEstado(false);
				documentosViabilidadFacade.guardar(item);
			}
		}

		if(!listaImagenesSatelite.isEmpty()) {
			for (DocumentoViabilidad fotoProyecto : listaImagenesSatelite) {
				if(fotoProyecto.getId() == null) {
					fotoProyecto.setIdViabilidad(viabilidadProyecto.getId());
					fotoProyecto.setIdTipoDocumento(TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_FOTO_COBERTURA_PROYECTO.getIdTipoDocumento());
					fotoProyecto = documentosViabilidadFacade.guardarDocumento(proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
							  "VIABILIDAD_AMBIENTAL", fotoProyecto, 1);
				}
			}
		}

		if(!listaImagenesSateliteEliminar.isEmpty()){
			for (DocumentoViabilidad item : listaImagenesSateliteEliminar) {
				item.setEstado(false);
				documentosViabilidadFacade.guardar(item);
			}
		}
		
		//CUALITATIVA
		if(informeFactivilidadForestal.getCaracterizacionCobertura() != null 
				&& informeFactivilidadForestal.getCaracterizacionCobertura().equals(InformeFactibilidadForestal.caracterizacionCualitativa)) {
			
			for (SitioMuestral sitio : listaSitioMuestral) {
				sitio.setTipoSitio(SitioMuestral.registroCualitativo);
				sitio.setIdInformeFactibilidad(informeFactivilidadForestal.getId());
				sitioMuestralFacade.guardar(sitio);
				
				CoordenadaSitioMuestral coordenadas = sitio.getCoordenadaSitioMuestral();
				coordenadas.setSitioMuestral(sitio);
				coordenadas.setOrden(1);
				coordenadaSitioMuestralFacade.guardar(coordenadas);
				
				sitio.setCoordenadaSitioMuestral(coordenadas);
			}

			if(!listaSitioMuestralEliminar.isEmpty()){
				for (SitioMuestral item : listaSitioMuestralEliminar) {
					item.setEstado(false);
					sitioMuestralFacade.guardar(item);
				}
			}

			if(!listaCaracterizacionCualitativaEspecies.isEmpty()) {
				for (EspeciesInformeForestal especie : listaCaracterizacionCualitativaEspecies) {
					if(especie.getId() == null) {
						for (SitioMuestral sitio : listaSitioMuestral) {
							if(sitio.getNombreSitio().equals(especie.getSitioMuestral().getNombreSitio())) {
								especie.setIdSitio(sitio.getId());
								break;
							}
						}
					}
					
					especiesInformeForestalFacade.guardar(especie);
				}
			}

			if(!listaCaracterizacionCualitativaEspeciesEliminar.isEmpty()){
				for (EspeciesInformeForestal item : listaCaracterizacionCualitativaEspeciesEliminar) {
					item.setEstado(false);
					especiesInformeForestalFacade.guardar(item);
				}
			}

			//ELIMINACION DE INFORMACION CUANTITATIVA			
						
			if(!listaFotografiaCuantitativa.isEmpty()){
				for (DocumentoViabilidad item : listaFotografiaCuantitativa) {
					item.setEstado(false);
					documentosViabilidadFacade.guardar(item);
				}
			}	
			listaFotografiaCuantitativa = new ArrayList<DocumentoViabilidad>();
			
			if(!listaFotografiaOficina.isEmpty()){
				for (DocumentoViabilidad item : listaFotografiaOficina) {
					item.setEstado(false);
					documentosViabilidadFacade.guardar(item);
				}
			}
			listaFotografiaOficina = new ArrayList<DocumentoViabilidad>();
			
			if (informeFactivilidadForestal.getRecoleccionDatos() != null && 
					informeFactivilidadForestal.getRecoleccionDatos().equals(InformeFactibilidadForestal.recoleccionMuestreo)) {

				if (!listaSitioMuestreo.isEmpty()) {
					for (SitioMuestral sitioEliminar : listaSitioMuestreo) {
						if (sitioEliminar.getId() != null) {
							sitioEliminar.setEstado(false);
							sitioMuestralFacade.guardar(sitioEliminar);
						}
					}
					listaSitioMuestreo = new ArrayList<SitioMuestral>();
					listaEspeciesMuestreo = new ArrayList<EspeciesInformeForestal>();
					listaSitioSumatoria = new ArrayList<SitioMuestral>();

				}

			}
			if (informeFactivilidadForestal.getRecoleccionDatos() != null
					&& informeFactivilidadForestal.getRecoleccionDatos().equals(InformeFactibilidadForestal.recoleccionCenso)) {

				if (sitioMuestralCenso != null && sitioMuestralCenso.getId() != null) {
					sitioMuestralCenso.setEstado(false);
					sitioMuestralFacade.guardar(sitioMuestralCenso);
					sitioMuestralCenso = new SitioMuestral();
					listaEspeciesCenso = new ArrayList<EspeciesInformeForestal>();
					especiesInformeForestalCenso = new EspeciesInformeForestal();
					listaIndiceValorImportancia = new ArrayList<AnalisisResultadoForestal>();
				}
			}	
			
			listaEspeciesImportancia = new ArrayList<EspeciesImportanciaForestal>();

		}
		
		//CUANTITATIVA
		if(informeFactivilidadForestal.getCaracterizacionCobertura() != null 
				&& informeFactivilidadForestal.getCaracterizacionCobertura().equals(InformeFactibilidadForestal.caracterizacionCuantitativa)) {
			
			if(!listaFotografiaCuantitativa.isEmpty()) {
				for (DocumentoViabilidad item : listaFotografiaCuantitativa) {
					if(item.getId() == null) {
						item.setIdViabilidad(viabilidadProyecto.getId());
						item.setIdTipoDocumento(TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_FOTO_FASE_CAMPO.getIdTipoDocumento());
						item = documentosViabilidadFacade.guardarDocumento(proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
								  "VIABILIDAD_AMBIENTAL", item, 1);
					}
				}
			}
			if(!listaFotografiaCuantitativaEliminar.isEmpty()){
				for (DocumentoViabilidad item : listaFotografiaCuantitativaEliminar) {
					item.setEstado(false);
					documentosViabilidadFacade.guardar(item);
				}
			}

			if(!listaFotografiaOficina.isEmpty()) {
				for (DocumentoViabilidad item : listaFotografiaOficina) {
					if(item.getId() == null) {
						item.setIdViabilidad(viabilidadProyecto.getId());
						item.setIdTipoDocumento(TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_FOTO_FASE_OFICINA.getIdTipoDocumento());
						item = documentosViabilidadFacade.guardarDocumento(proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
								  "VIABILIDAD_AMBIENTAL", item, 1);
					}
				}
			}
			if(!listaFotografiaOficinaEliminar.isEmpty()){
				for (DocumentoViabilidad item : listaFotografiaOficinaEliminar) {
					item.setEstado(false);
					documentosViabilidadFacade.guardar(item);
				}
			}
			
			//GUARDADO DATOS CENSO
			if (informeFactivilidadForestal.getRecoleccionDatos() != null
					&& informeFactivilidadForestal.getRecoleccionDatos().equals(InformeFactibilidadForestal.recoleccionCenso)) {
				if(sitioMuestralCenso.getId() == null) {
					sitioMuestralCenso.setNombreSitio("SITIO DEFAULT CENSO");
				}
				sitioMuestralCenso.setTipoSitio(SitioMuestral.registroCuantitativoCenso);
				sitioMuestralCenso.setIdInformeFactibilidad(informeFactivilidadForestal.getId());
				sitioMuestralFacade.guardar(sitioMuestralCenso);

				if(!listaEspeciesCenso.isEmpty()) {
					for (EspeciesInformeForestal especie : listaEspeciesCenso) {
						especie.setIdSitio(sitioMuestralCenso.getId());
						especiesInformeForestalFacade.guardar(especie);
					}
				}
				if(!listaEspeciesCensoEliminar.isEmpty()) {
					for (EspeciesInformeForestal especie : listaEspeciesCensoEliminar) {
						especie.setEstado(false);
						especiesInformeForestalFacade.guardar(especie);
					}
				}
				
				if(!listaEspeciesImportancia.isEmpty()) {
					for (EspeciesImportanciaForestal especie : listaEspeciesImportancia) {
						especie.setIdInformeFactibilidad(informeFactivilidadForestal.getId());
						especiesImportanciaForestalFacade.guardar(especie);
					}
				}
				if(!listaEspeciesImportanciaEliminar.isEmpty()) {
					for (EspeciesImportanciaForestal especie : listaEspeciesImportanciaEliminar) {
						especie.setEstado(false);
						especiesImportanciaForestalFacade.guardar(especie);
					}
				}
				
				if(!listaIndiceValorImportancia.isEmpty()) {
					calcularIndiceValorImportancia();
					for (AnalisisResultadoForestal item : listaIndiceValorImportancia) {
						item.setIdSitio(sitioMuestralCenso.getId());
						analisisResultadoForestalFacade.guardar(item);
					}
				}
				if(!listaIndiceValorImportanciaEliminar.isEmpty()) {
					for (AnalisisResultadoForestal item : listaIndiceValorImportanciaEliminar) {
						if(item.getId() != null) {
							item.setEstado(false);
							analisisResultadoForestalFacade.guardar(item);
						}
					}
				}
				
				//ELIMINACION
				if(!listaSitioMuestreo.isEmpty()){
					for(SitioMuestral sitioEliminar : listaSitioMuestreo){
						if(sitioEliminar.getId() != null){
							sitioEliminar.setEstado(false);
							sitioMuestralFacade.guardar(sitioEliminar);
						}
					}
					listaSitioMuestreo = new ArrayList<SitioMuestral>();
					listaEspeciesMuestreo = new ArrayList<EspeciesInformeForestal>();
					listaSitioSumatoria = new ArrayList<SitioMuestral>();
					
				}

			}

			//GUARDADO DATOS MUESTRO
			if (informeFactivilidadForestal.getRecoleccionDatos() != null
					&& informeFactivilidadForestal.getRecoleccionDatos().equals(InformeFactibilidadForestal.recoleccionMuestreo)) {
				if(!listaSitioMuestreo.isEmpty()) {
					for (SitioMuestral sitio : listaSitioMuestreo) {
						sitio.setTipoSitio(SitioMuestral.registroCuantitativoMuestreo);
						sitio.setIdInformeFactibilidad(informeFactivilidadForestal.getId());
						sitioMuestralFacade.guardar(sitio);
						
						int nro = 1;
						for (CoordenadaSitioMuestral coordenadas : sitio.getListaCoordenadas()) {
							coordenadas.setSitioMuestral(sitio);
							coordenadas.setOrden(nro);
							coordenadaSitioMuestralFacade.guardar(coordenadas);
							
							nro++;
						}
						
						for (UbicacionSitioMuestral ubicacion : sitio.getUbicacionesGeograficas()) {
							ubicacion.setIdSitio(sitio.getId());
							ubicacionSitioMuestralFacade.guardar(ubicacion);
						}
					}
				}

				if(!listaSitioMuestreoEliminar.isEmpty()) {
					for (SitioMuestral item : listaSitioMuestreoEliminar) {
						item.setEstado(false);
						sitioMuestralFacade.guardar(item);
					}
				}

				if(!listaEspeciesMuestreo.isEmpty()) {
					for (EspeciesInformeForestal especie : listaEspeciesMuestreo) {
						for (SitioMuestral sitio : listaSitioMuestreo) {
							if(especie.getSitioMuestral().getNombreSitio().equals(sitio.getNombreSitio())) {
								especie.setIdSitio(sitio.getId());
								especiesInformeForestalFacade.guardar(especie);
								break;
							}
						}
					}
				}
				if(!listaEspeciesMuestreoEliminar.isEmpty()) {
					for (EspeciesInformeForestal especie : listaEspeciesMuestreoEliminar) {
						especie.setEstado(false);
						especiesInformeForestalFacade.guardar(especie);
					}
				}

				if(!listaEspeciesImportancia.isEmpty()) {
					for (EspeciesImportanciaForestal especie : listaEspeciesImportancia) {
						especie.setIdInformeFactibilidad(informeFactivilidadForestal.getId());
						especiesImportanciaForestalFacade.guardar(especie);
					}
				}
				if(!listaEspeciesImportanciaEliminar.isEmpty()) {
					for (EspeciesImportanciaForestal especie : listaEspeciesImportanciaEliminar) {
						especie.setEstado(false);
						especiesImportanciaForestalFacade.guardar(especie);
					}
				}

				if(!listaSitioMuestreo.isEmpty()) {
					for (SitioMuestral sitio : listaSitioMuestreo) {
						if(sitio.getListaEspecieValorImportancia() != null && !sitio.getListaEspecieValorImportancia().isEmpty()) {
							calcularIndiceValorImportanciaMuestreo(sitio);
							for (AnalisisResultadoForestal item : sitio.getListaEspecieValorImportancia()) {
								item.setIdSitio(sitio.getId());
								analisisResultadoForestalFacade.guardar(item);
							}
						}
					}
				}
				if(!listaIndiceValorImportanciaEliminar.isEmpty()) {
					for (AnalisisResultadoForestal item : listaIndiceValorImportanciaEliminar) {
						if(item.getId() != null) {
							item.setEstado(false);
							analisisResultadoForestalFacade.guardar(item);
						}
					}
				}

				//ELIMINACION
				if(sitioMuestralCenso != null && sitioMuestralCenso.getId() != null){
					sitioMuestralCenso.setEstado(false);
					sitioMuestralFacade.guardar(sitioMuestralCenso);
					sitioMuestralCenso = new SitioMuestral();
					listaEspeciesCenso = new ArrayList<EspeciesInformeForestal>();
					especiesInformeForestalCenso = new EspeciesInformeForestal();
					listaIndiceValorImportancia = new ArrayList<AnalisisResultadoForestal>();
					
				}
			}
			
			//ELIMINAR CUALITATIVA
			if(!listaSitioMuestral.isEmpty()){
				for(SitioMuestral sitio : listaSitioMuestral){
					if(sitio.getId() != null){
						sitio.setEstado(false);
						sitioMuestralFacade.guardar(sitio);
					}				
				}
			}
			listaSitioMuestral = new ArrayList<SitioMuestral>();
			
			if(!listaCaracterizacionCualitativaEspecies.isEmpty()){
				for(EspeciesInformeForestal especie : listaCaracterizacionCualitativaEspecies){
					if(especie.getId() != null){
						especie.setEstado(false);
						especiesInformeForestalFacade.guardar(especie);
					}
				}
			}
			listaCaracterizacionCualitativaEspecies = new ArrayList<EspeciesInformeForestal>();
		}	
		
		if(!listaAfectacionForestal.isEmpty()) {
			for (AfectacionForestal item : listaAfectacionForestal) {
				item.setIdInformeFactibilidad(informeFactivilidadForestal.getId());
				afectacionForestalFacade.guardar(item);
			}
		}
		if(!listaAfectacionForestalEliminar.isEmpty()){
			for (AfectacionForestal item : listaAfectacionForestalEliminar) {
				item.setEstado(false);
				afectacionForestalFacade.guardar(item);
			}
		}

		if(!listaDocumentosAnexos.isEmpty()) {
			for (DocumentoViabilidad fotoProyecto : listaDocumentosAnexos) {
				if(fotoProyecto.getId() == null) {
					fotoProyecto.setIdViabilidad(viabilidadProyecto.getId());
					fotoProyecto.setIdTipoDocumento(TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_ANEXOS_INFORME_FACTIBILIDAD.getIdTipoDocumento());
					fotoProyecto = documentosViabilidadFacade.guardarDocumento(proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
							  "VIABILIDAD_AMBIENTAL", fotoProyecto, 1);
				}
			}
		}
		
		if(!listaDocumentosAnexosEliminar.isEmpty()){
			for (DocumentoViabilidad item : listaDocumentosAnexosEliminar) {
				item.setEstado(false);
				documentosViabilidadFacade.guardar(item);
			}
		}
		
		listaDocumentoAdjuntoEliminar = new ArrayList<DocumentoViabilidad>();
		listaDocumentosAnexosEliminar = new ArrayList<DocumentoViabilidad>();
		
	}
	
	public void cargarDatosInforme() {
		listaInterseccionUHidrograficas = unidadHidrograficaFacade.getListaPorInformeFactibilidad(informeFactivilidadForestal.getId());
		if (listaInterseccionUHidrograficas == null)
			listaInterseccionUHidrograficas = new ArrayList<UnidadHidrografica>();
		
		consultorPrincipal = equipoConsultorForestalFacade.getConsultorPrincipal(informeFactivilidadForestal.getId());
		if(consultorPrincipal == null) {
			consultorPrincipal = new EquipoConsultorForestal();
			consultorPrincipal.setConsultor(new ConsultorForestal());
		}
		
		listaEquipoConsultor = equipoConsultorForestalFacade.getListaEquipo(informeFactivilidadForestal.getId());
		if(listaEquipoConsultor == null) {
			listaEquipoConsultor = new ArrayList<>();
		}

		listaFotoArchivo = documentosViabilidadFacade.getDocumentoPorTipoTramite(viabilidadProyecto.getId(),
						TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_FOTO_GENERAL_PROYECTO.getIdTipoDocumento());
		
		listaDocumentoAdjunto = documentosViabilidadFacade.getDocumentoPorTipoTramite(viabilidadProyecto.getId(),
				TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_ADJUNTO_GENERAL_PROYECTO.getIdTipoDocumento());
		
		listaImagenesSatelite = documentosViabilidadFacade.getDocumentoPorTipoTramite(viabilidadProyecto.getId(),
				TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_FOTO_COBERTURA_PROYECTO.getIdTipoDocumento());
		
		//CUALITATIVA
		if(informeFactivilidadForestal.getCaracterizacionCobertura() != null 
				&& informeFactivilidadForestal.getCaracterizacionCobertura().equals(InformeFactibilidadForestal.caracterizacionCualitativa)) {
			listaSitioMuestral = sitioMuestralFacade.getSitiosPorInformeTipo(informeFactivilidadForestal.getId(), SitioMuestral.registroCualitativo);
			if(listaSitioMuestral == null) {
				listaSitioMuestral = new ArrayList<>();
			} else {
				listaCaracterizacionCualitativaEspecies = new ArrayList<>();
				for (SitioMuestral sitio : listaSitioMuestral) {
					List<CoordenadaSitioMuestral> coordenadasSitio = coordenadaSitioMuestralFacade.getListaCoordenadasPorSitioOrder(sitio.getId());
					if(coordenadasSitio != null) {
						sitio.setCoordenadaSitioMuestral(coordenadasSitio.get(0));
					}
							
					List<EspeciesInformeForestal> listaEspeciesAux = especiesInformeForestalFacade.getListaPorSitioMuestral(sitio.getId());
					if(listaEspeciesAux != null) {
						for (EspeciesInformeForestal item : listaEspeciesAux) {
							item.setSitioMuestral(sitio);
						}
						
						listaCaracterizacionCualitativaEspecies.addAll(listaEspeciesAux);
					}
				}
			}
		}
		
		//CUANTITATIVA
		if (informeFactivilidadForestal.getCaracterizacionCobertura() != null
				&& informeFactivilidadForestal.getCaracterizacionCobertura().equals(InformeFactibilidadForestal.caracterizacionCuantitativa)) {

			listaFotografiaCuantitativa = documentosViabilidadFacade.getDocumentoPorTipoTramite(viabilidadProyecto.getId(), TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_FOTO_FASE_CAMPO.getIdTipoDocumento());
			listaFotografiaOficina = documentosViabilidadFacade.getDocumentoPorTipoTramite(viabilidadProyecto.getId(), TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_FOTO_FASE_OFICINA.getIdTipoDocumento());

			//CENSO
			if (informeFactivilidadForestal.getRecoleccionDatos() != null
					&& informeFactivilidadForestal.getRecoleccionDatos().equals(InformeFactibilidadForestal.recoleccionCenso)) {
				List<SitioMuestral> listaSitios = sitioMuestralFacade.getSitiosPorInformeTipo(informeFactivilidadForestal.getId(), SitioMuestral.registroCuantitativoCenso);
				if(listaSitios != null && listaSitios.size() > 0) {
					sitioMuestralCenso = listaSitios.get(0);
					
					listaEspeciesCenso = especiesInformeForestalFacade.getListaPorSitioMuestral(sitioMuestralCenso.getId());
					if(listaEspeciesCenso == null) {
						listaEspeciesCenso = new ArrayList<>();
					}
					
					for(EspeciesInformeForestal especie : listaEspeciesCenso){
						if(!listFamiliaSelect.contains(especie.getFamiliaEspecie())){
							listFamiliaSelect.add(especie.getFamiliaEspecie());
						}						
					}
					
					listaIndiceValorImportancia = analisisResultadoForestalFacade.getListaPorSitio(sitioMuestralCenso.getId());
					if(listaIndiceValorImportancia == null) {
						listaIndiceValorImportancia = new ArrayList<>();
					}
				}
				
				listaEspeciesImportancia = especiesImportanciaForestalFacade.getListaPorInforme(informeFactivilidadForestal.getId());
				if(listaEspeciesImportancia == null) {
					listaEspeciesImportancia = new ArrayList<>();
				}
			}

			//MUESTREO
			if (informeFactivilidadForestal.getRecoleccionDatos() != null
					&& informeFactivilidadForestal.getRecoleccionDatos().equals(InformeFactibilidadForestal.recoleccionMuestreo)) {

				listaSitioMuestreo = sitioMuestralFacade.getSitiosPorInformeTipo(informeFactivilidadForestal.getId(), SitioMuestral.registroCuantitativoMuestreo);
				if(listaSitioMuestreo != null && listaSitioMuestreo.size() > 0) {
					for (SitioMuestral sitio : listaSitioMuestreo) {
						List<CoordenadaSitioMuestral> coordenadaSitio = coordenadaSitioMuestralFacade.getListaCoordenadasPorSitioOrder(sitio.getId());
						sitio.setListaCoordenadas(coordenadaSitio);
						
						String coordenadasX = "";
						String coordenadasY = "";
						for(CoordenadaSitioMuestral coordenada : sitio.getListaCoordenadas()){				
							coordenadasX += coordenadasX.equals("") ? coordenada.getCoordenadaX() : ", " + coordenada.getCoordenadaX();
							coordenadasY += coordenadasY.equals("") ? coordenada.getCoordenadaY() : ", " + coordenada.getCoordenadaY();				
						}
						sitio.setCoordenadasX(coordenadasX);
						sitio.setCoordenadasY(coordenadasY);
						
						List<UbicacionSitioMuestral> ubicacionesSitio = ubicacionSitioMuestralFacade.getListaPorInforme(sitio.getId());
						sitio.setUbicacionesGeograficas(ubicacionesSitio);
						
						String parroquia = "";
						String canton = "";
						String provincia = "";
						
						for(UbicacionSitioMuestral ubicacion : sitio.getUbicacionesGeograficas()){
							parroquia += parroquia.equals("") ? ubicacion.getUbicacionesGeografica().getNombre() : ", "
									+ ubicacion.getUbicacionesGeografica().getNombre();
							canton += canton.equals("") ? ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre() : ", "
									+ ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre();
							provincia += provincia.equals("") ? ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getUbicacionesGeografica().getNombre() : ", "
									+ ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getUbicacionesGeografica().getNombre();
						}
						
						sitio.setParroquia(parroquia);
						sitio.setCanton(canton);
						sitio.setProvincia(provincia);
						
						listaEspeciesCenso = especiesInformeForestalFacade.getListaPorSitioMuestral(sitioMuestralCenso.getId());
						if(listaEspeciesCenso == null) {
							listaEspeciesCenso = new ArrayList<>();
						}
						
						List<EspeciesInformeForestal> listaEspeciesAux = especiesInformeForestalFacade.getListaPorSitioMuestral(sitio.getId());
						if(listaEspeciesAux != null) {
							for (EspeciesInformeForestal item : listaEspeciesAux) {
								item.setSitioMuestral(sitio);
							}
							
							listaEspeciesMuestreo.addAll(listaEspeciesAux);
						}
						
						for(EspeciesInformeForestal especie : listaEspeciesMuestreo){
							if(!listFamiliaSelect.contains(especie.getFamiliaEspecie())){
								listFamiliaSelect.add(especie.getFamiliaEspecie());
							}						
						}
						
						List<AnalisisResultadoForestal> listaIvi = analisisResultadoForestalFacade.getListaPorSitio(sitio.getId());
						if(listaIvi != null) {
							sitio.setListaEspecieValorImportancia(listaIvi);
						}
						
						listaSitioSumatoria = new ArrayList<SitioMuestral>();
						for(SitioMuestral sitioM : listaSitioMuestreo){
							if(!listaEspeciesMuestreo.isEmpty())
								listaSitioSumatoria.add(sitioM);
						}
					}
				} else {
					listaSitioMuestreo = new ArrayList<>();
				}
				

				listaEspeciesImportancia = especiesImportanciaForestalFacade.getListaPorInforme(informeFactivilidadForestal.getId());
				if(listaEspeciesImportancia == null) {
					listaEspeciesImportancia = new ArrayList<>();
				}
				
			}
		}
		
		listaAfectacionForestal = afectacionForestalFacade.getListaPorInforme(informeFactivilidadForestal.getId());
		if(listaAfectacionForestal == null) {
			listaAfectacionForestal = new ArrayList<>();
		} else {
			for (AfectacionForestal item : listaAfectacionForestal) {
				item.setListaIdEcosistemas(getListaFromString(item.getTipoEcosistemas()));
				item.setListaIdCoberturaVegetal(getListaFromString(item.getTipoCoberturaVegetal()));
				item.setListaIdConvenio(getListaFromString(item.getTipoConvenio()));
				item.setListaIdUnidadHidrograficas(getListaFromString(item.getTipoUnidadesHidrograficas()));
				
				item.setNombresEcosistemas(getNombresCoberturas(item.getListaIdEcosistemas(), listaInterseccionesEcosistema));
				item.setNombresCoberturaVegetal(getNombresCoberturas(item.getListaIdCoberturaVegetal(), listaInterseccionesCobertura));
				item.setNombresConvenio(getNombresCoberturas(item.getListaIdConvenio(), listaInterseccionesConvenios));

				String nombre = "";
				if(item.getListaIdUnidadHidrograficas().size() > 0) {
					for (String unidad : item.getListaIdUnidadHidrograficas()) {
						for (UnidadHidrografica detalle : listaInterseccionUHidrograficas) {
							if(unidad.equals(detalle.getNombre().replaceAll("\\s", ""))) {
								nombre += (nombre == "") ? detalle.getNombre() : ", " + detalle.getNombre();
							}
						}
					}
				}
				item.setNombresUnidadHidrograficas(nombre);
			}
		}
		
		listaDocumentosAnexos = documentosViabilidadFacade.getDocumentoPorTipoTramite(viabilidadProyecto.getId(),
				TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_ANEXOS_INFORME_FACTIBILIDAD.getIdTipoDocumento());
	}
	
	private List<String>  getListaFromString(String cadena) {
		List<String> idSeleccionados = new ArrayList<>();
		
		if (cadena != null && !cadena.isEmpty()) {
			String[] arrayIds = cadena.split(",");
			idSeleccionados = Arrays.asList(arrayIds);
		}
		
		return idSeleccionados;
	}
	
	private String getNombresCoberturas(List<String> idSeleccionados, List<DetalleInterseccionProyectoAmbiental> listaIntersecciones) {
		String nombre = "";
		if(idSeleccionados.size() > 0) {
			for (String item : idSeleccionados) {
				Integer id = Integer.valueOf(item);
				for (DetalleInterseccionProyectoAmbiental detalle : listaIntersecciones) {
					if(id.equals(detalle.getId())) {
						nombre += (nombre == "") ? detalle.getNombreGeometria() : ", " + detalle.getNombreGeometria();
					}
				}
			}
		}
		
		return nombre;
	}

	public void validateDatosIngresoInforme(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		if(informeFactivilidadForestal.getAntecedentes() == null 
				|| informeFactivilidadForestal.getAntecedentes().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El campo 'Antecedentes' es requerido.", null));
		
		if(informeFactivilidadForestal.getMarcoLegal() == null 
				|| informeFactivilidadForestal.getMarcoLegal().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El campo 'Marco Legal' es requerido.", null));
		
		if(informeFactivilidadForestal.getObjetivo() == null 
				|| informeFactivilidadForestal.getObjetivo().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El campo 'Objetivo' es requerido.", null));

		if(informeFactivilidadForestal.getDescripcionProyecto() == null 
				|| informeFactivilidadForestal.getDescripcionProyecto().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El campo 'Descripción general del Proyecto' es requerido.", null));
		
		
		if(listaFotoArchivo == null || listaFotoArchivo.size() == 0) {
			errorMessages.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "La tabla Fotografías de la sección 5. Descripción General Del Proyecto es requerida.", null));
		}

		if(listaDocumentoAdjunto == null || listaDocumentoAdjunto.size() == 0) {
			errorMessages.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "La tabla Adjuntos de la sección 5. Descripción General Del Proyecto es requerida.", null));
		}

		if(informeFactivilidadForestal.getDescripcionCoberturaVegetal() == null 
				|| informeFactivilidadForestal.getDescripcionCoberturaVegetal().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El campo 'Descripción de la cobertura vegetal y uso de suelo de área geográfica del proyecto' es requerido.", null));
		
		if(informeFactivilidadForestal.getDescripcionEcosistemas() == null 
				|| informeFactivilidadForestal.getDescripcionEcosistemas().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El campo 'Descripción de los ecosistemas del área geográfica del proyecto' es requerido.", null));

		if(listaImagenesSatelite == null || listaImagenesSatelite.size() == 0) {
			errorMessages.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "La tabla 'Imagen Satelital' de la sección '6.2 Ecosistemas'  es requerida.", null));
		}

		//CUALITATIVA
		if(informeFactivilidadForestal.getCaracterizacionCobertura() != null 
				&& informeFactivilidadForestal.getCaracterizacionCobertura().equals(InformeFactibilidadForestal.caracterizacionCualitativa)) {
			if(listaSitioMuestral == null || listaSitioMuestral.size() == 0) {
				errorMessages.add(new FacesMessage(
								FacesMessage.SEVERITY_ERROR, "La tabla 'Ubicación del sitio de referencia caracterización cualitativa' es requerida.", null));
			}
			if(listaCaracterizacionCualitativaEspecies == null || listaCaracterizacionCualitativaEspecies.size() == 0) {
				errorMessages.add(new FacesMessage(
								FacesMessage.SEVERITY_ERROR, "La tabla 'Registro cualitativo de especies del área de estudio' es requerida.", null));
			} 
			
			if(listaSitioMuestral != null && listaSitioMuestral.size() >= 0
					&& listaCaracterizacionCualitativaEspecies != null && listaCaracterizacionCualitativaEspecies.size() >= 0) {
				for (SitioMuestral sitio : listaSitioMuestral) {
					Boolean especiesPorSitio = false;
					for (EspeciesInformeForestal especie : listaCaracterizacionCualitativaEspecies) {
						if(especie.getSitioMuestral().getNombreSitio().equals(sitio.getNombreSitio())) {
							especiesPorSitio = true;
							break;
						}
					}
					
					if(!especiesPorSitio) {
						errorMessages.add(new FacesMessage(
								FacesMessage.SEVERITY_ERROR, "Registre especies para el sitio '" + sitio.getNombreSitio() + "'.", null));
					}
				}
			}

		}
		
		if(informeFactivilidadForestal.getCaracterizacionCobertura() == null) {
			errorMessages.add(new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "El campo 'Seleccione el tipo de descripción utilizada' es requerido.", null));
		}
		
		//CUANTITATIVA
		if(informeFactivilidadForestal.getCaracterizacionCobertura() != null 
				&& informeFactivilidadForestal.getCaracterizacionCobertura().equals(InformeFactibilidadForestal.caracterizacionCuantitativa)) {
			if(informeFactivilidadForestal.getFaseCampo() == null 
					|| informeFactivilidadForestal.getFaseCampo().isEmpty())
				errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El campo 'Fase de Campo' es requerido.", null));
			
			if(informeFactivilidadForestal.getFaseOficina() == null 
					|| informeFactivilidadForestal.getFaseOficina().isEmpty())
				errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El campo 'Fase de Oficina' es requerido.", null));
			
			if(listaFotografiaCuantitativa == null || listaFotografiaCuantitativa.size() == 0) {
				errorMessages.add(new FacesMessage(
								FacesMessage.SEVERITY_ERROR, "La tabla Fotografías de la sección Fase de Campo es requerida.", null));
			}
			
			if(listaFotografiaOficina == null || listaFotografiaOficina.size() == 0) {
				errorMessages.add(new FacesMessage(
								FacesMessage.SEVERITY_ERROR, "La tabla Fotografías de la sección Fase de Oficina es requerida.", null));
			}
			
			if(informeFactivilidadForestal.getRecoleccionDatos() == null) {
				errorMessages.add(new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "El campo 'Método de recolección de datos' es requerido.", null));
			}
			
			//CENSO
			if(informeFactivilidadForestal.getRecoleccionDatos() != null 
					&& informeFactivilidadForestal.getRecoleccionDatos().equals(InformeFactibilidadForestal.recoleccionCenso)) {
				if(listaEspeciesCenso == null || listaEspeciesCenso.size() == 0) {
					errorMessages.add(new FacesMessage(
									FacesMessage.SEVERITY_ERROR, "La Tabla de cálculo del área basal y volumen es requerida.", null));
				}

				if(listaEspeciesImportancia == null || listaEspeciesImportancia.size() == 0) {
					errorMessages.add(new FacesMessage(
									FacesMessage.SEVERITY_ERROR, "La Tabla de especies de importancia es requerida.", null));
				}

				if(listaIndiceValorImportancia == null || listaIndiceValorImportancia.size() == 0) {
					errorMessages.add(new FacesMessage(
									FacesMessage.SEVERITY_ERROR, "La Tabla de índice de valor de importancia es requerida.", null));
				}
			}
			
			//MUESTREO
			if(informeFactivilidadForestal.getRecoleccionDatos() != null 
					&& informeFactivilidadForestal.getRecoleccionDatos().equals(InformeFactibilidadForestal.recoleccionMuestreo)) {
				if(listaSitioMuestreo == null || listaSitioMuestreo.size() == 0) {
					errorMessages.add(new FacesMessage(
									FacesMessage.SEVERITY_ERROR, "La tabla Ubicación de las unidades muestrales es requerida.", null));
				}

				if(listaEspeciesMuestreo == null || listaEspeciesMuestreo.size() == 0) {
					errorMessages.add(new FacesMessage(
									FacesMessage.SEVERITY_ERROR, "La Tabla de cálculo del área basal y volumen es requerida.", null));
				}

				if(listaEspeciesImportancia == null || listaEspeciesImportancia.size() == 0) {
					errorMessages.add(new FacesMessage(
									FacesMessage.SEVERITY_ERROR, "La Tabla de especies de importancia es requerida.", null));
				}
				
				if(listaSitioMuestreo != null && listaSitioMuestreo.size() >= 0) {
					for(SitioMuestral sitioM : listaSitioMuestreo){
						if(sitioM.getListaEspecieValorImportancia() == null || sitioM.getListaEspecieValorImportancia().size() == 0){
							errorMessages.add(new FacesMessage(
									FacesMessage.SEVERITY_ERROR, "La Tabla de índice de valor de importancia para: " + sitioM.getNombreSitio() + " es requerida.", null));
						}
					}
				}
			}
		}

		if(listaAfectacionForestal == null || listaAfectacionForestal.size() == 0) {
			errorMessages.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "La tabla 'Detalle de actividades del proyecto' es requerida.", null));
		}

		if(informeFactivilidadForestal.getConclusiones() == null 
				|| informeFactivilidadForestal.getConclusiones().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El campo 'Conclusiones' es requerido.", null));
		
		if(informeFactivilidadForestal.getRecomendaciones()== null 
				|| informeFactivilidadForestal.getRecomendaciones().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El campo 'Recomendaciones' es requerido.", null));

		if(informeFactivilidadForestal.getBibliografia()== null 
				|| informeFactivilidadForestal.getBibliografia().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El campo 'Bibliografía' es requerido.", null));

		if(listaDocumentosAnexos == null || listaDocumentosAnexos.size() == 0) {
			errorMessages.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "La tabla Anexos es requerida.", null));
		}

		if(context.getMessageList().size() > 0 || errorMessages.size() > 0)
			informeGuardado = false;
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	public void sumatoriaCenso() {
		//TODO: AQUI AÑADIR LA SUMATORIA IVAN
		listaSitioSumatoriaCenso = new ArrayList<SitioMuestral>();
		if(!listaEspeciesCenso.isEmpty()) {
			double sumatoriaAreaBasal = 0.0;
			double sumatoriaVolumenTotal = 0.0;
			double sumatoriaVolumenComercial = 0.0;
			for(EspeciesInformeForestal especieIng : listaEspeciesCenso){
				sumatoriaAreaBasal+=especieIng.getAreaBasal();
				sumatoriaVolumenTotal+=especieIng.getVolumenTotal();
				sumatoriaVolumenComercial+=especieIng.getVolumenComercial();
			}
			SitioMuestral sitioM=new SitioMuestral(); 
			sitioM.setSumatoriaAreaBasal(sumatoriaAreaBasal);
			sitioM.setSumatoriaVolumenTotal(sumatoriaVolumenTotal);
			sitioM.setSumatoriaVolumenComercial(sumatoriaVolumenComercial);
			listaSitioSumatoriaCenso.add(sitioM);
		}
	} 
	private static void writeDecimal(HSSFWorkbook objWB, HSSFRow row, Double data, int position) {
		String pattern = "0.00"; //String pattern = "#.0000000000";
		HSSFCell celda = row.createCell(position);
		HSSFCellStyle styleDecimal = objWB.createCellStyle();
		styleDecimal.setDataFormat(objWB.createDataFormat().getFormat(pattern));
		celda.setCellStyle(styleDecimal);
		celda.setCellType(Cell.CELL_TYPE_NUMERIC);
		celda.setCellValue(data);
	}
	/**
	 * genera archivo excel y lo envia directamente a alfresco
	 * @throws Exception
	 */
	public void generarReporteTaxonomia () throws Exception  {
		try {
		HSSFWorkbook objWB = new HSSFWorkbook();
		HSSFSheet hoja1 = objWB.createSheet("hoja 1");
		int nroFila = 0;
		HSSFRow fila = hoja1.createRow(nroFila);
		HSSFCell celdaPlan = fila.createCell(0);
		celdaPlan.setCellValue("Tabla de Cálculo del Área Basal y Volumen");
		nroFila++;
		List<String> listaCabecera = new ArrayList<>();
		if(informeFactivilidadForestal.getRecoleccionDatos()==1)
			listaCabecera.add("Código de la Unidad Muestral");
		listaCabecera.add("Nº");
		listaCabecera.add("Familia");
		listaCabecera.add("Género");
		listaCabecera.add("Especie");
		listaCabecera.add("Nombre Común");
		listaCabecera.add("Diámetro a la altura del pecho DAP(m)");
		listaCabecera.add("Altura Total HT (m)");
		listaCabecera.add("Altura comercial HC(m)");
		listaCabecera.add("Área basal AB(m2)");
		listaCabecera.add("Volumen Total Vt(m3)");
		listaCabecera.add("Volumen comercial Vc(m3)");
		HSSFRow filaHeader = hoja1.createRow(nroFila);
		for(int col=0; col < listaCabecera.size(); col++) {
			HSSFCell celdaHeader = filaHeader.createCell(col);
			celdaHeader.setCellValue(listaCabecera.get(col));
		}
		nroFila++;
		int nroEspecie = 1;
		if(informeFactivilidadForestal.getRecoleccionDatos()==2)
			listaEspecieGenerico=listaEspeciesCenso;
		else
			listaEspecieGenerico=listaEspeciesMuestreo;
		
		for (EspeciesInformeForestal especie : listaEspecieGenerico) { 
			int indexCol = 0;
			HSSFRow filaEspecie = hoja1.createRow(nroFila);
			if(informeFactivilidadForestal.getRecoleccionDatos()==1) {
				HSSFCell celdaCodigoMuestral = filaEspecie.createCell(indexCol++);
				celdaCodigoMuestral.setCellValue(especie.getSitioMuestral().getNombreSitio());
			}
			HSSFCell celdaNro = filaEspecie.createCell(indexCol++);
			celdaNro.setCellValue(nroEspecie);
			HSSFCell celdaFamilia = filaEspecie.createCell(indexCol++);
			celdaFamilia.setCellValue(especie.getGeneroEspecie().getHiclIdParent().getHiclScientificName());
			HSSFCell celdaGenero = filaEspecie.createCell(indexCol++);
			celdaGenero.setCellValue(especie.getGeneroEspecie().getHiclScientificName());			
			HSSFCell celdaNombre = filaEspecie.createCell(indexCol++);
			if(especie.getEspecie() == null) {
				celdaNombre.setCellValue(especie.getGeneroEspecie().getHiclScientificName());
			} else {
				celdaNombre.setCellValue(especie.getEspecie().getSptaScientificName());
			}
			HSSFCell celdaNombreComun = filaEspecie.createCell(indexCol++);
			celdaNombreComun.setCellValue(especie.getNombreComun());
			writeDecimal(objWB, filaEspecie, especie.getAlturaDap(), indexCol++);
			writeDecimal(objWB, filaEspecie, especie.getAlturaTotal(), indexCol++);
			writeDecimal(objWB, filaEspecie, especie.getAlturaComercial(), indexCol++);
			writeDecimal(objWB, filaEspecie, especie.getAreaBasal(), indexCol++);
			writeDecimal(objWB, filaEspecie, especie.getVolumenTotal(), indexCol++);
			writeDecimal(objWB, filaEspecie, especie.getVolumenComercial(), indexCol++);
			nroEspecie++;
			nroFila++;
		}
			ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
			objWB.write(outByteStream);
			byte[] contenidoDocumento = outByteStream.toByteArray();;
			DocumentoViabilidad documentoViabilidad = new DocumentoViabilidad();
			documentoViabilidad.setContenidoDocumento(contenidoDocumento);
			documentoViabilidad.setExtension(".xls");
			documentoViabilidad.setMime("application/vnd.ms-excel");
			documentoViabilidad.setNombre("Tabla de cálculo del área basal y volumen.xls");
			documentoViabilidad.setIdViabilidad(viabilidadProyecto.getId());
			documentoViabilidad.setIdProceso(bandejaTareasBean.getProcessId()); 
			documentoViabilidad.setIdTipoDocumento(TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_TABLA_BASAL_VOLUMEN.getIdTipoDocumento());
			documentoViabilidad = documentosViabilidadFacade.guardarDocumento(proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
					  "VIABILIDAD_AMBIENTAL", documentoViabilidad, 2);
			return;
		} catch (IOException ex) {
			LOG.error(ex);
		} 
	}
}
