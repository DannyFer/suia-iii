package ec.gob.ambiente.suia.domain.desechosfisicos;

import java.io.Serializable;
import java.math.BigDecimal;
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
 * The persistent class for the pma_waste database table.
 * 
 */
@Entity
@Table(name="pma_waste", schema="waste_dangerous")
@NamedQuery(name="PmaWaste.findAll", query="SELECT p FROM PmaWaste p")
public class PmaWaste implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="pmwa_id")
	private Integer pmwaId;

	@Column(name="pmwa_date_add")
	private Date pmwaDateAdd;

	@Column(name="pmwa_date_update")
	private Date pmwaDateUpdate;

	@Column(name="pmwa_quantity")
	private BigDecimal pmwaQuantity;

	@Column(name="pmwa_status")
	private Boolean pmwaStatus;

	@Column(name="user_id_add")
	private Integer userIdAdd;

	@Column(name="user_id_update")
	private Integer userIdUpdate;

	//bi-directional many-to-one association to CatalogsWaste
	@ManyToOne
	@JoinColumn(name="cawa_id")
	private CatalogsWaste catalogsWaste;

	//bi-directional many-to-one association to UpdatesPma
	@ManyToOne
	@JoinColumn(name="uppm_id")
	private UpdatesPma updatesPma;

	public PmaWaste() {
	}

	public Integer getPmwaId() {
		return this.pmwaId;
	}

	public void setPmwaId(Integer pmwaId) {
		this.pmwaId = pmwaId;
	}

	public Date getPmwaDateAdd() {
		return this.pmwaDateAdd;
	}

	public void setPmwaDateAdd(Date pmwaDateAdd) {
		this.pmwaDateAdd = pmwaDateAdd;
	}

	public Date getPmwaDateUpdate() {
		return this.pmwaDateUpdate;
	}

	public void setPmwaDateUpdate(Date pmwaDateUpdate) {
		this.pmwaDateUpdate = pmwaDateUpdate;
	}

	public BigDecimal getPmwaQuantity() {
		return this.pmwaQuantity;
	}

	public void setPmwaQuantity(BigDecimal pmwaQuantity) {
		this.pmwaQuantity = pmwaQuantity;
	}

	public Boolean getPmwaStatus() {
		return this.pmwaStatus;
	}

	public void setPmwaStatus(Boolean pmwaStatus) {
		this.pmwaStatus = pmwaStatus;
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

	public CatalogsWaste getCatalogsWaste() {
		return this.catalogsWaste;
	}

	public void setCatalogsWaste(CatalogsWaste catalogsWaste) {
		this.catalogsWaste = catalogsWaste;
	}

	public UpdatesPma getUpdatesPma() {
		return this.updatesPma;
	}

	public void setUpdatesPma(UpdatesPma updatesPma) {
		this.updatesPma = updatesPma;
	}

}