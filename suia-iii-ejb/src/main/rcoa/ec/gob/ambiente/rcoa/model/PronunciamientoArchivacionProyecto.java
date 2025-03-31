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

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;


/**
 * The persistent class for the project_prohibition_pronouncement database table.
 * 
 */
@Entity
@Table(name="project_prohibition_pronouncement", schema = "coa_mae")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "prpp_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "prpp_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "prpp_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "prpp_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "prpp_user_update")) })


@NamedQueries({
@NamedQuery(name="PronunciamientoArchivacionProyecto.findAll", query="SELECT p FROM PronunciamientoArchivacionProyecto p"),
@NamedQuery(name=PronunciamientoArchivacionProyecto.GET_POR_PROYECTO, query="SELECT p FROM PronunciamientoArchivacionProyecto p  where p.idProyecto = :idProyecto and p.estado = true order by id desc") })

public class PronunciamientoArchivacionProyecto extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final String PAQUETE = "ec.gob.ambiente.rcoa.model.";

	public static final String GET_POR_PROYECTO = PAQUETE + "PronunciamientoArchivacionProyecto.getPorProyecto";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="prpp_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="prco_id")
	private Integer idProyecto;

	@Getter
	@Setter
	@Column(name="prpp_code")
	private String codigo;
	
	@Getter
	@Setter
	@Column(name="prpp_date_code")
	private Date fechaOficio;

}