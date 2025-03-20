package ec.gob.ambiente.rcoa.registroAmbiental.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.hamcrest.core.SubstringMatcher;
import org.primefaces.context.RequestContext;

import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.css.StyleAttrCSSResolver;
import com.itextpdf.tool.xml.html.CssAppliers;
import com.itextpdf.tool.xml.html.CssAppliersImpl;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.prevencion.categoria2.bean.ImpresionFichaGeneralBean;
import ec.gob.ambiente.rcoa.dto.EntidadPma;
import ec.gob.ambiente.rcoa.dto.EntidadPmaViabilidad;
import ec.gob.ambiente.rcoa.facade.CoordenadasProyectoCoaFacade;
import ec.gob.ambiente.rcoa.facade.PlanManejoAmbientalCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.InventarioForestalAmbientalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.facade.ReporteInventarioForestalFacade;
import ec.gob.ambiente.rcoa.inventarioForestal.model.InventarioForestalAmbiental;
import ec.gob.ambiente.rcoa.inventarioForestal.model.ReporteInventarioForestal;
import ec.gob.ambiente.rcoa.model.CoordenadasProyecto;
import ec.gob.ambiente.rcoa.model.FasesRegistroAmbiental;
import ec.gob.ambiente.rcoa.model.PmaAceptadoRegistroAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.model.RegistroAmbientalRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.DesechosRegistroGeneradorRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.PermisoRegistroGeneradorDesechosFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.PuntoGeneracionDesechoFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosProyectosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.DesechosRegistroGeneradorRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.PermisoRegistroGeneradorDesechos;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.PuntoGeneracionDesecho;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosRcoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.RegistroSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.UbicacionSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.RegistroSustanciaQuimica;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.UbicacionSustancia;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InformeInspeccionBiodiversidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InformeRevisionForestalFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.InformeInspeccionBiodiversidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.InformeTecnicoForestal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ViabilidadCoa;
import ec.gob.ambiente.rcoa.viabilidadTecnica.facade.PmaViabilidadTecnicaFacade;
import ec.gob.ambiente.rcoa.viabilidadTecnica.model.FaseViabilidadTecnicaRcoa;
import ec.gob.ambiente.rcoa.viabilidadTecnica.model.PmaViabilidadTecnica;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Articulo;
import ec.gob.ambiente.suia.domain.CatalogoGeneral;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.dto.EntityNormativa;
import ec.gob.ambiente.suia.dto.EntityNormativaDetalle;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.reportes.DocumentoPDFPlantillaHtml;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilFichaMineria;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;
import lombok.Getter;
import lombok.Setter;


@ManagedBean
@ViewScoped
public class GenerarDocumentosRgistroAmbientalCoaController {

    private static final Logger LOG = Logger.getLogger(UtilGenerarInforme.class);
    private static final String VER_ANEXO = "<span style=\"font-size: small;font-weight: bold;background-color: inherit;\"> (Ver Anexo 1) </span>";

	@Getter
    @Setter
    @ManagedProperty(value = "#{marcoLegalReferencialController}")
    private MarcoLegalReferencialController marcoLegalReferencialBean;
	
	@EJB
	private DocumentosFacade documentosFacade;
    @EJB
    private ContactoFacade contactoFacade;
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	@EJB
	private CoordenadasProyectoCoaFacade coordenadasProyectoCoaFacade;
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
	@EJB
	private InventarioForestalAmbientalFacade inventarioForestalAmbientalFacade;
	@EJB
	private ReporteInventarioForestalFacade reporteInventarioForestalFacade;
	@EJB
	private RegistroGeneradorDesechosProyectosRcoaFacade registroGeneradorDesechosProyectosRcoaFacade;
	@EJB
	private RegistroGeneradorDesechosRcoaFacade registroGeneradorDesechosRcoaFacade;
	@EJB
	private DesechosRegistroGeneradorRcoaFacade desechosRegistroGeneradorRcoaFacade;
	@EJB
	private PermisoRegistroGeneradorDesechosFacade permisoRegistroGeneradorDesechosFacade;
	@EJB
	private PuntoGeneracionDesechoFacade puntoGeneracionDesechoFacade;
	@EJB
	private ViabilidadCoaFacade viabilidadCoaFacade;
	@EJB
	private InformeRevisionForestalFacade informeInspeccionFacade;
	@EJB
	private InformeInspeccionBiodiversidadFacade informeInspeccionSnapFacade;
    @EJB
    private RegistroSustanciaQuimicaFacade registroSustanciaQuimicaFacade;
    @EJB
    private UbicacionSustanciaQuimicaFacade ubicacionSustanciaQuimicaFacade; 
    @EJB
    private PmaViabilidadTecnicaFacade pmaViabilidadTecnicaFacade;

	@EJB
	private PlanManejoAmbientalCoaFacade planManejoAmbientalCoaFacade;

    @Getter
    @Setter
    static private Area areaResponsable;
    
    @Getter
    @Setter
    private ImpresionFichaGeneralBean impresionFichaGeneralBean;
    
    @Getter
    @Setter
    private ProyectoLicenciaCoa proyecto;
    
    @Getter
    @Setter
    private boolean existeError;
    
    @Getter
    @Setter
    private boolean esActividadRelleno, pmaViabilidad;
    
    private boolean existeConstruccion=false, existeoperacion=false, existeCierre=false;
    static private boolean formatoMae=false;
    @PostConstruct
	private void init() throws JbpmException{
    	areaResponsable = null;
    	if(marcoLegalReferencialBean.getRegistroAmbientalRcoa() != null && marcoLegalReferencialBean.getRegistroAmbientalRcoa().getProyectoCoa() != null){
        	proyecto = marcoLegalReferencialBean.getRegistroAmbientalRcoa().getProyectoCoa();
        	areaResponsable = proyecto.getAreaResponsable();
    	}
	}

    public static File generarRegistroAmbientalCoa(final String cadenaHtml,
	                                      final String entityReporte, final String nombreReporte,
	                                      final Boolean mostrarNumeracionPagina) {
	        Document document = null;
	        PdfWriter writer = null;
	        OutputStream fileOutputStream = null;
	        File file = null;
	        String archivo="";
	        try {
	            String buf = entityReporte;
	            file = File.createTempFile(nombreReporte, ".pdf");
	            document = new Document(PageSize.A4, 36, 36, 80, 70);

	            fileOutputStream = new FileOutputStream(file);
	            writer = PdfWriter.getInstance(document, fileOutputStream);
	            writer.createXmpMetadata();
	            writer.setPageEvent(new HeaderFichaAmbientalCoa(null, true, true));
	            document.open();
	            
	            PdfContentByte cb = writer.getDirectContent();
	            archivo=nombreReporte+new Date().getTime()+".pdf";
	            createPdfHtml(buf, System.getProperty("java.io.tmpdir")+"/"+archivo);

	            PdfReader readerF = new PdfReader(System.getProperty("java.io.tmpdir")+"/"+archivo);
	            Integer totalPages = readerF.getNumberOfPages();
	            for (int i = 1; i <= totalPages; i++) {
	                PdfImportedPage page = writer.getImportedPage(readerF, i);
	                document.newPage();
	                cb.addTemplate(page, 0, 0);
	            }

	        } catch (Exception e) {

				JsfUtil.addMessageError("Error al realizar la operación.");
	        } finally {
	            if (document != null && document.isOpen()) {
	                document.close();                
	            }
	            try {

	                if (fileOutputStream != null) {
	                    fileOutputStream.close();
	                }
	            } catch (IOException e) {
	            }
	            if (writer != null && !writer.isCloseStream()) {
	                writer.close();
	            }
	        }
	        if (Constantes.getDocumentosBorrador()) {
	        	UtilFichaMineria.deleteFileTmp(nombreReporte+".pdf");
	            return file;
	        } else {
	        	UtilFichaMineria.deleteFileTmp(nombreReporte+".pdf");
	            return file;
	        }

	}

