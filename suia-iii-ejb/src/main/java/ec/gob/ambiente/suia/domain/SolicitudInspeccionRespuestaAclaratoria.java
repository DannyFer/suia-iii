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

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * <b> Entity Bean. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: Nov 16, 2015]
 *          </p>
 */
@Entity
@NamedQueries({
		@NamedQuery(name = SolicitudInspeccionRespuestaAclaratoria.GET_BY_SOLICITUD_VECES_BANDEJA, query = "SELECT s FROM SolicitudInspeccionRespuestaAclaratoria s WHERE s.solicitudInspeccionControlAmbiental.id = :idSolicitud AND s.bandejaTecnico = :bandejaTecnico"),
		@NamedQuery(name = SolicitudInspeccionRespuestaAclaratoria.GET_BY_SOLICITUD, query = "SELECT s FROM SolicitudInspeccionRespuestaAclaratoria s WHERE s.solicitudInspeccionControlAmbiental.id = :idSolicitud") })
@Table(name = "environmental_inspection_answers", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "eian_status") ),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "eian_creation_date") ),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "eian_date_update") ),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "eian_creator_user") ),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "eian_user_update") ) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "eian_status = 'TRUE'")
public class SolicitudInspeccionRespuestaAclaratoria extends EntidadAuditable {

	private static final long serialVersionUID = -3015254433762142149L;

	public static final String GET_BY_SOLICITUD_VECES_BANDEJA = "ec.gob.ambiente.suia.domain.SolicitudInspeccionRespuestaAclaratoria.get_by_solicitud_veces_bandeja";
	public static final String GET_BY_SOLICITUD = "ec.gob.ambiente.suia.domain.SolicitudInspeccionRespuestaAclaratoria.get_by_solicitud";

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "ENVIRONMENTAL_INSPECTION_REQUEST_ANSWERS_EIAN_ID_GENERATOR", sequenceName = "seq_eian_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ENVIRONMENTAL_INSPECTION_REQUEST_ANSWERS_EIAN_ID_GENERATOR")
	@Column(name = "eian_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "eian_technical_times")
	private Integer bandejaTecnico;

	@Getter
	@Setter
	@Column(name = "eian_description", columnDefinition = "text")
	private String descripcion;

	@ManyToOne
	@JoinColumn(name = "docu_id")
	@ForeignKey(name = "fk_eian_id_docu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	@Getter
	@Setter
	private Documento documento;

	@ManyToOne
	@JoinColumn(name = "eire_id")
	@ForeignKey(name = "fk_eian_id_eire_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "eire_status = 'TRUE'")
	@Getter
	@Setter
	private SolicitudInspeccionControlAmbiental solicitudInspeccionControlAmbiental;
}
