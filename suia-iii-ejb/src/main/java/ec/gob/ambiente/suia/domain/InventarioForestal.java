/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.domain;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * @author ishmael
 */
@Entity
@Table(name = "forest_inventory", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "foin_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "foin_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "foin_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "foin_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "foin_user_update")) })
@NamedQueries({ @NamedQuery(name = InventarioForestal.OBTENER_POR_EIA, query = "SELECT i FROM InventarioForestal i WHERE i.estudioImpactoAmbiental = :estudioImpactoAmbiental and i.idHistorico = null") })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "foin_status = 'TRUE'")
public class InventarioForestal extends EntidadAuditable implements Cloneable {

	/**
	 *
	 */
	private static final long serialVersionUID = 6533405657798180077L;

	public static final String OBTENER_POR_EIA = "ec.gob.ambiente.suia.domain.InventarioForestal.obtenerPorEia";

	@Id
	@Basic(optional = false)
	@Getter
	@Setter
	@SequenceGenerator(name = "INVENTARIO_FORESTAL_ID_GENERATOR", initialValue = 1, sequenceName = "seq_foin_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "INVENTARIO_FORESTAL_ID_GENERATOR")
	@Column(name = "foin_id", nullable = false)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "foin_project_intervention_area")
	private Double areaInversionProyecto;

	@Getter
	@Setter
	@Column(name = "foin_area_removal_vegetation_coverage")
	private Double areaPorRemocionDeCoverturaVejetal;

	@Getter
	@Setter
	@Column(name = "foin_surface_sampling")
	private Double superficieMuestreo;

	@Getter
	@Setter
	@Column(name = "foin_value_carbon_sequestration")
	private Double capturaDeCarbono;

	@Getter
	@Setter
	@Column(name = "foin_value_tourism_scenic_beauty")
	private Double turismoBellezaEscenica;

	@Getter
	@Setter
	@Column(name = "foin_water_resource_value")
	private Double recursoAgua;

	@Getter
	@Setter
	@Column(name = "foin_value_timber_non")
	private Double productosForestalesMaderablesYNoMaderables;

	@Getter
	@Setter
	@Column(name = "foin_value_medicinal_plants")
	private Double plantasMedicinales;

	@Getter
	@Setter
	@Column(name = "foin_value_ornamental_plants")
	private Double plantasOrnamentales;

	@Getter
	@Setter
	@Column(name = "foin_value_crafts")
	private Double artesanias;

	@Getter
	@Setter
	@Column(name = "foin_value_carbon_sequestration_justification")
	private String capturaDeCarbonJustificacion;

	@Getter
	@Setter
	@Column(name = "foin_value_tourism_scenic_beauty_justification")
	private String turismoBellezaEscenicaJustificacion;

	@Getter
	@Setter
	@Column(name = "foin_water_resource_value_justification")
	private String recursoAguaJustificacion;

	@Getter
	@Setter
	@Column(name = "foin_value_timber_non_justification")
	private String productosForestalesMaderablesYNoMaderablesJustificacion;

	@Getter
	@Setter
	@Column(name = "foin_value_medicinal_plants_justification")
	private String plantasMedicinalesJustificacion;

	@Getter
	@Setter
	@Column(name = "foin_value_ornamental_plants_justification")
	private String plantasOrnamentalesJustificacion;

	@Getter
	@Setter
	@Column(name = "foin_value_crafts_justification")
	private String artesaniasJustificacion;

	@Getter
	@Setter
	@Column(name = "foin_average_ab")
	private Double promedioAb;

	@Getter
	@Setter
	@Column(name = "foin_total_volume_sampled_area")
	private Double volumenTotal;

	@Getter
	@Setter
	@Column(name = "foin_volume_commercial_area")
	private Double volumenComercial;

	@Getter
	@Setter
	@Column(name = "foin_total_average_volume_per_hectare")
	private Double volumenTotalPromedio;

	@Getter
	@Setter
	@Column(name = "foin_commercial_average_volume_per_hectare")
	private Double volumenComercialPromedio;

	@Getter
	@Setter
	@Column(name = "foin_total_volume_extrapolated_total_area_afectarce")
	private Double volumenTotalExtrapolado;

	@Getter
	@Setter
	@Column(name = "foin_commercial_volume_extrapolated_total_area_afectarce")
	private Double volumenComercialExtrapolado;

	@Getter
	@Setter
	@Column(name = "sutc_justification_species_under_threat_category", length = 1000)
	private String justificacionEspeciesBajoCategoriaAmenaza;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "eist_id")
	@ForeignKey(name = "fk_environmental_impact_studieseist_id_forest_inventory_id")
	@Getter
	@Setter
	private EstudioImpactoAmbiental estudioImpactoAmbiental;

	@Getter
	@Setter
	@OneToMany(mappedBy = "inventarioForestal", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<InventarioForestalPuntos> inventarioForestalPuntosList;

	@Getter
	@Setter
	@OneToMany(mappedBy = "inventarioForestal", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<InventarioForestalVolumen> inventarioForestalVolumenList;

	@Getter
	@Setter
	@OneToMany(mappedBy = "inventarioForestal", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<InventarioForestalIndice> inventarioForestalIndiceList;

	@Getter
	@Setter
	@OneToMany(mappedBy = "inventarioForestal", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<EspeciesBajoCategoriaAmenaza> especiesBajoCategoriaAmenazaList;

	@Getter
	@Setter
	@Column(name = "foin_vegetal_removal")
	private Boolean remocionVegetal;

	@Getter
	@Setter
	@Column(name = "foin_has_forest_inventory")
	private Boolean tieneInventarioForestal;
	
	/**
	 * Cris F: Aumento de variables y métodos para historial
	 */
	@Getter
	@Setter
	@Column(name = "foin_historical_id")
	private Integer idHistorico;
	
	@Getter
	@Setter
	@Column(name = "foin_notification_number")
	private Integer numeroNotificacion;
	
	@Getter
	@Setter
	@Transient
	private Double totalValoracion;
	
	@Override
	public InventarioForestal clone() throws CloneNotSupportedException {

		 InventarioForestal clone = (InventarioForestal)super.clone();
		 clone.setId(null);		 
		 return clone;
	}

}
