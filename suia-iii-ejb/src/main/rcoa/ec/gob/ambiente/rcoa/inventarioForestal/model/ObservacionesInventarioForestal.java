/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.rcoa.inventarioForestal.model;

import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
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
@Table(name="form_comments_inventory", schema = "coa_forest_inventory")

@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "fcin_status")),	
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "fcin_creator_user")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "fcin_creation_date")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "fcin_user_update")), 
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "fcin_date_update"))
})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "fcin_status = 'TRUE'")

@NamedQueries({
    @NamedQuery(name = ObservacionesInventarioForestal.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_SECCION, query = "SELECT u FROM ObservacionesInventarioForestal u WHERE u.idClase = :idClase AND u.nombreClase = :nombreClase AND u.seccionFormulario in :seccion ORDER BY u.seccionFormulario, u.id"),
    @NamedQuery(name = ObservacionesInventarioForestal.LISTAR_POR_ID_CLASE_NOMBRE_CLASE, query = "SELECT u FROM ObservacionesInventarioForestal u WHERE u.idClase = :idClase AND u.nombreClase = :nombreClase ORDER BY u.id"),
    @NamedQuery(name = ObservacionesInventarioForestal.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_NO_CORREGIDAS, query = "SELECT u FROM ObservacionesInventarioForestal u WHERE u.idClase = :idClase AND u.nombreClase = :nombreClase AND u.observacionCorregida = false ORDER BY u.seccionFormulario"),
    @NamedQuery(name = ObservacionesInventarioForestal.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_USUARIO_NO_CORREGIDAS, query = "SELECT u FROM ObservacionesInventarioForestal u WHERE u.idClase = :idClase AND u.nombreClase = :nombreClase AND u.usuario.id = :idUsuario AND u.observacionCorregida = false ORDER BY u.seccionFormulario"),
    @NamedQuery(name = ObservacionesInventarioForestal.LISTAR_POR_ID_USUARIO, query = "SELECT u FROM ObservacionesInventarioForestal u WHERE u.usuario.id = :idUsuario")})


public class ObservacionesInventarioForestal extends EntidadAuditable {

    private static final long serialVersionUID = 5016494380218175574L;
    private static final String PAQUETE = "ec.gob.ambiente.rcoa.inventarioForestal.model.";
    public static final String LISTAR_POR_ID_CLASE_NOMBRE_CLASE_SECCION = PAQUETE + "ObservacionesInventarioForestal.listaPorIdClaseNombreClaseSeccion";
    public static final String LISTAR_POR_ID_CLASE_NOMBRE_CLASE = PAQUETE + "ObservacionesInventarioForestal.listaPorIdClaseNombreClase";
    public static final String LISTAR_POR_ID_CLASE_NOMBRE_CLASE_NO_CORREGIDAS = PAQUETE + "ObservacionesInventarioForestal.listaPorIdClaseNombreClaseNoCorregidas";
    public static final String LISTAR_POR_ID_CLASE_NOMBRE_CLASE_USUARIO_NO_CORREGIDAS = PAQUETE + "ObservacionesInventarioForestal.listaPorIdClaseNombreClaseUsuarioNoCorregidas";

    public static final String LISTAR_POR_ID_USUARIO = PAQUETE
            + "ObservacionesInventarioForestal.listaPorIdUsuario";
    
    @Getter
    @Setter
    @Id
    @SequenceGenerator(name = "OBSERVATION_FORESTAL_ID_GENERATOR", sequenceName = "form_comments_inventory_fcin_id_seq", schema = "coa_forest_inventory", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OBSERVATION_FORESTAL_ID_GENERATOR")
    @Column(name = "fcin_id")
    private Integer id;
    
    @Getter
    @Setter
    @Column(name = "fcin_field", length = 255)
    private String campo;
    
    @Getter
    @Setter
    @Column(name = "fcin_description", length = 500)
    private String descripcion;
    
    @Getter
    @Setter
    @Column(name = "fcin_class_name", length = 255)
    private String nombreClase;
    
    @Getter
    @Setter
    @Column(name = "fcin_id_class")
    private Integer idClase;
    
    @Getter
    @Setter
    @Column(name = "fcin_section_form", length = 500)
    private String seccionFormulario;
    
    @Getter
    @Setter
    @Transient
    private int indice;
    
    @Getter
    @Setter
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @ManyToOne
    @ForeignKey(name = "fk_fcin_user")
    private Usuario usuario;
    
    @Getter
    @Setter
    @Column(insertable = false, updatable = false, name = "user_id")
    private Integer idUsuario;
    
    @Getter
    @Setter
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fcin_date")
    private Date fechaRegistro;
    
    @Getter
    @Setter
    @Transient
    private Boolean disabled;
    
    @Getter
    @Setter
    @Column(name = "fcin_observation_corrected")
    private boolean observacionCorregida;


    @Getter
    @Setter
    @Column(name = "taskid")
    private Long idTarea;


    @Getter
    @Setter
    @Column(name = "fcin_answer", length = 500)
    private String respuesta;

}
