package ec.gob.ambiente.rcoa.digitalizacion.model;


import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.CatalogoFasesCoa;
import ec.gob.ambiente.rcoa.model.CatalogoCIUU;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;	

import java.util.Date;
import java.util.List;

@Entity
@Table(name="environmental_administrative_authorizations", schema = "coa_digitalization_linkage")
@NamedQueries({
@NamedQuery(name="AutorizacionAdministrativaAmbiental.GETFINDALL", query="SELECT c FROM AutorizacionAdministrativaAmbiental c")})

@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "enaa_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "enaa_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "enaa_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "enaa_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "enaa_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "enaa_status = 'TRUE'")

public class AutorizacionAdministrativaAmbiental extends EntidadAuditable {
    private static final long serialVersionUID = 5016494380218175574L;    
    @Getter
    @Setter
    @Id
    @SequenceGenerator(name = "ADMINISTRACIONAUTORIZACION_ID_GENERATOR", sequenceName = "environmental_administrative_authorizations_enaa_id_seq", schema = "coa_digitalization_linkage", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ADMINISTRACIONAUTORIZACION_ID_GENERATOR")     
    @Column(name = "enaa_id")
    private Integer id;
    @Getter
    @Setter
    @Column(name = "enaa_resolution", length = 100)
    private String resolucion;
    @Getter
    @Setter
    @Column(name = "enaa_resolution_start_date")
    private Date fechaInicioResolucion; 
    @Getter
    @Setter
    @Column(name = "enaa_resolution_date")
    private Date fechaResolucion; 
    @Getter
    @Setter
    @Column(name = "enaa_project_code", length = 50)
    private String codigoProyecto;
    @Getter
    @Setter
    @Column(name = "enaa_id_proyecto")
    private Integer idProyecto; 
    @Getter
    @Setter
    @Column(name = "enaa_type_environmental_permit", length = 50)
    private String tipoPermisoAmbiental;
    @Getter
    @Setter
    @Column(name = "enaa_environmental_administrative_authorization")
    private String autorizacionAdministrativaAmbiental;
    @Getter
    @Setter
    @Column(name = "enaa_project_name")
    private String nombreProyecto;
    @Getter
    @Setter
    @Column(name = "enaa_project_summary")
    private String resumenProyecto;
    @Getter
    @Setter
    @Column(name = "enaa_catalog_activity")
    private String ActividadCatalogo;
    @Getter
    @Setter
    @Column(name = "enaa_catalog_activity_code", length = 15)
    private String codigoActividadCatalogo;
    @Getter
    @Setter
    @Column(name = "enaa_code_ciuu_equivalent")
    private Integer codigoCIIUEquivalente;
    @Getter
    @Setter
    @Column(name = "enaa_code")
    private String codigoDigitalizacion;
    @Getter
    @Setter
    @Column(name = "enaa_status_date")
    private Date fechaEstado;
    @Getter
    @Setter
    @Column(name = "enaa_register_date")
    private Date fechaRegistro;
    @Getter
    @Setter
    @Column(name = "enaa_register_finalized_date")
    private Date fechaFinalizacionRegistro;
    @Getter
    @Setter
    @Column(name = "enaa_system")
    private Integer sistema;
    @Getter
    @Setter
    @Column(name = "enaa_status_finalized")
    private boolean finalizado;
    @Getter
    @Setter
    @Column(name = "enaa_history")
    private boolean historico;
    @Getter
    @Setter
    @Column(name = "enna_update_status")
    private Boolean estadoActualizacion;
    @Getter
    @Setter
    @Column(name = "enna_update_coordinate")
    private boolean actualizacionCoordenadas;
    @Getter
    @Setter
    @Column(name = "enna_resolution_update")
    private boolean actualizacionResolucion;
    @Getter
    @Setter
    @Column(name = "enaa_parent_history")
    private Integer idPadreHistorico;
    @Getter
    @Setter
    @Column(name = "enaa_process_instance_id")
    private Integer idProceso;
	@ManyToOne
	@JoinColumn(name = "area_id_aaa")
	@ForeignKey(name = "fk_area_id_aaa")
	@Getter
	@Setter
	private Area areaEmisora;
    
	@ManyToOne
	@JoinColumn(name = "area_id_dzg")
	@ForeignKey(name = "fk_area_id_dzg")
	@Getter
	@Setter
	private Area areaDZGTecnico;
    
	@ManyToOne
	@JoinColumn(name = "area_id_ercs")
	@ForeignKey(name = "fk_area_id_ercs")
	@Getter
	@Setter
	private Area areaResponsableControl;
	
	@ManyToOne
	@JoinColumn(name = "caci_id")
	@ForeignKey(name = "fk_caci_id")
	@Getter
	@Setter
	private CatalogoCIUU catalogoCIUU;
	
	@ManyToOne
	@JoinColumn(name = "capp_id")
	@ForeignKey(name = "fk_capp_id")
	@Getter
	@Setter
	private CatalogoFasesCoa catalogoFasesCoa;
	
	@ManyToOne
	@JoinColumn(name = "enaa_parent_id")
	@ForeignKey(name = "fk_enaa_parent_id")
	@Getter
	@Setter
	private AutorizacionAdministrativaAmbiental autorizacionAdministrativa;
	
	@ManyToOne
	@JoinColumn(name = "sety_id")
	@ForeignKey(name = "fk_sety_id")
	@Getter
	@Setter
	private TipoSector tipoSector;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	@ForeignKey(name = "fk_user_id")
	@Getter
	@Setter
	private Usuario usuario;	
	
	@Getter
	@Setter
	@Column(name = "enaa_name_user")
	private String nombreUsuario;
	
	@Getter
	@Setter
	@Column(name = "enaa_ci_user")
	private String identificacionUsuario;
	
	@Getter
	@Setter
	@Column(name = "enaa_intersection_certificate_update")
	private Boolean tieneCertificado;
	
    	@Getter
	@Setter
	@Column(name = "enna_justify_intersection")
	private String justificacionInterseccion;
	
    @Getter
    @Setter
    @Transient
    private String motivoActualizacion;
	
    @Getter
    @Setter
    @Transient
    private String fuenteSistema;
	
	@Getter
	@Setter
	@Transient
	private List<ProyectoAsociadoDigitalizacion> listaProyectosAsociados;

}
