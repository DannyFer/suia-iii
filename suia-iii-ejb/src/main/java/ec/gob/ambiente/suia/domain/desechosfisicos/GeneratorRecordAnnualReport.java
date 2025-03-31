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
 * The persistent class for the generator_record_annual_report database table.
 * 
 */
@Entity
@Table(name="generator_record_annual_report", schema="waste_dangerous")
@NamedQuery(name="GeneratorRecordAnnualReport.findAll", query="SELECT g FROM GeneratorRecordAnnualReport g")
public class GeneratorRecordAnnualReport implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@Column(name="grar_id")
	private Integer grarId;

	@Column(name="grar_date_add")
	private Date grarDateAdd;

	@Column(name="grar_date_update")
	private Date grarDateUpdate;

	@Column(name="grar_is_new")
	private Boolean grarIsNew;

	@Column(name="grar_percentage_decline_generation")
	private BigDecimal grarPercentageDeclineGeneration;

	@Column(name="grar_quantity_delivered")
	private BigDecimal grarQuantityDelivered;

	@Column(name="grar_quantity_generating")
	private BigDecimal grarQuantityGenerating;

	@Column(name="grar_quantity_stored")
	private BigDecimal grarQuantityStored;

	@Column(name="grar_status")
	private Boolean grarStatus;

	@Column(name="user_id_add")
	private Integer userIdAdd;

	@Column(name="user_id_update")
	private Integer userIdUpdate;

	//bi-directional many-to-one association to CatalogsWaste
	@ManyToOne
	@JoinColumn(name="cawa_id")
	private CatalogsWaste catalogsWaste;
	
	//bi-directional many-to-one association to Catalog
	@ManyToOne
	@JoinColumn(name="grar_year")
	private Catalog year;

	//bi-directional many-to-one association to GeneratorRecordAnnualStatement
	@ManyToOne
	@JoinColumn(name="gras_id")
	private GeneratorRecordAnnualStatement generatorRecordAnnualStatement;

	public GeneratorRecordAnnualReport() {
	}

	public Integer getGrarId() {
		return this.grarId;
	}

	public void setGrarId(Integer grarId) {
		this.grarId = grarId;
	}

	public Date getGrarDateAdd() {
		return this.grarDateAdd;
	}

	public void setGrarDateAdd(Date grarDateAdd) {
		this.grarDateAdd = grarDateAdd;
	}

	public Date getGrarDateUpdate() {
		return this.grarDateUpdate;
	}

	public void setGrarDateUpdate(Date grarDateUpdate) {
		this.grarDateUpdate = grarDateUpdate;
	}

	public Boolean getGrarIsNew() {
		return this.grarIsNew;
	}

	public void setGrarIsNew(Boolean grarIsNew) {
		this.grarIsNew = grarIsNew;
	}

	public BigDecimal getGrarPercentageDeclineGeneration() {
		return this.grarPercentageDeclineGeneration;
	}

	public void setGrarPercentageDeclineGeneration(BigDecimal grarPercentageDeclineGeneration) {
		this.grarPercentageDeclineGeneration = grarPercentageDeclineGeneration;
	}

	public BigDecimal getGrarQuantityDelivered() {
		return this.grarQuantityDelivered;
	}

	public void setGrarQuantityDelivered(BigDecimal grarQuantityDelivered) {
		this.grarQuantityDelivered = grarQuantityDelivered;
	}

	public BigDecimal getGrarQuantityGenerating() {
		return this.grarQuantityGenerating;
	}

	public void setGrarQuantityGenerating(BigDecimal grarQuantityGenerating) {
		this.grarQuantityGenerating = grarQuantityGenerating;
	}

	public BigDecimal getGrarQuantityStored() {
		return this.grarQuantityStored;
	}

	public void setGrarQuantityStored(BigDecimal grarQuantityStored) {
		this.grarQuantityStored = grarQuantityStored;
	}

	public Boolean getGrarStatus() {
		return this.grarStatus;
	}

	public void setGrarStatus(Boolean grarStatus) {
		this.grarStatus = grarStatus;
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

	public GeneratorRecordAnnualStatement getGeneratorRecordAnnualStatement() {
		return this.generatorRecordAnnualStatement;
	}

	public void setGeneratorRecordAnnualStatement(GeneratorRecordAnnualStatement generatorRecordAnnualStatement) {
		this.generatorRecordAnnualStatement = generatorRecordAnnualStatement;
	}

	public Catalog getYear() {
		return year;
	}

	public void setYear(Catalog year) {
		this.year = year;
	}
}