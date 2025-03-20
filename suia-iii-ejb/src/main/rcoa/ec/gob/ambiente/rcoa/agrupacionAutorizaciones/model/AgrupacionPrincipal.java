package ec.gob.ambiente.rcoa.agrupacionAutorizaciones.model;

import java.io.Serializable;
import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;


/**
 * The persistent class for the main_grouping database table.
 * 
 */
@Entity
@Table(name="main_grouping", schema = "coa_grouping")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "magr_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "magr_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "magr_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "magr_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "magr_user_update")) })

@NamedQueries({
@NamedQuery(name="AgrupacionPrincipal.findAll", query="SELECT a FROM AgrupacionPrincipal a"),
@NamedQuery(name=AgrupacionPrincipal.GET_POR_PROYECTO_ESTADO, query="SELECT a FROM AgrupacionPrincipal a where a.codigoProyecto = :proyecto and a.estadoAgrupacion = :estado and a.estado = true"),
})
public class AgrupacionPrincipal extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final String PAQUETE = "ec.gob.ambiente.rcoa.agrupacionAutorizaciones.model.";

	public static final String GET_POR_PROYECTO_ESTADO = PAQUETE + "AgrupacionPrincipal.getPorProyectoEstado";
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="magr_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="magr_code_text")
	private String codigoProyecto;

	@Getter
	@Setter
	@Column(name="magr_grouping_status")
	private Integer estadoAgrupacion;

	@Getter
	@Setter
	@Column(name="magr_number_code")
	private Integer idProyecto;

	@Getter
	@Setter
	@Column(name="magr_operator_id")
	private String cedulaOperador;

	@Getter
	@Setter
	@Column(name="magr_operator_name")
	private String nombreOperador;

	@Getter
	@Setter
	@Column(name="magr_project_name")
	private String nombreProyecto;

	@Getter
	@Setter
	@Column(name="magr_sector")
	private String sector;

	@Getter
	@Setter
	@Column(name="magr_status_project")
	private String estadoProyecto;

	@Getter
	@Setter
	@Column(name="magr_system")
	private String sistemaFuente;

	@Getter
	@Setter
	@Column(name="magr_validate_information")
	private Boolean agrupacionCorrecta;

}