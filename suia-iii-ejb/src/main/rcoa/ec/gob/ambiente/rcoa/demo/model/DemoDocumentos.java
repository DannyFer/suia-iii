package ec.gob.ambiente.rcoa.demo.model;

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
@Table(name = "demo_documents", schema = "public")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "demo_docu_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "demo_docu_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "demo_docu_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "demo_docu_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "demo_docu_user_update")) })
@NamedQueries({
@NamedQuery(name="DemoDocumentos.findAll", query="SELECT d FROM DemoDocumentos d") })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "demo_docu_status = 'TRUE'")
public class DemoDocumentos extends EntidadAuditable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8690470579271502631L;
	
	private static final String PAQUETE = "ec.gob.ambiente.rcoa.demo.model.";
	
	
	@Getter
	@Setter
	@Id
	@Column(name = "demo_docu_id")
	@SequenceGenerator(name = "PROJECTS_ENVIRONMENTAL_LICENSING_DEMO_DOCU_ID_GENERATOR", sequenceName = "demo_documents_demo_docu_id_seq", schema = "public", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROJECTS_ENVIRONMENTAL_LICENSING_DEMO_DOCU_ID_GENERATOR")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "demo_docu_name", length = 255)
	private String nombreDocumento;
	
	@Getter
	@Setter
	@Column(name = "demo_docu_mime", length = 255)
	private String tipo;
	
	@ManyToOne
	@JoinColumn(name = "doty_id")
	@ForeignKey(name = "doty_id")
	@Getter
	@Setter
	private TipoDocumento tipoDocumento;
	

	@Getter
	@Setter
	@Column(name = "demo_docu_extesion", length = 255)
	private String extencionDocumento;
	
	@Getter
	@Setter
	@Column(name = "demo_docu_alfresco_id", length = 255)
	private String idAlfresco;
	
	@Getter
	@Setter
	@Column(name = "demo_docu_table_id")
	private Integer idTabla;
	
	@Getter
	@Setter
	@Column(name = "demo_docu_table_class")
	private String nombreTabla;
	
	@Getter
	@Setter
	@Transient
	private byte[] contenidoDocumento;
	
}
