package ec.gob.ambiente.suia.domain;


import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name = "contaminated_sites", schema = "suia_iii")
@AttributeOverrides({
        @AttributeOverride(name = "estado", column = @Column(name = "cosi_status"))

         })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "cosi_status = 'TRUE'")
@NamedQueries({ @NamedQuery(name = IdentificacionSitiosContaminadosFuentesContaminacion.LISTAR_POR_ID_EIA, query = "SELECT c FROM IdentificacionSitiosContaminadosFuentesContaminacion c WHERE c.estado=true AND c.eist_id = :eist_id") })
public class IdentificacionSitiosContaminadosFuentesContaminacion extends EntidadBase {

    private static final long serialVersionUID = 1218304605831535129L;
    private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
    public static final String LISTAR_POR_ID_EIA = PAQUETE
           + "IdentificacionSitiosContaminadosFuentesContaminacion.listarPorIdEia";
    @Getter
    @Setter
    @Id
    @SequenceGenerator(name = "CONTAMINATED_SITES_GENERATOR", initialValue = 1, schema = "suia_iii", sequenceName = "seq_cosi_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CONTAMINATED_SITES_GENERATOR")
    @Column(name = "cosi_id", unique = true, nullable = false)
    private Integer id;

    @Getter
    @Setter
    @Column(name = "cosi_eist_id")
    private Integer eist_id;


    @Getter
    @Setter
    @Column(name = "cosi_environmental_component_affected")
    private String componenteAmbientalAfectado;

    @Getter
    @Setter
    @Column(name = "cosi_x_coordinate")
    private Double coordenadaX;

    @Getter
    @Setter
    @Column(name = "cosi_y_coordinate")
    private Double coordenadaY;
    @Getter
    @Setter
    @Column(name = "cosi_source_pollution")
    private String fuenteContaminacion;


}

