package ec.gob.ambiente.suia.domain.desechosfisicos;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the licencia_ambiental_fisica_detalle database table.
 * 
 */
@Entity
@Table(name="licencia_ambiental_fisica_detalle", schema="public")
@NamedQuery(name="LicenciaAmbientalFisicaDetalle.findAll", query="SELECT l FROM LicenciaAmbientalFisicaDetalle l")
public class LicenciaAmbientalFisicaDetalle implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@Column(name="licd_secuencial")
	private Integer licdSecuencial;

	@Column(name="licd_create_date")
	private Date licdCreateDate;

	@Column(name="licd_create_user")
	private String licdCreateUser;

	@Column(name="licd_estado")
	private String licdEstado;

	@Column(name="licd_fase_estado")
	private String licdFaseEstado;

	@Column(name="licd_fecha_estado")
	private Date licdFechaEstado;

	@Column(name="licd_numero_oficio")
	private String licdNumeroOficio;

	@Column(name="licd_update_date")
	private Date licdUpdateDate;

	@Column(name="licd_update_user")
	private String licdUpdateUser;

	//bi-directional many-to-one association to LicenciaAmbientalFisica
	@ManyToOne
	@JoinColumn(name="lic_secuencial")
	private LicenciaAmbientalFisica licenciaAmbientalFisica;
	
	//bi-directional many-to-one association to Licensing
	@OneToMany(mappedBy="licenciaAmbientalFisicaDetalle")
	private List<LicensingProcess> licensingProcesses;

	public LicenciaAmbientalFisicaDetalle() {
	}

	public Integer getLicdSecuencial() {
		return this.licdSecuencial;
	}

	public void setLicdSecuencial(Integer licdSecuencial) {
		this.licdSecuencial = licdSecuencial;
	}

	public Date getLicdCreateDate() {
		return this.licdCreateDate;
	}

	public void setLicdCreateDate(Date licdCreateDate) {
		this.licdCreateDate = licdCreateDate;
	}

	public String getLicdCreateUser() {
		return this.licdCreateUser;
	}

	public void setLicdCreateUser(String licdCreateUser) {
		this.licdCreateUser = licdCreateUser;
	}

	public String getLicdEstado() {
		return this.licdEstado;
	}

	public void setLicdEstado(String licdEstado) {
		this.licdEstado = licdEstado;
	}

	public String getLicdFaseEstado() {
		return this.licdFaseEstado;
	}

	public void setLicdFaseEstado(String licdFaseEstado) {
		this.licdFaseEstado = licdFaseEstado;
	}

	public Date getLicdFechaEstado() {
		return this.licdFechaEstado;
	}

	public void setLicdFechaEstado(Date licdFechaEstado) {
		this.licdFechaEstado = licdFechaEstado;
	}

	public String getLicdNumeroOficio() {
		return this.licdNumeroOficio;
	}

	public void setLicdNumeroOficio(String licdNumeroOficio) {
		this.licdNumeroOficio = licdNumeroOficio;
	}

	public Date getLicdUpdateDate() {
		return this.licdUpdateDate;
	}

	public void setLicdUpdateDate(Date licdUpdateDate) {
		this.licdUpdateDate = licdUpdateDate;
	}

	public String getLicdUpdateUser() {
		return this.licdUpdateUser;
	}

	public void setLicdUpdateUser(String licdUpdateUser) {
		this.licdUpdateUser = licdUpdateUser;
	}

	public LicenciaAmbientalFisica getLicenciaAmbientalFisica() {
		return this.licenciaAmbientalFisica;
	}

	public void setLicenciaAmbientalFisica(LicenciaAmbientalFisica licenciaAmbientalFisica) {
		this.licenciaAmbientalFisica = licenciaAmbientalFisica;
	}
	
	public List<LicensingProcess> getLicensingProcesses() {
		return licensingProcesses;
	}

	public void setLicensingProcesses(List<LicensingProcess> licensingProcesses) {
		this.licensingProcesses = licensingProcesses;
	}

	public LicensingProcess addLicensingProcess(LicensingProcess licensingProcess) {
		getLicensingProcesses().add(licensingProcess);
		licensingProcess.setLicenciaAmbientalFisicaDetalle(this);

		return licensingProcess;
	}

	public LicensingProcess removeLicensingProcess(LicensingProcess licensingProcess) {
		getLicensingProcesses().remove(licensingProcess);
		licensingProcess.setLicenciaAmbientalFisicaDetalle(null);

		return licensingProcess;
	}
}