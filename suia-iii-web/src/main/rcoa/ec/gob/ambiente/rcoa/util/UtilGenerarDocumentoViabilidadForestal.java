package ec.gob.ambiente.rcoa.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
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

public class UtilGenerarDocumentoViabilidadForestal {

    private static final Logger LOG = Logger
            .getLogger(UtilGenerarDocumentoViabilidadForestal.class);
    
    private static String generarHtml(final String cadenaHtml,
                                      final Object entityInforme, String textoNull) {
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
                    String metodo = "$F{" + f.getName().replace("get", "")
                            + "}";
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
            }

            return buf;
        } catch (Exception e) {
            LOG.error(e, e);
            return "";
        }

    }

    public static File generarFichero(String cadenaHtml,
                                      final String nombreReporte, final Boolean mostrarNumeracionPagina,
                                      final Object entityInforme, String textoNull, String[] infoExtra, Integer nroPaginasAdicionales, final Boolean mostrarCabecera, final Boolean mostrarPie) {
        Document document = null;
        PdfWriter writer = null;
        OutputStream fileOutputStream = null;
        File file = null;
        String archivo="";
        try {
            cadenaHtml = generarHtml(cadenaHtml, entityInforme, textoNull);

            String buf = cadenaHtml;
            file = File.createTempFile(nombreReporte, ".pdf");
            List<String> listaTags = new ArrayList<>();
            Pattern pa = Pattern.compile("\\$F[{]\\w+[}$]");
            buf="<p></p>";
            Matcher mat = pa.matcher(buf);
            while (mat.find()) {
                listaTags.add(mat.group());
            }
            document = new Document(PageSize.A4, 36, 36, 135, 60);
            fileOutputStream = new FileOutputStream(file);
            writer = PdfWriter.getInstance(document, fileOutputStream);
            writer.createXmpMetadata();

            writer.setPageEvent(new HeaderDocumento(
                    infoExtra, mostrarNumeracionPagina, mostrarCabecera, mostrarPie, nroPaginasAdicionales));

            document.open();
            PdfContentByte cb = writer.getDirectContent();
            archivo=nombreReporte+new Date().getTime()+".pdf";
            createPdfHtml(cadenaHtml, System.getProperty("java.io.tmpdir")+"/"+archivo);

            PdfReader readerF = new PdfReader(System.getProperty("java.io.tmpdir")+"/"+archivo);
            Integer totalPages = readerF.getNumberOfPages();
            for (int i = 1; i <= totalPages; i++) {
                PdfImportedPage page = writer.getImportedPage(readerF, i);
                document.newPage();
                cb.addTemplate(page, 0, 0);
            }
            
            CSSResolver cssResolver = new StyleAttrCSSResolver();
            XMLWorkerFontProvider fontProvider = new XMLWorkerFontProvider();
            CssAppliers cssAppliers = new CssAppliersImpl(fontProvider);
            HtmlPipelineContext htmlContext = new HtmlPipelineContext(
                    cssAppliers);
            htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());
            PdfWriterPipeline pdf = new PdfWriterPipeline(document, writer);
            HtmlPipeline html = new HtmlPipeline(htmlContext, pdf);
            CssResolverPipeline css = new CssResolverPipeline(cssResolver, html);
            XMLWorker worker = new XMLWorker(css, true);
            XMLParser p = new XMLParser(worker);
            Reader reader = new StringReader(buf);
            p.parse(reader);

        } catch (Exception e) {
            LOG.error(e, e);
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
        deleteFileTmp(archivo);
        return file;
    }
    
	public static File generarFicheroBlanco(String cadenaHtml,
			final String nombreReporte, final Object entityInforme) {
		return generarFichero(cadenaHtml,
                nombreReporte, false,
                entityInforme, "<span style='color:red'>INGRESAR</span>", null, 0, false, false);
	}
	
	public static File generarFicheroSinCabecera(String cadenaHtml,
			final String nombreReporte, final Boolean mostrarNumeracionPagina,
			final Object entityInforme, String[] infoExtra, Integer nroPagInicio) {
		return generarFichero(cadenaHtml,
                nombreReporte, mostrarNumeracionPagina,
                entityInforme, "<span style='color:red'>INGRESAR</span>", infoExtra, nroPagInicio, false, true);
	}
	
