package ec.gob.ambiente.rcoa.inventarioForestal.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 *
 * @author Luis Lema
 * @version Revision: 1.0
 */

@Entity
@Table(name = "types_reports", schema = "coa_forest_inventory")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "tyre_status")),	
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "tyre_user_creator")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "type_date_creation")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "tyre_user_update")), 
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "tyre_date_update"))
})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "tyre_status = 'TRUE'")
public class TipoReporteInventarioForestal extends EntidadAuditable {

	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	@Id
	@Column(name="tyre_id")
	@SequenceGenerator(name = "TYPE_REPORT_FOREST_INVENTORY_GENERATOR", sequenceName = "types_reports_tyre_id_seq", schema = "coa_forest_inventory", allocationSize = 1)	
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TYPE_REPORT_FOREST_INVENTORY_GENERATOR")
	private Integer id;
	
	
	@Getter
	@Setter
	@Column(name="tyre_name")
	private String tipoReporte;	

}
