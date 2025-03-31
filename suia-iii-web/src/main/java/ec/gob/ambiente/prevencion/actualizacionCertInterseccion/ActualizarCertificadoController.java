package ec.gob.ambiente.prevencion.actualizacionCertInterseccion;

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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
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
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.mapa.webservices.GenerarMapaIVCatWS_Service;
import ec.gob.ambiente.mapa.webservices.GenerarMapaSuiaIIIWS_Service;
import ec.gob.ambiente.mapa.webservices.ResponseCertificado;
import ec.gob.ambiente.prevencion.registro.proyecto.bean.VerProyectoBean;
import ec.gob.ambiente.rcoa.enums.CatalogoTipoCoaEnum;
import ec.gob.ambiente.rcoa.facade.CapasCoaFacade;
import ec.gob.ambiente.rcoa.facade.CatalogoCoaFacade;
import ec.gob.ambiente.rcoa.model.CapasCoa;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.preliminar.contoller.GenerarQRCertificadoInterseccion;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.certificadointerseccion.service.CertificadoInterseccionFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.CertificadoInterseccion;
import ec.gob.ambiente.suia.domain.Coordenada;
import ec.gob.ambiente.suia.domain.DetalleInterseccionProyecto;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.InterseccionProyecto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.TipoForma;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.ProyectoCustom;
import ec.gob.ambiente.suia.integracion.bean.ContenidoExterno;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.classes.CoordinatesWrapper;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.Proyecto4CategoriasFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoActualizacionCertificadoInterseccionFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.tipoforma.facade.TipoFormaFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;

@ManagedBean
@ViewScoped
public class ActualizarCertificadoController {
	
	private static final Logger LOG = Logger.getLogger(ActualizarCertificadoController.class);
	
	@Setter
	@Getter
	@ManagedProperty(value = "#{verProyectoBean}")
	private VerProyectoBean verProyectoBean;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{proyectoSuiaVerdeBean}")
    private ProyectoSuiaVerdeBean proyectoSuiaVerdeBean;
	
	@EJB
	private TipoFormaFacade tipoFormaFacade;	
	@EJB
	private UbicacionGeograficaFacade ubicacionfacade;	
	@EJB
	private CapasCoaFacade capasCoaFacade;	
	@EJB
	private  UbicacionGeograficaFacade ubicacionGeograficaFacade;
	@EJB
	private  DocumentosFacade documentosFacade;	
	@EJB
	private ProyectoActualizacionCertificadoInterseccionFacade proyectoActualizacionCIFacade;	
	@EJB
	private OrganizacionFacade organizacionFacade;	
	@EJB
	private CatalogoCoaFacade catalogoCoaFacade;	
	@EJB
	private PlantillaReporteFacade plantillaReporteFacade;	
	@EJB
    private AreaFacade areaFacade;	
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private CertificadoInterseccionFacade certificadoInterseccionFacade;
	@EJB
	protected ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
	@EJB
	private Proyecto4CategoriasFacade proyecto4CategoriasFacade;
	
	@Getter
	@Setter
	private List<UbicacionesGeografica> ubicacionesSeleccionadas;
	
	@Getter
	@Setter
	private List<CoordinatesWrapper> coordinatesWrappers;
	
	@Getter
	@Setter
	private List<CoordinatesWrapper> coordinatesWrappersGeo;
	
	@Getter
	@Setter
    private List<Coordenada> coordenadasGeograficas = new ArrayList<Coordenada>();
	
	private HashMap<String, Double> varUbicacionArea = new HashMap<String,Double>();
	private HashMap<String, Double> varUbicacionAux = new HashMap<String,Double>();
	
	private  HashMap<InterseccionProyecto, List<DetalleInterseccionProyecto>> capasIntersecciones= new HashMap<InterseccionProyecto, List<DetalleInterseccionProyecto>>();	
	private  HashMap<InterseccionProyecto, List<DetalleInterseccionProyecto>> listaCapasInterseccionPrincipal= new HashMap<InterseccionProyecto, List<DetalleInterseccionProyecto>>();
	private List<String> listaNombresCapasInterseca = new ArrayList<>();
	
	private UbicacionesGeografica ubicacionOficinaTecnica = new UbicacionesGeografica();
	
	@Getter
	@Setter
	private UbicacionesGeografica ubicacionPrincipal = new UbicacionesGeografica();
	
	@Getter
	@Setter
	private Documento  documentoMapa, documentoCertificado, documentoCoordenadasGeograficas, documentoCoordenadasImplantacion;
	
	private CertificadoInterseccion oficioCI;	
	
	private TipoForma poligono;
	
	private Area areaResponsableProyecto, areaEmiteCertificado;
	
	private Usuario usuarioAutoridad;
	
	private byte[] byteArchivoCI;
	
	private Integer nroActualizacion;
	
	@Getter
	@Setter
	private Boolean coordenadasSubidas, mostrarDetalleInterseccion, verCoordenadas;
	
	@Getter
	@Setter
	private String nombreArchivoCoordenadas, detalleInterseccionProyecto;
	
	@Getter
	@Setter
	private String codigoProyecto;
	
	private String coodenadasgeograficas="";
	
	private Boolean esHidrocarburos;
	
	private Integer tipoProyecto; //1 suia-iii, 2 4categorias
	
	private GenerarMapaSuiaIIIWS_Service wsMapasSuiaAzul;
	
	private GenerarMapaIVCatWS_Service wsMapasSuiaVerde;
	
	@PostConstruct
	public void inicio()
	{
		poligono = getTipoForma(TipoForma.TIPO_FORMA_POLIGONO);
		
		verCoordenadas = false;
		
		if(verProyectoBean.getProyecto() != null) {
			codigoProyecto = verProyectoBean.getProyecto().getCodigo();
			tipoProyecto = 1;
		} else if(proyectoSuiaVerdeBean.getProyecto() != null) {
			codigoProyecto = proyectoSuiaVerdeBean.getCodigo();
			tipoProyecto = 2;
		}
	}
	
	public void nueva() {
		coordenadasSubidas = false;
		nombreArchivoCoordenadas = null;
		ubicacionesSeleccionadas = new ArrayList<UbicacionesGeografica>();
		coordinatesWrappers = new ArrayList<CoordinatesWrapper>();

		documentoMapa = new Documento();
		documentoCoordenadasGeograficas = new Documento();
		documentoCoordenadasImplantacion = new Documento();
		documentoCertificado = new Documento();
		oficioCI = null;
		verCoordenadas = true;

		RequestContext.getCurrentInstance().update(":frmDialogs:pnlButtons");
	}
	
	public void cancelarActualizacion() {
		coordenadasSubidas = false;
        nombreArchivoCoordenadas = null;
		
		ubicacionesSeleccionadas= new ArrayList<UbicacionesGeografica>();
		coordinatesWrappers = new ArrayList<CoordinatesWrapper>();
		
		documentoMapa = new Documento();
		documentoCoordenadasGeograficas = new Documento();
		documentoCoordenadasImplantacion = new Documento();
		documentoCertificado = new Documento();
        oficioCI = null;
		
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('adjuntarCoordenadas').hide();");
	}
	
	public TipoForma getTipoForma(Integer id) {
		List<TipoForma> tiposFormas = tipoFormaFacade.listarTiposForma();

		for (TipoForma tf : tiposFormas) {
			if (tf.getId() == id)
				return tf;
		}
		return null;
	}
	
