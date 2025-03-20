/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.domain;

import java.util.ArrayList;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

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
@Table(name = "environmental_aspect_evaluations", schema = "suia_iii")
@NamedQueries({
		@NamedQuery(name = EvaluacionAspectoAmbiental.FIND_BY_IMPACTO_AMBIENTAL, query = "SELECT e FROM EvaluacionAspectoAmbiental e WHERE e.identificacionEvaluacionImpactoAmbiental.id = :idImpactoAmbiental"),
		@NamedQuery(name = EvaluacionAspectoAmbiental.FIND_BY_ACTIVIDAD, query = "SELECT e FROM EvaluacionAspectoAmbiental e WHERE e.actividadLicenciamiento = :paramActividad")

})
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "enae_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "enae_status = 'TRUE'")
public class EvaluacionAspectoAmbiental extends EntidadBase implements Cloneable {

	private static final long serialVersionUID = 2754867540625887140L;

	public static final String FIND_BY_IMPACTO_AMBIENTAL = "ec.com.magmasoft.business.domain.EvaluacionAspectoAmbiental.byImpactoAmbiental";
	public static final String FIND_BY_ACTIVIDAD = "ec.com.magmasoft.business.domain.EvaluacionAspectoAmbiental.byActividad";

	@Id
	@SequenceGenerator(name = "ENVIRONMENTAL_AE_GENERATOR", schema = "suia_iii", sequenceName = "seq_enae_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ENVIRONMENTAL_AE_GENERATOR")
	@Column(name = "enae_id")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "acli_id")
	@ForeignKey(name = "fk_env_aspect_evalenae_id_act_licacli_id")
	private ActividadLicenciamiento actividadLicenciamiento;

	@Getter
	@Setter
	@OneToMany(mappedBy = "evaluacionAspectoAmbiental")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "eade_status = 'TRUE'")
	private List<DetalleEvaluacionAspectoAmbiental> detalleEvaluacionLista;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "enii_id")
	@ForeignKey(name = "fk_enae_id_enii_id")
	private IdentificacionEvaluacionImpactoAmbiental identificacionEvaluacionImpactoAmbiental;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "prst_id")
	@ForeignKey(name = "environmental_aspect_evaluations_prst_id_fkey")
	private EtapasProyecto etapasProyecto;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "stac_id")
	@ForeignKey(name = "environmental_aspect_evaluations_stac_id_fkey")
	private ActividadesPorEtapa actividadesPorEtapa;

	@Setter
	@Transient
	private List<DetalleEvaluacionAspectoAmbiental> detalleEvaluacionListaSalvadas;

	public List<DetalleEvaluacionAspectoAmbiental> getDetalleEvaluacionListaSalvadas() {
		return detalleEvaluacionListaSalvadas == null ? detalleEvaluacionListaSalvadas = new ArrayList<DetalleEvaluacionAspectoAmbiental>()
				: detalleEvaluacionListaSalvadas;
	}
	
	@Getter
	@Setter
	@Column(name = "enae_historical_id")
	private Integer idHistorico;
	
	@Getter
	@Setter
	@Column(name = "enae_notification_number")
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
	public EvaluacionAspectoAmbiental clone() throws CloneNotSupportedException {
		
		EvaluacionAspectoAmbiental clone = (EvaluacionAspectoAmbiental) super.clone();
		clone.setId(null);
		return clone;
	}
}
