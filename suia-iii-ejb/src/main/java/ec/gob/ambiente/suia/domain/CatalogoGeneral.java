package ec.gob.ambiente.suia.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
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
 * The persistent class for the categories_catalog database table.
 * 
 */
@Entity
@Table(name = "general_catalogs", catalog = "", schema = "public")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "geca_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "geca_status = 'TRUE'")
@NamedQueries({
    @NamedQuery(name = "CatalogoGeneral.findAll", query = "SELECT c FROM CatalogoGeneral c"),
    @NamedQuery(name = "CatalogoGeneral.findByType", query = "SELECT c FROM CatalogoGeneral c where c.estado = true and c.tipoCatalogo.id = :p_tipoId order by c.id"),
    @NamedQuery(name = "CatalogoGeneral.findByTypeOrderByDescription", query = "SELECT c FROM CatalogoGeneral c where c.estado = true and c.tipoCatalogo.id = :p_tipoId order by c.descripcion"),
    @NamedQuery(name = "CatalogoGeneral.findByTypeAndCode", query = "SELECT c FROM CatalogoGeneral c where c.estado = true and c.tipoCatalogo.id = :p_tipoId and c.codigo = :p_codigo"),
    @NamedQuery(name = "CatalogoGeneral.findByParent", query = "SELECT c FROM CatalogoGeneral c where c.estado = true and c.padreId = :p_padreId"),
    @NamedQuery(name = "CatalogoGeneral.findByParentOrderById", query = "SELECT c FROM CatalogoGeneral c where c.estado = true and c.padreId = :p_padreId ORDER BY c.id"),
    @NamedQuery(name = "CatalogoGeneral.findByParentOrderByDescripcion", query = "SELECT c FROM CatalogoGeneral c where c.estado = true and c.padreId = :p_padreId ORDER BY c.descripcion"),
    @NamedQuery(name = "CatalogoGeneral.findByParentAndCode", query = "SELECT c FROM CatalogoGeneral c where c.estado = true and c.padreId = :p_padreId and c.codigo = :p_codigo"),
    @NamedQuery(name = CatalogoGeneral.LISTAR_POR_TIPOID_CODIGO, query = "SELECT c FROM CatalogoGeneral c where c.estado = true and c.tipoCatalogo.id = :p_tipoId and c.codigo = :p_codigo")})
	
public class CatalogoGeneral extends EntidadBase implements Serializable {

	/**
     *
     */
    private static final long serialVersionUID = -3947900087722750769L;
   
	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR_POR_TIPOID_CODIGO = PAQUETE
			+ "CatalogoGeneral.listarPorTipoIdCodigo";

	public static final int CUALITATIVO = 15;
	public static final int CUANTITATIVO = 16;
	public static final int CUALITATIVO_CUANTITATIVO = 17;
	public static final int OTROS_RASTROS_TIPO_REGISTRO = 83;
	public static final int OTROS_DISTRIBUCION_VERTICAL_ESPECIE = 96;
	public static final int OTROS_COMPORTAMIENTO_SOCIAL = 99;
	public static final int OTROS_GREMIO_ALIMENTICIO = 106;
	public static final String FAUNA_CODIGO = "FAUNA";
	public static final String FLORA_CODIGO = "FLORA";
	public static final int MASTOFAUNA = 71;
	public static final int ORNITOFAUNA = 72;
	public static final int HERPETOFAUNA = 73;
	public static final int ENTOMOFAUNA = 74;
	public static final int ICTIOFAUNA = 75;
	public static final int MACROINVERTEBRADOS_ACUATICOS = 76;
	public static final String UNIDAD_CALIDAD_AIRE = "μg/m3";

	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "WASTE_DANGEROUS_TYPE_GECAID_GENERATOR", sequenceName = "seq_geca_id", schema = "public")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WASTE_DANGEROUS_TYPE_GECAID_GENERATOR")
	@Column(name = "geca_id", unique = true, nullable = false)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "geca_parent_id", nullable = true, length = 255)
	private Integer padreId;

	@Getter
	@Setter
	@Column(name = "geca_description", nullable = false, length = 255)
	private String descripcion;

	@Getter
	@Setter
	@Column(name = "geca_code", nullable = true, length = 255)
	private String codigo;

    @Getter
    @Setter
    @Column(name = "sector_id", nullable = true, length = 255)
    private Integer sectorId;
    
    @Getter
    @Setter
    @Column(name = "geca_value", nullable = true, length = 255)
    private String valor;

    @Getter
    @Setter
    @JoinColumn(name = "caty_id", referencedColumnName = "caty_id", nullable = false)
    @ForeignKey(name = "fk_caty_id_to_geca_id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private TipoCatalogo tipoCatalogo;
    
	@Getter
	@Setter
	@OneToMany(mappedBy = "catalogoGeneral")
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<ProyectoGeneralCatalogo> proyectoGeneralCatalogo;

	@OneToMany(mappedBy = "catalogoGeneral")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "faar_status = 'TRUE'")
	@Getter
	@Setter
	private List<Articulo> articulos;

	@Getter
	@Setter
	@Transient
	private boolean seleccionado;

	@Override
	public String toString() {
		return this.descripcion;
	}

	public CatalogoGeneral() {
	}

	public CatalogoGeneral(Integer id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CatalogoGeneral)) {
			return false;
		}
		CatalogoGeneral other = (CatalogoGeneral) obj;
		if (((this.id == null) && (other.id != null)) || ((this.id != null) && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}
}
