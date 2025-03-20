package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "project_stages", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "prst_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prst_status = 'TRUE'")
public class EtapasProyecto extends EntidadBase{

    private static final long serialVersionUID = 1L;

    @Id
    @Getter
    @Setter
    @Column(name = "prst_id")
    private Integer id;

    @Getter
    @Setter
    @Column(name = "prst_name")
    private String nombre;

    @Getter
    @Setter
    @OneToMany(mappedBy = "etapasProyecto")
    private List<ActividadesPorEtapa> actividadesPorEtapa;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "phas_id")
    @ForeignKey(name = "project_stages_phas_id_fkey")
    private Fase fase;
     /**/

    @Getter
    @Setter
    @OneToMany(mappedBy = "etapasProyecto")
    private List<EvaluacionAspectoAmbiental> evaluacionAspectoAmbientals;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "zbph_id")
    @ForeignKey(name = "project_stages_phas_id_fkey")
    private ZonaPorFase zonaPorFase;

    @Override
    public String toString() {
        return this.nombre;
    }

}
