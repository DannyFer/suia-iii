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
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 25/03/2015]
 *          </p>
 */
@Entity
@Table(name = "categories_catalog_public", catalog = "", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "cacp_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "cacp_status = 'TRUE'")
public class CatalogoCategoriaPublico extends EntidadBase {

	private static final long serialVersionUID = -227716239768501811L;

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "CATEGORIES_CATALOG_CACPID_GENERATOR", sequenceName = "seq_cacp_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CATEGORIES_CATALOG_CACPID_GENERATOR")
	@Column(name = "cacp_id", unique = true, nullable = false)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "cacp_name", length = 400)
	private String nombre;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "cacp_parent_id")
	@ForeignKey(name = "fk_categories_cpcacp_idcategories_cpcacp_parent_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "cacp_status = 'TRUE'")
	private CatalogoCategoriaPublico categoriaPublico;

	@Getter
	@Setter
	@OneToMany(mappedBy = "categoriaPublico")
	private List<CatalogoCategoriaPublico> categoriaPublicoHijos;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "sety_id")
	@ForeignKey(name = "fk_categories_catalog_publiccacp_id_sector_typessety_id")
	private TipoSector tipoSector;
	
	public boolean isCategoriaFinal() {
		return categoriaPublicoHijos == null || categoriaPublicoHijos.isEmpty();
	}

	@Override
	public String toString() {
		return this.nombre;
	}
}
