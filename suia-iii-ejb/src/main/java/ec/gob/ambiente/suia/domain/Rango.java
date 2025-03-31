/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 25/03/2015]
 *          </p>
 */
@Entity
@Table(name = "ranks", catalog = "", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "rank_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "rank_status = 'TRUE'")
public class Rango extends EntidadBase {

	private static final long serialVersionUID = 7079974146192171746L;

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "CATEGORIES_CATALOG_RANKID_GENERATOR", sequenceName = "seq_rank_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CATEGORIES_CATALOG_RANKID_GENERATOR")
	@Column(name = "rank_id", unique = true, nullable = false)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "rank_start", nullable = true)
	private Float inicioRango;

	@Getter
	@Setter
	@Column(name = "rank_end", nullable = true)
	private Float finRango;

	@Getter
	@Setter
	@Column(name = "rank_measurement_unit")
	private String unidadMedida;

	@Override
	public String toString() {
		if(inicioRango == null)
			return  "Hasta " + finRango;
		if(finRango == null)
			return  inicioRango + " o mayor";
		return inicioRango + " - " + finRango;
	}
}
