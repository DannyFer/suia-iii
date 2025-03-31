package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;

import java.io.Serializable;
import java.util.List;

/**
 * The persistent class for the categories_catalog database table. Frank Torres
 */
@Entity
@Table(name = "general_catalogs_biotic", catalog = "", schema = "public")
@AttributeOverrides({@AttributeOverride(name = "estado", column = @Column(name = "gcbi_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "gcbi_status = 'TRUE'")
@NamedQueries({
        @NamedQuery(name = "CatalogoGeneralBiotico.findAll", query = "SELECT c FROM CatalogoGeneralBiotico c"),
        @NamedQuery(name = "CatalogoGeneralBiotico.findByType", query = "SELECT c FROM CatalogoGeneralBiotico c where c.estado = true and c.tipoCatalogo.id = :p_tipoId order by c.id"),
        @NamedQuery(name = "CatalogoGeneralBiotico.findByParent", query = "SELECT c FROM CatalogoGeneralBiotico c where c.estado = true and c.catalogoPadre.id = :p_padreId"),
        @NamedQuery(name = "CatalogoGeneralBiotico.findByParentOrderById", query = "SELECT c FROM CatalogoGeneralBiotico c where c.estado = true and c.catalogoPadre.id = :p_padreId ORDER BY c.id")})
public class CatalogoGeneralBiotico extends EntidadBase implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -3947900087722750769L;

    private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
    public static final String LISTAR_POR_TIPOID_CODIGO = PAQUETE
            + "CatalogoGeneralBiotico.listarPorTipoIdCodigo";

    @Id
    @Getter
    @Setter
    @SequenceGenerator(name = "GENERALCATALOGPH_GCBIID_GENERATOR", sequenceName = "seq_gcbi_id", schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GENERALCATALOGPH_GCBIID_GENERATOR")
    @Column(name = "gcbi_id", unique = true, nullable = false)
    private Integer id;

    @Getter
    @Setter
    @JoinColumn(name = "gcbi_parent_id", referencedColumnName = "gcbi_id", nullable = true)
    @ForeignKey(name = "fk_gcbi_id_to_gcbi_id")
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private CatalogoGeneralBiotico catalogoPadre;

    @Getter
    @Setter
    @Column(name = "gcbi_description", nullable = false, length = 255)
    private String descripcion;

    @Getter
    @Setter
    @Column(name = "gcbi_help", nullable = true, length = 655)
    private String ayuda;

    @Getter
    @Setter
    @Column(name = "sector_id", nullable = true, length = 255)
    private Integer sectorId;

    @Getter
    @Setter
    @Column(name = "gcbi_weight", nullable = true)
    private Integer orden;

    @Getter
    @Setter
    @JoinColumn(name = "caty_id", referencedColumnName = "caty_id", nullable = false)
    @ForeignKey(name = "fk_caty_id_to_gcbi_id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private TipoCatalogo tipoCatalogo;

    @Override
    public String toString() {
        return this.descripcion;
    }

    public CatalogoGeneralBiotico() {

    }

    public CatalogoGeneralBiotico(Integer id) {
        this.id = id;
    }
    
    @Getter
	@Setter
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "catalogoPadre")
	private List<CatalogoGeneralBiotico> catalogoGeneralBioticoList;


    public CatalogoGeneralBiotico(Integer id, String descripcion, String ayuda, String codigo, String tipo, Integer tipo_id,
                                  Boolean estado, Integer orden, Integer id_padre, Integer sector) {
        this.id = id;
        this.descripcion = descripcion;
        this.ayuda = ayuda;
        this.tipoCatalogo = new TipoCatalogo();
        this.tipoCatalogo.setCodigo(codigo);
        this.tipoCatalogo.setTipo(tipo);
        this.tipoCatalogo.setId(tipo_id);
        this.estado = estado;
        this.orden = orden;

    }
}
