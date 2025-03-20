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
@NamedQueries({ @NamedQuery(name = HusoHorario.FIND_ALL, query = "SELECT u FROM HusoHorario u") })
@Entity
@Table(name = "time_zones", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "tizo_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tizo_status = 'TRUE'")
public class HusoHorario extends EntidadBase {
	
	private static final long serialVersionUID = -6076962620401679985L;

	public static final String FIND_ALL = "ec.com.magmasoft.business.domain.HusoHorario.findAll";
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "tizo_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "tizo_name")
	private String nombre;

	@Override
	public String toString() {
		return getNombre();
	}

	@Override
	public boolean equals(Object obj) {
		return ((HusoHorario) obj).getId() == getId();
	}

}
