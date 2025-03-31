package ec.gob.ambiente.suia.survey;

import java.io.Serializable;

import javax.persistence.*;

import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

import java.sql.Timestamp;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the survey_acceptance database table.
 * 
 */
@Entity
@Table(name="survey_acceptance", schema = "suia_survey")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "suac_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "suac_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "suac_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "suac_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "suac_user_update")) })
@NamedQuery(name="SurveyAcceptance.findAll", query="SELECT s FROM SurveyAcceptance s")
public class SurveyAcceptance extends EntidadAuditable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="suac_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="suac_poll")
	private Boolean llenoEncuesta;

	@Getter
	@Setter
	@Column(name="suac_procedure")
	private String tramite ;

	@Getter
	@Setter
	@Column(name="suac_process")
	private String nombreProceso;

	@Setter
	@Getter
	@JoinColumn(name="user_id")
	@ManyToOne
	private Usuario usuario;

	public SurveyAcceptance() {
	}

	

}