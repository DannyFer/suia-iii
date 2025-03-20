/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.domain;

import java.io.Serializable;
import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
@Table(name = "findings_plan", schema = "suia_iii")
@NamedQueries({ @NamedQuery(name = PlanHallazgosEia.LISTAR_POR_EIA, query = "SELECT a FROM PlanHallazgosEia a WHERE a.estado = true AND a.eia.id = :idEstudioImpactoAmbiental and a.idHistorico = null"),
	@NamedQuery(name = PlanHallazgosEia.LISTAR_TODOS_POR_EIA, query = "SELECT a FROM PlanHallazgosEia a WHERE a.estado = true AND a.eia.id = :idEstudioImpactoAmbiental")})
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "fipl_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "fipl_status = 'TRUE'")
public class PlanHallazgosEia extends EntidadBase implements Serializable, Cloneable {

	private static final long serialVersionUID = -8848052212310885892L;

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR_POR_EIA = PAQUETE + "PlanHallazgosEia.listarPorEIA";
	public static final String LISTAR_TODOS_POR_EIA = PAQUETE + "PlanHallazgosEia.listarTodosPorEIA";

	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "PLAN_HALLAZGOS_ID_GENERATOR", initialValue = 1, sequenceName = "seq_fipl_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PLAN_HALLAZGOS_ID_GENERATOR")
	@Column(name = "fipl_id")
	private Integer id;

	@Getter
	@Setter
	@JoinColumn(name = "eist_id", referencedColumnName = "eist_id")
	@ForeignKey(name = "fk_findings_plan_eist_id_environmental_impact_studies_eist_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private EstudioImpactoAmbiental eia;

	@Getter
	@Setter
	@JoinColumn(name = "fiid_id", referencedColumnName = "fiid_id")
	@ForeignKey(name = "fk_findings_plan_fipl_id_environmental_impact_studies_fipl_id")
	@ManyToOne(fetch = FetchType.EAGER)
	private IdentificacionHallazgosEia hallazgos;

	@Getter
	@Setter
	@Column(name = "fipl_solutions")
	private String medidaPropuesta;

	@Getter
	@Setter
	@Column(name = "fipl_verification")
	private String medioVerificacion;

	@Getter
	@Setter
	@Column(name = "fipl_person_charge")
	private String responsable;

	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fipl_date_ini")
	private Date fechaInicio;

	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fipl_date_fin")
	private Date fechaFin;

	public PlanHallazgosEia() {

	}

	public PlanHallazgosEia(Integer id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PlanHallazgosEia)) {
			return false;
		}
		PlanHallazgosEia other = (PlanHallazgosEia) obj;
		if (((this.id == null) && (other.id != null)) || ((this.id != null) && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return getId().toString();
	}
	
	@Getter
	@Setter
	@Column(name = "fipl_historical_id")
	private Integer idHistorico;
	
	@Getter
	@Setter
	@Column(name = "fipl_notification_number")
	private Integer numeroNotificacion;
	
	@Getter
	@Setter
	@Transient
	private Boolean nuevoEnModificacion;
	
	@Getter
	@Setter
	@Transient
	private Boolean registroModificado;
	
	@Override
	public PlanHallazgosEia clone() throws CloneNotSupportedException {
		
		PlanHallazgosEia clone = (PlanHallazgosEia) super.clone();
		clone.setId(null);
		return clone;
	}
	
	//Cris F: aumento de campo para fecha
	@Getter
	@Setter
	@Column(name = "fipl_date_create")
	private Date fechaCreacion;
	
}
