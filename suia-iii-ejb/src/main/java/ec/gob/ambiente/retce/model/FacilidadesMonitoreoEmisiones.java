package ec.gob.ambiente.retce.model;

import java.io.Serializable;

import javax.persistence.*;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the monitoring_facilities_atmospheric_emissions database table.
 * 
 */
@Entity
@Table(name="monitoring_facilities_atmospheric_emissions", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "mfae_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "mfae_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "mfae_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "mfae_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "mfae_user_update")) })
public class FacilidadesMonitoreoEmisiones extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="mfae_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="aede_id")
	private DetalleEmisionesAtmosfericas detalleEmisionesAtmosfericas;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="cmfa_id")
	private CatalogoFacilidadesMonitoreo catalogoFacilidadesMonitoreo;	
	
	@Getter
	@Setter
	@Column(name="mfae_history")
	private Boolean historial;	
	
	@Getter
	@Setter
	@Column(name="mfae_original_record_id")
	private Integer idRegistroOriginal;
    
    @Getter
	@Setter
	@Column(name="mfae_observation_number")
	private Integer numeroObservacion;
    
    @Getter
    @Setter
    @Transient
    private Boolean nuevoEnModificacion;
}