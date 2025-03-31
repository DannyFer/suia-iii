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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * @author christian
 */
@Entity
@Table(name = "audit_users")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "adus_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "adus_status = 'TRUE'")
public class AuditoriaSuplantacion extends EntidadBase {

	private static final long serialVersionUID = 2640081391799843222L;

	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "AUDIT_USERS_ID_GENERATOR", initialValue = 1, sequenceName = "seq_adus_id", schema = "public", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AUDIT_USERS_ID_GENERATOR")
	@Column(name = "adus_id")
	private Integer id;

	@Getter
	@Setter
	@JoinColumn(name = "user_id_mae")
	@ForeignKey(name = "fk_audit_users_user_id_mae_users_user_id")
	@ManyToOne
	private Usuario usuarioMae;
	@Getter
	@Setter
	@JoinColumn(name = "user_id_subject")
	@ForeignKey(name = "fk_audit_users_user_id_subject_users_user_id")
	@ManyToOne
	private Usuario usuarioSuplantado;

	@Getter
	@Setter
	@Column(name = "adus_date_impersonation")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaSuplantacion;

	public AuditoriaSuplantacion() {
	}

	public AuditoriaSuplantacion(Integer id) {
		this.id = id;
	}
}
