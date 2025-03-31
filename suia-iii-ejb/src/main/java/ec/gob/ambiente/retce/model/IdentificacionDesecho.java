package ec.gob.ambiente.retce.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the waste_identification database table.
 * 
 */
@Entity
@Table(name="waste_identification", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "waid_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "waid_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "waid_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "waid_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "waid_user_update")) })

@NamedQueries({
	@NamedQuery(name="IdentificacionDesecho.findAll", query="SELECT i FROM IdentificacionDesecho i"),
	@NamedQuery(name = IdentificacionDesecho.GET_LISTA_IDENTIFICACION_DESECHOS_POR_RGDRETCE, query = "SELECT t FROM IdentificacionDesecho t WHERE t.idGeneradorRetce = :idRgdRetce and estado = true and idRegistroOriginal is null ORDER BY t.id desc"),
	@NamedQuery(name = IdentificacionDesecho.GET_POR_RGDRETCE_POR_DESECHO, query = "SELECT t FROM IdentificacionDesecho t WHERE t.idGeneradorRetce = :idRgdRetce and estado = true and desechoPeligroso.id = :idDesecho and t.historial is null ORDER BY t.id desc"),
	@NamedQuery(name = IdentificacionDesecho.GET_HISTORIAL_POR_RGDRETCE, query = "SELECT t FROM IdentificacionDesecho t WHERE t.idGeneradorRetce = :idRgdRetce and estado = true and historial = true and numeroObservacion = :nroObservacion ORDER BY t.id desc"),
	@NamedQuery(name = IdentificacionDesecho.GET_HISTORIAL_POR_ID, query = "SELECT t FROM IdentificacionDesecho t WHERE t.idRegistroOriginal = :idIdentificacion and estado = true and historial = true ORDER BY t.id desc")})

public class IdentificacionDesecho extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String GET_LISTA_IDENTIFICACION_DESECHOS_POR_RGDRETCE = "ec.gob.ambiente.retce.model.IdentificacionDesecho.getListaIdentificacionDesechosPorRgdRetce";
	public static final String GET_HISTORIAL_POR_RGDRETCE = "ec.gob.ambiente.retce.model.IdentificacionDesecho.getHistorialPorRgdRetce";
	public static final String GET_HISTORIAL_POR_ID = "ec.gob.ambiente.retce.model.IdentificacionDesecho.getHistorialPorId";
	public static final String GET_POR_RGDRETCE_POR_DESECHO = "ec.gob.ambiente.retce.model.IdentificacionDesecho.getPorIdPorDesecho";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="waid_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="waid_annual_generation_quantity")
	private Double cantidadGeneracionAnual;

	@Getter
	@Setter
	@Column(name="waid_annual_generation_quantity_units")
	private Integer cantidadGeneracionAnualUnidades;

	@Getter
	@Setter
	@Column(name="waid_quantity_not_managed")
	private Double cantidadNoGestionada;

	@Getter
	@Setter
	@Column(name="waid_quantity_not_managed_units")
	private Integer cantidadNoGestionadaUnidades;

	@Getter
	@Setter
	@Column(name="waid_quantity_previous_year")
	private Double cantidadAnioAnterior;
	
	@Getter
	@Setter
	@Column(name="waid_total_quantity_tons")
	private Double cantidadTotalToneladas;

	@Getter
	@Setter
	@Column(name="waid_quantity_previous_year_units")
	private Integer cantidadAnioAnteriorUnidades;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="gcde_id_unit_type")
	private DetalleCatalogoGeneral tipoUnidad;
	
    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wada_id")
    private DesechoPeligroso desechoPeligroso;
    
    @Getter
	@Setter
	@Column(name="hwgr_id")
	private Integer idGeneradorRetce;
    
    @Getter
	@Setter
	@Column(name="waid_history")
	private Boolean historial;
    
    @Getter
	@Setter
	@Column(name="waid_original_record_id")
	private Integer idRegistroOriginal;
    
    @Getter
	@Setter
	@Column(name="waid_observation_number")
	private Integer numeroObservacion;
    
    @Getter
	@Setter
	@Transient
    private List<IdentificacionDesecho> listaHistorial;
    
  	public boolean equalsObject(Object obj) {
  		if (obj == null)
  			return false;
  		IdentificacionDesecho base = (IdentificacionDesecho) obj;
  		if (this.getId() == null && base.getId() == null)
  			return super.equals(obj);
  		if (this.getId() == null || base.getId() == null)
  			return false;
  		
  		if (this.getId().equals(base.getId())
  				&& ((this.getTipoUnidad() == null && base.getTipoUnidad() == null) || 
  						(this.getTipoUnidad() != null && base.getTipoUnidad() != null && 
  						this.getTipoUnidad().getId().equals(base.getTipoUnidad().getId())))
  				&& ((this.getCantidadAnioAnterior() == null && base.getCantidadAnioAnterior() == null) || 
  						(this.getCantidadAnioAnterior() != null && base.getCantidadAnioAnterior() != null && this.getCantidadAnioAnterior().equals(base.getCantidadAnioAnterior())))
  				&& this.getCantidadGeneracionAnual().equals(base.getCantidadGeneracionAnual())
  				&& ((this.getCantidadNoGestionada() == null && base.getCantidadNoGestionada() == null) || 
  						(this.getCantidadNoGestionada() != null && base.getCantidadNoGestionada() != null && this.getCantidadNoGestionada().equals(base.getCantidadNoGestionada())))
  				&& ((this.getDesechoPeligroso().isDesechoES_04() || this.getDesechoPeligroso().isDesechoES_06()) && 
  			  			((this.getCantidadAnioAnteriorUnidades() == null && base.getCantidadAnioAnteriorUnidades() == null) || 
  		  						(this.getCantidadAnioAnteriorUnidades() != null && base.getCantidadAnioAnteriorUnidades() != null && 
  		  						this.getCantidadAnioAnteriorUnidades().equals(base.getCantidadAnioAnteriorUnidades()))
  		  				&& this.getCantidadGeneracionAnualUnidades().equals(base.getCantidadGeneracionAnualUnidades())
  		  				&& ((this.getCantidadNoGestionadaUnidades() == null && base.getCantidadNoGestionadaUnidades() == null) || 
  		  						(this.getCantidadNoGestionadaUnidades() != null && base.getCantidadNoGestionadaUnidades() != null && 
  		  						this.getCantidadNoGestionadaUnidades().equals(base.getCantidadNoGestionadaUnidades())))))
  			)
  			return true;
  		else
  			return false;
  	}

}
