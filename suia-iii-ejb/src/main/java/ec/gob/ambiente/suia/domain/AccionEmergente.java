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
import java.util.Date;
import java.util.List;

/**
 * Created by msit-erislan on 16/11/2015.
 */
@NamedQueries({
        @NamedQuery(name = AccionEmergente.FIND_BY_EVENTO_OCURRIDO, query = "select a FROM AccionEmergente a where a.eventoOcurrido = :eventoOcurrido order by a.eventoOcurrido.id"),
        @NamedQuery(name = AccionEmergente.FIND_BY_EVENTOS_OCURRIDOS_AND_TYPE, query = "select a FROM AccionEmergente a where a.eventoOcurrido in :eventosOcurridos " +
                "and a.tipo = :tipo order by a.eventoOcurrido.id")
})
@Entity
@Table(name = "emerging_action", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "emac_status")),
        @AttributeOverride(name = "fechaCreacion", column = @Column(name = "emac_creation_date")),
        @AttributeOverride(name = "fechaModificacion", column = @Column(name = "emac_update_date")),
        @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "emac_creator_user")),
        @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "emac_updater_user")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "emac_status = 'TRUE'")
public class AccionEmergente extends EntidadAuditable {

    public static final Integer YA_IMPLEMENTADA = 1;
    public static final Integer POR_IMPLEMENTAR = 2;
    public static final Integer INFORME_FINAL = 3;
    public static final String FIND_BY_EVENTO_OCURRIDO = "AccionEmergente.findByEventoOcurrido";
    public static final String FIND_BY_EVENTOS_OCURRIDOS_AND_TYPE = "AccionEmergente.findByEventosOcurridos";

    @Getter
    @Setter
    @Id
    @Column(name = "emac_id")
    @SequenceGenerator(name = "EMERGING_ACTION_EMAC_ID_GENERATOR", sequenceName = "emerging_action_emac_id_seq", schema = "suia_iii", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EMERGING_ACTION_EMAC_ID_GENERATOR")
    private Integer id;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "ocev_id")
    @ForeignKey(name = "occurred_event_ocev_id_fkey")
    private EventoOcurrido eventoOcurrido;

    @Getter
    @Setter
    @Column(name = "emac_type")
    private Integer tipo;

    @Getter
    @Setter
    @OneToMany(mappedBy = "accionEmergente")
    @Filter(name = EntidadBase.FILTER_ACTIVE, condition = "come_status = 'TRUE'")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<MedidaCorrectiva> medidaCorrectivas;

    @Getter
    @Setter
    @Column(name = "emac_affected_area")
    private double areaAfectada;

    @Getter
    @Setter
    @Column(name = "emac_area_affected_unit", length = 20)
    private String unidadAreaAfectada;

    @Getter
    @Setter
    @Column(name = "emac_recovered_area")
    private double areaRecuperada;

    @Getter
    @Setter
    @Column(name = "emac_area_recovered_unit", length = 20)
    private String unidadAreaRecuperada;

    @Getter
    @Setter
    @Column(name = "emac_lost_area")
    private double areaPerdida;

    @Getter
    @Setter
    @Column(name = "emac_justification", length = 250)
    private String justificacion;

    @Getter
    @Setter
    @Column(name = "emac_verification_medium", length = 150)
    private String medioVerificacion;

    @Getter
    @Setter
    @Column(name = "emac_responsible", length = 150)
    private String responsable;

    @Temporal(TemporalType.DATE)
    @Getter
    @Setter
    @Column(name = "emac_start_date")
    private Date fechaInicio;

    @Temporal(TemporalType.DATE)
    @Getter
    @Setter
    @Column(name = "emac_end_date")
    private Date fechaFin;

    @Getter
    @Setter
    @Column(name = "emac_presupposition")
    private double presupuesto;

    @Getter
    @Setter
    @OneToMany(mappedBy = "accionEmergente")
    @Filter(name = EntidadBase.FILTER_ACTIVE, condition = "ceac_status = 'TRUE'")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<AccionEmergenteCoordenada> coordenadas;

    @Getter
    @Setter
    @Column(name = "emac_environment_factor", length = 150)
    private String factorAmbiental;

    @Getter
    @Setter
    @Column(name = "emac_environment_factor_specification", length = 500)
    private String especificacionFactorAmbiental;

    @Getter
    @Setter
    @Column(name = "emac_zone", length = 500)
    private String zona;

    public AccionEmergente() {}

    public AccionEmergente(Integer tipo) {
        this.tipo = tipo;
    }

    public AccionEmergente copiar() {
        AccionEmergente accionEmergente = new AccionEmergente();
        accionEmergente.setEventoOcurrido(eventoOcurrido); ;
        accionEmergente.setTipo(tipo);
        accionEmergente.setMedidaCorrectivas(medidaCorrectivas);
        accionEmergente.setAreaAfectada(areaAfectada);
        accionEmergente.setUnidadAreaAfectada(unidadAreaAfectada);
        accionEmergente.setAreaRecuperada(areaRecuperada);
        accionEmergente.setUnidadAreaRecuperada(unidadAreaRecuperada);
        accionEmergente.setAreaPerdida(areaPerdida);
        accionEmergente.setJustificacion(justificacion);
        accionEmergente.setMedioVerificacion(medioVerificacion);
        accionEmergente.setResponsable(responsable);
        accionEmergente.setFechaInicio(fechaInicio);
        accionEmergente.setFechaFin(fechaFin);
        accionEmergente.setPresupuesto(presupuesto);
        return accionEmergente;
    }
}
