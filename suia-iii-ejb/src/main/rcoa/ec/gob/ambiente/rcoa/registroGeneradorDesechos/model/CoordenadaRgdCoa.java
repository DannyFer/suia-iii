package ec.gob.ambiente.rcoa.registroGeneradorDesechos.model;

import javax.persistence.*;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the coordinates_coa database table.
 * 
 */
@Entity
@Table(name="coordinates_coa", schema="coa_waste_generator_record")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "coor_status")) })
@NamedQuery(name="CoordenadaRgdCoa.findAll", query="SELECT c FROM CoordenadaRgdCoa c")
public class CoordenadaRgdCoa extends EntidadBase implements Comparable<CoordenadaRgdCoa> {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="coor_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="coor_creation_date")
	private Date fechaCreacion;

	@Getter
	@Setter
	@Column(name="coor_creator_user")
	private String usuarioCreacion;

	@Getter
	@Setter
	@Column(name="coor_date_update")
	private Date fechaModificacion;

	@Getter
	@Setter
	@Column(name="coor_description")
	private String descripcion;

	@Getter
	@Setter
	@Column(name="coor_order")
	private Integer orden;

	@Getter
	@Setter
	@Column(name="coor_update_number")
	private Integer numeroModificacion;

	@Getter
	@Setter
	@Column(name="coor_user_update")
	private String usuarioModificacion;

	@Getter
	@Setter
	@Column(name="coor_x")
	private Double x;

	@Getter
	@Setter
	@Column(name="coor_y")
	private Double y;

	@Getter
	@Setter
	@Column(name="coor_zone")
	private String zona;

	//bi-directional many-to-one association to FormaPuntoRecuperacionRgdRcoa
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="rpsh_id")
	private FormaPuntoRecuperacionRgdRcoa formasPuntoRecuperacionRgdRcoa;

	public CoordenadaRgdCoa() {
	}
	
	public boolean isDataComplete() {
		return (orden != null && x != null && y != null);
	}

	public boolean isEqualsPoints(Object obj) {
		CoordenadaRgdCoa coordenada = (CoordenadaRgdCoa) obj;
		return coordenada.getX().equals(getX()) && coordenada.getY().equals(getY());
	}

	@Override
	public int compareTo(CoordenadaRgdCoa o) {
		if (this.getOrden() < o.getOrden())
			return -1;
		if (this.getOrden() > o.getOrden())
			return 1;
		return 0;
	}

	/**
	 * SE INVOCA PARA LA TRANSFORMACION
	 */
	@Override
	public String toString() {
		return x + " " + y;
	}

}