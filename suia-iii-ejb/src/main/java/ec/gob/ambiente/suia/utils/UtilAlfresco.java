package ec.gob.ambiente.suia.utils;

import ec.gob.ambiente.rcoa.demo.model.DemoDocumentos;
import ec.gob.ambiente.rcoa.model.DocumentoCertificadoAmbiental;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.participacionCiudadana.model.DocumentosPPC;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.DocumentosRgdRcoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.DocumentosSustanciasQuimicasRcoa;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.DocumentosTareasProceso;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.exceptions.ServiceException;

public class UtilAlfresco {

	public static Documento crearDocumento(String descripcion, String extesion, String idAlfresco, Integer idTable,
			String mime, String nombre, String nombreTabla, ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema tipoDocumentoSistema) {
		TipoDocumento tipoDocumento = new TipoDocumento();
		tipoDocumento.setId(tipoDocumentoSistema.getIdTipoDocumento());
		Documento documento = new Documento();
		documento.setDescripcion(descripcion);
		documento.setEstado(true);
		documento.setExtesion(extesion);
		documento.setIdAlfresco(idAlfresco);
		documento.setIdTable(idTable);
		documento.setMime(mime);
		documento.setNombre(nombre);
		documento.setNombreTabla(nombreTabla);
		documento.setTipoDocumento(tipoDocumento);

		return documento;
	}
	
	
	public static DocumentosRgdRcoa crearDocumentoRGD(String descripcion, String extesion, String idAlfresco, Integer idTable,
			String mime, String nombre, String nombreTabla, ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema tipoDocumentoSistema) {
		TipoDocumento tipoDocumento = new TipoDocumento();
		tipoDocumento.setId(tipoDocumentoSistema.getIdTipoDocumento());
		DocumentosRgdRcoa documento = new DocumentosRgdRcoa();
		documento.setDescripcion(descripcion);
		documento.setEstado(true);
		documento.setExtesion(extesion);
		documento.setIdAlfresco(idAlfresco);
		documento.setIdTable(idTable);
		documento.setMime(mime);
		documento.setNombre(nombre);
		documento.setNombreTabla(nombreTabla);
		documento.setTipoDocumento(tipoDocumento);

		return documento;
	}
	
	public static DocumentosSustanciasQuimicasRcoa crearDocumentoSustanciasQuimicas(String descripcion, String extesion, String idAlfresco, Integer idTable,
			String mime, String nombre, String nombreTabla, ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema tipoDocumentoSistema) {
		TipoDocumento tipoDocumento = new TipoDocumento();
		tipoDocumento.setId(tipoDocumentoSistema.getIdTipoDocumento());
		DocumentosSustanciasQuimicasRcoa documento = new DocumentosSustanciasQuimicasRcoa();
		documento.setDescripcion(descripcion);
		documento.setEstado(true);
		documento.setExtesion(extesion);
		documento.setIdAlfresco(idAlfresco);
		documento.setIdTable(idTable);
		documento.setMime(mime);
		documento.setNombre(nombre);
		documento.setNombreTabla(nombreTabla);
		documento.setTipoDocumento(tipoDocumento);

		return documento;
	}
	
	public static DocumentoCertificadoAmbiental crearDocumentoCA(String descripcion, String extesion, String idAlfresco, Integer idTable,
			String mime, String nombre, String nombreTabla, ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema tipoDocumentoSistema) {
		TipoDocumento tipoDocumento = new TipoDocumento();
		tipoDocumento.setId(tipoDocumentoSistema.getIdTipoDocumento());

		DocumentoCertificadoAmbiental documento = new DocumentoCertificadoAmbiental();
		documento.setDescripcion(descripcion);
		documento.setEstado(true);
		documento.setExtesion(extesion);
		documento.setAlfrescoId(idAlfresco);
		documento.setIdTable(idTable);
		documento.setMime(mime);
		documento.setNombre(nombre);
		documento.setNombreTabla(nombreTabla);
		documento.setTipoDocumento(tipoDocumento);

		return documento;
	}
	
	public static DocumentosTareasProceso crearDocumentoTareaProceso(Documento documento, Integer idTarea,
			Long processInstanceId) {

		DocumentosTareasProceso tareasProceso = new DocumentosTareasProceso();
		tareasProceso.setIdTarea(idTarea);
		tareasProceso.setProcessInstanceId(processInstanceId);
		tareasProceso.setDocumento(documento);

		return tareasProceso;
	}

	public static String generarEstructuraCarpetas(String codigoProyecto, String nombreProceso, String idProceso)
			throws ServiceException {
		if (codigoProyecto != null && nombreProceso != null) {
			return codigoProyecto + "/" + nombreProceso.replace(" ", "").toUpperCase();
		} else {
			throw new ServiceException("No se puede generar la estructura de carpetas para el alfresco");
		}

	}
	
	public static DocumentosCOA crearDocumentoCOA(String descripcion, String extesion, String idAlfresco, Integer idTable,
			String mime, String nombre, String nombreTabla, ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema tipoDocumentoSistema) {
		TipoDocumento tipoDocumento = new TipoDocumento();
		tipoDocumento.setId(tipoDocumentoSistema.getIdTipoDocumento());

		DocumentosCOA documento = new DocumentosCOA();
		
		documento.setEstado(true);
		documento.setExtencionDocumento(extesion);
		documento.setIdAlfresco(idAlfresco);
		documento.setIdTabla(idTable);
		documento.setNombreDocumento(nombre);
		documento.setNombreTabla(nombreTabla);
		documento.setTipoDocumento(tipoDocumento);
		documento.setTipo(mime);

		return documento;
	}
	
	public static DocumentosPPC crearDocumentoPPC(String descripcion, String extesion, String idAlfresco, Integer idTable,
			String mime, String nombre, String nombreTabla, ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema tipoDocumentoSistema) {
		TipoDocumento tipoDocumento = new TipoDocumento();
		tipoDocumento.setId(tipoDocumentoSistema.getIdTipoDocumento());

		DocumentosPPC documento = new DocumentosPPC();
		
		documento.setEstado(true);
		documento.setExtencionDocumento(extesion);
		documento.setIdAlfresco(idAlfresco);
		documento.setIdTabla(idTable);
		documento.setNombreDocumento(nombre);
		documento.setNombreTabla(nombreTabla);
		documento.setTipoDocumento(tipoDocumento);
		documento.setTipo(mime);

		return documento;
	}
	
	public static DemoDocumentos crearDemoDocumento(String descripcion, String extesion, String idAlfresco, Integer idTable,
			String mime, String nombre, String nombreTabla, ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema tipoDocumentoSistema) {
		TipoDocumento tipoDocumento = new TipoDocumento();
		tipoDocumento.setId(tipoDocumentoSistema.getIdTipoDocumento());

		DemoDocumentos documento = new DemoDocumentos();
		
		documento.setEstado(true);
		documento.setExtencionDocumento(extesion);
		documento.setIdAlfresco(idAlfresco);
		documento.setIdTabla(idTable);
		documento.setNombreDocumento(nombre);
		documento.setNombreTabla(nombreTabla);
		documento.setTipoDocumento(tipoDocumento);
		documento.setTipo(mime);

		return documento;
	}

}
