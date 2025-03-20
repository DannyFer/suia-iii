/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.proyectolicenciamientoambiental.classes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.Coordenada;
import ec.gob.ambiente.suia.domain.TipoForma;

/**
 * <b> Clase para manipular coordenadas y tipo de forma representadas. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: 20/02/2015]
 *          </p>
 */
public class CoordinatesWrapper {

	@Setter
	private List<Coordenada> coordenadas;

	@Getter
	@Setter
	private TipoForma tipoForma;
	
	@Getter
	@Setter
	private BigDecimal superficie;
	
	@Getter
	@Setter
	private String cadenaCoordenadas;

	public CoordinatesWrapper(List<Coordenada> coordenadas, TipoForma tipoForma) {
		super();
		this.coordenadas = coordenadas;
		this.tipoForma = tipoForma;
	}

	public CoordinatesWrapper() {
	}

	public List<Coordenada> getCoordenadas() {
		return coordenadas == null ? coordenadas = new ArrayList<Coordenada>() : coordenadas;
	}
}
