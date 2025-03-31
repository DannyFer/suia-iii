package ec.gob.ambiente.suia.domain.desechosfisicos;

import java.io.Serializable;
import java.math.BigDecimal;
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
 * The persistent class for the processed database table.
 * 
 */
@Entity
@Table(name="processed", schema="waste_dangerous")
@NamedQuery(name="Processed.findAll", query="SELECT p FROM Processed p")
public class Processed implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@Column(name="proc_id")
	private Integer procId;

	@Column(name="canton_id")
	private Integer cantonId;

	@Column(name="proc_bank_account")
	private String procBankAccount;

	@Column(name="proc_date_add")
	private Date procDateAdd;

	@Temporal(TemporalType.DATE)
	@Column(name="proc_date_admission")
	private Date procDateAdmission;

	@Temporal(TemporalType.DATE)
	@Column(name="proc_date_license")
	private Date procDateLicense;

	@Column(name="proc_date_update")
	private Date procDateUpdate;

	@Column(name="proc_legal_representative")
	private String procLegalRepresentative;

	@Column(name="proc_license_number")
	private String procLicenseNumber;

	@Column(name="proc_project_detail")
	private String procProjectDetail;

	@Column(name="proc_proponent")
	private String procProponent;

	@Column(name="proc_status")
	private Boolean procStatus;

	@Column(name="proc_technical_responsible")
	private String procTechnicalResponsible;

	@Column(name="proc_value")
	private BigDecimal procValue;

	@Column(name="proc_voucher")
	private String procVoucher;
	
	@Column(name="proc_number_processed")
	private String procNumberProcessed;	
	
	@Column(name="proc_document_id")
	private String procDocumentId;
	
	@Column(name="user_id_add")
	private Integer userIdAdd;

	@Column(name="user_id_update")
	private Integer userIdUpdate;

	//bi-directional many-to-one association to GeneratorRecord
	@OneToMany(mappedBy="processed")
	private List<GeneratorRecord> generatorRecords;

	//bi-directional many-to-one association to Catalog
	@ManyToOne
	@JoinColumn(name="proc_year")
	private Catalog year;

	//bi-directional many-to-one association to Catalog
	@ManyToOne
	@JoinColumn(name="proc_type")
	private Catalog type;
	
	//bi-directional many-to-one association to Catalog
	@ManyToOne
	@JoinColumn(name="proc_document_type")
	private Catalog documentType;

	//bi-directional many-to-one association to Ccan
	@ManyToOne
	@JoinColumn(name="ccan_id")
	private Ccan ccan;

	//bi-directional many-to-one association to Installation
	@ManyToOne
	@JoinColumn(name="inst_id")
	private Installation installation;

	//bi-directional many-to-one association to ServiceIndustry
	@ManyToOne
	@JoinColumn(name="sein_id")
	private ServiceIndustry serviceIndustry;

	public Processed() {
	}

	public Integer getProcId() {
		return this.procId;
	}

	public void setProcId(Integer procId) {
		this.procId = procId;
	}

	public Integer getCantonId() {
		return this.cantonId;
	}

	public void setCantonId(Integer cantonId) {
		this.cantonId = cantonId;
	}

	public String getProcBankAccount() {
		return this.procBankAccount;
	}

	public void setProcBankAccount(String procBankAccount) {
		this.procBankAccount = procBankAccount;
	}

	public Date getProcDateAdd() {
		return this.procDateAdd;
	}

	public void setProcDateAdd(Date procDateAdd) {
		this.procDateAdd = procDateAdd;
	}

	public Date getProcDateAdmission() {
		return this.procDateAdmission;
	}

	public void setProcDateAdmission(Date procDateAdmission) {
		this.procDateAdmission = procDateAdmission;
	}

	public Date getProcDateLicense() {
		return this.procDateLicense;
	}

	public void setProcDateLicense(Date procDateLicense) {
		this.procDateLicense = procDateLicense;
	}

	public Date getProcDateUpdate() {
		return this.procDateUpdate;
	}

	public void setProcDateUpdate(Date procDateUpdate) {
		this.procDateUpdate = procDateUpdate;
	}

	public String getProcLegalRepresentative() {
		return this.procLegalRepresentative;
	}

	public void setProcLegalRepresentative(String procLegalRepresentative) {
		this.procLegalRepresentative = procLegalRepresentative.toUpperCase();
	}

	public String getProcLicenseNumber() {
		return this.procLicenseNumber;
	}

	public void setProcLicenseNumber(String procLicenseNumber) {
		this.procLicenseNumber = procLicenseNumber;
	}

	public String getProcProjectDetail() {
		return this.procProjectDetail;
	}

	public void setProcProjectDetail(String procProjectDetail) {
		this.procProjectDetail = procProjectDetail;
	}

	public String getProcProponent() {
		return this.procProponent;
	}

	public void setProcProponent(String procProponent) {
		this.procProponent = procProponent.toUpperCase();
	}

	public Boolean getProcStatus() {
		return this.procStatus;
	}

	public void setProcStatus(Boolean procStatus) {
		this.procStatus = procStatus;
	}

	public String getProcTechnicalResponsible() {
		return this.procTechnicalResponsible;
	}

	public void setProcTechnicalResponsible(String procTechnicalResponsible) {
		this.procTechnicalResponsible = procTechnicalResponsible.toUpperCase();
	}

	public BigDecimal getProcValue() {
		return this.procValue;
	}

	public void setProcValue(BigDecimal procValue) {
		this.procValue = procValue;
	}

	public String getProcVoucher() {
		return this.procVoucher;
	}

	public void setProcVoucher(String procVoucher) {
		this.procVoucher = procVoucher;
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

	public List<GeneratorRecord> getGeneratorRecords() {
		return this.generatorRecords;
	}

	public void setGeneratorRecords(List<GeneratorRecord> generatorRecords) {
		this.generatorRecords = generatorRecords;
	}

	public GeneratorRecord addGeneratorRecord(GeneratorRecord generatorRecord) {
		getGeneratorRecords().add(generatorRecord);
		generatorRecord.setProcessed(this);

		return generatorRecord;
	}

	public GeneratorRecord removeGeneratorRecord(GeneratorRecord generatorRecord) {
		getGeneratorRecords().remove(generatorRecord);
		generatorRecord.setProcessed(null);

		return generatorRecord;
	}

	public Catalog getYear() {
		return year;
	}

	public void setYear(Catalog year) {
		this.year = year;
	}

	public Catalog getType() {
		return type;
	}

	public void setType(Catalog type) {
		this.type = type;
	}

	public Ccan getCcan() {
		return this.ccan;
	}

	public void setCcan(Ccan ccan) {
		this.ccan = ccan;
	}

	public Installation getInstallation() {
		return this.installation;
	}

	public void setInstallation(Installation installation) {
		this.installation = installation;
	}

	public ServiceIndustry getServiceIndustry() {
		return this.serviceIndustry;
	}

	public void setServiceIndustry(ServiceIndustry serviceIndustry) {
		this.serviceIndustry = serviceIndustry;
	}

	public String getProcNumberProcessed() {
		return procNumberProcessed;
	}

	public void setProcNumberProcessed(String procNumberProcessed) {
		this.procNumberProcessed = procNumberProcessed;
	}

	public String getProcDocumentId() {
		return procDocumentId;
	}

	public void setProcDocumentId(String procDocumentId) {
		this.procDocumentId = procDocumentId;
	}

	public Catalog getDocumentType() {
		return documentType;
	}

	public void setDocumentType(Catalog documentType) {
		this.documentType = documentType;
	}
}