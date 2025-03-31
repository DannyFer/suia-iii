package ec.gob.ambiente.rcoa.inventarioForestal.controller;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import org.primefaces.context.DefaultRequestContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import _154._1._180._10._8080.consultatitulossenescytwsv3.servicioconsultatitulo.GraduadoReporteDTO;
import _154._1._180._10._8080.consultatitulossenescytwsv3.servicioconsultatitulo.NivelTitulosDTO;
import ec.gob.ambiente.cliente.ConsultaRucCedula;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.facade.CoordenadasProyectoCoaFacade;
import ec.gob.ambiente.rcoa.facade.EspecialistaAmbientalCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaAmbientalCoaShapeFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.CertificadoAmbientalSumatoriaFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.CoordenadasInventarioForestalCertificadoFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.DocumentoInventarioForestalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.HigherClassificationFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.InventarioForestalAmbientalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.RegistroEspeciesForestalesFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.ShapeInventarioForestalCertificadoFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.SpecieTaxaFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.model.CertificadoAmbientalSumatoria;
import ec.gob.ambiente.rcoa.inventarioForestal.model.CoordenadasInventarioForestalCertificado;
import ec.gob.ambiente.rcoa.inventarioForestal.model.DocumentoInventarioForestal;
import ec.gob.ambiente.rcoa.inventarioForestal.model.HigherClassification;
import ec.gob.ambiente.rcoa.inventarioForestal.model.InventarioForestalAmbiental;
import ec.gob.ambiente.rcoa.inventarioForestal.model.RegistroEspeciesForestales;
import ec.gob.ambiente.rcoa.inventarioForestal.model.ShapeInventarioForestalCertificado;
import ec.gob.ambiente.rcoa.inventarioForestal.model.SpecieTaxa;
import ec.gob.ambiente.rcoa.model.CoordenadasProyecto;
import ec.gob.ambiente.rcoa.model.EspecialistaAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaAmbientalCoaShape;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
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
public class InventarioForestalCertificadoController {
	
	private static final Logger LOG = Logger.getLogger(InventarioForestalCertificadoController.class);
	
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
	private CoordenadasProyectoCoaFacade coordenadasProyectoCoaFacade;
	@EJB
	private InventarioForestalAmbientalFacade inventarioForestalAmbientalFacade;
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
	private CertificadoAmbientalSumatoriaFacade certificadoAmbientalSumatoriaFacade;
    @EJB
	private DocumentoInventarioForestalFacade documentoInventarioForestalFacade;
    @EJB
    private AsignarTareaFacade asignarTareaFacade;
    @EJB
    private UsuarioFacade usuarioFacade;
    @EJB
    private EspecialistaAmbientalCoaFacade especialistaAmbientalCoaFacade;
    @EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
    @EJB
	private TarifasFacade tarifasFacade;
    
    
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
	private List<CertificadoAmbientalSumatoria> listCertificadoAmbientalSumatoria = new ArrayList<CertificadoAmbientalSumatoria>();
    @Getter
    @Setter
    private List<CoordendasPoligonosCertificado> coordinatesWrappers = new ArrayList<CoordendasPoligonosCertificado>();
    @Getter
    private List<TipoForma> tiposFormas  = new ArrayList<TipoForma>();
    
    
    
