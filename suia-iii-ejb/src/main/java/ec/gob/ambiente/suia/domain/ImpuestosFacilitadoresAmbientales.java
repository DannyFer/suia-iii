package ec.gob.ambiente.suia.domain;

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
@Table(name = "environmental_facilitators_taxes", schema = "suia_iii")
@NamedQueries({
        @NamedQuery(name = ImpuestosFacilitadoresAmbientales.OBTENR_IMPUESTO_UBICACION, query = "SELECT i FROM ImpuestosFacilitadoresAmbientales i where i.ubicacionesGeografica.codificacionInec = :codigoInec"),
        @NamedQuery(name = ImpuestosFacilitadoresAmbientales.OBTENR_IMPUESTO_DEFECTO, query = "SELECT i FROM ImpuestosFacilitadoresAmbientales i where i.ubicacionesGeografica = null")
})
@AttributeOverrides({@AttributeOverride(name = "estado", column = @Column(name = "enft_status"))})

@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "enft_status = 'TRUE'")
public class ImpuestosFacilitadoresAmbientales extends EntidadBase {

    private static final long serialVersionUID = -4474358358280438526L;

    private static final String PAQUETE_CLASE = "ec.gob.ambiente.suia.domain.ImpuestosFacilitadoresAmbientales.";
    public static final String OBTENR_IMPUESTO_UBICACION = PAQUETE_CLASE + "inecCode";
    public static final String OBTENR_IMPUESTO_DEFECTO = PAQUETE_CLASE + "default";

    @Getter
    @Setter
    @Id
    @SequenceGenerator(name = "ENVIRONMENTAL_FACILITATORS_TAXES_ENFT_ID_GENERATOR", sequenceName = "seq_enft_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ENVIRONMENTAL_FACILITATORS_TAXES_ENFT_ID_GENERATOR")
    @Column(name = "enft_id")
    private Integer id;

    @Getter
    @Setter
    @Column(name = "enft_facilitator_outside")
    private Double valorFacilitadorExterno;

    @Getter
    @Setter
    @Column(name = "enft_facilitator_inside")
    private Double valorFacilitadorInterno;

    @Getter
    @Setter
    @Column(name = "enft_code", nullable = true, length = 2)
    private String codigo;

    @Getter
    @Setter
    @JoinColumn(name = "gelo_codification_inec", referencedColumnName = "gelo_codification_inec", nullable = true)
    @ForeignKey(name = "fk_environmental_facilitators_taxes_gelo_codification_inec")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private UbicacionesGeografica ubicacionesGeografica;

}
