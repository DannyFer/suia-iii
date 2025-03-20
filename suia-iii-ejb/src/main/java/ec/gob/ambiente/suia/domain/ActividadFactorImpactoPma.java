/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * @author ishmael
 */
@Entity
@Table(name = "fapma_activity_factor_impact", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "fafi_status")) })
@NamedQueries({
		@NamedQuery(name = ActividadFactorImpactoPma.LISTAR_TODO, query = "SELECT a FROM ActividadFactorImpactoPma a"),
		@NamedQuery(name = ActividadFactorImpactoPma.LISTAR_POR_ACTIVIDAD, query = "SELECT a FROM ActividadFactorImpactoPma a WHERE a.actividadComercial = :actividadComercial"),
		@NamedQuery(name = ActividadFactorImpactoPma.LISTAR_POR_ACTIVIDAD_Y_FACTOR, query = "SELECT a FROM ActividadFactorImpactoPma a WHERE a.actividadComercial = :actividadComercial AND a.factor = :factor") })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "fafi_status = 'TRUE'")
public class ActividadFactorImpactoPma extends EntidadBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8734991229629991950L;
	private static final String PAQUETE_CLASE = "ec.gob.ambiente.suia.domain.ActividadFactorImpactoPma.";
	public static final String LISTAR_TODO = PAQUETE_CLASE + "findAll";
	public static final String LISTAR_POR_ACTIVIDAD = PAQUETE_CLASE + "listarPorActividad";
	public static final String LISTAR_POR_ACTIVIDAD_Y_FACTOR = PAQUETE_CLASE + "listarPorActividadFactor";

	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "ACTIVIDAD_FACTOR_IMPACTO_ID_GENERATOR", initialValue = 1, sequenceName = "seq_fafi_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ACTIVIDAD_FACTOR_IMPACTO_ID_GENERATOR")
	@Column(name = "fafi_id", nullable = false)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "coac_id")
	@ForeignKey(name = "coac_fapma_activity_factor_impact_fk")
	@Getter
	@Setter
	private CatalogoActividadComercial actividadComercial;

	@ManyToOne
	@JoinColumn(name = "fafa_id")
	@ForeignKey(name = "fafa_fapma_activity_factor_impact_fk")
	@Getter
	@Setter
	private FactorPma factor;

	@ManyToOne
	@JoinColumn(name = "faim_id")
	@ForeignKey(name = "faim_fapma_activity_factor_impact_fk")
	@Getter
	@Setter
	private ImpactoPma impacto;

	public ActividadFactorImpactoPma() {
	}

	public ActividadFactorImpactoPma(Integer id) {
		this.id = id;
	}

	@Override
	public String toString() {
		if (this.impacto != null) {
			return this.impacto.getNombre();
		}
		return "ActividadFactorImpactoPma{" + "id=" + id + '}';
	}

}
