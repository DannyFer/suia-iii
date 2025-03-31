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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;


@Entity
@Table(name = "project_licencing_coa_ciuu", schema = "coa_mae")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "prli_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "prli_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "prli_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "prli_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "prli_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prli_status = 'TRUE'")
public class ProyectoLicenciaCuaCiuu extends EntidadAuditable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1645985721278202830L;
	
	@Getter
	@Setter
	@Id
	@Column(name = "prli_id")
	@SequenceGenerator(name = "PROJECTS_ENVIRONMENTAL_LICENSING_PRLI_ID_GENERATOR", sequenceName = "project_licencing_coa_ciuu_prli_id_seq", schema = "coa_mae", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROJECTS_ENVIRONMENTAL_LICENSING_PRLI_ID_GENERATOR")
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
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="cosu_id")
	private SubActividades subActividad;
	
	@Getter
	@Setter
	@Column(name = "prli_order_hierarchy")
	private int orderJerarquia;
		
	@Getter
	@Setter
	@Column(name = "prli_observation_bd", length = 255)
	private String observacionDB;
	
	@Getter
	@Setter
	@Column(name = "prli_primary")
	private Boolean primario;
	
	@Getter
	@Setter
	@Column(name = "prli_genetic")
	private Boolean genetico;
	
	@Getter
	@Setter
	@Column(name = "prli_scout_drilling")
	private Boolean scoutDrilling;
	
	@Getter
	@Setter
	@Column(name = "prli_financing")
	private Boolean financiadoBancoDesarrollo;
	
	@Getter
	@Setter
	@Column(name = "prli_urban")
	private Boolean zonaUbbana;
	
	@Getter
	@Setter
	@Column(name = "prli_payment_tases")
	private Boolean pagoAdministrativo;
	
	@Getter
	@Setter
	@Column(name = "prli_sanitation")
	private Boolean saneamiento;
	
	@Getter
	@Setter
	@Column(name = "prli_stores_hydrocarbons")
	private Boolean almacenaHidrocarburos;
	
	@Getter
	@Setter
	@Column(name = "prli_artesanal_miner")
	private Boolean mineriaArtesanal;
	
	@Getter
	@Setter
	@Column(name = "prli_water_purification")
	private Boolean potabilizacionAgua;
	
	@Getter
	@Setter
	@Column(name = "prli_cremation")
	private Boolean realizaCremacion;
	
	@Getter
	@Setter
	@Column(name = "prli_value_option")
	private Boolean valorOpcion;
	
	@Getter
	@Setter
	@Column(name = "prli_cosu_id_financed_state_bank")
	private Integer idActividadFinanciadoBancoEstado;	
	
	@Getter
	@Setter
	@Column(name = "prli_municipal_sewer")
	private Boolean alcantarilladoMunicipio;

	@ManyToOne
	@JoinColumn(name = "ccsu_id")
	@ForeignKey(name = "fk_ccsu_id")
	@Getter
	@Setter
	private CombinacionSubActividades combinacionSubActividades;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="geca_id")	
	private CatalogoGeneralCoa tipoRegimenMinero;

	@Getter
	@Setter
	@Column(name = "pety_id")
	private Integer idCatalogoCapaPermiso;
	
}
