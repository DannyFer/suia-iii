package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Where;

import javax.persistence.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

/**
 *
 * <b> Entity que representa la tabla usuarios. </b>
 *
 * @author pganan
 * @version Revision: 1.0
 *          <p>
 *          [Autor: pganan, Fecha: 22/12/2014]
 *          </p>
 */
@NamedQueries({
        @NamedQuery(name = Usuario.FIND_BY_ROLE, query = "SELECT u FROM Usuario u, RolUsuario ru INNER JOIN ru.usuario us WHERE ru.rol.nombre like :rol GROUP BY u"),
        @NamedQuery(name = Usuario.FIND_BY_USER, query = "SELECT u FROM Usuario u WHERE lower(u.nombre) = lower(:name)"),
        @NamedQuery(name = Usuario.FIND_BY_ID, query = "SELECT u FROM Usuario u WHERE u.id = :id"),
        @NamedQuery(name = Usuario.FIND_BY_LOGIN_PASSWORD_STATE, query = "SELECT u FROM Usuario u WHERE u.nombre = :login AND u.password = :password AND u.estado = :estado") })
@Entity
@Table(name = "users", schema = "public")
@AttributeOverrides({
        @AttributeOverride(name = "estado", column = @Column(name = "user_status")),
        @AttributeOverride(name = "fechaCreacion", column = @Column(name = "user_creation_date")),
        @AttributeOverride(name = "fechaModificacion", column = @Column(name = "user_date_update")),
        @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "user_creator_user")),
        @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "user_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "user_status = 'TRUE'")
public class Usuario extends EntidadAuditable {

    private static final long serialVersionUID = -8102508533188993515L;
    private static final Logger LOG = Logger.getLogger(Usuario.class);

    public static final String FIND_BY_ROLE = "ec.com.magmasoft.business.domain.Usuario.findByRole";
    public static final String FIND_BY_USER = "ec.com.magmasoft.business.domain.Usuario.findByUser";
    public static final String FIND_BY_ID = "ec.com.magmasoft.business.domain.Usuario.findById";
    public static final String FIND_BY_LOGIN_PASSWORD_STATE = "ec.com.magmasoft.business.domain.Usuario.findByLoginPasswordState";

    public static final String FIND_BY_ROL_AND_AREA = "select distinct u.user_name from users  u "
    		+ " inner join areas_users au on u.user_id = au.user_id "
            + " inner join areas a on a.area_id=au.area_id "
            + " inner join suia_iii.roles_users ru on ru.user_id=u.user_id and u.user_status=true and ru.rous_status=true"
            + " inner join suia_iii.roles r on r.role_id=ru.role_id and r.role_status=true"
            + " left join areas ap on ap.area_id = a.area_parent_id "
            + " where r.role_name=:rol and (a.area_name like :area or ap.area_name like :area) "
            + " and au.arus_status = true";
    
    @Getter
    @Setter
    @Id
    @Column(name = "user_id")
    @SequenceGenerator(name = "USERS_GENERATOR", initialValue = 1, sequenceName = "seq_user_id", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USERS_GENERATOR")
    private Integer id;

    @Getter
    @Setter
    @Column(name = "user_justification_access")
    private String justificacion;

    @Getter
    @Setter
    @Column(name = "user_name")
    private String nombre;

    @Getter
    @Setter
    @Column(name = "user_password")
    private String password;

    @Getter
    @Setter
    @Column(name = "user_temp_password")
    private String tempPassword;

    @Getter
    @Setter
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "user_date_expiration")
    private Date fechaExpiracionUsuario;

    @Getter
    @Setter
    @Column(name = "user_data_complete")
    private Boolean datosCompletos;

    @Getter
    @Setter
    @Column(name = "user_subrogante")
    private Boolean subrogante;

    @Getter
    @Setter
    @Column(name = "user_docu_id")
    private String docuId;

    @Getter
    @Setter
    @Column(name = "user_observations")
    private String observaciones;

    @Getter
    @Setter
    @Column(name = "user_token")
    private Boolean token;

    @Getter
    @Setter
    @Column(name = "user_pin")
    private String pin;

    @Getter
    @Setter
    @Column(name = "user_functionary")
    private Boolean funcionario;

    @Getter
    @Setter
    @Column(name = "user_central_functionary")
    private Boolean plantaCentral;

