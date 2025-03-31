package ec.gob.ambiente.suia.domain;

import java.io.Serializable;
import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the scdr_projects_activities_impact database table.
 * 
 */
@Entity
@Table(name = "scdr_projects_activities_impact", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "prai_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "prai_creation_date")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "prai_update_date")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "prai_creator_user")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "prai_update_user"))  })
@NamedQueries({ @NamedQuery(name = ActividadesImpactoProyecto.LISTAR_ACT_PORPROYECTO, query = "SELECT a FROM ActividadesImpactoProyecto a WHERE a.estado = true and a.perforacionExplorativa = :proyectoId "),
				@NamedQuery(name = ActividadesImpactoProyecto.IMPACTO_PORPROYECTOPORID, query = "SELECT a FROM ActividadesImpactoProyecto a WHERE a.estado = true and a.perforacionExplorativa = :proyectoId and a.id = :codigoId ")
	 })

public class ActividadesImpactoProyecto extends EntidadAuditable  {

	private static final long serialVersionUID = 8481235137819437968L;
	private static final String PAQUETE_CLASE = "ec.gob.ambiente.suia.domain.ActividadesImpactoProyecto.";
	public static final String LISTAR_ACT_PORPROYECTO = PAQUETE_CLASE + "obtenerPorProyecto";
	public static final String IMPACTO_PORPROYECTOPORID = PAQUETE_CLASE + "obtenerImpactoPorProyectoPorId";
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@Column(name = "prai_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "prai_tools")
	private String herramientas;

	@Getter
	@Setter
	@Column(name = "prai_impacts_environmental")
	private String impacto;

	@Getter
	@Setter
	@Column(name = "prai_impacts_description")
	private String descripcionImpacto;

	@Getter
	@Setter
	@Column(name = "prai_water")
	private boolean agua;

	@Getter
	@Setter
	@Column(name = "prai_air")
	private boolean aire;

	@Getter
	@Setter
	@Column(name = "prai_ground")
	private boolean suelo;

	@Getter
	@Setter
	@Column(name = "prai_biotic")
	private boolean biotico;

	@Getter
	@Setter
	@Column(name = "prai_landscape")
	private boolean paisaje;

	@Getter
	@Setter
	@Column(name = "prai_social")
	private boolean social;
	
	@ManyToOne
	@JoinColumn(name = "acti_id")
	@ForeignKey(name = "fk_projects_activities_impact_activities")
	@Getter
	@Setter
	private Actividades actividad;
	
	@Getter
	@Setter
	@Column(name = "scdr_id")
	private Integer perforacionExplorativa;

	public ActividadesImpactoProyecto(Integer id) {
		this.id = id;
	}

	public ActividadesImpactoProyecto() {

	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof ActividadesImpactoProyecto)) {
			return false;
		}
		ActividadesImpactoProyecto other = (ActividadesImpactoProyecto) object;
		return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
	}
}