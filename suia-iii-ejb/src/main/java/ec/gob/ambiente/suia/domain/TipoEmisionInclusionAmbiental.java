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
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
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
 *          [Autor: mayriliscs, Fecha: 30/01/2015]
 *          </p>
 */
@Entity
@Table(name = "environmental_inclusion_emission_types", catalog = "", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "enin_status")) })
@Inheritance(strategy = InheritanceType.JOINED)
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "enin_status = 'TRUE'")
public class TipoEmisionInclusionAmbiental extends EntidadBase {

	private static final long serialVersionUID = -9088067135846878247L;

	@Id
	@SequenceGenerator(name = "ENVIRONMENTAL_INCLUSION_EMISSION_TYPES_GENERATOR", schema = "suia_iii", sequenceName = "seq_enin_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ENVIRONMENTAL_INCLUSION_EMISSION_TYPES_GENERATOR")
	@Column(name = "enin_id")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@Column(name = "enin_name")
	private String nombre;

	@Override
	public String toString() {
		return this.nombre;
	}
}
