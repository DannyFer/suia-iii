package ec.gob.ambiente.suia.domain.desechosfisicos;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;
import java.util.Date;


/**
 * The persistent class for the catalogs_generator_record database table.
 * 
 */
@Entity
@Table(name="catalogs_generator_record", schema="waste_dangerous")
@NamedQuery(name="CatalogsGeneratorRecord.findAll", query="SELECT c FROM CatalogsGeneratorRecord c")
public class CatalogsGeneratorRecord implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@Column(name="cagr_id")
	private Integer cagrId;

	@Column(name="cagr_canton")
	private String cagrCanton;

	@Column(name="cagr_code")
	private String cagrCode;

	@Temporal(TemporalType.DATE)
	@Column(name="cagr_date")
	private Date cagrDate;

	@Column(name="cagr_date_add")
	private Timestamp cagrDateAdd;

	@Column(name="cagr_date_update")
	private Timestamp cagrDateUpdate;

	@Column(name="cagr_name")
	private String cagrName;

	@Column(name="cagr_province")
	private String cagrProvince;

	@Column(name="cagr_status")
	private Boolean cagrStatus;

	@Column(name="user_id_add")
	private Integer userIdAdd;

	@Column(name="user_id_update")
	private Integer userIdUpdate;

	public CatalogsGeneratorRecord() {
	}

	public Integer getCagrId() {
		return this.cagrId;
	}

	public void setCagrId(Integer cagrId) {
		this.cagrId = cagrId;
	}

	public String getCagrCanton() {
		return this.cagrCanton;
	}

	public void setCagrCanton(String cagrCanton) {
		this.cagrCanton = cagrCanton;
	}

	public String getCagrCode() {
		return this.cagrCode;
	}

	public void setCagrCode(String cagrCode) {
		this.cagrCode = cagrCode;
	}

	public Date getCagrDate() {
		return this.cagrDate;
	}

	public void setCagrDate(Date cagrDate) {
		this.cagrDate = cagrDate;
	}

	public Timestamp getCagrDateAdd() {
		return this.cagrDateAdd;
	}

	public void setCagrDateAdd(Timestamp cagrDateAdd) {
		this.cagrDateAdd = cagrDateAdd;
	}

	public Timestamp getCagrDateUpdate() {
		return this.cagrDateUpdate;
	}

	public void setCagrDateUpdate(Timestamp cagrDateUpdate) {
		this.cagrDateUpdate = cagrDateUpdate;
	}

	public String getCagrName() {
		return this.cagrName;
	}

	public void setCagrName(String cagrName) {
		this.cagrName = cagrName;
	}

	public String getCagrProvince() {
		return this.cagrProvince;
	}

	public void setCagrProvince(String cagrProvince) {
		this.cagrProvince = cagrProvince;
	}

	public Boolean getCagrStatus() {
		return this.cagrStatus;
	}

	public void setCagrStatus(Boolean cagrStatus) {
		this.cagrStatus = cagrStatus;
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

}