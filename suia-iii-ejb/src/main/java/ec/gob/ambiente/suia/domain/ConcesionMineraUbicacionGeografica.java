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
 * <b> Entity que representa la tabla clousure_plans_documents. </b>
 * 
 * @author carlos.pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: carlos.pupo, Fecha: 07/01/2015]
 *          </p>
 */
@Entity
@Table(name = "mining_grants_locations", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "mglo_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "mglo_status = 'TRUE'")
public class ConcesionMineraUbicacionGeografica extends EntidadBase {

	private static final long serialVersionUID = 3771248868636908112L;

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "MINING_GRANTS_LOCATIONS_ID_GENERATOR", sequenceName = "seq_mglo_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MINING_GRANTS_LOCATIONS_ID_GENERATOR")
	@Column(name = "mglo_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "migr_id")
	@ForeignKey(name = "fk_mining_grantsmglo_id_mining_grantsmigr_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "migr_status = 'TRUE'")
	private ConcesionMinera concesionMinera;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "gelo_id")
	@ForeignKey(name = "fk_mining_grantsmglo_id_geographical_locationsgelo_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "gelo_status = 'TRUE'")
	private UbicacionesGeografica ubicacionesGeografica;

}
