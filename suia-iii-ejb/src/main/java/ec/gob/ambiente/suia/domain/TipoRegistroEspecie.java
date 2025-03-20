package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;

import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * Created by Denis Linares on 17/11/15.
 */
@Entity
@Table(name = "specie_register_type_eia", schema = "suia_iii")
@NamedQueries({ @NamedQuery(name = TipoRegistroEspecie.LISTAR, query = "SELECT t FROM TipoRegistroEspecie t WHERE t.estado=true AND t.seccionMenu = :seccionMenu AND t.directo = :directo") })
@AttributeOverrides({@AttributeOverride(name = "estado", column = @Column(name = "srt_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "srt_status = 'TRUE'")
public class TipoRegistroEspecie extends EntidadBase {

    private static final long serialVersionUID = -9122710955205777726L;
    private static final String PAQUETE = "ec.gob.ambiente.suia.domain.TipoRegistroEspecie.";
    public static final String LISTAR = PAQUETE
            + "listar";
    @Setter
    @Getter
    @SequenceGenerator(name = "SPECIE_REGISTER_TYPE", schema = "suia_iii", sequenceName = "seq_srt_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SPECIE_REGISTER_TYPE")
    @Column(name = "srt_id", unique = true, nullable = false)
    @Id
    private Integer id;

    @Setter
    @Getter
    @Size(max = 100)
    @Column(name = "srt_name")
    private String nombre;

    @Setter
    @Getter
    @Size(max = 100)
    @Transient
    private String nombreOtro;

    @Setter
    @Getter
    @Size(max = 100)
    @Column(name = "srt_menu_section")
    private String seccionMenu;//Mastofauna,Entomofauna,... etc.

    @Setter
    @Getter
    @Column(name = "srt_direct_type")
    private Boolean directo;//Directo o indirecto.

    @Setter
    @Getter
    @Column(name = "srt_other")
    private Boolean otro;//Para controlar en la UI cuando el tipo es no nomenclado.

}