    public String detalleRegistroGeneradorDesechos() {
    	RegistroGeneradorDesechosRcoa registroGeneradorDesechos = registroGeneradorDesechosRcoaFacade.buscarRGDPorProyectoRcoa(proyecto.getId());
		SimpleDateFormat formatofecha = new SimpleDateFormat("yyyy-MM-dd");
		String fechaRgd="";
		if(registroGeneradorDesechos != null) {
			List<PermisoRegistroGeneradorDesechos> permisoRGD = permisoRegistroGeneradorDesechosFacade.findByRegistroGenerador(registroGeneradorDesechos.getId());
			if(permisoRGD != null && permisoRGD.size() > 0){
				fechaRgd = formatofecha.format(permisoRGD.get(0).getFechaCreacionDocumento());
			}
//			String desechos = "	<p style=\"text-align: justify;\"><span style=\"font-size:12px;\"> <strong>REGISTRO GENERADOR DE DESECHOS PELIGROSOS Y/O ESPECIALES</strong></span> <br />";
			List<DesechosRegistroGeneradorRcoa> listaDesechos = desechosRegistroGeneradorRcoaFacade.buscarPorRegistroGenerador(registroGeneradorDesechos);		
			String desechos =  "<div style=\"text-align: left;\"><p style=\"color: #FFF;\">..<p/>";
			desechos += "<span style=\"font-weight: bold;\">REGISTRO GENERADOR DE DESECHOS PELIGROSOS Y/O ESPECIALES<br/></span>";
			desechos += "<br/>Para este proyecto, obra o actividad se ha otorgado el Registro de Generador con código "
					+registroGeneradorDesechos.getCodigo()+ " de fecha "+fechaRgd+".<br/>";
			/*
			desechos += "<table style=\"width: 100%; font-size: 11px !important;\" border=\"1\" cellpadding=\"3\" cellspacing=\"0\"  class=\"w600Table\" >"
					+ "<tbody><tr>"
					+ "<td><strong>CÓDIGO DEL RESIDUO O DESECHO PELIGROSO Y/O ESPECIAL</strong></td>"
					+ "<td><strong>NOMBRE DEL RESIDUO O DESECHO PELIGROSO Y/O ESPECIAL</strong></td>"
					+ "<td><strong>CRTIB</strong></td>"
					+ "<td><strong>ORIGEN DE LA GENERACIÓN</strong></td>"
					+ "</tr>";
			for (DesechosRegistroGeneradorRcoa desecho : listaDesechos) {
				List<PuntoGeneracionDesecho> listaGeneracion = puntoGeneracionDesechoFacade.buscarPorDesechoRcoa(desecho.getId());
				
				String origen = "";
				for(PuntoGeneracionDesecho punto : listaGeneracion){
					origen += punto.getPuntoGeneracionRgdRcoa().getNombre() + "<br />";
				}
				
				desechos += "<tr>";
				desechos += "<td width=\"20%\">" + desecho.getDesechoPeligroso().getClave() + "</td>";
				desechos += "<td>" + desecho.getDesechoPeligroso().getDescripcion() + "</td>";
				desechos += "<td width=\"10%\">" + desecho.getDesechoPeligroso().getCritb() + "</td>";
				desechos += "<td width=\"25%\">" + origen + "</td>";
				desechos += "</tr>";
			}
			desechos += "</tbody></table>";
			*/
			return desechos+"</div>";
		}
		
		return null;
    }
    
    public String detalleViabilidad() {
    	String viabilidad = viabilidadCoaFacade.getDetalleInfoViabilidad(proyecto);
    	return viabilidad;
    } 
    
	private String detalleRegistroSustanciasQuimicas(){
		try {
			 StringBuilder tablaStandar = new StringBuilder();
			 String desechos="";
				RegistroSustanciaQuimica registroSustanciaQuimica =registroSustanciaQuimicaFacade.obtenerRegistroPorProyecto(proyecto);
				List<UbicacionSustancia> ubicacionSustanciaProyectoLista = new ArrayList<UbicacionSustancia>();
				if(registroSustanciaQuimica!=null) {		
					ubicacionSustanciaProyectoLista=ubicacionSustanciaQuimicaFacade.obtenerUbicacionesPorRSQ(registroSustanciaQuimica);				
				}		
			 if(ubicacionSustanciaProyectoLista != null && ubicacionSustanciaProyectoLista.size() > 0){
				desechos =  "<div style=\"text-align: left;\"><p style=\"color: #FFF;\">..<p/>";
				desechos += "<span style=\"font-weight: bold;\">REGISTRO O RENOVACIÓN DE SUSTANCIAS QUIMÍCAS PELIGROSAS<br/></span>";
				desechos += "<table style=\"width: 100%; font-size: 11px !important;\" border=\"1\" cellpadding=\"3\" cellspacing=\"0\"  class=\"w600Table\" >"
						+ "<tbody><tr>"
						+ "<td><strong>NOMBRE DE LA SUSTANCIA</strong></td>"
						+ "<td><strong>NÚMERO CAS</strong></td>"
						+ "<td><strong>ACTIVIDADES DE CADA SUSTANCIA</strong></td>"
						+ "<td><strong>NÚMERO DE REGISTRO</strong></td>"
						+ "</tr>";
				 for (UbicacionSustancia ubicacionSustancia : ubicacionSustanciaProyectoLista) {
					 
						desechos += "<tr>";
						desechos += "<td width=\"20%\">" + ubicacionSustancia.getGestionarProductosQuimicosProyectoAmbiental().getSustanciaquimica().getDescripcion() + "</td>";
						desechos += "<td>" + ubicacionSustancia.getGestionarProductosQuimicosProyectoAmbiental().getSustanciaquimica().getNumeroCas() + "</td>";
						desechos += "<td width=\"10%\">" + "" + "</td>";
						desechos += "<td width=\"25%\">" + "" + "</td>";
						desechos += "</tr>";
				}

					desechos += "</table>";
			 }
				tablaStandar.append(desechos);
			return tablaStandar.toString();
		 } catch (Exception e) {
			 existeError = true;
			 return "";
		 }
	}

	private String detalleInventarioForestal(){
		try {
			 StringBuilder tablaStandar = new StringBuilder();
			 InventarioForestalAmbiental inventario = inventarioForestalAmbientalFacade.getByIdRegistroPreliminar(proyecto.getId());
			 if(inventario != null){
				ReporteInventarioForestal reporte = reporteInventarioForestalFacade.getByIdCertificadoByIdTipoDocumento(inventario.getId(), 1);
				if(reporte != null) {
					tablaStandar.append("<div style=\"text-align: left;\">");
					tablaStandar.append("<span style=\"font-weight: bold;font-size: small;\"><br/>INVENTARIO FORESTAL<br/>");
					tablaStandar.append("<RECOMENDACIONES<br/>");
					tablaStandar.append("</span>");
					tablaStandar.append("</div>");
					tablaStandar.append("<br/>");

					tablaStandar.append("<p style=\" text-align: justify\">"+(reporte.getRecomendaciones() == null?"":reporte.getRecomendaciones())+"</p>");
					if(reporte.getRecomendaciones() == null){
						tablaStandar = new StringBuilder();
					}
				}
			 }
			return tablaStandar.toString();
		 } catch (Exception e) {
			 existeError = true;
			 return "";
		 }
	}
	
	private String detalleActividadesProyecto(){
		try {
			int i = 0;
			 StringBuilder tablaStandar = new StringBuilder();
			 tablaStandar.append("<p style=\"margin-top: -10px; margin-bottom: -10px; text-align: justify; \">");
			 List<ProyectoLicenciaCuaCiuu> listaactividades = proyectoLicenciaCuaCiuuFacade.actividadesPorProyecto(proyecto);
			 if(listaactividades.size() > 0){
				for (ProyectoLicenciaCuaCiuu objActividad : listaactividades) {
					tablaStandar.append((tablaStandar.toString().equals("")?"":"<br/>"));
					if (i==0) {
						tablaStandar.append("Actividad principal CIIU: ");
					}else if (i==1) {
						tablaStandar.append("<br/>Actividad complementaria 1 CIIU: ");
					}else if (i==2) {
						tablaStandar.append("<br/>Actividad complementaria 2 CIIU: ");
					}
					tablaStandar.append(objActividad.getCatalogoCIUU().getNombre()+"");
					i++;
				}
				tablaStandar.append("</p>");
			 }
			return tablaStandar.toString();
		 } catch (Exception e) {
			 existeError = true;
			 return "";
		 }
	}
	
	private String detalleCoordenadasnProyecto(){
		try {
			 StringBuilder tablaStandar = new StringBuilder();
			 List<CoordenadasProyecto> coordenadas = coordenadasProyectoCoaFacade.buscarPorFormaPorProyecto(proyecto);
			 if(coordenadas.size() > 0){
				 String cabeza="";
				 //String[] columnas = {"Este (X)", "Norte (Y)", "Altitud"};
				 String[] columnas = {"Este (X)", "Norte (Y)"};
				 String titulo="";
				 tablaStandar.append(cabeceraTabla(cabeza, titulo, columnas));
				for (CoordenadasProyecto objCoordenadas : coordenadas) {
					tablaStandar.append("<tr>");
					tablaStandar.append("<td style=\" text-align: left\">"+objCoordenadas.getX()+"</td>");
					tablaStandar.append("<td style=\"width: 30%; text-align: left\">"+objCoordenadas.getY()+"</td>");
					//tablaStandar.append("<td style=\"width: 30%; text-align: left\">"+"0"+"</td>");
					tablaStandar.append("</tr>");
				}
				tablaStandar.append("</tbody></table>");
			 }
			return tablaStandar.toString();
		 } catch (Exception e) {
			 existeError = true;
			 return "";
		 }
	}
	
