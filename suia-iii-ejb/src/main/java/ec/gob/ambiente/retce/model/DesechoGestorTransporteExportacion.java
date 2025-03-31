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
@Table(name="waste_manager_transport_export", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "wmte_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "wmte_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "wmte_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "wmte_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "wmte_user_update")) })
@NamedQueries({
	@NamedQuery(name = "DesechoGestorTransporteExportacion.findAll", query = "SELECT dge FROM DesechoGestorTransporteExportacion dge"),
	@NamedQuery(name = DesechoGestorTransporteExportacion.FIND_BY_ID, query = "SELECT dge FROM DesechoGestorTransporteExportacion dge WHERE dge.id= :id and estado = true"),
	@NamedQuery(name = DesechoGestorTransporteExportacion.GET_BY_DESECHO_GESTOR_TRANSPORTE, query = "SELECT dge FROM DesechoGestorTransporteExportacion dge WHERE dge.desechoGestorTransporte.id = :idDesechoGestorTransporte and estado = true ORDER BY dge.id desc")
})
public class DesechoGestorTransporteExportacion extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String FIND_BY_ID = "ec.com.magmasoft.business.domain.DesechoGestorTransporteExportacion.findById";
	public static final String GET_BY_DESECHO_GESTOR_TRANSPORTE = "ec.gob.ambiente.retce.model.DesechoGestorTransporteExportacion.getPorGestorDesechoTransporte";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="wmte_id")
	private Integer id;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="wmt_id")
	private DesechoGestorTransporte desechoGestorTransporte;

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
	@Column(name="wmte_amount")
	private Double cantidad;
	
	@ManyToOne
	@Getter
	@Setter
	@JoinColumn(name = "gelo_id_destination")
	private UbicacionesGeografica pais;
	
	@Getter
	@Setter
	@Column(name="wmte_history")
	private Boolean historial;
	
	@Getter
	@Setter
	@Column(name="wmte_id_owner")
	private Integer idPertenece;
	
	@Getter
	@Setter
	@Transient
	private Documento documentoAutorizacion;
	
}