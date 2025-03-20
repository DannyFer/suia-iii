/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.rcoa.model;

import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;

import java.util.Date;

@Entity
@Table(name="form_comments_coa", schema = "coa_mae")
@NamedQueries({
        @NamedQuery(name = ObservacionesPreliminar.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_SECCION, query = "SELECT u FROM ObservacionesPreliminar u WHERE u.idClase = :idClase AND u.nombreClase = :nombreClase AND u.seccionFormulario in :seccion ORDER BY u.seccionFormulario, u.id"),
        @NamedQuery(name = ObservacionesPreliminar.LISTAR_POR_ID_CLASE_NOMBRE_CLASE, query = "SELECT u FROM ObservacionesPreliminar u WHERE u.idClase = :idClase AND u.nombreClase = :nombreClase ORDER BY u.id"),
        @NamedQuery(name = ObservacionesPreliminar.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_NO_CORREGIDAS, query = "SELECT u FROM ObservacionesPreliminar u WHERE u.idClase = :idClase AND u.nombreClase = :nombreClase AND u.observacionCorregida = false ORDER BY u.seccionFormulario"),
        @NamedQuery(name = ObservacionesPreliminar.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_USUARIO_NO_CORREGIDAS, query = "SELECT u FROM ObservacionesPreliminar u WHERE u.idClase = :idClase AND u.nombreClase = :nombreClase AND u.usuario.id = :idUsuario AND u.observacionCorregida = false ORDER BY u.seccionFormulario"),
        @NamedQuery(name = ObservacionesPreliminar.LISTAR_POR_ID_USUARIO, query = "SELECT u FROM ObservacionesPreliminar u WHERE u.usuario.id = :idUsuario")})

@AttributeOverrides({@AttributeOverride(name = "estado", column = @Column(name = "foco_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "foco_status = 'TRUE'")
public class ObservacionesPreliminar extends EntidadBase {

    private static final long serialVersionUID = 5016494380218175574L;
    private static final String PAQUETE = "ec.gob.ambiente.rcoa.model.";
    public static final String LISTAR_POR_ID_CLASE_NOMBRE_CLASE_SECCION = PAQUETE
            + "ObservacionesPreliminar.listaPorIdClaseNombreClaseSeccion";
    public static final String LISTAR_POR_ID_CLASE_NOMBRE_CLASE = PAQUETE
            + "ObservacionesPreliminar.listaPorIdClaseNombreClase";
    public static final String LISTAR_POR_ID_CLASE_NOMBRE_CLASE_NO_CORREGIDAS = PAQUETE
            + "ObservacionesPreliminar.listaPorIdClaseNombreClaseNoCorregidas";
    public static final String LISTAR_POR_ID_CLASE_NOMBRE_CLASE_USUARIO_NO_CORREGIDAS = PAQUETE
            + "ObservacionesPreliminar.listaPorIdClaseNombreClaseUsuarioNoCorregidas";

    public static final String LISTAR_POR_ID_USUARIO = PAQUETE
            + "ObservacionesPreliminar.listaPorIdUsuario";
    
    @Getter
    @Setter
    @Id
    @SequenceGenerator(name = "OBSERVATION_PRELIMINARY_ID_GENERATOR", sequenceName = "form_comments_coa_foco_id_seq", schema = "coa_mae", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OBSERVATION_PRELIMINARY_ID_GENERATOR")
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
