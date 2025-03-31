package ec.gob.ambiente.rcoa.model;

import java.io.Serializable;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;


/**
 * The persistent class for the payment database table.
 * 
 */
@Entity
@Table(name="payment", schema = "coa_mae")
@NamedQuery(name="PagoKushki.findAll", query="SELECT p FROM PagoKushki p")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "paym_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "paym_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "paym_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "paym_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "paym_user_update")) })
public class PagoKushki extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	@Setter
	@Getter
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
	@Column(name="paym_id",unique=true, nullable = false)
	private Integer pago_Id;
	@Setter
	@Getter
	@Column(name="fini_id")
	private Integer financiero_Id;
	@Setter
	@Getter
	@Column(name="paym_json")
	private String pago_Json;
	@Setter
	@Getter
	@Column(name="paym_observation_bd")
	private String pago_observacion_bd;
	@Setter
	@Getter
	@Column(name="paym_paid_value")
	private BigDecimal pago_valor;
	@Setter
	@Getter
	@Column(name="paym_response_status")
	private Integer pago_response_status;
	@Setter
	@Getter
	@Column(name="paym_service_commission")
	private BigDecimal pago_comision_servicio;
	@Setter
	@Getter
	@Column(name="paym_status_date")
	private Timestamp pago_status_fecha;
	@Setter
	@Getter
	@Column(name="paym_total_payment")
	private BigDecimal pago_valor_total;
	@Setter
	@Getter
	@Column(name="paym_transaction_value")
	private BigDecimal pago_valor_transaccion;
	@Setter
	@Getter
	@Column(name="prco_id")
	private Integer codigo_proyecto;
	@Setter
	@Getter
	@Column(name="paco_id")
	private Integer comision_id;	
	public PagoKushki() {
	}
	

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setId(Integer id) {
		// TODO Auto-generated method stub
		
	}	

}