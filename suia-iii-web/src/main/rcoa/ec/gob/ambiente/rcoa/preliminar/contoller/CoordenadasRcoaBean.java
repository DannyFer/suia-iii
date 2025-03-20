package ec.gob.ambiente.rcoa.preliminar.contoller;

import index.Campos_coordenada;
import index.Concesion_resultado;
import index.Concesiones_entrada;
import index.ContieneCapaRCOA_entrada;
import index.ContieneCapa_entrada;
import index.ContieneZona_entrada;
import index.ContieneZona_resultado;
import index.IntersecadoCapa_capa;
import index.Intersecado_capa;
import index.Intersecado_coordenada;
import index.Intersecado_entrada;
import index.Intersecado_resultado;
import index.InterseccionCapa_resultado;
import index.SVA_Reproyeccion_IntersecadoPortTypeProxy;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
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
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.client.SuiaServicesArcon;
import ec.gob.ambiente.client.SuiaServices_Service_Arcon;
import ec.gob.ambiente.prevencion.registro.proyecto.bean.RegistroProyectoBean;
import ec.gob.ambiente.rcoa.facade.CapasCoaFacade;
import ec.gob.ambiente.rcoa.facade.CatalogoCoaFacade;
import ec.gob.ambiente.rcoa.model.CapasCoa;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.model.CoordenadasProyecto;
import ec.gob.ambiente.rcoa.model.DetalleInterseccionProyectoAmbiental;
import ec.gob.ambiente.rcoa.model.InterseccionProyectoLicenciaAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaAmbientalConcesionesMineras;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.util.CoordendasPoligonos;
import ec.gob.ambiente.rcoa.util.MayorAreaVO;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ConcesionMinera;
import ec.gob.ambiente.suia.domain.TipoForma;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.tipoforma.facade.TipoFormaFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.arcom.dm.ws.Coordenadas;
import ec.gob.arcom.dm.ws.DerechoMineroMAEDTO;

