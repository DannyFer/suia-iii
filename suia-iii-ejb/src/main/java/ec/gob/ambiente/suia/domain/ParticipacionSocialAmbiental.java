package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;

import java.util.Date;

@NamedQueries({@NamedQuery(name = ParticipacionSocialAmbiental.GET_ALL, query = "SELECT m FROM ParticipacionSocialAmbiental m WHERE m.publicacion=TRUE order by m.id desc "),
        @NamedQuery(name = ParticipacionSocialAmbiental.FIND_BY_PROJECT, query = "SELECT m FROM ParticipacionSocialAmbiental m WHERE m.proyectoLicenciamientoAmbiental.id=:prenId order by m.fechaCreacion desc ")})
@Entity
@Table(name = "environmental_social_participation", schema = "suia_iii")
@AttributeOverrides({
        @AttributeOverride(name = "estado", column = @Column(name = "ensp_status")),
        @AttributeOverride(name = "fechaCreacion", column = @Column(name = "ensp_creation_date")),
        @AttributeOverride(name = "fechaModificacion", column = @Column(name = "ensp_date_update")),
        @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "ensp_creator_user")),
        @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "ensp_user_update"))})
@Filter(name = EntidadAuditable.FILTER_ACTIVE, condition = "ensp_status = 'TRUE'")
public class ParticipacionSocialAmbiental extends EntidadAuditable {

    /**
     *
     */
    public static final String GET_ALL = "ec.com.magmasoft.business.domain.ParticipacionSocialAmbiental.getAll";
    public static final String FIND_BY_PROJECT = "ec.com.magmasoft.business.domain.ParticipacionSocialAmbiental.findByProject";

    private static final long serialVersionUID = 6814274812063777095L;

    @Id
    @SequenceGenerator(name = "ENVIRONMENTAL_SOCIAL_PARTICIPATION_ENSPID_GENERATOR", sequenceName = "seq_ensp_id", schema = "suia_iii", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ENVIRONMENTAL_SOCIAL_PARTICIPATION_ENSPID_GENERATOR")
    @Column(name = "ensp_id")
    @Getter
    @Setter
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "pren_id")
    @ForeignKey(name = "fk_projects_enviromental_licensingpren_id_enviromental_social_p")
    @Getter
    @Setter
    private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental;

    @ManyToOne
    @JoinColumn(name = "ensp_social_coordinator")
    @ForeignKey(name = "fk_enviromental_social_participationensp_social_coordinator_use")
    @Getter
    @Setter
    private Usuario usuario;

    /*
    @Getter
    @Setter
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "participacionSocialAmbiental", fetch = FetchType.LAZY)
    private List<RegistroMediosParticipacionSocial> registroMediosParticipacionSocial;
*/
    @Getter
    @Setter
    @Column(name = "ensp_requires_facilitators")
    private boolean requiereFaciltador;

    @Getter
    @Setter
    @Column(name = "ensp_facilitators_number")
    private Integer numeroFacilitador;

    @Getter
    @Setter
    @Column(name = "ensp_published")
    private boolean publicacion;

    @Getter
    @Setter
    @Column(name = "ensp_publication_date")
    private Date fechaPublicacionEstudio;

    @Getter
    @Setter
    @Column(name = "ensp_pubication_call_date")
    private Date fechaPublconvocatoria;

    @Getter
    @Setter
    @Column(name = "ensp_final_report_observations")
    private String observacionReporfinal;

    @Getter
    @Setter
    @Column(name = "ensp_final_report_general_observacion")
    private String observacionGeneralfinal;

    @Getter
    @Setter
    @Column(name = "ensp_complemetary_information")
    private String informacionComplementaria;

    @Getter
    @Setter
    @Column(name = "ensp_complementary_promotor_information")
    private String informacionComplementariaRespuesta;
    @Getter
    @Setter
    @Column(name = "ensp_complementary_promotor_information_es")
    private String informacionComplementariaRespuestaES;

    @Getter
    @Setter
    @Column(name = "ensp_technical_criterial_request")
    private boolean respCriteriotecnico;

    @Getter
    @Setter
    @Column(name = "ensp_technical_criterial_request_information")
    private String respCriteriotecnicoinformacion;

    @Getter
    @Setter
    @Column(name = "ensp_social_eia_observations")
    private String observacionesEia;

    @Getter
    @Setter
    @Column(name = "ensp_media_observations")
    private String observacionesMediosVerificacion;

    @Getter
    @Setter
    @Column(name = "ensp_aditional_observations")
    private String observacionAdicional;

    @Getter
    @Setter
    @Column(name = "ensp_report_conclusions")
    private String conclusion;

    @Getter
    @Setter
    @Column(name = "ensp_dnpca_determination")
    private String determinacionDnpca;

    @Getter
    @Setter
    @Column(name = "ensp_recomendations")
    private String recomendaciones;

    @Getter
    @Setter
    @Column(name = "ensp_facilitators_amount")
    private double montoPago;

