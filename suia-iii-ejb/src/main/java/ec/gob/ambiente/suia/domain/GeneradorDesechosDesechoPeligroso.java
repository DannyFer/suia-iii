package ec.gob.ambiente.suia.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

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
@Table(name = "hazardous_wastes_waste_dangerous", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "hwwd_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "hwwd_status = 'TRUE'")
public class GeneradorDesechosDesechoPeligroso extends EntidadBase {

	private static final long serialVersionUID = -790540583562854316L;

	@Getter
	@Setter
	@Id
	@Column(name = "hwwd_id")
	@SequenceGenerator(name = "HAZARDOUS_WASTES_WASTE_HWWD_ID_GENERATOR", sequenceName = "seq_hwwd_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "HAZARDOUS_WASTES_WASTE_HWWD_ID_GENERATOR")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "hwge_id")
	@ForeignKey(name = "fk_hwwd_id_hwge_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "hwge_status = 'TRUE'")
	private GeneradorDesechosPeligrosos generadorDesechosPeligrosos;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "wada_id")
	@ForeignKey(name = "fk_fk_hwwd_id_wada_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wada_status = 'TRUE'")
	private DesechoPeligroso desechoPeligroso;

	@Getter
	@Setter
	@OneToOne
	@JoinColumn(name = "hwwg_id")
	@ForeignKey(name = "fk_hwwd_id_hwwg_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "hwwg_status = 'TRUE'")
	private GeneradorDesechosDesechoPeligrosoDatosGenerales generadorDesechosDesechoPeligrosoDatosGenerales;

	@Getter
	@Setter
	@OneToOne
	@JoinColumn(name = "hwwl_id")
	@ForeignKey(name = "fk_hwwd_id_hwwl_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "hwwl_status = 'TRUE'")
	private GeneradorDesechosDesechoPeligrosoEtiquetado generadorDesechosDesechoPeligrosoEtiquetado;

	@Getter
	@Setter
	@OneToMany(mappedBy = "generadorDesechosDesechoPeligroso")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "hwdw_status = 'TRUE'")
	private List<AlmacenGeneradorDesechoPeligroso> almacenGeneradorDesechoPeligrosos;

	@Getter
	@Setter
	@OneToMany(mappedBy = "generadorDesechosDesechoPeligroso", fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "ihwg_status = 'TRUE'")
	private List<IncompatibilidadGeneradorDesechosDesecho> incompatibilidades;

	@Getter
	@Setter
	@OneToMany(mappedBy = "generadorDesechosDesechoPeligroso")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "remp_status = 'TRUE'")
	private List<PuntoEliminacion> puntosEliminacion;

	@Getter
	@Setter
	@OneToMany(mappedBy = "generadorDesechosDesechoPeligroso")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "hwco_status = 'TRUE'")
	private List<GeneradorDesechosRecolector> generadoresDesechosRecolectores;

	@Getter
	@Setter
	@OneToMany(mappedBy = "generadorDesechosDesechoPeligroso")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "hwel_status = 'TRUE'")
	private List<GeneradorDesechosEliminador> generadoresDesechosEliminadores;

	public List<IncompatibilidadDesechoPeligroso> getIncompatibilidadesDesechoPeligro() {
		List<IncompatibilidadDesechoPeligroso> result = new ArrayList<IncompatibilidadDesechoPeligroso>();
		for (IncompatibilidadGeneradorDesechosDesecho incompatibilidad : incompatibilidades) 
			result.add(incompatibilidad.getIncompatibilidadDesechoPeligroso());
		return result;
	}
}
