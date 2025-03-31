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
 * The persistent class for the updates_pma database table.
 * 
 */
@Entity
@Table(name="updates_pma", schema="waste_dangerous")
@NamedQuery(name="UpdatesPma.findAll", query="SELECT u FROM UpdatesPma u")
public class UpdatesPma implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="uppm_id")
	private Integer uppmId;

	@Column(name="uppm_date_add")
	private Date uppmDateAdd;

	@Temporal(TemporalType.DATE)
	@Column(name="uppm_date_admission")
	private Date uppmDateAdmission;

	@Temporal(TemporalType.DATE)
	@Column(name="uppm_date_response")
	private Date uppmDateResponse;

	@Column(name="uppm_date_update")
	private Date uppmDateUpdate;

	@Column(name="uppm_number_office_admission")
	private String uppmNumberOfficeAdmission;

	@Column(name="uppm_number_office_response")
	private String uppmNumberOfficeResponse;

	@Column(name="uppm_number_technical_report")
	private String uppmNumberTechnicalReport;

	@Column(name="uppm_observations")
	private String uppmObservations;

	@Column(name="uppm_status")
	private Boolean uppmStatus;

	@Column(name="user_id_add")
	private Integer userIdAdd;

	@Column(name="user_id_update")
	private Integer userIdUpdate;
	
	@Column(name="uppm_prerequisites")
	private Boolean uppmPrerequisites;
	
	@Column(name="uppm_document_wastes")
	private String uppmDocumentWastes;

	//bi-directional many-to-one association to Catalog
	@ManyToOne
	@JoinColumn(name="uppm_office_status")
	private Catalog catalog;

	//bi-directional many-to-one association to Licensing
	@ManyToOne
	@JoinColumn(name="lice_id")
	private Licensing licensing;
	
	//bi-directional many-to-one association to Vehicle
	@OneToMany(mappedBy="updatesPma")
	private List<Modality> modalities;
	
	//bi-directional many-to-one association to PmaWaste
	@OneToMany(mappedBy="updatesPma")
	private List<PmaWaste> pmaWastes;

	public UpdatesPma() {
	}

	public Integer getUppmId() {
		return this.uppmId;
	}

	public void setUppmId(Integer uppmId) {
		this.uppmId = uppmId;
	}

	public Date getUppmDateAdd() {
		return this.uppmDateAdd;
	}

	public void setUppmDateAdd(Date uppmDateAdd) {
		this.uppmDateAdd = uppmDateAdd;
	}

	public Date getUppmDateAdmission() {
		return this.uppmDateAdmission;
	}

	public void setUppmDateAdmission(Date uppmDateAdmission) {
		this.uppmDateAdmission = uppmDateAdmission;
	}

	public Date getUppmDateResponse() {
		return this.uppmDateResponse;
	}

	public void setUppmDateResponse(Date uppmDateResponse) {
		this.uppmDateResponse = uppmDateResponse;
	}

	public Date getUppmDateUpdate() {
		return this.uppmDateUpdate;
	}

	public void setUppmDateUpdate(Date uppmDateUpdate) {
		this.uppmDateUpdate = uppmDateUpdate;
	}

	public String getUppmNumberOfficeAdmission() {
		return this.uppmNumberOfficeAdmission;
	}

	public void setUppmNumberOfficeAdmission(String uppmNumberOfficeAdmission) {
		this.uppmNumberOfficeAdmission = uppmNumberOfficeAdmission;
	}

	public String getUppmNumberOfficeResponse() {
		return this.uppmNumberOfficeResponse;
	}

	public void setUppmNumberOfficeResponse(String uppmNumberOfficeResponse) {
		this.uppmNumberOfficeResponse = uppmNumberOfficeResponse;
	}

	public String getUppmNumberTechnicalReport() {
		return this.uppmNumberTechnicalReport;
	}

	public void setUppmNumberTechnicalReport(String uppmNumberTechnicalReport) {
		this.uppmNumberTechnicalReport = uppmNumberTechnicalReport;
	}

	public String getUppmObservations() {
		return this.uppmObservations;
	}

	public void setUppmObservations(String uppmObservations) {
		this.uppmObservations = uppmObservations;
	}

	public Boolean getUppmStatus() {
		return this.uppmStatus;
	}

	public void setUppmStatus(Boolean uppmStatus) {
		this.uppmStatus = uppmStatus;
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

	public Catalog getCatalog() {
		return this.catalog;
	}

	public void setCatalog(Catalog catalog) {
		this.catalog = catalog;
	}

	public Licensing getLicensing() {
		return this.licensing;
	}

	public void setLicensing(Licensing licensing) {
		this.licensing = licensing;
	}

	public Boolean getUppmPrerequisites() {
		return uppmPrerequisites;
	}

	public void setUppmPrerequisites(Boolean uppmPrerequisites) {
		this.uppmPrerequisites = uppmPrerequisites;
	}

	public String getUppmDocumentWastes() {
		return uppmDocumentWastes;
	}

	public void setUppmDocumentWastes(String uppmDocumentWastes) {
		this.uppmDocumentWastes = uppmDocumentWastes;
	}

	public List<Modality> getModalities() {
		return modalities;
	}

	public void setModalities(List<Modality> modalities) {
		this.modalities = modalities;
	}
	
	public List<PmaWaste> getPmaWastes() {
		return this.pmaWastes;
	}

	public void setPmaWastes(List<PmaWaste> pmaWastes) {
		this.pmaWastes = pmaWastes;
	}

	public PmaWaste addPmaWaste(PmaWaste pmaWaste) {
		getPmaWastes().add(pmaWaste);
		pmaWaste.setUpdatesPma(this);

		return pmaWaste;
	}

	public PmaWaste removePmaWaste(PmaWaste pmaWaste) {
		getPmaWastes().remove(pmaWaste);
		pmaWaste.setUpdatesPma(null);

		return pmaWaste;
	}
}