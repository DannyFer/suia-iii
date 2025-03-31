package ec.gob.ambiente.suia.reportes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.Files;

import javax.ejb.EJB;

import org.apache.log4j.Logger;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEvent;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;

import ec.gob.ambiente.control.aprobacionrequisitostecnicos.informes.bean.InformeregistromunicipalBean;
import ec.gob.ambiente.control.aprobacionrequisitostecnicos.informes.bean.InformeregistroprovincialBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.ReporteGADMunicipal;
import ec.gob.ambiente.suia.utils.ReporteGADProvincial;
import ec.gob.ambiente.suia.utils.ReporteResolucion020;

public class ReporteLicenciaAmbientalCategoriaII {

    private static final Logger LOG = Logger
            .getLogger(ReporteLicenciaAmbientalCategoriaII.class);

    private static final String GOBIERNO = "GOBIERNO";

    private static final String MUNICIPIO = "MUNICIPIO";
    
	@EJB
	private static DocumentosFacade documentosFacade;

    public static File crearLicenciaAmbientalCategoriaII(String pdfFilename,
                                                         String[] parametros, String keyPlantilla, String titulo,
                                                         Persona personaFirmaResponsable, byte[] imagenFirma, String rol,
                                                         Area area,Integer marcaAgua,boolean htmlMineriaPerforacion) {
		 /**
	     * Nombre:SUIA
	     * Descripción: Reporte de cada uno de los entes en Registro Ambiental
	     * ParametrosIngreso:
	     * PArametrosSalida:
	     * Fecha:
	     */

        ReporteGADProvincial gadProvincial = BeanLocator
                .getInstance(ReporteGADProvincial.class);// santiago
        ReporteGADMunicipal gadMunicipal = BeanLocator
                .getInstance(ReporteGADMunicipal.class);// danny
        ReporteResolucion020 resolucion020 = BeanLocator
                .getInstance(ReporteResolucion020.class);
        InformeregistroprovincialBean informeProvincial = BeanLocator
                .getInstance(InformeregistroprovincialBean.class);// hirma
        InformeregistromunicipalBean informeMunicipal = BeanLocator
                .getInstance(InformeregistromunicipalBean.class);// woker
    	/**
  		  * FIN Reporte de cada uno de los entes en Registro Ambiental
  		  */

        String htmlPage = null;
        File tmpFile = null;
        try {
            tmpFile = File.createTempFile(pdfFilename, ".pdf");
            FileOutputStream out = new FileOutputStream(tmpFile);
            // create a new document
            Document document = new Document(PageSize.A4, 36, 36, 80, 54);
            PdfWriter pdfWriter = PdfWriter.getInstance(document, out);
            pdfWriter.setBoxSize("art", new Rectangle(36, 54, 559, 788));
            // pdfWriter.setPageEvent(isInforme ? new HeaderFooter() : new
            // HeaderFooterOficio());
            pdfWriter
                    .setPageEvent((PdfPageEvent) new PlantillaHeaderFooterOficio(
                            area));
            // document header attributes
            document = DocumentoPDFPlantillaHtml.agregarInformacionDocumento(
                    document, titulo);
            // open document
            document.open();
            /**
			    * Nombre:SUIA
			    * Descripción: identificar que reporte se genera de acuerdo al sector. 
			    * ParametrosIngreso:
			    * PArametrosSalida:
			    * Fecha:16/08/2015
			    */
            
			String codigoProyecto = "", numeroResolucion = "", numeroOficio = "", numeroLicencia = "", sector = "";
			if(parametros!=null){
				for (int i = 0; i < parametros.length; i++) {
					if (i == 14 && parametros[i].equals("Minería")) {
//						codigoProyecto = parametros[24];
//						numeroResolucion = parametros[0];
//						numeroOficio = parametros[7];
//						numeroLicencia = parametros[29];
						
						codigoProyecto = parametros[39];
						numeroResolucion = parametros[0];
						numeroOficio = parametros[4];
						numeroLicencia = parametros[12];
						break;

					} else {
						numeroResolucion = parametros[0];
						numeroOficio = parametros[3];
						numeroLicencia = parametros[8];
						codigoProyecto = parametros[39];
					}
				}
				
				sector = gadProvincial.sector(codigoProyecto);
			}
			

			
			
			if (area.getTipoArea().getId().equals(2))
            htmlPage = DocumentoPDFPlantillaHtml.getPlantillaConParametros(
                    keyPlantilla, parametros);
			if (area.getTipoArea().getId().equals(3)) {
				if (GOBIERNO.equals(area.getTipoEnteAcreditado())) {
					if (sector.equals("Minería"))
						htmlPage = gadProvincial.visualizarOficio(
								codigoProyecto, numeroResolucion, numeroOficio,
								numeroLicencia);
					else
						htmlPage = informeProvincial.visualizarOficio(
								codigoProyecto, numeroResolucion, numeroOficio,
								numeroLicencia);
				} else if (MUNICIPIO.equals(area.getTipoEnteAcreditado())) {
					if (sector.equals("Minería"))
						htmlPage = gadMunicipal.visualizarOficio(
								codigoProyecto, numeroResolucion, numeroOficio,
								numeroLicencia);
					else
						htmlPage = informeMunicipal.visualizarOficio(
								codigoProyecto, numeroResolucion, numeroOficio,
								numeroLicencia);
				}
			}
			
			if(htmlMineriaPerforacion)
			{
//				htmlPage = resolucion020.visualizarOficio(
//						codigoProyecto, numeroResolucion, numeroOficio,
//						personaFirmaResponsable.getNombre());
				htmlPage = resolucion020.visualizarOficioRcoa(
						codigoProyecto, numeroResolucion, numeroLicencia,
						personaFirmaResponsable.getNombre());
			}
						
			// En caso de que no se obtenga una plantilla se pone por defecto
			if (htmlPage == null) {
				htmlPage = DocumentoPDFPlantillaHtml.getPlantillaConParametros(
						keyPlantilla, parametros);
			}
			/**
			  * FIN identificar que reporte se genera de acuerdo al sector. 
			  */

            // String htmlPage =
            // DocumentoPDFPlantillaHtml.getPlantillaConParametros(keyPlantilla,
            // parametros);

            // Pipelines
            PdfWriterPipeline pdf = new PdfWriterPipeline(document, pdfWriter);
            HtmlPipeline html = new HtmlPipeline(
                    DocumentoPDFPlantillaHtml.getHtmlContext(), pdf);

            CssResolverPipeline css = new CssResolverPipeline(
                    DocumentoPDFPlantillaHtml.getCss(), html);

            XMLWorker worker = new XMLWorker(css, true);
            XMLParser p = new XMLParser(worker);
            p.parse(new StringReader(htmlPage));

            // Firma automatica
            document.add(DocumentoPDFPlantillaHtml
                    .agregarFirmaImagen(imagenFirma));
            Font font = FontFactory.getFont("Times-Roman", 10);
            document.add(new Paragraph(personaFirmaResponsable.getNombre(),
                    font));
            document.add(new Paragraph(rol, font));
            // document.add(Chunk.NEWLINE);
            // document.add(Chunk.NEWLINE);

            // close the document
            document.close();
            // close the writer
            pdfWriter.close();
            if (marcaAgua == 1) {
				if (Constantes.getDocumentosBorrador() || marcaAgua==1) {
					return poneSelloAguaTotal(tmpFile);
				} else {
					return tmpFile;
				}
			} else {
				return tmpFile;
			}
            
        } catch (FileNotFoundException e) {
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
    
    public static File crearLicenciaAmbientalCategoriaIIA(String pdfFilename, String[] parametros, String keyPlantilla,
            String titulo, Persona personaFirmaResponsable, byte[] imagenFirma, byte[] imagenLogo, String rol, Area area,
            Integer marcaAgua, boolean htmlMineriaPerforacion) throws IOException {
        /**
         * Nombre:SUIA Descripción: Reporte de cada uno de los entes en Registro
         * Ambiental ParametrosIngreso: PArametrosSalida: Fecha:
         */

        ReporteGADProvincial gadProvincial = BeanLocator.getInstance(ReporteGADProvincial.class);
        ReporteGADMunicipal gadMunicipal = BeanLocator.getInstance(ReporteGADMunicipal.class);
        ReporteResolucion020 resolucion020 = BeanLocator.getInstance(ReporteResolucion020.class);
        InformeregistroprovincialBean informeProvincial = BeanLocator.getInstance(InformeregistroprovincialBean.class);
        InformeregistromunicipalBean informeMunicipal = BeanLocator.getInstance(InformeregistromunicipalBean.class);

        /**
         * CAMBIO DE LOGO
         */

        Area areaResponsable = area;
        String nombrePie = "pie__" + areaResponsable.getAreaAbbreviation().replace("/", "_") + ".png";
        URL pie = null;
        UtilDocumento utilDocumento = new UtilDocumento();

        // Obtener pie de página
        pie = UtilDocumento.getRecursoImage("ente/" + nombrePie);
        if (pie == null) {
            try {
                byte[] pieDatos = documentosFacade.descargarDocumentoPorNombre(nombrePie);
                File archivoPie = new File(JsfUtil.devolverPathImagenEnte(nombrePie));
                FileOutputStream filePie = new FileOutputStream(archivoPie);
                filePie.write(pieDatos);
                filePie.close();
            } catch (Exception e) {
                LOG.error("Error al obtener la imagen del pie para el área " + areaResponsable.getAreaAbbreviation()
                        + " en /Documentos Fijos/DatosEnte/" + nombrePie, e);
                File archivoPie = new File(JsfUtil.devolverPathImagenMae());
                Files.copy(archivoPie.toPath(), archivoPie.toPath());
            }
        }

        // Guardar imagen del logo recibida como parámetro
        if (imagenLogo != null) {
            try {
                File archivoLogo = new File(JsfUtil.devolverPathImagenEnte("logo__" + areaResponsable.getAreaAbbreviation().replace("/", "_") + ".png"));
                FileOutputStream fileLogo = new FileOutputStream(archivoLogo);
                fileLogo.write(imagenLogo);
                fileLogo.close();
            } catch (IOException e) {
                LOG.error("Error al guardar la imagen del logo para el área " + areaResponsable.getAreaAbbreviation(), e);
            }
        }

        String htmlPage = null;
        File tmpFile = null;
        try {
            tmpFile = File.createTempFile(pdfFilename, ".pdf");
            FileOutputStream out = new FileOutputStream(tmpFile);
            // create a new document
            Document document = new Document(PageSize.A4, 36, 36, 80, 54);
            PdfWriter pdfWriter = PdfWriter.getInstance(document, out);
            pdfWriter.setBoxSize("art", new Rectangle(36, 54, 559, 788));
            pdfWriter.setPageEvent(new PlantillaHeaderFooterOficio(area));
            // document header attributes
            document = DocumentoPDFPlantillaHtml.agregarInformacionDocumento(document, titulo);
            // open document
            document.open();

            String codigoProyecto = "", numeroResolucion = "", numeroOficio = "", numeroLicencia = "", sector = "";
            if (parametros != null) {
                for (int i = 0; i < parametros.length; i++) {
                    if (i == 15 && parametros[i].equals("Minería")) {
                        codigoProyecto = parametros[39];
                        numeroResolucion = parametros[0];
                        numeroOficio = parametros[5];
                        numeroLicencia = parametros[13];
                        break;
                    } else {
                        numeroResolucion = parametros[0];
                        numeroOficio = parametros[4];
                        numeroLicencia = parametros[9];
                        codigoProyecto = parametros[39];
                    }
                }
                sector = gadProvincial.sector(codigoProyecto);
            }

            if (area.getTipoArea().getId().equals(2)) {
                htmlPage = DocumentoPDFPlantillaHtml.getPlantillaConParametros(keyPlantilla, parametros);
            } else if (area.getTipoArea().getId().equals(3)) {
                if (GOBIERNO.equals(area.getTipoEnteAcreditado())) {
                    if (sector.equals("Minería")) {
                        htmlPage = gadProvincial.visualizarOficio(codigoProyecto, numeroResolucion, numeroOficio, numeroLicencia);
                    } else {
                        htmlPage = informeProvincial.visualizarOficio(codigoProyecto, numeroResolucion, numeroOficio, numeroLicencia);
                    }
                } else if (MUNICIPIO.equals(area.getTipoEnteAcreditado())) {
                    if (sector.equals("Minería")) {
                        htmlPage = gadMunicipal.visualizarOficio(codigoProyecto, numeroResolucion, numeroOficio, numeroLicencia);
                    } else {
                        htmlPage = informeMunicipal.visualizarOficio(codigoProyecto, numeroResolucion, numeroOficio, numeroLicencia);
                    }
                }
            }

            if (htmlMineriaPerforacion) {
                htmlPage = resolucion020.visualizarOficioRcoa(codigoProyecto, numeroResolucion, numeroLicencia,
                        personaFirmaResponsable.getNombre());
            }

            // En caso de que no se obtenga una plantilla se pone por defecto
            if (htmlPage == null) {
                htmlPage = DocumentoPDFPlantillaHtml.getPlantillaConParametros(keyPlantilla, parametros);
            }

            PdfWriterPipeline pdf = new PdfWriterPipeline(document, pdfWriter);
            HtmlPipeline html = new HtmlPipeline(DocumentoPDFPlantillaHtml.getHtmlContext(), pdf);
            CssResolverPipeline css = new CssResolverPipeline(DocumentoPDFPlantillaHtml.getCss(), html);

            XMLWorker worker = new XMLWorker(css, true);
            XMLParser p = new XMLParser(worker);
            p.parse(new StringReader(htmlPage));

            // Adding new lines before the signature
            for (int i = 0; i < 5; i++) {
                document.add(Chunk.NEWLINE);
            }

            Font font = FontFactory.getFont("Times-Roman", 10);
            document.add(new Paragraph(personaFirmaResponsable.getNombre(), font));
            document.add(new Paragraph(rol, font));

            // close the document
            document.close();
            pdfWriter.close();

            if (marcaAgua == 1) {
                if (Constantes.getDocumentosBorrador() || marcaAgua == 1) {
                    return poneSelloAguaTotal(tmpFile);
                } else {
                    return tmpFile;
                }
            } else {
                return tmpFile;
            }

        } catch (FileNotFoundException e) {
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

    public static File crearAnexoCoordenadasLicenciaAmbientalCategoriaII(
            String pdfFilename, String[] parametros, String keyPlantilla,
            String titulo, Area area, Integer marcaAgua) {

        File tmpFile = null;
        try {
            tmpFile = File.createTempFile(pdfFilename, ".pdf");
            FileOutputStream out = new FileOutputStream(tmpFile);
            // create a new document
            Document document = new Document(PageSize.A4, 36, 36, 90, 54);
            PdfWriter pdfWriter = PdfWriter.getInstance(document, out);
            pdfWriter.setBoxSize("art", new Rectangle(36, 54, 559, 788));
            PlantillaHeaderFooterOficio a = new PlantillaHeaderFooterOficio(
                    area);
            pdfWriter.setPageEvent((PdfPageEvent) a);
            // document header attributes
            document = DocumentoPDFPlantillaHtml.agregarInformacionDocumento(
                    document, titulo);
            // open document
            document.open();
            String htmlPage = DocumentoPDFPlantillaHtml
                    .getPlantillaConParametros(keyPlantilla, parametros);

            // Pipelines
            PdfWriterPipeline pdf = new PdfWriterPipeline(document, pdfWriter);
            HtmlPipeline html = new HtmlPipeline(
                    DocumentoPDFPlantillaHtml.getHtmlContext(), pdf);

            CssResolverPipeline css = new CssResolverPipeline(
                    DocumentoPDFPlantillaHtml.getCss(), html);

            XMLWorker worker = new XMLWorker(css, true);
            XMLParser p = new XMLParser(worker);
            p.parse(new StringReader(htmlPage));

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

        } catch (FileNotFoundException e) {
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