package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;

@NamedQueries({
        @NamedQuery(name = InformeTecnicoGeneralLA.OBTENER_INFORME_TECNICO_GENERAL_POR_LICENCIA,
                query = "select i from InformeTecnicoGeneralLA i where i.tipoDocumento.id = :tipoDocumentoId and i.licenciaAmbiental.id = :licenciaId "),
        @NamedQuery(name = InformeTecnicoGeneralLA.OBTENER_INFORME_TECNICO_GENERAL_POR_PROYECTO,
                query = "select i from InformeTecnicoGeneralLA i where i.tipoDocumento.id = :tipoDocumentoId and i.licenciaAmbiental.proyecto.id = :proyectoId and i.estado=true "),

})
@Entity
@Table(name = "technical_report_environmental_licensing_general", schema = "suia_iii")
@AttributeOverrides({
        @AttributeOverride(name = "estado", column = @Column(name = "trel_status")),
        @AttributeOverride(name = "fechaCreacion", column = @Column(name = "trel_creation_date")),
        @AttributeOverride(name = "fechaModificacion", column = @Column(name = "trel_date_update")),
        @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "trel_creator_user")),
        @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "trel_user_update"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "trel_status = 'TRUE'")
public class InformeTecnicoGeneralLA extends EntidadAuditable {

    /**
     *
     */
    private static final long serialVersionUID = -6318968712506201047L;

    private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
    public static final String OBTENER_INFORME_TECNICO_GENERAL_POR_LICENCIA = PAQUETE + "InformeTecnicoGeneralLA.InformeTecnicoGeneralPorLicenciaId";

    public static final String OBTENER_INFORME_TECNICO_GENERAL_POR_PROYECTO = PAQUETE + "InformeTecnicoGeneralLA.InformeTecnicoGeneralPorProyecto";

