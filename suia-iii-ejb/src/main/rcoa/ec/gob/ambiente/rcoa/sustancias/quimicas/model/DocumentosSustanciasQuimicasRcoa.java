package ec.gob.ambiente.rcoa.sustancias.quimicas.model;

import java.io.Serializable;

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

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.rcoa.model.GestionarProductosQuimicosProyectoAmbiental;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "documents_coa_chemical_sustances", schema = "coa_chemical_sustances")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "dosu_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "dosu_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "dosu_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "dosu_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "dosu_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "dosu_status = 'TRUE'")
@NamedQuery(name = "DocumentosSustanciasQuimicasRcoa.findAll", query = "SELECT d FROM DocumentosSustanciasQuimicasRcoa d")
@NamedQueries({
		@NamedQuery(name = DocumentosSustanciasQuimicasRcoa.LISTAR_POR_ID_NOMBRE_TABLA_TIPO, query = "SELECT d FROM DocumentosSustanciasQuimicasRcoa d WHERE d.tipoDocumento.id = :idTipo AND d.estado = true order by d.fechaCreacion DESC"), }

)
public class DocumentosSustanciasQuimicasRcoa extends EntidadAuditable implements Serializable {

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR_POR_ID_NOMBRE_TABLA_TIPO = PAQUETE
			+ "DocumentosSustanciasQuimicasRcoa.listarPorIdNombreTablaTipo";

	private static final long serialVersionUID = -3368958299512581216L;

	@Getter
	@Setter
	@Id
	@Column(name = "dosu_id")
	@SequenceGenerator(name = "documents_coa_chemical_sustances_dosu_id", sequenceName = "documents_coa_chemical_sustances_dosu_id_seq", schema = "coa_chemical_sustances", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "documents_coa_chemical_sustances_dosu_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "dosu_name", length = 255)
	private String nombre;

	@Getter
	@Setter
	@Column(name = "dosu_mime", length = 255)
	private String mime;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "doty_id")
	@ForeignKey(name = "doty_id")
	private TipoDocumento tipoDocumento;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "mach_id")
	@ForeignKey(name = "fk_mach_id")
	private GestionarProductosQuimicosProyectoAmbiental gestionarProductosQuimicosProyectoAmbiental;

	@Getter
	@Setter
	@Column(name = "dosu_alfresco_id", length = 255)
	private String idAlfresco;

	@Getter
	@Setter
	@Column(name = "dosu_extesion", length = 255)
	private String extesion;

	@Column(name = "geca_id")
	@Getter
	@Setter
	private Integer gecaId;

	@Column(name = "dosu_process_instance_id")
	@Getter
	@Setter
	private Integer processinstanceid;

	@Column(name = "dosu_table_id")
	@Getter
	@Setter
	private Integer idTable;

	@Column(name = "dosu_description")
	@Getter
	@Setter
	private String descripcion;

	@Column(name = "dosu_table_class")
	@Getter
	@Setter
	private String nombreTabla;

	@Column(name = "dosu_code_public")
	@Getter
	@Setter
	private String codigoPublico;

	@Getter
	@Setter
	@Column(name = "dosu_observation_bd", length = 255)
	private String observacionDB;

	@Getter
	@Setter
	@Transient
	private boolean contenidoActualizado;

	@Getter
	@Setter
	@Transient
	private byte[] contenidoDocumento;

	public void cargarArchivo(byte[] contenidoDocumento, String nombreArchivo) {
		this.contenidoDocumento = contenidoDocumento;
		this.nombre = nombreArchivo;
		contenidoActualizado = true;
	}

	public DocumentosSustanciasQuimicasRcoa() {
	}

	public DocumentosSustanciasQuimicasRcoa(TipoDocumentoSistema tipo, String nombreTabla, Integer idTabla,
			Long instanciaProceso) {
		this.tipoDocumento = new TipoDocumento();
		tipoDocumento.setId(tipo.getIdTipoDocumento());
		this.nombreTabla = nombreTabla;
		this.idTable = idTabla;
		if(instanciaProceso != null)
			this.processinstanceid = instanciaProceso.intValue();
	}

}