    /*Object*/
    @Setter
    @Getter
    private ProyectoLicenciaCoa proyectoLicenciaCoa = new ProyectoLicenciaCoa();
    @Setter
    @Getter
    private InventarioForestalAmbiental inventarioForestalAmbiental;
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
    private CoordendasPoligonosCertificado coor = new CoordendasPoligonosCertificado();
    @Setter
    @Getter
	private DocumentoInventarioForestal documentoCoordenadas = new DocumentoInventarioForestal();
    @Setter
    @Getter
	private DocumentoInventarioForestal documentoEspecies = new DocumentoInventarioForestal();
    @Setter
    @Getter
    private EspecialistaAmbiental especialistaAmbiental = new EspecialistaAmbiental();
    @Setter
    @Getter
    private CertificadoAmbientalSumatoria certificadoAmbientalSumatoria = new CertificadoAmbientalSumatoria();
    
    
	/*Boolean*/
    @Getter
    private boolean blockFamilia=true, blockGenero=true, blockEspecie=true, blockOtro=true, guardaTramite=false, nuevasCoordenadas=false;
    @Getter
    private Integer idRegistroPreliminar;
    @Setter
    @Getter
    private String tramite, tecnicoForestal="";
    @Getter
    @Setter
    private Date currentDate = new Date();
    @Setter
    @Getter
    private String volumenTotalEspecie="", areaBasalEspecie="";    
    @Setter
    @Getter
    private byte[] plantillaCoordenadasDesbroce;
    @Setter
    @Getter
    private Date fechaElaboracion;
    
    
    /*CONSTANTES*/
    public static final Integer TIPO_INVENTARIO = 1;
    public static final Double VALOR_AREA_BASAL = 0.7854;
    public static final Double VALOR_VOLUMEN = 0.7;
    public static final Double VALOR_M3_MADERA_PIE = 3.00;
    public static final Integer ID_LAYER_COBERTURA = 12;
    public static final Integer ID_ECOSISTEMAS = 13;
   
    
    @PostConstruct
	public void init() {
    	try {
    		variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getProcessId());
    		tramite = (String) variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
    		tecnicoForestal = (String) variables.get("tecnicoForestal");
    		idRegistroPreliminar = Integer.valueOf((String)variables.get(Constantes.ID_PROYECTO));
    		proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idRegistroPreliminar);
    		inventarioForestalAmbiental = new InventarioForestalAmbiental();
    		inventarioForestalAmbiental.setSuperficieProyecto(proyectoLicenciaCoa.getSuperficie());
    		// Listado de Familias PLANTAE
    		listFamilia = obtenerListaFamilia();
    		tiposFormas = tipoFormaFacade.listarTiposForma();
    		poligono = getTipoForma(TipoForma.TIPO_FORMA_POLIGONO);
    		cargarInventarioForestalCertificado();
    		
