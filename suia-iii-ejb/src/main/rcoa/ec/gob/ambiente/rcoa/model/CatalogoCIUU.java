package ec.gob.ambiente.rcoa.model;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Filter;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name = "catalog_ciuu", schema = "coa_mae")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "caci_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "caci_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "caci_creator_user")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "caci_date_update")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "caci_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "caci_status = 'TRUE'")
public class CatalogoCIUU extends EntidadBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4723226460021344828L;
	
	@Getter
	@Setter
	@Id
	@Column(name = "caci_id")
	@SequenceGenerator(name = "PROJECTS_ENVIRONMENTAL_LICENSING_CACI_ID_GENERATOR", sequenceName = "catalog_ciuu_caci_id_seq", schema = "coa_mae", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROJECTS_ENVIRONMENTAL_LICENSING_CACI_ID_GENERATOR")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "caci_code", length = 2500)
	private String codigo;
	
	@Getter
	@Setter
	@Column(name = "caci_name", length = 2500)
	private String nombre;
	
	@Getter
	@Setter
	@Column(name = "caci_level")
	private Integer nivel;
	
	@Getter
	@Setter
	@Column(name = "caci_chemical_criterion")
	private Integer criterioQuimico;
	
	@Getter
	@Setter
	@Column(name = "caci_environmental_criterion")
	private Integer criterioAmbiental;
	
	@Getter
	@Setter
	@Column(name = "ceci_concentration_criterion")
	private Integer criterioConcentracion;
	
	@Getter
	@Setter
	@Column(name = "caci_risk_criterion")
	private Integer criterioRiesgo;
	
	@Getter
	@Setter
	@Column(name = "caci_social_criteria")
	private Integer criterioSocial;
	
	@Getter
	@Setter
	@Column(name = "caci_associated_group")
	private Integer grupoAsignado;
	
	@Getter
	@Setter
	@Column(name = "caci_relative_importance")
	private Integer importanciaRelativa;
	
	@Getter
	@Setter
	@Column(name = "caci_environmental_authority")
	private String autoridadAmbiental;
	
	@Getter
	@Setter
	@Column(name = "caci_environmental_authority_number")
	private Integer tipoAutoridadAmbiental;
	
	@Getter
	@Setter
	@Column(name = "caci_genetic")
	private Boolean actividadGenetica;
	
	@Getter
	@Setter
	@Column(name = "caci_scout_drilling")
	private Boolean scoutDrilling;
	
	@Getter
	@Setter
	@Column(name = "caci_form_blocks")
	private Boolean actividadBloqueada;
	
	@Getter
	@Setter
	@Column(name = "caci_visible")
	private Boolean actividadVisible;
	
	@Getter
	@Setter
	@Column(name = "caci_payment_tases")
	private Boolean pagoAdministrativo;
	
	@Getter
	@Setter
	@Column(name = "caci_payment_tases_environmental_record")
	private Boolean pagoRegistroAmbiental;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="sety_id")
	private TipoSector tipoSector;
	
	@Getter
	@Setter
	@Column(name = "caci_protection_zone")
	private Boolean zonaDeProteccion;
	
	@Getter
	@Setter
	@Column(name = "caci_recovery_zone")
	private Boolean zonaDeRecuperacion;
	
	@Getter
	@Setter
	@Column(name = "caci_tourism_zone")
	private Boolean zonaTurismoRecreacion;
	
	@Getter
	@Setter
	@Column(name = "caci_sustainable_zone")
	private Boolean zonaUsoSostenible;
	
	@Getter
	@Setter
	@Column(name = "caci_marine_zone")
	private Boolean zonaMarinoCosteras;
	
	@Getter
	@Setter
	@Column(name = "caci_applies_zone")
	private Boolean aplicaZonas;
	
	@Getter
	@Setter
	@Column(name = "caci_applies_zone_description")
	private String descripcionZona;
	
	@Getter
	@Setter
	@Column(name = "caci_block")
	private Boolean bloques;
	
	@Getter
	@Setter
	@Column(name = "caci_concurrent_management")
	private Boolean gestionConcurrente;
	
	@Getter
	@Setter
	@Column(name = "caci_ppc_environmental_record")
	private Integer ppc;
	
	@Getter
	@Setter
	@Column(name = "caci_sanitation")
	private Boolean saneamiento;
	
	@Getter
	@Setter
	@Column(name = "caci_mtop")
	private Boolean mtop;
	
	@Getter
	@Setter
	@Column(name = "caci_free_use")
	private Boolean libreAprovechamiento;
	
	@Getter
	@Setter
	@Column(name = "caci_permit_type")
	private String tipoPermiso;
	
	@Getter
	@Setter
	@Column(name = "caci_require_viability_pngids")
	private Boolean requiereViabilidadPngids;
	
	@Getter
	@Setter
	@Column(name = "caci_art")
	private Boolean actividadART;
	
	@Getter
	@Setter
	@Column(name = "caci_impact_type")
	private String tipoImpacto;
	
	@Getter
	@Setter
	@Column(name = "caci_pma")
	private Boolean conPma;
	
	@Getter
	@Setter
	@Column(name = "caci_main")
	private Integer tipoAreaActividadPrincipal;
	
	@Getter
	@Setter
	@Column(name = "caci_type_subcategories")
	private Integer tipoPregunta;
	
	@Getter
	@Setter
	@Transient
	private List<SubActividades> subActividades;
}
