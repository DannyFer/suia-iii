package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;


@Entity
@Table(name = "management_plan_study_detail", schema = "coa_environmental_impact_study")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "mpsd_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "mpsd_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "mpsd_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "mpsd_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "mpsd_user_update")) })
@NamedQueries({
			@NamedQuery(name="DetallePlanManejoEsIA.findAll", query="SELECT d FROM DetallePlanManejoEsIA d"),
			@NamedQuery(name=DetallePlanManejoEsIA.GET_POR_PROGRAMA, query="SELECT d FROM DetallePlanManejoEsIA d where d.idPrograma = :idPrograma order by orden")
		})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "mpsd_status = 'TRUE'")
public class DetallePlanManejoEsIA extends EntidadAuditable {

	private static final long serialVersionUID = 5200035293094691659L;

	private static final String PAQUETE = "ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.";

	public static final String GET_POR_PROGRAMA = PAQUETE + "DetallePlanManejoEsIA.getPorPrograma";
	  
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "mpsd_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="mpsp_id")
	private Integer idPrograma;
	
	@Getter
	@Setter
	@Column(name = "mpsd_code")
	private String codigo;

	@Getter
	@Setter
	@Column(name = "mpsd_project_stage")
	private String etapa;

	@Getter
	@Setter
	@Column(name = "mpsd_processes_activity")
	private String actividad;

	@Getter
	@Setter
	@Column(name = "mpsd_environmental_aspect")
	private String aspectoAmbiental;

	@Getter
	@Setter
	@Column(name = "mpsd_environmental_impact")
	private String impacto;

	@Getter
	@Setter
	@Column(name = "mpsd_measures")
	private String medidas;

	@Getter
	@Setter
	@Column(name = "mpsd_indicators")
	private String indicadores;

	@Getter
	@Setter
	@Column(name = "mpsd_media_verification")
	private String mediosVerificacion;

	@Getter
	@Setter
	@Column(name = "mpsd_term")
	private String plazo;

	@Getter
	@Setter
	@Column(name = "mpsd_frequency_periodicity")
	private String frecuencia;

	@Getter
	@Setter
	@Column(name = "mpsd_estimated_cost")
	private Double costo;

	@Getter
	@Setter
	@Column(name = "mpsd_order")
	private Integer orden;
	
	@Getter
	@Setter
	@Transient
	private String nombrePrograma;
	
	@Getter
	@Setter
	@Transient
	private ProgramaPlanManejoEsIA programa;

}