	private String detalleUbicacionProyecto(){
		try {
			 StringBuilder tablaStandar = new StringBuilder();
			 List<UbicacionesGeografica> ubicacion = proyectoLicenciaCoaUbicacionFacade.buscarPorProyecto(proyecto);
			 if(ubicacion.size() > 0){
				 String cabeza="";
				 String[] columnas = {"Provincia", "Cantón", "Parroquia"};
				 String titulo="";
				 tablaStandar.append(cabeceraTabla(cabeza, titulo, columnas));
				for (UbicacionesGeografica objUbicacion : ubicacion) {
					tablaStandar.append("<tr>");
					tablaStandar.append("<td style=\" text-align: left\">"+objUbicacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre()+"</td>");
					tablaStandar.append("<td style=\"width: 30%; text-align: left\">"+objUbicacion.getUbicacionesGeografica().getNombre()+"</td>");
					tablaStandar.append("<td style=\"width: 30%; text-align: left\">"+objUbicacion.getNombre()+"</td>");
					tablaStandar.append("</tr>");
				}
				tablaStandar.append("</tbody></table>");
			 }
			return tablaStandar.toString();
		 } catch (Exception e) {
			 existeError = true;
			 return "";
		 }
	}

	private String detalleFasesProyecto(){
		try {
			existeConstruccion=false;
			existeoperacion=false;
			 StringBuilder tablaStandar = new StringBuilder();
			 SimpleDateFormat formatofecha = new SimpleDateFormat("yyyy-MM-dd");
			 // 2. DESCRIPCIÓN DEL PROYECTO FASES: CONSTRUCCIÓN, REHABILITACIÓN Y/O AMPLIACIÓN, OPERACIÓN Y MANTENIMIENTO, CIERRE Y ABANDONO
			 FaseRegistroAmbientalController FaseRegistroAmbientalController = JsfUtil.getBean(FaseRegistroAmbientalController.class);
			 if(!esActividadRelleno){
				 List<FasesRegistroAmbiental> listaFases = FaseRegistroAmbientalController.getListaFasesRegistroAmbiental();
				 if(listaFases.size() > 0){
					 String cabeza="";
					 String titulo="";
					 for (FasesRegistroAmbiental objFase : listaFases) {
						 if(objFase.getFasesCoa().getId().toString().equals("1")){
							 existeConstruccion=true;
						 }
						 if(objFase.getFasesCoa().getId().toString().equals("2")){
							 existeoperacion=true;
						 }
					 }
					 if(existeConstruccion){
						 String[] columnas = {"Etapa", "Fecha inicio", "Fecha fin", "Descripción"};
						 tablaStandar.append(cabeceraTabla(cabeza, titulo, columnas));
						for (FasesRegistroAmbiental objFase : listaFases) {
							tablaStandar.append("<tr>");
							tablaStandar.append("<td style=\"width: 30%; text-align: left\">"+objFase.getFasesCoa().getDescripcion()+"</td>");
							tablaStandar.append("<td style=\"width: 15%; text-align: center\">"+(objFase.getFechaInicio() != null ? formatofecha.format(objFase.getFechaInicio()): "")+"</td>");
							tablaStandar.append("<td style=\"width: 15%; text-align: center\">"+(objFase.getFechaFin() != null ? formatofecha.format(objFase.getFechaFin()) :"")+"</td>");
							tablaStandar.append("<td style=\"width: 40%; text-align: justify\">"+objFase.getDescripcion()+"</td>");
							tablaStandar.append("</tr>");
						}
					 }else{
						 String[] columnas = {"Etapa", "Descripción"};
						 tablaStandar.append(cabeceraTabla(cabeza, titulo, columnas));
						for (FasesRegistroAmbiental objFase : listaFases) {
							tablaStandar.append("<tr>");
							tablaStandar.append("<td style=\"width: 30%; text-align: left\">"+objFase.getFasesCoa().getDescripcion()+"</td>");
							tablaStandar.append("<td style=\"text-align: justify\">"+objFase.getDescripcion()+"</td>");
							tablaStandar.append("</tr>");
						}
					 }
					tablaStandar.append("</tbody></table>");
					//valido si tiene pma de cierre para la actividad principal
					existeCierre=FaseRegistroAmbientalController.validarActividadConPmaCierre();
				 }
			 }else{
				 List<FaseViabilidadTecnicaRcoa> listaFases = FaseRegistroAmbientalController.getListaFasesRegistroAmbientalViabilidad();
				 if(listaFases.size() > 0){
					 String cabeza="";
					 String titulo="";
					 for(FaseViabilidadTecnicaRcoa objFase : listaFases){
						 if(objFase.getFasesCoa().getId().toString().equals("1")){
							 existeConstruccion=true;
						 }
						 if(objFase.getFasesCoa().getId().toString().equals("2")){
							 existeoperacion=true;
						 }
					 }
					 if(existeConstruccion){
						 String[] columnas = {"Fase", "Fecha inicio", "Fecha fin", "Descripción"};
						 tablaStandar.append(cabeceraTabla(cabeza, titulo, columnas));
						for (FaseViabilidadTecnicaRcoa objFase : listaFases) {
							tablaStandar.append("<tr>");
							tablaStandar.append("<td style=\"width: 30%; text-align: left\">"+objFase.getFasesCoa().getDescripcion()+"</td>");
							tablaStandar.append("<td style=\"width: 15%; text-align: center\">"+(objFase.getFechaInicio() != null ? formatofecha.format(objFase.getFechaInicio()): "")+"</td>");
							tablaStandar.append("<td style=\"width: 15%; text-align: center\">"+(objFase.getFechaFin() != null ? formatofecha.format(objFase.getFechaFin()) :"")+"</td>");
							tablaStandar.append("<td style=\"width: 40%; text-align: justify\">"+objFase.getDescripcion()+"</td>");
							tablaStandar.append("</tr>");
						}
					 }else{
						 String[] columnas = {"Fase", "Descripción"};
						 tablaStandar.append(cabeceraTabla(cabeza, titulo, columnas));
						for (FaseViabilidadTecnicaRcoa objFase : listaFases) {
							tablaStandar.append("<tr>");
							tablaStandar.append("<td style=\"width: 30%; text-align: left\">"+objFase.getFasesCoa().getDescripcion()+"</td>");
							tablaStandar.append("<td style=\"text-align: justify\">"+objFase.getDescripcion()+"</td>");
							tablaStandar.append("</tr>");
						}
					 }
					tablaStandar.append("</tbody></table>");
				 }				 
			 }
			 
			return tablaStandar.toString();
		 } catch (Exception e) {
			 existeError = true;
			 return "";
		 }
	}
	
