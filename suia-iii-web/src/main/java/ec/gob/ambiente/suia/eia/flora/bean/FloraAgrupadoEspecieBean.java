package ec.gob.ambiente.suia.eia.flora.bean;

import ec.gob.ambiente.suia.utils.Archivo;
import lombok.Getter;
import lombok.Setter;

public class FloraAgrupadoEspecieBean {

	@Getter
	@Setter
	private Integer id;
	@Getter
	@Setter
	private Integer contador;
	@Getter
	@Setter
	private String puntoMuestreo = "";
	@Getter
	@Setter
	private String familia = "";
	@Getter
	@Setter
	private String genero = "";
	@Getter
	@Setter
	private String especie = "";
	@Getter
	@Setter
	private String nombreCientifico = "";
	@Getter
	@Setter
	private String nombreComun = "";
	@Getter
	@Setter
	private String tipoVegetacion = "";
	@Getter
	@Setter
	private String origen = "";
	@Getter
	@Setter
	private String uicnInternacional = "";
	@Getter
	@Setter
	private String cities = "";
	@Getter
	@Setter
	private String libroRojo = "";
	@Getter
	@Setter
	private Double sumDap = 0.0;
	@Getter
	@Setter
	private Double ab;
	@Getter
	@Setter
	private Double dmr;
	@Getter
	@Setter
	private Double dnr;
	@Getter
	@Setter
	private Double ivi;
	@Getter
	@Setter
	private Double biomasa;
	@Getter
	@Setter
	private Archivo archivo;
	@Getter
	@Setter
	private boolean editar = false;

	public FloraAgrupadoEspecieBean() {
	}

	public FloraAgrupadoEspecieBean(Integer id, Integer contador,
			String puntoMuestreo, String familia, String genero,
			String especie, String nombreCientifico, String nombreComun,
			String tipoVegetacion, String origen, String uicnInternacional,
			String cities, String libroRojo, Double sumDap, Double ab,
			Double dmr, Double dnr, Double ivi, Double biomasa, Archivo archivo,
			boolean editar) {
		super();
		this.id = id;
		this.contador = contador;
		this.puntoMuestreo = puntoMuestreo;
		this.familia = familia;
		this.genero = genero;
		this.especie = especie;
		this.nombreCientifico = nombreCientifico;
		this.nombreComun = nombreComun;
		this.tipoVegetacion = tipoVegetacion;
		this.origen = origen;
		this.uicnInternacional = uicnInternacional;
		this.cities = cities;
		this.libroRojo = libroRojo;
		this.sumDap = sumDap;
		this.ab = ab;
		this.dmr = dmr;
		this.dnr = dnr;
		this.ivi = ivi;
		this.biomasa = biomasa;
		this.archivo = archivo;
		this.editar = editar;
	}

}
