/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

import java.util.Collection;

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

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

/**
 * 
 * @author christian
 */
@Entity
@Table(name = "project_description_work_activity", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "pdwa_status")) })
@NamedQueries({
		@NamedQuery(name = "ProyectoDescripcionTrabajoActividad.findAll", query = "SELECT p FROM ProyectoDescripcionTrabajoActividad p"),
		@NamedQuery(name = ProyectoDescripcionTrabajoActividad.LISTAR_POR_EIA, query = "SELECT p FROM ProyectoDescripcionTrabajoActividad p WHERE p.idEstudioImpactoAmbiental = :idEstudioImpactoAmbiental AND p.estado = true ORDER BY p.idCatalogoTipoActividad") })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "pdwa_status = 'TRUE'")
public class ProyectoDescripcionTrabajoActividad extends EntidadBase {

	private static final long serialVersionUID = 4013386343462940060L;

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR_POR_EIA = PAQUETE + "ProyectoDescripcionTrabajoActividad.listarPorEIA";

	@Id
	@Basic(optional = false)
	@Getter
	@Setter
	@SequenceGenerator(name = "PROYECTO_DESCRIPCION_ID_GENERATOR", initialValue = 1, sequenceName = "seq_pdwa_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROYECTO_DESCRIPCION_ID_GENERATOR")
	@Column(name = "pdwa_id", nullable = false)
	private Integer id;
	@Getter
	@Setter
	@Column(name = "pdwa_determination", length = 500)
	private String determinacion;

	@Getter
	@Setter
	@JoinColumn(name = "geca_type_activity_id", referencedColumnName = "geca_id")
	@ForeignKey(name = "fk_project_description_work_activity_geca_type_activity_id_gene")
	@ManyToOne(fetch = FetchType.EAGER)
	private CatalogoGeneral catalogoTipoActividad;

	@Getter
	@Setter
	@Column(name = "geca_type_activity_id", insertable = false, updatable = false)
	private Integer idCatalogoTipoActividad;

	@Getter
	@Setter
	@JoinColumn(name = "eist_id", referencedColumnName = "eist_id")
	@ForeignKey(name = "fk_project_description_work_activity_eist_id_environmental_impact_studies_eist_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private EstudioImpactoAmbiental eistId;

	@Getter
	@Setter
	@Column(name = "eist_id", insertable = false, updatable = false)
	private Integer idEstudioImpactoAmbiental;

	@Getter
	@Setter
	@OneToMany(mappedBy = "pdwaId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Collection<DetalleProyectoDescripcionTrabajoActividad> detalleProyectoDescripcionTrabajoActividadCollection;

	@Getter
	@Setter
	@Column(name = "pdwa_have_schedule")
	private Boolean tieneCronograma;

	public ProyectoDescripcionTrabajoActividad() {
	}

	public ProyectoDescripcionTrabajoActividad(Integer id) {
		this.id = id;
	}

}
