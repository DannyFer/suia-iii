package ec.gob.ambiente.suia.domain.desechosfisicos;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the manager_annual_statement database table.
 * 
 */
@Entity
@Table(name="manager_annual_statement", schema="waste_dangerous")
@NamedQuery(name="ManagerAnnualStatement.findAll", query="SELECT m FROM ManagerAnnualStatement m")
public class ManagerAnnualStatement implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="maas_id")
	private Integer maasId;

	@Column(name="maas_date_add")
	private Date maasDateAdd;

	@Temporal(TemporalType.DATE)
	@Column(name="maas_date_response")
	private Date maasDateResponse;

	@Column(name="maas_date_update")
	private Date maasDateUpdate;

	@Column(name="maas_number_office_response")
	private String maasNumberOfficeResponse;

	@Column(name="maas_number_technical_report")
	private String maasNumberTechnicalReport;

	@Column(name="maas_observations")
	private String maasObservations;

	@Column(name="maas_status")
	private Boolean maasStatus;

	@Column(name="user_id_add")
	private Integer userIdAdd;

	@Column(name="user_id_update")
	private Integer userIdUpdate;
	
	@Column(name="maas_number_office_admission")
	private String maasNumberOfficeAdmission;

	@Column(name="maas_date_admission")
	private Date maasDateAdmission;

	//bi-directional many-to-one association to ManagerAnnualReport
	@OneToMany(mappedBy="managerAnnualStatement")
	private List<ManagerAnnualReport> managerAnnualReports;

	//bi-directional many-to-one association to Catalog
	@ManyToOne
	@JoinColumn(name="maas_year")
	private Catalog year;

	//bi-directional many-to-one association to Catalog
	@ManyToOne
	@JoinColumn(name="maas_office_status")
	private Catalog officeStatus;

	//bi-directional many-to-one association to Licensing
	@ManyToOne
	@JoinColumn(name="lice_id")
	private Licensing licensing;
	
	//bi-directional many-to-one association to Modality
	@OneToMany(mappedBy="managerAnnualStatement")
	private List<Modality> modalities;

	public ManagerAnnualStatement() {
	}

	public Integer getMaasId() {
		return this.maasId;
	}

	public void setMaasId(Integer maasId) {
		this.maasId = maasId;
	}

	public Date getMaasDateAdd() {
		return this.maasDateAdd;
	}

	public void setMaasDateAdd(Date maasDateAdd) {
		this.maasDateAdd = maasDateAdd;
	}

	public Date getMaasDateResponse() {
		return this.maasDateResponse;
	}

	public void setMaasDateResponse(Date maasDateResponse) {
		this.maasDateResponse = maasDateResponse;
	}

	public Date getMaasDateUpdate() {
		return this.maasDateUpdate;
	}

	public void setMaasDateUpdate(Date maasDateUpdate) {
		this.maasDateUpdate = maasDateUpdate;
	}

	public String getMaasNumberOfficeResponse() {
		return this.maasNumberOfficeResponse;
	}

	public void setMaasNumberOfficeResponse(String maasNumberOfficeResponse) {
		this.maasNumberOfficeResponse = maasNumberOfficeResponse;
	}

	public String getMaasNumberTechnicalReport() {
		return this.maasNumberTechnicalReport;
	}

	public void setMaasNumberTechnicalReport(String maasNumberTechnicalReport) {
		this.maasNumberTechnicalReport = maasNumberTechnicalReport;
	}

	public String getMaasObservations() {
		return this.maasObservations;
	}

	public void setMaasObservations(String maasObservations) {
		this.maasObservations = maasObservations;
	}

	public Boolean getMaasStatus() {
		return this.maasStatus;
	}

	public void setMaasStatus(Boolean maasStatus) {
		this.maasStatus = maasStatus;
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

	public List<ManagerAnnualReport> getManagerAnnualReports() {
		return this.managerAnnualReports;
	}

	public void setManagerAnnualReports(List<ManagerAnnualReport> managerAnnualReports) {
		this.managerAnnualReports = managerAnnualReports;
	}

	public ManagerAnnualReport addManagerAnnualReport(ManagerAnnualReport managerAnnualReport) {
		getManagerAnnualReports().add(managerAnnualReport);
		managerAnnualReport.setManagerAnnualStatement(this);

		return managerAnnualReport;
	}

	public ManagerAnnualReport removeManagerAnnualReport(ManagerAnnualReport managerAnnualReport) {
		getManagerAnnualReports().remove(managerAnnualReport);
		managerAnnualReport.setManagerAnnualStatement(null);

		return managerAnnualReport;
	}

	public Catalog getYear() {
		return year;
	}

	public void setYear(Catalog year) {
		this.year = year;
	}

	public Catalog getOfficeStatus() {
		return officeStatus;
	}

	public void setOfficeStatus(Catalog officeStatus) {
		this.officeStatus = officeStatus;
	}

	public Licensing getLicensing() {
		return this.licensing;
	}

	public void setLicensing(Licensing licensing) {
		this.licensing = licensing;
	}

	public String getMaasNumberOfficeAdmission() {
		return maasNumberOfficeAdmission;
	}

	public void setMaasNumberOfficeAdmission(String maasNumberOfficeAdmission) {
		this.maasNumberOfficeAdmission = maasNumberOfficeAdmission;
	}

	public Date getMaasDateAdmission() {
		return maasDateAdmission;
	}

	public void setMaasDateAdmission(Date maasDateAdmission) {
		this.maasDateAdmission = maasDateAdmission;
	}
	
	public List<Modality> getModalities() {
		return this.modalities;
	}

	public void setModalities(List<Modality> modalities) {
		this.modalities = modalities;
	}

	public Modality addModality(Modality modality) {
		getModalities().add(modality);
		modality.setManagerAnnualStatement(this);

		return modality;
	}

	public Modality removeModality(Modality modality) {
		getModalities().remove(modality);
		modality.setManagerAnnualStatement(null);

		return modality;
	}
}