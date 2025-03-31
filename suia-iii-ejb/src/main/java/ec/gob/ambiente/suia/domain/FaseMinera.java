package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Table(name="mining_phase", schema = "suia_iii")
@Entity
@NamedQuery(name = FaseMinera.FIND_ALL, query = "SELECT f FROM FaseMinera f WHERE f.estado = true order by f.nombre")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "miph_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "miph_status = 'TRUE'")
public class FaseMinera extends EntidadBase{
	
	private static final long serialVersionUID = -6003795448672784917L;
	
	public static final String FIND_ALL = "ec.gob.ambiente.suia.domain.FaseMinera.findAll";
	
	public static final String BENEFICIO = "1";
	public static final String EXPLORACION = "2";
	public static final String EXPLOTACION = "3";
	
	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "MINING_PHASES_MIPH_ID_GENERATOR", sequenceName = "seq_miph_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MINING_PHASES_MIPH_ID_GENERATOR")
	@Column(name = "miph_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "miph_name")
	private String nombre;

	@Override
	public String toString() {
		return nombre;
	}

}
