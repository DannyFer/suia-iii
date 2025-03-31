package ec.gob.ambiente.rcoa.inventarioForestal.controller;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import _154._1._180._10._8080.consultatitulossenescytwsv3.servicioconsultatitulo.GraduadoReporteDTO;
import _154._1._180._10._8080.consultatitulossenescytwsv3.servicioconsultatitulo.NivelTitulosDTO;
import ec.gob.ambiente.cliente.ConsultaRucCedula;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.enums.CatalogoTipoCoaEnum;
import ec.gob.ambiente.rcoa.facade.CapasCoaFacade;
import ec.gob.ambiente.rcoa.facade.CatalogoCoaFacade;
import ec.gob.ambiente.rcoa.facade.CoordenadasProyectoCoaFacade;
import ec.gob.ambiente.rcoa.facade.DetalleInterseccionProyectoAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.EspecialistaAmbientalCoaFacade;
import ec.gob.ambiente.rcoa.facade.InterseccionProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaAmbientalCoaShapeFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.BienesServiciosInventarioForestalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.CoordenadasInventarioForestalCertificadoFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.DocumentoInventarioForestalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.HigherClassificationFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.IndiceValorImportanciaInventarioForestalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.InventarioForestalAmbientalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.InventarioForestalDetalleFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.PromedioInventarioForestalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.RegistroAmbientalSumatoriaFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.RegistroEspeciesForestalesFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.ShapeInventarioForestalCertificadoFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.SpecieTaxaFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.model.BienesServiciosInventarioForestal;
import ec.gob.ambiente.rcoa.inventarioForestal.model.CoordenadasInventarioForestalCertificado;
import ec.gob.ambiente.rcoa.inventarioForestal.model.DocumentoInventarioForestal;
import ec.gob.ambiente.rcoa.inventarioForestal.model.HigherClassification;
import ec.gob.ambiente.rcoa.inventarioForestal.model.IndiceValorImportanciaInventarioForestal;
import ec.gob.ambiente.rcoa.inventarioForestal.model.InventarioForestalAmbiental;
import ec.gob.ambiente.rcoa.inventarioForestal.model.InventarioForestalDetalle;
import ec.gob.ambiente.rcoa.inventarioForestal.model.PromedioInventarioForestal;
import ec.gob.ambiente.rcoa.inventarioForestal.model.RegistroAmbientalSumatoria;
import ec.gob.ambiente.rcoa.inventarioForestal.model.RegistroEspeciesForestales;
import ec.gob.ambiente.rcoa.inventarioForestal.model.ShapeInventarioForestalCertificado;
import ec.gob.ambiente.rcoa.inventarioForestal.model.SpecieTaxa;
import ec.gob.ambiente.rcoa.model.CapasCoa;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.model.CoordenadasProyecto;
import ec.gob.ambiente.rcoa.model.DetalleInterseccionProyectoAmbiental;
import ec.gob.ambiente.rcoa.model.EspecialistaAmbiental;
import ec.gob.ambiente.rcoa.model.InterseccionProyectoLicenciaAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaAmbientalCoaShape;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.TipoForma;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.CodigoTasa;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.recaudaciones.facade.TarifasFacade;
import ec.gob.ambiente.suia.recaudaciones.model.Tarifas;
import ec.gob.ambiente.suia.tipoforma.facade.TipoFormaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.webservice.facade.AsignarTareaFacade;
import ec.gob.registrocivil.consultacedula.Cedula;
import index.ContieneZona_entrada;
import index.ContieneZona_resultado;
import index.SVA_Reproyeccion_IntersecadoPortTypeProxy;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class InventarioForestalRegistroController {
	
	private static final Logger LOG = Logger.getLogger(InventarioForestalRegistroController.class);
	
	@EJB
	private ProcesoFacade procesoFacade;
	private Map<String, Object> variables;
	
	/*BEANs*/
	@Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
	@ManagedProperty(value = "#{loginBean}")
	@Setter
	@Getter
	private LoginBean loginBean;
    
	/*EJBs*/
	@EJB
	private CrudServiceBean crudServiceBean;
    
    
    // FACADES GENERALES
    @EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
    @EJB
	private ProyectoLicenciaAmbientalCoaShapeFacade proyectoLicenciaAmbientalCoaShapeFacade;
    @EJB
	private CapasCoaFacade capasCoaFacade;
    @EJB
	private InterseccionProyectoLicenciaAmbientalFacade interseccionProyectoLicenciaAmbientalFacade;
    @EJB
	private DetalleInterseccionProyectoAmbientalFacade detalleInterseccionProyectoAmbientalFacade;
    @EJB
	private CoordenadasProyectoCoaFacade coordenadasProyectoCoaFacade;
    @EJB
	private InventarioForestalAmbientalFacade inventarioForestalAmbientalFacade;
    @EJB
	private InventarioForestalDetalleFacade inventarioForestalDetalleFacade;
    @EJB
	private HigherClassificationFacade higherClassificationFacade;
    @EJB	
	private SpecieTaxaFacade specieTaxaFacade;
    @EJB
	private TipoFormaFacade tipoFormaFacade;
    @EJB
	private ShapeInventarioForestalCertificadoFacade shapeInventarioForestalCertificadoFacade;
    @EJB
	private CoordenadasInventarioForestalCertificadoFacade coordenadasInventarioForestalCertificadoFacade;
    @EJB
	private RegistroEspeciesForestalesFacade registroEspeciesForestalesFacade;
    @EJB
	private RegistroAmbientalSumatoriaFacade registroAmbientalSumatoriaFacade;
    @EJB
	private PromedioInventarioForestalFacade promedioInventarioForestalFacade;
    @EJB
	private IndiceValorImportanciaInventarioForestalFacade indiceValorImportanciaInventarioForestalFacade;
    @EJB
	private BienesServiciosInventarioForestalFacade bienesServiciosInventarioForestalFacade;
    @EJB
	private DocumentoInventarioForestalFacade documentoInventarioForestalFacade;
    @EJB
    private AsignarTareaFacade asignarTareaFacade;
    @EJB
    private UsuarioFacade usuarioFacade;
    @EJB
    private EspecialistaAmbientalCoaFacade especialistaAmbientalCoaFacade;
    @EJB
    private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
    @EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
    @EJB
	private TarifasFacade tarifasFacade;
    @EJB
    private CatalogoCoaFacade catalogoCoaFacade;

    
    /*List*/
    @Setter
	@Getter
	private List<RegistroEspeciesForestales> listRegistroEspeciesForestales = new ArrayList<RegistroEspeciesForestales>();
    @Setter
	@Getter
	private List<RegistroEspeciesForestales> listRegistroEspeciesForestalesEliminar = new ArrayList<RegistroEspeciesForestales>();
    @Setter
	@Getter
	private List<HigherClassification> listFamilia = new ArrayList<HigherClassification>();
	@Setter
	@Getter
	private List<HigherClassification> listGenero = new ArrayList<HigherClassification>();
    @Setter
	@Getter
	private List<SpecieTaxa> listEspecie = new ArrayList<SpecieTaxa>();
    @Setter
	@Getter
	private List<InventarioForestalAmbiental> listInventarioForestalAmbiental = new ArrayList<InventarioForestalAmbiental>();
    @Getter
    @Setter
    private List<CoordendasPoligonosCertificado> coordinatesWrappers = new ArrayList<CoordendasPoligonosCertificado>();
    @Getter
    @Setter
    private List<CoordendasPoligonosCertificado> coordinatesWrappersEliminar = new ArrayList<CoordendasPoligonosCertificado>();
    @Getter
    private List<TipoForma> tiposFormas  = new ArrayList<TipoForma>();
    @Getter
    private List<RegistroAmbientalSumatoria> listRegistroAmbientalSumatoria = new ArrayList<RegistroAmbientalSumatoria>();
    @Getter
    private List<RegistroAmbientalSumatoria> listRegistroAmbientalSumatoriaEliminar = new ArrayList<RegistroAmbientalSumatoria>();
    @Getter
    private List<PromedioInventarioForestal> listPromedioInventarioForestal = new ArrayList<PromedioInventarioForestal>();
    @Getter
    private List<IndiceValorImportanciaInventarioForestal> listIndiceValorImportanciaInventarioForestal = new ArrayList<IndiceValorImportanciaInventarioForestal>();
    @Getter
    private List<CatalogoGeneralCoa> listMetodoRecoleccionDatos;
    
    
    /*Object*/
    @Setter
    @Getter
    private ProyectoLicenciaCoa proyectoLicenciaCoa = new ProyectoLicenciaCoa();
    @Setter
    @Getter
    private ProyectoLicenciaCuaCiuu actividadPrincipal = new ProyectoLicenciaCuaCiuu();
    @Setter
    @Getter
    private InventarioForestalAmbiental inventarioForestalAmbiental;
    @Setter
    @Getter
    private InventarioForestalDetalle inventarioForestalDetalle = new InventarioForestalDetalle();
    @Setter
    @Getter
    private RegistroEspeciesForestales registroEspeciesForestales = new RegistroEspeciesForestales();
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
    @Getter
    private UploadedFile uploadedFile;
    private TipoForma poligono=new TipoForma();
    @Getter
    @Setter
    private ShapeInventarioForestalCertificado shape = new ShapeInventarioForestalCertificado();
    @Getter
    @Setter
    private CoordendasPoligonosCertificado coor = new CoordendasPoligonosCertificado();
    @Getter
    @Setter
    private BienesServiciosInventarioForestal bienesServiciosInventarioForestal = new BienesServiciosInventarioForestal();
    @Setter
    @Getter
    private EspecialistaAmbiental especialistaAmbiental = new EspecialistaAmbiental();
    @Setter
    @Getter
	private DocumentoInventarioForestal documentoCoordenadas = new DocumentoInventarioForestal();
    @Setter
    @Getter
	private DocumentoInventarioForestal documentoEspecies = new DocumentoInventarioForestal();
	@Setter
    @Getter
	private DocumentoInventarioForestal documentoValoracion = new DocumentoInventarioForestal();
	
    
	/*Boolean*/
    @Getter
    private boolean blockFamilia=true, blockGenero=true, blockEspecie=true, blockOtro=true;
    @Getter
    private Integer idRegistroPreliminar;
    @Getter
    @Setter
    private String tramite, tecnicoForestal="";
    @Getter
    @Setter
    private Integer idInventarioForestalRegistro;
    @Getter
    @Setter
    private Date currentDate = new Date();
    @Setter
    @Getter
    private String volumenTotalEspecie="", areaBasalEspecie="", justificacionTecnica;
    //F
    private byte[] plantillaCoordenadasDesbroce;    
    @Getter
    @Setter
    private boolean mostrarValoracion = false, actualizaPagoDesbroce = false, actualizaCalculoIVI = false, guardaTramite = false;
    @Setter
    @Getter
    private Date fechaElaboracion;
        
    /*CONSTANTES*/
    public static final Integer TIPO_INVENTARIO = 2;
    public static final Double VALOR_AREA_BASAL = 0.7854;
    public static final Double VALOR_VOLUMEN = 0.7;
    public static final Double VALOR_M3_MADERA_PIE = 3.00;
    public static final Integer ID_LAYER_COBERTURA = 12;
    public static final Integer ID_LAYER_ECOSISTEMAS = 13;
    public static final Integer ID_SECTOR_HIDROCARBUROS = 1;
    public static final Integer ID_SECTOR_MINERIA = 2;
    public static final Integer ID_SECTOR_ELECTRICO = 4;
   
    
    @PostConstruct
	public void init() {
    	try {
    		variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getProcessId());
    		tramite = (String) variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
    		tecnicoForestal = (String) variables.get("tecnicoForestal");
    		idRegistroPreliminar = Integer.valueOf((String)variables.get(Constantes.ID_PROYECTO));
    		proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idRegistroPreliminar);
    		inventarioForestalAmbiental = new InventarioForestalAmbiental();
    		inventarioForestalAmbiental = inventarioForestalAmbientalFacade.getByIdRegistroPreliminar(idRegistroPreliminar);
	    	idInventarioForestalRegistro = inventarioForestalAmbiental.getId();
	    	if (inventarioForestalAmbiental.getId() != null) {
	    		cargarinventarioForestalRegistro();
			} else {
				inventarioForestalAmbiental.setSuperficieProyecto(proyectoLicenciaCoa.getSuperficie());				
				String ecosistema = getInterseccionCapa(ID_LAYER_ECOSISTEMAS);
				inventarioForestalDetalle.setEcosistemaAreaProyecto(ecosistema);
				String cobertura = getInterseccionCapa(ID_LAYER_COBERTURA);
				inventarioForestalDetalle.setCoberturaVegetalSuelo(cobertura);
			}
    		
    		// Listado de Familias PLANTAE
    		listFamilia = obtenerListaFamilia();
    		tiposFormas = tipoFormaFacade.listarTiposForma();
    		poligono = getTipoForma(TipoForma.TIPO_FORMA_POLIGONO);    		
    		
//    		plantillaCoordenadasDesbroce = documentoInventarioForestalFacade.descargarDocumentoPorNombreYDirectorioBase(Constantes.PLANTILLA_COORDENADAS_INVENTARIO_FORESTAL, null);
    		
    		actividadPrincipal = proyectoLicenciaCuaCiuuFacade.actividadPrincipal(proyectoLicenciaCoa);
    		actividadPrincipal.getCatalogoCIUU().getTipoSector();
    		
			if (actividadPrincipal.getCatalogoCIUU().getTipoSector().getId() == 1
					|| actividadPrincipal.getCatalogoCIUU().getTipoSector().getId() == 2
					|| actividadPrincipal.getCatalogoCIUU().getTipoSector().getId() == 4) {
				mostrarValoracion = true;
			}
			listMetodoRecoleccionDatos=catalogoCoaFacade.obtenerCatalogoOrden(CatalogoTipoCoaEnum.IF_METODO_RECOLECCION_DATOS);
           
    	} catch (Exception e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos del Inventario Forestal.");
		}
	}
    
    private String getInterseccionCapa(Integer idCapa) {
    	String capaValue = "---";
    	InterseccionProyectoLicenciaAmbiental interseca = new InterseccionProyectoLicenciaAmbiental();
    	CapasCoa interseccionCapa = new CapasCoa();
    	interseccionCapa = capasCoaFacade.getCapasById(idCapa);
    	List<InterseccionProyectoLicenciaAmbiental> interseccionProyecto = new ArrayList<InterseccionProyectoLicenciaAmbiental>();
    	interseccionProyecto = interseccionProyectoLicenciaAmbientalFacade.getByIdProyectoCoa(idRegistroPreliminar, 0);
    	for (InterseccionProyectoLicenciaAmbiental rowInterseccion : interseccionProyecto) {
			if (rowInterseccion.getCapa().getId() == interseccionCapa.getId()) {
				interseca = rowInterseccion;
				break;
			}
		}
    	List<DetalleInterseccionProyectoAmbiental> listDetalleInterseccion = new ArrayList<DetalleInterseccionProyectoAmbiental>();
    	listDetalleInterseccion = detalleInterseccionProyectoAmbientalFacade.getByInterseccionProyecto(interseca.getId());
    	if (listDetalleInterseccion.size() > 0) {
			capaValue = listDetalleInterseccion.get(0).getNombreGeometria();
		}
    	return capaValue;
    }
    
    // Excel de Coordenadas
    public TipoForma getTipoForma(Integer id) {
        for (TipoForma tf : tiposFormas) {
            if (tf.getId()==id)
                return tf;
        }
        return null;
    }

    public void editarSuperficieDesbroce() {
    	actualizaPagoDesbroce = false;
    }
    public void asignarFechaElaboracion(SelectEvent event) throws Exception {
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    	String date = simpleDateFormat.format(event.getObject());
    	fechaElaboracion=new SimpleDateFormat("yyyy-MM-dd").parse(date);
    	inventarioForestalAmbiental.setFechaElaboracion(fechaElaboracion);
    }
    
    public void handleFileUploadCoordenadas(final FileUploadEvent event) {   
        int rows = 0;
        List<String> listCoordenadasProyecto = new ArrayList<String>();
        List<ProyectoLicenciaAmbientalCoaShape> shapeProyecto = proyectoLicenciaAmbientalCoaShapeFacade.buscarFormaGeograficaPorProyecto(proyectoLicenciaCoa, 1, 0);
        for (ProyectoLicenciaAmbientalCoaShape rowShapeProyecto : shapeProyecto) {
        	List<CoordenadasProyecto> listCoordenadas = coordenadasProyectoCoaFacade.buscarPorForma(rowShapeProyecto);
        	String arrayCoordenadas = "";
        	for (CoordenadasProyecto rowCoordenadas : listCoordenadas) {
        		arrayCoordenadas += (arrayCoordenadas == "") ? rowCoordenadas.getX().toString()+" "+rowCoordenadas.getY().toString() : ","+rowCoordenadas.getX().toString()+" "+rowCoordenadas.getY().toString();
        	}
        	listCoordenadasProyecto.add(arrayCoordenadas);
        }
        
        if(coordinatesWrappers != null && coordinatesWrappers.size() > 0) {
        	//para eliminar las coordenadas cuando se modifica la información
        	coordinatesWrappersEliminar.addAll(coordinatesWrappers);
        }
        
        shape = new ShapeInventarioForestalCertificado();
        coordinatesWrappers = new ArrayList<CoordendasPoligonosCertificado>();
        CoordendasPoligonosCertificado coordinatesWrapper = new CoordendasPoligonosCertificado();
        coordinatesWrappers.add(coordinatesWrapper);
        try {
            uploadedFile = event.getFile();
            Workbook workbook = new HSSFWorkbook(uploadedFile.getInputstream());
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
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

                if (rows > 0) {
                    Iterator<Cell> cellIterator = row.cellIterator();
                    CoordenadasInventarioForestalCertificado coordenada = new CoordenadasInventarioForestalCertificado();
                    Integer poligonoActual = (int) cellIterator.next().getNumericCellValue();
                    coordenada.setAreaGeografica(poligonoActual);
                    coordenada.setOrdenCoordenada((int) cellIterator.next().getNumericCellValue());
                    coordenada.setX(new BigDecimal(Double.valueOf(cellIterator.next().toString().replace("," , "."))));
                    coordenada.setY(new BigDecimal(Double.valueOf(cellIterator.next().toString().replace("," , "."))));
                    if (coordinatesWrapper.getTipoForma() == null)
                        coordinatesWrapper.setTipoForma(poligono);                    
                    else if (!coordinatesWrapper.getTipoForma().equals(poligono) || coordinatesWrapper.getTipoForma().getId().intValue() == TipoForma.TIPO_FORMA_PUNTO || coordenada.getOrdenCoordenada() == 1) {
                        coordinatesWrapper = new CoordendasPoligonosCertificado(null, poligono);
                        coordinatesWrappers.add(coordinatesWrapper);
                    }
                    coordinatesWrapper.getCoordenadas().add(coordenada);
                }
                rows++;
            }
            List<String> listCoordenadasInventario = new ArrayList<String>();
            for (int i = 0; i < coordinatesWrappers.size(); i++) {
            	String coorInventario = "";
            	for (int j = 0; j < coordinatesWrappers.get(i).getCoordenadas().size(); j++) {
            		CoordenadasInventarioForestalCertificado coordenada = coordinatesWrappers.get(i).getCoordenadas().get(j);
            		coorInventario += (coorInventario == "") ? coordenada.getX().setScale(5, RoundingMode.HALF_DOWN).toString()+" "+coordenada.getY().setScale(5, RoundingMode.HALF_DOWN).toString() : ","+coordenada.getX().setScale(5, RoundingMode.HALF_DOWN).toString()+" "+coordenada.getY().setScale(5, RoundingMode.HALF_DOWN).toString();
            	}
            	listCoordenadasInventario.add(coorInventario);
            }
            // Verificar si las coordenadas estan contenidas en el poligono de implantacion del proyecto
            List<Boolean> listInImpl = new ArrayList<Boolean>();
            for (int i = 0; i < listCoordenadasInventario.size(); i++) {
            	Boolean inImplMuestra = false;
            	for (int j = 0; j < listCoordenadasProyecto.size(); j++) {
            		ContieneZona_entrada verificarGeoImpla = new ContieneZona_entrada();
            		verificarGeoImpla.setU(Constantes.getUserWebServicesSnap());
            		verificarGeoImpla.setXy1(listCoordenadasProyecto.get(j));
            		verificarGeoImpla.setXy2(listCoordenadasInventario.get(i));
            		SVA_Reproyeccion_IntersecadoPortTypeProxy wsGeoImpl=new SVA_Reproyeccion_IntersecadoPortTypeProxy();
            		ContieneZona_resultado[]intRestGeoImpl;
            		intRestGeoImpl=wsGeoImpl.contieneZona(verificarGeoImpla);
            		if (intRestGeoImpl[0].getContieneCapa().getValor().equals("t")) {
            			inImplMuestra = true;
            			listInImpl.add(inImplMuestra);
            			break;
            		}
            	}
            }
            if (listInImpl.size() != listCoordenadasInventario.size()) {
            	coordinatesWrappers = new ArrayList<CoordendasPoligonosCertificado>();
            	documentoCoordenadas = new DocumentoInventarioForestal();
            	JsfUtil.addMessageError("Las coordenadas están fuera la(s) áreas de implantación(es)");
            } else {
            	String[] split=event.getFile().getContentType().split("/");
            	String extension = "."+split[split.length-1];
            	documentoCoordenadas.setNombreDocumento(event.getFile().getFileName());
            	documentoCoordenadas.setMimeDocumento(event.getFile().getContentType());
            	documentoCoordenadas.setContenidoDocumento(event.getFile().getContents());
            	documentoCoordenadas.setExtencionDocumento(extension);
            }
        } catch (Exception e) {
        	documentoCoordenadas = new DocumentoInventarioForestal();
            LOG.error("Error procesando el excel de coordenadas", e);
        }
    }
    

    // Registro de Especies Manual
    public void desbloquearTaxonomia() {
    	blockFamilia=true;
		blockGenero=true;
		blockEspecie=true;
    	if (registroEspeciesForestales.getNivelTaxonomia().equals("E")) {
			blockFamilia=false;
			blockGenero=false;
			blockEspecie=false;
		}
    	if (registroEspeciesForestales.getNivelTaxonomia().equals("G")) {
    		blockFamilia=false;
			blockGenero=false;
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
    public void obtenerListaGeneroPorFamilia() {
    	listGenero = higherClassificationFacade.getByFamilia(objFamilia.getId());
    }
    public void obtenerListaEspecies() {
    	listEspecie = specieTaxaFacade.getByGenero(objGenero.getId());
    	objEspecieOtro.setId(0);
    	objEspecieOtro.setSptaScientificName("Otro");
    	listEspecie.add(objEspecieOtro);
    }
    public void desbloquearOtraEspecies() {
    	blockOtro=true;
    	registroEspeciesForestales.setOtroEspecie("");
    	if (objEspecie.getId() == 0) {
    		blockOtro=false;
		}
    }
    private Integer numeroIndividuoEspecie(String codMuestra) {
    	Integer numero = 1;
    	for (RegistroEspeciesForestales row : listRegistroEspeciesForestales) {
			if (row.getCodigoMuestra().equals(codMuestra)) {
				numero ++;
			}
		}
    	return numero;
    }
    
    public void crearEspecie() {
    	registroEspeciesForestales=new RegistroEspeciesForestales();
    }
    	
    public void agregarEspecies() {    
    	registroEspeciesForestales.setFamiliaEspecie(objFamilia);
    	registroEspeciesForestales.setGeneroEspecie(objGenero);
    	registroEspeciesForestales.setIdFamilia(objFamilia.getId());
    	registroEspeciesForestales.setIdGenero(objGenero.getId());
    	
    	if (blockEspecie) {
    		objEspecie.setSptaScientificName("sp");
		} else if (objEspecie.getId() == 0) {
			objEspecie.setSptaOtherScientificName(registroEspeciesForestales.getOtroEspecie());
		}
    	registroEspeciesForestales.setEspecieEspecie(objEspecie);
    	if(objEspecie != null && objEspecie.getId() != null){
    		registroEspeciesForestales.setIdEspecie(objEspecie.getId());
    	}
    	
    	if(getMetodoRecoleccionDatos().contains("MUESTREO")) {
    		String codMuestra = registroEspeciesForestales.getCodigoMuestra();
        	Integer numeroIndividuo = numeroIndividuoEspecie(codMuestra);
        	registroEspeciesForestales.setNumeroIndividuo(numeroIndividuo);
    	}else {
    		registroEspeciesForestales.setNumeroIndividuo(1+listRegistroEspeciesForestales.indexOf(registroEspeciesForestales));
    	}
    	
    	if (!listRegistroEspeciesForestales.contains(registroEspeciesForestales)) {
			listRegistroEspeciesForestales.add(registroEspeciesForestales);
		}
    	actualizaPagoDesbroce = false;
    	actualizaCalculoIVI = false;
    	
    	listRegistroAmbientalSumatoria = new ArrayList<RegistroAmbientalSumatoria>();
    	listIndiceValorImportanciaInventarioForestal=new ArrayList<IndiceValorImportanciaInventarioForestal>();
    	bienesServiciosInventarioForestal = new BienesServiciosInventarioForestal();
    	
    	resetEspecies();
    	RequestContext.getCurrentInstance().update("form:tblSumatoria");
    	
    }
    public void editarEspecie(RegistroEspeciesForestales item) {
    	registroEspeciesForestales = item;
    	objFamilia = item.getFamiliaEspecie();
    	objGenero = item.getGeneroEspecie();
    	objEspecie = item.getEspecieEspecie();
    	obtenerListaGeneroPorFamilia();
    	obtenerListaEspecies();
    	if (registroEspeciesForestales.getNivelTaxonomia().equals("G")) {
    		blockFamilia=false;
        	blockGenero=false;
		}
    	if (registroEspeciesForestales.getNivelTaxonomia().equals("E")) {
    		blockFamilia=false;
        	blockGenero=false;
        	blockEspecie=false;
		}
    	if (registroEspeciesForestales.getEspecieEspecie().getId() == null || registroEspeciesForestales.getEspecieEspecie().getId() == 0) {
    		blockOtro=false;
		}
    	actualizaPagoDesbroce = false;
    	actualizaCalculoIVI = false;
    	listRegistroAmbientalSumatoria = new ArrayList<RegistroAmbientalSumatoria>();
    	listIndiceValorImportanciaInventarioForestal=new ArrayList<IndiceValorImportanciaInventarioForestal>();
    }
    public void eliminarEspecie(RegistroEspeciesForestales item) {
    	if (item.getId() != null) {
    		listRegistroEspeciesForestalesEliminar.add(item);
		}
    	listRegistroEspeciesForestales.remove(item);
    	actualizaPagoDesbroce = false;
    	actualizaCalculoIVI = false;
    	listRegistroAmbientalSumatoria = new ArrayList<RegistroAmbientalSumatoria>();
    	listIndiceValorImportanciaInventarioForestal=new ArrayList<IndiceValorImportanciaInventarioForestal>();
    	listPromedioInventarioForestal=new ArrayList<PromedioInventarioForestal>();
    }
    public void resetEspecies() {
    	registroEspeciesForestales = new RegistroEspeciesForestales();
    	objFamilia = new HigherClassification();
    	objGenero = new HigherClassification();
    	objEspecie = new SpecieTaxa();
    	blockFamilia=true;
    	blockGenero=true;
    	blockEspecie=true;
    	blockOtro=true;
    	listFamilia = obtenerListaFamilia();
    	listGenero = new ArrayList<HigherClassification>();
    	listEspecie = new ArrayList<SpecieTaxa>();
    }
    public void calculoAreaBasal() {
    	Double area = 0.0000;
    	area = registroEspeciesForestales.getDiametroEspecie() * registroEspeciesForestales.getDiametroEspecie() * VALOR_AREA_BASAL;
    	BigDecimal areaC = new BigDecimal(area);
    	areaC = areaC.setScale(4, BigDecimal.ROUND_HALF_UP);
    	registroEspeciesForestales.setAreaBasalEspecie(areaC);
    }

    // Archivo Excel Especies
    public void handleFileUploadEspecies(final FileUploadEvent event) {
    	int rows = 0;
    	
    	if(listRegistroAmbientalSumatoria != null && listRegistroAmbientalSumatoria.size() > 0) {
    		//para eliminar las sumatorias cuando se modifica la información de las especies
    		listRegistroAmbientalSumatoriaEliminar.addAll(listRegistroAmbientalSumatoria);
    	}
    	
    	if(listRegistroEspeciesForestales != null && listRegistroEspeciesForestales.size() > 0) {
    		//para eliminar las especies cuando se modifica la información
    		listRegistroEspeciesForestalesEliminar.addAll(listRegistroEspeciesForestales);
    	}
    	
    	List<RegistroEspeciesForestales> listEspeciesTemp = new ArrayList<RegistroEspeciesForestales>(); 
    	listRegistroEspeciesForestales = new ArrayList<RegistroEspeciesForestales>();
    	listRegistroAmbientalSumatoria = new ArrayList<RegistroAmbientalSumatoria>();
    	listPromedioInventarioForestal = new ArrayList<PromedioInventarioForestal>();
    	listIndiceValorImportanciaInventarioForestal = new ArrayList<IndiceValorImportanciaInventarioForestal>();
    	try {
    		uploadedFile = event.getFile();
    		Workbook workbook = new HSSFWorkbook(uploadedFile.getInputstream());
    		Sheet sheet = workbook.getSheetAt(0);
    		Iterator<Row> rowIterator = sheet.iterator();
    		boolean validarFormato= false;
    		while (rowIterator.hasNext()) {
    			Row row = rowIterator.next();                
    			boolean isEmptyRow = true;
    			for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
    				Cell cell = row.getCell(cellNum);
    				if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK && StringUtils.isNotBlank(cell.toString())) {
    					isEmptyRow = false;
    				}
    			}
    			if (isEmptyRow)
    				break;
    			
    			if (rows > 0) {
    				RegistroEspeciesForestales registroEspeciesForestales = new RegistroEspeciesForestales();
    				Iterator<Cell> cellIterator = row.cellIterator();
    				    				
    				String codigoMuestra = "";
    				if(getMetodoRecoleccionDatos().contains("MUESTREO")) {
    				Cell cell = cellIterator.next();
    				switch (cell.getCellType()) {
	                   case Cell.CELL_TYPE_NUMERIC:
	                	   Double doubleCodigo = Double.valueOf(cell.getNumericCellValue());
	                       codigoMuestra = String.valueOf(doubleCodigo);
	                       break;
	                   case Cell.CELL_TYPE_STRING:
	                	   codigoMuestra = (String) cell.getStringCellValue().toUpperCase();
	                       break;
    				}}
    				String nivelTaxonomia = (String) cellIterator.next().getStringCellValue().toUpperCase();
    				String nombreCientifico = (String) cellIterator.next().getStringCellValue().toUpperCase();
    				Double dap = 0.00;
    				Double altura = 0.00;

    				dap = Double.valueOf(cellIterator.next().toString().replace("," , "."));
    				altura = Double.valueOf(cellIterator.next().toString().replace("," , "."));
    				HigherClassification objGenero = new HigherClassification(); 
    				HigherClassification objFamilia = new HigherClassification();
    				SpecieTaxa objEspecie = new SpecieTaxa();
    				if (nivelTaxonomia.equals("GENERO") || nivelTaxonomia.equals("GÉNERO")) {
    					registroEspeciesForestales.setNivelTaxonomia("G");
    					objGenero = higherClassificationFacade.getGeneroByHiclScientificName(nombreCientifico.trim());
    					objFamilia = objGenero.getHiclIdParent(); 
    					objEspecie.setSptaScientificName("sp");
    				} else if (nivelTaxonomia.equals("ESPECIE")) {
    					registroEspeciesForestales.setNivelTaxonomia("E");
						objEspecie = specieTaxaFacade.getBySptaScientificName(nombreCientifico.trim());
						if (objEspecie != null) {
							objGenero = objEspecie.getHigherClassification();
							objFamilia = objGenero.getHiclIdParent();
						}else {
							documentoEspecies = new DocumentoInventarioForestal();
							validarFormato = true;
							break;							
						}						
					} else {
    					JsfUtil.addMessageError("Documento con formato erróneo");
    					for (RegistroEspeciesForestales rowEspecie : listEspeciesTemp) {
							listRegistroEspeciesForestales.remove(rowEspecie);
						}
    					break;
    				}
    				
    				if(dap < 0.0001 || altura < 0.0001){
    					JsfUtil.addMessageError("El documento debe tener valores de DAP y altura mayores a 0.0001");    					
    					for (RegistroEspeciesForestales rowEspecie : listEspeciesTemp) {
							listRegistroEspeciesForestales.remove(rowEspecie);
						}
    					validarFormato = false;
    					break;
    				}    				
    				
    				registroEspeciesForestales.setCodigoMuestra(codigoMuestra);
    				registroEspeciesForestales.setNumeroIndividuo(getNumeroIndividuo(codigoMuestra));
    				registroEspeciesForestales.setFamiliaEspecie(objFamilia);
    				registroEspeciesForestales.setGeneroEspecie(objGenero);
    				registroEspeciesForestales.setEspecieEspecie(objEspecie);
    				registroEspeciesForestales.setDiametroEspecie(dap);
    				registroEspeciesForestales.setAlturaEspecie(altura);
    				registroEspeciesForestales.setIdFamilia(objFamilia.getId());
    				registroEspeciesForestales.setIdGenero(objGenero.getId());
    				registroEspeciesForestales.setIdEspecie(objEspecie.getId());

    				BigDecimal area = calculoAreaBasal(registroEspeciesForestales.getDiametroEspecie());
    				BigDecimal volumen = calculoVolumenTotal(area, registroEspeciesForestales.getAlturaEspecie());
    				registroEspeciesForestales.setAreaBasalEspecie(area);
    				registroEspeciesForestales.setVolumenTotalEspecie(volumen);
    				registroEspeciesForestales.setDesdeExcel(true);
    				
    				listEspeciesTemp.add(registroEspeciesForestales);
    				listRegistroEspeciesForestales.add(registroEspeciesForestales);
    				
    				String[] split=event.getFile().getContentType().split("/");
    		    	String extension = "."+split[split.length-1];
    		    	documentoEspecies.setNombreDocumento(event.getFile().getFileName());
    		    	documentoEspecies.setMimeDocumento(event.getFile().getContentType());
    		    	documentoEspecies.setContenidoDocumento(event.getFile().getContents());
    		    	documentoEspecies.setExtencionDocumento(extension);
    			}
    			rows++;    			
    		}
    		if(validarFormato) {
    			JsfUtil.addMessageError("Error en el archivo de especies");
    		}else if(listRegistroEspeciesForestales.isEmpty()){
    			JsfUtil.addMessageError("El formato del archivo es incorrecto");
    		}else{
    			sumatoriaEspecies();
    		}
    		resetEspecies();
		} catch (Exception e) {
			documentoEspecies = new DocumentoInventarioForestal();
			JsfUtil.addMessageError("Error en el archivo de especies");
            LOG.error(e);
		}
    }
    // Calculos de Datos Especies
    public BigDecimal calculoAreaBasal(Double diametro) {
    	BigDecimal area = new BigDecimal(0.0000);
		area = BigDecimal.valueOf(diametro * diametro * VALOR_AREA_BASAL);
		area = area.setScale(4, BigDecimal.ROUND_HALF_UP);
		return area;
    }
    public void calculoVolumenTotal() {
    	if(registroEspeciesForestales.getAreaBasalEspecie() != null && !registroEspeciesForestales.getAreaBasalEspecie().equals("")){
    		BigDecimal volumen = new BigDecimal(0.0000);
        	volumen = registroEspeciesForestales.getAreaBasalEspecie().multiply( BigDecimal.valueOf(registroEspeciesForestales.getAlturaEspecie()));
        	volumen = volumen.multiply(BigDecimal.valueOf(VALOR_VOLUMEN));
        	volumen = volumen.setScale(4, BigDecimal.ROUND_HALF_UP);
    		registroEspeciesForestales.setVolumenTotalEspecie(volumen);
    		volumenTotalEspecie = volumen.toString();
    	}else{
    		JsfUtil.addMessageError("Debe ingresar el campo DAP (Diámetro a la latura del pecho (m))");
    		registroEspeciesForestales.setAlturaEspecie(null);
    	}    	
    }
    public BigDecimal calculoVolumenTotal(BigDecimal area, Double altura) {
    	BigDecimal volumen = new BigDecimal(0.0000);
    	BigDecimal subtotal = area.multiply(BigDecimal.valueOf(altura));
    	volumen = subtotal.multiply(BigDecimal.valueOf(VALOR_VOLUMEN));
    	volumen = volumen.setScale(4, BigDecimal.ROUND_HALF_UP);
		return volumen;
    }
    private Integer getNumeroIndividuo(String codigoMuestra) {
    	Integer numeroIndividuo = 1;
    	List<RegistroEspeciesForestales> listEspeciesTemp = new ArrayList<RegistroEspeciesForestales>();
    	if (listRegistroEspeciesForestales.size() > 0) {
    		listEspeciesTemp.addAll(listRegistroEspeciesForestales);
    		for (RegistroEspeciesForestales rowEspecie : listEspeciesTemp) {
    			if (rowEspecie.getCodigoMuestra().equals(codigoMuestra)) {
    				numeroIndividuo ++;
    			}
    		}
    	}
    	return numeroIndividuo;
    }
    private Integer getNumeroEspecies(Integer idFamilia, Integer idGenero, Integer idEspecie) {
    	Integer numeroIndividuo = 0;
    	List<RegistroEspeciesForestales> listEspeciesTemp = new ArrayList<RegistroEspeciesForestales>();
    	if (listRegistroEspeciesForestales.size() > 0) {
    		listEspeciesTemp.addAll(listRegistroEspeciesForestales);
    		for (RegistroEspeciesForestales rowEspecie : listEspeciesTemp) {

    			rowEspecie.setIdFamilia(rowEspecie.getFamiliaEspecie().getId());
    			rowEspecie.setIdGenero(rowEspecie.getGeneroEspecie().getId());
    			if (rowEspecie.getEspecieEspecie() != null) {
					rowEspecie.setIdEspecie(rowEspecie.getEspecieEspecie().getId());
				}
    			if (rowEspecie.getIdFamilia().intValue() == idFamilia.intValue()) {
					if (rowEspecie.getIdGenero().intValue() == idGenero.intValue()) {
						if ((rowEspecie.getIdEspecie() == null) || (idEspecie != null && rowEspecie.getIdEspecie().intValue() == idEspecie.intValue())) {
							numeroIndividuo ++;
						}
					}
				}
    		}
    	}
    	return numeroIndividuo;
    }
    private Double getAreaBasalByEspecies(SpecieTaxa especie) {
    	Double areaBasal = 0.0000;
    	List<RegistroEspeciesForestales> listEspeciesTemp = new ArrayList<RegistroEspeciesForestales>();
    	if (listRegistroEspeciesForestales.size() > 0) {
    		listEspeciesTemp.addAll(listRegistroEspeciesForestales);
    		for (RegistroEspeciesForestales rowEspecie : listEspeciesTemp) {
    			if (especie.getId() == null) {
    				HigherClassification genero = especie.getHigherClassification();
    				if (genero.getId().intValue() == rowEspecie.getIdGenero().intValue()) {
    					HigherClassification familia = genero.getHiclIdParent();
						if (familia.getId().intValue() == rowEspecie.getIdFamilia().intValue()) {
							areaBasal += rowEspecie.getAreaBasalEspecie().doubleValue();
						}
					}
				} else {
					if (rowEspecie.getIdEspecie() != null && especie.getId().intValue() == rowEspecie.getIdEspecie().intValue()) {
						areaBasal += rowEspecie.getAreaBasalEspecie().doubleValue();
					}
				}
    		}
    	}
    	return areaBasal;
    }
    private Integer getTotalFrecuencias() {
    	Integer totalFrecuencia = 0;
    	if (listIndiceValorImportanciaInventarioForestal.size() > 0) {
    		for (IndiceValorImportanciaInventarioForestal row : listIndiceValorImportanciaInventarioForestal) {
    			totalFrecuencia += row.getFrecuenciaEspecies();
    		}
    	}
    	return totalFrecuencia;
    }
    private Double getTotalAreaBasal() {
    	Double totalAreaBasal = 0.0000;
    	if (listIndiceValorImportanciaInventarioForestal.size() > 0) {
    		for (IndiceValorImportanciaInventarioForestal row : listIndiceValorImportanciaInventarioForestal) {
    			totalAreaBasal += row.getAreaBasal();
    		}
    	}
    	// Evitar divisiones para cero (0)
    	if (totalAreaBasal == 0.0000) {
    		totalAreaBasal = 1.00;
		}
    	return totalAreaBasal;
    }
    
    public void sumatoriaEspecies() {
    	if(getMetodoRecoleccionDatos().contains("MUESTREO")) {
    		sumatoriaEspeciesMuestreo();
    	}
    	if(getMetodoRecoleccionDatos().contains("CENSO")) {
    		sumatoriaEspeciesCenso();
    	}
    }

    public void sumatoriaEspeciesMuestreo() {
    	listRegistroAmbientalSumatoria = new ArrayList<RegistroAmbientalSumatoria>();
		List<RegistroEspeciesForestales> listEspeciesTemp = new ArrayList<RegistroEspeciesForestales>();
    	if (listRegistroEspeciesForestales.size() > 0) {
    		listEspeciesTemp.addAll(listRegistroEspeciesForestales);
    		for (RegistroEspeciesForestales rowEspecie : listEspeciesTemp) {
    			Boolean enResumen = false;
    			BigDecimal sumatoriaAreaBasal = new BigDecimal(0.0000); 
    	    	BigDecimal sumatoriaVolumen = new BigDecimal(0.0000);
    			for (RegistroAmbientalSumatoria rowSuma : listRegistroAmbientalSumatoria) {
    				if (rowEspecie.getCodigoMuestra().equals(rowSuma.getCodigoMuestra())) {
    					enResumen = true;    
    					sumatoriaAreaBasal = rowSuma.getSumatoriaAreaBasal();
    					sumatoriaVolumen = rowSuma.getSumatoriaVolumen();
    					sumatoriaAreaBasal = sumatoriaAreaBasal.add(rowEspecie.getAreaBasalEspecie());    					
    					sumatoriaVolumen = sumatoriaVolumen.add(rowEspecie.getVolumenTotalEspecie());
    					rowSuma.setSumatoriaAreaBasal(sumatoriaAreaBasal);
    					rowSuma.setSumatoriaVolumen(sumatoriaVolumen);
    				}
    			}
    			if (!enResumen) {
					RegistroAmbientalSumatoria newResumen = new RegistroAmbientalSumatoria();
					newResumen.setCodigoMuestra(rowEspecie.getCodigoMuestra());
					newResumen.setSumatoriaAreaBasal(rowEspecie.getAreaBasalEspecie());
					newResumen.setSumatoriaVolumen(rowEspecie.getVolumenTotalEspecie());
					listRegistroAmbientalSumatoria.add(newResumen);
				}
    		}
		} else {
			JsfUtil.addMessageError("Ingrese a menos una especie para realizar el cálculo");
		}
    	RequestContext.getCurrentInstance().update("form:tblSumatoria");
    }
    public void sumatoriaEspeciesCenso() {
    	try {
    		BigDecimal sumaAreaBasal = new BigDecimal(0.0000); 
    		BigDecimal sumaVolumenTotal = new BigDecimal(0.0000);
    		
    		Double pagoDesbroce = 0.0000;
			Tarifas tarifa = tarifasFacade.buscarTarifasPorCodigo(CodigoTasa.INVENTARIO_FORESTAL_MADERA_PIE.getDescripcion());
			Double valorMaderaPie = tarifa.getTasasValor();
			if (listRegistroEspeciesForestales.size() > 0) {
				for (RegistroEspeciesForestales rowEspecie : listRegistroEspeciesForestales) {
					sumaAreaBasal = sumaAreaBasal.add(rowEspecie.getAreaBasalEspecie());
					sumaVolumenTotal = sumaVolumenTotal.add(rowEspecie.getVolumenTotalEspecie());
				}
				// BigDecimal subtotal = sumaVolumenTotal.multiply(BigDecimal.valueOf(inventarioForestalAmbiental.getSuperficieDesbroce().doubleValue()));
				// pagoDesbroceTotal = subtotal.multiply(BigDecimal.valueOf(VALOR_M3_MADERA_PIE));
				// pagoDesbroce = pagoDesbroceTotal.doubleValue();
				pagoDesbroce = sumaVolumenTotal.doubleValue() * valorMaderaPie.doubleValue();
				RegistroAmbientalSumatoria registroAmbientalSumatoria = listRegistroAmbientalSumatoria.isEmpty()?new RegistroAmbientalSumatoria():listRegistroAmbientalSumatoria.get(0);
				registroAmbientalSumatoria.setInventarioForestalAmbiental(inventarioForestalAmbiental);
				registroAmbientalSumatoria.setSuperficieDesbroce(inventarioForestalAmbiental.getSuperficieDesbroce());
				registroAmbientalSumatoria.setValorMaderaPie(tarifa.getTasasValor());
				registroAmbientalSumatoria.setSumatoriaAreaBasal(sumaAreaBasal);
				registroAmbientalSumatoria.setSumatoriaVolumenTotal(sumaVolumenTotal);
				
				BigDecimal pago = new BigDecimal(pagoDesbroce);
				pago = pago.setScale(2, BigDecimal.ROUND_HALF_UP);
								
				registroAmbientalSumatoria.setPagoDesbroceCobertura(pago.doubleValue());
				if (listRegistroAmbientalSumatoria.size() > 0) {
					listRegistroAmbientalSumatoria.clear();
				}
				listRegistroAmbientalSumatoria.add(registroAmbientalSumatoria);
				
				
				PromedioInventarioForestal promedioInventarioForestal = new PromedioInventarioForestal();
				promedioInventarioForestal.setInventarioForestalAmbiental(inventarioForestalAmbiental);
	    		promedioInventarioForestal.setValorMaderaPie(registroAmbientalSumatoria.getValorMaderaPie());
	    		promedioInventarioForestal.setPagoDesbroceCobertura(registroAmbientalSumatoria.getPagoDesbroceCobertura());
	    		promedioInventarioForestal.setObservacion("CENSO");
				listPromedioInventarioForestal.add(promedioInventarioForestal);
				
				if(mostrarValoracion){
					bienesServiciosInventarioForestal.setMaderableNoMaderable(registroAmbientalSumatoria.getPagoDesbroceCobertura());
				}
				
			} else {
				JsfUtil.addMessageError("Ingrese a menos una especie para realizar el cálculo");
			}
			RequestContext.getCurrentInstance().update(":form:tblSumatoria");
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public void promedioEspecies() throws ServiceException {
    	
		if (inventarioForestalAmbiental.getSuperficieDesbroce() == null
				|| inventarioForestalAmbiental.getSuperficieDesbroce().equals(
						"") || inventarioForestalAmbiental.getSuperficieDesbroce().equals(BigDecimal.valueOf(0))) {
			JsfUtil.addMessageError("Ingrese la Superficie total de desbroce de cobertura vegetal nativa (ha)");
			return;
		}
    	
    	if (listRegistroAmbientalSumatoria.size() > 0) {
    		boolean campoVacio = false;
    		for (RegistroAmbientalSumatoria rowPromedio : listRegistroAmbientalSumatoria) {
    			if(rowPromedio.getSuperficieSitio() == null || rowPromedio.getSuperficieSitio().equals("")){    
    				campoVacio = true;
    				break;
    			}
			}
    		
    		if(campoVacio){
    			JsfUtil.addMessageError("Ingrese la superficie del sitio de muestreo");
    			return;
    		}    			
    		
    		Integer numeroMuestra = listRegistroAmbientalSumatoria.size();
    		BigDecimal sumatoriaAreaBasal = new BigDecimal(0.0000); 
	    	BigDecimal sumatoriaVolumen = new BigDecimal(0.0000);
	    	BigDecimal sumatoriaSuperficieSitio = new BigDecimal(0.0000);
	    	Double superficieTotalDesbroce = inventarioForestalAmbiental.getSuperficieDesbroce().doubleValue();
    		for (RegistroAmbientalSumatoria rowPromedio : listRegistroAmbientalSumatoria) {
    			sumatoriaAreaBasal = sumatoriaAreaBasal.add(rowPromedio.getSumatoriaAreaBasal());
				sumatoriaVolumen = sumatoriaVolumen.add(rowPromedio.getSumatoriaVolumen());
				sumatoriaSuperficieSitio = sumatoriaSuperficieSitio.add(BigDecimal.valueOf(rowPromedio.getSuperficieSitio()));
			}
    		Double promedioSuperficieSitio = sumatoriaSuperficieSitio.doubleValue() / numeroMuestra;
    		Double promedioAreaBasal = sumatoriaAreaBasal.doubleValue() / numeroMuestra;
    		Double promedioVolumen = sumatoriaVolumen.doubleValue() / numeroMuestra;
    		Double valorAreaBasalPromedio = sumatoriaAreaBasal.doubleValue() / sumatoriaSuperficieSitio.doubleValue();
    		Double valorVolumenTotalPromedio = sumatoriaVolumen.doubleValue() / sumatoriaSuperficieSitio.doubleValue();
    		Tarifas tarifa = tarifasFacade.buscarTarifasPorCodigo(CodigoTasa.INVENTARIO_FORESTAL_MADERA_PIE.getDescripcion());
    		Double valorMaderaPie = tarifa.getTasasValor();
    		Double pagoDesbroceCobertura = valorVolumenTotalPromedio.doubleValue() * superficieTotalDesbroce * valorMaderaPie;
    		PromedioInventarioForestal promedioInventarioForestal = promedioInventarioForestalFacade.getByIdInventarioForestalRegistro(inventarioForestalAmbiental.getId());
    		promedioInventarioForestal.setPromedioAreaBasal(promedioAreaBasal);
    		promedioInventarioForestal.setPromedioVolumen(promedioVolumen);
    		promedioInventarioForestal.setValorAreaBasalPromedio(valorAreaBasalPromedio);
    		promedioInventarioForestal.setValorVolumenTotalPromedio(valorVolumenTotalPromedio);
    		promedioInventarioForestal.setValorMaderaPie(valorMaderaPie);
    		promedioInventarioForestal.setPagoDesbroceCobertura(pagoDesbroceCobertura);
    		promedioInventarioForestal.setObservacion("MUESTREO");
    		// Asigna el pago desbroce cobertura vegetal
    		bienesServiciosInventarioForestal.setMaderableNoMaderable(pagoDesbroceCobertura);
    		pagoTotalValoracionEconomica();
    		listPromedioInventarioForestal = new ArrayList<PromedioInventarioForestal>();
    		listPromedioInventarioForestal.add(promedioInventarioForestal);
    		actualizaPagoDesbroce = true;
    	} else {
			JsfUtil.addMessageError("Realizar primero el cálculo de sumatorias");
			actualizaPagoDesbroce = false;
		}
    }
    // Calculo IVI
    public void agruparEspecies() {
		List<RegistroEspeciesForestales> listEspeciesTemp = new ArrayList<RegistroEspeciesForestales>();
		listIndiceValorImportanciaInventarioForestal = new ArrayList<IndiceValorImportanciaInventarioForestal>();
    	if (listRegistroEspeciesForestales.size() > 0) {
    		listEspeciesTemp.addAll(listRegistroEspeciesForestales);
    		for (RegistroEspeciesForestales rowEspecie : listEspeciesTemp) {
    			IndiceValorImportanciaInventarioForestal newEspecie = new IndiceValorImportanciaInventarioForestal();
    			Boolean enResumen = false;
    			rowEspecie.setIdFamilia(rowEspecie.getFamiliaEspecie().getId());
    			rowEspecie.setIdGenero(rowEspecie.getGeneroEspecie().getId());
    			if (rowEspecie.getEspecieEspecie() != null) {
					rowEspecie.setIdEspecie(rowEspecie.getEspecieEspecie().getId());
				}
    			for (IndiceValorImportanciaInventarioForestal rowIVI : listIndiceValorImportanciaInventarioForestal) {
    				if (rowEspecie.getIdFamilia().intValue() == rowIVI.getIdFamilia().intValue()) {
						if (rowEspecie.getIdGenero().intValue() == rowIVI.getIdGenero().intValue()) {
							if ((rowEspecie.getEspecieEspecie().getId() == null && rowIVI.getEspecieEspecie().getId() == null) || (rowEspecie.getIdEspecie() !=null && rowIVI.getIdEspecie() != null && (rowEspecie.getIdEspecie().intValue() == rowIVI.getIdEspecie().intValue()))) {
								enResumen = true;
							}
						}
					}
    			}
    			if (!enResumen) {
    				SpecieTaxa especie = rowEspecie.getEspecieEspecie();
    				Double areaBasal = 0.00;
    				if (especie == null || especie.getSptaScientificName().equals("sp")) {
    					HigherClassification higherClassification = higherClassificationFacade.getById(rowEspecie.getIdGenero());
						especie.setHigherClassification(higherClassification);
						areaBasal = getAreaBasalByEspecies(especie);
					} else {
						areaBasal = getAreaBasalByEspecies(rowEspecie.getEspecieEspecie());
					}
    				Integer frecuenciaEspecies = getNumeroEspecies(rowEspecie.getIdFamilia(), rowEspecie.getIdGenero(), rowEspecie.getIdEspecie());
    				
    				newEspecie.setFamiliaEspecie(rowEspecie.getFamiliaEspecie());
    				newEspecie.setGeneroEspecie(rowEspecie.getGeneroEspecie());
    				newEspecie.setEspecieEspecie(rowEspecie.getEspecieEspecie());
    				newEspecie.setIdFamilia(rowEspecie.getIdFamilia());
    				newEspecie.setIdGenero(rowEspecie.getIdGenero());
    				newEspecie.setIdEspecie(rowEspecie.getIdEspecie());
    				newEspecie.setFrecuenciaEspecies(frecuenciaEspecies);
    				newEspecie.setAreaBasal(areaBasal);
    				listIndiceValorImportanciaInventarioForestal.add(newEspecie);
				}
    		}
		} else {
			JsfUtil.addMessageError("Realizar primero el ingreso de especies forestales");
		}
    }
    public void calculoIVI() {
    	if (listRegistroEspeciesForestales.size() > 0 && ((listPromedioInventarioForestal.size() > 0 && getMetodoRecoleccionDatos().contains("MUESTREO"))||getMetodoRecoleccionDatos().contains("CENSO"))) {
    		agruparEspecies();
    		for (IndiceValorImportanciaInventarioForestal rowIndice : listIndiceValorImportanciaInventarioForestal) {
				Double dnr = (Double.valueOf(rowIndice.getFrecuenciaEspecies()) / Double.valueOf(getTotalFrecuencias())) * 100;
				Double dmr = (rowIndice.getAreaBasal() / getTotalAreaBasal()) * 100;
				Double ivi = dnr + dmr;
				rowIndice.setDnr(dnr);
				rowIndice.setDmr(dmr);
				rowIndice.setIvi(ivi);
			}
    		actualizaCalculoIVI = true;
    	} else {
			JsfUtil.addMessageError("Realizar primero el ingreso de especies forestales");
			actualizaCalculoIVI = false;
		}
    }
    
    public void pagoTotalValoracionEconomica() {
    	Double pagoTotal = 0.00;
    	Double maderableNoMaderable = (bienesServiciosInventarioForestal.getMaderableNoMaderable() == null) ? 0.00 : bienesServiciosInventarioForestal.getMaderableNoMaderable();
    	Double almacenamientoCarbono = (bienesServiciosInventarioForestal.getAlmacenamientoCarbono() == null) ? 0.00 : bienesServiciosInventarioForestal.getAlmacenamientoCarbono();
		Double bellezaEscenica = (bienesServiciosInventarioForestal.getBellezaEscenica() == null) ? 0.00 : bienesServiciosInventarioForestal.getBellezaEscenica();
		Double agua = (bienesServiciosInventarioForestal.getAgua() == null) ? 0.00 : bienesServiciosInventarioForestal.getAgua();
		Double productosMedicinales = (bienesServiciosInventarioForestal.getProductosMedicinales() == null) ? 0.00 : bienesServiciosInventarioForestal.getProductosMedicinales();
		Double productosOrnamentales = (bienesServiciosInventarioForestal.getProductosOrnamentales() == null) ? 0.00 : bienesServiciosInventarioForestal.getProductosOrnamentales();
		Double productosArtesanales = (bienesServiciosInventarioForestal.getProductosArtesanales() == null) ? 0.00 : bienesServiciosInventarioForestal.getProductosArtesanales();
		pagoTotal = maderableNoMaderable + almacenamientoCarbono + bellezaEscenica + agua + productosMedicinales + productosOrnamentales + productosArtesanales;
		bienesServiciosInventarioForestal.setPagoTotal(pagoTotal);
    }
    
    public void asignarDocumentoValoracion(FileUploadEvent file) {
    	String[] split=file.getFile().getContentType().split("/");
    	String extension = "."+split[split.length-1];
    	documentoValoracion = new DocumentoInventarioForestal();
    	documentoValoracion.setNombreDocumento(file.getFile().getFileName());
    	documentoValoracion.setMimeDocumento(file.getFile().getContentType());
    	documentoValoracion.setContenidoDocumento(file.getFile().getContents());
    	documentoValoracion.setExtencionDocumento(extension);
    }
    
    public void validarCedula() throws MalformedURLException, javax.xml.rpc.ServiceException, RemoteException {

    	if(especialistaAmbiental.getIdentificacion() != null && !especialistaAmbiental.getIdentificacion().equals("")){
    		String cedulaRuc = especialistaAmbiental.getIdentificacion();
    		try{
	        	ConsultaRucCedula consultaRucCedula = new ConsultaRucCedula(Constantes.getUrlWsRegistroCivilSri());
	        	Cedula cedula = consultaRucCedula.obtenerPorCedulaRC(Constantes.USUARIO_WS_MAE_SRI_RC, Constantes.PASSWORD_WS_MAE_SRI_RC, cedulaRuc);
	        	if (cedula != null && cedula.getError().equals(Constantes.NO_ERROR_WS_MAE_SRI_RC)) {
	        		EspecialistaAmbiental especialistaBase = especialistaAmbientalCoaFacade.buscarEspecialistaPorNumregistroSenecyt(cedulaRuc, especialistaAmbiental.getNumeroRegistro());
		        	if (especialistaBase == null) {
		        		especialistaAmbiental.setNombre(cedula.getNombre());
		        		especialistaAmbiental = especialistaAmbientalCoaFacade.guardar(especialistaAmbiental);
		    		} else {
		    			especialistaAmbiental = especialistaBase;
		    		}
		        	String numerosTitulos=null;
		    		String nombresTitulos=null;
		    		GraduadoReporteDTO titulos = consultaRucCedula.obtenerTitulo(cedulaRuc);
		    		if(titulos != null && !titulos.getNiveltitulos().isEmpty()) {
		               for (NivelTitulosDTO titulo : titulos.getNiveltitulos()) {
		            	   numerosTitulos=numerosTitulos==null?"":numerosTitulos+";";
		            	   nombresTitulos=nombresTitulos==null?"":nombresTitulos+";";
		            	   numerosTitulos+=titulo.getTitulo().get(0).getNumeroRegistro();
		            	   nombresTitulos+=titulo.getTitulo().get(0).getNombreTitulo();        	   
		               }
		    		}
		    		especialistaAmbiental.setTitulo(nombresTitulos);
		    		especialistaAmbiental.setNumeroRegistro(numerosTitulos);
		
		        	inventarioForestalAmbiental.setEspecialistaAmbiental(especialistaAmbiental);
				} else {
					JsfUtil.addMessageError("Error al validar Cédula, Servicio Web No Disponible");
				}
    		} catch (Exception e) {
				JsfUtil.addMessageError("Error al validar Cédula o Ruc");
				return;
			}
    	}else{
    		JsfUtil.addMessageError("Ingrese el número de cédula");
    	}   	
    	
    }

    // Metodo para enviar el formulario
    private boolean validarDatos() {
    	boolean validar = true;
    	if (inventarioForestalAmbiental.getId() == null) {
			validar = false;
		}
    	if (getMetodoRecoleccionDatos().isEmpty()) {
    		JsfUtil.addMessageError("Método recolección datos requerido");
			validar = false;
		}
    	if (inventarioForestalAmbiental.getSuperficieDesbroce() == null) {
    		JsfUtil.addMessageError("Superficie de desbroce requerida");
			validar = false;
		}
    	if (inventarioForestalAmbiental.getFechaElaboracion() == null) {
    		JsfUtil.addMessageError("Fecha de Elaboración requerida");
			validar = false;
		}
    	if (inventarioForestalDetalle.getJustificacionTecnica() == null && getMetodoRecoleccionDatos().contains("MUESTREO")) {
    		JsfUtil.addMessageError("Justificación Técnica requerida");
			validar = false;
		}
    	if (coordinatesWrappers.size() == 0) {
    		JsfUtil.addMessageError("Ingrese al menos un sistema de refencia (coordenadas)");
			validar = false;
    	} else {
    		//Se cambio la validación realizando una consulta a la base de datos de las coordenadas cargadas al módulo de inventario forestal
    		if (shape.getId()!= null) {
    			List<CoordenadasInventarioForestalCertificado> ListCoordinatesWrappers = coordenadasInventarioForestalCertificadoFacade.getByShape(shape.getId());
    			if (ListCoordinatesWrappers!= null) {    				
    				for (CoordenadasInventarioForestalCertificado coordendaWrappers : ListCoordinatesWrappers) {					
    					if (coordendaWrappers.getId() == null) {
    						JsfUtil.addMessageError("Ingrese al menos un sistema de refencia (coordenadas)");
    						validar = false;						
    					}
    				}
    			}else {
    				JsfUtil.addMessageError("Ingrese al menos un sistema de refencia (coordenadas)");
    				validar = false;
    			}
    		}   	
    	}
    	if (listRegistroEspeciesForestales.size() == 0) {
    		JsfUtil.addMessageError("Ingrese al menos una especie");
    		validar = false;
    	} else {
			for (RegistroEspeciesForestales registroEspecies : listRegistroEspeciesForestales) {
				if (registroEspecies.getId() == null) {
					validar = false;					
				}
			}
		}
    	if (listRegistroAmbientalSumatoria.size() == 0) {
    		JsfUtil.addMessageError("Realizar la sumatoria de las muestras");
    		validar = false;
    	} else {
			for (RegistroAmbientalSumatoria sumatoria : listRegistroAmbientalSumatoria) {
				if (sumatoria.getId() == null) {
					validar = false;
				}
			}
		}
    	if (listPromedioInventarioForestal.size() == 0 && getMetodoRecoleccionDatos().contains("MUESTREO")) {
    		JsfUtil.addMessageError("Realizar el cálculo del promedio");
    		validar = false;
    	} else {
    		if (listPromedioInventarioForestal.size() > 0) {
    				if (listPromedioInventarioForestal.get(0).getId() == null) {    					
    					validar = false;
    				}
    		} else {
    			validar = false;
    		}
		}
    	if (listIndiceValorImportanciaInventarioForestal.size() == 0) {
    		JsfUtil.addMessageError("Realizar el cálculo del índice de valor de importancia");
    		validar = false;
    	} else {
			for (IndiceValorImportanciaInventarioForestal indiceValor : listIndiceValorImportanciaInventarioForestal) {
				if (indiceValor.getId() == null) {
					validar = false;
				}
			}
		}
    	if (actividadPrincipal.getCatalogoCIUU().getTipoSector().getId() == 1
				|| actividadPrincipal.getCatalogoCIUU().getTipoSector().getId() == 2
				|| actividadPrincipal.getCatalogoCIUU().getTipoSector().getId() == 4) {
    		if (documentoValoracion.getId() == null) {
    			JsfUtil.addMessageError("Falta Archivo de valoración económica de bienes y servicios ambientales");
    			validar = false;
    		}
		}
    	if (actualizaPagoDesbroce == false && getMetodoRecoleccionDatos().contains("MUESTREO")) {
    		JsfUtil.addMessageError("Actualizar el pago de desbroce");
    		validar = false;
		}
    	if (actualizaCalculoIVI == false) {
    		JsfUtil.addMessageError("Actualizar el Cálculo de índice de valor de importancia (IVI)");
    		validar = false;
		}
    	if (inventarioForestalAmbiental.getEspecialistaAmbiental() == null) {
    		JsfUtil.addMessageError("Ingrese el responsable del inventario forestal");
    		validar = false;
    	} else {
			if (inventarioForestalAmbiental.getEspecialistaAmbiental().getId() == null) {
				validar = false;
			}
		}
    	if (guardaTramite == false) {
    		JsfUtil.addMessageError("Debe guardar el trámite antes de continuar");
    		validar = false;
		}
    	return validar;
    }
    public void guardar() {    	
    	try {
    		Boolean banderaOK = true;
        	inventarioForestalAmbiental.setProyectoLicenciaCoa(proyectoLicenciaCoa);
        	inventarioForestalAmbiental.setTipoInventario(TIPO_INVENTARIO);
        	inventarioForestalAmbiental.setSuperficieProyecto(proyectoLicenciaCoa.getSuperficie());
        	BigDecimal superficie = (BigDecimal) ((inventarioForestalAmbiental.getSuperficieDesbroce() == null) ? BigDecimal.ZERO : inventarioForestalAmbiental.getSuperficieDesbroce());
        	inventarioForestalAmbiental.setSuperficieDesbroce(superficie);
        	inventarioForestalAmbiental = inventarioForestalAmbientalFacade.guardar(inventarioForestalAmbiental);
        	inventarioForestalDetalle.setInventarioForestalAmbiental(inventarioForestalAmbiental);
        	inventarioForestalDetalle.setJustificacionTecnica(justificacionTecnica);
        	inventarioForestalDetalle = inventarioForestalDetalleFacade.guardar(inventarioForestalDetalle);
        	
        	if(coordinatesWrappersEliminar != null && coordinatesWrappersEliminar.size() > 0) {
				for (int i = 0; i <= coordinatesWrappersEliminar.size() - 1; i++) {	
					for (int j = 0; j <= coordinatesWrappersEliminar.get(i).getCoordenadas().size() - 1; j++) {
	    				CoordenadasInventarioForestalCertificado coorImpl= coordinatesWrappersEliminar.get(i).getCoordenadas().get(j);
	    				if(coorImpl.getId() != null) {
	    					ShapeInventarioForestalCertificado shape = coorImpl.getShapeInventarioForestalCertificado();
	    					
	    					coorImpl.setEstado(false);
	    					coordenadasInventarioForestalCertificadoFacade.guardar(coorImpl);
	    					
	    					shape.setEstado(false);
	    					shapeInventarioForestalCertificadoFacade.guardar(shape);
	    				}
	    			}
        		}
				
				coordinatesWrappersEliminar = new ArrayList<CoordendasPoligonosCertificado>();
        	}

        	if (coordinatesWrappers.size() > 0) {
        		for(int i=0;i<=coordinatesWrappers.size()-1;i++){	
        			if (shape.getInventarioForestalAmbiental() == null) {
        				shape = new ShapeInventarioForestalCertificado();
        				shape.setInventarioForestalAmbiental(inventarioForestalAmbiental);
    	    			shape=shapeInventarioForestalCertificadoFacade.guardar(shape);
    	    			for (int j = 0; j <=coordinatesWrappers.get(i).getCoordenadas().size()-1; j++) {
    	    				CoordenadasInventarioForestalCertificado coorImpl= new CoordenadasInventarioForestalCertificado();
    	    				coorImpl.setShapeInventarioForestalCertificado(shape);
    	    				coorImpl.setAreaGeografica(coordinatesWrappers.get(i).getCoordenadas().get(j).getAreaGeografica());
    	    				coorImpl.setOrdenCoordenada(coordinatesWrappers.get(i).getCoordenadas().get(j).getOrdenCoordenada());
    	    				coorImpl.setX(coordinatesWrappers.get(i).getCoordenadas().get(j).getX());
    	    				coorImpl.setY(coordinatesWrappers.get(i).getCoordenadas().get(j).getY());
    	    				coordenadasInventarioForestalCertificadoFacade.guardar(coorImpl);
    	    			}
        			}
        		}
    		}
        	
        	if (listRegistroEspeciesForestalesEliminar != null && !listRegistroEspeciesForestalesEliminar.isEmpty()) {
        		for (RegistroEspeciesForestales especieEliminar : listRegistroEspeciesForestalesEliminar) {
        			if(especieEliminar.getId() != null) {
	        			especieEliminar.setEstado(false);
	        			especieEliminar = registroEspeciesForestalesFacade.guardar(especieEliminar);
        			}
    			}
        		
        		listRegistroEspeciesForestalesEliminar = new ArrayList<>();
    		}
        	
        	if (listRegistroEspeciesForestales != null && !listRegistroEspeciesForestales.isEmpty()) {
        		List<RegistroEspeciesForestales> especiesBase = registroEspeciesForestalesFacade.getByInventarioForestalCertificado(inventarioForestalAmbiental.getId());
        		if (especiesBase != null && !especiesBase.isEmpty()) {
        			for (RegistroEspeciesForestales especieBase : especiesBase) {
        				especieBase.setEstado(false);
        				especieBase = registroEspeciesForestalesFacade.guardar(especieBase);
    				}
        			for (RegistroEspeciesForestales especies : listRegistroEspeciesForestales) {
        				Boolean ingreso = true;
        				for (RegistroEspeciesForestales especieBase : especiesBase) {
        					especies.setInventarioForestalAmbiental(inventarioForestalAmbiental);
        					especies.setIdFamilia(especies.getFamiliaEspecie().getId());
        					especies.setIdGenero(especies.getGeneroEspecie().getId());
        					especies.setIdEspecie(especies.getEspecieEspecie().getId());
        					if (especies.getIdEspecie() == null) {
        						if (especies.getIdFamilia().equals(especieBase.getIdFamilia()) && especies.getIdGenero().equals(especieBase.getIdGenero()) && 
    									especies.getDiametroEspecie().equals(especieBase.getDiametroEspecie()) && especies.getAlturaEspecie().equals(especieBase.getAlturaEspecie())) {
    								especieBase.setEstado(true);
    			    				especieBase = registroEspeciesForestalesFacade.guardar(especieBase);
    			    				ingreso = false;
    			    				break;
    							}
    						} else {
    							if (especies.getIdFamilia().equals(especieBase.getIdFamilia()) && especies.getIdGenero().equals(especieBase.getIdGenero()) && 
    									especies.getIdEspecie().equals(especieBase.getIdEspecie()) && especies.getDiametroEspecie().equals(especieBase.getDiametroEspecie()) && 
    									especies.getAlturaEspecie().equals(especieBase.getAlturaEspecie())) {
    								especieBase.setEstado(true);
    			    				especieBase = registroEspeciesForestalesFacade.guardar(especieBase);
    			    				ingreso = false;
    			    				break;
    							}
    						}
    					}
        				if (ingreso) {
        					especies = registroEspeciesForestalesFacade.guardar(especies);
        				}
        			}
    			} else {
    				if(listRegistroEspeciesForestales != null && !listRegistroEspeciesForestales.isEmpty()){
    					for (RegistroEspeciesForestales especies : listRegistroEspeciesForestales) {
        					especies.setInventarioForestalAmbiental(inventarioForestalAmbiental);
        					especies.setIdFamilia(especies.getFamiliaEspecie().getId());
        					especies.setIdGenero(especies.getGeneroEspecie().getId());
        					especies.setIdEspecie(especies.getEspecieEspecie().getId());
        					especies = registroEspeciesForestalesFacade.guardar(especies);
        				}
    				}    				
    			}
    		}
        	
        	if (listRegistroAmbientalSumatoria != null && !listRegistroAmbientalSumatoria.isEmpty()) {
    			for (RegistroAmbientalSumatoria rowSuma : listRegistroAmbientalSumatoria) {
    				rowSuma.setInventarioForestalAmbiental(inventarioForestalAmbiental);
    				rowSuma = registroAmbientalSumatoriaFacade.guardar(rowSuma);
    			}
    		}
        	
        	if (listRegistroAmbientalSumatoriaEliminar != null && !listRegistroAmbientalSumatoriaEliminar.isEmpty()) {
    			for (RegistroAmbientalSumatoria rowSuma : listRegistroAmbientalSumatoriaEliminar) {
    				if(rowSuma.getId() != null) {
    					rowSuma.setEstado(false);
    					registroAmbientalSumatoriaFacade.guardar(rowSuma);
    				}
    			}
    			
    			listRegistroAmbientalSumatoriaEliminar = new ArrayList<>();
    		}
        	
        	if (listPromedioInventarioForestal != null && !listPromedioInventarioForestal.isEmpty()) {
    			PromedioInventarioForestal promedioInventarioForestal = listPromedioInventarioForestal.get(0);
    			promedioInventarioForestal.setInventarioForestalAmbiental(inventarioForestalAmbiental);
    			promedioInventarioForestal = promedioInventarioForestalFacade.guardar(promedioInventarioForestal);
    		}else{
    			banderaOK = false;
    			JsfUtil.addMessageError("Error al guardar el promedio. Por favor comuníquese con Mesa de Ayuda");
    		}
        	
        	if (listIndiceValorImportanciaInventarioForestal != null && !listIndiceValorImportanciaInventarioForestal.isEmpty()) {
        		List<IndiceValorImportanciaInventarioForestal> listIndiceBase = new ArrayList<IndiceValorImportanciaInventarioForestal>();
        		List<IndiceValorImportanciaInventarioForestal> listIndiceEliminar = new ArrayList<IndiceValorImportanciaInventarioForestal>();
        		listIndiceBase = indiceValorImportanciaInventarioForestalFacade.getByIdInventarioForestalRegistro(inventarioForestalAmbiental.getId());
        		if (listIndiceBase != null && !listIndiceBase.isEmpty()) {
    				for (IndiceValorImportanciaInventarioForestal rowIndiceBase : listIndiceBase) {
    					Boolean forEliminate = true;
    					for (IndiceValorImportanciaInventarioForestal rowIndice : listIndiceValorImportanciaInventarioForestal) {
    						if (rowIndiceBase.equals(rowIndice)) {
    							forEliminate = false;
    						}
    					}
    					if (forEliminate) {
    						rowIndiceBase.setEstado(false);
    						listIndiceEliminar.add(rowIndiceBase);
    					}
    				}
    				for (IndiceValorImportanciaInventarioForestal row : listIndiceEliminar) {
    					row = indiceValorImportanciaInventarioForestalFacade.guardar(row);
    				}
    			}
    			for (IndiceValorImportanciaInventarioForestal rowIndice : listIndiceValorImportanciaInventarioForestal) {
    				rowIndice.setIdFamilia(rowIndice.getFamiliaEspecie().getId());
    				rowIndice.setIdGenero(rowIndice.getGeneroEspecie().getId());
    				rowIndice.setIdEspecie(rowIndice.getEspecieEspecie().getId());
    				rowIndice.setInventarioForestalAmbiental(inventarioForestalAmbiental);
    				rowIndice = indiceValorImportanciaInventarioForestalFacade.guardar(rowIndice);
    			}
    		}
        	
        	if (bienesServiciosInventarioForestal.getMaderableNoMaderable() != null) {
    			bienesServiciosInventarioForestal.setInventarioForestalAmbiental(inventarioForestalAmbiental);
    			bienesServiciosInventarioForestalFacade.guardar(bienesServiciosInventarioForestal);
    		}
        	
        	especialistaAmbiental = especialistaAmbientalCoaFacade.guardar(especialistaAmbiental);
        	
        	if (inventarioForestalAmbiental.getEspecialistaAmbiental() != null) {
    			inventarioForestalAmbiental.setEspecialistaAmbiental(especialistaAmbiental);
    			especialistaAmbiental = especialistaAmbientalCoaFacade.guardar(especialistaAmbiental);
    			inventarioForestalAmbiental = inventarioForestalAmbientalFacade.guardar(inventarioForestalAmbiental);
    		}

        	if (documentoCoordenadas.getNombreDocumento() != null) {
        		try {
        			documentoCoordenadas.setInventarioForestalAmbiental(inventarioForestalAmbiental);
        			documentoCoordenadas.setIdTabla(inventarioForestalAmbiental.getId());
        			documentoCoordenadas.setNombreTabla(InventarioForestalAmbiental.class.getSimpleName());
        			documentoCoordenadas.setDescripcionTabla("Excel Coordenadas de Muestreo");
        			if (documentoCoordenadas.getIdAlfresco() == null) {
        				documentoCoordenadas = documentoInventarioForestalFacade.guardarDocumentoAlfrescoInventarioForestal(proyectoLicenciaCoa.getCodigoUnicoAmbiental(), "INVENTARIO_FORESTAL", 0L, documentoCoordenadas, TipoDocumentoSistema.RCOA_COORDENADAS_MUESTRAS);
    				}
        		} catch (Exception e) {
        			banderaOK = false;
        			JsfUtil.addMessageError("Error al guardar el archivo de coordenadas. Por favor comuníquese con Mesa de Ayuda");
        			LOG.error(e);
        		}
        	}
        	
        	if (documentoEspecies.getNombreDocumento() != null) {
        		try {
        			documentoEspecies.setInventarioForestalAmbiental(inventarioForestalAmbiental);
        			documentoEspecies.setIdTabla(inventarioForestalAmbiental.getId());
        			documentoEspecies.setNombreTabla(InventarioForestalAmbiental.class.getSimpleName());
        			documentoEspecies.setDescripcionTabla("Excel Listado de Especies");
        			if (documentoEspecies.getIdAlfresco() == null) {
        				documentoEspecies = documentoInventarioForestalFacade.guardarDocumentoAlfrescoInventarioForestal(proyectoLicenciaCoa.getCodigoUnicoAmbiental(), "INVENTARIO_FORESTAL", 0L, documentoEspecies, TipoDocumentoSistema.RCOA_LISTA_ESPECIES);
    				}
        		} catch (Exception e) {
        			banderaOK = false;
        			JsfUtil.addMessageError("Error al guardar el archivo de listado de especies. Por favor comuníquese con Mesa de Ayuda");
        			LOG.error(e);
        		}
        	}
        	
        	if (documentoValoracion.getNombreDocumento() != null && documentoValoracion.getId() == null) {
        		try {
        			documentoValoracion.setInventarioForestalAmbiental(inventarioForestalAmbiental);
        			documentoValoracion.setIdTabla(inventarioForestalAmbiental.getId());
        			documentoValoracion.setNombreTabla(InventarioForestalAmbiental.class.getSimpleName());
        			documentoValoracion.setDescripcionTabla("Valoración económica de bienes y servicios");
        			documentoValoracion = documentoInventarioForestalFacade.guardarDocumentoAlfrescoInventarioForestal(proyectoLicenciaCoa.getCodigoUnicoAmbiental(), "INVENTARIO_FORESTAL", 0L, documentoValoracion, TipoDocumentoSistema.RCOA_VALORACION_BIENES);
        		} catch (Exception e) {
        			banderaOK = false;
        			JsfUtil.addMessageError("Error al guardar el archivo de valoración económica. Por favor comuníquese con Mesa de Ayuda");
        			LOG.error(e);
        		}
        	}
        	
        	if (banderaOK) {
        		guardaTramite = true;
        		JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}    	
    }
    private Usuario asignarTecnicoForestal() {
    	Area areaTramite = proyectoLicenciaCoa.getAreaInventarioForestal();
    	String rolPrefijo;
		String rolTecnico;
    	rolPrefijo = "role.inventario.pc.tecnico";
    	rolTecnico = Constantes.getRoleAreaName(rolPrefijo);
    	List<Usuario> listaTecnicosResponsables = asignarTareaFacade.getCargaLaboralPorUsuariosV2(rolTecnico, areaTramite.getAreaName());			

		if (listaTecnicosResponsables==null || listaTecnicosResponsables.isEmpty()){
			LOG.error("No se encontro usuario " + rolTecnico + " en " + areaTramite.getAreaName());
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			return null;
		}
		Usuario tecnicoResponsable = listaTecnicosResponsables.get(0);
		return tecnicoResponsable;
    }

    public void enviar() {
    	if (validarDatos()) {
    		try {
    			
    			Usuario tecnico = (tecnicoForestal!=null && !tecnicoForestal.isEmpty())?usuarioFacade.buscarUsuario(tecnicoForestal):null;
    			
    			if(tecnico==null || tecnico.getEstado() == false) {
    				tecnico = asignarTecnicoForestal();
    			}
    			
    			if (tecnico == null) {
    				return;
    			}
    			
    			Map<String, Object> params=new HashMap<>();    			
    			params.put("tecnicoForestal",tecnico.getNombre());
    			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), params);
				notificacionParaRevision(tecnico);
    			
    			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
    			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
    	        JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
    		} catch (JbpmException e) {
    			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
    			e.printStackTrace();
    		}
		} 
    }
    
    private void notificacionParaRevision(Usuario uTecnico){		
		try {
			String nombreTecnico= uTecnico.getPersona().getNombre();
			String nombreProyecto=inventarioForestalAmbiental.getProyectoLicenciaCoa().getNombreProyecto();
			String codigoTramite=inventarioForestalAmbiental.getProyectoLicenciaCoa().getCodigoUnicoAmbiental();
			String mensaje = mensajeNotificacionFacade.recuperarValorMensajeNotificacion("bodyNotificacionIngresoInventarioForestal", new Object[]{nombreTecnico,nombreProyecto, codigoTramite});
			Email.sendEmail(uTecnico, "Inventario Forestal Certificado", mensaje, tramite, loginBean.getUsuario());				
		} catch (Exception e) {
			LOG.error("No se envio la notificacion al usuario. "+e.getCause()+" "+e.getMessage());
		}			
	}

    public void cargarinventarioForestalRegistro() {
    	try {
    		Integer idRegistroPreliminarProyecto = proyectoLicenciaCoa.getId();
	    	InventarioForestalAmbiental inventarioBase = inventarioForestalAmbientalFacade.getByIdRegistroPreliminar(idRegistroPreliminarProyecto);
	    	if (inventarioBase.getId() != null) {
				inventarioForestalAmbiental = inventarioBase;
				idInventarioForestalRegistro = inventarioForestalAmbiental.getId();
				inventarioForestalDetalle = inventarioForestalDetalleFacade.getByInventarioForestalAmbiental(idInventarioForestalRegistro);
				justificacionTecnica = inventarioForestalDetalle.getJustificacionTecnica();
				fechaElaboracion = inventarioForestalAmbiental.getFechaElaboracion();
			}

	    	List<ShapeInventarioForestalCertificado> listShapes = shapeInventarioForestalCertificadoFacade.getByInventarioForestalAmbiental(idInventarioForestalRegistro);
	    	for(int i=0;i<=listShapes.size()-1;i++){
	    		shape = new ShapeInventarioForestalCertificado();
    			shape = listShapes.get(i);
    			List<CoordenadasInventarioForestalCertificado> coorImpl= new ArrayList<CoordenadasInventarioForestalCertificado>();
    			coorImpl = coordenadasInventarioForestalCertificadoFacade.getByShape(shape.getId());
    			CoordendasPoligonosCertificado coordinatesWrapper = new CoordendasPoligonosCertificado(coorImpl, poligono);
    			coordinatesWrappers.add(coordinatesWrapper);
			}
	    	
	    	listRegistroEspeciesForestales = registroEspeciesForestalesFacade.getByInventarioForestalCertificado(idInventarioForestalRegistro);
	    	for (RegistroEspeciesForestales rowEspecies : listRegistroEspeciesForestales) {
	    		HigherClassification higherClassification = higherClassificationFacade.getById(rowEspecies.getIdGenero());
	    		rowEspecies.setGeneroEspecie(higherClassification);
	    		rowEspecies.setFamiliaEspecie(higherClassification.getHiclIdParent());
	    		SpecieTaxa especie = new SpecieTaxa();
	    		if (rowEspecies.getIdEspecie() == null) {
	    			especie.setSptaScientificName("sp");
				} else if (rowEspecies.getIdEspecie() == 0) {
					especie.setId(0);
					especie.setSptaScientificName("Otro");
					especie.setSptaOtherScientificName(rowEspecies.getOtroEspecie());
				} else {
					especie = specieTaxaFacade.getById(rowEspecies.getIdEspecie());
				}
	    		rowEspecies.setEspecieEspecie(especie);
			}
	    	
	    	listRegistroAmbientalSumatoria = registroAmbientalSumatoriaFacade.getByIdInventarioForestalRegistro(idInventarioForestalRegistro);
	    	if(listRegistroAmbientalSumatoria!=null && !listRegistroAmbientalSumatoria.isEmpty()) {
	    		listRegistroAmbientalSumatoria.get(0).setSuperficieDesbroce(inventarioForestalAmbiental.getSuperficieDesbroce());
	    	}
	    	
	    	PromedioInventarioForestal promedioBase = promedioInventarioForestalFacade.getByIdInventarioForestalRegistro(idInventarioForestalRegistro);
	    	if (promedioBase.getId() != null) {
				listPromedioInventarioForestal.add(promedioBase);
			}
	    	
	    	listIndiceValorImportanciaInventarioForestal = indiceValorImportanciaInventarioForestalFacade.getByIdInventarioForestalRegistro(idInventarioForestalRegistro);
	    	if (listIndiceValorImportanciaInventarioForestal.size() > 0) {
	    		for (IndiceValorImportanciaInventarioForestal rowIndice : listIndiceValorImportanciaInventarioForestal) {
	    			HigherClassification higherClassification = higherClassificationFacade.getById(rowIndice.getIdGenero());
	    			rowIndice.setGeneroEspecie(higherClassification);
		    		rowIndice.setFamiliaEspecie(higherClassification.getHiclIdParent());
		    		SpecieTaxa especie = new SpecieTaxa();
		    		if (rowIndice.getIdEspecie() == null) {
		    			especie.setSptaScientificName("sp");
					} else if (rowIndice.getIdEspecie() == 0) {
//						especie.setSptaOtherScientificName(rowIndice.getOtroEspecie());
					} else {
						especie = specieTaxaFacade.getById(rowIndice.getIdEspecie());
					}
		    		rowIndice.setEspecieEspecie(especie);
				}
			}
	    	
	    	bienesServiciosInventarioForestal = bienesServiciosInventarioForestalFacade.getByIdInventarioForestalRegistro(idInventarioForestalRegistro);

	    	documentoValoracion = documentoInventarioForestalFacade.getByInventarioTipoDocumento(idInventarioForestalRegistro, TipoDocumentoSistema.RCOA_VALORACION_BIENES);

	    	if (inventarioForestalAmbiental.getEspecialistaAmbiental() != null) {
				especialistaAmbiental = especialistaAmbientalCoaFacade.getById(inventarioForestalAmbiental.getEspecialistaAmbiental().getId());
			}
    	} catch (ServiceException | CmisAlfrescoException e) {
    		JsfUtil.addMessageError("Error obtener los Datos");
    		LOG.error(e);
    	}
    }
    
    public StreamedContent getPlantillaCoordenadasDesbroce() throws Exception {
    	
    	try {
    		plantillaCoordenadasDesbroce = documentoInventarioForestalFacade.descargarDocumentoPorNombreYDirectorioBase(Constantes.PLANTILLA_COORDENADAS_INVENTARIO_FORESTAL, null);
    		
		} catch (Exception e) {
			JsfUtil.addMessageError("Error al descargar la plantilla: Coordenadas área de desbroce");// TODO: handle exception
		}
    	
        DefaultStreamedContent content = null;
        try {
            if (plantillaCoordenadasDesbroce != null) {
                content = new DefaultStreamedContent(new ByteArrayInputStream(plantillaCoordenadasDesbroce));
                content.setName(Constantes.PLANTILLA_COORDENADAS_INVENTARIO_FORESTAL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }
    
    public String getMetodoRecoleccionDatos() {
    	if(inventarioForestalAmbiental.getMetodoRecoleccionDatos()!=null)
    		return inventarioForestalAmbiental.getMetodoRecoleccionDatos().getDescripcion().toUpperCase();
    	return "";
    }
    
    public void listenerMetodoRecoleccionDatos() {
    	listRegistroEspeciesForestales.clear();
    } 
    
    public String sumatoria(String campo) {
    	double valor=0;
    	Integer totalFrecuencia = getTotalFrecuencias();
    	Double totalAreaBasal = getTotalAreaBasal();
    	for (IndiceValorImportanciaInventarioForestal item : listIndiceValorImportanciaInventarioForestal) {    		
    		if (item.getDnr() == null) {
    			Double dnr = (Double.valueOf(item.getFrecuenciaEspecies()) / Double.valueOf(totalFrecuencia)) * 100;
    			item.setDnr(dnr);
    		}
    		if (item.getDmr() == null) {
    			Double dmr = (item.getAreaBasal() / totalAreaBasal) * 100;
    			item.setDmr(dmr);
    		}
    		if (item.getIvi() == null) {
    			Double ivi = item.getDnr() + item.getDmr();
    			item.setIvi(ivi);
    		}
    		
    		switch (campo) {
    		case "fr":
				valor+= item.getFrecuenciaEspecies();
				break;
			case "m2":
				valor+= item.getAreaBasal();
				break;
			case "dnr":
				valor+= item.getDnr();
				break;	
			case "dmr":
				valor+= item.getDmr();
				break;
			case "ivi":
				valor+= item.getIvi();
				break;
			default:
				break;
			}
    		
		}
    	
    	DecimalFormat df=new DecimalFormat(campo.contains("fr")?"#0":"#0.00");
    	return df.format(valor);
    }

}
