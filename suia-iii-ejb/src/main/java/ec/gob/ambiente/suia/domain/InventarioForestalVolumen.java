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
@Table(name = "forest_inventory_volume", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "foiv_status")) })
@NamedQueries({ @NamedQuery(name = InventarioForestalVolumen.LISTAR_POR_INVENTARIO, query = "SELECT i FROM InventarioForestalVolumen i WHERE i.inventarioForestal = :inventarioForestal AND i.estado = TRUE") })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "foiv_status = 'TRUE'")
public class InventarioForestalVolumen extends EntidadBase {

	private static final long serialVersionUID = 9125303852030498738L;

	public static final String LISTAR_POR_INVENTARIO = "ec.gob.ambiente.suia.domain.InventarioForestalVolumen.listarPorInventario";

	@Id
	@Basic(optional = false)
	@Getter
	@Setter
	@SequenceGenerator(name = "INVENTARIO_FORESTAL_VOLUMEN_ID_GENERATOR", initialValue = 1, sequenceName = "seq_foiv_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "INVENTARIO_FORESTAL_VOLUMEN_ID_GENERATOR")
	@Column(name = "foiv_id", nullable = false)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "foiv_number")
	private Integer numero;

	@Getter
	@Setter
	@Column(name = "foiv_code", length = 100)
	private String codigo;

	@Getter
	@Setter
	@Column(name = "foiv_family", length = 200)
	private String familia;

	@Getter
	@Setter
	@Column(name = "foiv_scientific_name", length = 100)
	private String nombreCientifico;

	@Getter
	@Setter
	@Column(name = "foiv_common_name", length = 100)
	private String nombreComun;

	@Getter
	@Setter
	@Column(name = "foiv_diameter_breast_height")
	private Double diametroAlturaPecho;

	@Getter
	@Setter
	@Column(name = "foiv_total_height")
	private Double alturaTotal;

	@Getter
	@Setter
	@Column(name = "foiv_commercial_height")
	private Double alturaComercial;

	@Getter
	@Setter
	@Column(name = "foiv_basal_area")
	private Double areaBasal;

	@Getter
	@Setter
	@Column(name = "foiv_total_volume")
	private Double volumenTotal;

	@Getter
	@Setter
	@Column(name = "foiv_commercial_volume")
	private Double volumenComercial;

	@Getter
	@Setter
	@JoinColumn(name = "foin_id", referencedColumnName = "foin_id")
	@ForeignKey(name = "fk_forest_inventory_foin_id_forest_inventory_volume_foin_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private InventarioForestal inventarioForestal;

	@Transient
	@Getter
	@Setter
	private int indice;
	@Transient
	@Getter
	@Setter
	private boolean editar;

}
