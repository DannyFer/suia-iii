package ec.gob.ambiente.suia.domain.desechosfisicos;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;

/**
 * The persistent class for the catalog_types database table.
 * 
 */
@Entity
@Table(name = "catalog_types", schema = "suia_share")
@NamedQuery(name="CatalogType.findAll", query = "SELECT c FROM CatalogType c")
public class CatalogType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="caty_id")
	private Integer catyId;

	@Column(name="caty_description")
	private String catyDescription;

	@Column(name="caty_name")
	private String catyName;

	// bi-directional many-to-one association to Catalog
	@OneToMany(mappedBy = "catalogType")
	private List<CatalogLocation> catalogs;

	public CatalogType() {
	}

	public Integer getCatyId() {
		return this.catyId;
	}

	public void setCatyId(Integer catyId) {
		this.catyId = catyId;
	}

	public String getCatyDescription() {
		return this.catyDescription;
	}

	public void setCatyDescription(String catyDescription) {
		this.catyDescription = catyDescription;
	}

	public String getCatyName() {
		return this.catyName;
	}

	public void setCatyName(String catyName) {
		this.catyName = catyName;
	}

//	public List<Catalog> getCatalogs() {
//		return this.catalogs;
//	}
//
//	public void setCatalogs(List<Catalog> catalogs) {
//		this.catalogs = catalogs;
//	}

//	public Catalog addCatalog(Catalog catalog) {
//		getCatalogs().add(catalog);
//		catalog.setCatalogType(this);
//
//		return catalog;
//	}
//
//	public Catalog removeCatalog(Catalog catalog) {
//		getCatalogs().remove(catalog);
//		catalog.setCatalogType(null);
//
//		return catalog;
//	}

}