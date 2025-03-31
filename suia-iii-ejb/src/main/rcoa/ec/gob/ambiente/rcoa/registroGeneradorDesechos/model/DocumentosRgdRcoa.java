package ec.gob.ambiente.rcoa.registroGeneradorDesechos.model;

import java.io.File;
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
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

/**
 * The persistent class for the documents_coa_waste_generator_record database
 * table.
 * 
 */
@Entity
@Table(name = "documents_coa_waste_generator_record", schema = "coa_waste_generator_record")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "dowa_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "dowa_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "dowa_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "dowa_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "dowa_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "dowa_status = 'TRUE'")
@NamedQuery(name = "DocumentosRgdRcoa.findAll", query = "SELECT d FROM DocumentosRgdRcoa d")
@NamedQueries({
		@NamedQuery(name = DocumentosRgdRcoa.LISTAR_POR_ID_NOMBRE_TABLA_TIPO, query = "SELECT d FROM DocumentosRgdRcoa d WHERE d.tipoDocumento.id = :idTipo AND d.estado = true AND d.idTable = :idTable AND d.nombreTabla = :nombreTabla order by d.fechaCreacion DESC"), }

)

public class DocumentosRgdRcoa extends EntidadAuditable implements Serializable {

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR_POR_ID_NOMBRE_TABLA_TIPO = PAQUETE
			+ "DocumentosRgdRcoa.listarPorIdNombreTablaTipo";

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "dowa_id")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "doty_id")
	@ForeignKey(name = "fk_documentsdocu_id_type_documentstydo_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tydo_status = 'TRUE'")
	@Getter
	@Setter
	private TipoDocumento tipoDocumento;

	@Getter
	@Setter
	@Column(name = "dowa_alfresco_id")
	private String idAlfresco;

	@Getter
	@Setter
	@Column(name = "dowa_extesion")
	private String extesion;

	@Getter
	@Setter
	@Column(name = "dowa_mime")
	private String mime;

	@Getter
	@Setter
	@Column(name = "dowa_name")
	private String nombre;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "ware_id")
	private RegistroGeneradorDesechosRcoa registroGeneradorDesechosRcoa;

	@Getter
	@Setter
	@Column(name = "dowa_type_user")
	private Integer tipoUsuario; // Tipo de usuario que almacena. 1 operador, 2 tecnico, 3 director

	@Getter
	@Setter
	@Column(name = "dowa_code_public")
	private String codigoPublico;

	@Column(name = "dowa_table_id")
	@Getter
	@Setter
	private Integer idTable;

	@Column(name = "dowa_table_class")
	@Getter
	@Setter
	private String nombreTabla;

	@Column(name = "dowa_description")
	@Getter
	@Setter
	private String descripcion;

	@Column(name = "dowa_process_instance_id")
	@Getter
	@Setter
	private Integer idProceso;

	public DocumentosRgdRcoa() {
	}

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
	private boolean subido;

}