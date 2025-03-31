package ec.gob.ambiente.retce.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
/**
 * The persistent class for the retce.project_information database table.
 * 
 */
@Entity
@Table(name="project_information", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "proj_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "proj_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "proj_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "proj_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "proj_user_update")) })
public class InformacionProyecto extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="proj_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name="proj_name")
	private String nombreProyecto;
	
	@Getter
	@Setter
	@Column(name="proj_code")
	private String codigo;
	
	@Getter
	@Setter
	@Column(name="proj_process_name")
	private String nombreProceso;
	
	@Getter
	@Setter
	@Column(name="proj_start_date_operations")
	private Date fechaInicioOperaciones;
		
	@Getter
	@Setter
	@Column(name="proj_resolution_number")
	private String numeroResolucion;
	
	@Getter
	@Setter
	@Column(name="proj_date_emission")
	private Date fechaEmision;
	
	@Getter
    @Setter
    @Column(name="proj_code_retce")
    private String codigoRetce;
   
    @Getter
    @Setter
    @Column(name = "proj_physical_emission")
    private Boolean esEmisionFisica;
		
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="acti_id")
	private ActividadCiiu actividadCiiu;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="resp_id")
	private TecnicoResponsable tecnicoResponsable;
					
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="responsible_area_id")
	private Area areaResponsable;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="tracking_area_id")
	private Area areaSeguimiento;
	
    @Getter
	@Setter
	@ManyToOne
    @JoinColumn(name="sety_id")
	private TipoSector tipoSector;
    
    @Getter
	@Setter
	@ManyToOne
    @JoinColumn(name="fase_id")
	private FaseRetce faseRetce;
    
    @Getter
    @Setter
	@ManyToOne
    @JoinColumn(name="proj_type_management")
    private DetalleCatalogoGeneral tipoGestion;
        
    @Getter
    @Setter
    @Column(name = "proj_sended_information")
    private Boolean informacionEnviada;
    
    @Getter
    @Setter
    @Column(name = "proj_validated_information")
    private Boolean informacionValidada;
    
    @Getter
    @Setter
    @Column(name = "proj_validated_information_date")
    private Date fechaInformacionValidada;
    
    @Getter
    @Setter
    @Column(name = "proj_validated_information_user")
    private String usuarioInformacionValidada;
    
    @Getter
    @Setter
    @Column(name = "proj_sended_update")
    private Boolean actualizacionEnviada;
    
    @Getter
    @Setter
    @Column(name = "proj_validated_update")
    private Boolean actualizacionValidada;
    
    @Getter
    @Setter
    @Column(name = "proj_validated_update_date")
    private Date fechaActualizacionValidada;
    
    @Getter
    @Setter
    @Column(name = "proj_validated_update_user")
    private String usuarioActualizacionValidada;
    
    @Getter
    @Setter
    @Column(name = "proj_management_changed_dangerous_to_not_dangerous")
    private boolean gestionCambioDesechos;
    
    @Getter
    @Setter
    @Column(name = "proj_waste_management")
    private boolean gestionDesechos;

	@Getter
	@Setter
	@Column(name = "proj_history")
	private boolean historico;

	@Getter
	@Setter
	@Column(name = "proj_id_owner")
	private Integer idPropietario;

	@Getter
	@Setter
	@Column(name = "proj_intersection_certificate")
	private String certificadoInterseccion;
    
    @Getter
    @Setter
    @Column(name = "proj_intersection_date")
    private Date fechaCertificadoInterseccion;

	@Getter
	@Setter
	@Column(name = "proj_manager")
	private boolean gestor;

	@Getter
	@Setter
	@Column(name = "proj_intersection")
	private boolean interseca;

	@Getter
	@Setter
	@Column(name = "proj_es_proyectoRcoa")
	private boolean esProyectoRcoa;

	@Getter
	@Setter
	@Column(name = "proj_coordinate_format")
	private String formatoCoordenadas;
	
	@Getter
	@Setter
	@Column(name = "proj_coordinate_zona")
	private String zona;
}
