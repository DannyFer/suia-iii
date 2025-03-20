package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;

@NamedQueries({
        @NamedQuery(name = OficioAprobacionTDRLA.OBTENER_OFICIO_TDR_POR_PROYECTO,
                query = "select o from OficioAprobacionTDRLA o where o.proyectoLicenciamientoAmbientalId = :p_proyecto_id  "
                        + "and o.tipoDocumentoId = :p_tipoDocumentoId")})
@Entity
@Table(name = "job_approval_tdr_la", schema = "suia_iii")
@AttributeOverrides({
        @AttributeOverride(name = "estado", column = @Column(name = "jatl_status")),
        @AttributeOverride(name = "fechaCreacion", column = @Column(name = "jatl_creation_date")),
        @AttributeOverride(name = "fechaModificacion", column = @Column(name = "jatl_date_update")),
        @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "jatl_creator_user")),
        @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "jatl_user_update"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "jatl_status = 'TRUE'")
public class OficioAprobacionTDRLA extends EntidadAuditable {


    private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
    public static final String OBTENER_OFICIO_TDR_POR_PROYECTO = PAQUETE + "OficioAprobacionTDRLA.InformeTecnicoTDRLAPorProyecto";
    private static final long serialVersionUID = 3374675069891158114L;

    @Id
    @SequenceGenerator(name = "job_approval_tdr_la_id_generator", initialValue = 1, sequenceName = "seq_jatl_id", schema = "suia_iii", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "job_approval_tdr_la_id_generator")
    @Column(name = "jatl_id")
    @Getter
    @Setter
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "doty_id")
    @ForeignKey(name = "fk_jatl_id_doty_id")
    @Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tydo_status = 'TRUE'")
    @Getter
    @Setter
    private TipoDocumento tipoDocumento;


    @Getter
    @Setter
    @OneToOne
    @ForeignKey(name = "job_approval_tdr_lapren_id_projects_environmental_licensing_pren_id")
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
    @Column(name = "jatl_job_number")
    private String numeroOficio;

    @Getter
    @Setter
    @Column(name = "jatl_subject")
    private String asunto;


    //No se almacenan
    @Getter
    @Setter
    @Transient
    private String ciudadInforme;

    @Getter
    @Setter
    @Transient
    private String fechaInforme;

    @Getter
    @Setter
    @Transient
    private String tratamientoReceptor;

    @Getter
    @Setter
    @Transient
    private String nombreReceptor;

    @Getter
    @Setter
    @Transient
    private String nombreRemitente;


    @Getter
    @Setter
    @Transient
    private String cargoRemitente;

    @Getter
    @Setter
    @Transient
    private String lugarFirma;


    @Getter
    @Setter
    @Transient
    private String cargoReceptor;

    @Getter
    @Setter
    @Transient
    private String institucionEmpresa;

    @Getter
    @Setter
    @Transient
    private String numeroProyecto;

    @Getter
    @Setter
    @Transient
    private String fechaProyecto;

    @Getter
    @Setter
    @Transient
    private String provinciaProyecto;

    @Getter
    @Setter
    @Transient
    private String cantonProyecto;

    @Getter
    @Setter
    @Transient
    private String fechaCoordenadaProyecto;

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
    private String numeroOficioInterseccion;

    @Getter
    @Setter
    @Transient
    private String fechaOficioInterseccion;

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
    private String baseOficio;

    @Getter
    @Setter
    @Transient
    private String listaObservaciones;


}
