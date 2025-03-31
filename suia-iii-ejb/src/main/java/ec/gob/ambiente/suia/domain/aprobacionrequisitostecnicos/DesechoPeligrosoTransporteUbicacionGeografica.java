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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * <b> Clase entidad para la ubicacion geografica con la transportacion con los
 * desechos. </b>
 * 
 * @author Javier Lucero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Javier Lucero $, $Date: 22/06/2015 $]
 *          </p>
 */
@Entity
@Table(name = "waste_dangerous_transportation_locations", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "wdtl_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wdtl_status = 'TRUE'")
@NamedQueries(@NamedQuery(name = DesechoPeligrosoTransporteUbicacionGeografica.LISTAR_POR_ID_DESECHO, query = "SELECT d FROM DesechoPeligrosoTransporteUbicacionGeografica d WHERE d.idDesechoPeligrosoTransporte = :idDesechoPeligrosoTransporte AND d.estado = TRUE"))
public class DesechoPeligrosoTransporteUbicacionGeografica extends EntidadBase {

	/**
	* 
	*/
	private static final long serialVersionUID = 1L;
	private static final String PAQUETE_CLASE = "ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.";
	public static final String LISTAR_POR_ID_DESECHO = PAQUETE_CLASE + "obtenerPorIdidAlmacen";

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "WASTE_DANGEROUS_TRANSPORTATION_LOCATIONS_ID_GENERATOR", sequenceName = "seq_wdtl_id", allocationSize = 1, schema = "suia_iii")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WASTE_DANGEROUS_TRANSPORTATION_LOCATIONS_ID_GENERATOR")
	@Column(name = "wdtl_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "gelo_id")
	@ForeignKey(name = "waste_dangerous_transportation_locations_geographical_locations_fk")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "gelo_status = 'TRUE'")
	private UbicacionesGeografica ubicacionGeografica;

	@Getter
	@Setter
	@Column(name = "gelo_id", insertable = false, updatable = false)
	private Integer idUbicacionGeografica;

	@Setter
	@Getter
	@ManyToOne
	@JoinColumn(name = "wadt_id")
	@ForeignKey(name = "waste_dangerous_transportation_locations_fk")
	private DesechoPeligrosoTransporte desechoPeligrosoTransporte;

	@Getter
	@Setter
	@Column(name = "wadt_id", insertable = false, updatable = false)
	private Integer idDesechoPeligrosoTransporte;

	@Setter
	@Getter
	@Column(name = "wadt_is_origin")
	private boolean esOrigen;

	@Getter
	@Setter
	@Transient
	private int indice;
}
