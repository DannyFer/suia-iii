package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by juangabriel on 14/07/15.
 */
@Entity
@Table(name = "sensitive_elements", schema = "suia_iii")
@AttributeOverrides({
        @AttributeOverride(name = "estado", column = @Column(name = "seel_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "seel_status = 'TRUE'")
public class ElementoSensible extends EntidadBase implements Serializable {
    @Id
    @Getter
    @Setter
    @SequenceGenerator(name = "ELEMENTO_SENSIBLE_ID_GENERATOR", initialValue = 1, sequenceName = "seq_seel_id", schema = "suia_iii", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ELEMENTO_SENSIBLE_ID_GENERATOR")
    @Column(name = "seel_id")
    private Integer id;
    @Getter
    @Setter
    @Column(name = "seel_name", length = 100)
    private String nombre;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ElementoSensible that = (ElementoSensible) o;

        if (id != that.id) return false;
        if (nombre != null ? !nombre.equals(that.nombre) : that.nombre != null) return false;


        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (nombre != null ? nombre.hashCode() : 0);

        return result;
    }
}
