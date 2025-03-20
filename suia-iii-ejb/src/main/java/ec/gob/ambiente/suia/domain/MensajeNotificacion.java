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
@Table(name = "notification_messages", schema = "suia_iii")
@NamedQueries({
        @NamedQuery(name = MensajeNotificacion.FIND_BY_NAME, query = "SELECT m FROM MensajeNotificacion m WHERE m.nombre = :nombre")
})
@AttributeOverrides({
        @AttributeOverride(name = "estado", column = @Column(name = "nome_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "nome_status = 'TRUE'")
public class MensajeNotificacion extends EntidadBase {

    private static final long serialVersionUID = -1756612644167548934L;

    public static final String FIND_BY_NAME = "ec.gob.ambiente.suia.domain.MensajeNotificacion.Nombre";
    @Getter
    @Setter
    @Column(name = "nome_id")
    @Id
    @SequenceGenerator(name = "NOTIFICATION_MESSAGES_GENERATOR", initialValue = 1, sequenceName = "seq_nome_id", allocationSize = 1,schema = "suia_iii")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "NOTIFICATION_MESSAGES_GENERATOR")
    private Integer id;

    @Getter
    @Setter
    @Column(name = "nome_name")
    private String nombre;

    @Getter
    @Setter
    @Column(name = "nome_value")
    private String valor;
    
    @Getter
    @Setter
    @Column(name = "nome_subject")
    private String asunto;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MensajeNotificacion)) {
            return false;
        }
        MensajeNotificacion other = (MensajeNotificacion) obj;
        if (((this.id == null) && (other.id != null))
                || ((this.id != null) && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getNombre();
    }

}
