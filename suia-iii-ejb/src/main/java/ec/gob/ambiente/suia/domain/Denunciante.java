package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "complainants", schema = "suia_iii")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "comp_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "comp_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "comp_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "comp_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "comp_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "comp_status = 'TRUE'")
public class Denunciante extends EntidadAuditable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Setter
	@Getter
	@SequenceGenerator(name = "COMPLAINANT_GENERATOR", sequenceName = "seq_campla_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COMPLAINANT_GENERATOR")
	@Column(name = "comp_id")
	@Id
	private Integer id;

	@Setter
	@Getter
	// @Size(max = 10)
	// @Pattern(regexp = "[1-9]*", message = "Debe ingresar numeros")
	@Column(name = "comp_identification")
	private String identificacion;

	@Setter
	@Getter
	@Size(max = 255)
	@Pattern(regexp = "[a-z]*", message = "Debe ingresar solo texto")
	@Column(name = "comp_name")
	private String nombre;

	@Setter
	@Getter
	@Column(name = "comp_phone")
	private String telefono;

	@Setter
	@Getter
	@Column(name = "comp_address")
	private String direccion;

}
