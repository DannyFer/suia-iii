package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the environmental_facilitators_taxes database table.
 *
 */
@Entity
@Table(name = "environmental_facilitators_taxes", schema = "suia_iii")
@NamedQueries({ @NamedQuery(name = "PagoFacilitador.findAll", query = "SELECT u FROM PagoFacilitador u") })
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "enft_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "enft_status = 'TRUE'")
public class PagoFacilitador extends EntidadBase {

	private static final long serialVersionUID = 260306046385650111L;

	@Id
	@SequenceGenerator(name = "FACILITAITOR_TAXES_PPS_GENERATOR", sequenceName = "seq_enft_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FACILITAITOR_TAXES_PPS_GENERATOR")
	@Column(name = "enft_id")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@Column(name = "enft_code")
	private String codigoUbicacionEspecial;

	@Getter
	@Setter
	@Column(name = "gelo_codification_inec")
	private String codigoInec;

	@Getter
	@Setter
	@Column(name = "enft_facilitator_inside")
	private double pagoDentroUbicacion;

	@Getter
	@Setter
	@Column(name = "enft_facilitator_outside")
	private double pagoFueraUbicacion;

}
