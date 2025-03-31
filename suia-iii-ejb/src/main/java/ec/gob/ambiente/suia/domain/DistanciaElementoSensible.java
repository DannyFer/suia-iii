package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by juangabriel on 14/07/15.
 */
@Entity
@Table(name = "sensitive_element_distances", schema = "suia_iii")
@AttributeOverrides({
        @AttributeOverride(name = "estado", column = @Column(name = "sedi_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "sedi_status = 'TRUE'")
public class DistanciaElementoSensible extends EntidadBase implements Serializable, Cloneable {

    @Id
    @Getter
    @Setter
    @SequenceGenerator(name = "DISTANCIA_ELEMENTO_SENSIBLE_ID_GENERATOR", initialValue = 1, sequenceName = "seq_sedi_id", schema = "suia_iii", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DISTANCIA_ELEMENTO_SENSIBLE_ID_GENERATOR")
    @Column(name = "sedi_id")
    private Integer id;


    @Getter
    @Setter
    @JoinColumn(name = "seel_id", referencedColumnName = "seel_id")
    @ForeignKey(name = "fk_sensitive_el_distseel_id_sensitive_elementsseel_id")
    @ManyToOne
    private ElementoSensible elementoSensible;

    @Getter
    @Setter
    @JoinColumn(name = "afin_id", referencedColumnName = "afin_id")
    @ForeignKey(name = "fk_sensitive_el_distafin_id_affected_infraestructuresafin_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private InfraestructuraAfectada infraestructuraAfectada;


    @Getter
    @Setter
    @Column(name = "sedi_description", length = 150)
    private String descripcion;

    @Getter
    @Setter
    @Column(name = "sedi_distance")
    private Double distancia;

    @Getter
    @Setter
    @Column(name = "sedi_another_sensitive_element", length = 150)
    private String otroElemento;

	@Getter
	@Setter
	@Column(name = "sedi_historical_id")
	private Integer idHistorico;
	
	@Getter
	@Setter
	@Column(name = "sedi_notification_number")
	private Integer numeroNotificacion;
	
	@Getter
	@Setter
	@Transient
	private Boolean nuevoEnModificacion;
	
	@Getter
	@Setter
	@Transient
	private Boolean registroModificado;
	
	@Getter
	@Setter
	@Column(name = "sedi_date_create")
	private Date fechaCreacion;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DistanciaElementoSensible that = (DistanciaElementoSensible) o;

        if (id != that.id) return false;
        if (descripcion != null ? !descripcion.equals(that.descripcion) : that.descripcion != null)
            return false;
        if (distancia != null ? !distancia.equals(that.distancia) : that.distancia != null) return false;


        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (descripcion != null ? descripcion.hashCode() : 0);
        result = 31 * result + (distancia != null ? distancia.hashCode() : 0);

        return result;
    }
    
    @Override
	public DistanciaElementoSensible clone() throws CloneNotSupportedException {

		 DistanciaElementoSensible clone = (DistanciaElementoSensible)super.clone();
		 clone.setId(null);		 
		 return clone;
	}
}
