package ec.gob.ambiente.suia.domain;

import java.util.Date;
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
import javax.persistence.OneToOne;
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
@Table(name = "hazardous_wastes_waste_dangerous_general_data", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "hwwg_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "hwwg_status = 'TRUE'")
public class GeneradorDesechosDesechoPeligrosoDatosGenerales extends EntidadBase {

	private static final long serialVersionUID = 2803906006723482472L;

	@Getter
	@Setter
	@Id
	@Column(name = "hwwg_id")
	@SequenceGenerator(name = "HAZARDOUS_WASTES_WASTE_HWWG_ID_GENERATOR", sequenceName = "seq_hwwg_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "HAZARDOUS_WASTES_WASTE_HWWG_ID_GENERATOR")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "hwwg_quantity_tons")
	private double cantidadToneladas;
	
	@Getter
	@Setter
	@Column(name = "hwwg_quantity_units")
	private double cantidadUnidades;

	@Getter
	@Setter
	@Column(name = "hwwg_waste_category_other")
	private String otroCategoriaDesecho;

	@Getter
	@Setter
	@Transient
	private double cantidadKilogramos;

	@Getter
	@Setter
	@Transient
	private Integer sistemaGestion;

	@Getter
	@Setter
	@Transient
	private String sistemaGestionNambre;

	@Getter
	@Setter
	@Transient
	private Date sistemaGestionFecha;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "wdca_id")
	@ForeignKey(name = "fk_hwwg_id_wdca_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wdca_status = 'TRUE'")
	private CategoriaDesechoPeligroso categoriaDesechoPeligroso;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "psty_id")
	@ForeignKey(name = "fk_hwwg_id_psty_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "psty_status = 'TRUE'")
	private TipoEstadoFisico tipoEstadoFisico;

	@Getter
	@Setter
	@OneToOne(mappedBy = "generadorDesechosDesechoPeligrosoDatosGenerales")
	private GeneradorDesechosDesechoPeligroso generadorDesechosDesechoPeligroso;

	@Getter
	@Setter
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "generaDesechosDesechoPeligrosoDatosGenerales")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "hwwp_status = 'TRUE'")
	private List<GeneradorDesechosDesechoPeligrosoPuntoGeneracion> generadorDesechosDesechoPeligrosoPuntosGeneracion;
	
	@Getter
	@Setter
	@Transient
	private List<GeneradorDesechosDesechoPeligrosoPuntoGeneracion> generadorDesechosDesechoPeligrosoPuntosGeneracionActuales;

	@Getter
	@Setter
	@Transient
	private DesechoPeligroso desechoPeligroso;
}