    @Id
    @SequenceGenerator(name = "technical_report_elg_id_generator", initialValue = 1, sequenceName = "trel_id_seq", schema = "suia_iii", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "technical_report_elg_id_generator")
    @Column(name = "trel_id")
    @Getter
    @Setter
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "doty_id")
    @ForeignKey(name = "fk_trel_id_doty_id")
    @Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tydo_status = 'TRUE'")
    @Getter
    @Setter
    private TipoDocumento tipoDocumento;

    @ManyToOne
    @JoinColumn(name = "lice_id")
    @ForeignKey(name = "fk_trel_id_lice_id")
    @Filter(name = EntidadBase.FILTER_ACTIVE, condition = "lice_status = 'TRUE'")
    @Getter
    @Setter
    private LicenciaAmbiental licenciaAmbiental;

    @Getter
    @Setter
    @Column(name = "doty_id", updatable = false, insertable = false)
    private Integer tipoDocumentoId;


    @Getter
    @Setter
    @Column(name = "trel_office_number")
    private String numeroOficio = "";

    @Getter
    @Setter
    @Column(name = "trel_regulation_per_sectors")
    private String normativasSector;

    @Getter
    @Setter
    @Column(name = "trel_obligations")
    private String obligaciones;

    @Getter
    @Setter
    @Column(name = "trel_license_number")
    private String numeroLicencia;

    @Getter
    @Setter
    @Column(name = "trel_application_support")
    private String respaldoSolicitud;


    @Getter
    @Setter
    @Column(name = "trel_ci_office_number")
    private String numeroOficioCI = "";

    @Getter
    @Setter
    @Column(name = "trel_ci_office_date")
    private String fechaOficioCI = "";


    @Getter
    @Setter
    @Column(name = "trel_intersect")
    private String interseca = "";


    @Getter
    @Setter
    @Column(name = "trel_tdr_date")
    private String fechaTDR = "";


    @Getter
    @Setter
    @Column(name = "trel_normative_observations")
    private String observacionesEIANormativa = "";

    @Getter
    @Setter
    @Column(name = "trel_observations_response")
    private String respuestaEIANormativa = "";


    @Getter
    @Setter
    @Column(name = "trel_office_request_number_ofi")
    private String oficioSolicitudOFI = "";


    @Getter
    @Setter
    @Column(name = "trel_office_request_date")
    private String fechaOficioSolicitudOFI = "";


    @Getter
    @Setter
    @Column(name = "trel_office_request_number_sca")
    private String numeroOficioSCA = "";

    @Getter
    @Setter
    @Column(name = "trel_office_request_date_sca")
    private String fechaOficioSCA = "";


    @Getter
    @Setter
    @Column(name = "trel_report_number_sca")
    private String numeroInformeSCA = "";

    @Getter
    @Setter
    @Column(name = "trel_report_date_sca")
    private String fechaInformeSCA = "";


    @Getter
    @Setter
    @Column(name = "trel_related_activities")
    private String actividadesRelacionadas = "";


    @Getter
    @Setter
    @Column(name = "trel_finalized")
    private boolean finalizado = false;

    @Getter
    @Setter
    @Transient
    private String ciudadInforme = "";

    @Getter
    @Setter
    @Transient
    private String fechaInforme = "";

    @Getter
    @Setter
    @Transient
    private String nombreMinistra = "";

    @Getter
    @Setter
    @Transient
    private String cargoMinistra = "";

    @Getter
    @Setter
    @Transient
    private String fechaRegistroProyecto = "";


    @Getter
    @Setter
    @Transient
    private String nombreEmpresaProponente = "";


    @Getter
    @Setter
    @Transient
    private String nombreProyecto = "";


    @Getter
    @Setter
    @Transient
    private String codigoProyecto = "";

    @Getter
    @Setter
    @Transient
    private String provinciaProyecto = "";


    @Getter
    @Setter
    @Transient
    private String areasInterseca = "";

    @Getter
    @Setter
    @Transient
    private String coordenadasProyecto = "";

    @Getter
    @Setter
    @Transient
    private String numeroMemorandoPrevencion = "";

    @Getter
    @Setter
    @Transient
    private String fechaMemorandoPrevencion = "";


    @Getter
    @Setter
    @Transient
    private String numeroMemorandoForestal = "";

    @Getter
    @Setter
    @Transient
    private String fechaMemorandoForestal = "";


    @Getter
    @Setter
    @Transient
    private String oficioInformeTecnico = "";


    @Getter
    @Setter
    @Transient
    private String fechaoficioInformeTecnico = "";


    @Getter
    @Setter
    @Transient
    private String numeroOficioInformeTecnico = "";


    @Getter
    @Setter
    @Transient
    private String fechaOficioInformeTecnico = "";

    @Getter
    @Setter
    @Transient
    private String lugarAsambleaPublica = "";

    @Getter
    @Setter
    @Transient
    private String fechaAsambleaPublica = "";

    @Getter
    @Setter
    @Transient
    private String lugarJunta = "";


    @Getter
    @Setter
    @Transient
    private String rangoFechaJunta = "";


    @Getter
    @Setter
    @Transient
    private String fechaRevisionEIA = "";


    @Getter
    @Setter
    @Transient
    private String numeroMemorandoContaminacion = "";

    @Getter
    @Setter
    @Transient
    private String fechaMemorandoContaminacion = "";

    @Getter
    @Setter
    @Transient
    private String numeroMemorandoForestalFinal = "";

    @Getter
    @Setter
    @Transient
    private String fechaMemorandoForestalFinal = "";


    @Getter
    @Setter
    @Transient
    private String memorandoFinanciero = "";

    @Getter
    @Setter
    @Transient
    private String direccionCompleta = "";

    @Getter
    @Setter
    @Transient
    private String numeroOficioCoordenadas = "";

    @Getter
    @Setter
    @Transient
    private String areaResponsable = "";


    @Getter
    @Setter
    @Transient
    private String nombreCompletoRepresentanteLegal = "";


    @Getter
    @Setter
    @Transient
    private String areaResponsableSumilla = "";

    @Getter
    @Setter
    @Transient
    private String firmaDigital = "";

    @Getter
    @Setter
    @Transient
    private String fechaFirma = "";

    @Getter
    @Setter
    @Transient
    private String anexo = "";

    @Getter
    @Setter
    @Column(name = "trel_resolution_number")
    private String numeroResolucion ="";

}
