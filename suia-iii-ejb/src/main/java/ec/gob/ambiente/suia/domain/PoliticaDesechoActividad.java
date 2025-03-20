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
@Table(name = "waste_policies_activities", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "waac_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "waac_status = 'TRUE'")
public class PoliticaDesechoActividad extends EntidadBase {

	private static final long serialVersionUID = -846392735976470554L;

	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "WASTE_POLICIES_WAAC_ID_GENERATOR", sequenceName = "seq_waac_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WASTE_POLICIES_WAAC_ID_GENERATOR")
	@Column(name = "waac_id", unique = true, nullable = false)
	private Integer id;
	  
	@Getter
	@Setter
	@Column(name = "waac_description")
	private String descripcion;
	
	@Getter
	@Setter
	@Column(name = "waac_codigo")
	private String codigo;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "wada_id")
	@ForeignKey(name = "fk_waac_id_wada_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wada_status = 'TRUE'")
	private DesechoPeligroso desechoPeligroso;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "wapo_id")
	@ForeignKey(name = "fk_waac_id_wapo_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wapo_status = 'TRUE'")
	private PoliticaDesecho politicaDesecho;
	
	@Override
	public String toString() {
		return descripcion;
	}
}