@ManagedBean
@ViewScoped
public class CoordenadasRcoaBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(CoordenadasRcoaBean.class);
	
	@EJB
	private TipoFormaFacade tipoFormaFacade;
	@EJB
	private UbicacionGeograficaFacade ubicacionfacade;
	@EJB
	private CapasCoaFacade capasCoaFacade;
	@EJB
    private CrudServiceBean crudServiceBean;
	@EJB
	private CatalogoCoaFacade catalogoCoaFacade;
	
	@Getter
	@Setter
    private List<CoordenadasProyecto> coordenadasGeograficas = new ArrayList<CoordenadasProyecto>();
	@Getter
    @Setter
    private List<CoordendasPoligonos> coordinatesWrappers = new ArrayList<CoordendasPoligonos>();
	@Getter
    @Setter
	private List<CoordendasPoligonos> coordinatesWrappersGeo = new ArrayList<CoordendasPoligonos>();
	@Getter
	@Setter
	private List<UbicacionesGeografica> ubicacionesSeleccionadas= new ArrayList<UbicacionesGeografica>();
	
	private List<CapasCoa> capas = new ArrayList<CapasCoa>();
	@Getter
	@Setter
	private List<CapasCoa> capasSoapInterseccion = new ArrayList<CapasCoa>();
	
	private HashMap<String, Double> varUbicacionArea= new HashMap<String,Double>();
	private HashMap<String, Double> varUbicacionAux = new HashMap<String,Double>();
	
	@Getter
	@Setter
	private HashMap<InterseccionProyectoLicenciaAmbiental, List<DetalleInterseccionProyectoAmbiental>> capasIntersecciones = new HashMap<InterseccionProyectoLicenciaAmbiental, List<DetalleInterseccionProyectoAmbiental>>();
	@Getter
	@Setter
	private HashMap<InterseccionProyectoLicenciaAmbiental, List<DetalleInterseccionProyectoAmbiental>> listaCapasInterseccionPrincipal = new HashMap<InterseccionProyectoLicenciaAmbiental, List<DetalleInterseccionProyectoAmbiental>>();

	@Getter
	@Setter
	private List<String> listaNombresCapasInterseca = new ArrayList<>();
	
	@Getter
	@Setter
	private List<Integer> wolframSocial= new ArrayList<Integer>();
	@Getter
	@Setter
    private List<Integer> wolframBiofisica= new ArrayList<Integer>();
    
    @Getter
	@Setter
	private TipoForma poligono;
    @Getter
	@Setter
	private UbicacionesGeografica ubicacionOficinaTecnica = new UbicacionesGeografica();
	@Getter
	@Setter
	private UbicacionesGeografica ubicacionPrincipal = new UbicacionesGeografica();
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto= new ProyectoLicenciaCoa();
	@Getter
	@Setter
	private MayorAreaVO area = new MayorAreaVO();
	
	@Getter
    private UploadedFile uploadedFileGeo;
	@Getter
    private UploadedFile uploadedFileImpl;
	@Getter
	@Setter
	private String coodenadasgeograficas="";
	@Getter
	@Setter
	private List<String> coordenadasImplantacion = new ArrayList<>();
	@Getter
	@Setter
	private BigDecimal superficieProyecto, superficieMetrosCuadrados;
	private List<TipoForma> tiposFormas;
	
	@Getter
    @Setter
	private double superficie=0.0;
	@Getter
    @Setter
	private double superficieMetros=0.0;
	
	@Getter
    @Setter
	private double superficieGeograficaMetros, superficieGeograficaHa;
	@Getter
	@Setter
	private boolean estadoFrontera, estadoZonaIntangible, estadoZonaIntangibleAux, esZonaSnapEspecial;
	@Getter
	@Setter
	private Boolean mostrarDetalleInterseccion,interseca=false;
	@Getter
	@Setter
	private String zonasInterseccionDetalle="";
	@Getter
    @Setter
	private Integer valorCapacidadSocial, valorCapacidadBiofisica;	
	@Getter
    @Setter
	private Boolean intersecaConCamaronera;
	@Getter
    @Setter
	private String map, nombreArchivoCoordenadas;
	@Getter
    @Setter
	private String nam;
	@Getter
    @Setter
	private Integer porcentajeInterseccionCamaroneras, progresoInterseccionCapas, totalPesoCapas;
	@Getter
    @Setter
	private Boolean intersecaBiosfera=false;
	@Getter
	@Setter
	private Boolean poligonosContiguos = false;
	@Getter
	@Setter
	private Boolean intersecaSnapChocoAndino = false;
	@Getter
	@Setter
	private List<String> listaCapas;	
	@Getter
	@Setter
	private Double totalPorcentajeProcesado;
	
	@Getter
	@Setter
	private ProyectoLicenciaAmbientalConcesionesMineras concesionMinera = new ProyectoLicenciaAmbientalConcesionesMineras();
	
	@PostConstruct 
    public void init() {
		capas = capasCoaFacade.listaCapas();
		capasSoapInterseccion = capasCoaFacade.listaCapasSoapInterseccion();
		
		tiposFormas = tipoFormaFacade.listarTiposForma();
		
		poligono = getTipoForma(TipoForma.TIPO_FORMA_POLIGONO);
		
		limpiarCampos();
		
		CatalogoGeneralCoa catalogo = catalogoCoaFacade.obtenerCatalogoPorCodigo("porcentaje.interseccion.camaronera");
		porcentajeInterseccionCamaroneras = Integer.valueOf(catalogo.getValor());
	}
	
	public void limpiarCampos() {
		valorCapacidadSocial = 0;
		valorCapacidadBiofisica = 0;
		
		ubicacionesSeleccionadas= new ArrayList<UbicacionesGeografica>();
        superficieProyecto = BigDecimal.ZERO;
		superficieMetrosCuadrados = BigDecimal.ZERO;
    	coordinatesWrappers = new ArrayList<CoordendasPoligonos>();
    	coordenadasGeograficas = new ArrayList<CoordenadasProyecto>();
		coordinatesWrappersGeo = new ArrayList<CoordendasPoligonos>();
		mostrarDetalleInterseccion = false;
    	superficie=0.0;
		superficieMetros = 0.0;
		zonasInterseccionDetalle="";
		ubicacionesSeleccionadas= new ArrayList<UbicacionesGeografica>();
		
		estadoFrontera = false;
		estadoZonaIntangible = false;
		estadoZonaIntangibleAux=false;
		esZonaSnapEspecial = false;
	}
	
	public TipoForma getTipoForma(Integer id) {
        for (TipoForma tf : tiposFormas) {
            if (tf.getId()==id)
                return tf;
        }
        return null;
    }
	
	public void handleFileUpload(final FileUploadEvent event) {   
		coodenadasgeograficas="";
		Integer poligonoAnterior = 0;
		Integer nroOrden = 1;
        int rows = 0;
        List<String> listCoodenadasgeograficas = new ArrayList<String>();
        coordenadasGeograficas = new ArrayList<CoordenadasProyecto>();
        List<CoordenadasProyecto> coordenadasGeograficasAux  = new ArrayList<CoordenadasProyecto>();
        coordinatesWrappersGeo = new ArrayList<CoordendasPoligonos>();
        
        coordinatesWrappers = new ArrayList<CoordendasPoligonos>();
        ubicacionesSeleccionadas= new ArrayList<UbicacionesGeografica>();
        superficieProyecto = BigDecimal.ZERO;
		superficieMetrosCuadrados = BigDecimal.ZERO;
		mostrarDetalleInterseccion = false;
		superficieGeograficaMetros = 0.0;
		superficieGeograficaHa = 0.0;
		varUbicacionArea = new HashMap<String,Double>();
		varUbicacionAux = new HashMap<String,Double>();
		
		nombreArchivoCoordenadas = null;
		intersecaConCamaronera = null;
		map = null;
		nam = null;
		
        try {
            uploadedFileGeo = event.getFile();
            Workbook workbook = new HSSFWorkbook(uploadedFileGeo.getInputstream());
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
                    CoordenadasProyecto coordenada = new CoordenadasProyecto();
                    Integer poligonoActual = (int) cellIterator.next().getNumericCellValue();
                    coordenada.setAreaGeografica(poligonoActual);
                    coordenada.setOrdenCoordenada((int) cellIterator.next().getNumericCellValue());
                    coordenada.setX(new BigDecimal(Double.valueOf(cellIterator.next().toString().replace("," , "."))).setScale(6, RoundingMode.HALF_DOWN));
                    coordenada.setY(new BigDecimal(Double.valueOf(cellIterator.next().toString().replace("," , "."))).setScale(6, RoundingMode.HALF_DOWN));
                    coordenada.setTipoCoordenada(2);
                    coordenadasGeograficas.add(coordenada);
                    // coodenadasgeograficas += (coodenadasgeograficas == "") ? coordenada.getX()+" "+coordenada.getY() : ","+coordenada.getX()+" "+coordenada.getY();
                    if (poligonoAnterior == 0) {
                    	poligonoAnterior = poligonoActual;
					}else if (poligonoAnterior != poligonoActual) {
                    	nroOrden = 1;
                    }
                    
                    if(!nroOrden.equals(coordenada.getOrdenCoordenada())) {
                    	//para validar el secuencial del nro de orden de las coordenadas
                    	JsfUtil.addMessageError("Error procesando el excel de coordenadas, verifique que la información se encuentre correcta");
                    	System.out.println("Error en la columna shape. Poligono: " + poligonoActual + ", shape: " + nroOrden);
                		coordenadasGeograficas = new ArrayList<CoordenadasProyecto>();
                		coordinatesWrappersGeo = new ArrayList<CoordendasPoligonos>();
                		return;
                    }
                    
                    if (poligonoAnterior == poligonoActual) {
                    	coodenadasgeograficas += (coodenadasgeograficas == "") ? coordenada.getX()+" "+coordenada.getY() : ","+coordenada.getX()+" "+coordenada.getY();
                    	if (sheet.getLastRowNum() == rows) {
                    		listCoodenadasgeograficas.add(coodenadasgeograficas);
    						
    						coordenadasGeograficasAux.add(coordenada);
    						CoordendasPoligonos coordinatesWrapper = new CoordendasPoligonos(null, poligono);
    		                coordinatesWrapper.getCoordenadas().addAll(coordenadasGeograficasAux);
    		                coordinatesWrapper.setCadenaCoordenadas(coodenadasgeograficas);
    		                coordinatesWrappersGeo.add(coordinatesWrapper);
    		                
    		                coodenadasgeograficas = "";
    		                coordenadasGeograficasAux = new ArrayList<>();
						}
                    	nroOrden++;
					} else {
						listCoodenadasgeograficas.add(coodenadasgeograficas);
						CoordendasPoligonos coordinatesWrapper = new CoordendasPoligonos(null, poligono);
                        coordinatesWrapper.getCoordenadas().addAll(coordenadasGeograficasAux);
                        coordinatesWrapper.setCadenaCoordenadas(coodenadasgeograficas);
                        coordinatesWrappersGeo.add(coordinatesWrapper);
                        coordenadasGeograficasAux = new ArrayList<>();
                        
						coodenadasgeograficas = "";
						coodenadasgeograficas += (coodenadasgeograficas == "") ? coordenada.getX()+" "+coordenada.getY() : ","+coordenada.getX()+" "+coordenada.getY();
						poligonoAnterior = poligonoActual;
						
						nroOrden++;
					}
                    
                    coordenadasGeograficasAux.add(coordenada);
                }
                rows++;
            }
            if(coodenadasgeograficas != "") {
            	listCoodenadasgeograficas.add(coodenadasgeograficas);
            	
            	CoordendasPoligonos coordinatesWrapper = new CoordendasPoligonos(null, poligono);
                coordinatesWrapper.getCoordenadas().addAll(coordenadasGeograficasAux);
                coordinatesWrapper.setCadenaCoordenadas(coodenadasgeograficas);
                coordinatesWrappersGeo.add(coordinatesWrapper);
                coordenadasGeograficasAux = new ArrayList<>();
            }
            
            for(CoordendasPoligonos item : coordinatesWrappersGeo) {
            	String coodenadasgeograficas = item.getCadenaCoordenadas();
            	
            	Intersecado_entrada poligono = new Intersecado_entrada();
                poligono.setU(Constantes.getUserWebServicesSnap());
                poligono.setIsla(Constantes.SIGLAS_INSTITUCION + "-RA");
                poligono.setTog("po");
                poligono.setXy(coodenadasgeograficas);
                poligono.setShp("dp");
                SVA_Reproyeccion_IntersecadoPortTypeProxy ws=new SVA_Reproyeccion_IntersecadoPortTypeProxy();
                ws.setEndpoint(Constantes.getInterseccionesWS());
                Intersecado_resultado[]intRest;
                try {
                	intRest=ws.interseccion(poligono);
                	if (intRest[0].getInformacion().getError() != null) {            		
                		JsfUtil.addMessageError(intRest[0].getInformacion().getError().toString());
                		System.out.println("Servicio no disponible ----> "+Constantes.getInterseccionesWS());
                		coordenadasGeograficas = new ArrayList<CoordenadasProyecto>();
                		coordinatesWrappersGeo = new ArrayList<CoordendasPoligonos>();
                		return;
                	}            	
                	if (intRest[0].getCapa()[0].getError() != null) {            		
                		JsfUtil.addMessageError(intRest[0].getCapa()[0].getError().toString());
                		System.out.println("Servicio no disponible ----> "+Constantes.getInterseccionesWS());
                		coordenadasGeograficas = new ArrayList<CoordenadasProyecto>();
                		coordinatesWrappersGeo = new ArrayList<CoordendasPoligonos>();
                		return;
                	}
                	
                	Double areaPoligonoMetros = recuperarAreaPoligono(coodenadasgeograficas, coodenadasgeograficas);
                	if(areaPoligonoMetros == null) {
                		JsfUtil.addMessageError("Error a recuperar la ubicación del proyecto");
                		coordenadasGeograficas = new ArrayList<CoordenadasProyecto>();
                		coordinatesWrappersGeo = new ArrayList<CoordendasPoligonos>();
                		return;
                	}
                	
                	Double areaPoligono = areaPoligonoMetros/10000;
                	superficieGeograficaMetros += areaPoligonoMetros;
                	superficieGeograficaHa += areaPoligono;
                	
                	item.setSuperficie(new BigDecimal(areaPoligono));
                	recuperarParroquiasInterseccion(areaPoligono, intRest);
                	cargarUbicacionProyecto(varUbicacionAux);
                	ubicacionMasArea();
                	
                	nombreArchivoCoordenadas = event.getFile().getFileName();
                	
                }
                catch (RemoteException e) {
                	e.printStackTrace();
                	JsfUtil.addMessageError("Error inesperado, comuníquese con mesa de ayuda");
                	coordenadasGeograficas = new ArrayList<CoordenadasProyecto>();
                	coordinatesWrappersGeo = new ArrayList<CoordendasPoligonos>();
                	coordinatesWrappers = new ArrayList<CoordendasPoligonos>();
                	superficieProyecto = BigDecimal.ZERO;
            		superficieMetrosCuadrados = BigDecimal.ZERO;
                }
			}
            
        } catch (Exception e) {
        	JsfUtil.addMessageError("Error inesperado, comuníquese con mesa de ayuda");
            LOGGER.error("Error procesando el excel de coordenadas", e);
            coordinatesWrappers = new ArrayList<CoordendasPoligonos>();
            ubicacionesSeleccionadas= new ArrayList<UbicacionesGeografica>();
            superficieProyecto = BigDecimal.ZERO;
    		superficieMetrosCuadrados = BigDecimal.ZERO;
    		coordenadasGeograficas = new ArrayList<CoordenadasProyecto>();
    		coordinatesWrappersGeo = new ArrayList<CoordendasPoligonos>();
    		mostrarDetalleInterseccion = false;
    		estadoFrontera=false;
        }
    }
	
	public void procesarIntersecciones() {
		try {
			/***********************************************************************************************/
	        capasIntersecciones= new HashMap<InterseccionProyectoLicenciaAmbiental, List<DetalleInterseccionProyectoAmbiental>>();
			listaCapasInterseccionPrincipal = new HashMap<InterseccionProyectoLicenciaAmbiental, List<DetalleInterseccionProyectoAmbiental>>();	                			
			estadoZonaIntangible=false;
			estadoZonaIntangibleAux=false;
			estadoFrontera=false;
			esZonaSnapEspecial = false;
	        wolframSocial = new ArrayList<Integer>();
	        wolframBiofisica = new ArrayList<Integer>();
	        listaNombresCapasInterseca = new ArrayList<String>();
	        
	        progresoInterseccionCapas = 5;
	        totalPorcentajeProcesado = 0.0;
	        
	        totalPesoCapas = capasCoaFacade.totalPesoCapasSoapInterseccion();
	        totalPesoCapas = totalPesoCapas * coordinatesWrappersGeo.size();
	        
	        for(CoordendasPoligonos item : coordinatesWrappersGeo) {
	        	verificarCapasInterseccion(item);
	        }
	        progresoInterseccionCapas = 100; //para detener el progress bar 
	        /*********************************************************************************************************/
	        
	        //para mostrar msj interseccion al cargar las coordenadas
	        getDetalleIntersecciones();
	        RequestContext.getCurrentInstance().update("frmPreliminar:certificadoIntercepcionRcoa1");
	        RequestContext.getCurrentInstance().update("frmPreliminar:idDocAutorizacionesDectoriales");
		} catch (Exception e) {
        	JsfUtil.addMessageError("Error inesperado, comuníquese con mesa de ayuda");
            LOGGER.error("Error procesando el excel de coordenadas", e);
            coordinatesWrappers = new ArrayList<CoordendasPoligonos>();
            ubicacionesSeleccionadas= new ArrayList<UbicacionesGeografica>();
            superficieProyecto = BigDecimal.ZERO;
    		superficieMetrosCuadrados = BigDecimal.ZERO;
    		coordenadasGeograficas = new ArrayList<CoordenadasProyecto>();
    		coordinatesWrappersGeo = new ArrayList<CoordendasPoligonos>();
    		mostrarDetalleInterseccion = false;
    		estadoFrontera=false;
    		progresoInterseccionCapas = null;
	        totalPorcentajeProcesado = 0.0;
        }
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
							Double areaValorParroquia = areaPoligono * valorParroquia; //calcula el área de intersección de la parroquia
							
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
	
	private Double recuperarAreaPoligono(String coordenada1, String coordenada2) throws Exception {
		SVA_Reproyeccion_IntersecadoPortTypeProxy ws=new SVA_Reproyeccion_IntersecadoPortTypeProxy();
        ws.setEndpoint(Constantes.getInterseccionesWS());
        
		ContieneZona_entrada verificarGeoImpla = new ContieneZona_entrada(); //verifica que el poligono este contenida dentro de la ubicación geográfica
		verificarGeoImpla.setU(Constantes.getUserWebServicesSnap());
		verificarGeoImpla.setXy1(coordenada1);
		verificarGeoImpla.setXy2(coordenada2);
		ContieneZona_resultado[]intRestGeoImpl = ws.contieneZona(verificarGeoImpla);
		if (intRestGeoImpl[0].getInformacion().getError() != null) { 
			return null;
		}
		else
		{
			if (!intRestGeoImpl[0].getContieneCapa().getValor().equals("f")) {
				Double areaPoligono = Double.valueOf(intRestGeoImpl[0].getValorArea().getArea());
				return areaPoligono;
			}
		}
		return null;
	}
	
	public void getDetalleIntersecciones()
	{	
		mostrarDetalleInterseccion = false;
		zonasInterseccionDetalle="";
		
		HashMap<InterseccionProyectoLicenciaAmbiental, List<DetalleInterseccionProyectoAmbiental>> lista =	listaCapasInterseccionPrincipal;
		
		if(lista.size()>0)
		{
			zonasInterseccionDetalle="Su proyecto obra o actividad interseca con: ";
			zonasInterseccionDetalle=zonasInterseccionDetalle+"<ul>";
			String nombreCapa="";
			for (InterseccionProyectoLicenciaAmbiental i : lista.keySet()) {
				nombreCapa=i.getDescripcionCapa();
				String nombreInterseccion="";
				for(DetalleInterseccionProyectoAmbiental j : lista.get(i))
				{
					nombreInterseccion += (nombreInterseccion == "") ? j.getNombreGeometria() : ","+j.getNombreGeometria();				
				}
				zonasInterseccionDetalle = zonasInterseccionDetalle + "<li>" + nombreCapa + ": " + nombreInterseccion + "</li>";
			}
			zonasInterseccionDetalle =zonasInterseccionDetalle + "</ul>";
		}
		
		if(zonasInterseccionDetalle != "") {
			mostrarDetalleInterseccion = true;
			interseca=true;
		}
					
		if(estadoZonaIntangible){
			mostrarDetalleInterseccion = true;
			interseca=true;
		}
	}
	
	public Boolean handleFileUploadImple(final FileUploadEvent event) {
		superficie=0.0;
		superficieMetros = 0.0;
		zonasInterseccionDetalle="";
		mostrarDetalleInterseccion = false;
		Boolean resultadoImple = false;
		
        String coodenadas="";
        Integer nroOrden = 1;
        int rows = 0;
        coordinatesWrappers = new ArrayList<CoordendasPoligonos>();
        CoordendasPoligonos coordinatesWrapper = new CoordendasPoligonos();
        coordinatesWrappers.add(coordinatesWrapper);
        coordenadasImplantacion = new ArrayList<>();
        
        try {
            uploadedFileImpl = event.getFile();
            Workbook workbook = new HSSFWorkbook(uploadedFileImpl.getInputstream());
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
                    CoordenadasProyecto coordenada = new CoordenadasProyecto();
                    Integer poligonoActual = (int) cellIterator.next().getNumericCellValue();
                    coordenada.setAreaGeografica(poligonoActual);
                    coordenada.setOrdenCoordenada((int) cellIterator.next().getNumericCellValue());
                    coordenada.setX(new BigDecimal(Double.valueOf(cellIterator.next().toString().replace("," , "."))).setScale(6, RoundingMode.HALF_DOWN));
                    coordenada.setY(new BigDecimal(Double.valueOf(cellIterator.next().toString().replace("," , "."))).setScale(6, RoundingMode.HALF_DOWN));
                    coordenada.setTipoCoordenada(1);
                    if (coordinatesWrapper.getTipoForma() == null)
                        coordinatesWrapper.setTipoForma(poligono);                    
                    else if (!coordinatesWrapper.getTipoForma().equals(poligono) || coordinatesWrapper.getTipoForma().getId().intValue() == TipoForma.TIPO_FORMA_PUNTO || coordenada.getOrdenCoordenada() == 1) {
                        coordinatesWrapper = new CoordendasPoligonos(null, poligono);
                        coordinatesWrappers.add(coordinatesWrapper);
                        
                        nroOrden = 1;
                    }
                    
                    if(!nroOrden.equals(coordenada.getOrdenCoordenada())) {
                    	//para validar el secuencial del nro de orden de las coordenadas
                    	JsfUtil.addMessageError("Error procesando el excel de coordenadas, verifique que la información se encuentre correcta");
                    	System.out.println("Error en la columna shape. Poligono: " + poligonoActual + ", shape: " + nroOrden);
                		coordinatesWrappers = new ArrayList<CoordendasPoligonos>();
                		return false;
                    }
                    
                    nroOrden++;
                    coordinatesWrapper.getCoordenadas().add(coordenada);
                }
                rows++;
            }
            
            SVA_Reproyeccion_IntersecadoPortTypeProxy ws=new SVA_Reproyeccion_IntersecadoPortTypeProxy();
            ws.setEndpoint(Constantes.getInterseccionesWS());			
			
            for(int i=0;i<=coordinatesWrappers.size()-1;i++){
            	coodenadas="";
            	Integer areaGeografica = 0;
                for (int j = 0; j <=coordinatesWrappers.get(i).getCoordenadas().size()-1; j++) {
                	coodenadas += (coodenadas == "") ? coordinatesWrappers.get(i).getCoordenadas().get(j).getX().toString()+" "+coordinatesWrappers.get(i).getCoordenadas().get(j).getY().toString() : ","+coordinatesWrappers.get(i).getCoordenadas().get(j).getX().toString()+" "+coordinatesWrappers.get(i).getCoordenadas().get(j).getY().toString();
                	areaGeografica = coordinatesWrappers.get(i).getCoordenadas().get(j).getAreaGeografica();
                }                
                Intersecado_entrada poligono = new Intersecado_entrada();//verifica que este bien el poligono
                poligono.setU(Constantes.getUserWebServicesSnap());
                poligono.setIsla(Constantes.SIGLAS_INSTITUCION + "-RA");
                poligono.setTog("po");
                poligono.setXy(coodenadas);
                poligono.setShp("dp");                
                Intersecado_resultado[]intRest;                

                try {
                	intRest=ws.interseccion(poligono);
                	if (intRest[0].getInformacion().getError() != null) {            		
                		JsfUtil.addMessageError(intRest[0].getInformacion().getError().toString());
                		coordinatesWrappers = new ArrayList<CoordendasPoligonos>();
                	}
                	else
                	{
                		coodenadasgeograficas = "";
                		for (CoordenadasProyecto coordenada : coordenadasGeograficas) {
                			if (coordenada.getAreaGeografica() == areaGeografica) {
                				coodenadasgeograficas += (coodenadasgeograficas == "") ? coordenada.getX()+" "+coordenada.getY() : ","+coordenada.getX()+" "+coordenada.getY();
							}
                        }
                		ContieneZona_entrada verificarGeoImpla = new ContieneZona_entrada(); //verifica que el poligono este contenida dentro de la ubicación geográfica
                		verificarGeoImpla.setU(Constantes.getUserWebServicesSnap());
                		verificarGeoImpla.setXy1(coodenadasgeograficas);
                		verificarGeoImpla.setXy2(coodenadas);
                		ContieneZona_resultado[]intRestGeoImpl;
                		intRestGeoImpl=ws.contieneZona(verificarGeoImpla);
                		if (intRestGeoImpl[0].getInformacion().getError() != null) {            		
                			JsfUtil.addMessageError(intRest[0].getInformacion().getError().toString());
                			coordinatesWrappers = new ArrayList<CoordendasPoligonos>();
                		}
                		else
                		{
                			if (intRestGeoImpl[0].getContieneCapa().getValor().equals("f"))
                			{
                				JsfUtil.addMessageError("Polígono no se encuentra dentro de la ubicación geográfica");
                                superficieProyecto = BigDecimal.ZERO;
                        		superficieMetrosCuadrados = BigDecimal.ZERO;
                            	coordinatesWrappers = new ArrayList<CoordendasPoligonos>();
	                			mostrarDetalleInterseccion = false;
                            	superficie=0.0;
                        		superficieMetros = 0.0;
                        		zonasInterseccionDetalle="";
                			}
                			else
                			{	
                				coordenadasImplantacion.add(coodenadas);
                				superficieMetros+=Double.valueOf(intRestGeoImpl[0].getValorArea().getArea());
                				superficie+=Double.valueOf(intRestGeoImpl[0].getValorArea().getArea())/10000;
                				coordinatesWrappers.get(i).setSuperficie(new BigDecimal(Double.valueOf(intRestGeoImpl[0].getValorArea().getArea())/10000));
                				coordinatesWrappers.get(i).setCadenaCoordenadas(coodenadas);
                				

                	            /***********************************************************************************************/
                				//verificarCapasInterseccion(coodenadas);
                	            /***********************************************************************************************/
                			}
                		}
                	}

                }
                catch (RemoteException e) {
                	JsfUtil.addMessageError("Error inesperado, comuníquese con mesa de ayuda");
                	System.out.println("Servicio no disponible ----> "+Constantes.getInterseccionesWS());
                	
                    superficieProyecto = BigDecimal.ZERO;
            		superficieMetrosCuadrados = BigDecimal.ZERO;
                	coordinatesWrappers = new ArrayList<CoordendasPoligonos>();
                	mostrarDetalleInterseccion = false;
            		//estadoFrontera=false;
                }                                
            }
            if(coordinatesWrappers.size()>0)
            {
            	ponderacionMayorSocial();
            	ponderacionMayorBiofisica();
            	
            	resultadoImple = true;
            }
            
            superficieProyecto = new BigDecimal(superficie).setScale(5, BigDecimal.ROUND_HALF_UP);
            superficieMetrosCuadrados = new BigDecimal(superficieMetros).setScale(5, BigDecimal.ROUND_HALF_UP);
            
        } catch (Exception e) {
        	JsfUtil.addMessageError("Error inesperado, comuníquese con mesa de ayuda");
            LOGGER.error("Error procesando el excel de coordenadas", e);
            
            superficieProyecto = BigDecimal.ZERO;
    		superficieMetrosCuadrados = BigDecimal.ZERO;
            coordinatesWrappers = new ArrayList<CoordendasPoligonos>();
            mostrarDetalleInterseccion = false;
    		//estadoFrontera=false;
        }
        
        return resultadoImple;
    }
	
		
	private void verificarCapasInterseccion(CoordendasPoligonos poligoPrincipal){
		try{
			String coodenadas = poligoPrincipal.getCadenaCoordenadas();
			
	        SVA_Reproyeccion_IntersecadoPortTypeProxy ws=new SVA_Reproyeccion_IntersecadoPortTypeProxy();
	        ws.setEndpoint(Constantes.getInterseccionesWS());
	        //Para validar capas para validar si intersacan en electrica
	        String stringCapas=Constantes.getCapasIntersacaLicencia();
	
			//Capas de intersección----------------------------------------------------------
	        List<DetalleInterseccionProyectoAmbiental> listaIntersecciones = new ArrayList<DetalleInterseccionProyectoAmbiental>();
	        listaCapas = new ArrayList<String>();
	        
	        for (CapasCoa itemCapa : capasSoapInterseccion) {
	        	System.out.println(itemCapa.getNombre());
	        	
	        	ContieneCapaRCOA_entrada capaImpla = new ContieneCapaRCOA_entrada(); 
				capaImpla.setU(Constantes.getUserWebServicesSnap());
				capaImpla.setEsquema(itemCapa.getEsquemaInterseccion());
				capaImpla.setTabla(itemCapa.getTablaInterseccion());
				capaImpla.setCampos(itemCapa.getCamposInterseccion());
				capaImpla.setXy(coodenadas);

				InterseccionCapa_resultado[] intRestCapaImpl = ws.interseccionRCOA(capaImpla);

				if (intRestCapaImpl[0].getInformacion().getError() != null) {
					JsfUtil.addMessageError(intRestCapaImpl[0].getInformacion().getError().toString());
					return;
				}
				
				InterseccionProyectoLicenciaAmbiental capaInterseccion = new InterseccionProyectoLicenciaAmbiental();
				
				if(intRestCapaImpl[0].getCapa() != null) {
					for (IntersecadoCapa_capa capas : intRestCapaImpl[0].getCapa()) {
						if(Integer.valueOf(capas.getNum())>0)
						{
							listaCapas.add("<br />" + capas.getNombre());
							capaInterseccion = new InterseccionProyectoLicenciaAmbiental();
							if(capas.getNombre().equals("COMPONENTE SOCIAL"))
							{
								for(Campos_coordenada social : capas.getCampos())
								{
									 wolframSocial.add(Integer.valueOf(social.getPonde()));
								}
							}
							else if(capas.getNombre().equals("COMPONENTE ACODIGA MARITIMA"))
							{
								for(Campos_coordenada maritima : capas.getCampos())
								{
									wolframBiofisica.add(Integer.valueOf(maritima.getPonde()));
								}
							}
							else if(capas.getNombre().equals("COMPONENTE ACOGIDA"))
							{
								for(Campos_coordenada biofisica : capas.getCampos())
								{
									wolframBiofisica.add(Integer.valueOf(biofisica.getPonde()));
								}
							}
							else if(capas.getNombre().equals("OFICINAS_TECNICAS"))
							{
								for(Campos_coordenada oficiona : capas.getCampos())
								{
									System.out.println("oficina tecnica :"+ oficiona.getFcode());
									ubicacionOficinaTecnica=ubicacionfacade.buscarUbicacionPorCodigoInec(oficiona.getFcode());
									break;
								}
							}
//							VALIDACIÓN CAMARONERA
							else if(capas.getNombre().equals("CAMARONERAS")) {
								Double valorInterseccionCapa = Double.valueOf(capas.getCampos()[0].getArea()) * 100;
								System.out.println("Porcentaje interseccion concesión camaronera " + valorInterseccionCapa);
								if (valorInterseccionCapa < porcentajeInterseccionCamaroneras) {
									intersecaConCamaronera = false;
								}else {
									intersecaConCamaronera = true;	
									map = capas.getCampos()[0].getMap();
									nam = capas.getCampos()[0].getNam();
									System.out.println("Concesión camaronera " + nam);
								}
							}
							else
							{
								intersecaConCamaronera = (intersecaConCamaronera != null) ? intersecaConCamaronera : false;
								if(capas.getNombre().equals("ZONAS INTANGIBLES Y ZONAS AMORTIGUAMIENTO"))
								{
									if(Integer.valueOf(capas.getNum())>0)
									estadoZonaIntangibleAux=true;
								}
								if(capas.getNombre().equals("ZONAS DE AMORTIGUAMIENTO YASUNI"))
								{
									if(Integer.valueOf(capas.getNum())>0)
									estadoZonaIntangibleAux=true;
								}
								if(capas.getNombre().equals("LIMITE INTERNO 20 KM"))
								{
									if(Integer.valueOf(capas.getNum())>0)
										estadoFrontera=true;
								}	   					
								
								if(!capas.getNombre().equals("MAR TERRITORIAL"))
								{
//									System.out.println("CAPA:::"+capas.getNombre());
									capaInterseccion.setCapa(getCapa(capas.getNombre()));
									capaInterseccion.setProyectoLicenciaCoa(proyecto);
									capaInterseccion.setDescripcionCapa(capas.getNombre());
									DetalleInterseccionProyectoAmbiental detallecapa = new DetalleInterseccionProyectoAmbiental();
									listaIntersecciones = new ArrayList<DetalleInterseccionProyectoAmbiental>();
									
									if(capas.getNombre().equals("SNAP")){
										intersecaSnapChocoAndino = true;
									}
									
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
											String nombreGeom = interseccion.getNam();
											if(capas.getNombre().equals("SNAP") || capas.getNombre().equals(CapasCoa.NAME_CAPA_SNAP_ZONAS)) {												
												nombreGeom = interseccion.getNam();
												nombreGeom = interseccion.getMap() + " " + interseccion.getNam();//,
			
												if(interseccion.getNam().equals("YASUNI") || interseccion.getNam().equals("CUYABENO")) {
													estadoZonaIntangibleAux = true;
													esZonaSnapEspecial = true;
												}	
												
												listaCapas.add(" - <b>" + nombreGeom + "</b>");
											}
											
											if(capas.getNombre().equals("RAMSAR")) {										
												nombreGeom = interseccion.getNam();										
											}
											detallecapa.setNombreGeometria(nombreGeom);
											detallecapa.setIdGeometria(Integer.valueOf(interseccion.getGid()));
											
											if(nombreGeom.contains("CHOCO ANDINO")){
												intersecaSnapChocoAndino = true;
												listaCapas.add(" - <b>" + nombreGeom + "</b>");
											}
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
										if(interseccion.getFcode() != null)
										{
											if(capas.getNombre().equals("SNAP") || capas.getNombre().equals(CapasCoa.NAME_CAPA_SNAP_ZONAS))
												detallecapa.setCodigoUnicoCapa(interseccion.getFcode());
											else
												detallecapa.setCodigoConvenio(interseccion.getFcode());
										}
										if(capas.getNombre().equals("LIMITE INTERNO 20 KM"))
										{
											detallecapa.setNombreGeometria("SI");
											detallecapa.setIdGeometria(Integer.valueOf(interseccion.getGid()));
										}
										if (capas.getNombre().equals(CapasCoa.NAME_CAPA_SNAP_ZONAS)) {
											detallecapa.setZona(interseccion.getPonde());
										}
										if(interseccion.getFcodec() != null)
										{
											detallecapa.setCodigoConvenio(interseccion.getFcodec());
										}
										if (capas.getNombre().equals("SOCIO BOSQUE") || capas.getNombre().equals("CONVENIO RESTAURACION")) {
											String tipo = (capas.getNombre().equals("SOCIO BOSQUE")) ? "Conservación" : "Restauración";
											detallecapa.setBeneficiarioConvenio(interseccion.getNam());
											detallecapa.setTipoConvenio(tipo);
											
											if (capas.getNombre().equals("CONVENIO RESTAURACION")) {
												detallecapa.setNombreGeometria(interseccion.getEjct());
											}
										}
										
										Double areaTotalCapa = null;
										if(interseccion.getAre() != null) {
											areaTotalCapa = Double.valueOf(interseccion.getAre());
										}
										
										Double porcentajeInterseccionCapa = Double.valueOf(interseccion.getArea());
										BigDecimal valorAreaInterseccion = new BigDecimal(porcentajeInterseccionCapa).multiply(poligoPrincipal.getSuperficie());
										
										detallecapa.setAreaTotalCapa(areaTotalCapa);
										detallecapa.setPorcentajeAreaInterseccion(porcentajeInterseccionCapa);
										detallecapa.setAreaInterseccion(valorAreaInterseccion.doubleValue());
										
										listaIntersecciones.add(detallecapa);
									}
									capasIntersecciones.put(capaInterseccion, listaIntersecciones);
			
									if(capaInterseccion.getCapa() != null && (capaInterseccion.getCapa().getId().equals(Constantes.ID_CAPA_SNAP) || capaInterseccion.getCapa().getId().equals(Constantes.ID_CAPA_BP) || capaInterseccion.getCapa().getId().equals(Constantes.ID_CAPA_PFE) || capaInterseccion.getCapa().getId().equals(Constantes.ID_CAPA_RAMSAR))) {
										List<DetalleInterseccionProyectoAmbiental> listaInterseccionesAux = new ArrayList<DetalleInterseccionProyectoAmbiental>();
										for (DetalleInterseccionProyectoAmbiental detalle : listaIntersecciones) {
											String capaInterseca = capaInterseccion.getCapa().getId() + "- " + detalle.getIdGeometria();
											
											if(!listaNombresCapasInterseca.contains(capaInterseca)) {
												listaNombresCapasInterseca.add(capaInterseca);
												listaInterseccionesAux.add(detalle);
											}
										}
										
										if(listaInterseccionesAux.size() > 0) {
											Iterator<Entry<InterseccionProyectoLicenciaAmbiental, List<DetalleInterseccionProyectoAmbiental>>> it = listaCapasInterseccionPrincipal.entrySet().iterator();
										    while (it.hasNext())
										    {
										       Entry<InterseccionProyectoLicenciaAmbiental, List<DetalleInterseccionProyectoAmbiental>> item = it.next();
										       if(item.getKey().getCapa().getId().equals(capaInterseccion.getCapa().getId())) {
										    	   listaInterseccionesAux.addAll(item.getValue());
										    	   listaCapasInterseccionPrincipal.remove(item.getKey());
										       }
										    }
										    listaCapasInterseccionPrincipal.put(capaInterseccion, listaInterseccionesAux);
										}
									}
								}
							}
						}
						
						if(stringCapas.contains(capas.getNombre().toString()))    
						{
							intersecaBiosfera=true;
						}
					}
				}
				
				totalPorcentajeProcesado = totalPorcentajeProcesado + ((itemCapa.getPesoTiempoCarga() * 100) / Double.valueOf(totalPesoCapas));
				Long porceInter = Math.round(totalPorcentajeProcesado);
				setProgresoInterseccionCapas(porceInter.intValue());
			}
	        
		} catch (RemoteException e) {
             	JsfUtil.addMessageError("Error inesperado, comuníquese con mesa de ayuda");
             	System.out.println("Servicio no disponible ----> "+Constantes.getInterseccionesWS());
             	
             	ubicacionesSeleccionadas= new ArrayList<UbicacionesGeografica>();
                superficieProyecto = BigDecimal.ZERO;
         		superficieMetrosCuadrados = BigDecimal.ZERO;
             	coordinatesWrappers = new ArrayList<CoordendasPoligonos>();
        		coordenadasGeograficas = new ArrayList<CoordenadasProyecto>();
             	mostrarDetalleInterseccion = false;
         		estadoFrontera=false;
             }
	}
	
	public void ponderacionMayorSocial()
	{
		Integer valor=0;
		for(Integer x: wolframSocial)
		{
			if (x == 7) {
				valor=x;
				break;
			}
			if(x>valor)
				valor=x;
		}
		valorCapacidadSocial=valor;
		
	}
	public void ponderacionMayorBiofisica()
	{
		Integer valor=0;
		for(Integer x: wolframBiofisica)
		{
			if (x == 7) {
				valor=x;
				break;
			}
			if(x>valor)
				valor=x;
		}		
		valorCapacidadBiofisica=valor;
	}
	
	public void ubicacionMasArea()
	{	
		Iterator<Entry<String, Double>> it = varUbicacionArea.entrySet().iterator();
		double orden=0;
		String inec="";
		double maxArea = 0;
		while (it.hasNext()) {
			Map.Entry<String, Double> e = (Entry<String, Double>) it.next();
			inec  = e.getKey();
			orden = e.getValue().doubleValue();
			if (orden > maxArea) {
				maxArea = orden;
				area.setInec(inec);
				area.setValor(orden);
			}
		}
		try
		{
			ubicacionPrincipal=ubicacionfacade.buscarUbicacionPorCodigoInec(area.getInec());			
		} catch(Exception e)
		{
			ubicacionesSeleccionadas= new ArrayList<UbicacionesGeografica>();
			System.out.println("geographical_locations --> codeInec:::: " +inec+ " ::::: no existe para la ubicacionPrincipal");
		}
	}
	
	
	public void cargarUbicacionProyecto(HashMap<String, Double> parroquia){
		ubicacionesSeleccionadas= new ArrayList<UbicacionesGeografica>();

		Iterator<Entry<String, Double>> it = parroquia.entrySet().iterator();
		String inec="";
		
		while (it.hasNext()) {
			Map.Entry<String, Double> e = (Entry<String, Double>) it.next();
			inec  = e.getKey();
			if (!inec.equals("")) {
				Map<String, Object> parametros = new HashMap<String, Object>();
				parametros.put("codeInec", inec);
				List<UbicacionesGeografica> lista = crudServiceBean.findByNamedQueryGeneric(UbicacionesGeografica.FIND_PARROQUIA,parametros);
				ubicacionesSeleccionadas.addAll(lista);
			}
		}
	}
	
	public CapasCoa getCapa(String capa) {
        for (CapasCoa c : capas) {
            if (c.getAbreviacion().equals(capa))
                return c;
        }
        return null;
    }
	
	public void verificarPoligono(){
		for(CoordendasPoligonos item : coordinatesWrappers) {
			verificarCapasInterseccionMineria(item);
        }
	}
	
	/**
	 * En este metodo solo se validava si las coordenadas ingresadas en la parte de implantación sean coordenadas de contrato de operaciones
	 * @param poligonoPrincipal
	 */
	
	@Getter
	@Setter
	private Boolean poligonosIguales = false;
	private void verificarCapasInterseccionMineria(CoordendasPoligonos poligonoPrincipal){
		try {
			String coodenadas = poligonoPrincipal.getCadenaCoordenadas();
			
			poligonosIguales = false;
			for(CoordendasPoligonos item : coordinatesWrappersGeo) {
				poligonosIguales = verificarCoordenadasIguales(item, coodenadas);
				if(poligonosIguales){
					break;
				}
	        }
			
			if(poligonosIguales){
				return;
			}
			
			SVA_Reproyeccion_IntersecadoPortTypeProxy ws = new SVA_Reproyeccion_IntersecadoPortTypeProxy();
			ws.setEndpoint(Constantes.getInterseccionesWS());

			// Capas de
			// intersección----------------------------------------------------------
			ContieneCapa_entrada capaImpla = new ContieneCapa_entrada();
			capaImpla.setU(Constantes.getUserWebServicesSnap());
			capaImpla.setXy(coodenadas);
			InterseccionCapa_resultado[] intRestCapaImpl;
			intRestCapaImpl = ws.interseccionCapa(capaImpla);

			if (intRestCapaImpl[0].getInformacion().getError() != null) {
				JsfUtil.addMessageError(intRestCapaImpl[0].getInformacion()
						.getError().toString());
				return;
			}

			for (IntersecadoCapa_capa capas : intRestCapaImpl[0].getCapa()) {
				if (Integer.valueOf(capas.getNum()) > 0) {
					if (capas.getNombre().equals("CONTRATO OPERACIONES")) {
						concesionMinera.setCodigo(capas.getCampos()[0].getCtn1());
						concesionMinera.setCodigoContrato(capas.getCampos()[0].getCtn2());
						concesionMinera.setNombre(capas.getCampos()[0].getFcode());
						concesionMinera.setRucTitularCon(capas.getCampos()[0].getNameco());
						concesionMinera.setRucOperador(capas.getCampos()[0].getCoddnf());
						concesionMinera.setRegimen(capas.getCampos()[0].getNam());
						concesionMinera.setArea(poligonoPrincipal.getSuperficie().doubleValue());
						concesionMinera.setFechaSuscripcion(capas.getCampos()[0].getPonde());
						concesionMinera.setMesesPlazo(capas.getCampos()[0].getMap());
						concesionMinera.setNombreOperador(capas.getCampos()[0].getTpcnv());
					}
				}
			}
		} catch (RemoteException e) {
			JsfUtil.addMessageError("Error inesperado, comuníquese con mesa de ayuda");
			System.out.println("Servicio no disponible ----> "
					+ Constantes.getInterseccionesWS());
		}
	}
	
	public void verificarCoordenadasGeograficasYArcom(String codigoConcesion){
		try {
			DerechoMineroMAEDTO derechoMinero = new DerechoMineroMAEDTO();
			SuiaServices_Service_Arcon concesion;
			concesion = new SuiaServices_Service_Arcon(new URL(Constantes.getUrlWsRegistroCivilSri()));
			SuiaServicesArcon arcon=concesion.getSuiaServicesPort();
			derechoMinero=arcon.getConsultarCatastral(codigoConcesion);
			List<Coordenadas> listaCoordenadas = derechoMinero.getCoordenadasWGS84();
			
			String coordenadas = "";
			int i = 0;
			
			BigDecimal x1 = new BigDecimal(0);
			BigDecimal y1 = new BigDecimal(0);
			
			/**
			 * Se obtiene el string de las coordenadas de ARCOM y además para completar el polígono se añade la primera coordena
			 * que viene de ARCOM.
			 */
			for(Coordenadas coordenada : listaCoordenadas){
				
				if(i == 0){
					x1 = new BigDecimal(Double.valueOf(coordenada.getUtmEste().toString().replace("," , "."))).setScale(6, RoundingMode.HALF_DOWN);
					y1 = new BigDecimal(Double.valueOf(coordenada.getUtmNorte().toString().replace("," , "."))).setScale(6, RoundingMode.HALF_DOWN);
				}
				
				
				BigDecimal x = new BigDecimal(Double.valueOf(coordenada.getUtmEste().toString().replace("," , "."))).setScale(6, RoundingMode.HALF_DOWN);
				BigDecimal y = new BigDecimal(Double.valueOf(coordenada.getUtmNorte().toString().replace("," , "."))).setScale(6, RoundingMode.HALF_DOWN);
				coordenadas += (coordenadas == "") ? x+" "+y : ","+x+" "+y;
				
				i++;
			}
			
			concesionMinera.setMaterial(derechoMinero.getDerechoMinero().getMaterialInteres());
			concesionMinera.setNombre(derechoMinero.getDerechoMinero().getNombreDerechoMinero());
			concesionMinera.setRegimen(derechoMinero.getDerechoMinero().getRegimen());
			concesionMinera.setArea(derechoMinero.getDerechoMinero().getSuperficie());
			
			coordenadas += ","+x1+" "+y1;
			
			for(CoordendasPoligonos item : coordinatesWrappersGeo) {
				verificarCoordenadasGeograficasYArcomMet(item, coordenadas);
	        }			
			
		} catch (Exception e) {
			JsfUtil.addMessageError("Sin servicio ARCOM");
			e.printStackTrace();
			return;		
		}		
	}
	
	private void verificarCoordenadasGeograficasYArcomMet(CoordendasPoligonos poligonoPrincipal, String coordenadasArcom){
		try {
			
			String coordenadas = poligonoPrincipal.getCadenaCoordenadas();
			
			if(coordenadas.equals(coordenadasArcom)){
				System.out.println("son iguales");
			}else{
				System.out.println("No son iguales");
			}
			
			 SVA_Reproyeccion_IntersecadoPortTypeProxy ws=new SVA_Reproyeccion_IntersecadoPortTypeProxy();
	         ws.setEndpoint(Constantes.getInterseccionesWS());
			
			
			ContieneZona_entrada verificarGeoImpla = new ContieneZona_entrada(); //verifica que el poligono este contenida dentro de la ubicación geográfica
    		verificarGeoImpla.setU(Constantes.getUserWebServicesSnap());
    		verificarGeoImpla.setXy1(coordenadasArcom);
    		verificarGeoImpla.setXy2(coordenadas);
    		ContieneZona_resultado[]intRestGeoImpl;
    		intRestGeoImpl=ws.contieneZona(verificarGeoImpla);
    		if (intRestGeoImpl[0].getInformacion().getError() != null) {            		
    			JsfUtil.addMessageError(intRestGeoImpl[0].getInformacion().getError().toString());
    			coordinatesWrappers = new ArrayList<CoordendasPoligonos>();
    		}
    		else
    		{
    			if (intRestGeoImpl[0].getContieneCapa().getValor().equals("f")){                   
            		concesionMinera.setErrorValidacionCoordArcom("Polígono no se encuentra dentro de la ubicación geográfica");
            		concesionMinera.setCoordenadasCoinciden(false);
    			}
    			else{	
    				concesionMinera.setCoordenadasCoinciden(true);
    			}
    		}			
		} catch (Exception e) {
			LOGGER.error(e.getStackTrace());
		}		
	}
	
	private Boolean verificarCoordenadasIguales(CoordendasPoligonos poligonoPrincipal, String coordenadasArcom){
		
		String coordenadas = poligonoPrincipal.getCadenaCoordenadas();
		
		if(coordenadas.equals(coordenadasArcom)){
			return true;
		}else{
			return false;
		}			
	}
	
	public void verificarCoordenadasContiguas(){
		try {
			poligonosContiguos = false;
			Concesiones_entrada con = new Concesiones_entrada();
			SVA_Reproyeccion_IntersecadoPortTypeProxy ws=new SVA_Reproyeccion_IntersecadoPortTypeProxy();
			Concesion_resultado[] salida = null;
			con.setU(Constantes.getUserWebServicesSnap());
			con.setConcesion1("");
			con.setConcesion2("");
			con.setConcesion3("");
			con.setConcesion4("");
			con.setConcesion5("");
			
			String coor1 = "";
			String coor2 = "";
			String coor3 = "";
			String coor4 = "";
			String coor5 = "";
			
			for(CoordenadasProyecto coordenada : coordenadasGeograficas){
				if(coordenada.getAreaGeografica().equals(1)){
					coor1 +=(coor1 == "") ? coordenada.getX()+" "+coordenada.getY() : ","+coordenada.getX()+" "+coordenada.getY();
					con.setConcesion1(coor1);					
					
				}else if(coordenada.getAreaGeografica().equals(2)){
					coor2 +=(coor2 == "") ? coordenada.getX()+" "+coordenada.getY() : ","+coordenada.getX()+" "+coordenada.getY();
					con.setConcesion2(coor2);
					
				}else if(coordenada.getAreaGeografica().equals(3)){
					coor3 +=(coor3 == "") ? coordenada.getX()+" "+coordenada.getY() : ","+coordenada.getX()+" "+coordenada.getY();
					con.setConcesion3(coor3);
					
				}else if(coordenada.getAreaGeografica().equals(4)){
					coor4 +=(coor4 == "") ? coordenada.getX()+" "+coordenada.getY() : ","+coordenada.getX()+" "+coordenada.getY();
					con.setConcesion4(coor4);
					
				}else if(coordenada.getAreaGeografica().equals(5)){
					coor5 +=(coor5 == "") ? coordenada.getX()+" "+coordenada.getY() : ","+coordenada.getX()+" "+coordenada.getY();
					con.setConcesion5(coor5);
				}
			}
			
			if(con.getConcesion2() != ""){
				try {
					salida=ws.colindante(con);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
								
				if(salida[0].getConcesion().getValor().equals("t")){
					poligonosContiguos = true;
					JsfUtil.addMessageInfo("Validación de polígonos correcta");
				}
				else{     						
					JsfUtil.addMessageError("Las coordenadas ingresadas del área geográfica para Tierras privadas o Zonas Altas y Zona de Playa y Bahía no son contiguas");									
				}
			}else{
				poligonosContiguos = true;
				JsfUtil.addMessageInfo("Las coordenadas de su proyecto acuícola Zona Mixta corresponden a un solo shape");				
			}
						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
