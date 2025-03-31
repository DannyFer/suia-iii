/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

import java.util.Collection;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
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
@Table(name = "prevented_type", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "prty_status")) })
@NamedQueries({
		@NamedQuery(name = "TipoImpedimento.findAll", query = "SELECT t FROM TipoImpedimento t"),
		@NamedQuery(name = "TipoImpedimento.listarActivos", query = "SELECT t FROM TipoImpedimento t WHERE t.estado = true ORDER BY t.nombre") })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prty_status = 'TRUE'")
public class TipoImpedimento extends EntidadBase {

	private static final long serialVersionUID = -2971942285278648771L;

	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "TIPO_IMPEDIMENTO_ID_GENERATOR", initialValue = 1, sequenceName = "seq_prty_id_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TIPO_IMPEDIMENTO_ID_GENERATOR")
	@Column(name = "prty_id", nullable = false)
	private Integer id;
	@Getter
	@Setter
	@Column(name = "prty_name", nullable = false, length = 100)
	private String nombre;
	@Getter
	@Setter
	@Column(name = "prty_description", nullable = false, length = 200)
	private String descripcion;
	@Getter
	@Setter
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "prtyId", fetch = FetchType.LAZY)
	private Collection<Impedido> preventedCollection;

	public TipoImpedimento() {
	}

	public TipoImpedimento(Integer id) {
		this.id = id;
	}
}
