package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;

/**
 * @author frank.torres
 * @version Revision: 1.0
 *          <p>
 *          [Autor: frank.torres, Fecha: 27/08/2015]
 *          </p>
 */
@Entity
@Table(name = "environmental_facilitator_question", schema = "suia_iii")
@NamedQueries({
        @NamedQuery(name = PreguntasFacilitadoresAmbientales.OBTENER_COMENTARIOS_ID, query = "SELECT p FROM PreguntasFacilitadoresAmbientales p where p.participacionSocialAmbiental.id = :idParticipacion order by p.id")
})
@AttributeOverrides({@AttributeOverride(name = "estado", column = @Column(name = "efqu_status")),
        @AttributeOverride(name = "fechaCreacion", column = @Column(name = "efqu_creation_date")),
        @AttributeOverride(name = "fechaModificacion", column = @Column(name = "efqu_date_update")),
        @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "efqu_creator_user")),
        @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "efqu_user_update"))})

@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "efqu_status = 'TRUE'")
public class PreguntasFacilitadoresAmbientales extends EntidadAuditable {

    private static final long serialVersionUID = -4474358358280438526L;

    private static final String PAQUETE_CLASE = "ec.gob.ambiente.suia.domain.PreguntasFacilitadoresAmbientales.";
    public static final String OBTENER_COMENTARIOS_ID = PAQUETE_CLASE + "obtenerComentariosPorId";

    @Getter
    @Setter
    @Id
    @SequenceGenerator(name = "PREGUNTASFACILITADORESAMBIENTALES_EFQU_ID_GENERATOR", sequenceName = "seq_efqu_id", schema = "suia_iii",allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PREGUNTASFACILITADORESAMBIENTALES_EFQU_ID_GENERATOR")
    @Column(name = "efqu_id")
    private Integer id;


    @Getter
    @Setter
    @Column(name = "efqu_question", nullable = true, length = 300)
    private String pregunta;

    @Getter
    @Setter
    @Column(name = "efqu_facilitator_answer", nullable = true, length = 2200)
    private String respuestaFacilitador;

    @Getter
    @Setter
    @Column(name = "efqu_answer", nullable = true, length = 2200)
    private String respuestaPromotor;

    @Getter
    @Setter
    @JoinColumn(name = "ensp_id", referencedColumnName = "ensp_id")
    @ForeignKey(name = "fk_espensp_id_environmental_facilitator_comments_espe_id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ParticipacionSocialAmbiental participacionSocialAmbiental;

}