    @Getter
    @Setter
    @Column(name = "ensp_documentation_observations")
    private String observacionesDocumentacion;
    
    
    @Getter
    @Setter
    @Column(name = "ensp_previous_visit_start_date")
    private Date visitaPreviaFechaInicio;

    @Getter
    @Setter
    @Column(name = "ensp_previous_visit_end_date")
    private Date visitaPreviaFechaFin;
    @Getter
    @Setter
    @Column(name = "ensp_attendant_facilitator_evaluation_value")
    private Boolean cumplioFacilitador;

    @Getter
    @Setter
    @Column(name = "ensp_favorable_statement")
    private Boolean pronunciamientoFavorable;

    @Getter
    @Setter
    @Column(name = "ensp_non_approval_statement")
    private Boolean pronunciamientoNoAprobacion;

    @Getter
    @Setter
    @Column(name = "ensp_final_report_date")
    private Date fechaEntregaInformeFinalPPS;

    @Getter
    @Setter
    @Transient
    private String fechaEntregaInformeSistematizacion;


    @Getter
    @Setter
    @Transient
    private String numeroInforme;

    @Getter
    @Setter
    @Transient
    private String numeroOficio;
    //@Column(name = "ensp_attendant_facilitator_evaluation_value")

    @Getter
    @Setter
    @Transient
    private String nombrePPS;

    @Getter
    @Setter
    @Transient
    private String nombreProponente;

    @Getter
    @Setter
    @Transient
    private String nombreConsultor;

    @Getter
    @Setter
    @Transient
    private String nombreFacilitador;

    @Getter
    @Setter
    @Transient
    private String fechaInforme;
    
    @Getter
    @Setter
    @Transient
    private String fechaMostrar;
    
    @Getter
    @Setter
    @Transient
    private String autoridad;
    
    @Getter
    @Setter
    @Transient
    private String autoridadFirma;
    
    @Getter
    @Setter
    @Transient
    private String cargoAutoridad;
    
    @Getter
    @Setter
    @Transient
    private String sujetoControlEncabez;

    @Getter
    @Setter
    @Transient
    private String fechaAsignacionFacilitador;

    @Getter
    @Setter
    @Transient
    private String fechaVisitaPrevia;

    @Getter
    @Setter
    @Transient
    private String fechaAprobacionVistaPrevia;


    @Getter
    @Setter
    @Transient
    private String fechaInicioProceso;

    @Getter
    @Setter
    @Transient
    private String fechaCierreProceso;

    @Getter
    @Setter
    @Transient
    private String fechaAsambleaPublica;

    @Getter
    @Setter
    @Transient
    private String mediosConvocatoria;

    @Getter
    @Setter
    @Transient
    private String mecansimosPS;

    @Getter
    @Setter
    @Transient
    private String evaluacionConformidad;

    @Getter
    @Setter
    @Transient
    private String cumplimientoObjetivos;

    @Getter
    @Setter
    @Transient
    private String elaboradoPor;

    @Getter
    @Setter
    @Transient
    private String revisadoPor;

    @Getter
    @Setter
    @Transient
    private String aprobadoPor;

    @Getter
    @Setter
    @Transient
    private String listaFacilitadores;
    
    @Getter
    @Setter
    @Transient
    private String visiblePC;

    @Getter
    @Setter
    @Transient
    private String visibleGAD;
    
    @Getter
    @Setter
    @Transient
    private String cargoTecnico;
    
    @Getter
    @Setter
    @Transient
    private String cargoCoordinador;

    @Getter 
    @Setter 
    @Transient 
    private String GADPC; 

    @Getter 
    @Setter 
    @Transient 
    private String segundoParrafoOficio; 
    
    @Getter 
    @Setter 
    @Transient 
    private String autoridadAmbiental;
    
    @Getter 
    @Setter 
    @Transient 
    private String ubicacion; 
    
    @Getter 
    @Setter 
    @Transient 
    private String codigoProyecto; 
    
    @Getter 
    @Setter 
    @Transient 
    private String nemeroReferencia;
    
    @Getter 
    @Setter 
    @Transient 
    private String fechaPago;
    
    @Getter 
    @Setter 
    @Transient 
    private String fechaAceptacionFacilitador;
    
    @Getter 
    @Setter 
    @Transient 
    private String cumple;
    
    @Getter 
    @Setter 
    @Transient 
    private String area;
    
    @Getter 
    @Setter 
    @Transient 
    private String parrafoAprobacion;
    
    //Cris F: aumento de variables para reporte tecnico
    @Getter
    @Setter
    @Transient
    private String entidadPublica;
    
    @Getter
    @Setter
    @Column(name = "ensp_report_antecedent")
    private String antecedentes;
    
    @Getter
    @Setter
    @Column(name = "ensp_job_conclusion")
    private String conclusionOficio;
    
    @Getter
    @Setter
    @Column(name = "ensp_date_start_publishing")
    private Date fechaInicioPublicacion;
    
    @Getter
    @Setter
    @Column(name = "ensp_date_end_publishing")
    private Date fechaFinPublicacion;
    
    
}
