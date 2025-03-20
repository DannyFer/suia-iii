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
import javax.persistence.SequenceGenerator;
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
@Table(name = "documents_certificate", schema = "coa_environmental_certificate")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "doce_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "doce_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "doce_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "doce_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "doce_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "doce_status = 'TRUE'")
public class DocumentoCertificadoAmbiental extends EntidadAuditable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5200035293094691659L;

	@Getter
	@Setter
	@Id
	@Column(name = "doce_id")
	@SequenceGenerator(name = "PROJECTS_ENVIRONMENTAL_LICENSING_DOCE_ID_GENERATOR", sequenceName = "documents_certificate_doce_id_seq", schema = "coa_environmental_certificate", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROJECTS_ENVIRONMENTAL_LICENSING_DOCE_ID_GENERATOR")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "doce_name", length = 255)
	private String nombre;
	
	@Getter
	@Setter
	@Column(name = "doce_mime", length = 255)
	private String mime;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "doty_id")
	@ForeignKey(name = "doty_id")
	private TipoDocumento tipoDocumento;
	
	@Getter
	@Setter
	@Column(name = "doce_alfresco_id", length = 255)
	private String alfrescoId;
	
	@Getter
	@Setter
	@Column(name = "doce_extesion", length = 255)
	private String extesion;
	
	@Getter
	@Setter
	@Column(name = "doce_observation_bd", length = 255)
	private String observacionDB;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "pece_id")
	@ForeignKey(name = "fk_pece_id")
	private ProyectoCertificadoAmbiental proyectoCerttificadombiental;
	
	@Column(name = "doce_description")
	@Getter
	@Setter
	private String descripcion; 
	
	
	@Column(name = "doce_table_id")
	@Getter
	@Setter
	private Integer idTable;
	
	@Column(name = "doce_table_class")
	@Getter
	@Setter
	private String nombreTabla;
	
	@Column(name = "doce_code_public")
	@Getter
	@Setter
	private String codigoPublico;

	@Getter
	@Setter
	@Transient
	private byte[] contenidoDocumento;
	
	@Getter
	@Setter
	@Column(name="doce_process_instance_id")
	private Long idProceso;
}
