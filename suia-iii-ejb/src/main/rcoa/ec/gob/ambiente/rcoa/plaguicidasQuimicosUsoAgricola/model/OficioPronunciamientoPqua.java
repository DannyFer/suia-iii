package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model;

import java.io.Serializable;
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
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

@Entity
@Table(name="official_pronouncement", schema = "chemical_pesticides")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "ofpr_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "ofpr_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "ofpr_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "ofpr_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "ofpr_user_update")) })

@NamedQueries({
@NamedQuery(name="OficioPronunciamientoPqua.findAll", query="SELECT o FROM OficioPronunciamientoPqua o"),
@NamedQuery(name=OficioPronunciamientoPqua.GET_POR_PROYECTO_REVISION, query="SELECT o FROM OficioPronunciamientoPqua o where o.idProyecto = :idProyecto and o.numeroRevision = :numeroRevision and o.estado = true order by id desc"),
@NamedQuery(name=OficioPronunciamientoPqua.GET_POR_PROYECTO_ULTIMO_FIRMADO, query="SELECT o FROM OficioPronunciamientoPqua o where o.idProyecto = :idProyecto and o.estado = true and fechaFirma != null order by id desc"),
@NamedQuery(name=OficioPronunciamientoPqua.GET_POR_ID, query="SELECT o FROM OficioPronunciamientoPqua o where o.id = :idOficio and o.estado = true order by id desc")
})

public class OficioPronunciamientoPqua extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAQUETE = "ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.";
	
	public static final String GET_POR_PROYECTO_REVISION = PAQUETE + "OficioPronunciamientoPqua.getPorProyectoRevision";
	public static final String GET_POR_PROYECTO_ULTIMO_FIRMADO = PAQUETE + "OficioPronunciamientoPqua.getPorProyectoUltimoFirmado";
	public static final String GET_POR_ID = PAQUETE + "OficioPronunciamientoPqua.getPorId";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ofpr_id")
	private Integer id;

	@Getter
	@Setter
	@Column(insertable = false, updatable = false, name = "chpe_id")
	private Integer idProyecto;

	@Getter
	@Setter
	@Column(name="ofpr_official_pronouncement")
	private String numeroOficio;

	@Getter
	@Setter
	@Column(name="ofpr_date_official_pronouncement")
	private Date fechaOficio;

	@Getter
	@Setter
	@Column(name="ofpr_pronouncement_type")
	private Boolean esAprobacion;

	@Getter
	@Setter
	@Column(name="id_task")
	private Integer idTarea;

	@Getter
	@Setter
	@Column(name="ofpr_revision_number")
	private Integer numeroRevision;

	@Getter
	@Setter
	@Column(name="ofpr_signature_date")
	private Date fechaFirma;

	@ManyToOne
	@JoinColumn(name = "chpe_id")
	@Getter
	@Setter
	private ProyectoPlaguicidas proyectoPlaguicidas;


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

}