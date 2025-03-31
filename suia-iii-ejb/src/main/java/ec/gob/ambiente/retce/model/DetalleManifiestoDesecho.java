package ec.gob.ambiente.retce.model;

import java.io.Serializable;

import javax.persistence.*;

import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the detail_manifests_waste database table.
 * 
 */
@Entity
@Table(name="detail_manifests_waste", schema = "retce")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "dmwa_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "dmwa_date_create")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "dmwa_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "dmwa_user_create")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "dmwa_user_update")) })


@NamedQueries({
	@NamedQuery(name="DetalleManifiestoDesecho.findAll", query="SELECT d FROM DetalleManifiestoDesecho d"),
	@NamedQuery(name = DetalleManifiestoDesecho.GET_HISTORIAL_POR_ID_NRO_OBSERVACION, query = "SELECT t FROM DetalleManifiestoDesecho t WHERE t.idRegistroOriginal = :idDetalle and estado = true and numeroObservacion = :nroObservacion ORDER BY t.id desc"),
	@NamedQuery(name = DetalleManifiestoDesecho.GET_HISTORIAL_POR_ID, query = "SELECT t FROM DetalleManifiestoDesecho t WHERE t.idRegistroOriginal = :idDetalle and estado = true ORDER BY t.id desc"),
	@NamedQuery(name = DetalleManifiestoDesecho.GET_POR_ID_MANIFIESTO, query = "SELECT t FROM DetalleManifiestoDesecho t WHERE t.idManifiesto = :idManifiesto and estado = true ORDER BY t.id desc"),
	@NamedQuery(name = DetalleManifiestoDesecho.GET_HISTORIAL_ELIMINADOS_POR_MANIFIESTO, 
	query = "SELECT t FROM DetalleManifiestoDesecho t WHERE t.idManifiesto = :idManifiesto and estado = true and idRegistroOriginal is not null and idRegistroOriginal in (SELECT a.id FROM DetalleManifiestoDesecho a where a.idManifiesto = :idManifiesto and a.estado = false and a.idRegistroOriginal is null ) ORDER BY t.id desc") })

public class DetalleManifiestoDesecho extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String GET_HISTORIAL_POR_ID_NRO_OBSERVACION = "ec.gob.ambiente.retce.model.DetalleManifiestoDesecho.getHistorialPorIdNroObservacion";
	public static final String GET_HISTORIAL_POR_ID = "ec.gob.ambiente.retce.model.DetalleManifiestoDesecho.getHistorialPorId";
	public static final String GET_POR_ID_MANIFIESTO = "ec.gob.ambiente.retce.model.DetalleManifiestoDesecho.getPorIdManifiesto";
	public static final String GET_HISTORIAL_ELIMINADOS_POR_MANIFIESTO = "ec.gob.ambiente.retce.model.Manifiesto.getHistorialEliminadosPorManifiesto";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "dmwa_id")
	private Integer id;	

	@Getter
	@Setter
	@Column(name="dmwa_quantity")
	private Double cantidad;
	
	@Getter
	@Setter
	@Column(name="dmwa_quantity_tons")
	private Double cantidadToneladas;

	@Getter
	@Setter
	@Column(name="dmwa_quantity_units")
	private Integer cantidadEnUnidades;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="gcde_id_unit_type")
	private DetalleCatalogoGeneral tipoUnidad;
	
    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "wada_id")
    private DesechoPeligroso desechoPeligroso;

//	@Getter
//    @Setter
//	@ManyToOne
//	@JoinColumn(name="dema_id")
//	private Manifiesto manifiesto;
    @Getter
	@Setter
	@Column(name="dema_id")
	private Integer idManifiesto;

	@Getter
	@Setter
	@Column(name="dmwa_history")
	private Boolean historial;
    
    @Getter
	@Setter
	@Column(name="dmwa_original_record_id")
	private Integer idRegistroOriginal;
    
    @Getter
	@Setter
	@Column(name="dmwa_observation_number")
	private Integer numeroObservacion;
    
    @Getter
	@Setter
	@Transient
	private Boolean nuevoEnModificacion;    

    public boolean equalsObject(Object obj) {
  		if (obj == null)
  			return false;
  		DetalleManifiestoDesecho base = (DetalleManifiestoDesecho) obj;
  		if (this.getId() == null && base.getId() == null)
  			return super.equals(obj);
  		if (this.getId() == null || base.getId() == null)
  			return false;
  		
  		if (this.getId().equals(base.getId())
  				&& ((this.getTipoUnidad() == null && base.getTipoUnidad() == null) || 
  						(this.getTipoUnidad() != null && base.getTipoUnidad() != null && 
  						this.getTipoUnidad().getId().equals(base.getTipoUnidad().getId())))
  				&& ((this.getCantidad() == null && base.getCantidad() == null) || 
  						(this.getCantidad() != null && base.getCantidad() != null 
  						&& this.getCantidad().equals(base.getCantidad())))
  				&& ((this.getCantidadEnUnidades() == null && base.getCantidadEnUnidades() == null) || 
  						(this.getCantidadEnUnidades() != null && base.getCantidadEnUnidades() != null 
  						&& this.getCantidadEnUnidades().equals(base.getCantidadEnUnidades())))
  				&& ((this.getDesechoPeligroso() == null && base.getDesechoPeligroso() == null) || 
  						(this.getDesechoPeligroso() != null && base.getDesechoPeligroso() != null && 
  						this.getDesechoPeligroso().getId().equals(base.getDesechoPeligroso().getId())))
  			)
  			return true;
  		else
  			return false;
  	}

}