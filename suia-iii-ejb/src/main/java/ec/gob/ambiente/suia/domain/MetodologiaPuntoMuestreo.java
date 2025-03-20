package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "sampling_points_methodology", schema = "suia_iii")
@NamedQueries({ @NamedQuery(name = MetodologiaPuntoMuestreo.LISTAR, query = "SELECT m FROM MetodologiaPuntoMuestreo m WHERE m.estado=true and m.flora=:flora ORDER BY  m.peso asc") })
@AttributeOverrides({@AttributeOverride(name = "estado", column = @Column(name = "spme_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "spme_status = 'TRUE'")
public class MetodologiaPuntoMuestreo extends EntidadBase {


    private static final long serialVersionUID = -9122775025590177726L;private static final
    String PAQUETE = "ec.gob.ambiente.suia.domain.MetodologiaPuntoMuestreo.";
    public static final String LISTAR = PAQUETE
            + "listar";
    @Setter
    @Getter
    @SequenceGenerator(name = "SAMPLING_POINTS_METHODOLOGY_GENERATOR", schema = "suia_iii", sequenceName = "seq_spme_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SAMPLING_POINTS_METHODOLOGY_GENERATOR")
    @Column(name = "spme_id", unique = true, nullable = false)
    @Id
    private Integer id;

    @Setter
    @Getter
    @Size(max = 255)
    @Column(name = "spme_name")
    private String nombre;

    @Setter
    @Getter
    @Column(name = "spme_other")
    private Boolean otro;

    @Setter
    @Getter
    @Column(name = "spme_weight")
    private Integer peso;

    @Setter
    @Getter
    @Column(name = "spme_flora")
    private Boolean flora;


}
