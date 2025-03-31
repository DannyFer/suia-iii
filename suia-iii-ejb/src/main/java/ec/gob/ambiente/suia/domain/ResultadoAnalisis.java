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

/**
 * Clase que almacena los resultados del análisis realizado por los laboratorios de las muestras.
 * @author Juan Gabriel Guzmán.
 * @version 1.0
 */

@Entity
@Table(name = "analysis_results", schema = "suia_iii")
@AttributeOverrides({
        @AttributeOverride(name = "estado", column = @Column(name = "anre_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "anre_status = 'TRUE'")
public class ResultadoAnalisis extends EntidadBase implements Serializable {

    @Id
    @Getter
    @Setter
    @SequenceGenerator(name = "RESULTADO_ANALISIS_ID_GENERATOR", initialValue = 1, sequenceName = "seq_anre_id", schema = "suia_iii", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RESULTADO_ANALISIS_ID_GENERATOR")
    @Column(name = "anre_id")
    private Integer id;

    @Getter
    @Setter
    @Column(name = "anre_value")
    private Double valor;

    @Getter
    @Column(name = "exceptional_type")
    private boolean tipoExcepcional;

    @Getter
    @Column(name = "exceptional_anre_value")
    private String valorExcepcional;

    @Getter
    @Setter
    @JoinColumn(name = "paqu_id", referencedColumnName = "paqu_id")
    @ForeignKey(name = "fk_anal_resanru_id_param_qualpaqu_id")
    @ManyToOne()
    private CalidadParametro calidadParametro;



    @Getter
    @Setter
    @ManyToOne()
    @JoinColumn(name="samp_id", referencedColumnName = "samp_id")
    @ForeignKey(name = "fk_anal_resanru_id_samplessamp_id")
    private Muestra muestra;




    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResultadoAnalisis that = (ResultadoAnalisis) o;
        if (id != that.id) return false;
        if (valor != null ? !valor.equals(that.valor) : that.valor != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = 31 * id;
        result = 31 * result + (valor != null ? valor.hashCode() : 0);
        return result;
    }

    public void setValorExcepcional(String valorExcepcional){
        this.valorExcepcional = valorExcepcional;
        this.tipoExcepcional = true;
    }
}
