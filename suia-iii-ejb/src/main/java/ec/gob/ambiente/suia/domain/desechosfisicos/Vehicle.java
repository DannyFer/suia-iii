package ec.gob.ambiente.suia.domain.desechosfisicos;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the vehicles database table.
 * 
 */
@Entity
@Table(name="vehicles", schema="waste_dangerous")
@NamedQuery(name="Vehicle.findAll", query="SELECT v FROM Vehicle v")
public class Vehicle implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@Column(name="vehi_id")
	private Integer vehiId;

	@Column(name="user_id_add")
	private Integer userIdAdd;

	@Column(name="user_id_update")
	private Integer userIdUpdate;

	@Column(name="vehi_date_add")
	private Date vehiDateAdd;

	@Column(name="vehi_date_update")
	private Date vehiDateUpdate;

	@Column(name="vehi_registration")
	private String vehiRegistration;

	@Column(name="vehi_status")
	private Boolean vehiStatus;
	
	@Column(name="vehi_other")
	private String vehiOther;	

	//bi-directional many-to-one association to Modality
	@ManyToOne
	@JoinColumn(name="moda_id")
	private Modality modality;
	
	//bi-directional many-to-one association to Catalog
	@ManyToOne
	@JoinColumn(name="vehi_type")
	private Catalog vehicleType;

	public Vehicle() {
	}

	public Integer getVehiId() {
		return this.vehiId;
	}

	public void setVehiId(Integer vehiId) {
		this.vehiId = vehiId;
	}

	public Integer getUserIdAdd() {
		return this.userIdAdd;
	}

	public void setUserIdAdd(Integer userIdAdd) {
		this.userIdAdd = userIdAdd;
	}

	public Integer getUserIdUpdate() {
		return this.userIdUpdate;
	}

	public void setUserIdUpdate(Integer userIdUpdate) {
		this.userIdUpdate = userIdUpdate;
	}

	public Date getVehiDateAdd() {
		return this.vehiDateAdd;
	}

	public void setVehiDateAdd(Date vehiDateAdd) {
		this.vehiDateAdd = vehiDateAdd;
	}

	public Date getVehiDateUpdate() {
		return this.vehiDateUpdate;
	}

	public void setVehiDateUpdate(Date vehiDateUpdate) {
		this.vehiDateUpdate = vehiDateUpdate;
	}

	public String getVehiRegistration() {
		return this.vehiRegistration;
	}

	public void setVehiRegistration(String vehiRegistration) {
		this.vehiRegistration = vehiRegistration;
	}

	public Boolean getVehiStatus() {
		return this.vehiStatus;
	}

	public void setVehiStatus(Boolean vehiStatus) {
		this.vehiStatus = vehiStatus;
	}

	public Modality getModality() {
		return this.modality;
	}

	public void setModality(Modality modality) {
		this.modality = modality;
	}

	public String getVehiOther() {
		return vehiOther;
	}

	public void setVehiOther(String vehiOther) {
		this.vehiOther = vehiOther;
	}

	public Catalog getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(Catalog vehicleType) {
		this.vehicleType = vehicleType;
	}	
}