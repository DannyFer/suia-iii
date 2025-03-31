/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import ec.gob.ambiente.suia.domain.interfaces.CoordinatesContainer;

@Entity
@Table(name = "coordinates_eia_shapes", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "cooe_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "cooe_status = 'TRUE'")
public class FormaCoordenadasEIA extends EntidadBase implements CoordinatesContainer {

    private static final long serialVersionUID = 3200901777244850048L;

    @Getter
    @Setter
    @Id
    @SequenceGenerator(name = "COORDINATES_EIA_SHAPES_GENERATOR", initialValue = 1, sequenceName = "seq_cooe_id", schema = "suia_iii", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COORDINATES_EIA_SHAPES_GENERATOR")
    @Column(name = "cooe_id")
    private Integer id;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "shty_id")
    private TipoForma tipoForma;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "eist_id")
    private EstudioImpactoAmbiental estudioImpactoAmbiental;

    @Getter
    @Setter
    @OneToMany(mappedBy = "formasCoordinatesEIA")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Coordenada> coordenadas;

    public FormaCoordenadasEIA() {
        coordenadas = new ArrayList<Coordenada>();
    }
}
