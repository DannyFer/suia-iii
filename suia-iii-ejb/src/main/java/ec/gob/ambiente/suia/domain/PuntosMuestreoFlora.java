/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.domain;

import java.util.Collection;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Filter;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 *
 * @author 
 */
@Entity
@Table(name = "sampling_points_flora", schema = "suia_iii")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "spfl_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "flsp_status = 'TRUE'")
@NamedQueries({
    @NamedQuery(name = "PuntosMuestreoFlora.findAll", query = "SELECT s FROM PuntosMuestreoFlora s"),
    @NamedQuery(name = "PuntosMuestreoFlora.findByFloraId", query = "SELECT s FROM PuntosMuestreoFlora s where s.estado = true and s.idFlora = :p_floraId")

})
public class PuntosMuestreoFlora extends EntidadBase {

    /**
	 * 
	 */
	private static final long serialVersionUID = -8229710686984131571L;
	
	@Id
    @Basic(optional = false)
    @Getter
    @Setter
    @SequenceGenerator(name = "PUNTOS_MUESTREO_FLORA_ID_GENERATOR", initialValue = 1, sequenceName = "seq_spfl_id", schema = "suia_iii", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PUNTOS_MUESTREO_FLORA_ID_GENERATOR")
    @Column(name = "spfl_id", nullable = false)
    private Integer id;

    @Getter
    @Setter
    @Column(name = "spfl_sampling_date")
    @Temporal(TemporalType.DATE)
    private Date fechaMuestreo;

    @Getter
    @Setter
    @Column(name = "geca_sampling_rate_id")
    private Integer idCatalogoTipoMuestreo;

    @Getter
    @Setter
    @Column(name = "gefl_id")
    private Integer idFlora;
    
    @Getter
    @Setter
    @Column(name = "spfl_sampling_effort", length = 255)
    private String esfuerzoMuestreo;

    @Getter
    @Setter
    @Column(name = "spfl_sampling_point", length = 255)
    private String puntoMuestreo;

	@Getter
	@Setter
	@Column(name = "geca_vegetation_type", nullable = false)
	private Integer tipoVegetacion;

    @Getter
    @Setter
    @Transient
    private Collection<FloraEspecie> listaFloraEspecie;

    @Getter
    @Setter
    @Transient
    private Collection<CoordenadaGeneral> listaCoordenadas;

}