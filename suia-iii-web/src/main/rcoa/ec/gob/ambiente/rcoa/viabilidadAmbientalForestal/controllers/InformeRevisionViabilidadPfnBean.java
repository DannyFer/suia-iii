package ec.gob.ambiente.rcoa.viabilidadAmbientalForestal.controllers;

import index.ContienePoligono_entrada;
import index.ContienePoligono_resultado;
import index.SVA_Reproyeccion_IntersecadoPortTypeProxy;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.cliente.ConsultaRucCedula;
import ec.gob.ambiente.rcoa.facade.CapasCoaFacade;
import ec.gob.ambiente.rcoa.facade.CatalogoCoaFacade;
import ec.gob.ambiente.rcoa.facade.CoordenadasProyectoCoaFacade;
import ec.gob.ambiente.rcoa.facade.DetalleInterseccionProyectoAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaAmbientalCoaShapeFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.HigherClassificationFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.SpecieTaxaFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.model.HigherClassification;
import ec.gob.ambiente.rcoa.inventarioForestal.model.SpecieTaxa;
import ec.gob.ambiente.rcoa.model.CapasCoa;
import ec.gob.ambiente.rcoa.model.CoordenadasProyecto;
import ec.gob.ambiente.rcoa.model.DetalleInterseccionProyectoAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaAmbientalCoaShape;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.utils.stringTablasViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.controllers.UtilViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.AfectacionCoberturaVegetalFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.AfectacionForestalFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.AnalisisResultadoForestalFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.CoordenadasForestalFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.DelegadosInspeccionForestalFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.DocumentosViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.EspeciesImportanciaForestalFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.EspeciesInformeForestalFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.EspeciesInspeccionForestalFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.FotografiasForestalFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InformacionViabilidadLegalFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InformeFactibilidadForestalFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InformeInspeccionForestalFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InformeRevisionForestalFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InterpretacionIndiceForestalFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InterseccionViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ObservacionesViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.SitioMuestralFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.UnidadHidrograficaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.AfectacionCoberturaVegetal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.AfectacionForestal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.AnalisisResultadoForestal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.CoordenadaForestal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.DelegadoOperadorForestal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.DelegadoTecnicoForestal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.DocumentoViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.EspeciesImportanciaForestal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.EspeciesInformeForestal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.EspeciesInspeccionForestal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.FotografiaInformeForestal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.InformeFactibilidadForestal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.InformeInspeccionForestal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.InformeInspeccionForestalEntity;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.InformeTecnicoForestal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.InterpretacionIndiceForestal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.SitioMuestral;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.UnidadHidrografica;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ViabilidadCoa;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;
import ec.gob.registrocivil.consultacedula.Cedula;
import ec.gov.sri.wsconsultacontribuyente.ContribuyenteCompleto;

