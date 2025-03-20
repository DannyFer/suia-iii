/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos;

import ec.gob.ambiente.suia.domain.TipoEnvase;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import ec.gob.ambiente.suia.dto.EntityRecepcionDesecho;

/**
 * <b> Clase Para mapear la relacion de los almacenes con los desechos. </b>
 * 
 * @author Javier Lucero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Javier Lucero $, $Date: 09/06/2015 $]
 *          </p>
 */
@Entity
@Table(name = "warehouse_receipt_waste", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "wawa_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "wawa_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "wawa_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "wawa_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "wawa_user_update")) })
@NamedQueries({
		@NamedQuery(name = AlmacenRecepcion.LISTAR_POR_ID_ALMACEN, query = "SELECT a FROM AlmacenRecepcion a WHERE a.idAlmacen = :idAlmacen AND a.estado = TRUE"),
		@NamedQuery(name = AlmacenRecepcion.LISTAR_POR_ID_RECEPCION, query = "SELECT a FROM AlmacenRecepcion a WHERE a.idRecepcionDesechoPeligroso = :idRecepcionDesechoPeligroso AND a.estado = TRUE") })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wawa_status = 'TRUE'")
public class AlmacenRecepcion extends EntidadAuditable {

	/**
     *
     */
	private static final long serialVersionUID = 1L;
	private static final String PAQUETE_CLASE = "ec.gob.ambiente.suia.domain.AlmacenRecepcion.";
	public static final String LISTAR_POR_ID_ALMACEN = PAQUETE_CLASE + "obtenerPorIdidAlmacen";
	public static final String LISTAR_POR_ID_RECEPCION = PAQUETE_CLASE + "obtenerPorIdidRecepcionDesechoPeligroso";

	@Id
	@Column(name = "wawa_id")
	@SequenceGenerator(name = "WAREHOUSE_WASTE_GENERATOR", sequenceName = "seq_wawa_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WAREHOUSE_WASTE_GENERATOR")
	@Getter
	@Setter
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "waho_id")
	@ForeignKey(name = "warehouse_warehouse_waste_fk")
	@Getter
	@Setter
	private Almacen almacen;

	@Getter
	@Setter
	@Column(name = "waho_id", insertable = false, updatable = false)
	private Integer idAlmacen;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "paty_id")
	@ForeignKey(name = "fk_wawa_id_paty_id")
	private TipoEnvase tipoEnvase;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rehw_id")
	@ForeignKey(name = "warehouse_receipt_waste_receipt_hazardous_waste_fk")
	@Getter
	@Setter
	private RecepcionDesechoPeligroso recepcionDesechoPeligroso;

	@Getter
	@Setter
	@Column(name = "rehw_id", insertable = false, updatable = false)
	private Integer idRecepcionDesechoPeligroso;

	@Getter
	@Setter
	@Column(name = "wawa_other_package")
	private String otroTipoEnvase;

	@Getter
	@Setter
	@Transient
	private int indice;

	@Getter
	@Setter
	@Transient
	private EntityRecepcionDesecho entityRecepcionDesecho;

	@Getter
	@Setter
	@Transient
	private boolean editar;

}
