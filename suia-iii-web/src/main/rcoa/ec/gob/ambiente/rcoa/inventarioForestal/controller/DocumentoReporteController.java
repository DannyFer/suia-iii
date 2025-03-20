package ec.gob.ambiente.rcoa.inventarioForestal.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.bean.ManagedProperty;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.facade.CapasCoaFacade;
import ec.gob.ambiente.rcoa.facade.DetalleInterseccionProyectoAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.InterseccionProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.CoordenadasInventarioForestalCertificadoFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.DocumentoInventarioForestalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.InformeOficioIFFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.InventarioForestalAmbientalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.ReporteInventarioForestalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.ShapeInventarioForestalCertificadoFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.TipoReportesInventarioForestalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.model.CoordenadasInventarioForestalCertificado;
import ec.gob.ambiente.rcoa.inventarioForestal.model.DocumentoInventarioForestal;
import ec.gob.ambiente.rcoa.inventarioForestal.model.InformeInspeccionCampoEntity;
import ec.gob.ambiente.rcoa.inventarioForestal.model.InventarioForestalAmbiental;
import ec.gob.ambiente.rcoa.inventarioForestal.model.ReporteInventarioForestal;
import ec.gob.ambiente.rcoa.inventarioForestal.model.ShapeInventarioForestalCertificado;
import ec.gob.ambiente.rcoa.inventarioForestal.model.TipoReporteInventarioForestal;
import ec.gob.ambiente.rcoa.model.CapasCoa;
import ec.gob.ambiente.rcoa.model.DetalleInterseccionProyectoAmbiental;
import ec.gob.ambiente.rcoa.model.InterseccionProyectoLicenciaAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.TipoForma;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;
import lombok.Getter;
import lombok.Setter;

public class DocumentoReporteController implements Serializable {

	private static final long serialVersionUID = -42729579304117925L;
	
	//final int INFORME_INSPECCION = 1; cambiar este es el original
	final int INFORME_INSPECCION = 1;
	
	final String INFORME_INSPECCION_DESCRIPCION = "Informe Inspeccion";
	final String INFORME_INSPECCION_IMAGEN1 = "CARACTERIZACION_TIPO";
	final String INFORME_INSPECCION_IMAGEN2 = "CARACTERIZACION_ECOSISTEMA";
	final String INFORME_INSPECCION_IMAGEN3 = "CARACTERIZACION_AREA";

	final Logger LOG = Logger.getLogger(DocumentoReporteController.class);

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
    ReporteInventarioForestalFacade reporteInventarioForestalFacade;

	@EJB
	private PlantillaReporteFacade plantillaReporteFacade;
	
	@EJB
	public ProcesoFacade procesoFacade;
	Map<String, Object> variables;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
    DocumentoInventarioForestalFacade documentoInventarioForestalFacade;
	
	@EJB
	InventarioForestalAmbientalFacade inventarioForestalAmbientalFacade;
	
	@EJB
	private InformeOficioIFFacade informeOficioIFFacade;
	@EJB
	private CapasCoaFacade capasCoaFacade;
	@EJB
	private InterseccionProyectoLicenciaAmbientalFacade interseccionProyectoLicenciaAmbientalFacade;
    @EJB
	private DetalleInterseccionProyectoAmbientalFacade detalleInterseccionProyectoAmbientalFacade;
	
	@Getter
	@Setter
	private PlantillaReporte plantillaReporteInforme;
	@Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    public BandejaTareasBean bandejaTareasBean;
	@Getter
    @Setter
    @ManagedProperty(value = "#{proyectosBean}")
    private ProyectosBean proyectosBean;
	
	@Getter
	ReporteInventarioForestal informeInspeccion;
	
	@Getter
    Integer idProyecto;
	
    @Getter
    @Setter
    String tramite, tecnicoForestal;
    
    @Setter
    @Getter
    public ProyectoLicenciaCoa proyectoLicenciaCoa;
    
