package ec.gob.ambiente.suia.domain;

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
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;


/**
 * The persistent class for the fapma_process_fertilizer database table.
 * 
 */
@Entity
@Table(name="fapma_process_sanitary_disposal", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "fapsd_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "fapsd_status = 'TRUE'")
public class DesechoSanitarioProcesoPma extends EntidadBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6137074911984392893L;

	@Id
	@SequenceGenerator(name = "PROCCES_SANITARY_DISPOSAL_PMA_GENERATOR", sequenceName = "fapma_process_sanitary_disposal_id_seq", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROCCES_SANITARY_DISPOSAL_PMA_GENERATOR")
	@Column(name="fapsd_id")
	@Getter
	@Setter
	private Integer id;
	
	@Column(name="fapsd_reduction_method")
	@Getter
	@Setter
	private String metodoReduccion;

	@Column(name="fapsd_previous_operations")
	@Getter
	@Setter
	private String operacionesPrevias;
	
	@Column(name="fapsd_storage_capacity")
	@Getter
	@Setter
	private Integer capacidadAlmacenamiento;
		
	// bi-directional many-to-one association to CatiiFapma
	@ManyToOne
	@JoinColumn(name = "cafa_id")
	@ForeignKey(name = "catii_fapma_process_sanitary_disposal_fk")
	@Getter
	@Setter
	private FichaAmbientalPma fichaAmbientalPma;
	
	/**
	 * Cris F: Nuevos Campos para historial
	 */	
	@Getter
	@Setter
	@Column(name="fapsd_original_record_id")
	private Integer idRegistroOriginal;
	
	@Getter
	@Setter
	@Column(name="fapsd_historical_date")
	private Date fechaHistorico;	
}