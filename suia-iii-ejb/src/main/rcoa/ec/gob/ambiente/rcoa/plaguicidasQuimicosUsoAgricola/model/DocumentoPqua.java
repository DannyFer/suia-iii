package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;


/**
 * The persistent class for the documents_chemicals_pesticides database table.
 * 
 */
@Entity
@Table(name="documents_chemicals_pesticides", schema="chemical_pesticides")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "doch_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "doch_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "doch_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "doch_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "doch_user_update")) })
@NamedQuery(name="DocumentoPqua.findAll", query="SELECT d FROM DocumentoPqua d")
public class DocumentoPqua extends EntidadAuditable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="doch_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name="doch_alfresco_id")
	private String idAlfresco;

	@Getter
	@Setter
	@Column(name="doch_extesion")
	private String extension;

	@Getter
	@Setter
	@Column(name="doch_mime")
	private String mime;

	@Getter
	@Setter
	@Column(name="doch_name")
	private String nombre;

	@Getter
	@Setter
	@Column(name="doty_id")
	private Integer idTipoDocumento;
	
	@Getter
	@Setter
	@Column(name="doch_table_id")
	private Integer idTabla;
	
	@Getter
	@Setter
	@Column(name = "doch_table_class")
	private String nombreTabla;
	
	@Getter
	@Setter
	@Column(name="doch_process_instance_id")
	private Long idProceso;
	
	@Getter
	@Setter
	@Transient
	private byte[] contenidoDocumento;

	public DocumentoPqua() {
	}

	public Boolean esNuevo() {
		Boolean esNuevo = false;

		try {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date fechaDocumento = sdf.parse(this.fechaCreacion.toString());
			Date fechaActual = sdf.parse(sdf.format(new Date()));
			
			if(fechaDocumento.compareTo(fechaActual) == 0) {
				esNuevo=true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return esNuevo;
	}

}