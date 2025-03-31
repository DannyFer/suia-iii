/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.domain;

import java.util.List;

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
import javax.persistence.OneToMany;
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
 * @author martinvc
 * @version Revision: 1.0
 *          <p>
 *          [Autor: martinvc, Fecha: 26/06/2015]
 *          </p>
 */
@Entity
@Table(name = "annexes_catalog", catalog = "", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "anca_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "anca_status = 'TRUE'")
public class CatalogoAnexo extends EntidadBase {

	private static final long serialVersionUID = -1988583780127908644L;

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "ANNEXES_CATALOG_ANCAID_GENERATOR", sequenceName = "seq_anca_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ANNEXES_CATALOG_ANCAID_GENERATOR")
	@Column(name = "anca_id", unique = true, nullable = false)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "anca_name", length = 400)
	private String nombre;

	@Getter
	@Setter
	@Column(name = "anca_description", length = 1000)
	private String descripcion;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "anca_parent_id")
	@ForeignKey(name = "fk_annexes_canca_idannexes_canca_parent_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "anca_status = 'TRUE'")
	private CatalogoAnexo catalogoAnexo;

	@Getter
	@Setter
	@OneToMany(mappedBy = "catalogoAnexo", fetch = FetchType.EAGER)
	private List<CatalogoAnexo> catalogoAnexoHijos;

	public boolean isCategoriaFinal() {
		return catalogoAnexoHijos == null || catalogoAnexoHijos.isEmpty();
	}

	@Override
	public String toString() {
		return this.nombre;
	}
}
