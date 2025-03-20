/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import ec.gob.ambiente.suia.dto.EntityAdjunto;

/**
 * 
 * @author christian
 */
@Entity
@Table(name = "detail_fauna", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "defa_status")) })
@NamedQueries({
		@NamedQuery(name = "DetailFauna.findAll", query = "SELECT d FROM DetalleFauna d"),
		@NamedQuery(name = DetalleFauna.LISTAR_POR_PUNTOS_MUESTRO_IDS, query = "SELECT d FROM DetalleFauna d WHERE d.idPuntosMuestreo IN :listaPuntosMuestro AND d.estado = true") })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "defa_status = 'TRUE'")
public class DetalleFauna extends EntidadBase {

	private static final long serialVersionUID = -213050553815331183L;

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR_POR_PUNTOS_MUESTRO_IDS = PAQUETE + "DetalleFauna.listarPorPuntosMuestreoIds";

	@Id
	@Basic(optional = false)
	@Getter
	@Setter
	@SequenceGenerator(name = "DETALLE_FAUNA_ID_GENERATOR", initialValue = 1, sequenceName = "seq_defa_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DETALLE_FAUNA_ID_GENERATOR")
	@Column(name = "defa_id", nullable = false)
	private Integer id;
	@Getter
	@Setter
	@Column(name = "defa_registration_number")
	private Integer numeroRegistro;
	@Getter
	@Setter
	@Column(name = "defa_order", length = 100)
	private String orden;
	@Getter
	@Setter
	@Column(name = "defa_family", length = 100)
	private String familia;
	@Getter
	@Setter
	@Column(name = "defa_genre", length = 100)
	private String genero;
	@Getter
	@Setter
	@Column(name = "defa_species", length = 100)
	private String especie;
	@Getter
	@Setter
	@Column(name = "defa_common_name", length = 100)
	private String nombreComun;

