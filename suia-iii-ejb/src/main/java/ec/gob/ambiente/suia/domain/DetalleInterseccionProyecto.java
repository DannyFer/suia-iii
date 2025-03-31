package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * <b> Entity que representa la tabla details_intersection_project. </b>
 * 
 * @author veronica
 * 
 */
@Entity
@Table(name = "details_intersection_project", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "dein_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "dein_status = 'TRUE'")
@NamedQueries({ @NamedQuery(name = "DetalleInterseccionProyecto.findByInterseccionProject", query = "SELECT dip FROM DetalleInterseccionProyecto dip, InterseccionProyecto ip where dip.interseccionProyecto=ip and ip.id=:idInterseccionProyecto") })
public class DetalleInterseccionProyecto extends EntidadBase {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 4969201416771387495L;

	@Getter
	@Setter
	@Id
	@Column(name = "dein_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "dein_geometry_name")
	private String nombre;

	@Getter
	@Setter
	@JoinColumn(name = "inpr_id", referencedColumnName = "inpr_id")
	@ManyToOne(fetch = FetchType.EAGER)
	@ForeignKey(name = "fk_details_intersect_project_inpr_id_intersect_project_inpr_id")
	private InterseccionProyecto interseccionProyecto;
	
	
	@Getter
	@Setter
	@Id
	@Column(name = "dein_subsystem_layer")
	private String capaSubsistema;
	
	
	@Getter
	@Setter
	@Column(name = "dein_geometry_id")
	private Integer idGeometria;
	
	@Getter
	@Setter
	@Column(name = "dein_code", length = 100)
	private String codigoConvenio;
	
	@Getter
	@Setter
	@Column(name = "dein_cap", length = 100)
	private String codigoUnicoCapa;

}
