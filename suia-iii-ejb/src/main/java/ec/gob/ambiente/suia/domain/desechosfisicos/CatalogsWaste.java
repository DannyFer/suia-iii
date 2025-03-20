package ec.gob.ambiente.suia.domain.desechosfisicos;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * The persistent class for the catalogs_waste database table.
 * 
 */
@Entity
@Table(name="catalogs_waste", schema="waste_dangerous")
@NamedQuery(name="CatalogsWaste.findAll", query="SELECT c FROM CatalogsWaste c")
public class CatalogsWaste implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@Column(name="cawa_id")
	private Integer cawaId;

	@Column(name="cawa_date_add")
	private Timestamp cawaDateAdd;

	@Column(name="cawa_date_update")
	private Timestamp cawaDateUpdate;

	@Column(name="cawa_key")
	private String cawaKey;

	@Column(name="cawa_name")
	private String cawaName;

	@Column(name="cawa_status")
	private Boolean cawaStatus;

	@Column(name="user_id_add")
	private Integer userIdAdd;

	@Column(name="user_id_update")
	private Integer userIdUpdate;

	//bi-directional many-to-one association to GeneratorRecordAnnualReport
	@OneToMany(mappedBy="catalogsWaste")
	private List<GeneratorRecordAnnualReport> generatorRecordAnnualReports;

	//bi-directional many-to-one association to GeneratorRecordWaste
	@OneToMany(mappedBy="catalogsWaste")
	private List<GeneratorRecordWaste> generatorRecordWastes;

	//bi-directional many-to-one association to LicensingWaste
	@OneToMany(mappedBy="catalogsWaste")
	private List<LicensingWaste> licensingWastes;
	
	//bi-directional many-to-one association to PmaWaste
	@OneToMany(mappedBy="catalogsWaste")
	private List<PmaWaste> pmaWastes;
	
	//bi-directional many-to-one association to ManagerAnnualReportWaste
	@OneToMany(mappedBy="catalogsWaste")
	private List<ManagerAnnualReportWaste> managerAnnualReportWastes;
	
	//bi-directional many-to-one association to LicensingProcessWaste
	@OneToMany(mappedBy="catalogsWaste")
	private List<LicensingProcessWaste> licensingProcessWastes;

	public CatalogsWaste() {
	}

	public Integer getCawaId() {
		return this.cawaId;
	}

	public void setCawaId(Integer cawaId) {
		this.cawaId = cawaId;
	}

	public Timestamp getCawaDateAdd() {
		return this.cawaDateAdd;
	}

	public void setCawaDateAdd(Timestamp cawaDateAdd) {
		this.cawaDateAdd = cawaDateAdd;
	}

	public Timestamp getCawaDateUpdate() {
		return this.cawaDateUpdate;
	}

	public void setCawaDateUpdate(Timestamp cawaDateUpdate) {
		this.cawaDateUpdate = cawaDateUpdate;
	}

	public String getCawaKey() {
		return this.cawaKey;
	}

	public void setCawaKey(String cawaKey) {
		this.cawaKey = cawaKey;
	}

	public String getCawaName() {
		return this.cawaName;
	}

	public void setCawaName(String cawaName) {
		this.cawaName = cawaName;
	}

	public Boolean getCawaStatus() {
		return this.cawaStatus;
	}

	public void setCawaStatus(Boolean cawaStatus) {
		this.cawaStatus = cawaStatus;
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
		generatorRecordAnnualReport.setCatalogsWaste(this);

		return generatorRecordAnnualReport;
	}

	public GeneratorRecordAnnualReport removeGeneratorRecordAnnualReport(GeneratorRecordAnnualReport generatorRecordAnnualReport) {
		getGeneratorRecordAnnualReports().remove(generatorRecordAnnualReport);
		generatorRecordAnnualReport.setCatalogsWaste(null);

		return generatorRecordAnnualReport;
	}

	public List<GeneratorRecordWaste> getGeneratorRecordWastes() {
		return this.generatorRecordWastes;
	}

	public void setGeneratorRecordWastes(List<GeneratorRecordWaste> generatorRecordWastes) {
		this.generatorRecordWastes = generatorRecordWastes;
	}

	public GeneratorRecordWaste addGeneratorRecordWaste(GeneratorRecordWaste generatorRecordWaste) {
		getGeneratorRecordWastes().add(generatorRecordWaste);
		generatorRecordWaste.setCatalogsWaste(this);

		return generatorRecordWaste;
	}

	public GeneratorRecordWaste removeGeneratorRecordWaste(GeneratorRecordWaste generatorRecordWaste) {
		getGeneratorRecordWastes().remove(generatorRecordWaste);
		generatorRecordWaste.setCatalogsWaste(null);

		return generatorRecordWaste;
	}

	public List<LicensingWaste> getLicensingWastes() {
		return this.licensingWastes;
	}

	public void setLicensingWastes(List<LicensingWaste> licensingWastes) {
		this.licensingWastes = licensingWastes;
	}

	public LicensingWaste addLicensingWaste(LicensingWaste licensingWaste) {
		getLicensingWastes().add(licensingWaste);
		licensingWaste.setCatalogsWaste(this);

		return licensingWaste;
	}

	public LicensingWaste removeLicensingWaste(LicensingWaste licensingWaste) {
		getLicensingWastes().remove(licensingWaste);
		licensingWaste.setCatalogsWaste(null);

		return licensingWaste;
	}	

	public List<PmaWaste> getPmaWastes() {
		return this.pmaWastes;
	}

	public void setPmaWastes(List<PmaWaste> pmaWastes) {
		this.pmaWastes = pmaWastes;
	}

	public PmaWaste addPmaWaste(PmaWaste pmaWaste) {
		getPmaWastes().add(pmaWaste);
		pmaWaste.setCatalogsWaste(this);

		return pmaWaste;
	}

	public PmaWaste removePmaWaste(PmaWaste pmaWaste) {
		getPmaWastes().remove(pmaWaste);
		pmaWaste.setCatalogsWaste(null);

		return pmaWaste;
	}
	
	public List<ManagerAnnualReportWaste> getManagerAnnualReportWastes() {
		return this.managerAnnualReportWastes;
	}

	public void setManagerAnnualReportWastes(List<ManagerAnnualReportWaste> managerAnnualReportWastes) {
		this.managerAnnualReportWastes = managerAnnualReportWastes;
	}

	public ManagerAnnualReportWaste addManagerAnnualReportWaste(ManagerAnnualReportWaste managerAnnualReportWaste) {
		getManagerAnnualReportWastes().add(managerAnnualReportWaste);
		managerAnnualReportWaste.setCatalogsWaste(this);

		return managerAnnualReportWaste;
	}

	public ManagerAnnualReportWaste removeManagerAnnualReportWaste(ManagerAnnualReportWaste managerAnnualReportWaste) {
		getManagerAnnualReportWastes().remove(managerAnnualReportWaste);
		managerAnnualReportWaste.setCatalogsWaste(null);

		return managerAnnualReportWaste;
	}
	
	public List<LicensingProcessWaste> getLicensingProcessWastes() {
		return this.licensingProcessWastes;
	}

	public void setLicensingProcessWastes(List<LicensingProcessWaste> licensingProcessWastes) {
		this.licensingProcessWastes = licensingProcessWastes;
	}

	public LicensingProcessWaste addLicensingProcessWaste(LicensingProcessWaste licensingProcessWaste) {
		getLicensingProcessWastes().add(licensingProcessWaste);
		licensingProcessWaste.setCatalogsWaste(this);

		return licensingProcessWaste;
	}

	public LicensingProcessWaste removeLicensingProcessWaste(LicensingProcessWaste licensingProcessWaste) {
		getLicensingProcessWastes().remove(licensingProcessWaste);
		licensingProcessWaste.setCatalogsWaste(null);

		return licensingProcessWaste;
	}
}