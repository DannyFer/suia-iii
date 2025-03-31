/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
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
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

/**
 *
 * @author ishmael
 */
@Entity
@Table(name = "species_under_threat_category", schema = "suia_iii")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "sutc_status"))})
@NamedQueries({
    @NamedQuery(name = EspeciesBajoCategoriaAmenaza.LISTAR_POR_INVENTARIO, query = "SELECT e FROM EspeciesBajoCategoriaAmenaza e WHERE e.inventarioForestal = :inventarioForestal AND e.estado = TRUE")})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "sutc_status = 'TRUE'")
public class EspeciesBajoCategoriaAmenaza extends EntidadBase implements Cloneable {

	private static final long serialVersionUID = 3524919506465413606L;

	public static final String LISTAR_POR_INVENTARIO = "ec.gob.ambiente.suia.domain.EspeciesBajoCategoriaAmenaza.listarPorInventario";

    @Id
    @Basic(optional = false)
    @Getter
    @Setter
    @SequenceGenerator(name = "ESPECIES_BAJO_CATEGORIA_AMENAZA_ID_GENERATOR", initialValue = 1, sequenceName = "seq_sutc_id", schema = "suia_iii", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ESPECIES_BAJO_CATEGORIA_AMENAZA_ID_GENERATOR")
    @Column(name = "sutc_id", nullable = false)
    private Integer id;

    @Getter
    @Setter
    @Column(name = "sutc_frequency")
    private Double frecuencia;

    @Getter
    @Setter
    @Column(name = "sutc_other_scientific_name", length = 100)
    private String otroNombreCientifico;

    @Getter
    @Setter
    @Column(name = "sutc_common_name", length = 100)
    private String nombreComun;

    @Getter
    @Setter
    @Column(name = "sutc_uses", length = 200)
    private String usos;

    @Getter
    @Setter
    @JoinColumn(name = "foin_id", referencedColumnName = "foin_id")
    @ForeignKey(name = "fk_forest_inventory_foin_id_species_under_threat_category_foin_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private InventarioForestal inventarioForestal;

    @Getter
    @Setter
    @JoinColumn(name = "borc_id", referencedColumnName = "borc_id")
    @ForeignKey(name = "fk_book_red_catalog_borc_id_species_under_threat_category_foin_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private CatalogoLibroRojo catalogoLibroRojo;

    @Getter
    @Setter
    @Column(name = "sutc_scientific_name", length = 100)
    private String nombreCientifico;

    @Transient
    @Getter
    @Setter
    private int indice;
    @Transient
    @Getter
    @Setter
    private boolean editar;
    
    @Getter
    @Setter
    @Column(name = "sutc_historical_id")
    private Integer idHistorico;
    
	@Getter
	@Setter
	@Column(name = "sutc_notification_number")
	private Integer numeroNotificacion;
	
	@Getter
	@Setter
	@Transient
	private Boolean nuevoEnModificacion;
	
	@Getter
	@Setter
	@Transient
	private Boolean registroModificado;
	
	@Override
	public EspeciesBajoCategoriaAmenaza clone() throws CloneNotSupportedException {

		 EspeciesBajoCategoriaAmenaza clone = (EspeciesBajoCategoriaAmenaza)super.clone();
		 clone.setId(null);		 
		 return clone;
	}

}
