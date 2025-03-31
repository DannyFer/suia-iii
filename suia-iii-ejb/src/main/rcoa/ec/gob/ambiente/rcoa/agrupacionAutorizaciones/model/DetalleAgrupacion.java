package ec.gob.ambiente.rcoa.agrupacionAutorizaciones.model;

import java.io.Serializable;
import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

/**
 * The persistent class for the secondary_grouping database table.
 * 
 */
@Entity
@Table(name="secondary_grouping", schema = "coa_grouping")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "segr_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "segr_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "segr_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "segr_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "segr_user_update")) })

@NamedQueries({
@NamedQuery(name="DetalleAgrupacion.findAll", query="SELECT d FROM DetalleAgrupacion d"),
@NamedQuery(name=DetalleAgrupacion.GET_POR_ID_AGRUPACION, query="SELECT d FROM DetalleAgrupacion d where d.idPrincipal = :idPrincipal and d.estado = true"),
})
public class DetalleAgrupacion extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final String PAQUETE = "ec.gob.ambiente.rcoa.agrupacionAutorizaciones.model.";

	public static final String GET_POR_ID_AGRUPACION = PAQUETE + "DetalleAgrupacion.getPorIdAgrupacion";
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="segr_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="segr_code_text")
	private String codigoProyecto;

	@Getter
	@Setter
	@Column(name="segr_number_code")
	private Integer idProyecto;

	@Getter
	@Setter
	@Column(name="segr_project_name")
	private String nombreProyecto;

	@Getter
	@Setter
	@Column(name="segr_sector")
	private String sector;

	@Getter
	@Setter
	@Column(name="segr_status_project")
	private String estadoProyecto;

	@Getter
	@Setter
	@Column(name="segr_system")
	private String sistemaFuente;

	@Getter
	@Setter
	@Column(name="magr_id")
	private Integer idPrincipal;
}