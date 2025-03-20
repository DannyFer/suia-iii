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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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
@Table(name = "environmental_management_plan_eia", schema = "suia_iii")
@NamedQueries({ @NamedQuery(name = PlanManejoAmbientalEIA.LISTAR_POR_EIA, query = "SELECT a FROM PlanManejoAmbientalEIA a WHERE a.estado = true and a.tipo = :tipoPlan AND a.eia.id = :idEstudioImpactoAmbiental AND a.idHistorico = null") })
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "empl_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "empl_status = 'TRUE'")
public class PlanManejoAmbientalEIA extends EntidadBase implements Serializable {

	private static final long serialVersionUID = -8848052543734565892L;

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR_POR_EIA = PAQUETE + "PlanManejoAmbientalEIA.listarPorEIA";

	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "PMA_EIA_EMPL_GEN", initialValue = 1, sequenceName = "seq_empl_eia_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PMA_EIA_EMPL_GEN")
	@Column(name = "empl_id")
	private Integer id;

	@Getter
	@Setter
	@JoinColumn(name = "eist_id", referencedColumnName = "eist_id")
	@ForeignKey(name = "fk_env_man_plan_eist_id_env_impact_studies_eist_id")
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	private EstudioImpactoAmbiental eia;

	@Getter
	@Setter
	@Column(name = "empl_type")
	private String tipo;

	@Getter
	@Setter
	@Column(name = "empl_reference_pma")
	private Integer pmaReferencia;

	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "empl_register_date")
	private Date fechaRegistro;

	@Getter
	@Setter
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "planManejoAmbientalEIA", fetch = FetchType.LAZY)
	private List<PlanManejoAmbientalEIADetalle> planManejoAmbientalEIADetalle;

	@Getter
	@Setter
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "pmaEIA", fetch = FetchType.LAZY)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "spma_status = 'TRUE'")
	private List<CronogramaPmaEia> cronogramaPmaEIA;

	public PlanManejoAmbientalEIA() {

	}

	public PlanManejoAmbientalEIA(Integer id) {
		this.id = id;
	}
	
	@Getter
	@Setter
	@Column(name = "empl_historical_id")
	private Integer idHistorico;

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PlanManejoAmbientalEIA)) {
			return false;
		}
		PlanManejoAmbientalEIA other = (PlanManejoAmbientalEIA) obj;
		if (((this.id == null) && (other.id != null)) || ((this.id != null) && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return getTipo();
	}
	
	//Cris F: aumento de fecha para el historial
	@Getter
	@Setter
	@Column(name = "empl_date_create")
	private Date fechaCreacion;
}