	@Getter
	@Setter
	@JoinColumn(name = "defa_record_type_id", referencedColumnName = "geca_id")
	@ForeignKey(name = "fk_detail_fauna_defa_record_type_id_general_catalogs_geca_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private CatalogoGeneral catalogoTipoRegistro;

	@Getter
	@Setter
	@Column(name = "defa_record_type_id", insertable = false, updatable = false)
	private Integer idTipoRegistro;

	@Getter
	@Setter
	@JoinColumn(name = "defa_vegetation_type_id", referencedColumnName = "geca_id")
	@ForeignKey(name = "fk_detail_fauna_defa_vegetation_type_id_general_catalogs_geca_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private CatalogoGeneral catalogoTipoVegetacion;

	@Getter
	@Setter
	@Column(name = "defa_vegetation_type_id", insertable = false, updatable = false)
	private Integer idTipoVegetacion;

	@Getter
	@Setter
	@JoinColumn(name = "defa_weather_conditions_id", referencedColumnName = "geca_id")
	@ForeignKey(name = "fk_detail_fauna_defa_weather_conditions_id_general_catalogs_gec")
	@ManyToOne(fetch = FetchType.LAZY)
	private CatalogoGeneral catalogoCondicionesClimaticas;

	@Getter
	@Setter
	@Column(name = "defa_weather_conditions_id", insertable = false, updatable = false)
	private Integer idCondicionesClimaticas;

	@Getter
	@Setter
	@JoinColumn(name = "defa_vertical_distribution_species_id", referencedColumnName = "geca_id")
	@ForeignKey(name = "fk_detail_fauna_defa_vertical_distribution_species_id_general_c")
	@ManyToOne(fetch = FetchType.LAZY)
	private CatalogoGeneral catalogoDistribucionEspecies;

	@Getter
	@Setter
	@Column(name = "defa_vertical_distribution_species_id", insertable = false, updatable = false)
	private Integer idDistribucionVerticalEspecie;

	@Getter
	@Setter
	@JoinColumn(name = "defa_social_behavior_id", referencedColumnName = "geca_id")
	@ForeignKey(name = "fk_detail_fauna_defa_social_behavior_id_general_catalogs_geca_i")
	@ManyToOne(fetch = FetchType.LAZY)
	private CatalogoGeneral catalogoComportamiento;

	@Getter
	@Setter
	@Column(name = "defa_social_behavior_id", insertable = false, updatable = false)
	private Integer idComportamientoSocial;

	@Getter
	@Setter
	@JoinColumn(name = "defa_food_guild_id", referencedColumnName = "geca_id")
	@ForeignKey(name = "fk_detail_fauna_defa_food_guild_id_general_catalogs_geca_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private CatalogoGeneral catalogoGremioAlimenticio;

	@Getter
	@Setter
	@Column(name = "defa_food_guild_id", insertable = false, updatable = false)
	private Integer idGremioAlimenticio;

	@Getter
	@Setter
	@JoinColumn(name = "defa_activity_pattern_id", referencedColumnName = "geca_id")
	@ForeignKey(name = "fk_detail_fauna_defa_activity_pattern_id_general_catalogs_geca_")
	@ManyToOne(fetch = FetchType.LAZY)
	private CatalogoGeneral catalogoPatronActividad;

	@Getter
	@Setter
	@Column(name = "defa_activity_pattern_id", insertable = false, updatable = false)
	private Integer idPatronActividad;

	@Getter
	@Setter
	@JoinColumn(name = "defa_moon_phase_id", referencedColumnName = "geca_id")
	@ForeignKey(name = "fk_detail_fauna_defa_moon_phase_id_general_catalogs_geca_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private CatalogoGeneral catalogoFaseLunar;

	@Getter
	@Setter
	@Column(name = "defa_moon_phase_id", insertable = false, updatable = false)
	private Integer idFaseLunar;

	@Getter
	@Setter
	@JoinColumn(name = "defa_sensitivity_id", referencedColumnName = "geca_id")
	@ForeignKey(name = "fk_detail_fauna_defa_sensitivity_id_general_catalogs_geca_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private CatalogoGeneral catalogoSensibilidad;

	@Getter
	@Setter
	@Column(name = "defa_sensitivity_id", insertable = false, updatable = false)
	private Integer idSensibilidad;

	@Getter
	@Setter
	@Column(name = "defa_sensitivity_criteria", length = 100)
	private String criterioSencibilidad;

	@Getter
	@Setter
	@JoinColumn(name = "defa_migratory_species_id", referencedColumnName = "geca_id")
	@ForeignKey(name = "fk_detail_fauna_defa_migratory_species_id_general_catalogs_geca")
	@ManyToOne(fetch = FetchType.LAZY)
	private CatalogoGeneral catalogoEspeciesMigratorias;

	@Getter
	@Setter
	@Column(name = "defa_migratory_species_id", insertable = false, updatable = false)
	private Integer idEspeciesMigratorias;

	@Getter
	@Setter
	@JoinColumn(name = "defa_bioindicator_species_id", referencedColumnName = "geca_id")
	@ForeignKey(name = "fk_detail_defa_bioindicator_species_id_general_catalogs_geca_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private CatalogoGeneral catalogoEspeciesBioindicadoras;

	@Getter
	@Setter
	@Column(name = "defa_bioindicator_species_id", insertable = false, updatable = false)
	private Integer idEspeciesBioindicadoras;

	@Getter
	@Setter
	@Column(name = "defa_criterion", length = 100)
	private String criterio;

	@Getter
	@Setter
	@JoinColumn(name = "defa_use_id", referencedColumnName = "geca_id")
	@ForeignKey(name = "fk_detail_fauna_defa_use_id_general_catalogs_geca_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private CatalogoGeneral catalogoUso;

	@Getter
	@Setter
	@Column(name = "defa_use_id", insertable = false, updatable = false)
	private Integer idUso;

	@Getter
	@Setter
	@JoinColumn(name = "defa_uicn_international_id", referencedColumnName = "geca_id")
	@ForeignKey(name = "fk_detail_fauna_defa_uicn_international_id_general_catalogs_gec")
	@ManyToOne(fetch = FetchType.LAZY)
	private CatalogoGeneral catalogoUicnInternacional;

	@Getter
	@Setter
	@Column(name = "defa_uicn_international_id", insertable = false, updatable = false)
	private Integer idUicnInternacional;

	@Getter
	@Setter
	@JoinColumn(name = "defa_cites_id", referencedColumnName = "geca_id")
	@ForeignKey(name = "fk_detail_fauna_defa_cites_id_general_catalogs_geca_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private CatalogoGeneral catalogoCites;

	@Getter
	@Setter
	@Column(name = "defa_cites_id", insertable = false, updatable = false)
	private Integer idCites;

	@Getter
	@Setter
	@JoinColumn(name = "defa_red_book_ecuador_id", referencedColumnName = "geca_id")
	@ForeignKey(name = "fk_detail_fauna_defa_red_book_ecuador_id_general_catalogs_geca_")
	@ManyToOne(fetch = FetchType.LAZY)
	private CatalogoGeneral catalogoLibroRojoEcuador;

	@Getter
	@Setter
	@Column(name = "defa_red_book_ecuador_id", insertable = false, updatable = false)
	private Integer idLibroRojoEcuador;

	@Getter
	@Setter
	@JoinColumn(name = "defa_incidental_collections_id", referencedColumnName = "geca_id")
	@ForeignKey(name = "fk_detail_fauna_defa_incidental_collections_id_general_catalogs")
	@ManyToOne(fetch = FetchType.LAZY)
	private CatalogoGeneral catalogoColectasIncidentales;

	@Getter
	@Setter
	@Column(name = "defa_incidental_collections_id", insertable = false, updatable = false)
	private Integer idColectasIncidentales;

	@Getter
	@Setter
	@JoinColumn(name = "defa_reproductive_mode_id", referencedColumnName = "geca_id")
	@ForeignKey(name = "fk_detail_fauna_defa_reproductive_mode_id_general_catalogs")
	@ManyToOne(fetch = FetchType.LAZY)
	private CatalogoGeneral catalogoModoReproductivo;

	@Getter
	@Setter
	@Column(name = "defa_reproductive_mode_id", insertable = false, updatable = false)
	private Integer idModoReproductivo;

	@Getter
	@Setter
	@JoinColumn(name = "defa_water_systems_id", referencedColumnName = "geca_id")
	@ForeignKey(name = "fk_detail_fauna_defa_water_systems_id_general_catalogs")
	@ManyToOne(fetch = FetchType.LAZY)
	private CatalogoGeneral catalogoSistemasHidrograficos;

	@Getter
	@Setter
	@Column(name = "defa_water_systems_id", insertable = false, updatable = false)
	private Integer idSistemasHidrograficos;

	@Getter
	@Setter
	@JoinColumn(name = "defa_distribution_water_column_id", referencedColumnName = "geca_id")
	@ForeignKey(name = "fk_detail_fauna_defa_distribution_water_column_id_general_catalogs")
	@ManyToOne(fetch = FetchType.LAZY)
	private CatalogoGeneral catalogoDistribucionColumnaAgua;

	@Getter
	@Setter
	@Column(name = "defa_distribution_water_column_id", insertable = false, updatable = false)
	private Integer idDistribucionColumnaAgua;

	@Getter
	@Setter
	@JoinColumn(name = "defa_water_quality_bioindicator_species_id", referencedColumnName = "geca_id")
	@ForeignKey(name = "fk_detail_fauna_defa_water_quality_bioindicator_species_id_general_catalogs")
	@ManyToOne(fetch = FetchType.LAZY)
	private CatalogoGeneral catalogoEspecieBioindicadoraCalidadAgua;

	@Getter
	@Setter
	@Column(name = "defa_water_quality_bioindicator_species_id", insertable = false, updatable = false)
	private Integer idEspecieBioindicadoraCalidadAgua;

	@Getter
	@Setter
	@Column(name = "defa_tenure_collection_center", length = 100)
	private String coleccionCentroTenencia;

	@Getter
	@Setter
	@Column(name = "defa_tenure_approved_center", length = 100)
	private String centroTeneciaAutorizado;

	@Getter
	@Setter
	@JoinColumn(name = "sapo_id", referencedColumnName = "sapo_id")
	@ForeignKey(name = "fk_detail_fauna_sapo_id_sampling_points_sapo_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private PuntosMuestreo puntosMuestreo;

	@Getter
	@Setter
	@Column(name = "sapo_id", insertable = false, updatable = false)
	private Integer idPuntosMuestreo;

	@Getter
	@Setter
	@Column(name = "defa_description_other_trails_type_register", length = 250)
	private String descOtrosRastrosTipoRegistro;

	@Getter
	@Setter
	@Column(name = "defa_description_other_vertical_distribution_species", length = 250)
	private String descOtrosDistribucionVerticalEspecie;

	@Getter
	@Setter
	@Column(name = "defa_description_other_social_behavior", length = 250)
	private String descOtrosComportamientoSocial;

	@Getter
	@Setter
	@Column(name = "defa_description_other_guild_alimentary", length = 250)
	private String descOtrosGremioAlimenticio;

	@Getter
	@Setter
	@JoinColumn(name = "defa_endemic_species_id", referencedColumnName = "geca_id")
	@ForeignKey(name = "fk_detail_fauna_defa_endemic_species_id_general_catalogs")
	@ManyToOne(fetch = FetchType.LAZY)
	private CatalogoGeneral catalogoEspecieEndemica;

	@Getter
	@Setter
	@Column(name = "defa_endemic_species_id", insertable = false, updatable = false)
	private Integer idEspecieEndemica;

	@Getter
	@Setter
	@JoinColumn(name = "defa_time_year_id", referencedColumnName = "geca_id")
	@ForeignKey(name = "fk_detail_fauna_defa_time_year_id_general_catalogs")
	@ManyToOne(fetch = FetchType.LAZY)
	private CatalogoGeneral catalogoEpocaDelAnio;

	@Getter
	@Setter
	@Column(name = "defa_time_year_id", insertable = false, updatable = false)
	private Integer idEpocaDelAnio;

	@Getter
	@Setter
	@JoinColumn(name = "defa_trophic_groups_id", referencedColumnName = "geca_id")
	@ForeignKey(name = "fk_detail_fauna_defa_trophic_groups_id_general_catalogs")
	@ManyToOne(fetch = FetchType.LAZY)
	private CatalogoGeneral catalogoGruposTroficos;

	@Getter
	@Setter
	@Column(name = "defa_trophic_groups_id", insertable = false, updatable = false)
	private Integer idGruposTroficos;

	@Getter
	@Setter
	@JoinColumn(name = "defa_eating_habits_id", referencedColumnName = "geca_id")
	@ForeignKey(name = "fk_detail_fauna_defa_eating_habits_id_general_catalogs")
	@ManyToOne(fetch = FetchType.LAZY)
	private CatalogoGeneral catalogoHabitoAlimenticio;

	@Getter
	@Setter
	@Column(name = "defa_eating_habits_id", insertable = false, updatable = false)
	private Integer idHabitoAlimenticio;

	@Getter
	@Setter
	@Column(name = "defa_zone_endenism", length = 250)
	private String zonaEndenismo;

	@Getter
	@Setter
	@Column(name = "defa_description_photo", length = 250)
	private String descripcionFoto;
	@Getter
	@Setter
	@Transient
	private String idOtrosRastrosTipoRegistro;
	@Getter
	@Setter
	@Transient
	private String idOtrosDistribucionVerticalEspecie;
	@Getter
	@Setter
	@Transient
	private String idOtrosComportamientoSocial;
	@Getter
	@Setter
	@Transient
	private String idOtrosGremioAlimenticio;
	@Getter
	@Setter
	@Transient
	private String idPuntoMuestreo;
	@Getter
	@Setter
	@Transient
	private boolean editar;
	@Getter
	@Setter
	@Transient
	private int indice;
	@Getter
	@Setter
	@Transient
	private EntityAdjunto adjunto;
	@Getter
	@Setter
	@Transient
	private DetalleFaunaEspecies detalleFaunaEspecies;

	public DetalleFauna() {
		super();
	}

	public DetalleFauna(Integer id) {
		super();
		this.id = id;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (especie != null ? especie.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof DetalleFauna)) {
			return false;
		}
		DetalleFauna other = (DetalleFauna) object;
		if ((this.especie == null && other.especie != null)
				|| (this.especie != null && !this.especie.equals(other.especie))) {
			return false;
		}
		return true;
	}

}
