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
 * 
 * <b> Entity Bean. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: Nov 06, 2015]
 *          </p>
 */
@Entity
@NamedQueries({
		@NamedQuery(name = CronogramaAnualInspeccionesControlAmbiental.GET_BY_YEAR_AND_AREA, query = "SELECT c FROM CronogramaAnualInspeccionesControlAmbiental c WHERE c.areaResponsable.id = :idAreaResponsable AND c.anno = :anno") })
@Table(name = "environmental_inspection_annual_schedule", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "eias_status") ),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "eias_creation_date") ),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "eias_date_update") ),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "eias_creator_user") ),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "eias_user_update") ) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "eias_status = 'TRUE'")
public class CronogramaAnualInspeccionesControlAmbiental extends EntidadAuditable {

	private static final long serialVersionUID = 1200763924266939916L;

	public static final String GET_BY_YEAR_AND_AREA = "ec.gob.ambiente.suia.domain.CronogramaAnualInspeccionesControlAmbiental.get_by_year_and_area";

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "ENVIRONMENTAL_INSPECTION_ANNUAL_SCHEDULE_GENERATORS_EIAS_ID_GENERATOR", sequenceName = "seq_eias_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ENVIRONMENTAL_INSPECTION_ANNUAL_SCHEDULE_GENERATORS_EIAS_ID_GENERATOR")
	@Column(name = "eias_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "eias_year")
	private Integer anno;

	@ManyToOne
	@JoinColumn(name = "user_id")
	@ForeignKey(name = "fk_e_i_requestseias_id_usersuser_id")
	@Getter
	@Setter
	private Usuario usuario;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "area_id")
	@ForeignKey(name = "fk_e_i_requestseias_id_area_id")
	private Area areaResponsable;
}
