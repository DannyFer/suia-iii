package ec.gob.ambiente.suia.domain.desechosfisicos;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;


/**
 * The persistent class for the catalogs_treatments database table.
 * 
 */
@Entity
@Table(name="catalogs_treatments", schema="waste_dangerous")
@NamedQuery(name="CatalogsTreatment.findAll", query="SELECT c FROM CatalogsTreatment c")
public class CatalogsTreatment implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="catr_id")
	private Integer catrId;

	@Column(name="catr_date_add")
	private Date catrDateAdd;

	@Column(name="catr_date_update")
	private Date catrDateUpdate;

	@Column(name="catr_description")
	private String catrDescription;

	@Column(name="catr_key")
	private String catrKey;

	@Column(name="catr_parent_id")
	private Integer catrParentId;

	@Column(name="catr_status")
	private Boolean catrStatus;

	@Column(name="catr_user_id_add")
	private Integer catrUserIdAdd;

	@Column(name="catr_user_id_update")
	private Integer catrUserIdUpdate;

	//bi-directional many-to-one association to LicensingProcess
	@OneToMany(mappedBy="catalogsTreatment")
	private List<LicensingProcess> licensingProcesses;
	
	@Transient
	private boolean isOther;

	public CatalogsTreatment() {
	}

	public Integer getCatrId() {
		return this.catrId;
	}

	public void setCatrId(Integer catrId) {
		this.catrId = catrId;
	}

	public Date getCatrDateAdd() {
		return this.catrDateAdd;
	}

	public void setCatrDateAdd(Date catrDateAdd) {
		this.catrDateAdd = catrDateAdd;
	}

	public Date getCatrDateUpdate() {
		return this.catrDateUpdate;
	}

	public void setCatrDateUpdate(Date catrDateUpdate) {
		this.catrDateUpdate = catrDateUpdate;
	}

	public String getCatrDescription() {
		return this.catrDescription;
	}

	public void setCatrDescription(String catrDescription) {
		this.catrDescription = catrDescription;
	}

	public String getCatrKey() {
		return this.catrKey;
	}

	public void setCatrKey(String catrKey) {
		this.catrKey = catrKey;
	}

	public Integer getCatrParentId() {
		return this.catrParentId;
	}

	public void setCatrParentId(Integer catrParentId) {
		this.catrParentId = catrParentId;
	}

	public Boolean getCatrStatus() {
		return this.catrStatus;
	}

	public void setCatrStatus(Boolean catrStatus) {
		this.catrStatus = catrStatus;
	}

	public Integer getCatrUserIdAdd() {
		return this.catrUserIdAdd;
	}

	public void setCatrUserIdAdd(Integer catrUserIdAdd) {
		this.catrUserIdAdd = catrUserIdAdd;
	}

	public Integer getCatrUserIdUpdate() {
		return this.catrUserIdUpdate;
	}

	public void setCatrUserIdUpdate(Integer catrUserIdUpdate) {
		this.catrUserIdUpdate = catrUserIdUpdate;
	}

	public List<LicensingProcess> getLicensingProcesses() {
		return this.licensingProcesses;
	}

	public void setLicensingProcesses(List<LicensingProcess> licensingProcesses) {
		this.licensingProcesses = licensingProcesses;
	}

	public LicensingProcess addLicensingProcess(LicensingProcess licensingProcess) {
		getLicensingProcesses().add(licensingProcess);
		licensingProcess.setCatalogsTreatment(this);

		return licensingProcess;
	}

	public LicensingProcess removeLicensingProcess(LicensingProcess licensingProcess) {
		getLicensingProcesses().remove(licensingProcess);
		licensingProcess.setCatalogsTreatment(null);

		return licensingProcess;
	}

	public boolean isOther() {
		
		isOther = false;
		
		if(getCatrId() != null)
		{
			if(getCatrId().equals(14) || getCatrId().equals(17) || getCatrId().equals(18) || getCatrId().equals(25) || getCatrId().equals(29) 
			 || getCatrId().equals(40) || getCatrId().equals(79))
			isOther = true;
		}
		
		return isOther;
	}

	public void setOther(boolean isOther) {
		this.isOther = isOther;
	}
	
	@Override
	public String toString() {
		if(getCatrKey() != null)
			return getCatrDescription() + "(CLAVE " + getCatrKey() + ")";
		else
			return getCatrDescription();
	}
}