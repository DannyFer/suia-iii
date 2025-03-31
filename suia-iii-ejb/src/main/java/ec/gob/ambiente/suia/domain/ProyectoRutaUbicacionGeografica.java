package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * <b> Entity que representa la tabla rutas proyecto ubicaciones geograficas.
 * </b>
 * 
 * @author carlos.pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: carlos.pupo, Fecha: 07/01/2015]
 *          </p>
 */
@Entity
@Table(name = "projects_routes_locations", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "prrl_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prrl_status = 'TRUE'")
public class ProyectoRutaUbicacionGeografica extends EntidadBase {

	private static final long serialVersionUID = 1274266446610658774L;

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "PROJECTS_ROUTES_LOCATIONS_ID_GENERATOR", sequenceName = "seq_prrl_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROJECTS_ROUTES_LOCATIONS_ID_GENERATOR")
	@Column(name = "prrl_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "pren_id")
	@ForeignKey(name = "fk_projects_locationsprrl_id_projects_environmental_licensingpren_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "pren_status = 'TRUE'")
	private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "gelo_id")
	@ForeignKey(name = "fk_projects_locationsprrl_id_geographical_locationsgelo_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "gelo_status = 'TRUE'")
	private UbicacionesGeografica ubicacionesGeografica;

}
