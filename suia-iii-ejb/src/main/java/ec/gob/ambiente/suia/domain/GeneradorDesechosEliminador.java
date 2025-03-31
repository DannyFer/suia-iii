package ec.gob.ambiente.suia.domain;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
 *          [Autor: carlos.pupo, Fecha: 10/06/2015]
 *          </p>
 */
@Entity
@Table(name = "hazardous_wastes_eliminators", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "hwel_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "hwel_status = 'TRUE'")
public class GeneradorDesechosEliminador extends EntidadBase {

	private static final long serialVersionUID = 2465668421485123021L;

	@Getter
	@Setter
	@Id
	@Column(name = "hwel_id")
	@SequenceGenerator(name = "HAZARDOUS_WASTES_HWEL_ID_GENERATOR", sequenceName = "seq_hwel_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "HAZARDOUS_WASTES_HWEL_ID_GENERATOR")
	private Integer id;

	@Getter
	@Setter
	@OneToMany(mappedBy = "generadorDesechosEliminador")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wepl_status = 'TRUE'")
	private List<GeneradorDesechosEliminadorSede> generadoresDesechosEliminadoresSedes;

	@Getter
	@Setter
	@Transient
	private List<GeneradorDesechosEliminadorSede> generadoresDesechosEliminadoresSedesActuales;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "hwwd_id")
	@ForeignKey(name = "fk_hwel_id_hwwd_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "hwwd_status = 'TRUE'")
	private GeneradorDesechosDesechoPeligroso generadorDesechosDesechoPeligroso;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "wdty_id")
	@ForeignKey(name = "fk_hwel_id_wdty_id")
	private TipoEliminacionDesecho tipoEliminacionDesecho;

	@Getter
	@Setter
	@Transient
	private DesechoPeligroso desechoPeligroso;

	@Getter
	@Setter
	@Column(name = "hwel_other_options_associated_text")
	private String textoAsociadoOpcionOtro;
}
