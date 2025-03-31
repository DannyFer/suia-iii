/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;

/**
 *
 * @author ishmael
 */
@Entity
@Table(name = "physical_environment_weather", schema = "suia_iii")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "phew_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "phew_creation_date")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "phew_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "phew_creator_user")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "phew_user_update"))})
@NamedQueries({
    @NamedQuery(name = Clima.OBTENER_POR_EIA, query = "SELECT i FROM Clima i WHERE i.estudioImpactoAmbiental = :estudioImpactoAmbiental")})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "phew_status = 'TRUE'")
public class Clima extends EntidadAuditable {

    public static final String OBTENER_POR_EIA = "ec.gob.ambiente.suia.domain.Clima.obtenerPorEia";

    @Id
    @Basic(optional = false)
    @Getter
    @Setter
    @SequenceGenerator(name = "CLIMA_ID_GENERATOR", initialValue = 1, sequenceName = "seq_phew_id", schema = "suia_iii", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CLIMA_ID_GENERATOR")
    @Column(name = "phew_id", nullable = false)
    private Integer id;

    @Getter
    @Setter
    @Column(name = "phew_precipitation_minimum")
    private Double precipitacionMinima;

    @Getter
    @Setter
    @Column(name = "phew_precipitation_average")
    private Double precipitacionPromedio;

    @Getter
    @Setter
    @Column(name = "phew_precipitation_maximum")
    private Double precipitacionMaxima;

    @Getter
    @Setter
    @Column(name = "phew_precipitation_origin")
    private String precipitacionFuente;


    @Getter
    @Setter
    @Column(name = "phew_temperature_minimum")
    private Double temperaturaMinima;

    @Getter
    @Setter
    @Column(name = "phew_temperature_average")
    private Double temperaturaPromedio;

    @Getter
    @Setter
    @Column(name = "phew_temperature_maximum")
    private Double temperaturaMaxima;

    @Getter
    @Setter
    @Column(name = "phew_temperature_origin")
    private String temperaturaFuente;


    @Getter
    @Setter
    @Column(name = "phew_wetness_minimum")
    private Double humedadMinima;

    @Getter
    @Setter
    @Column(name = "phew_wetness_average")
    private Double humedadPromedio;

    @Getter
    @Setter
    @Column(name = "phew_wetness_maximum")
    private Double humedadMaxima;

    @Getter
    @Setter
    @Column(name = "phew_wetness_origin")
    private String humedadFuente;

    @Getter
    @Setter
    @Column(name = "phew_wind_speed_minimum")
    private Double velocidadVientoMinima;

    @Getter
    @Setter
    @Column(name = "phew_wind_speed_average")
    private Double velocidadVientoPromedio;

    @Getter
    @Setter
    @Column(name = "phew_wind_speed_maximum")
    private Double velocidadVientoMaxima;

    @Getter
    @Setter
    @Column(name = "phew_wind_speed_origin")
    private String velocidadVientoFuente;

    @Getter
    @Setter
    @Column(name = "phew_evapotranspiration_minimum")
    private Double evapotranspiracionMinima;

    @Getter
    @Setter
    @Column(name = "phew_evapotranspiration_average")
    private Double evapotranspiracionPromedio;

    @Getter
    @Setter
    @Column(name = "phew_evapotranspiration_maximum")
    private Double evapotranspiracionMaxima;

    @Getter
    @Setter
    @Column(name = "phew_evapotranspiration_origin")
    private String evapotranspiracionFuente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eist_id")
    @ForeignKey(name = "fk_environmental_impact_studieseist_id_physical_environment_weather_id")
    @Getter
    @Setter
    private EstudioImpactoAmbiental estudioImpactoAmbiental;

    @Transient
    @Getter
    @Setter
    private int indice;
    @Transient
    @Getter
    @Setter
    private boolean editar;

}
