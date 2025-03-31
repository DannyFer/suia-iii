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
 * The persistent class for the manager_annual_report_waste database table.
 * 
 */
@Entity
@Table(name="manager_annual_report_waste", schema="waste_dangerous")
@NamedQuery(name="ManagerAnnualReportWaste.findAll", query="SELECT m FROM ManagerAnnualReportWaste m")
public class ManagerAnnualReportWaste implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="marw_id")
	private Integer marwId;

	@Column(name="marw_date_add")
	private Date marwDateAdd;

	@Column(name="marw_date_update")
	private Date marwDateUpdate;

	@Column(name="marw_quantity")
	private BigDecimal marwQuantity;

	@Column(name="marw_status")
	private Boolean marwStatus;

	@Column(name="user_id_add")
	private Integer userIdAdd;

	@Column(name="user_id_update")
	private Integer userIdUpdate;

	//bi-directional many-to-one association to CatalogsWaste
	@ManyToOne
	@JoinColumn(name="cawa_id")
	private CatalogsWaste catalogsWaste;

	//bi-directional many-to-one association to ManagerAnnualReport
	@ManyToOne
	@JoinColumn(name="maar_id")
	private ManagerAnnualReport managerAnnualReport;

	public ManagerAnnualReportWaste() {
	}

	public Integer getMarwId() {
		return this.marwId;
	}

	public void setMarwId(Integer marwId) {
		this.marwId = marwId;
	}

	public Date getMarwDateAdd() {
		return this.marwDateAdd;
	}

	public void setMarwDateAdd(Date marwDateAdd) {
		this.marwDateAdd = marwDateAdd;
	}

	public Date getMarwDateUpdate() {
		return this.marwDateUpdate;
	}

	public void setMarwDateUpdate(Date marwDateUpdate) {
		this.marwDateUpdate = marwDateUpdate;
	}

	public BigDecimal getMarwQuantity() {
		return this.marwQuantity;
	}

	public void setMarwQuantity(BigDecimal marwQuantity) {
		this.marwQuantity = marwQuantity;
	}

	public Boolean getMarwStatus() {
		return this.marwStatus;
	}

	public void setMarwStatus(Boolean marwStatus) {
		this.marwStatus = marwStatus;
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

	public ManagerAnnualReport getManagerAnnualReport() {
		return this.managerAnnualReport;
	}

	public void setManagerAnnualReport(ManagerAnnualReport managerAnnualReport) {
		this.managerAnnualReport = managerAnnualReport;
	}

}