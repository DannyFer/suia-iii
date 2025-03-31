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
 * The persistent class for the modalities database table.
 * 
 */
@Entity
@Table(name="modalities", schema="waste_dangerous")
@NamedQuery(name="Modality.findAll", query="SELECT m FROM Modality m")
public class Modality implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@Column(name="moda_id")
	private Integer modaId;

	@Column(name="moda_date_add")
	private Date modaDateAdd;

	@Column(name="moda_date_update")
	private Date modaDateUpdate;

	@Column(name="moda_status")
	private Boolean modaStatus;

	@Column(name="user_id_add")
	private Integer userIdAdd;

	@Column(name="user_id_update")
	private Integer userIdUpdate;

	//bi-directional many-to-one association to Catalog
	@ManyToOne
	@JoinColumn(name="cata_id")
	private Catalog catalog;

	//bi-directional many-to-one association to Licensing
	@ManyToOne
	@JoinColumn(name="lice_id")
	private Licensing licensing;

	//bi-directional many-to-one association to LicensingProcess
	@ManyToOne
	@JoinColumn(name="lipr_id")
	private LicensingProcess licensingProcess;

	//bi-directional many-to-one association to ManagerAnnualStatement
	@ManyToOne
	@JoinColumn(name="maas_id")
	private ManagerAnnualStatement managerAnnualStatement;
	
	//bi-directional many-to-one association to ModalitiesTransport
	@OneToMany(mappedBy="modality")
	private List<ModalitiesTransport> modalitiesTransports;

	//bi-directional many-to-one association to Vehicle
	@OneToMany(mappedBy="modality")
	private List<Vehicle> vehicles;

	//bi-directional many-to-one association to Licensing
	@ManyToOne
	@JoinColumn(name="uppm_id")
	private UpdatesPma updatesPma;
	
	public Modality() {
	}

	public Integer getModaId() {
		return this.modaId;
	}

	public void setModaId(Integer modaId) {
		this.modaId = modaId;
	}

	public Date getModaDateAdd() {
		return this.modaDateAdd;
	}

	public void setModaDateAdd(Date modaDateAdd) {
		this.modaDateAdd = modaDateAdd;
	}

	public Date getModaDateUpdate() {
		return this.modaDateUpdate;
	}

	public void setModaDateUpdate(Date modaDateUpdate) {
		this.modaDateUpdate = modaDateUpdate;
	}

	public Boolean getModaStatus() {
		return this.modaStatus;
	}

	public void setModaStatus(Boolean modaStatus) {
		this.modaStatus = modaStatus;
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

	public LicensingProcess getLicensingProcess() {
		return this.licensingProcess;
	}

	public void setLicensingProcess(LicensingProcess licensingProcess) {
		this.licensingProcess = licensingProcess;
	}	
	
	public List<ModalitiesTransport> getModalitiesTransports() {
		return this.modalitiesTransports;
	}

	public void setModalitiesTransports(List<ModalitiesTransport> modalitiesTransports) {
		this.modalitiesTransports = modalitiesTransports;
	}

	public ModalitiesTransport addModalitiesTransport(ModalitiesTransport modalitiesTransport) {
		getModalitiesTransports().add(modalitiesTransport);
		modalitiesTransport.setModality(this);

		return modalitiesTransport;
	}

	public ModalitiesTransport removeModalitiesTransport(ModalitiesTransport modalitiesTransport) {
		getModalitiesTransports().remove(modalitiesTransport);
		modalitiesTransport.setModality(null);

		return modalitiesTransport;
	}

	public List<Vehicle> getVehicles() {
		return this.vehicles;
	}

	public void setVehicles(List<Vehicle> vehicles) {
		this.vehicles = vehicles;
	}

	public Vehicle addVehicle(Vehicle vehicle) {
		getVehicles().add(vehicle);
		vehicle.setModality(this);

		return vehicle;
	}

	public Vehicle removeVehicle(Vehicle vehicle) {
		getVehicles().remove(vehicle);
		vehicle.setModality(null);

		return vehicle;
	}

	public UpdatesPma getUpdatesPma() {
		return updatesPma;
	}

	public void setUpdatesPma(UpdatesPma updatesPma) {
		this.updatesPma = updatesPma;
	}

	public ManagerAnnualStatement getManagerAnnualStatement() {
		return managerAnnualStatement;
	}

	public void setManagerAnnualStatement(
			ManagerAnnualStatement managerAnnualStatement) {
		this.managerAnnualStatement = managerAnnualStatement;
	}
}