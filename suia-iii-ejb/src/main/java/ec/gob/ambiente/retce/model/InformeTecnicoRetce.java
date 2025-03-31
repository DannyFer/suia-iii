package ec.gob.ambiente.retce.model;

import java.io.Serializable;

import javax.persistence.*;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the technical_report_retce database table.
 * 
 */
@Entity
@Table(name="technical_report", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "trre_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "trre_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "trre_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "trre_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "trre_user_update")) })

@NamedQueries({@NamedQuery(name="InformeTecnicoRetce.findAll", query="SELECT i FROM InformeTecnicoRetce i"), 
	@NamedQuery(name = InformeTecnicoRetce.GET_POR_CODIGO_TRAMITE_TIPO, query = "SELECT i FROM InformeTecnicoRetce i WHERE i.codigoTramite = :codTramite and i.idTipoDocumento = :idTipoDocumento and i.estado=true order by 1 desc"),
	@NamedQuery(name = InformeTecnicoRetce.GET_POR_CODIGO_TRAMITE_TIPO_REVISION, query = "SELECT i FROM InformeTecnicoRetce i WHERE i.codigoTramite = :codTramite and i.idTipoDocumento = :idTipoDocumento and i.estado=true and i.numeroRevision = :numeroRevision order by 1 desc")})
public class InformeTecnicoRetce extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final String PAQUETE = "ec.gob.ambiente.retce.model.";
	public static final String GET_POR_CODIGO_TRAMITE_TIPO = PAQUETE + "InformeTecnicoRetce.get_por_codigo_tramite_tipo";
	public static final String GET_POR_CODIGO_TRAMITE_TIPO_REVISION = PAQUETE + "InformeTecnicoRetce.get_por_codigo_tramite_tipo_revision";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="trre_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name="trre_code_form")
	private String codigoTramite;
	
	@Getter
	@Setter
	@Column(name="doty_id")
	private Integer idTipoDocumento;
	
	@Getter
	@Setter
	@Column(name="trre_is_approval_report")
	private Boolean esReporteAprobacion;
	
	@Getter
	@Setter
	@Column(name="trre_report_number")
	private String numeroInforme;
	
	@Getter
	@Setter
	@Column(name="trre_report_date")
	private Date fechaInforme;

	@Getter
	@Setter
	@Column(name="trre_conclusions")
	private String conclusiones;

	@Getter
	@Setter
	@Column(name="trre_general_observations")
	private String observaciones;

	@Getter
	@Setter
	@Column(name="trre_recommendations")
	private String recomendaciones;
	
	@Getter
	@Setter
	@Column(name="trre_finalized")
	private Boolean finalizado;	
	
	@Getter
	@Setter
	@Column(name="trre_revision_number")
	private Integer numeroRevision;
	
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
	private byte[] archivoInforme;
	
	@Getter
	@Setter
	@Transient
	private String informePath;
	
	public InformeTecnicoRetce(){}
	
	public InformeTecnicoRetce(String codigoTramite,TipoDocumentoSistema tipoDocumentoSistema,Integer numeroRevision){
		this.codigoTramite=codigoTramite;
		this.idTipoDocumento=tipoDocumentoSistema.getIdTipoDocumento();
		this.numeroRevision=numeroRevision;
		this.setFinalizado(false);
	}

}