	public void handleFileUpload(final FileUploadEvent event) { 
		coodenadasgeograficas="";
		Integer poligonoAnterior = 0;
        int rows = 0;
        List<String> listCoodenadasgeograficas = new ArrayList<String>();
        coordenadasGeograficas = new ArrayList<Coordenada>();
        List<Coordenada> coordenadasGeograficasAux  = new ArrayList<Coordenada>();
        coordinatesWrappersGeo = new ArrayList<CoordinatesWrapper>();
        
        coordinatesWrappers = new ArrayList<CoordinatesWrapper>();
        ubicacionesSeleccionadas= new ArrayList<UbicacionesGeografica>();
		mostrarDetalleInterseccion = false;
		varUbicacionArea = new HashMap<String,Double>();
		
        try {
        	UploadedFile uploadedFileGeo = event.getFile();
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
                    Coordenada coordenada = new Coordenada();
                    Integer poligonoActual = (int) cellIterator.next().getNumericCellValue();
                    coordenada.setAreaGeografica(poligonoActual);
                    coordenada.setOrden((int) cellIterator.next().getNumericCellValue());
                    coordenada.setX(new Double(Double.valueOf(cellIterator.next().toString().replace("," , "."))));
                    coordenada.setY(new Double(Double.valueOf(cellIterator.next().toString().replace("," , "."))));
                    coordenada.setTipoCoordenada(2);
                    coordenadasGeograficas.add(coordenada);
                    
                    if (poligonoAnterior == 0) {
                    	poligonoAnterior = poligonoActual;
					}
                    if (poligonoAnterior == poligonoActual) {
                    	coodenadasgeograficas += (coodenadasgeograficas == "") ? coordenada.getX()+" "+coordenada.getY() : ","+coordenada.getX()+" "+coordenada.getY();
                    	if (sheet.getLastRowNum() == rows) {
                    		listCoodenadasgeograficas.add(coodenadasgeograficas);
    						
    						coordenadasGeograficasAux.add(coordenada);
    						CoordinatesWrapper coordinatesWrapper = new CoordinatesWrapper(null, poligono);
    		                coordinatesWrapper.getCoordenadas().addAll(coordenadasGeograficasAux);
    		                coordinatesWrapper.setCadenaCoordenadas(coodenadasgeograficas);
    		                coordinatesWrappersGeo.add(coordinatesWrapper);
    		                
    		                coodenadasgeograficas = "";
    		                coordenadasGeograficasAux = new ArrayList<>();
						}
					} else {
						listCoodenadasgeograficas.add(coodenadasgeograficas);
						CoordinatesWrapper coordinatesWrapper = new CoordinatesWrapper(null, poligono);
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
            	
            	CoordinatesWrapper coordinatesWrapper = new CoordinatesWrapper(null, poligono);
                coordinatesWrapper.getCoordenadas().addAll(coordenadasGeograficasAux);
                coordinatesWrapper.setCadenaCoordenadas(coodenadasgeograficas);
                coordinatesWrappersGeo.add(coordinatesWrapper);
                coordenadasGeograficasAux = new ArrayList<>();
            }
            
            for(CoordinatesWrapper item : coordinatesWrappersGeo) {
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
	            		coordenadasGeograficas = new ArrayList<Coordenada>();
	            		coordinatesWrappersGeo = new ArrayList<CoordinatesWrapper>();
	            		return;
	            	}            	
	            	if (intRest[0].getCapa()[0].getError() != null) {            		
	            		JsfUtil.addMessageError(intRest[0].getCapa()[0].getError().toString());
	            		System.out.println("Servicio no disponible ----> "+Constantes.getInterseccionesWS());
	            		coordenadasGeograficas = new ArrayList<Coordenada>();
	            		coordinatesWrappersGeo = new ArrayList<CoordinatesWrapper>();
	            		return;
	            	}
	            	
	            	Double areaPoligonoMetros = recuperarAreaPoligono(coodenadasgeograficas, coodenadasgeograficas);
                	Double areaPoligono = areaPoligonoMetros/10000;
                	
                	item.setSuperficie(new BigDecimal(areaPoligono));
                	recuperarParroquiasInterseccion(areaPoligono, intRest);
                	cargarUbicacionProyecto(varUbicacionAux);
                	ubicacionMasArea();
                	
	            }
	            catch (RemoteException e) {
	            	JsfUtil.addMessageError("Error inesperado, comuníquese con mesa de ayuda");
	            	coordenadasGeograficas = new ArrayList<Coordenada>();
	            	coordinatesWrappers = new ArrayList<CoordinatesWrapper>();
	            	coordinatesWrappersGeo = new ArrayList<CoordinatesWrapper>();
	            }
            }
            /***********************************************************************************************/
            capasIntersecciones= new HashMap<InterseccionProyecto, List<DetalleInterseccionProyecto>>();
            listaCapasInterseccionPrincipal= new HashMap<InterseccionProyecto, List<DetalleInterseccionProyecto>>();
            listaNombresCapasInterseca = new ArrayList<String>();
            
            for (String coodenadasgeograficas : listCoodenadasgeograficas) {                
            	verificarCapasInterseccion(coodenadasgeograficas);
            }
            /*********************************************************************************************************/
            
            //para mostrar msj interseccion al cargar las coordenadas
            getDetalleIntersecciones();
            RequestContext.getCurrentInstance().update("frmPreliminar:certificadoIntercepcionRcoa1");
            
