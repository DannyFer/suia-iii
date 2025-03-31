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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author Jonathan Guerrero
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Jonathan Guerrero, Fecha: 08/05/2015]
 *          </p>
 */
@Entity
@Table(name = "categories_catalog_agreements", catalog = "", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "ccag_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "ccag_status = 'TRUE'")
public class CatalogoCategoriaAcuerdo extends EntidadBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2963953350778732361L;

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "CATEGORIES_CATALOG_CCAGID_GENERATOR", sequenceName = "seq_ccag_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CATEGORIES_CATALOG_CCAGID_GENERATOR")
	@Column(name = "ccag_id", unique = true, nullable = false)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "ccag_entity_name", length = 255)
	private String nombreOrganizacion;

	@Getter
	@Setter
	@Column(name = "ccag_entity_ruc", length = 20)
	private String rucOrganizacion;

	@Getter
	@Setter
	@Column(name = "cacs_code", nullable = false, length = 25)
	private String codigo;

	@Override
	public String toString() {
		return this.nombreOrganizacion;
	}
}
