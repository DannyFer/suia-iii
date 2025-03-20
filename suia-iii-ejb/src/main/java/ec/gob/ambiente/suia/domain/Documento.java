package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 
 * <b> Entity que representa la tabla documents. </b>
 * 
 * @author carlos.pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: carlos.pupo, Fecha: 07/01/2015]
 *          </p>
 */
@Entity
@Table(name = "documents", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "docu_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "docu_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "docu_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "docu_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "docu_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
@NamedQueries({
		@NamedQuery(name = Documento.LISTAR_POR_ID_NOMBRE_TABLA, query = "SELECT d FROM Documento d WHERE d.nombreTabla = :nombreTabla AND d.idTable = :idTabla AND d.estado = true AND d.idHistorico = null order by d.fechaCreacion"),
		@NamedQuery(name = Documento.LISTAR_POR_ID_NOMBRE_TABLA_TIPO, query = "SELECT d FROM Documento d WHERE d.nombreTabla = :nombreTabla AND d.idTable = :idTabla AND d.tipoDocumento.id = :idTipo AND d.estado = true AND d.idHistorico = null order by d.fechaCreacion DESC"),
		@NamedQuery(name = Documento.LISTAR_POR_ID_NOMBRE_TABLA_LISTA_TIPO, query = "SELECT d FROM Documento d WHERE d.nombreTabla = :nombreTabla AND d.idTable = :idTabla AND d.tipoDocumento.id in :idTipos AND d.estado = true AND d.idHistorico = null order by d.fechaCreacion DESC"),
		@NamedQuery(name = Documento.LISTAR_POR_IDS_NOMBRE_TABLA_LISTA_TIPO, query = "SELECT d FROM Documento d WHERE d.nombreTabla = :nombreTabla AND d.idTable in :idTabla AND d.tipoDocumento.id in :idTipos AND d.estado = true order by d.fechaCreacion DESC"),
		@NamedQuery(name = Documento.LISTAR_TODO_POR_ID_NOMBRE_TABLA, query = "SELECT d FROM Documento d WHERE d.nombreTabla = :nombreTabla AND d.idTable = :idTabla AND d.estado = true order by d.fechaCreacion DESC"),		
		@NamedQuery(name = Documento.LISTAR_TODOS_POR_ID_NOMBRE_TABLA_TIPO, query = "SELECT d FROM Documento d WHERE d.nombreTabla = :nombreTabla AND d.idTable = :idTabla AND d.tipoDocumento.id = :idTipo AND d.estado = true order by d.fechaCreacion DESC"),
		@NamedQuery(name = Documento.LISTAR_HISTORIAL_POR_ID_NOMBRE_TABLA_LISTA_TIPO, query = "SELECT d FROM Documento d WHERE d.nombreTabla = :nombreTabla AND d.idTable = :idTabla AND d.tipoDocumento.id in :idTipos AND d.estado = true AND d.idHistorico is not null order by d.fechaCreacion DESC"),
}

)
public class Documento extends EntidadAuditable implements Cloneable {

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR_POR_ID_NOMBRE_TABLA = PAQUETE + "Documento.listarPorIdNombreTabla";
	public static final String LISTAR_POR_ID_NOMBRE_TABLA_TIPO = PAQUETE + "Documento.listarPorIdNombreTablaTipo";
	public static final String LISTAR_POR_ID_NOMBRE_TABLA_LISTA_TIPO = PAQUETE + "Documento.listarPorIdNombreTablaListaTipo";
	public static final String LISTAR_POR_IDS_NOMBRE_TABLA_LISTA_TIPO = PAQUETE + "Documento.listarPorIdsNombreTablaListaTipo";
	public static final String LISTAR_TODO_POR_ID_NOMBRE_TABLA = PAQUETE + "Documento.listarTodoPorIdNombreTabla";
	public static final String LISTAR_TODOS_POR_ID_NOMBRE_TABLA_TIPO = PAQUETE + "Documento.listarTodosPorIdNombreTablaTipo";
	public static final String LISTAR_HISTORIAL_POR_ID_NOMBRE_TABLA_LISTA_TIPO = PAQUETE + "Documento.listarHistorialPorIdNombreTablaListaTipo";

	private static final long serialVersionUID = -1948131919746177457L;

	@Id
	@SequenceGenerator(name = "DOCUMENTS_DOCUID_GENERATOR", sequenceName="seq_docu_id", schema="suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DOCUMENTS_DOCUID_GENERATOR")
	@Column(name = "docu_id")
	@Getter
	@Setter
	private Integer id;

	@Column(name = "docu_address_alfresco")
	@Getter
	@Setter
	private String direccionAlfresco;

	@Column(name = "docu_code_public")
	@Getter
	@Setter
	private String codigoPublico;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "docu_date_generation_document")
	@Getter
	@Setter
	private Date fechaGeneracion;

	@Column(name = "docu_description")
	@Getter
	@Setter
	private String descripcion;

	@Column(name = "docu_extesion")
	@Getter
	@Setter
	private String extesion;

	@Column(name = "docu_mime")
	@Getter
	@Setter
	private String mime;

	@Column(name = "docu_name")
	@Getter
	@Setter
	private String nombre;

	@Column(name = "docu_alfresco_id", unique = true)
	@Getter
	@Setter
	private String idAlfresco;

	@Column(name = "docu_table_id")
	@Getter
	@Setter
	private Integer idTable;

	@Column(name = "docu_table_class")
	@Getter
	@Setter
	private String nombreTabla;

	@Transient
	@Getter
	@Setter
	private boolean editar;

	@ManyToOne
	@JoinColumn(name = "doty_id")
	@ForeignKey(name = "fk_documentsdocu_id_type_documentstydo_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tydo_status = 'TRUE'")
	@Getter
	@Setter
	private TipoDocumento tipoDocumento;

	@OneToMany(mappedBy = "documento")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Getter
	@Setter
	private List<DocumentosTareasProceso> documentosTareasProcesos;

	@OneToMany(mappedBy = "documento")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prdo_status = 'TRUE'")
	@Getter
	@Setter
	private List<DocumentoProyecto> documentoProyectos;

	@Getter
	@Setter
	@Transient
	private byte[] contenidoDocumento;
	
	@Getter
	@Setter
	@Transient
	private File contenidoDocumentoFile;

	@Transient
	@Getter
	@Setter
	private String tipoContenido;
	
	@Transient
	@Getter
	@Setter
	private Date fechaModificacion;

	@Getter
	@Setter
	@Column(name = "docu_historical_id")
	private Integer idHistorico;
	
	@Getter
	@Setter
	@Column(name = "docu_notification_number")
	private Integer numeroNotificacion;
	
	@Getter
	@Setter
	@Transient
	private Boolean nuevoEnModificacion;
	
	@Getter
	@Setter
	@Transient
	private Boolean registroModificado;
	
	@Getter
	@Setter
	@Transient
	private Integer idProyecto;
	
	@Override
	public Documento clone() throws CloneNotSupportedException {

		 Documento clone = (Documento)super.clone();
		 clone.setId(null);		 
		 return clone;
	}
	
	@Override
	public String toString() {
		return "Documento [id=" + id + ", direccionAlfresco=" + direccionAlfresco + ", codigoPublico=" + codigoPublico
				+ ", fechaGeneracion=" + fechaGeneracion + ", descripcion=" + descripcion + ", extesion=" + extesion
				+ ", mime=" + mime + ", nombre=" + nombre + ", idAlfresco=" + idAlfresco + ", idTable=" + idTable
				+ ", nombreTabla=" + nombreTabla + ", editar=" + editar + ", tipoDocumento=" + tipoDocumento
				+ ", documentosTareasProcesos=" + documentosTareasProcesos + ", documentoProyectos="
				+ documentoProyectos + ", contenidoDocumento=" + Arrays.toString(contenidoDocumento)
				+ ", contenidoDocumentoFile=" + contenidoDocumentoFile + ", tipoContenido=" + tipoContenido
				+ ", fechaModificacion=" + fechaModificacion + ", idHistorico=" + idHistorico + ", numeroNotificacion="
				+ numeroNotificacion + ", nuevoEnModificacion=" + nuevoEnModificacion + ", registroModificado="
				+ registroModificado + "]";
	}
}