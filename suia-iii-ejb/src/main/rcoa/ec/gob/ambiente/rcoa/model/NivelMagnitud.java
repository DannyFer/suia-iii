package ec.gob.ambiente.rcoa.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "level_criteria", schema = "coa_mae")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "lecr_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "lecr_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "lecr_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "lecr_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "lecr_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "lecr_status = 'TRUE'")
public class NivelMagnitud extends EntidadAuditable{
	
	private static final long serialVersionUID = -1639311735363990562L;
	
	@Getter
	@Setter
	@Id
	@Column(name = "lecr_id")	
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "lecr_level_name")
	private String nombre;
	
	@Getter
	@Setter
	@Column(name = "lecr_description")
	private String descripcion;
	
}
