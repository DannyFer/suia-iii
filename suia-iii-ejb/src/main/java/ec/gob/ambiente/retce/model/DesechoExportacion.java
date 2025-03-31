package ec.gob.ambiente.retce.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the waste_export database table.
 * 
 */
@Entity
@Table(name="waste_export", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "waex_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "waex_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "waex_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "waex_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "waex_user_update")) })

@NamedQueries({
	@NamedQuery(name="DesechoExportacion.findAll", query="SELECT d FROM DesechoExportacion d"),
	@NamedQuery(name = DesechoExportacion.GET_LISTA_DESECHOS_EXPORTACION_POR_RGDRETCE, query = "SELECT t FROM DesechoExportacion t WHERE t.idGeneradorRetce = :idRgdRetce and estado = true and idRegistroOriginal is null ORDER BY t.id desc"),
	@NamedQuery(name = DesechoExportacion.GET_HISTORIAL_POR_ID_NRO_OBSERVACION, query = "SELECT t FROM DesechoExportacion t WHERE t.idRegistroOriginal = :idDesecho and estado = true and numeroObservacion = :nroObservacion ORDER BY t.id desc"),
	@NamedQuery(name = DesechoExportacion.GET_HISTORIAL_POR_ID, query = "SELECT t FROM DesechoExportacion t WHERE t.idRegistroOriginal = :idDesecho and estado = true ORDER BY t.id desc"),
	@NamedQuery(name = DesechoExportacion.GET_HISTORIAL_ELIMINADOS_POR_RGDRETCE, 
	query = "SELECT t FROM DesechoExportacion t WHERE t.idGeneradorRetce = :idRgdRetce and estado = true and idRegistroOriginal is not null and idRegistroOriginal in (SELECT a.id FROM DesechoExportacion a where a.idGeneradorRetce = :idRgdRetce and a.estado = false and a.idRegistroOriginal is null ) ORDER BY t.id desc") })

public class DesechoExportacion extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String GET_LISTA_DESECHOS_EXPORTACION_POR_RGDRETCE = "ec.gob.ambiente.retce.model.DesechoExportacion.getListaDesechosExportacionPorRgdRetce";
	public static final String GET_HISTORIAL_POR_ID_NRO_OBSERVACION = "ec.gob.ambiente.retce.model.DesechoExportacion.getHistorialPorIdNroObservacion";
	public static final String GET_HISTORIAL_POR_ID = "ec.gob.ambiente.retce.model.DesechoExportacion.getHistorialPorId";
	public static final String GET_HISTORIAL_ELIMINADOS_POR_RGDRETCE = "ec.gob.ambiente.retce.model.DesechoExportacion.getHistorialEliminadosPorRgdRetce";
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="waex_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="gelo_id")
	private UbicacionesGeografica paisDestino;
	
	@Getter
	@Setter
	@Column(name="hwgr_id")
	private Integer idGeneradorRetce;

	@Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "wada_id")
    private DesechoPeligroso desechoPeligroso;

	@Getter
    @Setter
	@Column(name="waex_quantity")
	private Double cantidad;
	
	@Getter
	@Setter
	@Column(name="waex_history")
	private Boolean historial;
    
    @Getter
	@Setter
	@Column(name="waex_original_record_id")
	private Integer idRegistroOriginal;
    
    @Getter
	@Setter
	@Column(name="waex_observation_number")
	private Integer numeroObservacion;
    
    @Getter
	@Setter
	@Transient
	private List<Documento> documentosNotificacion, documentosAutorizacion, documentosMovimiento, documentosDestruccion;
    
    @Getter
	@Setter
	@Transient
	private Boolean nuevoEnModificacion;
    
    @Getter
	@Setter
	@Transient
    private List<DesechoExportacion> listaHistorial;
    
    @Getter
	@Setter
	@Transient
	private List<DocumentoExportacion> listaDocumentosExportacion, listaDocumentosExportacionHistorial;
    
    public boolean equalsObject(Object obj) {
  		if (obj == null)
  			return false;
  		DesechoExportacion base = (DesechoExportacion) obj;
  		if (this.getId() == null && base.getId() == null)
  			return super.equals(obj);
  		if (this.getId() == null || base.getId() == null)
  			return false;
  		
  		if (this.getId().equals(base.getId())
  				&& ((this.getPaisDestino() == null && base.getPaisDestino() == null) || 
  						(this.getPaisDestino() != null && base.getPaisDestino() != null && 
  						this.getPaisDestino().getId().equals(base.getPaisDestino().getId())))
  				&& ((this.getCantidad() == null && base.getCantidad() == null) || 
  						(this.getCantidad() != null && base.getCantidad() != null 
  						&& this.getCantidad().equals(base.getCantidad())))
  			)
  			return true;
  		else
  			return false;
  	}

}