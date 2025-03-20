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
 * The persistent class for the tool_catalog database table.
 * 
 */
@Entity
@Table(name = "tool_catalog", schema = "suia_iii")
@NamedQuery(name = "CatalogoHerramienta.findAll", query = "SELECT h FROM CatalogoHerramienta h")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "toca_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "toca_status = 'TRUE'")
public class CatalogoHerramienta extends EntidadBase {

	private static final long serialVersionUID = 4218664800749092817L;

	@Id
	@SequenceGenerator(name = "TOOL_CATALOG_GENERATOR", sequenceName = "tool_catalog_id_seq", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TOOL_CATALOG_GENERATOR")
	@Column(name = "toca_id")
	@Getter
	@Setter
	private Integer id;

	@Column(name = "toca_tool_name")
	@Getter
	@Setter
	private String nombreHerramienta;

	@Column(name = "toca_tool_description")
	@Getter
	@Setter
	private String descripcionHerramienta;

	// bi-directional many-to-one association to sectors_classifications_phases
	@ManyToOne
	@JoinColumn(name = "secp_id")
	@ForeignKey(name = "sectors_classifications_phases_tool_catalog_fk")
	@Getter
	@Setter
	private CatalogoCategoriaFase categoriaFase;

	//Para casos que no se especifica la fase 
	// bi-directional many-to-one association to special_activity
	@ManyToOne
	@JoinColumn(name = "spac_id")
	@ForeignKey(name = "special_activity_tool_catalog_fk")
	@Getter
	@Setter
	private ActividadEspecial actividadEspecial;

}