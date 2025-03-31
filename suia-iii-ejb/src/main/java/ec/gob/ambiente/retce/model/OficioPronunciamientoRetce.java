package ec.gob.ambiente.retce.model;

import java.io.Serializable;

import javax.persistence.*;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the pronouncement_job database table.
 * 
 */
@Entity
@Table(name="pronouncement_job", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "prjo_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "prjo_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "prjo_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "prjo_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "prjo_user_update")) })

@NamedQueries({@NamedQuery(name="OficioPronunciamientoRetce.findAll", query="SELECT o FROM OficioPronunciamientoRetce o"), 
	@NamedQuery(name = OficioPronunciamientoRetce.GET_POR_CODIGO_TRAMITE_TIPO, query = "SELECT i FROM OficioPronunciamientoRetce i WHERE i.codigoTramite = :codTramite and i.idTipoDocumento = :idTipoDocumento and i.estado=true order by 1 desc"),
	@NamedQuery(name = OficioPronunciamientoRetce.GET_POR_CODIGO_TRAMITE_TIPO_REVISION, query = "SELECT i FROM OficioPronunciamientoRetce i WHERE i.codigoTramite = :codTramite and i.idTipoDocumento = :idTipoDocumento and i.estado=true and i.numeroRevision = :numeroRevision order by 1 desc"),
	@NamedQuery(name = OficioPronunciamientoRetce.GET_SUBSANADOS_POR_CODIGO_TRAMITE, query = "SELECT i FROM OficioPronunciamientoRetce i WHERE i.codigoTramite = :codTramite and fechaEnvioCorrecciones is not null and i.estado=true order by id"),
	@NamedQuery(name = OficioPronunciamientoRetce.GET_POR_ID_INFORME, query = "SELECT i FROM OficioPronunciamientoRetce i WHERE i.idInformeTecnico = :idInforme and i.estado=true order by id")})
public class OficioPronunciamientoRetce extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String GET_POR_CODIGO_TRAMITE_TIPO = "ec.gob.ambiente.retce.model.OficioPronunciamientoRetce.get_por_codigo_tramite_tipo";
	public static final String GET_POR_CODIGO_TRAMITE_TIPO_REVISION = "ec.gob.ambiente.retce.model.OficioPronunciamientoRetce.get_por_codigo_tramite_tipo_revision";
	public static final String GET_SUBSANADOS_POR_CODIGO_TRAMITE = "ec.gob.ambiente.retce.model.OficioPronunciamientoRetce.get_subsanados_por_codigo_tramite";
	public static final String GET_POR_ID_INFORME = "ec.gob.ambiente.retce.model.OficioPronunciamientoRetce.get_por_id_informe";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="prjo_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name="doty_id")
	private Integer idTipoDocumento;

	@Getter
	@Setter
	@Column(name="id_technical_report")
	private Integer idInformeTecnico;
	
	@Getter
	@Setter
	@Column(name="prjo_code_form")
	private String codigoTramite;
	
	@Getter
	@Setter
	@Column(name="prjo_job_number")
	private String numeroOficio;

	@Getter
	@Setter
	@Column(name="prjo_job_date")
	private Date fechaOficio;

	@Getter
	@Setter
	@Column(name="prjo_subject")
	private String asunto;
	
	@Getter
	@Setter
	@Column(name="prjo_pronouncement")
	private String pronunciamiento;
	
	@Getter
	@Setter
	@Column(name="prjo_revision_number")
	private Integer numeroRevision;
	
	@Getter
	@Setter
	@Column(name="prjo_corrections_send_date")
	private Date fechaEnvioCorrecciones;
	
	@Getter
	@Setter
	@Transient
	private String nombreFichero;

	@Getter
	@Setter
	@Transient
	private String nombreReporte;
	
	@Getter
	@Setter
	@Transient
	private byte[] archivoOficio;
	
	@Getter
	@Setter
	@Transient
	private String oficioPath;
	
	public OficioPronunciamientoRetce(){}
	public OficioPronunciamientoRetce(String codigoTramite,TipoDocumentoSistema tipoDocumentoSistema,Integer numeroRevision){
		this.codigoTramite=codigoTramite;
		this.idTipoDocumento=tipoDocumentoSistema.getIdTipoDocumento();
		this.numeroRevision=numeroRevision;
	}
}