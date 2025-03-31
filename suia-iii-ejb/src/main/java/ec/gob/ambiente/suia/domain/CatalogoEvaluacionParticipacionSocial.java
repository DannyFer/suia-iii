package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the social_participation_evaluation_catalog database
 * table.
 *
 */
@Entity
@Table(name = "social_participation_evaluation_catalog", schema = "suia_iii")
@NamedQueries({ @NamedQuery(name = "CatalogoEvaluacionParticipacionSocial.findAll", query = "SELECT u FROM CatalogoEvaluacionParticipacionSocial u") })
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "spec_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "spec_status = 'TRUE'")
public class CatalogoEvaluacionParticipacionSocial extends EntidadBase {

	private static final long serialVersionUID = 4679695999131141304L;

	@Id
	@SequenceGenerator(name = "EVALUATION_CATALOG_PPS_GENERATOR", sequenceName = "seq_spec_id", initialValue = 1,schema = "suia_iii",allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EVALUATION_CATALOG_PPS_GENERATOR")
	@Column(name = "spec_id")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@Column(name = "spec_description")
	private String descripcion;
	@Getter
	@Setter
	@Column(name = "spec_group")
	private String grupo;

}
