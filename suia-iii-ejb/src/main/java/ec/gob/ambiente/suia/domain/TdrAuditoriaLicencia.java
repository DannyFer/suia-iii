package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Filter;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name = "tdr_audi_license", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "tdal_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tdal_status = 'TRUE'")
public class TdrAuditoriaLicencia extends EntidadBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7143660848077728611L;

	@Getter
	@Setter
	@Id
	@Column(name = "tdal_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "tdal_surface_area")
	private double areaSuperficie;

}
