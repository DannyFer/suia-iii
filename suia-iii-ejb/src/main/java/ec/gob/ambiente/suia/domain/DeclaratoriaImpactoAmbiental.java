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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * <b> Entity Bean. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: Dec 20, 2014]
 *          </p>
 */
@Entity
@Table(name = "environmental_impact_declaration", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "enim_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "enim_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "enim_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "enim_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "enim_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "enim_status = 'TRUE'")
public class DeclaratoriaImpactoAmbiental extends EntidadAuditable {

	private static final long serialVersionUID = 4769858916517281866L;

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "ENVIROMENTAL_IMPACT_DECLARATION_GENERATOR", schema = "suia_iii", sequenceName = "SEQ_ENIM_ID")
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "enim_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "enim_name")
	private String nombre;

	@OneToOne
	@JoinColumn(name = "pren_id")
	@ForeignKey(name="fk_environmental_impactenim_id_projects_environmentalpren_id")
	@Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyecto;
}
