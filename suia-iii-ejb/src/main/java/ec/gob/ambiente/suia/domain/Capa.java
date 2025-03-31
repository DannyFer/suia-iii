package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * <b> Entity que representa la tabla Layers. </b>
 * 
 * @author veronica 
 * 
 */
@Entity
@Table(name = "layers", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "laye_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "laye_status = 'TRUE'")
public class Capa extends EntidadBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@Column(name = "laye_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "laye_name")
	private String nombre;

}
