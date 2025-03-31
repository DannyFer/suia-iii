/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

import java.math.BigDecimal;

/**
 * <b> Clase entidad de transporte de sustancias químicas peligrosas. </b>
 * 
 * @author Jonathan Guerrero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Jonathan Guerrero $, $Date: 08/06/2015 $]
 *          </p>
 */
@Entity
@Table(name = "detail_confinement_capacity_hazardous_waste", schema = "suia_iii")
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "decac_status = 'TRUE'")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "decac_status")) })
public class DetalleCapacidadConfinamientoDesechoPeligroso extends EntidadBase {

	/**
	* 
	*/
	private static final long serialVersionUID = 6699207686437022843L;

	@Id
	@Column(name = "decac_id")
	@SequenceGenerator(name = "DETAIL_CONFINEMENT_CAPACITY_HAZARDOUS_WASTE_GENERATOR", sequenceName = "seq_decac_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DETAIL_CONFINEMENT_CAPACITY_HAZARDOUS_WASTE_GENERATOR")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@Column(name = "decac_confinement_name")
	private String nombreLugarConfinamiento;

	@Getter
	@Setter
	@Column(name = "decac_m3_capacity")
	private double capacidadEnM3;

	@Getter
	@Setter
	@Column(name = "decac_ton_capacity")
	private BigDecimal capacidadEnTonelada;

	@Getter
	@Setter
	@Column(name = "decac_x_coordinate")
	private BigDecimal coordenadaX;

	@Getter
	@Setter
	@Column(name = "decac_y_coordinate")
	private BigDecimal coordenadaY;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "mofid_map")
	@ForeignKey(name = "fk_modality_final_disposal_mofid_map_documents_docu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	private ModalidadDisposicionFinal modalidadDisposicionFinal;

	@Getter
	@Setter
	@Column(name = "mofid_monthly_capacity_confinement")
	private BigDecimal capacidadMesConfinamiento;

}
