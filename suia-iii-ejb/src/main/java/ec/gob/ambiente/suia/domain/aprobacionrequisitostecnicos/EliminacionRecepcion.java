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

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import ec.gob.ambiente.suia.dto.EntityRecepcionDesecho;
import java.util.List;
import javax.persistence.OneToMany;

/**
 * <b> Clase entidad para la eliminacion con la recepcion. </b>
 * 
 * @author Javier Lucero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Javier Lucero $, $Date: 25/06/2015 $]
 *          </p>
 */
@Entity
@Table(name = "waste_disposal_receipt", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "wadr_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "wadr_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "wadr_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "wadr_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "wadr_user_update")) })
@NamedQueries({
		@NamedQuery(name = EliminacionRecepcion.LISTAR_POR_ID_APROBACION, query = "SELECT a FROM EliminacionRecepcion a WHERE a.idAprobacionRequisitosTecnicos = :idAprobacionRequisitosTecnicos AND a.estado = TRUE"),
		@NamedQuery(name = EliminacionRecepcion.LISTAR_POR_ID_RECEPCION, query = "SELECT a FROM EliminacionRecepcion a WHERE a.idRecepcionDesechoPeligroso = :idRecepcionDesechoPeligroso AND a.estado = TRUE") })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wadr_status = 'TRUE'")
public class EliminacionRecepcion extends EntidadAuditable {

	/**
	* 
	*/
	private static final long serialVersionUID = 1L;
	private static final String PAQUETE_CLASE = "ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.EliminacionRecepcion.";
	public static final String LISTAR_POR_ID_APROBACION = PAQUETE_CLASE + "obtenerPorIdAprobacion";
	public static final String LISTAR_POR_ID_RECEPCION = PAQUETE_CLASE + "obtenerPorIdRecepcionDesechoPeligroso";

	@Id
	@Column(name = "wadr_id")
	@SequenceGenerator(name = "WASTE_DISPOSAL_RECEIPT_GENERATOR", sequenceName = "seq_wadr_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WASTE_DISPOSAL_RECEIPT_GENERATOR")
	@Getter
	@Setter
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rehw_id")
	@ForeignKey(name = "receipt_hazardous_waste_disposal_receipt_fk")
	@Getter
	@Setter
	private RecepcionDesechoPeligroso recepcion;

	@Getter
	@Setter
	@Column(name = "rehw_id", insertable = false, updatable = false)
	private Integer idRecepcionDesechoPeligroso;

	@Getter
	@Setter
	@ManyToOne()
	@JoinColumn(name = "apte_id")
	@ForeignKey(name = "waste_disposal_receipt_approval_requirement_apte_fk")
	private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;

	@Getter
	@Setter
	@Column(name = "apte_id", insertable = false, updatable = false)
	private Integer idAprobacionRequisitosTecnicos;

	@Getter
	@Setter
	@OneToMany(mappedBy = "eliminacionRecepcion", fetch = FetchType.LAZY)
	private List<EliminacionDesecho> eliminacionDesechos;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		EliminacionRecepcion other = (EliminacionRecepcion) obj;
		if (eliminacionDesechos == null) {
			if (other.eliminacionDesechos != null)
				return false;
		} else if (!eliminacionDesechos.equals(other.eliminacionDesechos))
			return false;
		if (entityRecepcionDesecho == null) {
			if (other.entityRecepcionDesecho != null)
				return false;
		} else if (!entityRecepcionDesecho.equals(other.entityRecepcionDesecho))
			return false;
		return true;
	}

}
