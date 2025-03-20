package ec.gob.ambiente.rcoa.participacionCiudadana.model;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "facilitators_ppc", schema = "coa_citizen_participation")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "fapc_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "fapc_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "fapc_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "fapc_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "fapc_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "fapc_status = 'TRUE'")
public class FacilitadorPPC extends EntidadAuditable{

	private static final long serialVersionUID = 8332880928622567456L;
	
	@Getter
	@Setter
	@Id
	@Column(name = "fapc_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "prco_id")
	@ForeignKey(name = "pk_project_licencing_coa_fapc")	
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "user_id")
	@ForeignKey(name = "fk_facilitators_ppc_user_id")
	private Usuario usuario;
	
	@Getter
	@Setter
	@Column(name = "fapc_acceptation")
	private Boolean aceptaProyecto;
	
	@Getter
	@Setter
	@Column(name = "fapc_principal")
	private Boolean esPrincipal;
	
	@Getter
	@Setter
	@Column(name = "fapc_additional")
	private Boolean esAdicional;
	
	@Getter
	@Setter
	@Column(name = "fapc_project_accept_date")
	private Date fechaAceptaProyecto;
	
	@Getter
	@Setter
	@Column(name = "fapc_justification")
	private String justificacion;
	
	@Getter
	@Setter
	@Column(name="fapc_special_rate")
	private Boolean tarifaEspecial;
	
	@Getter
	@Setter
	@Column(name = "fapc_payment_date")
	private Date fechaRegistroPago;
}
