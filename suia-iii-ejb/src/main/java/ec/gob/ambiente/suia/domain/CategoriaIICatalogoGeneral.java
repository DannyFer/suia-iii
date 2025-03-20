package ec.gob.ambiente.suia.domain;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.CatalogoGeneral;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * <b> Entidad para manejo de datos de Los catalogos de Licencia Ambiental
 * Categoría II. </b>
 * 
 * @author jgras
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Frank Torres, Fecha: 12/03/2015]
 *          </p>
 */

@NamedQueries({
		@NamedQuery(name = CategoriaIICatalogoGeneral.FIND_ALL, query = "SELECT c FROM CategoriaIICatalogoGeneral c"),
		@NamedQuery(name = CategoriaIICatalogoGeneral.FIND_BY_PROJECT, query = "SELECT c FROM CategoriaIICatalogoGeneral c WHERE c.fichaAmbientalPma.proyectoLicenciamientoAmbiental.id = :idProyecto"),
		@NamedQuery(name = CategoriaIICatalogoGeneral.FIND_BY_PROJECT_CATYCODE, query = "SELECT c FROM CategoriaIICatalogoGeneral c WHERE c.fichaAmbientalPma.proyectoLicenciamientoAmbiental.id = :idProyecto AND c.catalogoGeneral.tipoCatalogo.codigo = :catyCode"),
		@NamedQuery(name = CategoriaIICatalogoGeneral.FIND_BY_CATYCODE, query = "SELECT c FROM CategoriaIICatalogoGeneral c WHERE c.catalogoGeneral.tipoCatalogo.codigo = :catyCode") })
@Entity
@Table(name = "catii_general_catalog", schema = "suia_iii")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "cigc_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "cigc_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "cigc_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "cigc_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "cigc_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "cigc_status = 'TRUE'")
public class CategoriaIICatalogoGeneral extends EntidadAuditable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3551916596548023483L;
	public static final String FIND_ALL = "ec.com.magmasoft.business.domain.CategoriaIICatalogoGeneral.findAll";
	public static final String FIND_BY_PROJECT = "ec.com.magmasoft.business.domain.CategoriaIICatalogoGeneral.findByProject";
	public static final String FIND_BY_PROJECT_CATYCODE = "ec.com.magmasoft.business.domain.CategoriaIICatalogoGeneral.findByProjectCategoryTypeCode";
	public static final String FIND_BY_CATYCODE = "ec.com.magmasoft.business.domain.CategoriaIICatalogoGeneral.findByCategoryTypeCode";

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "CATIILICENSE_CIGC_ID_GENERATOR", sequenceName = "seq_cigc_id", schema = "suia_iii")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CATIILICENSE_CIGC_ID_GENERATOR")
	@Column(name = "cigc_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "cigc_section")
	private String seccion;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "cafa_id")
	@ForeignKey(name = "fk_catii_general_catalog_cigc_id_catii_fapma_cafa_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "cafa_status = 'TRUE'")
	private FichaAmbientalPma fichaAmbientalPma;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "geca_id")
	@ForeignKey(name = "fk_catii_general_catalog_geca_id_general_catalogs_geca_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "geca_status = 'TRUE'")
	private CatalogoGeneral catalogoGeneral;
}
