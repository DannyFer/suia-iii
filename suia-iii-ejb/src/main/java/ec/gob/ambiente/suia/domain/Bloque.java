/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * @author carlos.pupo
 */
@Entity
@Table(name = "blocks", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "bloc_status")) })
@NamedQueries({ @NamedQuery(name = Bloque.FILTRAR, query = "SELECT b FROM Bloque b WHERE (lower(b.nombre) LIKE :filter OR lower(b.denominacionArea) LIKE :filter )") })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "bloc_status = 'TRUE'")
public class Bloque extends EntidadBase {

	private static final long serialVersionUID = -5226419461146136619L;

	public static final String FILTRAR = "ec.gob.ambiente.suia.domain.Bloque.FILTRAR";
	
	public static final int ID_BLOQUE_31=18;
	public static final int ID_BLOQUE_43=21;

	@Id
	@Column(name = "bloc_id")
	@SequenceGenerator(name = "BLOCKS_GENERATOR", initialValue = 1, sequenceName = "seq_bloc_id", schema = "public", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BLOCKS_GENERATOR")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@Column(name = "bloc_name")
	private String nombre;

	@Getter
	@Setter
	@Column(name = "bloc_area_nomination")
	private String denominacionArea;
}
