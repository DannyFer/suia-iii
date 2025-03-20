package ec.gob.ambiente.retce.model;

import java.io.Serializable;

import javax.persistence.*;

import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the disposal_outside_installation database table.
 * 
 */
@Entity
@Table(name="disposal_outside_installation", schema = "retce")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "doin_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "doin_date_create")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "doin_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "doin_user_create")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "doin_user_update")) })

@NamedQueries({
	@NamedQuery(name="DisposicionFueraInstalacion.findAll", query="SELECT d FROM DisposicionFueraInstalacion d"),
	@NamedQuery(name = DisposicionFueraInstalacion.GET_POR_RGDRETCE, query = "SELECT t FROM DisposicionFueraInstalacion t WHERE t.idGeneradorRetce = :idRgdRetce and estado = true and idRegistroOriginal is null ORDER BY t.id desc"),
	@NamedQuery(name = DisposicionFueraInstalacion.GET_HISTORIAL_POR_ID_NRO_OBSERVACION, query = "SELECT t FROM DisposicionFueraInstalacion t WHERE t.idRegistroOriginal = :idDesecho and estado = true and numeroObservacion = :nroObservacion ORDER BY t.id desc"),
	@NamedQuery(name = DisposicionFueraInstalacion.GET_HISTORIAL_DESECHOS_ELIMINADOS_POR_RGDRETCE, 
	query = "SELECT t FROM DisposicionFueraInstalacion t WHERE t.idGeneradorRetce = :idRgdRetce and estado = true and idRegistroOriginal is not null and idRegistroOriginal in (SELECT a.id FROM DisposicionFueraInstalacion a where a.idGeneradorRetce = :idRgdRetce and a.estado = false and a.idRegistroOriginal is null ) ORDER BY t.id desc")})

public class DisposicionFueraInstalacion extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String GET_POR_RGDRETCE = "ec.gob.ambiente.retce.model.DisposicionFueraInstalacion.getPorRgdRetce";
	public static final String GET_HISTORIAL_POR_ID_NRO_OBSERVACION = "ec.gob.ambiente.retce.model.DisposicionFueraInstalacion.getHistorialPorIdNroObservacion";
	public static final String GET_HISTORIAL_DESECHOS_ELIMINADOS_POR_RGDRETCE = "ec.gob.ambiente.retce.model.DisposicionFueraInstalacion.getHistorialDesechosEliminadosPorRgdRetce";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "doin_id")
	private Integer id;


	@Getter
	@Setter
	@Column(name="doin_quantity")
	private Double cantidad;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="gcde_id_unit_type")
	private DetalleCatalogoGeneral tipoUnidad;

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
	@Column(name="doin_history")
	private Boolean historial;
    
    @Getter
	@Setter
	@Column(name="doin_original_record_id")
	private Integer idRegistroOriginal;
    
    @Getter
	@Setter
	@Column(name="doin_observation_number")
	private Integer numeroObservacion;
    
    @Getter
	@Setter
	@Transient
	private Boolean nuevoEnModificacion;
}