            documentoCoordenadasGeograficas = new Documento();
            documentoCoordenadasGeograficas.setContenidoDocumento(uploadedFileGeo.getContents());
            documentoCoordenadasGeograficas.setNombreTabla(ProyectoLicenciamientoAmbiental.class.getSimpleName());
            documentoCoordenadasGeograficas.setIdTable(0);
            documentoCoordenadasGeograficas.setMime("application/vnd.ms-excel");
            documentoCoordenadasGeograficas.setExtesion(".xls");
            documentoCoordenadasGeograficas.setNombre("Coordenadas área geográfica actualizadas.xls");
            
        } catch (Exception e) {
        	JsfUtil.addMessageError("Error inesperado, comuníquese con mesa de ayuda");
            coordinatesWrappers = new ArrayList<CoordinatesWrapper>();
            ubicacionesSeleccionadas= new ArrayList<UbicacionesGeografica>();
    		coordenadasGeograficas = new ArrayList<Coordenada>();
    		coordinatesWrappersGeo = new ArrayList<CoordinatesWrapper>();
    		mostrarDetalleInterseccion = false;
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
	
	public void handleFileUploadImple(final FileUploadEvent event) {
		nroActualizacion = null;
		
		mostrarDetalleInterseccion = false;
		
        String coodenadas="";
        int rows = 0;
        coordinatesWrappers = new ArrayList<CoordinatesWrapper>();
        CoordinatesWrapper coordinatesWrapper = new CoordinatesWrapper();
        coordinatesWrappers.add(coordinatesWrapper);
        try {
        	UploadedFile uploadedFileImpl = event.getFile();
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
                    Coordenada coordenada = new Coordenada();
                    Integer poligonoActual = (int) cellIterator.next().getNumericCellValue();
                    coordenada.setAreaGeografica(poligonoActual);
                    coordenada.setOrden((int) cellIterator.next().getNumericCellValue());
                    coordenada.setX(new Double(Double.valueOf(cellIterator.next().toString().replace("," , "."))));
                    coordenada.setY(new Double(Double.valueOf(cellIterator.next().toString().replace("," , "."))));
                    coordenada.setTipoCoordenada(1);
                    if (coordinatesWrapper.getTipoForma() == null)
                        coordinatesWrapper.setTipoForma(poligono);                    
                    else if (!coordinatesWrapper.getTipoForma().equals(poligono) || coordinatesWrapper.getTipoForma().getId().intValue() == TipoForma.TIPO_FORMA_PUNTO || coordenada.getOrden() == 1) {
                        coordinatesWrapper = new CoordinatesWrapper(null, poligono);
                        coordinatesWrappers.add(coordinatesWrapper);
                    }
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
                		coordinatesWrappers = new ArrayList<CoordinatesWrapper>();
                	}
                	else
                	{
                		coodenadasgeograficas = "";
                		for (Coordenada coordenada : coordenadasGeograficas) {
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
                			coordinatesWrappers = new ArrayList<CoordinatesWrapper>();
                		}
                		else
                		{
                			if (intRestGeoImpl[0].getContieneCapa().getValor().equals("f"))
                			{
                				JsfUtil.addMessageError("Polígono no se encuentra dentro de la ubicación geográfica");
                            	coordinatesWrappers = new ArrayList<CoordinatesWrapper>();
	                			mostrarDetalleInterseccion = false;    
                			}
                			else
                			{	
                				coordinatesWrappers.get(i).setSuperficie(new BigDecimal(Double.valueOf(intRestGeoImpl[0].getValorArea().getArea())/10000));
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
                	
                	coordinatesWrappers = new ArrayList<CoordinatesWrapper>();
                	mostrarDetalleInterseccion = false;
                }                                
            }
            if(coordinatesWrappers.size()>0)
            {
            	if(tipoProyecto.equals(1))
            		actualizarInformacionSuiaAzul();
            	else
            		actualizarInformacionSuiaVerde();
            	
            	documentoCoordenadasImplantacion = new Documento();
            	documentoCoordenadasImplantacion.setContenidoDocumento(uploadedFileImpl.getContents());
            	documentoCoordenadasImplantacion.setNombreTabla(ProyectoLicenciamientoAmbiental.class.getSimpleName());
            	documentoCoordenadasImplantacion.setIdTable(0);
            	documentoCoordenadasImplantacion.setMime("application/vnd.ms-excel");
            	documentoCoordenadasImplantacion.setExtesion(".xls");
            	documentoCoordenadasImplantacion.setNombre("Coordenadas área de implantación actualizados.xls");
            }
        } catch (Exception e) {
        	JsfUtil.addMessageError("Error inesperado, comuníquese con mesa de ayuda");
        	e.printStackTrace();
            
            coordinatesWrappers = new ArrayList<CoordinatesWrapper>();
            mostrarDetalleInterseccion = false;
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
			
			if (intRestCapaImpl[0].getInformacion().getError() != null) {            		
        		JsfUtil.addMessageError(intRestCapaImpl[0].getInformacion().getError().toString());
        		return;
        	}
			
			InterseccionProyecto capaInterseccion = new InterseccionProyecto();	                			
			List<DetalleInterseccionProyecto> listaIntersecciones = new ArrayList<DetalleInterseccionProyecto>();
			
			for (IntersecadoCapa_capa capas : intRestCapaImpl[0].getCapa()) {
				if(Integer.valueOf(capas.getNum())>0)
				{
					capaInterseccion = new InterseccionProyecto();
					
					if(capas.getNombre().equals("OFICINAS_TECNICAS"))
					{
						for(Campos_coordenada oficiona : capas.getCampos())
						{
							System.out.println("oficina tecnica:"+ oficiona.getFcode());
							ubicacionOficinaTecnica=ubicacionfacade.buscarUbicacionPorCodigoInec(oficiona.getFcode());
							break;
						}
					}
					else
					{		                						
						if(!capas.getNombre().equals("MAR TERRITORIAL") 
								&& !capas.getNombre().equals("COMPONENTE SOCIAL")
								&& !capas.getNombre().equals("COMPONENTE ACODIGA MARITIMA")
								&& !capas.getNombre().equals("COMPONENTE ACOGIDA"))
						{
	//						System.out.println("CAPA:::"+capas.getNombre());
							capaInterseccion.setCapaCoa(getCapa(capas.getNombre()));
							if(tipoProyecto.equals(1))
    							capaInterseccion.setProyecto(verProyectoBean.getProyecto());
    						else 
    							capaInterseccion.setIdCuatroCategorias(proyectoSuiaVerdeBean.getCodigo());
							capaInterseccion.setDescripcion(capas.getNombre());
							
							DetalleInterseccionProyecto detallecapa = new DetalleInterseccionProyecto();
							listaIntersecciones = new ArrayList<DetalleInterseccionProyecto>();
							for(Campos_coordenada interseccion : capas.getCampos())
							{
								detallecapa = new DetalleInterseccionProyecto();
								if(interseccion.getTphmd()!=null)
								{
									detallecapa.setNombre(interseccion.getTphmd());
									detallecapa.setIdGeometria(Integer.valueOf(interseccion.getGid()));
								}
								if(interseccion.getTpcnv()!=null)
								{	                							
									detallecapa.setNombre(interseccion.getTpcnv());
									detallecapa.setIdGeometria(Integer.valueOf(interseccion.getGid()));
								}
								if(interseccion.getNam()!=null)
								{	                							
									String nombreGeom = interseccion.getNam();
									if(capas.getNombre().equals("SNAP")) {
										nombreGeom = interseccion.getMap() + " " + interseccion.getNam();
									}
									detallecapa.setNombre(nombreGeom);
									detallecapa.setIdGeometria(Integer.valueOf(interseccion.getGid()));
								}
								if(interseccion.getNameco()!=null)
								{
									detallecapa.setNombre(interseccion.getNameco());
									detallecapa.setIdGeometria(Integer.valueOf(interseccion.getGid()));
								}
								if(interseccion.getCtn2()!=null)
								{
									detallecapa.setNombre(interseccion.getCtn2());
									detallecapa.setIdGeometria(Integer.valueOf(interseccion.getGid()));
								}
								if(interseccion.getFcode()!=null)
								{
									if(capas.getNombre().equals("SNAP"))
										detallecapa.setCodigoUnicoCapa(interseccion.getFcode());
									else
										detallecapa.setCodigoConvenio(interseccion.getFcode());
								}
								if(capas.getNombre().equals("LIMITE INTERNO 20 KM"))
								{
									detallecapa.setNombre("SI");
									detallecapa.setIdGeometria(Integer.valueOf(interseccion.getGid()));
								}
								listaIntersecciones.add(detallecapa);
							}
							capasIntersecciones.put(capaInterseccion, listaIntersecciones);
	
							if(capaInterseccion.getCapaCoa() != null && (capaInterseccion.getCapaCoa().getId().equals(Constantes.ID_CAPA_SNAP) || capaInterseccion.getCapaCoa().getId().equals(Constantes.ID_CAPA_BP) || capaInterseccion.getCapaCoa().getId().equals(Constantes.ID_CAPA_PFE))) {
								List<DetalleInterseccionProyecto> listaInterseccionesAux = new ArrayList<DetalleInterseccionProyecto>();
								for (DetalleInterseccionProyecto detalle : listaIntersecciones) {
									String capaInterseca = capaInterseccion.getCapaCoa().getId() + "- " + detalle.getIdGeometria();
									
									if(!listaNombresCapasInterseca.contains(capaInterseca)) {
										listaNombresCapasInterseca.add(capaInterseca);
										listaInterseccionesAux.add(detalle);
									}
								}
								
								if(listaInterseccionesAux.size() > 0) {
									Iterator<Entry<InterseccionProyecto, List<DetalleInterseccionProyecto>>> it = listaCapasInterseccionPrincipal.entrySet().iterator();
								    while (it.hasNext())
								    {
								       Entry<InterseccionProyecto, List<DetalleInterseccionProyecto>> item = it.next();
								       if(item.getKey().getCapaCoa().getId().equals(capaInterseccion.getCapaCoa().getId())) {
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
		} catch (RemoteException e) {
             	JsfUtil.addMessageError("Error inesperado, comuníquese con mesa de ayuda");
             	System.out.println("Servicio no disponible ----> "+Constantes.getInterseccionesWS());
             	
             	ubicacionesSeleccionadas= new ArrayList<UbicacionesGeografica>();
             	coordinatesWrappers = new ArrayList<CoordinatesWrapper>();
        		coordenadasGeograficas = new ArrayList<Coordenada>();
        		coordinatesWrappersGeo = new ArrayList<CoordinatesWrapper>();
             	mostrarDetalleInterseccion = false;
             }
	}
	
	public void getDetalleIntersecciones()
	{	
		mostrarDetalleInterseccion = false;
		detalleInterseccionProyecto = "";
		
		HashMap<InterseccionProyecto, List<DetalleInterseccionProyecto>> lista =  listaCapasInterseccionPrincipal;
		
		if(lista.size()>0)
		{
			detalleInterseccionProyecto = "Su proyecto obra o actividad interseca con: ";
			detalleInterseccionProyecto = detalleInterseccionProyecto + "<ul>";
			String nombreCapa = "";
			for (InterseccionProyecto i : lista.keySet()) {
				nombreCapa = i.getDescripcion();
				String nombreInterseccion = "";
				for (DetalleInterseccionProyecto j : lista.get(i)) {
					nombreInterseccion += (nombreInterseccion == "") ? j.getNombre() : "," + j.getNombre();
				}
				detalleInterseccionProyecto = detalleInterseccionProyecto + "<li>" + nombreCapa + ": " + nombreInterseccion + "</li>";
			}
			detalleInterseccionProyecto = detalleInterseccionProyecto + "</ul>";
		}
		
		if(detalleInterseccionProyecto != "")
			mostrarDetalleInterseccion = true;
	}
	
	public CapasCoa getCapa(String capa) {
		List<CapasCoa> capas = capasCoaFacade.listaCapas();
		
        for (CapasCoa c : capas) {
            if (c.getAbreviacion().equals(capa))
                return c;
        }
        return null;
    }
	
	public void cargarUbicacionProyecto(HashMap<String, Double> parroquia){
		ubicacionesSeleccionadas= new ArrayList<UbicacionesGeografica>();

		Iterator<Entry<String, Double>> it = parroquia.entrySet().iterator();
		String inec="";
		
		while (it.hasNext()) {
			Map.Entry<String, Double> e = (Entry<String, Double>) it.next();
			inec  = e.getKey();
			if (!inec.equals("")) {
				List<UbicacionesGeografica> lista = ubicacionGeograficaFacade.buscarUbicacionGeograficaPorParroquia(inec);
				ubicacionesSeleccionadas.addAll(lista);
			}
		}
	}
	
	public void ubicacionMasArea()
	{	
		Iterator<Entry<String, Double>> it = varUbicacionArea.entrySet().iterator();
		double orden=0;
		String inec="";
		String inecMayor = "";
		double maxArea = 0;
		while (it.hasNext()) {
			Map.Entry<String, Double> e = (Entry<String, Double>) it.next();
			inec  = e.getKey();
			orden = e.getValue().doubleValue();
			if (orden > maxArea) {
				maxArea = orden;
				inecMayor = inec;
			}
		}
		try
		{
			ubicacionPrincipal=ubicacionfacade.buscarUbicacionPorCodigoInec(inecMayor);			
		} catch(Exception e)
		{
			ubicacionesSeleccionadas= new ArrayList<UbicacionesGeografica>();
			System.out.println("geographical_locations --> codeInec:::: " +inec+ " ::::: no existe para la ubicacionPrincipal");
		}
	}
		
	public void actualizarInformacionSuiaAzul() {
		try{
			try {			
				wsMapasSuiaAzul = new GenerarMapaSuiaIIIWS_Service(new URL(Constantes.getActualizarMapaSuiaWS()));
			} catch (Exception e) {
				System.out.println("Servicio no disponible ---> "+Constantes.getActualizarMapaSuiaWS());
				JsfUtil.addMessageError("Servicio mapa no disponible. Por favor intente más tarde.");
				return;
			} 
			
			Area areaTramite = verProyectoBean.getProyecto().getAreaResponsable();
			if(areaTramite.getTipoArea().getSiglas().equals("DP") && 
	    			!areaTramite.getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS))
				areaResponsableProyecto = areaFacade.getAreaCoordinacionZonal(ubicacionOficinaTecnica);
			else 
				areaResponsableProyecto = areaTramite;
			
			String tipoRol = "role.ci.cz.autoridad";
			
			UbicacionesGeografica provincia = ubicacionPrincipal.getUbicacionesGeografica().getUbicacionesGeografica();
			if(provincia.getCodificacionInec().equals(Constantes.CODIGO_INEC_GALAPAGOS) 
					|| ubicacionPrincipal.getNombre().toUpperCase().contains("INSULAR"))
				areaEmiteCertificado = areaFacade.getArea(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS);
			else if(areaResponsableProyecto.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
				tipoRol = "role.ci.pc.autoridad";
				areaEmiteCertificado = areaFacade.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL);
			}else if(ubicacionOficinaTecnica != null && ubicacionOficinaTecnica.getId() != null) {
				areaEmiteCertificado = areaFacade.getAreaCoordinacionZonal(ubicacionOficinaTecnica);
				if(areaEmiteCertificado.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_OT))
					areaEmiteCertificado = areaEmiteCertificado.getArea();
			}
			
			if(areaEmiteCertificado == null || areaEmiteCertificado.getId() == null) {
				JsfUtil.addMessageError("Ocurrió un error al actualizar la información.");
				return;
			}
			
			String rolAutoridad = Constantes.getRoleAreaName(tipoRol);
			
			List<Usuario>uList = usuarioFacade.buscarUsuariosPorRolYArea(rolAutoridad, areaEmiteCertificado);
			if(uList==null || uList.size()==0)			
			{
				JsfUtil.addMessageError("Ocurrió un error al actualizar la información.");
				return;
			}else{
				usuarioAutoridad=uList.get(0);
			}
			
			//guardar coordenadas para generar el mapa
			if(nroActualizacion == null || nroActualizacion <= 0) {
				nroActualizacion = proyectoActualizacionCIFacade.guardarCoordenadasActualizadas(verProyectoBean.getProyecto(),
						ubicacionesSeleccionadas, coordinatesWrappers, coordinatesWrappersGeo);
				
				if(capasIntersecciones.size() > 0) 
					proyectoActualizacionCIFacade.guardarInterseccionesActualizacion(nroActualizacion, capasIntersecciones);
				
				//verificar si el proyecto es de hidrocarburos
				ProyectoCustom proyectoCustom = new ProyectoCustom();
				proyectoCustom.setCodigo(codigoProyecto);
				esHidrocarburos =JsfUtil.getBean(ContenidoExterno.class).esHidrocarburos(proyectoCustom);
			}
			
			if(nroActualizacion != null) {				
				//generar el nuevo certificado de intersección
				documentoCertificado = new Documento();
				generarCertificadoInterseccion(true);
				
				documentoMapa = new Documento();
				Boolean resultMapa = generarMapa();
				if(!resultMapa) {
					JsfUtil.addMessageError("Ocurrió un error al generar el mapa.");
					return;
				}
				
				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('adjuntarCoordenadas').hide();");
				context.execute("PF('docActualizados').show();");
			}
		} catch (Exception ex) {
			LOG.error("Error al generar los nuevos documentos.", ex);
			JsfUtil.addMessageError("Ocurrió un error al generar los documentos.");
		}
	}
	
	public Boolean generarMapa() {
		ResponseCertificado resCer = new ResponseCertificado();
		resCer=wsMapasSuiaAzul.getGenerarMapaSuiaIIIWSPort().generarCertificadoInterseccionSuiaIII(codigoProyecto, nroActualizacion);
		if(resCer.getWorkspaceAlfresco()==null)
		{
			return false;	
		}
		else
		{
			TipoDocumento tipoDocumento = new TipoDocumento();
			tipoDocumento.setId(TipoDocumentoSistema.TIPO_CI_MAPA_ACTUALIZACION.getIdTipoDocumento());
			documentoMapa = new Documento();
			documentoMapa.setIdAlfresco(resCer.getWorkspaceAlfresco());
			documentoMapa.setExtesion(".pdf");		
			documentoMapa.setMime("application/pdf");
			documentoMapa.setTipoDocumento(tipoDocumento);
			documentoMapa.setNombreTabla(ProyectoLicenciamientoAmbiental.class.getSimpleName());
			documentoMapa.setNombre("Mapa del certificado de interseccion actualizado.pdf");
			documentoMapa.setIdTable(verProyectoBean.getProyecto().getId());

		}
		
		return true;
	}
	
	public void generarCertificadoInterseccion(Boolean marcaAgua) throws Exception {
		String nombreReporte= "Oficio del certificado de interseccion actualizado";
		
		oficioCI = certificadoInterseccionFacade.getCertificadoInterseccionActualizacion(verProyectoBean.getProyecto().getId(), nroActualizacion);
		if(oficioCI == null || oficioCI.getId() == null) {
			CertificadoInterseccion ci = new CertificadoInterseccion();
			ci.setProyectoLicenciamientoAmbiental(verProyectoBean.getProyecto());
			ci.setNroActualizacion(nroActualizacion);
			ci.setUsuarioFirma(usuarioAutoridad.getNombre());
			ci.setAreaUsuarioFirma(areaEmiteCertificado.getId());
			oficioCI = certificadoInterseccionFacade.guardarActualizacionCertificadoInterseccion(ci);
		} else {
			oficioCI.setUsuarioFirma(usuarioAutoridad.getNombre());
			oficioCI.setAreaUsuarioFirma(areaEmiteCertificado.getId());
			certificadoInterseccionFacade.guardarActualizacionCertificadoInterseccion(oficioCI);
		}
		
		PlantillaReporte plantillaReporte = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.TIPO_CI_OFICIO_ACTUALIZACION);
		
		EntityActualizacionCertificadoInters entityInforme =cargarDatosCertificado(marcaAgua);
		
		File informePdfAux = UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(),nombreReporte, true, entityInforme);
		
		File informePdf = JsfUtil.fileMarcaAgua(informePdfAux, marcaAgua ? " - - BORRADOR - - " : " ", BaseColor.GRAY);

		Path path = Paths.get(informePdf.getAbsolutePath());
		String reporteHtmlfinal = informePdf.getName();
		byteArchivoCI = Files.readAllBytes(path);
		File fileArchivo = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
		FileOutputStream file = new FileOutputStream(fileArchivo);
		file.write(Files.readAllBytes(path));
		file.close();
		
		Path pathQr = Paths.get(entityInforme.getCodigoQrFirma());
		Files.delete(pathQr);
		
		certificadoInterseccionFacade.guardarCertificadoInterseccion(oficioCI);
		
    	documentoCertificado.setContenidoDocumento(byteArchivoCI);
    	documentoCertificado.setExtesion(".pdf");		
    	documentoCertificado.setMime("application/pdf");
    	documentoCertificado.setIdTable(verProyectoBean.getProyecto().getId());	       		
    	documentoCertificado.setNombreTabla(ProyectoLicenciamientoAmbiental.class.getSimpleName());
    	documentoCertificado.setNombre(nombreReporte + ".pdf");
    	
	}
	
	public EntityActualizacionCertificadoInters cargarDatosCertificado(Boolean marcaAgua) throws Exception {
		
		DateFormat formatoFecha = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));
		String fechaEmision = formatoFecha.format(new Date());
		
		EntityActualizacionCertificadoInters entity = new EntityActualizacionCertificadoInters();
		
		String nombreOperador = "";
		String cedulaOperador = "";
		String razonSocial = "";
		String nombreProyecto = "";
		
		if(tipoProyecto.equals(1)) {
			Usuario uOperador = verProyectoBean.getProyecto().getUsuario();
			Organizacion orga = organizacionFacade.buscarPorRuc(uOperador.getNombre());
			nombreOperador = uOperador.getPersona().getNombre();
			cedulaOperador = uOperador.getPersona().getPin();
			razonSocial = orga == null ? " " : orga.getNombre();
			nombreProyecto = verProyectoBean.getProyecto().getNombre();
		} else if(tipoProyecto.equals(2)) {
			Usuario uOperador = JsfUtil.getLoggedUser();
			Organizacion orga = organizacionFacade.buscarPorRuc(uOperador.getNombre());
			nombreOperador = uOperador.getPersona().getNombre();
			cedulaOperador = uOperador.getPersona().getPin();
			razonSocial = orga == null ? " " : orga.getNombre();
			nombreProyecto = proyectoSuiaVerdeBean.getNombre();
		}
		
		//generacion Ubicacion
		String strTableUbicacion = "<center><table border=\"1\" cellpadding=\"5\" cellspacing=\"0\" class=\"w600Table\" style=\"width: 100%;border-collapse:collapse;font-size:12px;font-family: arial,helvetica,sans-serif;\">"
				+ "<tbody><tr BGCOLOR=\"#B2B2B2\">"			
				+ "<td><strong>Provincia</strong></td>"
				+ "<td><strong>Cantón</strong></td>"
				+ "<td><strong>Parroquia</strong></td>"
				+ "</tr>";
		
		for (UbicacionesGeografica ubicacion : ubicacionesSeleccionadas) {
			strTableUbicacion += "<tr>";
			strTableUbicacion += "<td>" + ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre() + "</td>";
			strTableUbicacion += "<td>" + ubicacion.getUbicacionesGeografica().getNombre() + "</td>";
			strTableUbicacion += "<td>" + ubicacion.getNombre() + "</td>";
			strTableUbicacion += "</tr>";
		}		
		strTableUbicacion += "</tbody></table></center>";
		
		//generacion interseccion
		List<String> detalleIntersecaCapasViabilidad=new ArrayList<String>();
		List<String> detalleIntersecaOtrasCapas=new ArrayList<String>();
		
		for (InterseccionProyecto i : capasIntersecciones.keySet()) {
			if(i.getCapaCoa() != null) {
				for(DetalleInterseccionProyecto detalleInterseccion : capasIntersecciones.get(i))
				{
					String capaDetalle=i.getCapaCoa().getNombre()+": "+detalleInterseccion.getNombre();
					if(i.getCapaCoa().getViabilidad()){
						if(!detalleIntersecaCapasViabilidad.contains(capaDetalle))
							detalleIntersecaCapasViabilidad.add(capaDetalle);
					}else{				
						if(!detalleIntersecaOtrasCapas.contains(capaDetalle))
							detalleIntersecaOtrasCapas.add(capaDetalle);
					}
				}
			}
		}
		
		
		String strTableIntersecaViabilidad = "";		
		for (String detalleInterseca : detalleIntersecaCapasViabilidad) {			
			strTableIntersecaViabilidad += detalleInterseca+"<br/>";			
		}
		
		String strTableOtrasIntersecciones = "";		
		for (String detalleInterseca : detalleIntersecaOtrasCapas) {			
			strTableOtrasIntersecciones += detalleInterseca+"<br/>";			
		}
		
		//generacion Capas
		List<CapasCoa> capasCoaLista = capasCoaFacade.listaCapasCertificadoInterseccion();
		String strTableCapas = "";
		for (CapasCoa capa : capasCoaLista) {			
			String fecha = "ND";
			if(capa.getFechaActualizacionCapa() != null)
				fecha = JsfUtil.getSimpleDateFormat(capa.getFechaActualizacionCapa());
			strTableCapas += capa.getNombre() + " (" + fecha + ")<br/>";			
		}
		
		//capas CONALI
		List<CatalogoGeneralCoa> listaCapas =catalogoCoaFacade.obtenerCatalogo(CatalogoTipoCoaEnum.COA_CAPAS_EXTERNAS_CONALI);
		String strTableCapaCONALI = "";
		for (CatalogoGeneralCoa catalogoCapas : listaCapas) {		
			strTableCapaCONALI += catalogoCapas.getDescripcion() + "<br/>";
		}
		
		String mostrarViabilidad = "display: none";
		if(listaCapasInterseccionPrincipal != null && listaCapasInterseccionPrincipal.size() > 0)
			mostrarViabilidad =  "display: inline";
		
		String noMostrarViabilidad = (mostrarViabilidad.contains("inline")) ? "display: none" :  "display: inline";
		
		String mostrarOtras = (strTableOtrasIntersecciones.equals("")) ? "display: none" :  "display: inline";
		
		entity.setNumeroOficio(oficioCI.getCodigo());
		entity.setUbicacion(usuarioAutoridad.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
		entity.setFechaEmision(fechaEmision);
		entity.setRazonSocial(razonSocial);
		entity.setNombreOperador(nombreOperador);
		entity.setCedulaOperador(cedulaOperador);
		entity.setNombreProyecto(nombreProyecto);
		entity.setCodigoProyecto(codigoProyecto);
		entity.setUbicacionProyecto(strTableUbicacion);
		entity.setAreaResponsable(areaResponsableProyecto.getAreaName());
		entity.setDetalleIntersecaCapasViabilidad(strTableIntersecaViabilidad);
		entity.setDetalleIntersecaOtrasCapas(strTableOtrasIntersecciones);
		entity.setActualizacionCapas(strTableCapas);
		entity.setCapasCONALI(strTableCapaCONALI);
		entity.setMostarIntersecaViabilidad(mostrarViabilidad);
		entity.setMostarNoIntersecaViabilidad(noMostrarViabilidad);
		entity.setMostarIntersecaOtrasCapas(mostrarOtras);
		entity.setNombreAutoridad(usuarioAutoridad.getPersona().getNombre());
		entity.setAreaAutoridad(areaEmiteCertificado.getAreaName());
		
		List<String> resultadoQr = GenerarQRCertificadoInterseccion.getCodigoQrUrl(!marcaAgua, 
				entity.getCodigoProyecto(), entity.getNumeroOficio(),
				tipoProyecto, nroActualizacion);
		
		entity.setCodigoQrFirma(resultadoQr.get(1));
		oficioCI.setUrlCodigoValidacion(resultadoQr.get(0));
		
		return entity;
	}
	
	public void actualizarInformacionSuiaVerde() throws Exception {
			try {			
				wsMapasSuiaVerde = new GenerarMapaIVCatWS_Service(new URL(Constantes.getActualizarMapaIvCatWS()));
			} catch (Exception e) {
				System.out.println("Servicio no disponible ---> "+Constantes.getActualizarMapaIvCatWS());
				JsfUtil.addMessageError("Servicio mapa no disponible. Por favor intente más tarde.");
				ubicacionesSeleccionadas= new ArrayList<UbicacionesGeografica>();
	            coordinatesWrappers = new ArrayList<CoordinatesWrapper>();
	            mostrarDetalleInterseccion = false;
				return;
			} 
			
			String tipoRol = "role.ci.cz.autoridad";
			
			UbicacionesGeografica provincia = ubicacionPrincipal.getUbicacionesGeografica().getUbicacionesGeografica();
			if(provincia.getCodificacionInec().equals(Constantes.CODIGO_INEC_GALAPAGOS) 
					|| ubicacionPrincipal.getNombre().toUpperCase().contains("INSULAR"))
				areaResponsableProyecto = areaFacade.getArea(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS);
			else 
				areaResponsableProyecto = areaFacade.getAreaCoordinacionZonal(ubicacionOficinaTecnica);
			
			Boolean esEstrategico = (Boolean) proyectoSuiaVerdeBean.getProyecto()[10];
			if(esEstrategico) {
				areaResponsableProyecto = areaFacade.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL);
				tipoRol = "role.ci.pc.autoridad";
			} else if(proyectoSuiaVerdeBean.getProyecto()[10].toString().equals("GALAPAGOS") 
					|| areaResponsableProyecto.getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)) {
				areaResponsableProyecto = areaFacade.getArea(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS);
				tipoRol = "role.ci.galapagos.autoridad";
			}
			
			areaEmiteCertificado = areaResponsableProyecto;
			
			if(areaResponsableProyecto.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_OT))
				areaEmiteCertificado = areaResponsableProyecto.getArea();
			
			String rolAutoridad = Constantes.getRoleAreaName(tipoRol);
			
			List<Usuario>uList = usuarioFacade.buscarUsuariosPorRolYArea(rolAutoridad, areaEmiteCertificado);
			if(uList==null || uList.size()==0)			
			{
				JsfUtil.addMessageError("Ocurrió un error al actualizar la información.");
				return;
			}else{
				usuarioAutoridad=uList.get(0);
			}
			
			//guardar coordenadas para generar el mapa
			if(nroActualizacion == null || nroActualizacion <= 0) {
				nroActualizacion = proyectoActualizacionCIFacade.guardarCoordenadasActualizadasSuiaVerde(codigoProyecto,
						ubicacionesSeleccionadas, coordinatesWrappers, coordinatesWrappersGeo);
				
				if(capasIntersecciones.size() > 0) 
					proyectoActualizacionCIFacade.guardarInterseccionesActualizacionSuiaVerde(nroActualizacion, capasIntersecciones);
			}
			
			if(nroActualizacion != null) {				
				//generar el nuevo certificado de intersección
				documentoCertificado = new Documento();
				generarCertificadoInterseccionSuiaVerde(true);
				
				documentoMapa = new Documento();
				Boolean resultMapa = generarMapaSuiaVerde();
				if(!resultMapa) {
					JsfUtil.addMessageError("Ocurrió un error al generar el mapa.");
					return;
				}
				
				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('adjuntarCoordenadas').hide();");
				context.execute("PF('docActualizados').show();");
			}
		
	}
	
	public void generarCertificadoInterseccionSuiaVerde(Boolean marcaAgua) throws Exception {
		String nombreReporte= "Oficio del certificado de interseccion actualizado";
		
		oficioCI = certificadoInterseccionFacade.getCertificadoInterseccionActualizacionSuiaVerde(codigoProyecto, nroActualizacion);
		if(oficioCI == null || oficioCI.getId() == null) {
			CertificadoInterseccion ci = new CertificadoInterseccion();
			ci.setIdCuatroCategorias(codigoProyecto);
			ci.setNroActualizacion(nroActualizacion);
			ci.setUsuarioFirma(usuarioAutoridad.getNombre());
			ci.setAreaUsuarioFirma(areaEmiteCertificado.getId());
			oficioCI = certificadoInterseccionFacade.guardarActualizacionCertificadoInterseccion(ci);
		} else {
			oficioCI.setUsuarioFirma(usuarioAutoridad.getNombre());
			oficioCI.setAreaUsuarioFirma(areaEmiteCertificado.getId());
			certificadoInterseccionFacade.guardarActualizacionCertificadoInterseccion(oficioCI);
		}
		
		PlantillaReporte plantillaReporte = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.TIPO_CI_OFICIO_ACTUALIZACION);
		
		EntityActualizacionCertificadoInters entityInforme =cargarDatosCertificado(marcaAgua);
		
		File informePdfAux = UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(),nombreReporte, true, entityInforme);
		
		File informePdf = JsfUtil.fileMarcaAgua(informePdfAux, marcaAgua ? " - - BORRADOR - - " : " ", BaseColor.GRAY);

		Path path = Paths.get(informePdf.getAbsolutePath());
		String reporteHtmlfinal = informePdf.getName();
		byteArchivoCI = Files.readAllBytes(path);
		File fileArchivo = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
		FileOutputStream file = new FileOutputStream(fileArchivo);
		file.write(Files.readAllBytes(path));
		file.close();
		
		Path pathQr = Paths.get(entityInforme.getCodigoQrFirma());
		Files.delete(pathQr);
		
		certificadoInterseccionFacade.guardarCertificadoInterseccion(oficioCI);
        
    	documentoCertificado.setContenidoDocumento(byteArchivoCI);
    	documentoCertificado.setExtesion(".pdf");		
    	documentoCertificado.setMime("application/pdf");
    	documentoCertificado.setNombre(nombreReporte + ".pdf");
    	
	}
	
	public Boolean generarMapaSuiaVerde() {
		ResponseCertificado resCer = new ResponseCertificado();
		resCer = wsMapasSuiaVerde.getGenerarMapaIVCatWSPort().generarCertificadoInterseccionIVCat(codigoProyecto, proyectoSuiaVerdeBean.getNombre(), nroActualizacion);
		if (resCer.getWorkspaceAlfresco() == null) {
			return false;
		} else {
			TipoDocumento tipoDocumento = new TipoDocumento();
			tipoDocumento.setId(TipoDocumentoSistema.TIPO_CI_MAPA_ACTUALIZACION.getIdTipoDocumento());
			documentoMapa = new Documento();
			documentoMapa.setIdAlfresco(resCer.getWorkspaceAlfresco());
			documentoMapa.setExtesion(".pdf");		
			documentoMapa.setMime("application/pdf");
			documentoMapa.setTipoDocumento(tipoDocumento);
			documentoMapa.setNombre("Mapa del certificado de interseccion actualizado.pdf");
		}
		
		return true;
	}
	
	public StreamedContent descargarMapa() throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
				
			if (documentoMapa != null && documentoMapa.getIdAlfresco() != null) {				
				File fileAux = documentosFacade.descargarFileAnterior(documentoMapa.getIdAlfresco());
				File fileDoc = JsfUtil.fileMarcaAguaOverH(fileAux, " - - BORRADOR - - ", BaseColor.GRAY);
				Path path = Paths.get(fileDoc.getAbsolutePath());
				documentoContent = Files.readAllBytes(path);
			}
			
			if (documentoMapa != null && documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documentoMapa.getNombre());
			} 
			else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
			
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public StreamedContent descargarCertificadoInterseccion() throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			
			if (documentoCertificado != null && documentoCertificado.getContenidoDocumento() != null) {
				documentoContent = byteArchivoCI;
			} else if (documentoCertificado != null && documentoCertificado.getIdAlfresco() != null) {
				documentoContent = documentosFacade.descargar(documentoCertificado.getIdAlfresco());
			}
			
			if (documentoCertificado != null && documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documentoCertificado.getNombre());
			} 
			else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
			
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void guardarDocumentos() {
		try {
			
			//generar el nuevo certificado de intersección
			documentoCertificado = new Documento();
			generarCertificadoInterseccion(false);
			
			documentoMapa = new Documento();
			Boolean resultMapa = generarMapa();
			if(!resultMapa) {
				JsfUtil.addMessageError("Ocurrió un guadar el documento del mapa.");
				return;
			}
			
			if (documentoCoordenadasGeograficas != null && documentoCoordenadasGeograficas.getContenidoDocumento() != null) {
				documentoCoordenadasGeograficas.setIdTable(verProyectoBean.getProyecto().getId());
				documentoCoordenadasGeograficas.setDescripcion("Coordenadas_Actualizadas_" + nroActualizacion);
				documentoCoordenadasGeograficas.setEstado(true);
				documentoCoordenadasGeograficas = documentosFacade.guardarDocumentoAlfresco(
								codigoProyecto,
								"Coordenadas_Actualizadas", 0L, documentoCoordenadasGeograficas,
								TipoDocumentoSistema.TIPO_COORDENADAS_ACTUALIZACION, null);
			}
			
			if (documentoCoordenadasImplantacion != null && documentoCoordenadasImplantacion.getContenidoDocumento() != null) {
				documentoCoordenadasImplantacion.setIdTable(verProyectoBean.getProyecto().getId());
				documentoCoordenadasImplantacion.setDescripcion("Coordenadas_Actualizadas_" + nroActualizacion);
				documentoCoordenadasImplantacion.setEstado(true);
				documentoCoordenadasImplantacion = documentosFacade.guardarDocumentoAlfresco(
								codigoProyecto,
								"Coordenadas_Actualizadas", 0L, documentoCoordenadasImplantacion,
								TipoDocumentoSistema.TIPO_COORDENADAS_ACTUALIZACION, null);
			}
			
			if (documentoCertificado != null && documentoCertificado.getContenidoDocumento() != null) {
				documentoCertificado.setEstado(true);
				documentoCertificado.setDescripcion("Certificado_Interseccion_Actualizado_" + nroActualizacion);
				documentoCertificado = documentosFacade.guardarDocumentoAlfresco(codigoProyecto,
						"Certificado_Interseccion_Actualizado", 0L, documentoCertificado, TipoDocumentoSistema.TIPO_CI_OFICIO_ACTUALIZACION, null);
			
			}
			
			if (documentoMapa != null && documentoMapa.getIdAlfresco() != null) {
				documentoMapa.setDescripcion("Mapa_Actualizado_" + nroActualizacion);
				documentoMapa = documentosFacade.actualizarDocumento(documentoMapa);
			
			}
			
			if (esHidrocarburos) {
				proyectoActualizacionCIFacade.migrarDocumentos("COORDENADAS_GEOGRAFICAS_ACTUALIZADAS", "Coordenadas área geográfica del proyecto", "coordUbicacion", "xls", "xls", codigoProyecto, documentoCoordenadasGeograficas.getIdAlfresco(), nroActualizacion);
				
				proyectoActualizacionCIFacade.migrarDocumentos("COORDENADAS_IMPLANTACION_ACTUALIZADAS", "Coordenadas área de implantación del proyecto", "coordUbicacionImp", "xls", "xls", codigoProyecto, documentoCoordenadasImplantacion.getIdAlfresco(), nroActualizacion);
				
				proyectoActualizacionCIFacade.migrarDocumentos("OFICIO_ACTUALIZADO", "Mapa del Certificado de Intersección.", "CertificadoInterseccionAutomatico", "pdf", "pdf", codigoProyecto, documentoCertificado.getIdAlfresco(), nroActualizacion);
				
				proyectoActualizacionCIFacade.migrarDocumentos("MAPA_ACTUALIZADO", "CI pdf con mapa.", "MapaCIProvisional", "pdf", "pdf", codigoProyecto, documentoMapa.getIdAlfresco(), nroActualizacion);
			}
			
//			boolean firmado = firmaAutomaticaDocumento();
//			if(firmado) {
				verProyectoBean.getProyecto().setEstadoActualizacionCertInterseccion(3); //3 modificado por el operador
				proyectoActualizacionCIFacade.guardarEstadoActualizacionCertificado(verProyectoBean.getProyecto(), null);
				
				if (esHidrocarburos) {
					proyectoActualizacionCIFacade.guardarSolicitudActualizacion(codigoProyecto, 3);  //3 modificado por el operador
				}
				
				JsfUtil.addMessageInfo("La actualización se realizó con éxito");
				
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
//			} else {
//				JsfUtil.addMessageError("Ocurrió un error al firmar los documentos.");
//			}
			
		} catch (Exception ex) {
			LOG.error("Error al guardar los documentos.", ex);
			JsfUtil.addMessageError("Ocurrió un error al guardar los documentos.");
		}
	}
	
	public void guardarDocumentosSuiaVerde() {
		try {
			
			//generar el nuevo certificado de intersección
			documentoCertificado = new Documento();
			generarCertificadoInterseccionSuiaVerde(false);
			
			documentoMapa = new Documento();
			Boolean resultMapa = generarMapaSuiaVerde();
			if(!resultMapa) {
				JsfUtil.addMessageError("Ocurrió un guadar el documento del mapa.");
				return;
			}
			
			if (documentoCertificado != null && documentoCertificado.getContenidoDocumento() != null) {
				String idAlfresco = proyectoActualizacionCIFacade.guardarDocumentosSuiaVerdeAlfresco(proyectoSuiaVerdeBean.getCodigo(), documentoCertificado.getContenidoDocumento(), "OFICIO_ACTUALIZADO", "pdf");
				
				if(idAlfresco != null) 
					documentoCertificado.setIdAlfresco(idAlfresco);
			}
			
//			boolean firmado = firmaAutomaticaDocumento();
//			if(firmado) {
				proyectoActualizacionCIFacade.migrarDocumentos("OFICIO_ACTUALIZADO", "Certificado de Intersección", "CertificadoInterseccionAutomatico", "pdf", "pdf", codigoProyecto, documentoCertificado.getIdAlfresco(), nroActualizacion);
				
				if (documentoCoordenadasGeograficas != null && documentoCoordenadasGeograficas.getContenidoDocumento() != null) {
					String idAlfresco = proyectoActualizacionCIFacade.guardarDocumentosSuiaVerde(
							proyectoSuiaVerdeBean.getCodigo(), documentoCoordenadasGeograficas.getContenidoDocumento(), "COORDENADAS_GEOGRAFICAS_ACTUALIZADAS",
							"Coordenadas área geográfica del proyecto", "xls", "xls",
							"coordUbicacion",
							"Registro Proyecto", nroActualizacion);
					if(idAlfresco != null)
						documentoCoordenadasGeograficas.setContenidoDocumento(null);
				}
				
				if (documentoCoordenadasImplantacion != null && documentoCoordenadasImplantacion.getContenidoDocumento() != null) {
					String idAlfresco = proyectoActualizacionCIFacade.guardarDocumentosSuiaVerde(
							proyectoSuiaVerdeBean.getCodigo(), documentoCoordenadasImplantacion.getContenidoDocumento(), "COORDENADAS_IMPLANTACION_ACTUALIZADAS",
							"Coordenadas área de implantación del proyecto", "xls", "xls",
							"coordUbicacionImp",
							"Registro Proyecto", nroActualizacion);
					if(idAlfresco != null)
						documentoCoordenadasImplantacion.setContenidoDocumento(null);
				}
				
				if (documentoMapa != null && documentoMapa.getIdAlfresco() != null) {
					proyectoActualizacionCIFacade.migrarDocumentos("MAPA_ACTUALIZADO", "Mapa del Certificado de Intersección", "MapaCIFinal", "pdf", "pdf", codigoProyecto, documentoMapa.getIdAlfresco(), nroActualizacion);
				}
				
				proyectoActualizacionCIFacade.guardarHistorialActualizacionCertificado(codigoProyecto, 3, null);
				
				proyectoActualizacionCIFacade.guardarSolicitudActualizacion(codigoProyecto, 3);  //3 modificado por el operador
				
				JsfUtil.addMessageInfo("La actualización se realizó con éxito");
				
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
//			} else {
//				JsfUtil.addMessageError("Ocurrió un error al firmar los documentos.");
//			}
			
		} catch (Exception ex) {
			LOG.error("Error al guardar los documentos.", ex);
			JsfUtil.addMessageError("Ocurrió un error al guardar los documentos.");
		}
	}
	
	public void limpiarActualizacion() {
		try {
			if(tipoProyecto.equals(1))
				proyectoActualizacionCIFacade.eliminarInformacionActualizada(verProyectoBean.getProyecto().getId(), nroActualizacion, oficioCI.getId(), null);
			else 
				proyectoActualizacionCIFacade.eliminarInformacionActualizada(null, nroActualizacion, oficioCI.getId(), codigoProyecto);
			
			JsfUtil.redirectTo("/prevencion/actualizacionCertInterseccion/proyectosPendientes.jsf");
		} catch (Exception ex) {
			LOG.error("Error al eliminar los datos de las coordenadas.", ex);
			JsfUtil.addMessageError("Ocurrió un error al ejecutar la operación.");
		}
	}
	
	public void cerrarActualizacion() {
		try {
			verProyectoBean.getProyecto().setEstadoActualizacionCertInterseccion(3); //3 modificado por el operador
			proyectoActualizacionCIFacade.guardarEstadoActualizacionCertificado(verProyectoBean.getProyecto(), "No actualizado se finalizó por interseccion");

            if (esHidrocarburos!= null && esHidrocarburos) {
            	proyectoActualizacionCIFacade.guardarSolicitudActualizacion(codigoProyecto, 3);  //3 modificado por el operador
            }
			
			JsfUtil.redirectTo("/prevencion/actualizacionCertInterseccion/proyectosPendientes.jsf");
		} catch (Exception ex) {
			LOG.error("Error al eliminar los datos de las coordenadas.", ex);
			JsfUtil.addMessageError("Ocurrió un error al ejecutar la operación.");
		}
	}

}
