package ec.gob.ambiente.rcoa.inventarioForestal.model;

import java.math.BigDecimal;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
@Table(name = "forest_species_registry", schema = "coa_forest_inventory")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "spre_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "spre_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "spre_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "spre_user_update")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "spre_creator_user")) 
})
@NamedQueries({
	@NamedQuery(name = "RegistroEspeciesForestales.findAll", query = "SELECT ref FROM RegistroEspeciesForestales ref"),
	@NamedQuery(name = RegistroEspeciesForestales.FIND_BY_ID, query = "SELECT ref FROM RegistroEspeciesForestales ref WHERE ref.id= :idHigher")
})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "spre_status = 'TRUE'")
public class RegistroEspeciesForestales extends EntidadBase {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -4723226460021344828L;
	public static final String FIND_BY_ID = "ec.com.magmasoft.business.domain.RegistroEspeciesForestales.findById";
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="spre_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name="spre_taxo_level")
	private String nivelTaxonomia;
	
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
	@Column(name="spre_family_id")
	private Integer idFamilia;
	
	@Getter
	@Setter
	@Column(name="spre_gender_id")
	private Integer idGenero;
	
	@Getter
	@Setter
	@Column(name="spre_specie_id")
	private Integer idEspecie;
	
	
	@Getter
	@Setter
	@Column(name="spre_other_specie")
	private String otroEspecie;
	
	@Getter
	@Setter
	@Column(name="spre_dap")
	private Double diametroEspecie;
	
	@Getter
	@Setter
	@Column(name="spre_total_height")
	private Double alturaEspecie;
	
	@Getter
	@Setter
	@Column(name="spre_basal_area")
	private BigDecimal areaBasalEspecie;
	
	@Getter
	@Setter
	@Column(name="spre_total_volume")
	private BigDecimal volumenTotalEspecie;
	
	@Getter
	@Setter
	@Column(name="spre_excel_load")
	private Boolean desdeExcel;
	
	// Inventario Forestal Certificado
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="foin_id")
	private InventarioForestalAmbiental inventarioForestalAmbiental;
	
	@Getter
	@Setter
	@Column(name="spre_sample_code")
	private String codigoMuestra;
	
	@Getter
	@Setter
	@Column(name="spre_numero_del_individuo")
	private Integer numeroIndividuo;
	

}
