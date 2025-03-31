package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
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
 *          [Autor: Carlos Pupo, Fecha: Feb 18, 2015]
 *          </p>
 */
@Table(name = "material_types", schema = "suia_iii")
@NamedQuery(name = "TipoMaterial.findById", query = "SELECT t FROM TipoMaterial t WHERE t.id=:id")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "maty_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "maty_status = 'TRUE'")
@Entity
public class TipoMaterial extends EntidadBase {

	private static final long serialVersionUID = -7283996033280195877L;

	public static final int MATERIAL_METALICO = 1;
	public static final int MATERIAL_NO_METALICO = 2;
	public static final int MATERIAL_CONSTRUCCION = 3;
	public static final int MATERIAL_RELAVES = 4;
	public static final int MATERIAL_RELAVESCONCENTRADO = 5;

	@Id
	@SequenceGenerator(name = "MATERIAL_TYPES_GENERATOR", schema = "suia_iii", sequenceName = "seq_maty_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MATERIAL_TYPES_GENERATOR")
	@Getter
	@Setter
	@Column(name = "maty_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "maty_name")
	private String nombre;

	@Override
	public String toString() {
		return nombre;
	}

	public TipoMaterial() {
	}

	public TipoMaterial(Integer id) {
		this.id = id;
	}

}
