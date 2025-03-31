/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 *
 * @author Oscar Campana
 */
@Entity
@Table(name = "monitoring_plan", schema = "suia_iii")
@NamedQueries({ @NamedQuery(name = PlanMonitoreoEia.LISTAR_POR_EIA, query = "SELECT a FROM PlanMonitoreoEia a WHERE a.estado = true AND a.eia.id = :idEstudioImpactoAmbiental AND a.idHistorico = null order by 1"),
	@NamedQuery(name = PlanMonitoreoEia.LISTAR_TODOS_POR_EIA, query = "SELECT a FROM PlanMonitoreoEia a WHERE a.estado = true AND a.eia.id = :idEstudioImpactoAmbiental order by 1")})
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "mopl_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "mopl_status = 'TRUE'")
public class PlanMonitoreoEia extends EntidadBase implements Serializable, Cloneable {

	private static final long serialVersionUID = -8848052343734885892L;

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR_POR_EIA = PAQUETE + "PlanMonitoreoEia.listarPorEIA";
	public static final String LISTAR_TODOS_POR_EIA = PAQUETE + "PlanMonitoreoEia.listarTodosPorEIA";

	
	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "PLAN_MONITOREO_ID_GENERATOR", initialValue = 1, sequenceName = "seq_mopl_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PLAN_MONITOREO_ID_GENERATOR")
	@Column(name = "plmo_id")
	private Integer id;

	@Getter
	@Setter
	@JoinColumn(name = "eist_id", referencedColumnName = "eist_id")
	@ForeignKey(name = "fk_monitoring_plan_eist_id_environmental_impact_studies_eist_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private EstudioImpactoAmbiental eia;

	@Getter
	@Setter
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "planMonitoreoEia", fetch = FetchType.LAZY)
	private List<TablasPlanMonitoreo> tablasPlanMonitoreo;

	@Getter
	@Setter
	@OneToOne
	@ForeignKey(name = "fk_regulation_regu_id_monitoring_plan_regu_id")
	@JoinColumn(name = "regu_id")
	private Normativas normativas;

	@Getter
	@Setter
	@Column(name = "plmo_ambiental_component")
	private String componente;

	@Getter
	@Setter
	@Column(name = "plmo_component_type")
	private String tipoComponente;

	@Getter
	@Setter
	@Column(name = "plmo_x")
	private Double coordenadaX;

	@Getter
	@Setter
	@Column(name = "plmo_y")
	private Double coordenadaY;

	@Getter
	@Setter
	@Column(name = "plmo_monitoring_frecuency")
	private Integer frecuencia;

	@Getter
	@Setter
	@Column(name = "plmo_perodicity_frecuency")
	private String periodicidad;
	
	@Getter
	@Setter
	@Column(name = "plmo_historical_id")
	private Integer idHistorico;
	
	@Getter
	@Setter
	@Column(name = "plmo_notification_number")
	private Integer numeroNotificacion;
	
	@Getter
	@Setter
	@Transient
	private Boolean nuevoEnModificacion;
	
	@Getter
	@Setter
	@Transient
	private Boolean registroModificado;
	
	@Getter
	@Setter
	@Transient
	private Boolean tablasModificadas;


	public PlanMonitoreoEia(){

	}

	public PlanMonitoreoEia(Integer id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PlanMonitoreoEia)) {
			return false;
		}
		PlanMonitoreoEia other = (PlanMonitoreoEia) obj;
		if (((this.id == null) && (other.id != null)) || ((this.id != null) && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return getComponente();
	}
	
	@Override
	public PlanMonitoreoEia clone() throws CloneNotSupportedException {

		 PlanMonitoreoEia clone = (PlanMonitoreoEia)super.clone();
		 clone.setId(null);		 
		 return clone;
	}
	
	//Cris F: afumento de campo para fecha de historial
	@Getter
	@Setter
	@Column(name = "plmo_date_create")
	private Date fechaCreacion;
	
	
	
	
}


