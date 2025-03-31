/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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

/**
 * <b> AGREGAR DESCRIPCION. </b>
 *
 * @author martin villalon
 * @version Revision: 1.0
 *          <p>
 *          [Autor: martin villalon, Fecha: 05/08/2015]
 *          </p>
 */
@Entity
@Table(name = "uses_regulations_tables", schema = "suia_iii")
@AttributeOverrides({@AttributeOverride(name = "estado", column = @Column(name = "usrt_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "usrt_status = 'TRUE'")
public class UsoTablaNormativa extends EntidadBase {

    private static final long serialVersionUID = 273403518780994630L;

    private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";

    @Id
    @SequenceGenerator(name = "USES_REGULATIONS_TABLES_GENERATOR", schema = "suia_iii", sequenceName = "seq_usrt_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USES_REGULATIONS_TABLES_GENERATOR")
    @Column(name = "usrt_id")
    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    @JoinColumn(name = "usca_id", referencedColumnName = "usca_id")
    @ForeignKey(name = "fk_uses_regulations_tables_usrt_id_uses_catalog_usca_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CatalogoUso catalogoUso;


    @Getter
    @Setter
    @JoinColumn(name = "reta_id", referencedColumnName = "reta_id")
    @ForeignKey(name = "fk_uses_regulations_tables_usrt_id_regulations_tables_reta_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private TablasNormativas tablaNormativa;

}
