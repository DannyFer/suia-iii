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
 * <b> Clase que representa a la de desecho de la modalidad reciclaje. </b>
 * 
 * @author vero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: vero $, $Date: 10/06/2015 $]
 *          </p>
 */
@Entity
@Table(name = "modality_recycling_waste", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "morw_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "morw_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "morw_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "morw_ureator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "morw_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "morw_status = 'TRUE'")
public class DesechoModalidadReciclaje extends EntidadAuditable {

	public DesechoModalidadReciclaje() {

	}

	public DesechoModalidadReciclaje(DesechoPeligroso desecho, ModalidadReciclaje modalidad) {
		this.desecho = desecho;
		this.modalidadReciclaje = modalidad;

	}

	/**
	* 
	*/
	private static final long serialVersionUID = -1148646903615828148L;

	@Id
	@Column(name = "morw_id")
	@SequenceGenerator(name = "MODALITY_RECYCLING_WASTE_GENERATOR", sequenceName = "seq_morw_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MODALITY_RECYCLING_WASTE_GENERATOR")
	@Getter
	@Setter
	private Integer id;

	@Column(name = "morw_yearly_capacity_process")
	@Getter
	@Setter
	private Double capacidadAnualProceso;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "wada_id")
	@ForeignKey(name = "fk_modality_treatment_wastemotw_id_waste_dangerouswada_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	private DesechoPeligroso desecho;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "modec_id")
	@ForeignKey(name = "fk_modality_recycling_wastemorw_id_modality_recyclingmoreu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "moreu_status = 'TRUE'")
	private ModalidadReciclaje modalidadReciclaje;

	public boolean isRegistroCompleto() {
		return capacidadAnualProceso != null;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DesechoModalidadReciclaje)) {
			return false;
		}
		DesechoModalidadReciclaje other = (DesechoModalidadReciclaje) obj;
		if (((this.desecho == null) && (other.getDesecho() != null))
				|| ((this.desecho != null) && !this.desecho.equals(other.getDesecho()))) {
			return false;
		}
		return true;

	}

	public int hashCode() {
		return (desecho.getId() + desecho.getDescripcion()).hashCode();
	}

}
