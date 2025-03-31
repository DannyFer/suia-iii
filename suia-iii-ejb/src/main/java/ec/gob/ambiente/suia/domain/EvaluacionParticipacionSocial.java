package ec.gob.ambiente.suia.domain;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the
 * environmental_social_participation_evaluation_record database table.
 *
 */
@Entity
@Table(name = "environmental_social_participation_evaluation_record", schema = "suia_iii")
@NamedQueries({ @NamedQuery(name = "EvaluacionParticipacionSocial.findAll", query = "SELECT u FROM EvaluacionParticipacionSocial u") })
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "spec_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "spec_status = 'TRUE'")
public class EvaluacionParticipacionSocial extends EntidadBase {

	private static final long serialVersionUID = 4501180266579264355L;

	@Id
	@SequenceGenerator(name = "EVALUATION_RECORD_PPS_GENERATOR", sequenceName = "seq_espe_id", initialValue = 1,schema = "suia_iii",allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EVALUATION_RECORD_PPS_GENERATOR")
	@Column(name = "espe_id")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@ForeignKey(name = "fk_espspec_id_environmental_social_participation_evaluation_rec")
	@JoinColumn(name = "spec_id")
	private CatalogoEvaluacionParticipacionSocial catalogoEvaluacionParticipacionSocial;

	@Getter
	@Setter
	@Column(name = "espe_value")
	private Boolean valor;

	@Getter
	@Setter
	@Column(name = "spec_creation_date")
	private Date fechaCreacion;

	@Getter
	@Setter
	@ManyToOne
	@ForeignKey(name = "fk_espensp_id_environmental_social_participation_evaluation_rec")
	@JoinColumn(name = "ensp_id")
	private ParticipacionSocialAmbiental participacionSocialAmbiental;

	@Getter
	@Setter
	@ManyToOne
	@ForeignKey(name = "fk_espensp_id_espeuser_id")
	@JoinColumn(name = "user_id")
	private Usuario usuario;
	
	//Cris F: aumento de campos
	@Getter
	@Setter
	@Column(name = "espe_description")
	private String descripcion;
	
	@Getter
	@Setter
	@Column(name = "espe_record_value")
	private Integer valorRegistro;

}
