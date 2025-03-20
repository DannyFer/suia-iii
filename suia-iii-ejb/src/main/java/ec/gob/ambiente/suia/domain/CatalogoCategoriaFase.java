package ec.gob.ambiente.suia.domain;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "sectors_classifications_phases", schema = "suia_iii")
@NamedQueries({
		@NamedQuery(name = "CatalogoCategoria.findByCategory", query = "SELECT c FROM CatalogoCategoriaFase c where c.estado = true and c.tipoSubsector.id =:p_idCategory order by c.id"),
		@NamedQuery(name = CatalogoCategoriaFase.LISTAR_POR_SUBSECTOR_LA, query = "SELECT c FROM CatalogoCategoriaFase c where c.estado = true and c.tipoSubsector = null"),
		@NamedQuery(name = CatalogoCategoriaFase.LISTAR_POR_SUBSECTOR_CODIGO, query = "SELECT c FROM CatalogoCategoriaFase c where c.estado = true and c.tipoSubsector.codigo = :p_codigo order by c.tipoSubsector.nombre"),
		@NamedQuery(name = CatalogoCategoriaFase.LISTAR_POR_ESTUDIO, query = "SELECT DISTINCT a.catalogoCategoriaFase FROM ActividadLicenciamiento a where a.estudioImpactoAmbiental = :paramEstudio and a.idHistorico = null")})
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "secp_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "secp_status = 'TRUE'")
public class CatalogoCategoriaFase extends EntidadBase {

	private static final long serialVersionUID = 4550228153568889626L;

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR_POR_SUBSECTOR_CODIGO = PAQUETE
			+ "CatalogoCategoriaFase.listarPorSubsectorCodigo";
	public static final String LISTAR_POR_SUBSECTOR_LA = PAQUETE
			+ "CatalogoCategoriaFase.listarPorLA";
	public static final String LISTAR_POR_ESTUDIO = PAQUETE + "CatalogoCategoriaFase.listarPorEstudio";

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "SECTORS_CLASSIFICATIONS_PHASES_ID_GENERATOR", sequenceName = "seq_secp_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SECTORS_CLASSIFICATIONS_PHASES_ID_GENERATOR")
	@Column(name = "secp_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "phas_id", referencedColumnName = "phas_id")
	@ForeignKey(name = "fk_categories_catalog_phases_phas_id_phase_phas_id")
	private Fase fase;

	@Getter
	@Setter
	@JoinColumn(name = "secl_id", referencedColumnName = "secl_id")
	@ManyToOne(fetch = FetchType.LAZY)
	@ForeignKey(name = "fk_categories_cscacs_id_subsector_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "secl_status = 'TRUE'")
	private TipoSubsector tipoSubsector;


	@Getter
	@Setter
	@OneToMany(mappedBy = "catalogoCategoriaFase")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "acli_status = 'TRUE'")
	private List<ActividadLicenciamiento> actividadLicenciamientos;

	@Override
	public String toString() {
		if (this.fase != null) {
			return this.fase.getNombre();
		} else {
			return "CatalogoCategoriaFase [id=" + id + ", catalogoCategoria="
					+ tipoSubsector + ", fase=" + fase + "]";
		}
	}
	
	/**
	 * Cris F: aumento de variable
	 */
	@Getter
	@Setter
	@Transient
	private String nuevoEnModificacion;
	
	@Getter
	@Setter
	@Transient
	private Date fechaHistorico;

}