	private String detallePlan(){
		try {
			 StringBuilder tablaStandar = new StringBuilder();
			 Double valor = 12.5, ponderacion=0.00;
			 SimpleDateFormat fecha= new SimpleDateFormat("yyyy-MM-dd", new Locale("es"));
			 // PLAN DE MANEJO AMBIENTAL CONSTRUCCION
			 if(!pmaViabilidad){
				 if(existeConstruccion){
					 boolean existeEliminado = false;
					 PlanManejoAmbientalConstruccionCoaController planManejoAmbientalConstruccionCoaController = JsfUtil.getBean(PlanManejoAmbientalConstruccionCoaController.class);
					 List<EntidadPma> listaPmaAceptado  = planManejoAmbientalConstruccionCoaController.getListaPlanPma();
					 Integer tipoPlan = 1;
					 List<EntidadPma> listaPma = planManejoAmbientalCoaFacade.obtenerPmaPorRegistroAmbiental(marcoLegalReferencialBean.getRegistroAmbientalRcoa(), tipoPlan, true);
					 if(listaPma.size() > 0){
						 String titulo="";
						 tablaStandar.append("<span style=\"font-weight: bold;\"><br/>PLAN DE MANEJO AMBIENTAL - CONSTRUCCIÓN</span>");
						 String[] columnas = {"Aspecto Ambiental", "Medida Propuesta", "Medio de verificación de la medida", "Frecuencia", "Plazo", "Ponderacion"};
						 String[] columnasTodas = {"Aspecto Ambiental", "Medida Propuesta", "Medio de verificación de la medida", "Frecuencia", "Plazo", "Ponderacion", ""};
						 for (EntidadPma entidadPlan : listaPma){
							 ponderacion=0.0;
							 for (EntidadPma objPlan : listaPmaAceptado) {
								 if(entidadPlan.getPlanId().equals(objPlan.getPlanId())){
									 existeEliminado = (entidadPlan.getMedidasProyecto().size() != objPlan.getMedidasProyecto().size());
									 ponderacion = valor/ objPlan.getMedidasProyecto().size();
									 ponderacion = Math.round(ponderacion*100.0)/100.0;
									 break;
								 }
							 }
							 if(entidadPlan.getMedidasProyecto() != null && entidadPlan.getMedidasProyecto().size() > 0){
								 titulo = entidadPlan.getPlanNombre();
								 if(existeEliminado)
									tablaStandar.append(cabeceraTabla(null, titulo, columnasTodas));
								 else
									 tablaStandar.append(cabeceraTabla(null, titulo, columnas));
								for (PmaAceptadoRegistroAmbiental medida: entidadPlan.getMedidasProyecto()){
										tablaStandar.append("<tr>");
										tablaStandar.append("<td style=\"width: 20%; text-align: left\">"+(medida.getMedidaVerificacionPma() != null ? medida.getMedidaVerificacionPma().getAspectoAmbientalPma().getDescripcion() : medida.getAspectoAmbientalPma().getDescripcion())+"</td>");
										tablaStandar.append("<td style=\"width: 25%; text-align: center\">"+(medida.getMedidaVerificacionPma() != null ? medida.getMedidaVerificacionPma().getDescripcion() : medida.getMedidaPropuesta())+"</td>");
										tablaStandar.append("<td style=\"width: 25%; text-align: center\">"+(medida.getMedidaVerificacionPma() != null ? medida.getMedidaVerificacionPma().getMedidaVerificacion() : medida.getMedida())+"</td>");
										tablaStandar.append("<td style=\"width: 7%; text-align: justify\">"+(medida.getMedidaVerificacionPma() != null ? (medida.getMedidaVerificacionPma().getFrecuencia().toUpperCase().contains("RGD QUE OBTUVO")?planManejoAmbientalConstruccionCoaController.getCodigoRGD(): medida.getMedidaVerificacionPma().getFrecuencia()) : medida.getFrecuencia())+"</td>");
										if(medida.getMedidaVerificacionPma() == null){
											tablaStandar.append("<td style=\"width: 7%; text-align: justify\">"+(medida.getPlazoFecha() == null ? "": fecha.format(medida.getPlazoFecha()))+"</td>");
										}else{
											if(!medida.getMedidaVerificacionPma().isAplicaPlazo()){
												tablaStandar.append("<td style=\"width: 7%; text-align: justify\">"+"NO APLICA"+"</td>");
											}else {
												if(medida.getMedidaVerificacionPma().isTipoPlazo())
													tablaStandar.append("<td style=\"width: 7%; text-align: justify\">"+medida.getPlazo()+"</td>");
												else
													tablaStandar.append("<td style=\"width: 7%; text-align: justify\">"+(medida.getPlazoFecha() != null ? fecha.format(medida.getPlazoFecha()) : "")+"</td>");
											}
										}
										tablaStandar.append("<td style=\"width: 7%; text-align: rigth\">"+(medida.isAceptado() ? ponderacion : "" )+"</td>");
										if(existeEliminado)
											tablaStandar.append("<td style=\"width: 7%; text-align: rigth\">"+(medida.isAceptado()?"":"MEDIDA NO APLICA<br/>"+medida.getJustificacion())+"</td>");
										tablaStandar.append("</tr>");	
								}
								tablaStandar.append("</tbody></table><br/>");
							 }
						}
					 }
				}

				 // PLAN DE MANEJO AMBIENTAL OPERACION
				 if(existeoperacion){
					 boolean existeEliminado = false;
					 PlanManejoAmbientalOperacionCoaController planManejoAmbientalOperacionCoaController = JsfUtil.getBean(PlanManejoAmbientalOperacionCoaController.class);
					 List<EntidadPma> listaPmaAceptado  = planManejoAmbientalOperacionCoaController.getListaPlanPma();
					 Integer tipoPlan = 2;
					 List<EntidadPma> listaPmaOperacion = planManejoAmbientalCoaFacade.obtenerPmaPorRegistroAmbiental(marcoLegalReferencialBean.getRegistroAmbientalRcoa(), tipoPlan, true);
					 if(listaPmaOperacion.size() > 0){
						 String titulo="";
						 tablaStandar.append("<span style=\"font-weight: bold;\"><br/>PLAN DE MANEJO AMBIENTAL - OPERACIÓN</span>");
						 String[] columnas = {"Aspecto Ambiental", "Medida Ambiental", "Medio de verificación", "Frecuencia", "Ponderación"};
						 String[] columnasTodas = {"Aspecto Ambiental", "Medida Propuesta", "Medio de verificación de la medida", "Frecuencia", "Ponderación", ""};
						 for (EntidadPma entidadPlan : listaPmaOperacion){
							 ponderacion=0.0;
							 for (EntidadPma objPlan : listaPmaAceptado) {
								 if(entidadPlan.getPlanId().equals(objPlan.getPlanId())){
									 existeEliminado = (entidadPlan.getMedidasProyecto().size() != objPlan.getMedidasProyecto().size());
									 ponderacion = valor/ objPlan.getMedidasProyecto().size();
									 ponderacion = Math.round(ponderacion*100.0)/100.0;
									 break;
								 }
							 }
							 if(entidadPlan.getMedidasProyecto() != null && entidadPlan.getMedidasProyecto().size() > 0){
								 	titulo = entidadPlan.getPlanNombre();
									if(existeEliminado)
										tablaStandar.append(cabeceraTabla(null, titulo, columnasTodas));
									else
										 tablaStandar.append(cabeceraTabla(null, titulo, columnas));
									for (PmaAceptadoRegistroAmbiental medida: entidadPlan.getMedidasProyecto()){
											tablaStandar.append("<tr>");
											tablaStandar.append("<td style=\"width: 20%; text-align: left\">"+(medida.getMedidaVerificacionPma() != null ? medida.getMedidaVerificacionPma().getAspectoAmbientalPma().getDescripcion() : medida.getAspectoAmbientalPma().getDescripcion())+"</td>");
											tablaStandar.append("<td style=\"width: 25%; text-align: center\">"+(medida.getMedidaVerificacionPma() != null ? medida.getMedidaVerificacionPma().getDescripcion() : medida.getMedidaPropuesta())+"</td>");
											tablaStandar.append("<td style=\"width: 25%; text-align: center\">"+(medida.getMedidaVerificacionPma() != null ? medida.getMedidaVerificacionPma().getMedidaVerificacion() : medida.getMedida())+"</td>");
											tablaStandar.append("<td style=\"width: 7%; text-align: justify\">"+(medida.getMedidaVerificacionPma() != null ? (medida.getMedidaVerificacionPma().getFrecuencia().toUpperCase().contains("RGD QUE OBTUVO")?medida.getFrecuencia(): medida.getMedidaVerificacionPma().getFrecuencia()) : medida.getFrecuencia())+"</td>");
											tablaStandar.append("<td style=\"width: 7%; text-align: rigth\">"+(medida.isAceptado() ? ponderacion : "" )+"</td>");
											if(existeEliminado)
												tablaStandar.append("<td style=\"width: 7%; text-align: rigth\">"+(medida.isAceptado()?"":"MEDIDA NO APLICA<br/>"+medida.getJustificacion())+"</td>");
											tablaStandar.append("</tr>");	
									}
									tablaStandar.append("</tbody></table>");
							 }
						}
					 }
				 }
				 
				 // PLAN DE MANEJO AMBIENTAL CIERRE
				 if(existeCierre){
					 boolean existeEliminado = false;
					 PlanManejoAmbientalCierreCoaController planManejoAmbientalCierreCoaController = JsfUtil.getBean(PlanManejoAmbientalCierreCoaController.class);
					 List<EntidadPma> listaPmaAceptado  = planManejoAmbientalCierreCoaController.getListaPlanPma();
					 Integer tipoPlan = 3;
					 List<EntidadPma> listaPmaOperacion = planManejoAmbientalCoaFacade.obtenerPmaPorRegistroAmbiental(marcoLegalReferencialBean.getRegistroAmbientalRcoa(), tipoPlan, true);
					 if(listaPmaOperacion.size() > 0){
						 String titulo="";
						 tablaStandar.append("<br/><span style=\"font-weight: bold;\"><br/>PLAN DE MANEJO AMBIENTAL - CIERRE</span>");
						 String[] columnas = {"Aspecto Ambiental", "Medida Ambiental", "Medio de verificación", "Frecuencia", "Ponderación"};
						 String[] columnasTodas = {"Aspecto Ambiental", "Medida Propuesta", "Medio de verificación de la medida", "Frecuencia", "Ponderación", ""};
						 for (EntidadPma entidadPlan : listaPmaOperacion){
							 ponderacion=0.0;
							 for (EntidadPma objPlan : listaPmaAceptado) {
								 if(entidadPlan.getPlanId().equals(objPlan.getPlanId())){
									 existeEliminado = (entidadPlan.getMedidasProyecto().size() != objPlan.getMedidasProyecto().size());
									 ponderacion = valor/ objPlan.getMedidasProyecto().size();
									 ponderacion = Math.round(ponderacion*100.0)/100.0;
									 break;
								 }
							 }
							 if(entidadPlan.getMedidasProyecto() != null && entidadPlan.getMedidasProyecto().size() > 0){
								 	titulo = entidadPlan.getPlanNombre();
									if(existeEliminado)
										tablaStandar.append(cabeceraTabla(null, titulo, columnasTodas));
									else
										 tablaStandar.append(cabeceraTabla(null, titulo, columnas));
									for (PmaAceptadoRegistroAmbiental medida: entidadPlan.getMedidasProyecto()){
											tablaStandar.append("<tr>");
											tablaStandar.append("<td style=\"width: 20%; text-align: left\">"+(medida.getMedidaVerificacionPma() != null ? medida.getMedidaVerificacionPma().getAspectoAmbientalPma().getDescripcion() : medida.getAspectoAmbientalPma().getDescripcion())+"</td>");
											tablaStandar.append("<td style=\"width: 25%; text-align: center\">"+(medida.getMedidaVerificacionPma() != null ? medida.getMedidaVerificacionPma().getDescripcion() : medida.getMedidaPropuesta())+"</td>");
											tablaStandar.append("<td style=\"width: 25%; text-align: center\">"+(medida.getMedidaVerificacionPma() != null ? medida.getMedidaVerificacionPma().getMedidaVerificacion() : medida.getMedida())+"</td>");
											tablaStandar.append("<td style=\"width: 7%; text-align: justify\">"+(medida.getMedidaVerificacionPma() != null ? (medida.getMedidaVerificacionPma().getFrecuencia().toUpperCase().contains("RGD QUE OBTUVO")?medida.getFrecuencia(): medida.getMedidaVerificacionPma().getFrecuencia()) : medida.getFrecuencia())+"</td>");
											tablaStandar.append("<td style=\"width: 7%; text-align: rigth\">"+(medida.isAceptado() ? ponderacion : "" )+"</td>");
											if(existeEliminado)
												tablaStandar.append("<td style=\"width: 7%; text-align: rigth\">"+(medida.isAceptado()?"":"MEDIDA NO APLICA<br/>"+medida.getJustificacion())+"</td>");
											tablaStandar.append("</tr>");	
									}
									tablaStandar.append("</tbody></table>");
							 }
						}
					 }
				 }
			 }else{
				 /**METODOS PARA VIABILIDAD  TECNICA*/
				 if(existeConstruccion){
					 boolean existeEliminado = false;
					 PlanManejoAmbientalConstruccionCoaController planManejoAmbientalConstruccionCoaController = JsfUtil.getBean(PlanManejoAmbientalConstruccionCoaController.class);
					 List<EntidadPmaViabilidad> listaPmaAceptado  = planManejoAmbientalConstruccionCoaController.getListaPlanPmaVia();
					 Integer tipoPlan = 1;
					 List<EntidadPmaViabilidad> listaPma = pmaViabilidadTecnicaFacade.obtenerPmaPorViabilidaTecnica(tipoPlan, planManejoAmbientalConstruccionCoaController.getViabilidadTecnica().getId());
							 
					 if(listaPma.size() > 0){
						 String titulo="";
						 tablaStandar.append("<span style=\"font-weight: bold;\"><br/>PLAN DE MANEJO AMBIENTAL - CONSTRUCCIÓN</span>");
						 String[] columnas = {"Aspecto Ambiental", "Medida Propuesta", "Medio de verificación de la medida", "Frecuencia", "Plazo", "Ponderacion"};
						 String[] columnasTodas = {"Aspecto Ambiental", "Medida Propuesta", "Medio de verificación de la medida", "Frecuencia", "Plazo", "Ponderacion", ""};
						 for (EntidadPmaViabilidad entidadPlan : listaPma){
							 ponderacion=0.0;
							 for (EntidadPmaViabilidad objPlan : listaPmaAceptado) {
								 if(entidadPlan.getPlanId().equals(objPlan.getPlanId())){
									 existeEliminado = (entidadPlan.getMedidasProyecto().size() != objPlan.getMedidasProyecto().size());
									 ponderacion = valor/ objPlan.getMedidasProyecto().size();
									 ponderacion = Math.round(ponderacion*100.0)/100.0;
									 break;
								 }
							 }
							 if(entidadPlan.getMedidasProyecto() != null && entidadPlan.getMedidasProyecto().size() > 0){
								 titulo = entidadPlan.getPlanNombre();
								 if(existeEliminado)
									tablaStandar.append(cabeceraTabla(null, titulo, columnasTodas));
								 else
									 tablaStandar.append(cabeceraTabla(null, titulo, columnas));
								for (PmaViabilidadTecnica medida: entidadPlan.getMedidasProyecto()){
										tablaStandar.append("<tr>");
										tablaStandar.append("<td style=\"width: 20%; text-align: left\">"+(medida.getMedioFrecuenciaMedida() != null ? medida.getMedioFrecuenciaMedida().getAspectoViabilidad().getAspecto() : medida.getAspectoViabilidadTecnica().getAspecto())+"</td>");
										tablaStandar.append("<td style=\"width: 25%; text-align: center\">"+(medida.getMedioFrecuenciaMedida() != null ? medida.getMedioFrecuenciaMedida().getMedidaPropuesta() : medida.getMedidaPropuesta())+"</td>");
										tablaStandar.append("<td style=\"width: 25%; text-align: center\">"+(medida.getMedioFrecuenciaMedida() != null ? medida.getMedioFrecuenciaMedida().getMedioVerificacion() : medida.getMedioVerificacion())+"</td>");
										tablaStandar.append("<td style=\"width: 7%; text-align: justify\">"+(medida.getMedioFrecuenciaMedida() != null ? (medida.getMedioFrecuenciaMedida().getFrecuencia().toUpperCase().contains("RGD QUE OBTUVO")?planManejoAmbientalConstruccionCoaController.getCodigoRGD(): medida.getMedioFrecuenciaMedida().getFrecuencia()) : medida.getFrecuencia())+"</td>");
										if(medida.getMedioFrecuenciaMedida() == null){
											tablaStandar.append("<td style=\"width: 7%; text-align: justify\">"+(medida.getPlazo() == null ? "": medida.getPlazo())+"</td>");
										}else{
											if(!medida.getMedioFrecuenciaMedida().getPlazo()){
												tablaStandar.append("<td style=\"width: 7%; text-align: justify\">"+"NO APLICA"+"</td>");
											}else {												
												tablaStandar.append("<td style=\"width: 7%; text-align: justify\">"+(medida.getPlazo() != null ? medida.getPlazo() : "")+"</td>");
											}
										}
										tablaStandar.append("<td style=\"width: 7%; text-align: rigth\">"+(medida.isAceptado() ? ponderacion : "" )+"</td>");
										if(existeEliminado)
											tablaStandar.append("<td style=\"width: 7%; text-align: rigth\">"+(medida.isAceptado()?"":"MEDIDA NO APLICA<br/>"+medida.getJustificacion())+"</td>");
										tablaStandar.append("</tr>");	
								}
								tablaStandar.append("</tbody></table><br/>");
							 }
						}
					 }
				}

				 // PLAN DE MANEJO AMBIENTAL OPERACION
				 if(existeoperacion){
					 boolean existeEliminado = false;
					 PlanManejoAmbientalOperacionCoaController planManejoAmbientalOperacionCoaController = JsfUtil.getBean(PlanManejoAmbientalOperacionCoaController.class);
					 List<EntidadPmaViabilidad> listaPmaAceptado  = planManejoAmbientalOperacionCoaController.getListaPlanPmaVia();
					 Integer tipoPlan = 2;
					 List<EntidadPmaViabilidad> listaPmaOperacion = pmaViabilidadTecnicaFacade.obtenerPmaPorViabilidaTecnica(tipoPlan, planManejoAmbientalOperacionCoaController.getViabilidadTecnica().getId());
					 if(listaPmaOperacion.size() > 0){
						 String titulo="";
						 tablaStandar.append("<span style=\"font-weight: bold;\"><br/>PLAN DE MANEJO AMBIENTAL - OPERACIÓN</span>");
						 String[] columnas = {"Aspecto Ambiental", "Medida Ambiental", "Medio de verificación", "Frecuencia", "Ponderación"};
						 String[] columnasTodas = {"Aspecto Ambiental", "Medida Propuesta", "Medio de verificación de la medida", "Frecuencia", "Ponderación", ""};
						 for (EntidadPmaViabilidad entidadPlan : listaPmaOperacion){
							 ponderacion=0.0;
							 for (EntidadPmaViabilidad objPlan : listaPmaAceptado) {
								 if(entidadPlan.getPlanId().equals(objPlan.getPlanId())){
									 existeEliminado = (entidadPlan.getMedidasProyecto().size() != objPlan.getMedidasProyecto().size());
									 ponderacion = valor/ objPlan.getMedidasProyecto().size();
									 ponderacion = Math.round(ponderacion*100.0)/100.0;
									 break;
								 }
							 }
							 if(entidadPlan.getMedidasProyecto() != null && entidadPlan.getMedidasProyecto().size() > 0){
								 	titulo = entidadPlan.getPlanNombre();
									if(existeEliminado)
										tablaStandar.append(cabeceraTabla(null, titulo, columnasTodas));
									else
										 tablaStandar.append(cabeceraTabla(null, titulo, columnas));
									for (PmaViabilidadTecnica medida: entidadPlan.getMedidasProyecto()){
											tablaStandar.append("<tr>");
											tablaStandar.append("<td style=\"width: 20%; text-align: left\">"+(medida.getMedioFrecuenciaMedida() != null ? medida.getMedioFrecuenciaMedida().getAspectoViabilidad().getAspecto() : medida.getAspectoViabilidadTecnica().getAspecto())+"</td>");
											tablaStandar.append("<td style=\"width: 25%; text-align: center\">"+(medida.getMedioFrecuenciaMedida() != null ? medida.getMedioFrecuenciaMedida().getMedidaPropuesta() : medida.getMedidaPropuesta())+"</td>");
											tablaStandar.append("<td style=\"width: 25%; text-align: center\">"+(medida.getMedioFrecuenciaMedida() != null ? medida.getMedioFrecuenciaMedida().getMedioVerificacion() : medida.getMedioVerificacion())+"</td>");
											tablaStandar.append("<td style=\"width: 7%; text-align: justify\">"+(medida.getMedioFrecuenciaMedida() != null ? (medida.getMedioFrecuenciaMedida().getFrecuencia().toUpperCase().contains("RGD QUE OBTUVO")?medida.getFrecuencia(): medida.getMedioFrecuenciaMedida().getFrecuencia()) : medida.getFrecuencia())+"</td>");
											tablaStandar.append("<td style=\"width: 7%; text-align: rigth\">"+(medida.isAceptado() ? ponderacion : "" )+"</td>");											
											if(existeEliminado)
												tablaStandar.append("<td style=\"width: 7%; text-align: rigth\">"+(medida.isAceptado()?"":"MEDIDA NO APLICA<br/>"+medida.getJustificacion())+"</td>");
											tablaStandar.append("</tr>");	
									}
									tablaStandar.append("</tbody></table>");
							 }
						}
					 }
				 }
			 }
			return tablaStandar.toString();
		 } catch (Exception e) {
			 e.printStackTrace();
			 existeError = true;
			 return "";
		 }
	}
	
