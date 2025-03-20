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
 * @author ishmael
 */
@Entity
@Table(name = "fapma_impact", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "faim_status")) })
@NamedQueries({ @NamedQuery(name = ImpactoPma.LISTAR_TODO, query = "SELECT i FROM ImpactoPma i") })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "faim_status = 'TRUE'")
public class ImpactoPma extends EntidadBase {

	private static final long serialVersionUID = 1283286257316135872L;

	private static final String PAQUETE_CLASE = "ec.gob.ambiente.suia.domain.ImpactoPma.";
	public static final String LISTAR_TODO = PAQUETE_CLASE + "findAll";

	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "IMPACTO_ID_GENERATOR", initialValue = 1, sequenceName = "seq_faim_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "IMPACTO_ID_GENERATOR")
	@Column(name = "faim_id", nullable = false)
	private Integer id;
	@Getter
	@Setter
	@Column(name = "faim_name", nullable = false, length = 200)
	private String nombre;

	public ImpactoPma() {
	}

	public ImpactoPma(Integer id) {
		this.id = id;
	}

}
