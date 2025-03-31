/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.domain;

import java.util.Date;
import java.util.List;

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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

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
@Table(name = "environmental_impact_identification_assessment", schema = "suia_iii")
@NamedQueries({ @NamedQuery(name = IdentificacionEvaluacionImpactoAmbiental.GET_BY_ESTUDIO, query = "SELECT i FROM IdentificacionEvaluacionImpactoAmbiental i WHERE i.estudioImpactoAmbiental.id = :idEstudio and i.idHistorico = null") })
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "enii_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "enii_status = 'TRUE'")
public class IdentificacionEvaluacionImpactoAmbiental extends EntidadBase implements Cloneable{

	private static final long serialVersionUID = -4740032092975847476L;

	public static final String GET_BY_ESTUDIO = "ec.gob.ambiente.suia.domain.IdentificacionEvaluacionImpactoAmbiental.Get_by_estudio";

	@Id
	@SequenceGenerator(name = "ENVIRONMENTAL_IIA_GENERATOR", schema = "suia_iii", sequenceName = "seq_enii_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ENVIRONMENTAL_IIA_GENERATOR")
	@Column(name = "enii_id")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@Column(name = "enii_conclusions")
	private String conclusiones;

	@Getter
	@Setter
	@OneToMany(mappedBy = "identificacionEvaluacionImpactoAmbiental")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "enae_status = 'TRUE'")
	private List<EvaluacionAspectoAmbiental> evaluacionAspectoAmbientals;

	@OneToOne
	@JoinColumn(name = "docu_id")
	@ForeignKey(name = "fk_enii_id_docu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	@Getter
	@Setter
	private Documento documento;

	@OneToOne
	@JoinColumn(name = "docu_treat_id")
	@ForeignKey(name = "fk_environmentalenii_id_docu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	@Getter
	@Setter
	private Documento tratamiento;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "eist_id")
	@ForeignKey(name = "fk_enii_id_studieseist_id")
	private EstudioImpactoAmbiental estudioImpactoAmbiental;
	
	@Getter
	@Setter
	@Column(name = "enii_historical_id")
	private Integer idHistorico;
	
	@Getter
	@Setter
	@Column(name = "enii_notification_number")
	private Integer numeroNotificacion;
	
	@Getter
	@Setter
	@Column(name = "enii_date_create")
	private Date fechaCreacion;
	
	 @Override
		public IdentificacionEvaluacionImpactoAmbiental clone() throws CloneNotSupportedException {

			 IdentificacionEvaluacionImpactoAmbiental clone = (IdentificacionEvaluacionImpactoAmbiental)super.clone();
			 clone.setId(null);		 
			 return clone;
		}

}