    List<DocumentoInventarioForestal> listaDocumentos;
    
    @Getter
	@Setter
	public InventarioForestalAmbiental inventarioForestalAmbiental;
	
	@Getter
	@Setter
	private InformeInspeccionCampoEntity informeInspeccionCampoEntity = new InformeInspeccionCampoEntity();
	@EJB
	private TipoReportesInventarioForestalFacade tipoReportesInventarioForestalFacade;
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	@EJB
	public ShapeInventarioForestalCertificadoFacade shapeInventarioForestalCertificadoFacade;
	@EJB
	private CoordenadasInventarioForestalCertificadoFacade coordenadasInventarioForestalCertificadoFacade;
	@EJB
    private UsuarioFacade usuarioFacade;
	@Getter
    @Setter
    private ShapeInventarioForestalCertificado shape = new ShapeInventarioForestalCertificado();
    @Getter
    @Setter
    private List<CoordendasPoligonosCertificado> coordinatesWrappers = new ArrayList<CoordendasPoligonosCertificado>();
    @Getter
    private List<TipoForma> tiposFormas  = new ArrayList<TipoForma>();
    @Getter
    @Setter  
    private TipoForma poligono=new TipoForma();
    @Getter
    @Setter  
    private Usuario usuarioTecnicoForestal = new Usuario();
    
    
    public static final Integer ID_LAYER_COBERTURA = 12;
	
	
	void visualizarInforme(boolean setCurrentDate) throws Exception {
		if (plantillaReporteInforme == null) {
			plantillaReporteInforme = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.INVENTARIO_FORESTAL_INFORME_INSPECCION);
		}
		
		//INVENTARIO_FORESTAL_INFORME_TECNICO
		if (informeInspeccion == null) {
			consultarReporteInventarioForestal();
		}

		File informePdfAux = UtilGenerarInforme.generarFichero(plantillaReporteInforme.getHtmlPlantilla(), informeInspeccionCampoEntity.getNombreFichero(), true, informeInspeccionCampoEntity);

		File informePdf = JsfUtil.fileMarcaAgua(informePdfAux, setCurrentDate ? " - - BORRADOR - - " : " ", BaseColor.GRAY);
		
