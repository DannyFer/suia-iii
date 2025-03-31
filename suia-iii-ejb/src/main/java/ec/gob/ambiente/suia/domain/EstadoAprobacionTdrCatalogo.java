package ec.gob.ambiente.suia.domain;

import java.io.Serializable;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;


/**
 * The persistent class for the approval_status_tdr_catalog database table. hazardous_wastes_waste_dangerous_labeled
 * 
 */

@NamedQueries({ 
	@NamedQuery(name = EstadoAprobacionTdrCatalogo.LISTAR_ESTADOS_POR_ORDEN, query = "SELECT a FROM EstadoAprobacionTdrCatalogo a where a.tipo = :tipo") })
@Entity
@Table(name = "approval_status_tdr_catalog", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "astc_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "astc_status = 'TRUE'")
public class EstadoAprobacionTdrCatalogo extends EntidadBase {
	private static final long serialVersionUID = 1L;
	
	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR_ESTADOS_POR_ORDEN = PAQUETE + "EstadoAprobacionTdrCatalogo.listarEstadosPorOrden";
	
	@Id
	@SequenceGenerator(name = "HWGA_HWGAID_GENERATOR", sequenceName="hazardous_wastes_generators_associated_hwga_id_seq", schema="suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "HWGA_HWGAID_GENERATOR")
	@Column(name = "astc_id")
	@Getter
	@Setter
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "astc_description")
	private String descripcion;
	
	@Getter
	@Setter
	@Column(name = "astc_name")
	private String nombre;
	
	@Getter
	@Setter
	@Column(name = "astc_type")
	private Integer tipo;
	
	@Getter
	@Setter
	@Column(name = "astc_status_approval")
	private Boolean estadoLogicoAprobacion;

	@Override
	public String toString() {
		return String.valueOf(id);
	}
}