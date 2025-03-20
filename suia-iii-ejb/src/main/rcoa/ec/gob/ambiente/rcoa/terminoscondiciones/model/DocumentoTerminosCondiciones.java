package ec.gob.ambiente.rcoa.terminoscondiciones.model;

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
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "documents_terms_conditions", schema = "public")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "dotc_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "dotc_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "dotc_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "dotc_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "dotc_user_update")) })
public class DocumentoTerminosCondiciones extends EntidadAuditable implements Serializable {

	private static final long serialVersionUID = -1L;

	@Getter
	@Setter
	@Id
	@Column(name = "dotc_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "dotc_name", length = 255)
	private String nombre;

	@Getter
	@Setter
	@Column(name = "dotc_mime", length = 255)
	private String mime;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "doty_id")
	@ForeignKey(name = "doty_id")
	private TipoDocumento tipoDocumento;

	@Getter
	@Setter
	@Column(name = "dotc_alfresco_id", length = 255)
	private String idAlfresco;

	@Getter
	@Setter
	@Column(name = "dotc_extesion", length = 255)
	private String extesion;

	@Column(name = "user_id")
	@Getter
	@Setter
	private Integer usuarioId;

	@Getter
	@Setter
	@Column(name = "dotc_observation_bd", length = 255)
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

	public DocumentoTerminosCondiciones() {
	}

	public DocumentoTerminosCondiciones(TipoDocumentoSistema tipo, Integer usuarioId) {
		this.tipoDocumento = new TipoDocumento();
		tipoDocumento.setId(tipo.getIdTipoDocumento());
		this.usuarioId = usuarioId;
	}

}
