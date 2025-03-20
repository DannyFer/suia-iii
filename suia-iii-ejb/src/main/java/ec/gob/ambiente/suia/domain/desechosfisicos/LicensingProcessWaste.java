package ec.gob.ambiente.suia.domain.desechosfisicos;

import java.io.Serializable;
import java.math.BigDecimal;
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
 * The persistent class for the licensing_process_waste database table.
 * 
 */
@Entity
@Table(name="licensing_process_waste", schema="waste_dangerous")
@NamedQuery(name="LicensingProcessWaste.findAll", query="SELECT l FROM LicensingProcessWaste l")
public class LicensingProcessWaste implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="lipw_id")
	private Integer lipwId;

	@Column(name="lipw_date_add")
	private Date lipwDateAdd;

	@Column(name="lipw_date_update")
	private Date lipwDateUpdate;

	@Column(name="lipw_quantity")
	private BigDecimal lipwQuantity;

	@Column(name="lipw_status")
	private Boolean lipwStatus;

	@Column(name="user_id_add")
	private Integer userIdAdd;

	@Column(name="user_id_update")
	private Integer userIdUpdate;

	//bi-directional many-to-one association to CatalogsWaste
	@ManyToOne
	@JoinColumn(name="cawa_id")
	private CatalogsWaste catalogsWaste;

	//bi-directional many-to-one association to LicensingProcess
	@ManyToOne
	@JoinColumn(name="lipr_id")	
	private LicensingProcess licensingProcess;

	public LicensingProcessWaste() {
	}

	public Integer getLipwId() {
		return this.lipwId;
	}

	public void setLipwId(Integer lipwId) {
		this.lipwId = lipwId;
	}

	public Date getLipwDateAdd() {
		return this.lipwDateAdd;
	}

	public void setLipwDateAdd(Date lipwDateAdd) {
		this.lipwDateAdd = lipwDateAdd;
	}

	public Date getLipwDateUpdate() {
		return this.lipwDateUpdate;
	}

	public void setLipwDateUpdate(Date lipwDateUpdate) {
		this.lipwDateUpdate = lipwDateUpdate;
	}

	public BigDecimal getLipwQuantity() {
		return this.lipwQuantity;
	}

	public void setLipwQuantity(BigDecimal lipwQuantity) {
		this.lipwQuantity = lipwQuantity;
	}

	public Boolean getLipwStatus() {
		return this.lipwStatus;
	}

	public void setLipwStatus(Boolean lipwStatus) {
		this.lipwStatus = lipwStatus;
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

	public CatalogsWaste getCatalogsWaste() {
		return this.catalogsWaste;
	}

	public void setCatalogsWaste(CatalogsWaste catalogsWaste) {
		this.catalogsWaste = catalogsWaste;
	}

	public LicensingProcess getLicensingProcess() {
		return this.licensingProcess;
	}

	public void setLicensingProcess(LicensingProcess licensingProcess) {
		this.licensingProcess = licensingProcess;
	}

}