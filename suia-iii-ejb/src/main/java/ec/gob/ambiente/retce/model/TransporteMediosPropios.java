package ec.gob.ambiente.retce.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;

/**
 * The persistent class for the transport_own_means database table.
 * 
 */
@Entity
@Table(name = "transport_own_means", schema = "retce")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "tome_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "tome_date_create")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "tome_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "tome_user_create")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "tome_user_update")) })

@NamedQueries({
	@NamedQuery(name = "TransporteMediosPropios.findAll", query = "SELECT t FROM TransporteMediosPropios t"),
	@NamedQuery(name = TransporteMediosPropios.GET_POR_ID, query = "SELECT t FROM TransporteMediosPropios t WHERE t.id = :idMedioPropio and estado = true ORDER BY t.id desc"),
	@NamedQuery(name = TransporteMediosPropios.GET_POR_RGDRETCE, query = "SELECT t FROM TransporteMediosPropios t WHERE t.idGeneradorRetce = :idRgdRetce and estado = true and idRegistroOriginal is null ORDER BY t.id desc"),
	@NamedQuery(name = TransporteMediosPropios.GET_HISTORIAL_POR_ID_NRO_OBSERVACION, query = "SELECT t FROM TransporteMediosPropios t WHERE t.idRegistroOriginal = :idMedioPropio and estado = true and numeroObservacion = :nroObservacion ORDER BY t.id desc"),
	@NamedQuery(name = TransporteMediosPropios.GET_HISTORIAL_POR_ID, query = "SELECT t FROM TransporteMediosPropios t WHERE t.idRegistroOriginal = :idMedioPropio and estado = true ORDER BY t.id desc"),
	@NamedQuery(name = TransporteMediosPropios.GET_HISTORIAL_ELIMINADOS_POR_RGDRETCE, 
	query = "SELECT t FROM TransporteMediosPropios t WHERE t.idGeneradorRetce = :idRgdRetce and estado = true and idRegistroOriginal is not null and idRegistroOriginal in (SELECT a.id FROM TransporteMediosPropios a where a.idGeneradorRetce = :idRgdRetce and a.estado = false and a.idRegistroOriginal is null ) ORDER BY t.id desc") })

public class TransporteMediosPropios extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String GET_POR_ID = "ec.gob.ambiente.retce.model.TransporteMediosPropios.getPorId";
	public static final String GET_POR_RGDRETCE = "ec.gob.ambiente.retce.model.TransporteMediosPropios.getPorRgdRetce";
	public static final String GET_HISTORIAL_POR_ID_NRO_OBSERVACION = "ec.gob.ambiente.retce.model.TransporteMediosPropios.getHistorialPorIdNroObservacion";
	public static final String GET_HISTORIAL_POR_ID = "ec.gob.ambiente.retce.model.TransporteMediosPropios.getHistorialPorId";
	public static final String GET_HISTORIAL_ELIMINADOS_POR_RGDRETCE = "ec.gob.ambiente.retce.model.TransporteMediosPropios.getHistorialEliminadosPorRgdRetce";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tome_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "tome_value_transport_type")
	private String nroResolucion;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="gcde_id_transport_type")
	private DetalleCatalogoGeneral tipoResolucion;

	@Getter
	@Setter
	@Column(name="hwgr_id")
	private Integer idGeneradorRetce;

	@Getter
	@Setter
	@Column(name="tome_history")
	private Boolean historial;
    
    @Getter
	@Setter
	@Column(name="tome_original_record_id")
	private Integer idRegistroOriginal;
    
    @Getter
	@Setter
	@Column(name="tome_observation_number")
	private Integer numeroObservacion;
    
    @Getter
	@Setter
	@Transient
	private Boolean nuevoEnModificacion;
    
    @Getter
	@Setter
	@Transient
    private List<TransporteMediosPropios> listaHistorial;
    

    public boolean equalsObject(Object obj) {
  		if (obj == null)
  			return false;
  		TransporteMediosPropios base = (TransporteMediosPropios) obj;
  		if (this.getId() == null && base.getId() == null)
  			return super.equals(obj);
  		if (this.getId() == null || base.getId() == null)
  			return false;
  		
  		if (this.getId().equals(base.getId())
  				&& ((this.getTipoResolucion() == null && base.getTipoResolucion() == null) || 
  						(this.getTipoResolucion() != null && base.getTipoResolucion() != null && 
  						this.getTipoResolucion().getId().equals(base.getTipoResolucion().getId())))
  				&& ((this.getNroResolucion() == null && base.getNroResolucion() == null) || 
  						(this.getNroResolucion() != null && base.getNroResolucion() != null 
  						&& this.getNroResolucion().equals(base.getNroResolucion())))
  			)
  			return true;
  		else
  			return false;
  	}
}