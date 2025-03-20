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
 * The persistent class for the electric_power_consumption database table.
 * 
 */
@Entity
@Table(name="electric_power_consumption", schema = "retce")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "epco_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "epco_date_create")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "epco_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "epco_user_create")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "epco_user_update")) })

@NamedQueries({
	@NamedQuery(name="ConsumoEnergia.findAll", query="SELECT c FROM ConsumoEnergia c"),
	@NamedQuery(name = ConsumoEnergia.GET_POR_CONSUMO, query = "SELECT t FROM ConsumoEnergia t WHERE t.idConsumoRecurso = :idConsumo and estado = true ORDER BY t.id desc") })
public class ConsumoEnergia  extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String GET_POR_CONSUMO = "ec.gob.ambiente.retce.model.ConsumoEnergia.getPorConsumo";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "epco_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="epco_annual_value")
	private Double valorAnual;

	@Getter
	@Setter
	@Column(name="epco_number_supplies")
	private Integer numeroSuministro;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="gcde_id_type_supplies")
	private DetalleCatalogoGeneral tipoSuministro;
	
	@Getter
	@Setter
	@Column(name="epco_other_type_supplies")
	private String otroTipoSuministro;

	@Getter
	@Setter
	@Column(name="reco_id")
	private Integer idConsumoRecurso;
	
	@Getter
	@Setter
	@OneToMany(mappedBy = "idConsumoElectrico")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "cpty_status = 'TRUE'")
	private List<TipoProcesoConsumo> listaTipoProcesos;
	
	@Getter
	@Setter
	@OneToMany(mappedBy = "consumoEnergia")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "sure_status = 'TRUE'")
	private List<SubstanciasRetce> listaSustanciasRetce;
	
	@Getter
	@Setter
	@Transient
	private List<DetalleCatalogoGeneral> listaProcesos;
	
	@Getter
	@Setter
	@Transient
	private List<Documento> listaMediosVerificacion;
	
}