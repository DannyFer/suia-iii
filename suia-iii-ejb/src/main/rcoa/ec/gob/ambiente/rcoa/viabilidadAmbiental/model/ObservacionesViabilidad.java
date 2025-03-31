/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.rcoa.viabilidadAmbiental.model;

import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;

import java.util.Date;

@Entity
@Table(name="form_comments_viability", schema = "coa_viability")
@NamedQueries({
        @NamedQuery(name = ObservacionesViabilidad.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_SECCION, query = "SELECT u FROM ObservacionesViabilidad u WHERE u.idClase = :idClase AND u.nombreClase = :nombreClase AND u.seccionFormulario in :seccion ORDER BY u.seccionFormulario, u.id"),
        @NamedQuery(name = ObservacionesViabilidad.LISTAR_POR_ID_CLASE_NOMBRE_CLASE, query = "SELECT u FROM ObservacionesViabilidad u WHERE u.idClase = :idClase AND u.nombreClase = :nombreClase ORDER BY u.id"),
        @NamedQuery(name = ObservacionesViabilidad.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_NO_CORREGIDAS, query = "SELECT u FROM ObservacionesViabilidad u WHERE u.idClase = :idClase AND u.nombreClase = :nombreClase AND u.observacionCorregida = false ORDER BY u.seccionFormulario"),
        @NamedQuery(name = ObservacionesViabilidad.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_USUARIO_NO_CORREGIDAS, query = "SELECT u FROM ObservacionesViabilidad u WHERE u.idClase = :idClase AND u.nombreClase = :nombreClase AND u.usuario.id = :idUsuario AND u.observacionCorregida = false ORDER BY u.seccionFormulario"),
        @NamedQuery(name = ObservacionesViabilidad.LISTAR_POR_ID_USUARIO, query = "SELECT u FROM ObservacionesViabilidad u WHERE u.usuario.id = :idUsuario")})

@AttributeOverrides({@AttributeOverride(name = "estado", column = @Column(name = "fovi_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "fovi_status = 'TRUE'")
public class ObservacionesViabilidad extends EntidadBase {

    private static final long serialVersionUID = 5016494380218175574L;
    private static final String PAQUETE = "ec.gob.ambiente.rcoa.viabilidadAmbiental.model.";
    public static final String LISTAR_POR_ID_CLASE_NOMBRE_CLASE_SECCION = PAQUETE
            + "ObservacionesViabilidad.listaPorIdClaseNombreClaseSeccion";
    public static final String LISTAR_POR_ID_CLASE_NOMBRE_CLASE = PAQUETE
            + "ObservacionesViabilidad.listaPorIdClaseNombreClase";
    public static final String LISTAR_POR_ID_CLASE_NOMBRE_CLASE_NO_CORREGIDAS = PAQUETE
            + "ObservacionesViabilidad.listaPorIdClaseNombreClaseNoCorregidas";
    public static final String LISTAR_POR_ID_CLASE_NOMBRE_CLASE_USUARIO_NO_CORREGIDAS = PAQUETE
            + "ObservacionesViabilidad.listaPorIdClaseNombreClaseUsuarioNoCorregidas";

    public static final String LISTAR_POR_ID_USUARIO = PAQUETE
            + "ObservacionesViabilidad.listaPorIdUsuario";
    
    @Getter
    @Setter
    @Id
    @SequenceGenerator(name = "OBSERVATION_VIABILITY_ID_GENERATOR", sequenceName = "form_comments_viability_fovi_id_seq", schema = "coa_viability", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OBSERVATION_VIABILITY_ID_GENERATOR")
    @Column(name = "fovi_id")
    private Integer id;
    @Getter
    @Setter
    @Column(name = "fovi_field", length = 255)
    private String campo;
    @Getter
    @Setter
    @Column(name = "fovi_description", length = 500)
    private String descripcion;
    @Getter
    @Setter
    @Column(name = "fovi_class_name", length = 255)
    private String nombreClase;
    @Getter
    @Setter
    @Column(name = "fovi_id_class")
    private Integer idClase;
    @Getter
    @Setter
    @Column(name = "fovi_section_form", length = 500)
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
    @Column(name = "fovi_date")
    private Date fechaRegistro;
    @Getter
    @Setter
    @Transient
    private boolean disabled;
    @Getter
    @Setter
    @Column(name = "fovi_observation_corrected")
    private boolean observacionCorregida;


    @Getter
    @Setter
    @Column(name = "taskId")
    private Long idTarea;


    @Getter
    @Setter
    @Column(name = "fovi_answer", length = 500)
    private String respuesta;

}
