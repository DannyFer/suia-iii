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


@NamedQueries({
@NamedQuery(name = GestionIntegral2.GET_ALL, query = "select e from GestionIntegral2 e"),	
@NamedQuery(name = GestionIntegral2.OBTENER_ESTUDIO_VIABILIDAD_POR_PROCESO, 
query = "select e from GestionIntegral2 e where e.estudioViabilidadTecnica.id = :p_procesoId") })

@Entity
@Table(name = "integrated_management", schema = "suia_iii")
@AttributeOverrides({@AttributeOverride(name = "estado", column = @Column(name = "inma_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "inma_status = 'TRUE'")

public class GestionIntegral2 extends EntidadBase {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String GET_ALL 				 =  PAQUETE + "GestionIntegral2.getAll";
	public static final String OBTENER_ESTUDIO_VIABILIDAD_POR_PROCESO = PAQUETE + "GestionIntegral2.obtenerEstudioViabilidadPorProceso";


	
	@Id
	@SequenceGenerator(name = "integrated_record_generator", initialValue = 1, sequenceName = "seq_integrated_mana_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "integrated_record_generator")
	@Column(name = "im_id")
	@Getter 
	@Setter
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "im_temporal_storage", length = 500)
	private String almacenamiento_temporal;	
	
	@Getter
	@Setter
	@Column(name = "im_public_sweep", length = 500)
	private String barrido_publico;	
	
	@Getter
	@Setter
	@Column(name = "im_transportation_pickup", length = 500)
	private String recoleccion_transporte;	
	
	@Getter
	@Setter
	@Column(name = "im_treatment_utilization", length = 500)
	private String tratamiento_aprovechamiento;	
	
	@Getter
	@Setter
	@Column(name = "im_final_diagnosis_arrangement", length = 500)
	private String  diagnostico_disposicion_final;	
	
	@Getter
	@Setter
	@Column(name = "im_topographical_works", length = 500)
	private String trabajos_topograficos;	
	
	@Getter
	@Setter
	@Column(name = "im_geological_survey", length = 500)
	private String estudio_geologico;	
	
	@Getter
	@Setter
	@Column(name = "im_hydrological_studies", length = 500)
	private String estudios_hidrologicos;	
	
	@Getter
	@Setter
	@Column(name = "im_meteorological_studies", length = 500)
	private String estudios_meteorologicos;	
	
	@Getter
	@Setter
	@Column(name = "im_edaphological_studies", length = 500)
	private String  estudios_edafologicos;	
	
	@Getter
	@Setter
	@Column(name = "im_summary_plan_municipal_gov", length = 500)
	private String  resumen_plan_gob_municipal;	
	
	@Getter
	@Setter
	@Column(name = "im_administrative_structure", length = 500)
	private String  estructura_administrativa;
	
	@Getter
	@Setter
	@Column(name = "im_financial_situation", length = 500)
	private String  situacion_financiera;
	
	@Getter
	@Setter
	@Column(name = "im_agency_service_sanitary_waste", length = 500)
	private String  organismo_serv_desechos_sanitarios;
	
	@Getter
	@Setter
	@Column(name = "im_environmental_education_plans_cost", length = 500)
	private String  educacion_ambiental_planes_costo;
		
	@Getter
	@Setter
	@Column(name = "im_socio_economic_survey_report", length = 500)
	private String  informe_encuesta_socio_economica;
	
    public GestionIntegral2(Integer id) {
        this.id = id;
    }
    
    public GestionIntegral2() {
    	    
    }

    @ManyToOne
    @JoinColumn(name = "tvst_id")
    @ForeignKey(name = "fk_technical_viability_study_integrated_management_id")
    @Getter
    @Setter
    private EstudioViabilidadTecnica estudioViabilidadTecnica;

    @OneToMany(mappedBy = "gestionIntegral2")
  	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tvdm2_status = 'TRUE'")
  	@Getter
  	@Setter
  	private List<ViabilidadTecnicaMaterialesDiagnostico_2> viabilidadTecnicaMaterialesDiagnostico_2;

  	
}


