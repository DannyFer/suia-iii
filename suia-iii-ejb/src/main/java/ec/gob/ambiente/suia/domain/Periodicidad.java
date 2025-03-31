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
 * <b> Entity Bean tabla periodicidad. </b>
 * 
 * @author vero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: vero $, $Date: 27/06/2015 $]
 *          </p>
 */
@Table(name = "periodicity", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "peri_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "peri_status = 'TRUE'")
@Entity
public class Periodicidad extends EntidadBase {

	/**
	* 
	*/
	private static final long serialVersionUID = -5355627983579760820L;

	@Id
	@Getter
	@Setter
	@Column(name = "peri_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "peri_name")
	private String nombre;

	@Getter
	@Setter
	@Column(name = "peri_description")
	private String descripcion;

	@Override
	public String toString() {
		return nombre;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Periodicidad)) {
			return false;
		}
		Periodicidad other = (Periodicidad) obj;
		if (((this.id == null) && (other.id != null)) || ((this.id != null) && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}
}
