package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the environmental_certificate_report_snap database table.
 * 
 */
@NamedQueries({ @NamedQuery(name = CertificadoAmbientalInformeSnap.FIND_BY_PROJECT, query = "SELECT c FROM CertificadoAmbientalInformeSnap c WHERE c.estado=true and c.proyecto.id = :idProyecto order by 1 desc"),
	@NamedQuery(name = CertificadoAmbientalInformeSnap.FIND_BY_CODE, query = "SELECT c FROM CertificadoAmbientalInformeSnap c WHERE c.estado=true and c.codigo = :codigoInforme order by 1 desc")})
@Entity
@Table(name = "environmental_certificate_report_snap", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "ecrs_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "ecrs_date_create")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "ecrs_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "ecrs_user_create")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "ecrs_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "ecrs_status = 'TRUE'")
public class CertificadoAmbientalInformeSnap extends EntidadAuditable {

	private static final long serialVersionUID = 4390395846566873634L;
	
	public static final String FIND_BY_PROJECT = "ec.com.magmasoft.business.domain.CertificadoAmbientalInformeSnap.byProject";
	public static final String FIND_BY_CODE = "ec.com.magmasoft.business.domain.CertificadoAmbientalInformeSnap.byCode";

	@Getter
	@Setter
	@Id
	@Column(name = "ecrs_id")	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
    @Getter
    @Setter
    @JoinColumn(name = "pren_id", referencedColumnName = "pren_id")
    @ManyToOne
    @ForeignKey(name = "fk_ecrs_pren")
    private ProyectoLicenciamientoAmbiental proyecto;
    
	@Getter
	@Setter
	@Column(name = "ecrs_code", length = 50)
	private String codigo;
	
	@Getter
	@Setter
	@Column(name = "ecrs_extension_length")
	private Double longitud;
	
	@Getter
	@Setter
	@Column(name = "ecrs_unit_length", length = 50)
	private String unidadLongitud;
	
	@Getter
	@Setter
	@Column(name = "ecrs_surface_extension")
	private Double superficie;
	
	@Getter
	@Setter
	@Column(name = "ecrs_surface_unit", length = 50)
	private String unidadSuperficie;
	
	@Getter
	@Setter
	@Column(name = "ecrs_description_other_extension", length = 50)
	private String otraExtension;
	@Getter
	@Setter
	@Column(name = "ecrs_value_other_extension")
	private Double valorOtraExtension;
	@Getter
	@Setter
	@Column(name = "ecrs_unit_other_extension", length = 50)
	private String unidadOtraExtension;
		  
	@Getter
	@Setter
	@Column(name = "ecrs_score")
	private Integer puntaje;
	
	@Getter
	@Setter
	@Column(name = "ecrs_favorable_pronouncement")
	private Boolean pronunciamientoFavorable;
	
	@Getter
	@Setter
	@Column(name = "ecrs_protection_area")
	private Boolean zonaProteccion;
	
	@Getter
	@Setter
	@Column(name = "ecrs_public_tourism_use_area")
	private Boolean zonaUsoPublicaTurismo;
	
	@Getter
	@Setter
	@Column(name = "ecrs_recovery_area")
	private Boolean zonaRecuperacion;
	
	@Getter
	@Setter
	@Column(name = "ecrs_sustainable_use_area")
	private Boolean zonaUsoSostenible;
	
	@Getter
	@Setter
	@Column(name = "ecrs_community_management_area")
	private Boolean zonaManejoComunitario;
	
	@Getter
	@Setter
	@Column(name = "ecrs_activity_allowed")
	private Boolean actividadPermitida;

	
	@Getter
	@Setter
	@Column(name = "ecrs_intersects_ramsar")
	private Boolean intersecaRamsar;

	@Getter
	@Setter
	@Column(name = "ecrs_intersects_ramsar_description", length = 2000)
	private String intersecaRamsarDescripcion;
	
	@Getter
	@Setter
	@Column(name = "ecrs_territory_ansestral")
	private Boolean territorioAnsestral;

	@Getter
	@Setter
	@Column(name = "ecrs_territory_ansestral_description", length = 2000)
	private String territorioAnsestralDescripcion;
	
	@Getter
	@Setter
	@Column(name = "ecrs_vegetation_status", length = 50)
	private String estadoVegetacion;
	
	@Getter
	@Setter	
	@JoinColumn(name = "ecrs_vegetation_status_docu_id",referencedColumnName = "docu_id")
	@ManyToOne
	@ForeignKey(name = "fk_ecrs_docu_vegetation")	
	private Documento estadoVegetacionDocumento;
		
	@Getter
	@Setter
	@Column(name = "ecrs_native_vegetation")
	private Boolean vegetacionNativa;
	
	@Getter
	@Setter
	@Column(name = "ecrs_impact_conservation")
	private Boolean afectacionValoresConservacion;

	@Getter
	@Setter
	@Column(name = "ecrs_impact_conservation_description", length = 2000)
	private String afectacionValoresConservacionDescripcion;
	
	@Getter
	@Setter
	@Column(name = "ecrs_impact_species")
	private Boolean afectacionEspecies;

	@Getter
	@Setter
	@Column(name = "ecrs_impact_water_sources")
	private Boolean afectacionFuentesHidricas;

	@Getter
	@Setter
	@Column(name = "ecrs_impact_water_sources_description", length = 2000)
	private String afectacionFuentesHidricasDescripcion;
	
	@Getter
	@Setter
	@Column(name = "ecrs_impact_ecosystem_services")
	private Boolean afectacionServiciosEcosistemicos;

	@Getter
	@Setter
	@Column(name = "ecrs_impact_ecosystem_services_description", length = 2000)
	private String afectacionServiciosEcosistemicosDescripcion;
	
	@Getter
	@Setter
	@Column(name = "ecrs_investment_projects")
	private Boolean existeProyectosInversion;

	@Getter
	@Setter
	@Column(name = "ecrs_investment_projects_description", length = 2000)
	private String existeProyectosInversionDescripcion;
	
}