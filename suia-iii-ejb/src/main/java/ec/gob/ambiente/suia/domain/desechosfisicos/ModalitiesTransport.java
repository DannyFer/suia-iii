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


/**
 * The persistent class for the modalities_transport database table.
 * 
 */
@Entity
@Table(name="modalities_transport", schema="waste_dangerous")
@NamedQuery(name="ModalitiesTransport.findAll", query="SELECT m FROM ModalitiesTransport m")
public class ModalitiesTransport implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@Column(name="motr_id")
	private Integer motrId;

	@Column(name="motr_date_add")
	private Date motrDateAdd;

	@Column(name="motr_date_update")
	private Date motrDateUpdate;

	@Column(name="motr_description")
	private String motrDescription;

	@Column(name="motr_status")
	private Boolean motrStatus;

	@Column(name="user_id_add")
	private Integer userIdAdd;

	@Column(name="user_id_update")
	private Integer userIdUpdate;

	//bi-directional many-to-one association to Modality
	@ManyToOne
	@JoinColumn(name="moda_id")
	private Modality modality;
	
	//bi-directional many-to-one association to Catalog
	@ManyToOne
	@JoinColumn(name="motr_coverage_type")
	private Catalog coverageType;

	public ModalitiesTransport() {
	}

	public Integer getMotrId() {
		return this.motrId;
	}

	public void setMotrId(Integer motrId) {
		this.motrId = motrId;
	}

	public Date getMotrDateAdd() {
		return this.motrDateAdd;
	}

	public void setMotrDateAdd(Date motrDateAdd) {
		this.motrDateAdd = motrDateAdd;
	}

	public Date getMotrDateUpdate() {
		return this.motrDateUpdate;
	}

	public void setMotrDateUpdate(Date motrDateUpdate) {
		this.motrDateUpdate = motrDateUpdate;
	}

	public String getMotrDescription() {
		return this.motrDescription;
	}

	public void setMotrDescription(String motrDescription) {
		this.motrDescription = motrDescription;
	}

	public Boolean getMotrStatus() {
		return this.motrStatus;
	}

	public void setMotrStatus(Boolean motrStatus) {
		this.motrStatus = motrStatus;
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

	public Modality getModality() {
		return this.modality;
	}

	public void setModality(Modality modality) {
		this.modality = modality;
	}

	public Catalog getCoverageType() {
		return coverageType;
	}

	public void setCoverageType(Catalog coverageType) {
		this.coverageType = coverageType;
	}	
}