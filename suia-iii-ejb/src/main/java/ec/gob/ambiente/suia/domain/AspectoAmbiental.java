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
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 25/06/2015]
 *          </p>
 */
@Entity
@Table(name = "environmental_aspects", schema = "suia_iii")
@NamedQueries({ @NamedQuery(name = AspectoAmbiental.FIND_BY_COMPONENT, query = "SELECT a FROM AspectoAmbiental a WHERE a.componente.id = :idComponente") })
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "enas_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "enas_status = 'TRUE'")
public class AspectoAmbiental extends EntidadBase {

	private static final long serialVersionUID = 2005017970532086876L;

	public static final String FIND_BY_COMPONENT = "ec.com.magmasoft.business.domain.AspectoAmbiental.byComponent";

	@Id
	@SequenceGenerator(name = "ENVIRONMENTAL_ASPECTS_GENERATOR", schema = "suia_iii", sequenceName = "seq_enas_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ENVIRONMENTAL_ASPECTS_GENERATOR")
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
	@ForeignKey(name = "fk_environmentalenas_id_componentscomp_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "comp_status = 'TRUE'")
	private Componente componente;

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
