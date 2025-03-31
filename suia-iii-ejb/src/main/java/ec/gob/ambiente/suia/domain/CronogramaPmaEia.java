package ec.gob.ambiente.suia.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
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
 * The persistent class for the categories_catalog database table.
 *
 */
@Entity
@Table(name = "schedule_pma_eia", catalog = "", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "spma_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "spma_status = 'TRUE'")
@NamedQueries({ @NamedQuery(name = CronogramaPmaEia.LISTAR_POR_PMA, query = "SELECT c FROM CronogramaPmaEia c WHERE c.pmaEIA.id = :p_eiaId") })
public class CronogramaPmaEia extends EntidadBase implements Serializable, Cloneable {

	private static final long serialVersionUID = -2222123796122625159L;

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR_POR_PMA = PAQUETE
			+ "CronogramaPmaEia.listarPorPmaId";

	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "PMA_SVPM_GEN", sequenceName = "seq_svpeia_id", initialValue = 1, schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PMA_SVPM_GEN")
	@Column(name = "spma_id", unique = true, nullable = false)
	private Integer id;


	@Getter
	@Setter
    @JoinColumn(name = "empt_id", referencedColumnName = "empt_id", nullable = false)
    @ForeignKey(name = "fk_spma_id_to_empt_id")
    @ManyToOne
	private TipoPlanManejoAmbiental tipoPlanManejoAmbiental;

	@Getter
	@Setter
	@JoinColumn(name = "empl_id", referencedColumnName = "empl_id")
	@ForeignKey(name = "fk_sch_pma_empl_id_env_man_plan_empl_id")
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private PlanManejoAmbientalEIA pmaEIA;

	@Getter
	@Setter
	@Column(name = "spma_month1")
	private Boolean mes1;
	@Getter
	@Setter
	@Column(name = "spma_month2")
	private Boolean mes2;
	@Getter
	@Setter
	@Column(name = "spma_month3")
	private Boolean mes3;
	@Getter
	@Setter
	@Column(name = "spma_month4")
	private Boolean mes4;
	@Getter
	@Setter
	@Column(name = "spma_month5")
	private Boolean mes5;
	@Getter
	@Setter
	@Column(name = "spma_month6")
	private Boolean mes6;
	@Getter
	@Setter
	@Column(name = "spma_month7")
	private Boolean mes7;
	@Getter
	@Setter
	@Column(name = "spma_month8")
	private Boolean mes8;
	@Getter
	@Setter
	@Column(name = "spma_month9")
	private Boolean mes9;
	@Getter
	@Setter
	@Column(name = "spma_month10")
	private Boolean mes10;
	@Getter
	@Setter
	@Column(name = "spma_month11")
	private Boolean mes11;
	@Getter
	@Setter
	@Column(name = "spma_month12")
	private Boolean mes12;
	@Getter
	@Setter
	@Column(name = "spma_budget")
	private Double presupuesto;
	@Getter
	@Setter
	@Column(name = "spma_order")
	private Integer orden;
	@Getter
	@Setter
	@Column(name = "spma_historical_id")
	private Integer idHistorico;
	@Getter
	@Setter
	@Column(name = "spma_notification_number")
	private Integer numeroNotificacion;

	@Getter
	@Setter
	@Transient
	private Boolean registroModificado;

	public CronogramaPmaEia() {

	}
	
	public CronogramaPmaEia(Integer id) {
		this.id = id;
	}
	
	@Override
	public CronogramaPmaEia clone() throws CloneNotSupportedException {

		 CronogramaPmaEia clone = (CronogramaPmaEia)super.clone();
		 clone.setId(null);		 
		 return clone;
	}
	
	//Cris F: fecha
	@Getter
	@Setter
	@Column(name = "spma_date_create")
	private Date fechaCreacion;

}