package ec.gob.ambiente.suia.domain;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the categories_catalog database table.
 * 
 */
@Entity
@Table(name = "schedule_valued_pma", catalog = "", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "svpm_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "svpm_status = 'TRUE'")
@NamedQueries({ @NamedQuery(name = CronogramaValoradoEiaPma.LISTAR_POR_EIAID, query = "SELECT c FROM CronogramaValoradoEiaPma c WHERE c.eiaId = :p_eiaId") })
public class CronogramaValoradoEiaPma extends EntidadAuditable implements
		Serializable {
 
	/**
	 *  
	 */
	private static final long serialVersionUID = -2222123796122625159L;

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR_POR_EIAID = PAQUETE
			+ "CronogramaValoradoPMA.listarPorEiaId";

	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "PMA_SVPM_GENERATOR", sequenceName = "seq_svpm_id", initialValue = 1, schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PMA_SVPM_GENERATOR")
	@Column(name = "svpm_id", unique = true, nullable = false)
	private Integer id;

	
	@Getter
	@Setter
	@Column(name = "empt_id", insertable=false, updatable=false)
	private Integer tipoPmaId;
	@Getter
	@Setter
    @JoinColumn(name = "empt_id", referencedColumnName = "empt_id", nullable = false)
    @ForeignKey(name = "fk_svpm_id_to_empt_id")
    @ManyToOne
	private TipoPlanManejoAmbiental tipoPlanManejoAmbiental;
	@Getter
	@Setter
	@Column(name = "svpm_month1")
	private Double mes1 = 0.0;
	@Getter
	@Setter
	@Column(name = "svpm_month2")
	private Double mes2 = 0.0;
	@Getter
	@Setter
	@Column(name = "svpm_month3")
	private Double mes3 = 0.0;
	@Getter
	@Setter
	@Column(name = "svpm_month4")
	private Double mes4 = 0.0;
	@Getter
	@Setter
	@Column(name = "svpm_month5")
	private Double mes5 = 0.0;
	@Getter
	@Setter
	@Column(name = "svpm_month6")
	private Double mes6 = 0.0;
	@Getter
	@Setter
	@Column(name = "svpm_month7")
	private Double mes7 = 0.0;
	@Getter
	@Setter
	@Column(name = "svpm_month8")
	private Double mes8 = 0.0;
	@Getter
	@Setter
	@Column(name = "svpm_month9")
	private Double mes9 = 0.0;
	@Getter
	@Setter
	@Column(name = "svpm_month10")
	private Double mes10 = 0.0;
	@Getter
	@Setter
	@Column(name = "svpm_month11")
	private Double mes11 = 0.0;
	@Getter
	@Setter
	@Column(name = "svpm_month12")
	private Double mes12 = 0.0;
	@Getter
	@Setter
	@Column(name = "svpm_budget")
	private Double presupuesto = 0.0;
	@Getter
	@Setter
	@Column(name = "svpm_eia_id")
	private Integer eiaId;

	public Double getSumatoriaPresupuesto(){
		return mes1+mes2+mes3+mes4+mes5+mes6+mes7+mes8+mes9+mes10+mes11+mes12;
	}
	
	public CronogramaValoradoEiaPma() {

	}

}