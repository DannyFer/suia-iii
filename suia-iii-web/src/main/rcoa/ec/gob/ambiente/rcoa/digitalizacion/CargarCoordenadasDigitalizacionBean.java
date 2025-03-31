package ec.gob.ambiente.rcoa.digitalizacion;

import index.ContieneZona_entrada;
import index.ContieneZona_resultado;
import index.Intersecado_entrada;
import index.Intersecado_resultado;
import index.SVA_Reproyeccion_IntersecadoPortTypeProxy;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import ec.gob.ambiente.rcoa.digitalizacion.model.ShapeDigitalizacion;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.TipoForma;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class CargarCoordenadasDigitalizacionBean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = Logger.getLogger(CargarCoordenadasDigitalizacionBean.class);
	
	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
	@EJB
	private DocumentosFacade documentosFacade;
	@EJB
	private CrudServiceBean crudServiceBean;
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
	
	private TipoForma poligono;

	@Getter
	@Setter
	private UploadedFile uploadedFileGeo;
	
	private byte[] plantillaCoordenadas, plantillaCoordenadasPsad;
	
	private byte[] ayudaCoordenadas;
	
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
	private AutorizacionAdministrativaAmbiental autorizacionAdministrativa;
    @Getter
    @Setter
    private String sistemareferenciaSeleccionado, zonaSeleccionada;
	
	@PostConstruct
	public void init(){
		try {
			shapeDigitalizacion = new ShapeDigitalizacion();
	        coordenadasGeograficas = new ArrayList<CoordenadaDigitalizacion>();
	        coordinatesWrappersGeo = new ArrayList<CoordenadasPoligonos>();
			tiposFormas = proyectoLicenciamientoAmbientalFacade.getTiposFormas();
			poligono = getTipoForma(TipoForma.TIPO_FORMA_POLIGONO);
			try {
				plantillaCoordenadas = documentosFacade.descargarDocumentoPorNombre(Constantes.PLANTILLA_COORDENADAS_WGS);
				plantillaCoordenadasPsad = documentosFacade.descargarDocumentoPorNombre(Constantes.PLANTILLA_COORDENADAS_PSAD);
			} catch (Exception e) {
			}
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
	
	public void cargarCoordenadasOtroSistema(){
		List<ShapeDigitalizacion> objShape = shapeDigitalizacionFacade.obtenerShapePorSistema(autorizacionAdministrativa.getId(), 2, false);
		// cargo ubicaciones
		if(objShape != null && objShape.size() > 0){
			for (ShapeDigitalizacion shapeAux : objShape) {
				List<CoordenadaDigitalizacion> coordenadas = coordenadaDigitalizacionFacade.obtenerCoordenadas(shapeAux.getId());
				CoordenadasPoligonos coorP = new CoordenadasPoligonos();
				coorP.setCoordenadas(coordenadas);
				coorP.setTipoForma(shapeAux.getTipoForma());
				coordinatesWrappersGeo.add(coorP);
				shapeDigitalizacion = shapeAux;
			}
		}
	}

	private String coodenadasgeograficas="";
	public void handleFileUpload(final FileUploadEvent event, Integer tipoCoordenadas) {
		String tipoDeCoordenadas="";
		switch (tipoCoordenadas.toString()) {
		case "1":
			tipoDeCoordenadas="pu";
			break;
		case "2":
			tipoDeCoordenadas="li";
			break;
		case "3":
			tipoDeCoordenadas="po";
			break;

		default:
			break;
		}
        TipoForma formaAux = getTipoForma(tipoCoordenadas);
		shapeDigitalizacion.setTipoIngreso(2);// ingreso manual
		shapeDigitalizacion.setTipoForma(poligono);
		shapeDigitalizacion.setTipo(2);//coordenadas geograficas
		coodenadasgeograficas="";
        int rows = 0;
        List<String> listCoodenadasgeograficas = new ArrayList<String>();
        coordenadasGeograficas = new ArrayList<CoordenadaDigitalizacion>();
        List<CoordenadaDigitalizacion> coordenadasGeograficasAux  = new ArrayList<CoordenadaDigitalizacion>();
        coordinatesWrappersGeo = new ArrayList<CoordenadasPoligonos>();
        
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
                    //Integer poligonoActual = (int) cellIterator.next().getNumericCellValue();
                    coordenada.setAreaGeografica(1);
                    coordenada.setOrden((int) cellIterator.next().getNumericCellValue());
                    coordenada.setX(new BigDecimal(Double.valueOf(cellIterator.next().toString().replace("," , "."))).setScale(6, RoundingMode.HALF_DOWN));
                    coordenada.setY(new BigDecimal(Double.valueOf(cellIterator.next().toString().replace("," , "."))).setScale(6, RoundingMode.HALF_DOWN));
                    String sistemaRef = cellIterator.next().toString(); 
                    if(!sistemaRef.equals(sistemareferenciaSeleccionado)){
                    	JsfUtil.addMessageError("Error no corresponde al sistema de referencia indicado");
                    	return;
                    }
                    if(zonaSeleccionada != null){
	                    String zona = cellIterator.next().toString(); 
	                    if(!zona.equals(zonaSeleccionada)){
	                    	JsfUtil.addMessageError("Error no corresponde a la zona indicada");
	                    	return;
	                    }
                    }
                }
                rows++;
            }
            rowIterator = sheet.iterator();
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
                    CoordenadaDigitalizacion coordenada = new CoordenadaDigitalizacion();
                    //Integer poligonoActual = (int) cellIterator.next().getNumericCellValue();
                    coordenada.setAreaGeografica(1);
                    coordenada.setOrden((int) cellIterator.next().getNumericCellValue());
                    coordenada.setX(new BigDecimal(Double.valueOf(cellIterator.next().toString().replace("," , "."))).setScale(6, RoundingMode.HALF_DOWN));
                    coordenada.setY(new BigDecimal(Double.valueOf(cellIterator.next().toString().replace("," , "."))).setScale(6, RoundingMode.HALF_DOWN));
                    coordenada.setTipo(2);
                    coordenadasGeograficas.add(coordenada);
                	coodenadasgeograficas += (coodenadasgeograficas == "") ? coordenada.getX()+" "+coordenada.getY() : ","+coordenada.getX()+" "+coordenada.getY();
                	if (sheet.getLastRowNum() == rows) {
                		listCoodenadasgeograficas.add(coodenadasgeograficas);
						
						coordenadasGeograficasAux.add(coordenada);
						CoordenadasPoligonos coordinatesWrapper = new CoordenadasPoligonos(null, poligono);
		                coordinatesWrapper.getCoordenadas().addAll(coordenadasGeograficasAux);
		                coordinatesWrapper.setCadenaCoordenadas(coodenadasgeograficas);
		                coordinatesWrapper.setTipoForma(formaAux);
		                coordinatesWrappersGeo.add(coordinatesWrapper);
		                
		                coodenadasgeograficas = "";
		                coordenadasGeograficasAux = new ArrayList<>();
					}
                    
                    coordenadasGeograficasAux.add(coordenada);
                }
                rows++;
            }
            if(rows > 2 && tipoDeCoordenadas.equals("pu")){
            	JsfUtil.addMessageError("Error las coordenadas no corresponden a un punto.");
            	return;
            }
            if(coodenadasgeograficas != "") {
            	listCoodenadasgeograficas.add(coodenadasgeograficas);
            	
            	CoordenadasPoligonos coordinatesWrapper = new CoordenadasPoligonos(null, poligono);
                coordinatesWrapper.getCoordenadas().addAll(coordenadasGeograficasAux);
                coordinatesWrapper.setCadenaCoordenadas(coodenadasgeograficas);
                coordinatesWrapper.setTipoForma(formaAux);
                coordinatesWrappersGeo.add(coordinatesWrapper);
                coordenadasGeograficasAux = new ArrayList<>();
            }
            
            for(CoordenadasPoligonos item : coordinatesWrappersGeo) {
            	String coodenadasgeograficas = item.getCadenaCoordenadas();
            	
            	Intersecado_entrada poligono = new Intersecado_entrada();
                poligono.setU(Constantes.getUserWebServicesSnap());
                poligono.setIsla(Constantes.SIGLAS_INSTITUCION + "-RA");
                poligono.setTog(tipoDeCoordenadas);
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
                	if(tipoDeCoordenadas.equals("po")){
                    	Double areaPoligonoMetros = recuperarAreaPoligono(coodenadasgeograficas, coodenadasgeograficas);
                    	if(areaPoligonoMetros == null) {
                    		JsfUtil.addMessageError("Error a recuperar la ubicación del proyecto");
                    		coordenadasGeograficas = new ArrayList<CoordenadaDigitalizacion>();
                    		coordinatesWrappersGeo = new ArrayList<CoordenadasPoligonos>();
                    		return;
                    	}
                	}
                }
                catch (RemoteException e) {
                	JsfUtil.addMessageError("Error inesperado, comuníquese con mesa de ayuda");
                	coordenadasGeograficas = new ArrayList<CoordenadaDigitalizacion>();
                	coordinatesWrappersGeo = new ArrayList<CoordenadasPoligonos>();
                }
			}
        } catch (Exception e) {
        	JsfUtil.addMessageError("Error inesperado, comuníquese con mesa de ayuda");
            LOGGER.error("Error procesando el excel de coordenadas", e);
    		coordenadasGeograficas = new ArrayList<CoordenadaDigitalizacion>();
    		coordinatesWrappersGeo = new ArrayList<CoordenadasPoligonos>();
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
	
	public StreamedContent getPlantillaCoordenadasPsad() throws Exception {
		DefaultStreamedContent content = null;
		try {
			if (plantillaCoordenadasPsad != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(plantillaCoordenadasPsad));
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
}