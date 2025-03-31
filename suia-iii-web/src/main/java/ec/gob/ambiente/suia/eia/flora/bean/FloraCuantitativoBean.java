package ec.gob.ambiente.suia.eia.flora.bean;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.CatalogoGeneral;

public class FloraCuantitativoBean {

	//PRIMERA TABLA
	
	@Getter
	@Setter
	private Integer id;
	@Getter
	@Setter
	private String puntoMuestreo;
	@Getter
	@Setter
	private int contador;
	@Getter
	@Setter
	private Double px1;
	@Getter
	@Setter
	private Double px2;
	@Getter
	@Setter
	private Double px3;
	@Getter
	@Setter
	private Double px4;
	@Getter
	@Setter
	private Double py1;
	@Getter
	@Setter
	private Double py2;
	@Getter
	@Setter
	private Double py3;
	@Getter
	@Setter
	private Double py4;
	@Getter
	@Setter
	private Double altitud1;
	@Getter
	@Setter
	private Double altitud2;
	@Getter
	@Setter
	private Double altitud3;
	@Getter
	@Setter
	private Double altitud4;
	@Getter
	@Setter
	private CatalogoGeneral tipoVegetacion;
	@Getter
	@Setter
	private Date fechaMuestreo;
	@Getter
	@Setter
	private String esfuerzoMuestreo;

	@Getter
	@Setter
	private Integer p1Id;
	@Getter
	@Setter
	private Integer p2Id;
	@Getter
	@Setter
	private Integer p3Id;
	@Getter
	@Setter
	private Integer p4Id;

	@Getter
	@Setter
	private boolean editar = false;

	@Override
	public String toString() {
		return this.contador + "|" +this.puntoMuestreo;
	}

	public FloraCuantitativoBean(Integer id, String puntoMuestreo,
			int contador, Double px1, Double px2, Double px3, Double px4,
			Double py1, Double py2, Double py3, Double py4, Double altitud1,
			Double altitud2, Double altitud3, Double altitud4,
			CatalogoGeneral tipoVegetacion, Date fechaMuestreo,
			String esfuerzoMuestreo) {
		super();
		this.id = id;
		this.puntoMuestreo = puntoMuestreo;
		this.contador = contador;
		this.px1 = px1;
		this.px2 = px2;
		this.px3 = px3;
		this.px4 = px4;
		this.py1 = py1;
		this.py2 = py2;
		this.py3 = py3;
		this.py4 = py4;
		this.altitud1 = altitud1;
		this.altitud2 = altitud2;
		this.altitud3 = altitud3;
		this.altitud4 = altitud4;
		this.tipoVegetacion = tipoVegetacion;
		this.fechaMuestreo = fechaMuestreo;
		this.esfuerzoMuestreo = esfuerzoMuestreo;
	}

	public FloraCuantitativoBean() {
	}

}
