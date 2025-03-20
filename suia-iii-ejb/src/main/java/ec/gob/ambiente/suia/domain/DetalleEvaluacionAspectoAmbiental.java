/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.domain;

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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 25/06/2015]
 *          </p>
 */
@Entity
@Table(name = "environmental_aspect_detail_evaluations", schema = "suia_iii")
@NamedQueries({ @NamedQuery(name = DetalleEvaluacionAspectoAmbiental.FIND_BY_ENVIRONMENTAL_ASPECT_EVALUATION, query = "SELECT d FROM DetalleEvaluacionAspectoAmbiental d WHERE d.evaluacionAspectoAmbiental.id = :idEvaluacion and idHistorico = null"),
@NamedQuery(name = DetalleEvaluacionAspectoAmbiental.FIND_BY_ENVIRONMENTAL_POR_EIA, query = "SELECT d FROM DetalleEvaluacionAspectoAmbiental d WHERE d.evaluacionAspectoAmbiental.identificacionEvaluacionImpactoAmbiental.estudioImpactoAmbiental.id = :idEia and idHistorico = null order by d.componente.nombre")})
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "eade_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "eade_status = 'TRUE'")
public class DetalleEvaluacionAspectoAmbiental extends EntidadBase implements Cloneable{

	private static final long serialVersionUID = 2754867540625887140L;

	public static final String FIND_BY_ENVIRONMENTAL_ASPECT_EVALUATION = "ec.com.magmasoft.business.domain.DetalleEvaluacionAspectoAmbiental.byEnvironmentalAspectEvaluation";
	public static final String FIND_BY_ENVIRONMENTAL_POR_EIA = "ec.com.magmasoft.business.domain.DetalleEvaluacionAspectoAmbiental.byEnvironmentalPorEia";

	@Id
	@SequenceGenerator(name = "ENVIRONMENTAL_ADE_GENERATOR", schema = "suia_iii", sequenceName = "seq_eade_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ENVIRONMENTAL_ADE_GENERATOR")
	@Column(name = "eade_id")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@Column(name = "eade_identified_impacts")
	private String impactosIdentificados;

	@Getter
	@Setter
	@Column(name = "eade_results")
	private String resultados;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "comp_id")
	@ForeignKey(name = "fk_environmentaleade_id_componentscomp_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "comp_status = 'TRUE'")
	private Componente componente;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "enas_id")
	@ForeignKey(name = "fk_environmentaleade_id_environmental_aspectsenas_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "enas_status = 'TRUE'")
	private AspectoAmbiental aspectoAmbiental;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "enae_id")
	@ForeignKey(name = "fk_eade_id_enae_id")
	private EvaluacionAspectoAmbiental evaluacionAspectoAmbiental;
	
	@Getter
	@Setter
	@Column(name = "eade_historical_id")
	private Integer idHistorico;
	
	@Getter
	@Setter
	@Column(name = "eade_notification_number")
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
	@Column(name = "eade_date_create")
	private Date fechaCreacion;
	
	@Override
	public DetalleEvaluacionAspectoAmbiental clone() throws CloneNotSupportedException {
		
		DetalleEvaluacionAspectoAmbiental clone = (DetalleEvaluacionAspectoAmbiental) super.clone();
		clone.setId(null);
		return clone;
	}
}
