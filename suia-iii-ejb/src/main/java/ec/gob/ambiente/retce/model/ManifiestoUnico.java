package ec.gob.ambiente.retce.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;


/**
 * The persistent class for the general_catalog database table.
 * 
 */
@Entity
@Table(name="unique_manifest_manager", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "unm_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "unm_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "unm_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "unm_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "unm_user_update")) })
@NamedQueries({
	@NamedQuery(name = "ManifiestoUnico.findAll", query = "SELECT mu FROM ManifiestoUnico mu"),
	@NamedQuery(name = ManifiestoUnico.FIND_BY_ID, query = "SELECT mu FROM ManifiestoUnico mu WHERE mu.id= :idManifiestoUnico and estado = true"),
	@NamedQuery(name = ManifiestoUnico.GET_BY_GESTOR_DESECHO_PELIGROSO, query = "SELECT mu FROM ManifiestoUnico mu WHERE mu.gestorDesechosPeligrosos.id = :idGestorDesechosPeligroso and estado = true ORDER BY mu.id desc"),
	@NamedQuery(name = ManifiestoUnico.GET_BY_GESTOR_FASE, query = "SELECT mu FROM ManifiestoUnico mu WHERE mu.gestorDesechosPeligrosos.id = :idGestorDesechosPeligroso and mu.faseManifiesto.id = :idFase and estado = true ORDER BY mu.id desc"),
	@NamedQuery(name = ManifiestoUnico.GET_BY_GESTOR_FASE_TIPO, query = "SELECT mu FROM ManifiestoUnico mu WHERE mu.gestorDesechosPeligrosos.id = :idGestorDesechosPeligroso and mu.faseManifiesto.id = :idFase and mu.tipoManifiesto.id = :idTipo and estado = true ORDER BY mu.id desc")
})
public class ManifiestoUnico extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String FIND_BY_ID = "ec.com.magmasoft.business.domain.ManifiestoUnico.findById";
	public static final String GET_BY_GESTOR_DESECHO_PELIGROSO = "ec.gob.ambiente.retce.model.ManifiestoUnico.getManifiestoUnicoPorGestorDesechoPeligroso";
	public static final String GET_BY_GESTOR_FASE = "ec.gob.ambiente.retce.model.ManifiestoUnico.getPorGestorFase";
	public static final String GET_BY_GESTOR_FASE_TIPO = "ec.gob.ambiente.retce.model.ManifiestoUnico.getPorGestorFaseTipo";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="unm_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="gcde_id_phase")
//	Para manifiestos Transferencia en Modalidad: Tratamiento (Eliminacion) y Disposición Final
	private DetalleCatalogoGeneral faseManifiesto;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="gcde_id_manifest_type")
//	Para manifiestos - Fase almacenamiento - Recepcion / Transferencia
	private DetalleCatalogoGeneral tipoManifiesto;
	
	@Getter
	@Setter
	@Column(name="unm_manifest_number")
	private String numeroManifiesto;
	
	@Getter
	@Setter
	@Column(name="unm_rgd_number")
	private String numeroRGD;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Getter
	@Setter
	@Column(name="unm_reception_date")
	private Date fechaRecepcion;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="hwm_id")
	private GestorDesechosPeligrosos gestorDesechosPeligrosos;
	
	@Getter
	@Setter
	@Column(name="unm_history")
	private Boolean historial;
	
	@Getter
	@Setter
	@Column(name="unm_id_owner")
	private Integer idPertenece;

	@Getter
	@Setter
	@Transient
	private Documento adjuntoManifiesto;
	
	@Getter
	@Transient
	public List<DesechoGestorTransporte> listDesechoTransporte = new ArrayList<DesechoGestorTransporte>();
	@Getter
	@Transient
	public List<DesechoGestorAlmacenamiento> listDesechoRecepcion = new ArrayList<DesechoGestorAlmacenamiento>();
	@Getter
	@Transient
	public List<DesechoGestorAlmacenamiento> listDesechoTransferencia = new ArrayList<DesechoGestorAlmacenamiento>();
	@Getter
	@Transient
	public List<ManifiestoUnicoTransferencia> listEmpresasRecepcion = new ArrayList<ManifiestoUnicoTransferencia>();
	@Getter
	@Transient
	public List<ManifiestoUnicoTransferencia> listEmpresasTransferencia = new ArrayList<ManifiestoUnicoTransferencia>();
	@Getter
	@Transient
	public List<ManifiestoUnicoTransferencia> listEmpresasTransporte = new ArrayList<ManifiestoUnicoTransferencia>();
	
	
}
