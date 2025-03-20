package ec.gob.ambiente.rcoa.model;

import java.io.Serializable;

import javax.persistence.*;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the payment_commission database table.
 * 
 */
@Entity
@Table(name = "payment_commission", schema = "coa_mae")
@NamedQuery(name="PagoKushkiComision.findAll", query="SELECT p FROM PagoKushkiComision p")
public class PagoKushkiComision implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="paco_id")
	private Integer pacoId;

	@Column(name="paco_creation_date")
	private Timestamp pacoCreationDate;

	@Column(name="paco_creator_user")
	private String pacoCreatorUser;

	@Column(name="paco_fixed_rate")
	private BigDecimal pacoFixedRate;

	@Column(name="paco_maximum_value")
	private BigDecimal pacoMaximumValue;

	@Column(name="paco_minimum_value")
	private BigDecimal pacoMinimumValue;

	@Column(name="paco_observation_bd")
	private String pacoObservationBd;

	@Column(name="paco_total_fee")
	private BigDecimal pacoTotalFee;

	@Column(name="paco_transaction_value")
	private String pacoTransactionValue;

	@Column(name="paco_update_date")
	private Timestamp pacoUpdateDate;

	@Column(name="paco_user_update")
	private String pacoUserUpdate;

	@Column(name="paco_variable_fee")
	private BigDecimal pacoVariableFee;
	
	@Setter
	@Getter
	@Column(name="paco_status")
	private Boolean pacostatus;
	@Setter
	@Getter
	@Column(name="paco_status_date")
	private Timestamp pacostatusdate;

	//bi-directional many-to-one association to PagoKushki
	//@OneToMany(mappedBy="paymentCommission")
	//private List<PagoKushki> payments;

	public PagoKushkiComision() {
	}

	public Integer getPacoId() {
		return this.pacoId;
	}

	public void setPacoId(Integer pacoId) {
		this.pacoId = pacoId;
	}

	public Timestamp getPacoCreationDate() {
		return this.pacoCreationDate;
	}

	public void setPacoCreationDate(Timestamp pacoCreationDate) {
		this.pacoCreationDate = pacoCreationDate;
	}

	public String getPacoCreatorUser() {
		return this.pacoCreatorUser;
	}

	public void setPacoCreatorUser(String pacoCreatorUser) {
		this.pacoCreatorUser = pacoCreatorUser;
	}

	public BigDecimal getPacoFixedRate() {
		return this.pacoFixedRate;
	}

	public void setPacoFixedRate(BigDecimal pacoFixedRate) {
		this.pacoFixedRate = pacoFixedRate;
	}

	public BigDecimal getPacoMaximumValue() {
		return this.pacoMaximumValue;
	}

	public void setPacoMaximumValue(BigDecimal pacoMaximumValue) {
		this.pacoMaximumValue = pacoMaximumValue;
	}

	public BigDecimal getPacoMinimumValue() {
		return this.pacoMinimumValue;
	}

	public void setPacoMinimumValue(BigDecimal pacoMinimumValue) {
		this.pacoMinimumValue = pacoMinimumValue;
	}

	public String getPacoObservationBd() {
		return this.pacoObservationBd;
	}

	public void setPacoObservationBd(String pacoObservationBd) {
		this.pacoObservationBd = pacoObservationBd;
	}

	public BigDecimal getPacoTotalFee() {
		return this.pacoTotalFee;
	}

	public void setPacoTotalFee(BigDecimal pacoTotalFee) {
		this.pacoTotalFee = pacoTotalFee;
	}

	public String getPacoTransactionValue() {
		return this.pacoTransactionValue;
	}

	public void setPacoTransactionValue(String pacoTransactionValue) {
		this.pacoTransactionValue = pacoTransactionValue;
	}

	public Timestamp getPacoUpdateDate() {
		return this.pacoUpdateDate;
	}

	public void setPacoUpdateDate(Timestamp pacoUpdateDate) {
		this.pacoUpdateDate = pacoUpdateDate;
	}

	public String getPacoUserUpdate() {
		return this.pacoUserUpdate;
	}

	public void setPacoUserUpdate(String pacoUserUpdate) {
		this.pacoUserUpdate = pacoUserUpdate;
	}

	public BigDecimal getPacoVariableFee() {
		return this.pacoVariableFee;
	}

	public void setPacoVariableFee(BigDecimal pacoVariableFee) {
		this.pacoVariableFee = pacoVariableFee;
	}

	/*public List<PagoKushki> getPayments() {
		return this.payments;
	}

	public void setPayments(List<PagoKushki> payments) {
		this.payments = payments;
	}*/

	/*public PagoKushki addPayment(PagoKushki payment) {
		getPayments().add(payment);
		payment.setPaymentCommission(this);

		return payment;
	}

	public PagoKushki removePayment(PagoKushki payment) {
		getPayments().remove(payment);
		payment.setPaymentCommission(null);

		return payment;
	}*/

}