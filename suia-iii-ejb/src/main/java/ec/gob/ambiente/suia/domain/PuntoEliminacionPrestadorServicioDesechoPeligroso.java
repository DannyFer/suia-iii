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

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name = "removal_points_h_w_service_providers", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "rphw_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "rphw_status = 'TRUE'")
public class PuntoEliminacionPrestadorServicioDesechoPeligroso extends EntidadBase {

	private static final long serialVersionUID = 2437247755728368704L;

	@Getter
	@Setter
	@Id
	@Column(name = "rphw_id")
	@SequenceGenerator(name = "REMOVAL_POINTS_LOCATIONS_RPHW_ID_GENERATOR", sequenceName = "seq_rphw_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REMOVAL_POINTS_LOCATIONS_RPHW_ID_GENERATOR")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "remp_id")
	@ForeignKey(name = "fk_rphw_id_remp_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "remp_status = 'TRUE'")
	private PuntoEliminacion puntoEliminacion;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "hwpw_id")
	@ForeignKey(name = "fk_rphw_id_hwpw_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "hwpw_status = 'TRUE'")
	private PrestadorServiciosDesechoPeligroso prestadorServiciosDesechoPeligroso;

	@Getter
	@Setter
	@Column(name = "repl_quantity_tons")
	private double cantidadToneladas;

	@Getter
	@Setter
	@Column(name = "repl_quantity_units")
	private double cantidadUnidades;
}
