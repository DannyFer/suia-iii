package ec.gob.ambiente.suia.domain;

import java.util.Date;

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

import ec.gob.ambiente.suia.domain.CatalogoGeneralBiotico;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * <b> Entidad para manejo de datos de Los catalogos de Licencia Ambiental
 * Categoría II. </b>
 * 
 * @author Frank Torres
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Frank Torres, Fecha: 12/03/2015]
 *          </p>
 */

@NamedQueries({
		@NamedQuery(name = CategoriaIICatalogoGeneralBiotico.FIND_ALL, query = "SELECT c FROM CategoriaIICatalogoGeneralBiotico c"),
		@NamedQuery(name = CategoriaIICatalogoGeneralBiotico.FIND_BY_PROJECT, query = "SELECT c FROM CategoriaIICatalogoGeneralBiotico c WHERE c.fichaAmbientalPma.proyectoLicenciamientoAmbiental.id = :idProyecto"),
		@NamedQuery(name = CategoriaIICatalogoGeneralBiotico.FIND_BY_PROJECT_CATYCODE, query = "SELECT c FROM CategoriaIICatalogoGeneralBiotico c WHERE c.fichaAmbientalPma.proyectoLicenciamientoAmbiental.id = :idProyecto AND c.catalogo.tipoCatalogo.codigo = :catyCode"),
		@NamedQuery(name = CategoriaIICatalogoGeneralBiotico.FIND_BY_CATYCODE, query = "SELECT c FROM CategoriaIICatalogoGeneralBiotico c WHERE c.catalogo.tipoCatalogo.codigo = :catyCode") })
@Entity
@Table(name = "catii_general_catalogs_biotic", schema = "suia_iii")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "cgcb_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "cgcb_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "cgcb_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "cgcb_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "cgcb_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "cgcb_status = 'TRUE'")
public class CategoriaIICatalogoGeneralBiotico extends EntidadAuditable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3551916596548023483L;
	public static final String FIND_ALL = "ec.com.magmasoft.business.domain.CategoriaIICatalogoGeneralBiotico.findAll";
	public static final String FIND_BY_PROJECT = "ec.com.magmasoft.business.domain.CategoriaIICatalogoGeneralBiotico.findByProject";
	public static final String FIND_BY_PROJECT_CATYCODE = "ec.com.magmasoft.business.domain.CategoriaIICatalogoGeneralBiotico.findByProjectCategoryTypeCode";
	public static final String FIND_BY_CATYCODE = "ec.com.magmasoft.business.domain.CategoriaIICatalogoGeneralBiotico.findByCategoryTypeCode";

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "CATIILICENSE_CGCB_ID_GENERATOR", sequenceName = "seq_cgcb_id", schema = "suia_iii")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CATIILICENSE_CGCB_ID_GENERATOR")
	@Column(name = "cgcb_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "cgcb_section")
	private String seccion;

	@Getter
	@Setter
	@Column(name = "cgcb_value")
	private String valor;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "cafa_id")
	@ForeignKey(name = "fk_catii_general_catalog_cgcb_id_catii_fapma_cafa_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "cafa_status = 'TRUE'")
	private FichaAmbientalPma fichaAmbientalPma;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "gcbi_id")
	@ForeignKey(name = "fk_catii_generalcatalobiotgcbi_id_general_catalogs_gcbi_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "geca_status = 'TRUE'")
	private CatalogoGeneralBiotico catalogo;

    public CategoriaIICatalogoGeneralBiotico() {
    }

    public CategoriaIICatalogoGeneralBiotico(Integer id, Boolean estado, String valor, String codigo,  int idficha, Integer id_catalogo, String seccion, String descripcionCategoria) {
        this.setId(id);
        this.setEstado(estado);
        this.setValor(valor);
        this.catalogo = new CatalogoGeneralBiotico();
        this.catalogo.setId(id_catalogo);
        this.catalogo.setDescripcion(descripcionCategoria);
        TipoCatalogo tipo = new TipoCatalogo();
        tipo.setCodigo(codigo);
        catalogo.setTipoCatalogo(tipo);
        this.fichaAmbientalPma = new FichaAmbientalPma();
        this.fichaAmbientalPma.setId(idficha);
        this.seccion = seccion;
    }
    
  //Cris F: aumento de campos para historial
  	@Getter
  	@Setter
  	@Column(name = "cgcb_original_record_id")
  	private Integer idRegistroOriginal;
  		
  	@Getter
  	@Setter
  	@Column(name = "cgcb_historical_date")
  	private Date fechaHistorico;
    
    //Cris F: constructor para historico
    public CategoriaIICatalogoGeneralBiotico(Integer id, Boolean estado, String valor, String codigo,  int idficha, Integer id_catalogo, String seccion, String descripcionCategoria, Integer idRegistroOriginal, Date fechaHistorico) {
        this.setId(id);
        this.setEstado(estado);
        this.setValor(valor);
        this.catalogo = new CatalogoGeneralBiotico();
        this.catalogo.setId(id_catalogo);
        this.catalogo.setDescripcion(descripcionCategoria);
        TipoCatalogo tipo = new TipoCatalogo();
        tipo.setCodigo(codigo);
        catalogo.setTipoCatalogo(tipo);
        this.fichaAmbientalPma = new FichaAmbientalPma();
        this.fichaAmbientalPma.setId(idficha);
        this.seccion = seccion;
        this.setIdRegistroOriginal(idRegistroOriginal);
        this.setFechaHistorico(fechaHistorico);
    }
}
