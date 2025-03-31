package ec.gob.ambiente.suia.reportes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEvent;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.css.CssFile;
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

import ec.gob.ambiente.suia.domain.EquipoTecnico;
import ec.gob.ambiente.suia.domain.ProyectoBloque;
import ec.gob.ambiente.suia.domain.ProyectoUbicacionGeografica;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;

public class DocumentoPDFPlantillaHtml {

	private static final Logger LOG = Logger.getLogger(DocumentoPDFPlantillaHtml.class);

	public static String crearTablaUbicacion(List<ProyectoUbicacionGeografica> ubicaciones) {
		String rowUbicacion = getResourcePlantillaInformes().getString("row3columns");
		String row;
		StringBuilder tabla = new StringBuilder(getResourcePlantillaInformes().getString("inicio_table_html"));
		for (ProyectoUbicacionGeografica proyectoUbicacion : ubicaciones) {
			UbicacionesGeografica ubicacionesGeografica = proyectoUbicacion.getUbicacionesGeografica();
			row = String.format(rowUbicacion, ubicacionesGeografica.getNombre(), ubicacionesGeografica
					.getUbicacionesGeografica().getNombre(), ubicacionesGeografica.getUbicacionesGeografica()
					.getUbicacionesGeografica().getNombre());
			tabla.append(row);

		}
		tabla.append(getResourcePlantillaInformes().getString("fin_table_html"));
		return tabla.toString();
	}

	public static String crearTablaBloques(List<ProyectoBloque> proyectoBloques) {
		String row2columns = getResourcePlantillaInformes().getString("row2columns");
		String row;
		StringBuilder tabla = new StringBuilder(getResourcePlantillaInformes().getString("inicio_table_html"));
		for (ProyectoBloque proyectoBloque : proyectoBloques) {
			row = String.format(row2columns, proyectoBloque.getBloque().getNombre(), proyectoBloque.getBloque()
					.getDenominacionArea());
			tabla.append(row);

		}
		tabla.append(getResourcePlantillaInformes().getString("fin_table_html"));
		return tabla.toString();
	}

	public static String crearTabaEquipoTecnico(List<EquipoTecnico> equipo) {
		String row2columns = getResourcePlantillaInformes().getString("row2columns");
		String row;
		StringBuilder tabla = new StringBuilder(getResourcePlantillaInformes().getString("inicio_table_html"));
		for (EquipoTecnico eq : equipo) {
			row = String.format(row2columns, eq.getEspecialidad(), eq.getTareasDesarrollar());
			tabla.append(row);

		}
		tabla.append(getResourcePlantillaInformes().getString("fin_table_html"));
		return tabla.toString();
	}

	public static ResourceBundle getResourcePlantillaInformes() {
		FacesContext context = FacesContext.getCurrentInstance();
		ResourceBundle bundle = context.getApplication().getResourceBundle(context, "plantillas");
		return bundle;
	}

	public static String getValorResourcePlantillaInformes(String key) {
		return getResourcePlantillaInformes().getString(key);
	}

	public static void descargarArchivo(String pdfFilename, String[] parametros, String keyPlantilla,
			boolean isInforme, String titulo) {
		try {

			File archivoTemporal = crearPDFConCss(pdfFilename, parametros, keyPlantilla, isInforme, titulo);

			UtilDocumento.descargarPDF(archivoTemporal);

		} catch (Exception e) {
			LOG.error(e , e);
		}
	}

	public static String verificarSiTieneObservacion(String campo) {
		String msjSinObservacion = getResourcePlantillaInformes().getString("sin_observacion");
		return (campo == null) ? msjSinObservacion : campo;

	}

