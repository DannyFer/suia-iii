/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.domain;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 *
 * @author oscar campana
 * @version Revision: 1.0
 *          <p>
 *          [Autor: oscar campana, Fecha: 10/08/2015]
 *          </p>
 */
@Entity
@Table(name = "components_pma", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "comp_status")) })
@NamedQueries({ @NamedQuery(name = ComponentePMA.LISTAR, query = "SELECT a FROM ComponentePMA a WHERE a.estado = true and a.nombre = :nombre") })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "comp_status = 'TRUE'")
public class ComponentePMA extends EntidadBase {

	private static final long serialVersionUID = 273403518627904630L;

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR = PAQUETE + "ComponentePMA.listar";

	@Id
	@SequenceGenerator(name = "COMPONENTSPMA_GENERATOR", schema = "suia_iii", sequenceName = "seq_cmpo_pma_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COMPONENTSPMA_GENERATOR")
	@Column(name = "comp_id")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@Column(name = "comp_name")
	private String nombre;

	@Getter
	@Setter
	@OneToMany(mappedBy = "componente")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "enas_status = 'TRUE'")
	private List<AspectoAmbiental> aspectoAmbientals;

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
