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

@Table(name = "waste_management_phases", schema = "suia_iii")
@Entity
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "wmph_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wmph_status = 'TRUE'")
public class FaseGestionDesecho extends EntidadBase {

	private static final long serialVersionUID = 5372041677126236373L;

	public static final int FASE_ALMACENAMIENTO = 1;
	public static final int FASE_TRANSPORTE = 2;
	public static final int FASE_ELIMINACION = 3;
	public static final int FASE_DISPOSICION_FINAL = 4;

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "WASTE_MANAGEMENT_PHASES_WAPH_ID_GENERATOR", sequenceName = "seq_wmph_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WASTE_MANAGEMENT_PHASES_WAPH_ID_GENERATOR")
	@Column(name = "wmph_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "wmph_name")
	private String nombre;

	@Override
	public String toString() {
		return nombre;
	}
}
