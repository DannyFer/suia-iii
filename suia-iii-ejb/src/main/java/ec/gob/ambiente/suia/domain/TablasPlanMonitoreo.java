/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.domain;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 *
 * @author oscar campana
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Oscar Campana, Fecha: 27/07/2015]
 *          </p>
 */
@Entity
@Table(name = "monitoring_tables", schema = "suia_iii")
@NamedQueries({ @NamedQuery(name = TablasPlanMonitoreo.LISTAR_POR_PLAN, query = "SELECT a FROM TablasPlanMonitoreo a WHERE a.estado = true AND a.planMonitoreoEia.id = :idPlanMonitoreo") })
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "mota_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "mota_status = 'TRUE'")
public class TablasPlanMonitoreo extends EntidadBase implements Cloneable {

	private static final long serialVersionUID = 273403518780994630L;

    private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
    public static final String LISTAR_POR_PLAN = PAQUETE + "TablasPlanMonitoreo.listarPorPlan";

	@Id
	@SequenceGenerator(name = "PLAN_TABLES_GENERATOR", schema = "suia_iii", sequenceName = "seq_mota_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PLAN_TABLES_GENERATOR")
	@Column(name = "mota_id")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@JoinColumn(name = "plmo_id", referencedColumnName = "plmo_id")
	@ForeignKey(name = "fk_monitoring_plan_plmo_id_monitoring_tables_plmo_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private PlanMonitoreoEia planMonitoreoEia;

	@Getter
	@Setter
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "tablasPlanMonitoreo", fetch = FetchType.LAZY)
	private List<ParametrosPlanMonitoreo> parametrosPlanMonitoreo;


	@Getter
	@Setter
	@Column(name = "mota_description")
	private String descripcion;

	@Getter
	@Setter
	@Column(name = "mota_refenrences_id")
	private Integer idReferencia;

	@Getter
	@Setter
	@Column(name = "mota_parameters")
	private String parametros;
	
	@Getter
	@Setter
	@Column(name = "mota_historical_id")
	private Integer idHistorico;
	
	
	@Getter
	@Setter
	@Column(name = "mota_notification_number")
	private Integer numeroNotificacion;
	
	@Getter
	@Setter
	@Transient
	private Boolean nuevoEnModificacion;
	
	@Getter
	@Setter
	@Transient
	private Boolean registroModificado;

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.descripcion;
	}
	
	@Override
	public TablasPlanMonitoreo clone() throws CloneNotSupportedException {

		 TablasPlanMonitoreo clone = (TablasPlanMonitoreo)super.clone();
		 clone.setId(null);		 
		 return clone;
	}	
	
	public TablasPlanMonitoreo (Integer id){
		this.id = id;
	}
	
	public TablasPlanMonitoreo (){
		
	}
	
	//Cris F: aumento para fecha
	@Getter
	@Setter
	@Column(name = "mota_date_create")
	private Date fechaCreacion;
	
}
