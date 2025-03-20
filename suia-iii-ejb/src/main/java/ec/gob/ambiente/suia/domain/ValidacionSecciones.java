package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;

import javax.persistence.*;

/**
 * <b> Entity que representa la tabla areas. </b>
 *
 * @author frank torres
 * @version Revision: 1.0
 *          <p>
 *          [Autor: frank torres, Fecha: 21/05/2015]
 *          </p>
 */
@Entity
@Table(name = "section_validations", schema = "suia_iii")
@NamedQueries({
        @NamedQuery(name = ValidacionSecciones.FIND_BY_NAME_ID_CLASS, query = "SELECT v FROM ValidacionSecciones v WHERE v.nombreClase = :nombreClase and v.idClase = :idClase"),
        @NamedQuery(name = ValidacionSecciones.FIND_BY_NAME_SECTION_ID_CLASS, query = "SELECT v FROM ValidacionSecciones v WHERE v.nombreClase = :nombreClase and v.valorSeccion = :valorSeccion and v.idClase = :idClase")
})
@AttributeOverrides({
        @AttributeOverride(name = "estado", column = @Column(name = "seva_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "seva_status = 'TRUE'")
public class ValidacionSecciones extends EntidadBase {

    private static final long serialVersionUID = -1756398154767548934L;

    public static final String FIND_BY_NAME_ID_CLASS = "ec.gob.ambiente.suia.domain.ValidacionSecciones.NombreIdClase";
    public static final String FIND_BY_NAME_SECTION_ID_CLASS = "ec.gob.ambiente.suia.domain.ValidacionSecciones.NombreSeccionIdClase";
    @Getter
    @Setter
    @Column(name = "seva_id")
    @Id
    @SequenceGenerator(name = "SECTION_VALIDATION_GENERATOR", initialValue = 1, sequenceName = "seq_seva_id", allocationSize = 1, schema = "suia_iii")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SECTION_VALIDATION_GENERATOR")
    private Integer id;

    @Getter
    @Setter
    @Column(name = "seva_class_name")
    private String nombreClase;

    @Getter
    @Setter
    @Column(name = "seva_class_id")
    private String idClase;

    @Getter
    @Setter
    @Column(name = "seva_section_value")
    private String valorSeccion;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ValidacionSecciones)) {
            return false;
        }
        ValidacionSecciones other = (ValidacionSecciones) obj;
        if (((this.id == null) && (other.id != null))
                || ((this.id != null) && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public ValidacionSecciones() {
    }

    public ValidacionSecciones(String nombreClase, String valorSeccion, String idClase, Boolean estado) {
        this.nombreClase = nombreClase;
        this.valorSeccion = valorSeccion;
        this.estado = estado;
        this.idClase = idClase;
    }

}