    		plantillaCoordenadasDesbroce = documentoInventarioForestalFacade
                    .descargarDocumentoPorNombreYDirectorioBase(Constantes.PLANTILLA_COORDENADAS_INVENTARIO_FORESTAL, null);
    	} catch (Exception e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
	}
    
    public void editarSuperficieDesbroce() {
    	certificadoAmbientalSumatoria.setSuperficieDesbroce(inventarioForestalAmbiental.getSuperficieDesbroce());
    	listCertificadoAmbientalSumatoria = new ArrayList<CertificadoAmbientalSumatoria>();
    	listCertificadoAmbientalSumatoria.add(certificadoAmbientalSumatoria);
    }
    public void asignarFechaElaboracion(SelectEvent event) throws Exception {
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    	String date = simpleDateFormat.format(event.getObject());
    	fechaElaboracion=new SimpleDateFormat("yyyy-MM-dd").parse(date);
    	inventarioForestalAmbiental.setFechaElaboracion(fechaElaboracion);
    }
    
    // Excel de Coordenadas
    public TipoForma getTipoForma(Integer id) {
        for (TipoForma tf : tiposFormas) {
            if (tf.getId()==id)
                return tf;
        }
        return null;
    	
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
            	nuevasCoordenadas=false;
            } else {
            	String[] split=event.getFile().getContentType().split("/");
            	String extension = "."+split[split.length-1];
            	documentoCoordenadas.setNombreDocumento(event.getFile().getFileName());
            	documentoCoordenadas.setMimeDocumento(event.getFile().getContentType());
            	documentoCoordenadas.setContenidoDocumento(event.getFile().getContents());
            	documentoCoordenadas.setExtencionDocumento(extension);
            	nuevasCoordenadas=true;
            }
            nuevasCoordenadas=true;
        } catch (Exception e) {
            LOG.error("Error procesando el excel de coordenadas", e);
        }
    }
    public void eliminarCoordenadas() {
    	Integer idInventarioForestalAmbiental = inventarioForestalAmbiental.getId();
    	List<ShapeInventarioForestalCertificado> listShapes = shapeInventarioForestalCertificadoFacade.getByInventarioForestalAmbiental(idInventarioForestalAmbiental);
    	for (ShapeInventarioForestalCertificado shapeInventario : listShapes) {
    		List<CoordenadasInventarioForestalCertificado> coorImpl= new ArrayList<CoordenadasInventarioForestalCertificado>();
    		coorImpl = coordenadasInventarioForestalCertificadoFacade.getByShape(shapeInventario.getId());
    		for (CoordenadasInventarioForestalCertificado coordenada : coorImpl) {
    			coordenada.setEstado(false);
    			coordenadasInventarioForestalCertificadoFacade.guardar(coordenada);
    		}
    		shapeInventario.setEstado(false);
    		shapeInventarioForestalCertificadoFacade.guardar(shapeInventario);
		}
    }
    

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

    // Registro de Especies
    public void calculoAreaBasal() {
    	BigDecimal area = new BigDecimal(0.0000);
		area =  BigDecimal.valueOf(registroEspeciesForestales.getDiametroEspecie() * registroEspeciesForestales.getDiametroEspecie() * VALOR_AREA_BASAL);
		area = area.setScale(4, BigDecimal.ROUND_HALF_UP);
	    registroEspeciesForestales.setAreaBasalEspecie(area);
	    areaBasalEspecie = area.toString();
    }
    public BigDecimal calculoAreaBasal(Double diametro) {
    	BigDecimal area = new BigDecimal(0.0000);
		area = BigDecimal.valueOf(diametro * diametro * VALOR_AREA_BASAL);
		area = area.setScale(4, BigDecimal.ROUND_HALF_UP);
		return area;
    }
    public void calculoVolumenTotal() {
    	System.out.println("ingreso calculo volumen");
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
    public void agregarEspecies() {
    	
    	if(registroEspeciesForestales.getDiametroEspecie() == null){
    		JsfUtil.addMessageError("DAP requerida");
    		return;
    	}   	
    	
    	registroEspeciesForestales.setFamiliaEspecie(objFamilia);
    	registroEspeciesForestales.setGeneroEspecie(objGenero);
    	if (blockEspecie) {
    		objEspecie.setSptaScientificName("sp");
		} else if (objEspecie.getId() == 0) {
			objEspecie.setSptaOtherScientificName(registroEspeciesForestales.getOtroEspecie());
		}
    	registroEspeciesForestales.setEspecieEspecie(objEspecie);
    	if (!listRegistroEspeciesForestales.contains(registroEspeciesForestales)) {
			listRegistroEspeciesForestales.add(registroEspeciesForestales);
		}
    	
    	resetEspecies();
    	sumatoriaEspecies();
    	DefaultRequestContext.getCurrentInstance().execute("PF('especiesDiag').hide();");
    	RequestContext.getCurrentInstance().update("form:tblSumatoria");
    }
    public void editarEspecie(RegistroEspeciesForestales item) {
    	registroEspeciesForestales = item;
    	objFamilia = item.getFamiliaEspecie();
    	objGenero = item.getGeneroEspecie();
    	objEspecie = item.getEspecieEspecie();
    	areaBasalEspecie = registroEspeciesForestales.getAreaBasalEspecie().toString();
    	volumenTotalEspecie = registroEspeciesForestales.getVolumenTotalEspecie().toString();
    	obtenerListaGeneroPorFamilia();
    	obtenerListaEspecies();
    	if (registroEspeciesForestales.getNivelTaxonomia().equals("G")) {
    		blockFamilia=false;
        	blockGenero=false;
        	blockOtro=true;
		}
    	if (registroEspeciesForestales.getNivelTaxonomia().equals("E")) {
    		blockFamilia=false;
        	blockGenero=false;
        	blockEspecie=false;
		}
    	if (registroEspeciesForestales.getEspecieEspecie().getId() == null || registroEspeciesForestales.getEspecieEspecie().getId() == 0) {
    		blockOtro=false;
		}
    	sumatoriaEspecies();
    }
    public void eliminarEspecie(RegistroEspeciesForestales item) {
    	if (item.getId() != null) {
			listRegistroEspeciesForestalesEliminar.add(item);
			listCertificadoAmbientalSumatoria = new ArrayList<CertificadoAmbientalSumatoria>();
		}
    	listRegistroEspeciesForestales.remove(item);
    	sumatoriaEspecies();
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
    	areaBasalEspecie = "";
    	volumenTotalEspecie = "";
    }
    public void handleFileUploadEspecies(final FileUploadEvent event) {
    	
    	if(inventarioForestalAmbiental.getSuperficieDesbroce() == null || inventarioForestalAmbiental.getSuperficieDesbroce().equals("")){
    		JsfUtil.addMessageError("Superficie total de desbroce requerida antes de ingresar el archivo de especies");
    		return;
    	}    	
    	
    	int rows = 0;
    	Boolean existeError = false;
    	
    	List<RegistroEspeciesForestales> listEspeciesTemp = new ArrayList<RegistroEspeciesForestales>();
    	listRegistroEspeciesForestales = new ArrayList<RegistroEspeciesForestales>();
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
    				if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK && StringUtils.isNotBlank(cell.toString())) {
    					isEmptyRow = false;
    				}
    			}
    			if (isEmptyRow)
    				break;
    			
    			if (rows > 0) {
    				RegistroEspeciesForestales registroEspeciesForestales = new RegistroEspeciesForestales();
    				Iterator<Cell> cellIterator = row.cellIterator();
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
    					if (objGenero == null || objGenero.getId() == null) {
							JsfUtil.addMessageError("Género "+nombreCientifico+" desconocido");
							existeError = true;
	    					for (RegistroEspeciesForestales rowEspecie : listEspeciesTemp) {
								listRegistroEspeciesForestales.remove(rowEspecie);
							}
	    					break;
						} else {
							objFamilia = objGenero.getHiclIdParent(); 
							objEspecie.setSptaScientificName("sp");
						}
    				} else if (nivelTaxonomia.equals("ESPECIE")) {
    					registroEspeciesForestales.setNivelTaxonomia("E");
						objEspecie = specieTaxaFacade.getBySptaScientificName(nombreCientifico.trim());
						if (objEspecie == null || objEspecie.getId() == null) {
							JsfUtil.addMessageError("Especie "+nombreCientifico+" desconocida");
							existeError = true;
	    					for (RegistroEspeciesForestales rowEspecie : listEspeciesTemp) {
								listRegistroEspeciesForestales.remove(rowEspecie);
							}
	    					break;
						} else {
							objGenero = objEspecie.getHigherClassification();
							objFamilia = objGenero.getHiclIdParent();
						}
					} else {
    					JsfUtil.addMessageError("Documento con formato erróneo");
    					for (RegistroEspeciesForestales rowEspecie : listEspeciesTemp) {
							listRegistroEspeciesForestales.remove(rowEspecie);
						}
    					break;
    				}
    				
    				if(dap < 0.0001 || altura < 0.0001){
    					JsfUtil.addMessageError("El documento debe tener valores de DAP y altura mayores a 0.1");
    					existeError = true;
    					for (RegistroEspeciesForestales rowEspecie : listEspeciesTemp) {
							listRegistroEspeciesForestales.remove(rowEspecie);
						}
    					break;
    				}
    				
    				
    				registroEspeciesForestales.setFamiliaEspecie(objFamilia);
    				registroEspeciesForestales.setGeneroEspecie(objGenero);
    				registroEspeciesForestales.setEspecieEspecie(objEspecie);
    				registroEspeciesForestales.setDiametroEspecie(dap);
    				registroEspeciesForestales.setAlturaEspecie(altura);
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
    		
    		if(listRegistroEspeciesForestales.isEmpty() && !existeError){
    			JsfUtil.addMessageError("El formato del archivo es incorrecto");
    		}
    		else if(!listRegistroEspeciesForestales.isEmpty()){
    			sumatoriaEspecies();
    		}
    		resetEspecies();
		} catch (Exception e) {
			JsfUtil.addMessageError("Error en el archivo de especies");
            LOG.error(e);
		}
    }

    public void sumatoriaEspecies() {
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
				pagoDesbroce = sumaVolumenTotal.doubleValue() * valorMaderaPie.doubleValue();
				certificadoAmbientalSumatoria.setInventarioForestalAmbiental(inventarioForestalAmbiental);
				certificadoAmbientalSumatoria.setSuperficieDesbroce(inventarioForestalAmbiental.getSuperficieDesbroce());
				certificadoAmbientalSumatoria.setValorMaderaPie(tarifa.getTasasValor());
				certificadoAmbientalSumatoria.setSumatoriaAreaBasal(sumaAreaBasal);
				certificadoAmbientalSumatoria.setSumatoriaVolumenTotal(sumaVolumenTotal);
				BigDecimal pago = new BigDecimal(pagoDesbroce);
				pago = pago.setScale(2, BigDecimal.ROUND_HALF_UP);
				
				certificadoAmbientalSumatoria.setPagoDesbroceCobertura(pago.doubleValue());
				if (listCertificadoAmbientalSumatoria.size() > 0) {
					listCertificadoAmbientalSumatoria = new ArrayList<CertificadoAmbientalSumatoria>();
				}
				listCertificadoAmbientalSumatoria.add(certificadoAmbientalSumatoria);
			} else {
				JsfUtil.addMessageError("Ingrese a menos una especie para realizar el cálculo");
			}
			RequestContext.getCurrentInstance().update(":form:tblSumatoria");
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void validarCedula() {
    	if(especialistaAmbiental.getIdentificacion() != null && !especialistaAmbiental.getIdentificacion().equals("")){
    		String cedulaRuc = especialistaAmbiental.getIdentificacion();
        	
        	try {
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
		} else {
			JsfUtil.addMessageError("Ingrese el número de cédula");
		}
    }

    // Metodo para enviar el formulario
    private boolean validarDatos() {
    	boolean validar = true;
    	if (inventarioForestalAmbiental.getId() == null) {
			validar = false;
		}
    	if (inventarioForestalAmbiental.getFechaElaboracion() == null) {
    		JsfUtil.addMessageError("Fecha de elaboración requerida");
			validar = false;
		}
    	if (inventarioForestalAmbiental.getSuperficieDesbroce() == null) {
    		JsfUtil.addMessageError("Superfice de desbroce requerida");
			validar = false;
		}
    	if (coordinatesWrappers.size() == 0) {
    		JsfUtil.addMessageError("Ingrese al menos un sistema de refencia (coordenadas)");
			validar = false;
		} else {
			for (CoordendasPoligonosCertificado coordendaWrappers : coordinatesWrappers) {
				List<CoordenadasInventarioForestalCertificado> listCoordenada = coordendaWrappers.getCoordenadas();
				for (CoordenadasInventarioForestalCertificado coordenda : listCoordenada) {
					if (coordenda.getId() == null) {
						validar = false;						
					}
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
    	if (listCertificadoAmbientalSumatoria.size() == 0) {
    		JsfUtil.addMessageError("Realizar el cálculo del pago");
    		validar = false;
		} else {
			for (CertificadoAmbientalSumatoria sumatoria : listCertificadoAmbientalSumatoria) {
				if (sumatoria.getId() == null) {
					validar = false;
				}
			}
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
    	Boolean banderaOK = true;
    	inventarioForestalAmbiental.setProyectoLicenciaCoa(proyectoLicenciaCoa);
    	inventarioForestalAmbiental.setTipoInventario(TIPO_INVENTARIO);
    	inventarioForestalAmbiental.setSuperficieProyecto(proyectoLicenciaCoa.getSuperficie());
    	BigDecimal superficie = (BigDecimal) ((inventarioForestalAmbiental.getSuperficieDesbroce() == null) ? 0.00 : inventarioForestalAmbiental.getSuperficieDesbroce());
    	inventarioForestalAmbiental.setSuperficieDesbroce(superficie);
    	inventarioForestalAmbiental = inventarioForestalAmbientalFacade.guardar(inventarioForestalAmbiental);

    	if (nuevasCoordenadas) {
    		eliminarCoordenadas();
    		for(int i=0;i<=coordinatesWrappers.size()-1;i++){
    			ShapeInventarioForestalCertificado shape = new ShapeInventarioForestalCertificado();
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
    	
    	if (listRegistroEspeciesForestales.size() > 0) {
    		List<RegistroEspeciesForestales> especiesBase = registroEspeciesForestalesFacade.getByInventarioForestalCertificado(inventarioForestalAmbiental.getId());
    		if (especiesBase.size() > 0) {
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
							if (((especies.getId() != null && especies.getId().equals(especieBase.getId())) || (especies.getId() == null && !especieBase.getEstado())) &&
									especies.getIdFamilia().equals(especieBase.getIdFamilia()) && especies.getIdGenero().equals(especieBase.getIdGenero()) && 
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
				for (RegistroEspeciesForestales especies : listRegistroEspeciesForestales) {
					especies.setInventarioForestalAmbiental(inventarioForestalAmbiental);
					especies.setIdFamilia(especies.getFamiliaEspecie().getId());
					especies.setIdGenero(especies.getGeneroEspecie().getId());
					especies.setIdEspecie(especies.getEspecieEspecie().getId());
					especies = registroEspeciesForestalesFacade.guardar(especies);
				}
			}
		}else{
			banderaOK = false;
		}
    	
    	if (listRegistroEspeciesForestalesEliminar.size() > 0) {
    		for (RegistroEspeciesForestales especieEliminar : listRegistroEspeciesForestalesEliminar) {
    			especieEliminar.setEstado(false);
    			especieEliminar = registroEspeciesForestalesFacade.guardar(especieEliminar);
			}
			
		}
    	if (listCertificadoAmbientalSumatoria.size() == 0) {
    		certificadoAmbientalSumatoria.setSumatoriaAreaBasal(BigDecimal.valueOf(0.00));
    		certificadoAmbientalSumatoria.setSumatoriaVolumenTotal(BigDecimal.valueOf(0.00));
    		certificadoAmbientalSumatoria.setValorMaderaPie(0.00);
    		certificadoAmbientalSumatoria.setPagoDesbroceCobertura(0.00);
		}
    	certificadoAmbientalSumatoria = certificadoAmbientalSumatoriaFacade.guardar(certificadoAmbientalSumatoria);
    	
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
    	if (banderaOK) {
    		guardaTramite = true;
    		JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
		}else{
			guardaTramite = false;
			JsfUtil.addMessageWarning("Verifique que toda la información esté ingresada");
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
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		}
		Usuario tecnicoResponsable = listaTecnicosResponsables.get(0);
		return tecnicoResponsable;
    }

    public void enviar() {
    	if (validarDatos()) {
    		try {
    			Map<String, Object> params=new HashMap<String, Object>();
    			if (tecnicoForestal == null) {
    				Usuario tecnico = asignarTecnicoForestal();
    				params.put("tecnicoForestal",tecnico.getNombre());
    				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), params);
    				notificacionParaRevision(tecnico);
    			} else {
    				Usuario tecnicoBPM = usuarioFacade.buscarUsuario(tecnicoForestal);
    				if (tecnicoBPM.getEstado() == false) {
    					Usuario tecnico = asignarTecnicoForestal();
    					params.put("tecnicoForestal",tecnico.getNombre());
    					procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), params);
    					notificacionParaRevision(tecnico);
    				} else {
    					notificacionParaRevision(tecnicoBPM);
    				}
    			}
    			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
    			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
    	        JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
    		} catch (JbpmException e) {
    			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
    			e.printStackTrace();
    		}
		} else {
			JsfUtil.addMessageError("Error al enviar la Información. Por favor comuníquese con Mesa de Ayuda");
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
    
    public void cargarInventarioForestalCertificado() {
    	try {
    		Integer idRegistroPreliminarProyecto = proyectoLicenciaCoa.getId();
	    	InventarioForestalAmbiental inventarioBase = inventarioForestalAmbientalFacade.getByIdRegistroPreliminar(idRegistroPreliminarProyecto);
	    	if (inventarioBase.getId() != null) {
				inventarioForestalAmbiental = inventarioBase;
			}
	    	Integer idInventarioForestalAmbiental = inventarioForestalAmbiental.getId();
	    	fechaElaboracion = inventarioForestalAmbiental.getFechaElaboracion();
	    	certificadoAmbientalSumatoria = certificadoAmbientalSumatoriaFacade.getByIdInventarioForestalAmbiental(idInventarioForestalAmbiental);
	    	if (!listCertificadoAmbientalSumatoria.contains(certificadoAmbientalSumatoria)) {
	    		listCertificadoAmbientalSumatoria.add(certificadoAmbientalSumatoria);
			}

	    	List<ShapeInventarioForestalCertificado> listShapes = shapeInventarioForestalCertificadoFacade.getByInventarioForestalAmbiental(idInventarioForestalAmbiental);
	    	for(int i=0;i<=listShapes.size()-1;i++){
	    		ShapeInventarioForestalCertificado shape = new ShapeInventarioForestalCertificado();
    			shape = listShapes.get(i);
    			List<CoordenadasInventarioForestalCertificado> coorImpl= new ArrayList<CoordenadasInventarioForestalCertificado>();
    			coorImpl = coordenadasInventarioForestalCertificadoFacade.getByShape(shape.getId());
    			CoordendasPoligonosCertificado coordinatesWrapper = new CoordendasPoligonosCertificado(coorImpl, poligono);
    			coordinatesWrappers.add(coordinatesWrapper);
			}
	    	
	    	listRegistroEspeciesForestales = registroEspeciesForestalesFacade.getByInventarioForestalCertificado(idInventarioForestalAmbiental);
	    	if (listRegistroEspeciesForestales.size() > 0) {
	    		for (RegistroEspeciesForestales rowEspecies : listRegistroEspeciesForestales) {
	    			HigherClassification higherClassification = higherClassificationFacade.getById(rowEspecies.getIdGenero());
	    			rowEspecies.setGeneroEspecie(higherClassification);
	    			rowEspecies.setFamiliaEspecie(higherClassification.getHiclIdParent());
	    			SpecieTaxa especie = new SpecieTaxa();
	    			if (rowEspecies.getIdEspecie() == null) {
	    				especie.setSptaScientificName("sp");
	    			} else if (rowEspecies.getIdEspecie() == 0) {
	    				especie.setSptaOtherScientificName(rowEspecies.getOtroEspecie());
	    			} else {
	    				especie = specieTaxaFacade.getById(rowEspecies.getIdEspecie());
	    			}
	    			rowEspecies.setEspecieEspecie(especie);
	    		}
	    		sumatoriaEspecies();
			}
	    	
	    	if (listCertificadoAmbientalSumatoria.size() == 0) {
	    		CertificadoAmbientalSumatoria certificadoBase = certificadoAmbientalSumatoriaFacade.getByIdInventarioForestalAmbiental(idInventarioForestalAmbiental);
	    		if (certificadoBase.getId() != null) {
	    			listCertificadoAmbientalSumatoria = new ArrayList<CertificadoAmbientalSumatoria>();
	    			certificadoBase.setSuperficieDesbroce(inventarioForestalAmbiental.getSuperficieDesbroce());
	    			listCertificadoAmbientalSumatoria.add(certificadoBase);
	    		}
			}
	    	
	    	if (inventarioForestalAmbiental.getEspecialistaAmbiental() != null) {
				especialistaAmbiental = especialistaAmbientalCoaFacade.getById(inventarioForestalAmbiental.getEspecialistaAmbiental().getId());
			}
    	} catch (ServiceException e) {
    		JsfUtil.addMessageError("Error obtener los Datos");
    		LOG.error(e);
    	}
    }
    
    public StreamedContent getPlantillaCoordenadasDesbroce() throws Exception {
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
    
    public void validacionEspecies(){
    	if(inventarioForestalAmbiental.getSuperficieDesbroce() == null || inventarioForestalAmbiental.getSuperficieDesbroce().equals("")){
    		JsfUtil.addMessageError("Superficie total de desbroce requerida");
    		return;
    	}else{
    		DefaultRequestContext.getCurrentInstance().execute("PF('especiesDiag').show();");
    	}
    }
    
    public void validacionEspeciesArchivo(){
    	if(inventarioForestalAmbiental.getSuperficieDesbroce() == null || inventarioForestalAmbiental.getSuperficieDesbroce().equals("")){
    		JsfUtil.addMessageError("Superficie total de desbroce requerida");
    		return;
    	}else{
    		DefaultRequestContext.getCurrentInstance().execute("PF('adjuntarEspeciesDiag').show();");
    	}
    }
    

}
