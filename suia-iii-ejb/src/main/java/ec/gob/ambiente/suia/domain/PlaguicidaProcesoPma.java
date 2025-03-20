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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;


/**
 * The persistent class for the fapma_process_pesticide database table.
 * 
 */
@Entity
@Table(name="fapma_process_pesticide", schema = "suia_iii")
@NamedQuery(name="PlaguicidaProcesoPma.findAll", query="SELECT p FROM PlaguicidaProcesoPma p")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "fapp_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "fapp_status = 'TRUE'")
public class PlaguicidaProcesoPma extends EntidadBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1919651200526838349L;

	@Id
	@SequenceGenerator(name = "PROCCES_PESTICIDE_PMA_GENERATOR", sequenceName = "fapma_process_pesticide_id_seq", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROCCES_PESTICIDE_PMA_GENERATOR")
	@Column(name="fapp_id")
	@Getter
	@Setter
	private Integer id;

	@Column(name="fapp_tradename")
	@Getter
	@Setter
	private String nombreComercial;
	
	//Kg/ha
	@Column(name="fapp_application_rate")
	@Getter
	@Setter
	private double dosisAplicacion;
	
	//Días
	@Column(name="fapp_application_frequency")
	@Getter
	@Setter
	private Integer frecuenciaAplicacion;
	
	@Column(name="fapp_expiration_date")
	@Getter
	@Setter
	@Temporal(TemporalType.DATE)
	private Date fechaCaducidad;
	
	@Column(name="fapp_presentation")
	@Getter
	@Setter
	private String presentacion;
	
	//bi-directional many-to-one association to CatiiFapma
	@ManyToOne
	@JoinColumn(name="tocc_id")
	@ForeignKey(name = "toxicological_category_catalog_fapma_process_pesticide_fk")
	@Getter
	@Setter
	private CatalogoCategoriaToxicologica categoriaToxicologica;
	
	// bi-directional many-to-one association to CatiiFapma
	@ManyToOne
	@JoinColumn(name = "cafa_id")
	@ForeignKey(name = "catii_fapma_process_pesticide_fk")
	@Getter
	@Setter
	private FichaAmbientalPma fichaAmbientalPma;
	
	@Getter
	@Setter
	@Column(name = "fapp_original_record_id")
	private Integer idRegistroOriginal;
	
	@Getter
	@Setter
	@Column(name = "fapp_historical_date")
	private Date fechaHistorico;
	
	@Getter
	@Setter
	@Transient
	private String nuevoEnModificacion;

	public PlaguicidaProcesoPma() {
		super();
	}

	public PlaguicidaProcesoPma(Integer id) {
		super();
		this.id = id;
	}

}