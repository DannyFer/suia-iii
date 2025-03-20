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
import ec.gob.ambiente.suia.domain.ModalidadGestionDesechos;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * <b> Clase que representa a la tabla que tiene la relación de aprobación de
 * requisitos tecnicos y las modalidades de gestión. </b>
 * 
 * @author vero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: vero $, $Date: 17/06/2015 $]
 *          </p>
 */
@Entity
@Table(name = "approval_technical_requirements_modality", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "atrm_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "atrm_status = 'TRUE'")
public class AprobacionRequisitosTecnicosModalidad extends EntidadBase{
	
	/**
	* 
	*/ 
	private static final long serialVersionUID = -3643301674245412112L;

	@Id
	@Column(name = "atrm_id")
	@SequenceGenerator(name = "APPROVAL_TECHNICAL_REQUIREMENTS_MODALITY_GENERATOR", sequenceName = "seq_atrm_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "APPROVAL_TECHNICAL_REQUIREMENTS_MODALITY_GENERATOR")
	@Getter
	@Setter
	private Integer id;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "apte_id")
	@ForeignKey(name = "fk_approval_requirements_modalityatrm_id_approval_requirementsapte_id")	
	private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "wmmo_id")
	@ForeignKey(name = "fk_approval_requirements_modalityatrm_id_waste_management_modewmmo_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wmmo_status = 'TRUE'")
	private ModalidadGestionDesechos modalidad;

}
