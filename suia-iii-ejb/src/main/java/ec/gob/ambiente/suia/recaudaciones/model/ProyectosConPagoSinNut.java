package ec.gob.ambiente.suia.recaudaciones.model;

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

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name = "projects_not_nut", schema = "payments")
@NamedQueries({
		@NamedQuery(name = ProyectosConPagoSinNut.LISTAR_TODOS, query = "SELECT c FROM ProyectosConPagoSinNut c WHERE c.estado= true"),
		@NamedQuery(name = ProyectosConPagoSinNut.LISTAR_SOLICITUD_POR_TRAMITE_POR_USUARIO, query = "SELECT c FROM ProyectosConPagoSinNut c WHERE c.estado= true and c.processId = :processinstanceid and c.usuario.id = :usuarioId  and c.numeroTramiteUsado is false "),
		@NamedQuery(name = ProyectosConPagoSinNut.LISTAR_SOLICITUD_POR_TRAMITE_POR_USUARIO_PAGO_LIBERADO, query = "SELECT c FROM ProyectosConPagoSinNut c WHERE c.estado= true and c.numeroTramiteUsado is false and c.processId = :processinstanceid and c.usuario.id = :usuarioId and (c.codigoProyecto = :codigoUnico or tipoProceso = 1)"),
		@NamedQuery(name = ProyectosConPagoSinNut.LISTAR_SOLICITUD_POR_NUMEROTRANSACCION, query = "SELECT c FROM ProyectosConPagoSinNut c WHERE c.estado= true AND C.numeroTramite = :numeroTransaccion and c.numeroTramiteUsado is false  "),
		@NamedQuery(name = ProyectosConPagoSinNut.LISTAR_SOLICITUD_POR_CODIGOPROYECTO, query = "SELECT c FROM ProyectosConPagoSinNut c WHERE c.estado= true AND C.codigoProyecto = :codigoProyecto and c.numeroTramite is not null and c.numeroTramiteUsado is false and c.usuario.id = :usuarioId "),
		@NamedQuery(name = ProyectosConPagoSinNut.LISTAR_SOLICITUD_POR_TRAMITE_POR_USUARIO_POR_NUMERO, query = "SELECT c FROM ProyectosConPagoSinNut c WHERE c.estado= true and c.codigoProyecto = :codigoUnico AND C.numeroTramite = :numeroTransaccion and c.numeroTramiteUsado is false and c.processId = :processinstanceid and c.usuario.id = :usuarioId ")})
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "prnn_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "prnn_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "prnn_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "prnn_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "prnn_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prnn_status = 'TRUE'")
public class ProyectosConPagoSinNut extends EntidadAuditable {

	private static final long serialVersionUID = -1756612644167548934L;

	public static final String LISTAR_TODOS = "ec.com.magmasoft.business.domain.ProyectosConPagoSinNut.listarTodos";
	public static final String LISTAR_SOLICITUD_POR_TRAMITE_POR_USUARIO = "ec.com.magmasoft.business.domain.ProyectosConPagoSinNut.listarSolicitudPorTramite";
	public static final String LISTAR_SOLICITUD_POR_TRAMITE_POR_USUARIO_PAGO_LIBERADO = "ec.com.magmasoft.business.domain.ProyectosConPagoSinNut.listarSolicitudPorTramitePagoLiberado";
	public static final String LISTAR_SOLICITUD_POR_TRAMITE_POR_USUARIO_POR_NUMERO = "ec.com.magmasoft.business.domain.ProyectosConPagoSinNut.listarSolicitudPorTramitePago";
	public static final String LISTAR_SOLICITUD_POR_CODIGOPROYECTO = "ec.com.magmasoft.business.domain.ProyectosConPagoSinNut.listarSolicitudPorProyecto";
	public static final String LISTAR_SOLICITUD_POR_NUMEROTRANSACCION = "ec.com.magmasoft.business.domain.ProyectosConPagoSinNut.listarSolicitudPorNumero";

	@Getter
	@Setter
	@Id
	@Column(name = "prnn_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "prnn_project")
	private String codigoProyecto;

	@Getter
	@Setter
	@Column(name = "processinstanceid")
	private Long processId;

	@Getter
	@Setter
	@Column(name = "tramit_number")
	private String numeroTramite;

	@Getter
	@Setter
	@Column(name = "prnn_status_tramit_number")
	private boolean numeroTramiteUsado;

	@Getter
	@Setter
	@Temporal(TemporalType.DATE)
	@Column(name = "prnn_tramit_number_date")
	private Date fechaNumeroTramiteUsado;

	@Getter
	@Setter
	@Column(name = "prnn_project_rgd")
	private String codigoProyectoRGD; 

	@Getter
	@Setter
	@Column(name = "prnn_type_permission")
	private Integer tipoProceso;

	@Getter
    @Setter
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @ManyToOne
    @ForeignKey(name = "fk_users_user_id_projects_not_nut_user_id")
    private Usuario usuario;
	
	@Getter
	@Setter
	@Column(name = "prnn_observation_bd")
	private String observacionBdd;

}
