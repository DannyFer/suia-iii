package ec.gob.ambiente.suia.domain;

import java.io.Serializable;

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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the categories_catalog database table. Frank Torres
 * 
 */
@Entity
@Table(name = "general_catalogs_social", catalog = "", schema = "public")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "gcso_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "gcso_status = 'TRUE'")
@NamedQueries({
		@NamedQuery(name = "CatalogoGeneralSocial.findAll", query = "SELECT c FROM CatalogoGeneralSocial c"),
		@NamedQuery(name = "CatalogoGeneralSocial.findByType", query = "SELECT c FROM CatalogoGeneralSocial c where c.estado = true and c.tipoCatalogo.id = :p_tipoId order by c.id"),
		@NamedQuery(name = "CatalogoGeneralSocial.findByParent", query = "SELECT c FROM CatalogoGeneralSocial c where c.estado = true and c.catalogoPadre.id = :p_padreId"),
		@NamedQuery(name = "CatalogoGeneralSocial.findByParentOrderById", query = "SELECT c FROM CatalogoGeneralSocial c where c.estado = true and c.catalogoPadre.id = :p_padreId ORDER BY c.id") })
public class CatalogoGeneralSocial extends EntidadBase implements Serializable {

	/**
     *
     */
	private static final long serialVersionUID = -3947900087722750769L;

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR_POR_TIPOID_CODIGO = PAQUETE
			+ "CatalogoGeneralSocial.listarPorTipoIdCodigo";

	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "GENERALCATALOGPH_GCSOID_GENERATOR", sequenceName = "seq_gcso_id", schema = "public")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GENERALCATALOGPH_GCSOID_GENERATOR")
	@Column(name = "gcso_id", unique = true, nullable = false)
	private Integer id;

	@Getter
	@Setter
	@JoinColumn(name = "gcso_parent_id", referencedColumnName = "gcso_id", nullable = true)
	@ForeignKey(name = "fk_gcso_id_to_gcso_id")
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private CatalogoGeneralSocial catalogoPadre;

	@Getter
	@Setter
	@Column(name = "gcso_description", nullable = false, length = 255)
	private String descripcion;
	
	@Getter
	@Setter
	@Column(name = "gcso_help", nullable = true, length = 655)
	private String ayuda;

	@Getter
	@Setter
	@Column(name = "sector_id", nullable = true, length = 255)
	private Integer sectorId;

	@Getter
	@Setter
	@Column(name = "gcso_weight", nullable = true)
	private Integer orden;

	@Getter
	@Setter
	@JoinColumn(name = "caty_id", referencedColumnName = "caty_id", nullable = false)
	@ForeignKey(name = "fk_caty_id_to_gcso_id")
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private TipoCatalogo tipoCatalogo;

	@Override
	public String toString() {
		return this.descripcion;
	}
	
	public CatalogoGeneralSocial(){
		
	}
	
	public CatalogoGeneralSocial(Integer id){
		this.id = id;
	}

    public CatalogoGeneralSocial(Integer id, String descripcion, String ayuda, String codigo, String tipo, Integer tipo_id,
                                 Boolean estado, Integer orden, Integer id_padre, Integer sector) {
        this.id = id;
        this.descripcion = descripcion;
        this.ayuda = ayuda;
        this.tipoCatalogo = new TipoCatalogo();
        this.tipoCatalogo.setCodigo(codigo);
        this.tipoCatalogo.setTipo(tipo);
        this.tipoCatalogo.setId(tipo_id);
        this.estado = estado;
        this.orden  = orden;

    }
}
