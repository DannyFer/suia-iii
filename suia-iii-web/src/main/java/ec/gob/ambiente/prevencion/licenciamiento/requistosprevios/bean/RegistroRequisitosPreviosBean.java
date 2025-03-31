/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.prevencion.licenciamiento.requistosprevios.bean;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import ec.gob.ambiente.suia.domain.RequisitosPreviosLicenciamiento;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: 06/03/2015]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class RegistroRequisitosPreviosBean implements Serializable {

	private static final long serialVersionUID = -7734412605019550467L;

	@Getter
	private RequisitosPreviosLicenciamiento requisitos;

	@PostConstruct
	public void init() {
		requisitos = new RequisitosPreviosLicenciamiento();
	}
}
