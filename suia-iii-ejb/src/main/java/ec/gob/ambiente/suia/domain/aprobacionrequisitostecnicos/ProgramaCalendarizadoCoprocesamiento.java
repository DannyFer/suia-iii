/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos;

import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * <b> Clase que representa a la tabla que contiene las actividades de los
 * protocolos de pruebas. </b>
 * 
 * @author Jonathan Guerrero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Jonathan Guerrero $, $Date: 02/07/2015 $]
 *          </p>
 * 
 */
@Entity
@Table(name = "coprocessing_timetabled_program", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "cotp_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "cotp_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "cotp_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "cotp_ureator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "cotp_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "cotp_status = 'TRUE'")
public class ProgramaCalendarizadoCoprocesamiento extends EntidadAuditable {

	/**
	* 
	*/
	private static final long serialVersionUID = 8663216887186924551L;

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "COPROCESSING_TIMETABLED_PROGRAM_GENERATOR", sequenceName = "seq_cotp_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COPROCESSING_TIMETABLED_PROGRAM_GENERATOR")
	@Column(name = "cotp_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "prta_id")
	@ForeignKey(name = "fk_coprocessing_timeta_programcotp_id_test_activityprta_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prta__status = 'TRUE'")
	private ActividadProtocoloPrueba actividad;

	@Getter
	@Setter
	@Temporal(TemporalType.DATE)
	@Column(name = "cotp_start_date")
	private Date fechaInicio;

	@Getter
	@Setter
	@Temporal(TemporalType.DATE)
	@Column(name = "cotp_end_date")
	private Date fechaFin;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "mocop_id")
	@ForeignKey(name = "fk_coprocessing_timeta_programcotp_id_modal_coprocessmocop_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "mocop_status = 'TRUE'")
	private ModalidadCoprocesamiento modalidadCoprocesamiento;

}
