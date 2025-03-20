package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
@Table(name = "projects_waste_phases", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "prwp_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prwp_status = 'TRUE'")
public class ProyectoFaseDesecho extends EntidadBase {

	private static final long serialVersionUID = 5369913803444370498L;

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "PROJECTS_WASTE_PHASES_prwp_ID_GENERATOR", sequenceName = "seq_prwp_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROJECTS_WASTE_PHASES_prwp_ID_GENERATOR")
	@Column(name = "prwp_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pren_id")
	@ForeignKey(name = "fk_projects_waste_phasesprwp_id_projects_environmental_licensingpren_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "pren_status = 'TRUE'")
	private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "coor_id")
	@ForeignKey(name = "fk_projects_waste_phasesprwp_id_waste_phaseswaph_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "waph_status = 'TRUE'")
	private FaseDesecho faseDesecho;

	@Override
	public String toString() {
		return faseDesecho != null ? faseDesecho.toString() : "";
	}
}
