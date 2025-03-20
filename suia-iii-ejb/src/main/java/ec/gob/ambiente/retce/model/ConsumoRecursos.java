package ec.gob.ambiente.retce.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the resource_consumption database table.
 * 
 */
@Entity
@Table(name="resource_consumption", schema = "retce")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "reco_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "reco_date_create")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "reco_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "reco_user_create")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "reco_user_update")) })

@NamedQueries({
	@NamedQuery(name="ConsumoRecursos.findAll", query="SELECT c FROM ConsumoRecursos c"),
	@NamedQuery(name = ConsumoRecursos.GET_POR_ANIO_INFORMACION_PROYECTO, query = "SELECT t FROM ConsumoRecursos t WHERE t.informacionProyecto.id = :idProyecto and t.anioDeclaracion = :anio and estado = true ORDER BY t.id desc"),
	@NamedQuery(name = ConsumoRecursos.GET_POR_USUARIO, query = "SELECT t FROM ConsumoRecursos t WHERE t.usuarioCreacion = :usuario and estado = true ORDER BY t.id desc"),
	@NamedQuery(name = ConsumoRecursos.GET_POR_INFORMACION_PROYECTO, query = "SELECT t FROM ConsumoRecursos t WHERE t.informacionProyecto.id = :idProyecto and estado = true ORDER BY t.id desc")
	})
public class ConsumoRecursos extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String GET_POR_ANIO_INFORMACION_PROYECTO = "ec.gob.ambiente.retce.model.ConsumoRecursos.getPorAnioInformacionProyecto";
	public static final String GET_POR_USUARIO = "ec.gob.ambiente.retce.model.ConsumoRecursos.getPorUsuario";
	public static final String GET_POR_INFORMACION_PROYECTO = "ec.gob.ambiente.retce.model.ConsumoRecursos.getPorInformacionProyecto";
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "reco_id")
	private Integer id;	

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
	@Column(name="reco_code")
	private String codigoTramite;

	@Getter
	@Setter
	@Column(name = "reco_declaration_year")
	private Integer anioDeclaracion;
	
	@Getter
	@Setter
	@Column(name = "reco_date_processing")
	private Date fechaTramite;

	@Getter
	@Setter
	@Column(name = "reco_registration_finalized")
	private Boolean registroFinalizado;

	
}