package ec.gob.ambiente.rcoa.model;

import java.io.Serializable;

import javax.persistence.*;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

import java.sql.Timestamp;
import java.util.Date;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the record_payment database table.
 * 
 */
@Entity
@Table(name="record_payment", schema="coa_environmental_record")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "repa_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "repa_creation_date")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "repa_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "repa_creator_user")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "repa_user_update")) })
@NamedQuery(name="PagoRegistroAmbiental.findAll", query="SELECT p FROM PagoRegistroAmbiental p")
public class PagoRegistroAmbiental extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="repa_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="enre_id")
	private RegistroAmbientalRcoa registroAmbientalRcoa;

	@Getter
	@Setter
	@Column(name="repa_bill_date")
	private Date fechaFactura;

	@Getter
	@Setter
	@Column(name="repa_bill_number")
	private String numeroFactura;

	@Getter
	@Setter	
	@Column(name="repa_payment_date")
	private Date fechaPago;

	@Getter
	@Setter
	@Column(name="repa_reference_number")
	private String numeroReferencia;

	@Getter
	@Setter
	@Column(name="repa_value_paid")
	private BigDecimal valorPagado;

	public PagoRegistroAmbiental() {
	}

	
}