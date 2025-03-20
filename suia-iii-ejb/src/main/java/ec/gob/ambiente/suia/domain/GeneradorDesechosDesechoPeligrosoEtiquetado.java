package ec.gob.ambiente.suia.domain;

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
 * 
 * <b> Entity. </b>
 * 
 * @author carlos.pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: carlos.pupo, Fecha: 08/06/2015]
 *          </p>
 */
@Entity
@Table(name = "hazardous_wastes_waste_dangerous_labeled", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "hwwl_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "hwwl_status = 'TRUE'")
public class GeneradorDesechosDesechoPeligrosoEtiquetado extends EntidadBase {

	private static final long serialVersionUID = -6936483870666934575L;

	@Getter
	@Setter
	@Id
	@Column(name = "hwwl_id")
	@SequenceGenerator(name = "HAZARDOUS_WASTES_WASTE_HWWL_ID_GENERATOR", sequenceName = "seq_hwwl_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "HAZARDOUS_WASTES_WASTE_HWWL_ID_GENERATOR")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "hwwl_quantity")
	private double cantidad;

	@Getter
	@Setter
	@Column(name = "hwwl_observations")
	private String observaciones;
	
	@Getter
	@Setter
	@Column(name = "hwwl_other_package")
	private String otroTipoEnvase;
	
	@Getter
	@Setter
	@Column(name = "hwwl_labeled_other")
	private String otroMetodoEtiquetado;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "paty_id")
	@ForeignKey(name = "fk_hwwl_id_paty_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "paty_status = 'TRUE'")
	private TipoEnvase tipoEnvase;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "laty_id")
	@ForeignKey(name = "fk_hwwl_id_laty_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "laty_status = 'TRUE'")
	private TipoEtiquetado tipoEtiquetado;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "meun_id")
	@ForeignKey(name = "fk_hwwl_id_meun_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "meun_status = 'TRUE'")
	private UnidadMedida unidadMedida;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "docu_id")
	@ForeignKey(name = "fk_hwwl_id_docu_id")
	private Documento modeloEtiqueta;
	
	@Getter
	@Setter
	@Transient
	private DesechoPeligroso desechoPeligroso;
}
