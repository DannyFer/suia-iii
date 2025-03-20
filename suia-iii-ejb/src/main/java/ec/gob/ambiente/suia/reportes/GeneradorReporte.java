package ec.gob.ambiente.suia.reportes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.fill.JRTemplatePrintText;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.engine.JasperExportManager;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.log4j.Logger;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.DocumentosTareasProceso;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.UtilAlfresco;

public class GeneradorReporte {

	private static GeneradorReporte instance;

	private GeneradorReporte() {
	}

	public static GeneradorReporte getInstance() {
		return (instance != null) ? instance : (instance = new GeneradorReporte());
	}

	private static final Logger LOG = Logger.getLogger(GeneradorReporte.class);

	public void generarReportePDF(HttpServletResponse response, Map<String, Object> parametros, String nombreReporte,
			List<String> reportes, List<String> reportesUrl, List<String> reportesImages) throws JRException {

		Locale locale = new Locale("es", "ES");
		parametros.put(JRParameter.REPORT_LOCALE, locale);
		if (reportesUrl != null && reportes != null) {
			for (int i = 0; i < reportes.size(); i++) {
				try {
					parametros.put(reportes.get(i), GeneradorReporte.getInstance().crearReporte(reportesUrl.get(i)));
				} catch (JRException ex) {
					ex.printStackTrace();
				}
			}
		}

		if (reportesImages != null) {
			for (int i = 0; i < reportesImages.size(); i++) {
				try {
					String imagen = reportesImages.get(i);
					parametros.put("urlImagen" + "-" + i,
							this.getClass().getResourceAsStream("/ec/gob/ambiente/reportes/images/" + imagen));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}

		JasperReport reporte = crearReporte(nombreReporte);

		int totalElementosHeaderFooter = -1;
		try {
			totalElementosHeaderFooter = reporte.getPageHeader().getElements().length
					+ reporte.getPageFooter().getElements().length;

		} catch (Exception exception) {
			LOG.error("Error al completar la operacion", exception);
		}

		JasperPrint jasperPrint = JasperFillManager.fillReport(reporte, parametros, new JREmptyDataSource());

		if (totalElementosHeaderFooter != -1)
			removeBlankPage(jasperPrint, totalElementosHeaderFooter);

		generarPDF(response, jasperPrint, nombreReporte);
	}

	private void removeBlankPage(JasperPrint jp, int totalElementosHeaderFooter) {
		List<JRPrintPage> pages = jp.getPages();
		for (Iterator<JRPrintPage> i = pages.iterator(); i.hasNext();) {
			JRPrintPage page = i.next();
			if (page.getElements().size() <= totalElementosHeaderFooter) {
				i.remove();
			}
		}

		for (Iterator<JRPrintPage> i = pages.iterator(); i.hasNext();) {
			JRPrintPage page = i.next();
			for (JRPrintElement jr : page.getElements()) {
				if (jr.getKey() != null && jr.getKey().equals("pages"))
					((JRTemplatePrintText) jr).setText("" + pages.size());
			}
		}
	}

	private void generarPDF(HttpServletResponse response, JasperPrint jp, String nombreReporte) {
		ByteArrayOutputStream reporteGenerado = new ByteArrayOutputStream();
		response.setHeader("Content-disposition", "attachment;filename=" + nombreReporte + ".pdf");

		// removeBlankPage(jp);

		JRPdfExporter exporter = new JRPdfExporter();
		SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
		exporter.setConfiguration(configuration);
		exporter.setExporterInput(new SimpleExporterInput(jp));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(reporteGenerado));

		try {
			exporter.exportReport();
		} catch (JRException exception) {
			LOG.error("Error al completar la operacion", exception);
		}
		byte[] arregloBytes = reporteGenerado.toByteArray();
		response.setContentLength(arregloBytes.length);
		try {
			reporteGenerado.close();
			response.setContentType("application/pdf");
			ServletOutputStream flujoSalida = response.getOutputStream();
			flujoSalida.write(arregloBytes, 0, arregloBytes.length);
			flujoSalida.flush();
			flujoSalida.close();
		} catch (IOException exception) {
			LOG.error("Error al completar la operacion", exception);
		}
	}

	public Documento generarSalvarReportePDF(Map<String, Object> parametros, String nombreReporte,
			List<String> reportes, List<String> reportesUrl, List<String> reportesImages, long idInstanciaProceso,
			String codigoProyecto, long idTarea, String nombreProcesoDirectorioGuardar,
			String nombreProcesoConcatenarNombreFichero, String nombreFichero, String mime, String extension,
			Integer idTabla, String nombreTabla, TipoDocumentoSistema tipoDocumento) throws JRException {
		Locale locale = new Locale("es", "ES");
		parametros.put(JRParameter.REPORT_LOCALE, locale);

		if (reportesUrl != null && reportes != null) {
			for (int i = 0; i < reportes.size(); i++) {
				try {
					parametros.put(reportes.get(i), GeneradorReporte.getInstance().crearReporte(reportesUrl.get(i)));
				} catch (JRException ex) {
					ex.printStackTrace();
				}
			}
		}

		if (reportesImages != null) {
			for (int i = 0; i < reportesImages.size(); i++) {
				try {
					String imagen = reportesImages.get(i);
					if(imagen.equals("fondo-documentos.png"))
						parametros.put("urlImagen-4",this.getClass().getResourceAsStream("/ec/gob/ambiente/reportes/images/" + imagen));
					else
						parametros.put("urlImagen" + "-" + i, this.getClass().getResourceAsStream("/ec/gob/ambiente/reportes/images/" + imagen));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}

		JasperReport reporte = crearReporte(nombreReporte);

		JasperPrint jasperPrint = JasperFillManager.fillReport(reporte, parametros, new JREmptyDataSource());

		return generarSalvarPDF(jasperPrint, nombreReporte, idInstanciaProceso, codigoProyecto, idTarea,
				nombreProcesoDirectorioGuardar, nombreProcesoConcatenarNombreFichero, nombreFichero, mime, extension,
				idTabla, nombreTabla, tipoDocumento, parametros);
	}

	private Documento generarSalvarPDF(JasperPrint jp, String nombreReporte, Long idInstanciaProceso,
			String codigoProyecto, long idTarea, String nombreProcesoDirectorioGuardar,
			String nombreProcesoConcatenarNombreFichero, String nombreFichero, String mime, String extension,
			Integer idTabla, String nombreTabla, TipoDocumentoSistema tipoDocumento, Map<String, Object> parametros) {
		ByteArrayOutputStream reporteGenerado = new ByteArrayOutputStream();
		JRPdfExporter exporter = new JRPdfExporter();
		SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
		exporter.setConfiguration(configuration);
		exporter.setExporterInput(new SimpleExporterInput(jp));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(reporteGenerado));

		try {
			exporter.exportReport();
		} catch (JRException exception) {
			LOG.error("Error al completar la operacion", exception);
		}
		byte[] arregloBytes = reporteGenerado.toByteArray();

		// Guardar en alfresco y base de datos
		AlfrescoServiceBean alfrescoServiceBean = (AlfrescoServiceBean) BeanLocator
				.getInstance(AlfrescoServiceBean.class);
		CrudServiceBean crudServiceBean = (CrudServiceBean) BeanLocator.getInstance(CrudServiceBean.class);

		try {
			String folderName = UtilAlfresco.generarEstructuraCarpetas(codigoProyecto, nombreProcesoDirectorioGuardar,
					Long.toString(idInstanciaProceso));

			String folderId = alfrescoServiceBean.createFolderStructure(folderName, Constantes.getRootId());

			Document documentCreate = alfrescoServiceBean.fileSaveStream(arregloBytes, nombreFichero, folderId,
					nombreProcesoConcatenarNombreFichero, folderId, idInstanciaProceso.intValue());

			Documento documentoAux = UtilAlfresco.crearDocumento(documentCreate.getName(), extension,
					documentCreate.getId(), idTabla, mime, nombreFichero, nombreTabla, tipoDocumento);

			String codigoPublico = (String) parametros.get("numeroResolucionCertificado");
			if (codigoPublico != null && !codigoPublico.isEmpty()) {
				documentoAux.setCodigoPublico(codigoPublico);
			}

			Documento documento = (Documento) crudServiceBean.saveOrUpdate(documentoAux);

			DocumentosTareasProceso documentosTareasProceso = new DocumentosTareasProceso();
			documentosTareasProceso.setDocumento(documento);
			documentosTareasProceso.setIdTarea(idTarea);
			documentosTareasProceso.setProcessInstanceId(idInstanciaProceso);
			crudServiceBean.saveOrUpdate(documentosTareasProceso);

			return documento;

		} catch (Exception exception) {
			LOG.error("Error al completar la operacion", exception);
		}
		return null;
	}

	public JasperReport crearReporte(String nombreReporte) throws JRException {
		JasperReport reporte = JasperCompileManager.compileReport(this.getClass().getResourceAsStream(
				"/ec/gob/ambiente/reportes/" + nombreReporte + ".jrxml"));
		return reporte;
	}
	
	private String temporal = System.getProperty("java.io.tmpdir")+File.separator;
	
	public void generarSalvarReportePDFCertificadoAmbiental(Map<String, Object> parametros, String nombreReporte,
			List<String> reportes, List<String> reportesUrl, List<String> reportesImages, long idInstanciaProceso,
			String codigoProyecto, long idTarea, String nombreProcesoDirectorioGuardar,
			String nombreProcesoConcatenarNombreFichero, String nombreFichero, String mime, String extension,
			Integer idTabla, String nombreTabla, TipoDocumentoSistema tipoDocumento) throws JRException {
		Locale locale = new Locale("es", "ES");
		parametros.put(JRParameter.REPORT_LOCALE, locale);

		if (reportesUrl != null && reportes != null) {
			for (int i = 0; i < reportes.size(); i++) {
				try {
					parametros.put(reportes.get(i), GeneradorReporte.getInstance().crearReporte(reportesUrl.get(i)));
				} catch (JRException ex) {
					ex.printStackTrace();
				}
			}
		}

		if (reportesImages != null) {
			for (int i = 0; i < reportesImages.size(); i++) {
				try {
					String imagen = reportesImages.get(i);
					parametros.put("urlImagen" + "-" + i,
							this.getClass().getResourceAsStream("/ec/gob/ambiente/reportes/images/" + imagen));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}

		JasperReport reporte = crearReporte(nombreReporte);

		JasperPrint jasperPrint = JasperFillManager.fillReport(reporte, parametros, new JREmptyDataSource());
		
		JasperExportManager.exportReportToPdfFile(jasperPrint, temporal+"categoría uno-certificado"+codigoProyecto+".pdf");		
	}
	public Documento guaradarSalvarPDFCA(File file,Long idInstanciaProceso,
			String codigoProyecto, long idTarea, String nombreProcesoDirectorioGuardar,
			String nombreProcesoConcatenarNombreFichero, String nombreFichero, String mime, String extension,
			Integer idTabla, String nombreTabla, TipoDocumentoSistema tipoDocumento) {
		

		byte[] arregloBytes = null;
		try {
			arregloBytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Guardar en alfresco y base de datos
		AlfrescoServiceBean alfrescoServiceBean = (AlfrescoServiceBean) BeanLocator
				.getInstance(AlfrescoServiceBean.class);
		CrudServiceBean crudServiceBean = (CrudServiceBean) BeanLocator.getInstance(CrudServiceBean.class);

		try {
			String folderName = UtilAlfresco.generarEstructuraCarpetas(codigoProyecto, nombreProcesoDirectorioGuardar,
					Long.toString(idInstanciaProceso));

			String folderId = alfrescoServiceBean.createFolderStructure(folderName, Constantes.getRootId());

			Document documentCreate = alfrescoServiceBean.fileSaveStream(arregloBytes, nombreFichero, folderId,
					nombreProcesoConcatenarNombreFichero, folderId, idInstanciaProceso.intValue());

			Documento documentoAux = UtilAlfresco.crearDocumento(documentCreate.getName(), extension,
					documentCreate.getId(), idTabla, mime, nombreFichero, nombreTabla, tipoDocumento);

//			String codigoPublico = (String) parametros.get("numeroResolucionCertificado");
//			if (codigoPublico != null && !codigoPublico.isEmpty()) {
//				documentoAux.setCodigoPublico(codigoPublico);
//			}

			Documento documento = (Documento) crudServiceBean.saveOrUpdate(documentoAux);

			DocumentosTareasProceso documentosTareasProceso = new DocumentosTareasProceso();
			documentosTareasProceso.setDocumento(documento);
			documentosTareasProceso.setIdTarea(idTarea);
			documentosTareasProceso.setProcessInstanceId(idInstanciaProceso);
			crudServiceBean.saveOrUpdate(documentosTareasProceso);

			return documento;

		} catch (Exception exception) {
			LOG.error("Error al completar la operacion", exception);
		}
		return null;
	}
}