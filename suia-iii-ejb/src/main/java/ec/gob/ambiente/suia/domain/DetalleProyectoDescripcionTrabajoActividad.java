/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

/**
 * 
 * @author christian
 */
@Entity
@Table(name = "detail_project_description_work_activity", schema = "suia_iii")
@NamedQueries({
		@NamedQuery(name = "DetailProjectDescriptionWorkActivity.findAll", query = "SELECT d FROM DetalleProyectoDescripcionTrabajoActividad d"),
		@NamedQuery(name = DetalleProyectoDescripcionTrabajoActividad.LISTAR_POR_PROYECTO, query = "SELECT d FROM DetalleProyectoDescripcionTrabajoActividad d WHERE d.idProyecto = :idProyecto AND d.estado = true") })
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "dpdw_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "dpdw_status = 'TRUE'")
public class DetalleProyectoDescripcionTrabajoActividad extends EntidadBase {

	private static final long serialVersionUID = 3719941084648401044L;

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR_POR_PROYECTO = PAQUETE
			+ "DetalleProyectoDescripcionTrabajoActividad.listarPorProyecto";
	@Id
	@Basic(optional = false)
	@Getter
	@Setter
	@SequenceGenerator(name = "DETALLE_PROYECTO_DESCRIPCION_ID_GENERATOR", initialValue = 1, sequenceName = "seq_dpdw_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DETALLE_PROYECTO_DESCRIPCION_ID_GENERATOR")
	@Column(name = "dpdw_id", nullable = false)
	private Integer id;
	@Getter
	@Setter
	@Column(name = "dpdw_activity", length = 500)
	private String actividad;
	@Getter
	@Setter
	@JoinColumn(name = "pdwa_id", referencedColumnName = "pdwa_id")
	@ForeignKey(name = "fk_detail_project_description_work_activity_pdwa_id_project_description_work_activity_pdwa_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private ProyectoDescripcionTrabajoActividad pdwaId;
	@Getter
	@Setter
	@Column(name = "pdwa_id", insertable = false, updatable = false)
	private Integer idProyecto;
	@Getter
	@Setter
	@Column(name = "dpdw_date_from")
	@Temporal(TemporalType.DATE)
	private Date fechaDesde;
	@Getter
	@Setter
	@Column(name = "dpdw_date_until")
	@Temporal(TemporalType.DATE)
	private Date fechaHasta;
	@Getter
	@Setter
	@Transient
	private int indice;

	public DetalleProyectoDescripcionTrabajoActividad() {
	}

	public DetalleProyectoDescripcionTrabajoActividad(Integer id) {
		this.id = id;
	}
}
