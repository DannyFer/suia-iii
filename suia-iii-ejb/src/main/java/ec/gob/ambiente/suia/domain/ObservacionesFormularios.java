/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import java.util.Date;

/**
 * @author christian
 */
@Entity
@Table(name = "form_comments", schema = "suia_iii")
@NamedQueries({
        @NamedQuery(name = ObservacionesFormularios.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_SECCION, query = "SELECT u FROM ObservacionesFormularios u WHERE u.idClase = :idClase AND u.nombreClase = :nombreClase AND u.seccionFormulario in :seccion ORDER BY u.seccionFormulario, u.id"),
        @NamedQuery(name = ObservacionesFormularios.LISTAR_POR_ID_CLASE_NOMBRE_CLASE, query = "SELECT u FROM ObservacionesFormularios u WHERE u.idClase = :idClase AND u.nombreClase = :nombreClase ORDER BY u.id"),
        @NamedQuery(name = ObservacionesFormularios.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_NO_CORREGIDAS, query = "SELECT u FROM ObservacionesFormularios u WHERE u.idClase = :idClase AND u.nombreClase = :nombreClase AND u.observacionCorregida = false ORDER BY u.seccionFormulario"),
        @NamedQuery(name = ObservacionesFormularios.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_USUARIO_NO_CORREGIDAS, query = "SELECT u FROM ObservacionesFormularios u WHERE u.idClase = :idClase AND u.nombreClase = :nombreClase AND u.usuario.id = :idUsuario AND u.observacionCorregida = false ORDER BY u.seccionFormulario"),
        @NamedQuery(name = ObservacionesFormularios.LISTAR_POR_ID_USUARIO, query = "SELECT u FROM ObservacionesFormularios u WHERE u.usuario.id = :idUsuario")})

@AttributeOverrides({@AttributeOverride(name = "estado", column = @Column(name = "foco_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "foco_status = 'TRUE'")
public class ObservacionesFormularios extends EntidadBase {

    private static final long serialVersionUID = 5016494380218175574L;
    private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
    public static final String LISTAR_POR_ID_CLASE_NOMBRE_CLASE_SECCION = PAQUETE
            + "ObservacionesFormularios.listaPorIdClaseNombreClaseSeccion";
    public static final String LISTAR_POR_ID_CLASE_NOMBRE_CLASE = PAQUETE
            + "ObservacionesFormularios.listaPorIdClaseNombreClase";
    public static final String LISTAR_POR_ID_CLASE_NOMBRE_CLASE_NO_CORREGIDAS = PAQUETE
            + "ObservacionesFormularios.listaPorIdClaseNombreClaseNoCorregidas";
    public static final String LISTAR_POR_ID_CLASE_NOMBRE_CLASE_USUARIO_NO_CORREGIDAS = PAQUETE
            + "ObservacionesFormularios.listaPorIdClaseNombreClaseUsuarioNoCorregidas";

    public static final String LISTAR_POR_ID_USUARIO = PAQUETE
            + "ObservacionesFormularios.listaPorIdUsuario";
    
    @Getter
    @Setter
    @Id
    @SequenceGenerator(name = "OBSERVATION_FORMS_ID_GENERATOR", sequenceName = "foco_id_seq", schema = "suia_iii", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OBSERVATION_FORMS_ID_GENERATOR")
    @Column(name = "foco_id")
    private Integer id;
    @Getter
    @Setter
    @Column(name = "foco_field", length = 255)
    private String campo;
    @Getter
    @Setter
    @Column(name = "foco_description", length = 500)
    private String descripcion;
    @Getter
    @Setter
    @Column(name = "foco_class_name", length = 255)
    private String nombreClase;
    @Getter
    @Setter
    @Column(name = "foco_id_class")
    private Integer idClase;
    @Getter
    @Setter
    @Column(name = "foco_section_form", length = 500)
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
    @Column(name = "foco_date")
    private Date fechaRegistro;
    @Getter
    @Setter
    @Transient
    private boolean disabled;
    @Getter
    @Setter
    @Column(name = "foco_observation_corrected")
    private boolean observacionCorregida;


    @Getter
    @Setter
    @Column(name = "taskId")
    private Long idTarea;


    @Getter
    @Setter
    @Column(name = "foco_answer", length = 500)
    private String respuesta;

}
