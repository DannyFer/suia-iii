package ec.gob.ambiente.rcoa.viabilidadAmbiental.model;

import java.io.Serializable;

import javax.persistence.*;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the questions database table.
 * 
 */
@Entity
@Table(name="questions", schema = "coa_viability")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "ques_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "ques_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "ques_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "ques_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "ques_user_update")) })

@NamedQuery(name="PreguntaFormulario.findAll", query="SELECT p FROM PreguntaFormulario p")
public class PreguntaFormulario extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ques_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="ques_description")
	private String descripcion;

	@Getter
	@Setter
	@Column(name="ques_observation_bd")
	private String observacionBd;

	@Getter
	@Setter
	@Column(name="ques_order")
	private Integer orden;

	@Getter
	@Setter
	@Column(name="ques_type")
	private Integer tipo;

	@Getter
	@Setter
	@Column(name="ques_value_false")
	private Integer valorFalse;

	@Getter
	@Setter
	@Column(name="ques_value_true")
	private Integer valorTrue;
	
	@Getter
	@Setter
	@Column(name="ques_requiered")
	private Boolean esRequerido;
	
	@Getter
	@Setter
	@Column(name="titl_id")
	private Integer idCabecera;
	
	@Getter
	@Setter
	@Column(name="ques_other")
	private Integer otroValor;

	@Getter
	@Setter
	@Transient
    private RespuestaFormularioSnap respuesta = new RespuestaFormularioSnap();
}