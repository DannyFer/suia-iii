package ec.gob.ambiente.retce.model;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the hazardous_waste_generated_disposal database table.
 * 
 */
@Entity
@Table(name="waste_generated_by_disposal", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "wgbd_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "wgbd_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "wgbd_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "wgbd_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "wgbd_user_update")) })

@NamedQueries({
	@NamedQuery(name="DesechoGeneradoEliminacion.findAll", query="SELECT d FROM DesechoGeneradoEliminacion d"),
	@NamedQuery(name = DesechoGeneradoEliminacion.GET_HISTORIAL_POR_ID_NRO_REVISION, query = "SELECT t FROM DesechoGeneradoEliminacion t WHERE t.idRegistroOriginal = :idDesecho and estado = true and numeroObservacion = :nroObservacion ORDER BY t.id desc"),
	@NamedQuery(name = DesechoGeneradoEliminacion.GET_HISTORIAL_POR_ID, query = "SELECT t FROM DesechoGeneradoEliminacion t WHERE t.idRegistroOriginal = :idDesecho and estado = true ORDER BY t.id desc"),
	@NamedQuery(name = DesechoGeneradoEliminacion.GET_ELIMINADOS_POR_ID_ELIMINACION, query = "SELECT t FROM DesechoGeneradoEliminacion t WHERE t.idDesechoEliminacionAutogestion = :idTipo and estado = true and t.idRegistroOriginal is not null ORDER BY t.id desc") })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wgbd_status = 'TRUE'")
public class DesechoGeneradoEliminacion extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String GET_HISTORIAL_POR_ID_NRO_REVISION = "ec.gob.ambiente.retce.model.DesechoGeneradoEliminacion.getHistorialPorIdNroRevision";
	public static final String GET_HISTORIAL_POR_ID = "ec.gob.ambiente.retce.model.DesechoGeneradoEliminacion.getHistorialPorId";
	public static final String GET_ELIMINADOS_POR_ID_ELIMINACION = "ec.gob.ambiente.retce.model.DesechoGeneradoEliminacion.getEliminadosPorIdEliminacion";
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="wgbd_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="wgbd_quantity")
	private Double cantidad;
	
	@Getter
	@Setter
	@Column(name="wgbd_waste_description")
	private String descripcion;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="gcde_id_unit_type")
	private DetalleCatalogoGeneral tipoUnidad;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="gcde_id_type_waste")
	private DetalleCatalogoGeneral tipoDesechoGenerado;

	@Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "wada_id")
    private DesechoPeligroso desechoPeligroso;
	
//	@Getter
//	@Setter
//	@ManyToOne
//	@JoinColumn(name="wdsm_id")
//	private DesechoEliminacionAutogestion desechoEliminacionAutogestion;
	
	@Getter
	@Setter
	@Column(name="wdsm_id")
	private Integer idDesechoEliminacionAutogestion;
	
	@Getter
	@Setter
	@Column(name="wgbd_history")
	private Boolean historial;
    
    @Getter
	@Setter
	@Column(name="wgbd_original_record_id")
	private Integer idRegistroOriginal;
    
    @Getter
	@Setter
	@Column(name="wgbd_observation_number")
	private Integer numeroObservacion;
    
    @Getter
	@Setter
	@Transient
	private Boolean nuevoEnModificacion;
    
    public boolean equalsObject(Object obj) {
  		if (obj == null)
  			return false;
  		DesechoGeneradoEliminacion base = (DesechoGeneradoEliminacion) obj;
  		if (this.getId() == null && base.getId() == null)
  			return super.equals(obj);
  		if (this.getId() == null || base.getId() == null)
  			return false;
  		
  		if (this.getId().equals(base.getId())
  				&& ((this.getTipoUnidad() == null && base.getTipoUnidad() == null) || 
  						(this.getTipoUnidad() != null && base.getTipoUnidad() != null && 
  						this.getTipoUnidad().getId().equals(base.getTipoUnidad().getId())))
  				&& ((this.getTipoDesechoGenerado() == null && base.getTipoDesechoGenerado() == null) || 
  						(this.getTipoDesechoGenerado() != null && base.getTipoDesechoGenerado() != null && 
  						this.getTipoDesechoGenerado().getId().equals(base.getTipoDesechoGenerado().getId())))
  				&& ((this.getCantidad() == null && base.getCantidad() == null) || 
  						(this.getCantidad() != null && base.getCantidad() != null 
  						&& this.getCantidad().equals(base.getCantidad())))
  				&& ((this.getDescripcion() == null && base.getDescripcion() == null) || 
  						(this.getDescripcion() != null && base.getDescripcion() != null 
  						&& this.getDescripcion().equals(base.getDescripcion())))
  				&& ((this.getDesechoPeligroso() == null && base.getDesechoPeligroso() == null) || 
  						(this.getDesechoPeligroso() != null && base.getDesechoPeligroso() != null && 
  						this.getDesechoPeligroso().getId().equals(base.getDesechoPeligroso().getId())))
  			)
  			return true;
  		else
  			return false;
  	}
	
}