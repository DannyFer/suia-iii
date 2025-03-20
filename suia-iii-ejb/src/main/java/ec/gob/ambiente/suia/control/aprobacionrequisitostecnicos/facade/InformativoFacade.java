/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;

@Stateless
public class InformativoFacade {

	public static final String GUIA_REMISION = "guiaRemision.png";
	public static final String BITACORA_HORAS_VIAJE = "bitacoraHorasViajeConductor.png";
	public static final String REGISTRO_ACCIDENTES = "registroAccidentes.png";
	public static final String MODELO_TARJETA_EMERGENCIA = "modeloTargetaEmergencia.png";
	public static final String MODELO_HOJA_SEGURIDAD = "modeloHojaSeguidadMaterialesPeligrosos.png";

	@EJB
	private DocumentosFacade documentosFacade;

	public byte[] getDocumentoInformativo(String nombre) throws ServiceException, CmisAlfrescoException {

		try {
			return documentosFacade.descargarDocumentoPorNombre(nombre);
		} catch (RuntimeException e) {
			throw new ServiceException("Error al recuperar el documento " + nombre, e);
		}
	}

}
