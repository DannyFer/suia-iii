/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * @author carlos.pupo
 */
@Entity
@Table(name = "documents_copies_sent", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "dcse_status")) })
@NamedQueries({
		@NamedQuery(name = EnvioCopiaDocumento.GET_BY_CC_PARAMS, query = "SELECT e FROM EnvioCopiaDocumento e WHERE e.nombreClase = :className AND e.idEntidad = :entityId AND e.discriminador = :discriminator"),
		@NamedQuery(name = EnvioCopiaDocumento.GET_BY_CLASS_NAME_AND_ID, query = "SELECT e FROM EnvioCopiaDocumento e WHERE e.nombreClase = :className AND e.idEntidad = :entityId") })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "dcse_status = 'TRUE'")
public class EnvioCopiaDocumento extends EntidadBase {

	private static final long serialVersionUID = -6655079497294842487L;

	public static final String GET_BY_CC_PARAMS = "ec.gob.ambiente.suia.domain.EnvioCopiaDocumento.get_by_cc_params";
	public static final String GET_BY_CLASS_NAME_AND_ID = "ec.gob.ambiente.suia.domain.EnvioCopiaDocumento.get_by_class_name_and_id";

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "DOCUMENTS_COPIES_SENT_ID_GENERATOR", sequenceName = "dcse_id_seq", schema = "suia_iii", allocationSize = 1, initialValue = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DOCUMENTS_COPIES_SENT_ID_GENERATOR")
	@Column(name = "dcse_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "dcse_class_name", length = 255)
	private String nombreClase;

	@Getter
	@Setter
	@Column(name = "dcse_entity_id")
	private Integer idEntidad;

	@Getter
	@Setter
	@Column(name = "dcse_discriminator", length = 255)
	private String discriminador;

	@Getter
	@Setter
	@Column(name = "dcse_to", columnDefinition = "text")
	private String destinatarios;
}
