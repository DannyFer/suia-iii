/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.domain;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
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
@Table(name = "contacts_forms")
@NamedQueries({
    @NamedQuery(name = FormasContacto.FIND_BY_STATE, query = "SELECT c FROM FormasContacto c WHERE c.estado = :estado ORDER BY c.orden")})
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "cofo_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "cofo_status = 'TRUE'")
public class FormasContacto extends EntidadBase {

    private static final long serialVersionUID = 8327684074126366659L;

    public static final String FIND_BY_STATE = "ec.com.magmasoft.business.domain.FormasContacto.findByState";

    public static final int FAX = 1;
    public static final int DIRECCION = 2;
    public static final int POBOX = 3;
    public static final int CELULAR = 4;
    public static final int EMAIL = 5;
    public static final int TELEFONO = 6;
    public static final int URL = 7;
    public static final int POSTFIX_ZIP = 8;

    @Id
    @Column(name = "cofo_id")
    @SequenceGenerator(name = "CONTACTS_FORMS_GENERATOR", initialValue = 1, sequenceName = "seq_cofo_id", schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CONTACTS_FORMS_GENERATOR")
    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    @Column(name = "cofo_name")
    private String nombre;

    @Getter
    @Setter
    @Column(name = "cofo_order")
    private Integer orden;

    @Getter
    @Setter
    @OneToMany(mappedBy = "formasContacto", fetch = FetchType.LAZY)
    private List<Contacto> contactoList;

    public FormasContacto(Integer id) {
        this.id = id;
    }

    public FormasContacto() {
    }

}
