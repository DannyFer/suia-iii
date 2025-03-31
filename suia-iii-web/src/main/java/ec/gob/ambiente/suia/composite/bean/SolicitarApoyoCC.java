/*
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.composite.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: 23/12/2014]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class SolicitarApoyoCC implements Serializable{
    
    private static final long serialVersionUID = -3526371287117778486L;

	@Getter
	@Setter
	private String listaInicial;

	@Getter
	@Setter
	private Boolean requiereApoyo;

	@Getter
	@Setter
	private List<String> areasApoyo;

	@PostConstruct
	public void init() {
		requiereApoyo = false;
		listaInicial = "";
		areasApoyo = new ArrayList<String>();
		areasApoyo.add("Area 1");
		areasApoyo.add("Area 2");
		areasApoyo.add("Area 3");
		areasApoyo.add("Area 4");
	}

	public void initValue(String areas, Boolean requiereA) {

		if (requiereA != null) {
			this.requiereApoyo = requiereA;// requiereA.equals("si");
		} else {
			this.requiereApoyo = false;
		}
		listaInicial = areas;
		if (listaInicial != null && !listaInicial.isEmpty()) {
			areasApoyo = Arrays.asList(areas.split("\\s*,\\s*"));
		}
	}

	public void actualizarValor(Boolean valor) {
		this.requiereApoyo = valor;// valor.equals("si");
	}

}
