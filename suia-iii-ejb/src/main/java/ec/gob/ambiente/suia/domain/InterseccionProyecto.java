package ec.gob.ambiente.suia.domain;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.rcoa.model.CapasCoa;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * <b> Entity que representa la tabla del resultado de la interseccion proyecto.
 * </b>
 * 
 * @author veronica rodriguez
 */
@Entity		   
@Table(name = "intersections_project", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "inpr_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "inpr_status = 'TRUE'")
@NamedQueries({ 
	@NamedQuery(name = "InterseccionProyecto.findByCodeProject", query = "SELECT ip FROM InterseccionProyecto ip, ProyectoLicenciamientoAmbiental p where ip.proyecto=p and p.codigo = :codigoProyecto and ip.intersectaConCapaAmortiguamiento=false"),
	@NamedQuery(name = "InterseccionProyecto.findByCodeProjectUpdated", query = "SELECT ip FROM InterseccionProyecto ip, ProyectoLicenciamientoAmbiental p where ip.proyecto=p and p.codigo = :codigoProyecto and ip.nroActualizacion =:nroActualizacion")
	})
public class InterseccionProyecto extends EntidadBase {

	private static final long serialVersionUID = -5688436551899781269L;

	@Getter
	@Setter
	@Id
	@Column(name = "inpr_id")
	private Integer id;
	@Getter
	@Setter
	@JoinColumn(name = "laye_id", referencedColumnName = "laye_id")
	@ManyToOne(fetch = FetchType.LAZY)
	@ForeignKey(name = "fk_intersections_project_laye_id_layers_laye_id")
	private Capa capa;

	@Getter
	@Setter
	@JoinColumn(name = "pren_id", referencedColumnName = "pren_id")
	@ManyToOne(fetch = FetchType.LAZY)
	@ForeignKey(name = "fk_intersections_project_pren_id_projects_environmental_pren_id")
	private ProyectoLicenciamientoAmbiental proyecto;

	@Getter
	@Setter
	@Temporal(TemporalType.DATE)
	@Column(name = "inpr_process_date")
	private Date fecha;

	@Getter
	@Setter
	@Column(name = "inpr_layer_description")
	private String descripcion;

	@Getter
	@Setter
	@Column(name = "inpr_buffer_intersection")
	private Boolean intersectaConCapaAmortiguamiento;
	
	@ManyToOne
	@JoinColumn(name = "coa_laye_id")
	@ForeignKey(name = "fk_coa_laye_id_coa_layers_id")
	@Getter
	@Setter
	private CapasCoa capaCoa;
	
	@Getter
	@Setter
	@Column(name = "inpr_update_number")
	private Integer nroActualizacion = 0;
	
	@Getter
	@Setter
	@Column(name = "id_4_cat")
	private String idCuatroCategorias;
	
}