    private String cabeceraTabla(String cabeza, String titulo, String[] columnas){
		 StringBuilder tablaStandar = new StringBuilder();
		 if (cabeza != null && !cabeza.isEmpty()) {
			tablaStandar.append("<table align=\"left\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"width:100%\">");
			tablaStandar.append("<tbody><tr>");
			tablaStandar.append("<td style=\"width: 100%; text-align: left\"> <div style=\"font-size: small;font-weight: bold;background-color: inherit;\">");
			tablaStandar.append(cabeza);
			tablaStandar.append("<br/></div></td>");
			tablaStandar.append("</tr>");
			tablaStandar.append("</tbody></table>");
		}
		 if(columnas != null && columnas.length > 0){
			tablaStandar.append("<table align=\"left\" border=\"1\" cellpadding=\"1\" cellspacing=\"1\" style=\"width:100%; font-size: small; \">");
			tablaStandar.append("<tbody>");
			if(!titulo.isEmpty())
				tablaStandar.append("<tr><td style=\"font-weight: bold; text-align: center\" colspan=\""+columnas.length+"\">"+titulo+"</td></tr> ");
			tablaStandar.append("<tr>");
			for (String s : columnas) {
				tablaStandar.append("<td style=\"font-weight: bold; text-align: center\"> <div style=\"font-size: small\">");
				tablaStandar.append(s);
				tablaStandar.append("</div></td>");
			}
			tablaStandar.append("</tr>");
		 }
    	return tablaStandar.toString();
    }


