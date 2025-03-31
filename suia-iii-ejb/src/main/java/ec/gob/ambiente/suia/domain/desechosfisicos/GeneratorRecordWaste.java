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
 * The persistent class for the generator_record_waste database table.
 * 
 */
@Entity
@Table(name="generator_record_waste", schema="waste_dangerous")
@NamedQuery(name="GeneratorRecordWaste.findAll", query="SELECT g FROM GeneratorRecordWaste g")
public class GeneratorRecordWaste implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@Column(name="grwa_id")
	private Integer grwaId;

	@Column(name="grwa_date_add")
	private Date grwaDateAdd;

	@Column(name="grwa_date_update")
	private Date grwaDateUpdate;

	@Column(name="grwa_quantity")
	private BigDecimal grwaQuantity;

	@Column(name="grwa_status")
	private Boolean grwaStatus;

	@Column(name="user_id_add")
	private Integer userIdAdd;

	@Column(name="user_id_update")
	private Integer userIdUpdate;

	//bi-directional many-to-one association to CatalogsWaste
	@ManyToOne
	@JoinColumn(name="cawa_id")
	private CatalogsWaste catalogsWaste;

	//bi-directional many-to-one association to GeneratorRecord
	@ManyToOne
	@JoinColumn(name="gere_id")
	private GeneratorRecord generatorRecord;

	public GeneratorRecordWaste() {
	}

	public Integer getGrwaId() {
		return this.grwaId;
	}

	public void setGrwaId(Integer grwaId) {
		this.grwaId = grwaId;
	}

	public Date getGrwaDateAdd() {
		return this.grwaDateAdd;
	}

	public void setGrwaDateAdd(Date grwaDateAdd) {
		this.grwaDateAdd = grwaDateAdd;
	}

	public Date getGrwaDateUpdate() {
		return this.grwaDateUpdate;
	}

	public void setGrwaDateUpdate(Date grwaDateUpdate) {
		this.grwaDateUpdate = grwaDateUpdate;
	}

	public BigDecimal getGrwaQuantity() {
		return this.grwaQuantity;
	}

	public void setGrwaQuantity(BigDecimal grwaQuantity) {
		this.grwaQuantity = grwaQuantity;
	}

	public Boolean getGrwaStatus() {
		return this.grwaStatus;
	}

	public void setGrwaStatus(Boolean grwaStatus) {
		this.grwaStatus = grwaStatus;
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

	public GeneratorRecord getGeneratorRecord() {
		return this.generatorRecord;
	}

	public void setGeneratorRecord(GeneratorRecord generatorRecord) {
		this.generatorRecord = generatorRecord;
	}

}