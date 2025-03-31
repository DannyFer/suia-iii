/*
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * <b> Entity Bean. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: Dec 20, 2014]
 *          </p>
 */
@Entity
@Table(name = "prerequisites_licensing", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "prli_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "prli_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "prli_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "prli_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "prli_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prli_status = 'TRUE'")
public class RequisitosPreviosLicenciamiento extends EntidadAuditable {

	private static final long serialVersionUID = 3051438345692684411L;

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "PREREQUISITES_LICENSING_PRLI_ID_GENERATOR", sequenceName = "seq_prli_id", schema = "suia_iii")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PREREQUISITES_LICENSING_PRLI_ID_GENERATOR")
	@Column(name = "prli_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "pren_id")
	@ForeignKey(name = "fk_prerequisitesprli_id_projectspren_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "pren_status = 'TRUE'")
	private ProyectoLicenciamientoAmbiental proyecto;

}
