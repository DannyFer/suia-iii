package ec.gob.ambiente.retce.model;

import java.io.Serializable;

import javax.persistence.*;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the consumption_processes_type database table.
 * 
 */
@Entity
@Table(name="consumption_processes_type", schema = "retce")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "cpty_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "cpty_date_create")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "cpty_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "cpty_user_create")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "cpty_user_update")) })

@NamedQueries({
	@NamedQuery(name="TipoProcesoConsumo.findAll", query="SELECT p FROM TipoProcesoConsumo p"),
	@NamedQuery(name = TipoProcesoConsumo.GET_POR_ID_CONSUMO_COMBUSTIBLE, query = "SELECT t.tipoProceso FROM TipoProcesoConsumo t WHERE t.idConsumoCombustible = :idConsumoCombustible and t.estado = true ORDER BY t.id desc"),
	@NamedQuery(name = TipoProcesoConsumo.GET_POR_ID_CONSUMO_ENERGIA, query = "SELECT t.tipoProceso FROM TipoProcesoConsumo t WHERE t.idConsumoElectrico = :idConsumoElectrico and t.estado = true ORDER BY t.id desc"),
	@NamedQuery(name = TipoProcesoConsumo.GET_POR_ID_CONSUMO_AGUA, query = "SELECT t.tipoProceso FROM TipoProcesoConsumo t WHERE t.idConsumoAgua = :idConsumoAgua and t.estado = true ORDER BY t.id desc") })
public class TipoProcesoConsumo extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String GET_POR_ID_CONSUMO_COMBUSTIBLE = "ec.gob.ambiente.retce.model.TipoProcesoConsumo.getPorIDConsumoCombustible";
	public static final String GET_POR_ID_CONSUMO_ENERGIA = "ec.gob.ambiente.retce.model.TipoProcesoConsumo.getPorIDConsumoEnergia";
	public static final String GET_POR_ID_CONSUMO_AGUA = "ec.gob.ambiente.retce.model.TipoProcesoConsumo.getPorIDConsumoAgua";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cpty_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name="epco_id")
	private Integer idConsumoElectrico;

	@Getter
	@Setter
	@Column(name="fuco_id")
	private Integer idConsumoCombustible;

	@Getter
	@Setter
	@Column(name="waus_id")
	private Integer idConsumoAgua;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="gcde_id_processes_type")
	private DetalleCatalogoGeneral tipoProceso;
}