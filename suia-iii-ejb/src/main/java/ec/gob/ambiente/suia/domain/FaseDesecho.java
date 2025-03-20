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

@Table(name = "waste_phases", schema = "suia_iii")
@Entity
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "waph_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "waph_status = 'TRUE'")
public class FaseDesecho extends EntidadBase {

	private static final long serialVersionUID = 5124195794013501177L;

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "WASTE_PHASES_WAPH_ID_GENERATOR", sequenceName = "seq_waph_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WASTE_PHASES_WAPH_ID_GENERATOR")
	@Column(name = "waph_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "waph_name")
	private String nombre;

	@Override
	public String toString() {
		return nombre;
	}
}
