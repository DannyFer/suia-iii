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
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformacionProyectoEia;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.TipoComponenteParticipacion;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;


@Entity
@Table(name = "catalogue_project_phase", schema = "coa_environmental_impact_study")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "capp_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "capp_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "capp_update_date")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "capp_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "capp_update_user")) })
@NamedQueries({
			@NamedQuery(name=CatalogoFasesCoa.GET_FASES_POR_SECTOR, query="SELECT m FROM CatalogoFasesCoa m where m.estado = true and m.tipoSector.id = :tipoSectorId order by m.nombre")
		})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "capp_status = 'TRUE'")
public class CatalogoFasesCoa extends EntidadAuditable {
	public static final String GET_FASES_POR_SECTOR = "ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.CatalogoFasesCoa.getFasesPorSector";

	private static final long serialVersionUID = 5200035293094691659L;
	  
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "capp_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "capp_name", length = 255)
	private String nombre;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "sety_id")
	@ForeignKey(name = "sety_id")
	private TipoSector tipoSector;
	
	@Getter
	@Setter
	@Column(name = "capp_observation_bdd")
	private String descripcionBdd;
}


