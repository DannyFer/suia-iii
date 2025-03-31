/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.domain;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
 * 
 * @author jorge
 */
@Entity
@Table(name = "key_operation", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "keop_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "keop_status = 'TRUE'")
@NamedQueries({ @NamedQuery(name = "ClaveOperacion.findByClasificacion", query = "SELECT c FROM ClaveOperacion c where c.estado = true and c.clasificacionClave.id = :p_clasificacion") })
public class ClaveOperacion extends EntidadBase implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1510485297658589203L;

	@Getter
	@Setter
	@Column(name = "keop_id")
	@Id
	@SequenceGenerator(name = "KEY_OPERATION_GENERATOR", initialValue = 1, sequenceName = "seq_keop_id", schema = "suia_iii")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "KEY_OPERATION_GENERATOR")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "keop_key_code")
	private String codigoClave;

	@Getter
	@Setter
	@Column(name = "keop_operation_name")
	private String operacion;

	@Getter
	@Setter
	@JoinColumn(name = "kecl_id", referencedColumnName = "kecl_id", nullable = false)
	@ForeignKey(name = "fk_kecl_id_to_keop_id")
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private ClasificacionClave clasificacionClave;

	@Override
	public String toString() {
		return this.codigoClave + " | " + this.operacion;
	}

}
