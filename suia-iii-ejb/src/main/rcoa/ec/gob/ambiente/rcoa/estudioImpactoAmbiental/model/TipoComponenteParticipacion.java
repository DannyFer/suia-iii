package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;


@Entity
@Table(name = "type_component_participation", schema = "coa_environmental_impact_study")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "tycp_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "tycp_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "tycp_update_date")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "tycp_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "tycp_update_user")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tycp_status = 'TRUE'")
public class TipoComponenteParticipacion extends EntidadAuditable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5200035293094691659L;

	@Getter
	@Setter
	@Id
	@Column(name = "tycp_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "tycp_options_name", length = 255)
	private String nombre;
	
	@Getter
	@Setter
	@Column(name = "tycp_observation_bdd", length = 255)
	private String observacionDB;
	
}