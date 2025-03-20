package ec.gob.ambiente.suia.reportes;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.log4j.Logger;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

public class PlantillaHeaderFooterOficioCertificadoInterseccion extends PdfPageEventHelper {
	private static final Logger LOG = Logger.getLogger(PlantillaHeaderFooterOficioCertificadoInterseccion.class);
	/** The template with the total number of pages. */
	PdfTemplate total;

	/**
	 * Creates the PdfTemplate that will hold the total number of pages.
	 * 
	 * @see com.itextpdf.text.pdf.PdfPageEventHelper#onOpenDocument(com.itextpdf.text.pdf.PdfWriter,
	 *      com.itextpdf.text.Document)
	 */
	public void onOpenDocument(PdfWriter writer, Document document) {
		total = writer.getDirectContent().createTemplate(30, 16);
	}

	public void onCloseDocument(PdfWriter writer, Document document) {
		ColumnText.showTextAligned(total, Element.ALIGN_LEFT, new Phrase(String.valueOf(writer.getPageNumber() - 1)),
				2, 2, 0);
	}

	public void onEndPage(PdfWriter writer, Document document) {
		Image imghead = null;
		Image imgbackground = null;

		try {
			imghead = Image.getInstance(UtilDocumento.getRecursoImage("logo_mae.png"));

			imghead.setAbsolutePosition(0, 0);
			//imghead.setAlignment(Image.ALIGN_CENTER);
			imghead.scalePercent(50);
			// inserto la imagen del bacgound
			imgbackground = Image.getInstance(UtilDocumento.getRecursoImage("fondo-documentos.png"));
			imgbackground.setAbsolutePosition(0, 20);
			imgbackground.scalePercent(50);
			document.add(imgbackground);

			PdfPTable tableHeader = new PdfPTable(4);
			tableHeader.setWidths(new int[] { 14, 10, 10, 14 });
			tableHeader.setTotalWidth(527);
			tableHeader.setLockedWidth(true);
			tableHeader.getDefaultCell().setFixedHeight(20);
			tableHeader.getDefaultCell().setBorder(Rectangle.NO_BORDER);
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

			PdfPTable tableFooter = DocumentoPDFPlantillaHtml.crearFoot(writer.getPageNumber(),
					Image.getInstance(total));
			tableFooter.setTotalWidth(527);
			tableFooter.writeSelectedRows(0, -1, 36, 64, writer.getDirectContent());

		} catch (DocumentException de) {
			throw new ExceptionConverter(de);
		} catch (MalformedURLException e) {
			LOG.error(e , e);
		} catch (IOException e) {
			LOG.error(e , e);
		}
	}
}