	public static PdfPTable crearFoot(int numPaginaActual, Image total) throws DocumentException,
			MalformedURLException, IOException {
		// ya no va la imagen del pie de pagina
		//Image imgfoot = null;
		//imgfoot = Image.getInstance(UtilDocumento.getRecursoImage("pie_ci.png"));
		//imgfoot.setAbsolutePosition(0, 0);
		//imgfoot.setAlignment(Image.ALIGN_CENTER);
		//imgfoot.scalePercent(60f);
		PdfPTable table = new PdfPTable(3);
		table.setWidthPercentage(288 / 5.23f);
		table.setWidths(new int[] { 16, 1, 1 });
		PdfPCell cell;
		//cell = new PdfPCell(imgfoot);
		cell = new PdfPCell(new Phrase("    "));
		cell.setFixedHeight(30);
		cell.setColspan(3);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		cell.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 8)));
		cell.setRowspan(2);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell);
		PdfPCell celdaPageActual = new PdfPCell(new Phrase(String.valueOf(numPaginaActual) + " /"));
		celdaPageActual.setHorizontalAlignment(Element.ALIGN_RIGHT);
		celdaPageActual.setBorder(Rectangle.NO_BORDER);
		table.addCell(celdaPageActual);
		PdfPCell cellTotal = new PdfPCell(total);
		cellTotal.setBorder(Rectangle.NO_BORDER);
		cellTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cellTotal);
		return table;
	}

	public static String fechaActual() {
		DateFormat df4 = DateFormat.getDateInstance(DateFormat.FULL);
		return df4.format(new Date());
	}

	public static File crearPDFConCss(String pdfFilename, String[] parametros, String keyPlantilla, boolean isInforme,
			String titulo) {

		File tmpFile = null;
		try {
			tmpFile = File.createTempFile(pdfFilename, ".pdf");
			FileOutputStream out = new FileOutputStream(tmpFile);
			// create a new document
			Document document = new Document(PageSize.A4, 36, 36, 54, 54);
			PdfWriter pdfWriter = PdfWriter.getInstance(document, out);
			pdfWriter.setBoxSize("art", new Rectangle(36, 54, 559, 788));
			pdfWriter.setPageEvent((PdfPageEvent) (isInforme ? new PlantillaHeaderFooter()
					: new PlantillaHeaderFooterOficio()));
			// document header attributes
			document = agregarInformacionDocumento(document, titulo);
			// open document
			document.open();
			String htmlPage = getPlantillaConParametros(keyPlantilla, parametros);

			// Pipelines
			PdfWriterPipeline pdf = new PdfWriterPipeline(document, pdfWriter);
			HtmlPipeline html = new HtmlPipeline(getHtmlContext(), pdf);

			CssResolverPipeline css = new CssResolverPipeline(getCss(), html);

			XMLWorker worker = new XMLWorker(css, true);
			XMLParser p = new XMLParser(worker);
			p.parse(new StringReader(htmlPage));

			// close the document
			document.close();
			// close the writer
			pdfWriter.close();
			return tmpFile;

		}

		catch (FileNotFoundException e) {
			LOG.error("Error al crear el PDF " + pdfFilename, e);
			return null;
		} catch (IOException e) {
			LOG.error("Error al crear el PDF " + pdfFilename, e);
			return null;
		} catch (DocumentException e) {
			LOG.error("Error al crear el PDF " + pdfFilename, e);
			return null;
		}

	}

	public static String getPlantillaConParametros(String keyPlantilla, String[] parametros) {
		String htmlPage = getResourcePlantillaInformes().getString(keyPlantilla);
		if (parametros != null && parametros.length >= 1) {
			for (int i = 0; i < parametros.length; i++) {
				if (parametros[i] == null) {
					parametros[i] = "";
				}
			}
			htmlPage = String.format(htmlPage, (Object[]) parametros);

		}
		return htmlPage;
	}

	public static CSSResolver getCss() {
		CSSResolver cssResolver = new StyleAttrCSSResolver();
		CssFile cssFile = XMLWorkerHelper.getCSS(getRecursoCss());
		cssResolver.addCss(cssFile);
		return cssResolver;
	}

	public static HtmlPipelineContext getHtmlContext() {
		XMLWorkerFontProvider fontProvider = new XMLWorkerFontProvider();
		CssAppliers cssAppliers = new CssAppliersImpl(fontProvider);
		HtmlPipelineContext htmlContext = new HtmlPipelineContext(cssAppliers);
		htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());
		return htmlContext;
	}

	public static Image agregarFirmaImagen(byte[] imagenByte) throws BadElementException, MalformedURLException,
			IOException {
		Image imagen = Image.getInstance(imagenByte);
		return imagen;
	}

	private static InputStream getRecursoCss() {
		ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext()
				.getContext();
		return servletContext.getResourceAsStream("/resources/css/reporte.css");
	}

	public static Document agregarInformacionDocumento(Document document, String titulo) {
		document.addAuthor("SUIA");
		document.addCreationDate();
		document.addProducer();
		document.addCreator("SUIA");
		document.addTitle(titulo);
		return document;
	}
}
