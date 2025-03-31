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
 * The persistent class for the generator_record_annual_statement database table.
 * 
 */
@Entity
@Table(name="generator_record_annual_statement", schema="waste_dangerous")
@NamedQuery(name="GeneratorRecordAnnualStatement.findAll", query="SELECT g FROM GeneratorRecordAnnualStatement g")
public class GeneratorRecordAnnualStatement implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@Column(name="gras_id")
	private Integer grasId;

	@Column(name="gras_date_add")
	private Date grasDateAdd;

	@Temporal(TemporalType.DATE)
	@Column(name="gras_date_response")
	private Date grasDateResponse;

	@Column(name="gras_date_update")
	private Date grasDateUpdate;

	@Column(name="gras_number_office_response")
	private String grasNumberOfficeResponse;

	@Column(name="gras_number_technical_report")
	private String grasNumberTechnicalReport;

	@Column(name="gras_observations")
	private String grasObservations;

	@Column(name="gras_status")
	private Boolean grasStatus;

	@Column(name="user_id_add")
	private Integer userIdAdd;

	@Column(name="user_id_update")
	private Integer userIdUpdate;
	
	@Column(name="gras_number_office_admission")
	private String grasNumberOfficeAdmission;
	
	@Column(name="gras_date_admission")
	private Date grasDateAdmission;	

	//bi-directional many-to-one association to GeneratorRecordAnnualReport
	@OneToMany(mappedBy="generatorRecordAnnualStatement")
	private List<GeneratorRecordAnnualReport> generatorRecordAnnualReports;

	//bi-directional many-to-one association to Catalog
	@ManyToOne
	@JoinColumn(name="gras_year")
	private Catalog year;

	//bi-directional many-to-one association to Catalog
	@ManyToOne
	@JoinColumn(name="gras_office_status")
	private Catalog officeStatus;

	//bi-directional many-to-one association to GeneratorRecord
	@ManyToOne
	@JoinColumn(name="gere_id")
	private GeneratorRecord generatorRecord;

	public GeneratorRecordAnnualStatement() {
	}

	public Integer getGrasId() {
		return this.grasId;
	}

	public void setGrasId(Integer grasId) {
		this.grasId = grasId;
	}

	public Date getGrasDateAdd() {
		return this.grasDateAdd;
	}

	public void setGrasDateAdd(Date grasDateAdd) {
		this.grasDateAdd = grasDateAdd;
	}

	public Date getGrasDateResponse() {
		return this.grasDateResponse;
	}

	public void setGrasDateResponse(Date grasDateResponse) {
		this.grasDateResponse = grasDateResponse;
	}

	public Date getGrasDateUpdate() {
		return this.grasDateUpdate;
	}

	public void setGrasDateUpdate(Date grasDateUpdate) {
		this.grasDateUpdate = grasDateUpdate;
	}	

	public String getGrasNumberOfficeResponse() {
		return this.grasNumberOfficeResponse;
	}

	public void setGrasNumberOfficeResponse(String grasNumberOfficeResponse) {
		this.grasNumberOfficeResponse = grasNumberOfficeResponse;
	}

	public String getGrasNumberTechnicalReport() {
		return this.grasNumberTechnicalReport;
	}

	public void setGrasNumberTechnicalReport(String grasNumberTechnicalReport) {
		this.grasNumberTechnicalReport = grasNumberTechnicalReport;
	}

	public String getGrasObservations() {
		return this.grasObservations;
	}

	public void setGrasObservations(String grasObservations) {
		this.grasObservations = grasObservations;
	}

	public Boolean getGrasStatus() {
		return this.grasStatus;
	}

	public void setGrasStatus(Boolean grasStatus) {
		this.grasStatus = grasStatus;
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

	public List<GeneratorRecordAnnualReport> getGeneratorRecordAnnualReports() {
		return this.generatorRecordAnnualReports;
	}

	public void setGeneratorRecordAnnualReports(List<GeneratorRecordAnnualReport> generatorRecordAnnualReports) {
		this.generatorRecordAnnualReports = generatorRecordAnnualReports;
	}

	public GeneratorRecordAnnualReport addGeneratorRecordAnnualReport(GeneratorRecordAnnualReport generatorRecordAnnualReport) {
		getGeneratorRecordAnnualReports().add(generatorRecordAnnualReport);
		generatorRecordAnnualReport.setGeneratorRecordAnnualStatement(this);

		return generatorRecordAnnualReport;
	}

	public GeneratorRecordAnnualReport removeGeneratorRecordAnnualReport(GeneratorRecordAnnualReport generatorRecordAnnualReport) {
		getGeneratorRecordAnnualReports().remove(generatorRecordAnnualReport);
		generatorRecordAnnualReport.setGeneratorRecordAnnualStatement(null);

		return generatorRecordAnnualReport;
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

	public GeneratorRecord getGeneratorRecord() {
		return this.generatorRecord;
	}

	public void setGeneratorRecord(GeneratorRecord generatorRecord) {
		this.generatorRecord = generatorRecord;
	}

	public String getGrasNumberOfficeAdmission() {
		return grasNumberOfficeAdmission;
	}

	public void setGrasNumberOfficeAdmission(String grasNumberOfficeAdmission) {
		this.grasNumberOfficeAdmission = grasNumberOfficeAdmission;
	}

	public Date getGrasDateAdmission() {
		return grasDateAdmission;
	}

	public void setGrasDateAdmission(Date grasDateAdmission) {
		this.grasDateAdmission = grasDateAdmission;
	}	
}