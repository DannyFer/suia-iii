package ec.gob.ambiente.rcoa.model;

import java.io.Serializable;
import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="environmental_record", schema="coa_environmental_record")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "enre_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "enre_creation_date")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "enre_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "enre_creator_user")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "enre_user_update")) })
@NamedQueries({
	@NamedQuery(name=RegistroAmbientalRcoa.GET_REGISTROAMBIENTAL_POR_PROYECTO, query="SELECT m FROM RegistroAmbientalRcoa m where m.estado = true and m.proyectoCoa.id = :proyectoId")
})
public class RegistroAmbientalRcoa extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String GET_REGISTROAMBIENTAL_POR_PROYECTO = "ec.gob.ambiente.rcoa.model.RegistroAmbientalRcoa.getRegistroAmbientalPorProyecto";
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="enre_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="enre_normativas")
	private boolean aceptacion;

	@Getter
	@Setter
	@Column(name="enre_registration_end")
	private boolean finalizadoIngreso;

	@Getter
	@Setter
	@Column(name="enre_finalized")
	private boolean finalizado;

	@Getter
	@Setter
	@Column(name="enre_number_resolution")
	private String numeroResolucion;

	@Getter
	@Setter
	@Column(name="enre_number_office")
	private String numeroOficio;

	@Getter
	@Setter
	@Temporal(TemporalType.DATE)
	@Column(name = "enre_date_registration")
	private Date fechaFinalizacionIngreso;

	@Getter
	@Setter
	@Temporal(TemporalType.DATE)
	@Column(name = "enre_date_finalized")
	private Date fechaFinalizacion;

	
	@Getter
	@Setter
	@Column(name="enre_shrimp_concession_beach")
	private String concesionCamaroneraPlaya;
	
	@Getter
	@Setter
	@Column(name="enre_shrimp_concession_high")
	private String concesionCamaroneraAlta;
	
	@Getter
	@Setter
	@Column(name="enre_resolution_physical")
	private String resolucionAmbietalFisica;
	
	@Getter
	@Setter
	@Temporal(TemporalType.DATE)
	@Column(name = "enre_date_environmental_resolution")
	private Date fechaResolucionAmbientalFisica;
	
	@Getter
	@Setter
	@Column(name="enre_physical_job_ppc")
	private Boolean esOficioPPCFisico;
	
	@Getter
	@Setter
	@Column(name="enre_pronouncement_type_ppc")
	private Boolean esPronunciamientoAprobadoPPC;
	
	@Getter
	@Setter
	@Column(name="enre_physical_registration_resolution")
	private Boolean esResolucionRegistroFisica;
	
	@Getter
	@Setter
	@Column(name="enre_payment_resolution")
	private Boolean tienePagoResolucion;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "prco_id")
	private ProyectoLicenciaCoa proyectoCoa;

}