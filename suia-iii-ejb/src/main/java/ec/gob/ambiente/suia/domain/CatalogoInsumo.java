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
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the supplie_catalog database table.
 * 
 */
@Entity
@Table(name = "supplie_catalog", schema = "suia_iii")
@NamedQuery(name = "CatalogoInsumo.findAll", query = "SELECT i FROM CatalogoInsumo i")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "suca_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "suca_status = 'TRUE'")
public class CatalogoInsumo extends EntidadBase {

	private static final long serialVersionUID = 4218668740749092817L;

	@Id
	@SequenceGenerator(name = "SUPPLIE_CATALOG_GENERATOR", sequenceName = "supplie_catalog_id_seq", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SUPPLIE_CATALOG_GENERATOR")
	@Column(name = "suca_id")
	@Getter
	@Setter
	private Integer id;

	@Column(name = "suca_supplie_name")
	@Getter
	@Setter
	private String nombreInsumo;

	@Column(name = "suca_supplie_description")
	@Getter
	@Setter
	private String descripcionInsumo;

	// bi-directional many-to-one association to phase
	@ManyToOne
	@JoinColumn(name = "secp_id")
	@ForeignKey(name = "sectors_classifications_phases_supplie_catalog_fk")
	@Getter
	@Setter
	private CatalogoCategoriaFase categoriaFase;

	//Para casos que no se especifica la fase 
	// bi-directional many-to-one association to special_activity
	@ManyToOne
	@JoinColumn(name = "spac_id")
	@ForeignKey(name = "special_activity_supplie_catalog_fk")
	@Getter
	@Setter
	private ActividadEspecial actividadEspecial;

}