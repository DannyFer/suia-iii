package ec.gob.ambiente.suia.certificado.ambiental;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.EJB;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
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

import ec.gob.ambiente.certificado.ambiental.bean.CertificadoAmbientalMaeBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.rcoa.facade.SecuenciaCertificadoAmbientalRcoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.reportes.DocumentoPDFPlantillaHtml;
import ec.gob.ambiente.suia.reportes.ReporteLicenciaAmbientalCategoriaII;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.Constantes;

public class CertificadoAmbientalMAE {
	
	private static final Logger LOG = Logger
            .getLogger(ReporteLicenciaAmbientalCategoriaII.class);
	
	
    @Getter
    @Setter
    static String areaRes = null;
    
	public static File crearCertificadoMae(String pdfFilename, String titulo,
			Persona personaFirmaResponsable, String rol, Area area,
			Integer marcaAgua, boolean htmlMineriaPerforacion,
			ProyectoLicenciaCoa proyectoactivo, Usuario usuario) throws Exception {

		CertificadoAmbientalMaeBean informeProvincial = BeanLocator.getInstance(CertificadoAmbientalMaeBean.class);
				
		String secuencia = "";
		
		if(area.getTipoArea().getSiglas().equals("OT")){
			String abreviacion = area.getArea().getAreaAbbreviation();
			areaRes = abreviacion;
			if(areaRes != null){
				secuencia = generarCodigoCertificado(area.getArea());
			}			
		}else if(area.getTipoArea().getSiglas().equals("EA")){
			areaRes = area.getAreaAbbreviation();
			if(areaRes != null){
				secuencia = generarCodigoCertificadoGad(area);
			}
		}else{
			areaRes = area.getAreaAbbreviation();
			if(areaRes != null){
				secuencia = generarCodigoCertificado(area);
			}
		}		
		
		if(areaRes == null){
			if(area.getTipoArea().getSiglas().equals("OT")){				
				areaRes = proyectoactivo.getAreaResponsable().getArea().getAreaAbbreviation();
				secuencia = generarCodigoCertificado(proyectoactivo.getAreaResponsable().getArea());
			}else if(area.getTipoArea().getSiglas().equals("EA")){
				areaRes = proyectoactivo.getAreaResponsable().getAreaAbbreviation();
				secuencia = generarCodigoCertificadoGad(area);
			}else{
				areaRes = proyectoactivo.getAreaResponsable().getAreaAbbreviation();
				secuencia = generarCodigoCertificado(area);
			}
		}

		 Document document = null;
	        PdfWriter writer = null;
	        OutputStream fileOutputStream = null;
	        File file = null;
	        String archivo="";
	        String cadenaHtml="";
	        try {
	        	String anios = "";

				SimpleDateFormat fecha = new SimpleDateFormat("yyyy", new Locale(
						"es"));
				anios = fecha.format(new java.util.Date());
								
	            cadenaHtml = informeProvincial.visualizarOficio(proyectoactivo.getCodigoUnicoAmbiental(), secuencia, usuario);

	            String buf = cadenaHtml;
	            file = File.createTempFile(pdfFilename, ".pdf");
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

	            writer.setPageEvent(new HeaderFichaAmbientalMineria(
	                    null, true, false));

	            document.open();
	            PdfContentByte cb = writer.getDirectContent();
	            archivo=pdfFilename+new Date().getTime()+".pdf";
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
	        File directory = new File(System.getProperty("java.io.tmpdir") + "/"+archivo);
            File files = directory;
            files.delete();
            return file;
	}
    
    private static File poneSelloAguaTotal(File file) {
        try {
            String nombre = file.getAbsolutePath();
            PdfReader reader = new PdfReader(nombre);
            int n = reader.getNumberOfPages();

            // Create a stamper that will copy the document to a new file
            PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(
                    file.getAbsolutePath() + ".tmp"));
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
                // under.showText("Borrador ");
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
    
	public static PdfPTable crearFoot(int numPaginaActual, Image total,
			Boolean mostrarNumeracionPagina, Boolean mostrarDireccionMae)
			throws DocumentException, MalformedURLException, IOException {
		Image imgfoot = null;
		String direccionMae = " ";
		/**
		 * Nombre:SUIA Descripción: Permite obtener logos deacuerdo al area
		 * ParametrosIngreso: PArametrosSalida: Fecha:15/08/2015
		 */
		DocumentosFacade docf = BeanLocator.getInstance(DocumentosFacade.class);
		String nombre_pie = null;

		if (areaRes != null) {
			nombre_pie = "pie__" + areaRes.replace("/", "_") + ".png";
		} else {
//			imgfoot = Image.getInstance(getRecursoImage("pie_ci.png"));
			imgfoot = Image.getInstance(urlGenerar("pie_ci.png"));
		}
		byte[] logo_datos = null;
		try {
			logo_datos = docf.descargarDocumentoPorNombre(nombre_pie);
		} catch (CmisAlfrescoException e) {
		}
		PdfPTable table = new PdfPTable(3);
		table.setWidthPercentage(288 / 5.23f);
		table.setWidths(new int[] { 16, 1, 1 });
		PdfPCell cell;
		if (logo_datos == null) {
			byte[] logoPie = null;
			try {
				logoPie = docf.descargarDocumentoPorNombre("logo_ministerio_pie_documento.png");
			} catch (CmisAlfrescoException e) {
			}
			
			imgfoot = Image.getInstance(logoPie);
			imgfoot.setAbsolutePosition(0, 0);
            imgfoot.setAlignment(Image.ALIGN_LEFT);
            imgfoot.scalePercent(50f);
            cell = new PdfPCell(imgfoot);
            cell.setColspan(4);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            mostrarDireccionMae=false;
            logo_datos= new byte[2]; 
		} else {
			imgfoot = Image.getInstance(logo_datos);
			imgfoot.setAbsolutePosition(0, 0);
			imgfoot.setAlignment(Image.ALIGN_CENTER);
			imgfoot.scalePercent(60f);
			cell = new PdfPCell(imgfoot);
			cell.setColspan(3);
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);
		}
		/**
		 * FIN Permite obtener logos deacuerdo al area
		 */
		cell = new PdfPCell(new Phrase("    "));
		cell.setFixedHeight(28);
		cell.setColspan(3);
		cell.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell);
		if (logo_datos == null) {
			if (mostrarDireccionMae) {
				direccionMae = getResourcePlantillaInformes().getString(
						"direccion_mae_pie");

				Phrase frase = new Phrase("Dirección: ", FontFactory.getFont(
						FontFactory.HELVETICA, 8, Font.BOLD, BaseColor.GRAY));
				frase.add(new Chunk(direccionMae, FontFactory.getFont(
						FontFactory.HELVETICA, 8, BaseColor.GRAY)));
				cell = new PdfPCell(frase);
			} else {
				cell = new PdfPCell(new Phrase(direccionMae,
						FontFactory.getFont(FontFactory.HELVETICA, 8,
								BaseColor.GRAY)));
			}
		} else {
			cell = new PdfPCell(new Phrase(""));
		}
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setRowspan(3);
		cell.setBorder(Rectangle.NO_BORDER);
		if (mostrarNumeracionPagina != null && !mostrarNumeracionPagina) {
			cell.setColspan(3);
		}
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
    
    static class HeaderFichaAmbientalMineria extends PdfPageEventHelper {

        String[] params;
        Boolean mostrarNumeracionPagina;
        Boolean mostrarDireccionMae;
        /**
         * The template with the total number of pages.
         */
        PdfTemplate total;

        public HeaderFichaAmbientalMineria() {
        }

        public HeaderFichaAmbientalMineria(String[] params,
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

                /**
                 * Nombre:SUIA
                 * Descripción: Permite obtener logos deacuerdo al area
                 * ParametrosIngreso:
                 * PArametrosSalida:
                 * Fecha:15/08/2015
                 */
                DocumentosFacade docf = BeanLocator
                        .getInstance(DocumentosFacade.class);
                String nombre_logo = null;
                if (areaRes != null) {
                    nombre_logo = "logo__" + areaRes.replace("/", "_") + ".png";
                } else {//                	
                	imghead = Image.getInstance(urlGenerar("logo_mae_pie.png"));
//                    imghead = Image.getInstance(getRecursoImage("logo_mae_pie.png"));
                }


                byte[] logo_datos = null;
                try {
                    logo_datos = docf.descargarDocumentoPorNombre(nombre_logo);
                } catch (CmisAlfrescoException e) {
                    // TODO Auto-generated catch block
//							e.printStackTrace();
                }
                if (logo_datos == null){
                	byte[] logoCabecera = null;
                	byte[] logoFondo = null;
					try {
						logoCabecera = docf.descargarDocumentoPorNombre("logo_ministerio_cabecera_documento.png");
						logoFondo = docf.descargarDocumentoPorNombre("logo_ministerio_fondo_documento.png");
					} catch (CmisAlfrescoException e) {

					}
					
                	imghead = Image.getInstance(logoCabecera);
                    imghead.setAbsolutePosition(0, 0);		
        			imghead.scalePercent(50);
        			
        			imgbackground = Image.getInstance(logoFondo);
        			imgbackground.setAbsolutePosition(0, -1);
        			imgbackground.scalePercent(50);
        			document.add(imgbackground);
                }else{
                    imghead = Image.getInstance(logo_datos);
                    imghead.setAbsolutePosition(0, 0);
                    imghead.setAlignment(Image.ALIGN_CENTER);
                    imghead.scalePercent(60f);
                }
                /**
                 * FIN Permite obtener logos deacuerdo al area
                 */

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
                tableHeader.writeSelectedRows(0, -1, 34, 815,
                        writer.getDirectContent());
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
                        Image.getInstance(total), this.mostrarNumeracionPagina, this.mostrarDireccionMae);
                tableFooter.setTotalWidth(527);
                tableFooter.writeSelectedRows(0, -36, 0, 55,
                        writer.getDirectContent());
                tableFooter.writeSelectedRows(0, -36, 0, 55,
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
        html=html.replace("<table", "<table border=\"1\" align=\"center\"").replace("<a", "<a style=\"word-wrap:break-word;\"");

        try (   com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter(destinoFile);
                PdfDocument pdfDoc = new PdfDocument(writer);
                com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdfDoc);   )
        {
            pdfDoc.setTagged();
            pdfDoc.setDefaultPageSize(pageSize);     
            document.setMargins(100, 36, 70, 36);

            List<IElement> elements = HtmlConverter.convertToElements(html);
            for (IElement element : elements)
            {
                if (element instanceof IBlockElement)
                    document.add((IBlockElement) element);
            }
        }
    }
    
  //MAAE-SUIA-DZDP-2020-CA-001
  	private static String generarCodigoCertificado(Area area) {
  		SecuenciasFacade secuenciasFacade=BeanLocator.getInstance(SecuenciasFacade.class);
  		String anioActual=secuenciasFacade.getCurrentYear();
  		String nombreSecuencia="CODIGO_CERTIFICADO_AMBIENTAL"+anioActual+"_"+area.getAreaAbbreviation();
  		
  		if(area.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_PC.toUpperCase())){
  			area = area.getArea();
  		}
  		
  		try {
  			return Constantes.SIGLAS_INSTITUCION + "-SUIA-"
  					+ area.getAreaAbbreviation()
  					+ "-"
  					+ anioActual
  					+ "-"		
  					+ "CA"
  					+ "-"
  					+ secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia, 4);
  		} catch (Exception e) {
  			e.printStackTrace();
  		}
  		return "";
  	}
  	
  	//GADPXXX-SUIA-2020-CA-001
  	private static String generarCodigoCertificadoGad(Area area) {
  		SecuenciasFacade secuenciasFacade=BeanLocator.getInstance(SecuenciasFacade.class);
  		String anioActual=secuenciasFacade.getCurrentYear();
  		String nombreSecuencia="CODIGO_CERTIFICADO_AMBIENTAL"+anioActual+"_"+area.getAreaAbbreviation();
  		
  		try {
  			return 	area.getAreaAbbreviation()
  					+ "-"
  					+ "SUIA"
  					+ "-"
  					+ anioActual
  					+ "-"		
  					+ "CA"
  					+ "-"
  					+ secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia, 4);
  		} catch (Exception e) {
  			e.printStackTrace();
  		}
  		return "";
  	}
    
  	public static URL urlGenerar(String imagen) throws MalformedURLException{
  		URL url = new URL(Constantes.getUrlCodigoCertificadoQR()+"/resources/images/"+imagen);
  		return url;
  	}
}
