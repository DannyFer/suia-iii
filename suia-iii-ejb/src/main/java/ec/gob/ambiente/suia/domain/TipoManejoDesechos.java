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


/***
 * 
 * <b>
 *Entity Bean.
 * </b>
 *  
 * @author vero
 * @version $Revision: 1.0 $ <p>[$Author: vero $, $Date: 01/07/2015 $]</p>
 */
@Table(name = "type_waste_management", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "tywm_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tywm_status = 'TRUE'")
@Entity
public class TipoManejoDesechos extends EntidadBase {
	


	/**
	* 
	*/ 
	private static final long serialVersionUID = 3890041652458641837L;

	@Id
	@Getter
	@Setter
	@Column(name = "tywm_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "tywm_name")
	private String nombre;
	
	@Getter
	@Setter
	@Column(name = "tywm_type")
	private String tipo;

	@Override
	public String toString() {
		return nombre;
	}
}
