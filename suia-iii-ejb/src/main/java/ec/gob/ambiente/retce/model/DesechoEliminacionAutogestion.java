package ec.gob.ambiente.retce.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ec.gob.ambiente.suia.domain.TipoEliminacionDesecho;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the final_waste_disposal database table.
 * 
 */
@Entity
@Table(name="waste_disposal_self_management", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "wdsm_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "wdsm_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "wdsm_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "wdsm_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "wdsm_user_update")) })

@NamedQueries({
	@NamedQuery(name="DesechoEliminacionAutogestion.findAll", query="SELECT e FROM DesechoEliminacionAutogestion e"),
	@NamedQuery(name = DesechoEliminacionAutogestion.GET_HISTORIAL_POR_ID_NRO_OBSERVACION, query = "SELECT t FROM DesechoEliminacionAutogestion t WHERE t.idRegistroOriginal = :idDesecho and estado = true and numeroObservacion = :nroObservacion ORDER BY t.id desc"),
	@NamedQuery(name = DesechoEliminacionAutogestion.GET_HISTORIAL_POR_ID, query = "SELECT t FROM DesechoEliminacionAutogestion t WHERE t.idRegistroOriginal = :idDesecho and estado = true ORDER BY t.id desc"),
	@NamedQuery(name = DesechoEliminacionAutogestion.GET_ELIMINADOS_POR_ID_DESECHO_AUTOGESTION, query = "SELECT t FROM DesechoEliminacionAutogestion t WHERE t.idDesechoAutogestion = :idDesecho and estado = true and t.idRegistroOriginal is not null ORDER BY t.id desc") })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wdsm_status = 'TRUE'")
public class DesechoEliminacionAutogestion  extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String GET_HISTORIAL_POR_ID_NRO_OBSERVACION = "ec.gob.ambiente.retce.model.DesechoEliminacionAutogestion.getHistorialPorIdNroObservacion";
	public static final String GET_HISTORIAL_POR_ID = "ec.gob.ambiente.retce.model.DesechoEliminacionAutogestion.getHistorialPorId";
	public static final String GET_ELIMINADOS_POR_ID_DESECHO_AUTOGESTION = "ec.gob.ambiente.retce.model.DesechoEliminacionAutogestion.getEliminadosPorIdDesechoAutogestion";
	

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="wdsm_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="wdsm_generates_waste")
	private Boolean generaDesecho;

	@Getter
	@Setter
	@Column(name="wdsm_quantity")
	private Double cantidad;
	
//	@Getter
//    @Setter
//    @ManyToOne
//    @JoinColumn(name = "wsma_id")
//    private DesechoAutogestion desechoAutogestion;
	@Getter
	@Setter
	@Column(name="wsma_id")
	private Integer idDesechoAutogestion;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="wdty_id")
	private TipoEliminacionDesecho tipoEliminacion;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="gcde_id_unit_type")
	private DetalleCatalogoGeneral tipoUnidad;	
	
	@Getter
	@Setter
	@OneToMany(mappedBy = "idDesechoEliminacionAutogestion")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filters({
			@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wgbd_status = 'TRUE'"),
			@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wgbd_original_record_id is null") })
	private List<DesechoGeneradoEliminacion> listaDesechosGeneradosPorEliminacion;
	
	@Getter
	@Setter
	@Column(name="wdsm_history")
	private Boolean historial;
    
    @Getter
	@Setter
	@Column(name="wdsm_original_record_id")
	private Integer idRegistroOriginal;
    
    @Getter
	@Setter
	@Column(name="wdsm_observation_number")
	private Integer numeroObservacion;
    
    @Getter
	@Setter
	@Transient
    private List<DesechoEliminacionAutogestion> listaHistorial;
    
    @Getter
	@Setter
	@Transient
    private List<DesechoGeneradoEliminacion> listaHistorialDesechoGenerado;
    
    @Getter
	@Setter
	@Transient
	private Boolean nuevoEnModificacion;
    
    @Getter
	@Setter
	@Transient
    private Boolean mostrarHistorial = false;    
    
    public boolean equalsObject(Object obj) {
  		if (obj == null)
  			return false;
  		DesechoEliminacionAutogestion base = (DesechoEliminacionAutogestion) obj;
  		if (this.getId() == null && base.getId() == null)
  			return super.equals(obj);
  		if (this.getId() == null || base.getId() == null)
  			return false;
  		
  		if (this.getId().equals(base.getId())
  				&& ((this.getTipoUnidad() == null && base.getTipoUnidad() == null) || 
  						(this.getTipoUnidad() != null && base.getTipoUnidad() != null && 
  						this.getTipoUnidad().getId().equals(base.getTipoUnidad().getId())))
  				&& ((this.getGeneraDesecho() == null && base.getGeneraDesecho() == null) || 
  						(this.getGeneraDesecho() != null && base.getGeneraDesecho() != null && 
  						this.getGeneraDesecho().equals(base.getGeneraDesecho())))
  				&& ((this.getCantidad() == null && base.getCantidad() == null) || 
  						(this.getCantidad() != null && base.getCantidad() != null 
  						&& this.getCantidad().equals(base.getCantidad())))
  			)
  			return true;
  		else
  			return false;
  	}
}