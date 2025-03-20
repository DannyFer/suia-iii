package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformacionProyectoEia;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.TipoComponenteParticipacion;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;


@Entity
@Table(name = "multidisciplinary_team_assignment", schema = "coa_environmental_impact_study")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "mtea_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "mtea_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "mtea_update_date")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "mtea_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "mtea_update_user")) })
@NamedQueries({
			@NamedQuery(name=EquipoApoyoProyecto.GET_EQUIPOAPOYO_PROYECTO, query="SELECT m FROM EquipoApoyoProyecto m where m.estado = true and m.informacionProyectoEia.id = :proyectoEIAId")
		})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "mtea_status = 'TRUE'")
public class EquipoApoyoProyecto extends EntidadAuditable {
	public static final String GET_EQUIPOAPOYO_PROYECTO = "ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.EquipoApoyoProyecto.getEquipoApoyoProyecto";

	private static final long serialVersionUID = 5200035293094691659L;
	  
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "mtea_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "mtea_social_technical")
	private boolean tecnicoSocial;
	
	@Getter
	@Setter
	@Column(name = "mtea_cartographer_technical")
	private boolean tecnicoCartografo;
	
	@Getter
	@Setter
	@Column(name = "mtea_biotic_technical")
	private boolean tecnicoBiotico;
	
	@Getter
	@Setter
	@Column(name = "mtea_biodiversity_technical_intersection_snap")
	private boolean tecnicoBiodiversidad;
	
	@Getter
	@Setter
	@Column(name = "mtea_forest_technical_intersction_pfn")
	private boolean tecnicoForestal;
	
	@Getter
	@Setter
	@Column(name = "mtea_forest_technical_removing_native_plant_cover")
	private boolean tecnicoForestalRemocion;
	
	@Getter
	@Setter
	@Column(name = "mtea_number_technicians")
	private Integer numeroTecnicosApoyo;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "prin_id")
	@ForeignKey(name = "prin_id")
	private InformacionProyectoEia informacionProyectoEia;
	
	@Getter
	@Setter
	@Column(name = "mtea_observation_bdd")
	private String descripcionBdd;
}


