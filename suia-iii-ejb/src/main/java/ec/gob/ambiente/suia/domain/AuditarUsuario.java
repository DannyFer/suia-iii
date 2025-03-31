package ec.gob.ambiente.suia.domain;

import java.util.Date;

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


@Entity
@Table(name = "audit_user", schema = "public")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "aud_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "aud_status = 'TRUE'")
public class AuditarUsuario extends EntidadBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1880467935746431591L;
	
	@Getter
    @Setter
    @Id
    @Column(name = "aud_id")
    @SequenceGenerator(name = "AUD_GENERATOR", initialValue = 1, sequenceName = "seq_aud_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AUD_GENERATOR")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "aud_pass_ante")
	private String passwordAnterior;
	
	@Getter
	@Setter
	@Column(name = "aud_pass_acct")
	private String passwordActual;
	
	@Getter
	@Setter
	@Column(name = "aud_user")
	private Integer usuario;
	
	@Getter
	@Setter
	@Column(name = "aud_date")
	private Date fechaActualizacion;
	
	@Getter
	@Setter
	@Column(name = "aud_description")
	private String descripcion;
	
	@Getter
	@Setter
	@Column(name = "aud_user_mae")
	private Integer usuarioMae;
	
	
}
