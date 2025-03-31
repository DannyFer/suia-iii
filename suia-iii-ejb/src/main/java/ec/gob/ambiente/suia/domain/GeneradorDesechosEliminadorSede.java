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
@Table(name = "hazardous_wastes_eliminators_provider_locations", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "wepl_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wepl_status = 'TRUE'")
public class GeneradorDesechosEliminadorSede extends EntidadBase {

	private static final long serialVersionUID = -3683214053088104152L;

	@Getter
	@Setter
	@Id
	@Column(name = "wepl_id")
	@SequenceGenerator(name = "HAZARDOUS_WASTES_ELIMINATORS_PROVIDER_LOCATIONS_WEPL_ID_GENERATOR", sequenceName = "seq_wepl_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "HAZARDOUS_WASTES_ELIMINATORS_PROVIDER_LOCATIONS_WEPL_ID_GENERATOR")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "hwel_provider")
	private String otraEmpresa;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "hwel_id")
	@ForeignKey(name = "fk_wepl_id_hwel_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "hwel_status = 'TRUE'")
	private GeneradorDesechosEliminador generadorDesechosEliminador;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "hwpl_id")
	@ForeignKey(name = "fk_wepl_id_hwpl_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wepl_status = 'TRUE'")
	private SedePrestadorServiciosDesechos sedePrestadorServiciosDesechos;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "docu_id")
	@ForeignKey(name = "fk_hazardous_wastes_eliminators_provider_locations_documents")
	private Documento permisoAmbiental;
}
