package ec.gob.ambiente.rcoa.model;

import java.math.BigDecimal;

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

import ec.gob.ambiente.suia.domain.TipoForma;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "project_licencing_coa_shapes", schema = "coa_mae")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "prsh_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "prsh_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "prsh_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "prsh_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "prsh_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prsh_status = 'TRUE'")
public class ProyectoLicenciaAmbientalCoaShape extends EntidadAuditable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7560890748668981821L;
	
	@Getter
	@Setter
	@Id
	@Column(name = "prsh_id")
	@SequenceGenerator(name = "PROJECTS_ENVIRONMENTAL_LICENSING_PRSH_ID_GENERATOR", sequenceName = "project_licencing_coa_shapes_prsh_id_seq", schema = "coa_mae", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROJECTS_ENVIRONMENTAL_LICENSING_PRSH_ID_GENERATOR")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "prco_id")
	@ForeignKey(name = "fk_prco_id")
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@ManyToOne
	@JoinColumn(name = "shty_id")
	@ForeignKey(name = "fk_shty_id")
	@Getter
	@Setter
	private TipoForma tipoForma;
		
	@Getter
	@Setter
	@Column(name = "prsh_type")
	private int tipo;
	
	@Getter
	@Setter
	@Column(name = "prsh_observation_bd", length = 255)
	private String observacionDB;
	
	
	@Getter
	@Setter
	@Column(name = "prsh_update_number")
	private Integer numeroActualizaciones;
	
	@Getter
	@Setter
	@Column(name = "prsh_surface")
	private BigDecimal superficie;
	
	
}
