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
 * The persistent class for the fapma_process_waste_dangerous database table.
 * 
 */
@Entity
@Table(name="fapma_process_waste_dangerous", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "fapw_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "fapw_status = 'TRUE'")
public class DesechoPeligrosoProcesoPma extends EntidadBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2414751321477328189L;

	@Id
	@SequenceGenerator(name = "PROCCES_WASTE_DANGEROUS_GENERATOR", sequenceName = "fapma_process_waste_dangerous_id_seq", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROCCES_WASTE_DANGEROUS_GENERATOR")
	@Column(name="fapw_id")
	@Getter
	@Setter
	private Integer id;
	
	@Column(name="fapw_packing_type")
	@Getter
	@Setter
	private String tipoEnvalaje;
	
	@Column(name="fapv_collection_capacity")
	@Getter
	@Setter
	private Double capacidadRecoleccion;
	
	@Column(name="fapv_vehicle_description")
	@Getter
	@Setter
	private String vehiculoDescripcionExantes;
	
	// bi-directional many-to-one association
	@ManyToOne
	@JoinColumn(name = "fapv_id")
	@ForeignKey(name = "fapma_process_vehicle_disposal_process_waste_dangerous_fk")
	@Getter
	@Setter
	private VehiculoDesechoSanitarioProcesoPma vehiculo;
	
	// bi-directional many-to-one association
	@ManyToOne
	@JoinColumn(name = "wada_id")
	@ForeignKey(name = "waste_dangerous_process_waste_dangerous_fk")
	@Getter
	@Setter
	private DesechoPeligroso desechoPeligroso;
		
	// bi-directional many-to-one association
	@ManyToOne
	@JoinColumn(name = "cafa_id")
	@ForeignKey(name = "catii_fapma_process_waste_dangerous_fk")
	@Getter
	@Setter
	private FichaAmbientalPma fichaAmbientalPma;

	/**
	 * CF: Aumento de constructores para comparator
	 */	
	@Getter
	@Setter
	@Column(name="fapw_original_record_id")
	private Integer idRegistroOriginal;
	
	@Getter
	@Setter
	@Column(name="fapw_historical_date")
	private Date fechaHistorico;
	
	@Getter
	@Setter
	@Transient
	private String nuevoEnModificacion;
	
	public DesechoPeligrosoProcesoPma() {
		super();
	}

	public DesechoPeligrosoProcesoPma(Integer id) {
		super();
		this.id = id;
	}
}