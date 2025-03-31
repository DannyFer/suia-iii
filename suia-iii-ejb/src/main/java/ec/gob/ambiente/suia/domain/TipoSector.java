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
@NamedQueries({
		@NamedQuery(name = TipoSector.FIND_BY_ID_IN_LIST, query = "select s FROM TipoSector s where s.id in :ids")
})
@Table(name = "sector_types", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "sety_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "sety_status = 'TRUE'")
@Entity
public class TipoSector extends EntidadBase {

	public static final String FIND_BY_ID_IN_LIST = "ec.gob.ambiente.suia.domain.TipoSector.findByIdInList";

	private static final long serialVersionUID = -1393294198751084762L;

	public static final int TIPO_SECTOR_HIDROCARBUROS = 1;
	public static final int TIPO_SECTOR_MINERIA = 2;
	public static final int TIPO_SECTOR_OTROS = 3;
	public static final int TIPO_SECTOR_ELECTRICO = 4;
	public static final int TIPO_SECTOR_LICENCIAMIENTO = 5;
	public static final int TIPO_SECTOR_TELECOMUNICACIONES = 6;
	public static final int TIPO_SECTOR_SANEAMIENTO = 7;

	@Id
	@SequenceGenerator(name = "SECTOR_TYPES_GENERATOR", schema = "suia_iii", sequenceName = "seq_sety_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SECTOR_TYPES_GENERATOR")
	@Getter
	@Setter
	@Column(name = "sety_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "sety_name")
	private String nombre;

	public TipoSector(int id) {
		this.id = id;
	}

	public TipoSector() {
	}

	@Override
	public String toString() {
		return nombre;
	}
}
