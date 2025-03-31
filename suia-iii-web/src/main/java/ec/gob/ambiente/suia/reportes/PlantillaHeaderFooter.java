package ec.gob.ambiente.suia.reportes;

import org.apache.log4j.Logger;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

public class PlantillaHeaderFooter extends PdfPageEventHelper {

	private static final Logger LOG = Logger.getLogger(PlantillaHeaderFooter.class);
	/** The template with the total number of pages. */
	PdfTemplate total;
	PdfTemplate tp = null;
	Rectangle rect = null;

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
		rect = writer.getBoxSize("art");
		Image imghead = null;
		PdfContentByte cbhead = null;

		try {
			imghead = Image.getInstance(UtilDocumento.getRecursoImage("logo_mae_pie.png"));

			imghead.setAbsolutePosition(0, 0);
			imghead.setAlignment(Image.ALIGN_CENTER);
			imghead.scalePercent(60f);

			cbhead = writer.getDirectContent();
			tp = cbhead.createTemplate(500, 250);
			tp.addImage(imghead);
			cbhead.addTemplate(tp, 500, rect.getTop());

			PdfPTable tableFooter = DocumentoPDFPlantillaHtml.crearFoot(writer.getPageNumber(),
					Image.getInstance(total));
			tableFooter.setTotalWidth(527);
			tableFooter.writeSelectedRows(0, -1, 36, 64, writer.getDirectContent());
			tableFooter.writeSelectedRows(0, -1, 36, 64, writer.getDirectContent());
		} catch (Exception e) {
			LOG.error(e , e);
		}
	}
}
