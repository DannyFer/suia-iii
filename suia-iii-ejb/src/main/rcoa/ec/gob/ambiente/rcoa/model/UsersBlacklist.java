package ec.gob.ambiente.rcoa.model;

import java.io.Serializable;
import javax.persistence.*;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

import java.sql.Timestamp;


/**
 * The persistent class for the users_blacklist database table.
 * 
 */
@Entity
@Table(name="users_blacklist", schema = "public")
@NamedQueries({
	@NamedQuery(name = UsersBlacklist.findAll, query="SELECT u FROM UsersBlacklist u"),
	@NamedQuery(name = UsersBlacklist.LISTAR_POR_CED, query="SELECT u FROM UsersBlacklist u where u.usblIdentification =:ci and u.estado= true")})
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "usbl_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "usbl_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "usbl_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "usbl_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "usbl_user_update"))})

public class UsersBlacklist extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final String PAQUETE_CLASE = "ec.gob.ambiente.rcoa.model.UsersBlacklist";
	public static final String findAll = PAQUETE_CLASE + "listarTodos";
	public static final String LISTAR_POR_CED = PAQUETE_CLASE + "listarPorTramite";	

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="usbl_id")
	private Integer usblId;

	//@Column(name="usbl_creation_date")
	//private Timestamp usblCreationDate;

	//@Column(name="usbl_creator_user")
	//private String usblCreatorUser;

	@Column(name="usbl_date_reactivation")
	private Timestamp usblDateReactivation;

	//@Column(name="usbl_date_update")
	//private Timestamp usblDateUpdate;

	@Column(name="usbl_identification")
	private String usblIdentification;

	@Column(name="usbl_observation_bd")
	private String usblObservationBd;

	@Column(name="usbl_reason")
	private String usblReason;

	//@Column(name="usbl_status")
	//private Boolean usblStatus;

	@Column(name="usbl_usbl_reason_reactivation")
	private String usblUsblReasonReactivation;

	//@Column(name="usbl_user_update")
	//private String usblUserUpdate;

	@Column(name="user_id")
	private Integer userId;

	public UsersBlacklist() {
	}

	public Integer getUsblId() {
		return this.usblId;
	}

	public void setUsblId(Integer usblId) {
		this.usblId = usblId;
	}


	public Timestamp getUsblDateReactivation() {
		return this.usblDateReactivation;
	}

	public void setUsblDateReactivation(Timestamp usblDateReactivation) {
		this.usblDateReactivation = usblDateReactivation;
	}

	public String getUsblIdentification() {
		return this.usblIdentification;
	}

	public void setUsblIdentification(String usblIdentification) {
		this.usblIdentification = usblIdentification;
	}

	public String getUsblObservationBd() {
		return this.usblObservationBd;
	}

	public void setUsblObservationBd(String usblObservationBd) {
		this.usblObservationBd = usblObservationBd;
	}

	public String getUsblReason() {
		return this.usblReason;
	}

	public void setUsblReason(String usblReason) {
		this.usblReason = usblReason;
	}


	public String getUsblUsblReasonReactivation() {
		return this.usblUsblReasonReactivation;
	}

	public void setUsblUsblReasonReactivation(String usblUsblReasonReactivation) {
		this.usblUsblReasonReactivation = usblUsblReasonReactivation;
	}

	public Integer getUserId() {
		return this.userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setId(Integer id) {
		// TODO Auto-generated method stub
		
	}

}