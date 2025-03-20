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
 * The persistent class for the generator_record database table.
 * 
 */
@Entity
@Table(name="generator_record", schema="waste_dangerous")
@NamedQuery(name="GeneratorRecord.findAll", query="SELECT g FROM GeneratorRecord g")
public class GeneratorRecord implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@Column(name="gere_id")
	private Integer gereId;

	@Column(name="gere_annex")
	private String gereAnnex;

	@Column(name="gere_code")
	private String gereCode;

	@Column(name="gere_code_annual_statement")
	private String gereCodeAnnualStatement;

	@Column(name="gere_code_single_manifest")
	private String gereCodeSingleManifest;

	@Temporal(TemporalType.DATE)
	@Column(name="gere_date_actualization")
	private Date gereDateActualization;

	@Column(name="gere_date_add")
	private Date gereDateAdd;

	@Temporal(TemporalType.DATE)
	@Column(name="gere_date_response")
	private Date gereDateResponse;

	@Column(name="gere_date_update")
	private Date gereDateUpdate;

	@Column(name="gere_number_office_response")
	private String gereNumberOfficeResponse;

	@Column(name="gere_number_technical_report")
	private String gereNumberTechnicalReport;

	@Column(name="gere_observations")
	private String gereObservations;

	@Column(name="gere_status")
	private Boolean gereStatus;

	@Column(name="gere_waste")
	private String gereWaste;

	@Column(name="user_id_add")
	private Integer userIdAdd;

	@Column(name="user_id_update")
	private Integer userIdUpdate;
	
	@Column(name="gere_office_admission_number")
	private String gereOfficeAdmissionNumber;
	
	@Temporal(TemporalType.DATE)
	@Column(name="gere_date_admission_procedure")
	private Date gereDateAdmissionProcedure;
	
	//bi-directional many-to-one association to Catalog
	@ManyToOne
	@JoinColumn(name="gere_document_status")
	private Catalog documentStatus;

	//bi-directional many-to-one association to Catalog
	@ManyToOne
	@JoinColumn(name="gere_type")
	private Catalog type;

	//bi-directional many-to-one association to Processed
	@ManyToOne
	@JoinColumn(name="proc_id")
	private Processed processed;
	
	//bi-directional many-to-one association to Processed
	@ManyToOne
	@JoinColumn(name="gere_id_parent")
	private GeneratorRecord generatorParent;

	//bi-directional many-to-one association to GeneratorRecordAnnualStatement
	@OneToMany(mappedBy="generatorRecord")
	private List<GeneratorRecordAnnualStatement> generatorRecordAnnualStatements;

	//bi-directional many-to-one association to GeneratorRecordWaste
	@OneToMany(mappedBy="generatorRecord")
	private List<GeneratorRecordWaste> generatorRecordWastes;

	//bi-directional many-to-one association to MinimizationProgram
	@OneToMany(mappedBy="generatorRecord")
	private List<MinimizationProgram> minimizationPrograms;
	
	//bi-directional many-to-one association to Catalog
	@OneToMany(mappedBy="generatorParent")
	private List<GeneratorRecord> generatorsRecord;

	public GeneratorRecord() {
	}

	public Integer getGereId() {
		return this.gereId;
	}

	public void setGereId(Integer gereId) {
		this.gereId = gereId;
	}

	public String getGereAnnex() {
		return this.gereAnnex;
	}

	public void setGereAnnex(String gereAnnex) {
		this.gereAnnex = gereAnnex;
	}

	public String getGereCode() {
		return this.gereCode;
	}

	public void setGereCode(String gereCode) {
		this.gereCode = gereCode;
	}

	public String getGereCodeAnnualStatement() {
		return this.gereCodeAnnualStatement;
	}

	public void setGereCodeAnnualStatement(String gereCodeAnnualStatement) {
		this.gereCodeAnnualStatement = gereCodeAnnualStatement;
	}

	public String getGereCodeSingleManifest() {
		return this.gereCodeSingleManifest;
	}

	public void setGereCodeSingleManifest(String gereCodeSingleManifest) {
		this.gereCodeSingleManifest = gereCodeSingleManifest;
	}

	public Date getGereDateActualization() {
		return this.gereDateActualization;
	}

	public void setGereDateActualization(Date gereDateActualization) {
		this.gereDateActualization = gereDateActualization;
	}

	public Date getGereDateAdd() {
		return this.gereDateAdd;
	}

	public void setGereDateAdd(Date gereDateAdd) {
		this.gereDateAdd = gereDateAdd;
	}

	public Date getGereDateResponse() {
		return this.gereDateResponse;
	}

	public void setGereDateResponse(Date gereDateResponse) {
		this.gereDateResponse = gereDateResponse;
	}

	public Date getGereDateUpdate() {
		return this.gereDateUpdate;
	}

	public void setGereDateUpdate(Date gereDateUpdate) {
		this.gereDateUpdate = gereDateUpdate;
	}

	public String getGereNumberOfficeResponse() {
		return this.gereNumberOfficeResponse;
	}

	public void setGereNumberOfficeResponse(String gereNumberOfficeResponse) {
		this.gereNumberOfficeResponse = gereNumberOfficeResponse;
	}

	public String getGereNumberTechnicalReport() {
		return this.gereNumberTechnicalReport;
	}

	public void setGereNumberTechnicalReport(String gereNumberTechnicalReport) {
		this.gereNumberTechnicalReport = gereNumberTechnicalReport;
	}

	public String getGereObservations() {
		return this.gereObservations;
	}

	public void setGereObservations(String gereObservations) {
		this.gereObservations = gereObservations;
	}

	public Boolean getGereStatus() {
		return this.gereStatus;
	}

	public void setGereStatus(Boolean gereStatus) {
		this.gereStatus = gereStatus;
	}

	public String getGereWaste() {
		return this.gereWaste;
	}

	public void setGereWaste(String gereWaste) {
		this.gereWaste = gereWaste;
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
	
	public Catalog getDocumentStatus() {
		return documentStatus;
	}

	public void setDocumentStatus(Catalog documentStatus) {
		this.documentStatus = documentStatus;
	}

	public Catalog getType() {
		return type;
	}

	public void setType(Catalog type) {
		this.type = type;
	}

	public Processed getProcessed() {
		return this.processed;
	}

	public void setProcessed(Processed processed) {
		this.processed = processed;
	}

	public List<GeneratorRecordAnnualStatement> getGeneratorRecordAnnualStatements() {
		return this.generatorRecordAnnualStatements;
	}

	public void setGeneratorRecordAnnualStatements(List<GeneratorRecordAnnualStatement> generatorRecordAnnualStatements) {
		this.generatorRecordAnnualStatements = generatorRecordAnnualStatements;
	}

	public GeneratorRecordAnnualStatement addGeneratorRecordAnnualStatement(GeneratorRecordAnnualStatement generatorRecordAnnualStatement) {
		getGeneratorRecordAnnualStatements().add(generatorRecordAnnualStatement);
		generatorRecordAnnualStatement.setGeneratorRecord(this);

		return generatorRecordAnnualStatement;
	}

	public GeneratorRecordAnnualStatement removeGeneratorRecordAnnualStatement(GeneratorRecordAnnualStatement generatorRecordAnnualStatement) {
		getGeneratorRecordAnnualStatements().remove(generatorRecordAnnualStatement);
		generatorRecordAnnualStatement.setGeneratorRecord(null);

		return generatorRecordAnnualStatement;
	}

	public List<GeneratorRecordWaste> getGeneratorRecordWastes() {
		return this.generatorRecordWastes;
	}

	public void setGeneratorRecordWastes(List<GeneratorRecordWaste> generatorRecordWastes) {
		this.generatorRecordWastes = generatorRecordWastes;
	}

	public GeneratorRecordWaste addGeneratorRecordWaste(GeneratorRecordWaste generatorRecordWaste) {
		getGeneratorRecordWastes().add(generatorRecordWaste);
		generatorRecordWaste.setGeneratorRecord(this);

		return generatorRecordWaste;
	}

	public GeneratorRecordWaste removeGeneratorRecordWaste(GeneratorRecordWaste generatorRecordWaste) {
		getGeneratorRecordWastes().remove(generatorRecordWaste);
		generatorRecordWaste.setGeneratorRecord(null);

		return generatorRecordWaste;
	}

	public List<MinimizationProgram> getMinimizationPrograms() {
		return this.minimizationPrograms;
	}

	public void setMinimizationPrograms(List<MinimizationProgram> minimizationPrograms) {
		this.minimizationPrograms = minimizationPrograms;
	}

	public MinimizationProgram addMinimizationProgram(MinimizationProgram minimizationProgram) {
		getMinimizationPrograms().add(minimizationProgram);
		minimizationProgram.setGeneratorRecord(this);

		return minimizationProgram;
	}

	public MinimizationProgram removeMinimizationProgram(MinimizationProgram minimizationProgram) {
		getMinimizationPrograms().remove(minimizationProgram);
		minimizationProgram.setGeneratorRecord(null);

		return minimizationProgram;
	}

	public String getGereOfficeAdmissionNumber() {
		return gereOfficeAdmissionNumber;
	}

	public void setGereOfficeAdmissionNumber(String gereOfficeAdmissionNumber) {
		this.gereOfficeAdmissionNumber = gereOfficeAdmissionNumber;
	}

	public Date getGereDateAdmissionProcedure() {
		return gereDateAdmissionProcedure;
	}

	public void setGereDateAdmissionProcedure(Date gereDateAdmissionProcedure) {
		this.gereDateAdmissionProcedure = gereDateAdmissionProcedure;
	}

	public GeneratorRecord getGeneratorParent() {
		return generatorParent;
	}

	public void setGeneratorParent(GeneratorRecord generatorParent) {
		this.generatorParent = generatorParent;
	}

	public List<GeneratorRecord> getGeneratorsRecord() {
		return generatorsRecord;
	}

	public void setGeneratorsRecord(List<GeneratorRecord> generatorsRecord) {
		this.generatorsRecord = generatorsRecord;
	}
	
	public GeneratorRecord addGeneratorRecord(GeneratorRecord generatorRecord) {
		getGeneratorsRecord().add(generatorRecord);
		generatorRecord.setGeneratorParent(generatorRecord);
		return generatorRecord;
	}

	public GeneratorRecord removeCatalog(GeneratorRecord generatorRecord) {
		getGeneratorsRecord().remove(generatorRecord);
		generatorRecord.setGeneratorParent(null);

		return generatorRecord;
	}
	
}