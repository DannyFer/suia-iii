package ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.model;

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

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name="documents_environmental_resolution", schema = "coa_emission_environmental_resolution")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "dere_status")),	
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "dere_creator_user")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "dere_creation_date")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "dere_user_update")), 
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "dere_date_update"))
})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "dere_status = 'TRUE'")


public class DocumentoResolucionAmbiental extends EntidadAuditable {
	
	private static final long serialVersionUID = 1L;	

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="dere_id")
	private Integer id;	

	@Getter
	@Setter
	@Column(name="dere_name")
	private String nombre;
	
	@Getter
	@Setter
	@Column(name="dere_mime")
	private String mime;
	
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="doty_id")
	private TipoDocumento tipoDocumento;
	
	@Getter
	@Setter
	@Column(name="dere_alfresco_id")
	private String idAlfresco;
	
	@Getter
	@Setter
	@Column(name="dere_extesion")
	private String extension;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="geca_id")
    private CatalogoGeneralCoa tipoUsuarioAlmacena;
	
	@Getter
	@Setter
	@Column(name="dere_process_instance_id")
	private Long idProceso;
	
	@Getter
	@Setter
	@Column(name="dere_table_id")
	private Integer idTabla;
	
	@Getter
	@Setter
	@Column(name="dere_description")
	private String descripcionTabla;
	
	@Getter
	@Setter
	@Column(name="dere_table_class")
	private String nombreTabla;
	
	@Getter
	@Setter
	@Column(name="dere_code_public")
	private String codigoPublico;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="enre_id")
	private ResolucionAmbiental resolucionAmbiental;
	
	@Getter
	@Setter
	@Transient
	private byte[] contenidoDocumento;
	
	@Getter
	@Setter
	@Transient
	private String informePath;
	
	@Getter
	@Setter
	@Transient
	private String nombreFichero;

}