package ec.gob.ambiente.suia.reportes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import org.apache.log4j.Logger;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.dto.EntityPersona;
import ec.gob.ambiente.suia.utils.Constantes;

public class ReporteCertificadoInterseccion {
	private static final Logger LOG = Logger.getLogger(ReporteCertificadoInterseccion.class);

	public static File crearCertificadoInterseccionPDF(String[] parametros, String keyPlantilla,
			Persona personaFirmaResponsable, String keyTextoDeCompromiso, EntityPersona proponente, byte[] imagenFirma, Integer marcaAgua) {

		File tmpFile = null;
		try {
			tmpFile = File.createTempFile("certificadoInterseccion", ".pdf");
			FileOutputStream out = new FileOutputStream(tmpFile);
			// create a new document
			Document document = new Document(PageSize.A4, 36, 36, 68, 45);
			PdfWriter pdfWriter = PdfWriter.getInstance(document, out);
			pdfWriter.setBoxSize("art", new Rectangle(36, 54, 559, 788));
			pdfWriter.setPageEvent(new PlantillaHeaderFooterOficioCertificadoInterseccion());
			// document header attributes
			document = DocumentoPDFPlantillaHtml.agregarInformacionDocumento(document, "Certificado intersección");
			// open document
			document.open();
			String htmlPage = DocumentoPDFPlantillaHtml.getPlantillaConParametros(keyPlantilla, parametros);
			// Pipelines
			PdfWriterPipeline pdf = new PdfWriterPipeline(document, pdfWriter);
			HtmlPipeline html = new HtmlPipeline(DocumentoPDFPlantillaHtml.getHtmlContext(), pdf);
			CssResolverPipeline css = new CssResolverPipeline(DocumentoPDFPlantillaHtml.getCss(), html);

			XMLWorker worker = new XMLWorker(css, true);
			XMLParser p = new XMLParser(worker);
			p.parse(new StringReader(htmlPage));

			document.add(DocumentoPDFPlantillaHtml.agregarFirmaImagen(imagenFirma));
			Font font = FontFactory.getFont("Helvetica", 8);
			document.add(new Paragraph(personaFirmaResponsable.getTitulo()+" "+personaFirmaResponsable.getNombre(), font));
			document.add(new Paragraph(DocumentoPDFPlantillaHtml.getResourcePlantillaInformes().getString("cargo_autoridad_responsable"), font));
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			String compromiso = DocumentoPDFPlantillaHtml.getPlantillaConParametros(keyTextoDeCompromiso, new String[] { proponente.getNombre(),
					proponente.getPin() });
			Paragraph parrofoCompromiso = new Paragraph(10, compromiso, FontFactory.getFont("Helvetica", 8));
			parrofoCompromiso.setAlignment(Element.ALIGN_JUSTIFIED);
			document.add(parrofoCompromiso);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);

			Paragraph atentamente = new Paragraph("Atentamente,", font);
			Paragraph nombreResponsable = new Paragraph(proponente.getNombre(), font);
			Paragraph cedulaResponsable = new Paragraph(proponente.getPin(), font);
			atentamente.setAlignment(Element.ALIGN_CENTER);
			nombreResponsable.setAlignment(Element.ALIGN_CENTER);
			cedulaResponsable.setAlignment(Element.ALIGN_CENTER);
			document.add(atentamente);
			document.add(nombreResponsable);
			document.add(cedulaResponsable);

			// close the document
			document.close();
			// close the writer
			pdfWriter.close();
			
			if (marcaAgua==1){
            	if (Constantes.getDocumentosBorrador() || marcaAgua==1) {
                    return poneSelloAguaTotal(tmpFile);
                } else {
                    return tmpFile;
                }
            }else{
            	return tmpFile;
            }
			
//			return tmpFile;

		} catch (Exception e) {
			LOG.error(e , e);
			return null;
		}

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

}
