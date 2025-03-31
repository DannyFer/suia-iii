package ec.gob.ambiente.suia.domain;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name="payments", schema="suia_iii")
@NamedQueries({
@NamedQuery(name = "InterseccionProyecto.findPayment", query = "SELECT pf FROM Pago pf WHERE pf.proyectoLicenciamientoAmbiental.id = :idProyecto")})
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "paym_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "paym_status = 'TRUE'")
public class Pago extends EntidadBase{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6142314582891417808L;
	
	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "PAYMENTS_PAYMID_GENERATOR", sequenceName = "SEQ_PAYM_ID")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PAYMENTS_PAYMID_GENERATOR")
	@Column(name="paym_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name="paym_value_payment")
	private Float valorPago;
	
	@Getter
	@Setter
	@Column(name="paym_bank_branch", length=500)
	private String entidadBancaria;
	
	@Getter
	@Setter
	@Column(name="paym_account_nro", length=100)
	private String numeroCuenta;
	
	@Getter
	@Setter
	@Column(name="paym_reference", length=100)
	private String referencia;
	
	@Getter
	@Setter
	@Column(name="paym_observations", length=500)
	private String observaciones;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "pren_id", referencedColumnName = "pren_id")
	@ForeignKey(name = "fk_payments_projects_enviroment_licensing_pren_id")
	private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental;
	
}
