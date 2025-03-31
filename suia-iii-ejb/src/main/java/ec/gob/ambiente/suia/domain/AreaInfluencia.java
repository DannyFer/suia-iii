package ec.gob.ambiente.suia.domain;

import java.io.Serializable;
import java.util.Date;

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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * Created by juangabriel on 14/07/15.
 */
@Entity
@Table(name = "influency_areas", schema = "suia_iii")
@AttributeOverrides({
        @AttributeOverride(name = "estado", column = @Column(name = "inar_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "inar_status = 'TRUE'")
@NamedQueries({
        @NamedQuery(name = AreaInfluencia.FIND_BY_ESTUDIO, query = "SELECT a FROM AreaInfluencia a WHERE a.estudioImpactoAmbiental.id = :paramId and a.idHistorico = null")
})
public class AreaInfluencia extends EntidadBase implements Serializable, Cloneable {

    private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
    public static final String FIND_BY_ESTUDIO = PAQUETE + "finbByEstudio";

    @Id
    @Column(name = "inar_id")
    @Getter
    @Setter
    @SequenceGenerator(name = "AREA_INFLUENCIA_ID_GENERATOR", initialValue = 1, sequenceName = "seq_inar_id", schema = "suia_iii", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AREA_INFLUENCIA_ID_GENERATOR")
    private Integer id;

    @Getter
    @Setter
    @JoinColumn(name = "eist_id", referencedColumnName = "eist_id")
    @ForeignKey(name = "fk_influency_areaseist_id_environmental_impact_studieseist_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private EstudioImpactoAmbiental estudioImpactoAmbiental;


    @Getter
    @Setter
    @Column(name = "inar_direct_physical_description", length = 2200)
    private String directaFisicoDescripcion;


    @Getter
    @Setter
    @Column(name = "inar_direct_physical_distance")
    private Double directaFisicoDistancia;

    @Getter
    @Setter
    @Column(name = "inar_direct_physical_distance_unity")    
    private String directaFisicoDistanciaUnidad;

    @Getter
    @Setter
    @Column(name = "inar_direct_biotic_description", length = 2200)
    private String directaBioticoDescripcion;

    @Getter
    @Setter
    @Column(name = "inar_direct_biotic_distance")
    private Double directaBioticoDistancia;

    @Getter
    @Setter
    @Column(name = "inar_direct_biotic_distance_unity")    
    private String directaBioticoDistanciaUnidad;

    @Getter
    @Setter
    @Column(name = "inar_indirect_physical_description", length = 2200)
    private String indirectaFisicoDescripcion;

    @Getter
    @Setter
    @Column(name = "inar_indirect_physical_distance")
    private Double indirectaFisicoDistancia;

    @Getter
    @Setter
    @Column(name = "inar_indirect_physical_distance_unity")
    private String indirectaFisicoDistanciaUnidad;

    @Getter
    @Setter
    @Column(name = "inar_indirect_biotic_description", length = 2200)
    private String indirectaBioticoDescripcion;

    @Getter
    @Setter
    @Column(name = "inar_indirect_biotic_distance")
    private Double indirectaBioticoDistancia;

    @Getter
    @Setter
    @Column(name = "inar_indirect_biotic_distance_unity")
    private String indirectaBioticoDistanciaUnidad;
    
	@Getter
	@Setter
	@Column(name = "inar_historical_id")
	private Integer idHistorico;
	
	@Getter
	@Setter
	@Column(name = "inar_notification_number")
	private Integer numeroNotificacion;
	
	@Getter
	@Setter
	@Column(name = "inar_date_create")
	private Date fechaCreacion;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AreaInfluencia that = (AreaInfluencia) o;

        if (id!= that.id) return false;
        if (directaFisicoDescripcion != null ? !directaFisicoDescripcion.equals(that.directaFisicoDescripcion) : that.directaFisicoDescripcion!= null)
            return false;
        if (directaFisicoDistancia != null ? !directaFisicoDistancia.equals(that.directaFisicoDistancia) : that.directaFisicoDistancia != null)
            return false;
        if (directaFisicoDistanciaUnidad != null ? !directaFisicoDistanciaUnidad.equals(that.directaFisicoDistanciaUnidad) : that.directaFisicoDistanciaUnidad != null)
            return false;
        if (directaBioticoDescripcion != null ? !directaBioticoDescripcion.equals(that.directaBioticoDescripcion) : that.directaBioticoDescripcion != null)
            return false;
        if (directaBioticoDistancia != null ? !directaBioticoDistancia.equals(that.directaBioticoDistancia) : that.directaBioticoDistancia != null)
            return false;
        if (directaBioticoDistanciaUnidad != null ? !directaBioticoDistanciaUnidad.equals(that.directaBioticoDistanciaUnidad) : that.directaBioticoDistanciaUnidad != null)
            return false;
        if (indirectaFisicoDescripcion != null ? !indirectaFisicoDescripcion.equals(that.indirectaFisicoDescripcion) : that.indirectaFisicoDescripcion != null)
            return false;
        if (indirectaFisicoDistancia != null ? !indirectaFisicoDistancia.equals(that.indirectaFisicoDistancia) : that.indirectaFisicoDistancia != null)
            return false;
        if (indirectaFisicoDistanciaUnidad != null ? !indirectaFisicoDistanciaUnidad.equals(that.indirectaFisicoDistanciaUnidad) : that.indirectaFisicoDistanciaUnidad != null)
            return false;
        if (indirectaBioticoDistancia != null ? !indirectaBioticoDistancia.equals(that.indirectaBioticoDistancia) : that.indirectaBioticoDistancia != null)
            return false;
        if (indirectaBioticoDistancia != null ? !indirectaBioticoDistancia.equals(that.indirectaBioticoDistancia) : that.indirectaBioticoDistancia != null)
            return false;
        if (indirectaBioticoDistanciaUnidad != null ? !indirectaBioticoDistanciaUnidad.equals(that.indirectaBioticoDistanciaUnidad) : that.indirectaBioticoDistanciaUnidad != null)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (directaFisicoDescripcion != null ? directaFisicoDescripcion.hashCode() : 0);
        result = 31 * result + (directaFisicoDistancia != null ? directaFisicoDistancia.hashCode() : 0);
        result = 31 * result + (directaFisicoDistanciaUnidad != null ? directaFisicoDistanciaUnidad.hashCode() : 0);
        result = 31 * result + (directaBioticoDescripcion != null ? directaBioticoDescripcion.hashCode() : 0);
        result = 31 * result + (directaBioticoDistancia != null ? directaBioticoDistancia.hashCode() : 0);
        result = 31 * result + (directaBioticoDistanciaUnidad != null ? directaBioticoDistanciaUnidad.hashCode() : 0);
        result = 31 * result + (indirectaFisicoDescripcion != null ? indirectaFisicoDescripcion.hashCode() : 0);
        result = 31 * result + (indirectaFisicoDistancia != null ? indirectaFisicoDistancia.hashCode() : 0);
        result = 31 * result + (indirectaFisicoDistanciaUnidad != null ? indirectaFisicoDistanciaUnidad.hashCode() : 0);
        result = 31 * result + (indirectaBioticoDistancia != null ? indirectaBioticoDistancia.hashCode() : 0);
        result = 31 * result + (indirectaBioticoDistancia != null ? indirectaBioticoDistancia.hashCode() : 0);
        result = 31 * result + (indirectaBioticoDistanciaUnidad != null ? indirectaBioticoDistanciaUnidad.hashCode() : 0);
        return result;
    }
    
    @Override
	public AreaInfluencia clone() throws CloneNotSupportedException {

		 AreaInfluencia clone = (AreaInfluencia)super.clone();
		 clone.setId(null);		 
		 return clone;
	}
}
