/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
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
import javax.persistence.OneToOne;
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
 *          [Autor: mayriliscs, Fecha: 30/03/2015]
 *          </p>
 */
@Entity
@Table(name = "artisanals_miner", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "armi_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "armi_status = 'TRUE'")
public class MineroArtesanal extends EntidadBase {

	private static final long serialVersionUID = -6112952464187943506L;

	@Id
	@Column(name = "armi_id")
	@SequenceGenerator(name = "ARTISANALS_MINER_GENERATOR", initialValue = 1, sequenceName = "seq_armi_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ARTISANALS_MINER_GENERATOR")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@Column(name = "armi_name")
	private String nombre;

	@Getter
	@Setter
	@Column(name = "armi_code")
	private String codigo;

	@OneToOne
	@JoinColumn(name = "docu_id")
	@ForeignKey(name = "fk_artisanals_minerarmi_id_documentsdocu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	@Getter
	@Setter
	private Documento contratoOperacion;

	@OneToOne
	@JoinColumn(name = "docu_rma_id")
	@ForeignKey(name = "fk_artisanals_minerarmi_id_documentsamadocu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	@Getter
	@Setter
	private Documento registroMineroArtesanal;

	@OneToOne
	@JoinColumn(name = "user_id")
	@ForeignKey(name = "fk_artisanals_minerarmi_id_usersuser_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "user_status = 'TRUE'")
	@Getter
	@Setter
	private Usuario usuario;

}
