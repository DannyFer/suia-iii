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

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * <b> Clase entidad de los requisitos del conductor. </b>
 * 
 * @author Javier Lucero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Javier Lucero $, $Date: 04/06/2015 $]
 *          </p>
 */
@Entity
@Table(name = "requirements_driver", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "redri_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "redri_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "redri_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "redri_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "redri_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "redri_status = 'TRUE'")
public class RequisitosConductor extends EntidadAuditable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4357256048649563613L;
	

	@Id
	@Column(name = "redri_id")
	@SequenceGenerator(name = "REQUIREMENTS_DRIVER_GENERATOR", sequenceName = "seq_redri_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REQUIREMENTS_DRIVER_GENERATOR")
	@Getter
	@Setter
	private Integer id;
	
	@Getter
	@Setter
	@ManyToOne()
	@JoinColumn(name = "apte_id")
	@ForeignKey(name = "fk_rrequirements_driver_apte_id_approval_requirements_apte_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "apte_status = 'TRUE'")
	private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;
	
	@Getter
	@Setter
	@ManyToOne()
	@JoinColumn(name = "qudr_id")
	@ForeignKey(name = "fk_qualified_drivers_qudr_id_requirements_driver_redri_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "qudr_status = 'TRUE'")
	private Conductor conductor;

}
