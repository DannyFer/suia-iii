/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;

import java.util.Date;
import java.util.List;

/**
 * 
 * @author ishmael
 */
@Entity
@Table(name = "forest_inventory_points", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "foip_status")) })
@NamedQueries({ @NamedQuery(name = InventarioForestalPuntos.LISTAR_POR_INVENTARIO, query = "SELECT i FROM InventarioForestalPuntos i WHERE i.inventarioForestal = :inventarioForestal AND i.estado = TRUE") })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "foip_status = 'TRUE'")
public class InventarioForestalPuntos extends EntidadBase implements Cloneable {

	private static final long serialVersionUID = -1810046713255316209L;

	public static final String LISTAR_POR_INVENTARIO = "ec.gob.ambiente.suia.domain.InventarioForestalPuntos.listarPorInventario";

	@Id
	@Basic(optional = false)
	@Getter
	@Setter
	@SequenceGenerator(name = "INVENTARIO_FORESTAL_PUNTOS_ID_GENERATOR", initialValue = 1, sequenceName = "seq_foip_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "INVENTARIO_FORESTAL_PUNTOS_ID_GENERATOR")
	@Column(name = "foip_id", nullable = false)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "foip_code_parcel", length = 100)
	private String codigoParcela;

	@Getter
	@Setter
	@Column(name = "foip_area_parcel")
	private Double areaParcela;

	@Getter
	@Setter
	@Column(name = "foip_index_simpson")
	private Integer indiceSimpson;

	@Getter
	@Setter
	@Column(name = "foip_index_shannon")
	private Integer indiceShannon;

	@Getter
	@Setter
	@Column(name = "foip_name_table_volume", length = 1000)
	private String nombreTablaVolumen;

	@Getter
	@Setter
	@Column(name = "foip_name_table_index_values_importance", length = 1000)
	private String nombreTablaIndiceValoresImportancia;

	@Getter
	@Setter
	@JoinColumn(name = "foin_id", referencedColumnName = "foin_id")
	@ForeignKey(name = "fk_forest_inventory_foin_id_forest_inventory_points_foin_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private InventarioForestal inventarioForestal;

	@Getter
	@Setter
	@OneToMany(mappedBy = "inventarioForestalPuntos")
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<CoordenadaGeneral> coordenadasGeneral;

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
	@Column(name = "foip_historical_id")
	private Integer idHistorico;

	@Getter
	@Setter
	@Column(name = "foip_notification_number")
	private Integer numeroNotificacion;
	
	@Transient
	@Getter
	@Setter
	private boolean puntoModificado = false;
	
	@Transient
	@Getter
	@Setter
	private boolean coordenadasModificadas;
	
	@Getter
	@Setter
	@Transient
	private Boolean nuevoEnModificacion;
	
	@Getter
	@Setter
	@Transient
	private Boolean registroModificado;
	
	@Getter
	@Setter
	@Column(name = "foip_date_create")
	private Date fechaCreacion;
	
	@Override
	public InventarioForestalPuntos clone() throws CloneNotSupportedException {

		 InventarioForestalPuntos clone = (InventarioForestalPuntos)super.clone();
		 clone.setId(null);		 
		 return clone;
	}


}
