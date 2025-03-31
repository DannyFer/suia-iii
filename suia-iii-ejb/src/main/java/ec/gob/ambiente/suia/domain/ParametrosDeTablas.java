/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name = "parameters_by_table", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "pata_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "pata_status = 'TRUE'")
public class ParametrosDeTablas extends EntidadBase {

    private static final long serialVersionUID = 273403518780994630L;

    private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";

    @Id
    @SequenceGenerator(name = "PARAMETERS_TABLES_GENERATOR", schema = "suia_iii", sequenceName = "seq_pata_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PARAMETERS_TABLES_GENERATOR")
    @Column(name = "pata_id")
    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private String parametrosNormativas;

    @Getter
    @Setter
    @Column(name = "pata_frecuency")
    private Integer frecuencia;

    @Getter
    @Setter
    @Column(name = "pata_perodicity")
    private String periodicidad;

    @Getter
    @Setter
    @JoinColumn(name = "tare_id", referencedColumnName = "tare_id")
    @ForeignKey(name = "parameters_by_table_tare_id_fkey")
    @ManyToOne
    private TablasDeNormativas tablasDeNormativas;
}
