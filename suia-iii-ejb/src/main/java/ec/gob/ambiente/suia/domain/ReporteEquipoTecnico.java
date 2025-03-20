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

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the technical_report_pps database table.
 *
 */
@NamedQueries({ @NamedQuery(name = ReporteEquipoTecnico.GET_ALL, query = "SELECT m FROM ReporteEquipoTecnico m") })
@Entity
@Table(name = "technical_report_pps", schema = "suia_iii")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "trpp_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "trpp_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "trpp_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "trpp_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "trpp_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "ensp_status = 'TRUE'")
public class ReporteEquipoTecnico extends EntidadAuditable {

	private static final long serialVersionUID = -2981363444919430056L;
	public static final String GET_ALL = "ec.com.magmasoft.business.domain.ReporteEquipoTecnico.getAll";

	@Id
	@SequenceGenerator(name = "TECHNICAL_TEAM_REPORT_PPS_GENERATOR", sequenceName = "seq_tete_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TECHNICAL_TEAM_REPORT_PPS_GENERATOR")
	@Column(name = "trpp_id")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@ForeignKey(name = "fk_trpp_id_doty_id")
	@JoinColumn(name = "doty_id")
	private TipoDocumento tipoDocumento;

	@Getter
	@Setter
	@ManyToOne
	@ForeignKey(name = "fk_technical_report_ppsensp_id_ensp_ensp_id")
	@JoinColumn(name = "ensp_id")
	private ParticipacionSocialAmbiental participacionSocialAmbiental;

	@Getter
	@Setter
	@Column(name = "trpp_report_number")
	private String numeroReporte;

	@Getter
	@Setter
	@Column(name = "trpp_report_city")
	private String ciudad;

	@Getter
	@Setter
	@Column(name = "trpp_report_date")
	private String fechaReporte;

	@Getter
	@Setter
	@Column(name = "trpp_background")
	private String antecedentes;

	@Getter
	@Setter
	@Column(name = "trpp_goals")
	private String metas;

	@Getter
	@Setter
	@Column(name = "trpp_project_features")
	private String caracteristicasProyecto;

	@Getter
	@Setter
	@Column(name = "trpp_technical_evaluation")
	private String evaluacionTecnica;

	@Getter
	@Setter
	@Column(name = "trpp_conclusions_recommendations")
	private String conclucionesRecomendaciones;

	@Getter
	@Setter
	@Column(name = "trpp_observations")
	private String observaciones;

	@Getter
	@Setter
	@Column(name = "trpp_other_obligations")
	private String otrasObligaciones;

	@Getter
	@Setter
	@Column(name = "trpp_pronouncing")
	private String pronunciamiento;

}
