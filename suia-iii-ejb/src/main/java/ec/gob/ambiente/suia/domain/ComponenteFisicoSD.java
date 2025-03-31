package ec.gob.ambiente.suia.domain;

import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the environmental_record_physical_component database table.
 * 
 */
@Entity
@Table(name="scdr_environmental_record_physical_component", schema = "suia_iii")
@AttributeOverrides({@AttributeOverride(name = "estado", column = @Column(name = "erpc_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "erpc_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "erpc_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "erpc_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "erpc_user_update"))})


@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "erpc_status = 'TRUE'")
public class ComponenteFisicoSD extends EntidadAuditable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Getter
	@Setter
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="erpc_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="erpc_altitude")
	private String altitud;

	@Getter
	@Setter
	@Column(name="erpc_annual_rainfall")
	private String precipitacionAnual;

	@Getter
	@Setter
	@Column(name="erpc_average_annual_temperature")
	private String temperaturaMediaAnual;

	@Getter
	@Setter
	@Column(name="erpc_water_component")
	private String componenteHidrico;

	@Getter
	@Setter
	@Column(name = "scdr_id")
	private Integer perforacionExplorativa;
	
	@Getter
	@Setter
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "componenteFisicoSD")
	private List<ComponenteFisicoPendienteSD> componenteFisicoPendienteList;
	
}