//	public static File generarFichero(String cadenaHtml,
//			final String nombreReporte, final Boolean mostrarNumeracionPagina,
//			final Object entityInforme, String[] infoExtra, Integer nroPagInicio,
//			final Boolean mostrarCabecera, final Boolean mostrarPie) {
//		return generarFichero(cadenaHtml,
//                nombreReporte, mostrarNumeracionPagina,
//                entityInforme, "<span style='color:red'>INGRESAR</span>", infoExtra, nroPagInicio, mostrarCabecera, mostrarPie);
//	}

    public static void deleteFileTmp(String directoryTmp) {
        try {
            File directory = new File(System.getProperty("java.io.tmpdir") + "/"+directoryTmp);
            File files = directory;
            files.delete();
        } catch (Exception e) {
            
        }
    }

	public static void createPdfHtml(String html, String destinoFile) throws IOException {        

        com.itextpdf.kernel.geom.PageSize pageSize =  com.itextpdf.kernel.geom.PageSize.A4;
        html=html.replace("<table", "<table border=\"1\" align=\"center\"").replace("<a", "<a style=\"word-wrap:break-word;\"");

        try (   com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter(destinoFile);
                PdfDocument pdfDoc = new PdfDocument(writer);
                com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdfDoc);   )
        {
            pdfDoc.setTagged();
            pdfDoc.setDefaultPageSize(pageSize);     
            document.setMargins(80, 36, 70, 36);

            List<IElement> elements = HtmlConverter.convertToElements(html);
            for (IElement element : elements)
            {
                if (element instanceof IBlockElement)
                    document.add((IBlockElement) element);
            }
        }
    }

    public static String generarFicheroHtml(String cadenaHtml,
                                            final String nombreReporte, final Boolean mostrarNumeracionPagina,
                                            final Object entityInforme) {
        try {
            cadenaHtml = generarHtml(cadenaHtml, entityInforme, "<span style='color:red'>INGRESAR</span>");
            return cadenaHtml;

        } catch (Exception e) {
            LOG.error(e, e);
        }
        return cadenaHtml;
    }

    public static PdfPTable crearFoot(int numPaginaActual, Image total,
                                      Boolean mostrarNumeracionPagina) throws DocumentException,
            MalformedURLException, IOException {
    	
		PdfPTable table = new PdfPTable(3);
		table.setWidths(new int[] { 8, 8, 6 });

		if (mostrarNumeracionPagina != null && mostrarNumeracionPagina) {
			PdfPCell cell;
			cell = new PdfPCell(new Phrase("    "));
			cell.setBorderColor(BaseColor.GRAY);
			table.addCell(cell);

			cell = new PdfPCell(new Phrase("    "));
			cell.setBorderColor(BaseColor.GRAY);
			table.addCell(cell);
			
			Font font = new Font(Font.FontFamily.HELVETICA, 6);
            font.setStyle(Font.BOLD);

			PdfPCell celdaPageActual = new PdfPCell(new Phrase("Página:" + String.valueOf(numPaginaActual), font));
			celdaPageActual.setHorizontalAlignment(Element.ALIGN_CENTER);
			celdaPageActual.setVerticalAlignment(Element.ALIGN_CENTER);
			celdaPageActual.setBorderColor(BaseColor.GRAY);
			table.addCell(celdaPageActual);
		}
		return table;
    }

	public static ResourceBundle getResourcePlantillaInformes() {
		FacesContext context = FacesContext.getCurrentInstance();
		ResourceBundle bundle = context.getApplication().getResourceBundle(context, "plantillas");
		return bundle;
	}

    static class HeaderDocumento extends PdfPageEventHelper {

        String[] params;
        Boolean mostrarNumeracionPagina;
        Boolean mostrarCabecera;
        Boolean mostrarPie;
        Integer nroPaginasAdicionales;
        /**
         * The template with the total number of pages.
         */
        PdfTemplate total;

        public HeaderDocumento() {
        }

        public HeaderDocumento(String[] params,
                                           Boolean mostrarNumeracionPagina, Boolean mostrarCabecera, Boolean mostrarPie, Integer nroPaginasAdicionales) {
            this.params = params;
            this.mostrarNumeracionPagina = mostrarNumeracionPagina;
            this.mostrarCabecera = mostrarCabecera;
            this.mostrarPie = mostrarPie;
            this.nroPaginasAdicionales = nroPaginasAdicionales;
        }

        /**
         * Creates the PdfTemplate that will hold the total number of pages.
         *
         * @see com.itextpdf.text.pdf.PdfPageEventHelper#onOpenDocument(com.itextpdf.text.pdf.PdfWriter,
         * com.itextpdf.text.Document)
         */
        public void onOpenDocument(PdfWriter writer, Document document) {
            total = writer.getDirectContent().createTemplate(30, 16);
        }

        public void onCloseDocument(PdfWriter writer, Document document) {
            ColumnText.showTextAligned(total, Element.ALIGN_LEFT, new Phrase(
                    String.valueOf(writer.getPageNumber() - 1)), 2, 2, 0);
        }
        
        public void onEndPage(PdfWriter writer, Document document) {
            Image imghead;
            
            try {
            	
            	if(this.mostrarCabecera) {
	            	imghead = Image.getInstance(getRecursoImage("logo_mae_header.png"));
	            	imghead.setAlignment(Element.ALIGN_CENTER);
		            imghead.scalePercent(24);
		            
	                PdfPTable tableHeader = new PdfPTable(2);
	                tableHeader.setWidths(new int[]{5,19});
	                tableHeader.setTotalWidth(527);
	                tableHeader.setLockedWidth(true);
	                tableHeader.getDefaultCell().setFixedHeight(20);
	                
	                PdfPCell cellImagen = new PdfPCell();
	                cellImagen.addElement(imghead);
	                cellImagen.setRowspan(2);                
	                tableHeader.addCell(cellImagen);
	                
	                Font font = new Font(Font.FontFamily.HELVETICA, 8);
	                font.setStyle(Font.BOLD);
	                
	                PdfPCell cell = new PdfPCell(new Phrase("MINISTERIO DEL AMBIENTE, AGUA Y TRANSICIÓN ECOLÓGICA", font));
	                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	                cell.setPadding(8);
	                tableHeader.addCell(cell);
	                
	                cell = new PdfPCell(new Phrase("CÓDIGO SUIA " + params[0], font));
	                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	                cell.setPadding(8);
	                tableHeader.addCell(cell);
	                
	                tableHeader.writeSelectedRows(0, -1, 34, 815, writer.getDirectContent());
            	}
                
                if(this.mostrarPie) {
                	PdfPTable tableFooter = crearFoot(writer.getPageNumber() + nroPaginasAdicionales, Image.getInstance(total), this.mostrarNumeracionPagina);
                    tableFooter.setTotalWidth(527);
                    tableFooter.writeSelectedRows(0, -1, 34, 55, writer.getDirectContent());
                }

            } catch (Exception e) {
                LOG.error(e, e);
            }
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

}
