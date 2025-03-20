package ec.gob.ambiente.retce.model;

import java.io.Serializable;

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

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;


/**
 * The persistent class for the general_catalog database table.
 * 
 */
@Entity
@Table(name="waste_manager_transport", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "wmt_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "wmt_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "wmt_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "wmt_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "wmt_user_update")) })
@NamedQueries({
	@NamedQuery(name = "DesechoGestorTransporte.findAll", query = "SELECT d FROM DesechoGestorTransporte d"),
	@NamedQuery(name = DesechoGestorTransporte.FIND_BY_ID, query = "SELECT d FROM DesechoGestorTransporte d WHERE d.id= :id and estado = true"),
	@NamedQuery(name = DesechoGestorTransporte.GET_BY_MANIFIESTO, query = "SELECT d FROM DesechoGestorTransporte d WHERE d.manifiestoUnico.id = :idManifiestoUnico and estado = true ORDER BY d.id desc")
})
public class DesechoGestorTransporte extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String FIND_BY_ID = "ec.com.magmasoft.business.domain.DesechoGestorTransporte.findById";
	public static final String GET_BY_MANIFIESTO = "ec.com.magmasoft.business.domain.DesechoGestorTransporte.getDesechoPeligrosoGestorPorManifiestoUnico";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="wmt_id")
	private Integer id;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="wada_id")
	private DesechoPeligroso desechoPeligroso;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="gcde_id_unit")
	private DetalleCatalogoGeneral unidadDesechoTransporte;
	
	@Getter
	@Setter
	@Transient
	private String clave;
	
	@Getter
	@Setter
	@Transient
	private Double cantidad;

	@Getter
	@Setter
	@Column(name="wmt_amount_tons")
	private Double cantidadToneladas;
	
	@Getter
	@Setter
	@Column(name="wmt_amount_kilo")
	private Double cantidadKilogramos;

	@Getter
	@Setter
	@Column(name="wmt_unit_report")
	private Integer reporteUnidades;
	
	@Getter
	@Setter
	@Column(name="wmt_exportation")
	private Boolean esExportacion;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="unm_id")
	private ManifiestoUnico manifiestoUnico;
	
	@Getter
	@Setter
	@Column(name="wmt_history")
	private Boolean historial;
	
	@Getter
	@Setter
	@Column(name="wmt_id_owner")
	private Integer idPertenece;
	

}
