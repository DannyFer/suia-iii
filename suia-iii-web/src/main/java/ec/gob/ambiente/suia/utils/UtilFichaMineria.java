/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import lombok.Getter;
import lombok.Setter;

import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
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
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.reportes.UtilDocumento;

/**
 * @author christian
 */
public class UtilFichaMineria {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(UtilFichaMineria.class);
    @Getter
    @Setter
    static private Area areaResponsable;

    public static String devolverDetalle(String cabeza,
                                         final String[] columnas, final List<? extends Object> listaFilas,
                                         final String[] ordenColumnas, String alinearTexto) {
        try {
            StringBuilder tablaStandar = new StringBuilder();
            String alineacion = "center";
            if (alinearTexto != null) {
                alineacion = alinearTexto;
            }
            if (cabeza != null) {
                tablaStandar
                        .append("<table align=\"left\" border=\"0\" cellpadding=\"1\" cellspacing=\"1\" style=\"width:100%\">");
                tablaStandar.append("<tbody><tr>");
                // int tamanio = 100 / columnas.length;
                tablaStandar
                        .append("<td style=\"width: ")
                        .append(100)
                        .append("%; text-align: left\"> <div style=\"font-size: small;font-weight: bold;background-color: inherit;\">");
                tablaStandar.append(cabeza);
                tablaStandar.append("<br/></div></td>");
                tablaStandar.append("</tr>");
                tablaStandar.append("</tbody></table>");
            }

            tablaStandar
                    .append("<table align=\"left\" border=\"0\" cellpadding=\"1\" cellspacing=\"1\" style=\"width:100%\">");
            tablaStandar.append("<tbody><tr>");
            int tamanio = 100 / columnas.length;
            for (String s : columnas) {
                tablaStandar
                        .append("<td style=\"width: ")
                        .append(tamanio)
                        .append("%; text-align: center\"> <div style=\"font-size: small\">");
                tablaStandar.append(s);
                tablaStandar.append("<br/></div></td>");
            }
            tablaStandar.append("</tr>");
            for (Object o : listaFilas) {
                tablaStandar.append("<tr>");
                Class claseR = o.getClass();
                Method[] campos = claseR.getMethods();
                for (String s : ordenColumnas) {
                    for (Method f : campos) {
                        if (f.getName().startsWith("get")
                                && f.getName().contains(s)) {
                            tablaStandar
                                    .append("<td style=\"width: ")
                                    .append(tamanio)
                                    .append("%; text-align: ")
                                    .append(alineacion)
                                    .append("\"> <div style=\"font-size: small;  text-align: " + alineacion + "\" >");
                            tablaStandar.append(f.invoke(o, null));
                            tablaStandar.append("<br/></div></td>");
                            break;
                        }
                    }
                }

                tablaStandar.append("</tr>");
            }
            tablaStandar.append("</tbody></table>");
            return tablaStandar.toString();
        } catch (Exception e) {
            LOG.error(e, e);
            return null;
        }
    }

    public static void generarHtmlPdf(final String cadenaHtml,
                                      final Object entityReporte, final String nombreReporte,
                                      final Boolean mostrarNumeracionPagina,
                                      final String... parametrosCabecera) {

        File file = generarFichero(cadenaHtml, entityReporte, nombreReporte,
                mostrarNumeracionPagina, parametrosCabecera);

        descargar(file.getAbsolutePath(), nombreReporte);

    }

    public static File generarFichero(final String cadenaHtml,
                                      final Object entityReporte, final String nombreReporte,
                                      final Boolean mostrarNumeracionPagina,
                                      final String... parametrosCabecera) {
        return generarFichero(cadenaHtml,
                entityReporte, nombreReporte,
                mostrarNumeracionPagina, null, parametrosCabecera);
    }

    public static File generarFichero(final String cadenaHtml,
                                      final Object entityReporte, final String nombreReporte,
                                      final Boolean mostrarNumeracionPagina, Area area,
                                      final String... parametrosCabecera) {
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
                buf = buf.replace(m.getKey(), m.getValue() == null ? "" : m
                        .getValue().toString());
            }

            document = new Document(PageSize.A4, 36, 36, 100, 70);

