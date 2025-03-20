package ec.gob.ambiente.retce.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;

/**
 * The persistent class for the hazardous_waste_generator_retce database table.
 * 
 */
@Entity
@Table(name = "hazardous_waste_generator_retce", schema = "retce")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "hwgr_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "hwgr_date_create")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "hwgr_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "hwgr_user_create")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "hwgr_user_update")) })
@NamedQueries({
		@NamedQuery(name = "GeneradorDesechosPeligrososRetce.findAll", query = "SELECT g FROM GeneradorDesechosPeligrososRetce g"),
		@NamedQuery(name = GeneradorDesechosPeligrososRetce.GET_GENERADOR_POR_RGD_ANIO, query = "SELECT t FROM GeneradorDesechosPeligrososRetce t WHERE t.idGeneradorDesechosPeligrosos = :idRgd and t.anioDeclaracion = :anio and estado = true and idRegistroOriginal is null ORDER BY t.id desc"),
		@NamedQuery(name = GeneradorDesechosPeligrososRetce.GET_GENERADOR_POR_INFORMACION_PROYECTO, query = "SELECT t FROM GeneradorDesechosPeligrososRetce t WHERE t.informacionProyecto.id = :idProyecto and estado = true and idRegistroOriginal is null ORDER BY t.id desc"),
		@NamedQuery(name = GeneradorDesechosPeligrososRetce.GET_GENERADOR_POR_ID, query = "SELECT t FROM GeneradorDesechosPeligrososRetce t WHERE t.id = :idRgdRetce and estado = true and idRegistroOriginal is null ORDER BY t.id desc"),
		@NamedQuery(name = GeneradorDesechosPeligrososRetce.GET_GENERADOR_POR_CODIGO, query = "SELECT t FROM GeneradorDesechosPeligrososRetce t WHERE t.codigoGenerador = :codigo and estado = true and idRegistroOriginal is null ORDER BY t.id desc"),
		@NamedQuery(name = GeneradorDesechosPeligrososRetce.GET_HISTORIAL_GENERADOR, query = "SELECT t FROM GeneradorDesechosPeligrososRetce t WHERE t.idRegistroOriginal = :idRgdRetce and estado = true and historial = true and numeroObservacion = :nroObservacion ORDER BY t.id desc"),
		@NamedQuery(name = GeneradorDesechosPeligrososRetce.GET_HISTORIAL_GENERADOR_POR_ID, query = "SELECT t FROM GeneradorDesechosPeligrososRetce t WHERE t.idRegistroOriginal = :idRgdRetce and estado = true and historial = true ORDER BY t.id desc") })
public class GeneradorDesechosPeligrososRetce extends EntidadAuditable
		implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String GET_GENERADOR_POR_RGD_ANIO = "ec.gob.ambiente.retce.model.GeneradorDesechosPeligrososRetce.getGeneradorPorRgdAnio";
	public static final String GET_GENERADOR_POR_INFORMACION_PROYECTO = "ec.gob.ambiente.retce.model.GeneradorDesechosPeligrososRetce.getGeneradorPorInformacionProyecto";
	public static final String GET_GENERADOR_POR_ID = "ec.gob.ambiente.retce.model.GeneradorDesechosPeligrososRetce.getGeneradorPorId";
	public static final String GET_GENERADOR_POR_CODIGO = "ec.gob.ambiente.retce.model.GeneradorDesechosPeligrososRetce.getGeneradorPorCodigo";
	public static final String GET_HISTORIAL_GENERADOR = "ec.gob.ambiente.retce.model.GeneradorDesechosPeligrososRetce.getHistorialGenerador";
	public static final String GET_HISTORIAL_GENERADOR_POR_ID = "ec.gob.ambiente.retce.model.GeneradorDesechosPeligrososRetce.getHistorialGeneradorPorId";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "hwgr_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "hwgr_code")
	private String codigoGenerador;
	
	@Getter
	@Setter
	@Column(name = "hwgr_authorization_number")
	private String numeroAutorizacion;

	@Getter
	@Setter
	@Column(name = "hwgr_declaration_year")
	private Integer anioDeclaracion;

	@Getter
	@Setter
	@Column(name = "hwgr_perform_export")
	private Boolean realizaExportacion;

	@Getter
	@Setter
	@Column(name = "hwgr_perform_self_management")
	private Boolean realizaAutogestion;
	
	@Getter
	@Setter
	@Column(name = "hwgr_registration_finalized")
	private Boolean registroFinalizado;
	
	@Getter
	@Setter
	@Column(name = "hwgr_date_processing")
	private Date fechaTramite;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "proj_id")
	private InformacionProyecto informacionProyecto;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "resp_id")
	private TecnicoResponsable tecnicoResponsable;
	
	@Getter
	@Setter
	@Column(name = "hwge_id")
	private Integer idGeneradorDesechosPeligrosos;
	
	@Getter
	@Setter
	@Column(name = "ware_id")
	private Integer idGeneradorDesechosPeligrososRcoa;
	
	@Getter
	@Setter
	@Column(name = "hwgr_generator_code_request")
	private String codigoGeneradorDesechosPeligrosos;
	
	@Getter
	@Setter
	@Column(name = "hwgr_generator_approval_date")
	private Date fechaAprobacionRgd;
	
	@Getter
	@Setter
	@Column(name = "area_id")
	private Integer idArea;
	
	@Getter
	@Setter
	@Column(name="hwgr_history")
	private Boolean historial;
    
    @Getter
	@Setter
	@Column(name="hwgr_original_record_id")
	private Integer idRegistroOriginal;
    
    @Getter
	@Setter
	@Column(name="hwgr_observation_number")
	private Integer numeroObservacion;
    
    @Getter
	@Setter
	@Column(name = "hwgr_finalized")
	private Boolean tramiteFinalizado;

}
