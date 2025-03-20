package ec.gob.ambiente.suia.domain.desechosfisicos;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the ccan database table.
 * 
 */
@Entity
@Table(name="ccan", schema="waste_dangerous")
@NamedQuery(name="Ccan.findAll", query="SELECT c FROM Ccan c")
public class Ccan implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@Column(name="ccan_id")
	private Integer ccanId;

	@Column(name="ccan_codigo")
	private String ccanCodigo;

	@Column(name="ccan_date_add")
	private Timestamp ccanDateAdd;

	@Column(name="ccan_date_update")
	private Timestamp ccanDateUpdate;

	@Column(name="ccan_description_activities")
	private String ccanDescriptionActivities;

	@Column(name="ccan_status")
	private Boolean ccanStatus;

	@Column(name="user_id_add")
	private Integer userIdAdd;

	@Column(name="user_id_update")
	private Integer userIdUpdate;

	//bi-directional many-to-one association to Processed
	@OneToMany(mappedBy="ccan")
	private List<Processed> processeds;

	public Ccan() {
	}

	public Integer getCcanId() {
		return this.ccanId;
	}

	public void setCcanId(Integer ccanId) {
		this.ccanId = ccanId;
	}

	public String getCcanCodigo() {
		return this.ccanCodigo;
	}

	public void setCcanCodigo(String ccanCodigo) {
		this.ccanCodigo = ccanCodigo;
	}

	public Timestamp getCcanDateAdd() {
		return this.ccanDateAdd;
	}

	public void setCcanDateAdd(Timestamp ccanDateAdd) {
		this.ccanDateAdd = ccanDateAdd;
	}

	public Timestamp getCcanDateUpdate() {
		return this.ccanDateUpdate;
	}

	public void setCcanDateUpdate(Timestamp ccanDateUpdate) {
		this.ccanDateUpdate = ccanDateUpdate;
	}

	public String getCcanDescriptionActivities() {
		return this.ccanDescriptionActivities;
	}

	public void setCcanDescriptionActivities(String ccanDescriptionActivities) {
		this.ccanDescriptionActivities = ccanDescriptionActivities;
	}

	public Boolean getCcanStatus() {
		return this.ccanStatus;
	}

	public void setCcanStatus(Boolean ccanStatus) {
		this.ccanStatus = ccanStatus;
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
		processed.setCcan(this);

		return processed;
	}

	public Processed removeProcessed(Processed processed) {
		getProcesseds().remove(processed);
		processed.setCcan(null);

		return processed;
	}

}