		Path path = Paths.get(informePdf.getAbsolutePath());
		informeInspeccion.setArchivoInforme(Files.readAllBytes(path));
		String reporteHtmlfinal = informeInspeccion.getNombreFichero().replace("/", "-");
		File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
		FileOutputStream file = new FileOutputStream(archivoFinal);
		file.write(informeInspeccion.getArchivoInforme());
		file.close();
		informeInspeccion.setInformePath(JsfUtil.devolverContexto("/reportesHtml/" + informeInspeccion.getNombreFichero()));
	}
	

	public String elimuinarParrafoHtml(String texto) {
		if (texto == null) {
			return null;
		}
		texto = texto.replace("<p>\r\n", "");
		texto = texto.replace("</p>\r\n", "");
		return texto;
	}

	

	public StreamedContent getStream(String name, byte[] fileContent) throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (fileContent != null) {
			content = new DefaultStreamedContent(new ByteArrayInputStream(fileContent), "application/octet-stream");
			content.setName(name);
		}
		return content;
	}

	public void consultarReporteInventarioForestal() {
		String mensaje = "Ocurrió un error al recuperar los datos del Inventario Forestal";
		try {
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getProcessId());
    		tramite = (String) variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
			idProyecto = Integer.valueOf((String)variables.get(Constantes.ID_PROYECTO));
			tecnicoForestal = (String) variables.get("tecnicoForestal");
			usuarioTecnicoForestal = usuarioFacade.buscarUsuario(tecnicoForestal);
			
			proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
			if (null != proyectoLicenciaCoa) {
					inventarioForestalAmbiental = inventarioForestalAmbientalFacade.getByIdRegistroPreliminar(proyectoLicenciaCoa.getId());
					if(inventarioForestalAmbiental.getId()!=null)
						informeInspeccion = reporteInventarioForestalFacade.getByIdCertificadoByIdTipoDocumento(inventarioForestalAmbiental.getId(),INFORME_INSPECCION);//Informe inspeccion				
			}
			
			
			if (informeInspeccion.getId() != null ) {
				listaDocumentos = documentoInventarioForestalFacade.getByInventarioForestalReporte(informeInspeccion.getId());
				DocumentoInventarioForestal informe = new DocumentoInventarioForestal();
				for(DocumentoInventarioForestal item : listaDocumentos) {
					if(item.getDescripcionTabla().equals(INFORME_INSPECCION_DESCRIPCION)) {
						informe = item;
						break;
					}
				}
				if(informe.getNombreDocumento() == null) {
					informe.setNombreDocumento("InformeInspeccion.pdf");
				}
				informeInspeccion.setNombreFichero(informe.getNombreDocumento());
				asignaInformeInspeccion();
			} else {
				inicializarReporteInventarioForestal();
			}
		} catch (JbpmException e) {
			LOG.error(e, e);
			JsfUtil.addMessageError(mensaje);
		} catch (CmisAlfrescoException e) {
			LOG.error(e, e);
			JsfUtil.addMessageError(mensaje);
		}
	}
	
	public void inicializarReporteInventarioForestal() {
		try {
			String direccionForestal = proyectoLicenciaCoa.getAreaInventarioForestal().getAreaName();
			String direccionSiglas = proyectoLicenciaCoa.getAreaInventarioForestal().getAreaAbbreviation();
			String codigoReporte = informeOficioIFFacade.generarCodigoInspeccion(proyectoLicenciaCoa.getAreaInventarioForestal());
			Date fechaActual = new Date();
			String interseccionProyecto = getInterseccionCapa(ID_LAYER_COBERTURA);
			SimpleDateFormat fechaFormat = new SimpleDateFormat("dd/MM/yyy HH:mm");
			String elaboradoPor = (usuarioTecnicoForestal == null) ? "" : usuarioTecnicoForestal.getPersona().getNombre();
			String denominacionCargo = (usuarioTecnicoForestal == null) ? "" : usuarioTecnicoForestal.getPersona().getPosicion();
			String tablaCoordenadasInventarioForestal = listCoordenadas(inventarioForestalAmbiental.getId());
			informeInspeccionCampoEntity.setDireccionForestal(direccionForestal);
			informeInspeccionCampoEntity.setSiglas(direccionSiglas);
			informeInspeccionCampoEntity.setCodigoReporte(codigoReporte);
			informeInspeccionCampoEntity.setNombreFichero("InformeInspeccion.pdf");
			informeInspeccionCampoEntity.setNombreProyecto(proyectoLicenciaCoa.getNombreProyecto());
			informeInspeccionCampoEntity.setCodigoProyecto(proyectoLicenciaCoa.getCodigoUnicoAmbiental());
			informeInspeccionCampoEntity.setRazonSocialOperador(proyectosBean.getProponente());
			informeInspeccionCampoEntity.setInterseccionProyecto(interseccionProyecto);
			informeInspeccionCampoEntity.setFecha(fechaFormat.format(fechaActual));
			informeInspeccionCampoEntity.setElaboradoPor(elaboradoPor);
			informeInspeccionCampoEntity.setDenominacionCargo(denominacionCargo);
			informeInspeccionCampoEntity.setTablaCoordenadasInventarioForestal(tablaCoordenadasInventarioForestal);
			

			TipoReporteInventarioForestal tipoReporte = tipoReportesInventarioForestalFacade.getByIdTipoReporteInventarioForestal(INFORME_INSPECCION);
			informeInspeccion = new ReporteInventarioForestal();
			informeInspeccion.setNombreFichero("InformeInspeccion.pdf");
			informeInspeccion.setTipoReporteInventarioForestal(tipoReporte);
			informeInspeccion.setFecha(fechaActual);
			informeInspeccion.setCodigoReporte(codigoReporte);
			informeInspeccion.setInventarioForestalAmbiental(inventarioForestalAmbiental);
			informeInspeccion.setSuperficieCoberturaVegetal(inventarioForestalAmbiental.getSuperficieDesbroce());
		} catch (Exception e) {
			LOG.error(e);
			System.out.println("Error al inicializar el informe de inspección de campo - INVENTARIO FORESTAL");
		}
	}
	
	public void asignaInformeInspeccion() {
		try {
			String direccionForestal = proyectoLicenciaCoa.getAreaInventarioForestal().getAreaName();
			String direccionSiglas = proyectoLicenciaCoa.getAreaInventarioForestal().getAreaAbbreviation();
			String codigoReporte = informeInspeccion.getCodigoReporte();
			SimpleDateFormat fechaFormat = new SimpleDateFormat("dd/MM/yyy HH:mm");
			String fechaInspeccion = (informeInspeccion.getFechaInspeccion() == null) ? "" : fechaFormat.format(informeInspeccion.getFechaInspeccion());
			String fechaFin = (informeInspeccion.getFechaFin() == null) ? "" : fechaFormat.format(informeInspeccion.getFechaFin());
			String interseccionProyecto = getInterseccionCapa(ID_LAYER_COBERTURA);
			String delegadosInspecion = (informeInspeccion.getDelegadosInspecion() == null) ? "" : informeInspeccion.getDelegadosInspecion();
			String nombresDelegadoInspeccion = (informeInspeccion.getNombresDelegadoInspeccion() == null) ? "" : informeInspeccion.getNombresDelegadoInspeccion();
			String areaDelegado =  (informeInspeccion.getAreaDelegado() == null) ? "" : informeInspeccion.getAreaDelegado();
			String cargoDelegado =  (informeInspeccion.getCargoDelegado() == null) ? "" : informeInspeccion.getCargoDelegado();
			String antecedentes =  (informeInspeccion.getAntecedentes() == null) ? "" : informeInspeccion.getAntecedentes();
			String marcoLegal =  (informeInspeccion.getMarcoLegal() == null) ? "" : informeInspeccion.getMarcoLegal();
			String objetivo =  (informeInspeccion.getObjetivo() == null) ? "" : informeInspeccion.getObjetivo();
			String tablaCoordenadasInventarioForestal = listCoordenadas(inventarioForestalAmbiental.getId());
			
			String fotografias = getFotografias();
			String caracterizacionCobertura =  (informeInspeccion.getCaracterizacionEstadoVegetal() == null) ? "" : informeInspeccion.getCaracterizacionEstadoVegetal();
			String caracterizacionCoberturaImp = caracterizacionCobertura + "<br />" + fotografias;
			
			String fotografiasEco = getFotografiasEcosistemas();
			String caracterizacionEcosistemas =  (informeInspeccion.getCaracterizacionEcosistemas() == null) ? "" : informeInspeccion.getCaracterizacionEcosistemas();
			String caracterizacionEcosistemasImpr = caracterizacionEcosistemas + "<br />" + fotografiasEco;
			
			String fotografiasArea = getFotografiasAreaImplantacion();
			String caracterizacionAreaImplantacion =  (informeInspeccion.getCaracterizacionAreaImplantacion() == null) ? "" : informeInspeccion.getCaracterizacionAreaImplantacion();
			String caracterizacionAreaImplantacionImpr = caracterizacionAreaImplantacion + "<br />" + fotografiasArea;
			
			String conclusiones =  (informeInspeccion.getConclusiones() == null) ? "" : informeInspeccion.getConclusiones();
			String recomendaciones =  (informeInspeccion.getRecomendaciones() == null) ? "" : informeInspeccion.getRecomendaciones();
			String elaboradoPor = (usuarioTecnicoForestal == null) ? "" : usuarioTecnicoForestal.getPersona().getNombre();
			String denominacionCargo = (usuarioTecnicoForestal == null) ? "" : usuarioTecnicoForestal.getPersona().getTitulo();
			String anexos = "<br>"+getFotografiasAnexos();
			
			informeInspeccionCampoEntity.setDireccionForestal(direccionForestal);
			informeInspeccionCampoEntity.setSiglas(direccionSiglas);
			informeInspeccionCampoEntity.setCodigoReporte(codigoReporte);
			informeInspeccionCampoEntity.setNombreFichero(informeInspeccion.getNombreFichero());
			informeInspeccionCampoEntity.setNombreProyecto(proyectoLicenciaCoa.getNombreProyecto());
			informeInspeccionCampoEntity.setCodigoProyecto(proyectoLicenciaCoa.getCodigoUnicoAmbiental());
			informeInspeccionCampoEntity.setRazonSocialOperador(proyectosBean.getProponente());
			informeInspeccionCampoEntity.setFecha(fechaFormat.format(informeInspeccion.getFecha()));
			informeInspeccionCampoEntity.setInterseccionProyecto(interseccionProyecto);
			informeInspeccionCampoEntity.setTablaCoordenadasInventarioForestal(tablaCoordenadasInventarioForestal);
			informeInspeccionCampoEntity.setElaboradoPor(elaboradoPor);
			informeInspeccionCampoEntity.setDenominacionCargo(denominacionCargo);
			informeInspeccionCampoEntity.setFechaInspeccion(fechaInspeccion);
			informeInspeccionCampoEntity.setFechaFin(fechaFin);
			informeInspeccionCampoEntity.setDelegadosInspecion(delegadosInspecion);
			informeInspeccionCampoEntity.setNombresDelegadoInspeccion(nombresDelegadoInspeccion);
			informeInspeccionCampoEntity.setAreaDelegado(areaDelegado);
			informeInspeccionCampoEntity.setCargoDelegado(cargoDelegado);
			informeInspeccionCampoEntity.setAntecedentes(antecedentes);
			informeInspeccionCampoEntity.setMarcoLegal(marcoLegal);
			informeInspeccionCampoEntity.setObjetivo(objetivo);
			informeInspeccionCampoEntity.setCaracterizacionCobertura(caracterizacionCoberturaImp);
			informeInspeccionCampoEntity.setCaracterizacionEcosistemas(caracterizacionEcosistemasImpr);
			informeInspeccionCampoEntity.setCaracterizacionAreaImplantacion(caracterizacionAreaImplantacionImpr);
			informeInspeccionCampoEntity.setConclusiones(conclusiones);
			informeInspeccionCampoEntity.setRecomendaciones(recomendaciones);
			informeInspeccionCampoEntity.setAnexos(anexos);
		} catch (Exception e) {
			LOG.error(e);
		}
	}
	
	private String getInterseccionCapa(Integer idCapa) {
    	String capaValue = "---";
    	InterseccionProyectoLicenciaAmbiental interseca = new InterseccionProyectoLicenciaAmbiental();
    	CapasCoa interseccionCapa = new CapasCoa();
    	interseccionCapa = capasCoaFacade.getCapasById(idCapa);
    	List<InterseccionProyectoLicenciaAmbiental> interseccionProyecto = new ArrayList<InterseccionProyectoLicenciaAmbiental>();
    	interseccionProyecto = interseccionProyectoLicenciaAmbientalFacade.getByIdProyectoCoa(inventarioForestalAmbiental.getId(), 0);
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
	
	public TipoForma getTipoForma(Integer id) {
        for (TipoForma tf : tiposFormas) {
            if (tf.getId()==id)
                return tf;
        }
        return null;
    }
	
	public String listCoordenadas(Integer idInventarioForestalRegistro) {
		String tablaCoordenadasInventarioForestal = "<table style='width:100%;'>";
		List<ShapeInventarioForestalCertificado> listShapes = shapeInventarioForestalCertificadoFacade.getByInventarioForestalAmbiental(idInventarioForestalRegistro);
    	for(int i=0;i<=listShapes.size()-1;i++){
    		tablaCoordenadasInventarioForestal += "<thead style='text-align:center;'>Grupo de coordenadas "+(i+1)+"</thead>";
    		shape = new ShapeInventarioForestalCertificado();
			shape = listShapes.get(i);
			List<CoordenadasInventarioForestalCertificado> coorImpl= new ArrayList<CoordenadasInventarioForestalCertificado>();
			coorImpl = coordenadasInventarioForestalCertificadoFacade.getByShape(shape.getId());
			if (coorImpl.size() > 0) {
				tablaCoordenadasInventarioForestal += "<tbody>";
				tablaCoordenadasInventarioForestal += "<th>Shape</th><th>X</th><th>Y</th>";
				for (int j=0;j<coorImpl.size();j++) {
					tablaCoordenadasInventarioForestal += "<tr><td>"+(j+1)+"</td><td>"+coorImpl.get(j).getX()+"</td><td>"+coorImpl.get(j).getY()+"</td></tr>";
				}
			}
			tablaCoordenadasInventarioForestal += "</body>";
			CoordendasPoligonosCertificado coordinatesWrapper = new CoordendasPoligonosCertificado(coorImpl, poligono);
			coordinatesWrappers.add(coordinatesWrapper);
		}
    	tablaCoordenadasInventarioForestal += "</table>";
    	return tablaCoordenadasInventarioForestal;
	}
	
	private String getFotografias() throws CmisAlfrescoException{
		
		List<DocumentoInventarioForestal> imagenesList;
		
		imagenesList = (ArrayList<DocumentoInventarioForestal>) documentoInventarioForestalFacade.getDocumentosByInventarioTipoDocumento(inventarioForestalAmbiental.getId(), TipoDocumentoSistema.IMAGEN_COBERTURA_VEGETAL);
		
		if(imagenesList!=null)
		{
			String fotografias = "<table style=\"width: 650px; font-size: 11px !important;\" border=\"1\" cellpadding=\"7\" cellspacing=\"0\" class=\"w600Table\">"
					+ "<tbody>";			
			int i = 0;
			int numFotos = imagenesList.size();
			for (DocumentoInventarioForestal imagen : imagenesList) {	
				i++;
				String urlImagen=documentoInventarioForestalFacade.obtenerUrlAlfresco(imagen.getIdAlfresco());
				
				if(esImpar(i)){
					fotografias += "<tr>";
				}				
				
				fotografias += "<td style=\"text-align: center;\"><img src=\'" + urlImagen + "\' height=\'160\' width=\'160\' style=\'padding-left: 40%\'></img><span><br />"+ imagen.getDescripcionTabla() +"</span></td>";
				
				if (i == numFotos) {
					fotografias += "</tr>";
				} else {
					if (!esImpar(i)) {
						fotografias += "</tr>";
					}
				}				
			}
			fotografias += "</tbody></table>";
			return fotografias;
		}
		return "";		
	}	
	
	public boolean esImpar(int iNumero) {
		if (iNumero % 2 != 0)
			return true;
		else
			return false;
	}
	
	private String getFotografiasEcosistemas() throws CmisAlfrescoException{
		
		List<DocumentoInventarioForestal> imagenesList;
		
		imagenesList = (ArrayList<DocumentoInventarioForestal>) documentoInventarioForestalFacade.getDocumentosByInventarioTipoDocumento(inventarioForestalAmbiental.getId(), TipoDocumentoSistema.IMAGEN_ECOSISTEMAS_PRESENTES);
		
		if(imagenesList!=null)
		{
			String fotografias = "<table style=\"width: 650px; font-size: 11px !important;\" border=\"1\" cellpadding=\"7\" cellspacing=\"0\" class=\"w600Table\">"
					+ "<tbody>";			
			int i = 0;
			int numFotos = imagenesList.size();
			for (DocumentoInventarioForestal imagen : imagenesList) {	
				i++;
				String urlImagen=documentoInventarioForestalFacade.obtenerUrlAlfresco(imagen.getIdAlfresco());
				
				if(esImpar(i)){
					fotografias += "<tr>";
				}				
				
				fotografias += "<td style=\"text-align: center;\"><img src=\'" + urlImagen + "\' height=\'160\' width=\'160\' style=\'padding-left: 40%\'></img><span><br />"+ imagen.getDescripcionTabla() +"</span></td>";
				
				if (i == numFotos) {
					fotografias += "</tr>";
				} else {
					if (!esImpar(i)) {
						fotografias += "</tr>";
					}
				}				
			}
			fotografias += "</tbody></table>";
			return fotografias;
		}
		return "";		
	}	
	
	private String getFotografiasAreaImplantacion() throws CmisAlfrescoException{
		
		List<DocumentoInventarioForestal> imagenesList;
		
		imagenesList = (ArrayList<DocumentoInventarioForestal>) documentoInventarioForestalFacade.getDocumentosByInventarioTipoDocumento(inventarioForestalAmbiental.getId(), TipoDocumentoSistema.IMAGEN_AREA_IMPLANTACION);
		
		if(imagenesList!=null)
		{
			String fotografias = "<table style=\"width: 650px; font-size: 11px !important;\" border=\"1\" cellpadding=\"7\" cellspacing=\"0\" class=\"w600Table\">"
					+ "<tbody>";			
			int i = 0;
			int numFotos = imagenesList.size();
			for (DocumentoInventarioForestal imagen : imagenesList) {	
				i++;
				String urlImagen=documentoInventarioForestalFacade.obtenerUrlAlfresco(imagen.getIdAlfresco());
				
				if(esImpar(i)){
					fotografias += "<tr>";
				}				
				
				fotografias += "<td style=\"text-align: center;\"><img src=\'" + urlImagen + "\' height=\'160\' width=\'160\' style=\'padding-left: 40%\'></img><span><br />"+ imagen.getDescripcionTabla() +"</span></td>";
				
				if (i == numFotos) {
					fotografias += "</tr>";
				} else {
					if (!esImpar(i)) {
						fotografias += "</tr>";
					}
				}				
			}
			fotografias += "</tbody></table>";
			return fotografias;
		}
		return "";		
	}	
	
	private String getFotografiasAnexos() throws CmisAlfrescoException {
		List<DocumentoInventarioForestal> imagenesList;
		imagenesList = (ArrayList<DocumentoInventarioForestal>) documentoInventarioForestalFacade.getDocumentosByInventarioTipoDocumento(inventarioForestalAmbiental.getId(), TipoDocumentoSistema.IMAGEN_ANEXO);
		if(imagenesList!=null) {
			String fotografias = "<table style=\"width: 650px; font-size: 11px !important;\" border=\"1\" cellpadding=\"7\" cellspacing=\"0\" class=\"w600Table\">"
					+ "<tbody>";			
			int i = 0;
			int numFotos = imagenesList.size();
			for (DocumentoInventarioForestal imagen : imagenesList) {	
				i++;
				String urlImagen=documentoInventarioForestalFacade.obtenerUrlAlfresco(imagen.getIdAlfresco());
				
				if(esImpar(i)){
					fotografias += "<tr>";
				}				
				fotografias += "<td style=\"text-align: center;\"><img src=\'" + urlImagen + "\' height=\'160\' width=\'160\' style=\'padding-left: 40%\'></img><span><br />"+ imagen.getDescripcionTabla() +"</span></td>";
				if (i == numFotos) {
					fotografias += "</tr>";
				} else {
					if (!esImpar(i)) {
						fotografias += "</tr>";
					}
				}				
			}
			fotografias += "</tbody></table>";
			return fotografias;
		}
		return "";		
	}

}