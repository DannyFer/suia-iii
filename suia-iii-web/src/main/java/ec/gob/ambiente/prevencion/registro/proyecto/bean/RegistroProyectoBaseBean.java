/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.prevencion.registro.proyecto.bean;

import java.io.Serializable;

import org.primefaces.event.FileUploadEvent;

import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 10/04/2015]
 *          </p>
 */

public class RegistroProyectoBaseBean implements Serializable {

	private static final long serialVersionUID = 4575272703890775424L;

	public Documento uploadListener(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		Documento documento = crearDocumentoPdf(contenidoDocumento);
		documento.setNombre(event.getFile().getFileName());
		return documento;
	}

	public Documento crearDocumentoPdf(byte[] contenidoDocumento) {
		Documento documento = new Documento();
		documento.setContenidoDocumento(contenidoDocumento);
		documento.setNombreTabla(ProyectoLicenciamientoAmbiental.class.getSimpleName());
		documento.setIdTable(0);
		documento.setMime("application/pdf");
		documento.setExtesion(".pdf");
		return documento;
	}
}
