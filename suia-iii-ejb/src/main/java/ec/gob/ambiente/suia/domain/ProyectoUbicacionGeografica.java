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
 * <b> Entity que representa la tabla clousure_plans_documents. </b>
 * 
 * @author carlos.pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: carlos.pupo, Fecha: 07/01/2015]
 *          </p>
 */
@Entity
@Table(name = "projects_locations", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "prlo_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prlo_status = 'TRUE'")
public class ProyectoUbicacionGeografica extends EntidadBase {

	private static final long serialVersionUID = 1274266446610658774L;

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "PROJECTS_LOCATIONS_ID_GENERATOR", sequenceName = "seq_prlo_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROJECTS_LOCATIONS_ID_GENERATOR")
	@Column(name = "prlo_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "pren_id")
	@ForeignKey(name = "fk_projects_locationsprlo_id_projects_environmental_licensingpren_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "pren_status = 'TRUE'")
	private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "gelo_id")
	@ForeignKey(name = "fk_projects_locationsprlo_id_geographical_locationsgelo_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "gelo_status = 'TRUE'")
	private UbicacionesGeografica ubicacionesGeografica;

	@Getter
	@Setter
	@Column(name = "pren_id", insertable=false, updatable=false)
	private Integer idProyecto;
	
	@Getter
	@Setter
	@Column(name = "prlo_order")
	private Integer orden;
	
	
	@Getter
	@Setter
	@Column(name = "prlo_update_number")
	private Integer nroActualizacion = 0;
	
	
	@Getter
	@Setter
	@Column(name = "id_4_cat")
	private String idCuatroCategorias;

}
