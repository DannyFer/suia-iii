package ec.gob.ambiente.retce.model;

import java.io.Serializable;
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
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
/**
 * The persistent class for the retce.services database table.
 * 
 */
@Entity
@Table(name="inclusion", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "incl_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "incl_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "incl_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "incl_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "incl_user_update")) })
public class Inclusion extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="incl_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name="incl_inclusion_number")
	private String numeroInclusion;
	
	@Getter
	@Setter
	@Column(name="incl_inclusion_date")
	private Date fechaInclusion;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="proj_id")
	private InformacionProyecto informacionProyecto;
	
	@Transient
	@Getter
	@Setter
	private Documento documento;
	
	@Transient
	@Getter
	@Setter
	private Documento documentoEstudio;
	
	@Getter
	@Setter
	@Column(name="incl_revision_date")
	private Date fechaRevision;
	
	@Getter
	@Setter
	@Column(name="incl_revision_user")
	private String usuarioRevision;
	
	@Getter
	@Setter
	@Column(name="incl_validated_information")
	private Boolean informacionValidada;
    
    @Getter
	@Setter
	@ManyToOne
    @JoinColumn(name="fase_id")
	private FaseRetce faseRetce;

}
