package ec.gob.ambiente.suia.domain;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name = "zone_by_phase", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "zbph_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "zbph_status = 'TRUE'")
public class ZonaPorFase extends EntidadBase{

    private static final long serialVersionUID = 1L;

    @Id
    @Getter
    @Setter
    @SequenceGenerator(name = "ZONA_POR_FASE_ID_GENERATOR", initialValue = 1, sequenceName = "seq_zbph_id", schema = "suia_iii", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ZONA_POR_FASE_ID_GENERATOR")
    @Column(name = "zbph_id")
    private Integer id;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "phas_id")
    @ForeignKey(name = "zone_by_phase_phas_id_fkey")
    private Fase fase;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "phzo_id")
    @ForeignKey(name = "zone_by_phase_phzo_id_fkey")
    private ZonaPorFase zonaPorFase;

    @Getter
    @Setter
    @OneToMany(mappedBy = "zonaPorFase")
    private List<EtapasProyecto> etapasProyectos;
}
