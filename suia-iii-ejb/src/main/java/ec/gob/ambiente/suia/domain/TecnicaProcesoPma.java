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

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name = "fapma_process_technicals", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "fapte_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "fapte_status = 'TRUE'")
public class TecnicaProcesoPma extends EntidadBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7708159954765910638L;

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "PROCESS_TECHNICALS_GENERATOR", sequenceName = "process_technicals_seq", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROCESS_TECHNICALS_GENERATOR")
	@Column(name = "fapte_id")
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name="teca_id")
	@ForeignKey(name = "technicals_catalog_fapma_process_technicals_fk")
	@Getter
	@Setter
	private CatalogoTecnica catalogoTecnica;
	
	@ManyToOne
	@JoinColumn(name="cafa_id")
	@ForeignKey(name = "catii_fapma_process_technicals_fk")
	@Getter
	@Setter
	private FichaAmbientalPma fichaAmbientalPma;
	
	@Getter
	@Setter
	@Column(name = "fapte_original_record_id")
	private Integer idRegistroOriginal;
	
	@Getter
	@Setter
	@Column(name = "fapte_historical_date")
	private Date fechaHistorico;
	
}