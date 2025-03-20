/*
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.control.aprobacionpuntosmonitoreo.bean;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 20/12/2014]
 *          </p>
 */
@ViewScoped
@ManagedBean
public class IngresarInformacionAclaratoriaBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1395235191995105360L;

	private UploadedFile fichero;

	private String informacionAclaratoria;

	private static final Logger LOG = Logger.getLogger(IngresarInformacionAclaratoriaBean.class);

	public UploadedFile getFichero() {
		return fichero;
	}

	public void setFichero(UploadedFile fichero) {
		this.fichero = fichero;
	}

	public void uploadListener(FileUploadEvent event) {
		LOG.info("Aqui va el codigo para salvar el fichero");
	}

	public String getInformacionAclaratoria() {
		return informacionAclaratoria;
	}

	public void setInformacionAclaratoria(String informacionAclaratoria) {
		this.informacionAclaratoria = informacionAclaratoria;
	}

}
