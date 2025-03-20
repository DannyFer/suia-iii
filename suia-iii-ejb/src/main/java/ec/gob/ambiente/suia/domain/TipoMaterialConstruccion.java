package ec.gob.ambiente.suia.domain;

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

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * <b> Entity Bean. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: Feb 2, 2015]
 *          </p>
 */
@Table(name = "building_material_types", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "bmty_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "bmty_status = 'TRUE'")
@Entity
public class TipoMaterialConstruccion extends EntidadBase {

	private static final long serialVersionUID = -4283458726019202658L;

	@Id
	@SequenceGenerator(name = "BUILDING_MATERIAL_TYPES_GENERATOR", schema = "suia_iii", sequenceName = "seq_bmty_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BUILDING_MATERIAL_TYPES_GENERATOR")
	@Getter
	@Setter
	@Column(name = "bmty_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "bmty_name")
	private String nombre;

	@Getter
	@Setter
	@Column(name = "bmty_key")
	private String clave;

	@Override
	public String toString() {
		return nombre;
	}
}
