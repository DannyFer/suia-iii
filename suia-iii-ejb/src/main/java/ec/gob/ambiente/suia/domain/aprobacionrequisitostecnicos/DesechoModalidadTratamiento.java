/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos;

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

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * <b> Clase que representa a la de desecho de la modalidad tratamiento. </b>
 * 
 * @author vero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: vero $, $Date: 10/06/2015 $]
 *          </p>
 */
@Entity
@Table(name = "modality_treatment_waste", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "motw_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "motw_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "motw_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "motw_ureator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "motw_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "motw_status = 'TRUE'")
public class DesechoModalidadTratamiento extends EntidadAuditable {

	public DesechoModalidadTratamiento() {

	}

	public DesechoModalidadTratamiento(DesechoPeligroso desecho, ModalidadTratamiento modalidad) {
		this.desecho = desecho;
		this.modalidadTratamiento = modalidad;

	}

	/**
	* 
	*/
	private static final long serialVersionUID = -1148646903615828148L;

	@Id
	@Column(name = "motw_id")
	@SequenceGenerator(name = "MODALITY_TREATMENT_WASTE_GENERATOR", sequenceName = "seq_motw_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MODALITY_TREATMENT_WASTE_GENERATOR")
	@Getter
	@Setter
	private Integer id;

	@Column(name = "motw_yearly_capacity_process")
	@Getter
	@Setter
	private Double capacidadAnualProceso;

	@Getter
	@Setter
	@ManyToOne()
	@JoinColumn(name = "wada_id")
	@ForeignKey(name = "fk_modality_treatment_wastemotw_id_waste_dangerouswada_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wada_status = 'TRUE'")
	private DesechoPeligroso desecho;

	@Getter
	@Setter
	@ManyToOne()
	@JoinColumn(name = "motr_id")
	@ForeignKey(name = "fk_modality_treatment_wastemotw_id_modality_treatmentmotr_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	private ModalidadTratamiento modalidadTratamiento;

	@Column(name = "motw_maxima_amount_waste_testing")
	@Getter
	@Setter
	private Double cantidadMaximaDesechosPruebas;

	public boolean isRegistroCompleto() {
		return (capacidadAnualProceso != null) && (cantidadMaximaDesechosPruebas != null);
	}

}
