package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model;

import java.util.Date;

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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;


@Entity
@Table(name="form_comments_pesticides", schema = "chemical_pesticides")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "fope_status")),	
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "fope_creator_user")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "fope_creation_date")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "fope_user_update")), 
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "fope_date_update"))
})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "fope_status = 'TRUE'")

@NamedQueries({
    @NamedQuery(name = ObservacionesActualizacionPqua.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_SECCION, query = "SELECT u FROM ObservacionesActualizacionPqua u WHERE u.idClase = :idClase AND u.nombreClase = :nombreClase AND u.seccionFormulario in :seccion ORDER BY u.seccionFormulario, u.id"),
    @NamedQuery(name = ObservacionesActualizacionPqua.LISTAR_POR_ID_CLASE_NOMBRE_CLASE, query = "SELECT u FROM ObservacionesActualizacionPqua u WHERE u.idClase = :idClase AND u.nombreClase = :nombreClase ORDER BY u.id"),
    @NamedQuery(name = ObservacionesActualizacionPqua.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_NO_CORREGIDAS, query = "SELECT u FROM ObservacionesActualizacionPqua u WHERE u.idClase = :idClase AND u.nombreClase = :nombreClase AND u.observacionCorregida = false ORDER BY u.seccionFormulario"),
    @NamedQuery(name = ObservacionesActualizacionPqua.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_USUARIO_NO_CORREGIDAS, query = "SELECT u FROM ObservacionesActualizacionPqua u WHERE u.idClase = :idClase AND u.nombreClase = :nombreClase AND u.usuario.id = :idUsuario AND u.observacionCorregida = false ORDER BY u.seccionFormulario"),
    @NamedQuery(name = ObservacionesActualizacionPqua.LISTAR_POR_ID_USUARIO, query = "SELECT u FROM ObservacionesActualizacionPqua u WHERE u.usuario.id = :idUsuario")})


public class ObservacionesActualizacionPqua extends EntidadAuditable {
	
	private static final long serialVersionUID = 1L;
	private static final String PAQUETE = "ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.";
	public static final String LISTAR_POR_ID_CLASE_NOMBRE_CLASE_SECCION = PAQUETE + "ObservacionesActualizacionPqua.listaPorIdClaseNombreClaseSeccion";
    public static final String LISTAR_POR_ID_CLASE_NOMBRE_CLASE = PAQUETE + "ObservacionesActualizacionPqua.listaPorIdClaseNombreClase";
    public static final String LISTAR_POR_ID_CLASE_NOMBRE_CLASE_NO_CORREGIDAS = PAQUETE + "ObservacionesActualizacionPqua.listaPorIdClaseNombreClaseNoCorregidas";
    public static final String LISTAR_POR_ID_CLASE_NOMBRE_CLASE_USUARIO_NO_CORREGIDAS = PAQUETE + "ObservacionesActualizacionPqua.listaPorIdClaseNombreClaseUsuarioNoCorregidas";
    public static final String LISTAR_POR_ID_USUARIO = PAQUETE + "ObservacionesActualizacionPqua.listaPorIdUsuario";
	
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "fope_id")
    private Integer id;
    
    @Getter
    @Setter
    @Column(name = "fope_field", length = 255)
    private String campo;
    
    @Getter
    @Setter
    @Column(name = "fope_description")
    private String descripcion;
    
    @Getter
    @Setter
    @Column(name = "fope_class_name", length = 255)
    private String nombreClase;
    
    @Getter
    @Setter
    @Column(name = "fope_id_class")
    private Integer idClase;
    
    @Getter
    @Setter                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               
    @Column(name = "fope_section_form", length = 500)
    private String seccionFormulario;
    
    @Getter
    @Setter
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @ManyToOne
    @ForeignKey(name = "fk_form_comments_user_id")
    private Usuario usuario;
    
    @Getter
    @Setter
    @Column(insertable = false, updatable = false, name = "user_id")
    private Integer idUsuario;
    
    @Getter
    @Setter
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fope_date")
    private Date fechaRegistro;
    
    @Getter
    @Setter
    @Column(name = "fope_observation_corrected")
    private boolean observacionCorregida;


    @Getter
    @Setter
    @Column(name = "taskid")
    private Long idTarea;


    @Getter
    @Setter
    @Column(name = "fope_answer", length = 500)
    private String respuesta;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name="tyob_id") 
    private TipoObservacion tipoObservacion = new TipoObservacion();

    @Getter
    @Setter
    @Transient
    private int indice;

    @Getter
    @Setter
    @Transient
    private Boolean disabled;

}