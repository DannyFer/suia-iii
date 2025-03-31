package ec.gob.ambiente.suia.utils;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
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
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.dto.EntityDocumentoResponsabilidad;
import ec.gob.ambiente.rcoa.dto.EntityInformeTecnicoEsIA;
import ec.gob.ambiente.rcoa.dto.EntityOficioArchivacionEsIA;
import ec.gob.ambiente.rcoa.dto.EntityOficioPronunciamientoEsIA;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.dto.EntityCertificadoParticipacionRCOA;
import ec.gob.ambiente.suia.reportes.DocumentoPDFPlantillaHtml;

public class UtilGenerarPdf {

    private static final Logger LOG = Logger.getLogger(UtilGenerarPdf.class);
    @Getter
    @Setter
    static String areaRes = null;
    
    @Getter
    @Setter
    static Area areas = null;
    
    public static File generarFichero(String cadenaHtml,
            final String nombreReporte, final Boolean mostrarNumeracionPagina,
            final Object entityInforme) {
    	return generarFichero(cadenaHtml, nombreReporte, mostrarNumeracionPagina, entityInforme, "<span style='color:red'>INGRESAR</span>");
	}
    
    public static File generarFichero(String cadenaHtml,
            final String nombreReporte, final Boolean mostrarNumeracionPagina,
            final Object entityInforme, String textoNull) {
		return generarFichero(cadenaHtml,
		nombreReporte, mostrarNumeracionPagina,
		entityInforme, textoNull, null, false);
	}
    
