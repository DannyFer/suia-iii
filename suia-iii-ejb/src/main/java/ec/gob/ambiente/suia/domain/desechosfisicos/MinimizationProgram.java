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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the minimization_program database table.
 * 
 */
@Entity
@Table(name="minimization_program", schema="waste_dangerous")
@NamedQuery(name="MinimizationProgram.findAll", query="SELECT m FROM MinimizationProgram m")
public class MinimizationProgram implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@Column(name="mipr_id")
	private Integer miprId;

	@Column(name="mipr_date_add")
	private Date miprDateAdd;

	@Temporal(TemporalType.DATE)
	@Column(name="mipr_date_response")
	private Date miprDateResponse;

	@Column(name="mipr_date_update")
	private Date miprDateUpdate;

	@Column(name="mipr_number_office_response")
	private String miprNumberOfficeResponse;

	@Column(name="mipr_number_technical_report")
	private String miprNumberTechnicalReport;

	@Column(name="mipr_observations")
	private String miprObservations;

	@Column(name="mipr_status")
	private Boolean miprStatus;

	@Column(name="user_id_add")
	private Integer userIdAdd;

	@Column(name="user_id_update")
	private Integer userIdUpdate;
	
	@Column(name="mipr_number_office_admission")
	private String miprNumberOfficeAdmission;
	
	@Column(name="mipr_date_admission")
	private Date miprDateAdmission;	

	//bi-directional many-to-one association to GeneratorRecord
	@ManyToOne
	@JoinColumn(name="gere_id")
	private GeneratorRecord generatorRecord;
	
	//bi-directional many-to-one association to GeneratorRecord
	@ManyToOne
	@JoinColumn(name="mipr_document_status")
	private Catalog documentStatus;
	
	//bi-directional many-to-one association to GeneratorRecord
	@ManyToOne
	@JoinColumn(name="mipr_responsability")
	private Catalog responsability;
	
	//bi-directional many-to-one association to GeneratorRecord
	@ManyToOne
	@JoinColumn(name="mipr_year")
	private Catalog year;	
	
	public MinimizationProgram() {
	}

	public Integer getMiprId() {
		return this.miprId;
	}

	public void setMiprId(Integer miprId) {
		this.miprId = miprId;
	}

	public Date getMiprDateAdd() {
		return this.miprDateAdd;
	}

	public void setMiprDateAdd(Date miprDateAdd) {
		this.miprDateAdd = miprDateAdd;
	}

	public Date getMiprDateResponse() {
		return this.miprDateResponse;
	}

	public void setMiprDateResponse(Date miprDateResponse) {
		this.miprDateResponse = miprDateResponse;
	}

	public Date getMiprDateUpdate() {
		return this.miprDateUpdate;
	}

	public void setMiprDateUpdate(Date miprDateUpdate) {
		this.miprDateUpdate = miprDateUpdate;
	}

	public String getMiprNumberOfficeResponse() {
		return this.miprNumberOfficeResponse;
	}

	public void setMiprNumberOfficeResponse(String miprNumberOfficeResponse) {
		this.miprNumberOfficeResponse = miprNumberOfficeResponse;
	}

	public String getMiprNumberTechnicalReport() {
		return this.miprNumberTechnicalReport;
	}

	public void setMiprNumberTechnicalReport(String miprNumberTechnicalReport) {
		this.miprNumberTechnicalReport = miprNumberTechnicalReport;
	}

	public String getMiprObservations() {
		return this.miprObservations;
	}

	public void setMiprObservations(String miprObservations) {
		this.miprObservations = miprObservations;
	}

	public Boolean getMiprStatus() {
		return this.miprStatus;
	}

	public void setMiprStatus(Boolean miprStatus) {
		this.miprStatus = miprStatus;
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

	public GeneratorRecord getGeneratorRecord() {
		return this.generatorRecord;
	}

	public void setGeneratorRecord(GeneratorRecord generatorRecord) {
		this.generatorRecord = generatorRecord;
	}

	public String getMiprNumberOfficeAdmission() {
		return miprNumberOfficeAdmission;
	}

	public void setMiprNumberOfficeAdmission(String miprNumberOfficeAdmission) {
		this.miprNumberOfficeAdmission = miprNumberOfficeAdmission;
	}

	public Date getMiprDateAdmission() {
		return miprDateAdmission;
	}

	public void setMiprDateAdmission(Date miprDateAdmission) {
		this.miprDateAdmission = miprDateAdmission;
	}

	public Catalog getDocumentStatus() {
		return documentStatus;
	}

	public void setDocumentStatus(Catalog documentStatus) {
		this.documentStatus = documentStatus;
	}

	public Catalog getResponsability() {
		return responsability;
	}

	public void setResponsability(Catalog responsability) {
		this.responsability = responsability;
	}

	public Catalog getYear() {
		return year;
	}

	public void setYear(Catalog year) {
		this.year = year;
	}	
}