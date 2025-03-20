package ec.gob.ambiente.retce.model;

import java.io.Serializable;

import javax.persistence.*;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the transport_environmental_manager database table.
 * 
 */
@Entity
@Table(name="transport_environmental_manager", schema = "retce")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "tema_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "tema_date_create")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "tema_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "tema_user_create")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "tema_user_update")) })

@NamedQueries({
	@NamedQuery(name="TransporteGestorAmbiental.findAll", query="SELECT t FROM TransporteGestorAmbiental t"),
	@NamedQuery(name = TransporteGestorAmbiental.GET_POR_RGDRETCE, query = "SELECT t FROM TransporteGestorAmbiental t WHERE t.idGeneradorRetce = :idRgdRetce and estado = true and idRegistroOriginal is null ORDER BY t.id desc"),
	@NamedQuery(name = TransporteGestorAmbiental.GET_HISTORIAL_POR_ID_NRO_OBSERVACION, query = "SELECT t FROM TransporteGestorAmbiental t WHERE t.idRegistroOriginal = :idGestor and estado = true and numeroObservacion = :nroObservacion ORDER BY t.id desc"),
	@NamedQuery(name = TransporteGestorAmbiental.GET_HISTORIAL_ELIMINADOS_POR_RGDRETCE, 
	query = "SELECT t FROM TransporteGestorAmbiental t WHERE t.idGeneradorRetce = :idRgdRetce and estado = true and idRegistroOriginal is not null and idRegistroOriginal in (SELECT a.id FROM TransporteGestorAmbiental a where a.idGeneradorRetce = :idRgdRetce and a.estado = false and a.idRegistroOriginal is null ) ORDER BY t.id desc") })

public class TransporteGestorAmbiental extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String GET_POR_RGDRETCE = "ec.gob.ambiente.retce.model.TransporteGestorAmbiental.getPorRgdRetce";
	public static final String GET_HISTORIAL_POR_ID_NRO_OBSERVACION = "ec.gob.ambiente.retce.model.TransporteGestorAmbiental.getHistorialPorIdNroObservacion";
	public static final String GET_HISTORIAL_ELIMINADOS_POR_RGDRETCE = "ec.gob.ambiente.retce.model.TransporteGestorAmbiental.getHistorialEliminadosPorRgdRetce";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tema_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="hwgr_id")
	private Integer idGeneradorRetce;
	
	@Getter
	@Setter
	@Column(name="tema_history")
	private Boolean historial;
    
    @Getter
	@Setter
	@Column(name="tema_original_record_id")
	private Integer idRegistroOriginal;
    
    @Getter
	@Setter
	@Column(name="tema_observation_number")
	private Integer numeroObservacion;
}