package ec.gob.ambiente.suia.domain;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name = "task_comments", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "taco_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "taco_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "tacop_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "taco_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "taco_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "taco_status = 'TRUE'")
public class ComentarioTarea extends EntidadAuditable {

	private static final long serialVersionUID = 4566036411599650555L;

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "COMMENT_GENERATOR", schema = "suia_iii", sequenceName = "seq_coment_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COMMENT_GENERATOR")
	@Column(name = "taco_id", unique = true, nullable = false)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "taco_user")
	private String idUsuario;

	@Getter
	@Setter
	@Temporal(TemporalType.DATE)
	@Column(name = "taco_date")
	private Date fecha;

	@Getter
	@Setter
	@Temporal(TemporalType.TIME)
	@Column(name = "taco_time")
	private Date hora;
	@Getter
	@Setter
	@Column(name = "taco_comment")
	private String comentario;
	@Getter
	@Setter
	private Integer idDocumento;
	@Getter
	@Setter
	@Column(name = "taco_documment")
	private String tipoDocumento;
	@Getter
	@Setter
	@Column(name = "taco_required_corrections")
	private boolean requiereCorrecciones;

	@Getter
	@Setter
	@Column(name = "taco_instance")
	private Long idInstanciaProceso;
}
