package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the toxicological_category_catalog database table.
 * 
 */
@Entity
@Table(name = "toxicological_category_catalog", schema = "suia_iii")
@NamedQuery(name = CatalogoCategoriaToxicologica.FIND_ALL, query = "SELECT t FROM CatalogoCategoriaToxicologica t")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "tocc_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tocc_status = 'TRUE'")
public class CatalogoCategoriaToxicologica extends EntidadBase {

	private static final long serialVersionUID = 4218668758249092817L;
	public static final String FIND_ALL = "ec.com.magmasoft.business.domain.CatalogoCategoriaToxicologica.findAll";

	@Id
	@SequenceGenerator(name = "TOXICOLOGICAL_CATEGORY_CATALOG_GENERATOR", sequenceName = "toxicological_category_catalog_id_seq", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TOXICOLOGICAL_CATEGORY_CATALOG_GENERATOR")
	@Column(name = "tocc_id")
	@Getter
	@Setter
	private Integer id;

	@Column(name = "tocc_description")
	@Getter
	@Setter
	private String descripcion;

}