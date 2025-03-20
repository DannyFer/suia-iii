package ec.gob.ambiente.suia.recaudaciones.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.InstitucionFinanciera;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * <b> Entity que representa la tabla SolicitudUsuario. </b>
 * 
 * @author Santiago Flores
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Santiago Flores, Fecha: 29/05/2019]
 *          </p>
 */
@Entity
@Table(name = "request_user", schema = "payments")
@NamedQueries({
		@NamedQuery(name = SolicitudUsuario.LISTAR_TODOS, query = "SELECT c FROM SolicitudUsuario c WHERE c.estado= true"),
		@NamedQuery(name = SolicitudUsuario.LISTAR_SOLICITUD_POR_TRAMITE, query = "SELECT c FROM SolicitudUsuario c WHERE c.estado= true and c.solicitudCodigo = :tramite")})
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "reus_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "reus_date_create")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "reus_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "reus_user_create")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "reus_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "reus_status = 'TRUE'")
public class SolicitudUsuario extends EntidadAuditable {

	private static final long serialVersionUID = -1756612644167548934L;

	public static final String LISTAR_TODOS = "ec.com.magmasoft.business.domain.SolicitudUsuario.listarTodos";
	public static final String LISTAR_SOLICITUD_POR_TRAMITE = "ec.com.magmasoft.business.domain.SolicitudUsuario.listarSolicitudPorTramite";

	@Getter
	@Setter
	@Id
	@Column(name = "reus_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "reus_request_user")
	private String solicitudDescripcion;

	@Getter
	@Setter
	@Column(name = "reus_request")
	private String solicitudCodigo;

	@Getter
    @Setter
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @ManyToOne
    @ForeignKey(name = "fk_users_user_id_request_user_user_id")
    private Usuario usuario;
	
	@Getter
	@Setter
	@Column(name = "reus_user_other_system")
	private String usuarioOtroSistema;
	
	@Getter
    	@Setter
	@JoinColumn(name = "fini_id", referencedColumnName = "fini_id")
	@ManyToOne
	@ForeignKey(name = "fk_fini_id")
	private InstitucionFinanciera institucionFinanciera;
	
	@Getter
	@Setter
	@Column(name = "reus_value")
	private Double valorTotal;
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SolicitudUsuario)) {
			return false;
		}
		SolicitudUsuario other = (SolicitudUsuario) obj;
		if (((this.id == null) && (other.id != null)) || ((this.id != null) && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return getSolicitudDescripcion();
	}

}
