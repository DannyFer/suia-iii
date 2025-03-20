package ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade;

import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.DocumentosRgdRcoa;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.exceptions.ServiceException;

public class UtilAlfrescoRgd {
	
	public static DocumentosRgdRcoa crearDocumento(String extesion, String idAlfresco, 
			String mime, String nombre, ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema tipoDocumentoSistema) {
		TipoDocumento tipoDocumento = new TipoDocumento();
		tipoDocumento.setId(tipoDocumentoSistema.getIdTipoDocumento());

		DocumentosRgdRcoa documento = new DocumentosRgdRcoa();
		
		documento.setEstado(true);
		documento.setExtesion(extesion);
		documento.setIdAlfresco(idAlfresco);		
		documento.setMime(mime);
		documento.setNombre(nombre);		
		documento.setTipoDocumento(tipoDocumento);

		return documento;
	}

	public static String generarEstructuraCarpetas(String codigoProyecto, String nombreProceso, String idProceso)
			throws ServiceException {
		if (codigoProyecto != null && nombreProceso != null) {
			return codigoProyecto + "/" + nombreProceso.replace(" ", "").toUpperCase();
		} else {
			throw new ServiceException("No se puede generar la estructura de carpetas para el alfresco");
		}

	}


}
