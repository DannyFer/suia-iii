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
        @NamedQuery(name = CategoriaIICatalogoGeneralSocial.FIND_ALL, query = "SELECT c FROM CategoriaIICatalogoGeneralSocial c"),
        @NamedQuery(name = CategoriaIICatalogoGeneralSocial.FIND_BY_PROJECT, query = "SELECT c FROM CategoriaIICatalogoGeneralSocial c WHERE c.fichaAmbientalPma.proyectoLicenciamientoAmbiental.id = :idProyecto"),
        @NamedQuery(name = CategoriaIICatalogoGeneralSocial.FIND_BY_PROJECT_CATYCODE, query = "SELECT c FROM CategoriaIICatalogoGeneralSocial c WHERE c.fichaAmbientalPma.proyectoLicenciamientoAmbiental.id = :idProyecto AND c.catalogo.tipoCatalogo.codigo = :catyCode"),
        @NamedQuery(name = CategoriaIICatalogoGeneralSocial.FIND_BY_CATYCODE, query = "SELECT c FROM CategoriaIICatalogoGeneralSocial c WHERE c.catalogo.tipoCatalogo.codigo = :catyCode")})
@Entity
@Table(name = "catii_general_catalogs_social", schema = "suia_iii")
@AttributeOverrides({
        @AttributeOverride(name = "estado", column = @Column(name = "cgcs_status")),
        @AttributeOverride(name = "fechaCreacion", column = @Column(name = "cgcs_creation_date")),
        @AttributeOverride(name = "fechaModificacion", column = @Column(name = "cgcs_date_update")),
        @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "cgcs_creator_user")),
        @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "cgcs_user_update"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "cgcs_status = 'TRUE'")
public class CategoriaIICatalogoGeneralSocial extends EntidadAuditable {

    /**
     *
     */
    private static final long serialVersionUID = 3551916596548023483L;
    public static final String FIND_ALL = "ec.com.magmasoft.business.domain.CategoriaIICatalogoGeneralSocial.findAll";
    public static final String FIND_BY_PROJECT = "ec.com.magmasoft.business.domain.CategoriaIICatalogoGeneralSocial.findByProject";
    public static final String FIND_BY_PROJECT_CATYCODE = "ec.com.magmasoft.business.domain.CategoriaIICatalogoGeneralSocial.findByProjectCategoryTypeCode";
    public static final String FIND_BY_CATYCODE = "ec.com.magmasoft.business.domain.CategoriaIICatalogoGeneralSocial.findByCategoryTypeCode";

    @Getter
    @Setter
    @Id
    @SequenceGenerator(name = "CATIILICENSE_CGCS_ID_GENERATOR", sequenceName = "seq_cgcs_id", schema = "suia_iii")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CATIILICENSE_CGCS_ID_GENERATOR")
    @Column(name = "cgcs_id")
    private Integer id;

    @Getter
    @Setter
    @Column(name = "cgcs_section")
    private String seccion;

    @Getter
    @Setter
    @Column(name = "cgcs_value")
    private String valor;


    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "cafa_id")
    @ForeignKey(name = "fk_catii_general_catalog_cgcs_id_catii_fapma_cafa_id")
    @Filter(name = EntidadBase.FILTER_ACTIVE, condition = "cafa_status = 'TRUE'")
    private FichaAmbientalPma fichaAmbientalPma;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "gcso_id")
    @ForeignKey(name = "fk_catii_generalcatalosocialgcso_id_general_catalogs_gcso_id")
    @Filter(name = EntidadBase.FILTER_ACTIVE, condition = "geca_status = 'TRUE'")
    private CatalogoGeneralSocial catalogo;
    
    //campos agregados 
    @Getter
    @Setter
    @Column(name = "cgcs_original_record_id")
    private Integer idRegistroOriginal;
    
    @Getter
    @Setter
    @Column(name = "cgcs_historical_date")
    private Date fechaHistorico;
    

    public CategoriaIICatalogoGeneralSocial() {
    }

    public CategoriaIICatalogoGeneralSocial(Integer id, Boolean estado, String valor, String codigo,
                                            int idficha, Integer id_catalogo, String seccion, String tipoTipo, Integer caty_id, String descripcionCategoria, String descripcionAyuda) {
        this.setId(id);
        this.setEstado(estado);
        this.setValor(valor);
        this.catalogo = new CatalogoGeneralSocial();
        TipoCatalogo tipo = new TipoCatalogo();
        tipo.setId(caty_id);
        tipo.setCodigo(codigo);
        tipo.setTipo(tipoTipo);
        this.catalogo.setTipoCatalogo(tipo);
        this.catalogo.setId(id_catalogo);
        this.catalogo.setOrden(0);
        this.catalogo.setDescripcion(descripcionCategoria);
        this.catalogo.setAyuda(descripcionAyuda);

        this.fichaAmbientalPma = new FichaAmbientalPma();
        this.fichaAmbientalPma.setId(idficha);
        this.seccion = seccion;


        //categoriaIICatalogoGeneralFisico.getCatalogo()
//        .getTipoCatalogo().getCodigo();
        // ct.caty_code,ct.caty_type
    }
    
    /**
     * Cris F: constructor para historico
     */
    
	public CategoriaIICatalogoGeneralSocial(Integer id, Boolean estado,
			String valor, String codigo, int idficha, Integer id_catalogo,
			String seccion, String tipoTipo, Integer caty_id,
			String descripcionCategoria, String descripcionAyuda, Date fechaHistorico, Integer idRegistroOriginal) {
		this.setId(id);
		this.setEstado(estado);
		this.setValor(valor);
		this.catalogo = new CatalogoGeneralSocial();
		TipoCatalogo tipo = new TipoCatalogo();
		tipo.setId(caty_id);
		tipo.setCodigo(codigo);
		tipo.setTipo(tipoTipo);
		this.catalogo.setTipoCatalogo(tipo);
		this.catalogo.setId(id_catalogo);
		this.catalogo.setOrden(0);
		this.catalogo.setDescripcion(descripcionCategoria);
		this.catalogo.setAyuda(descripcionAyuda);

		this.fichaAmbientalPma = new FichaAmbientalPma();
		this.fichaAmbientalPma.setId(idficha);
		this.seccion = seccion;
		this.setFechaHistorico(fechaHistorico);
		this.setIdRegistroOriginal(idRegistroOriginal);

		// categoriaIICatalogoGeneralFisico.getCatalogo()
		// .getTipoCatalogo().getCodigo();
		// ct.caty_code,ct.caty_type
	}
    
    
}
