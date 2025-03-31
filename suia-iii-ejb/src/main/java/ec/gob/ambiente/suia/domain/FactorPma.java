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
@Table(name = "fapma_factor", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "fafa_status")) })
@NamedQueries({ @NamedQuery(name = FactorPma.LISTAR_TODO, query = "SELECT f FROM FactorPma f WHERE f.estado = TRUE") })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "fafa_status = 'TRUE'")
public class FactorPma extends EntidadBase {

	private static final long serialVersionUID = 6851389968224213420L;

	private static final String PAQUETE_CLASE = "ec.gob.ambiente.suia.domain.FactorPma.";
	public static final String LISTAR_TODO = PAQUETE_CLASE + "findAll";

	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "FACTOR_ID_GENERATOR", initialValue = 1, sequenceName = "seq_fafa_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FACTOR_ID_GENERATOR")
	@Column(name = "fafa_id", nullable = false)
	private Integer id;
	@Getter
	@Setter
	@Column(name = "fafa_name", nullable = false, length = 200)
	private String nombre;

	public FactorPma() {
	}

	public FactorPma(Integer id) {
		this.id = id;
	}

	public FactorPma(String nombre) {
		this.nombre = nombre;
	}

	@Override
	public String toString() {
		return this.nombre;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 83 * hash + (this.id != null ? this.id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final FactorPma other = (FactorPma) obj;
		if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

}
