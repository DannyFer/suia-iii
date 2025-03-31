/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

@Entity
@Table(name = "send_notifications", schema = "public")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "seno_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "seno_creation_date")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "seno_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "seno_creator_user")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "seno_user_update")) })
public class EnvioNotificacionesMail extends EntidadAuditable implements Serializable{

	private static final long serialVersionUID = 8865485375746830917L;

	//public static final String LISTAR_NOTIFICACIOCIONES_POR_USUARIO_ENVIADAS = "ec.gob.ambiente.suia.domain.EnvioNotificacionesMail.getNotificacionesPorUsuarioEnviadas";

	public static final Integer SISTEMA_REGULARIZACION = 1;
	public static final Integer SISTEMA_CONTROL = 2;
	
	@Id
	@Getter
	@Setter
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "seno_id", nullable = false)
	private Integer id;
	@Getter
	@Setter
	@Column(name = "seno_project_code")
	private String codigoProyecto;
	@Getter
	@Setter
	@Column(name = "seno_subject")
	private String asunto;
	@Getter
	@Setter
	@Column(name = "seno_content")
	private String contenido;
	@Getter
	@Setter
	@Column(name = "seno_destination_mail")
	private String email;
	@Getter
	@Setter
	@Column(name = "seno_mail_destination_copy")
	private String emailCopia;
	@Getter
	@Setter
	@Column(name = "seno_send_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaEnvio;
	@Getter
	@Setter
	@Column(name = "seno_status_send")
	private boolean enviado;
	@Getter
	@Setter
	@Column(name = "seno_check_message")
	private boolean notificacionRevisada;
	@Getter
	@Setter
	@Column(name = "seno_check_message_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaRevision;
	@Getter
	@Setter
	@Column(name = "seno_process_id")
	private Integer procesoId;
	@Getter
	@Setter
	@Column(name = "seno_task_id")
	private Integer tareaId;
	@Getter
	@Setter
	@Column(name = "seno_user_name_destination")
	private String nombreUsuarioDestino;
	@Getter
	@Setter
	@Column(name = "seno_user_id_destination")
	private Integer usuarioDestinoId;
	@Getter
	@Setter
	@Column(name = "seno_user_id_sends")
	private Integer usuarioEnvioId;
	@Getter
	@Setter
	@Column(name = "seno_observable")
	private Boolean esVisible;
	@Getter
	@Setter
	@Column(name = "seno_system")
	private Integer idSistema;

}
