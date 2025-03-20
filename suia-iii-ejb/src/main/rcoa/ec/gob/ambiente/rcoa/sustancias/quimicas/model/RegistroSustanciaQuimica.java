package ec.gob.ambiente.rcoa.sustancias.quimicas.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.SustanciaQuimicaPeligrosa;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

@Entity
@Table(name="chemical_sustances_records", schema = "coa_chemical_sustances")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "chsr_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "chsr_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "chsr_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "chsr_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "chsr_user_update")) })
public class RegistroSustanciaQuimica extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public RegistroSustanciaQuimica() {}
	
	public RegistroSustanciaQuimica(ProyectoLicenciaCoa proyectoLicenciaCoa) {
		this.proyectoLicenciaCoa=proyectoLicenciaCoa;
	}
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="chsr_id")
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "area_id")	
	@Getter
	@Setter
	private Area area;
	
	@ManyToOne
	@JoinColumn(name = "prco_id")	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@ManyToOne
	@JoinColumn(name = "geca_id")	
	@Getter
	@Setter
	private CatalogoGeneralCoa tipoIdentificacion;
	
	@ManyToOne
	@JoinColumn(name = "geca_id_pronouncement")	
	@Getter
	@Setter
	private CatalogoGeneralCoa tipoPronunciamiento;
	
	@Getter
	@Setter
	@Column(name="chsr_identification_rep_legal")
	private String identificacionRepLegal;
	
	@Getter
	@Setter
	@Column(name="chsr_name_rep_legal")
	private String nombreRepLegal;
	
	@Getter
	@Setter
	@Column(name="chsr_address_rep_legal")
	private String direccionRepLegal;
	
	@Getter
	@Setter
	@Column(name="chsr_phone_rep_legal")
	private String telefonoRepLegal;
	
	@Getter
	@Setter
	@Column(name="chsr_email_rep_legal")
	private String correoRepLegal;
	
	@Getter
	@Setter
	@Column(name="chsr_technological_change")
	private String cambioTecnologico;
	
	@Getter
	@Setter
	@Column(name="chsr_reduction_substance")
	private String metaReduccionSubstancia;
	
	@Getter
	@Setter
	@Column(name="chsr_application_number")
	private String numeroAplicacion;
	
	@Getter
	@Setter
	@Column(name="chsr_substance_registration")
	private String registroSustancia;
	
	@Getter
	@Setter
	@Column(name="chsr_rep_estudent")
	private Boolean repEstudiante;
	
	@Getter
	@Setter
	@Column(name="chsr_additional_responses")
	private String respuestasAdicionales;
	
	@Getter
	@Setter
	@Column(name="chsr_additional_justifications")
	private String justificacionAdicional;	
	
	@Getter
	@Setter
	@Column(name="chsr_valid_since")
	private Date vigenciaDesde;
	
	@Getter
	@Setter
	@Column(name="chsr_valid_until")
	private Date vigenciaHasta;
	
	@Getter
	@Setter
	@Column(name="chsr_craft_operator")
	private Boolean operadorArtesanal;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="user_id")
	private Usuario usuario;

	@Getter
	@Setter
	@Column(name="chsr_status_import_declaration")
	private Boolean estadoImportacionDeclaracion;

	@Getter
	@Setter
	@Column(name="chsr_consumption")
	private Boolean registroConsumo;
	
	@Getter
	@Setter
	@Column(name="chsr_code")
	private String codigo;
	
	@Getter
	@Setter
	@Transient
	private String nombreOperador;
	
	@Getter
	@Setter
	@Transient
	private String empresaActiva;
	
	@Getter
	@Setter
	@Transient
	private Boolean declaracionPendiente;

	@Getter
	@Setter
	@Transient
	public List<SustanciaQuimicaPeligrosa> listaSustancia;
	
	@Getter
	@Setter
	@Transient
	public String codigoGenerado;
	
	@Getter
	@Setter
	@Transient
	public String tipoPersona;
	
	@Getter
	@Setter
	@Transient
	public String actividades;
		
	public boolean pronunciamientoAprobado() {
		if(tipoPronunciamiento!=null
		&& tipoPronunciamiento.getNombre().toUpperCase().contains("APROBADO")){
			return true;
		}
		return false;
	}
	
	public boolean pronunciamientoObservado() {
		if(tipoPronunciamiento!=null
		&& tipoPronunciamiento.getNombre().toUpperCase().contains("OBSERVADO")){
			return true;
		}
		return false;
	}
	
	public RegistroSustanciaQuimica(Object[] array) {
		
		String proponente = "";
		if(array[8] == null){
			proponente = (array[7] == null) ? "" : (String) array[7];
			if((array[7] != null))
				this.nombreRepLegal = "N/A";
			else
				this.nombreRepLegal = (String)array[3];
		}else{
			proponente = (String) array[8];
			this.nombreRepLegal = (String)array[3];
		}
		
		this.nombreOperador = proponente;		
		this.identificacionRepLegal = (String)array[2];
		this.id = (Integer)array[0];		
		
		this.numeroAplicacion = array[4] == null ? "" : (String)array[4];
				
		if(array[5] != null){
			this.vigenciaDesde = (Date)array[5];
		}
		
		if(array[6] != null){
			this.vigenciaHasta = (Date)array[6];
		}
				
		if(array[9] != null){
			this.usuarioCreacion = (String)array[9];
		}
	
		if(array[10] != null){
			Usuario us = new Usuario();
			us.setId((Integer)array[10]);
			this.usuario = us;
		}
		
	}

}