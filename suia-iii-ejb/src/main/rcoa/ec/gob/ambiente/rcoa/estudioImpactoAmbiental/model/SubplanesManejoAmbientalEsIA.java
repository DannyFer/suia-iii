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
@Table(name = "environmental_management_plan", schema = "coa_environmental_impact_study")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "enmp_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "enmp_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "enmp_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "enmp_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "enmp_user_update")) })
@NamedQueries({
			@NamedQuery(name=SubplanesManejoAmbientalEsIA.GET_PLANES_ACTIVOS, query="SELECT p FROM SubplanesManejoAmbientalEsIA p where p.estado = true order by orden")
		})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "enmp_status = 'TRUE'")
public class SubplanesManejoAmbientalEsIA extends EntidadAuditable {
	public static final String GET_PLANES_ACTIVOS = "ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.SubplanesManejoAmbientalEsIA.getPlanesActivos";

	private static final long serialVersionUID = 5200035293094691659L;
	  
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "enmp_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "enmp_name")
	private String nombre;

	@Getter
	@Setter
	@Column(name = "enmp_description")
	private String descripcion;

	@Getter
	@Setter
	@Column(name = "enmp_order")
	private Integer orden;

	@Getter
	@Setter
	@Column(name = "enmp_required")
	private Boolean requerido;

	@Getter
	@Setter
	@Column(name = "enmp_code")
	private String codigo;
	
}


