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

import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the general_catalog database table.
 * 
 */
@Entity
@Table(name="waste_manager_storage_export", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "wmse_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "wmse_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "wmse_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "wmse_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "wmse_user_update")) })
@NamedQueries({
	@NamedQuery(name = "DesechoGestorAlmacenamientoExportacion.findAll", query = "SELECT dae FROM DesechoGestorAlmacenamientoExportacion dae"),
	@NamedQuery(name = DesechoGestorAlmacenamientoExportacion.FIND_BY_ID, query = "SELECT dae FROM DesechoGestorAlmacenamientoExportacion dae WHERE dae.id= :id and estado = true"),
	@NamedQuery(name = DesechoGestorAlmacenamientoExportacion.GET_BY_GESTOR_ALMACENAMIENTO, query = "SELECT dae FROM DesechoGestorAlmacenamientoExportacion dae WHERE dae.desechoGestorAlmacenamiento.id = :idDesechoGestorAlmacenamiento and estado = true ORDER BY dae.id desc")
})
public class DesechoGestorAlmacenamientoExportacion extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String FIND_BY_ID = "ec.com.magmasoft.business.domain.DesechoGestorAlmacenamientoExportacion.findById";
	public static final String GET_BY_GESTOR_ALMACENAMIENTO = "ec.gob.ambiente.retce.model.DesechoGestorAlmacenamientoExportacion.getDesechoExportacionPorGestorDesechoPeligroso";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="wmse_id")
	private Integer id;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="wada_id")
	private DesechoPeligroso desechoPeligroso;

	@Getter
	@Setter
	@Transient
	private String clave;
	
	
	@Getter
	@Setter
	@Column(name="wmse_amount")
	private Double cantidad;
	
	@ManyToOne
	@Getter
	@Setter
	@JoinColumn(name = "gelo_id_destination")
	private UbicacionesGeografica pais;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="wms_id")
	private DesechoGestorAlmacenamiento desechoGestorAlmacenamiento;
	
	@Getter
	@Setter
	@Column(name="wmse_history")
	private Boolean historial;
	
	@Getter
	@Setter
	@Column(name="wmse_id_owner")
	private Integer idPertenece;
	
	@Getter
	@Setter
	@Transient
	private Documento documentoNotificacion, documentoAutorizacion, documentoMovimiento, documentoDestruccion;

}