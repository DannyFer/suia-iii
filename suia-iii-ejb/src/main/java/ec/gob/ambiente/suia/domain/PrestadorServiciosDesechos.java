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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * <b> Entity Bean. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: Jun 10, 2015]
 *          </p>
 */
@Entity
@Table(name = "hazardous_wastes_service_providers", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "hwsp_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "hwsp_status = 'TRUE'")
public class PrestadorServiciosDesechos extends EntidadBase {

	private static final long serialVersionUID = 658850472531988259L;

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "HAZARDOUS_WASTES_SERVICE_PROVIDER_HWSP_ID_GENERATOR", sequenceName = "seq_hwsp_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "HAZARDOUS_WASTES_SERVICE_PROVIDER_HWSP_ID_GENERATOR")
	@Column(name = "hwsp_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "hwsp_name")
	private String nombre;

	@Getter
	@Setter
	@Column(name = "hwsp_ruc")
	private String ruc;

	@Getter
	@Setter
	@Column(name = "hwsp_web")
	private String web;

	@Override
	public String toString() {
		return this.nombre;
	}
}
