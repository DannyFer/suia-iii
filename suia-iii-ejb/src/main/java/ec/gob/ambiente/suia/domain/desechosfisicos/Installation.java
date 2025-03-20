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


/**
 * The persistent class for the installation database table.
 * 
 */
@Entity
@Table(name="installation", schema="waste_dangerous")
@NamedQuery(name="Installation.findAll", query="SELECT i FROM Installation i")
public class Installation implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@Column(name="inst_id")
	private Integer instId;

	@Column(name="inst_date_add")
	private Date instDateAdd;

	@Column(name="inst_date_update")
	private Date instDateUpdate;

	@Column(name="inst_name")
	private String instName;

	@Column(name="inst_status")
	private Boolean instStatus;

	@Column(name="user_id_add")
	private Integer userIdAdd;

	@Column(name="user_id_update")
	private Integer userIdUpdate;

	//bi-directional many-to-one association to Processed
	@OneToMany(mappedBy="installation")
	private List<Processed> processeds;

	public Installation() {
	}

	public Integer getInstId() {
		return this.instId;
	}

	public void setInstId(Integer instId) {
		this.instId = instId;
	}

	public Date getInstDateAdd() {
		return this.instDateAdd;
	}

	public void setInstDateAdd(Date instDateAdd) {
		this.instDateAdd = instDateAdd;
	}

	public Date getInstDateUpdate() {
		return this.instDateUpdate;
	}

	public void setInstDateUpdate(Date instDateUpdate) {
		this.instDateUpdate = instDateUpdate;
	}

	public String getInstName() {
		return this.instName;
	}

	public void setInstName(String instName) {
		this.instName = instName.toUpperCase();
	}

	public Boolean getInstStatus() {
		return this.instStatus;
	}

	public void setInstStatus(Boolean instStatus) {
		this.instStatus = instStatus;
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
		processed.setInstallation(this);

		return processed;
	}

	public Processed removeProcessed(Processed processed) {
		getProcesseds().remove(processed);
		processed.setInstallation(null);

		return processed;
	}

}