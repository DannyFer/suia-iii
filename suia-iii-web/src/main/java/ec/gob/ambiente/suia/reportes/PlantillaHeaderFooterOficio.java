package ec.gob.ambiente.suia.reportes;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import ec.gob.ambiente.suia.domain.Area;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

public class PlantillaHeaderFooterOficio extends PdfPageEventHelper {

    private static final Logger LOGG = Logger.getLogger(PlantillaHeaderFooterOficio.class);
    /**
     * The template with the total number of pages.
     */
    PdfTemplate total;
    private Area areaActual;

    public PlantillaHeaderFooterOficio() {
    }

    public PlantillaHeaderFooterOficio(Area area) {
        areaActual = area;
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
        ColumnText.showTextAligned(total, Element.ALIGN_LEFT, new Phrase(String.valueOf(writer.getPageNumber() - 1)),
                2, 2, 0);
    }

    public void onEndPage(PdfWriter writer, Document document) {
        Image imghead = null;
        Image imgbackground = null;
        try {
            Map<String, String> recursos = UtilDocumento.cargarImageneArea(areaActual);
            //verifico si es el logo del ministerio cambio de logo y pongo el fondo del condor
            if(recursos.get("logo_mae_pie").equals("logo_mae_pie.png")){
                imghead = Image.getInstance(UtilDocumento.getRecursoImage("logo_mae.png"));
                imghead.setAbsolutePosition(0, 0);
    			imghead.scalePercent(50);

    			imgbackground = Image.getInstance(UtilDocumento.getRecursoImage("fondo-documentos.png"));
    			imgbackground.setAbsolutePosition(0, 15);
    			imgbackground.scalePercent(50);
    			document.add(imgbackground);
            }else{
                imghead = Image.getInstance(UtilDocumento.getRecursoImage(recursos.get("logo_mae_pie")));
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
            PdfPCell cellImagen = new PdfPCell();
            cellImagen.addElement(imghead);
            cellImagen.setBorder(Rectangle.NO_BORDER);
            
            cellImagen.setPaddingLeft(50f);
            
            tableHeader.addCell(cellImagen);
            tableHeader.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            PdfPCell cellVacia = new PdfPCell();
            cellVacia.setBorder(Rectangle.NO_BORDER);
            tableHeader.addCell(cellVacia);
            tableHeader.addCell(cellVacia);
            tableHeader.addCell(cellVacia);
            tableHeader.writeSelectedRows(0, -1, 0, 830, writer.getDirectContent());
        } catch (DocumentException de) {
            throw new ExceptionConverter(de);
        } catch (MalformedURLException e) {
            LOGG.error(e, e);
        } catch (IOException e) {
            LOGG.error(e, e);
        }
    }


}