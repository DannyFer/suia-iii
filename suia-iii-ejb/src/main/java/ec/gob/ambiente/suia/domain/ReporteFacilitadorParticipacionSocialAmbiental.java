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
 * The persistent class for the project_facilitators_reports database table.
 *
 */
@Entity
@Table(name = "project_facilitators_reports", schema = "suia_iii")
@NamedQueries({
		@NamedQuery(name = "ReporteFacilitadorParticipacionSocialAmbiental.findAll", query = "SELECT u FROM ReporteFacilitadorParticipacionSocialAmbiental u")
		 })
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "prfr_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prfr_status = 'TRUE'")
public class ReporteFacilitadorParticipacionSocialAmbiental extends EntidadBase {

	private static final long serialVersionUID = -1988928497564457738L;


	@Id
	@SequenceGenerator(name = "FACILITAITOR_REPORT_PPS_GENERATOR", sequenceName = "seq_prfr_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FACILITAITOR_REPORT_PPS_GENERATOR")
	@Column(name = "prfr_id")
	@Getter
	@Setter
	private Integer id;
	
	@Getter
	@Setter
	@ManyToOne
	@ForeignKey(name = "fk_project_facilitators_reportsprfa_id_project_facilitatorsprfa")
	@JoinColumn(name = "prfa_id")	
	private FacilitadorProyecto facilitadorProyecto;



}
