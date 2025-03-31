/**
 * Copyright (c) 2015 MAGMASOFT (Innovando tecnologia)
 * Todos los derechos reservados.
 * Este software es confidencial y debe usarlo de acorde con los términos de uso.
 */
package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Clase que representa a el laboratorio encargado de realizar el análisis de las muestras tomadas.
 * @author Juan Gabriel Guzmán.
 * @version 1.0
 */

@Entity
@Table(name = "laboratories", schema = "suia_iii")
@AttributeOverrides({
        @AttributeOverride(name = "estado", column = @Column(name = "labo_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "labo_status = 'TRUE'")
@NamedQueries({
        @NamedQuery(name = Laboratorio.FIND_BY_QUERY_NOMBRE, query = "SELECT l FROM Laboratorio l WHERE UPPER(l.nombre) LIKE :paramQueryNombre")
}

)
public class Laboratorio extends EntidadBase implements Serializable {

    private static final String FULL_NAME = "ec.gob.ambiente.suia.domain.Laboratorio.";
    public static final String FIND_BY_QUERY_NOMBRE = "findByQueryNombre";
    @Id
    @Getter
    @Setter
    @SequenceGenerator(name = "LABORATORIO_ID_GENERATOR", initialValue = 1, sequenceName = "seq_labo_id", schema = "suia_iii", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LABORATORIO_ID_GENERATOR")
    @Column(name = "labo_id")
    private Integer id;
    @Getter
    @Setter
    @Column(name = "labo_name", length = 200)
    private String nombre;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Laboratorio that = (Laboratorio) o;
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


    @Override
    public String toString() {
        if(this.getNombre()!=null) {
            return this.getNombre().toString();
        }
        else
            return null;
    }
}