    static class HeaderFichaAmbientalCoa extends PdfPageEventHelper {

        String[] params;
        Boolean mostrarNumeracionPagina;
        Boolean mostrarDireccionMae;
        /**
         * The template with the total number of pages.
         */
        PdfTemplate total;

        public HeaderFichaAmbientalCoa() {
        }

        public HeaderFichaAmbientalCoa(String[] params,
                                           Boolean mostrarNumeracionPagina, Boolean mostrarDireccionMae) {
            this.params = params;
            this.mostrarNumeracionPagina = mostrarNumeracionPagina;
            this.mostrarDireccionMae = mostrarDireccionMae;
        }

        /**
         * Creates the PdfTemplate that will hold the total number of pages.
         *
         * @see com.itextpdf.text.pdf.PdfPageEventHelper#onOpenDocument(com.itextpdf.text.pdf.PdfWriter,
         * com.itextpdf.text.Document)
         */
        public void onOpenDocument(PdfWriter writer, Document document) {
            total = writer.getDirectContent().createTemplate(30, 14);
        }

        public void onCloseDocument(PdfWriter writer, Document document) {
            ColumnText.showTextAligned(total, Element.ALIGN_LEFT, new Phrase(
                    String.valueOf(writer.getPageNumber() - 1), FontFactory.getFont (FontFactory.HELVETICA, 10, Font.BOLD, BaseColor.GRAY)), 2, 2, 0);
        }


        public void onEndPage(PdfWriter writer, Document document) {
            Image imghead = null;
            Image imgbackground = null;
            String nombre_logo ="";
            try {
                DocumentosFacade docf = BeanLocator.getInstance(DocumentosFacade.class);
                if(areaResponsable != null){
               		nombre_logo = "logo__" + areaResponsable.getAreaAbbreviation().replace("/", "_") + ".png";	
                }
                //nombre_logo = "logo__GADPS.png";
           		byte[] logo_datos = null;
                try {
                    logo_datos = docf.descargarDocumentoPorNombre(nombre_logo);
                } catch (CmisAlfrescoException e) {
                    // TODO Auto-generated catch block
//							e.printStackTrace();
                }
                if (logo_datos == null || formatoMae){
                    imghead = Image.getInstance(getRecursoImage("logo_mae.png"));
                    imghead.setAbsolutePosition(0, 12);		
        			imghead.scalePercent(70);
        			
        			imgbackground = Image.getInstance(getRecursoImage("escudoEcua.png"));
        			imgbackground.setAbsolutePosition(0, 220);
        			imgbackground.scalePercent(100);
        			document.add(imgbackground);

                    Image imgPie = Image.getInstance(getRecursoImage("pie.png"));
                    imgPie.setAbsolutePosition(0, 0);
                    imgPie.scalePercent(75);
        			document.add(imgPie);
                }else{
                    imghead = Image.getInstance(logo_datos);
                    imghead = Image.getInstance(logo_datos);
                    imghead.setAbsolutePosition(0, 12);
                    imghead.setAlignment(Image.ALIGN_CENTER);
                    imghead.scalePercent(80);
//                    imghead.scalePercent(60f);
                }
                /**
                 * FIN Permite obtener logos deacuerdo al area
                 */

                PdfPTable tableHeader = new PdfPTable(4);
                tableHeader.setWidths(new int[]{14, 10, 10, 14});
                tableHeader.setTotalWidth(2700);
                tableHeader.setLockedWidth(true);
                tableHeader.getDefaultCell().setFixedHeight(20);
                tableHeader.getDefaultCell().setBorder(Rectangle.NO_BORDER);
                tableHeader.setHorizontalAlignment(Element.ALIGN_LEFT);
                PdfPCell cellImagen = new PdfPCell();
                if(imghead != null)
                	cellImagen.addElement(imghead);
                cellImagen.setBorder(Rectangle.NO_BORDER);
                tableHeader.addCell(cellImagen);
                tableHeader.getDefaultCell().setHorizontalAlignment(
                        Element.ALIGN_RIGHT);
                PdfPCell cellVacia = new PdfPCell();
                cellVacia.setBorder(Rectangle.NO_BORDER);
                tableHeader.addCell(cellVacia);
                tableHeader.addCell(cellVacia);
                tableHeader.addCell(cellVacia);
                // ubicacion de la imagen superior
                tableHeader.writeSelectedRows(0, -1, 34, 590,writer.getDirectContent());
                if (params != null) {
                	// para ubicacion del codigo 570
                    int top = 500;
                    Font font = new Font(Font.FontFamily.HELVETICA, 8);
                    font.setStyle(Font.BOLD);
                    for (String s : params) {
                        PdfPTable tableHeader1 = new PdfPTable(4);
                        Phrase letra = new Phrase(s, font);
                        tableHeader1.setWidths(new int[]{14, 10, 10, 14});
                        tableHeader1.setTotalWidth(770);
                        tableHeader1.setLockedWidth(true);
                        tableHeader1.getDefaultCell().setFixedHeight(20);
                        tableHeader1.getDefaultCell().setBorder(Rectangle.NO_BORDER);
                        tableHeader1.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
                        tableHeader1.addCell(cellVacia);
                        tableHeader1.addCell(cellVacia);
                        tableHeader1.addCell(cellVacia);
                        tableHeader1.addCell(letra);
                        tableHeader1.writeSelectedRows(0, -1, 36, top, writer.getDirectContent());
                        top -= 10;
                    }
                }

                PdfPTable tableFooter = crearFoot(writer.getPageNumber(),
                        Image.getInstance(total), this.mostrarNumeracionPagina, this.mostrarDireccionMae);
                tableFooter.setTotalWidth(1027);
                tableFooter.writeSelectedRows(0, -1, 36, 64, writer.getDirectContent());
                tableFooter.writeSelectedRows(0, -1, 36, 64, writer.getDirectContent());

            } catch (DocumentException de) {
                throw new ExceptionConverter(de);
            } catch (MalformedURLException e) {
                LOG.error(e, e);
            } catch (IOException e) {
                LOG.error(e, e);
            }
        }
    }

