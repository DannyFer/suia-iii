package ec.gob.ambiente.retce.model;

import java.io.Serializable;
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
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

/**
 * The persistent class for the liquid_downloads_detail database table.
 * 
 */
@Entity
@Table(name="liquid_downloads_detail", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "ldde_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "ldde_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "ldde_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "ldde_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "ldde_user_update")) })
public class DetalleDescargasLiquidas extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ldde_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="cadt_id")
	private CatalogoTipoDescarga catalogoTipoDescarga;

	@Getter
	@Setter
	@Column(name = "ldde_start_date_monitoring")
	private String fechaInicioMonitoreo;
	
	@Getter
	@Setter
	@Column(name = "ldde_end_date_monitoring")
	private String fechaFinMonitoreo;
/*
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="crbt_id")
	private CatalogoTipoCuerpoReceptor catalogoTipoCuerpoReceptor;   */

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="camf_id")
	private CatalogoFrecuenciaMonitoreo catalogoFrecuenciaMonitoreo;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="crmp_id")
	private CatalogoTipoCuerpoReceptorCaracteristicasPunto catalogoTipoCuerpoReceptorCaracteristicasPunto;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="cwtr_id")
	private CatalogoTratamientoAguas catalogoTratamientoAguas;

	@Getter
	@Setter
	@Column(name="ldde_monitoring_point_code")
	private String codigoPuntoMonitoreo;
	
	@Getter
	@Setter
	@Column(name="ldde_coordinate_x")
	private Double coordenadaX;

	@Getter
	@Setter
	@Column(name="ldde_coordinate_y")
	private Double coordenadaY;

	@Getter
	@Setter
	@Column(name="ldde_download_hours")
	private Double horasDescargaDia;

	@Getter
	@Setter
	@Column(name="ldde_download_volume")
	private Double volumenDescarga;

	@Getter
	@Setter
	@Column(name="ldde_measured_flow")
	private Double caudalMedido;

	@Getter
	@Setter
	@Column(name="ldde_monitoring_point")
	private String numeroPuntoMonitoreo;

	/* no se esta usando
	@Getter
	@Setter
	@Column(name="ldde_other_receiver_body_type")
	private String otroTipoCuerpoReceptor;*/

	@Getter
	@Setter
	@Column(name="ldde_other_water_treatment")
	private String otroTratamientoAgua;

	@Getter
	@Setter
	@Column(name="ldde_place_monitoring_point")
	private String lugarPuntoMonitoreo;
	
	@Getter
	@Setter
	@Column(name="ldde_history")
	private Boolean historial;
    
    @Getter
	@Setter
	@Column(name="ldde_original_record_id")
	private Integer idRegistroOriginal;
    
    @Getter
	@Setter
	@Column(name="ldde_observation_number")
	private Integer numeroObservacion;

	@Getter
	@Setter
	@Column(name="ldde_original_id")
	private Integer detalleOriginalId;
    
    @Getter
	@Setter
	@Transient
	private List<DetalleDescargasLiquidas> historialLista;
		
	public boolean getTieneHistorial(){
		return historialLista==null || historialLista.isEmpty()? false:true;
	}

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="lido_id")
	private DescargasLiquidas descargasLiquidas;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="mpch_id")
	private CaracteristicasPuntoMonitoreo caracteristicasPuntoMonitoreo;

	/*@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="resp_id")
	private TecnicoResponsable tecnicoResponsable;*/

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="saty_id")
	private TipoMuestra tipoMuestra;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="unlo_id")
	private LugarDescarga lugarDescarga;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "fase_id")
	private FaseRetce faseRetce;
	
    @Getter
	@Setter
	@Column(name="ldde_other_unlo")
	private String otroLugarDescarga;
	/*
	public String getTipoCuerpoReceptor(){
		return (otroTipoCuerpoReceptor!=null && !otroTipoCuerpoReceptor.isEmpty())?otroTipoCuerpoReceptor:catalogoTipoCuerpoReceptor.getDescripcion();
	}
	*/
	@Getter
	@Setter
	@Transient
	public List<DatosLaboratorio> datosLaboratoriosList;
	
	@Getter
	@Setter
	@Transient
	public List<DatoObtenidoMedicionDescargas> datoObtenidoMedicionDescargasList;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "gelo_id")
	private UbicacionesGeografica ubicacionGeografica;
	
}
