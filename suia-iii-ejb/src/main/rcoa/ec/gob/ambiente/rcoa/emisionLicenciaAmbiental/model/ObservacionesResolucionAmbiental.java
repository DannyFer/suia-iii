package ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.model;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name="form_comments_environment_resolution", schema = "coa_emission_environmental_resolution")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "fcer_status")),	
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "fcer_creator_user")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "fcer_creation_date")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "fcer_user_update")), 
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "fcer_date_update"))
})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "fcer_status = 'TRUE'")

@NamedQueries({
    @NamedQuery(name = ObservacionesResolucionAmbiental.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_SECCION, query = "SELECT u FROM ObservacionesResolucionAmbiental u WHERE u.idClase = :idClase AND u.nombreClase = :nombreClase AND u.seccionFormulario in :seccion ORDER BY u.seccionFormulario, u.id"),
    @NamedQuery(name = ObservacionesResolucionAmbiental.LISTAR_POR_ID_CLASE_NOMBRE_CLASE, query = "SELECT u FROM ObservacionesResolucionAmbiental u WHERE u.idClase = :idClase AND u.nombreClase = :nombreClase ORDER BY u.id"),
    @NamedQuery(name = ObservacionesResolucionAmbiental.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_NO_CORREGIDAS, query = "SELECT u FROM ObservacionesResolucionAmbiental u WHERE u.idClase = :idClase AND u.nombreClase = :nombreClase AND u.observacionCorregida = false ORDER BY u.seccionFormulario"),
    @NamedQuery(name = ObservacionesResolucionAmbiental.LISTAR_POR_ID_CLASE_NOMBRE_CLASE_USUARIO_NO_CORREGIDAS, query = "SELECT u FROM ObservacionesResolucionAmbiental u WHERE u.idClase = :idClase AND u.nombreClase = :nombreClase AND u.usuario.id = :idUsuario AND u.observacionCorregida = false ORDER BY u.seccionFormulario"),
    @NamedQuery(name = ObservacionesResolucionAmbiental.LISTAR_POR_ID_USUARIO, query = "SELECT u FROM ObservacionesResolucionAmbiental u WHERE u.usuario.id = :idUsuario")})


public class ObservacionesResolucionAmbiental extends EntidadAuditable {
	
	private static final long serialVersionUID = 1L;
	private static final String PAQUETE = "ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.model.";
	public static final String LISTAR_POR_ID_CLASE_NOMBRE_CLASE_SECCION = PAQUETE + "ObservacionesResolucionAmbiental.listaPorIdClaseNombreClaseSeccion";
    public static final String LISTAR_POR_ID_CLASE_NOMBRE_CLASE = PAQUETE + "ObservacionesResolucionAmbiental.listaPorIdClaseNombreClase";
    public static final String LISTAR_POR_ID_CLASE_NOMBRE_CLASE_NO_CORREGIDAS = PAQUETE + "ObservacionesResolucionAmbiental.listaPorIdClaseNombreClaseNoCorregidas";
    public static final String LISTAR_POR_ID_CLASE_NOMBRE_CLASE_USUARIO_NO_CORREGIDAS = PAQUETE + "ObservacionesResolucionAmbiental.listaPorIdClaseNombreClaseUsuarioNoCorregidas";
    public static final String LISTAR_POR_ID_USUARIO = PAQUETE + "ObservacionesResolucionAmbiental.listaPorIdUsuario";
	
    @Getter
    @Setter
    @Id
    @SequenceGenerator(name = "OBSERVATION_EMISION_ID_GENERATOR", sequenceName = "form_comments_environment_resolution_fcer_id_seq", schema = "coa_emission_environmental_resolution", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OBSERVATION_EMISION_ID_GENERATOR")
    @Column(name = "fcer_id")
    private Integer id;
    
    @Getter
    @Setter
    @Column(name = "fcer_field", length = 255)
    private String campo;
    
    @Getter
    @Setter
    @Column(name = "fcer_description")
    private String descripcion;
    
    @Getter
    @Setter
    @Column(name = "fcer_class_name", length = 255)
    private String nombreClase;
    
    @Getter
    @Setter
    @Column(name = "fcer_id_class")
    private Integer idClase;
    
    @Getter
    @Setter                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               
    @Column(name = "fcer_section_form", length = 500)
    private String seccionFormulario;
    
    @Getter
    @Setter
    @Transient
    private int indice;
    
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
    @Column(name = "fcer_date")
    private Date fechaRegistro;
    
    @Getter
    @Setter
    @Transient
    private Boolean disabled;
    
    @Getter
    @Setter
    @Column(name = "fcer_observation_corrected")
    private boolean observacionCorregida;


    @Getter
    @Setter
    @Column(name = "task_id")
    private Long idTarea;


    @Getter
    @Setter
    @Column(name = "fcer_answer", length = 500)
    private String respuesta;

}