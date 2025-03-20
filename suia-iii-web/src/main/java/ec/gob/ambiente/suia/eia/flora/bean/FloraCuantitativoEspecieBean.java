package ec.gob.ambiente.suia.eia.flora.bean;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.CatalogoGeneral;

public class FloraCuantitativoEspecieBean {

	@Getter
	@Setter
	private Integer id;
	@Getter
	@Setter
	private Integer contador;
	@Getter
	@Setter
	private FloraCuantitativoBean floraCuantitativo;
	@Getter
	@Setter
	private String familia;
	@Getter
	@Setter
	private String genero;
	@Getter
	@Setter
	private String especie;
	@Getter
	@Setter
	private String nombreCientifico;
	@Getter
	@Setter
	private String nombreComun;
	@Getter
	@Setter
	private CatalogoGeneral tipoVegetacion;
	@Getter
	@Setter
	private CatalogoGeneral habito;
	@Getter
	@Setter
	private CatalogoGeneral estadoIndividuo;
	@Getter
	@Setter
	private CatalogoGeneral uso;
	@Getter
	@Setter
	private CatalogoGeneral origen;
	@Getter
	@Setter
	private CatalogoGeneral uicnInternacional;
	@Getter
	@Setter
	private CatalogoGeneral nivelIdentificacion;
	@Getter
	@Setter
	private CatalogoGeneral cities;
	@Getter
	@Setter
	private CatalogoGeneral libroRojo;
	@Getter
	@Setter
	private Double nroColeccion;
	@Getter
	@Setter
	private String centroTenencia;
	@Getter
	@Setter
	private String descripcionFoto;
	@Getter
	@Setter
	private Double dap;
	@Getter
	@Setter
	private Double aTotal;
	@Getter
	@Setter
	private Double aComercial;

	// Archivos

	@Getter
	@Setter
	private byte[] fotoEspecie;
	@Getter
	@Setter
	private String fotoEspecieContentType;
	@Getter
	@Setter
	private String fotoEspecieName;

	@Getter
	@Setter
	private boolean editar = false;
	@Getter
	@Setter
	private boolean archivoEditado = false;

	public FloraCuantitativoEspecieBean(Integer id, Integer contador,
			FloraCuantitativoBean floraCuantitativo, String familia,
			String genero, String especie, String nombreCientifico,
			String nombreComun, CatalogoGeneral tipoVegetacion,
			CatalogoGeneral habito, CatalogoGeneral estadoIndividuo,
			CatalogoGeneral uso, CatalogoGeneral origen,
			CatalogoGeneral uicnInternacional,
			CatalogoGeneral nivelIdentificacion, CatalogoGeneral cities,
			CatalogoGeneral libroRojo, Double nroColeccion,
			String centroTenencia, String descripcionFoto, Double dap,
			Double aTotal, Double aComercial, byte[] fotoEspecie,
			String fotoEspecieContentType, String fotoEspecieName) {
		super();
		this.id = id;
		this.contador = contador;
		this.floraCuantitativo = floraCuantitativo;
		this.familia = familia;
		this.genero = genero;
		this.especie = especie;
		this.nombreCientifico = nombreCientifico;
		this.nombreComun = nombreComun;
		this.tipoVegetacion = tipoVegetacion;
		this.habito = habito;
		this.estadoIndividuo = estadoIndividuo;
		this.uso = uso;
		this.origen = origen;
		this.uicnInternacional = uicnInternacional;
		this.nivelIdentificacion = nivelIdentificacion;
		this.cities = cities;
		this.libroRojo = libroRojo;
		this.nroColeccion = nroColeccion;
		this.centroTenencia = centroTenencia;
		this.descripcionFoto = descripcionFoto;
		this.dap = dap;
		this.aTotal = aTotal;
		this.aComercial = aComercial;
		this.fotoEspecie = fotoEspecie;
		this.fotoEspecieContentType = fotoEspecieContentType;
		this.fotoEspecieName = fotoEspecieName;
	}

	public FloraCuantitativoEspecieBean() {
	}
}
