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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="environmental_record_phases", schema="coa_environmental_record")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "enph_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "enph_creation_date")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "enph_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "enph_creator_user")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "enph_user_update")) })
@NamedQueries({
	@NamedQuery(name=FasesRegistroAmbiental.GET_FASES_POR_REGISTROAMBIENTAL, query="SELECT m FROM FasesRegistroAmbiental m where m.estado = true and m.registroAmbientalId = :registroAmbientalId")
})
public class FasesRegistroAmbiental extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String GET_FASES_POR_REGISTROAMBIENTAL = "ec.gob.ambiente.rcoa.model.FasesRegistroAmbiental.getFasePorRegistroAmbiental";
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="enph_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="enph_start_date")
	private Date fechaInicio;

	@Getter
	@Setter
	@Column(name="enph_end_date")
	private Date fechaFin;

	@Getter
	@Setter
	@Column(name="enph_description")
	private String descripcion;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "phas_id")
	private FasesCoa fasesCoa;

	@Getter
	@Setter
	@Column(name="enre_id")
	private Integer registroAmbientalId;


	@Getter
	@Setter
	@Transient
	private boolean nuevoRegistro;
}