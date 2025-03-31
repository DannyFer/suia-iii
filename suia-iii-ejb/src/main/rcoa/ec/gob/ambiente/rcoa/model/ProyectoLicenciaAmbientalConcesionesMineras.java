package ec.gob.ambiente.rcoa.model;

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
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "project_licencing_coa_ciuu_mining_concessions", schema = "coa_mae")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "prmi_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "prmi_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "prmi_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "prmi_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "prmi_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prmi_status = 'TRUE'")
public class ProyectoLicenciaAmbientalConcesionesMineras extends EntidadAuditable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8759705010194504396L;


	@Getter
	@Setter
	@Id
	@Column(name = "prmi_id")
	@SequenceGenerator(name = "PROJECTS_ENVIRONMENTAL_LICENSING_PRMI_ID_GENERATOR", sequenceName = "project_licencing_coa_ciuu_mining_concessions_prmi_id_seq", schema = "coa_mae", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROJECTS_ENVIRONMENTAL_LICENSING_PRMI_ID_GENERATOR")
	private Integer id;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "prli_id")
	@ForeignKey(name = "prli_id_")
	private ProyectoLicenciaCuaCiuu proyectoLicenciaCuaCiuu;
	
	@Getter
	@Setter
	@Column(name = "prmi_code")
	private String codigo;
	
	@Getter
	@Setter
	@Column(name = "prmi_name")
	private String nombre;
	
	@Getter
	@Setter
	@Column(name = "prmi_regime")
	private String regimen;
	
	@Getter
	@Setter
	@Column(name = "prmi_material")
	private String material;
	
	@Getter
	@Setter
	@Column(name = "prmi_area")
	private double area;
	
	@Getter
	@Setter
	@Column(name = "prmi_observation_bd", length = 255)
	private String observacionDB;
	
	@Getter
	@Setter
	@Column(name = "prmi_contract")
	private Boolean tieneContrato;
	
	@Getter
	@Setter
	@Transient
	private String codigoContrato;
	
	@Getter
	@Setter
	@Transient
	private String rucTitularCon;
	
	@Getter
	@Setter
	@Transient
	private String rucOperador;
	
	@Getter
	@Setter
	@Transient
	private String errorValidacionCoordArcom;
	
	@Getter
	@Setter
	@Transient
	private Boolean coordenadasCoinciden;
	
	@Getter
	@Setter
	@Transient
	private String fechaSuscripcion;
	
	@Getter
	@Setter
	@Transient
	private String mesesPlazo;
	
	@Getter
	@Setter
	@Transient
	private String nombreOperador;
	
	
}
