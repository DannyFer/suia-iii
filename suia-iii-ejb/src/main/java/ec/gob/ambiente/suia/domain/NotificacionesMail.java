/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.domain;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * @author christian
 */
@Entity
@Table(name = "mail_notifications", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "mano_envio")) })
@NamedQueries({
		@NamedQuery(name = NotificacionesMail.LISTAR_TODO, query = "SELECT m FROM NotificacionesMail m"),
		@NamedQuery(name = NotificacionesMail.LISTAR_NO_ENVIADOS, query = "SELECT m FROM NotificacionesMail m WHERE m.estado = false") })
public class NotificacionesMail extends EntidadBase {

	private static final long serialVersionUID = 8865485375746830917L;

	private static final String PAQUETE_CLASE = "ec.gob.ambiente.suia.domain.NotificacionesMail.";
	public static final String LISTAR_TODO = PAQUETE_CLASE + "findAll";
	public static final String LISTAR_NO_ENVIADOS = PAQUETE_CLASE + "listarNoEnviados";

	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "MANO_ID_GENERATOR", initialValue = 1, sequenceName = "seq_mano_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MANO_ID_GENERATOR")
	@Column(name = "mano_id", nullable = false)
	private Integer id;
	@Getter
	@Setter
	@Column(name = "mano_subject", nullable = false, length = 300)
	private String asunto;
	@Getter
	@Setter
	@Column(name = "mano_content", nullable = false, length = 5000)
	private String contenido;
	@Getter
	@Setter
	@Column(name = "mano_email_recipient", nullable = false, length = 100)
	private String email;
	@Getter
	@Setter
	@Column(name = "mano_observation", length = 1000)
	private String observacion;
	@Getter
	@Setter
	@Column(name = "mano_observation_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaObservacion;

	@Getter
	@Setter
	@Column(name = "mano_type_message", length = 50)
	private String tipoMensaje;

	@Getter
	@Setter
	@Column(name = "mano_attachments")
	@Lob
	private String adjuntos;

	public NotificacionesMail() {
	}

	public NotificacionesMail(Integer id) {
		this.id = id;
	}
}
