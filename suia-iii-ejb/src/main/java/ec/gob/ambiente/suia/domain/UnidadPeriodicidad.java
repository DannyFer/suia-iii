package ec.gob.ambiente.suia.domain;


import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
//import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.Filter;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name = "frecuency_units", schema = "public")
//@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "UnidadPeriodicidad.findAll", query = "SELECT u FROM UnidadPeriodicidad u"),
		@NamedQuery(name = "UnidadPeriodicidad.findById", query = "SELECT u FROM UnidadPeriodicidad u WHERE u.id = :id"),
		@NamedQuery(name = "UnidadPeriodicidad.findByFreunName", query = "SELECT u FROM UnidadPeriodicidad u WHERE u.freunName = :freunName"),
		@NamedQuery(name = "UnidadPeriodicidad.findByFreunDescription", query = "SELECT u FROM UnidadPeriodicidad u WHERE u.freunDescription = :freunDescription"),
		/*@NamedQuery(name = "UnidadPeriodicidad.findByFreunStatus", query = "SELECT u FROM UnidadPeriodicidad u WHERE u.freunStatus = :freunStatus")*/ })
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "freun_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "freun_status = 'TRUE'")
public class UnidadPeriodicidad extends EntidadBase {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "freun_id")
	@Getter
	@Setter
	private Integer id;
	@Size(max = 255)
	@Column(name = "freun_name")
	@Getter
	@Setter
	private String freunName;
	@Size(max = 255)
	@Column(name = "freun_description")
	@Getter
	@Setter
	private String freunDescription;

	@Override
	public String toString() {
		return "ec.gob.ambiente.suia.domain.UnidadPeriodicidad[ Id=" + id + " ]";
	}

}
