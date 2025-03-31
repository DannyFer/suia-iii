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

import javax.persistence.*;

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
@Table(name = "uses_water_bodies", schema = "suia_iii")
@AttributeOverrides({@AttributeOverride(name = "estado", column = @Column(name = "uswb_status"))})
@NamedQueries({
        @NamedQuery(name = UsoCuerpoHidrico.LISTAR_POR_CUERPO_HIDRICO, query = "SELECT c FROM UsoCuerpoHidrico c WHERE c.cuerpoHidrico = :cuerpoHidrico AND c.estado = true")

})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "uswb_status = 'TRUE'")
public class UsoCuerpoHidrico extends EntidadBase {

    private static final long serialVersionUID = 273403518780994630L;

    private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";

    public static final String LISTAR_POR_CUERPO_HIDRICO = PAQUETE
            + "UsoCuerpoHidrico.listarCuerpoHidrico";

    @Id
    @SequenceGenerator(name = "USES_WATER_BODIES_GENERATOR", schema = "suia_iii", sequenceName = "seq_uswb_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USES_WATER_BODIES_GENERATOR")
    @Column(name = "uswb_id")
    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    @JoinColumn(name = "usca_id", referencedColumnName = "usca_id")
    @ForeignKey(name = "fk_uses_water_bodies_eswb_id_uses_catalog_usca_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private CatalogoUso catalogoUso;

    @Getter
    @Setter
    @JoinColumn(name = "wabo_id", referencedColumnName = "wabo_id")
    @ForeignKey(name = "fk_uses_water_bodies_uswb_id_water_bodies_wabo_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CuerpoHidrico cuerpoHidrico;

}
