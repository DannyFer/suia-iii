package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.domain.Consultor;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;


@Entity
@Table(name = "project_information", schema = "coa_environmental_impact_study")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "prin_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "prin_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "prin_update_date")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "prin_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "prin_update_user")) })
@NamedQueries({
	@NamedQuery(name=InformacionProyectoEia.GET_INFORMACIONPROYECTOEIA_POR_PROYECTO, query="SELECT m FROM InformacionProyectoEia m where m.estado = true and m.proyectoLicenciaCoa.id = :proyectoId")
})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prin_status = 'TRUE'")
public class InformacionProyectoEia extends EntidadAuditable {
	
	public static final String GET_INFORMACIONPROYECTOEIA_POR_PROYECTO = "ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformacionProyectoEia.getInformacionProyectoEIAPorProyecto";
	
	private static final long serialVersionUID = 5200035293094691659L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "prin_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "prin_project_code_id", length = 255)
	private String codigo;
	
	@Getter
	@Setter
	@Column(name = "prin_requires_multi_disciplinary_team")
	private Boolean equipoMultidisciplinario;
	
	@Getter
	@Setter
	@Column(name = "prin_requires_inspection")
	private Boolean inspeccion;
	
	@Getter
	@Setter
	@Column(name = "prin_justification")
	private String justificacionInspeccion;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "prco_id")
	@ForeignKey(name = "prco_id")
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Getter
	@Setter
	@OneToOne
	@JoinColumn(name = "cons_id")
	@ForeignKey(name = "cons_id")
	private Consultor consultor;
	
	@Getter
	@Setter
	@Column(name = "prin_observation_bdd")
	private String descripcionBdd;
	
	@Getter
	@Setter
	@Column(name = "prin_contract_value")
	private Double valorContrato;
	
	@Getter
	@Setter
	@Column(name = "prin_payment_value")
	private Double valorAPagar;
	
	@Getter
	@Setter
	@Column(name = "prin_number_facilitators")
	private Integer numeroFacilitadores;
	
	@Getter
	@Setter
	@Column(name = "prin_forest_inventory_payment")
	private Double valorPorInventarioForestal;
	
	@Getter
	@Setter
	@Column(name = "prin_study_send_date")
	private Date fechaEnvioEstudio;
	
	@Getter
	@Setter
	@Column(name = "prin_ending_date_study")
	private Date fechaFinEstudio; //fecha que finaliza el proceso del estudio
	
	@Getter
	@Setter
	@Column(name = "prin_final_result_eia_cata_id")
	private Integer resultadoFinalEia; //resultado final del estudio
	
	@Getter
	@Setter
	@Column(name = "prin_study_second_send_date")
	private Date fechaSegundoEnvio;
	
	@Getter
	@Setter
	@Column(name = "prin_study_third_send_date")
	private Date fechaTercerEnvio;
	
	@Getter
	@Setter
	@Column(name = "prin_shrimp_concession_beach")
	private String concesionCamaroneraPlaya;
	
	@Getter
	@Setter
	@Column(name = "prin_shrimp_concession_high")
	private String concesionCamaroneraAlta;
}
