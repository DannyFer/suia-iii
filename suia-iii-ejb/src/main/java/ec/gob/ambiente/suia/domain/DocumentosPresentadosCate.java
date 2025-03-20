package ec.gob.ambiente.suia.domain;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name = "documents_submitted_hidrocarbons", schema = "alert_notification")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "dohi_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "dohi_status = 'TRUE'")
public class DocumentosPresentadosCate extends EntidadBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = -397300880112318540L;
	
	
	@Id
	@SequenceGenerator(name = "DOCUMENTS_SUBMIT_CAT_GENERATOR", sequenceName="documents_submitted_hidrocarbons_dohi_id_seq ", schema="alert_notification", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DOCUMENTS_SUBMIT_CAT_GENERATOR")
	@Column(name = "dohi_id")
	@Getter
	@Setter
	private Integer id;
	
	@Column(name = "dohi_present")
	@Getter
	@Setter
	private boolean documentoPresentadoCat;
	
	@Column(name = "dohi_project_code")
	@Getter
	@Setter
	private String codigoProyectoCat;

	@ManyToOne
	@JoinColumn(name = "cado_id")
	@ForeignKey(name = "cado___id")
    @Getter
	@Setter
	private CatalogoDocumento catalogoDocumentocat;
	
	@Column(name = "dohi_date_duration")
	@Getter
	@Setter
	private Date fechaPresentacionDocCat;
	
	@Column(name = "user_id_creation")
	@Getter
	@Setter
	private Integer usuarioCreadoCat;
	
	@Column(name = "user_id_modification")
	@Getter
	@Setter
	private Integer usuarioModificadoCat;
	
	@Column(name = "dohi_user_creation_date")
	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaUsuarioCreadoCat;
	
	
	@Column(name = "dohi_user_modification_date")
	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaUsuarioModificadoCat;
	
	public DocumentosPresentadosCate() {
    }
	
	public DocumentosPresentadosCate(Integer id) {
		this.id=id;
    }
	
}
