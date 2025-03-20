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
 *          [Autor: carlos.pupo, Fecha: 09/06/2015]
 *          </p>
 */
@Entity
@Table(name = "removal_points", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "remp_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "remp_status = 'TRUE'")
public class PuntoEliminacion extends EntidadBase {

	private static final long serialVersionUID = 5616020428378151406L;

	@Getter
	@Setter
	@Id
	@Column(name = "remp_id")
	@SequenceGenerator(name = "REMOVAL_POINT_REMP_ID_GENERATOR", sequenceName = "seq_remp_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REMOVAL_POINT_REMP_ID_GENERATOR")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "remp_anual_quantity_tons")
	private double cantidadAnualToneladas;

	@Getter
	@Setter
	@Column(name = "remp_anual_quantity_units")
	private double cantidadAnualUnidades;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "wdty_id")
	@ForeignKey(name = "fk_remp_id_wdty_id")
	private TipoEliminacionDesecho tipoEliminacionDesecho;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "hwwd_id")
	@ForeignKey(name = "fk_remp_id_hwwd_id")
	private GeneradorDesechosDesechoPeligroso generadorDesechosDesechoPeligroso;

	@Getter
	@Setter
	@Transient
	private DesechoPeligroso desechoPeligroso;

	@Getter
	@Setter
	@Column(name = "remp_other_options_associated_text")
	private String textoAsociadoOpcionOtro;

	@Getter
	@Setter
	@OneToMany(mappedBy = "puntoEliminacion")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "rphw_status = 'TRUE'")
	private List<PuntoEliminacionPrestadorServicioDesechoPeligroso> puntoEliminacionPrestadorServicioDesechoPeligrosos;
	
	@Getter
	@Setter
	@Transient
	private List<PuntoEliminacionPrestadorServicioDesechoPeligroso> puntoEliminacionPrestadorServicioDesechoPeligrososActuales;
}
