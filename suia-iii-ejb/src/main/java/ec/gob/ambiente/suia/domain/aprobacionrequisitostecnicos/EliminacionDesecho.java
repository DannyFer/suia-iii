/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos;

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
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.TipoEliminacionDesecho;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * <b> Clase entidad para la eliminacion de desechos. </b>
 * 
 * @author Javier Lucero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Javier Lucero $, $Date: 11/06/2015 $]
 *          </p>
 */
@Entity
@Table(name = "waste_disposal", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "wadi_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "wadi_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "wadi_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "wadi_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "wadi_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wadi_status = 'TRUE'")
@NamedQueries({ @NamedQuery(name = EliminacionDesecho.LISTAR_POR_ID_ELIMINACION, query = "SELECT a FROM EliminacionDesecho a WHERE a.idEliminacionRecepcion = :idEliminacionRecepcion AND a.estado = TRUE") })
public class EliminacionDesecho extends EntidadAuditable {

	/**
     *
     */
	private static final long serialVersionUID = 1L;
	private static final String PAQUETE_CLASE = "ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.EliminacionDesecho.";
	public static final String LISTAR_POR_ID_ELIMINACION = PAQUETE_CLASE + "obtenerPorIdElimicacion";

	@Id
	@Column(name = "wadi_id")
	@SequenceGenerator(name = "WASTE_DISPOSAL_GENERATOR", sequenceName = "seq_wadi_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WASTE_DISPOSAL_GENERATOR")
	@Getter
	@Setter
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "wdty_id")
	@ForeignKey(name = "waste_disposal_types_waste_disposal_fk")
	@Getter
	@Setter
	private TipoEliminacionDesecho tipoEliminacionDesecho;

	@Getter
	@Setter
	@Column(name = "wadi_cantidad")
	private Double cantidad;

	@Getter
	@Setter
	@Column(name = "wadi_special_quantity")
	private Double cantidadEspecial;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "wada_id")
	@ForeignKey(name = "waste_dangerous_waste_disposal_fk")
	@Getter
	@Setter
	private DesechoPeligroso desecho;

	@Getter
	@Setter
	@Column(name = "wada_id", insertable = false, updatable = false)
	private Integer idDesecho;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "wadr_id")
	@ForeignKey(name = "waste_disposal_receipt_waste_disposal_fk")
	@Getter
	@Setter
	private EliminacionRecepcion eliminacionRecepcion;

	@Getter
	@Setter
	@Column(name = "wadr_id", insertable = false, updatable = false)
	private Integer idEliminacionRecepcion;

	@Getter
	@Setter
	@Column(name = "wadi_name_waste")
	private String nombreDesecho;

	@Getter
	@Setter
	@Column(name = "wadi_other_waste")
	private String otroDesecho;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "gelo_id")
	private UbicacionesGeografica cantonDestino;

	@Getter
	@Setter
	@Column(name = "wadi_hazardous_waste")
	private boolean esDesechoPeligro;

	@Getter
	@Setter
	@Column(name = "wadi_special_waste")
	private boolean desechoEspecial;

	@Getter
	@Setter
	@Column(name = "wadi_code_generator")
	private String codigoGenerador;

	@Getter
	@Setter
	@Transient
	private int indice;

	@Getter
	@Setter
	@Transient
	private boolean editar;

	/**
	 * @param id
	 */
	public EliminacionDesecho(Integer id) {
		super();
		this.id = id;
	}

	/**
     *
     */
	public EliminacionDesecho() {
		super();
	}

}
