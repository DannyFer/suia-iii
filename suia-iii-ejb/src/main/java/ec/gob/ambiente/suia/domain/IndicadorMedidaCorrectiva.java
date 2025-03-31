package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;

/**
 * Created by msit-erislan on 16/11/2015.
 */
@NamedQueries({
        @NamedQuery(name = IndicadorMedidaCorrectiva.FIND_BY_MEDIDA_CORRECTIVA, query = "select i FROM IndicadorMedidaCorrectiva i where i.medidaCorrectiva = :medidaCorrectiva order by i.id")
})
@Entity
@Table(name = "corrective_mean_indicator", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "comi_status")),
        @AttributeOverride(name = "fechaCreacion", column = @Column(name = "comi_creation_date")),
        @AttributeOverride(name = "fechaModificacion", column = @Column(name = "comi_update_date")),
        @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "comi_creator_user")),
        @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "comi_updater_user")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "come_status = 'TRUE'")
public class IndicadorMedidaCorrectiva extends EntidadAuditable {

    public static final String FIND_BY_MEDIDA_CORRECTIVA = "ec.gob.ambiente.suia.domain.IndicadorMedidaCorrectiva.findByMedidaCorrectiva";

    @Getter
    @Setter
    @Id
    @Column(name = "comi_id")
    @SequenceGenerator(name = "CORRECTIVE_MEAN_INDICATOR_COMI_ID_GENERATOR", sequenceName = "corrective_mean_indicator_comi_id_seq", schema = "suia_iii", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CORRECTIVE_MEAN_INDICATOR_COMI_ID_GENERATOR")
    private Integer id;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "come_id")
    @ForeignKey(name = "corrective_mean_come_id_fkey")
    private MedidaCorrectiva medidaCorrectiva;

    @Getter
    @Setter
    @Column(name = "comi_description", length = 100)
    private String descripcion;

    public IndicadorMedidaCorrectiva copiar() {
        IndicadorMedidaCorrectiva indicadorMedidaCorrectiva = new IndicadorMedidaCorrectiva();
        indicadorMedidaCorrectiva.setMedidaCorrectiva(medidaCorrectiva);
        indicadorMedidaCorrectiva.setDescripcion(descripcion);
        return indicadorMedidaCorrectiva;
    }
}
