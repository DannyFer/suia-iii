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

/**
 * <b> Clase entidad de detalle de capacidad total de almacenamiento de materias
 * primas para desechos peligrosos. </b>
 * 
 * @author Jonathan Guerrero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Jonathan Guerrero $, $Date: 23/06/2015 $]
 *          </p>
 */
@Entity
@Table(name = "detail_total_storage_capacity_raw_materials_hazardous_waste", schema = "suia_iii")
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "detos_status = 'TRUE'")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "detos_status")) })
public class DetalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligroso extends EntidadBase {
	/**
	* 
	*/
	private static final long serialVersionUID = 1068682874307709572L;

	@Id
	@Column(name = "detos_id")
	@SequenceGenerator(name = "DETAIL_TOTAL_STORAGE_CAPACITY_RAW_MATERIALS_HAZARDOUS_WASTE_GENERATOR", sequenceName = "seq_detos_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DETAIL_TOTAL_STORAGE_CAPACITY_RAW_MATERIALS_HAZARDOUS_WASTE_GENERATOR")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@Column(name = "detos_storage_name")
	private String nombreLugarAlmacenamiento;

	@Getter
	@Setter
	@Column(name = "detos_m3_capacity")
	private double capacidadEnM3;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "mofid_map")
	@ForeignKey(name = "fk_modality_final_disposal_mofid_map_documents_docu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	private ModalidadDisposicionFinal modalidadDisposicionFinal;
}
