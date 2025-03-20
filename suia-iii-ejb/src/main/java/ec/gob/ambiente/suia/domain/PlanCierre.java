/*
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.domain;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
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
@Table(name = "clousure_plans", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "clpl_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "clpl_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "clpl_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "clpl_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "clpl_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "clpl_status = 'TRUE'")
public class PlanCierre extends EntidadAuditable {

	private static final long serialVersionUID = -5973182034607936030L;

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "CLOUSURE_PLANS_CLPC_ID_GENERATOR", sequenceName = "seq_pren_id", schema = "suia_iii")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CLOUSURE_PLANS_CLPC_ID_GENERATOR")
	@Column(name = "clpl_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "clpl_name")
	private String nombre;

	@Getter
	@Setter
	@Column(name = "clpl_temporary_abandonment")
	private boolean abandonoTemporal;

	@Getter
	@Setter
	@Column(name = "clpl_inspection_required")
	private boolean requiereInspeccion;

	@OneToOne
	@JoinColumn(name = "pren_id")
	@ForeignKey(name = "fk_clousure_plansclpl_id_projects_environmental_licensingpren_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "pren_status = 'TRUE'")
	@Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyecto;

	@OneToMany(mappedBy = "planCierre")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "clpd_status = 'TRUE'")
	@Getter
	@Setter
	private List<PlanCierreDocumentos> planesCierreDocumentos;
}
