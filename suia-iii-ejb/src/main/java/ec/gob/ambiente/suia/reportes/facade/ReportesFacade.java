/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.reportes.facade;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;

import org.apache.log4j.Logger;

import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.reportes.GeneradorReporte;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 28/01/2015]
 *          </p>
 */
@Stateless
public class ReportesFacade {

	private static final Logger LOG = Logger.getLogger(ReportesFacade.class);

	public Documento generarReporteGuardarAlfresco(Map<String, Object> parametros, String nombreReporte,
			List<String> reportes, List<String> reportesUrl, List<String> reportesImages, long idInstanciaProceso,
			String codigoProyecto, long idTarea, String nombreProcesoDirectorioGuardar,
			String nombreProcesoConcatenarNombreFichero, String nombreFichero, String mime, String extension,
			Integer idTabla, String nombreTabla, TipoDocumentoSistema tipoDocumento) {
		try {
			return GeneradorReporte.getInstance().generarSalvarReportePDF(parametros, nombreReporte, reportes,
					reportesUrl, reportesImages, idInstanciaProceso, codigoProyecto, idTarea,
					nombreProcesoDirectorioGuardar, nombreProcesoConcatenarNombreFichero, nombreFichero, mime,
					extension, idTabla, nombreTabla, tipoDocumento);
		} catch (JRException exception) {
			LOG.error("Ocurrio un error al generar el reporte", exception);
		}
		return null;
	}
	public void generarReporteCertificadoAmbiental(Map<String, Object> parametros, String nombreReporte,
			List<String> reportes, List<String> reportesUrl, List<String> reportesImages, long idInstanciaProceso,
			String codigoProyecto, long idTarea, String nombreProcesoDirectorioGuardar,
			String nombreProcesoConcatenarNombreFichero, String nombreFichero, String mime, String extension,
			Integer idTabla, String nombreTabla, TipoDocumentoSistema tipoDocumento) {
		try {
			GeneradorReporte.getInstance().generarSalvarReportePDFCertificadoAmbiental(parametros, nombreReporte, reportes,
					reportesUrl, reportesImages, idInstanciaProceso, codigoProyecto, idTarea,
					nombreProcesoDirectorioGuardar, nombreProcesoConcatenarNombreFichero, nombreFichero, mime,
					extension, idTabla, nombreTabla, tipoDocumento);
		} catch (JRException exception) {
			LOG.error("Ocurrio un error al generar el reporte", exception);
		}
		
	}
	
	
	public Documento guardarReporteCertificadoAmbiental(File documento, long idInstanciaProceso,
			String codigoProyecto, long idTarea, String nombreProcesoDirectorioGuardar,
			String nombreProcesoConcatenarNombreFichero, String nombreFichero, String mime, String extension,
			Integer idTabla, String nombreTabla, TipoDocumentoSistema tipoDocumento) {
		return GeneradorReporte.getInstance().guaradarSalvarPDFCA(documento,idInstanciaProceso, codigoProyecto, idTarea, nombreProcesoDirectorioGuardar, nombreProcesoConcatenarNombreFichero, nombreFichero, mime, extension, idTabla, nombreTabla, tipoDocumento);		
	}

	public void generarReportePDF(HttpServletResponse response, Map<String, Object> parametros, String nombreReporte,
			List<String> reportes, List<String> reportesUrl, List<String> reportesImages) {
		try {
			GeneradorReporte.getInstance().generarReportePDF(response, parametros, nombreReporte, reportes,
					reportesUrl, reportesImages);
		} catch (JRException exception) {
			LOG.error("Ocurrio un error al generar el reporte", exception);
		}
	}
}
