package ec.gob.ambiente.rcoa.digitalizacion.model;


import java.io.File;

import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;	

@Entity
@Table(name="documents", schema = "coa_digitalization_linkage")
@NamedQueries({
@NamedQuery(name="DocumentoDigitalizacion.GETFINDALL", query="SELECT c FROM DocumentoDigitalizacion c")})

@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "docu_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "docu_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "docu_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "docu_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "docu_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")

public class DocumentoDigitalizacion extends EntidadAuditable {
    private static final long serialVersionUID = 5016494380218175574L;    
    @Getter
    @Setter
    @Id    
    @SequenceGenerator(name = "ADMINISTRACIONAUTORIZACIONDOCUMENTO_ID_GENERATOR", sequenceName = "documents_docu_id_seq", schema = "coa_digitalization_linkage", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ADMINISTRACIONAUTORIZACIONDOCUMENTO_ID_GENERATOR")     
    @Column(name = "docu_id")
    private Integer id;
    @Getter
    @Setter
    @Column(name = "docu_name", length = 255)
    private String nombre;
    @Getter
    @Setter
    @Column(name = "docu_mime", length = 255)
    private String mime;
    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "doty_id")
    private TipoDocumento tipoDocumento;    
    @Getter
    @Setter
    @Column(name = "docu_extesion", length = 255)
    private String extension;  
    @Getter
    @Setter
    @Column(name = "docu_alfresco_id", length = 255)
    private String idAlfresco;
    @Getter
    @Setter
    @Column(name = "docu_type_user")
    private Integer tipoUsuario;
    @Getter
    @Setter
    @Column(name = "docu_type_entry")
    private Integer tipoIngreso;
    @Getter
    @Setter
    @Column(name = "docu_table_id")
    private Integer idTabla;
    @Getter
    @Setter
    @Column(name = "docu_description", length = 255)
    private String descripcion;
    @Getter
    @Setter
    @Column(name = "docu_table_class", length = 255)
    private String nombreTabla;
    @Getter
    @Setter
    @Column(name = "docu_code_public", length = 255)
    private String codigoPublico;
    @Getter
    @Setter
    @Column(name = "docu_process_instance_id")
    private Integer idProceso;
    @Getter
    @Setter
    @Column(name = "docu_update_number")
    private Integer numeroActualizacion; 
	@ManyToOne
	@JoinColumn(name = "enaa_id")
	@ForeignKey(name = "fk_enaa_id")
	@Getter
	@Setter
	private AutorizacionAdministrativaAmbiental autorizacionAdministrativaAmbiental;
	
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
