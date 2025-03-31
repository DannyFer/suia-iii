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

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;

/**
 * The persistent class for the data_obtained_measurement database table.
 * 
 */
@Entity
@Table(name="data_obtained_measurement", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "dome_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "dome_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "dome_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "dome_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "dome_user_update")) })
public class DatoObtenidoMedicion extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="dome_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="aede_id")
	private DetalleEmisionesAtmosfericas detalleEmisionesAtmosfericas;

	@Getter
	@Setter
	@Column(name="dome_concentration_value")
	private Double valorConcentracion; //Concentración a condiciones normales

	@Getter
	@Setter
	@Column(name="dome_corrected_value")
	private Double valorCorregido;

	@Getter
	@Setter
	@Column(name="dome_correction_oxygen_percentage")
	private Double porcentajeOxigenoCorrecion;
	
	@Getter
	@Setter
	@Column(name="dome_dry_gas_flow")
	private Double valorFlujoGasSeco;

	@Getter
	@Setter
	@Column(name="dome_entered_value")
	private Double valorIngresado;

	@Getter
	@Setter
	@Column(name="dome_oxygen_percentage")
	private Double valorPorcentajeOxigeno;

	@Getter
	@Setter
	@Column(name="dome_temperature")
	private Double valorTemperatura;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="mapl_id")
	private LimiteMaximoPermitido limiteMaximoPermitido;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="caem_id")
	private CatalogoMetodoEstimacion catalogoMetodoEstimacion;
	
	@Getter
	@Setter
	@Column(name="dome_tons_conversion")
	private Double conversionToneladas;
	
	@Getter
	@Setter
	@Transient
	private String cumple;
	
	@Getter
	@Setter
	@Column(name="dome_history")
	private Boolean historial;
	
    @Getter
	@Setter
	@Column(name="dome_original_record_id")
	private Integer idRegistroOriginal;
    
    @Getter
	@Setter
	@Column(name="dome_observation_number")
	private Integer numeroObservacion;
    
    @Getter
    @Setter
    @Transient
    private Boolean nuevoEnModificacion;
    
    @Getter
    @Setter
    @Transient
    private Boolean registroModificado;
    
	public DatoObtenidoMedicion() {
		super();
	}

	public DatoObtenidoMedicion(Integer id) {
		super();
		this.id = id;
	}
    	
}