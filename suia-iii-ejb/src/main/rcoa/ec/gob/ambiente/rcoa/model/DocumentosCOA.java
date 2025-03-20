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
@Table(name = "documents_coa", schema = "coa_mae")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "docu_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "docu_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "docu_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "docu_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "docu_user_update")) })
@NamedQueries({
@NamedQuery(name="DocumentosCOA.findAll", query="SELECT d FROM DocumentosCOA d"),
@NamedQuery(name=DocumentosCOA.GET_POR_TIPO_Y_ID, query="SELECT d FROM DocumentosCOA d where d.tipoDocumento.id = :idTipo and d.idTabla = :id and d.estado = true order by d.id DESC")})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
public class DocumentosCOA extends EntidadAuditable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8690470579271502631L;
	
	private static final String PAQUETE = "ec.gob.ambiente.rcoa.rcoa.model.";
	
	public static final String GET_POR_TIPO_Y_ID = PAQUETE + "DocumentosCOA.getPorTipoYID";

	@Getter
	@Setter
	@Id
	@Column(name = "docu_id")
	@SequenceGenerator(name = "PROJECTS_ENVIRONMENTAL_LICENSING_DOCU_ID_GENERATOR", sequenceName = "documents_docu_id_seq", schema = "coa_mae", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROJECTS_ENVIRONMENTAL_LICENSING_DOCU_ID_GENERATOR")
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "prco_id")
	@ForeignKey(name = "fk_prco_id")
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Getter
	@Setter
	@Column(name = "docu_name", length = 255)
	private String nombreDocumento;
	
	@Getter
	@Setter
	@Column(name = "docu_mime", length = 255)
	private String tipo;
	
	@ManyToOne
	@JoinColumn(name = "doty_id")
	@ForeignKey(name = "doty_id")
	@Getter
	@Setter
	private TipoDocumento tipoDocumento;
	

	@Getter
	@Setter
	@Column(name = "docu_extesion", length = 255)
	private String extencionDocumento;
	
	@Getter
	@Setter
	@Column(name = "docu_alfresco_id", length = 255)
	private String idAlfresco;
	
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
	@Transient
	private byte[] contenidoDocumento;
	
	@Getter
	@Setter
	@Column(name="docu_process_instance_id")
	private Long idProceso;
	
	@Getter
	@Setter
	@Column(name = "docu_update_number")
	private Integer nroActualizacion = 0;
}