    @Getter
    @Setter
    @Column(name = "user_edif_id")
    private Integer idEdificio;

    @Getter
    @Setter
    @OneToMany(mappedBy = "usuario")
    @LazyCollection(LazyCollectionOption.FALSE)
    //@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "rous_status = 'TRUE'")
    @Where(clause = "rous_status = 'true'")
    private List<RolUsuario> rolUsuarios;

    @Getter
    @Setter
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "peop_id")
    @ForeignKey(name = "fk_usersuser_id_peoplepeop_id")
    private Persona persona;

    @Getter
    @Setter
    @Column(name = "user_is_area_boss")
    private Boolean esResponsableArea;
    
    
    @Getter
    @Setter
    @Column(name = "user_renew_user_date")
    private Date fechaRenovarPassusuario;
    
    @Getter
    @Setter
    @Column(name = "user_code_capcha")
    private String usuarioCodigoCapcha;           

    @Getter
    @Setter
    @Column(name = "user_status_capcha")
    private Boolean estadoCodigoCapcha;  
    
    
    @Getter
    @Setter
    @Column(name = "user_date_expiration_link")
    private Date fechaLinkExpiracion;
    
    @Getter
    @Setter
    @Column(name = "user_active_as_facilitator")
    private Boolean esFacilitador;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_immediate_superior")
    @ForeignKey(name = "fk_users_user_immediate_superior_users_user_id")
    private Usuario jefeInmediato;

    @Getter
    @Setter
    @Column(name = "user_work_performance_ratio")
    private Double coeficienteRendimiento;

    @Getter
    @Setter
    @Column(name = "user_number_sessions")
    private Integer numeroSesiones;
    
    @Transient
    @Getter
    @Setter
    private Integer pesoTotalTareas;

    @Transient
    @Getter
    @Setter
    private Integer numeroTramites;

    @Transient
    @Getter
    @Setter
    private double carga;

    @Transient
    @Getter
    @Setter
    private String nombrePersona;

    @Transient
    @Getter
    @Setter
    private String documentoIdentidad;

    @Transient
    @Getter
    @Setter
    private Boolean available = true;
    
    @Getter
	@Setter
	@OneToMany(mappedBy = "idUsuario")
	@LazyCollection(LazyCollectionOption.FALSE)
    @Where(clause = "arus_status = 'true'")
	private List<AreaUsuario> listaAreaUsuario;


    @Transient
    @Setter
    @Getter
    private Boolean selectable;

    public String getPasswordBase64() {
        return toBase64(getPassword());
    }

    public String toBase64(String string) {
        return new String(Base64.encodeBase64(string.getBytes()));
    }

    public String getPasswordSha1() {
        return toSha1(getPassword());
    }

    public String toSha1(String string) {
        try {
            byte[] buffer = string.getBytes();
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(buffer);
            byte[] digest = md.digest();
            String valorHash = "";
            for (byte aux : digest) {
                int b = aux & 0xff;
                if (Integer.toHexString(b).length() == 1)
                    valorHash += "0";
                valorHash += Integer.toHexString(b);
            }
            return valorHash;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String getPasswordSha1Base64() {
        return toBase64(getPassword());
        // return toBase64(toSha1(getPassword()));
    }

    public static boolean isUserInRole(Usuario usuario, String roleName) {
        try {
            List<RolUsuario> roles = usuario.getRolUsuarios();
            if (roles != null) {
                for (RolUsuario rolUsuario : roles) {
                    if (rolUsuario.getEstado() && rolUsuario.getRol().getNombre()
                            .equalsIgnoreCase(roleName))
                        return true;
                }
            }
        } catch (Exception e) {
            LOG.error("Error al recuperar el rol", e);
        }

        return false;
    }

    public Boolean isSelectable() {
        return selectable;
    }

    @Override
    public String toString() {
        return getPersona().getNombre();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Usuario)) {
            return false;
        }
        Usuario other = (Usuario) obj;
        if (((this.id == null) && (other.id != null)) || ((this.id != null) && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
    
    public Usuario (){
    	
    }
    
    public Usuario(Object[] array, Persona persona){
    	
    	this.id= (Integer)array[0];
    	this.nombre = (String)array[1];
    	this.persona = persona;
    	this.pin = (String)array[3];
    }
    
}