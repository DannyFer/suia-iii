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
 *          [Autor: mayriliscs, Fecha: 18/03/2015]
 *          </p>
 */
@Entity
@Table(name = "affectation_procedures", schema = "suia_iii_administrative")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "afpr_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "afpr_status = 'TRUE'")
public class ProcedimientoAfectacion extends EntidadBase {

	private static final long serialVersionUID = -8953946821585989193L;

	public static final int PROYECTO = 1;
	public static final int PERSONA = 2;

	@Getter
	@Setter
	@Id
	@Column(name = "afpr_id")
	@SequenceGenerator(name = "ADMINISTRATIVE_PROCEDURES_AFPRID_GENERATOR", sequenceName = "seq_afpr_id", schema = "suia_iii_administrative", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ADMINISTRATIVE_PROCEDURES_AFPRID_GENERATOR")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "afpr_name")
	private String nombre;

}
