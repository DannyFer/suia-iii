package ec.gob.ambiente.rcoa.inventarioForestal.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "calculation_index_inventory_environmental_register", schema = "coa_forest_inventory")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "cire_status")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "cire_creator_user")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "cire_creation_date")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "cire_user_update")), 
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "cire_date_update"))
})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "cire_status = 'TRUE'")
public class IndiceValorImportanciaInventarioForestal extends EntidadBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="cire_id")
	private Integer id;
	
	@Getter
	@Setter
	@Transient
	private HigherClassification familiaEspecie;
	
	@Getter
	@Setter
	@Transient
	private HigherClassification generoEspecie;
	
	@Getter
	@Setter
	@Transient
	private SpecieTaxa especieEspecie;
	
	@Getter
	@Setter
	@Column(name="cire_gender_id")
	private Integer idFamilia;
	
	@Getter
	@Setter
	@Column(name="cire_family_id")
	private Integer idGenero;
	
	@Getter
	@Setter
	@Column(name="cire_specie_id")
	private Integer idEspecie;
	
	@Getter
	@Setter
	@Column(name="cire_frecuency_species")
	private Integer frecuenciaEspecies;
	
	@Getter
	@Setter
	@Column(name="cire_ab_basar_area")
	private Double areaBasal;
	
	@Getter
	@Setter
	@Column(name="cire_dnr")
	private Double dnr;
	
	@Getter
	@Setter
	@Column(name="cire_dmr")
	private Double dmr;
	
	@Getter
	@Setter
	@Column(name="cire_ivi")
	private Double ivi;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="foin_id")
	private InventarioForestalAmbiental inventarioForestalAmbiental;

}
