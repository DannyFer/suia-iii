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
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformacionProyectoEia;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;


@Entity
@Table(name = "project_phase", schema = "coa_environmental_impact_study")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "ppha_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "ppha_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "ppha_update_date")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "ppha_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "ppha_update_user")) })
@NamedQueries({
			@NamedQuery(name=ProyectoFasesEiaCoa.GET_FASES_POR_PROYECTO, query="SELECT m FROM ProyectoFasesEiaCoa m where m.estado = true and m.informacionProyectoEia.id = :proyectoEIAId")
		})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "ppha_status = 'TRUE'")
public class ProyectoFasesEiaCoa extends EntidadAuditable {
	public static final String GET_FASES_POR_PROYECTO = "ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.ProyectoFasesEiaCoa.getFasesPorProyecto";

	private static final long serialVersionUID = 5200035293094691659L;
	  
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ppha_is")
	private Integer id;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "capp_id")
	@ForeignKey(name = "capp_id")
	private CatalogoFasesCoa faseSector;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "prin_id")
	@ForeignKey(name = "prin_id")
	private InformacionProyectoEia informacionProyectoEia;
	
	@Getter
	@Setter
	@Column(name = "ppha_observation_bdd")
	private String descripcionBdd;
}


