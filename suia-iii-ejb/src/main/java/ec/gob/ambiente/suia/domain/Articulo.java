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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ec.gob.ambiente.suia.domain.CatalogoGeneral;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * <b> Clase que mapea los articulos. </b>
 *
 * @author Javier Lucero
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Javier Lucero, Fecha: 25/03/2015]
 *          </p>
 */
@NamedQueries({ @NamedQuery(name = Articulo.GET_ALL, query = "SELECT m FROM Articulo m") })
@Entity
@Table(name = "fapma_article", schema = "suia_iii")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "faar_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "faar_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "faar_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "faar_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "faar_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "faar_status = 'TRUE'")
public class Articulo extends EntidadAuditable {

	private static final long serialVersionUID = 1L;

	public static final String GET_ALL = "ec.com.magmasoft.business.domain.Article.getAll";
	public static final String SEQUENCE_CODE = "fapma_article_faar_id_seq";

	@Id
	@Column(name = "faar_id")
	@SequenceGenerator(name = "ARTICLE_FAAID_GENERATOR", sequenceName = "fapma_article_faar_id_seq", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ARTICLE_FAAID_GENERATOR")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@Column(name = "faar_article", nullable = false, length = 255)
	private String articulo;

	@ManyToOne
	@JoinColumn(name = "geca_id")
	@ForeignKey(name = "general_catalogs_fapma_article_fk")
	@Getter
	@Setter
	private CatalogoGeneral catalogoGeneral;

	@OneToMany(mappedBy = "articulo")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "faca_status = 'TRUE'")
	@Getter
	@Setter
	private List<ArticuloCatalogo> articuloCatalogos;



}
