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
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/***
 * 
 * <b> Entity Bean tabla modalidades de gestion de desechos. </b>
 * 
 * @author vero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: vero $, $Date: 24/06/2015 $]
 *          </p>
 */
@Table(name = "waste_management_mode", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "wmmo_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wmmo_status = 'TRUE'")
@Entity
public class ModalidadGestionDesechos extends EntidadBase {

	private static final long serialVersionUID = -6987168037150557182L;

	public static final int ID_MODALIDAD_DISPOSICION_FINAL = 6;

	public static final int ID_MODALIDAD_COPROCESAMIENTO = 5;

	public static final int ID_MODALIDAD_INCINERACION = 4;

	public static final int ID_MODALIDAD_TRATAMIENTO = 3;

	public static final int ID_MODALIDAD_REUSO = 2;

	public static final int ID_MODALIDAD_RECICLAJE = 1;

	public static final int ID_MODALIDAD_TRANSPORTE = 7;

	public static final int ID_OTROS = 8;

	@Id
	@SequenceGenerator(name = "WASTE_MANAGEMENT_MODE_GENERATOR", schema = "suia_iii", sequenceName = "seq_waste_management_mode_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WASTE_MANAGEMENT_MODE_GENERATOR")
	@Getter
	@Setter
	@Column(name = "wmmo_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "wmmo_name")
	private String nombre;

	@Override
	public String toString() {
		return nombre;
	}
	
	@Getter
	@Setter
	@Transient
	private Boolean deshabilitado = false;

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ModalidadGestionDesechos)) {
			return false;
		}
		ModalidadGestionDesechos other = (ModalidadGestionDesechos) obj;
		if (((this.id == null) && (other.id != null)) || ((this.id != null) && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}
}
