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
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

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
@Table(name = "hazardous_wastes_collectors", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "hwco_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "hwco_status = 'TRUE'")
public class GeneradorDesechosRecolector extends EntidadBase {

	private static final long serialVersionUID = -1704140958463355819L;

	@Getter
	@Setter
	@Id
	@Column(name = "hwco_id")
	@SequenceGenerator(name = "HAZARDOUS_WASTES_HWCO_ID_GENERATOR", sequenceName = "seq_hwco_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "HAZARDOUS_WASTES_HWCO_ID_GENERATOR")
	private Integer id;

	@Getter
	@Setter
	@OneToMany(mappedBy = "generadorDesechosRecolector")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wcpl_status = 'TRUE'")
	private List<GeneradorDesechosRecolectorSede> generadoresDesechosRecolectoresSedes;

	@Getter
	@Setter
	@Transient
	private List<GeneradorDesechosRecolectorSede> generadoresDesechosRecolectoresSedesActuales;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "hwwd_id")
	@ForeignKey(name = "fk_hwco_id_hwwd_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "hwwd_status = 'TRUE'")
	private GeneradorDesechosDesechoPeligroso generadorDesechosDesechoPeligroso;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "gelo_id")
	@ForeignKey(name = "fk_hwco_id_gelo_id")
	private UbicacionesGeografica ubicacionesGeograficaDestino;

	@Getter
	@Setter
	@OneToMany(mappedBy = "generadorDesechosRecolector")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "hwcl_status = 'TRUE'")
	private List<GeneradorDesechosRecoletorUbicacionGeografica> generadorDesechosRecoletorUbicacionesGeograficas;

	@Getter
	@Setter
	@Transient
	private List<GeneradorDesechosRecoletorUbicacionGeografica> generadorDesechosRecoletorUbicacionesGeograficasActuales;

	@Getter
	@Setter
	@Transient
	private DesechoPeligroso desechoPeligroso;
}
