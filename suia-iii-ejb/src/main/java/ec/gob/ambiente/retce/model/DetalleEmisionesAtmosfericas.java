package ec.gob.ambiente.retce.model;

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

import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the atmospheric_emissions_details database table.
 * 
 */
@Entity
@Table(name="atmospheric_emissions_details", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "aede_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "aede_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "aede_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "aede_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "aede_user_update")) })
public class DetalleEmisionesAtmosfericas extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="aede_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="aede_brand")
	private String marca;

	@Getter
	@Setter
	@Column(name="aede_chimney_peak")
	private Double cuspideChimenea;

	@Getter
	@Setter
	@Column(name="aede_coordinate_x")
	private Double coordenadaX;

	@Getter
	@Setter
	@Column(name="aede_coordinate_y")
	private Double coordenadaY;

	@Getter
	@Setter
	@Column(name="aede_diameter_chimney")
	private Double diametroChimenea;

	@Getter
	@Setter
	@Column(name="aede_fuel_consumption")
	private Double consumoCombustible;

	@Getter
	@Setter
	@Column(name="aede_height_length_chimney")
	private Double alturaLongitudChimenea;

	@Getter
	@Setter
	@Column(name="aede_last_disturbance")
	private Double ultimaPerturbacion;

	@Getter
	@Setter
	@Column(name="aede_monitoring_point_code")
	private String codigoPuntoMonitoreo;

	@Getter
	@Setter
	@Column(name="aede_number_ducts_chimneys")
	private String numeroDuctosChimenea;

	@Getter
	@Setter
	@Column(name="aede_operating_hours")
	private Double horasFuncionamiento;

	@Getter
	@Setter
	@Column(name="aede_output_speed")
	private Double velocidadSalida;

	@Getter
	@Setter
	@Column(name="aede_place_sampling_point")
	private String lugarPuntoMuestreo;

	@Getter
	@Setter
	@Column(name="aede_power")
	private Double potencia;

	@Getter
	@Setter
	@Column(name="aede_serial_number")
	private String numeroSerie;

	@Getter
	@Setter
	@Column(name="aede_source_code")
	private String codigoFuente;
	
	@Getter
	@Setter
	@Column(name = "aede_justification")
	private String justificacion;
	
	@Getter
	@Setter
	@Column(name="aede_start_date_monitoring")
	private String fechaInicioMonitoreo;
	
	@Getter
	@Setter
	@Column(name="aede_end_date_monitoring")
	private String fechaFinMonitoreo;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "camf_id")
	private CatalogoFrecuenciaMonitoreo frecuenciaMonitoreo;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="atem_id")
	private EmisionesAtmosfericas emisionesAtmosfericas;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="auth_id")
	private AutorizacionEmisiones autorizacionEmisiones;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="futy_id")
	private TipoCombustibleRetce tipoCombustible;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="gcde_id")
	private DetalleCatalogoGeneral estadoFuenteDetalleCatalogo;//estado de la fuente

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="gcde_type_operation_id")
	private DetalleCatalogoGeneral tipoOperacionDetalleCatalogo;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="unit_fuel_id")
	private CatalogoUnidades catalogoUnidadesCombustible;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="unit_power_id")
	private CatalogoUnidades catalogoUnidadesPotencia;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="docu_job_id")
	private Documento documentoOficioAprobacion;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "fsco_id")
	private FuenteFijaCombustion fuenteFijaCombustion;
	
	@Getter
	@Setter
	@Column(name="aede_history")
	private Boolean historial;
	
    @Getter
	@Setter
	@Column(name="aede_original_record_id")
	private Integer idRegistroOriginal;
    
    @Getter
	@Setter
	@Column(name="aede_observation_number")
	private Integer numeroObservacion;
    
    @Getter
    @Setter
    @Transient
    private Boolean nuevoEnModificacion;
    
    @Getter
    @Setter
    @Transient
    private Boolean registroModificado;

	@Getter
	@Setter
	@Column(name="aede_original_id")
	private Integer detalleOriginalId;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "gelo_id")
	private UbicacionesGeografica ubicacionGeografica;
	
	@Getter
	@Setter
	@Column(name = "aede_associated_location")
	private String locacionAsociada;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "fase_id")
	private FaseRetce faseRetce;
    
	public DetalleEmisionesAtmosfericas() {
		super();
	}

	public DetalleEmisionesAtmosfericas(Integer id) {
		super();
		this.id = id;
	}
    
    
	
}
