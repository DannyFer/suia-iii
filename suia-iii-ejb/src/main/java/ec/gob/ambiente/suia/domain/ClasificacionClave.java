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
 * @author jorge
 */
@Entity
@Table(name = "key_clasification", schema = "suia_iii")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "kecl_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "kecl_status = 'TRUE'")
@NamedQueries({
	@NamedQuery(name = "ClasificacionClave.findByNumeroClave", query = "SELECT c FROM ClasificacionClave c where c.estado = true and c.numeroClave = :p_numeroClave")
})
public class ClasificacionClave extends EntidadBase implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1510485297658589203L;

	@Getter
    @Setter
    @Column(name = "kecl_id")
    @Id
    @SequenceGenerator(name = "KEY_CLASIFICATION_GENERATOR", initialValue = 1, sequenceName = "seq_kecl_id", schema = "suia_iii")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "KEY_CLASIFICATION_GENERATOR")
    private Integer id;

    @Getter
    @Setter
    @Column(name = "kecl_key_number")
    private Integer numeroClave;

    @Getter
    @Setter
    @Column(name = "kecl_clasification_name")
    private String clasificacion;

}
