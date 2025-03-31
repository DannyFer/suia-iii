package ec.gob.ambiente.retce.model;

import java.io.Serializable;

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

import ec.gob.ambiente.retce.model.DetalleCatalogoGeneral;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;



/**
 * The persistent class for the project_information_shapes database table.
 * 
 */
@Entity
@Table(name="project_information_waste_fases", schema="retce")
@NamedQueries({ @NamedQuery(name = InformacionProyectoDesechoFases.FIND_BY_PROJECT_WASTES, query = "SELECT o FROM InformacionProyectoDesechoFases o WHERE o.estado=true and o.informacionProyecto.id = :informacionProyecto order by 1 desc") })
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "piwf_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "piwf_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "piwf_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "piwf_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "piwf_user_update")) })
public class InformacionProyectoDesechoFases extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String FIND_BY_PROJECT_WASTES = "ec.com.magmasoft.business.domain.InformacionProyectoDesechoFases.byProject";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="piwf_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="proj_id")
	private InformacionProyecto informacionProyecto;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="gcde_id")
	private DetalleCatalogoGeneral desechoFases;

}