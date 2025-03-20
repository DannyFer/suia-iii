/**
 * 
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

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * <b> Clase Para mapear la relacion de los articulos con el catalogo. </b>
 * 
 * @author Javier Lucero
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Javier Lucero, Fecha: 25/03/2015]
 *          </p>
 */
@NamedQueries({ @NamedQuery(name = ArticuloCatalogo.GET_ALL, query = "SELECT m FROM ArticuloCatalogo m") })
@Entity
@Table(name = "fapma_catalogo_article", schema = "suia_iii")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "faca_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "faca_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "faca_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "faca_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "faca_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "faca_status = 'TRUE'")
public class ArticuloCatalogo extends EntidadAuditable {
	private static final long serialVersionUID = 1L;

	public static final String GET_ALL = "ec.com.magmasoft.business.domain.ArticleCatalogo.getAll";
	public static final String SEQUENCE_CODE = "fapma_catalogo_article_faca_id_seq";

	@Id
	@Column(name = "faca_id")
	@SequenceGenerator(name = "CATALOGO_ARTICLE_FACAID_GENERATOR", sequenceName = "fapma_catalogo_article_faca_id_seq", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CATALOGO_ARTICLE_FACAID_GENERATOR")
	@Getter
	@Setter
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "secl_id")
	@ForeignKey(name = "sectors_classification_fapma_catalogo_article_fk")
	@Getter
	@Setter
	private TipoSubsector tipoSubsector;

	@ManyToOne
	@JoinColumn(name = "faar_id")
	@ForeignKey(name = "fapma_article_fapma_catalogo_article_fk")
	@Getter
	@Setter
	private Articulo articulo;

	@OneToMany(mappedBy = "articuloCatalogo")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "fale_status = 'TRUE'")
	@Getter
	@Setter
	private List<MarcoLegalPma> marcosLegales;

	@Column(name="faca_use_code")
	@Getter
	@Setter
	private String codigoUso;

}
