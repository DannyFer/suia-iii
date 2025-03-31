/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Filter;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author christian
 */
@Entity
@Table(name = "projects_general_location", schema = "public")
@NamedQueries({ @NamedQuery(name = Region.LISTAR_TODO, query = "SELECT u FROM Region u ORDER BY u.id") })
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "pglo_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "pglo_status = 'TRUE'")
public class Region extends EntidadBase {

	private static final long serialVersionUID = -6925013749256203097L;
	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR_TODO = PAQUETE + "Region.listarTodo";

	@Id
	@SequenceGenerator(name = "PROJECTS_GENERAL_LOCATION_PGLO_ID_GENERATOR", initialValue = 1, sequenceName = "seq_plgo_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROJECTS_GENERAL_LOCATION_PGLO_ID_GENERATOR")
	@Getter
	@Setter
	@Column(name = "pglo_id")
	private Integer id;
	@Getter
	@Setter
	@Column(name = "pglo_name")
	private String nombre;

}
