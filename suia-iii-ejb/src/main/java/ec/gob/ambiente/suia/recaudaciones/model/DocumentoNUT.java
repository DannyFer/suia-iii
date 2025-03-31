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
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;


@Entity
@Table(name = "documents", schema = "payments")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "docu_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "docu_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "docu_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "docu_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "docu_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
public class DocumentoNUT extends EntidadAuditable {

	private static final long serialVersionUID = 5200035293094691659L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "docu_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "docu_name", length = 255)
	private String nombre;
	
	@Getter
	@Setter
	@Column(name = "docu_mime", length = 255)
	private String mime;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "doty_id")
	@ForeignKey(name = "doty_id")
	private TipoDocumento tipoDocumento;
	
	@Getter
	@Setter
	@Column(name = "docu_alfresco_id", length = 255)
	private String alfrescoId;
	
	@Getter
	@Setter
	@Column(name = "docu_extesion", length = 255)
	private String extesion;
	
	@Getter
	@Setter
	@Column(name = "docu_description")
	private String descripcion;
	
	@Getter
	@Setter
	@Column(name = "docu_table_id")
	private Integer idTabla;
	
	@Getter
	@Setter
	@Column(name = "docu_table_class")
	private String nombreTabla;
	
	@Getter
	@Setter
	@Column(name = "docu_code_public")
	private String codigoPublico;
	
	@Getter
	@Setter
	@Column(name="docu_process_instance_id")
	private Long idProceso;

	@Getter
	@Setter
	@Column(name = "reus_id")
	private Integer solicitudId;
	
	@Getter
	@Setter
	@Column(name = "docu_observation_bd")
	private String observaciones;	
	
	@Getter
	@Setter
	@Transient
	private byte[] contenidoDocumento;
}