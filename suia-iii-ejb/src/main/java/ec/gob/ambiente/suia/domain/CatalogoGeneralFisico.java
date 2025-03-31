package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import java.io.Serializable;

/**
 * The persistent class for the categories_catalog database table. Frank Torres
 */
@Entity
@Table(name = "general_catalogs_physical", catalog = "", schema = "public")
@AttributeOverrides({@AttributeOverride(name = "estado", column = @Column(name = "gcph_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "gcph_status = 'TRUE'")
@NamedQueries({
        @NamedQuery(name = "CatalogoGeneralFisico.findAll", query = "SELECT c FROM CatalogoGeneral c"),
        @NamedQuery(name = "CatalogoGeneralFisico.findByType", query = "SELECT c FROM CatalogoGeneral c where c.estado = true and c.tipoCatalogo.id = :p_tipoId order by c.id"),
        @NamedQuery(name = "CatalogoGeneralFisico.findByTypeAndCode", query = "SELECT c FROM CatalogoGeneral c where c.estado = true and c.tipoCatalogo.id = :p_tipoId and c.codigo = :p_codigo"),
        @NamedQuery(name = "CatalogoGeneralFisico.findByParent", query = "SELECT c FROM CatalogoGeneral c where c.estado = true and c.padreId = :p_padreId"),
        @NamedQuery(name = "CatalogoGeneralFisico.findByParentOrderById", query = "SELECT c FROM CatalogoGeneral c where c.estado = true and c.padreId = :p_padreId ORDER BY c.id"),
        @NamedQuery(name = "CatalogoGeneralFisico.findByParentAndCode", query = "SELECT c FROM CatalogoGeneral c where c.estado = true and c.padreId = :p_padreId and c.codigo = :p_codigo"),
        @NamedQuery(name = CatalogoGeneralFisico.LISTAR_POR_TIPOID_CODIGO, query = "SELECT c FROM CatalogoGeneral c where c.estado = true and c.tipoCatalogo.id = :p_tipoId and c.codigo = :p_codigo")})
public class CatalogoGeneralFisico extends EntidadBase implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -3947900087722750769L;

    private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
    public static final String LISTAR_POR_TIPOID_CODIGO = PAQUETE
            + "CatalogoGeneralFisico.listarPorTipoIdCodigo";

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
    public static final int OTRO_PREDIO_ACTIVO = 13;
    public static final int MACROINVERTEBRADOS_ACUATICOS = 76;
    public static final String UNIDAD_CALIDAD_AIRE = "μg/m3";

    @Id
    @Getter
    @Setter
    @SequenceGenerator(name = "GENERALCATALOGPH_GCPHID_GENERATOR", sequenceName = "seq_gcph_id", schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GENERALCATALOGPH_GCPHID_GENERATOR")
    @Column(name = "gcph_id", unique = true, nullable = false)
    private Integer id;

    @Getter
    @Setter
    @JoinColumn(name = "gcph_parent_id", referencedColumnName = "gcph_id", nullable = true)
    @ForeignKey(name = "fk_gcph_id_to_gcph_id")
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private CatalogoGeneralFisico catalogoPadre;

    @Getter
    @Setter
    @Column(name = "gcph_parent_id", insertable = false, updatable = false)
    private Integer idPadre;

    @Getter
    @Setter
    @Column(name = "gcph_description", nullable = false, length = 255)
    private String descripcion;

    @Getter
    @Setter
    @Column(name = "gcph_help", nullable = true, length = 655)
    private String ayuda;

    @Getter
    @Setter
    @Column(name = "sector_id", nullable = true, length = 255)
    private Integer sectorId;

    @Getter
    @Setter
    @Column(name = "gcph_weight", nullable = true)
    private Integer orden;

    @Getter
    @Setter
    @Column(name = "gcph_from", nullable = true)
    private Integer valorDesde;

    @Getter
    @Setter
    @Column(name = "gcph_to", nullable = true)
    private Integer valorHasta;

    @Getter
    @Setter
    @JoinColumn(name = "caty_id", referencedColumnName = "caty_id", nullable = false)
    @ForeignKey(name = "fk_caty_id_to_gcph_id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private TipoCatalogo tipoCatalogo;

    @Override
    public String toString() {
        return this.descripcion;
    }

    public CatalogoGeneralFisico() {
    }

    public CatalogoGeneralFisico(Integer id) {
        this.id = id;
    }

    public CatalogoGeneralFisico(Integer id, String descripcion, String ayuda, String codigo, String tipo, Integer tipo_id,
                                 Boolean estado, Integer orden, Integer id_padre, Integer valorDesde, Integer valorHasta, Integer sector) {
        this.id = id;
        this.descripcion = descripcion;
        this.ayuda = ayuda;
        this.tipoCatalogo = new TipoCatalogo();
        this.tipoCatalogo.setCodigo(codigo);
        this.tipoCatalogo.setTipo(tipo);
        this.tipoCatalogo.setId(tipo_id);
        this.estado = estado;
        this.orden = orden;
        this.valorDesde = valorDesde;
        this.valorHasta = valorHasta;
    }

}
