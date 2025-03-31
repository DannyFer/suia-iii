package ec.gob.ambiente.rcoa.viabilidadAmbiental.model;

import java.io.Serializable;

import javax.persistence.*;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the documents_coa_viability database table.
 * 
 */
@Entity
@Table(name="documents_coa_viability", schema = "coa_viability")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "dovi_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "dovi_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "dovi_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "dovi_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "dovi_user_update")) })

@NamedQueries({
@NamedQuery(name="DocumentoViabilidad.findAll", query="SELECT d FROM DocumentoViabilidad d"),
@NamedQuery(name=DocumentoViabilidad.GET_POR_TIPO_Y_TRAMITE, query="SELECT d FROM DocumentoViabilidad d where d.idTipoDocumento = :idTipo and d.idViabilidad = :idTramite and d.estado = true order by d.fechaCreacion DESC"),
@NamedQuery(name=DocumentoViabilidad.GET_POR_TIPOPROYECTOVIABILIDAD, query="SELECT d FROM DocumentoViabilidad d where d.idTipoDocumento = :idTipo and d.idProyectoViabilidad = :idTramite and d.estado = true order by d.fechaCreacion DESC"),
@NamedQuery(name=DocumentoViabilidad.GET_POR_IDTABLA_TIPODOC, query="SELECT d FROM DocumentoViabilidad d WHERE d.idTabla = :idTabla AND d.nombreTabla=:nombreTabla AND d.idTipoDocumento = :idTipo AND d.estado = true order by d.id DESC")
})
public class DocumentoViabilidad extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final String PAQUETE = "ec.gob.ambiente.rcoa.viabilidadAmbiental.model.";
	
	public static final String GET_POR_TIPO_Y_TRAMITE = PAQUETE + "DocumentoViabilidad.getPorTipoYTramite";
	public static final String GET_POR_TIPOPROYECTOVIABILIDAD = PAQUETE + "DocumentoViabilidad.getPorTipoProyectoViabilidad";
	public static final String GET_POR_IDTABLA_TIPODOC = PAQUETE + "DocumentoViabilidad.getPorIdTablaTipoDoc";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="dovi_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="doty_id")
	private Integer idTipoDocumento;

	@Getter
	@Setter
	@Column(name="dovi_alfresco_id")
	private String idAlfresco;

	@Getter
	@Setter
	@Column(name="dovi_extesion")
	private String extension;

	@Getter
	@Setter
	@Column(name="dovi_mime")
	private String mime;

	@Getter
	@Setter
	@Column(name="dovi_name")
	private String nombre;
	
	@Getter
	@Setter
	@Column(name="dovi_description")
	private String descripcion;

	@Getter
	@Setter
	@Column(name="dovi_observation_bd")
	private String observacionBd;
	
	@Getter
	@Setter
	@Column(name="dovi_type_user")
	private Integer tipoUsuario;

	@Getter
	@Setter
	@Transient
	private byte[] contenidoDocumento;
	
	@Getter
	@Setter
	@Column(name="prvi_id")
	private Integer idViabilidad;
	
	@Getter
	@Setter
	@Column(name="prtv_id")
	private Integer idProyectoViabilidad;

	@Getter
	@Setter
	@Column(name = "dovi_table_id")
	private Integer idTabla;
	
	@Getter
	@Setter
	@Column(name = "dovi_table_class")
	private String nombreTabla;

	@Getter
	@Setter
	@Column(name="dovi_process_instance_id")
	private Long idProceso;
	
	@Getter
	@Setter
	@Column(name="dovi_description_attached")
	private String descripcionAnexo;
	
}