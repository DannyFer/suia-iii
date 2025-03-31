package ec.gob.ambiente.rcoa.model;

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

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "documents_environmental_record", schema = "coa_environmental_record")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "doer_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "doer_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "doer_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "doer_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "doer_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "doer_status = 'TRUE'")
public class DocumentoRegistroAmbiental extends EntidadAuditable {

	private static final long serialVersionUID = 5200035293094691659L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "doer_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "doer_name", length = 255)
	private String nombre;
	
	@Getter
	@Setter
	@Column(name = "doer_mime", length = 255)
	private String mime;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "doty_id")
	@ForeignKey(name = "doty_id")
	private TipoDocumento tipoDocumento;
	
	@Getter
	@Setter
	@Column(name = "doer_alfresco_id", length = 255)
	private String alfrescoId;
	
	@Getter
	@Setter
	@Column(name = "doer_extesion", length = 255)
	private String extesion;

	@Getter
	@Setter
	@Column(name = "enre_id")
	private Integer registroAmbientalId;
	
	@Getter
	@Setter
	@Column(name = "doer_description")
	private String descripcion; 

	@Getter
	@Setter
	@Column(name = "doer_type_user")
	private Integer tipoUsuarioId;
	
	@Getter
	@Setter
	@Column(name = "doer_table_id")
	private Integer idTable;
	
	@Getter
	@Setter
	@Column(name = "doer_table_class")
	private String nombreTabla;
	
	@Getter
	@Setter
	@Column(name = "doer_code_public")
	private String codigoPublico;
	
	@Getter
	@Setter
	@Column(name="doer_process_instance_id")
	private Long idProceso;

	@Getter
	@Setter
	@Transient
	private byte[] contenidoDocumento;
	
	@Getter
	@Setter
	@Transient
	private Boolean subido;
}


