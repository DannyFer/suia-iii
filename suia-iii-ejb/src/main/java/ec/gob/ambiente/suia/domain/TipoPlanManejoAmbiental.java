package ec.gob.ambiente.suia.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the categories_catalog database table.
 *
 */
@Entity
@Table(name = "environmental_management_plan_types", catalog = "", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "empt_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "empt_status = 'TRUE'")
@NamedQueries({
@NamedQuery(name = TipoPlanManejoAmbiental.FIND_PLAN, query = "SELECT c FROM TipoPlanManejoAmbiental c WHERE c.estado = true and c.tipoProceso=:tipoProceso")
})
public class TipoPlanManejoAmbiental extends EntidadBase implements Serializable {

    private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String FIND_PLAN = PAQUETE+ "TipoPlanManejoAmbiental.findAll";

	/**
	 *
	 */
	private static final long serialVersionUID = 2498442654200330337L;

	//TIPOS DE PLAN
	public static final int NORMATIVAS = 1;

	public static final String REGISTRO_AMBIENTAL = "REGISTRO_AMBIENTAL";

	public static final String LICENCIA_AMBIENTAL = "LICENCIA_AMBIENTAL";

	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "PMA_TYPE_CATLYID_GENERATOR", sequenceName = "seq_empt_id", schema = "suia_iii")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PMA_TYPE_CATLYID_GENERATOR")
	@Column(name = "empt_id", unique = true, nullable = false)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "empt_type", nullable = false, length = 255)
	private String tipo;

	@Getter
	@Setter
	@Column(name = "empt_code", nullable = true, length = 255)
	private String codigo;

	@Getter
	@Setter
	@OneToMany(mappedBy = "tipoPlanManejoAmbiental", fetch = FetchType.LAZY)
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "plse_status = 'TRUE'")
	private List<PlanSector> planSectorList;

	@Getter
	@Setter
	@Column(name="empt_process_type", nullable = true, length = 255)
	private String tipoProceso;

	public TipoPlanManejoAmbiental(Integer id, String tipo, String codigo) {
		super();
		this.id = id;
		this.tipo = tipo;
		this.codigo = codigo;
	}

	public TipoPlanManejoAmbiental(){

	}

    public TipoPlanManejoAmbiental(String tipo) {
        this.tipo = tipo;
    }


}