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
 * The persistent class for the licensing database table.
 * 
 */
@Entity
@Table(name="licensing", schema="waste_dangerous")
@NamedQuery(name="Licensing.findAll", query="SELECT l FROM Licensing l")
public class Licensing implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@Column(name="lice_id")
	private Integer liceId;

	@Column(name="lice_conditions")
	private String liceConditions;

	@Column(name="lice_date_add")
	private Date liceDateAdd;

	@Temporal(TemporalType.DATE)
	@Column(name="lice_date_resolution")
	private Date liceDateResolution;

	@Column(name="lice_date_update")
	private Date liceDateUpdate;

	@Column(name="lice_number")
	private String liceNumber;

	@Column(name="lice_number_office")
	private String liceNumberOffice;

	@Column(name="lice_status")
	private Boolean liceStatus;

	@Column(name="user_id_add")
	private Integer userIdAdd;

	@Column(name="user_id_update")
	private Integer userIdUpdate;
	
	@Column(name="lice_document_coordinates")
	private String liceDocumentCoordinates;
	
	@Column(name="lice_document_wastes")
	private String liceDocumentWastes;

	@Column(name="lice_has_documentation_annexe_b")
	private Boolean liceHasDocumentationAnnexeB;
	  
	@Column(name="lice_has_documentation_annexe_bc")
	private Boolean liceHasDocumentationAnnexeBc;
	
	@Column(name="lice_has_documentation_annexe_c")
	private Boolean liceHasDocumentationAnnexeC;

	//bi-directional many-to-one association to Processed
	@ManyToOne
	@JoinColumn(name="lic_id")
	private LicenciaAmbientalFisica licenciaAmbientalFisica;

	//bi-directional many-to-one association to LicensingWaste
	@OneToMany(mappedBy="licensing")
	private List<LicensingWaste> licensingWastes;

	//bi-directional many-to-one association to ManagerAnnualStatement
	@OneToMany(mappedBy="licensing")
	private List<ManagerAnnualStatement> managerAnnualStatements;

	//bi-directional many-to-one association to Modality
	@OneToMany(mappedBy="licensing")
	private List<Modality> modalities;
	
	//bi-directional many-to-one association to Coordinate
	@OneToMany(mappedBy="licensing")
	private List<Coordinate> coordinates;
	
	//bi-directional many-to-one association to UpdatesPma
	@OneToMany(mappedBy="licensing")
	private List<UpdatesPma> updatesPmas;

	public Licensing() {
	}

	public Integer getLiceId() {
		return this.liceId;
	}

	public void setLiceId(Integer liceId) {
		this.liceId = liceId;
	}

	public String getLiceConditions() {
		return this.liceConditions;
	}

	public void setLiceConditions(String liceConditions) {
		this.liceConditions = liceConditions;
	}

	public Date getLiceDateAdd() {
		return this.liceDateAdd;
	}

	public void setLiceDateAdd(Date liceDateAdd) {
		this.liceDateAdd = liceDateAdd;
	}

	public Date getLiceDateResolution() {
		return this.liceDateResolution;
	}

	public void setLiceDateResolution(Date liceDateResolution) {
		this.liceDateResolution = liceDateResolution;
	}

	public Date getLiceDateUpdate() {
		return this.liceDateUpdate;
	}

	public void setLiceDateUpdate(Date liceDateUpdate) {
		this.liceDateUpdate = liceDateUpdate;
	}

	public String getLiceNumber() {
		return this.liceNumber;
	}

	public void setLiceNumber(String liceNumber) {
		this.liceNumber = liceNumber;
	}

	public String getLiceNumberOffice() {
		return this.liceNumberOffice;
	}

	public void setLiceNumberOffice(String liceNumberOffice) {
		this.liceNumberOffice = liceNumberOffice;
	}

	public Boolean getLiceStatus() {
		return this.liceStatus;
	}

	public void setLiceStatus(Boolean liceStatus) {
		this.liceStatus = liceStatus;
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

	public LicenciaAmbientalFisica getLicenciaAmbientalFisica() {
		return licenciaAmbientalFisica;
	}

	public void setLicenciaAmbientalFisica(
			LicenciaAmbientalFisica licenciaAmbientalFisica) {
		this.licenciaAmbientalFisica = licenciaAmbientalFisica;
	}

	public List<LicensingWaste> getLicensingWastes() {
		return this.licensingWastes;
	}

	public void setLicensingWastes(List<LicensingWaste> licensingWastes) {
		this.licensingWastes = licensingWastes;
	}

	public LicensingWaste addLicensingWaste(LicensingWaste licensingWaste) {
		getLicensingWastes().add(licensingWaste);
		licensingWaste.setLicensing(this);

		return licensingWaste;
	}

	public LicensingWaste removeLicensingWaste(LicensingWaste licensingWaste) {
		getLicensingWastes().remove(licensingWaste);
		licensingWaste.setLicensing(null);

		return licensingWaste;
	}

	public List<ManagerAnnualStatement> getManagerAnnualStatements() {
		return this.managerAnnualStatements;
	}

	public void setManagerAnnualStatements(List<ManagerAnnualStatement> managerAnnualStatements) {
		this.managerAnnualStatements = managerAnnualStatements;
	}

	public ManagerAnnualStatement addManagerAnnualStatement(ManagerAnnualStatement managerAnnualStatement) {
		getManagerAnnualStatements().add(managerAnnualStatement);
		managerAnnualStatement.setLicensing(this);

		return managerAnnualStatement;
	}

	public ManagerAnnualStatement removeManagerAnnualStatement(ManagerAnnualStatement managerAnnualStatement) {
		getManagerAnnualStatements().remove(managerAnnualStatement);
		managerAnnualStatement.setLicensing(null);

		return managerAnnualStatement;
	}

	public List<Modality> getModalities() {
		return this.modalities;
	}

	public void setModalities(List<Modality> modalities) {
		this.modalities = modalities;
	}

	public Modality addModality(Modality modality) {
		getModalities().add(modality);
		modality.setLicensing(this);

		return modality;
	}

	public Modality removeModality(Modality modality) {
		getModalities().remove(modality);
		modality.setLicensing(null);

		return modality;
	}
	
	public List<Coordinate> getCoordinates() {
		return this.coordinates;
	}

	public void setCoordinates(List<Coordinate> coordinates) {
		this.coordinates = coordinates;
	}

	public Coordinate addCoordinate(Coordinate coordinate) {
		getCoordinates().add(coordinate);
		coordinate.setLicensing(this);

		return coordinate;
	}

	public Coordinate removeCoordinate(Coordinate coordinate) {
		getCoordinates().remove(coordinate);
		coordinate.setLicensing(null);

		return coordinate;
	}

	public String getLiceDocumentCoordinates() {
		return liceDocumentCoordinates;
	}

	public void setLiceDocumentCoordinates(String liceDocumentCoordinates) {
		this.liceDocumentCoordinates = liceDocumentCoordinates;
	}

	public String getLiceDocumentWastes() {
		return liceDocumentWastes;
	}

	public void setLiceDocumentWastes(String liceDocumentWastes) {
		this.liceDocumentWastes = liceDocumentWastes;
	}
	
	public List<UpdatesPma> getUpdatesPmas() {
		return this.updatesPmas;
	}

	public void setUpdatesPmas(List<UpdatesPma> updatesPmas) {
		this.updatesPmas = updatesPmas;
	}

	public UpdatesPma addUpdatesPma(UpdatesPma updatesPma) {
		getUpdatesPmas().add(updatesPma);
		updatesPma.setLicensing(this);

		return updatesPma;
	}

	public UpdatesPma removeUpdatesPma(UpdatesPma updatesPma) {
		getUpdatesPmas().remove(updatesPma);
		updatesPma.setLicensing(null);

		return updatesPma;
	}

	public Boolean getLiceHasDocumentationAnnexeB() {
		return liceHasDocumentationAnnexeB;
	}

	public void setLiceHasDocumentationAnnexeB(Boolean liceHasDocumentationAnnexeB) {
		this.liceHasDocumentationAnnexeB = liceHasDocumentationAnnexeB;
	}

	public Boolean getLiceHasDocumentationAnnexeBc() {
		return liceHasDocumentationAnnexeBc;
	}

	public void setLiceHasDocumentationAnnexeBc(Boolean liceHasDocumentationAnnexeBc) {
		this.liceHasDocumentationAnnexeBc = liceHasDocumentationAnnexeBc;
	}

	public Boolean getLiceHasDocumentationAnnexeC() {
		return liceHasDocumentationAnnexeC;
	}

	public void setLiceHasDocumentationAnnexeC(Boolean liceHasDocumentationAnnexeC) {
		this.liceHasDocumentationAnnexeC = liceHasDocumentationAnnexeC;
	}	
}