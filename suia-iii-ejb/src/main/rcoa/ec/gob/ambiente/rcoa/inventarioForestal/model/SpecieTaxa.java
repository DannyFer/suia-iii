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
import javax.persistence.Transient;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="species_taxa", schema="biodiversity")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "spta_status"))
})
@NamedQueries({
	@NamedQuery(name = "species_taxa.findAll", query = "SELECT st FROM SpecieTaxa st"),
	@NamedQuery(name = SpecieTaxa.GET_BY_GENERO, query = "SELECT st FROM SpecieTaxa st WHERE st.higherClassification.id = :idGenero ORDER BY st.id desc")
})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "spta_status = 'TRUE'")
public class SpecieTaxa extends EntidadBase {

	
	private static final long serialVersionUID = 1L;
	
	public static final String GET_BY_GENERO = "ec.gob.ambiente.retce.model.SpecieTaxa.getByFamilia";
	
	// Clave primaria
	@Getter
	@Setter
	@Id
	@Column(name="spta_id")
	private Integer id;

	// Nombre cientifico
	@Getter
	@Setter
	@Column(name="spta_scientific_name")
	private String sptaScientificName;
	
	// Nombre Clasificacion
	@Getter
	@Setter
	@Column(name="spta_higher_classification")
	private String sptaHigherClassification;
	
	// Id del rango
	@Getter
	@Setter
	@Column(name="tara_id")
	private Integer idTaxaRank;
	
	// Nombre del rango
	@Getter
	@Setter
	@Column(name="spta_taxon_rank_name")
	private String sptaTaxonRankName;
	
	// Id del padre
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="hicl_id")
	private HigherClassification higherClassification;
	
	// Id del padre
	@Getter
	@Setter
	@Transient
	private String sptaOtherScientificName;
	
}
