package ec.gob.ambiente.rcoa.viabilidadAmbiental.model;

import java.io.Serializable;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

/**
 * The persistent class for the feasibility_report_forest database table.
 * 
 */
@Entity
@Table(name="feasibility_report_forest", schema = "coa_viability")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "fere_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "fere_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "fere_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "fere_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "fere_user_update")) })

@NamedQueries({
@NamedQuery(name="InformeFactibilidadForestal.findAll", query="SELECT i FROM InformeFactibilidadForestal i"),
@NamedQuery(name=InformeFactibilidadForestal.GET_INFORME_POR_VIABILIDAD, query="SELECT i FROM InformeFactibilidadForestal i where i.idViabilidad = :idViabilidad and i.estado = true order by id")
})
public class InformeFactibilidadForestal extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAQUETE = "ec.gob.ambiente.rcoa.viabilidadAmbiental.model.";
	
	public static final String GET_INFORME_POR_VIABILIDAD = PAQUETE + "InformeFactibilidadForestal.getInformePorViabilidad";

	public static Integer caracterizacionCualitativa = 1;
	public static Integer caracterizacionCuantitativa = 2;
	public static Integer recoleccionMuestreo = 1;
	public static Integer recoleccionCenso = 2;


	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "FEASIBILITY_REPORT_ID_GENERATOR", sequenceName = "feasibility_report_fere_id_seq", schema = "coa_viability", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FEASIBILITY_REPORT_ID_GENERATOR")
	@Column(name="fere_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "prvi_id")
	private Integer idViabilidad;

	@Getter
	@Setter
	@Column(name="fere_court_box")
	private String casilleroJudicial;

	@Getter
	@Setter
	@Column(name="fere_phase")
	private String faseProyecto;

	@Getter
	@Setter
	@Column(name="fere_background")
	private String antecedentes;

	@Getter
	@Setter
	@Column(name="fere_legal_framework")
	private String marcoLegal;

	@Getter
	@Setter
	@Column(name="fere_objective")
	private String objetivo;

	@Getter
	@Setter
	@Column(name="fere_description")
	private String descripcionProyecto;

	@Getter
	@Setter
	@Column(name="fere_vegetable_cover")
	private String descripcionCoberturaVegetal;

	@Getter
	@Setter
	@Column(name="fere_ecosystems")
	private String descripcionEcosistemas;

	@Getter
	@Setter
	@Column(name="fere_vegetable_cover_characterization")
	private Integer caracterizacionCobertura; //1 = Cualitativa, 2 = Cuantitativa

	@Getter
	@Setter
	@Column(name="fere_qualitative_methodology")
	private String descripcionMetodologia;

	@Getter
	@Setter
	@Column(name="fere_phase_field")
	private String faseCampo;

	@Getter
	@Setter
	@Column(name="fere_phase_office")
	private String faseOficina;

	@Getter
	@Setter
	@Column(name="fere_collect_data")
	private Integer recoleccionDatos; //.1 = Muestreo, 2 = Censo

	@Getter
	@Setter
	@Column(name="fere_result_basal_volume")
	private String interpretacionAreaBasal;

	@Getter
	@Setter
	@Column(name="fere_value_importance")
	private String interpretacionValorImportancia;

	@Getter
	@Setter
	@Column(name="fere_species_importance")
	private String interpretacionEspeciesImportancia;

	@Getter
	@Setter
	@Column(name="fere_total_sample_unit")
	private Double areaSitioMuestral;

	@Getter
	@Setter
	@Column(name="fere_conclusions")
	private String conclusiones;

	@Getter
	@Setter
	@Column(name="fere_recommendations")
	private String recomendaciones;

	@Getter
	@Setter
	@Column(name="fere_bibliography")
	private String bibliografia;

	@Getter
	@Setter
	@Column(name="fere_annexes")
	private Boolean cargaAnexos;

	@Getter
	@Setter
	@Column(name="fere_interpretation_sha_simp")
	private String interpretacionShanonSimpson;

	@Getter
	@Setter
	@Transient
	private String nombreReporte;
	
	@Getter
	@Setter
	@Transient
	private byte[] archivoInforme;
	
	@Getter
	@Setter
	@Transient
	private String informePath;

}