package ec.gob.ambiente.suia.domain.desechosfisicos;

import java.io.Serializable;

import javax.persistence.*;


/**
 * The persistent class for the coordinates database table.
 * 
 */
@Entity
@Table(name="coordinates", schema="waste_dangerous")
@NamedQuery(name="Coordinate.findAll", query="SELECT c FROM Coordinate c")
public class Coordinate implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@Column(name="coor_id")
	private Integer coorId;

	@Column(name="coor_description")
	private String coorDescription;

	@Column(name="coor_shape")
	private Integer coorShape;

	@Column(name="coor_type")
	private String coorType;

	@Column(name="coor_x")
	private Integer coorX;

	@Column(name="coor_y")
	private Integer coorY;

	//bi-directional many-to-one association to Licensing
	@ManyToOne
	@JoinColumn(name="lice_id")
	private Licensing licensing;

	public Coordinate() {
	}

	public Integer getCoorId() {
		return this.coorId;
	}

	public void setCoorId(Integer coorId) {
		this.coorId = coorId;
	}

	public String getCoorDescription() {
		return this.coorDescription;
	}

	public void setCoorDescription(String coorDescription) {
		this.coorDescription = coorDescription;
	}

	public Integer getCoorShape() {
		return this.coorShape;
	}

	public void setCoorShape(Integer coorShape) {
		this.coorShape = coorShape;
	}

	public String getCoorType() {
		return this.coorType;
	}

	public void setCoorType(String coorType) {
		this.coorType = coorType;
	}

	public Integer getCoorX() {
		return this.coorX;
	}

	public void setCoorX(Integer coorX) {
		this.coorX = coorX;
	}

	public Integer getCoorY() {
		return this.coorY;
	}

	public void setCoorY(Integer coorY) {
		this.coorY = coorY;
	}

	public Licensing getLicensing() {
		return this.licensing;
	}

	public void setLicensing(Licensing licensing) {
		this.licensing = licensing;
	}

}