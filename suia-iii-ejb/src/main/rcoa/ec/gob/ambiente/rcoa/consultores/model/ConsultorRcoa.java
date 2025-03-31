package ec.gob.ambiente.rcoa.consultores.model;

import java.io.Serializable;
import java.util.List;

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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name="consultant", schema="coa_consultants")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "conu_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "conu_creation_date")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "conu_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "conu_creator_user")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "conu_user_update")) })

@NamedQueries({
@NamedQuery(name=ConsultorRcoa.GET_CONSULTOR_POR_ORGANIZACION, query="SELECT c FROM ConsultorRcoa c where c.idOrganizacion = :idOrganizacion and c.estado = true order by c.id desc"),
@NamedQuery(name=ConsultorRcoa.GET_CONSULTOR_POR_RUC, query="SELECT c FROM ConsultorRcoa c where c.usuario.nombre = :nombre")
})


@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "conu_status = 'TRUE'")
public class ConsultorRcoa extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String GET_CONSULTOR_POR_ORGANIZACION = "ConsultorRcoa.getConsultorPorOrganizacion";
	public static final String GET_CONSULTOR_POR_RUC = "ConsultorRcoa.getConsultorPorRuc";
	
	@Id
	@Getter
	@Setter
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="conu_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="orga_id", insertable = false, updatable = false)
	private Integer idOrganizacion;
	
	@Getter
	@Setter
	@Column(name="conu_status_certificate")
	private Boolean estadoCertificado;
	
	@Getter
	@Setter
	@Column(name="conu_tramit_number")
	private String certificado;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn (name="user_id")
	private Usuario usuario;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn (name="orga_id")
	private Organizacion organizacion;

	@Getter
	@Setter
	@OneToMany(mappedBy = "consultor")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "mute_status = 'TRUE'")
	private List<EquipoMultidisciplinarioConsultor> listaEquipo;
	
	public String getIdentificacion() {
	    return usuario != null ? usuario.getNombre() : null;
	}
	
	public String getNombreConsultor() {
	    if (idOrganizacion == null) {
	        return (usuario != null && usuario.getPersona() != null) ? usuario.getPersona().getNombre() : null;
	    } else {
	        return (organizacion != null) ? organizacion.getNombre() : null;
	    }
	}
	
	public String getRepresentante() {
	    return (idOrganizacion != null) ? organizacion.getPersona().getNombre() : null;
	}
	
	public String getCorreo() {
	    if (usuario == null || usuario.getPersona() == null) return null;
	    List<Contacto> contactos = usuario.getPersona().getContactos();
	    if (contactos == null) return null;
	    for (Contacto contacto : contactos) {
	        if (contacto != null && contacto.getFormasContacto() != null && contacto.getFormasContacto().getId() == 5) {
	            return contacto.getValor();
	        }
	    }
	    return null;
	}

	public String getTelefono() {
	    if (usuario == null || usuario.getPersona() == null) return null;
	    List<Contacto> contactos = usuario.getPersona().getContactos();
	    if (contactos == null) return null;
	    for (Contacto contacto : contactos) {
	        if (contacto != null && contacto.getFormasContacto() != null && contacto.getFormasContacto().getId() == 6) {
	            return contacto.getValor();
	        }
	    }
	    return null;
	}

}