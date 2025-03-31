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

import ec.gob.ambiente.suia.domain.SustanciaQuimicaPeligrosa;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "manage_chemicals_project_licencing_coa", schema = "coa_mae")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "mach_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "mach_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "mach_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "mach_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "mach_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "mach_status = 'TRUE'")
public class GestionarProductosQuimicosProyectoAmbiental extends EntidadAuditable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7696580702061847730L;
		
	@Getter
	@Setter
	@Id
	@Column(name = "mach_id")
	@SequenceGenerator(name = "PROJECTS_ENVIRONMENTAL_LICENSING_MACH_ID_GENERATOR", sequenceName = "manage_chemicals_project_licencing_coa_mach_id_seq", schema = "coa_mae", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROJECTS_ENVIRONMENTAL_LICENSING_MACH_ID_GENERATOR")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "chsu_id")
	@ForeignKey(name = "fk_chsu_id")
	@Getter
	@Setter
	private SustanciaQuimicaPeligrosa sustanciaquimica;
	
	
	@Getter
	@Setter
	@Column(name = "mach_observation_bd", length = 255)
	private String observacionDB;
	
	@ManyToOne
	@JoinColumn(name = "prco_id")
	@ForeignKey(name = "prco_id")
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Getter
	@Setter
	@Column(name = "mach_status_control")
	private Boolean controlSustancia;
	
	@Getter
	@Setter
	@Column(name = "mach_another_substance", length = 200)
	private String otraSustancia;
	
	@Getter
	@Setter
	@Column(name = "mach_manages_chemical_substance")
	private Boolean gestionaSustancia;
	
	@Getter
	@Setter
	@Column(name = "mach_carries_chemical_substance")
	private Boolean transportaSustancia;
	
	@Getter
	@Setter
	@Transient
	private Boolean informacionIngresada;
	
}
