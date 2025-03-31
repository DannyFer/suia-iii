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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
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
 *          [Autor: Carlos Pupo, Fecha: Oct 30, 2015]
 *          </p>
 */
@Entity
@NamedQueries({
		@NamedQuery(name = SolicitudInspeccionControlAmbiental.GET_BY_SOLICITUD, query = "SELECT s FROM SolicitudInspeccionControlAmbiental s WHERE s.solicitud = :solicitud") })
@Table(name = "environmental_inspection_request", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "eire_status") ),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "eire_creation_date") ),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "eire_date_update") ),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "eire_creator_user") ),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "eire_user_update") ) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "eire_status = 'TRUE'")
public class SolicitudInspeccionControlAmbiental extends EntidadAuditable {

	private static final long serialVersionUID = -4821663201575058076L;

	public static final String VARIABLE_NUMERO_SOLICITUD = "numeroSolicitudInspeccionControlAmbiental";
	public static final String VARIABLE_ID_SOLICITUD = "idSolicitudInspeccionControlAmbiental";
	public static final String VARIABLE_APOYO_REQUERIDO = "esApoyoRequerido";
	public static final String VARIABLE_CANTIDAD_OBSERVACIONES = "cantidadObservaciones";
	public static final String VARIABLE_TIPO_OFICIO = "tipoOficio";
	

	public static final String GET_BY_SOLICITUD = "ec.gob.ambiente.suia.domain.SolicitudInspeccionControlAmbiental.get_by_solicitud";

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "ENVIRONMENTAL_INSPECTION_REQUEST_GENERATORS_EIRE_ID_GENERATOR", sequenceName = "seq_eire_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ENVIRONMENTAL_INSPECTION_REQUEST_GENERATORS_EIRE_ID_GENERATOR")
	@Column(name = "eire_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "eire_request")
	private String solicitud;

	@Getter
	@Setter
	@Column(name = "eire_description", columnDefinition = "text")
	private String descripcion;

	@Getter
	@Setter
	@Column(name = "eire_is_launched_request")
	private boolean solicitudLanzada;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "pren_id")
	@ForeignKey(name = "fk_e_i_requestseire_id_projectspren_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "pren_status = 'TRUE'")
	private ProyectoLicenciamientoAmbiental proyecto;

	@ManyToOne
	@JoinColumn(name = "user_id")
	@ForeignKey(name = "fk_e_i_requestseire_id_usersuser_id")
	@Getter
	@Setter
	private Usuario usuario;

	@ManyToOne
	@JoinColumn(name = "user_technical_id")
	@ForeignKey(name = "fk_eire_id_userstechnicaluser_id")
	@Getter
	@Setter
	private Usuario tecnicoPlanificado;

	@ManyToOne
	@JoinColumn(name = "eias_id")
	@ForeignKey(name = "fk_eire_id_eias_id")
	@Getter
	@Setter
	private CronogramaAnualInspeccionesControlAmbiental cronogramaAnualInspeccionesControlAmbiental;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "area_id")
	@ForeignKey(name = "fk_e_i_requestseire_id_area_id")
	private Area areaResponsable;
	
	@Getter
	@Setter
	@OneToMany(mappedBy="solicitudInspeccionControlAmbiental")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "eian_status = 'TRUE'")
	private List<SolicitudInspeccionRespuestaAclaratoria> respuestasAclaratorias;
}