@ManagedBean
@ViewScoped
public class InformeRevisionViabilidadPfnBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Logger LOG = Logger.getLogger(InformeRevisionViabilidadPfnBean.class);
	
	private final ConsultaRucCedula consultaRucCedula = new ConsultaRucCedula(Constantes.getUrlWsRegistroCivilSri());
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@EJB
	private PlantillaReporteFacade plantillaReporteFacade;
	
	@EJB
	private DocumentosViabilidadFacade documentosFacade;
	
	@EJB
	private ViabilidadCoaFacade viabilidadCoaFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
	private InformeInspeccionForestalFacade informeInspeccionFacade;
	
	@EJB
    private UsuarioFacade usuarioFacade;
	
	@EJB
	private DelegadosInspeccionForestalFacade delegadosInspeccionForestalFacade;
	
	@EJB
	private CoordenadasForestalFacade coordenadasForestalFacade;
	
	@EJB
	private FotografiasForestalFacade fotografiasForestalFacade;
	
	@EJB
    private ProcesoFacade procesoFacade;
	
	@EJB
	private OrganizacionFacade organizacionFacade;
	
	@EJB
	private InterseccionViabilidadCoaFacade interseccionViabilidadCoaFacade;
	
	@EJB
	private	InformacionViabilidadLegalFacade revisionTecnicoJuridicoFacade;
	
	@EJB
    private ObservacionesViabilidadFacade observacionesViabilidadFacade;
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	
	@EJB
   	private ProyectoLicenciaAmbientalCoaShapeFacade proyectoLicenciaAmbientalCoaShapeFacade;
    @EJB
	private CoordenadasProyectoCoaFacade coordenadasProyectoCoaFacade;
    @EJB
	private HigherClassificationFacade higherClassificationFacade;
    @EJB
	private SpecieTaxaFacade specieTaxaFacade;
    @EJB
	private DetalleInterseccionProyectoAmbientalFacade detalleInterseccionProyectoAmbientalFacade;
    @EJB
	private EspeciesInformeForestalFacade especiesInformeForestalFacade;
    @EJB
	private SitioMuestralFacade sitioMuestralFacade;
    @EJB
	private InformeFactibilidadForestalFacade informeFactibilidadForestalFacade;
    @EJB
	private AnalisisResultadoForestalFacade analisisResultadoForestalFacade;
    @EJB
	private InterpretacionIndiceForestalFacade interpretacionIndiceForestalFacade;
    @EJB
	private EspeciesImportanciaForestalFacade especiesImportanciaForestalFacade;
    @EJB
	private AfectacionForestalFacade afectacionForestalFacade;
    @EJB
	private AfectacionCoberturaVegetalFacade afectacionCoberturaVegetalFacade;
    @EJB
	private EspeciesInspeccionForestalFacade especiesInspeccionForestalFacade;
    @EJB
	private UnidadHidrograficaFacade unidadHidrograficaFacade;
    @EJB
	private CapasCoaFacade capasCoaFacade;
    @EJB
	private InformeRevisionForestalFacade informeRevisionForestalFacade;
	@EJB
	private CatalogoCoaFacade catalogoCoaFacade;
	
	@Getter
	@Setter
	private ViabilidadCoa viabilidadProyecto;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Getter
	@Setter
	private DocumentoViabilidad documentoInformeAlfresco, informeFirmaManual;
	
	@Getter
	@Setter
	private InformeInspeccionForestal informeInspeccion;

	@Getter
	@Setter
	private PlantillaReporte plantillaReporteInforme;
	
	@Getter
	@Setter
	private InformeFactibilidadForestal informeFactibilidadForestal;
	
	@Getter
	@Setter
	private List<EspeciesInspeccionForestal> listaEspeciesMuestreoCenso, listaEspeciesInspeccionEliminar;
	
	@Getter
	@Setter
	private List<EspeciesInspeccionForestal> listaEspeciesCaracterizacionCualitativa;
	
	@Getter
	@Setter
	private List<DelegadoTecnicoForestal> listaTecnicosDelegados, listaTecnicosDelegadosEliminar;
	
	@Getter
	@Setter
	private List<FotografiaInformeForestal> listaFotosRecorrido, listaImgsEliminar;
	
	@Getter
	@Setter
	private List<CoordenadaForestal> listaCoordenadas, listaCoordenadasEliminar;
	
	@Getter
	@Setter
	private List<File> filesFotos;
	
	@Getter
	@Setter
	private List<DelegadoOperadorForestal> listaDelegadosOperador, listaDelegadosOperadorEliminar;
	
	@Setter
	@Getter
	private List<HigherClassification> listFamilia, listGenero;
	
	@Setter
	@Getter
	private List<SpecieTaxa> listEspecie;
	
	@Getter
	@Setter
	private List<UnidadHidrografica> listaUnidadesHidrografica, listaUnidadesHidrograficaEliminar;
	
	@Getter
	@Setter
	private List<UbicacionesGeografica> ubicacionesProyecto;
	
	@Getter
	@Setter
	private List<AfectacionCoberturaVegetal> listaAfectacionCoberturaVegetal, listaAfectacionEcosistemas, listaAfectacionConvenios;
	
	@Getter
	@Setter
	private List<SitioMuestral> listaSitiosMuestrales, listaSitiosMuestralesCenso,listaSitioMuestra;
	
	@Getter
	@Setter
	private List<ResultadoRevisionForestal> listaPromediosAreaBasalVolumen, listaResultadosIvi, listaResultadosIviCenso;
	
	@Getter
	@Setter
	private List<InterpretacionIndiceForestal> listaInterpretacionIndices, listaInterpretacionIndicesCenso;
	
	@Getter
	@Setter
	private List<EspeciesImportanciaForestal> listaEspeciesImportancia;
	
	@Getter
	@Setter
	private List<EspeciesInformeForestal> listaEspeciesCualitativa;
	
	@Getter
	@Setter
	private List<AfectacionForestal> listaAfectacionForestal;
	
	@Setter
    @Getter
    private HigherClassification objFamilia, objGenero;
    
    @Setter
    @Getter
    private SpecieTaxa objEspecie, objEspecieOtro;
	
	@Getter
	@Setter
	private DelegadoOperadorForestal delegadoOperador;
	
	@Getter
	@Setter
	private DelegadoTecnicoForestal tecnicoDelegado;
	
	@Getter
	@Setter
	private FotografiaInformeForestal nuevaFotografia;
	
	@Getter
	@Setter
	private CoordenadaForestal nuevaCoordenada;
	
	@Getter
	@Setter
	private TipoDocumentoSistema tipoInforme_;
	
	@Setter
    @Getter
    private EspeciesInspeccionForestal registroEspeciesForestales;
	
	@Setter
    @Getter
    private UnidadHidrografica registroUnidadHidrografica;
	
	@Setter
    @Getter
    private CapasCoa capaRestauracion;
	
	@Setter
    @Getter
    private List<String> listaCoordenadasProyecto;
	
	@Getter
	@Setter
	private Boolean cedulaDelegadoValida, ejecutarSeleccion, blockFamilia, blockGenero, blockEspecie;
	
	@Getter
	@Setter
	private Boolean esInformeViabilidad, esCobertura2022;

	@Getter
	@Setter
	private String nombreTipoInforme, nombreTipoOficio, nombreDocumentoFirmado;

	@Getter
	@Setter
	private String nombreInforme, razonSocial, interseccionesProyecto;
	
	@Getter
	@Setter
	private Integer tipoFotografia, idTarea, numeroRevision;
	
	@Getter
	@Setter
	private Double totalAreaInterseccion;
	
	@Getter
	@Setter
	private String caracteristicasInfraestructura;
	
	@Setter
    @Getter
    private int activeIndex = 0;
	
	@Getter
	@Setter
	private List<EspeciesInformeForestal> listaEspeciesCenso;

	@Getter
	@Setter
	private List<SitioMuestral> listaSitioSumatoriaCenso,listaSitioSumatoria;
	
	public static final Double FACTOR_FORMA = 0.7;

	@PostConstruct
	private void iniciar() {
		try {
			
			Map<String, Object> variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());

			String idProyectoString = (String) variables.get("idProyecto");
			Integer idProyecto = Integer.valueOf(idProyectoString);

			proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
			viabilidadProyecto = viabilidadCoaFacade.getViabilidadForestalPorProyecto(idProyecto);			
			
			numeroRevision = Integer.valueOf((String) variables.get("numeroRevisionInformacion"));

			esCobertura2022 = false;
			if (variables.get("usaCobertura2022") != null) {
				esCobertura2022 = Boolean.parseBoolean(variables.get("usaCobertura2022").toString());
			}

			capaRestauracion = capasCoaFacade.getCapaByNombre(CapasCoa.NAME_CAPA_RESTAURACION);
				
			registroEspeciesForestales = new EspeciesInspeccionForestal();
			objFamilia = new HigherClassification();
			objGenero = new HigherClassification();
			objEspecie = new SpecieTaxa();
			objEspecieOtro = new SpecieTaxa();
			listFamilia = new ArrayList<HigherClassification>();
			listGenero = new ArrayList<HigherClassification>();
			listEspecie = new ArrayList<SpecieTaxa>();
			
			listaDelegadosOperador = new ArrayList<>();
			listaTecnicosDelegados = new ArrayList<>();
			listaDelegadosOperadorEliminar = new ArrayList<>();
			listaTecnicosDelegadosEliminar = new ArrayList<>();
			listaFotosRecorrido = new ArrayList<>();
			listaImgsEliminar = new ArrayList<>();
			listaCoordenadas = new ArrayList<>();
			listaCoordenadasEliminar = new ArrayList<>();
			filesFotos = new ArrayList<>();
			listaEspeciesMuestreoCenso = new ArrayList<>();
			listaEspeciesInspeccionEliminar = new ArrayList<>();
			listaEspeciesCaracterizacionCualitativa = new ArrayList<>();
			listaUnidadesHidrografica = new ArrayList<>();
			listaUnidadesHidrograficaEliminar = new ArrayList<>();
			listaSitioMuestra = new ArrayList<>();
			listaSitioSumatoria= new ArrayList<>();
			
			ejecutarSeleccion = false;
			
			blockFamilia = true;
			blockGenero = true;
			blockEspecie = true;
			
			listFamilia = obtenerListaFamilia();
			
			idTarea = (int)bandejaTareasBean.getTarea().getTaskId();
							
			interseccionesProyecto = interseccionViabilidadCoaFacade.getInterseccionesForestal(proyectoLicenciaCoa.getId(), 2);
			
			Integer idInformeCarga = null;
			Integer idNuevoInforme = null;
			informeInspeccion = informeInspeccionFacade.getInformePorViabilidadRevision(viabilidadProyecto.getId(), numeroRevision);
			if(informeInspeccion == null) {
				informeInspeccion = new InformeInspeccionForestal();
				esInformeViabilidad = null;
				informeInspeccion.setMarcoLegal(catalogoCoaFacade.obtenerCatalogoPorId(Constantes.ID_MARCO_LEGAL_TECNICO).getValor());
				
				
				if(viabilidadProyecto.getRequiereApoyo() != null && viabilidadProyecto.getRequiereApoyo()) {
					InformeTecnicoForestal informeApoyo = informeRevisionForestalFacade.getInformePorViabilidad(viabilidadProyecto.getId(), InformeTecnicoForestal.apoyo);
					informeInspeccion.setConclusiones(informeApoyo.getConclusiones());
					informeInspeccion.setRecomendaciones(informeApoyo.getRecomendaciones());
				}
			} else {
				idInformeCarga = informeInspeccion.getId();
				
				if(informeInspeccion.getIdTarea() == null) {
					informeInspeccion.setIdTarea(idTarea);
					informeInspeccionFacade.guardarInformeViabilidad(informeInspeccion, viabilidadProyecto.getAreaResponsable());
				} else if(!informeInspeccion.getIdTarea().equals(idTarea)) {
					InformeInspeccionForestal informeNuevo = (InformeInspeccionForestal) SerializationUtils.clone(informeInspeccion);
					informeNuevo.setId(null);
					informeNuevo.setNumeroInforme(null);
					informeNuevo.setIdTarea(idTarea);
					informeInspeccion = informeInspeccionFacade.guardarInformeViabilidad(informeNuevo, viabilidadProyecto.getAreaResponsable());
					
					idNuevoInforme = informeInspeccion.getId();
				} 
				
				cargarDatos(idInformeCarga, idNuevoInforme);
				
				esInformeViabilidad = (informeInspeccion.getTipoInforme().equals(InformeInspeccionForestal.viabilidad)) ? true : false;
				
				ejecutarSeleccion = true;
			}
			
			plantillaReporteInforme = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_INFORME_VIABILIDAD);
			
			cargarDatosProyecto();
			
			showCuantitativaMuestreo();
			showCuantitativaCenso();
			showEspeciesImportancia();
			showCualitativa();
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
	}
	
	public void cargarDatosProyecto() throws Exception {
		listaCoordenadasProyecto = new ArrayList<String>();
		List<ProyectoLicenciaAmbientalCoaShape> shapeProyecto = proyectoLicenciaAmbientalCoaShapeFacade.buscarFormaGeograficaPorProyecto(proyectoLicenciaCoa,2, 0);
		for (ProyectoLicenciaAmbientalCoaShape rowShapeProyecto : shapeProyecto) {
			List<CoordenadasProyecto> listCoordenadas = coordenadasProyectoCoaFacade.buscarPorForma(rowShapeProyecto);
			String arrayCoordenadas = "";
			for (CoordenadasProyecto rowCoordenadas : listCoordenadas) {
				arrayCoordenadas += (arrayCoordenadas == "") ? rowCoordenadas.getX().toString()+ " " + rowCoordenadas.getY().toString() 
						: "," + rowCoordenadas.getX().toString() + " " + rowCoordenadas.getY().toString();
			}
			listaCoordenadasProyecto.add(arrayCoordenadas);
		}
		
		ubicacionesProyecto = proyectoLicenciaCoaUbicacionFacade.ubicacionesGeograficas(proyectoLicenciaCoa);
		
		informeFactibilidadForestal = informeFactibilidadForestalFacade.getInformePorViabilidad(viabilidadProyecto.getId());
		
		listaAfectacionForestal = afectacionForestalFacade.getListaPorInforme(informeFactibilidadForestal.getId());
		
		listaAfectacionCoberturaVegetal = afectacionCoberturaVegetalFacade.getListaPorInformeTipo(informeInspeccion.getId(), AfectacionCoberturaVegetal.coberturaVegetal);
		if(listaAfectacionCoberturaVegetal == null) {
			listaAfectacionCoberturaVegetal = new ArrayList<AfectacionCoberturaVegetal>();
			List<DetalleInterseccionProyectoAmbiental> detalleCobertura = new ArrayList<>();
			 
			if(esCobertura2022) {
				CapasCoa capaCobertura2022 = capasCoaFacade.getCapaByNombre(CapasCoa.NAME_COBERTURA_VEGETAL_2022);
				detalleCobertura = detalleInterseccionProyectoAmbientalFacade.getDetalleProyectoCapa(proyectoLicenciaCoa, capaCobertura2022.getId());
			} else {
				detalleCobertura = detalleInterseccionProyectoAmbientalFacade.getDetalleProyectoCapa(proyectoLicenciaCoa, CapasCoa.ID_COBERTURA_VEGETAL_2018);
			}
			 
			if(detalleCobertura != null && detalleCobertura.size() > 0) {
				for (DetalleInterseccionProyectoAmbiental item : detalleCobertura) {
					AfectacionCoberturaVegetal nuevaAfectacion = new AfectacionCoberturaVegetal();
					nuevaAfectacion.setDetalleInterseccion(item);
					nuevaAfectacion.setTipoRegistro(AfectacionCoberturaVegetal.coberturaVegetal);
					
					listaAfectacionCoberturaVegetal.add(nuevaAfectacion);
				}
			}
		}
		
		listaAfectacionEcosistemas = afectacionCoberturaVegetalFacade.getListaPorInformeTipo(informeInspeccion.getId(), AfectacionCoberturaVegetal.ecosistemas);
		if(listaAfectacionEcosistemas == null) {
			listaAfectacionEcosistemas = new ArrayList<AfectacionCoberturaVegetal>();
			List<DetalleInterseccionProyectoAmbiental> detalleEcosistemas = detalleInterseccionProyectoAmbientalFacade.getDetalleProyectoCapa(proyectoLicenciaCoa, CapasCoa.ID_ECOSISTEMAS);
			if(detalleEcosistemas != null && detalleEcosistemas.size() > 0) {
				for (DetalleInterseccionProyectoAmbiental item : detalleEcosistemas) {
					AfectacionCoberturaVegetal nuevaAfectacion = new AfectacionCoberturaVegetal();
					nuevaAfectacion.setDetalleInterseccion(item);
					nuevaAfectacion.setTipoRegistro(AfectacionCoberturaVegetal.ecosistemas);
					
					listaAfectacionEcosistemas.add(nuevaAfectacion);
				}
			}
		}
		
		listaAfectacionConvenios = afectacionCoberturaVegetalFacade.getListaPorInformeTipo(informeInspeccion.getId(), AfectacionCoberturaVegetal.convenios);
		if(listaAfectacionConvenios == null) {
			listaAfectacionConvenios = new ArrayList<AfectacionCoberturaVegetal>();
			List<DetalleInterseccionProyectoAmbiental> detalleConvenios = detalleInterseccionProyectoAmbientalFacade.getDetalleProyectoCapa(proyectoLicenciaCoa, CapasCoa.ID_SOCIO_BOSQUE);
			List<DetalleInterseccionProyectoAmbiental> listaInterseccionesRestauracion = detalleInterseccionProyectoAmbientalFacade.getDetalleProyectoCapa(proyectoLicenciaCoa, capaRestauracion.getId());
			if(listaInterseccionesRestauracion != null && listaInterseccionesRestauracion.size() > 0) {
				detalleConvenios.addAll(listaInterseccionesRestauracion);
			}
			
			if(detalleConvenios != null && detalleConvenios.size() > 0) {
				for (DetalleInterseccionProyectoAmbiental item : detalleConvenios) {
					AfectacionCoberturaVegetal nuevaAfectacion = new AfectacionCoberturaVegetal();
					nuevaAfectacion.setDetalleInterseccion(item);
					nuevaAfectacion.setTipoRegistro(AfectacionCoberturaVegetal.convenios);
					
					listaAfectacionConvenios.add(nuevaAfectacion);
				}
			}
		}
		//TODO: IVAN PARA CENSO TIPO=2
		listaSitioMuestra=sitioMuestralFacade.getSitiosPorInformeTipo(informeFactibilidadForestal.getId(), SitioMuestral.registroCuantitativoCenso);
		if(listaSitioMuestra!=null) {
			for(SitioMuestral sitio:listaSitioMuestra) {
				listaEspeciesCenso=especiesInformeForestalFacade.getListaPorSitioMuestral(sitio.getId());
			}
			sumatoriaCenso();
		}
		listaSitioSumatoria=sitioMuestralFacade.getSitiosPorInformeTipo(informeFactibilidadForestal.getId(), SitioMuestral.registroCuantitativoMuestreo);


	}
	
	public void cargarDatos(Integer idInformeCarga, Integer idNuevoInforme) {
		
		listaDelegadosOperador = (List<DelegadoOperadorForestal>) delegadosInspeccionForestalFacade.getDelegadosOperador(idInformeCarga);
		if(listaDelegadosOperador == null)
			listaDelegadosOperador = new ArrayList<>();
		
		listaTecnicosDelegados = (List<DelegadoTecnicoForestal>) delegadosInspeccionForestalFacade.getDelegadosTecnicos(idInformeCarga);
		if(listaTecnicosDelegados == null)
			listaTecnicosDelegados = new ArrayList<>();
		
		listaCoordenadas = coordenadasForestalFacade.getListaCoordenadasPorInformeOrder(idInformeCarga);
		if(listaCoordenadas == null)
			listaCoordenadas = new ArrayList<>();
		
		listaFotosRecorrido = fotografiasForestalFacade.getListaFotografiasPorInformeTipo(idInformeCarga, 3);
		if(listaFotosRecorrido == null)
			listaFotosRecorrido = new ArrayList<>();
		
		if(informeInspeccion.getEsMuestreoCenso() != null && informeInspeccion.getEsMuestreoCenso()) {
			listaEspeciesMuestreoCenso = especiesInspeccionForestalFacade.getListaPorInformeTipo(idInformeCarga, EspeciesInspeccionForestal.muestreoCenso);
			if(listaEspeciesMuestreoCenso == null)
				listaEspeciesMuestreoCenso = new ArrayList<EspeciesInspeccionForestal>();
		}
		
		if (informeInspeccion.getEsCaracterizacionCualitativa() != null && informeInspeccion.getEsCaracterizacionCualitativa()) {
			listaEspeciesCaracterizacionCualitativa = especiesInspeccionForestalFacade.getListaPorInformeTipo(idInformeCarga, EspeciesInspeccionForestal.cualitativo);
			if (listaEspeciesCaracterizacionCualitativa == null)
				listaEspeciesCaracterizacionCualitativa = new ArrayList<EspeciesInspeccionForestal>();
		}
		
		if (informeInspeccion.getHabilitarUnidadesHidrograficas() != null && informeInspeccion.getHabilitarUnidadesHidrograficas()) {
			listaUnidadesHidrografica = unidadHidrograficaFacade.getListaPorInformeInspeccion(idInformeCarga);
			if (listaUnidadesHidrografica == null)
				listaUnidadesHidrografica = new ArrayList<UnidadHidrografica>();
		}
		
		listaInterpretacionIndices = interpretacionIndiceForestalFacade.getListaPorInformeTipoSitio(idInformeCarga, SitioMuestral.registroCuantitativoMuestreo);
		if(listaInterpretacionIndices == null) {
			listaInterpretacionIndices = new ArrayList<>();
		}
		
		listaInterpretacionIndicesCenso = interpretacionIndiceForestalFacade.getListaPorInformeTipoSitio(idInformeCarga, SitioMuestral.registroCuantitativoCenso);
		if(listaInterpretacionIndicesCenso == null) {
			listaInterpretacionIndicesCenso = new ArrayList<>();
		}
		
		listaAfectacionCoberturaVegetal = afectacionCoberturaVegetalFacade.getListaPorInformeTipo(idInformeCarga, AfectacionCoberturaVegetal.coberturaVegetal);
		if(listaAfectacionCoberturaVegetal == null) {
			listaAfectacionCoberturaVegetal = new ArrayList<>();
		}
		listaAfectacionEcosistemas = afectacionCoberturaVegetalFacade.getListaPorInformeTipo(idInformeCarga, AfectacionCoberturaVegetal.ecosistemas);
		if(listaAfectacionEcosistemas == null) {
			listaAfectacionEcosistemas = new ArrayList<>();
		}
		listaAfectacionConvenios = afectacionCoberturaVegetalFacade.getListaPorInformeTipo(idInformeCarga, AfectacionCoberturaVegetal.convenios);
		if(listaAfectacionConvenios == null) {
			listaAfectacionConvenios = new ArrayList<>();
		}
		
		if(idNuevoInforme != null) {
			//clonar los datos
			for (DelegadoOperadorForestal  item : listaDelegadosOperador) {
				DelegadoOperadorForestal nuevoItem = (DelegadoOperadorForestal) SerializationUtils.clone(item);
				nuevoItem.setId(null);
				nuevoItem.setIdInformeInspeccion(idNuevoInforme);
				
				delegadosInspeccionForestalFacade.guardarDelegado(nuevoItem);
			}
			
			for (DelegadoTecnicoForestal  item : listaTecnicosDelegados) {
				DelegadoTecnicoForestal nuevoItem = (DelegadoTecnicoForestal) SerializationUtils.clone(item);
				nuevoItem.setId(null);
				nuevoItem.setIdInformeInspeccion(idNuevoInforme);
				
				delegadosInspeccionForestalFacade.guardarTecnicoDelegado(nuevoItem);
			}
			
			for (CoordenadaForestal  item : listaCoordenadas) {
				CoordenadaForestal nuevoItem = (CoordenadaForestal) SerializationUtils.clone(item);
				nuevoItem.setId(null);
				nuevoItem.setIdInformeInspeccion(idNuevoInforme);
				
				coordenadasForestalFacade.guardarCoordenada(nuevoItem);
			}
			
			for (FotografiaInformeForestal  foto : listaFotosRecorrido) {
				FotografiaInformeForestal nuevaFoto = (FotografiaInformeForestal) SerializationUtils.clone(foto);
				nuevaFoto.setId(null);
				nuevaFoto.setIdInformeInspeccion(idNuevoInforme);
				
				fotografiasForestalFacade.guardarFotografia(nuevaFoto);
			}
			
			for (UnidadHidrografica  item : listaUnidadesHidrografica) {
				UnidadHidrografica nuevoItem = (UnidadHidrografica) SerializationUtils.clone(item);
				nuevoItem.setId(null);
				nuevoItem.setIdInformeInspeccion(idNuevoInforme);
				
				unidadHidrograficaFacade.guardar(nuevoItem);
			}
			
			for (EspeciesInspeccionForestal  item : listaEspeciesMuestreoCenso) {
				EspeciesInspeccionForestal nuevoItem = (EspeciesInspeccionForestal) SerializationUtils.clone(item);
				nuevoItem.setId(null);
				nuevoItem.setIdInforme(idNuevoInforme);
				
				especiesInspeccionForestalFacade.guardarEspecie(nuevoItem);
			}
			
			for (EspeciesInspeccionForestal  item : listaEspeciesCaracterizacionCualitativa) {
				EspeciesInspeccionForestal nuevoItem = (EspeciesInspeccionForestal) SerializationUtils.clone(item);
				nuevoItem.setId(null);
				nuevoItem.setIdInforme(idNuevoInforme);
				
				especiesInspeccionForestalFacade.guardarEspecie(nuevoItem);
			}
			
			for (InterpretacionIndiceForestal  item : listaInterpretacionIndices) {
				InterpretacionIndiceForestal nuevoItem = (InterpretacionIndiceForestal) SerializationUtils.clone(item);
				nuevoItem.setId(null);
				nuevoItem.setIdInformeInspeccion(idNuevoInforme);
				
				interpretacionIndiceForestalFacade.guardar(nuevoItem);
			}
			
			for (InterpretacionIndiceForestal  item : listaInterpretacionIndicesCenso) {
				InterpretacionIndiceForestal nuevoItem = (InterpretacionIndiceForestal) SerializationUtils.clone(item);
				nuevoItem.setId(null);
				nuevoItem.setIdInformeInspeccion(idNuevoInforme);
				
				interpretacionIndiceForestalFacade.guardar(nuevoItem);
			}

			for (AfectacionCoberturaVegetal  item : listaAfectacionCoberturaVegetal) {
				AfectacionCoberturaVegetal nuevoItem = (AfectacionCoberturaVegetal) SerializationUtils.clone(item);
				nuevoItem.setId(null);
				nuevoItem.setIdInforme(idNuevoInforme);
				
				afectacionCoberturaVegetalFacade.guardar(nuevoItem);
			}

			for (AfectacionCoberturaVegetal  item : listaAfectacionConvenios) {
				AfectacionCoberturaVegetal nuevoItem = (AfectacionCoberturaVegetal) SerializationUtils.clone(item);
				nuevoItem.setId(null);
				nuevoItem.setIdInforme(idNuevoInforme);
				
				afectacionCoberturaVegetalFacade.guardar(nuevoItem);
			}

			for (AfectacionCoberturaVegetal  item : listaAfectacionEcosistemas) {
				AfectacionCoberturaVegetal nuevoItem = (AfectacionCoberturaVegetal) SerializationUtils.clone(item);
				nuevoItem.setId(null);
				nuevoItem.setIdInforme(idNuevoInforme);
				
				afectacionCoberturaVegetalFacade.guardar(nuevoItem);
			}
			
			//después de clonar todo los datos cargar la nueva información
			cargarDatos(idNuevoInforme, null);
			return;
		}
		
		if(listaFotosRecorrido != null) {
			for (FotografiaInformeForestal  foto : listaFotosRecorrido) {
				String url = DatatypeConverter.printBase64Binary(foto.getDocImagen().getContenidoDocumento());
				foto.setUrl(url);
			}
		} else 
			listaFotosRecorrido = new ArrayList<>();
		
	}

	public void validarCedulaListener() {
		String cedulaRuc = delegadoOperador.getCedula();
		cedulaDelegadoValida = false;
		delegadoOperador.setNombre(null);

		if (JsfUtil.validarCedulaORUC(cedulaRuc)) {
			try {
				if (cedulaRuc.length() == 10) {
					Cedula cedula = consultaRucCedula.obtenerPorCedulaRC(
							Constantes.USUARIO_WS_MAE_SRI_RC,
							Constantes.PASSWORD_WS_MAE_SRI_RC, cedulaRuc);
					cedulaDelegadoValida = true;
					delegadoOperador.setNombre(cedula.getNombre());
				} else if (cedulaRuc.length() == 13) {
					ContribuyenteCompleto contribuyenteCompleto = consultaRucCedula
							.obtenerPorRucSRI(Constantes.USUARIO_WS_MAE_SRI_RC,
									Constantes.PASSWORD_WS_MAE_SRI_RC,
									cedulaRuc);
					if (contribuyenteCompleto != null) {
						cedulaDelegadoValida = true;
						delegadoOperador.setNombre(contribuyenteCompleto
								.getRazonSocial());
					} else {
						JsfUtil.addMessageError("Error al validar Ruc, Servicio Web No Disponible");
						return;
					}
				}
			} catch (Exception e) {
				JsfUtil.addMessageError("Error al validar Cédula o Ruc");
				return;
			}
		}

		if (!cedulaDelegadoValida) {
			JsfUtil.addMessageError("Error en Cédula o Ruc no válido");
		}
	}
	
	public void nuevoDelegado() {
		delegadoOperador = new DelegadoOperadorForestal();
		cedulaDelegadoValida = false;
	}
	
	public void eliminarDelegado(DelegadoOperadorForestal delegado) {
		try {
			if (delegado.getId() != null) {
				delegado.setEstado(false);
				listaDelegadosOperadorEliminar.add(delegado);
			}
			listaDelegadosOperador.remove(delegado);
		} catch (Exception e) {

		}
	}
	
	public void agregarDelegadoOperador() {
		if(!cedulaDelegadoValida){
			JsfUtil.addMessageError("Debe ingresar una cedula válida. ");
			return;
		}
		
		if (!listaDelegadosOperador.contains(delegadoOperador))
			listaDelegadosOperador.add(delegadoOperador);

		JsfUtil.addCallbackParam("addDelegadoOperador");
	}

	public void editarDelegado(DelegadoOperadorForestal delegado) {
		delegadoOperador = delegado;
		cedulaDelegadoValida = true;
	}
	
	public void nuevoTecnicoDelegado() {
		tecnicoDelegado = new DelegadoTecnicoForestal();
	}
	
	public void eliminarTecnico(DelegadoTecnicoForestal delegado) {
		try {
			if (delegado.getId() != null) {
				delegado.setEstado(false);
				listaTecnicosDelegadosEliminar.add(delegado);
			}
			listaTecnicosDelegados.remove(delegado);
		} catch (Exception e) {

		}
	}
	
	public void buscarUsuario() {
		Usuario usuario =usuarioFacade.buscarUsuario(tecnicoDelegado.getCedula());
		if(usuario!=null)
		{
			tecnicoDelegado.setUsuario(usuario);
			return;
		}
		
		JsfUtil.addMessageError("Usuario no encontrado. ");
	}
	
	public void agregarTecnicoDelegado() {
		
		if(tecnicoDelegado.getUsuario() == null){
			JsfUtil.addMessageError("Debe ingresar un usuario válido. ");
			return;
		}
		
		if (!listaTecnicosDelegados.contains(tecnicoDelegado))
			listaTecnicosDelegados.add(tecnicoDelegado);

		JsfUtil.addCallbackParam("addTecnioDelegado");
	}
	
	public void editarTecnicoDelegado(DelegadoTecnicoForestal tecnico) {
		tecnicoDelegado = tecnico;
	}
	
	//FOTOS
	public void nuevaFotografia(Integer tipoFoto) {
		tipoFotografia = tipoFoto;
		
		nuevaFotografia = new FotografiaInformeForestal();
		nuevaFotografia.setTipoFoto(tipoFoto);
	}
	
	public void uploadFileFoto(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		DocumentoViabilidad documentoImagen = new DocumentoViabilidad(); 
		documentoImagen.setId(null);
		documentoImagen.setContenidoDocumento(contenidoDocumento);
		documentoImagen.setNombre(event.getFile().getFileName());
		documentoImagen.setMime("image/jpg");
		documentoImagen.setIdTipoDocumento(TipoDocumentoSistema.RCOA_VIABILIDAD_FOTOGRAFIA_INFORME_FORESTAL.getIdTipoDocumento());
		
		nuevaFotografia.setDocImagen(documentoImagen);
		nuevaFotografia.setNombre(event.getFile().getFileName());
	}
	
	public void agregarFotografia() {
		if (!listaFotosRecorrido.contains(nuevaFotografia))
			listaFotosRecorrido.add(nuevaFotografia);
		
		JsfUtil.addCallbackParam("addFotografia");
	}
	
	public void validateDatosFoto(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		if (nuevaFotografia == null || nuevaFotografia.getDocImagen() == null)
			errorMessages.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"El campo 'Fotografía' es requerido.", null));
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public void cancelarFotografia() {
		tipoFotografia = null;
		
		nuevaFotografia = new FotografiaInformeForestal();
	}
	
	public void eliminarFotografia(Integer tipoFoto, FotografiaInformeForestal foto) {
		try {
			if (foto.getId() != null) {
				foto.setEstado(false);
				listaImgsEliminar.add(foto);
			}
			
			listaFotosRecorrido.remove(foto);
		} catch (Exception e) {

		}
	}
	
	public void editarFotografia(FotografiaInformeForestal foto) {
		nuevaFotografia = foto;
	}
	
	public StreamedContent descargarFotografia(FotografiaInformeForestal fotografia) throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			
				documentoContent = fotografia.getDocImagen().getContenidoDocumento();
			
			if (fotografia != null && documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(fotografia.getDocImagen().getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	//fin FOTOS
	
	//COORDENADA
	public void nuevaCoordenada() {
		nuevaCoordenada = new CoordenadaForestal();
	}
	
	public void agregarCoordenada() {
		if (!listaCoordenadas.contains(nuevaCoordenada))
			listaCoordenadas.add(nuevaCoordenada);
		
		JsfUtil.addCallbackParam("addCoordenada");
	}
	
	public void eliminarCoordenada(CoordenadaForestal coordenada) {
		try {
			if (coordenada.getId() != null) {
				coordenada.setEstado(false);
				listaCoordenadasEliminar.add(coordenada);
			}
			listaCoordenadas.remove(coordenada);
		} catch (Exception e) {

		}
	}
	
	public void editarCoordenada(CoordenadaForestal coordenada) {
		nuevaCoordenada = coordenada;
	}
	
	public void validateCooordedana(FacesContext context, UIComponent validate, Object value) throws RemoteException {
		List<FacesMessage> errorMessages = new ArrayList<>();
		// validar coordenada
		if (nuevaCoordenada.getCoordenadaX() != null && nuevaCoordenada.getCoordenadaY() != null) {
			String coordenadaX = nuevaCoordenada.getCoordenadaX().toString().replace(",", ".");
			String coordenadaY = nuevaCoordenada.getCoordenadaY().toString().replace(",", ".");

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
	
	public String validarCoordenadaArea(String coordenadaPunto) throws RemoteException {
		// Verificar si las coordenadas estan contenidas en el poligono de implantacion del proyecto
		String mensaje = null;
		Boolean intersecaProyecto = false;

		List<String> listaCoordenadasValidar = new ArrayList<String>();
		listaCoordenadasValidar.add(coordenadaPunto);
		
		
		for (int i = 0; i < listaCoordenadasValidar.size(); i++) {
			for (int j = 0; j < listaCoordenadasProyecto.size(); j++) {
				ContienePoligono_entrada verificarGeoImpla = new ContienePoligono_entrada();
				verificarGeoImpla.setU(Constantes.getUserWebServicesSnap());
				verificarGeoImpla.setTipo("pu");
				verificarGeoImpla.setXy1(listaCoordenadasProyecto.get(j));
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
	//fin COORDENADA
	
	//ESPECIES
	public void desbloquearTaxonomia() {
    	blockFamilia=true;
		blockGenero=true;
		blockEspecie=true;
    	if (registroEspeciesForestales.getNivelTaxonomia().equals(2)) {
			blockFamilia=false;
			blockGenero=false;
			blockEspecie=false;
		}
    	if (registroEspeciesForestales.getNivelTaxonomia().equals(1)) {
    		blockFamilia=false;
			blockGenero=false;
		}
    }
	
	public List<HigherClassification> obtenerListaFamilia() throws ServiceException {
    	List<HigherClassification> familia = new ArrayList<HigherClassification>();
    	familia = higherClassificationFacade.getFamilia();
    	return familia;
    }
	
	public void obtenerListaGeneroPorFamilia() {
    	listGenero = higherClassificationFacade.getByFamilia(objFamilia.getId());
    }
    public void obtenerListaEspecies() {
    	listEspecie = specieTaxaFacade.getByGenero(objGenero.getId());
    	objEspecieOtro.setId(0);
    	objEspecieOtro.setSptaScientificName("Otro");
    	listEspecie.add(objEspecieOtro);
    }
	//fin ESPECIES
	
	//MUESTREO CENSO
	public void nuevoMuestreoCenso() {
		registroEspeciesForestales = new EspeciesInspeccionForestal();
		
		blockFamilia = true;
		blockGenero = true;
		blockEspecie = true;

		objFamilia = new HigherClassification();
		objGenero = new HigherClassification();
		objEspecie = new SpecieTaxa();
	}
	
	public void eliminarMuestreoCenso(EspeciesInspeccionForestal registro) {
		try {
			if (registro.getId() != null) {
				registro.setEstado(false);
				listaEspeciesInspeccionEliminar.add(registro);
			}
			listaEspeciesMuestreoCenso.remove(registro);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void agregarMuestreoCenso() {
		registroEspeciesForestales.setFamiliaEspecie(objFamilia);
    	registroEspeciesForestales.setGeneroEspecie(objGenero);
    	registroEspeciesForestales.setEspecie(objEspecie);
    	
    	if(registroEspeciesForestales.getNivelTaxonomia().equals(1)) {
    		registroEspeciesForestales.setEspecie(null);
    	}
    	
		if (!listaEspeciesMuestreoCenso.contains(registroEspeciesForestales))
			listaEspeciesMuestreoCenso.add(registroEspeciesForestales);

		JsfUtil.addCallbackParam("addMuestreoCenso");
	}

	public void editarMuestreoCenso(EspeciesInspeccionForestal registro) {
		registroEspeciesForestales = registro;
		
		objFamilia = registro.getFamiliaEspecie();
		objGenero = registro.getGeneroEspecie();
		objEspecie = registro.getEspecie();
		
		if(objFamilia != null && objFamilia.getId() != null) {
			obtenerListaGeneroPorFamilia();
			
			if (registro.getNivelTaxonomia().equals(2)) {
				obtenerListaEspecies();
			}
		}
		
		desbloquearTaxonomia();
	}
	//fin MUESTREO CENSO
	
	//CARACTERIZACION
	public void nuevaCaracterizacion() {
		registroEspeciesForestales = new EspeciesInspeccionForestal();
		
		blockFamilia = true;
		blockGenero = true;
		blockEspecie = true;

		objFamilia = new HigherClassification();
		objGenero = new HigherClassification();
		objEspecie = new SpecieTaxa();
	}
	
	public void eliminarCaracterizacion(EspeciesInspeccionForestal registro) {
		try {
			if (registro.getId() != null) {
				registro.setEstado(false);
				listaEspeciesInspeccionEliminar.add(registro);
			}
			listaEspeciesCaracterizacionCualitativa.remove(registro);
		} catch (Exception e) {

		}
	}
	
	public void agregarCaracterizacion() {
		registroEspeciesForestales.setFamiliaEspecie(objFamilia);
    	registroEspeciesForestales.setGeneroEspecie(objGenero);
    	registroEspeciesForestales.setEspecie(objEspecie);
    	
    	if(registroEspeciesForestales.getNivelTaxonomia().equals(1)) {
    		registroEspeciesForestales.setEspecie(null);
    	}
    	
		if (!listaEspeciesCaracterizacionCualitativa.contains(registroEspeciesForestales))
			listaEspeciesCaracterizacionCualitativa.add(registroEspeciesForestales);

		JsfUtil.addCallbackParam("addCaracterizacionCualitativa");
	}

	public void editarCaracterizacion(EspeciesInspeccionForestal registro) {
		registroEspeciesForestales = registro;
		
		objFamilia = registro.getFamiliaEspecie();
		objGenero = registro.getGeneroEspecie();
		objEspecie = registro.getEspecie();
		
		if(objFamilia != null && objFamilia.getId() != null) {
			obtenerListaGeneroPorFamilia();
			
			if (registro.getNivelTaxonomia().equals(2)) {
				obtenerListaEspecies();
			}
		}
		
		desbloquearTaxonomia();
	}
	//fin CARACTERIZACION
	
	// UNIDAD HIDROGRAFICA
	public void nuevaUnidadHidrografica() {
		registroUnidadHidrografica = new UnidadHidrografica();
	}

	public void eliminarUnidadHidrografica(UnidadHidrografica registro) {
		try {
			if (registro.getId() != null) {
				registro.setEstado(false);
				listaUnidadesHidrograficaEliminar.add(registro);
			}
			listaUnidadesHidrografica.remove(registro);
		} catch (Exception e) {

		}
	}

	public void agregarUnidadHidrografica() {
		if (!listaUnidadesHidrografica.contains(registroUnidadHidrografica))
			listaUnidadesHidrografica.add(registroUnidadHidrografica);

		JsfUtil.addCallbackParam("addUnidadHidrografica");
	}

	public void editarUnidadHidrografica(UnidadHidrografica registro) {
		registroUnidadHidrografica = registro;
	}
	
	public void validateUnidadHidrografica(FacesContext context, UIComponent validate, Object value) throws RemoteException {
		List<FacesMessage> errorMessages = new ArrayList<>();
		// validar coordenada
		if (registroUnidadHidrografica.getCoordenadaX() != null && registroUnidadHidrografica.getCoordenadaY() != null) {
			String coordenadaX = registroUnidadHidrografica.getCoordenadaX().toString().replace(",", ".");
			String coordenadaY = registroUnidadHidrografica.getCoordenadaY().toString().replace(",", ".");

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
	// fin UNIDAD HIDROGRAFICA
	
	public Integer getTotalEspeciesSitio(SitioMuestral sitio) {
		List<EspeciesInformeForestal> especiesCodigo = especiesInformeForestalFacade.getListaPorSitioMuestral(sitio.getId());
		
		return especiesCodigo.size();
	}
	
	public void generarInforme(Boolean marcaAgua) {
		try {
			
			if(informeInspeccion == null || informeInspeccion.getId() == null) {
				if(informeInspeccion == null) {
					informeInspeccion = new InformeInspeccionForestal();
				}
				
				informeInspeccion.setNombreFichero(nombreTipoInforme + ".pdf");
				informeInspeccion.setFechaElaboracion(new Date());
			} else {
				informeInspeccion.setNombreFichero(nombreTipoInforme + " " + UtilViabilidad.getFileNameEscaped(informeInspeccion.getNumeroInforme().replace("/", "-")) + ".pdf");
			}
			informeInspeccion.setNombreReporte(nombreTipoInforme + ".pdf");
			
			InformeInspeccionForestalEntity informeEntity = cargarDatosDocumento();

			File informePdfAux = UtilGenerarInforme.generarFichero(
					plantillaReporteInforme.getHtmlPlantilla(),
					informeInspeccion.getNombreReporte(), true, informeEntity);
			
			File informePdf = JsfUtil.fileMarcaAgua(informePdfAux, marcaAgua ? " - - BORRADOR - - " : " ", BaseColor.GRAY);

			Path path = Paths.get(informePdf.getAbsolutePath());
			informeInspeccion.setArchivoInforme(Files.readAllBytes(path));
			String reporteHtmlfinal = informeInspeccion.getNombreFichero().replace("/", "-");
			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(informeInspeccion.getArchivoInforme());
			file.close();
			informeInspeccion.setInformePath(JsfUtil.devolverContexto("/reportesHtml/" + informeInspeccion.getNombreFichero()));
			
			if(filesFotos!= null && filesFotos.size() > 0) {
				for (File foto : filesFotos) {
					foto.delete();
				}
			}
			
			filesFotos = new ArrayList<>();

		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public InformeInspeccionForestalEntity cargarDatosDocumento() throws Exception {
		filesFotos = new ArrayList<>();
		
		informeInspeccion.setFechaElaboracion(new Date());
		
		DateFormat formatoFechaLarga = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));
		DateFormat formatoFechaCorta = new SimpleDateFormat("dd-MM-yyyy");
		
		razonSocial = usuarioFacade.recuperarNombreOperador(proyectoLicenciaCoa.getUsuario()).get(0);
		
		String interseccionesProyecto = interseccionViabilidadCoaFacade.getInterseccionesForestal(proyectoLicenciaCoa.getId(), 2);
		
		List<String> listaTablaDelgados = getTablaDelegados();
		
		InformeInspeccionForestalEntity informeEntity = new InformeInspeccionForestalEntity();
		String tituloInforme = (esInformeViabilidad == null) ? "INFORME DE VIABILIDAD AMBIENTAL" : ((esInformeViabilidad) ? "INFORME DE VIABILIDAD AMBIENTAL" : "INFORME DE OBSERVACIONES");
		
		informeEntity.setTipoInforme(tituloInforme);;
		informeEntity.setNroInforme(informeInspeccion.getNumeroInforme());
		informeEntity.setCiudad(JsfUtil.getLoggedUser().getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
		informeEntity.setFechaElaboracion(formatoFechaLarga.format(informeInspeccion.getFechaElaboracion()));
		
		informeEntity.setCodigoProyecto(proyectoLicenciaCoa.getCodigoUnicoAmbiental());
		informeEntity.setNombreProyecto(proyectoLicenciaCoa.getNombreProyecto());
		informeEntity.setIntersecaProyecto(interseccionesProyecto);
		informeEntity.setRazonSocial(razonSocial);
		informeEntity.setSuperficieProyecto(proyectoLicenciaCoa.getSuperficie().toString());
		informeEntity.setUbicacionProyecto(getTablaUbicacionProyecto());
		informeEntity.setNombreTecnicoRevision(informeInspeccion.getNombreTecnico());
		informeEntity.setCargoTecnicoRevision(informeInspeccion.getCargoTecnico());
		informeEntity.setAreaTecnicoRevision(informeInspeccion.getAreaTecnico());
		
		informeEntity.setAntecedentes(informeInspeccion.getAntecedentes());
		informeEntity.setMarcoLegal(informeInspeccion.getMarcoLegal());
		informeEntity.setObjetivo(informeInspeccion.getObjetivo());
		
		if(viabilidadProyecto.getRequiereInspeccionTecnica()) {
			informeEntity.setDisplayDatosInspeccion("inline");
			if(informeInspeccion.getFechaInspeccion() == null)
				informeEntity.setFechaInspeccion("");
			else
				informeEntity.setFechaInspeccion(formatoFechaCorta.format(informeInspeccion.getFechaInspeccion()));
			
			informeEntity.setDelegadosOperador(listaTablaDelgados.get(0));
			informeEntity.setEquipoTecnico(listaTablaDelgados.get(1));
			informeEntity.setTablaCoordenadas(getTablaCoordenadas());
			
			informeEntity.setVerificacionRecursoForestal(getTablaVerificacionRecursoForestal());
			
			informeEntity.setFotografiasRecorrido(getTablaFotos(listaFotosRecorrido));
		}else {
			informeEntity.setDisplayNoInspeccion("inline");
		}
		
		//informeEntity.setAreaInterseccion(null); //no hay area de interseccion con PFN
		if(informeInspeccion.getHabilitarZonificacionBosque() != null 
				&& informeInspeccion.getHabilitarZonificacionBosque()) {
			informeEntity.setDisplayZonificacion("inline");
			informeEntity.setZonificacionBosque(getTablaZonificacion());
		}
		
		informeEntity.setDetalleZonificacionBosque(informeInspeccion.getCaracteristicasInfraestructura());
		informeEntity.setDetalleTipoCobertura(getTablaCoberturaVegetal());
		informeEntity.setEcosistemasProyecto(getTablaEcosistemas());
		
		if(informeInspeccion.getHabilitarEcosistemasFragiles() != null && informeInspeccion.getHabilitarEcosistemasFragiles()) {
			informeEntity.setDisplayEcosistemasFragiles("inline");
			informeEntity.setDetalleEcosistemasFragiles(informeInspeccion.getDetalleEcosistemasFragiles());
		}
		
		if(informeInspeccion.getHabilitarUnidadesHidrograficas() != null && informeInspeccion.getHabilitarUnidadesHidrograficas()) {
			informeEntity.setDisplayUnidadesHidrograficas("inline");
			informeEntity.setUnidadesHidrograficas(getTablaUnidadesHidrograficas());
		}
		
		informeEntity.setAfectacionConvenios(getTablaAfectacionConvenios());
		
		if(informeInspeccion.getHabilitarDescripcionActividades() != null && informeInspeccion.getHabilitarDescripcionActividades()) {
			informeEntity.setDisplayDescripcionActividades("inline");
			informeEntity.setDescripcionActividades(informeInspeccion.getDescripcionActividades());
		}
		
		if(informeInspeccion.getHabilitarCuantitativaMuestreo() != null && informeInspeccion.getHabilitarCuantitativaMuestreo()) {
			informeEntity.setDisplayCuantitativaMuestreo("inline");
			informeEntity.setDatosCuantitativaMuestreo(getDatosCuantitativaMuestreo());
		}
		
		if(informeInspeccion.getHabilitarCuantitativaCenso() != null && informeInspeccion.getHabilitarCuantitativaCenso()) {
			informeEntity.setDisplayCuantitativaCenso("inline");
			informeEntity.setDatosCuantitativaCenso(getDatosCuantitativaCenso());
		}
		
		if(informeInspeccion.getHabilitarEspeciesImportancia() != null && informeInspeccion.getHabilitarEspeciesImportancia()) {
			informeEntity.setDisplayEspeciesImportancia("inline");
			informeEntity.setEspeciesImportancia(getTablaEspeciesImportancia());
		}
		
		if(informeInspeccion.getHabilitarCaracterizacionCualitativa() != null && informeInspeccion.getHabilitarCaracterizacionCualitativa()) {
			informeEntity.setDisplayCaracterizacionCualitativa("inline");
			informeEntity.setCaracterizacionCualitativa(getTablaCaracterizacionCualitativa());
		}
		
		if(informeInspeccion.getHabilitarAfectacionesPfn() != null && informeInspeccion.getHabilitarAfectacionesPfn()) {
			informeEntity.setDisplayAfectacionPfn("inline");
			informeEntity.setAfectacionPatrimonioForestal(getTablaAfectacionForestal());
		}
		
		informeEntity.setConclusiones(informeInspeccion.getConclusiones());
		informeEntity.setRecomendaciones(informeInspeccion.getRecomendaciones());
		informeEntity.setNombreTecnico(JsfUtil.getLoggedUser().getPersona().getNombre());
		
		if(viabilidadProyecto.getRequiereInspeccionTecnica()) {
			informeEntity.setAnexos(getAnexos());
		} else {
			informeEntity.setAnexos("NA");
		}
		
		return informeEntity;
	}
	
	public String getAnexos() throws Exception {
		StringBuilder stringBuilder = new StringBuilder();
		List<DocumentoViabilidad> documentoImagenMapa = documentosFacade
				.getDocumentoXTablaIdXIdDoc(informeInspeccion.getId(), 
						TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_IMAGEN_MAPA_RECORRIDO, "Informe viabilidad PFN");
		if (documentoImagenMapa != null && documentoImagenMapa.size() > 0) {
			byte[] mapaContent = documentosFacade.descargar(documentoImagenMapa.get(0).getIdAlfresco());
			
			if(mapaContent != null) {
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				
				ByteArrayInputStream inputStreamMapa = new ByteArrayInputStream(mapaContent);
				BufferedImage bImageMapa = ImageIO.read(inputStreamMapa);
				String pathImagenMapa = timestamp.getTime() +"_" + documentoImagenMapa.get(0).getNombre();
				File imageFile = new File(pathImagenMapa);
				ImageIO.write(bImageMapa, "jpg", imageFile);
				
				stringBuilder.append("<img src=\'" + imageFile.getPath() + "\' height=\'200\' width=\'200\' ></img>");
				
				filesFotos.add(imageFile);
			}
		} else {
			stringBuilder.append("");
		}

		return stringBuilder.toString();
	}
	
	public String getTablaUbicacionProyecto() throws Exception {
        String ubicacionCompleta = "";
		
		if (ubicacionesProyecto.size() == 1) {
			UbicacionesGeografica parroquiaU = ubicacionesProyecto.get(0);
			ubicacionCompleta = parroquiaU.getUbicacionesGeografica().getUbicacionesGeografica() + ", " + parroquiaU.getUbicacionesGeografica() + ", " + parroquiaU.getNombre();
		} else {
			ubicacionCompleta = "<table>";
			ubicacionCompleta += "<tr><td>PROVINCIA</td><td>CANTÓN</td><td>PARROQUIA</td></tr>";
			for (UbicacionesGeografica ubicacionActual : ubicacionesProyecto) {
				ubicacionCompleta += "<tr>"
						+ "<td>" + ubicacionActual.getUbicacionesGeografica().getUbicacionesGeografica().getNombre() + "</td>"
						+ "<td>" + ubicacionActual.getUbicacionesGeografica().getNombre() + "</td>"
						+ "<td>" + ubicacionActual.getNombre() + "</td></tr>";
			}

			ubicacionCompleta += "</table>";
		}
		
		return ubicacionCompleta;
	}
	
	public List<String> getTablaDelegados() {
		List<String> delegados = new ArrayList<String>();
		
		StringBuilder stringBuilder = new StringBuilder();
		if(listaDelegadosOperador != null && !listaDelegadosOperador.isEmpty()) {
			stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 90%; border-collapse:collapse;font-size:12px;\">");
			//stringBuilder.append("<tr><th bgcolor=\"#C5C5C5\">Cédula</th><th bgcolor=\"#C5C5C5\">Nombres y Apellidos</th><th bgcolor=\"#C5C5C5\">Cargo</th></tr>");
			stringBuilder.append("<tr><th>Cédula</th><th>Nombres y Apellidos</th><th>Cargo</th></tr>");
			for (DelegadoOperadorForestal delegado : listaDelegadosOperador) {
				stringBuilder.append("<tr>");
				stringBuilder.append("<td>");
				stringBuilder.append(delegado.getCedula());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(delegado.getNombre());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(delegado.getCargo());
				stringBuilder.append("</td>");
				stringBuilder.append("</tr>");
			}
			stringBuilder.append("</table>");
		}
		delegados.add(stringBuilder.toString());
		
		stringBuilder = new StringBuilder();
		if(listaTecnicosDelegados != null && !listaTecnicosDelegados.isEmpty()) {
			stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 90%; border-collapse:collapse;font-size:12px;\">");
			stringBuilder.append("<tr><th>Nombres y Apellidos</th><th>Cargo</th></tr>");
			for (DelegadoTecnicoForestal delegado : listaTecnicosDelegados) {
				stringBuilder.append("<tr>");
				stringBuilder.append("<td>");
				stringBuilder.append(delegado.getUsuario().getPersona().getNombre());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(delegado.getCargo());
				stringBuilder.append("</td>");
				stringBuilder.append("</tr>");
			}
			stringBuilder.append("</table>");
		}
		
		delegados.add(stringBuilder.toString());
		
		return delegados;
	}
	
	public String getTablaCoordenadas() {
		StringBuilder stringBuilder = new StringBuilder();
		if(listaCoordenadas != null && !listaCoordenadas.isEmpty()) {
			stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:12px;\">");
			stringBuilder.append("<tr><th>Número</th><th>Coordenada X</th><th>Coordenada Y</th><th>Descripción</th><th>Elevación</th></tr>");
			int nro=1;
			for (CoordenadaForestal coordenada : listaCoordenadas) {
				stringBuilder.append("<tr>");
				stringBuilder.append("<td style=\"text-align: center;\">" + nro + "</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(coordenada.getCoordenadaX());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(coordenada.getCoordenadaY());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(coordenada.getDescripcion());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(coordenada.getElevacion());
				stringBuilder.append("</td>");
				stringBuilder.append("</tr>");
				
				nro++;
			}
			stringBuilder.append("</table>");
		}
		
		return stringBuilder.toString();
	}
	
	public String getTablaFotos(List<FotografiaInformeForestal> listaFotos) throws Exception {
		StringBuilder stringBuilder = new StringBuilder();
		
		if(listaFotos != null && listaFotos.size() > 0) {
			DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			
			String tamanioTabla = (listaFotos.size() >= 2) ? "width: 100%;" :  "width: 50%;";
			
			stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\""+tamanioTabla+" border-collapse:collapse;font-size:12px;\">");
			
			for (int i = 0; i < listaFotos.size(); i++) {
				stringBuilder.append("<tr>");
				StringBuilder stringBuilderDescripcion = new StringBuilder();
				for (int j = 0; j < 2; j++) {
					FotografiaInformeForestal foto = null;
					if(i < listaFotos.size())
						foto = listaFotos.get(i);
					
					if(foto != null) {
						Timestamp timestamp = new Timestamp(System.currentTimeMillis());
						
						if(foto.getDocImagen().getContenidoDocumento() == null) {
							foto.getDocImagen().setContenidoDocumento(documentosFacade.descargar(foto.getDocImagen().getIdAlfresco(), foto.getDocImagen().getFechaCreacion()));
						}
						
						ByteArrayInputStream inputStreamFirma = new ByteArrayInputStream(foto.getDocImagen().getContenidoDocumento());
						BufferedImage bImageFirma = ImageIO.read(inputStreamFirma);
						File imageFile = new File(timestamp.getTime() +"_" + foto.getDocImagen().getNombre());
						ImageIO.write(bImageFirma, "jpg", imageFile);
						
						stringBuilder.append("<td style=\"text-align: center; width: 50%\">");
						stringBuilder.append("<img src=\'" + imageFile.getPath() + "\' height=\'160\' width=\'160\' ></img>");
						stringBuilder.append("</td>");
						
						stringBuilderDescripcion.append("<td style=\"text-align: justify;\">");
						stringBuilderDescripcion.append((i + 1) + ". " + foto.getDescripcion());
						stringBuilderDescripcion.append("<br />Fuente: Visita técnica " + dateFormat.format(foto.getFechaFotografia()));
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
	
	public String getTablaVerificacionRecursoForestal() {
		StringBuilder stringBuilder = new StringBuilder();
		
		List<EspeciesInspeccionForestal> listaEspecies = new ArrayList<EspeciesInspeccionForestal>();
		
		Boolean esMuestreo = (informeInspeccion.getEsMuestreoCenso() != null) ? informeInspeccion.getEsMuestreoCenso() : false;
		Boolean esCualitativa = (informeInspeccion.getEsCaracterizacionCualitativa() != null) ? informeInspeccion.getEsCaracterizacionCualitativa() : false;
		
		if(esMuestreo) {
			listaEspecies = listaEspeciesMuestreoCenso;
			
			stringBuilder.append("<strong>Muestreo o censo</strong> <br />");
			
			if(listaEspecies != null && !listaEspecies.isEmpty()) {
				stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:12px;\">");
				stringBuilder.append("<tr><th>Código unidad muestral</th><th>Número de registro del individuo</th>"
						+ "<th>Familia</th><th>Género</th><th>Especie</th>"
						+ "<th>Nombre común</th><th>DAP (m)</th><th>HC (m)</th>"
						+ "<th>HT (m)</th>"
						+ "</tr>");
				int nroRegistro = 1;
				for (EspeciesInspeccionForestal registro : listaEspecies) {
					stringBuilder.append("<tr>");
					stringBuilder.append("<td>" + registro.getNombreSitio() + "</td>");
					stringBuilder.append("<td style=\"text-align: center;\">");
					stringBuilder.append(nroRegistro);
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
					
					stringBuilder.append("</tr>");
				}
				stringBuilder.append("</table>");
			}
		}
		
		if(esCualitativa) {
			listaEspecies = listaEspeciesCaracterizacionCualitativa;
			
			if(esMuestreo) {
				stringBuilder.append("<br /><br />");
			}
			
			stringBuilder.append("<strong>Caracterización Cualitativa</strong> <br />");
			
			if(listaEspecies != null && !listaEspecies.isEmpty()) {
				stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:12px;\">");
				stringBuilder.append("<tr><th>Familia</th><th>Género</th><th>Especie</th>"
						+ "<th>Nombre común</th></tr>");
				for (EspeciesInspeccionForestal registro : listaEspecies) {
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
					stringBuilder.append(registro.getNombreComun());
					stringBuilder.append("</td>");
					stringBuilder.append("</tr>");
				}
				stringBuilder.append("</table>");
			}
		}
		
		
		
		return stringBuilder.toString();
	}
	
	public String getTablaZonificacion() {
		StringBuilder stringBuilder = new StringBuilder();
		
		String valorZona = (informeInspeccion.getBpTieneZonificacion() != null) ? ((informeInspeccion.getBpTieneZonificacion()) ? "SI" : "NO") : "";
		String valorPlan = (informeInspeccion.getBpTienePlanManejo() != null) ? ((informeInspeccion.getBpTieneZonificacion()) ? "SI" : "NO") : "";
		String valorInfraestructura = (informeInspeccion.getBpTieneInfraestructura() != null) ? ((informeInspeccion.getBpTieneZonificacion()) ? "SI" : "NO") : "";
		
		stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:12px;\">");
		stringBuilder.append("<tr>");
		stringBuilder.append("<td>El bosque protector tiene zonificación</td>");
		stringBuilder.append("<td style=\"text-align: center;\">" + valorZona + "</td>");
		stringBuilder.append("</tr>");
		stringBuilder.append("<tr>");
		stringBuilder.append("<td>El bosque protector cuenta con Plan de Manejo Integral</td>");
		stringBuilder.append("<td style=\"text-align: center;\">" + valorPlan + "</td>");
		stringBuilder.append("</tr>");
		stringBuilder.append("<tr>");
		stringBuilder.append("<td>Existe infraestructura del proyecto</td>");
		stringBuilder.append("<td style=\"text-align: center;\">" + valorInfraestructura + "</td>");
		stringBuilder.append("</tr>");
		
		stringBuilder.append("</table>");
		
		return stringBuilder.toString();
	}
	
	public String getTablaCoberturaVegetal() {
		StringBuilder stringBuilder = new StringBuilder();
		if(listaAfectacionCoberturaVegetal != null && !listaAfectacionCoberturaVegetal.isEmpty()) {
			stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:12px;\">");
			stringBuilder.append("<tr><th>Tipo de cobertura vegetal y uso de suelo</th><th>Superficie total de la cobertura vegetal y uso de suelo </th>"
					+ "<th>Superficie verificada por el técnico</th><th>Afectación a la cobertura vegetal</th></tr>");
			for (AfectacionCoberturaVegetal item : listaAfectacionCoberturaVegetal) {
				String afectacion = (item.getAfectacion() != null) ? ((item.getAfectacion()) ? "SI" : "NO") : ""; 
				
				stringBuilder.append("<tr>");
				stringBuilder.append("<td>" + item.getDetalleInterseccion().getNombreGeometria() + "</td>");
				stringBuilder.append("<td style=\"text-align: center;\">");
				stringBuilder.append(String.format("%.2f", item.getDetalleInterseccion().getPorcentajeAreaInterseccion() * proyectoLicenciaCoa.getSuperficie().doubleValue()));
				stringBuilder.append("</td>");
				stringBuilder.append("<td style=\"text-align: center;\">");
				stringBuilder.append((item.getSuperficieDescripcion() == null) ? "" : item.getSuperficieDescripcion());
				stringBuilder.append("</td>");
				stringBuilder.append("<td style=\"width: 10%;\">");
				stringBuilder.append(afectacion);
				stringBuilder.append("</td>");
				stringBuilder.append("</tr>");
			}
			stringBuilder.append("</table>");
		}
		
		return stringBuilder.toString();
	}
	
	public String getTablaEcosistemas() {
		StringBuilder stringBuilder = new StringBuilder();
		if(listaAfectacionEcosistemas != null && !listaAfectacionEcosistemas.isEmpty()) {
			stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:12px;\">");
			stringBuilder.append("<tr><th>Código del ecosistema</th><th>Nombre del ecosistema</th>"
					+ "<th>Superficie total del ecosistema</th>"
					+ "<th>Superficie verificada por el técnico</th><th>Afectación al ecosistema</th></tr>");
			for (AfectacionCoberturaVegetal item : listaAfectacionEcosistemas) {
				String afectacion = (item.getAfectacion() != null) ? ((item.getAfectacion()) ? "SI" : "NO") : ""; 
				
				stringBuilder.append("<tr>");
				stringBuilder.append("<td>" + item.getDetalleInterseccion().getCodigoConvenio() + "</td>");
				stringBuilder.append("<td>" + item.getDetalleInterseccion().getNombreGeometria() + "</td>");
				stringBuilder.append("<td style=\"text-align: center;\">");
				stringBuilder.append(String.format("%.2f", item.getDetalleInterseccion().getPorcentajeAreaInterseccion() * proyectoLicenciaCoa.getSuperficie().doubleValue()));
				stringBuilder.append("</td>");
				stringBuilder.append("<td style=\"text-align: center;\">");
				stringBuilder.append((item.getSuperficieDescripcion() == null) ? "" : item.getSuperficieDescripcion());
				stringBuilder.append("</td>");
				stringBuilder.append("<td style=\"width: 10%;\">");
				stringBuilder.append(afectacion);
				stringBuilder.append("</td>");
				stringBuilder.append("</tr>");
			}
			stringBuilder.append("</table>");
		}
		
		return stringBuilder.toString();
	}
	
	public String getTablaUnidadesHidrograficas() {
		StringBuilder stringBuilder = new StringBuilder();
		if(listaUnidadesHidrografica != null && !listaUnidadesHidrografica.isEmpty()) {
			stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:12px;\">");
			stringBuilder.append("<tr><th>Nombre de la unidad hidrográfica</th><th>Características de la unidad hidrográfica</th>"
					+ "<th>Coordenada X</th><th>Coordenada Y</th><th>Afectación a la unidad hidrográfica</th></tr>");
			
			for (UnidadHidrografica item : listaUnidadesHidrografica) {
				String caracteristica = (item.getCaracteristica().equals(1)) ? "Permanente" : "Estacional" ;
				String afectacion = (item.getAfectacion()) ? "SI" : "NO" ;
				
				stringBuilder.append("<tr>");
				stringBuilder.append("<td style=\"text-align: center;\">" + item.getNombre() + "</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(caracteristica);
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(item.getCoordenadaX());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(item.getCoordenadaY());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(afectacion);
				stringBuilder.append("</td>");
				stringBuilder.append("</tr>");
			}
			stringBuilder.append("</table>");
		}
		
		return stringBuilder.toString();
	}
	
	public String getTablaAfectacionConvenios() {
		StringBuilder stringBuilder = new StringBuilder();
		if(listaAfectacionConvenios != null && !listaAfectacionConvenios.isEmpty()) {
			stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:12px;\">");
			stringBuilder.append("<tr><th>Tipo de convenio</th><th>Código de convenio</th>"
					+ "<th>Nombre del beneficiario</th>"
					+ "<th>Afectación a la cobertura vegetal</th></tr>");
			for (AfectacionCoberturaVegetal item : listaAfectacionConvenios) {
				String afectacion = (item.getAfectacion() != null) ? ((item.getAfectacion()) ? "SI" : "NO") : "";
				
				stringBuilder.append("<tr>");
				stringBuilder.append("<td>" + item.getDetalleInterseccion().getTipoConvenio() + "</td>");
				stringBuilder.append("<td>" + item.getDetalleInterseccion().getCodigoConvenio() + "</td>");
				stringBuilder.append("<td>" + item.getDetalleInterseccion().getBeneficiarioConvenio() + "</td>");
				stringBuilder.append("<td style=\"width: 10%;\">");
				stringBuilder.append(afectacion);
				stringBuilder.append("</td>");
				stringBuilder.append("</tr>");
			}
			stringBuilder.append("</table>");
		}
		
		return stringBuilder.toString();
	}
	
	public String getDatosCuantitativaMuestreo() {
		StringBuilder stringBuilder = new StringBuilder();
		
		if(listaSitiosMuestrales != null && !listaSitiosMuestrales.isEmpty()) {
			stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:12px;\">");
			
			stringBuilder.append("<tr><th colspan=\"6\">Análisis de área basal y volumen</th></tr>");
			stringBuilder.append("<tr><th>Código unidad muestral</th><th>Superficie de unidad muestral (m2)</th>"
					+ "<th>Número de individuos de la unidad muestral</th><th>Área basal (m2)</th>"
					+ "<th>Volumen comercial (m3)</th><th>Volumen total (m3)</th></tr>");
			for (SitioMuestral item : listaSitiosMuestrales) {
				stringBuilder.append("<tr>");
				stringBuilder.append("<td>" + item.getNombreSitio() + "</td>");
				stringBuilder.append("<td>" + item.getAreaSitio() + "</td>");
				stringBuilder.append("<td>" + getTotalEspeciesSitio(item) + "</td>");
				stringBuilder.append("<td>" + item.getSumatoriaAreaBasal() + "</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(item.getSumatoriaVolumenComercial());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(item.getSumatoriaVolumenTotal());
				stringBuilder.append("</td>");
				stringBuilder.append("</tr>");
			}
			stringBuilder.append("</table>");
		}
		
		if(listaPromediosAreaBasalVolumen != null && !listaPromediosAreaBasalVolumen.isEmpty()) {
			stringBuilder.append("<br />");
			stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 700px; border-collapse:collapse;font-size:10px;\">");
			
			stringBuilder.append("<tr><th colspan=\"6\">Promedios del área basal y volumen</th></tr>");
			stringBuilder.append("<tr><th>Promedio del área basal m2</th><th>Promedio del Volumen total m3</th>"
					+ "<th>Promedio del Volumen comercial m3</th><th>Valor de área basal por hectárea m2</th>"
					+ "<th>Valor del volumen total por hectárea</th><th>Valor del volumen comercial por hectárea</th></tr>");
			for (ResultadoRevisionForestal item : listaPromediosAreaBasalVolumen) {
				stringBuilder.append("<tr>");
				stringBuilder.append("<td>" + item.getPromedioAreaBasal() + "</td>");
				stringBuilder.append("<td>" + item.getPromedioVolumenT() + "</td>");
				stringBuilder.append("<td>" + item.getPromedioVolumenC() + "</td>");
				stringBuilder.append("<td>" + item.getAreaBasalPorH() + "</td>");
				stringBuilder.append("<td>" + item.getVolumenTotalPorH() + "</td>");
				stringBuilder.append("<td>" + item.getVolumenComercialPorH() + "</td>");
				stringBuilder.append("</tr>");
			}
			stringBuilder.append("</table>");
		}
		
		if(listaResultadosIvi != null && !listaResultadosIvi.isEmpty()) {
			stringBuilder.append("<br />");
			stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:12px;\">");
			stringBuilder.append("<tr><th colspan=\"7\">Análisis de Índice de valor de importancia</th></tr>");
			stringBuilder.append("<tr><th>Código de unidad muestral</th>"
					+ "<th>Especie de mayor porcentaje DnR</th><th>Valor %</th>"
					+ "<th>Especie de mayor porcentaje DmR</th><th>Valor %</th>"
					+ "<th>Especie de mayor porcentaje IVI</th><th>Valor %</th></tr>");
			for (ResultadoRevisionForestal item : listaResultadosIvi) {
				stringBuilder.append("<tr>");
				stringBuilder.append("<td>" + item.getNombreSitio() + "</td>");
				stringBuilder.append("<td>" + item.getValorDnr() + "</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(item.getEspecieDnr());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(item.getValorDmr());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(item.getEspecieDmr());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(item.getEspecieIvi());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(item.getValorIvi());
				stringBuilder.append("</td>");
				stringBuilder.append("</tr>");
			}
			stringBuilder.append("</table>");
		}
		
		if(listaInterpretacionIndices != null && !listaInterpretacionIndices.isEmpty()) {
			List<String> listaValores = new ArrayList<String>();
			listaValores.add("Alto");
			listaValores.add("Medio");
			listaValores.add("Bajo");
			
			stringBuilder.append("<br />");
			stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:12px;\">");
			stringBuilder.append("<tr><th colspan=\"7\">Análisis de Índice de valor de importancia</th></tr>");
			stringBuilder.append("<tr><th>Código de unidad muestral</th>"
					+ "<th>Número de individuos de la unidad muestral</th><th>Índice de Shannon</th>"
					+ "<th>Interpretación</th><th>Exponencial de Shannon</th>"
					+ "<th>Índice de Simpson</th><th>Interpretación</th>"
					+ "<th>Inverso de Simpson</th></tr>");
			for (InterpretacionIndiceForestal item : listaInterpretacionIndices) {
				String valor1 = (item.getInterpretacionShannon() != null) ? (listaValores.get(item.getInterpretacionShannon() - 1)) : "";
				String valor2 = (item.getInterpretacionSimpson() != null) ? (listaValores.get(item.getInterpretacionSimpson() - 1)) : "";
				String valorInversoSimpson = (item.getSitio().getIndiceSimpson().equals(0.0)) ? "Indefinido" : item.getSitio().getInversoSimpson().toString(); 
				
				stringBuilder.append("<tr>");
				stringBuilder.append("<td>" + item.getSitio().getNombreSitio() + "</td>");
				stringBuilder.append("<td>" + item.getNroIndividuos() + "</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(item.getSitio().getIndiceShanon());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(valor1);
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(item.getSitio().getExponencialShannon());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(item.getSitio().getIndiceSimpson());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(valor2);
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(valorInversoSimpson);
				stringBuilder.append("</td>");
				stringBuilder.append("</tr>");
			}
			stringBuilder.append("</table>");
		}
		
		return stringBuilder.toString();
	}
	
	public String getDatosCuantitativaCenso() {
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append("Superficie del censo: " + proyectoLicenciaCoa.getSuperficie());
		
		if(listaSitiosMuestralesCenso != null && !listaSitiosMuestralesCenso.isEmpty()) {
			stringBuilder.append(stringTablasViabilidad.getTablaAreaBasalCenso( listaEspeciesCenso));
			stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:12px;\">");
			stringBuilder.append("<tr><th colspan=\"4\">Resultados de caracterización cuantitativa</th></tr>");
			stringBuilder.append("<tr><th>Número de individuos</th><th>Área basal (m2)</th>"
					+ "<th>Volumen comercial (m3)</th><th>Volumen total (m3)</th></tr>");
			for (SitioMuestral item : listaSitiosMuestralesCenso) {
				stringBuilder.append("<tr>");
				stringBuilder.append("<td>" + item.getNroIndividuos() + "</td>");
				stringBuilder.append("<td>" + item.getSumatoriaAreaBasal() + "</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(item.getSumatoriaVolumenComercial());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(item.getSumatoriaVolumenTotal());
				stringBuilder.append("</td>");
				stringBuilder.append("</tr>");
			}
			stringBuilder.append("</table>");
		}
		
		if(listaResultadosIviCenso != null && !listaResultadosIviCenso.isEmpty()) {
			stringBuilder.append("<br />");
			stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:12px;\">");
			stringBuilder.append("<tr><th colspan=\"6\">Análisis de Índice de valor de importancia</th></tr>");
			stringBuilder.append("<tr><th>Especie de mayor porcentaje DnR</th><th>Valor %</th>"
					+ "<th>Especie de mayor porcentaje DmR</th><th>Valor %</th>"
					+ "<th>Especie de mayor porcentaje IVI</th><th>Valor %</th></tr>");
			for (ResultadoRevisionForestal item : listaResultadosIviCenso) {
				stringBuilder.append("<tr>");
				stringBuilder.append("<td>" + item.getValorDnr() + "</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(item.getEspecieDnr());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(item.getValorDmr());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(item.getEspecieDmr());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(item.getEspecieIvi());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(item.getValorIvi());
				stringBuilder.append("</td>");
				stringBuilder.append("</tr>");
			}
			stringBuilder.append("</table>");
		}
		
		if(listaInterpretacionIndicesCenso != null && !listaInterpretacionIndicesCenso.isEmpty()) {
			List<String> listaValores = new ArrayList<String>();
			listaValores.add("Alto");
			listaValores.add("Medio");
			listaValores.add("Bajo");
			
			stringBuilder.append("<br />");
			stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:12px;\">");
			stringBuilder.append("<tr><th colspan=\"7\">Análisis de índices de diversidad</th></tr>");
			stringBuilder.append("<tr><th>Número de individuos de la unidad muestral</th><th>Índice de Shannon</th>"
					+ "<th>Interpretación</th><th>Exponencial de Shannon</th>"
					+ "<th>Índice de Simpson</th><th>Interpretación</th>"
					+ "<th>Inverso de Simpson</th></tr>");
			for (InterpretacionIndiceForestal item : listaInterpretacionIndicesCenso) {
				String valor1 = (item.getInterpretacionShannon() != null) ? (listaValores.get(item.getInterpretacionShannon() - 1)) : "";
				String valor2 = (item.getInterpretacionSimpson() != null) ? (listaValores.get(item.getInterpretacionSimpson() - 1)) : "";
				String valorInversoSimpson = (item.getSitio().getIndiceSimpson().equals(0.0)) ? "Indefinido" : item.getSitio().getInversoSimpson().toString();
				
				stringBuilder.append("<tr>");
				stringBuilder.append("<td>" + item.getNroIndividuos() + "</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(item.getSitio().getIndiceShanon());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(valor1);
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(item.getSitio().getExponencialShannon());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(item.getSitio().getIndiceSimpson());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(valor2);
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(valorInversoSimpson);
				stringBuilder.append("</td>");
				stringBuilder.append("</tr>");
			}
			stringBuilder.append("</table>");
		}
		
		return stringBuilder.toString();
	}
	
	public String getTablaEspeciesImportancia () {
		StringBuilder stringBuilder = new StringBuilder();
		if(listaEspeciesImportancia != null && !listaEspeciesImportancia.isEmpty()) {
			stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:12px;\">");
			stringBuilder.append("<tr><th>Familia</th><th>Género</th><th>Especie</th>"
					+ "<th>Frecuencia</th><th>Estado de conservación UICN</th>"
					+ "<th>Apéndice CITES</th><th>Aprovechamiento condicionado</th><th>Especies endémicas</th></tr>");
			for (EspeciesImportanciaForestal item : listaEspeciesImportancia) {
				stringBuilder.append("<tr>");
				stringBuilder.append("<td>" + item.getFamiliaEspecie().getHiclScientificName() + "</td>");
				stringBuilder.append("<td>" + item.getGeneroEspecie().getHiclScientificName() + "</td>");
				stringBuilder.append("<td>");
				stringBuilder.append((item.getEspecie() != null) ? item.getEspecie().getSptaScientificName() : "");
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(item.getFrecuencia());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(item.getEstadoConservacion().getNombre());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(item.getEstadoCites().getNombre());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append((item.getAprovechamientoCondicionado() != null) ? (item.getAprovechamientoCondicionado() ? "SI" : "NO") : "");
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append((item.getEspecieEndemica() != null) ? (item.getEspecieEndemica() ? "SI" : "NO") : "");
				stringBuilder.append("</td>");
				stringBuilder.append("</tr>");
			}
			stringBuilder.append("</table>");
		}
		
		return stringBuilder.toString();
	}
	
	public String getTablaCaracterizacionCualitativa () {
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:12px;\">");
		stringBuilder.append("<tr><th>Código o nombre del sitio</th><th>Familia</th><th>Género</th><th>Especie</th>"
				+ "<th>Frecuencia</th><th>Estado de conservación UICN</th>"
				+ "<th>Apéndice CITES</th><th>Aprovechamiento condicionado</th><th>Especies endémicas</th></tr>");
		
		if(listaEspeciesCualitativa != null && !listaEspeciesCualitativa.isEmpty()) {
			for (EspeciesInformeForestal item : listaEspeciesCualitativa) {
				stringBuilder.append("<tr>");
				stringBuilder.append("<td>" + item.getSitioMuestral().getNombreSitio() + "</td>");
				stringBuilder.append("<td>" + item.getFamiliaEspecie().getHiclScientificName() + "</td>");
				stringBuilder.append("<td>" + item.getGeneroEspecie().getHiclScientificName() + "</td>");
				stringBuilder.append("<td>");
				stringBuilder.append((item.getEspecie() != null) ? item.getEspecie().getSptaScientificName() : "");
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(item.getFrecuencia());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(item.getEstadoConservacion().getNombre());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(item.getEstadoCites().getNombre());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append((item.getEspecieEndemica() != null) ? (item.getEspecieEndemica() ? "SI" : "NO") : "");
				stringBuilder.append("</td>");
				stringBuilder.append("</tr>");
			}
		}
		
		stringBuilder.append("</table>");
		
		return stringBuilder.toString();
	}
	
	public String getTablaAfectacionForestal() {
		StringBuilder stringBuilder = new StringBuilder();
		if(listaAfectacionForestal != null && !listaAfectacionForestal.isEmpty()) {
			stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:12px;\">");
			stringBuilder.append("<tr><th>Actividad</th>"
					+ "<th>Descripción de la posible afectación al Patrimonio Forestal Nacional</th>"
					+ "<th>Medida propuesta</th></tr>");
			for (AfectacionForestal item : listaAfectacionForestal) {
				stringBuilder.append("<tr>");
				stringBuilder.append("<td>" + item.getActividad() + "</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(item.getDescripcionAfectacionTecnico() != null ? item.getDescripcionAfectacionTecnico() : "");
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(item.getMedidaPropuestaTecnico() != null ? item.getMedidaPropuestaTecnico() : "");
				stringBuilder.append("</td>");
				stringBuilder.append("</tr>");
			}
			stringBuilder.append("</table>");
		}
		
		return stringBuilder.toString();
	}
	
	public void showCuantitativaMuestreo() {
		if(informeInspeccion.getHabilitarCuantitativaMuestreo() != null && informeInspeccion.getHabilitarCuantitativaMuestreo()) {
			if(informeFactibilidadForestal.getCaracterizacionCobertura().equals(InformeFactibilidadForestal.caracterizacionCuantitativa) 
					&& informeFactibilidadForestal.getRecoleccionDatos().equals(InformeFactibilidadForestal.recoleccionMuestreo)) {
				listaSitiosMuestrales = sitioMuestralFacade.getSitiosPorInformeTipo(informeFactibilidadForestal.getId(), SitioMuestral.registroCuantitativoMuestreo);
				
				if(listaSitiosMuestrales != null && listaSitiosMuestrales.size() > 0) {
					listaPromediosAreaBasalVolumen = new ArrayList<ResultadoRevisionForestal>();
					listaResultadosIvi = new ArrayList<ResultadoRevisionForestal>();
					
					int totalRegistros = listaSitiosMuestrales.size();
					Double sumaAreaBasal = 0.0;
					Double sumaVolumenT = 0.0;
					Double sumaVolumenC = 0.0;
					Double areaTotalMuestreo = 0.0;
					AnalisisResultadoForestal mayorDnr, mayorDmr, mayorIvi;
					for (SitioMuestral sitio : listaSitiosMuestrales) {
						sumaAreaBasal += sitio.getSumatoriaAreaBasal();
						sumaVolumenT += sitio.getSumatoriaVolumenTotal();
						sumaVolumenC += sitio.getSumatoriaVolumenComercial();
						
						areaTotalMuestreo += sitio.getAreaSitio();
						
						mayorDnr = analisisResultadoForestalFacade.getEspecieMayorValor(sitio.getId(), 1);
						mayorDmr = analisisResultadoForestalFacade.getEspecieMayorValor(sitio.getId(), 2);
						mayorIvi = analisisResultadoForestalFacade.getEspecieMayorValor(sitio.getId(), 3);
						
						ResultadoRevisionForestal mayorEspecies = new ResultadoRevisionForestal();
						mayorEspecies.setNombreSitio(sitio.getNombreSitio());
						mayorEspecies.setEspecieDnr((mayorDnr.getEspecie() == null) ? mayorDnr.getGeneroEspecie().getHiclScientificName() : mayorDnr.getEspecie().getSptaScientificName());
						mayorEspecies.setEspecieDmr((mayorDmr.getEspecie() == null) ? mayorDmr.getGeneroEspecie().getHiclScientificName() : mayorDmr.getEspecie().getSptaScientificName());
						mayorEspecies.setEspecieIvi((mayorIvi.getEspecie() == null) ? mayorIvi.getGeneroEspecie().getHiclScientificName() : mayorIvi.getEspecie().getSptaScientificName());
						mayorEspecies.setValorDnr(mayorDnr.getValorDnr());
						mayorEspecies.setValorDmr(mayorDmr.getValorDmr());
						mayorEspecies.setValorIvi(mayorIvi.getValorIvi());
						
						listaResultadosIvi.add(mayorEspecies);
						
					}
					
					ResultadoRevisionForestal resultado = new ResultadoRevisionForestal();
					resultado.setPromedioAreaBasal(sumaAreaBasal/totalRegistros);
					resultado.setPromedioVolumenC(sumaVolumenC/totalRegistros);
					resultado.setPromedioVolumenT(sumaVolumenT/totalRegistros);
					
					Double areaBasalPorH = (resultado.getPromedioAreaBasal() * 10000) /areaTotalMuestreo;
					Double volumenTotalPorH = (resultado.getPromedioVolumenT() * 10000) /areaTotalMuestreo;
					Double volumenComercialPorH = (resultado.getPromedioVolumenC() * 10000) /areaTotalMuestreo;
					resultado.setAreaBasalPorH(areaBasalPorH);
					resultado.setVolumenTotalPorH(volumenTotalPorH);
					resultado.setVolumenComercialPorH(volumenComercialPorH);
					
					listaPromediosAreaBasalVolumen.add(resultado);
					
					if(listaInterpretacionIndices == null || listaInterpretacionIndices.size() == 0) {
						listaInterpretacionIndices = new ArrayList<InterpretacionIndiceForestal>();
						
						for (SitioMuestral sitio : listaSitiosMuestrales) {
							List<EspeciesInformeForestal> especiesSitio = especiesInformeForestalFacade.getListaPorSitioMuestral(sitio.getId());
							InterpretacionIndiceForestal interpretacion = new InterpretacionIndiceForestal();
							interpretacion.setSitio(sitio);
							interpretacion.setIdInformeInspeccion(informeInspeccion.getId());
							interpretacion.setNroIndividuos(especiesSitio.size());
							
							listaInterpretacionIndices.add(interpretacion);
						}
					}
				}
			} else {
				listaSitiosMuestrales = new ArrayList<>();
				listaPromediosAreaBasalVolumen = new ArrayList<ResultadoRevisionForestal>();
				listaResultadosIvi = new ArrayList<ResultadoRevisionForestal>();
			}
		}
	}
	
	public void showCuantitativaCenso() {
		if(informeInspeccion.getHabilitarCuantitativaCenso() != null && informeInspeccion.getHabilitarCuantitativaCenso()) {
			if(informeFactibilidadForestal.getCaracterizacionCobertura().equals(InformeFactibilidadForestal.caracterizacionCuantitativa) 
					&& informeFactibilidadForestal.getRecoleccionDatos().equals(InformeFactibilidadForestal.recoleccionCenso)) {
				
				listaSitiosMuestralesCenso = sitioMuestralFacade.getSitiosPorInformeTipo(informeFactibilidadForestal.getId(), SitioMuestral.registroCuantitativoCenso);
				
				if(listaSitiosMuestralesCenso != null && listaSitiosMuestralesCenso.size() > 0) {
					SitioMuestral sitioCenso = listaSitiosMuestralesCenso.get(0);
					
					List<EspeciesInformeForestal> especiesCenso = especiesInformeForestalFacade.getListaPorSitioMuestral(sitioCenso.getId());
					int totalIndividuos = especiesCenso.size();
					
					listaSitiosMuestralesCenso.get(0).setNroIndividuos(totalIndividuos);
					
					listaResultadosIviCenso = new ArrayList<ResultadoRevisionForestal>();
					
					AnalisisResultadoForestal mayorDnr = analisisResultadoForestalFacade.getEspecieMayorValor(sitioCenso.getId(), 1);
					AnalisisResultadoForestal mayorDmr = analisisResultadoForestalFacade.getEspecieMayorValor(sitioCenso.getId(), 2);
					AnalisisResultadoForestal mayorIvi = analisisResultadoForestalFacade.getEspecieMayorValor(sitioCenso.getId(), 3);
					
					ResultadoRevisionForestal mayorEspecies = new ResultadoRevisionForestal();
					mayorEspecies.setNombreSitio(sitioCenso.getNombreSitio());
					mayorEspecies.setEspecieDnr((mayorDnr.getEspecie() == null) ? mayorDnr.getGeneroEspecie().getHiclScientificName() : mayorDnr.getEspecie().getSptaScientificName());
					mayorEspecies.setEspecieDmr((mayorDmr.getEspecie() == null) ? mayorDmr.getGeneroEspecie().getHiclScientificName() : mayorDmr.getEspecie().getSptaScientificName());
					mayorEspecies.setEspecieIvi((mayorIvi.getEspecie() == null) ? mayorIvi.getGeneroEspecie().getHiclScientificName() : mayorIvi.getEspecie().getSptaScientificName());
					mayorEspecies.setValorDnr(mayorDnr.getValorDnr());
					mayorEspecies.setValorDmr(mayorDmr.getValorDmr());
					mayorEspecies.setValorIvi(mayorIvi.getValorIvi());
					
					listaResultadosIviCenso.add(mayorEspecies);
					
					if(listaInterpretacionIndicesCenso == null || listaInterpretacionIndicesCenso.size() == 0) {
						listaInterpretacionIndicesCenso = new ArrayList<InterpretacionIndiceForestal>();
						
						InterpretacionIndiceForestal interpretacion = new InterpretacionIndiceForestal();
						interpretacion.setSitio(sitioCenso);
						interpretacion.setIdInformeInspeccion(informeInspeccion.getId());
						interpretacion.setNroIndividuos(totalIndividuos);
						
						listaInterpretacionIndicesCenso.add(interpretacion);
					}
				}
			} else {
				listaSitiosMuestrales = new ArrayList<>();
				listaPromediosAreaBasalVolumen = new ArrayList<ResultadoRevisionForestal>();
				listaResultadosIvi = new ArrayList<ResultadoRevisionForestal>();
			}
		}
	}
	
	public void showEspeciesImportancia() {
		if(informeInspeccion.getHabilitarEspeciesImportancia() != null && informeInspeccion.getHabilitarEspeciesImportancia()) {
			if(informeFactibilidadForestal.getCaracterizacionCobertura().equals(InformeFactibilidadForestal.caracterizacionCuantitativa)) {
				listaEspeciesImportancia = especiesImportanciaForestalFacade.getListaPorInforme(informeFactibilidadForestal.getId());
			} else {
				listaEspeciesImportancia = new ArrayList<>();
			}
		}
	}
	
	public void showCualitativa() {
		if(informeInspeccion.getHabilitarCaracterizacionCualitativa() != null && informeInspeccion.getHabilitarCaracterizacionCualitativa()) {
			if(informeFactibilidadForestal.getCaracterizacionCobertura().equals(InformeFactibilidadForestal.caracterizacionCualitativa) ) {
				listaEspeciesCualitativa = new ArrayList<EspeciesInformeForestal>();
				
				List<SitioMuestral> listaSitiosMuestrales = sitioMuestralFacade.getSitiosPorInformeTipo(informeFactibilidadForestal.getId(), SitioMuestral.registroCualitativo);
				
				if(listaSitiosMuestrales != null && listaSitiosMuestrales.size() > 0) {
					for (SitioMuestral sitio : listaSitiosMuestrales) {
						List<EspeciesInformeForestal> especiesSitio = especiesInformeForestalFacade.getListaPorSitioMuestral(sitio.getId());
						for (EspeciesInformeForestal especiesInformeForestal : especiesSitio) {
							especiesInformeForestal.setSitioMuestral(sitio);
						}
						
						listaEspeciesCualitativa.addAll(especiesSitio);
					}
				}			
			} else {
				listaEspeciesCualitativa = new ArrayList<>();
			}
		}
	}
	
	
	public void generarReporteAnalisisAreaBasal(SitioMuestral sitioMuestral) {
		List<EspeciesInformeForestal> especiesCodigo = especiesInformeForestalFacade.getListaPorSitioMuestral(sitioMuestral.getId());

		FacesContext fc = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();

		response.reset();
		response.setContentType("application/ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename=\""
				+ "Anexo 10 Tabla de volumen.xls" + "\"");
		HSSFWorkbook objWB = new HSSFWorkbook();
		HSSFSheet hoja1 = objWB.createSheet("hoja 1");
		
		int nroFila = 0;
		
		HSSFRow fila = hoja1.createRow(nroFila);
		HSSFCell celdaPlan = fila.createCell(0);
		celdaPlan.setCellValue(sitioMuestral.getNombreSitio());
		nroFila++;
		
		List<String> listaCabecera = new ArrayList<>();
		listaCabecera.add("Nº");
		listaCabecera.add("Familia");
		listaCabecera.add("Género");
		listaCabecera.add("Nombre Científico");
		listaCabecera.add("N. Común");
		listaCabecera.add("DAP m");
		listaCabecera.add("Ht m");
		listaCabecera.add("Hc m");
		listaCabecera.add("AB m²");
		listaCabecera.add("Vol. Total m³");
		listaCabecera.add("Vol. Comer m³");
		
		HSSFRow filaHeader = hoja1.createRow(nroFila);
		for(int col=0; col < listaCabecera.size(); col++) {
			HSSFCell celdaHeader = filaHeader.createCell(col);
			celdaHeader.setCellValue(listaCabecera.get(col));
		}
		nroFila++;
		
		int nroEspecie = 1;
		for (EspeciesInformeForestal especie : especiesCodigo) {
			int indexCol = 0;
			HSSFRow filaEspecie = hoja1.createRow(nroFila);
			
			HSSFCell celdaNro = filaEspecie.createCell(indexCol++);
			celdaNro.setCellValue(nroEspecie);
			
			HSSFCell celdaFamilia = filaEspecie.createCell(indexCol++);
			celdaFamilia.setCellValue(especie.getFamiliaEspecie().getHiclScientificName());
			
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
		
		int indexTotales = 7;
		HSSFRow filaTotales = hoja1.createRow(nroFila);
		HSSFCell celdaHeader = filaTotales.createCell(indexTotales++);
		celdaHeader.setCellValue("TOTAL");
		
		writeDecimal(objWB, filaTotales, sitioMuestral.getSumatoriaAreaBasal(), indexTotales++);
		
		writeDecimal(objWB, filaTotales, sitioMuestral.getSumatoriaVolumenTotal(), indexTotales++);
		
		writeDecimal(objWB, filaTotales, sitioMuestral.getSumatoriaVolumenComercial(), indexTotales++);
		
		try {
			ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
			objWB.write(outByteStream);
			byte[] outArray = outByteStream.toByteArray();
			OutputStream output = response.getOutputStream();
			output.write(outArray);
			output.flush();
			output.close();

			fc.responseComplete();
		} catch (IOException ex) {
			LOG.error(ex);
		}
	}
	
	public void generarReporteIndiceDiversidad(SitioMuestral sitioMuestral) {
		
//		List<EspeciesInformeForestal> especiesCodigo = especiesInformeForestalFacade.getListaPorSitioMuestral(sitioMuestral.getId());
		
		List<AnalisisResultadoForestal> analisisSitio = analisisResultadoForestalFacade.getListaPorSitio(sitioMuestral.getId());

		FacesContext fc = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();

		response.reset();
		response.setContentType("application/ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename=\""
				+ "Anexo 9 Tabla de índices.xls" + "\"");
		HSSFWorkbook objWB = new HSSFWorkbook();
		HSSFSheet hoja1 = objWB.createSheet("hoja 1");
		
		int nroFila = 0;
		
		HSSFRow fila = hoja1.createRow(nroFila);
		HSSFCell celdaPlan = fila.createCell(0);
		celdaPlan.setCellValue("Código de unidad muestral " + sitioMuestral.getNombreSitio());
		nroFila++;
		
		List<String> listaCabecera = new ArrayList<>();
		listaCabecera.add("Nº");
		listaCabecera.add("Familia");
		listaCabecera.add("Nombre Científico");
		listaCabecera.add("Nombre común");
		listaCabecera.add("Frecuencia");
		listaCabecera.add("AB (m2)");
		listaCabecera.add("DnR");
		listaCabecera.add("DmR");
		listaCabecera.add("IVI");
		listaCabecera.add("pi");
		listaCabecera.add("pi × ln(pi)");
		listaCabecera.add("pi 2 ");
		
		HSSFRow filaHeader = hoja1.createRow(nroFila);
		for(int col=0; col < listaCabecera.size(); col++) {
			HSSFCell celdaHeader = filaHeader.createCell(col);
			celdaHeader.setCellValue(listaCabecera.get(col));
		}
		nroFila++;
		
		int nroEspecie = 1;
		int frecuenciaTotalSitio = 0;
		for (AnalisisResultadoForestal especie : analisisSitio) {
			int indexCol = 0;
			HSSFRow filaEspecie = hoja1.createRow(nroFila);
			
			HSSFCell celdaNro = filaEspecie.createCell(indexCol++);
			celdaNro.setCellValue(nroEspecie);
			
			HSSFCell celdaFamilia = filaEspecie.createCell(indexCol++);
			celdaFamilia.setCellValue(especie.getGeneroEspecie().getHiclIdParent().getHiclScientificName());
			
//			HSSFCell celdaGenero = filaEspecie.createCell(indexCol++);
//			celdaGenero.setCellValue(especie.getGeneroEspecie().getHiclScientificName());
			
			HSSFCell celdaNombre = filaEspecie.createCell(indexCol++);
			if(especie.getEspecie() == null) {
				celdaNombre.setCellValue(especie.getGeneroEspecie().getHiclScientificName());
			} else {
				celdaNombre.setCellValue(especie.getEspecie().getSptaScientificName());
			}
			
			HSSFCell celdaNombreComun = filaEspecie.createCell(indexCol++);
			celdaNombreComun.setCellValue(especie.getNombreComun());
			
			HSSFCell celdaFrecuencia = filaEspecie.createCell(indexCol++);
			celdaFrecuencia.setCellValue(especie.getFrecuencia());
			
			writeDecimal(objWB, filaEspecie, especie.getAreaBasal(), indexCol++);
			
			writeDecimal(objWB, filaEspecie, especie.getValorDnr(), indexCol++);
			
			writeDecimal(objWB, filaEspecie, especie.getValorDmr(), indexCol++);
			
			writeDecimal(objWB, filaEspecie, especie.getValorIvi(), indexCol++);
			
			writeDecimal(objWB, filaEspecie, especie.getValorPi(), indexCol++);
			
			writeDecimal(objWB, filaEspecie, especie.getValorLnPi(), indexCol++);
			
			writeDecimal(objWB, filaEspecie, especie.getValorPiCuadrado(), indexCol++);
			
			nroEspecie++;
			nroFila++;
			
			frecuenciaTotalSitio += especie.getFrecuencia();
		}
		
		int indexTotales = 3;
		HSSFRow filaTotales = hoja1.createRow(nroFila);
		HSSFCell celdaHeader = filaTotales.createCell(indexTotales++);
		celdaHeader.setCellValue("TOTAL");
		
		HSSFCell celdaFrecuencia = filaTotales.createCell(indexTotales++);
		celdaFrecuencia.setCellValue(frecuenciaTotalSitio);
		
		writeDecimal(objWB, filaTotales, sitioMuestral.getSumatoriaAreaBasal(), indexTotales++);
		
		writeDecimal(objWB, filaTotales, sitioMuestral.getTotalDnr().doubleValue(), indexTotales++);
		
		writeDecimal(objWB, filaTotales, sitioMuestral.getTotalDmr(), indexTotales++);
		
		writeDecimal(objWB, filaTotales, sitioMuestral.getTotalIvi(), indexTotales++);
		
		HSSFCell celdaShannon = filaTotales.createCell(indexTotales++);
		celdaShannon.setCellValue("Índice de Shannon");
		writeDecimal(objWB, filaTotales, sitioMuestral.getIndiceShanon(), indexTotales++);
		
		nroFila++;
		int indexTotalesIndices = 9;
		HSSFRow filaExpShannon = hoja1.createRow(nroFila);
		HSSFCell celdaExpShannon = filaExpShannon.createCell(indexTotalesIndices++);
		celdaExpShannon.setCellValue("Exponencial Shannon");
		writeDecimal(objWB, filaExpShannon, sitioMuestral.getExponencialShannon(), indexTotalesIndices++);
		
		nroFila++;
		indexTotalesIndices = 9;
		HSSFRow filaSimpson = hoja1.createRow(nroFila);
		HSSFCell celdaSimpson = filaSimpson.createCell(indexTotalesIndices++);
		celdaSimpson.setCellValue("Índice de Simpson");
		writeDecimal(objWB, filaSimpson, sitioMuestral.getIndiceSimpson(), indexTotalesIndices++);
		
		nroFila++;
		indexTotalesIndices = 9;
		HSSFRow filaInversoSimpson = hoja1.createRow(nroFila);
		HSSFCell celdaInversoSimpson = filaInversoSimpson.createCell(indexTotalesIndices++);
		celdaInversoSimpson.setCellValue("Inverso de Simpson");
		if(sitioMuestral.getInversoSimpson() != null) {
			writeDecimal(objWB, filaInversoSimpson, sitioMuestral.getInversoSimpson(), indexTotalesIndices++);
		} else {
			String valor = (sitioMuestral.getIndiceSimpson().equals(0.0)) ? "Indefinido"  : "";
			HSSFCell celdaInversoSimpsonVacio = filaInversoSimpson.createCell(indexTotalesIndices++);
			celdaInversoSimpsonVacio.setCellValue(valor);
		}
		
		try {
			ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
			objWB.write(outByteStream);
			byte[] outArray = outByteStream.toByteArray();
			OutputStream output = response.getOutputStream();
			output.write(outArray);
			output.flush();
			output.close();

			fc.responseComplete();
		} catch (IOException ex) {
			LOG.error(ex);
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
	
	public void guardarDatosInforme() throws Exception {
		Integer tipoInforme = (esInformeViabilidad) ? InformeInspeccionForestal.viabilidad : InformeInspeccionForestal.observado;
		
		informeInspeccion.setIdTarea(idTarea);
		informeInspeccion.setIdViabilidad(viabilidadProyecto.getId());
		informeInspeccion.setTipoInforme(tipoInforme);
		informeInspeccion.setNumeroRevision(numeroRevision);

		informeInspeccionFacade.guardarInformeViabilidad(informeInspeccion, viabilidadProyecto.getAreaResponsable());
		
		if(viabilidadProyecto.getRequiereInspeccionTecnica()) {
			guardarDelegados();
			
			if(!listaCoordenadas.isEmpty()){
				int orden = 1;
				for (CoordenadaForestal coordenada : listaCoordenadas) {
					coordenada.setIdInformeInspeccion(informeInspeccion.getId());
					coordenada.setOrden(orden);
					
					coordenadasForestalFacade.guardarCoordenada(coordenada);
					orden++;
				}
			}
			
			if(!listaCoordenadasEliminar.isEmpty()){
				for (CoordenadaForestal coordenada : listaCoordenadasEliminar) {
					coordenada.setEstado(false);
					
					coordenadasForestalFacade.guardarCoordenada(coordenada);
				}
			}
			listaCoordenadasEliminar = new ArrayList<> ();
			
			guardarEspecies();
			
			guardarFotografias();
		}
		
		guardarAfectacionCoberturas();
		
		if(informeInspeccion.getHabilitarEcosistemasFragiles() != null && !informeInspeccion.getHabilitarEcosistemasFragiles()) {
			informeInspeccion.setDetalleEcosistemasFragiles(null);;
		}
		
		if(informeInspeccion.getHabilitarUnidadesHidrograficas() != null && informeInspeccion.getHabilitarUnidadesHidrograficas()) {
			if(!listaUnidadesHidrografica.isEmpty()) {
				for (UnidadHidrografica unidad : listaUnidadesHidrografica) {
					unidad.setIdInformeInspeccion(informeInspeccion.getId());
					unidadHidrograficaFacade.guardar(unidad);
				}
			}
		}
		if(!listaUnidadesHidrograficaEliminar.isEmpty()){
			for (UnidadHidrografica unidad : listaUnidadesHidrograficaEliminar) {
				unidad.setEstado(false);
				
				unidadHidrograficaFacade.guardar(unidad);
			}
		}
		listaUnidadesHidrograficaEliminar = new ArrayList<> ();
		
		if(informeInspeccion.getHabilitarDescripcionActividades() != null && !informeInspeccion.getHabilitarDescripcionActividades()) {
			informeInspeccion.setDescripcionActividades(null);;
		}
		
		if(informeInspeccion.getHabilitarCuantitativaMuestreo() != null && informeInspeccion.getHabilitarCuantitativaMuestreo()) {
			if(!listaInterpretacionIndices.isEmpty()) {
				for (InterpretacionIndiceForestal interpretacion : listaInterpretacionIndices) {
					interpretacion.setIdInformeInspeccion(informeInspeccion.getId());
					interpretacionIndiceForestalFacade.guardar(interpretacion);
				}
			}
		}
		
		if(informeInspeccion.getHabilitarCuantitativaCenso() != null && informeInspeccion.getHabilitarCuantitativaCenso()) {
			if(!listaInterpretacionIndicesCenso.isEmpty()) {
				for (InterpretacionIndiceForestal interpretacion : listaInterpretacionIndicesCenso) {
					interpretacion.setIdInformeInspeccion(informeInspeccion.getId());
					interpretacionIndiceForestalFacade.guardar(interpretacion);
				}
			}
		}
		
		if(informeInspeccion.getHabilitarAfectacionesPfn() != null && informeInspeccion.getHabilitarAfectacionesPfn()) {
			if(!listaAfectacionForestal.isEmpty()) {
				for (AfectacionForestal detalleAfectacion : listaAfectacionForestal) {
					afectacionForestalFacade.guardar(detalleAfectacion);
				}
			}
		}
		
		informeInspeccionFacade.guardar(informeInspeccion);
	}
	
	public void guardarDelegados() {
		if(!listaDelegadosOperador.isEmpty()){
			for (DelegadoOperadorForestal delegado : listaDelegadosOperador) {
				delegado.setIdInformeInspeccion(informeInspeccion.getId());
				
				delegadosInspeccionForestalFacade.guardarDelegado(delegado);
			}
		}
		
		if(!listaTecnicosDelegados.isEmpty()){
			for (DelegadoTecnicoForestal tecnico : listaTecnicosDelegados) {
				tecnico.setIdInformeInspeccion(informeInspeccion.getId());
				
				delegadosInspeccionForestalFacade.guardarTecnicoDelegado(tecnico);
			}
		}
		
		if(!listaDelegadosOperadorEliminar.isEmpty()){
			for (DelegadoOperadorForestal delegado : listaDelegadosOperadorEliminar) {
				delegado.setEstado(false);
				
				delegadosInspeccionForestalFacade.guardarDelegado(delegado);
			}
		}
		
		if(!listaTecnicosDelegadosEliminar.isEmpty()){
			for (DelegadoTecnicoForestal tecnico : listaTecnicosDelegadosEliminar) {
				tecnico.setEstado(false);
				
				delegadosInspeccionForestalFacade.guardarTecnicoDelegado(tecnico);
			}
		}
		
		listaDelegadosOperadorEliminar = new ArrayList<>();
		listaTecnicosDelegadosEliminar = new ArrayList<>();
		
	}
	
	public void guardarFotografias() throws Exception {
		if(!listaFotosRecorrido.isEmpty()){
			for (FotografiaInformeForestal fotografia : listaFotosRecorrido) {
				if (fotografia.getDocImagen().getIdAlfresco() == null) {
					fotografia.getDocImagen().setIdViabilidad(viabilidadProyecto.getId());
					DocumentoViabilidad imagen = documentosFacade
							.guardarDocumento(proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
									"VIABILIDAD_AMBIENTAL", fotografia.getDocImagen(), 2);

					if (imagen == null || imagen.getId() == null) {
						JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
						return;
					}

					fotografia.setDocImagen(imagen);
				}
				fotografia.setIdInformeInspeccion(informeInspeccion.getId());
				fotografiasForestalFacade.guardarFotografia(fotografia);
			}
		}
		
		if (!listaImgsEliminar.isEmpty()) {
			for (FotografiaInformeForestal fotografia : listaImgsEliminar) {
				fotografia.setEstado(false);
				fotografiasForestalFacade.guardarFotografia(fotografia);
			}
		}
	}
	
	public void guardarEspecies() {
		if(informeInspeccion.getEsMuestreoCenso() != null && informeInspeccion.getEsMuestreoCenso()) {
			if(!listaEspeciesMuestreoCenso.isEmpty()) {
				for (EspeciesInspeccionForestal especie : listaEspeciesMuestreoCenso) {
					especie.setIdInforme(informeInspeccion.getId());
					especie.setTipoRegistro(EspeciesInspeccionForestal.muestreoCenso);
					especiesInspeccionForestalFacade.guardarEspecie(especie);
				}
			}
		}
		
		if(informeInspeccion.getEsCaracterizacionCualitativa() != null && informeInspeccion.getEsCaracterizacionCualitativa()) {
			if(!listaEspeciesCaracterizacionCualitativa.isEmpty()) {
				for (EspeciesInspeccionForestal especie : listaEspeciesCaracterizacionCualitativa) {
					especie.setIdInforme(informeInspeccion.getId());
					especie.setTipoRegistro(EspeciesInspeccionForestal.cualitativo);
					especiesInspeccionForestalFacade.guardarEspecie(especie);
				}
			}
		}
		
		if(!listaEspeciesInspeccionEliminar.isEmpty()){
			for (EspeciesInspeccionForestal especie : listaEspeciesInspeccionEliminar) {
				especie.setEstado(false);
				especiesInspeccionForestalFacade.guardarEspecie(especie);
			}
		}
		listaEspeciesInspeccionEliminar = new ArrayList<> ();
	}
	
	public void guardarAfectacionCoberturas() {
		if(!listaAfectacionCoberturaVegetal.isEmpty()) {
			for (AfectacionCoberturaVegetal especie : listaAfectacionCoberturaVegetal) {
				especie.setIdInforme(informeInspeccion.getId());
				especie.setTipoRegistro(AfectacionCoberturaVegetal.coberturaVegetal);
				afectacionCoberturaVegetalFacade.guardar(especie);
			}
		}
		
		if(!listaAfectacionEcosistemas.isEmpty()) {
			for (AfectacionCoberturaVegetal especie : listaAfectacionEcosistemas) {
				especie.setIdInforme(informeInspeccion.getId());
				especie.setTipoRegistro(AfectacionCoberturaVegetal.ecosistemas);
				afectacionCoberturaVegetalFacade.guardar(especie);
			}
		}
		
		if(!listaAfectacionConvenios.isEmpty()) {
			for (AfectacionCoberturaVegetal especie : listaAfectacionConvenios) {
				especie.setIdInforme(informeInspeccion.getId());
				especie.setTipoRegistro(AfectacionCoberturaVegetal.convenios);
				afectacionCoberturaVegetalFacade.guardar(especie);
			}
		}
		
	}
	//TODO: Ivan
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
	public void generarReporteTaxonomia() {
		
//		List<EspeciesInformeForestal> especiesCodigo = especiesInformeForestalFacade.getListaPorSitioMuestral(sitioMuestral.getId());
		
		//List<AnalisisResultadoForestal> analisisSitio = analisisResultadoForestalFacade.getListaPorSitio(sitioMuestral.getId());

		FacesContext fc = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();

		response.reset();
		response.setContentType("application/ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename=\""
				+ "Tabla de Cálculo del Área Basal y Volumen.xls" + "\"");
		HSSFWorkbook objWB = new HSSFWorkbook();
		HSSFSheet hoja1 = objWB.createSheet("hoja 1");
		
		int nroFila = 0;
		
		HSSFRow fila = hoja1.createRow(nroFila);
		HSSFCell celdaPlan = fila.createCell(0);
		celdaPlan.setCellValue("Tabla de Cálculo del Área Basal y Volumen");
		nroFila++;
		
		List<String> listaCabecera = new ArrayList<>();
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
		for (EspeciesInformeForestal especie : listaEspeciesCenso) { 
			int indexCol = 0;
			HSSFRow filaEspecie = hoja1.createRow(nroFila);
			
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

		try {
			ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
			objWB.write(outByteStream);
			byte[] outArray = outByteStream.toByteArray();
			OutputStream output = response.getOutputStream();
			output.write(outArray);
			output.flush();
			output.close();

			fc.responseComplete();
		} catch (IOException ex) {
			LOG.error(ex);
		}
	}
}
