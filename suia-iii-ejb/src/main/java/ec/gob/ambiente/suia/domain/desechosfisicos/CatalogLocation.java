package ec.gob.ambiente.suia.domain.desechosfisicos;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "catalogs", schema = "suia_share")
public class CatalogLocation implements java.io.Serializable{
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@Column(name = "cata_id", unique = true, nullable = false)
	private int cataId;	
	
	@Column(name = "cata_name", nullable = false, length = 250)
	private String cataName;
	
	@Column(name = "cata_description", length = 250)
	private String cataDescription;
	
	@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="cata_id_main")
	private CatalogLocation catalogMain;
	
	@Column(name = "cata_code", length = 250)
	private String cataCode;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "caty_id", nullable = false)
	private CatalogType catalogType;
	
	@Column(name = "cata_id_aux")
	private Integer cataIdAux;	
	
	@Column(name = "cata_extent")
	private String cataExtent;
	
	@Column(name = "cata_id_parent")
	private Integer cataIdParent;
	
	@Column(name = "cata_id_aux1")
	private Integer cataIdAux1;
	
	@Column(name = "cata_id_aux2")
	private Integer cataIdAux2;
	
	@Column(name = "cata_locked")
	private Boolean cataLocked;
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="catalogMain")
	private Set<CatalogLocation> catalogs = new HashSet<CatalogLocation>(0);
	
	public CatalogLocation() {
	}
			
	public int getCataId() {
		return cataId;
	}
	public void setCataId(int cataId) {
		this.cataId = cataId;
	}	
	
	public String getCataName() {
		return cataName;
	}
	
	public void setCataName(String cataName) {
		this.cataName = cataName;
	}
		
	public String getCataDescription() {
		return cataDescription;
	}
	public void setCataDescription(String cataDescription) {
		this.cataDescription = cataDescription;
	}	
	
	public CatalogLocation getCatalogMain() {
		return catalogMain;
	}
	
	public void setCatalogMain(CatalogLocation catalogMain) {
		this.catalogMain = catalogMain;
	}
		
	public String getCataCode() {
		return cataCode;
	}
	
	public void setCataCode(String cataCode) {
		this.cataCode = cataCode;
	}
		
	public CatalogType getCatalogType() {
		return catalogType;
	}
	public void setCatalogType(CatalogType catalogType) {
		this.catalogType = catalogType;
	}
		
	public Integer getCataIdAux() {
		return cataIdAux;
	}
	
	public void setCataIdAux(Integer cataIdAux) {
		this.cataIdAux = cataIdAux;
	}
		
	public String getCataExtent() {
		return cataExtent;
	}
	public void setCataExtent(String cataExtent) {
		this.cataExtent = cataExtent;
	}
		
	public Integer getCataIdParent() {
		return cataIdParent;
	}
	
	public void setCataIdParent(Integer cataIdParent) {
		this.cataIdParent = cataIdParent;
	}
		
	public Integer getCataIdAux1() {
		return cataIdAux1;
	}
	
	public void setCataIdAux1(Integer cataIdAux1) {
		this.cataIdAux1 = cataIdAux1;
	}
		
	public Integer getCataIdAux2() {
		return cataIdAux2;
	}
	
	public void setCataIdAux2(Integer cataIdAux2) {
		this.cataIdAux2 = cataIdAux2;
	}
		
	public Boolean isCataLocked() {
		return cataLocked;
	}
	
	public void setCataLocked(Boolean cataLocked) {
		this.cataLocked = cataLocked;
	}		
	
	public Set<CatalogLocation> getCatalogs() {
		return catalogs;
	}

	public void setCatalogs(Set<CatalogLocation> catalogs) {
		this.catalogs = catalogs;
	}

}