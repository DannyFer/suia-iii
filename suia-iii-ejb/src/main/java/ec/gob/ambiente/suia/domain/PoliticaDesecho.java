/*
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * <b> Entity Bean. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: Jun 08, 2015]
 *          </p>
 */
@Entity
@Table(name = "waste_policies", catalog = "", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "wapo_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wapo_status = 'TRUE'")
public class PoliticaDesecho extends EntidadBase {

	private static final long serialVersionUID = -2001050202099820088L;

	public static final int POLITICA_NEUMATICOS = 1;
	public static final int POLITICA_PILAS = 2;
	public static final int POLITICA_PLASTICOS = 3;
	public static final int POLITICA_CELULARES = 4;

	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "WASTE_POLICIES_WAPO_ID_GENERATOR", sequenceName = "seq_wapo_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WASTE_POLICIES_WAPO_ID_GENERATOR")
	@Column(name = "wapo_id", unique = true, nullable = false)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "wapo_name")
	private String nombre;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "area_id")
	@ForeignKey(name = "fk_wapo_id_area_id")
	private Area areaResponsable;

	@Override
	public String toString() {
		return nombre;
	}
}