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


/**
 * The persistent class for the manager_annual_report database table.
 * 
 */
@Entity
@Table(name="manager_annual_report", schema="waste_dangerous")
@NamedQuery(name="ManagerAnnualReport.findAll", query="SELECT m FROM ManagerAnnualReport m")
public class ManagerAnnualReport implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@Column(name="maar_id")
	private Integer maarId;

	@Column(name="maar_company")
	private String maarCompany;
	
	@Column(name="maar_document_id")
	private String maarDocumentId;

	@Column(name="maar_date_add")
	private Date maarDateAdd;

	@Column(name="maar_date_update")
	private Date maarDateUpdate;

	@Column(name="maar_status")
	private Boolean maarStatus;

	@Column(name="user_id_add")
	private Integer userIdAdd;

	@Column(name="user_id_update")
	private Integer userIdUpdate;
	
	@Column(name="maar_code_generator_record")
	private String maarCodeGeneratorRecord;	
	
	//bi-directional many-to-one association to Catalog
	@ManyToOne
	@JoinColumn(name="maar_document_type")
	private Catalog documentType;	

	//bi-directional many-to-one association to ManagerAnnualStatement
	@ManyToOne
	@JoinColumn(name="maas_id")
	private ManagerAnnualStatement managerAnnualStatement;
	
	//bi-directional many-to-one association to ManagerAnnualReportWaste
	@OneToMany(mappedBy="managerAnnualReport")
	private List<ManagerAnnualReportWaste> managerAnnualReportWastes;

	public ManagerAnnualReport() {
	}

	public Integer getMaarId() {
		return this.maarId;
	}

	public void setMaarId(Integer maarId) {
		this.maarId = maarId;
	}

	public String getMaarCompany() {
		return this.maarCompany;
	}

	public void setMaarCompany(String maarCompany) {
		this.maarCompany = maarCompany;
	}

	public Date getMaarDateAdd() {
		return this.maarDateAdd;
	}

	public void setMaarDateAdd(Date maarDateAdd) {
		this.maarDateAdd = maarDateAdd;
	}

	public Date getMaarDateUpdate() {
		return this.maarDateUpdate;
	}

	public void setMaarDateUpdate(Date maarDateUpdate) {
		this.maarDateUpdate = maarDateUpdate;
	}

	public Boolean getMaarStatus() {
		return this.maarStatus;
	}

	public void setMaarStatus(Boolean maarStatus) {
		this.maarStatus = maarStatus;
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

	public ManagerAnnualStatement getManagerAnnualStatement() {
		return this.managerAnnualStatement;
	}

	public void setManagerAnnualStatement(ManagerAnnualStatement managerAnnualStatement) {
		this.managerAnnualStatement = managerAnnualStatement;
	}

	public String getMaarCodeGeneratorRecord() {
		return maarCodeGeneratorRecord;
	}

	public void setMaarCodeGeneratorRecord(String maarCodeGeneratorRecord) {
		this.maarCodeGeneratorRecord = maarCodeGeneratorRecord;
	}

	public String getMaarDocumentId() {
		return maarDocumentId;
	}

	public void setMaarDocumentId(String maarDocumentId) {
		this.maarDocumentId = maarDocumentId;
	}

	public Catalog getDocumentType() {
		return documentType;
	}

	public void setDocumentType(Catalog documentType) {
		this.documentType = documentType;
	}
	
	public List<ManagerAnnualReportWaste> getManagerAnnualReportWastes() {
		return this.managerAnnualReportWastes;
	}

	public void setManagerAnnualReportWastes(List<ManagerAnnualReportWaste> managerAnnualReportWastes) {
		this.managerAnnualReportWastes = managerAnnualReportWastes;
	}

	public ManagerAnnualReportWaste addManagerAnnualReportWaste(ManagerAnnualReportWaste managerAnnualReportWaste) {
		getManagerAnnualReportWastes().add(managerAnnualReportWaste);
		managerAnnualReportWaste.setManagerAnnualReport(this);

		return managerAnnualReportWaste;
	}

	public ManagerAnnualReportWaste removeManagerAnnualReportWaste(ManagerAnnualReportWaste managerAnnualReportWaste) {
		getManagerAnnualReportWastes().remove(managerAnnualReportWaste);
		managerAnnualReportWaste.setManagerAnnualReport(null);

		return managerAnnualReportWaste;
	}
}