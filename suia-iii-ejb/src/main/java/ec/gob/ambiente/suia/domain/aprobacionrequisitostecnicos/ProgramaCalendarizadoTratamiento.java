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
 * <b>
 * Clase que representa a la tabla que contiene las actividades de los protocolos de pruebas.
 * </b>
 *  
 * @author vero
 * @version $Revision: 1.0 $ <p>[$Author: vero $, $Date: 15/06/2015 $]</p>
 * 
 */
@Entity
@Table(name = "treatment_timetabled_program", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "tetp_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "tetp_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "tetp_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "tetp_ureator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "tetp_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tetp_status = 'TRUE'")
public class ProgramaCalendarizadoTratamiento extends EntidadAuditable {
	
	
	/**
	* 
	*/ 
	private static final long serialVersionUID = -6150695841279756584L;

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "TREATMENT_TIMETABLED_PROGRAM_GENERATOR", sequenceName = "seq_tetp_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TREATMENT_TIMETABLED_PROGRAM_GENERATOR")
	@Column(name = "tetp_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne()
	@JoinColumn(name = "prta_id")
	@ForeignKey(name = "fk_creatment_modality_test_activityprta_id_test_activityprta_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prta__status = 'TRUE'")	
	private ActividadProtocoloPrueba actividad;
	
	@Getter
	@Setter
	@Temporal(TemporalType.DATE)
	@Column(name = "tetp_start_date")
	private Date fechaInicio;
	
	
	@Getter
	@Setter
	@Temporal(TemporalType.DATE)
	@Column(name = "tetp_end_date")
	private Date fechaFin;
	
	@Getter
	@Setter
	@ManyToOne()
	@JoinColumn(name = "motr_id")
	@ForeignKey(name = "fk_creatment_mod_activit_calendar_motr_id_mod_treatment_motr_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "motr_status = 'TRUE'")
	private ModalidadTratamiento modalidadTratamiento;
	

}
