package ec.gob.ambiente.suia.domain;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Filter;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

@NamedQueries({ @NamedQuery(name = Nacionalidad.FIND_BY_STATE, query = "SELECT n FROM Nacionalidad n WHERE n.estado = :estado ORDER BY n.descripcion") })
@Entity
@Table(name = "nationalities", schema = "public")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "nati_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "nati_status = 'TRUE'")
public class Nacionalidad extends EntidadBase {

	private static final long serialVersionUID = 3405797533058898711L;

	public static final String FIND_BY_STATE = "ec.com.magmasoft.business.domain.Nacionalidad.findByState";
	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "NATIONALITIES_GENERATOR", initialValue = 1, sequenceName = "seq_nati_id", schema = "public")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "NATIONALITIES_GENERATOR")
	@Column(name = "nati_id")
	private Integer id;
	@Getter
	@Setter
	@Column(name = "nati_description")
	private String descripcion;

	@Getter
	@Setter
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "nacionalidad")
	private List<Persona> personas;

	public Nacionalidad() {
	}

	public Nacionalidad(Integer id) {
		this.id = id;
	}
}
