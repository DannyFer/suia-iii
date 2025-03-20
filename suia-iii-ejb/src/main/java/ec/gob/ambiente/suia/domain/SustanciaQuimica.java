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

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Table(name = "chemical_sustances", schema = "suia_iii")
@Entity
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "chsu_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "chsu_status = 'TRUE'")
public class SustanciaQuimica extends EntidadBase {

	private static final long serialVersionUID = -205019976556057709L;

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "CHEMICAL_SUSTANCES_CHSU_ID_GENERATOR", sequenceName = "seq_chsu_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CHEMICAL_SUSTANCES_CHSU_ID_GENERATOR")
	@Column(name = "chsu_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "chsu_code")
	private String codigo;
	
	@Getter
	@Setter
	@Column(name = "chsu_name")
	private String nombre;

	@Override
	public String toString() {
		return nombre;
	}
}
