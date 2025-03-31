package ec.gob.ambiente.suia.domain;

import java.util.List;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name = "categories_catalog_system", catalog = "", schema = "suia_iii")
@NamedQueries({
		@NamedQuery(name = "CatalogoCategoriaSistema.findAll", query = "SELECT c FROM CatalogoCategoriaSistema c"),
		@NamedQuery(name = CatalogoCategoriaSistema.FIND_ALL_BY_TIPO_SECTOR, query = "select c from CatalogoCategoriaSistema c where c.catalogoCategoriaPublico.tipoSector = :tipoSector")
})
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "cacs_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "cacs_status = 'TRUE'")
public class CatalogoCategoriaSistema extends EntidadBase {

	private static final long serialVersionUID = -7277474977154968148L;

	public static final String FIND_ALL = "ec.com.magmasoft.business.domain.CatalogoCategoriaSistema.findAll";
	public static final String FIND_ALL_BY_TIPO_SECTOR = "ec.com.magmasoft.business.domain.CatalogoCategoriaSistema.findAllByTipoSector";

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "CATEGORIES_CATALOG_SISTEM_CACSID_GENERATOR", sequenceName = "seq_cacs_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CATEGORIES_CATALOG_SISTEM_CACSID_GENERATOR")
	@Column(name = "cacs_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "cacs_code", nullable = false, length = 255)
	private String codigo;

	@Getter
	@Setter
	@Column(name = "cacs_description", length = 400)
	private String descripcion;

	@Getter
	@Setter
	@Column(name = "cacs_strategic")
	private Boolean estrategico;
	
	@Getter
	@Setter
	@Column(name = "cacs_is_shrimp")
	private Boolean camoreneraList;

	@Getter
	@Setter
	@Column(name = "cacs_administrative_services_pay")
	private Boolean pagoServiciosAdministrativos;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "cacs_parent_id")
	@ForeignKey(name = "fk_categories_cscacs_idcategories_cscacs_parent_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "cacs_status = 'TRUE'")
	private CatalogoCategoriaSistema categoriaSistema;

	@Getter
	@Setter
	@OneToMany(mappedBy = "categoriaSistema")
	private List<CatalogoCategoriaSistema> categoriaSistemaHijos;

	@Getter
	@Setter
	@JoinColumn(name = "cacp_id")
	@ManyToOne
	@ForeignKey(name = "fk_categories_cscacs_id_categories_cpcacp_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "cacp_status = 'TRUE'")
	private CatalogoCategoriaPublico catalogoCategoriaPublico;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "rank_id")
	@ForeignKey(name = "fk_categories_catalog_sistemcacs_id_rankrank_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "rank_status = 'TRUE'")
	private Rango rango;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "cate_id")
	private Categoria categoria;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "phas_id")
	@ForeignKey(name = "fk_categories_catalog_sistemcacs_id_phsesphas_id")
	private Fase fase;

	@Getter
	@Setter
	@JoinColumn(name = "arty_id", referencedColumnName = "arty_id")
	@ManyToOne
	@ForeignKey(name = "fk_categories_cscacs_id_areas_typesarty_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "arty_status = 'TRUE'")
	private TipoArea tipoArea;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "lity_id")
	@ForeignKey(name = "fk_categories_catalog_sistemcacs_id_licensetypeslity_id")
	private TipoLicenciamiento tipoLicenciamiento;

	@Getter
	@Setter
	@JoinColumn(name = "secl_id", referencedColumnName = "secl_id")
	@ManyToOne
	@ForeignKey(name = "fk_categories_cscacs_id_subsector_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "secl_status = 'TRUE'")
	private TipoSubsector tipoSubsector;

	@Getter
	@Setter
	@Column(name = "cacs_practices_guide", length = 255)
	private String guiaBuenasPracticas;

	@Getter
	@Setter
	@Column(name = "cacs_environmental_files", length = 255)
	private String formatoRegistroAmbiental;

	@Getter
	@Setter
	@Column(name = "cacs_air_quality_specific_requirement", nullable = true)
	private Boolean requerimientoEspecíficoCalidadAire;
	
	@Getter
	@Setter
	@Column(name = "cacs_viability")
	private Boolean requiereViabilidad;

    @Getter
    @Setter
    @Column(name = "cacs_waste_generated")
    private Boolean generaDesechos;

	@Getter
	@Setter
	@Column(name = "cacs_manages_hazardous_waste")
	private Boolean gestionaDesechosPeligrosos;

	@Getter
	@Setter
	@Column(name = "cacs_uses_chemical_substances")
	private Boolean utilizaSustaciasQuimicas;

	@Getter
	@Setter
	@Column(name = "cacs_mtop")
	private Boolean actividadMtop;
	
	@Getter
	@Setter
	@Column(name = "cacs_transpor_hazardous_chemicals")
	private Boolean transportaSustanciasQuimicasPeligrosos;
	
	@Getter
	@Setter
	@Column(name = "cacs_financed_by_the_state")
	private Boolean actividadFinanciadaEstado;
	

	public boolean isCategoriaFinal() {
		return categoriaSistemaHijos == null || categoriaSistemaHijos.isEmpty();
	}

	@Override
	public String toString() {
		return this.codigo + " | " + this.descripcion;
	}

}