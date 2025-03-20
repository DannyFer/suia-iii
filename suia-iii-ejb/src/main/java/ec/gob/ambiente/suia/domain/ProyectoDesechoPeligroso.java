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
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

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
@Table(name = "projects_waste_dangerous", catalog = "", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "prwd_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prwd_status = 'TRUE'")
public class ProyectoDesechoPeligroso extends EntidadBase {

	private static final long serialVersionUID = 353148124058771258L;

	@Getter
	@Setter
	@Id
	@Column(name = "prwd_id")
	@SequenceGenerator(name = "PROJECTS_WASTE_DANGEROUS_prwd_ID_GENERATOR", sequenceName = "seq_prwd_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROJECTS_WASTE_DANGEROUS_prwd_ID_GENERATOR")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "prwd_management_capacity")
	private Double capacidadGestion;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pren_id")
	@ForeignKey(name = "fk_projects_waste_dangerousprwd_id_projects_environmental_licensingpren_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "pren_status = 'TRUE'")
	private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "wada_id")
	@LazyCollection(LazyCollectionOption.FALSE)
	@ForeignKey(name = "fk_projects_waste_dangerousprwd_id_waste_dangerouswada_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wada_status = 'TRUE'")
	private DesechoPeligroso desechoPeligroso;

	@Override
	public String toString() {
		return desechoPeligroso != null ? desechoPeligroso.toString() : "";
	}
}
