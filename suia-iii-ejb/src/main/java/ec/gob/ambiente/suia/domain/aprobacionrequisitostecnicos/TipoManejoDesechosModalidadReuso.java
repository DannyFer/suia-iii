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

import ec.gob.ambiente.suia.domain.TipoManejoDesechos;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * <b> Tabla que relaciona el tipo de manejo de desecho y la modalidad
 * reciclaje. </b>
 * 
 * @author vero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: vero $, $Date: 24/09/2015 $]
 *          </p>
 */
@Table(name = "type_waste_management_modality_reuse", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "twmru_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "twmru_status = 'TRUE'")
@Entity
public class TipoManejoDesechosModalidadReuso extends EntidadBase {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	/**
	* 
	*/

	@Id
	@Column(name = "twmru_id")
	@SequenceGenerator(name = "TYPE_WASTE_MANAGEMENT_MODALITY_REUSE_GENERATOR", sequenceName = "seq_twmru_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TYPE_WASTE_MANAGEMENT_MODALITY_REUSE_GENERATOR")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "moreu_id")
	@ForeignKey(name = "fk_type_waste_management_reuse_type_waste_managementtywm_id")
	private ModalidadReuso modalidadReuso;

	@Getter
	@Setter
	@JoinColumn(name = "tywm_id")
	@ForeignKey(name = "fk_type_waste_management_reuse_type_waste_managementtywm_id")
	@ManyToOne
	private TipoManejoDesechos tipoManejoDesecho;

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TipoManejoDesechosModalidadReuso)) {
			return false;
		}
		TipoManejoDesechosModalidadReuso other = (TipoManejoDesechosModalidadReuso) obj;
		if (((this.tipoManejoDesecho.getId() == null) && (other.tipoManejoDesecho.getId() != null))
				|| ((this.tipoManejoDesecho.getId() != null) && !this.tipoManejoDesecho.getId().equals(
						other.tipoManejoDesecho.getId()))) {
			return false;
		}
		return true;
	}

}
