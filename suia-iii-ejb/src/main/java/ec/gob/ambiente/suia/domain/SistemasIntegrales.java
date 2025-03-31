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
import javax.persistence.NamedQueries; 
import javax.persistence.NamedQuery; 
import javax.persistence.SequenceGenerator; 
import javax.persistence.Table; 

import org.hibernate.annotations.Filter;

import lombok.Getter; 
import lombok.Setter; 
import ec.gob.ambiente.suia.domain.base.EntidadBase; 
 
/** 
 * <b> AGREGAR DESCRIPCION. </b> 
 *  
 * @author Santiago Flores 
 * @version Revision: 1.0 
 *          <p> 
 *          [Autor: Santiago Flores, Fecha: 22/12/2015] 
 *          </p> 
 */ 
@NamedQueries({ @NamedQuery(name = SistemasIntegrales.GET_SISTEMAS_INTEGRALES_PROYECTO, query = "SELECT s FROM SistemasIntegrales s WHERE s.proyectoLicenciamientoAmbiental = :p_proyecto") }) 
@Entity 
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "isys_status")) }) 
@Table(name = "integrated_systems", schema = "suia_iii") 
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "isys_status = 'TRUE'")
public class SistemasIntegrales extends EntidadBase { 
 
    /** 
     *  
     */ 
    public static final String GET_SISTEMAS_INTEGRALES_PROYECTO = "ec.gob.ambiente.suia.domain.SistemasIntegrales.getSistemasIntegralesProyecto"; 
    private static final long serialVersionUID = -2173922016468662219L; 
 
    @Id 
    @SequenceGenerator(name = "INTEGRATED_SYSTEM_SEQUENCE", sequenceName = "integrated_systems_isys_id_seq", schema = "suia_iii", allocationSize = 1) 
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "INTEGRATED_SYSTEM_SEQUENCE") 
    @Column(name = "isys_id") 
    @Getter 
    @Setter 
    private Integer id; 
 
    @Getter 
    @Setter 
    @Column(name = "isys_description",length = 2550) 
    private String descripcion; 
 
    @Getter 
    @Setter 
    @Column(name = "isys_selected") 
    private boolean seleccionado; 
     
    @Getter 
    @Setter 
    @ManyToOne 
    @JoinColumn(name = "pren_id") 
    private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental; 
    
    @Getter 
    @Setter 
    @Column(name = "isys_code") 
    private String codigo; 
} 