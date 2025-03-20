package ec.gob.ambiente.retce.model;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Where;

import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the detail_manifests database table.
 * 
 */
@Entity
@Table(name="detail_manifests", schema = "retce")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "dema_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "dema_date_create")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "dema_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "dema_user_create")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "dema_user_update")) })

@NamedQueries({
	@NamedQuery(name="Manifiesto.findAll", query="SELECT m FROM Manifiesto m"),
	@NamedQuery(name = Manifiesto.GET_LISTA_POR_TRANSPORTE_MEDIO_PROPIO, query = "SELECT t FROM Manifiesto t WHERE t.transporteMediosPropios.id = :idMedioPropio and estado = true and t.idRegistroOriginal is null ORDER BY t.id desc"),
	@NamedQuery(name = Manifiesto.GET_LISTA_POR_GESTOR_AMBIENTAL, query = "SELECT t FROM Manifiesto t WHERE t.transporteGestorAmbiental.id = :idGestorAmbiental and estado = true and t.idRegistroOriginal is null ORDER BY t.id desc"),
	@NamedQuery(name = Manifiesto.GET_HISTORIAL_POR_ID_NRO_OBSERVACION, query = "SELECT t FROM Manifiesto t WHERE t.idRegistroOriginal = :idManifiesto and estado = true and numeroObservacion = :nroObservacion ORDER BY t.id desc"),
	@NamedQuery(name = Manifiesto.GET_HISTORIAL_POR_ID, query = "SELECT t FROM Manifiesto t WHERE t.idRegistroOriginal = :idManifiesto and estado = true ORDER BY t.id desc"),
	@NamedQuery(name = Manifiesto.GET_HISTORIAL_ELIMINADOS_POR_TRANSPORTE_MEDIO_PROPIO, 
	query = "SELECT t FROM Manifiesto t WHERE t.transporteMediosPropios.id = :idMedioPropio and estado = true and idRegistroOriginal is not null and idRegistroOriginal in (SELECT a.id FROM Manifiesto a where a.transporteMediosPropios.id = :idMedioPropio and a.estado = false and a.idRegistroOriginal is null ) ORDER BY t.id desc"),
	@NamedQuery(name = Manifiesto.GET_HISTORIAL_ELIMINADOS_POR_GESTOR_AMBIENTAL, 
	query = "SELECT t FROM Manifiesto t WHERE t.transporteGestorAmbiental.id = :idGestorAmbiental and estado = true and idRegistroOriginal is not null and idRegistroOriginal in (SELECT a.id FROM Manifiesto a where a.transporteGestorAmbiental.id = :idGestorAmbiental and a.estado = false and a.idRegistroOriginal is null ) ORDER BY t.id desc") })

public class Manifiesto extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String GET_LISTA_POR_TRANSPORTE_MEDIO_PROPIO = "ec.gob.ambiente.retce.model.Manifiesto.getListaPorTransporteMedioPropio";
	public static final String GET_LISTA_POR_GESTOR_AMBIENTAL = "ec.gob.ambiente.retce.model.Manifiesto.getListaPorGestorAmbiental";
	public static final String GET_HISTORIAL_POR_ID_NRO_OBSERVACION = "ec.gob.ambiente.retce.model.Manifiesto.getHistorialPorIdNroObservacion";
	public static final String GET_HISTORIAL_POR_ID = "ec.gob.ambiente.retce.model.Manifiesto.getHistorialPorId";
	public static final String GET_HISTORIAL_ELIMINADOS_POR_TRANSPORTE_MEDIO_PROPIO = "ec.gob.ambiente.retce.model.Manifiesto.getHistorialEliminadosPorTransporteMedioPropio";
	public static final String GET_HISTORIAL_ELIMINADOS_POR_GESTOR_AMBIENTAL = "ec.gob.ambiente.retce.model.Manifiesto.getHistorialEliminadosPorGestorAmbiental";
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "dema_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="dema_boarding_date")
	private Date fechaEmbarque;

	@Getter
	@Setter
	@Column(name="dema_manifest_number")
	private String numeroManifiesto;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "tema_id")
	private TransporteGestorAmbiental transporteGestorAmbiental;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "tome_id")
	private TransporteMediosPropios transporteMediosPropios;
	
	@Getter
	@Setter
	@OneToMany(mappedBy = "idManifiesto", fetch = FetchType.LAZY)
	@Where(clause = "dmwa_status = 'TRUE' and dmwa_original_record_id is null")
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<DetalleManifiestoDesecho> listaManifiestoDesechos;
	
	@Getter
	@Setter
	@Transient
	private Documento manifiestoUnico;
	
	@Getter
	@Setter
	@Column(name="dema_history")
	private Boolean historial;
    
    @Getter
	@Setter
	@Column(name="dema_original_record_id")
	private Integer idRegistroOriginal;
    
    @Getter
	@Setter
	@Column(name="dema_observation_number")
	private Integer numeroObservacion;
    
    @Getter
	@Setter
	@Transient
	private Boolean nuevoEnModificacion;
    
    @Getter
	@Setter
	@Transient
	private List<Manifiesto> listaHistorial;
    
    @Getter
	@Setter
	@Transient
	private List<DetalleManifiestoDesecho> listaHistorialDetalle;
    
    @Getter
	@Setter
	@Transient
	private List<Documento> listaHistorialDocumentos;
    
    @Getter
	@Setter
	@Transient
	private List<DetalleManifiestoDesecho> listaDetallesEliminados;
    
    public boolean equalsObject(Object obj) {
  		if (obj == null)
  			return false;
  		Manifiesto base = (Manifiesto) obj;
  		if (this.getId() == null && base.getId() == null)
  			return super.equals(obj);
  		if (this.getId() == null || base.getId() == null)
  			return false;
  		
  		if (this.getId().equals(base.getId())
  				&& this.getFechaEmbarque().compareTo(base.getFechaEmbarque()) == 0
  				&& this.getNumeroManifiesto().equals(base.getNumeroManifiesto())
  				&& ((this.getManifiestoUnico() == null && base.getManifiestoUnico() == null) || 
  						(this.getManifiestoUnico() != null && base.getManifiestoUnico() != null && 
  						this.getManifiestoUnico().getId().equals(base.getManifiestoUnico().getId())))
  			)
  			return true;
  		else
  			return false;
  	}
}