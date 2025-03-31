package ec.gob.ambiente.rcoa.model;

import java.io.Serializable;

import javax.persistence.*;

import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

import java.sql.Timestamp;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the pronouncement_office database table.
 * 
 */
@Entity
@Table(name="pronouncement_office", schema="coa_environmental_record")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "prof_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "prof_creation_date")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "prof_update_date")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "prof_creator_user")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "prof_update_user")) })
@NamedQuery(name="OficioPronunciamientoPPC.findAll", query="SELECT o FROM OficioPronunciamientoPPC o")
public class OficioPronunciamientoPPC extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="prof_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="area_id")
	private Area area;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="enre_id")
	private RegistroAmbientalRcoa registroAmbiental;

	@Getter
	@Setter
	@Column(name="prof_affair")
	private String asunto;

	@Getter
	@Setter
	@Column(name="prof_background")
	private String antecedentes;

	@Getter
	@Setter
	@Column(name="prof_code_document")
	private String codigoDocumento;

	@Getter
	@Setter
	@Column(name="prof_date_elaboration")
	private Date fechaElaboracion;

	@Getter
	@Setter
	@Column(name="prof_pronouncement")
	private String pronunciamiento;

	@Getter
	@Setter
	@Column(name="prof_task_id")
	private Integer tareaId;

	@Getter
	@Setter
	@Column(name="prof_type")
	private Integer tipo; //1.- Aprobacion, 2.- Archivo
	
	@Getter
	@Setter
	@Transient
	private String url;
	
	@Getter
	@Setter
	@Transient
	private byte[] contenido;
	
	@Getter
	@Setter
	@Transient
	private String nombreReporte;
	
	@Getter
	@Setter
	@Transient
	private Usuario director;

	public OficioPronunciamientoPPC() {
	}

}