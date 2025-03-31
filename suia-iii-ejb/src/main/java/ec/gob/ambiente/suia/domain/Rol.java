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

/**
 * 
 * <b>Entity que representa la tabla roles. </b>
 * 
 * @author pganan
 * @version Revision: 1.0
 *          <p>
 *          [Autor: pganan, Fecha: 22/12/2014]
 *          </p>
 */
@Entity
@Table(name = "roles", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "role_status")) })
@NamedQueries({ @NamedQuery(name = Rol.LISTAR_TODO, query = "SELECT r FROM Rol r ORDER BY r.nombre "),
		@NamedQuery(name = Rol.OBTENER_POR_NOMBRE, query = "SELECT r FROM Rol r WHERE r.nombre = :nombre ") })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "role_status = 'TRUE'")
public class Rol extends EntidadBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String PAQUETE_CLASE = "ec.gob.ambiente.suia.domain.Rol.";
	public static final String LISTAR_TODO = PAQUETE_CLASE + "findAll";
	public static final String OBTENER_POR_NOMBRE = PAQUETE_CLASE + "obtenerPorNombre";

	public static final Integer SUJETO_CONTROL = 4;

	@Id
	@SequenceGenerator(name = "ROLE_ID_GENERATOR", initialValue = 1, sequenceName = "seq_role_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ROLE_ID_GENERATOR")
	@Getter
	@Setter
	@Column(name = "role_id")
	private Integer id;
	@Getter
	@Setter
	@Column(name = "role_name")
	private String nombre;
	@Getter
	@Setter
	@Column(name = "role_description")
	private String descripcion;
	@Getter
	@Setter
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rol")
	private List<RolUsuario> rolUsuarios;
	@Getter
	@Setter
	@Column(name = "role_unique")
	private Boolean unicoPorProvincia;
	@Getter
	@Setter
	@Column(name = "role_used_authority")
	private String autoridadUso;
	@Getter
	@Setter
	@Column(name = "role_sistemas")
	private String usoSistemas;
	@Getter
	@Setter
	@Column(name = "role_unique_zonal_direction")
	private Boolean unicoPorDireccionZonal;
	@Getter
	@Setter
	@Column(name = "role_unique_technical_office")
	private Boolean unicoPorOficinaTecnica;

	public Rol() {
	}

	public Rol(Integer id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Rol)) {
			return false;
		}
		Rol other = (Rol) object;
		return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
	}
}
