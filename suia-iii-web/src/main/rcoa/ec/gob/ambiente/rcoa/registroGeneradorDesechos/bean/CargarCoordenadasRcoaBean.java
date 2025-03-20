package ec.gob.ambiente.rcoa.registroGeneradorDesechos.bean;

import index.Campos_coordenada;
import index.ContieneCapa_entrada;
import index.ContienePoligono_entrada;
import index.ContienePoligono_resultado;
import index.ContieneZona_entrada;
import index.ContieneZona_resultado;
import index.IntersecadoCapa_capa;
import index.Intersecado_capa;
import index.Intersecado_coordenada;
import index.Intersecado_entrada;
import index.Intersecado_resultado;
import index.InterseccionCapa_resultado;
import index.SVA_Reproyeccion_IntersecadoPortTypeProxy;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.rcoa.digitalizacion.facade.CoordenadaDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.ShapeDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.model.CoordenadaDigitalizacion;
import ec.gob.ambiente.rcoa.digitalizacion.model.ShapeDigitalizacion;
import ec.gob.ambiente.rcoa.facade.CapasCoaFacade;
import ec.gob.ambiente.rcoa.facade.CoordenadasProyectoCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaAmbientalCoaShapeFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.CapasCoa;
import ec.gob.ambiente.rcoa.model.CoordenadasProyecto;
import ec.gob.ambiente.rcoa.model.DetalleInterseccionProyectoAmbiental;
import ec.gob.ambiente.rcoa.model.InterseccionProyectoLicenciaAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaAmbientalCoaShape;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.clases.CoordinatesWrapper;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.DocumentosRgdRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosProyectosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.CoordenadaRgdCoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.DocumentosRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.PuntoRecuperacionRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosProyectosRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosRcoa;
import ec.gob.ambiente.rcoa.util.CoordendasPoligonos;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.crud.facade.PostgisFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Coordenada;
import ec.gob.ambiente.suia.domain.FormaProyecto;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TipoForma;
import ec.gob.ambiente.suia.domain.TipoProyeccion;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class CargarCoordenadasRcoaBean implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = Logger.getLogger(CargarCoordenadasRcoaBean.class);
	
	@EJB
    private PostgisFacade postgisFacade;
	@EJB
    private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
	@EJB
	private DocumentosRgdRcoaFacade documentosFacade;
	@EJB
	private ProyectoLicenciaAmbientalCoaShapeFacade proyectoLicenciaAmbientalShapeFacade;
	@EJB
	private CoordenadasProyectoCoaFacade coordenadasProyectoCoaFacade;
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private SecuenciasFacade secuenciasFacade;
    @EJB
    private CrudServiceBean crudServiceBean;
    @EJB
	private CapasCoaFacade capasCoaFacade;
    @EJB
    private UbicacionGeograficaFacade ubicacionfacade;
	@EJB
	private DocumentosRgdRcoaFacade documentosRgdRcoaFacade;
	@EJB
	private ShapeDigitalizacionFacade shapeDigitalizacionFacade;
	@EJB
	private CoordenadaDigitalizacionFacade coordenadaDigitalizacionFacade;
	@EJB
	private RegistroGeneradorDesechosRcoaFacade registroGeneradorFacade;
	@EJB
	private RegistroGeneradorDesechosProyectosRcoaFacade registroGeneradorDesechosProyectosRcoaFacade;
	
	
	/***************************************************************************************************/
	
	
	@Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyectoSuia;
	/****************************************************************************************************/
	
	@Getter
    private List<TipoForma> tiposFormas;
	
	@Getter
    private String[] nombresTiposFormas;

    @Getter
    @Setter
    private String updateComponentRoute;

    @Getter
    @Setter
    private boolean modalLoadFile;

    @Getter
    private boolean formatoVerificado;

    @Getter
    private boolean transformarFormato;
    
	@Getter
	@Setter
	private List<CoordinatesWrapper> coordinatesWrappers;
	
	@Getter
    private List<TipoProyeccion> tiposProyecciones;

    private List<TipoProyeccion> tiposProyeccionesFiltradas;
    
    @Getter
    @Setter
    private TipoProyeccion tipoProyeccion;
    
    @Getter
    private UploadedFile uploadedFile;

    @Getter
    private boolean transformarFormatoalerta;
    
//    private byte[] transformedFile;

    private byte[] plantillaCoordenadas;

    private byte[] ayudaCoordenadas; 
    
