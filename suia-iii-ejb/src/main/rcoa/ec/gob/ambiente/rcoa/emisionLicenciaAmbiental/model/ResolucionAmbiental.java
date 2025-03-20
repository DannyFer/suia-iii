package ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.model;

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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name="environmental_resolution", schema = "coa_emission_environmental_resolution")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "enre_status")),	
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "enre_creator_user")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "enre_creation_date")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "enre_user_update")), 
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "enre_date_update"))
})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "enre_status = 'TRUE'")


public class ResolucionAmbiental extends EntidadAuditable {
	
	private static final long serialVersionUID = 1L;	
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="enre_id")
	private Integer id;
	
	// Registro Preliminar
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="prco_id")
	private ProyectoLicenciaCoa proyectoLicenciaCoa;

	@Getter
	@Column(name="enre_date_delete_record")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaEliminacion;

	@Getter
	@Setter
	@Column(name="enre_invoice_environmental_permit")
	private Boolean subeFacturaPermiso;

	@Getter
	@Setter
	@Column(name="enre_payment_protocol")
	private Boolean subeDocumentoPago;

	@Getter
	@Setter
	@Column(name="enre_policy_or_guarantee")
	private Boolean subeCostoImplementacion;

	@Getter
	@Setter
	@Column(name="enre_cost_justification")
	private Boolean subeJustificacionCosto;

	@Getter
	@Setter
	@Column(name="enre_schedule_valued")
	private Boolean subeCronogramaPMA;
	
	@Getter
	@Setter
	@Column(name="enre_policy_number")
	private String numeroPoliza;

	@Getter
	@Setter
	@Column(name="enre_implementation_cost")
	private Double costoImplementacion;

	@Getter
	@Setter
	@Column(name="enre_home_policy")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaInicioVigenciaPoliza;

	@Getter
	@Setter
	@Column(name="enre_end_policy")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaFinVigenciaPoliza;
	
	@Getter
	@Setter
	@Column(name="enre_project_value")
	private Double costoProyecto;
	
	@Getter
	@Setter
	@Column(name="enre_number_resolution")
	private String numeroResolucion;

	@Getter
	@Setter
	@Column(name="enre_forest_inventory_payment")
	private Double pagoInventarioForestal;

	@Getter
	@Setter
	@Column(name="enre_payment_rgdp")
	private Double pagoRGDP;

	@Getter
	@Setter
	@Column(name="enre_minimum_payment_cost")
	private Double pagoMinimoProyecto;

	@Getter
	@Setter
	@Column(name="enre_total_payment_value")
	private Double montoTotalProyecto;

	@Getter
	@Setter
	@Column(name="enre_original_policy")
	private Boolean entregoPolizaGarantia;

	@Getter
	@Setter
	@Column(name="enre_policy_reception_office")
	private String numeroOficioRecepcionPoliza;
	
	@Getter
	@Setter
	@Column(name="enre_policy_reception_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaRecepcionPoliza;

	@Getter
	@Setter
	@Column(name="enre_policy_amount")
	private Double montoPoliza;

	@Getter
	@Setter
	@Column(name="enre_policy_start_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaInicioPoliza;

	@Getter
	@Setter
	@Column(name="enre_policy_end_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaFinPoliza;
	
	@Getter
	@Setter
	@Column(name="enre_full_payment_inspection")
	private Double pagoTotalInspeccion;
	
	@Getter
	@Setter
	@Column(name="enre_date_resolution")
	private Date fechaResolucion;
	
	@Getter
	@Setter
	@Column(name="enre_flow")
	private Integer flujo;
	
	@Getter
	@Setter
	@Column(name="enre_aaa")
	private Boolean subeAutorizacionAdministrativaAmbiental;
	
}