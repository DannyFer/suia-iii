package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model;

import java.util.ArrayList;
import java.util.List;

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

import ec.gob.ambiente.rcoa.dto.EntityPmaEsIA;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;


@Entity
@Table(name = "management_plan_study", schema = "coa_environmental_impact_study")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "maps_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "maps_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "maps_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "maps_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "maps_user_update")) })
@NamedQueries({
			@NamedQuery(name="PlanManejoAmbientalEsIA.findAll", query="SELECT p FROM PlanManejoAmbientalEsIA p"),
			@NamedQuery(name=PlanManejoAmbientalEsIA.GET_POR_ESTUDIO_SUBPLAN, query="SELECT p FROM PlanManejoAmbientalEsIA p where p.idEstudio = :idEstudio and p.idSubplan = :idSubplan and p.idSubPlanPadre is null order by id desc"),
			@NamedQuery(name=PlanManejoAmbientalEsIA.GET_POR_ESTUDIO_PRINCIPALES, query="SELECT p FROM PlanManejoAmbientalEsIA p where p.idEstudio = :idEstudio and p.idSubPlanPadre is null order by id"),
			@NamedQuery(name=PlanManejoAmbientalEsIA.GET_POR_ESTUDIO, query="SELECT p FROM PlanManejoAmbientalEsIA p where p.idEstudio = :idEstudio order by id"),
			@NamedQuery(name=PlanManejoAmbientalEsIA.GET_POR_PADRE, query="SELECT d FROM PlanManejoAmbientalEsIA d where d.idSubPlanPadre = :idPadre order by id")

		})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "maps_status = 'TRUE'")
public class PlanManejoAmbientalEsIA extends EntidadAuditable {

	private static final long serialVersionUID = 5200035293094691659L;

	private static final String PAQUETE = "ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.";

	public static final String GET_POR_ESTUDIO_SUBPLAN = PAQUETE + "PlanManejoAmbientalEsIA.getPorEstudioSubplan";
	public static final String GET_POR_ESTUDIO_PRINCIPALES = PAQUETE + "PlanManejoAmbientalEsIA.getPorEstudioPrincipales";
	public static final String GET_POR_ESTUDIO = PAQUETE + "PlanManejoAmbientalEsIA.getPorEstudio";
	public static final String GET_POR_PADRE = PAQUETE + "PlanManejoAmbientalEsIA.getPorPadre";
	  
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "maps_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="prin_id")
	private Integer idEstudio;

	@Getter
	@Setter
	@Column(name="enmp_id")
	private Integer idSubplan;

	@Getter
	@Setter
	@Column(name="maps_parent_id")
	private Integer idSubPlanPadre;

	@Getter
	@Setter
	@Column(name = "maps_ingress")
	private Boolean ingresoDetalle;

	@Getter
	@Setter
	@Column(name = "maps_justification")
	private String justificacion;

	@Getter
	@Setter
	@Column(name = "maps_cost")
	private Double costoSubplan;

	@Getter
	@Setter
	@Column(name = "maps_observations")
	private Boolean tieneObservaciones;

	@Getter
	@Setter
	@Column(name = "maps_number_observation")
	private Integer numeroObservacion = 0;
	
	@Getter
	@Setter
	@Column(name = "maps_review_status")
	private Boolean registroRevisado = false;
	
	@Getter
	@Setter
	@Column(name = "maps_status_enable")
	private Boolean registroHabilitado = true;

	@Getter
    @Setter
    @Transient
    private DocumentoEstudioImpacto plantillaSubPlan;

    @Getter
    @Setter
    @Transient
    private PlanManejoEsIAObservaciones planManejoObservacion;

    @Getter
    @Setter
    @Transient
    private List<DocumentoEstudioImpacto> listaAnexosSubPlan = new ArrayList<>();
	
	@Getter
    @Setter
    @Transient
    private List<EntityPmaEsIA> listaSubsanacionesSubPlan;

    @Getter
    @Setter
    @Transient
    private List<ObservacionesEsIA> listaObservacionesPendientes = new ArrayList<>();

    public String getTituloObservacion() {
    	String nroObservacion = this.numeroObservacion.toString();
    	switch (this.numeroObservacion) {
		case 1:
			nroObservacion = "Primera";
			break;
		case 2:
			nroObservacion = "Segunda";
			break;
		case 3:
			nroObservacion = "Tercera";
			break;
		case 4:
			nroObservacion = "Tercera";
			break;
		default:
			break;
		}

		return nroObservacion + " observación";
	}

}


