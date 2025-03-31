/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.domain;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 *
 * @author martinvc
 * @version Revision: 1.0
 *          <p>
 *          [Autor: martinvc, Fecha: 26/06/2015]
 *          </p>
 */
@Entity
@Table(name = "book_red_catalog", catalog = "", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "borc_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "borc_status = 'TRUE'")
public class CatalogoLibroRojo extends EntidadBase {

	/**
	 *
	 */
	private static final long serialVersionUID = -3204053949319999533L;

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "BOOK_RED_CATALOG_BORCID_GENERATOR", sequenceName = "seq_borc_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BOOK_RED_CATALOG_BORCID_GENERATOR")
	@Column(name = "borc_id", unique = true, nullable = false)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "borc_name", length = 400)
	private String nombre;

	@Getter
	@Setter
	@Column(name = "borc_description", length = 1000)
	private String descripcion;

	@Getter
	@Setter
	@OneToMany(mappedBy = "catalogoLibroRojo", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<EspeciesBajoCategoriaAmenaza> especiesBajoCategoriaAmenazaList;

	@Override
	public String toString() {
		return this.nombre;
	}
}
