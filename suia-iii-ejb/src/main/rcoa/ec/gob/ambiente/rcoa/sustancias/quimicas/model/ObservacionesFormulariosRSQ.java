package ec.gob.ambiente.rcoa.sustancias.quimicas.model;

import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "form_comments_chemical_sustances", schema = "coa_chemical_sustances")
@NamedQueries({
        @NamedQuery(name = ObservacionesFormulariosRSQ.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_SECCION, query = "SELECT u FROM ObservacionesFormulariosRSQ u WHERE u.idClase = :idClase AND u.nombreClase = :nombreClase AND u.seccionFormulario in :seccion ORDER BY u.seccionFormulario, u.id"),
        @NamedQuery(name = ObservacionesFormulariosRSQ.LISTAR_POR_ID_CLASE_NOMBRE_CLASE, query = "SELECT u FROM ObservacionesFormulariosRSQ u WHERE u.idClase = :idClase AND u.nombreClase = :nombreClase ORDER BY u.id"),
        @NamedQuery(name = ObservacionesFormulariosRSQ.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_NO_CORREGIDAS, query = "SELECT u FROM ObservacionesFormulariosRSQ u WHERE u.idClase = :idClase AND u.nombreClase = :nombreClase AND u.observacionCorregida = false ORDER BY u.seccionFormulario"),
        @NamedQuery(name = ObservacionesFormulariosRSQ.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_USUARIO_NO_CORREGIDAS, query = "SELECT u FROM ObservacionesFormulariosRSQ u WHERE u.idClase = :idClase AND u.nombreClase = :nombreClase AND u.usuario.id = :idUsuario AND u.observacionCorregida = false ORDER BY u.seccionFormulario"),
        @NamedQuery(name = ObservacionesFormulariosRSQ.LISTAR_POR_ID_USUARIO, query = "SELECT u FROM ObservacionesFormulariosRSQ u WHERE u.usuario.id = :idUsuario")})

@AttributeOverrides({@AttributeOverride(name = "estado", column = @Column(name = "focs_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "focs_status = 'TRUE'")
public class ObservacionesFormulariosRSQ extends EntidadBase {

    private static final long serialVersionUID = 1L;
    private static final String PAQUETE = "ec.gob.ambiente.rcoa.sustancias.quimicas.model.";
    public static final String LISTAR_POR_ID_CLASE_NOMBRE_CLASE_SECCION = PAQUETE
            + "ObservacionesFormulariosRSQ.listaPorIdClaseNombreClaseSeccion";
    public static final String LISTAR_POR_ID_CLASE_NOMBRE_CLASE = PAQUETE
            + "ObservacionesFormulariosRSQ.listaPorIdClaseNombreClase";
    public static final String LISTAR_POR_ID_CLASE_NOMBRE_CLASE_NO_CORREGIDAS = PAQUETE
            + "ObservacionesFormulariosRSQ.listaPorIdClaseNombreClaseNoCorregidas";
    public static final String LISTAR_POR_ID_CLASE_NOMBRE_CLASE_USUARIO_NO_CORREGIDAS = PAQUETE
            + "ObservacionesFormulariosRSQ.listaPorIdClaseNombreClaseUsuarioNoCorregidas";

    public static final String LISTAR_POR_ID_USUARIO = PAQUETE
            + "ObservacionesFormulariosRSQ.listaPorIdUsuario";
    
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //@SequenceGenerator(name = "OBSERVATION_FORMS_ID_GENERATOR", sequenceName = "focs_id_seq", schema = "suia_iii", allocationSize = 1, initialValue = 1)
    //@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OBSERVATION_FORMS_ID_GENERATOR")
    @Column(name = "focs_id")
    private Integer id;
    @Getter
    @Setter
    @Column(name = "focs_field", length = 255)
    private String campo;
    @Getter
    @Setter
    @Column(name = "focs_description", length = 500)
    private String descripcion;
    @Getter
    @Setter
    @Column(name = "focs_class_name", length = 255)
    private String nombreClase;
    @Getter
    @Setter
    @Column(name = "focs_id_class")
    private Integer idClase;
    @Getter
    @Setter
    @Column(name = "focs_section_form", length = 500)
    private String seccionFormulario;
    @Getter
    @Setter
    @Transient
    private int indice;
    @Getter
    @Setter
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @ManyToOne
    @ForeignKey(name = "fk_form_comments_user_id_user_user_id")
    private Usuario usuario;
    @Getter
    @Setter
    @Column(insertable = false, updatable = false, name = "user_id")
    private Integer idUsuario;
    @Getter
    @Setter
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "focs_date")
    private Date fechaRegistro;
    @Getter
    @Setter
    @Transient
    private boolean disabled;
    @Getter
    @Setter
    @Column(name = "focs_observation_corrected")
    private boolean observacionCorregida;


    @Getter
    @Setter
    @Column(name = "taskId")
    private Long idTarea;


    @Getter
    @Setter
    @Column(name = "focs_answer", length = 500)
    private String respuesta;

}
