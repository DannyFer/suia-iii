/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados
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
 * <b> AGREGAR DESCRIPCION. </b>
 *
 * @author oscar campana
 * @version Revision: 1.0
 *          <p>
 *          [Autor: oscar campana, Fecha: 25/06/2015]
 *          </p>
 */
@Entity
@Table(name = "environmental_aspects_pma", schema = "suia_iii")
@NamedQueries({ @NamedQuery(name = AspectoAmbientalPMA.FIND_BY_COMPONENT, query = "SELECT a FROM AspectoAmbientalPMA a WHERE a.componente.id = :idComponente") })
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "enas_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "enas_status = 'TRUE'")
public class AspectoAmbientalPMA extends EntidadBase {

	private static final long serialVersionUID = 2005017970532086876L;

	public static final String FIND_BY_COMPONENT = "ec.com.magmasoft.business.domain.AspectoAmbientalPMA.byComponent";

	@Id
	@SequenceGenerator(name = "ENVIRONMENTAL_ASPECTS_PMA_GENERATOR", schema = "suia_iii", sequenceName = "seq_enas_pma_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ENVIRONMENTAL_ASPECTS_PMA_GENERATOR")
	@Column(name = "enas_id")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@Column(name = "enas_name")
	private String nombre;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "comp_id")
	@ForeignKey(name = "fk_environmentalenas_id_componentspma_comp_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "comp_status = 'TRUE'")
	private ComponentePMA componente;

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.nombre;
	}
}
