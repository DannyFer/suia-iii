/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model;

import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;

import java.util.Date;

@Entity
@Table(name="form_comments_impact", schema = "coa_environmental_impact_study")
@NamedQueries({
        @NamedQuery(name = ObservacionesEsIA.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_SECCION, query = "SELECT u FROM ObservacionesEsIA u WHERE u.idClase = :idClase AND u.nombreClase = :nombreClase AND u.seccionFormulario in :seccion ORDER BY u.seccionFormulario, u.id"),
        @NamedQuery(name = ObservacionesEsIA.LISTAR_POR_ID_CLASE_NOMBRE_CLASE, query = "SELECT u FROM ObservacionesEsIA u WHERE u.idClase = :idClase AND u.nombreClase = :nombreClase ORDER BY u.id"),
        @NamedQuery(name = ObservacionesEsIA.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_NO_CORREGIDAS, query = "SELECT u FROM ObservacionesEsIA u WHERE u.idClase = :idClase AND u.nombreClase = :nombreClase AND u.observacionCorregida = false ORDER BY u.id"),
        @NamedQuery(name = ObservacionesEsIA.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_USUARIO_NO_CORREGIDAS, query = "SELECT u FROM ObservacionesEsIA u WHERE u.idClase = :idClase AND u.nombreClase = :nombreClase AND u.usuario.id = :idUsuario AND u.observacionCorregida = false ORDER BY u.seccionFormulario"),
        @NamedQuery(name = ObservacionesEsIA.LISTAR_POR_ID_USUARIO, query = "SELECT u FROM ObservacionesEsIA u WHERE u.usuario.id = :idUsuario"),
        @NamedQuery(name = ObservacionesEsIA.LISTAR_POR_ID_CLASE_SECCION_NO_CORREGIDAS, query = "SELECT u FROM ObservacionesEsIA u WHERE u.idClase = :idClase AND u.seccionFormulario in :seccion AND u.observacionCorregida = false ORDER BY u.seccionFormulario, u.id"),
        @NamedQuery(name = ObservacionesEsIA.LISTAR_POR_ID_CLASE_SECCION_ALL, query = "SELECT u FROM ObservacionesEsIA u WHERE u.idClase = :idClase AND u.seccionFormulario in :seccion ORDER BY u.seccionFormulario, u.id"),
        @NamedQuery(name = ObservacionesEsIA.LISTAR_POR_ID_CLASE_NO_CORREGIDAS, query = "SELECT u FROM ObservacionesEsIA u, InformeTecnicoEsIA i WHERE u.idTarea = i.idTarea and u.idClase = :idClase and u.observacionCorregida = false and u.estado = true and u.nombreClase <> :nombreClase ORDER BY i.codigoInforme, u.idTarea, u.id"),
        @NamedQuery(name = ObservacionesEsIA.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_SECCION_NO_CORREGIDAS, query = "SELECT u FROM ObservacionesEsIA u WHERE u.idClase = :idClase AND u.nombreClase = :nombreClase AND u.seccionFormulario in :seccion AND u.observacionCorregida = false ORDER BY u.seccionFormulario, u.id"),
        @NamedQuery(name = ObservacionesEsIA.LISTAR_POR_ID_CLASE_NO_CORREGIDAS_TODAS, query = "SELECT u FROM ObservacionesEsIA u WHERE u.idClase = :idClase and u.observacionCorregida = false and u.estado = true ")

        })

@AttributeOverrides({@AttributeOverride(name = "estado", column = @Column(name = "foci_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "foci_status = 'TRUE'")
public class ObservacionesEsIA extends EntidadBase {

    private static final long serialVersionUID = 5016494380218175574L;
    private static final String PAQUETE = "ec.gob.ambiente.rcoa.model.";
    public static final String LISTAR_POR_ID_CLASE_NOMBRE_CLASE_SECCION = PAQUETE
            + "ObservacionesEsIA.listaPorIdClaseNombreClaseSeccion";
    public static final String LISTAR_POR_ID_CLASE_NOMBRE_CLASE = PAQUETE
            + "ObservacionesEsIA.listaPorIdClaseNombreClase";
    public static final String LISTAR_POR_ID_CLASE_NOMBRE_CLASE_NO_CORREGIDAS = PAQUETE
            + "ObservacionesEsIA.listaPorIdClaseNombreClaseNoCorregidas";
    public static final String LISTAR_POR_ID_CLASE_NOMBRE_CLASE_USUARIO_NO_CORREGIDAS = PAQUETE
            + "ObservacionesEsIA.listaPorIdClaseNombreClaseUsuarioNoCorregidas";

    public static final String LISTAR_POR_ID_USUARIO = PAQUETE
            + "ObservacionesEsIA.listaPorIdUsuario";
    
    public static final String LISTAR_POR_ID_CLASE_SECCION_NO_CORREGIDAS = PAQUETE
            + "ObservacionesEsIA.listaPorIdClaseSeccionNoCorregidas";
    
    public static final String LISTAR_POR_ID_CLASE_NO_CORREGIDAS = PAQUETE
            + "ObservacionesEsIA.listaPorIdClaseNoCorregidas";

    public static final String LISTAR_POR_ID_CLASE_SECCION_ALL = PAQUETE
            + "ObservacionesEsIA.listaPorIdClaseSeccionAll";
    
    public static final String LISTAR_POR_ID_CLASE_NOMBRE_CLASE_SECCION_NO_CORREGIDAS = PAQUETE + "ObservacionesEsIA.listaPorIdClaseNombreClaseSeccionNoCorregidas";
    
    public static final String LISTAR_POR_ID_CLASE_NO_CORREGIDAS_TODAS = PAQUETE + "ObservacionesEsIA.listarPorIdClaseNoCorregidasTodas";
    
    @Getter
    @Setter
    @Id
    @SequenceGenerator(name = "OBSERVATION_STUDY_ID_GENERATOR", sequenceName = "form_comments_impact_foci_id_seq", schema = "coa_environmental_impact_study", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OBSERVATION_STUDY_ID_GENERATOR")
    @Column(name = "foci_id")
    private Integer id;
    @Getter
    @Setter
    @Column(name = "foci_field", length = 255)
    private String campo;
    @Getter
    @Setter
    @Column(name = "foci_description")
    private String descripcion;
    @Getter
    @Setter
    @Column(name = "foci_class_name", length = 255)
    private String nombreClase;
    @Getter
    @Setter
    @Column(name = "foci_id_class")
    private Integer idClase;
    @Getter
    @Setter
    @Column(name = "foci_section_form", length = 500)
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
    @Column(name = "foci_date")
    private Date fechaRegistro;
    @Getter
    @Setter
    @Transient
    private boolean disabled;
    @Getter
    @Setter
    @Column(name = "foci_observation_corrected")
    private boolean observacionCorregida;


    @Getter
    @Setter
    @Column(name = "taskId")
    private Long idTarea;


    @Getter
    @Setter
    @Column(name = "foci_answer", length = 500)
    private String respuesta;

}
