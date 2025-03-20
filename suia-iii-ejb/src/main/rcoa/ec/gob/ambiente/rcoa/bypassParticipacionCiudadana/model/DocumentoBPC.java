package ec.gob.ambiente.rcoa.bypassParticipacionCiudadana.model;

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

import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="documents_bypass", schema = "coa_bypass_ppc")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "doby_status")),	
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "doby_creator_user")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "doby_creation_date")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "doby_user_update")), 
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "doby_date_update"))
})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "doby_status = 'TRUE'")
@NamedQueries({
	@NamedQuery(name = DocumentoBPC.LISTAR_POR_ID_NOMBRE_TABLA, query = "SELECT d FROM DocumentoBPC d WHERE d.nombreTabla = :nombreTabla AND d.idTabla = :idTabla AND d.estado = true order by d.fechaCreacion"),
	@NamedQuery(name = DocumentoBPC.LISTAR_POR_ID_NOMBRE_TABLA_TIPO, query = "SELECT d FROM DocumentoBPC d WHERE d.nombreTabla = :nombreTabla AND d.idTabla = :idTabla AND d.tipoDocumento.id = :idTipo AND d.estado = true order by d.fechaCreacion DESC")
})

@Getter
@Setter
public class DocumentoBPC extends EntidadAuditable {
	
	private static final String PAQUETE = "ec.gob.ambiente.rcoa.bypassParticipacionCiudadana.";
	public static final String LISTAR_POR_ID_NOMBRE_TABLA = PAQUETE + "DocumentoBPC.listarPorIdNombreTabla";
	public static final String LISTAR_POR_ID_NOMBRE_TABLA_TIPO = PAQUETE + "DocumentoBPC.listarPorIdNombreTablaTipo";
	
	private static final long serialVersionUID = 1L;	

	@Id
	@Getter
	@Setter
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "doby_id")
	private Integer id;	

	@Getter
	@Setter
	@Column(name="doby_name")
	private String nombreDocumento;
	
	@Getter
	@Setter
	@Column(name="doby_mime")
	private String mimeDocumento;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="doty_id")
	private TipoDocumento tipoDocumento;
	
	@Getter
	@Setter
	@Column(name="doby_extesion")
	private String extencionDocumento;

	@Getter
	@Setter
	@Column(name="doby_alfresco_id")
	private String idAlfresco;
	
	@Getter
	@Setter
	@Column(name="doby_type_user")
	private Integer tipoUsuario;
	
	@Getter
	@Setter
	@Column(name="doby_table_id")
	private Integer idTabla;
	
	@Getter
	@Setter
	@Column(name="doby_description")
	private String descripcionTabla;
	
	@Getter
	@Setter
	@Column(name="doby_table_class")
	private String nombreTabla;
	
	@Getter
	@Setter
	@Column(name = "doby_process_instance_id")
	private Long idProceso;

	@Getter
	@Setter
	@Column(name="doby_update_number")
	private Integer numeroActualizacion;
	
	@Getter
	@Setter
	@Transient
	private byte[] contenidoDocumento;

}