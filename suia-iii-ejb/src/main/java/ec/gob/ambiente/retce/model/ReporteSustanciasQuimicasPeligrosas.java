package ec.gob.ambiente.retce.model;

import java.io.Serializable;

import javax.persistence.*;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the dangerous_chemistry_substances_retce database table.
 * 
 */
@Entity
@Table(name="dangerous_chemistry_substances_retce", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "dcsr_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "dcsr_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "dcsr_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "dcsr_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "dcsr_user_update")) })
@NamedQueries({
	@NamedQuery(name = ReporteSustanciasQuimicasPeligrosas.GET_POR_ANIO_INFORMACION_PROYECTO, query = "SELECT t FROM ReporteSustanciasQuimicasPeligrosas t WHERE t.informacionProyecto.id = :idProyecto and t.anioDeclaracion = :anio and estado = true ORDER BY t.id desc")
	})
public class ReporteSustanciasQuimicasPeligrosas extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String GET_POR_ANIO_INFORMACION_PROYECTO = "ec.gob.ambiente.retce.model.ReporteSustanciasQuimicasPeligrosas.getPorAnioInformacionProyecto";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="dcsr_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="dcsr_code")
	private String codigo;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="proj_id")
	private InformacionProyecto informacionProyecto;
	
	@Getter
	@Setter
	@Column(name = "dcsr_sended")
	private Boolean enviado;

	@Getter
	@Setter
	@Column(name = "dcsr_declaration_year")
	private Integer anioDeclaracion;

	public ReporteSustanciasQuimicasPeligrosas() {
	}

}
