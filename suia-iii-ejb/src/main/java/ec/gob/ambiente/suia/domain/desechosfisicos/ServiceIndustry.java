package ec.gob.ambiente.suia.domain.desechosfisicos;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the service_industry database table.
 * 
 */
@Entity
@Table(name="service_industry", schema="waste_dangerous")
@NamedQuery(name="ServiceIndustry.findAll", query="SELECT s FROM ServiceIndustry s")
public class ServiceIndustry implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="sein_id")
	private Integer seinId;

	@Column(name="sein_date_add")
	private Timestamp seinDateAdd;

	@Column(name="sein_date_update")
	private Timestamp seinDateUpdate;

	@Column(name="sein_name")
	private String seinName;

	@Column(name="sein_status")
	private Boolean seinStatus;

	@Column(name="user_id_add")
	private Integer userIdAdd;

	@Column(name="user_id_update")
	private Integer userIdUpdate;

	//bi-directional many-to-one association to Processed
	@OneToMany(mappedBy="serviceIndustry")
	private List<Processed> processeds;

	public ServiceIndustry() {
	}

	public Integer getSeinId() {
		return this.seinId;
	}

	public void setSeinId(Integer seinId) {
		this.seinId = seinId;
	}

	public Timestamp getSeinDateAdd() {
		return this.seinDateAdd;
	}

	public void setSeinDateAdd(Timestamp seinDateAdd) {
		this.seinDateAdd = seinDateAdd;
	}

	public Timestamp getSeinDateUpdate() {
		return this.seinDateUpdate;
	}

	public void setSeinDateUpdate(Timestamp seinDateUpdate) {
		this.seinDateUpdate = seinDateUpdate;
	}

	public String getSeinName() {
		return this.seinName;
	}

	public void setSeinName(String seinName) {
		this.seinName = seinName;
	}

	public Boolean getSeinStatus() {
		return this.seinStatus;
	}

	public void setSeinStatus(Boolean seinStatus) {
		this.seinStatus = seinStatus;
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

	public List<Processed> getProcesseds() {
		return this.processeds;
	}

	public void setProcesseds(List<Processed> processeds) {
		this.processeds = processeds;
	}

	public Processed addProcessed(Processed processed) {
		getProcesseds().add(processed);
		processed.setServiceIndustry(this);

		return processed;
	}

	public Processed removeProcessed(Processed processed) {
		getProcesseds().remove(processed);
		processed.setServiceIndustry(null);

		return processed;
	}

}