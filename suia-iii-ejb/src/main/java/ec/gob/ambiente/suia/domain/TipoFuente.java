/*
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 20/12/2014]
 *          </p>
 */
@NamedQueries({ @NamedQuery(name = TipoFuente.FIND_ALL, query = "SELECT p FROM TipoFuente p") })
@Entity
@Table(name = "source_types", catalog = "", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "soty_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "soty_status = 'TRUE'")
public class TipoFuente extends EntidadBase {

	private static final long serialVersionUID = -1658513184204151632L;

	public static final String FIND_ALL = "ec.com.magmasoft.business.domain.TipoFuente.findAll";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "soty_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "soty_name")
	private String nombre;

	@Override
	public String toString() {
		return getNombre();
	}

	@Override
	public boolean equals(Object obj) {
		return ((TipoFuente) obj).getId() == getId();
	}
}
