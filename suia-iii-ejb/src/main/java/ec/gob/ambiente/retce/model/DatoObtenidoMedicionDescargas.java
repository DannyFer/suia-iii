package ec.gob.ambiente.retce.model;

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

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the data_obtained_measurement_downloads database table.
 * 
 */
@Entity
@Table(name="data_obtained_measurement_downloads", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "domd_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "domd_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "domd_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "domd_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "domd_user_update")) })
public class DatoObtenidoMedicionDescargas extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="domd_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="domd_entered_value")
	private Double valorIngresado;

	@Getter
	@Setter	
	@ManyToOne
	@JoinColumn(name="ldde_id")
	private DetalleDescargasLiquidas detalleDescargasLiquidas;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="caem_id")
	private CatalogoMetodoEstimacion metodoEstimacion;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="tapa_id")
	private ParametrosTablas parametrosTablas;
	
	@Getter
	@Setter
	@Column(name="domd_entered_value_correct")
	private Boolean valorIngresadoCorrecto;
	
	@Getter
	@Setter
	@Column(name="domd_history")
	private Boolean historial;
	
    @Getter
	@Setter
	@Column(name="domd_original_record_id")
	private Integer idRegistroOriginal;
    
    @Getter
	@Setter
	@Column(name="domd_observation_number")
	private Integer numeroObservacion;
    
	@Getter
	@Setter
	@Transient
	private List<DatoObtenidoMedicionDescargas> historialLista;
		
	public boolean getTieneHistorial(){
		return historialLista==null || historialLista.isEmpty()? false:true;
	}
}