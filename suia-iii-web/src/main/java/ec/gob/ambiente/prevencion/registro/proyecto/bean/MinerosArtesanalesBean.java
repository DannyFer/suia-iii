/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.prevencion.registro.proyecto.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.event.FileUploadEvent;

import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.MineroArtesanal;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 01/04/2015]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class MinerosArtesanalesBean extends RegistroProyectoBaseBean {

	private static final long serialVersionUID = -5675942752268634225L;

	private MineroArtesanal mineroArtesanal;

	@Getter
	@Setter
	private List<MineroArtesanal> minerosArtesanales;

	@PostConstruct
	public void init() {
		minerosArtesanales = new ArrayList<MineroArtesanal>();
	}

	public void aceptar() {
		if (!minerosArtesanales.contains(mineroArtesanal))
			minerosArtesanales.add(mineroArtesanal);
		clear();
		JsfUtil.addCallbackParam("mineroArtesanal");
	}

	public void validateArchivo(FacesContext context, UIComponent validate, Object value) {
		if (mineroArtesanal.getContratoOperacion() == null)
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Contrato de operación' es requerido.", null));
	}

	public void editar(MineroArtesanal mineroArtesanal) {
		this.mineroArtesanal = mineroArtesanal;
	}

	public void eliminar(MineroArtesanal mineroArtesanal) {
		minerosArtesanales.remove(mineroArtesanal);
	}

	public void clear() {
		mineroArtesanal = null;
	}

	public MineroArtesanal getMineroArtesanal() {
		return mineroArtesanal == null ? mineroArtesanal = new MineroArtesanal() : mineroArtesanal;
	}

	public void uploadListenerContratoOperacion(FileUploadEvent event) {
		Documento documento = super.uploadListener(event);
		mineroArtesanal.setContratoOperacion(documento);
	}

}
