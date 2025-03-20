package ec.gob.ambiente.suia.domain;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * Created by Denis Linares on 13/11/15.
 */
@Entity
@Table(name = "comparation_sampling_points_eia", schema = "suia_iii")
@NamedQueries({ @NamedQuery(name = ComparacionPuntosMuestreoEIA.LISTAR_POR_MUESTRA_A, query = "SELECT c FROM ComparacionPuntosMuestreoEIA c WHERE c.estado = true and c.muestraA.id = :idMuestraA") })
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "copo_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "copo_status = 'TRUE'")
public class ComparacionPuntosMuestreoEIA extends EntidadBase{
    private static final long serialVersionUID = 273184035787987760L;

    private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
    public static final String LISTAR_POR_MUESTRA_A = PAQUETE + "ComparacionPuntosMuestreoEIA.listar";

    @Id
    @SequenceGenerator(name = "COPO_GENERATOR", schema = "suia_iii", sequenceName = "seq_copo_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COPO_GENERATOR")
    @Column(name = "copo_id")
    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    @JoinColumn(name = "copo_sample_a_id", referencedColumnName = "sapo_id")
    @ForeignKey(name = "fk_copo_sample_a_id_sampling_points_eia_sapo_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private PuntosMuestreoEIA muestraA;

    @Getter
    @Setter
    @JoinColumn(name = "copo_sample_b_id", referencedColumnName = "sapo_id")
    @ForeignKey(name = "fk_copo_sample_b_id_sampling_points_eia_sapo_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private PuntosMuestreoEIA muestraB;

    @Getter
    @Setter
    @Column(name = "copo_jaccard")
    private Double jaccard;

    @Getter
    @Setter
    @Column(name = "copo_sorensen")
    private Double sorensen;

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.muestraA.getNombrePunto()+"";
    }
}
