package ec.gob.ambiente.rcoa.sustancias.quimicas.model;

import java.io.Serializable;

import javax.persistence.*;

import ec.gob.ambiente.rcoa.model.GestionarProductosQuimicosProyectoAmbiental;
import ec.gob.ambiente.suia.domain.SustanciaQuimicaPeligrosa;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the import_request database table.
 * 
 */
@Entity
@Table(name="import_request", schema = "coa_chemical_sustances")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "inre_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "inre_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "inre_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "inre_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "inre_user_update")) })
public class SolicitudImportacionRSQ extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="inre_id")
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "chsr_id")	
	@Getter
	@Setter
	private RegistroSustanciaQuimica registroSustanciaQuimica;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="dach_id")
	private SustanciaQuimicaPeligrosa sustanciaQuimicaPeligrosa;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="dosu_id")
	private DocumentosSustanciasQuimicasRcoa documentosSustanciasQuimicasRcoa;	
	
	@Getter
	@Setter
	@Column(name="inre_annulment")	
	private Boolean anulacion;

	@Getter
	@Setter
	@Column(name="inre_annulment_date")
	private Date fechaAnulacion;

	@Getter
	@Setter
	@Column(name="inre_authorization")
	private Boolean autorizacion;

	@Getter
	@Setter
	@Column(name="inre_begin_authorization_date")
	private Date fechaInicioAutorizacion;

	@Getter
	@Setter
	@Column(name="inre_date_status")
	private Date fechaCambioEstado;	

	@Getter
	@Setter
	@Column(name="inre_end_authorization_date")
	private Date fechaFinAutorizacion;	

	@Getter
	@Setter
	@Column(name="justification_annulment")
	private String justificacionAnulacion;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="mach_id")
	private GestionarProductosQuimicosProyectoAmbiental gestionarProductosQuimicosProyectoAmbiental;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="user_id_annulmente")
	private Usuario usuarioAutorizaAnulacion;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="user_id_authorization")
	private Usuario usuarioAutorizaSolicitud;
		
	@Getter
	@Setter
	@Column(name="inre_processing_code")
	private String tramite;
	
	@Getter
	@Setter
	@Column(name="inre_document_autorizes")
	private String numeroDocumentoAutorizacion;
	
	@Getter
	@Setter
	@Column(name="inre_document_cancel")
	private String numeroDocumentoAnulacion;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="inre_parent_id")
	private SolicitudImportacionRSQ solicitudAnulada;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="user_id_correction")
	private Usuario usuarioCorrigeSolicitud;
	
	@Getter
	@Setter
	@Column(name="inre_document_correction")
	private String numeroDocumentoCorreccion;

	@Getter
	@Setter
	@Column(name="inre_begin_correction_date")
	private Date fechaInicioCorreccion;

	@Getter
	@Setter
	@Column(name="inre_end_correction_date")
	private Date fechaFinCorreccion;	
	
	@Getter
	@Setter
	@Column(name="inre_correction")
	private Boolean correccion;
	
	@Getter
	@Setter
	@Column(name="justification_correction")
	private String justificacionCorreccion;
	
	@Getter
	@Setter
	@Column(name="process_instance_id")
	private Long idInstanciaProceso;
	
	@Transient
	@Getter
	@Setter
	private SolicitudImportacionRSQExtVue solicitudImportacionRSQExtVue;
	
	public SolicitudImportacionRSQ() {
		solicitudImportacionRSQExtVue = new SolicitudImportacionRSQExtVue();
	}	
	
	@Getter
	@Setter
	@Transient
	private boolean seleccionado;
	
	public SolicitudImportacionRSQ(Object[]  array) {
		
		this.id = (Integer)array[0];
		this.autorizacion = (Boolean)array[1];
		this.fechaInicioAutorizacion = (Date)array[2];
		this.fechaFinAutorizacion = (Date)array[3];
		this.estado = (Boolean)array[4];
		this.fechaCreacion = (Date)array[5];
		this.usuarioCreacion = (String)array[6];
		this.tramite = (String)array[7];
		this.numeroDocumentoAutorizacion = (String)array[8];		
		
	}

	@Getter
	@Setter
	@Column(name="req_no")
	private String reqNo;
	
	@Getter
	@Setter
	@Column(name="req_proc")
	private String procesoRequerimiento;
}