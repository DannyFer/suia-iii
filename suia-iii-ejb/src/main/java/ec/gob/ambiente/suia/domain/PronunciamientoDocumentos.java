/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 20/01/2015]
 *          </p>
 */
@Entity
@Table(name = "pronouncings_documents", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "prdo_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prdo_status = 'TRUE'")
public class PronunciamientoDocumentos extends EntidadBase {

	private static final long serialVersionUID = -4169534017555806874L;

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "PRONOUNCINGS_DOCUMENTS_GENERATOR", schema = "suia_iii", sequenceName = "seq_prdo_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PRONOUNCINGS_DOCUMENTS_GENERATOR")
	@Column(name = "prdo_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pron_id")
	@ForeignKey(name = "fk_pronouncings_documentsprdo_id_pronouncingspron_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "pron_status = 'TRUE'")
	private Pronunciamiento pronunciamiento;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "docu_id")
	@ForeignKey(name = "fk_pronouncings_documentsprdo_id_documentsdocuid")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	private Documento documento;
}