    public static void createPdfHtml(String html, String destinoFile) throws IOException {
    	com.itextpdf.kernel.geom.PageSize pageSize ;
    	pageSize =  com.itextpdf.kernel.geom.PageSize.A4.rotate();
        try(
        		com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter(destinoFile);
                PdfDocument pdfDoc = new PdfDocument(writer);
                com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdfDoc);   )
        {
            pdfDoc.setTagged();
            pdfDoc.setDefaultPageSize(pageSize);
            //para el margen superior 70
            document.setMargins(120, 36, 90, 36);
            List<IElement> elements = HtmlConverter.convertToElements(html);
            for (IElement element : elements)
            {
                if (element instanceof IBlockElement)
                    document.add((IBlockElement) element);
            }
        }
    }

    public static PdfPTable crearFoot(int numPaginaActual, Image total,
            Boolean mostrarNumeracionPagina, Boolean mostrarDireccionMae) throws DocumentException,
			MalformedURLException, IOException {
			Image imgfoot = null;
			String direccionMae=" ", nombre_pie = "";
			DocumentosFacade docf = BeanLocator.getInstance(DocumentosFacade.class);

			PdfPTable table = new PdfPTable(3);
			table.setWidthPercentage(288 / 5.23f);
			table.setWidths(new int[]{2, 1, 1});

			if (mostrarNumeracionPagina != null && mostrarNumeracionPagina) {
				//Phrase paginacion = new Phrase(	String.valueOf(numPaginaActual) + " /", FontFactory.getFont (FontFactory.HELVETICA, 10, Font.BOLD, BaseColor.GRAY));
				Phrase paginacion = new Phrase(	String.valueOf(numPaginaActual) + " /", FontFactory.getFont (FontFactory.HELVETICA, 10, Font.BOLD, BaseColor.GRAY));
				PdfPCell celdaPageActual = new PdfPCell(paginacion);
				celdaPageActual.setHorizontalAlignment(Element.ALIGN_RIGHT);
				celdaPageActual.setBorder(Rectangle.NO_BORDER);
				table.addCell(celdaPageActual);
				PdfPCell cellTotal = new PdfPCell(total);
				cellTotal.setBorder(Rectangle.NO_BORDER);
				cellTotal.setHorizontalAlignment(Element.ALIGN_LEFT);
				table.addCell(cellTotal);
			}
			return table;
	}

	public File exportarMarcoLegalCoaPdf(List<CatalogoGeneral> articulosCoa, Area area) {
		File file = null;
		try {
			EntityNormativa entityNormativa = new EntityNormativa();
			String[] columnas = { " " };
			String htmlTablas = "";
			String[] ordenColumnas = { "Articulo" };
			for (CatalogoGeneral catalogoGeneral : articulosCoa) {
				List<EntityNormativaDetalle> detalles = new ArrayList<EntityNormativaDetalle>();
				for (Articulo articulo : catalogoGeneral.getArticulos()) {
					detalles.add(new EntityNormativaDetalle(articulo.getArticulo()));
				}
				htmlTablas += UtilFichaMineria.devolverDetalle(catalogoGeneral.getDescripcion(), columnas, detalles,
						ordenColumnas, "justify");
			}
			entityNormativa.setDetalleNormativa(htmlTablas);
			areaResponsable = area;
			file = generarFicheroCoa(UtilFichaMineria.extraeHtml(JsfUtil.devolverPathReportesHtml("normativa.html")), entityNormativa,
					"Normativas", false,areaResponsable);

		} catch (Exception e) {
			JsfUtil.addMessageError("No se puede exportar el archivo.");
		}
		return file;
	}
	

    public static File generarFicheroCoa(final String cadenaHtml,
                                      final Object entityReporte, final String nombreReporte,
                                      final Boolean mostrarNumeracionPagina, Area area
                                      , final String... parametrosCabecera) {
        Document document = null;
        PdfWriter writer = null;
        OutputStream fileOutputStream = null;
        File file = null;
        String archivo="";
        try {
            String buf = cadenaHtml;
            file = File.createTempFile(nombreReporte, ".pdf");
            List<String> listaTags = new ArrayList<>();
            Pattern pa = Pattern.compile("\\$F[{]\\w+[}$]");
            Matcher mat = pa.matcher(buf);
            while (mat.find()) {
                listaTags.add(mat.group());
            }
            Map<String, Object> mapa = new HashMap<>();
            Class clase = entityReporte.getClass();
            Method[] campos = clase.getMethods();
            for (Method f : campos) {
                if (f.getName().startsWith("get")) {
                    String metodo = "$F{" + f.getName().replace("get", "")
                            + "}";
                    for (String s : listaTags) {
                        if (s.equalsIgnoreCase(metodo)) {
                            mapa.put(s, f.invoke(entityReporte, null));
                            break;
                        }
                    }
                }
            }
            for (Map.Entry<String, Object> m : mapa.entrySet()) {
                buf = buf.replace(m.getKey(), m.getValue() == null ? "" : m.getValue().toString());
            }
            
            document = new Document(PageSize.A4.rotate(), 36, 36, 80, 70);

            fileOutputStream = new FileOutputStream(file);
            writer = PdfWriter.getInstance(document, fileOutputStream);
            writer.createXmpMetadata();

            writer.setPageEvent(new HeaderFichaAmbientalCoa(parametrosCabecera, true, false));
            /*
            if (parametrosCabecera == null) {
                header = new HeaderFichaAmbientalMineria();
            } else {
                header = new HeaderFichaAmbientalMineria(
                        parametrosCabecera, mostrarNumeracionPagina);
            }*/
            
            document.open();
            
            PdfContentByte cb = writer.getDirectContent();
            archivo=nombreReporte+new Date().getTime()+".pdf";
            createPdfHtml(buf, System.getProperty("java.io.tmpdir")+"/"+archivo);

            PdfReader readerF = new PdfReader(System.getProperty("java.io.tmpdir")+"/"+archivo);
            Integer totalPages = readerF.getNumberOfPages();
            for (int i = 1; i <= totalPages; i++) {
                PdfImportedPage page = writer.getImportedPage(readerF, i);
                document.newPage();
                cb.addTemplate(page, 0, 0);
            }
            if (Constantes.getDocumentosBorrador()) {
                //poneSelloAgua(writer);
            }
        } catch (Exception e) {
			JsfUtil.addMessageError("Error al realizar la operación.");
        } finally {
            if (document != null && document.isOpen()) {
                document.close();                
            }
            try {

                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
            }
            if (writer != null && !writer.isCloseStream()) {
                writer.close();
            }

        }
        if (Constantes.getDocumentosBorrador()) {
            return file;
        } else {
            return file;
        }
    }
    private static String generarHtml(final String cadenaHtml, final Object entityInforme, String textoNull) {
		try {
			String buf = cadenaHtml;
			List<String> listaTags = new ArrayList<>();
			Pattern pa = Pattern.compile("\\$F[{]\\w+[}$]");
			Matcher mat = pa.matcher(buf);
			while (mat.find()) {
				listaTags.add(mat.group());
			}
			Map<String, Object> mapa = new HashMap<>();
			Class<?> clase = entityInforme.getClass();
			Method[] campos = clase.getMethods();
			for (Method f : campos) {
				if (f.getName().startsWith("get")) {
					String metodo = "$F{" + f.getName().replace("get", "")  + "}";
					for (String s : listaTags) {
						if (s.equalsIgnoreCase(metodo)) {
							mapa.put(s, f.invoke(entityInforme, null));
							break;
						}
					}
				}
			}
			for (Map.Entry<String, Object> m : mapa.entrySet()) {
				buf = buf.replace(m.getKey(), m.getValue() == null ? "<span style='color:red'>INGRESAR</span>" : m.getValue().toString());
				//.getValue().toString().replace("\n", "<br />"));
			}
			
			return buf;
			} catch (Exception e) {
				LOG.error(e, e);
				return "";
			}
    }
    
    private static URL getRecursoImage(String nombreImagen) {
        ServletContext servletContext = (ServletContext) FacesContext
                .getCurrentInstance().getExternalContext().getContext();
        try {
            return servletContext.getResource("/resources/images/"
                    + nombreImagen);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
	public static ResourceBundle getResourcePlantillaInformes() {
		FacesContext context = FacesContext.getCurrentInstance();
		ResourceBundle bundle = context.getApplication().getResourceBundle(context, "plantillas");
		return bundle;
	}

    public static void deleteFileTmp(String directoryTmp) {
        try {
            File directory = new File(System.getProperty("java.io.tmpdir") + "/"+directoryTmp);
            File files = directory;
            files.delete();
        } catch (Exception e) {
            // info(e.getMessage());
        }
    }
    
    public boolean validarLogo(Area areaR){
    	boolean existeLogo= true;
        String nombre_logo ="";
        DocumentosFacade docf = BeanLocator.getInstance(DocumentosFacade.class);
        if(areaR != null){
          		nombre_logo = "logo__" + areaResponsable.getAreaAbbreviation().replace("/", "_") + ".png";	
        }
   		byte[] logo_datos = null;
        try {
            logo_datos = docf.descargarDocumentoPorNombre(nombre_logo);
        } catch (CmisAlfrescoException e) {
            // TODO Auto-generated catch block
//						e.printStackTrace();
        }
        // para desabilitar por falta del logo
        if(areaResponsable.getTipoArea().getSiglas().equals("EA") && logo_datos == null){
        	existeLogo= false;
        }
    	return existeLogo;
    }
 
    public File imprimirFichaPdf(RegistroAmbientalRcoa registroAmbientalRcoa, Boolean esActividadRelleno, Boolean esPmaViabilidad) {
        try {
        	
        	if(!validarLogo(registroAmbientalRcoa.getProyectoCoa().getAreaResponsable())){
            	existeError = true;
            	return null;
            }
        	setEsActividadRelleno(esActividadRelleno);
        	setPmaViabilidad(esPmaViabilidad);
            String parametros[] = {registroAmbientalRcoa.getProyectoCoa().getCodigoUnicoAmbiental(), JsfUtil.devuelveFechaEnLetrasSinHora(new Date())};
            setImpresionFichaGeneralBean(new ImpresionFichaGeneralBean());
            getImpresionFichaGeneralBean().iniciarDatos();
            getImpresionFichaGeneralBean().setPlantillaHtml(UtilFichaMineria.extraeHtml(JsfUtil.devolverPathReportesHtml("fichaAmbientalCoa.html")));
            imprimirFichaConfigurar(registroAmbientalRcoa);
            
            return generarFicheroCoa(getImpresionFichaGeneralBean().getPlantillaHtml(),
                    getImpresionFichaGeneralBean().getEntityFichaGeneralReporte(), "Ficha_Ambiental",
                    true, proyecto.getAreaResponsable(), parametros);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void imprimirFichaConfigurar(RegistroAmbientalRcoa registroAmbientalRcoa) {
        try {
			existeError = false;
        	cargarDatosContacto();
            getImpresionFichaGeneralBean().getEntityFichaGeneralReporte().setNombreActividadEconomica(detalleActividadesProyecto());
            getImpresionFichaGeneralBean().getEntityFichaGeneralReporte().setNombreProyectoObraActividad("<br/>"+proyecto.getNombreProyecto()+"<br/>");
            getImpresionFichaGeneralBean().getEntityFichaGeneralReporte().setDescripcionProyecto("<br/>"+proyecto.getDescripcionProyecto()+"<br/>");
            getImpresionFichaGeneralBean().getEntityFichaGeneralReporte().setDireccionProyecto(proyecto.getDireccionProyecto());
            getImpresionFichaGeneralBean().getEntityFichaGeneralReporte().setDetalleCoordenadas(detalleCoordenadasnProyecto());
            getImpresionFichaGeneralBean().getEntityFichaGeneralReporte().setDetalleUbicacionGeografica(detalleUbicacionProyecto());
            getImpresionFichaGeneralBean().getEntityFichaGeneralReporte().setTipoZona(proyecto.getTipoPoblacion().getNombre());
            getImpresionFichaGeneralBean().getEntityFichaGeneralReporte().setMarcoLegalReferencial(detalleMarcoLegal());
            getImpresionFichaGeneralBean().getEntityFichaGeneralReporte().setDetalleFasesProyecto(detalleFasesProyecto());
            getImpresionFichaGeneralBean().getEntityFichaGeneralReporte().setCronogramaValoradoPlanManejoAmbiental(detallePlan());
            // si hay generacion de desechos y residuos
            if(proyecto.getGeneraDesechos()){
            	getImpresionFichaGeneralBean().getEntityFichaGeneralReporte().setDetalleRegistroGenerador(detalleRegistroGeneradorDesechos());
            }
            // viabilidad
            if(proyecto.getTieneViabilidadFavorable() != null){
            	getImpresionFichaGeneralBean().getEntityFichaGeneralReporte().setDetalleViabilidad(detalleViabilidad());
            }
            // si hay inventario forestal
            //if(proyecto.getGeneraDesechos()){
            	getImpresionFichaGeneralBean().getEntityFichaGeneralReporte().setDetalleInventarioForestal(detalleInventarioForestal());
            //}
            // si hay sustancias quimicas
            if(proyecto.getSustanciasQuimicas()){
            	getImpresionFichaGeneralBean().getEntityFichaGeneralReporte().setDetalleSustanciasQuimicas(detalleRegistroSustanciasQuimicas());
            }

            if(existeError){
				JsfUtil.addMessageError("Error al generar el documento.");
            }
        } catch (Exception e) {
        	existeError = true;
            LOG.error(e, e);
        }
    }

    private String detalleMarcoLegal(){
    	String text = "<span style=\"font-size: small;background-color: inherit;\"> ";
    	text = text +  "Constitución de la República del Ecuador<br/>";
    	text = text +  "Código Orgánico del Ambiente<br/>";
    	text = text +  "Reglamento al Código Orgánico del Ambiente<br/>";
    	text = text +  "</span>";
    	return text;
    }
    
    private void cargarDatosContacto() {
        List<Contacto> listaContactos = null;
        String correo ="", direccion ="", telefono="";
        try {
            listaContactos = contactoFacade.buscarUsuarioNativeQuery(marcoLegalReferencialBean.getProyectoLicenciaCoa().getUsuario().getNombre());
            
            for (Contacto c : listaContactos) {
                if (correo.isEmpty() && c.getFormasContacto().getId().intValue() == FormasContacto.EMAIL) {
                    correo = c.getValor();
                } else if (direccion.isEmpty() && c.getFormasContacto().getId().intValue() == FormasContacto.DIRECCION) {
                    direccion = c.getValor();
                } else if (telefono.isEmpty() && c.getFormasContacto().getId().intValue() == FormasContacto.TELEFONO) {
                    telefono = c.getValor();
                }
            }
            getImpresionFichaGeneralBean().getEntityFichaGeneralReporte().setDatosPromotor(marcoLegalReferencialBean.getProyectoLicenciaCoa().getUsuario().getPersona().getNombre());
            getImpresionFichaGeneralBean().getEntityFichaGeneralReporte().setCorreoElectronicoPromotor(correo);
            getImpresionFichaGeneralBean().getEntityFichaGeneralReporte().setDomicilioPromotor(direccion);
            getImpresionFichaGeneralBean().getEntityFichaGeneralReporte().setTelefonoPromotor(telefono);
        } catch (ServiceException e) {
            LOG.error(e, e);
        }
    }
}
