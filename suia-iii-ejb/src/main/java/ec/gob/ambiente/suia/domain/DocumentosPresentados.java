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
@Table(name = "documents_submitted", schema = "alert_notification")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "dosu_status"))})
//		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "dosu_user_creation_date")),
//		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "dosu_user_modification_date")),
//		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "user_id_creation")),
//		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "user_id_modification")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "dosu_status = 'TRUE'")

public class DocumentosPresentados extends EntidadBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name = "DOCUMENTS_SUBMIT_GENERATOR", sequenceName="documents_submitted_dosu_id_seq ", schema="alert_notification", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DOCUMENTS_SUBMIT_GENERATOR")
	@Column(name = "dosu_id")
	@Getter
	@Setter
	private Integer id;
	
	@Column(name = "dosu_present")
	@Getter
	@Setter
	private boolean documentoPresentado;
	
	@ManyToOne
	@JoinColumn(name = "pren_id")
	@ForeignKey(name = "pren__id")
    @Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental;
	
	@ManyToOne
	@JoinColumn(name = "cado_id")
	@ForeignKey(name = "cado_id")
    @Getter
	@Setter
	private CatalogoDocumento catalogoDocumento;

	@Column(name = "dosu_date_duration")
	@Getter
	@Setter
	private Date fechaPresentacionDoc;
	
	@Column(name = "dosu_emission_date")
	@Getter
	@Setter
	private Date fechaEmision;
		
	@Column(name = "user_id_creation")
	@Getter
	@Setter
	private Integer usuarioCreado;
	
	@Column(name = "user_id_modification")
	@Getter
	@Setter
	private Integer usuarioModificado;
	
	@Column(name = "dosu_user_creation_date")
	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaUsuarioCreado;
	
	
	@Column(name = "dosu_user_modification_date")
	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaUsuarioModificado;
		
	public DocumentosPresentados() {
    }
	
	public DocumentosPresentados(Integer id) {
		this.id=id;
    }
}
