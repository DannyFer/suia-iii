package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;

/**
 * <b> Entity que representa la tabla areas. </b>
 *
 * @author pganan
 * @version Revision: 1.0
 *          <p>
 *          [Autor: pganan, Fecha: 22/12/2014]
 *          </p>
 */
@Entity
@Table(name = "categories_catalogs_facilitators_rules", schema = "suia_iii")
@NamedQueries({
        @NamedQuery(name = ReglaFacilitadoresCatalogoCategoria.BUSCAR_REGLA, query = "SELECT r FROM ReglaFacilitadoresCatalogoCategoria r WHERE r.tipoEstudio.id =:idEstudio and r.catalogoCategoria.id = :idCategoria and r.valor = true")
})
@AttributeOverrides({@AttributeOverride(name = "estado", column = @Column(name = "ccfr_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "ccfr_status = 'TRUE'")
public class ReglaFacilitadoresCatalogoCategoria extends EntidadBase {

    private static final long serialVersionUID = -1756612644167548934L;

    private static final String PAQUETE_CLASE = "ec.gob.ambiente.suia.domain.ReglaFacilitadoresCatalogoCategoria.";
    public static final String BUSCAR_REGLA = PAQUETE_CLASE + "buscarRegla";

    @Getter
    @Setter
    @Column(name = "ccfr_id")
    @Id
    @SequenceGenerator(name = "REGLA_FACILITADORES_CATALOGO_CATEGORIA_GENERATOR", initialValue = 1, sequenceName = "seq_ccfr_id", schema = "suia_iii")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REGLA_FACILITADORES_CATALOGO_CATEGORIA_GENERATOR")
    private Integer id;

    @Getter
    @Setter
    @Column(name = "ccfr_value")
    private boolean valor;


    @ManyToOne
    @JoinColumn(name = "cacs_id")
    @ForeignKey(name = "fk_cat_catalogs_fac_rule_cacs_id_categoriescatalogscacs_id")
    @Getter
    @Setter
    private CatalogoCategoriaSistema catalogoCategoria;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "stty_id")
    @ForeignKey(name = "fk_cat_catalogs_fac_rule_stty_id_study_typesstty_id")
    private TipoEstudio tipoEstudio;

}
