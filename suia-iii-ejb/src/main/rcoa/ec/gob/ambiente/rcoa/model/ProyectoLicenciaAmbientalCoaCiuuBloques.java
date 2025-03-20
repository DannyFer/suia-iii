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

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.Bloque;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name = "project_licencing_coa_ciuu_blocks", schema = "coa_mae")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "prbl_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "prbl_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "prbl_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "prbl_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "prbl_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prbl_status = 'TRUE'")
public class ProyectoLicenciaAmbientalCoaCiuuBloques extends EntidadAuditable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4984247105405578978L;

	@Getter
	@Setter
	@Id
	@Column(name = "prbl_id")
	@SequenceGenerator(name = "PROJECTS_ENVIRONMENTAL_LICENSING_PRBL_ID_GENERATOR", sequenceName = "project_licencing_coa_ciuu_blocks_prbl_id_seq", schema = "coa_mae", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROJECTS_ENVIRONMENTAL_LICENSING_PRBL_ID_GENERATOR")
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "prli_id")
	@ForeignKey(name = "prli_id__")
	@Getter
	@Setter
	private ProyectoLicenciaCuaCiuu proyectoLicenciaCuaCiuu;
	
	@ManyToOne
	@JoinColumn(name = "bloc_id")
	@ForeignKey(name = "bloc_id")
	@Getter
	@Setter
	private Bloque bloque;
	
	@Getter
	@Setter
	@Column(name = "prbl_observation_bd", length = 255)
	private String observacionDB;
	
}
