package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
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
 * Created by msit-erislan on 16/11/2015.
 */
@NamedQueries({
    @NamedQuery(name = TipoSectorFase.FIND_FASES_BY_TIPO_SECTOR,
            query = "select tsf.fase FROM TipoSectorFase tsf where tsf.tipoSector = :tipoSector order by tsf.id"),
})
@Entity
@Table(name = "sector_types_phase", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "stph_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "stph_status = 'TRUE'")
public class TipoSectorFase extends EntidadBase {

    public static final String FIND_FASES_BY_TIPO_SECTOR = "ec.gob.ambiente.suia.domain.TipoSectorFase.findFasesByTipoSector";

    @Getter
    @Setter
    @Id
    @Column(name = "stph_id")
    @SequenceGenerator(name = "SECTOR_TYPE_PHASE_GENERATOR", sequenceName = "sector_types_phase_pkey", schema = "suia_iii", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SECTOR_TYPE_PHASE_GENERATOR")
    private Integer id;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "sety_id")
    @ForeignKey(name = "sector_types_sety_id_fkey")
    private TipoSector tipoSector;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "phas_id")
    @ForeignKey(name = "phases_phas_id_fkey")
    private Fase fase;

    @Getter
    @Setter
    @Column(name = "stph_description", length = 500)
    private String descripcion;

    public TipoSectorFase() {}
}
