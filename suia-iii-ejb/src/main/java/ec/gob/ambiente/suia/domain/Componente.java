/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.domain;

import java.util.List;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
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
@Table(name = "components", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "comp_status")) })
@NamedQueries({ @NamedQuery(name = Componente.LISTAR, query = "SELECT a FROM Componente a WHERE a.estado = true and a.nombre = :nombre") })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "comp_status = 'TRUE'")
public class Componente extends EntidadBase {

	private static final long serialVersionUID = 273403518627904630L;

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR = PAQUETE + "Componente.listar";

	@Id
	@SequenceGenerator(name = "COMPONENTS_GENERATOR", schema = "suia_iii", sequenceName = "seq_cmpo_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COMPONENTS_GENERATOR")
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
