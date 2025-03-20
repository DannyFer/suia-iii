package ec.gob.ambiente.rcoa.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;


@Entity
@Table(name = "type_resolutions", schema = "coa_mae")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "tyre_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "tyre_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "tyre_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "tyre_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "tyre_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tyre_status = 'TRUE'")
public class TipoResolucion extends EntidadAuditable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -696518963643827553L;

	@Getter
	@Setter
	@Id
	@Column(name = "tyre_id")
	@SequenceGenerator(name = "PROJECTS_ENVIRONMENTAL_LICENSING_TYRE_ID_GENERATOR", sequenceName = "type_resolutions_tyre_id_seq", schema = "coa_mae", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROJECTS_ENVIRONMENTAL_LICENSING_TYRE_ID_GENERATOR")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "tyre_name", length = 255)
	private String nombre;
	
	@Getter
	@Setter
	@Column(name = "tyre_description", length = 255)
	private String descripcion;
	
	@Getter
	@Setter
	@Column(name = "tyre_abbreviation", length = 255)
	private String abreviacion;
	
	@Getter
	@Setter
	@Column(name = "tyre_sequentyal")
	private Integer sequencial;
	
	@Getter
	@Setter
	@Column(name = "tyre_title", length = 255)
	private String titulo;
	
}
