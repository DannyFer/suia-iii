/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.domain;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
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

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 *
 * @author ishmael
 */
@Entity
@Table(name = "treatments_types")
@NamedQueries({
    @NamedQuery(name = TipoTratos.FIND_BY_STATE, query = "SELECT t FROM TipoTratos t WHERE t.estado = :estado")})
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "trty_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "trty_status = 'TRUE'")
public class TipoTratos extends EntidadBase {

	private static final long serialVersionUID = 4523254865583177806L;

	public static final String FIND_BY_STATE = "ec.com.magmasoft.business.domain.TipoTratos.findByState";
    @Id
    @Basic(optional = false)
    @Getter
    @Setter
    @SequenceGenerator(name = "TREATMENTS_TYPES_GENERATOR", initialValue = 1, sequenceName = "seq_trty_id", schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TREATMENTS_TYPES_GENERATOR")
    @Column(name = "trty_id")
    private Integer id;

    @Getter
    @Setter
    @Column(name = "trty_name")
    private String nombre;

    @Getter
    @Setter
    @OneToMany(mappedBy = "tipoTratos", fetch = FetchType.LAZY)
    private List<Persona> personaList;

    public TipoTratos(Integer id) {
        this.id = id;
    }

    public TipoTratos() {
    }

}
