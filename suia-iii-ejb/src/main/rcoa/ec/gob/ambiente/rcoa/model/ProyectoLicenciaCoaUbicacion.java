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

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "project_licencing_coa_location", schema = "coa_mae")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "prlo_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "prlo_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "prlo_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "prlo_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "prlo_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prlo_status = 'TRUE'")
public class ProyectoLicenciaCoaUbicacion extends EntidadAuditable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2459636191641484340L;
	
	@Getter
	@Setter
	@Id
	@Column(name = "prlo_id")
	@SequenceGenerator(name = "PROJECTS_ENVIRONMENTAL_LICENSING_PRLO_ID_GENERATOR", sequenceName = "project_licencing_coa_location_prlo_id_seq", schema = "coa_mae", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROJECTS_ENVIRONMENTAL_LICENSING_PRLO_ID_GENERATOR")
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "prco_id")
	@ForeignKey(name = "fk_prco_id")
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	
	@ManyToOne
	@JoinColumn(name = "gelo_id")
	@ForeignKey(name = "fk_gelo_id")
	@Getter
	@Setter
	private UbicacionesGeografica ubicacionesGeografica;
	
	@Getter
	@Setter
	@Column(name = "prlo_observations", length = 255)
	private String observacion;
	
	@Getter
	@Setter
	@Column(name = "prlo_observation_bd", length = 255)
	private String observacionDB;
	
	@Getter
	@Setter
	@Column(name = "prlo_primary")
	private Boolean primario;
	
	@Getter
	@Setter
	@Column(name = "prlo_update_number")
	private Integer nroActualizacion = 0;

}
