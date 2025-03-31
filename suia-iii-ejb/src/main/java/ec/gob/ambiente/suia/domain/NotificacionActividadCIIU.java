package ec.gob.ambiente.suia.domain;

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

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "notification_activity_ciiu", schema = "public")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "naciiu_status")) 
})
@NamedQueries({
	@NamedQuery(name = "NotificacionActividadCIIU.findAll", query = "SELECT nac FROM NotificacionActividadCIIU nac"),
	@NamedQuery(name = NotificacionActividadCIIU.FIND_BY_ID, query = "SELECT nac FROM NotificacionActividadCIIU nac WHERE nac.id= :idHigher")
})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "naciiu_status = 'TRUE'")
public class NotificacionActividadCIIU extends EntidadBase {
	
	private static final long serialVersionUID = 1L;
	public static final String FIND_BY_ID = "ec.com.magmasoft.business.domain.NotificacionActividadCIIU.findById";
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="naciiu_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name="naciiu_to")
	private String destinatario;
	
	@Getter
	@Setter
	@Column(name="naciiu_message")
	private String mensaje;
	
	// FOREIGN KEY
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="cont_id")
	private Contacto contacto;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="user_id")
	private Usuario usuario;
	
	@Getter
	@Setter
	@Column(name="orga_id")
	private Integer organizacion;
	
}
