/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.composite.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.suia.domain.Pronunciamiento;
import java.io.Serializable;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 12/01/2015]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class EmitirPronunciamientoCC implements Serializable{
    
    private static final long serialVersionUID = -3526371287965589686L;

	private Pronunciamiento pronunciamiento;

	@Getter
	@Setter
	private String fechaMostrar;

	List<UploadedFile> ficherosSubidos;

	@Getter
	@Setter
	private Date fecha;

	public Pronunciamiento getPronunciamiento() {
		return pronunciamiento == null ? pronunciamiento = new Pronunciamiento() : pronunciamiento;
	}

	public void setPronunciamiento(Pronunciamiento pronunciamiento) {
		this.pronunciamiento = pronunciamiento;
	}

	public List<UploadedFile> getFicherosSubidos() {
		return ficherosSubidos == null ? ficherosSubidos = new ArrayList<UploadedFile>() : ficherosSubidos;
	}

	public void setFicherosSubidos(List<UploadedFile> ficherosSubidos) {
		this.ficherosSubidos = ficherosSubidos;
	}

	@PostConstruct
	public void initFunction() {
		this.fecha = new Date();
	}

}
