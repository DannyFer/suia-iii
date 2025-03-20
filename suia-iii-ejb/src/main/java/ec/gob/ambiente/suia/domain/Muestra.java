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
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Clase que representa las muestras tomadas para el análisis de la calidad del medio físico
 * @author Juan Gabriel Guzmán.
 * @version 1.0
 */

@Entity
@Table(name = "samples", schema = "suia_iii")
@AttributeOverrides({
        @AttributeOverride(name = "estado", column = @Column(name = "samp_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "samp_status = 'TRUE'")
public class Muestra extends EntidadBase implements Serializable {
    @Id
    @Getter
    @Setter
    @SequenceGenerator(name = "MUESTRA_ID_GENERATOR", initialValue = 1, sequenceName = "seq_samp_id", schema = "suia_iii", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MUESTRA_ID_GENERATOR")
    @Column(name = "samp_id")
    private Integer id;
    @Getter
    @Setter
    @Column(name = "samp_code", length = 100)
    private String codigo;

    @Getter
    @Setter
    @Column(name = "samp_samle_date")
    private Date fechaMuestra;

    @Getter
    @Setter
    @Column(name = "samp_diurnal")
    private Boolean diurno;

    @Getter
    @Setter
    @Column(name = "samp_site_description")
    private String descripcionSitioMuestra;

    @Getter
    @Setter
    @Column(name = "sample_value")
    private Double valor;
    @Getter
    @Setter
    @Column(name = "zone")
    private String zona;



    @Getter
    @Setter
    @JoinColumn(name = "wabo_id", referencedColumnName = "wabo_id")
    @ForeignKey(name = "fk_samplessamp_id_water_bodieswabo_id")
    @ManyToOne()
    private CuerpoHidrico cuerpoHidrico;

    @Getter
    @Setter
    @OneToMany( mappedBy="muestra")
    private List<ResultadoAnalisis> resultadosAnalisis;


    @Getter
    @Setter
    @JoinColumn(name="geco_id")
    @ForeignKey(name = "fk_samplessamp_id_general_coordinatesgeco_id")
    @OneToOne(cascade=CascadeType.ALL)
    private CoordenadaGeneral coordenadaGeneral;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Muestra that = (Muestra) o;

        if (id != that.id) return false;
        if (codigo != null ? !codigo.equals(that.codigo) : that.codigo != null) return false;


        return true;
    }

    @Override
    public int hashCode() {
        int result = (codigo != null ? codigo.hashCode() : 0);
        result = 31 * result + (codigo != null ? codigo.hashCode() : 0);

        return result;
    }
}
