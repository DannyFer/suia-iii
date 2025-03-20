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
import javax.persistence.NamedQuery;
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
@Table(name="fapma_process_fertilizer", schema = "suia_iii")
@NamedQuery(name="FertilizanteProcesoPma.findAll", query="SELECT f FROM FertilizanteProcesoPma f")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "fapp_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "fapp_status = 'TRUE'")
public class FertilizanteProcesoPma extends EntidadBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3297393581250713107L;

	@Id
	@SequenceGenerator(name = "PROCCES_FERTILIZER_PMA_GENERATOR", sequenceName = "fapma_process_fertilizer_id_seq", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROCCES_FERTILIZER_PMA_GENERATOR")
	@Column(name="fapf_id")
	@Getter
	@Setter
	private Integer id;

	@Column(name="fapf_tradename")
	@Getter
	@Setter
	private String nombreComercial;
	
	//Kg/ha
	@Column(name="fapf_application_rate")
	@Getter
	@Setter
	private double dosisAplicacion;
	
	@Column(name="fapf_is_Organic")
	@Getter
	@Setter
	private Boolean esOrganico;
		
	// bi-directional many-to-one association to CatiiFapma
	@ManyToOne
	@JoinColumn(name = "cafa_id")
	@ForeignKey(name = "catii_fapma_process_fertilizer_fk")
	@Getter
	@Setter
	private FichaAmbientalPma fichaAmbientalPma;
	
	/**
	 * CF: Campos para historico
	 */	
	@Getter
	@Setter
	@Column(name="fapf_original_record_id")
	private Integer idRegistroOriginal;	
	
	@Getter
	@Setter
	@Column(name="fapf_historical_date")
	private Date fechaHistorico;
	
	@Getter
	@Setter
	@Transient
	private String nuevoEnModificacion;

	public FertilizanteProcesoPma() {
		super();
	}

	public FertilizanteProcesoPma(Integer id) {
		super();
		this.id = id;
	}

}