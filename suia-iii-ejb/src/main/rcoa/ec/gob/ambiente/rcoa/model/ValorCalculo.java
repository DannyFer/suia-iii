package ec.gob.ambiente.rcoa.model;

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

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "calculation_values", schema = "coa_mae")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "cava_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "cava_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "cava_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "cava_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "cava_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "cava_status = 'TRUE'")
public class ValorCalculo extends EntidadAuditable{
	
	private static final long serialVersionUID = -2406205868897970664L;
	
	@Getter
	@Setter
	@Id
	@Column(name = "cava_id")	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "prco_id")
	@ForeignKey(name = "fk_prco_id")
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;

	@ManyToOne
	@JoinColumn(name = "caci_id")
	@ForeignKey(name = "fk_caci_id")
	@Getter
	@Setter
	private CatalogoCIUU catalogoCIUU;

	@ManyToOne
	@JoinColumn(name = "macr_id")
	@ForeignKey(name = "fk_macr_id")
	@Getter
	@Setter
	private CriterioMagnitud criterioMagnitud;
	
	@Getter
	@Setter
	@Column(name = "cava_code")
	private String codigo;
	
	@Getter
	@Setter
	@Column(name = "cava_level")
	private Integer nivel;
	
	@Getter
	@Setter
	@Column(name = "cava_chemical")
	private Integer valorQuimico;
	
	@Getter
	@Setter
	@Column(name = "cava_enviromental")
	private Integer valorAmbiental;

	@Getter
	@Setter
	@Column(name = "cava_concentration")
	private Integer valorConcentracion;
	
	@Getter
	@Setter
	@Column(name = "cava_risk")
	private Integer valorRiesgo;

	@Getter
	@Setter
	@Column(name = "cava_social")
	private Integer valorSocial;

	@Getter
	@Setter
	@Column(name = "cava_social_capacity")
	private Integer valorCapacidadSocial;

	@Getter
	@Setter
	@Column(name = "cava_biophysical_capacity")
	private Integer valorCapacidadBiofisica;

	@Getter
	@Setter
	@Column(name = "cava_magnitude")
	private Integer valorMagnitud;

	@Getter
	@Setter
	@Column(name = "cava_relative_importance")
	private Integer valorImportanciaRelativa;


	@Getter
	@Setter
	@Column(name = "cava_formula_result")
	private Integer valorResultadoFormula;	

	@Getter
	@Setter
	@Column(name = "cava_alternate_categorization")
	private Integer categorizacionAlternativa;
	
	@Getter
	@Setter
	@Column(name = "cava_rank")
	private Integer rango;

	@Getter
	@Setter
	@Column(name = "cava_value")
	private Integer valor;

	@Getter
	@Setter
	@Column(name = "cava_observation")
	private String observacion;


	@Getter
	@Setter
	@Column(name = "cava_reference_exception")
	private String excepcionReferencia;

	@Getter
	@Setter
	@Column(name = "cava_observation_bd")
	private String observacionBase;

}
