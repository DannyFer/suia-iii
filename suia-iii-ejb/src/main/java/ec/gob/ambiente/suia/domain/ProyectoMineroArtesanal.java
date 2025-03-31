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
 *          [Autor: mayriliscs, Fecha: 02/04/2015]
 *          </p>
 */
@Entity
@Table(name = "projects_artisanal_miner", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "pram_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "pram_status = 'TRUE'")
public class ProyectoMineroArtesanal extends EntidadBase{
	
	private static final long serialVersionUID = -1047222134126500354L;

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "PROJECTS_ARTISANAL_MINER_ID_GENERATOR", sequenceName = "seq_pram_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROJECTS_ARTISANAL_MINER_ID_GENERATOR")
	@Column(name = "pram_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pren_id")
	@ForeignKey(name = "fk_projects_artisanal_minerpram_id_projectspren_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "pren_status = 'TRUE'")
	private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "armi_id")
	@ForeignKey(name = "fk_projects_artisanal_minerpram_id_artisanals_minerarmi_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "armi_status = 'TRUE'")
	private MineroArtesanal mineroArtesanal;
}
