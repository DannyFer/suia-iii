package ec.gob.ambiente.rcoa.model;

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


/**
 * The persistent class for the project_prohibition_pronouncement database table.
 * 
 */
@Entity
@Table(name="pronouncement_not_rectifiable_observation", schema = "coa_mae")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "pnro_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "pnro_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "pnro_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "pnro_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "pnro_user_update")) })


@NamedQueries({
@NamedQuery(name="PronunciamientoObservacionesNoSubsanables.findAll", query="SELECT p FROM PronunciamientoObservacionesNoSubsanables p"),
@NamedQuery(name=PronunciamientoObservacionesNoSubsanables.GET_POR_PROYECTO, query="SELECT p FROM PronunciamientoObservacionesNoSubsanables p  where p.idProyecto = :idProyecto and p.estado = true order by id desc") })

public class PronunciamientoObservacionesNoSubsanables extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final String PAQUETE = "ec.gob.ambiente.rcoa.model.";

	public static final String GET_POR_PROYECTO = PAQUETE + "PronunciamientoObservacionesNoSubsanables.getPorProyecto";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="pnro_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="prco_id")
	private Integer idProyecto;

	@Getter
	@Setter
	@Column(name="pnro_trade_code")
	private String codigo;

	@Getter
	@Setter
	@Column(name="pnro_trade_date")
	private Date fechaOficio;

	@Getter
	@Setter
	@Column(name="pnro_pronouncement")
	private String pronunciamiento;
	
	@Getter
	@Setter
	@Column(name="pnro_analysis_start_date")
	private Date fechaInicioAnalisisTramite;
	
	@Getter
	@Setter
	@Transient
	private String nombreOficio;
	
	@Getter
	@Setter
	@Transient
	private String oficioPath;
	
	@Getter
	@Setter
	@Transient
	private byte[] archivoOficio;

}