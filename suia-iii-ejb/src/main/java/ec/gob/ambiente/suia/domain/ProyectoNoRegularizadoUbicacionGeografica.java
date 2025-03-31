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
 *          [Autor: Christian Marquez, Fecha: 07/01/2015]
 *          </p>
 */
@Entity
@Table(name = "unregulated_projects_locations", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "unpl_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "unpl_status = 'TRUE'")
public class ProyectoNoRegularizadoUbicacionGeografica extends EntidadBase {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "ProyectoNoRegularizadoUbicacionGeografica_GENERATOR", sequenceName = "seq_unpl_id_loc", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ProyectoNoRegularizadoUbicacionGeografica_GENERATOR")
	@Column(name = "unpl_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "unep_id")
	@ForeignKey(name = "fk_un_p_lunep_id_un_e_punep_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "unep_status = 'TRUE'")
	private ProyectoAmbientalNoRegulado proyectoAmbientalNoRegulado;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "gelo_id")
	@ForeignKey(name = "fk_projects_locationsunpl_id_geographical_locationsgelo_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "gelo_status = 'TRUE'")
	private UbicacionesGeografica ubicacionesGeografica;



}
