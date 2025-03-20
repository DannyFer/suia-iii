package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 *
 * <b> Entity Bean. </b>
 *
 * @author Jonathan Guerrero
 * @version Revision: 1.0
 * <p>
 * [Autor: Jonathan Guerrero, Fecha: Jan 28, 2015]
 * </p>
 */
@Table(name = "shape_types", schema = "suia_iii")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "shty_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "shty_status = 'TRUE'")
@Entity
public class TipoForma extends EntidadBase {

    private static final long serialVersionUID = -8299238911470247707L;

    public static final int TIPO_FORMA_PUNTO = 1;
    public static final int TIPO_FORMA_LINEA = 2;
    public static final int TIPO_FORMA_POLIGONO = 3;

    @Id
    @Getter
    @Setter
    @Column(name = "shty_id")
    private Integer id;

    @Getter
    @Setter
    @Column(name = "shty_name")
    private String nombre;

    @Override
    public String toString() {
        return nombre;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TipoForma)) {
            return false;
        }
        TipoForma other = (TipoForma) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

}
