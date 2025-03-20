package ec.gob.ambiente.retce.model;

import java.io.Serializable;
import java.util.Date;

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

import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.SedePrestadorServiciosDesechos;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the general_catalog database table.
 * 
 */
@Entity
@Table(name="unique_manifest_transfer", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "umt_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "umt_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "umt_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "umt_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "umt_user_update")) })
@NamedQueries({
	@NamedQuery(name = "ManifiestoUnicoTransferencia.findAll", query = "SELECT t FROM ManifiestoUnicoTransferencia t"),
	@NamedQuery(name = ManifiestoUnicoTransferencia.FIND_BY_ID, query = "SELECT t FROM ManifiestoUnicoTransferencia t WHERE t.id= :idManifiestoUnicoTransferencia and estado = true"),
	@NamedQuery(name = ManifiestoUnicoTransferencia.GET_BY_MANIFIESTO, query = "SELECT t FROM ManifiestoUnicoTransferencia t WHERE t.manifiestoUnico.id = :idManifiestoUnico and estado = true ORDER BY t.id desc")
})
public class ManifiestoUnicoTransferencia extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String FIND_BY_ID = "ec.com.magmasoft.business.domain.ManifiestoUnicoTransferencia.findById";
	public static final String GET_BY_MANIFIESTO = "ec.com.magmasoft.business.domain.ManifiestoUnicoTransferencia.getTransferenciaDesechoPeligrosoGestorPorManifiestoUnico";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="umt_id")
	private Integer id;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Getter
	@Setter
	@Column(name="umt_transfer_date")
	private Date fechaTransferencia;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="hwpl_id")
	private SedePrestadorServiciosDesechos sedePrestadorServiciosDesechos;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="unm_id")
	private ManifiestoUnico manifiestoUnico;

	@Getter
	@Setter
	@Column(name="umt_history")
	private Boolean historial;
	
	@Getter
	@Setter
	@Column(name="umt_id_owner")
	private Integer idPertenece;

}