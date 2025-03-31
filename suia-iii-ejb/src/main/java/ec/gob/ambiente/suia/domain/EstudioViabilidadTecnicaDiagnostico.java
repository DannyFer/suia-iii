package ec.gob.ambiente.suia.domain;

import java.util.List;

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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

@NamedQueries({ @NamedQuery(name = EstudioViabilidadTecnicaDiagnostico.OBTENER_ESTUDIO_VIABILIDAD_POR_PROCESO, query = "select e from EstudioViabilidadTecnicaDiagnostico e where e.estudioViabilidadTecnica.id = :p_procesoId"),
				@NamedQuery(name = EstudioViabilidadTecnicaDiagnostico.OBTENER_ESTUDIO_VIABILIDAD_POR_ID, query = "select e from EstudioViabilidadTecnicaDiagnostico e where e.id = :Id")	
})
@Entity
@Table(name = "technical_viability_study_diagnostic", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "tvsd_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tvsd_status = 'TRUE'")
public class EstudioViabilidadTecnicaDiagnostico extends EntidadBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = -115591172646358416L;
	/**
	 * 
	 */

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String OBTENER_ESTUDIO_VIABILIDAD_POR_PROCESO = PAQUETE + "EstudioViabilidadTecnica.obtenerEstudioViabilidadPorProceso";
	public static final String OBTENER_ESTUDIO_VIABILIDAD_POR_ID = PAQUETE + "EstudioViabilidadTecnicaDiagnostico.obtenerEstudioViabilidadTecnicaDiagnosticoPorId";
	
	@Id
	@SequenceGenerator(name = "technical_viability_study_diag_id_generator", initialValue = 1, sequenceName = "seq_tvsd_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "technical_viability_study_diag_id_generator")
	@Column(name = "tvsd_id")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@Column(name = "tvsd_temporal_storage")
	private String almacenamientoTemporal;

	@Getter
	@Setter
	@Column(name = "tvsd_public_sweep")
	private String barridoPublico;

	@Getter
	@Setter
	@Column(name = "tvsd_transportation_pickup")
	private String transporteRecogida;

	@Getter
	@Setter
	@Column(name = "tvsd_processing_use")
	private String procesoUso;

	@Getter
	@Setter
	@Column(name = "tvsd_final_disposition")
	private String dispociocionFinal;

	@Getter
	@Setter
	@Column(name = "tvsd_total_waste_dangerous")
	private Double residuosPeligrosos;

	@Getter
	@Setter
	@Column(name = "tvsd_dump_description_")
	private String descripcionDesperdicio;

	@Getter
	@Setter
	@Column(name = "tvsd_influence_area")
	private String areaInfluencia;

	@Getter
	@Setter
	@Column(name = "tvsd_demographic_analysis")
	private String analisisDemografico;

	@Getter
	@Setter
	@Column(name = "tvsd_topography")
	private String topografia;

	@Getter
	@Setter
	@Column(name = "tvsd_total_area")
	private Double areaTotal;

	@Getter
	@Setter
	@Column(name = "tvsd_garbage_height")
	private Double alturaBasura;

	@Getter
	@Setter
	@Column(name = "tvsd_garbage_volumen")
	private Double volumenBasura;

	@Getter
	@Setter
	@Column(name = "tvsd_geology")
	private String geologia;

	@Getter
	@Setter
	@Column(name = "tvsd_geotechnical")
	private String geotermica;

	@Getter
	@Setter
	@Column(name = "tvsd_hydrogeological_analysis")
	private String analisisHidrogeologico;

	@Getter
	@Setter
	@Column(name = "tvsd_structure_evaluation")
	private String evaluacionEstructura;

	@Getter
	@Setter
	@Column(name = "tvsd_risk_analysis")
	private String analisisRiesgo;

	@Getter
	@Setter
	@Column(name = "tvsd_legal_gruoun_situation")
	private String situacionLegalSuelo;

	@Getter
	@Setter
	@Column(name = "tvsd_social_recycler_description")
	private String descripcionRecicladoSocial;

	@Getter
	@Setter
	@Column(name = "tvsd_optimal_alternative")
	private String alternativasOptimas;

	@Getter
	@Setter
	@Column(name = "tvsd_analysis_alternative")
	private String analisisAlternativas;

	@Getter
	@Setter
	@Column(name = "tvsd_erosion_sedimentation_description")
	private String erosionSedimentacionDescripcion;

	@Getter
	@Setter
	@Column(name = "tvsd_required_processing_system")
	private Boolean procesoRequeridoSistema;

	@Getter
	@Setter
	@Column(name = "tvsd_processing_units_description")
	private String descripcionUnidadProceso;

	@Getter
	@Setter
	@Column(name = "tvsd_smokestack_number")
	private Integer numeroChimeneas;

	@Getter
	@Setter
	@Column(name = "tvsd_smokestack_spreading")
	private Integer difusionChimeneas;

	@Getter
	@Setter
	@Column(name = "tvsd_biogas_use")
	private Boolean usoBiogas;

	@Getter
	@Setter
	@Column(name = "tvsd_technical_stability_closure")
	private String cierreEstabilidadTecnica;

	@Getter
	@Setter
	@Column(name = "tvsd_final_cover_layer_desing")
	private String capaDisenoCubiertaFinal;

	@Getter
	@Setter
	@Column(name = "tvsd_final_salary_use")
	private String usoSalarioFinal;

	@Getter
	@Setter
	@Column(name = "tvsd_delivery_planes")
	private Boolean planDesarrollo;

	@Getter
	@Setter
	@Column(name = "tvsd_surface_water")
	private String aguaSuperficial;

	@ManyToOne
	@JoinColumn(name = "tvst_id")
	@ForeignKey(name = "fk_technical_viability_study_diagnostictvst_id_technical_viabil")
	@Getter
	@Setter
	private EstudioViabilidadTecnica estudioViabilidadTecnica;

	@OneToMany(mappedBy = "estudioViabilidadTecnicaDiagnostico")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tvdm_status = 'TRUE'")
	@Getter
	@Setter
	private List<ViabilidadTecnicaMaterialesDiagnostico> viabilidadTecnicaMaterialesDiagnostico;

	@Getter 
	@Setter
	@Column(name = "tvsd_urban_roads")
	private String viasUrbanas;
	
	// public EstudioViabilidadTecnicaDiagnostico(Integer id) {
	// this.id = id;
	// }
}
