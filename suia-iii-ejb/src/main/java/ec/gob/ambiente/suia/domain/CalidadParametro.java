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
import java.util.List;

/**
 * Clase que representa los valores ingresados para calidad de aire, suelo y agua
 *
 * @author Juan Gabriel Guzmán.
 * @version 1.0
 */

@Entity
@Table(name = "parameter_qualities", schema = "suia_iii")
@AttributeOverrides({
        @AttributeOverride(name = "estado", column = @Column(name = "paqu_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "paqu_status = 'TRUE'")
@NamedQueries({
        @NamedQuery(name = CalidadParametro.FIND_BY_EIA_AND_COMPONENTE, query = "SELECT c FROM CalidadParametro c WHERE c.estudioImpactoAmbiental = :paramEia AND c.componente = :paramComponente")
}
)
public class CalidadParametro extends EntidadBase implements Serializable {

    private static final String FULL_NAME = "ec.gob.ambiente.suia.domain.CalidadParametro.";
    public static final String FIND_BY_EIA_AND_COMPONENTE = FULL_NAME + "findByEiaAndComponente";


    @Id
    @Getter
    @Setter
    @SequenceGenerator(name = "CALIDAD_PARAMETRO_ID_GENERATOR", initialValue = 1, sequenceName = "seq_paqu_id", schema = "suia_iii", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CALIDAD_PARAMETRO_ID_GENERATOR")
    @Column(name = "paqu_id")
    private Integer id;

    @Getter
    @Setter
    @JoinColumn(name = "labo_id", referencedColumnName = "labo_id")
    @ForeignKey(name = "fk_param_qualpaqu_id_laboratorieslabo_id")
    @ManyToOne()
    private Laboratorio laboratorio;

    @Getter
    @Setter
    @JoinColumn(name = "fafa_id", referencedColumnName = "fafa_id")
    @ForeignKey(name = "fk_param_qualpaqu_id_fapma_factorfafa_id")
    @ManyToOne()
    private FactorPma componente;

    @Getter
    @Setter
    @JoinColumn(name = "eist_id", referencedColumnName = "eist_id")
    @ForeignKey(name = "fk_param_qualpaqu_id_env_imp_steist_id")
    @ManyToOne()
    private EstudioImpactoAmbiental estudioImpactoAmbiental;

    @Getter
    @Setter
    @JoinColumn(name = "repa_id", referencedColumnName = "repa_id")
    @ForeignKey(name = "fk_param_qualpaqu_id_reg_paramrepa_id")
    @ManyToOne()
    private ParametrosNormativas parametroNormativas;

    @Getter
    @Setter
    @OneToMany(mappedBy = "calidadParametro")
    @Filter(name = EntidadBase.FILTER_ACTIVE, condition = "anre_status = 'TRUE'")
    private List<ResultadoAnalisis> resultadosAnalisis;



}
