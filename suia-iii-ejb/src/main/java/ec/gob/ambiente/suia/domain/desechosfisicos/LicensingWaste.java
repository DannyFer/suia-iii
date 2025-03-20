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
 * The persistent class for the licensing_waste database table.
 * 
 */
@Entity
@Table(name="licensing_waste", schema="waste_dangerous")
@NamedQuery(name="LicensingWaste.findAll", query="SELECT l FROM LicensingWaste l")
public class LicensingWaste implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@Column(name="liwa_id")
	private Integer liwaId;

	@Column(name="liwa_date_add")
	private Date liwaDateAdd;

	@Column(name="liwa_date_update")
	private Date liwaDateUpdate;

	@Column(name="liwa_quantity")
	private BigDecimal liwaQuantity;

	@Column(name="liwa_status")
	private Boolean liwaStatus;

	@Column(name="user_id_add")
	private Integer userIdAdd;

	@Column(name="user_id_update")
	private Integer userIdUpdate;

	//bi-directional many-to-one association to CatalogsWaste
	@ManyToOne
	@JoinColumn(name="cawa_id")
	private CatalogsWaste catalogsWaste;

	//bi-directional many-to-one association to Licensing
	@ManyToOne
	@JoinColumn(name="lice_id")
	private Licensing licensing;

	public LicensingWaste() {
	}

	public Integer getLiwaId() {
		return this.liwaId;
	}

	public void setLiwaId(Integer liwaId) {
		this.liwaId = liwaId;
	}

	public Date getLiwaDateAdd() {
		return this.liwaDateAdd;
	}

	public void setLiwaDateAdd(Date liwaDateAdd) {
		this.liwaDateAdd = liwaDateAdd;
	}

	public Date getLiwaDateUpdate() {
		return this.liwaDateUpdate;
	}

	public void setLiwaDateUpdate(Date liwaDateUpdate) {
		this.liwaDateUpdate = liwaDateUpdate;
	}

	public BigDecimal getLiwaQuantity() {
		return this.liwaQuantity;
	}

	public void setLiwaQuantity(BigDecimal liwaQuantity) {
		if(liwaQuantity != null)
		{
			this.liwaQuantity = liwaQuantity.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		}
	}

	public Boolean getLiwaStatus() {
		return this.liwaStatus;
	}

	public void setLiwaStatus(Boolean liwaStatus) {
		this.liwaStatus = liwaStatus;
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

	public Licensing getLicensing() {
		return this.licensing;
	}

	public void setLicensing(Licensing licensing) {
		this.licensing = licensing;
	}

}