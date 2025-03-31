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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
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
@NamedQueries({ @NamedQuery(name = ProcesoAdministrativo.GET_ENTE_RECEPTOR_PROCESO_ADMINISTRATIVO_POR_TIPO, query = "SELECT p FROM ProcesoAdministrativo p WHERE p.procedimientoAfectacion.id = :idProcedimientoAfectacion") })
@Entity
@Table(name = "administrative_procedures", schema = "suia_iii_administrative")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "adpr_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "adpr_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "adpr_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "adpr_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "adpr_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "adpr_status = 'TRUE'")
public class ProcesoAdministrativo extends EntidadAuditable {

	private static final long serialVersionUID = -7414679517658499518L;

	public static final String GET_ENTE_RECEPTOR_PROCESO_ADMINISTRATIVO = "ec.com.magmasoft.business.domain.ProcesoAdministrativo.getEnteReceptorProcesoAdministrativo";
	public static final String GET_ENTE_RECEPTOR_PROCESO_ADMINISTRATIVO_POR_TIPO = "ec.com.magmasoft.business.domain.ProcesoAdministrativo.getEnteReceptorProcesoAdministrativoPorTipo";

	@Getter
	@Setter
	@Id
	@Column(name = "adpr_id")
	@SequenceGenerator(name = "ADMINISTRATIVE_PROCEDURES_ADPRID_GENERATOR", sequenceName = "seq_adpr_id", schema = "suia_iii_administrative", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ADMINISTRATIVE_PROCEDURES_ADPRID_GENERATOR")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "peop_id")
	@ForeignKey(name = "fk_administrativeadpr_id_peoplepeop_id")
	private Persona persona;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "pren_id")
	@ForeignKey(name = "fk_administrativeadpr_id_projectspren_id")
	private ProyectoLicenciamientoAmbiental proyecto;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "afpr_id")
	@ForeignKey(name = "fk_administrativeadpr_id_affectatafpr_id")
	private ProcedimientoAfectacion procedimientoAfectacion;

}
