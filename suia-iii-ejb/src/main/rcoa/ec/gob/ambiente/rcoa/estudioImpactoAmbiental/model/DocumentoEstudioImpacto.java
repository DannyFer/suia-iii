package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model;

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
@Table(name = "documents_impact_study", schema = "coa_environmental_impact_study")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "dois_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "dois_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "dois_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "dois_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "dois_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "dois_status = 'TRUE'")
public class DocumentoEstudioImpacto extends EntidadAuditable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5200035293094691659L;

	@Getter
	@Setter
	@Id
	@Column(name = "dois_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "dois_name", length = 255)
	private String nombre;
	
	@Getter
	@Setter
	@Column(name = "dois_mime", length = 255)
	private String mime;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "doty_id")
	@ForeignKey(name = "doty_id")
	private TipoDocumento tipoDocumento;
	
	@Getter
	@Setter
	@Column(name = "dois_alfresco_id", length = 255)
	private String alfrescoId;
	
	@Getter
	@Setter
	@Column(name = "dois_extesion", length = 255)
	private String extesion;
	
	@Getter
	@Setter
	@Column(name = "dois_observation_bdd", length = 255)
	private String observacionDB;
	
	@Getter
	@Setter
	@Column(name = "dois_description")
	private String descripcion;
	
	@Getter
	@Setter
	@Column(name = "dois_table_id")
	private Integer idTable;
	
	@Getter
	@Setter
	@Column(name = "dois_tabla_class")
	private String nombreTabla;
	
	@Getter
	@Setter
	@Column(name = "dois_code_public")
	private String codigoPublico;
	
	@Getter
	@Setter
	@Column(name="dois_process_instance_id")
	private Long idProceso;

	@Getter
	@Setter
	@Transient
	private byte[] contenidoDocumento;
	
	@Getter
	@Setter
	@Transient
	private boolean contenidoActualizado;
	
	public void cargarArchivo(byte[] contenidoDocumento,String nombreArchivo) {
		this.contenidoDocumento=contenidoDocumento;
		this.nombre=nombreArchivo;
		contenidoActualizado=true;
	}
	
}