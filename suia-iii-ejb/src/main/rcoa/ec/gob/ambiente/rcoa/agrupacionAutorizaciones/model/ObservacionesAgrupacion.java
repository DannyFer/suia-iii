package ec.gob.ambiente.rcoa.agrupacionAutorizaciones.model;

import java.util.Date;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.base.EntidadBase;


/**
 * The persistent class for the form_comments_grouping database table.
 * 
 */
@Entity
@Table(name="form_comments_grouping", schema = "coa_grouping")
@NamedQueries({
        @NamedQuery(name = ObservacionesAgrupacion.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_SECCION, query = "SELECT u FROM ObservacionesAgrupacion u WHERE u.idClase = :idClase AND u.nombreClase = :nombreClase AND u.seccionFormulario in :seccion ORDER BY u.seccionFormulario, u.id"),
        @NamedQuery(name = ObservacionesAgrupacion.LISTAR_POR_ID_CLASE_NOMBRE_CLASE, query = "SELECT u FROM ObservacionesAgrupacion u WHERE u.idClase = :idClase AND u.nombreClase = :nombreClase ORDER BY u.id"),
        @NamedQuery(name = ObservacionesAgrupacion.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_NO_CORREGIDAS, query = "SELECT u FROM ObservacionesAgrupacion u WHERE u.idClase = :idClase AND u.nombreClase = :nombreClase AND u.observacionCorregida = false ORDER BY u.seccionFormulario"),
        @NamedQuery(name = ObservacionesAgrupacion.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_USUARIO_NO_CORREGIDAS, query = "SELECT u FROM ObservacionesAgrupacion u WHERE u.idClase = :idClase AND u.nombreClase = :nombreClase AND u.usuario.id = :idUsuario AND u.observacionCorregida = false ORDER BY u.seccionFormulario"),
        @NamedQuery(name = ObservacionesAgrupacion.LISTAR_POR_ID_USUARIO, query = "SELECT u FROM ObservacionesAgrupacion u WHERE u.usuario.id = :idUsuario")})

@AttributeOverrides({@AttributeOverride(name = "estado", column = @Column(name = "fogr_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "fogr_status = 'TRUE'")
public class ObservacionesAgrupacion extends EntidadBase {
	private static final long serialVersionUID = 1L;
	
    private static final String PAQUETE = "ec.gob.ambiente.rcoa.agrupacionAutorizaciones.model.";
    public static final String LISTAR_POR_ID_CLASE_NOMBRE_CLASE_SECCION = PAQUETE
            + "ObservacionesAgrupacion.listaPorIdClaseNombreClaseSeccion";
    public static final String LISTAR_POR_ID_CLASE_NOMBRE_CLASE = PAQUETE
            + "ObservacionesAgrupacion.listaPorIdClaseNombreClase";
    public static final String LISTAR_POR_ID_CLASE_NOMBRE_CLASE_NO_CORREGIDAS = PAQUETE
            + "ObservacionesAgrupacion.listaPorIdClaseNombreClaseNoCorregidas";
    public static final String LISTAR_POR_ID_CLASE_NOMBRE_CLASE_USUARIO_NO_CORREGIDAS = PAQUETE
            + "ObservacionesAgrupacion.listaPorIdClaseNombreClaseUsuarioNoCorregidas";

    public static final String LISTAR_POR_ID_USUARIO = PAQUETE
            + "ObservacionesAgrupacion.listaPorIdUsuario";
    
    @Getter
    @Setter
    @Id
    @SequenceGenerator(name = "OBSERVATION_GROUPING_ID_GENERATOR", sequenceName = "form_comments_grouping_fogr_id_seq", schema = "coa_grouping", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OBSERVATION_GROUPING_ID_GENERATOR")
    @Column(name = "fogr_id")
    private Integer id;
    @Getter
    @Setter
    @Column(name = "fogr_field", length = 255)
    private String campo;
    @Getter
    @Setter
    @Column(name = "fogr_description", length = 500)
    private String descripcion;
    @Getter
    @Setter
    @Column(name = "fogr_class_name", length = 255)
    private String nombreClase;
    @Getter
    @Setter
    @Column(name = "fogr_id_class")
    private Integer idClase;
    @Getter
    @Setter
    @Column(name = "fogr_section_form", length = 500)
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
    @Column(name = "fogr_date")
    private Date fechaRegistro;
    @Getter
    @Setter
    @Transient
    private boolean disabled;
    @Getter
    @Setter
    @Column(name = "fogr_observation_corrected")
    private boolean observacionCorregida;

    @Getter
    @Setter
    @Column(name = "taskId")
    private Long idTarea;

    @Getter
    @Setter
    @Column(name = "fogr_answer", length = 500)
    private String respuesta;
}