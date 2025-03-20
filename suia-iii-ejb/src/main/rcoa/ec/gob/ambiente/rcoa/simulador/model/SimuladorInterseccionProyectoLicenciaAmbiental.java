package ec.gob.ambiente.rcoa.simulador.model;


import java.io.Serializable;
import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.rcoa.model.CapasCoa;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "intersections_project_licencing_coa", schema = "coa_simulator")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "inpr_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "inpr_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "inpr_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "inpr_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "inpr_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "inpr_status = 'TRUE'")
public class SimuladorInterseccionProyectoLicenciaAmbiental extends EntidadAuditable implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1804152954490635893L;
	
	@Getter
	@Setter
	@Id
	@Column(name = "inpr_id")
	@SequenceGenerator(name = "PROJECTS_ENVIRONMENTAL_LICENSING_INPR_ID_GENERATOR", sequenceName = "intersections_project_licencing_coa_inpr_id_seq", schema = "coa_mae", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROJECTS_ENVIRONMENTAL_LICENSING_INPR_ID_GENERATOR")
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "laye_id")
	@ForeignKey(name = "fk_intersections_project_layer_id_layers_layer_id")
	@Getter
	@Setter
	private CapasCoa capa;
	
	@ManyToOne
	@JoinColumn(name = "prco_id")
	@ForeignKey(name = "fk_intersections_project_pren_id_project_envirolm_licen_prco_id")
	@Getter
	@Setter
	private SimuladorProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Getter
	@Setter
	@Temporal(TemporalType.DATE)
	@Column(name = "inpr_process_date")
	private Date fechaproceso;
	
	@Getter
	@Setter
	@Column(name = "inpr_layer_description", length = 255)
	private String descripcionCapa;
	
	@Getter
	@Setter
	@Column(name = "inpr_observation_bd", length = 255)
	private String observacionDB;	
	
}
