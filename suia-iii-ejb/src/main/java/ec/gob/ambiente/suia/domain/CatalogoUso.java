/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;

import javax.persistence.*;
import java.util.List;

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
@Table(name = "uses_catalog", schema = "suia_iii")
@AttributeOverrides({@AttributeOverride(name = "estado", column = @Column(name = "usca_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "usca_status = 'TRUE'")
public class CatalogoUso extends EntidadBase {

    private static final long serialVersionUID = 273403518780994630L;

    private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";

    @Id
    @SequenceGenerator(name = "USES_CATALOG_GENERATOR", schema = "suia_iii", sequenceName = "seq_usca_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USES_CATALOG_GENERATOR")
    @Column(name = "usca_id")
    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    @Column(name = "usca_description")
    private String descripcion;

    @Getter
    @Setter
    @OneToMany(mappedBy = "catalogoUso", fetch = FetchType.LAZY)
    private List<UsoTablaNormativa> usosTablasNormativas;

    @Getter
    @Setter
    @OneToMany(mappedBy = "catalogoUso", fetch = FetchType.LAZY)
    private List<UsoCuerpoHidrico> usosCuerposHidricos;

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.descripcion;
    }
}
