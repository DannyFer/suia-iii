package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;

import javax.persistence.*;

/**
 * 
 * <b> Entity Bean. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: Feb 2, 2015]
 *          </p>
 */
@Table(name = "study_types", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "stty_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "stty_status = 'TRUE'")
@Entity
public class TipoEstudio extends EntidadBase implements Comparable<TipoEstudio> {

	private static final long serialVersionUID = -5336099296136945923L;

	public static final int ESTUDIO_EX_ANTE = 1;
	public static final int ESTUDIO_EX_POST = 2;
	public static final int AUDITORIA_FINES_LICENCIAMIENTO = 3;
	public static final int ESTUDIO_EMISION_INCLUSION_AMBIENTAL = 4;

	@Id
	@SequenceGenerator(name = "STUDY_TYPES_GENERATOR", schema = "suia_iii", sequenceName = "seq_stty_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "STUDY_TYPES_GENERATOR")
	@Getter
	@Setter
	@Column(name = "stty_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "stty_name")
	private String nombre;

	@Override
	public String toString() {
		return nombre;
	}

	@Override
	public int compareTo(TipoEstudio o) {
		if (o == null)
			return -1;
		return getId().compareTo(o.getId());
	}

}
