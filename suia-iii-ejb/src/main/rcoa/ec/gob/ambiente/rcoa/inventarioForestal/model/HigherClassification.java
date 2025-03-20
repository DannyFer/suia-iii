package ec.gob.ambiente.rcoa.inventarioForestal.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="higher_classifications", schema="biodiversity")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "hicl_status"))
})
@NamedQueries({
	@NamedQuery(name = "HigherClassification.findAll", query = "SELECT hc FROM HigherClassification hc"),
	@NamedQuery(name = HigherClassification.GET_BY_FAMILIA, query = "SELECT hc FROM HigherClassification hc WHERE hc.hiclIdParent.id = :idFamilia ORDER BY hc.hiclScientificName asc")
})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "hicl_status = 'TRUE'")
public class HigherClassification extends EntidadBase {

	
	private static final long serialVersionUID = 1L;
	
	public static final String GET_BY_FAMILIA = "ec.gob.ambiente.retce.model.HigherClassification.getByFamilia";
	
	// Clave primaria
	@Getter
	@Setter
	@Id
	@Column(name="hicl_id")
	private Integer id;

	// Nombre cientifico
	@Getter
	@Setter
	@Column(name="hicl_scientific_name")
	private String hiclScientificName;
	
	// Nombre Clasificacion
	@Getter
	@Setter
	@Column(name="hicl_higher_classification")
	private String nombreClasificacion;
	
	// Id del rango
	@Getter
	@Setter
	@Column(name="tara_id")
	private Integer idTaxaRank;
	
	// Nombre del rango
	@Getter
	@Setter
	@Column(name="hicl_taxon_rank_name")
	private String hiclTaxonRankName;
	
	// Id del padre
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="hicl_id_parent")
	private HigherClassification hiclIdParent;
	
	// Nombre Padre
	@Getter
	@Setter
	@Column(name="hicl_parent_name")
	private String nombrePadre;

}
