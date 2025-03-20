package ec.gob.ambiente.rcoa.registroGeneradorDesechos.bean;

import index.Campos_coordenada;
import index.ContieneCapa_entrada;
import index.ContienePuntoLineaCapa_capa;
import index.ContienePuntoLineaCapa_entrada;
import index.ContienePuntoLineaCapa_resultado;
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
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.rcoa.facade.CapasCoaFacade;
import ec.gob.ambiente.rcoa.facade.CoordenadasProyectoCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.CapasCoa;
import ec.gob.ambiente.rcoa.model.DetalleInterseccionProyectoAmbiental;
import ec.gob.ambiente.rcoa.model.InterseccionProyectoLicenciaAmbiental;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.clases.CoordinatesWrapper;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.DocumentosRgdRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.CoordenadaRgdCoa;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.crud.facade.PostgisFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.TipoForma;
import ec.gob.ambiente.suia.domain.TipoProyeccion;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class CargarCoordenadasREPRcoaBean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = Logger.getLogger(CargarCoordenadasREPRcoaBean.class);
	
	@EJB
	private PostgisFacade postgisFacade;
	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
	@EJB
	private DocumentosRgdRcoaFacade documentosFacade;
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
	
	private byte[] plantillaCoordenadas;
	
	private byte[] ayudaCoordenadas;
	
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
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
	@Getter
	@Setter
	private BandejaTareasBean bandejaTareasBean;
	
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
	
	private  HashMap<InterseccionProyectoLicenciaAmbiental, List<DetalleInterseccionProyectoAmbiental>> listaCapasInterseccionPrincipal= new HashMap<InterseccionProyectoLicenciaAmbiental, List<DetalleInterseccionProyectoAmbiental>>();
	
	@Getter
	@Setter
	private boolean mostrarDetalleInterseccion = false;
	
	@Getter
	@Setter
	private String zonasInterccionDetalle="";
	
	@PostConstruct
	public void init(){
		try {
			coordinatesWrappers = new ArrayList<CoordinatesWrapper>();
			tiposFormas = proyectoLicenciamientoAmbientalFacade.getTiposFormas();
			nombresTiposFormas = new String[tiposFormas.size()];
			int pos = 0;
			for (TipoForma tipoForma : tiposFormas) {
				nombresTiposFormas[pos++] = tipoForma.getNombre();
			}
			this.updateComponentRoute = ":form:containerCoordenadas";
			this.modalLoadFile = true;
			tiposProyecciones = postgisFacade.getTiposProyeccionesSoportadas();
			
			try {
				plantillaCoordenadas = documentosFacade.descargarDocumentoPorNombre(Constantes.PLANTILLA_COORDENADAS_REGISTRO_GENERADOR_REP);
			} catch (Exception e) {
			}
			
			dialogWidgetVarCoordenadas=false;
			codigoProyecto="";
			capas=capasCoaFacade.listaCapas();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void reset() {
		coordinatesWrappers = new ArrayList<CoordinatesWrapper>();
		tipoProyeccion = null;
		formatoVerificado = false;
		transformarFormato = false;
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
		coordinatesWrappers = new ArrayList<CoordinatesWrapper>();
		CoordinatesWrapper coordinatesWrapper = new CoordinatesWrapper();
		coordinatesWrappers.add(coordinatesWrapper);
		String coodenadas="";
		int rows = 0;
		String formaPoligono="Polígono";
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
					BigDecimal coorX = new BigDecimal((double)cellIterator.next().getNumericCellValue()).setScale(5,5);
					BigDecimal coorY = new BigDecimal((double)cellIterator.next().getNumericCellValue()).setScale(5,5);
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
						showErrorClearCoordenadas("La fila " + rows + " no cuenta con todos los datos necesarios. Por favor corrija.");
						return;
					}
				}
				rows++;
			}
			
			varUbicacionArea= new HashMap<String,Double>();
			SVA_Reproyeccion_IntersecadoPortTypeProxy ws=new SVA_Reproyeccion_IntersecadoPortTypeProxy();
			ws.setEndpoint(Constantes.getInterseccionesWS());
			
			HashMap<String, Double>varUbicacion= new HashMap<String,Double>();
			for(int i=0;i<=coordinatesWrappers.size()-1;i++){
				coodenadas="";
				boolean tipoPoligono = coordinatesWrappers.get(i).getCoordenadas().size() > 3;
				for (int j = 0; j <=coordinatesWrappers.get(i).getCoordenadas().size()-1; j++) {
					coodenadas += (coodenadas == "") ? BigDecimal.valueOf(coordinatesWrappers.get(i).getCoordenadas().get(j).getX()).setScale(5, RoundingMode.HALF_DOWN).toString()+" "+BigDecimal.valueOf(coordinatesWrappers.get(i).getCoordenadas().get(j).getY()).setScale(5, RoundingMode.HALF_DOWN).toString() : ","+BigDecimal.valueOf(coordinatesWrappers.get(i).getCoordenadas().get(j).getX()).setScale(5, RoundingMode.HALF_DOWN).toString()+" "+BigDecimal.valueOf(coordinatesWrappers.get(i).getCoordenadas().get(j).getY()).setScale(5, RoundingMode.HALF_DOWN).toString();
				}
				// si es poligono valido intersecion con area portegidas
				if(tipoPoligono){
					verificarCapasInterseccionPoligono(coodenadas);
					// si interseca con alguna area protegida no se ingresa
					if(mostrarDetalleInterseccion){
						coordinatesWrappers = new ArrayList<CoordinatesWrapper>();
						ubicacionesSeleccionadas= new ArrayList<UbicacionesGeografica>();
						parroquiaSeleccionadas= new HashMap<String, UbicacionesGeografica>();
						RequestContext context = RequestContext.getCurrentInstance();
						context.execute("PF('certificadoIntercepcionRcoa1').show();");
						return;
					}
				}else{
					verificarCapasInterseccion(coodenadas);
					// si interseca con alguna area protegida no se ingresa
					if(mostrarDetalleInterseccion){
						coordinatesWrappers = new ArrayList<CoordinatesWrapper>();
						ubicacionesSeleccionadas= new ArrayList<UbicacionesGeografica>();
						parroquiaSeleccionadas= new HashMap<String, UbicacionesGeografica>();
						RequestContext context = RequestContext.getCurrentInstance();
						context.execute("PF('certificadoIntercepcionRcoa1').show();");
						return;
					}
				}
				Intersecado_entrada poligono = new Intersecado_entrada();//verifica que este bien el poligono
				poligono.setU(Constantes.getUserWebServicesSnap());
				poligono.setIsla(Constantes.SIGLAS_INSTITUCION + "-RA");
				poligono.setTog(tipoPoligono?"po":"pu");
				poligono.setXy(coodenadas);
				poligono.setShp("dp");
				Intersecado_resultado[]intRest;
				String parroquia="";
				Double valorParroquia=0.0;
				Integer orden=2;
				try {
					intRest=ws.interseccion(poligono);
					if (intRest[0].getInformacion().getError() != null) {
						JsfUtil.addMessageError(intRest[0].getInformacion().getError().toString());
						coordinatesWrappers = new ArrayList<CoordinatesWrapper>();
					}else{
						boolean coordenadasRepetidas = false;
						if(!coordenadasRepetidas){
							//carga las parroquias ----------------------------------
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
							
							capasIntersecciones= new HashMap<InterseccionProyectoLicenciaAmbiental, List<DetalleInterseccionProyectoAmbiental>>();
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
			JsfUtil.addMessageError("Por favor verifique el formato del archivo de acuerdo a la plantilla proporcionada");
			ex.printStackTrace();
		}
	}
	
	private void verificarCapasInterseccionPoligono(String coodenadas){
		try{
			capasIntersecciones= new HashMap<InterseccionProyectoLicenciaAmbiental, List<DetalleInterseccionProyectoAmbiental>>();
			listaCapasInterseccionPrincipal = new HashMap<InterseccionProyectoLicenciaAmbiental, List<DetalleInterseccionProyectoAmbiental>>();
			
			SVA_Reproyeccion_IntersecadoPortTypeProxy ws=new SVA_Reproyeccion_IntersecadoPortTypeProxy();
			ws.setEndpoint(Constantes.getInterseccionesWS());
			
			//Capas de intersección----------------------------------------------------------
			ContieneCapa_entrada capaImpla = new ContieneCapa_entrada();
			capaImpla.setU(Constantes.getUserWebServicesSnap());
			capaImpla.setXy(coodenadas);
			InterseccionCapa_resultado[]intRestCapaImpl;
			intRestCapaImpl=ws.interseccionCapa(capaImpla);
			
			InterseccionProyectoLicenciaAmbiental capaInterseccion = new InterseccionProyectoLicenciaAmbiental();
			List<DetalleInterseccionProyectoAmbiental> listaIntersecciones = new ArrayList<DetalleInterseccionProyectoAmbiental>();
			
			for (IntersecadoCapa_capa capas : intRestCapaImpl[0].getCapa()) {
				if(Integer.valueOf(capas.getNum())>0)
				{
					capaInterseccion = new InterseccionProyectoLicenciaAmbiental();
					if(!capas.getNombre().equals("MAR TERRITORIAL"))
					{
						capaInterseccion.setCapa(getCapa(capas.getNombre()));
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
								String nombreGeom = interseccion.getNam();
								if(capas.getNombre().equals("SNAP")) {
									nombreGeom = interseccion.getMap() + " " + interseccion.getNam();
								}
								detallecapa.setNombreGeometria(nombreGeom);
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
							if(interseccion.getFcode()!=null)
							{
								if(capas.getNombre().equals("SNAP"))
									detallecapa.setCodigoUnicoCapa(interseccion.getFcode());
								else
									detallecapa.setCodigoConvenio(interseccion.getFcode());
							}
							if(capas.getNombre().equals("LIMITE INTERNO 20 KM"))
							{
								detallecapa.setNombreGeometria("SI");
								detallecapa.setIdGeometria(Integer.valueOf(interseccion.getGid()));
							}
							listaIntersecciones.add(detallecapa);
						}
						capasIntersecciones.put(capaInterseccion, listaIntersecciones);
						if(capaInterseccion.getCapa() != null && (capaInterseccion.getCapa().getId().equals(Constantes.ID_CAPA_SNAP) || capaInterseccion.getCapa().getId().equals(Constantes.ID_CAPA_BP) || capaInterseccion.getCapa().getId().equals(Constantes.ID_CAPA_PFE)))
							listaCapasInterseccionPrincipal.put(capaInterseccion, listaIntersecciones);
					}
				}
			}
			getDetalleIntersecciones();
		} catch (RemoteException e) {
			JsfUtil.addMessageError("Error inesperado, comuníquese con mesa de ayuda");
			System.out.println("Servicio no disponible ----> "+Constantes.getInterseccionesWS());
			ubicacionesSeleccionadas= new ArrayList<UbicacionesGeografica>();
			coordinatesWrappers = new ArrayList<CoordinatesWrapper>();
			mostrarDetalleInterseccion = false;
		}
	}
	
	private void verificarCapasInterseccion(String coodenadas){
		try{
		capasIntersecciones= new HashMap<InterseccionProyectoLicenciaAmbiental, List<DetalleInterseccionProyectoAmbiental>>();
		listaCapasInterseccionPrincipal = new HashMap<InterseccionProyectoLicenciaAmbiental, List<DetalleInterseccionProyectoAmbiental>>();
		String[] numCoordenadas = coodenadas.split(",");
		SVA_Reproyeccion_IntersecadoPortTypeProxy ws=new SVA_Reproyeccion_IntersecadoPortTypeProxy();
		ws.setEndpoint(Constantes.getInterseccionesWS());
		//Capas de intersección----------------------------------------------------------
		ContienePuntoLineaCapa_entrada capaImpla = new ContienePuntoLineaCapa_entrada();
		capaImpla.setU(Constantes.getUserWebServicesSnap());
		capaImpla.setXy(coodenadas);
		capaImpla.setCapas("SNAP");
		capaImpla.setTipo(numCoordenadas.length == 1 ? "pu": "li");
		ContienePuntoLineaCapa_resultado[] intRestCapaImpl;
		intRestCapaImpl=ws.contienePuntoLineaCapa(capaImpla);
		
		InterseccionProyectoLicenciaAmbiental capaInterseccion = new InterseccionProyectoLicenciaAmbiental();
		List<DetalleInterseccionProyectoAmbiental> listaIntersecciones = new ArrayList<DetalleInterseccionProyectoAmbiental>();
		
		for (ContienePuntoLineaCapa_capa capas : intRestCapaImpl[0].getCapa()) {
			if(Integer.valueOf(capas.getNum())>0)
			{
				capaInterseccion = new InterseccionProyectoLicenciaAmbiental();
				if(!capas.getNombre().equals("MAR TERRITORIAL"))
				{
					capaInterseccion.setCapa(getCapa(capas.getNombre()));
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
							String nombreGeom = interseccion.getNam();
							if(capas.getNombre().equals("SNAP")) {
								nombreGeom = interseccion.getMap() + " " + interseccion.getNam();
							}
							detallecapa.setNombreGeometria(nombreGeom);
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
						if(interseccion.getFcode()!=null)
						{
							if(capas.getNombre().equals("SNAP"))
								detallecapa.setCodigoUnicoCapa(interseccion.getFcode());
							else
								detallecapa.setCodigoConvenio(interseccion.getFcode());
						}
						if(capas.getNombre().equals("LIMITE INTERNO 20 KM"))
						{
							detallecapa.setNombreGeometria("SI");
							detallecapa.setIdGeometria(Integer.valueOf(interseccion.getGid()));
						}
						listaIntersecciones.add(detallecapa);
					}
					capasIntersecciones.put(capaInterseccion, listaIntersecciones);
					if(capaInterseccion.getCapa() != null && (capaInterseccion.getCapa().getId().equals(Constantes.ID_CAPA_SNAP) || capaInterseccion.getCapa().getId().equals(Constantes.ID_CAPA_BP) || capaInterseccion.getCapa().getId().equals(Constantes.ID_CAPA_PFE)))
						listaCapasInterseccionPrincipal.put(capaInterseccion, listaIntersecciones);
				}
			}
		}
		getDetalleIntersecciones();
		} catch (RemoteException e) {
			JsfUtil.addMessageError("Error inesperado, comuníquese con mesa de ayuda");
			System.out.println("Servicio no disponible ----> "+Constantes.getInterseccionesWS());
			ubicacionesSeleccionadas= new ArrayList<UbicacionesGeografica>();
			coordinatesWrappers = new ArrayList<CoordinatesWrapper>();
			mostrarDetalleInterseccion = false;
		}
	}
	
	public void getDetalleIntersecciones()
	{
		mostrarDetalleInterseccion = false;
		zonasInterccionDetalle="";
		HashMap<InterseccionProyectoLicenciaAmbiental, List<DetalleInterseccionProyectoAmbiental>> lista = listaCapasInterseccionPrincipal;
		
		if(lista.size()>0)
		{
			zonasInterccionDetalle="Estimado usuario el punto que desea ingresar se encuentra intersecando con: ";
			zonasInterccionDetalle=zonasInterccionDetalle+"<ul>";
			String nombreCapa="";
			for (InterseccionProyectoLicenciaAmbiental i : lista.keySet()) {
				nombreCapa=i.getDescripcionCapa();
				String nombreInterseccion="";
				for(DetalleInterseccionProyectoAmbiental j : lista.get(i))
				{
					nombreInterseccion += (nombreInterseccion == "") ? j.getNombreGeometria() : ","+j.getNombreGeometria();
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
		parroquiaSeleccionadas= new HashMap<String, UbicacionesGeografica>();
		
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
		JsfUtil.getBean(PuntosRecuperacionRgdREPBean.class).setParroquia(ubicacionfacade.buscarUbicacionPorCodigoInec(inecMayor));
		JsfUtil.getBean(AdicionarUbicacionesRcoaBean.class).setUbicacionSeleccionada(ubicacionfacade.buscarUbicacionPorCodigoInec(inecMayor), 3);
	}
}