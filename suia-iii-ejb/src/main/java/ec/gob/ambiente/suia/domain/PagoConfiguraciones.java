package ec.gob.ambiente.suia.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.ForeignKey;

/**
 * The persistent class for the payments_configurations database table.
 * 
 */
@Entity
@Table(name="payments_configurations", schema = "suia_iii")

public class PagoConfiguraciones implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name = "PAGO_CONFIGURACIONES_GENERATOR", sequenceName = "seq_paco_id", schema = "suia_iii")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PAGO_CONFIGURACIONES_GENERATOR")
	@Column(name="paco_id")
	@Getter
	@Setter
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "paco_name")
	private String pacoName;
	
	@Getter
	@Setter
	@Column(name = "paco_description")
	private String pacoDescripcion;
	
	@Getter
	@Setter
	@Column(name = "paco_status")
	private boolean pacoStatus;
	
	@Getter
	@Setter
	@Column(name = "paco_value")
	private String pacoValue;
	
	@Getter
	@Setter
	@Column(name = "paco_start_date")
	private String pacoStartDate;
	
	@Getter
	@Setter
	@Column(name = "paco_end_date")
	private String pacoEndDate;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "flow_id", referencedColumnName = "flow_id")
	@ForeignKey(name="fk_payments_configuration_flow_id_flows_flow_id")
	private Flujo flujoId;
	
	public PagoConfiguraciones(String pacoDescripcion, String pacoValue) {
		this.pacoDescripcion = pacoDescripcion;
		this.pacoValue = pacoValue;
	}
	
	public PagoConfiguraciones() {
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof PagoConfiguraciones)) {
			return false;
		}
		return ((PagoConfiguraciones) obj).getPacoDescripcion().equals(getPacoDescripcion());
	}
	
}