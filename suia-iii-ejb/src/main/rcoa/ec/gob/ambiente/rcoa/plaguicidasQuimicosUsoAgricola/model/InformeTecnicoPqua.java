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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

@Entity
@Table(name="technical_report", schema = "chemical_pesticides")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "tere_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "tere_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "tere_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "tere_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "tere_user_update")) })

@NamedQueries({
@NamedQuery(name="InformeTecnicoPqua.findAll", query="SELECT i FROM InformeTecnicoPqua i"),
@NamedQuery(name=InformeTecnicoPqua.GET_POR_PROYECTO_REVISION, query="SELECT i FROM InformeTecnicoPqua i where i.idProyecto = :idProyecto and i.numeroRevision = :numeroRevision and i.estado = true order by id desc")
})

public class InformeTecnicoPqua extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAQUETE = "ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.";
	
	public static final String GET_POR_PROYECTO_REVISION = PAQUETE + "InformeTecnicoPqua.getPorProyectoRevision";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="tere_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="chpe_id")
	private Integer idProyecto;

	@Getter
	@Setter
	@Column(name="tere_report_number")
	private String numeroInforme;

	@Getter
	@Setter
	@Column(name="tere_date_report_name")
	private Date fechaInforme;

	@Getter
	@Setter
	@Column(name="tere_pronouncement_type")
	private Boolean esAprobacion;

	@Getter
	@Setter
	@Column(name="id_task")
	private Integer idTarea;

	@Getter
	@Setter
	@Column(name="tere_revision_number")
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

}