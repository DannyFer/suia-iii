package ec.gob.ambiente.suia.domain.desechosfisicos;

import java.io.Serializable;
import java.sql.Timestamp;
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
 * The persistent class for the catalogs database table.
 * 
 */
@Entity
@Table(name="catalogs", schema="waste_dangerous")
@NamedQuery(name="Catalog.findAll", query="SELECT c FROM Catalog c")
public class Catalog implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@Column(name="cata_id")
	private Integer cataId;

	@Column(name="cata_date_add")
	private Timestamp cataDateAdd;

	@Column(name="cata_date_update")
	private Timestamp cataDateUpdate;

	@Column(name="cata_description")
	private String cataDescription;

	@Column(name="cata_status")
	private Boolean cataStatus;

	@Column(name="user_id_add")
	private Integer userIdAdd;

	@Column(name="user_id_update")
	private Integer userIdUpdate;

	//bi-directional many-to-one association to Catalog
	@ManyToOne
	@JoinColumn(name="cata_parent_id")
	private Catalog catalog;

	//bi-directional many-to-one association to Catalog
	@OneToMany(mappedBy="catalog")
	private List<Catalog> catalogs;

	//bi-directional many-to-one association to GeneratorRecord
	@OneToMany(mappedBy="documentStatus")
	private List<GeneratorRecord> generatorRecords1;

	//bi-directional many-to-one association to GeneratorRecord
	@OneToMany(mappedBy="type")
	private List<GeneratorRecord> generatorRecords2;

	//bi-directional many-to-one association to GeneratorRecordAnnualStatement
	@OneToMany(mappedBy="year")
	private List<GeneratorRecordAnnualStatement> generatorRecordAnnualStatements1;

	//bi-directional many-to-one association to GeneratorRecordAnnualStatement
	@OneToMany(mappedBy="officeStatus")
	private List<GeneratorRecordAnnualStatement> generatorRecordAnnualStatements2;

	//bi-directional many-to-one association to LicensingProcess
	@OneToMany(mappedBy="anenexesRequest")
	private List<LicensingProcess> licensingProcesses3;

	//bi-directional many-to-one association to ManagerAnnualStatement
	@OneToMany(mappedBy="year")
	private List<ManagerAnnualStatement> managerAnnualStatements1;

	//bi-directional many-to-one association to ManagerAnnualStatement
	@OneToMany(mappedBy="officeStatus")
	private List<ManagerAnnualStatement> managerAnnualStatements2;

	//bi-directional many-to-one association to Modality
	@OneToMany(mappedBy="catalog")
	private List<Modality> modalities;

	//bi-directional many-to-one association to Processed
	@OneToMany(mappedBy="year")
	private List<Processed> processeds1;

	//bi-directional many-to-one association to Processed
	@OneToMany(mappedBy="type")
	private List<Processed> processeds2;
	
	//bi-directional many-to-one association to GeneratorRecordAnnualReport
	@OneToMany(mappedBy="year")
	private List<GeneratorRecordAnnualReport> generatorRecordAnnualReports;
	
	//bi-directional many-to-one association to Processed
	@OneToMany(mappedBy="documentType")
	private List<Processed> processeds3;
	
	//bi-directional many-to-one association to Processed
	@OneToMany(mappedBy="documentStatus")
	private List<MinimizationProgram> minimizationPrograms;
	
	//bi-directional many-to-one association to Processed
	@OneToMany(mappedBy="responsability")
	private List<MinimizationProgram> minimizationPrograms1;
	
	//bi-directional many-to-one association to Processed
	@OneToMany(mappedBy="year")
	private List<MinimizationProgram> minimizationPrograms2;
	
	@OneToMany(mappedBy="vehicleType")
	private List<Vehicle> vehicles;	
	
	@OneToMany(mappedBy="coverageType")
	private List<ModalitiesTransport> modalitiesTransports;
	
	//bi-directional many-to-one association to UpdatesPma
	@OneToMany(mappedBy="catalog")
	private List<UpdatesPma> updatesPmas;
	
	public Catalog() {
	}

	public Integer getCataId() {
		return this.cataId;
	}

	public void setCataId(Integer cataId) {
		this.cataId = cataId;
	}

	public Timestamp getCataDateAdd() {
		return this.cataDateAdd;
	}

	public void setCataDateAdd(Timestamp cataDateAdd) {
		this.cataDateAdd = cataDateAdd;
	}

	public Timestamp getCataDateUpdate() {
		return this.cataDateUpdate;
	}

	public void setCataDateUpdate(Timestamp cataDateUpdate) {
		this.cataDateUpdate = cataDateUpdate;
	}

	public String getCataDescription() {
		return this.cataDescription;
	}

	public void setCataDescription(String cataDescription) {
		this.cataDescription = cataDescription;
	}

	public Boolean getCataStatus() {
		return this.cataStatus;
	}

	public void setCataStatus(Boolean cataStatus) {
		this.cataStatus = cataStatus;
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

	public List<Catalog> getCatalogs() {
		return this.catalogs;
	}

	public void setCatalogs(List<Catalog> catalogs) {
		this.catalogs = catalogs;
	}

	public Catalog addCatalog(Catalog catalog) {
		getCatalogs().add(catalog);
		catalog.setCatalog(this);

		return catalog;
	}

	public Catalog removeCatalog(Catalog catalog) {
		getCatalogs().remove(catalog);
		catalog.setCatalog(null);

		return catalog;
	}

	public List<GeneratorRecord> getGeneratorRecords1() {
		return this.generatorRecords1;
	}

	public void setGeneratorRecords1(List<GeneratorRecord> generatorRecords1) {
		this.generatorRecords1 = generatorRecords1;
	}

	public GeneratorRecord addGeneratorRecords1(GeneratorRecord generatorRecords1) {
		getGeneratorRecords1().add(generatorRecords1);
		generatorRecords1.setDocumentStatus(this);

		return generatorRecords1;
	}

	public GeneratorRecord removeGeneratorRecords1(GeneratorRecord generatorRecords1) {
		getGeneratorRecords1().remove(generatorRecords1);
		generatorRecords1.setDocumentStatus(null);

		return generatorRecords1;
	}

	public List<GeneratorRecord> getGeneratorRecords2() {
		return this.generatorRecords2;
	}

	public void setGeneratorRecords2(List<GeneratorRecord> generatorRecords2) {
		this.generatorRecords2 = generatorRecords2;
	}

	public GeneratorRecord addGeneratorRecords2(GeneratorRecord generatorRecords2) {
		getGeneratorRecords2().add(generatorRecords2);
		generatorRecords2.setType(this);

		return generatorRecords2;
	}

	public GeneratorRecord removeGeneratorRecords2(GeneratorRecord generatorRecords2) {
		getGeneratorRecords2().remove(generatorRecords2);
		generatorRecords2.setType(null);

		return generatorRecords2;
	}

	public List<GeneratorRecordAnnualStatement> getGeneratorRecordAnnualStatements1() {
		return this.generatorRecordAnnualStatements1;
	}

	public void setGeneratorRecordAnnualStatements1(List<GeneratorRecordAnnualStatement> generatorRecordAnnualStatements1) {
		this.generatorRecordAnnualStatements1 = generatorRecordAnnualStatements1;
	}

	public GeneratorRecordAnnualStatement addGeneratorRecordAnnualStatements1(GeneratorRecordAnnualStatement generatorRecordAnnualStatements1) {
		getGeneratorRecordAnnualStatements1().add(generatorRecordAnnualStatements1);
		generatorRecordAnnualStatements1.setYear(this);

		return generatorRecordAnnualStatements1;
	}

	public GeneratorRecordAnnualStatement removeGeneratorRecordAnnualStatements1(GeneratorRecordAnnualStatement generatorRecordAnnualStatements1) {
		getGeneratorRecordAnnualStatements1().remove(generatorRecordAnnualStatements1);
		generatorRecordAnnualStatements1.setYear(null);

		return generatorRecordAnnualStatements1;
	}

	public List<GeneratorRecordAnnualStatement> getGeneratorRecordAnnualStatements2() {
		return this.generatorRecordAnnualStatements2;
	}

	public void setGeneratorRecordAnnualStatements2(List<GeneratorRecordAnnualStatement> generatorRecordAnnualStatements2) {
		this.generatorRecordAnnualStatements2 = generatorRecordAnnualStatements2;
	}

	public GeneratorRecordAnnualStatement addGeneratorRecordAnnualStatements2(GeneratorRecordAnnualStatement generatorRecordAnnualStatements2) {
		getGeneratorRecordAnnualStatements2().add(generatorRecordAnnualStatements2);
		generatorRecordAnnualStatements2.setOfficeStatus(this);

		return generatorRecordAnnualStatements2;
	}

	public GeneratorRecordAnnualStatement removeGeneratorRecordAnnualStatements2(GeneratorRecordAnnualStatement generatorRecordAnnualStatements2) {
		getGeneratorRecordAnnualStatements2().remove(generatorRecordAnnualStatements2);
		generatorRecordAnnualStatements2.setOfficeStatus(null);

		return generatorRecordAnnualStatements2;
	}	

	public List<LicensingProcess> getLicensingProcesses3() {
		return this.licensingProcesses3;
	}

	public void setLicensingProcesses3(List<LicensingProcess> licensingProcesses3) {
		this.licensingProcesses3 = licensingProcesses3;
	}

	public LicensingProcess addLicensingProcesses3(LicensingProcess licensingProcesses3) {
		getLicensingProcesses3().add(licensingProcesses3);
		licensingProcesses3.setAnenexesRequest(this);

		return licensingProcesses3;
	}

	public LicensingProcess removeLicensingProcesses3(LicensingProcess licensingProcesses3) {
		getLicensingProcesses3().remove(licensingProcesses3);
		licensingProcesses3.setAnenexesRequest(null);

		return licensingProcesses3;
	}

	public List<ManagerAnnualStatement> getManagerAnnualStatements1() {
		return this.managerAnnualStatements1;
	}

	public void setManagerAnnualStatements1(List<ManagerAnnualStatement> managerAnnualStatements1) {
		this.managerAnnualStatements1 = managerAnnualStatements1;
	}

	public ManagerAnnualStatement addManagerAnnualStatements1(ManagerAnnualStatement managerAnnualStatements1) {
		getManagerAnnualStatements1().add(managerAnnualStatements1);
		managerAnnualStatements1.setYear(this);

		return managerAnnualStatements1;
	}

	public ManagerAnnualStatement removeManagerAnnualStatements1(ManagerAnnualStatement managerAnnualStatements1) {
		getManagerAnnualStatements1().remove(managerAnnualStatements1);
		managerAnnualStatements1.setYear(null);

		return managerAnnualStatements1;
	}

	public List<ManagerAnnualStatement> getManagerAnnualStatements2() {
		return this.managerAnnualStatements2;
	}

	public void setManagerAnnualStatements2(List<ManagerAnnualStatement> managerAnnualStatements2) {
		this.managerAnnualStatements2 = managerAnnualStatements2;
	}

	public ManagerAnnualStatement addManagerAnnualStatements2(ManagerAnnualStatement managerAnnualStatements2) {
		getManagerAnnualStatements2().add(managerAnnualStatements2);
		managerAnnualStatements2.setOfficeStatus(this);

		return managerAnnualStatements2;
	}

	public ManagerAnnualStatement removeManagerAnnualStatements2(ManagerAnnualStatement managerAnnualStatements2) {
		getManagerAnnualStatements2().remove(managerAnnualStatements2);
		managerAnnualStatements2.setOfficeStatus(null);

		return managerAnnualStatements2;
	}

	public List<Modality> getModalities() {
		return this.modalities;
	}

	public void setModalities(List<Modality> modalities) {
		this.modalities = modalities;
	}

	public Modality addModality(Modality modality) {
		getModalities().add(modality);
		modality.setCatalog(this);

		return modality;
	}

	public Modality removeModality(Modality modality) {
		getModalities().remove(modality);
		modality.setCatalog(null);

		return modality;
	}

	public List<Processed> getProcesseds1() {
		return this.processeds1;
	}

	public void setProcesseds1(List<Processed> processeds1) {
		this.processeds1 = processeds1;
	}

	public Processed addProcesseds1(Processed processeds1) {
		getProcesseds1().add(processeds1);
		processeds1.setYear(this);

		return processeds1;
	}

	public Processed removeProcesseds1(Processed processeds1) {
		getProcesseds1().remove(processeds1);
		processeds1.setYear(null);

		return processeds1;
	}

	public List<Processed> getProcesseds2() {
		return this.processeds2;
	}

	public void setProcesseds2(List<Processed> processeds2) {
		this.processeds2 = processeds2;
	}

	public Processed addProcesseds2(Processed processeds2) {
		getProcesseds2().add(processeds2);
		processeds2.setType(this);

		return processeds2;
	}

	public Processed removeProcesseds2(Processed processeds2) {
		getProcesseds2().remove(processeds2);
		processeds2.setType(null);

		return processeds2;
	}
	
	public List<GeneratorRecordAnnualReport> getGeneratorRecordAnnualReports() {
		return this.generatorRecordAnnualReports;
	}

	public void setGeneratorRecordAnnualReports(List<GeneratorRecordAnnualReport> generatorRecordAnnualReports) {
		this.generatorRecordAnnualReports = generatorRecordAnnualReports;
	}

	public GeneratorRecordAnnualReport addGeneratorRecordAnnualReport(GeneratorRecordAnnualReport generatorRecordAnnualReport) {
		getGeneratorRecordAnnualReports().add(generatorRecordAnnualReport);
		generatorRecordAnnualReport.setYear(this);

		return generatorRecordAnnualReport;
	}

	public GeneratorRecordAnnualReport removeGeneratorRecordAnnualReport(GeneratorRecordAnnualReport generatorRecordAnnualReport) {
		getGeneratorRecordAnnualReports().remove(generatorRecordAnnualReport);
		generatorRecordAnnualReport.setYear(null);

		return generatorRecordAnnualReport;
	}	
	
	public List<Processed> getProcesseds3() {
		return processeds3;
	}

	public void setProcesseds3(List<Processed> processeds3) {
		this.processeds3 = processeds3;
	}

	public Processed addProcesseds3(Processed processeds3) {
		getProcesseds2().add(processeds3);
		processeds3.setType(this);

		return processeds3;
	}

	public Processed removeProcesseds3(Processed processeds3) {
		getProcesseds2().remove(processeds3);
		processeds3.setType(null);

		return processeds3;
	}

	public List<MinimizationProgram> getMinimizationPrograms() {
		return minimizationPrograms;
	}

	public void setMinimizationPrograms(
			List<MinimizationProgram> minimizationPrograms) {
		this.minimizationPrograms = minimizationPrograms;
	}
	
	public MinimizationProgram addMinimizationProgram(MinimizationProgram minimizationProgram) {
		getMinimizationPrograms().add(minimizationProgram);
		minimizationProgram.setDocumentStatus(this);

		return minimizationProgram;
	}

	public MinimizationProgram removeMinimizationProgram(MinimizationProgram minimizationProgram) {
		getMinimizationPrograms().remove(minimizationProgram);
		minimizationProgram.setDocumentStatus(null);

		return minimizationProgram;
	}

	public List<MinimizationProgram> getMinimizationPrograms1() {
		return minimizationPrograms1;
	}

	public void setMinimizationPrograms1(
			List<MinimizationProgram> minimizationPrograms1) {
		this.minimizationPrograms1 = minimizationPrograms1;
	}
	
	public MinimizationProgram addMinimizationProgram1(MinimizationProgram minimizationProgram) {
		getMinimizationPrograms1().add(minimizationProgram);
		minimizationProgram.setDocumentStatus(this);

		return minimizationProgram;
	}

	public MinimizationProgram removeMinimizationProgram1(MinimizationProgram minimizationProgram) {
		getMinimizationPrograms1().remove(minimizationProgram);
		minimizationProgram.setDocumentStatus(null);

		return minimizationProgram;
	}

	public List<MinimizationProgram> getMinimizationPrograms2() {
		return minimizationPrograms2;
	}

	public void setMinimizationPrograms2(
			List<MinimizationProgram> minimizationPrograms2) {
		this.minimizationPrograms2 = minimizationPrograms2;
	}
	
	public MinimizationProgram addMinimizationProgram2(MinimizationProgram minimizationProgram) {
		getMinimizationPrograms2().add(minimizationProgram);
		minimizationProgram.setDocumentStatus(this);

		return minimizationProgram;
	}

	public MinimizationProgram removeMinimizationProgram2(MinimizationProgram minimizationProgram) {
		getMinimizationPrograms2().remove(minimizationProgram);
		minimizationProgram.setDocumentStatus(null);

		return minimizationProgram;
	}

	public List<Vehicle> getVehicles() {
		return vehicles;
	}

	public void setVehicles(List<Vehicle> vehicles) {
		this.vehicles = vehicles;
	}
	
	public Vehicle addVehicle(Vehicle vehicle) {
		getVehicles().add(vehicle);
		vehicle.setVehicleType(this);

		return vehicle;
	}

	public Vehicle removeVehicle(Vehicle vehicle) {
		getVehicles().remove(vehicle);
		vehicle.setVehicleType(null);

		return vehicle;
	}

	public List<ModalitiesTransport> getModalitiesTransports() {
		return modalitiesTransports;
	}

	public void setModalitiesTransports(
			List<ModalitiesTransport> modalitiesTransports) {
		this.modalitiesTransports = modalitiesTransports;
	}
	
	public ModalitiesTransport addModalitiesTransport(ModalitiesTransport modalitiesTransport) {
		getModalitiesTransports().add(modalitiesTransport);
		modalitiesTransport.setCoverageType(this);

		return modalitiesTransport;
	}

	public ModalitiesTransport removeModalitiesTransport(ModalitiesTransport modalitiesTransport) {
		getModalitiesTransports().remove(modalitiesTransport);
		modalitiesTransport.setCoverageType(null);

		return modalitiesTransport;
	}
	
	public List<UpdatesPma> getUpdatesPmas() {
		return this.updatesPmas;
	}

	public void setUpdatesPmas(List<UpdatesPma> updatesPmas) {
		this.updatesPmas = updatesPmas;
	}

	public UpdatesPma addUpdatesPma(UpdatesPma updatesPma) {
		getUpdatesPmas().add(updatesPma);
		updatesPma.setCatalog(this);

		return updatesPma;
	}

	public UpdatesPma removeUpdatesPma(UpdatesPma updatesPma) {
		getUpdatesPmas().remove(updatesPma);
		updatesPma.setCatalog(null);

		return updatesPma;
	}
	
}