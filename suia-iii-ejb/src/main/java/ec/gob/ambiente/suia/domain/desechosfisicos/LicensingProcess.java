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
 * The persistent class for the licensing_process database table.
 * 
 */
@Entity
@Table(name="licensing_process", schema="waste_dangerous")
@NamedQuery(name="LicensingProcess.findAll", query="SELECT l FROM LicensingProcess l")
public class LicensingProcess implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@Column(name="lipr_id")
	private Integer liprId;

	@Column(name="lipr_date_add")
	private Date liprDateAdd;

	@Temporal(TemporalType.DATE)
	@Column(name="lipr_date_response")
	private Date liprDateResponse;

	@Temporal(TemporalType.DATE)
	@Column(name="lipr_date_response_optional")
	private Date liprDateResponseOptional;

	@Column(name="lipr_date_update")
	private Date liprDateUpdate;

	@Column(name="lipr_number_office_response")
	private String liprNumberOfficeResponse;

	@Column(name="lipr_number_office_response_optional")
	private String liprNumberOfficeResponseOptional;

	@Column(name="lipr_number_technical_report")
	private String liprNumberTechnicalReport;

	@Column(name="lipr_number_technical_report_optional")
	private String liprNumberTechnicalReportOptional;

	@Column(name="lipr_observations")
	private String liprObservations;

	@Column(name="lipr_status")
	private Boolean liprStatus;
	
	@Column(name="lipr_has_documentation_annexe_bc")
	private Boolean liprHasDocumentationAnnexeBc;
	
	@Column(name="lipr_has_documentation_annexe_b")
	private Boolean liprHasDocumentationAnnexeB;
	
	@Column(name="lipr_has_documentation_annexe_c")
	private Boolean liprHasDocumentationAnnexeC;
	
	@Column(name="lipr_document_wastes")
	private String liprDocumentWastes;	

	@Column(name="user_id_add")
	private Integer userIdAdd;

	@Column(name="user_id_update")
	private Integer userIdUpdate;
	
	//bi-directional many-to-one association to Catalog
	@ManyToOne
	@JoinColumn(name="lipr_office_status")
	private Catalog catalog;
	
	//bi-directional many-to-one association to Catalog
	@ManyToOne
	@JoinColumn(name="lipr_annexes_request")
	private Catalog anenexesRequest;

	//bi-directional many-to-one association to Processed
	@ManyToOne
	@JoinColumn(name="licd_secuencial")
	private LicenciaAmbientalFisicaDetalle licenciaAmbientalFisicaDetalle;

	//bi-directional many-to-one association to Modality
	@OneToMany(mappedBy="licensingProcess")
	private List<Modality> modalities;
	
	//bi-directional many-to-one association to LicensingProcessWaste
	@OneToMany(mappedBy="licensingProcess")
	private List<LicensingProcessWaste> licensingProcessWastes;
	
	//bi-directional many-to-one association to CatalogsTreatment
	@ManyToOne
	@JoinColumn(name="catr_id")
	private CatalogsTreatment catalogsTreatment;
	
	@Column(name="lipr_other_treatment")
	private String liprOtherTreatment;

	public LicensingProcess() {
	}

	public Integer getLiprId() {
		return this.liprId;
	}

	public void setLiprId(Integer liprId) {
		this.liprId = liprId;
	}

	public Date getLiprDateAdd() {
		return this.liprDateAdd;
	}

	public void setLiprDateAdd(Date liprDateAdd) {
		this.liprDateAdd = liprDateAdd;
	}

	public Date getLiprDateResponse() {
		return this.liprDateResponse;
	}

	public void setLiprDateResponse(Date liprDateResponse) {
		this.liprDateResponse = liprDateResponse;
	}

	public Date getLiprDateResponseOptional() {
		return this.liprDateResponseOptional;
	}

	public void setLiprDateResponseOptional(Date liprDateResponseOptional) {
		this.liprDateResponseOptional = liprDateResponseOptional;
	}

	public Date getLiprDateUpdate() {
		return this.liprDateUpdate;
	}

	public void setLiprDateUpdate(Date liprDateUpdate) {
		this.liprDateUpdate = liprDateUpdate;
	}

	public String getLiprNumberOfficeResponse() {
		return this.liprNumberOfficeResponse;
	}

	public void setLiprNumberOfficeResponse(String liprNumberOfficeResponse) {
		this.liprNumberOfficeResponse = liprNumberOfficeResponse;
	}

	public String getLiprNumberOfficeResponseOptional() {
		return this.liprNumberOfficeResponseOptional;
	}

	public void setLiprNumberOfficeResponseOptional(String liprNumberOfficeResponseOptional) {
		this.liprNumberOfficeResponseOptional = liprNumberOfficeResponseOptional;
	}

	public String getLiprNumberTechnicalReport() {
		return this.liprNumberTechnicalReport;
	}

	public void setLiprNumberTechnicalReport(String liprNumberTechnicalReport) {
		this.liprNumberTechnicalReport = liprNumberTechnicalReport;
	}

	public String getLiprNumberTechnicalReportOptional() {
		return this.liprNumberTechnicalReportOptional;
	}

	public void setLiprNumberTechnicalReportOptional(String liprNumberTechnicalReportOptional) {
		this.liprNumberTechnicalReportOptional = liprNumberTechnicalReportOptional;
	}

	public String getLiprObservations() {
		return this.liprObservations;
	}

	public void setLiprObservations(String liprObservations) {
		this.liprObservations = liprObservations;
	}

	public Boolean getLiprStatus() {
		return this.liprStatus;
	}

	public void setLiprStatus(Boolean liprStatus) {
		this.liprStatus = liprStatus;
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
	
	public Catalog getAnenexesRequest() {
		return anenexesRequest;
	}

	public void setAnenexesRequest(Catalog anenexesRequest) {
		this.anenexesRequest = anenexesRequest;
	}
	
	public LicenciaAmbientalFisicaDetalle getLicenciaAmbientalFisicaDetalle() {
		return licenciaAmbientalFisicaDetalle;
	}

	public void setLicenciaAmbientalFisicaDetalle(
			LicenciaAmbientalFisicaDetalle licenciaAmbientalFisicaDetalle) {
		this.licenciaAmbientalFisicaDetalle = licenciaAmbientalFisicaDetalle;
	}

	public List<Modality> getModalities() {
		return this.modalities;
	}

	public void setModalities(List<Modality> modalities) {
		this.modalities = modalities;
	}

	public Modality addModality(Modality modality) {
		getModalities().add(modality);
		modality.setLicensingProcess(this);

		return modality;
	}

	public Modality removeModality(Modality modality) {
		getModalities().remove(modality);
		modality.setLicensingProcess(null);

		return modality;
	}

	public Boolean getLiprHasDocumentationAnnexeBc() {
		return liprHasDocumentationAnnexeBc;
	}

	public void setLiprHasDocumentationAnnexeBc(Boolean liprHasDocumentationAnnexeBc) {
		this.liprHasDocumentationAnnexeBc = liprHasDocumentationAnnexeBc;
	}

	public Boolean getLiprHasDocumentationAnnexeB() {
		return liprHasDocumentationAnnexeB;
	}

	public void setLiprHasDocumentationAnnexeB(Boolean liprHasDocumentationAnnexeB) {
		this.liprHasDocumentationAnnexeB = liprHasDocumentationAnnexeB;
	}

	public Boolean getLiprHasDocumentationAnnexeC() {
		return liprHasDocumentationAnnexeC;
	}

	public void setLiprHasDocumentationAnnexeC(Boolean liprHasDocumentationAnnexeC) {
		this.liprHasDocumentationAnnexeC = liprHasDocumentationAnnexeC;
	}
	
	public List<LicensingProcessWaste> getLicensingProcessWastes() {
		return this.licensingProcessWastes;
	}

	public void setLicensingProcessWastes(List<LicensingProcessWaste> licensingProcessWastes) {
		this.licensingProcessWastes = licensingProcessWastes;
	}

	public LicensingProcessWaste addLicensingProcessWaste(LicensingProcessWaste licensingProcessWaste) {
		getLicensingProcessWastes().add(licensingProcessWaste);
		licensingProcessWaste.setLicensingProcess(this);

		return licensingProcessWaste;
	}

	public LicensingProcessWaste removeLicensingProcessWaste(LicensingProcessWaste licensingProcessWaste) {
		getLicensingProcessWastes().remove(licensingProcessWaste);
		licensingProcessWaste.setLicensingProcess(null);

		return licensingProcessWaste;
	}

	public String getLiprDocumentWastes() {
		return liprDocumentWastes;
	}

	public void setLiprDocumentWastes(String liprDocumentWastes) {
		this.liprDocumentWastes = liprDocumentWastes;
	}
	
	public CatalogsTreatment getCatalogsTreatment() {
		return this.catalogsTreatment;
	}

	public void setCatalogsTreatment(CatalogsTreatment catalogsTreatment) {
		this.catalogsTreatment = catalogsTreatment;
	}

	public String getLiprOtherTreatment() {
		return liprOtherTreatment;
	}

	public void setLiprOtherTreatment(String liprOtherTreatment) {
		this.liprOtherTreatment = liprOtherTreatment;
	}
	
	public Catalog getCatalog() {
		return this.catalog;
	}

	public void setCatalog(Catalog catalog) {
		this.catalog = catalog;
	}
}