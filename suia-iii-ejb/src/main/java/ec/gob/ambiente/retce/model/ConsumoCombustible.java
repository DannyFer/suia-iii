package ec.gob.ambiente.retce.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the fuel_consumption database table.
 * 
 */
@Entity
@Table(name="fuel_consumption", schema = "retce")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "fuco_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "fuco_date_create")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "fuco_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "fuco_user_create")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "fuco_user_update")) })

@NamedQueries({
	@NamedQuery(name="ConsumoCombustible.findAll", query="SELECT c FROM ConsumoCombustible c"),
	@NamedQuery(name = ConsumoCombustible.GET_POR_CONSUMO, query = "SELECT t FROM ConsumoCombustible t WHERE t.idConsumoRecurso = :idConsumo and estado = true ORDER BY t.id desc") })
public class ConsumoCombustible extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String GET_POR_CONSUMO = "ec.gob.ambiente.retce.model.ConsumoCombustible.getPorConsumo";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "fuco_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="fuco_annual_value_m3")
	private Double valorAnualMetrosCubicos;

	@Getter
	@Setter
	@Column(name="fuco_annual_value_ton")
	private Double valorAnualToneladas;

	@Getter
	@Setter
	@Column(name="fuco_density_value")
	private Double valorDensidad;

	@Getter
	@Setter
	@Column(name="fuco_other_fuel_type")
	private String otroTipoCombustible;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="gcde_id_fuel_type")
	private DetalleCatalogoGeneral tipoCombustible;
	
	@Getter
	@Setter
	@Column(name="reco_id")
	private Integer idConsumoRecurso;	
	
	@Getter
	@Setter
	@OneToMany(mappedBy = "idConsumoCombustible")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "cpty_status = 'TRUE'")
	private List<TipoProcesoConsumo> listaTipoProcesos;
	
	@Getter
	@Setter
	@Transient
	private List<DetalleCatalogoGeneral> listaProcesos;
	
	@Getter
	@Setter
	@Transient
	private List<Documento> listaMediosVerificacion;
	
}