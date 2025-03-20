package ec.gob.ambiente.suia.domain;

import java.util.Date;
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
@NamedQueries(
        @NamedQuery(name = ActividadesPorEtapa.FIND_BY_ESTUDIO, query = "select a from ActividadesPorEtapa a where a.estudioImpactoAmbiental = :estudio AND a.idHistorico = null order by a.id")
)
@Entity
@Table(name = "stage_activities", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "stac_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "stac_status = 'TRUE'")
public class ActividadesPorEtapa extends EntidadBase{

    private static final long serialVersionUID = 1L;
    public static final String FIND_BY_ESTUDIO = "ActividadesPorEtapa.findByEstudio";

    @Id
    @Getter
    @Setter
    @SequenceGenerator(name = "ACTIVIDADES_POR_ETAPA_ID_GENERATOR", initialValue = 1, sequenceName = "seq_stac_id", schema = "suia_iii", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ACTIVIDADES_POR_ETAPA_ID_GENERATOR")
    @Column(name = "stac_id")
    private Integer id;

    @Getter
    @Setter
    @Column(name = "stac_description")
    private String descripcion;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "prst_id")
    @ForeignKey(name = "stage_activities_prst_id_fkey")
    private EtapasProyecto etapasProyecto;

    @Getter
    @Setter
    @OneToMany(mappedBy = "actividadesPorEtapa")
    private List<EvaluacionAspectoAmbiental> evaluacionAspectoAmbientals;

    @ManyToOne
    @JoinColumn(name = "docu_id")
    @ForeignKey(name = "stage_activities_docu_id_fkey")
    @Getter
    @Setter
    private Documento coordenadaActividad;

    @Getter
    @Setter
    @Column(name = "start_date")
    private Date fechaInicio;

    @Getter
    @Setter
    @Column(name = "end_date")
    private Date fechaFin;
    
    @Getter
    @Setter
    @Column(name = "stac_historical_id")
    private Integer idHistorico;
    
    @Getter
    @Setter
    @Column(name = "stac_notification_number")
    private Integer numeroNotificacion;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "eist_id")
    @ForeignKey(name = "environmental_impact_studies_eist_id_fk")
    private EstudioImpactoAmbiental estudioImpactoAmbiental;

}
