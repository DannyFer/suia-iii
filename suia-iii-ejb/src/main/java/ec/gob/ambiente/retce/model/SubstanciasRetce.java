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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the substances_retce database table.
 * 
 */
@Entity
@Table(name="substances_retce", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "sure_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "sure_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "sure_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "sure_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "sure_user_update")) })

@NamedQueries({
	@NamedQuery(name = SubstanciasRetce.GET_HISTORIAL_POR_ID_NRO_OBSERVACION, query = "SELECT t FROM SubstanciasRetce t WHERE t.idRegistroOriginal = :idSustancia and estado = true and historial = true and numeroObservacion = :nroObservacion ORDER BY t.id desc"),
	@NamedQuery(name = SubstanciasRetce.GET_HISTORIAL_POR_ID, query = "SELECT t FROM SubstanciasRetce t WHERE t.idRegistroOriginal = :idSustancia and estado = true and historial = true ORDER BY t.id desc"),
	@NamedQuery(name = SubstanciasRetce.GET_POR_ID_AUTOGESTION, query = "SELECT t FROM SubstanciasRetce t WHERE t.idDesechoAutogestion = :idAutogestion and estado = true and idRegistroOriginal is not null ORDER BY t.id desc") })
public class SubstanciasRetce extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String GET_HISTORIAL_POR_ID_NRO_OBSERVACION = "ec.gob.ambiente.retce.model.SubstanciasRetce.getHistorialPorIdNroObservacion";
	public static final String GET_HISTORIAL_POR_ID = "ec.gob.ambiente.retce.model.SubstanciasRetce.getHistorialPorId";
	public static final String GET_POR_ID_AUTOGESTION = "ec.gob.ambiente.retce.model.SubstanciasRetce.getPorIdAutogestion";	
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="sure_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="aede_id")
	private DetalleEmisionesAtmosfericas detalleEmisionesAtmosfericas;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="caem_id")
	private CatalogoMetodoEstimacion catalogoMetodoEstimacion;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="csre_id")
	private CatalogoSustanciasRetce catologSustanciasRetce;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="docu_id")
	private Documento documento;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="lada_id")
	private DatosLaboratorio datosLaboratorio;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="ldde_id")
	private DetalleDescargasLiquidas detalleDescargasLiquidas;	

	@Getter
	@Setter
	@Column(name="sure_report_ton_year")
	private Double reporteToneladaAnio;
	
	@Getter
	@Setter
	@Transient
	private String fuenteFija;
	
	@Getter
	@Setter
	@Transient
	private String CodigoPuntoMonitoreo;
	
	@Getter
	@Setter
	@Column(name="wsma_id")
	private Integer idDesechoAutogestion;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="epco_id")
	private ConsumoEnergia consumoEnergia;
	
	@Getter
	@Setter
	@Column(name="sure_history")
	private Boolean historial;
    
    @Getter
	@Setter
	@Column(name="sure_original_record_id")
	private Integer idRegistroOriginal;
    
    @Getter
	@Setter
	@Column(name="sure_observation_number")
	private Integer numeroObservacion;

    @Getter
   	@Setter
   	@Transient
   	private List<SubstanciasRetce> historialLista;
   		
   	public boolean getTieneHistorial(){
   		return historialLista==null || historialLista.isEmpty()? false:true;
   	}

   	@Getter
	@Setter
	@Transient
	private Boolean nuevoEnModificacion;
    
    @Getter
	@Setter
	@Transient
    private Boolean mostrarHistorial = false;
    
    @Getter
	@Setter
	@Transient
    List<SubstanciasRetce> listaHistorial;
    
    @Getter
	@Setter
	@Transient
    List<DatosLaboratorio> listaHistorialLaboratorio;
    
    @Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="resu_id")
	private ReporteSustancias reporteSustancias;
    
    @Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="gcde_id_type_component")
	private DetalleCatalogoGeneral tipoComponente;
    
}