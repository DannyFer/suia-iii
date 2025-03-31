package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import java.util.Date;

@NamedQueries({

        @NamedQuery(name = InformeTecnicoTDRLA.OBTENER_INFORME_TECNICO_TDR_POR_PROYECTO,
                query = "select i from InformeTecnicoTDRLA i where i.proyectoLicenciamientoAmbientalId = :p_proyecto_id and i.tipoDocumentoId= :p_tipoDocumentoId"
        )})
@Entity
@Table(name = "technical_report_tdr_la", schema = "suia_iii")
@AttributeOverrides({
        @AttributeOverride(name = "estado", column = @Column(name = "trtl_status")),
        @AttributeOverride(name = "fechaCreacion", column = @Column(name = "trtl_creation_date")),
        @AttributeOverride(name = "fechaModificacion", column = @Column(name = "trtl_date_update")),
        @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "trtl_creator_user")),
        @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "trtl_user_update"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "trtl_status = 'TRUE'")
public class InformeTecnicoTDRLA extends EntidadAuditable {

    private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";

    public static final String OBTENER_INFORME_TECNICO_TDR_POR_PROYECTO = PAQUETE + "InformeTecnicoTDRLA.InformeTecnicoTDRLAPorProyecto";
    private static final long serialVersionUID = 6466773467596265347L;

    @Id
    @SequenceGenerator(name = "technical_report_tdr_la_id_generator", initialValue = 1, sequenceName = "seq_trtl_id", schema = "suia_iii", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "technical_report_tdr_la_id_generator")
    @Column(name = "trtl_id")
    @Getter
    @Setter
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "doty_id")
    @ForeignKey(name = "fk_trtl_id_doty_id")
    @Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tydo_status = 'TRUE'")
    @Getter
    @Setter
    private TipoDocumento tipoDocumento;


    @Getter
    @Setter
    @OneToOne
    @ForeignKey(name = "technical_report_tdr_lapren_id_projects_environmental_licensing_pren_id")
    @JoinColumn(name = "pren_id")
    private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental;

    @Getter
    @Setter
    @Column(name = "pren_id", updatable = false, insertable = false)
    private Integer proyectoLicenciamientoAmbientalId;

    @Getter
    @Setter
    @Column(name = "doty_id", updatable = false, insertable = false)
    private Integer tipoDocumentoId;


    @Getter
    @Setter
    @Column(name = "trtl_office_number")
    private String numeroOficio;

    @Getter
    @Setter
    @Column(name = "trtl_project_features")
    private String caracteristicasImportantes;

    @Getter
    @Setter
    @Column(name = "trtl_observations")
    private String comentarioObservaciones;


    @Getter
    @Setter
    @Column(name = "trtl_conclusions_recommendations")
    private String conclusionesRecomendaciones;

    @Getter
    @Setter
    @Column(name = "trtl_pronouncing")
    private String pronunciamiento;

    @Getter
    @Setter
    @Column(name = "trtl_request_office_number")
    private String numeroOficioSolicitud;

    @Getter
    @Setter
    @Column(name = "trtl_request_office_date")
    private Date fechaOficioSolicitud;


    @Getter
    @Setter
    @Column(name = "trtl_jm_office_number")
    private String numeroOficioMJ;

    @Getter
    @Setter
    @Column(name = "trtl_jm_office_date")
    private Date fechaOficioMJ;


    //-----
    @Getter
    @Setter
    @Transient
    private String fichaTecnica;
    @Getter
    @Setter
    @Transient
    private String fechaProyecto;
    @Getter
    @Setter
    @Transient
    private String nombrePromotor;
    @Getter
    @Setter
    @Transient
    private String nombreProyecto;
    @Getter
    @Setter
    @Transient
    private String codigoProyecto;
    @Getter
    @Setter
    @Transient
    private String provinciaProyecto;
    @Getter
    @Setter
    @Transient
    private String intersecaProyecto;
    @Getter
    @Setter
    @Transient
    private String coordenadasProyecto;
    @Getter
    @Setter
    @Transient
    private String intersecaZIZA;
    @Getter
    @Setter
    @Transient
    private String fechaCoordenadaProyecto;
    @Getter
    @Setter
    @Transient
    private String numeroOficioInterseccion;
    @Getter
    @Setter
    @Transient
    private String fechaOficioInterseccion;
    @Getter
    @Setter
    @Transient
    private String elaboradoPor;
}
