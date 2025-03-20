package ec.gob.ambiente.rcoa.viabilidadAmbiental.model;

import java.io.Serializable;

import javax.persistence.*;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the response_report_project_viability_coa_biodiversity database table.
 * 
 */
@Entity
@Table(name="response_report_project_viability_coa_biodiversity", schema = "coa_viability")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "repb_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "repb_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "repb_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "repb_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "repb_user_update")) })

@NamedQueries({
@NamedQuery(name="RespuestaFormularioSnap.findAll", query="SELECT r FROM RespuestaFormularioSnap r"),
@NamedQuery(name=RespuestaFormularioSnap.GET_POR_VIABILIDAD_PREGUNTA, query="SELECT r FROM RespuestaFormularioSnap r where r.idViabilidad = :idViabilidad and r.idInforme = :idInforme and r.idPregunta = :idPregunta and r.estado = true"),
@NamedQuery(name=RespuestaFormularioSnap.GET_POR_TIPOPROYECTOVIABILIDAD_PREGUNTA, query="SELECT r FROM RespuestaFormularioSnap r where r.idProyectoViabilidad = :idProyectoViabilidad and r.idPregunta = :idPregunta and r.estado = true") })
public class RespuestaFormularioSnap extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final String PAQUETE = "ec.gob.ambiente.rcoa.viabilidadAmbiental.model.";
	
	public static final String GET_POR_VIABILIDAD_PREGUNTA = PAQUETE + "RespuestaFormularioSnap.getPorViabilidadPregunta";
	public static final String GET_POR_TIPOPROYECTOVIABILIDAD_PREGUNTA = PAQUETE + "RespuestaFormularioSnap.getPorTipoProyectoViabilidadPregunta";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="repb_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="prvi_id")
	private Integer idViabilidad;

	@Getter
	@Setter
	@Column(name="ques_id")
	private Integer idPregunta;

	@Getter
	@Setter
	@Column(name="repb_observation_bd")
	private String observacionBd;

	@Getter
	@Setter
	@Column(name="repb_radio_button")
	private Boolean respBoolean;

	@Getter
	@Setter
	@Column(name="repb_text")
	private String respText;

	@Getter
	@Setter
	@Column(name="repb_value")
	private Integer respInteger;
	
	@Getter
	@Setter
	@Column(name="prtv_id")
	private Integer idProyectoViabilidad;
	
	@Getter
	@Setter
	@Column(name="inre_id")
	private Integer idInforme;

	@Getter
	@Setter
	@Column(name="repb_radio_button_type_2")
	private Integer respValueRadio;
}