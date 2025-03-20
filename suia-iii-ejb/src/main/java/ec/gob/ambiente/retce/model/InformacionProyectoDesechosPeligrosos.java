package ec.gob.ambiente.retce.model;

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

import antlr.collections.List;
import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;



/**
 * The persistent class for the project_information_shapes database table.
 * 
 */
@Entity
@Table(name="project_information_waste_dangerous", schema="retce")
@NamedQueries({ @NamedQuery(name = InformacionProyectoDesechosPeligrosos.FIND_BY_PROJECT_WASTES, query = "SELECT o FROM InformacionProyectoDesechosPeligrosos o WHERE o.estado=true and o.informacionProyecto.id = :informacionProyecto order by 1 desc") })
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "piwd_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "piwd_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "piwd_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "piwd_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "piwd_user_update")) })
public class InformacionProyectoDesechosPeligrosos extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String FIND_BY_PROJECT_WASTES = "ec.com.magmasoft.business.domain.InformacionProyectoDesechosPeligrosos.byProject";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="piwd_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "piwd_transport")
	private boolean transporte;

	@Getter
	@Setter
	@Column(name = "piwd_storage")
	private boolean almacenamiento;

	@Getter
	@Setter
	@Column(name = "piwd_elimination")
	private boolean eliminacion;

	@Getter
	@Setter
	@Column(name = "piwd_disposition")
	private boolean disposicion;
	
	@Getter
	@Setter
	@Column(name="piwd_revision_date")
	private Date fechaRevision;
	
	@Getter
	@Setter
	@Column(name="piwd_revision_user")
	private String usuarioRevision;
	
	@Getter
	@Setter
	@Column(name="piwd_validated_information")
	private Boolean informacionValidada;

	@Getter
	@Setter
	@Column(name = "piwd_history")
	private boolean historico;

	@Getter
	@Setter
	@Column(name = "piwd_id_owner")
	private Integer idPropietario;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="proj_id")
	private InformacionProyecto informacionProyecto;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="wada_id")
	private DesechoPeligroso desechosPeligrosos;
}
