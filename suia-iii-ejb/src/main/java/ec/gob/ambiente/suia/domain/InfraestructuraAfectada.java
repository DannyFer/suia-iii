package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by juangabriel on 14/07/15.
 */
@NamedQueries({ @NamedQuery(name = InfraestructuraAfectada.FIND_INFRAESTRUCTURAS_AFECTADAS_BY_ESTUDIO, query = "select ia FROM InfraestructuraAfectada ia where ia.estudioImpactoAmbiental = :estudio and ia.idHistorico = null order by ia.id") })
@Entity
@Table(name = "affected_infrastructures", schema = "suia_iii")
@AttributeOverrides({
        @AttributeOverride(name = "estado", column = @Column(name = "afin_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "afin_status = 'TRUE'")
public class InfraestructuraAfectada extends EntidadBase implements Serializable, Cloneable {

    private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
    public static final String FIND_INFRAESTRUCTURAS_AFECTADAS_BY_ESTUDIO = "InfraestructuraAfectada.findInfraestructurasAfectadasByEstudio";


    @Id
    @Getter
    @Setter
    @SequenceGenerator(name = "INFRAESTRUCTURA_AFECTADA_ID_GENERATOR", initialValue = 1, sequenceName = "seq_afin_id", schema = "suia_iii", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "INFRAESTRUCTURA_AFECTADA_ID_GENERATOR")
    @Column(name = "afin_id")
    private Integer id;

    @Getter
    @Setter
    @JoinColumn(name = "eist_id")
    @ForeignKey(name = "environmental_impact_studies_eist_id_fk")
    @ManyToOne
    private EstudioImpactoAmbiental estudioImpactoAmbiental;

    @Getter
    @Setter
    @Column(name = "afin_name", length = 500)
    private String nombre;

    @Getter
    @Setter
    @Column(name = "afin_owner", length = 500)
    private String propietario;

    @Getter
    @Setter
    @Column(name = "afin_clanship", length = 500)
    private String comunidad;


    @Getter
    @Setter
    @Column(name = "afin_location", length = 500)
    private String lugar;

    @Getter
    @Setter
    @Column(name = "afin_another_jurisdiction", length = 500)
    private String otraJurisdiccion;
    
	@Getter
	@Setter
	@Column(name = "afin_historical_id")
	private Integer idHistorico;
	
	@Getter
	@Setter
	@Column(name = "afin_notification_number")
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
	@Column(name = "afin_date_create")
	private Date fechaCreacion;

    @Getter
    @Setter
    @OneToMany(mappedBy = "infraestructuraAfectada")
    @Filter(name = EntidadBase.FILTER_ACTIVE, condition = "sedi_status = 'TRUE'")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<DistanciaElementoSensible> distanciaElementoSensibles;



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InfraestructuraAfectada that = (InfraestructuraAfectada) o;

        if (id != that.id) return false;
        if (nombre != null ? !nombre.equals(that.nombre) : that.nombre != null) return false;
        if (propietario != null ? !propietario.equals(that.propietario) : that.propietario != null) return false;
        if (comunidad != null ? !comunidad.equals(that.comunidad) : that.comunidad != null) return false;
        if (lugar != null ? !lugar.equals(that.lugar) : that.lugar != null) return false;
        if (otraJurisdiccion != null ? !otraJurisdiccion.equals(that.otraJurisdiccion) : that.otraJurisdiccion != null)
            return false;


        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (nombre != null ? nombre.hashCode() : 0);
        result = 31 * result + (propietario != null ? propietario.hashCode() : 0);
        result = 31 * result + (comunidad != null ? comunidad.hashCode() : 0);
        result = 31 * result + (lugar != null ? lugar.hashCode() : 0);
        result = 31 * result + (otraJurisdiccion != null ? otraJurisdiccion.hashCode() : 0);
        return result;
    }
    
    @Override
	public InfraestructuraAfectada clone() throws CloneNotSupportedException {

		 InfraestructuraAfectada clone = (InfraestructuraAfectada)super.clone();
		 clone.setId(null);		 
		 return clone;
	}
}
