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

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * The persistent class for the fapma_activities database table.
 *
 */
@NamedQueries({
    @NamedQuery(name = Actividad.GET_ALL, query = "SELECT a FROM Actividad a"),
    @NamedQuery(name = Actividad.OBTENER_POR_CRONOGRAMA, query = "SELECT a FROM Actividad a WHERE a.cronogramaActividadesPma = :cronogramaActividadesPma")})
@Entity
@Table(name = "catii_activities", schema = "suia_iii")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "caac_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "caac_creation_date")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "caac_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "caac_creator_user")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "caac_user_update"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "caac_status = 'TRUE'")
public class Actividad extends EntidadAuditable {

    public static final String GET_ALL = "ec.com.magmasoft.business.domain.categoriaii.Actividad.getAll";
    public static final String OBTENER_POR_CRONOGRAMA = "ec.com.magmasoft.business.domain.categoriaii.Actividad.obtenerPorCronograma";
	//public static final String SEQUENCE_CODE = "fapma_activities_caac_id_seq";

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "caac_id")
    @SequenceGenerator(name = "ACTIVITY_PMA_CAAC_GENERATOR", sequenceName = "catii_activities_caac_id_seq", schema = "suia_iii", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ACTIVITY_PMA_CAAC_GENERATOR")
    @Getter
    @Setter
    private Integer id;

    @Column(name = "caac_description", length = 255)
    @Getter
    @Setter
    private String descripcion;

    @Column(name = "caac_start_date")
    @Getter
    @Setter
    @Temporal(TemporalType.DATE)
    private Date fechaInicio;

    @Column(name = "caac_end_date")
    @Getter
    @Setter
    @Temporal(TemporalType.DATE)
    private Date fechaFin;

    @ManyToOne
    @JoinColumn(name = "caas_id")
    @ForeignKey(name = "catii_activities_schedule_catii_activities_fk")
    @Getter
    @Setter
    private CronogramaActividadesPma cronogramaActividadesPma;

    @Getter
    @Setter
    @Transient
    private boolean editar;
    
    @Getter
    @Setter
    @Transient
    private int indice;

}