            fileOutputStream = new FileOutputStream(file);
            writer = PdfWriter.getInstance(document, fileOutputStream);
            writer.createXmpMetadata();
            if (area != null) {
                areaResponsable = area;
                HeaderFichaAmbientalMineria header;
                if (parametrosCabecera == null) {
                    header = new HeaderFichaAmbientalMineria();
                } else {
                    header = new HeaderFichaAmbientalMineria(
                            parametrosCabecera, mostrarNumeracionPagina);
                }
                writer.setPageEvent(header);
            } else {
                if (parametrosCabecera == null) {
                    writer.setPageEvent(new HeaderFichaAmbientalMineria());
                } else {
                    writer.setPageEvent(new HeaderFichaAmbientalMineria(
                            parametrosCabecera, mostrarNumeracionPagina));
                }
            }
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
//            CSSResolver cssResolver = new StyleAttrCSSResolver();
//            XMLWorkerFontProvider fontProvider = new XMLWorkerFontProvider();
//            CssAppliers cssAppliers = new CssAppliersImpl(fontProvider);
//            HtmlPipelineContext htmlContext = new HtmlPipelineContext(
//                    cssAppliers);
//            htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());
//            PdfWriterPipeline pdf = new PdfWriterPipeline(document, writer);
//            HtmlPipeline html = new HtmlPipeline(htmlContext, pdf);
//            CssResolverPipeline css = new CssResolverPipeline(cssResolver, html);
//            XMLWorker worker = new XMLWorker(css, true);
//            XMLParser p = new XMLParser(worker);
//            Reader reader = new StringReader(buf);
//            p.parse(reader);

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
        if (Constantes.getDocumentosBorrador()) {
        	deleteFileTmp(nombreReporte+".pdf");
            return poneSelloAguaTotal(file);
        } else {
        	deleteFileTmp(nombreReporte+".pdf");
            return file;
        }

    }

    public static String generarHtml(final String cadenaHtml,
                                     final Object entityReporte) {
        try {
            String buf = cadenaHtml;
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
                buf = buf.replace(m.getKey(), m.getValue() == null ? "" : m
                        .getValue().toString());
            }

            return buf;
        } catch (Exception e) {
            LOG.error(e, e);
            return "";
        }

    }

    public static void descargar(String pathAbsoluto, String nombreReporte) {
        try {
            HttpServletResponse response = (HttpServletResponse) FacesContext
                    .getCurrentInstance().getExternalContext().getResponse();
            OutputStream out = response.getOutputStream();
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment;filename=\""
                    + nombreReporte + ".pdf\"");
            response.setDateHeader("Expires", 0);
            InputStream inputStream = new FileInputStream(pathAbsoluto);
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            inputStream.close();
            out.flush();
            FacesContext.getCurrentInstance().responseComplete();

        } catch (Exception e) {
            LOG.error(e, e);
        }
    }

    public static void descargar(byte[] bytes, String nombreReporte) {
        try {
            HttpServletResponse response = (HttpServletResponse) FacesContext
                    .getCurrentInstance().getExternalContext().getResponse();
            OutputStream out = response.getOutputStream();
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment;filename=\""
                    + nombreReporte + ".pdf\"");
            response.setDateHeader("Expires", 0);

            out.write(bytes);

            out.flush();
            FacesContext.getCurrentInstance().responseComplete();

        } catch (Exception e) {
            LOG.error(e, e);
        }
    }

    public static String extraeHtml(final String path) {
        try {
            StringBuilder bufHtml = new StringBuilder();
            BufferedReader in = new BufferedReader(new FileReader(path));
            String str;
            while ((str = in.readLine()) != null) {
                bufHtml.append(str);
            }
            in.close();
            return bufHtml.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static PdfPTable crearFoot(int numPaginaActual, Image total,
                                      Boolean mostrarNumeracionPagina) throws DocumentException,
            MalformedURLException, IOException {
//        Image imgfoot = null;
//        Map<String, String> recursos = UtilDocumento.cargarImageneArea(areaResponsable);
//        imgfoot = Image.getInstance(UtilDocumento.getRecursoImage(recursos.get("pie_ci")));//"pie_ci.png"}
    	Image imgfoot = null;
    	String direccionMae="";
        Map<String, String> recursos =null;
        DocumentosFacade docf = BeanLocator
				.getInstance(DocumentosFacade.class);       
        String nombre_pie = "pie__" + areaResponsable.getAreaAbbreviation().replace("/", "_") + ".png";
        byte[] logo_datos = null;
        try {
			logo_datos = docf.descargarDocumentoPorNombre(nombre_pie);
		} catch (CmisAlfrescoException e) {
		}           
        if(logo_datos==null)     {
        	recursos = UtilDocumento.cargarImageneArea(areaResponsable);
        	imgfoot = Image.getInstance(UtilDocumento.getRecursoImage(recursos.get("pie_ci")));//"pie_ci.png"
        } else{
        	imgfoot=Image.getInstance(logo_datos);
        }
//        imgfoot.setAbsolutePosition(0, 0);
//        imgfoot.setAlignment(Image.ALIGN_CENTER);
//        imgfoot.scalePercent(60f);
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(288 / 5.23f);
        table.setWidths(new int[]{16, 1, 1});
        PdfPCell cell;
//        cell = new PdfPCell(imgfoot);
		cell = new PdfPCell(new Phrase("    "));
		cell.setFixedHeight(28);
        cell.setColspan(3);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);
        cell = new PdfPCell(new Phrase(direccionMae, FontFactory.getFont(FontFactory.HELVETICA, 8)));
        cell.setRowspan(2);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);
        if (mostrarNumeracionPagina != null && mostrarNumeracionPagina) {
            PdfPCell celdaPageActual = new PdfPCell(new Phrase(
                    String.valueOf(numPaginaActual) + " /"));
            celdaPageActual.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celdaPageActual.setBorder(Rectangle.NO_BORDER);
            table.addCell(celdaPageActual);
            PdfPCell cellTotal = new PdfPCell(total);
            cellTotal.setBorder(Rectangle.NO_BORDER);
            cellTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cellTotal);
        }
        return table;
    }

	public static ResourceBundle getResourcePlantillaInformes() {
		FacesContext context = FacesContext.getCurrentInstance();
		ResourceBundle bundle = context.getApplication().getResourceBundle(context, "plantillas");
		return bundle;
	}

    public static File unirPdf(final List<File> listaArchivosUnir,
                               final String nombreArchivoResultante) throws Exception {
        List<InputStream> lista = new ArrayList<InputStream>();
        for (File f : listaArchivosUnir) {
            InputStream file = new FileInputStream(f);
            lista.add(file);
        }
        File salida = File.createTempFile(nombreArchivoResultante, ".pdf");
        OutputStream out = new FileOutputStream(salida);
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, out);
        document.open();
        PdfContentByte cb = writer.getDirectContent();
        for (InputStream in : lista) {
            PdfReader reader = new PdfReader(in);
            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                document.newPage();
                PdfImportedPage page = writer.getImportedPage(reader, i);
                cb.addTemplate(page, 0, 0);
            }
        }
        out.flush();
        document.close();
        out.close();
        return salida;
    }

    private static void poneSelloAgua(PdfWriter writer) {
        try {
            PdfContentByte cb = writer.getDirectContent();
            //Se crea un templete para asignar la marca de agua
            PdfTemplate template = cb.createTemplate(700, 300);
            template.beginText();
            //Inicializamos los valores para el templete
            //Se define el tipo de letra, color y tamaño
            BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            template.setColorFill(BaseColor.GRAY);
            template.setFontAndSize(bf, 56);

            template.setTextMatrix(0, 0);
            //Se define el texto que se agregara como marca de agua
            template.showText("Borrador");
            template.endText();
            //Se asigna el templete
            //Se asignan los valores para el texto de marca de agua
            // Se asigna el grado de inclinacion y la posicion donde aparecerá el texto
            //                                                     x    y
            cb.addTemplate(template, 1, 1, -1, 1, 150, 500);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private static File poneSelloAguaTotal(File file) {
        try {
            String nombre = file.getAbsolutePath();
            PdfReader reader = new PdfReader(nombre);
            int n = reader.getNumberOfPages();

            // Create a stamper that will copy the document to a new file
            PdfStamper stamp = new PdfStamper(reader,
                    new FileOutputStream(file.getAbsolutePath() + ".tmp"));
            int i = 1;
            PdfContentByte under;
            PdfContentByte over;

            BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA,
                    BaseFont.WINANSI, BaseFont.EMBEDDED);

            while (i <= n) {
                // Watermark under the existing page
                under = stamp.getUnderContent(i);
                // Text over the existing page
                over = stamp.getOverContent(i);
                under.beginText();
                under.setFontAndSize(bf, 48);
                under.setColorFill(BaseColor.GRAY);
                //under.showText("Borrador ");
                under.showTextAligned(1, "--BORRADOR--", 300, 500, 45);
                under.endText();

                i++;
            }

            stamp.close();
            File borrador = new File(file.getAbsolutePath() + ".tmp");
            borrador.renameTo(file);
            return new File(nombre);
        } catch (Exception de) {

            return file;
        }

    }

    static class HeaderFichaAmbientalMineria extends PdfPageEventHelper {

        String[] params;
        Boolean mostrarNumeracionPagina;

        /**
         * The template with the total number of pages.
         */
        PdfTemplate total;

        public HeaderFichaAmbientalMineria() {
        }

        public HeaderFichaAmbientalMineria(String[] params,
                                           Boolean mostrarNumeracionPagina) {
            this.params = params;
            this.mostrarNumeracionPagina = mostrarNumeracionPagina;
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
            Image imghead = null;
            Image imgbackground = null;
            int positionYLogo = 803;
            int top = 790;
            try {
//                Map<String, String> recursos = UtilDocumento.cargarImageneArea(areaResponsable);
//                imghead = Image
//                        .getInstance(UtilDocumento.getRecursoImage(recursos.get("logo_mae_pie"))); //"logo_mae_pie.png"
            	Map<String, String> recursos =null;
           		DocumentosFacade docf = BeanLocator
           				.getInstance(DocumentosFacade.class);       
           		String nombre_logo = "logo__" + areaResponsable.getAreaAbbreviation().replace("/", "_") + ".png";
           		byte[] logo_datos = null;
           		try {
           				logo_datos = docf.descargarDocumentoPorNombre(nombre_logo);
           		} catch (CmisAlfrescoException e) {
           		}           
           		if(logo_datos==null)     {
           			positionYLogo = 830;
           			top = 765;
           			recursos = UtilDocumento.cargarImageneArea(areaResponsable);
           			imghead = Image.getInstance(UtilDocumento.getRecursoImage(recursos.get("logo_mae"))); //"logo_mae_pie.png"
                	imghead.setAbsolutePosition(0, 0);
                    imghead.scalePercent(50);
        			imgbackground = Image.getInstance(UtilDocumento.getRecursoImage("fondo-documentos.png"));
        			imgbackground.setAbsolutePosition(0, 15);
        			imgbackground.scalePercent(50);
        			document.add(imgbackground);
           		} else{
           			imghead=Image.getInstance(logo_datos);
                	imghead.setAbsolutePosition(0, 0);
                    imghead.setAlignment(Image.ALIGN_CENTER);
                    imghead.scalePercent(60f);
           		}
                PdfPTable tableHeader = new PdfPTable(4);
                tableHeader.setWidths(new int[]{14, 10, 10, 14});
                tableHeader.setTotalWidth(527);
                tableHeader.setLockedWidth(true);
                tableHeader.getDefaultCell().setFixedHeight(20);
                tableHeader.getDefaultCell().setBorder(Rectangle.NO_BORDER);
                tableHeader.setHorizontalAlignment(Element.ALIGN_LEFT);
                PdfPCell cellImagen = new PdfPCell();
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
                tableHeader.writeSelectedRows(0, -1, 34, positionYLogo,
                        writer.getDirectContent());
                if (params != null) {
                    Font font = new Font(Font.FontFamily.HELVETICA, 8);
                    font.setStyle(Font.BOLD);
                    for (String s : params) {
                        PdfPTable tableHeader1 = new PdfPTable(4);
                        Phrase letra = new Phrase(s, font);
                        tableHeader1.setWidths(new int[]{14, 10, 10, 14});
                        tableHeader1.setTotalWidth(527);
                        tableHeader1.setLockedWidth(true);
                        tableHeader1.getDefaultCell().setFixedHeight(20);
                        tableHeader1.getDefaultCell().setBorder(
                                Rectangle.NO_BORDER);
                        tableHeader1.getDefaultCell().setHorizontalAlignment(
                                Element.ALIGN_RIGHT);

                        tableHeader1.addCell(cellVacia);
                        tableHeader1.addCell(cellVacia);
                        tableHeader1.addCell(cellVacia);
                        tableHeader1.addCell(letra);
                        tableHeader1.writeSelectedRows(0, -1, 36, top,
                                writer.getDirectContent());
                        top -= 10;
                    }
                }

                PdfPTable tableFooter = crearFoot(writer.getPageNumber(),
                        Image.getInstance(total), this.mostrarNumeracionPagina);
                tableFooter.setTotalWidth(527);
                tableFooter.writeSelectedRows(0, -1, 36, 64,
                        writer.getDirectContent());
                tableFooter.writeSelectedRows(0, -1, 36, 64,
                        writer.getDirectContent());

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
        com.itextpdf.kernel.geom.PageSize pageSize =  com.itextpdf.kernel.geom.PageSize.A4;
//        html=html.replace("<table", "<table border=\"1\" align=\"center\"").replace("<a", "<a style=\"word-wrap:break-word;\"");
        try (   com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter(destinoFile);
                PdfDocument pdfDoc = new PdfDocument(writer);
                com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdfDoc);   )
        {
            pdfDoc.setTagged();
            pdfDoc.setDefaultPageSize(pageSize);     
            document.setMargins(125, 36, 70, 36);
            List<IElement> elements = HtmlConverter.convertToElements(html);
            for (IElement element : elements)
            {
                if (element instanceof IBlockElement)
                    document.add((IBlockElement) element);
            }
        }
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
}