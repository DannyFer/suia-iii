package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by msit-erislan on 16/11/2015.
 */
@NamedQueries({
        @NamedQuery(name = MedidaCorrectiva.FIND_BY_ACCION_EMERGENTE, query = "select m FROM MedidaCorrectiva m where m.accionEmergente = :accionEmergente order by m.id")
})
@Entity
@Table(name = "corrective_mean", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "come_status")),
        @AttributeOverride(name = "fechaCreacion", column = @Column(name = "come_creation_date")),
        @AttributeOverride(name = "fechaModificacion", column = @Column(name = "come_update_date")),
        @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "come_creator_user")),
        @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "come_updater_user")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "come_status = 'TRUE'")
public class MedidaCorrectiva extends EntidadAuditable {

    public static final String FIND_BY_ACCION_EMERGENTE = "ec.gob.ambiente.suia.domain.MedidaCorrectiva.findByAccionEmergente";

    @Getter
    @Setter
    @Id
    @Column(name = "come_id")
    @SequenceGenerator(name = "CORRECTIVE_MEAN_COME_ID_GENERATOR", sequenceName = "corrective_mean_come_id_seq", schema = "suia_iii", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CORRECTIVE_MEAN_COME_ID_GENERATOR")
    private Integer id;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "emac_id")
    @ForeignKey(name = "emerging_action_emac_id_fkey")
    private AccionEmergente accionEmergente;

    @Getter
    @Setter
    @Column(name = "come_description", length = 200)
    private String descripcion;

    @Getter
    @Setter
    @OneToMany(mappedBy = "medidaCorrectiva")
    @Filter(name = EntidadBase.FILTER_ACTIVE, condition = "comi_status = 'TRUE'")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<IndicadorMedidaCorrectiva> indicadores;

    public MedidaCorrectiva() {
        indicadores = new ArrayList<IndicadorMedidaCorrectiva>();
    }

    public MedidaCorrectiva copiar() {
        MedidaCorrectiva medidaCorrectiva = new MedidaCorrectiva();
        medidaCorrectiva.setAccionEmergente(accionEmergente);
        medidaCorrectiva.setDescripcion(descripcion);
        medidaCorrectiva.setIndicadores(indicadores);
        return medidaCorrectiva;
    }
}