//    @Setter
//    @ManagedProperty(value = "#{adicionarUbicacionesBean}")
//    protected AdicionarUbicacionesBean adicionarUbicacionesBean;
    
    @Getter
    private String mensajeErrorCoordenada="";
    
    @Getter
    @Setter
    private boolean verUbicacion=true;
    
    @Getter
    @Setter
    private String formato="";
    
    @Getter
    @Setter
    private String zonaDescripcion="";
    
    @Getter
    protected boolean dialogWidgetVarCoordenadas;
    
   
    
    public String codigoProyecto="";
    
    private HashMap<String, Double> varUbicacionArea= new HashMap<String,Double>();
    
    @Getter
	@Setter
    private List<CoordendasPoligonos> coordinatesWrappersPre;
    
    private Map<String, Object> variables;
    
    private String tramite, tipoPermisoRGD;
    
    @Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;
    
    @ManagedProperty(value = "#{bandejaTareasBean}")
	@Getter
	@Setter
	private BandejaTareasBean bandejaTareasBean;
    
    private String coodenadasgeograficas="";
    
    @Getter
	@Setter
	private List<UbicacionesGeografica> ubicacionesSeleccionadas;
    
	@Setter
	@Getter
	private HashMap<String, UbicacionesGeografica> parroquiaSeleccionadas;
	
	private  HashMap<InterseccionProyectoLicenciaAmbiental, List<DetalleInterseccionProyectoAmbiental>> capasIntersecciones= new HashMap<InterseccionProyectoLicenciaAmbiental, List<DetalleInterseccionProyectoAmbiental>>();
	
	private List<CapasCoa> capas = new ArrayList<CapasCoa>();
			
	@Getter
	@Setter
	private String coordenadasPunto;
	private Integer tipoformaId=3;
	
	@PostConstruct
	public void init(){
		try {
			
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
			tipoPermisoRGD =(String)variables.get(Constantes.VARIABLE_TIPO_RGD);
			coordinatesWrappers = new ArrayList<CoordinatesWrapper>();
			coordinatesWrappersPre = new ArrayList<CoordendasPoligonos>();
			proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
			if(proyecto.getId() != null){
				
			}else{
				Integer proyectoId = Integer.valueOf(((String)variables.get("idProyectoDigitalizacion") == null)?"0":(String)variables.get("idProyectoDigitalizacion"));
				//verifico si viene de un proyecto digitalizado
				if(proyectoId > 0 ){
					RegistroGeneradorDesechosRcoa registroGeneradorDesechos = registroGeneradorFacade.buscarRGDPorProyectoDigitalizado(proyectoId);
					List<RegistroGeneradorDesechosProyectosRcoa> lista = new ArrayList<RegistroGeneradorDesechosProyectosRcoa>();
					if(registroGeneradorDesechos != null && registroGeneradorDesechos.getId() != null){
						lista = registroGeneradorDesechosProyectosRcoaFacade.buscarPorRegistroGenerador(registroGeneradorDesechos.getId());
					}
					if(lista != null && lista.size() > 0){
						// si es de digitalizacion ya no busco el proyecto aosciado
						List<ShapeDigitalizacion> objShape = shapeDigitalizacionFacade.obtenerShape(lista.get(0).getProyectoDigitalizado().getId(), 2);
						if(objShape != null && objShape.size() > 0){
							for (ShapeDigitalizacion shapeAux : objShape) {
								if(shapeAux.getTipoForma() != null)
									tipoformaId = shapeAux.getTipoForma().getId();
								List<CoordenadaDigitalizacion> coordenadas = coordenadaDigitalizacionFacade.obtenerCoordenadas(shapeAux.getId());
								coodenadasgeograficas="";
								if(coordenadas.size() == 1)
									tipoformaId = TipoForma.TIPO_FORMA_PUNTO;
								for (CoordenadaDigitalizacion coordenada : coordenadas) {
									coodenadasgeograficas += (coodenadasgeograficas == "") ? coordenada.getX().setScale(5, RoundingMode.HALF_DOWN).toString()+" "+coordenada.getY().setScale(5, RoundingMode.HALF_DOWN).toString() : ","+coordenada.getX().setScale(5, RoundingMode.HALF_DOWN).toString()+" "+coordenada.getY().setScale(5, RoundingMode.HALF_DOWN).toString();
								}
							}
						}
					}
				}else{
					proyectoSuia = new ProyectoLicenciamientoAmbiental();
					proyectoSuia = proyectoLicenciamientoAmbientalFacade.buscarProyectoPorCodigoCompleto(tramite);
					if(proyectoSuia != null && proyectoSuia.getId() != null){
						for (FormaProyecto listaFormas : proyectoSuia.getFormasProyectos()) {
							if (listaFormas.getTipoForma().getId().equals(TipoForma.TIPO_FORMA_POLIGONO)){
								coodenadasgeograficas="";
								for (Coordenada coordenada : listaFormas.getCoordenadas()) {
									coodenadasgeograficas += (coodenadasgeograficas == "") ? BigDecimal.valueOf(coordenada.getX()).setScale(5, RoundingMode.HALF_DOWN).toString()+" "+BigDecimal.valueOf(coordenada.getY()).setScale(5, RoundingMode.HALF_DOWN).toString() : ","+BigDecimal.valueOf(coordenada.getX()).setScale(5, RoundingMode.HALF_DOWN).toString()+" "+BigDecimal.valueOf(coordenada.getY()).setScale(5, RoundingMode.HALF_DOWN).toString();
								}
							}
						}
					}
				}
			}	
			
			tiposFormas = proyectoLicenciamientoAmbientalFacade.getTiposFormas();
			nombresTiposFormas = new String[tiposFormas.size()];
			int pos = 0;
	        for (TipoForma tipoForma : tiposFormas) {
	            nombresTiposFormas[pos++] = tipoForma.getNombre();
	        }
	        this.updateComponentRoute = ":form:containerCoordenadas";
	        this.modalLoadFile = true;
			
	        tiposProyecciones = postgisFacade.getTiposProyeccionesSoportadas();
	        
	        plantillaCoordenadas = "\u009d".getBytes();
	        
	        dialogWidgetVarCoordenadas=false;
	        codigoProyecto="";
	        
	        
		//cargando coordenadas rcoa
	        if(proyecto != null && proyecto.getId() != null){
			List<ProyectoLicenciaAmbientalCoaShape> formasImplantacion = proyectoLicenciaAmbientalShapeFacade.buscarFormaGeograficaPorProyecto(proyecto, 1, 0); //coordenadas implantacion
				
				if(formasImplantacion == null){
					formasImplantacion = new ArrayList<ProyectoLicenciaAmbientalCoaShape>();				
				}else{
					for(ProyectoLicenciaAmbientalCoaShape forma : formasImplantacion){
						List<CoordenadasProyecto> coordenadasGeograficasImplantacion = coordenadasProyectoCoaFacade.buscarPorForma(forma);
						
						CoordendasPoligonos poligono = new CoordendasPoligonos();
						poligono.setCoordenadas(coordenadasGeograficasImplantacion);
						poligono.setTipoForma(forma.getTipoForma());
						
						coordinatesWrappersPre.add(poligono);
						
						for(CoordenadasProyecto coordenada : coordenadasGeograficasImplantacion){
                                                                                                                                                                                            							coodenadasgeograficas += (coodenadasgeograficas == "") ? coordenada.getX().setScale(5, RoundingMode.HALF_DOWN).toString()+" "+coordenada.getY().setScale(5, RoundingMode.HALF_DOWN).toString() : ","+coordenada.getX().setScale(5, RoundingMode.HALF_DOWN).toString()+" "+coordenada.getY().setScale(5, RoundingMode.HALF_DOWN).toString();
						}					
					}				
				}
	        }
			
			capas=capasCoaFacade.listaCapas();
			
			deleteFileTmp("Coordenadas Área geográfica_rgd.xls");
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void reset() {
        coordinatesWrappers = new ArrayList<CoordinatesWrapper>();
        tipoProyeccion = null;
        formatoVerificado = false;
        transformarFormato = false;
//        transformedFile = null;
        transformarFormatoalerta=true;
    }
	
	public void transformar() {
        Workbook workbook = null;
        Iterator<Row> rowIterator = null;
        try {
            workbook = new HSSFWorkbook(uploadedFile.getInputstream());
            Sheet sheet = workbook.getSheetAt(0);
            rowIterator = sheet.iterator();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (rowIterator != null)
            rowIterator.next();

        try {
            List<String> coordinates = new ArrayList<String>();
            for (CoordinatesWrapper cw : coordinatesWrappers) {
                coordinates.clear();
                for (CoordenadaRgdCoa coordenada : cw.getCoordenadas()) {
                    coordinates.add(coordenada.toString());
                }
                List<String> coordinatesTransformed = postgisFacade.transformarCoordenadas(coordinates, tipoProyeccion);
                for (int i = 0; i < coordinatesTransformed.size(); i++) {
                    String string = coordinatesTransformed.get(i);
                    String x = string.split(" ")[0];
                    String y = string.split(" ")[1];
                    cw.getCoordenadas().get(i).setX((new Double(x)));
                    cw.getCoordenadas().get(i).setY((new Double(y)));

                    if (rowIterator != null) {
                        Row row = rowIterator.next();
                        Iterator<Cell> cellIterator = row.cellIterator();
                        cellIterator.next().getNumericCellValue();
                        cellIterator.next().setCellValue(new Double(x));
                        cellIterator.next().setCellValue(new Integer(y));
                        cellIterator.next().getStringCellValue();
                    }
                }
            }
        } catch (NumberFormatException e) {
            JsfUtil.addMessageError("Las coordenadas no pueden ser transformadas desde el formato especificado.");
            coordinatesWrappers.clear();
        } catch (Exception e) {
            JsfUtil.addMessageError("Ha ocurrido un error al intentar transformar las coordenadas.");
            coordinatesWrappers.clear();
            LOGGER.error("Error en la trasnformacion de coordenadas", e);
            return;
        }

        if (workbook != null) {
            try {
                File tempFile = File.createTempFile(uploadedFile.getFileName(), ".xls");
                workbook.write(new FileOutputStream(tempFile));
//                transformedFile = documentosFacade.getBytesFromFile(tempFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        formatoVerificado = true;
    }
	
	public void solititarTransformacion() {
		transformarFormato = true;
	}
	
	
		public void handleFileUpload(final FileUploadEvent event) {
			
			formatoVerificado = false;
	        transformarFormato = false;
	        transformarFormatoalerta=true;
	//        transformedFile = null;
	
	        coordinatesWrappers = new ArrayList<CoordinatesWrapper>();
	        CoordinatesWrapper coordinatesWrapper = new CoordinatesWrapper();
	        coordinatesWrappers.add(coordinatesWrapper);
	//        boolean formatoCorrecto=true;
	        BigDecimal coorX = null;
            BigDecimal coorY = null;
	        
	        String coodenadas="";
	        int rows = 0;
	
	        String formaPoligono="poligono";
	        try{        	
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
	                rows++;
	            }
	
	            rowIterator = sheet.iterator();
	            if(rows != 2){
	    			JsfUtil.addMessageError("Estimado usuario las coordenadas ingresadas no corresponden a un punto de coordenadas.");
	    			coordinatesWrappers = new ArrayList<CoordinatesWrapper>();
	    			return;
	            }
	            if(rows < 4)
	            	formaPoligono="punto";
	            rows = 0;
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
	                    CoordenadaRgdCoa coordenada = new CoordenadaRgdCoa();
	                    coordenada.setOrden((int) cellIterator.next().getNumericCellValue());
	                    // coordenada.setX((double)cellIterator.next().getNumericCellValue());
	                    // coordenada.setY((double)cellIterator.next().getNumericCellValue());	                  
	                   
	                    coorX = new BigDecimal((double)cellIterator.next().getNumericCellValue()).setScale(5,5);
	                    coorY = new BigDecimal((double)cellIterator.next().getNumericCellValue()).setScale(5,5);
	                    coordenada.setX(coorX.doubleValue());
	                    coordenada.setY(coorY.doubleValue());
	                   
	                    String tipo = "";
	                    tipo = JsfUtil.getStringAsAnyPrimaryStrings(formaPoligono, getNombresTiposFormas());
	
	                    TipoForma tipoForma = getTipoForma(tipo);
	                    if (coordinatesWrapper.getTipoForma() == null)
	                        coordinatesWrapper.setTipoForma(tipoForma);
	                    else if (!coordinatesWrapper.getTipoForma().equals(tipoForma)
	                            || coordinatesWrapper.getTipoForma().getId().intValue() == TipoForma.TIPO_FORMA_PUNTO
	                            || coordenada.getOrden() == 1) {
	                        coordinatesWrapper = new CoordinatesWrapper(null, tipoForma);
	                        coordinatesWrappers.add(coordinatesWrapper);
	                    }
	                                        
	                    String zona = "17S";
	                    try {
	                        coordenada.setZona(zona);
	                    } catch (Exception e) {
	                        // TODO: handle exception
	                    }                    
	                    
	                    String descripcion = " ";
	                    try {
	                        descripcion = cellIterator.next().getStringCellValue();
	                        coordenada.setDescripcion(descripcion);
	                    } catch (Exception ex) {
	                    }
	                    
	                    if (coordenada.isDataComplete()){
	                        coordinatesWrapper.getCoordenadas().add(coordenada);
	                    }
	                    else {
	                        showErrorClearCoordenadas("La fila " + rows
	                                + " no cuenta con todos los datos necesarios. Por favor corrija.");
	                        coordinatesWrappers = new ArrayList<CoordinatesWrapper>();
	                        return;
	                    }	                    
	
	                }
	                rows++;
	                
	            }
	        
	        
	        
	            
	            varUbicacionArea= new HashMap<String,Double>();
	            
	            SVA_Reproyeccion_IntersecadoPortTypeProxy ws=new SVA_Reproyeccion_IntersecadoPortTypeProxy();
	            ws.setEndpoint(Constantes.getInterseccionesWS());
	            
	            for(int i=0;i<=coordinatesWrappers.size()-1;i++){
	        		boolean tipoPoligono = coordinatesWrappers.get(i).getCoordenadas().size() > 3;
	            	coodenadas="";
	                for (int j = 0; j <=coordinatesWrappers.get(i).getCoordenadas().size()-1; j++) {
	                	coodenadas += (coodenadas == "") ? BigDecimal.valueOf(coordinatesWrappers.get(i).getCoordenadas().get(j).getX()).setScale(5, RoundingMode.HALF_DOWN).toString()+" "+BigDecimal.valueOf(coordinatesWrappers.get(i).getCoordenadas().get(j).getY()).setScale(5, RoundingMode.HALF_DOWN).toString() : ","+BigDecimal.valueOf(coordinatesWrappers.get(i).getCoordenadas().get(j).getX()).setScale(5, RoundingMode.HALF_DOWN).toString()+" "+BigDecimal.valueOf(coordinatesWrappers.get(i).getCoordenadas().get(j).getY()).setScale(5, RoundingMode.HALF_DOWN).toString();
	                }
	                                                
	                Intersecado_entrada poligono = new Intersecado_entrada();//verifica que este bien el poligono
	                poligono.setU(Constantes.getUserWebServicesSnap());
	                poligono.setIsla(Constantes.SIGLAS_INSTITUCION + "-RA");
	                poligono.setTog(tipoPoligono?"po":"pu");
	                poligono.setXy(coodenadas);
	                poligono.setShp("dp");
	                Intersecado_resultado[]intRest;                
	                HashMap<String, Double>varUbicacion= new HashMap<String,Double>();
	                String parroquia="";
	                Double valorParroquia=0.0;
	                Integer orden=2;
	                try {
	                	intRest=ws.interseccion(poligono);
	                	if (intRest[0].getInformacion().getError() != null) {
	                		JsfUtil.addMessageError(intRest[0].getInformacion().getError().toString());
	                		coordinatesWrappers = new ArrayList<CoordinatesWrapper>();
	                	}
	                	else
	                	{
	                		boolean correcto=false;
	                		// si son coordenads de rcoa
	                		if(coordinatesWrappersPre != null && coordinatesWrappersPre.size()> 0){
		                		for (CoordendasPoligonos coordenadaRmpl : coordinatesWrappersPre) {
		                			coodenadasgeograficas="";
									for (CoordenadasProyecto coordenada : coordenadaRmpl.getCoordenadas()) {
										coodenadasgeograficas += (coodenadasgeograficas == "") ? coordenada.getX().setScale(5, RoundingMode.HALF_DOWN).toString()+" "+coordenada.getY().setScale(5, RoundingMode.HALF_DOWN).toString() : ","+coordenada.getX().setScale(5, RoundingMode.HALF_DOWN).toString()+" "+coordenada.getY().setScale(5, RoundingMode.HALF_DOWN).toString();
									}
									correcto = validarCoordenadas(TipoForma.TIPO_FORMA_POLIGONO, tipoPoligono, coodenadasgeograficas, coodenadas);
									if(correcto)
										break;
									
		                		}
	                		}else{
								if(proyectoSuia != null && proyectoSuia.getId() != null){
									for (FormaProyecto listaFormas : proyectoSuia.getFormasProyectos()) {
										if (listaFormas.getTipoForma().getId().equals(TipoForma.TIPO_FORMA_POLIGONO)|| listaFormas.getTipoForma().getId().equals(TipoForma.TIPO_FORMA_PUNTO)){
											coodenadasgeograficas="";
											for (Coordenada coordenada : listaFormas.getCoordenadas()) {
												coodenadasgeograficas += (coodenadasgeograficas == "") ? BigDecimal.valueOf(coordenada.getX()).setScale(5, RoundingMode.HALF_DOWN).toString()+" "+BigDecimal.valueOf(coordenada.getY()).setScale(5, RoundingMode.HALF_DOWN).toString() : ","+BigDecimal.valueOf(coordenada.getX()).setScale(5, RoundingMode.HALF_DOWN).toString()+" "+BigDecimal.valueOf(coordenada.getY()).setScale(5, RoundingMode.HALF_DOWN).toString();
											}
											if(!correcto)
												correcto = validarCoordenadas(listaFormas.getTipoForma().getId(), tipoPoligono, coodenadasgeograficas, coodenadas);
											if(correcto)
												break;
										}
									}
								}else{
									// si son coordenadas de digitalizacion
									Integer proyectoId = Integer.valueOf(((String)variables.get("idProyectoDigitalizacion") == null)?"0":(String)variables.get("idProyectoDigitalizacion"));
									//verifico si viene de un proyecto digitalizado
									if(proyectoId > 0 ){
										RegistroGeneradorDesechosRcoa registroGeneradorDesechos = registroGeneradorFacade.buscarRGDPorProyectoDigitalizado(proyectoId);
										List<RegistroGeneradorDesechosProyectosRcoa> lista = new ArrayList<RegistroGeneradorDesechosProyectosRcoa>();
										if(registroGeneradorDesechos != null && registroGeneradorDesechos.getId() != null){
											lista = registroGeneradorDesechosProyectosRcoaFacade.buscarPorRegistroGenerador(registroGeneradorDesechos.getId());
										}
										if(lista != null && lista.size() > 0){
											// si es de digitalizacion ya no busco el proyecto aosciado
											//busco coordenadas cargadas automaticamente
											List<ShapeDigitalizacion> objShape = shapeDigitalizacionFacade.obtenerShapePorSistema(lista.get(0).getProyectoDigitalizado().getId(), 1, true);
											// si no existe coordenadas cargadas automaticamente busco coordenadas ingresadas manualmente
											if(objShape == null || objShape.size() == 0){
												objShape = shapeDigitalizacionFacade.obtenerShapePorSistema(lista.get(0).getProyectoDigitalizado().getId(), 2, true);
											}
											if(objShape != null && objShape.size() > 0){
												for (ShapeDigitalizacion shapeAux : objShape) {
													if(shapeAux.getTipoForma() != null)
														tipoformaId = shapeAux.getTipoForma().getId();
													List<CoordenadaDigitalizacion> coordenadas = coordenadaDigitalizacionFacade.obtenerCoordenadas(shapeAux.getId());
													coodenadasgeograficas="";
													if(coordenadas.size() == 1)
														tipoformaId = TipoForma.TIPO_FORMA_PUNTO;
													for (CoordenadaDigitalizacion coordenada : coordenadas) {
														coodenadasgeograficas += (coodenadasgeograficas == "") ? coordenada.getX().setScale(5, RoundingMode.HALF_DOWN).toString()+" "+coordenada.getY().setScale(5, RoundingMode.HALF_DOWN).toString() : ","+coordenada.getX().setScale(5, RoundingMode.HALF_DOWN).toString()+" "+coordenada.getY().setScale(5, RoundingMode.HALF_DOWN).toString();
													}
													correcto = validarCoordenadas(tipoformaId, tipoPoligono, coodenadasgeograficas, coodenadas);
													if(correcto)
														break;
												}
											}
										}
									}
								}
			                }
	            			if (!correcto)
	            			{
	            				if((proyectoSuia != null && proyectoSuia.getId() != null) || proyecto == null || proyecto.getId() == null)
	            					JsfUtil.addMessageError("Estimado usuario las coordenadas ingresadas de los puntos de generación deben encontrarse dentro del área de geográfica del proyecto, misma que fue ingresada en el registro del proyecto o proceso de digitalización");
	            				else
	            					JsfUtil.addMessageError("Estimado usuario las coordenadas ingresadas de los puntos de generación deben encontrarse dentro del área de implantación del proyecto, misma que fue ingresada en el registro preliminar");
	            				coordinatesWrappers = new ArrayList<CoordinatesWrapper>();
	            			}
	            			else
	            			{
	            				boolean coordenadasRepetidas = false;
	            				for(PuntoRecuperacionRgdRcoa punto : JsfUtil.getBean(PuntosRecuperacionRgdBean.class).getPuntosRecuperacion()){
	            					
	            					if(!punto.getId().equals(JsfUtil.getBean(PuntosRecuperacionRgdBean.class).getPuntoRecuperacion().getId())){
	            						String[] cantidad = punto.getCoordenadasIngresadas().split(",");
	            						if(cantidad.length < 5){
	                            			if (coodenadas.equals(punto.getCoordenadasIngresadas())){
	                            				JsfUtil.addMessageError("Las coordenadas ingresadas están repetidas");
	                            				coordinatesWrappers = new ArrayList<CoordinatesWrapper>();
	                            				coordenadasRepetidas = true;
	                            				break;
	                            			}
	            						}
	            						if(tipoPoligono){
	                						ContieneZona_entrada verificarRepeticion = new ContieneZona_entrada(); //verifica que el poligono este contenida dentro de la ubicación geográfica
	                    					verificarRepeticion.setU(Constantes.getUserWebServicesSnap());
	                    					verificarRepeticion.setXy1(punto.getCoordenadasIngresadas());
	                    					verificarRepeticion.setXy2(coodenadas);
	                                		ContieneZona_resultado[]intRestGeoImpl_;
	                                		intRestGeoImpl_=ws.contieneZona(verificarRepeticion);
	                                		if (intRestGeoImpl_[0].getInformacion().getError() != null) {
	                                			JsfUtil.addMessageError(intRestGeoImpl_[0].getInformacion().getError().toString());
	                                			coordinatesWrappers = new ArrayList<CoordinatesWrapper>();
	                                		}
	                                		else
	                                		{
	                                			if (intRestGeoImpl_[0].getContieneCapa().getValor().equals("f")){
	                                				
	                                			}else{
	                                				JsfUtil.addMessageError("Las coordenadas ingresadas están repetidas"); 
	                                				coordinatesWrappers = new ArrayList<CoordinatesWrapper>();
	                                				coordenadasRepetidas = true;
	                                				break;
	                                			}
	                                		}
	            						}else{
	                            			if (coodenadas.equals(punto.getCoordenadasIngresadas())){
	                            				JsfUtil.addMessageError("Las coordenadas ingresadas están repetidas");
	                            				coordinatesWrappers = new ArrayList<CoordinatesWrapper>();
	                            				coordenadasRepetidas = true;
	                            				break;
	                            			}
	            						}
	            					}
	            				}
	            				//validacon coordenadas repetidas
	            				if(!coordenadasRepetidas){
	            					//carga las parroquias ----------------------------------
	                				for (Intersecado_capa intersecado_capa : intRest[0].getCapa()) {
	                					String capaNombre=intersecado_capa.getNombre();
	                					if(intersecado_capa.getError()!=null){
	                						JsfUtil.addMessageError(intersecado_capa.getError().toString());
	                						coordinatesWrappers = new ArrayList<CoordinatesWrapper>();
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
	                									varUbicacionArea.put(parroquia,valorParroquia);                							                								
	                									if (varUbicacion.get(parroquia) != null) {
	                										if (valorParroquia >= varUbicacion.get(parroquia)) {
	                											varUbicacion.put(parroquia,orden.doubleValue());
	                										}
	                									} else {
	                										varUbicacion.put(parroquia,orden.doubleValue());
	                									}
	                								}
	                							}
	                							orden ++;
	                						}
	                					}
	                				}
	                				cargarUbicacionProyecto(varUbicacion);
	
	                				//Capas de intersección----------------------------------------------------------
	                				ContieneCapa_entrada capaImpla = new ContieneCapa_entrada();
		                			capaImpla.setU(Constantes.getUserWebServicesSnap());
		                			capaImpla.setXy(coodenadas);
		                			InterseccionCapa_resultado[]intRestCapaImpl;
		                			intRestCapaImpl=ws.interseccionCapa(capaImpla);
		                			
		                			InterseccionProyectoLicenciaAmbiental capaInterseccion = new InterseccionProyectoLicenciaAmbiental();
		                			List<DetalleInterseccionProyectoAmbiental> listaIntersecciones = new ArrayList<DetalleInterseccionProyectoAmbiental>();
		                			capasIntersecciones= new HashMap<InterseccionProyectoLicenciaAmbiental, List<DetalleInterseccionProyectoAmbiental>>();
		                			if(intRestCapaImpl[0].getCapa() != null){
	    	                			for (IntersecadoCapa_capa capas : intRestCapaImpl[0].getCapa()) {
	    	                				if(Integer.valueOf(capas.getNum())>0)
	    	                				{
	    	                					capaInterseccion = new InterseccionProyectoLicenciaAmbiental();
	    	                					capaInterseccion.setCapa(getCapa(capas.getNombre()));
	                    						capaInterseccion.setProyectoLicenciaCoa(proyecto);
	                    						capaInterseccion.setDescripcionCapa(capas.getNombre());
	                    						DetalleInterseccionProyectoAmbiental detallecapa = new DetalleInterseccionProyectoAmbiental();
	                    						listaIntersecciones = new ArrayList<DetalleInterseccionProyectoAmbiental>();
	                    						for(Campos_coordenada interseccion : capas.getCampos())
	                    						{
	                    							detallecapa = new DetalleInterseccionProyectoAmbiental();
	                    							if(interseccion.getTphmd()!=null)
	                    							{
	                    								detallecapa.setNombreGeometria(interseccion.getTphmd());
	                    								detallecapa.setIdGeometria(Integer.valueOf(interseccion.getGid()));
	                    							}
	                    							if(interseccion.getTpcnv()!=null)
	                    							{
	                    								detallecapa.setNombreGeometria(interseccion.getTpcnv());
	                    								detallecapa.setIdGeometria(Integer.valueOf(interseccion.getGid()));
	                    							}
	                    							if(interseccion.getNam()!=null)
	                    							{
	                    								detallecapa.setNombreGeometria(interseccion.getNam());
	                    								detallecapa.setIdGeometria(Integer.valueOf(interseccion.getGid()));
	                    							}
	                    							if(interseccion.getNameco()!=null)
	                    							{
	                    								detallecapa.setNombreGeometria(interseccion.getNameco());
	                    								detallecapa.setIdGeometria(Integer.valueOf(interseccion.getGid()));
	                    							}
	                    							if(interseccion.getCtn2()!=null)
	                    							{
	                    								detallecapa.setNombreGeometria(interseccion.getCtn2());
	                    								detallecapa.setIdGeometria(Integer.valueOf(interseccion.getGid()));
	                    							}
	                    							listaIntersecciones.add(detallecapa);
	                    						}
	                    						capasIntersecciones.put(capaInterseccion, listaIntersecciones);
	                    						
	                    						coordenadasPunto = coodenadas;
	    	                				}
	    	                			}
		                			}
	            				}
	            			}
	                	}
	
	                }
	                catch (RemoteException e) {
	                	JsfUtil.addMessageError("Error insesperado, comuníquese con mesa de ayuda");
	                	System.out.println("Servicio no disponible ----> "+Constantes.getInterseccionesWS());
	                	coordinatesWrappers = new ArrayList<CoordinatesWrapper>();
	                }
	            }
	            
	            if(coordinatesWrappers.size()>0)
	            {
	            	ubicacionMasArea();
	            }
	            
	        }catch(Exception ex){
	        	ex.printStackTrace();	        	
                if(coorX ==null ||  coorY==null)
                {
                	JsfUtil.addMessageError("Error insesperado, verifique el formato del archivo que este de acuerdo a la plantilla proporcionada.");
                	coordinatesWrappers = new ArrayList<CoordinatesWrapper>();
                }
	        	
	        	//coordinatesWrappers = new ArrayList<CoordinatesWrapper>();
	        }
		}
	
	/**
	 * valida si las coordenadas ingresads estan dentro del area de implantacion
	 * @param tipoFormaPoligono forma del poligono contenedor
	 * @param poligonoTipo forma del poligono contenido
	 * @param coordenadasArea  coordenadas del poligono contenedor (area geografica o implantacion)
	 * @param coordenadasingresadas
	 * @return
	 */
	public boolean validarCoordenadas(Integer tipoFormaPoligono, boolean poligonoTipo, String coordenadasArea, String coordenadasingresadas){
		try{
			boolean estaDentro = false;
	        SVA_Reproyeccion_IntersecadoPortTypeProxy ws=new SVA_Reproyeccion_IntersecadoPortTypeProxy();
	        ws.setEndpoint(Constantes.getInterseccionesWS());
			if(tipoFormaPoligono.equals(TipoForma.TIPO_FORMA_POLIGONO)){
				if(poligonoTipo){
					ContieneZona_entrada verificarGeoImpla = new ContieneZona_entrada(); //verifica que el poligono este contenida dentro de la ubicación geográfica
	        		verificarGeoImpla.setU(Constantes.getUserWebServicesSnap());
	        		verificarGeoImpla.setXy1(coordenadasArea);
	        		verificarGeoImpla.setXy2(coordenadasingresadas);
	        		ContieneZona_resultado[]intRestGeoImpl;
	        		intRestGeoImpl=ws.contieneZona(verificarGeoImpla);
	        		if (intRestGeoImpl[0].getInformacion().getError() != null) {
	        			
	           		}else if (intRestGeoImpl[0].getContieneCapa().getValor().equals("t")){
	           			estaDentro=true;
	           		}
				}else{
					ContienePoligono_entrada verificarGeoImpla = new ContienePoligono_entrada(); //verifica que el punto este contenida dentro de la ubicación geográfica o de implantacion
	        		verificarGeoImpla.setU(Constantes.getUserWebServicesSnap());
	        		verificarGeoImpla.setTipo("pu");
	        		verificarGeoImpla.setXy1(coordenadasArea);
	        		verificarGeoImpla.setXy2(coordenadasingresadas);
	        		ContienePoligono_resultado[]intRestGeoImpl;
	        		intRestGeoImpl=ws.contienePoligono(verificarGeoImpla);
	        		if (intRestGeoImpl[0].getInformacion().getError() != null) {
	           		}else if (intRestGeoImpl[0].getContienePoligono().getValor().equals("t")){
	           			estaDentro=true;
	              	}
				}
			}else{
    			if (coordenadasArea.equals(coordenadasingresadas)){
    				estaDentro = true;
    			}else if (coordenadasArea.contains(coordenadasingresadas)){
    				estaDentro = true;
    			}
			}
			return estaDentro;
		}catch(RemoteException e){
			return false;
		}
	}
	
	public CapasCoa getCapa(String capa) {
        for (CapasCoa c : capas) {
            if (c.getNombre().equals(capa))
                return c;
        }
        return null;
    }
	
	public void cargarUbicacionProyecto(HashMap<String, Double> parroquia){
		ubicacionesSeleccionadas= new ArrayList<UbicacionesGeografica>();
		parroquiaSeleccionadas= new HashMap<String, UbicacionesGeografica>();

		Iterator it = parroquia.entrySet().iterator();
		Integer orden=0;
		String inec="";
		while (it.hasNext()) {
			Map.Entry<String, Double> e = (Entry<String, Double>) it.next();
			inec  = e.getKey();
			orden = e.getValue().intValue();
			if (!inec.equals("")) {
				Map<String, Object> parametros = new HashMap<String, Object>();
				parametros.put("codeInec", inec);
				List<UbicacionesGeografica> lista = crudServiceBean.findByNamedQueryGeneric(UbicacionesGeografica.FIND_PARROQUIA,parametros);
				ubicacionesSeleccionadas.addAll(lista);
				JsfUtil.getBean(AdicionarUbicacionesRcoaBean.class).setUbicacionesSeleccionadas(ubicacionesSeleccionadas);
			}
		}

	}
	
	private TipoForma getTipoForma(String nombre) {
        for (TipoForma tipoForma : getTiposFormas()) {
            if (JsfUtil.comparePrimaryStrings(tipoForma.getNombre(), nombre))
                return tipoForma;
        }
        return null;
    }
	
	private void showErrorClearCoordenadas(String message) {
        JsfUtil.addMessageError(message);
        coordinatesWrappers.clear();
    }
	
	public List<TipoProyeccion> cargarTiposProyecciones(String param) {
        if (param == null || param.isEmpty())
            return tiposProyecciones;
        else {
            if (tiposProyeccionesFiltradas == null) {
                tiposProyeccionesFiltradas = new ArrayList<>();
                String[] tipos = param.split(";");
                for (TipoProyeccion tipoProyeccion : tiposProyecciones) {
                    for (String string : tipos) {
                        if (tipoProyeccion.getNombre().toLowerCase().startsWith(string.trim().toLowerCase())) {
                            tiposProyeccionesFiltradas.add(tipoProyeccion);
                            break;
                        }
                    }
                }
            }
            return tiposProyeccionesFiltradas;
        }
    }
	
	public StreamedContent getPlantillaCoordenadas() throws Exception {
		
		
		try {
            plantillaCoordenadas = documentosFacade.descargarDocumentoPorNombre(Constantes.PLANTILLA_COORDENADAS_REGISTRO_GENERADOR);
        	if(tipoPermisoRGD != null && tipoPermisoRGD.equals(Constantes.TIPO_RGD_AAA)){
	            plantillaCoordenadas = documentosFacade.descargarDocumentoPorNombre(Constantes.PLANTILLA_COORDENADAS_REGISTRO_GENERADOR_AAA);
        	
        	}
        } catch (Exception e) {
        }
		
        
        try {
            if (plantillaCoordenadas != null) {
            	
            	DefaultStreamedContent content = null;
                content = new DefaultStreamedContent(new ByteArrayInputStream(plantillaCoordenadas));
                content.setName(Constantes.PLANTILLA_COORDENADAS);
                deleteFileTmp("Coordenadas Área geográfica_rgd.xls");
                return content;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
        
        return null;
        
    }
	
	public static void deleteFileTmp(String directoryTmp) {
        try {
            File directory = new File(System.getProperty("java.io.tmpdir") + "/"+directoryTmp);
            File files = directory;
            files.delete();
        } catch (Exception e) {
         //    info(e.getMessage());
        }
    }
	

    public StreamedContent getAyudaCoordenadas() throws Exception {
      
        try {
            if (ayudaCoordenadas != null) {
            	  DefaultStreamedContent content = null;
                content = new DefaultStreamedContent(new ByteArrayInputStream(ayudaCoordenadas));
                content.setName(Constantes.AYUDA_COORDENADAS);
                deleteFileTmp(content.getName());
                return content;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
     
    }
    
    public void solititarTransformacionalerta() {
        transformarFormatoalerta = false;
    }
    
    public void ubicacionMasArea()
	{
		Iterator it = varUbicacionArea.entrySet().iterator();
		double orden=0;
		String inec="";
		double maxArea = 0;
		String inecMayor = "";
		while (it.hasNext()) {
			Map.Entry<String, Double> e = (Entry<String, Double>) it.next();
			inec  = e.getKey();
			orden = e.getValue().doubleValue();
			if (orden >= maxArea) {
				maxArea = orden;
				inecMayor = inec;
			}
		}
		
		if(inecMayor == null || inecMayor.isEmpty())
			return;
		JsfUtil.getBean(PuntosRecuperacionRgdBean.class).setParroquia(ubicacionfacade.buscarUbicacionPorCodigoInec(inecMayor));
		JsfUtil.getBean(AdicionarUbicacionesRcoaBean.class).setUbicacionSeleccionada(ubicacionfacade.buscarUbicacionPorCodigoInec(inecMayor), 3);
		
	}

	public void guardarCoorGeografica(RegistroGeneradorDesechosRcoa registro)
	{
		if(uploadedFile!=null)
		{
			DocumentosRgdRcoa documento = new DocumentosRgdRcoa();
			documento.setContenidoDocumento(uploadedFile.getContents());
			documento.setExtesion(".xls");
			documento.setMime("application/vnd.ms-excel");
			documento.setIdTable(registro.getId());
			documento.setNombreTabla(RegistroGeneradorDesechosRcoa.class.getSimpleName());
			documento.setNombre("Coordenadas área geográfica.xls");
			documento.setRegistroGeneradorDesechosRcoa(registro);

			try {
				documentosRgdRcoaFacade.guardarDocumentoAlfrescoSinProyecto(registro.getCodigo(), "Coordenadas_Geográficas", 1L, documento, TipoDocumentoSistema.RCOA_COORDENADA_GEOGRAFICA);
			} catch (ServiceException | CmisAlfrescoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
