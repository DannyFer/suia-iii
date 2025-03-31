/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.domain;

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
 *          [Autor: Oscar Campana, Fecha: 13/08/2015]
 *          </p>
 */
@Entity
@Table(name = "biota_formulas_eia", schema = "suia_iii")
@NamedQueries({ @NamedQuery(name = MedioBioticoFormulasEIA.LISTAR, query = "SELECT a FROM MedioBioticoFormulasEIA a WHERE a.estado = true and a.eia.id = :idEia and a.tipoMedioBiotico= :tipo") })
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "bifo_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "bifo_status = 'TRUE'")
public class MedioBioticoFormulasEIA extends EntidadBase {

	private static final long serialVersionUID = 273403518787987760L;

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR = PAQUETE + "MedioBioticoFormulasEIA.listar";

	@Id
	@SequenceGenerator(name = "BIFOEIA_GENERATOR", schema = "suia_iii", sequenceName = "seq_bifo_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BIFOEIA_GENERATOR")
	@Column(name = "bifo_id")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@JoinColumn(name = "eist_id", referencedColumnName = "eist_id")
	@ForeignKey(name = "fk_bifo_eist_id_env_impact_studies_eist_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private EstudioImpactoAmbiental eia;

	@Getter
	@Setter
	@Column(name = "bifo_wealth")
	private Integer riqueza;

	@Getter
	@Setter
	@Column(name = "bifo_abundance")
	private Integer abundancia;

	@Getter
	@Setter
	@Column(name = "bifo_shannon")
	private Double shannon;

	@Getter
	@Setter
	@Column(name = "bifo_simpson")
	private Double simpson;

	@Getter
	@Setter
	@Column(name = "bifo_jaccard")
	private Double jaccard;

	@Getter
	@Setter
	@Column(name = "bifo_sorensen")
	private Double sorensen;

	@Getter
	@Setter
	@Column(name = "bifo_type")
	private String tipoMedioBiotico;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.tipoMedioBiotico+(this.shannon+"");
	}
}
