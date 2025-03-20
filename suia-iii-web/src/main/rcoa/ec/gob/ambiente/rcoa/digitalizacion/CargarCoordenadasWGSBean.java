package ec.gob.ambiente.rcoa.digitalizacion;

import index.Campos_coordenada;
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

import java.io.ByteArrayInputStream;
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

import ec.gob.ambiente.rcoa.digitalizacion.facade.CoordenadaDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.ShapeDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.UbicacionDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.model.AutorizacionAdministrativaAmbiental;
import ec.gob.ambiente.rcoa.digitalizacion.model.CoordenadaDigitalizacion;
import ec.gob.ambiente.rcoa.digitalizacion.model.DetalleInterseccionDigitalizacion;
import ec.gob.ambiente.rcoa.digitalizacion.model.InterseccionProyectoDigitalizacion;
import ec.gob.ambiente.rcoa.digitalizacion.model.ShapeDigitalizacion;
import ec.gob.ambiente.rcoa.digitalizacion.model.UbicacionDigitalizacion;
import ec.gob.ambiente.rcoa.facade.CapasCoaFacade;
import ec.gob.ambiente.rcoa.model.CapasCoa;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.facade.PostgisFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.TipoForma;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class CargarCoordenadasWGSBean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = Logger.getLogger(CargarCoordenadasWGSBean.class);
	
	@EJB
	private PostgisFacade postgisFacade;
	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
	@EJB
	private DocumentosFacade documentosFacade;
	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB
	private CapasCoaFacade capasCoaFacade;
	@EJB
	private UbicacionGeograficaFacade ubicacionfacade;
	@EJB
	private ShapeDigitalizacionFacade shapeDigitalizacionFacade;
	@EJB
	private CoordenadaDigitalizacionFacade coordenadaDigitalizacionFacade;
	@EJB
	private UbicacionDigitalizacionFacade ubicacionDigitalizacionFacade;
	
	@Getter
	private List<TipoForma> tiposFormas;
	
	private TipoForma poligono, tipoFormaCoordenada;

	@Getter
	@Setter
	private UploadedFile uploadedFileGeo;
	
	private byte[] plantillaCoordenadas;
	
	private byte[] ayudaCoordenadas;
	
	private HashMap<String, Double> varUbicacionArea= new HashMap<String,Double>();
	
	private HashMap<String, Double> varUbicacionAux = new HashMap<String,Double>();
	
	@Getter
	@Setter
	private List<CoordenadaDigitalizacion> coordenadasGeograficas;
	
	@Getter
    @Setter
	private List<CoordenadasPoligonos> coordinatesWrappersGeo = new ArrayList<CoordenadasPoligonos>();
	
	@Getter
	@Setter
	private ShapeDigitalizacion shapeDigitalizacion;
	
	@Getter
	@Setter
	private List<UbicacionesGeografica> ubicacionesSeleccionadas;

	@Getter
	@Setter
	private HashMap<InterseccionProyectoDigitalizacion, List<DetalleInterseccionDigitalizacion>> capasIntersecciones, listaCapasInterseccionPrincipal;
	
	private List<CapasCoa> capas = new ArrayList<CapasCoa>();
	
	@Getter
	@Setter
	private boolean mostrarDetalleInterseccion = false;
	
	@Getter
	@Setter
	private String zonasInterccionDetalle="";
	
	@Getter
    @Setter
    private List<String> areasProtegidasWgs, bosquesProtectoresWgs;
	
	@Getter
	@Setter
	private AutorizacionAdministrativaAmbiental autorizacionAdministrativa;
	
	private boolean esPoligono=true;
	
	@PostConstruct
	public void init(){
		try {
			areasProtegidasWgs = new ArrayList<String>();
			bosquesProtectoresWgs = new ArrayList<String>();
			shapeDigitalizacion = new ShapeDigitalizacion();
	        coordenadasGeograficas = new ArrayList<CoordenadaDigitalizacion>();
	        coordinatesWrappersGeo = new ArrayList<CoordenadasPoligonos>();
			tiposFormas = proyectoLicenciamientoAmbientalFacade.getTiposFormas();
			poligono = getTipoForma(TipoForma.TIPO_FORMA_POLIGONO);
			capasIntersecciones= new HashMap<InterseccionProyectoDigitalizacion, List<DetalleInterseccionDigitalizacion>>();
			listaCapasInterseccionPrincipal= new HashMap<InterseccionProyectoDigitalizacion, List<DetalleInterseccionDigitalizacion>>();
			try {
				plantillaCoordenadas = documentosFacade.descargarDocumentoPorNombre(Constantes.PLANTILLA_COORDENADAS_WGS_17S);
			} catch (Exception e) {
			}
			capas=capasCoaFacade.listaCapas();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void reset() {
		coordinatesWrappersGeo = new ArrayList<CoordenadasPoligonos>();
	}
	
	public TipoForma getTipoForma(Integer id) {
        for (TipoForma tf : tiposFormas) {
            if (tf.getId()==id)
                return tf;
        }
        return null;
    }
	
	public void cargarCoordenadasWgs(){
		List<ShapeDigitalizacion> objShape = shapeDigitalizacionFacade.obtenerShapePorSistema(autorizacionAdministrativa.getId(), 2, true);
		// cargo ubicaciones
		if(objShape != null && objShape.size() > 0){
			Integer tipoIngreso = objShape.get(0).getTipoIngreso().equals(1)?objShape.get(0).getTipoIngreso():3;
			for (ShapeDigitalizacion shapeAux : objShape) {
				List<CoordenadaDigitalizacion> coordenadas = coordenadaDigitalizacionFacade.obtenerCoordenadas(shapeAux.getId());
				CoordenadasPoligonos coorP = new CoordenadasPoligonos();
				coorP.setCoordenadas(coordenadas);
				coorP.setTipoForma(shapeAux.getTipoForma());
				coordinatesWrappersGeo.add(coorP);
				shapeDigitalizacion = shapeAux;
			}
			//cargo las ubicaciones 
			List<UbicacionDigitalizacion> ListaUbicacionTipo = ubicacionDigitalizacionFacade.obtenerUbicacinesPorProyectoPorSistema(autorizacionAdministrativa.getId(), tipoIngreso, shapeDigitalizacion.getSistemaReferencia(), shapeDigitalizacion.getZona());
			ubicacionesSeleccionadas = new ArrayList<UbicacionesGeografica>();
			if(ListaUbicacionTipo != null && ListaUbicacionTipo.size() > 0){
				for (UbicacionDigitalizacion ubicacionDigitalizacion : ListaUbicacionTipo) {
					ubicacionesSeleccionadas.add(ubicacionDigitalizacion.getUbicacionesGeografica());
				}
			}
		}
	}

	private String coodenadasgeograficas="";
	public void handleFileUploadWGS(final FileUploadEvent event) {
		TipoForma tipoFormaCoordenadas = poligono;
		// si no es poligono asigno la nueva forma
		if(!esPoligono && tipoFormaCoordenada != null && tipoFormaCoordenada.getId() != null)
			tipoFormaCoordenadas = tipoFormaCoordenada;
		shapeDigitalizacion.setTipoIngreso(2);// ingreso manual
		shapeDigitalizacion.setTipoForma(tipoFormaCoordenadas);
		shapeDigitalizacion.setTipo(2);//coordenadas geograficas
		shapeDigitalizacion.setSistemaReferencia("WGS84");
		shapeDigitalizacion.setZona("17S");
		coodenadasgeograficas="";
		Integer poligonoAnterior = 0;
        int rows = 0;
        List<String> listCoodenadasgeograficas = new ArrayList<String>();
        coordenadasGeograficas = new ArrayList<CoordenadaDigitalizacion>();
        List<CoordenadaDigitalizacion> coordenadasGeograficasAux  = new ArrayList<CoordenadaDigitalizacion>();
        coordinatesWrappersGeo = new ArrayList<CoordenadasPoligonos>();
        
        ubicacionesSeleccionadas= new ArrayList<UbicacionesGeografica>();
		mostrarDetalleInterseccion = false;
		varUbicacionArea = new HashMap<String,Double>();
		varUbicacionAux = new HashMap<String,Double>();
		
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
                    CoordenadaDigitalizacion coordenada = new CoordenadaDigitalizacion();
                    Integer poligonoActual = (int) cellIterator.next().getNumericCellValue();
                    coordenada.setAreaGeografica(poligonoActual);
                    coordenada.setOrden((int) cellIterator.next().getNumericCellValue());
                    coordenada.setX(new BigDecimal(Double.valueOf(cellIterator.next().toString().replace("," , "."))).setScale(6, RoundingMode.HALF_DOWN));
                    coordenada.setY(new BigDecimal(Double.valueOf(cellIterator.next().toString().replace("," , "."))).setScale(6, RoundingMode.HALF_DOWN));
                    coordenada.setTipo(2);
                    coordenadasGeograficas.add(coordenada);
                    // coodenadasgeograficas += (coodenadasgeograficas == "") ? coordenada.getX()+" "+coordenada.getY() : ","+coordenada.getX()+" "+coordenada.getY();
                    if (poligonoAnterior == 0) {
                    	poligonoAnterior = poligonoActual;
					}
                    if (poligonoAnterior == poligonoActual) {
                    	coodenadasgeograficas += (coodenadasgeograficas == "") ? coordenada.getX()+" "+coordenada.getY() : ","+coordenada.getX()+" "+coordenada.getY();
                    	if (sheet.getLastRowNum() == rows) {
                    		listCoodenadasgeograficas.add(coodenadasgeograficas);
    						
    						coordenadasGeograficasAux.add(coordenada);
    						CoordenadasPoligonos coordinatesWrapper = new CoordenadasPoligonos(null, tipoFormaCoordenadas);
    		                coordinatesWrapper.getCoordenadas().addAll(coordenadasGeograficasAux);
    		                coordinatesWrapper.setCadenaCoordenadas(coodenadasgeograficas);
    		                coordinatesWrappersGeo.add(coordinatesWrapper);
    		                
    		                coodenadasgeograficas = "";
    		                coordenadasGeograficasAux = new ArrayList<>();
						}
					} else {
						listCoodenadasgeograficas.add(coodenadasgeograficas);
						CoordenadasPoligonos coordinatesWrapper = new CoordenadasPoligonos(null, tipoFormaCoordenadas);
                        coordinatesWrapper.getCoordenadas().addAll(coordenadasGeograficasAux);
                        coordinatesWrapper.setCadenaCoordenadas(coodenadasgeograficas);
                        coordinatesWrappersGeo.add(coordinatesWrapper);
                        coordenadasGeograficasAux = new ArrayList<>();
                        
						coodenadasgeograficas = "";
						coodenadasgeograficas += (coodenadasgeograficas == "") ? coordenada.getX()+" "+coordenada.getY() : ","+coordenada.getX()+" "+coordenada.getY();
						poligonoAnterior = poligonoActual;
					}
                    
                    coordenadasGeograficasAux.add(coordenada);
                }
                rows++;
            }
            if(coodenadasgeograficas != "") {
            	listCoodenadasgeograficas.add(coodenadasgeograficas);
            	
            	CoordenadasPoligonos coordinatesWrapper = new CoordenadasPoligonos(null, tipoFormaCoordenadas);
                coordinatesWrapper.getCoordenadas().addAll(coordenadasGeograficasAux);
                coordinatesWrapper.setCadenaCoordenadas(coodenadasgeograficas);
                coordinatesWrappersGeo.add(coordinatesWrapper);
                coordenadasGeograficasAux = new ArrayList<>();
            }
            
            for(CoordenadasPoligonos item : coordinatesWrappersGeo) {
            	String coodenadasgeograficas = item.getCadenaCoordenadas();
            	
            	Intersecado_entrada poligono = new Intersecado_entrada();
                poligono.setU(Constantes.getUserWebServicesSnap());
                poligono.setIsla(Constantes.SIGLAS_INSTITUCION + "-RA");
                poligono.setTog(tipoFormaCoordenadas.getNombre().substring(0, 2).toLowerCase().replace("í","i"));
                poligono.setXy(coodenadasgeograficas);
                poligono.setShp("dp");
                SVA_Reproyeccion_IntersecadoPortTypeProxy ws=new SVA_Reproyeccion_IntersecadoPortTypeProxy();
                ws.setEndpoint(Constantes.getInterseccionesWS());
                Intersecado_resultado[]intRest;
                try {
                	intRest=ws.interseccion(poligono);
                	if (intRest[0].getInformacion().getError() != null) {
                		JsfUtil.addMessageError(intRest[0].getInformacion().getError().toString()+ " o no corresponde al sistema de referencia o tipo de coordenadas indicado");
                		System.out.println("Servicio no disponible ----> "+Constantes.getInterseccionesWS());
                		coordenadasGeograficas = new ArrayList<CoordenadaDigitalizacion>();
                		coordinatesWrappersGeo = new ArrayList<CoordenadasPoligonos>();
                		return;
                	}            	
                	if (intRest[0].getCapa()[0].getError() != null) {
                		JsfUtil.addMessageError(intRest[0].getCapa()[0].getError().toString());
                		System.out.println("Servicio no disponible ----> "+Constantes.getInterseccionesWS());
                		coordenadasGeograficas = new ArrayList<CoordenadaDigitalizacion>();
                		coordinatesWrappersGeo = new ArrayList<CoordenadasPoligonos>();
                		return;
                	}
                	Double areaPoligono = 0.00;
                	// solo si es poligono calculo el area 
                	if(tipoFormaCoordenadas.getId().equals(TipoForma.TIPO_FORMA_POLIGONO)){
                    	Double areaPoligonoMetros = recuperarAreaPoligono(coodenadasgeograficas, coodenadasgeograficas);
                    	if(areaPoligonoMetros == null) {
                    		JsfUtil.addMessageError("Error a recuperar la ubicación del proyecto");
                    		coordenadasGeograficas = new ArrayList<CoordenadaDigitalizacion>();
                    		coordinatesWrappersGeo = new ArrayList<CoordenadasPoligonos>();
                    		return;
                    	}
                    	areaPoligono = areaPoligonoMetros/10000;
                	}
                	
                	item.setSuperficie(new BigDecimal(areaPoligono));
                	recuperarParroquiasInterseccion(areaPoligono, intRest);
                	cargarUbicacionProyecto(varUbicacionAux);
                	ubicacionMasArea();
                }
                catch (RemoteException e) {
                	JsfUtil.addMessageError("Error inesperado, comuníquese con mesa de ayuda");
                	coordenadasGeograficas = new ArrayList<CoordenadaDigitalizacion>();
                	coordinatesWrappersGeo = new ArrayList<CoordenadasPoligonos>();
                }
			}
            /***********************************************************************************************/
            capasIntersecciones= new HashMap<InterseccionProyectoDigitalizacion, List<DetalleInterseccionDigitalizacion>>();
			listaCapasInterseccionPrincipal = new HashMap<InterseccionProyectoDigitalizacion, List<DetalleInterseccionDigitalizacion>>();

			areasProtegidasWgs = new ArrayList<String>();
			bosquesProtectoresWgs = new ArrayList<String>();
            for (String coodenadasgeograficas : listCoodenadasgeograficas) {                
            	verificarCapasInterseccion(coodenadasgeograficas);
            }
            /*********************************************************************************************************/
            //para mostrar msj interseccion al cargar las coordenadas
            getDetalleIntersecciones();
        } catch (Exception e) {
        	JsfUtil.addMessageError("Error inesperado, comuníquese con mesa de ayuda");
            LOGGER.error("Error procesando el excel de coordenadas", e);
            ubicacionesSeleccionadas= new ArrayList<UbicacionesGeografica>();
    		coordenadasGeograficas = new ArrayList<CoordenadaDigitalizacion>();
    		coordinatesWrappersGeo = new ArrayList<CoordenadasPoligonos>();
    		mostrarDetalleInterseccion = false;
        }
	}
	
	public void handleFileUploadWGSAux(final FileUploadEvent event, Integer tipoFormaId) {
		if(tipoFormaId.equals(TipoForma.TIPO_FORMA_POLIGONO)){
			esPoligono=true;
		}else{
			esPoligono=false;
			tipoFormaCoordenada = getTipoForma(tipoFormaId);
		}
		handleFileUploadWGS(event);
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

	private void verificarCapasInterseccion(String coodenadas){
		try{
			SVA_Reproyeccion_IntersecadoPortTypeProxy ws=new SVA_Reproyeccion_IntersecadoPortTypeProxy();
			ws.setEndpoint(Constantes.getInterseccionesWS());
			//Capas de intersección----------------------------------------------------------
			ContieneCapa_entrada capaImpla = new ContieneCapa_entrada();
			capaImpla.setU(Constantes.getUserWebServicesSnap());
			capaImpla.setXy(coodenadas);
			InterseccionCapa_resultado[]intRestCapaImpl;
			intRestCapaImpl=ws.interseccionCapa(capaImpla);

			if (intRestCapaImpl[0].getInformacion().getError() != null){
				JsfUtil.addMessageError(intRestCapaImpl[0].getInformacion().getError().toString());
				return;
			}
			List<String> listaNombresCapasInterseca = new ArrayList<String>();
			InterseccionProyectoDigitalizacion capaInterseccion = new InterseccionProyectoDigitalizacion();
			List<DetalleInterseccionDigitalizacion> listaIntersecciones = new ArrayList<DetalleInterseccionDigitalizacion>();
			if(intRestCapaImpl[0].getCapa() != null){
				for (IntersecadoCapa_capa capas : intRestCapaImpl[0].getCapa()){
					if(Integer.valueOf(capas.getNum())>0){
						capaInterseccion = new InterseccionProyectoDigitalizacion();
						//VALIDACIÓN CAMARONERA
						if(capas.getNombre().equals("CAMARONERAS")){
							if (Double.valueOf((capas.getCampos()[0].getArea())) != 1){
								//validacion_camaronera = false;
							}else {
								//validacion_camaronera = true;
								//map = capas.getCampos()[0].getMap();
								//nam = capas.getCampos()[0].getNam();
							}
						}else{
							if(!capas.getNombre().equals("MAR TERRITORIAL")){
								//System.out.println("CAPA:::"+capas.getNombre());
								capaInterseccion.setCapa(getCapa(capas.getNombre()));
								//capaInterseccion.setProyectoLicenciaCoa(autorizacionAdministrativa.getCodigoProyecto());
								capaInterseccion.setDescripcionCapa(capas.getNombre());
								DetalleInterseccionDigitalizacion detallecapa = new DetalleInterseccionDigitalizacion();
								listaIntersecciones = new ArrayList<DetalleInterseccionDigitalizacion>();
								for(Campos_coordenada interseccion : capas.getCampos()){
									detallecapa = new DetalleInterseccionDigitalizacion();
									if(interseccion.getTphmd()!=null){
										detallecapa.setNombreGeometria(interseccion.getTphmd());
										detallecapa.setIdGeometria(Integer.valueOf(interseccion.getGid()));
									}
									if(interseccion.getTpcnv()!=null){
										detallecapa.setNombreGeometria(interseccion.getTpcnv());
										detallecapa.setIdGeometria(Integer.valueOf(interseccion.getGid()));
									}
									if(interseccion.getNam()!=null){
										String nombreGeom = interseccion.getNam();
										if(capas.getNombre().equals("SNAP")){
											nombreGeom = interseccion.getNam();
											nombreGeom = interseccion.getMap() + " " + interseccion.getNam();//,
	
											if(interseccion.getNam().equals("YASUNI") || interseccion.getNam().equals("CUYABENO")){
												//estadoZonaIntangibleAux = true;
												//esZonaSnapEspecial = true;
											}
										}
			
										if(capas.getNombre().equals("RAMSAR")){
											nombreGeom = interseccion.getNam();
										}
										detallecapa.setNombreGeometria(nombreGeom);
										detallecapa.setIdGeometria(Integer.valueOf(interseccion.getGid()));
									}
									if(interseccion.getNameco()!=null){
										detallecapa.setNombreGeometria(interseccion.getNameco());
										detallecapa.setIdGeometria(Integer.valueOf(interseccion.getGid()));
									}
									if(interseccion.getCtn2()!=null){
										detallecapa.setNombreGeometria(interseccion.getCtn2());
										detallecapa.setIdGeometria(Integer.valueOf(interseccion.getGid()));
									}
									if(interseccion.getFcode()!=null){
										if(capas.getNombre().equals("SNAP"))
											detallecapa.setCodigoUnicoCapa(interseccion.getFcode());
										else
											detallecapa.setCodigoConvenio(interseccion.getFcode());
									}
									if(capas.getNombre().equals("LIMITE INTERNO 20 KM")){
										detallecapa.setNombreGeometria("SI");
										detallecapa.setIdGeometria(Integer.valueOf(interseccion.getGid()));
									}
									listaIntersecciones.add(detallecapa);
								}
								capasIntersecciones.put(capaInterseccion, listaIntersecciones);
	
								if(capaInterseccion.getCapa() != null && (capaInterseccion.getCapa().getId().equals(Constantes.ID_CAPA_SNAP) || capaInterseccion.getCapa().getId().equals(Constantes.ID_CAPA_BP) || capaInterseccion.getCapa().getId().equals(Constantes.ID_CAPA_PFE) || capaInterseccion.getCapa().getId().equals(Constantes.ID_CAPA_RAMSAR))){
									List<DetalleInterseccionDigitalizacion> listaInterseccionesAux = new ArrayList<DetalleInterseccionDigitalizacion>();
									for (DetalleInterseccionDigitalizacion detalle : listaIntersecciones){
										String capaInterseca = capaInterseccion.getCapa().getId() + "- " + detalle.getIdGeometria();
										if(!listaNombresCapasInterseca.contains(capaInterseca)){
											listaNombresCapasInterseca.add(capaInterseca);
											listaInterseccionesAux.add(detalle);
										}
									}
	
									if(listaInterseccionesAux.size() > 0){
										Iterator<Entry<InterseccionProyectoDigitalizacion, List<DetalleInterseccionDigitalizacion>>> it = listaCapasInterseccionPrincipal.entrySet().iterator();
										while (it.hasNext()){
											Entry<InterseccionProyectoDigitalizacion, List<DetalleInterseccionDigitalizacion>> item = it.next();
											if(item.getKey().getCapa().getId().equals(capaInterseccion.getCapa().getId())){
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
				}
			}
		}catch (RemoteException e){
			JsfUtil.addMessageError("Error inesperado, comuníquese con mesa de ayuda");
			System.out.println("Servicio no disponible ----> "+Constantes.getInterseccionesWS());
			ubicacionesSeleccionadas= new ArrayList<UbicacionesGeografica>();
			//superficieProyecto = BigDecimal.ZERO;
			//superficieMetrosCuadrados = BigDecimal.ZERO;
			//coordinatesWrappersGeo = new ArrayList<Coordendas>();
			//coordenadasGeograficas = new ArrayList<CoordenadasProyecto>();
			mostrarDetalleInterseccion = false;
			//estadoFrontera=false;
		}
	}
	
	public void getDetalleIntersecciones()
	{
		mostrarDetalleInterseccion = false;
		zonasInterccionDetalle="";
		HashMap<InterseccionProyectoDigitalizacion, List<DetalleInterseccionDigitalizacion>> lista = listaCapasInterseccionPrincipal;
		
		if(lista.size()>0)
		{
			zonasInterccionDetalle="Estimado usuario el punto que desea ingresar se encuentra intersecando con: ";
			zonasInterccionDetalle=zonasInterccionDetalle+"<ul>";
			String nombreCapa="";
			for (InterseccionProyectoDigitalizacion i : lista.keySet()) {
				nombreCapa=i.getDescripcionCapa();
				String nombreInterseccion="";
				for(DetalleInterseccionDigitalizacion j : lista.get(i))
				{
					nombreInterseccion += (nombreInterseccion == "") ? j.getNombreGeometria() : ","+j.getNombreGeometria();
					// verifico si es una area protegida
					if(nombreCapa.equals("SNAP")){
						areasProtegidasWgs.add(j.getNombreGeometria());
					}
					// verifico si es un bosque protector 
					if(nombreCapa.equals("BOSQUES PROTECTORES")){
						bosquesProtectoresWgs.add(j.getNombreGeometria());
					}
				}
				zonasInterccionDetalle = zonasInterccionDetalle + "<li>" + nombreCapa + ": " + nombreInterseccion + "</li>";
			}
			zonasInterccionDetalle =zonasInterccionDetalle + "</ul>";
			zonasInterccionDetalle +="<b/>lo cual no está permitido para las políticas REP";
		}
		if(zonasInterccionDetalle != "")
			mostrarDetalleInterseccion = true;
	}
	
	public CapasCoa getCapa(String capa) {
		for (CapasCoa c : capas) {
			if (c.getAbreviacion().equals(capa))
				return c;
		}
		return null;
	}
	
	public void cargarUbicacionProyecto(HashMap<String, Double> parroquia){
		ubicacionesSeleccionadas= new ArrayList<UbicacionesGeografica>();
		Iterator it = parroquia.entrySet().iterator();
		String inec="";
		while (it.hasNext()) {
			Map.Entry<String, Double> e = (Entry<String, Double>) it.next();
			inec  = e.getKey();
			if (!inec.equals("")) {
				Map<String, Object> parametros = new HashMap<String, Object>();
				parametros.put("codeInec", inec);
				List<UbicacionesGeografica> lista = crudServiceBean.findByNamedQueryGeneric(UbicacionesGeografica.FIND_PARROQUIA,parametros);
				ubicacionesSeleccionadas.addAll(lista);
				JsfUtil.getBean(AdicionarUbicacionesWGSBean.class).setUbicacionesSeleccionadas(ubicacionesSeleccionadas);
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
	}
	
	public StreamedContent getPlantillaCoordenadas() throws Exception {
		DefaultStreamedContent content = null;
		try {
			if (plantillaCoordenadas != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(plantillaCoordenadas));
				content.setName(Constantes.PLANTILLA_COORDENADAS);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}
	
	public StreamedContent getAyudaCoordenadas() throws Exception {
		DefaultStreamedContent content = null;
		try {
			if (ayudaCoordenadas != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(ayudaCoordenadas));
				content.setName(Constantes.AYUDA_COORDENADAS);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
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
		UbicacionesGeografica ubi = ubicacionfacade.buscarUbicacionPorCodigoInec(inecMayor);
		if(ubi != null && !ubicacionesSeleccionadas.contains(ubi))
			ubicacionesSeleccionadas.add(ubi);
	}
}