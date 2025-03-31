package ec.gob.ambiente.rcoa.agrupacionAutorizaciones.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

/**
 * The persistent class for the documents_grouping database table.
 * 
 */
@Entity
@Table(name="documents_grouping", schema = "coa_grouping")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "dogr_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "dogr_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "dogr_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "dogr_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "dogr_user_update")) })

@NamedQueries({
@NamedQuery(name="DocumentoAgrupacion.findAll", query="SELECT d FROM DocumentoAgrupacion d"),
@NamedQuery(name=DocumentoAgrupacion.GET_POR_ID_TABLA, query="SELECT d FROM DocumentoAgrupacion d where d.idTipoDocumento = :idTipo and d.idTabla = :idTabla and d.estado = true order by d.fechaCreacion DESC"),
})
public class DocumentoAgrupacion extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final String PAQUETE = "ec.gob.ambiente.rcoa.agrupacionAutorizaciones.model.";

	public static final String GET_POR_ID_TABLA = PAQUETE + "DocumentoAgrupacion.getPorIdTabla";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="dogr_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="doty_id")
	private Integer idTipoDocumento;

	@Getter
	@Setter
	@Column(name="dogr_alfresco_id")
	private String idAlfresco;

	@Getter
	@Setter
	@Column(name="dogr_extesion")
	private String extension;

	@Getter
	@Setter
	@Column(name="dogr_mime")
	private String mime;

	@Getter
	@Setter
	@Column(name="dogr_name")
	private String nombre;

	@Getter
	@Setter
	@Column(name="dogr_description")
	private String descripcion;
	
	@Getter
	@Setter
	@Column(name="dogr_table_class")
	private String nombreClaseTable;

	@Getter
	@Setter
	@Column(name="dogr_table_id")
	private Integer idTabla;

	@Getter
	@Setter
	@Column(name="dogr_type_user")
	private Integer tipoUsuario;

	@Getter
	@Setter
	@Transient
	private byte[] contenidoDocumento;
}