	public static File generarFichero(String cadenaHtml,
			final String nombreReporte, final Boolean mostrarNumeracionPagina,
			final Object entityInforme, String textoNull, byte[] imagenByte,
			final Boolean mostrarDireccionMae) {
		
		if (entityInforme.getClass().getSimpleName().equals("EntityOficioPronunciamientoEsIA")) {
			EntityOficioPronunciamientoEsIA area1 = (EntityOficioPronunciamientoEsIA) entityInforme;
			areaRes = area1.getSiglasEnte();
		} else if (entityInforme.getClass().getSimpleName().equals("EntityCertificadoParticipacionRCOA")) {
			EntityCertificadoParticipacionRCOA area1 = (EntityCertificadoParticipacionRCOA) entityInforme;
			areaRes = area1.getAreaResponsable();
		} else if (entityInforme.getClass().getSimpleName().equals("EntityInformeTecnicoEsIA")) {
			EntityInformeTecnicoEsIA entidad = (EntityInformeTecnicoEsIA) entityInforme;
			areaRes = entidad.getSiglasEnte();
		} else if (entityInforme.getClass().getSimpleName().equals("EntityDocumentoResponsabilidad")) {
			EntityDocumentoResponsabilidad entidad = (EntityDocumentoResponsabilidad) entityInforme;
			areaRes = entidad.getArea().getAreaAbbreviation();
		} else if (entityInforme.getClass().getSimpleName().equals("EntityOficioArchivacionEsIA")) {
			EntityOficioArchivacionEsIA entidad = (EntityOficioArchivacionEsIA) entityInforme;
			areaRes = entidad.getSiglasEnte();
		} else {
			areaRes = null;
		}

		if (areaRes != null) {
			Area areaProyecto = JsfUtil.getBean(ProyectosBean.class).getProyecto() == null ? 
					JsfUtil.getBean(ProyectosBean.class).getProyectoRcoa().getAreaResponsable() : 
						JsfUtil.getBean(ProyectosBean.class).getProyecto().getAreaResponsable();
			areas = areaProyecto;
		} else {
			areas = null;
		}

		/**
		 * FIN Pasa las entidades para obtener las áreas responsables
		 */
		Document document = null;
		PdfWriter writer = null;
		OutputStream fileOutputStream = null;
		File file = null;
		String archivo = "";
		try {
			cadenaHtml = generarHtml(cadenaHtml, entityInforme, textoNull);

			String buf = cadenaHtml;
			file = File.createTempFile(nombreReporte, ".pdf");
			List<String> listaTags = new ArrayList<>();
			Pattern pa = Pattern.compile("\\$F[{]\\w+[}$]");
			buf = "<p></p>";
			Matcher mat = pa.matcher(buf);
			while (mat.find()) {
				listaTags.add(mat.group());
			}
			document = new Document(PageSize.A4, 36, 36, 135, 60);
			fileOutputStream = new FileOutputStream(file);
			writer = PdfWriter.getInstance(document, fileOutputStream);
			writer.createXmpMetadata();

			writer.setPageEvent(new HeaderDocumentoPdf(null,
					mostrarNumeracionPagina, mostrarDireccionMae));

			document.open();
			PdfContentByte cb = writer.getDirectContent();
			archivo = nombreReporte + new Date().getTime() + ".pdf";
			createPdfHtml(cadenaHtml, System.getProperty("java.io.tmpdir")
					+ "/" + archivo);

			PdfReader readerF = new PdfReader(
					System.getProperty("java.io.tmpdir") + "/" + archivo);
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

			// Firma automatica
			if (imagenByte != null) {
				document.add(DocumentoPDFPlantillaHtml
						.agregarFirmaImagen(imagenByte));
			}

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
	
	static class HeaderDocumentoPdf extends PdfPageEventHelper {

        String[] params;
        Boolean mostrarNumeracionPagina;
        Boolean mostrarDireccionMae;
        PdfTemplate total; //The template with the total number of pages

        public HeaderDocumentoPdf() {
        }

        public HeaderDocumentoPdf(String[] params,
                                           Boolean mostrarNumeracionPagina, Boolean mostrarDireccionMae) {
            this.params = params;
            this.mostrarNumeracionPagina = mostrarNumeracionPagina;
            this.mostrarDireccionMae = mostrarDireccionMae;
        }

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
            try {

                DocumentosFacade docf = BeanLocator.getInstance(DocumentosFacade.class);
                String nombre_logo = null;
                if (areaRes != null) {
                    nombre_logo = "logo__" + areaRes.replace("/", "_") + ".png";
                } else {
                    imghead = Image.getInstance(getRecursoImage("logo_mae_pie.png"));
                }


                byte[] logo_datos = null;
				try {
					if (nombre_logo != null)
						logo_datos = docf.descargarDocumentoPorNombre(nombre_logo);
				} catch (CmisAlfrescoException e) {
					e.printStackTrace();
				}
				
                if (logo_datos == null){
                    imghead = Image.getInstance(getRecursoImage("logo_mae.png"));
                    imghead.setAbsolutePosition(0, 0);		
                    imghead.scalePercent(50);
        			
        			imgbackground = Image.getInstance(getRecursoImage("fondo-documentos.png"));
        			imgbackground.setAbsolutePosition(0, -1);
        			imgbackground.scalePercent(50);
        			document.add(imgbackground);
                }else{
                    imghead = Image.getInstance(logo_datos);
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
                tableHeader.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
                PdfPCell cellVacia = new PdfPCell();
                cellVacia.setBorder(Rectangle.NO_BORDER);
                tableHeader.addCell(cellVacia);
                tableHeader.addCell(cellVacia);
                tableHeader.addCell(cellVacia);
                tableHeader.writeSelectedRows(0, -1, 34, 835, writer.getDirectContent());
                
                if (params != null) {
                    int top = 790;
                    Font font = new Font(Font.FontFamily.HELVETICA, 8);
                    font.setStyle(Font.BOLD);
                    for (String s : params) {
                        PdfPTable tableHeader1 = new PdfPTable(4);
                        Phrase letra = new Phrase(s, font);
                        tableHeader1.setWidths(new int[]{14, 10, 10, 14});
                        tableHeader1.setTotalWidth(527);
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
                tableFooter.setTotalWidth(527);
                tableFooter.writeSelectedRows(0, -36, 10, 40,writer.getDirectContent());
                tableFooter.writeSelectedRows(0, -36, 10, 40,writer.getDirectContent());

            } catch (DocumentException de) {
                throw new ExceptionConverter(de);
            } catch (MalformedURLException e) {
                LOG.error(e, e);
            } catch (IOException e) {
                LOG.error(e, e);
            }
        }
    }
	
	public static PdfPTable crearFoot(int numPaginaActual, Image total,
			Boolean mostrarNumeracionPagina, Boolean mostrarDireccionMae)
			throws DocumentException, MalformedURLException, IOException {
		Image imgfoot = null;

		DocumentosFacade docf = BeanLocator.getInstance(DocumentosFacade.class);
		if (areaRes == null) {
			imgfoot = Image.getInstance(getRecursoImage("pie_ci.png"));
			imgfoot.scalePercent(50f);
		} else {
			try {
				String nombre_pie = "pie__" + areaRes.replace("/", "_") + ".png";
				byte[] logo_datos = docf.descargarDocumentoPorNombre(nombre_pie);
				imgfoot = Image.getInstance(logo_datos);
				imgfoot.scalePercent(60f);
			} catch (Exception e) {
			}
		}

		PdfPTable table = new PdfPTable(3);
		table.setWidthPercentage(288 / 5.23f);
		table.setWidths(new int[] { 16, 1, 1 });
		if (imgfoot != null) {
			imgfoot.setAbsolutePosition(0, 0);
			imgfoot.setAlignment(Image.ALIGN_CENTER);

			PdfPCell cell;
			cell = new PdfPCell(imgfoot);
			cell.setColspan(3);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(""));
			cell.setRowspan(2);
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);
		}
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
	
	public static void createPdfHtml(String html, String destinoFile) throws IOException {        

        com.itextpdf.kernel.geom.PageSize pageSize =  com.itextpdf.kernel.geom.PageSize.A4;
        html=html.replace("<table", "<table border=\"1\" align=\"center\"").replace("<a", "<a style=\"word-wrap:break-word;\"");

        try (   com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter(destinoFile);
                PdfDocument pdfDoc = new PdfDocument(writer);
                com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdfDoc);   )
        {
            pdfDoc.setTagged();
            pdfDoc.setDefaultPageSize(pageSize);     
            document.setMargins(100, 70, 70, 70); //top, right, bottom, left

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
