/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tables_by_regulation", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "tare_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tare_status = 'TRUE'")
public class TablasDeNormativas extends EntidadBase {

    private static final long serialVersionUID = 273403518780994630L;

    private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";

    @Id
    @SequenceGenerator(name = "TABLES_BY_REG_GENERATOR", schema = "suia_iii", sequenceName = "seq_tare_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TABLES_BY_REG_GENERATOR")
    @Column(name = "tare_id")
    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    @JoinColumn(name = "plmo_id", referencedColumnName = "plmo_id")
    @ForeignKey(name = "tables_by_regulation_plmo_id_fkey")
    @ManyToOne
    private PlanMonitoreoEia planMonitoreoEia;

    @Getter
    @Setter
    @JoinColumn(name = "reta_id", referencedColumnName = "reta_id")
    @ForeignKey(name = "tables_by_regulation_reta_id_fkey")
    @ManyToOne
    private TablasNormativas tablasNormativas;

    @Getter
    @Setter
    @Column(name = "delete")
    private String borrar;

    @Setter
    @OneToMany(mappedBy = "tablasDeNormativas")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<ParametrosDeTablas> parametrosDeTablases;

    public List<ParametrosDeTablas> getParametrosDeTablases() {
        return parametrosDeTablases==null ? new ArrayList<ParametrosDeTablas>() : parametrosDeTablases;
    }
}
