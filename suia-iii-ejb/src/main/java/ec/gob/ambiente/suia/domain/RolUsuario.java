package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * <b> Entity que representa la tabla roles_usuarios. </b>
 * 
 * @author pganan
 * @version Revision: 1.0
 *          <p>
 *          [Autor: pganan, Fecha: 22/12/2014]
 *          </p>
 */
@Entity
@Table(name = "roles_users", schema = "suia_iii")
@NamedQueries({
		@NamedQuery(name = RolUsuario.FIND_BY_USER, query = "SELECT ru.rol FROM RolUsuario ru WHERE ru.idUsuario = :usuario"),
		@NamedQuery(name = RolUsuario.COUNT_ROL_USUARIO, query = "SELECT count(ru) FROM RolUsuario ru WHERE ru.idRol = :idRol AND ru.idUsuario <> :idUsuario"),
		@NamedQuery(name = RolUsuario.FIND_BY_USER_ROLE, query = "SELECT ru FROM RolUsuario ru WHERE ru.idUsuario = :usuario") })
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "rous_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "rous_status = 'TRUE'")
public class RolUsuario extends EntidadBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String FIND_BY_USER = "ec.com.magmasoft.business.domain.RolUsuario.findByUser";
	public static final String COUNT_ROL_USUARIO = "ec.com.magmasoft.business.domain.RolUsuario.countRolUsuario";
	public static final String FIND_BY_USER_ROLE = "ec.com.magmasoft.business.domain.RolUsuario.findByUserRol";

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "ROUS_ID_GENERATOR", initialValue = 1, sequenceName = "seq_rous_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ROUS_ID_GENERATOR")
	@Column(name = "rous_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@ForeignKey(name = "fk_roles_user_role_id_roles_role_id")
	@JoinColumn(name = "role_id")
	private Rol rol;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@ForeignKey(name = "fk_roles_user_user_id_user_user_id")
	@JoinColumn(name = "user_id")
	private Usuario usuario;

	@Getter
	@Setter
	@Column(name = "user_id", insertable = false, updatable = false)
	private Integer idUsuario;

	@Getter
	@Setter
	@Column(name = "role_id", insertable = false, updatable = false)
	private Integer idRol;
	
	
	@Getter
	@Setter
	@Column(name = "rous_description")
	private String descripcion;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return rol != null ? rol.getNombre() : "<Eliminado>";
	}
}
