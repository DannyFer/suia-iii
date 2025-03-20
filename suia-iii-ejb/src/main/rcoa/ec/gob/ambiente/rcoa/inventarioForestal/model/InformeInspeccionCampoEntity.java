package ec.gob.ambiente.rcoa.inventarioForestal.model;

import lombok.Getter;
import lombok.Setter;

public class InformeInspeccionCampoEntity {

	@Getter
	@Setter
	private String direccionForestal;
	
	@Getter
	@Setter
	private String codigoReporte;
	
	@Getter
	@Setter
	private String nombreProyecto;
	
	@Getter
	@Setter
	private String codigoProyecto;
	
	@Getter
	@Setter
	private String razonSocialOperador;
	
	@Getter
	@Setter
	private String ubicacionProyecto;
	
	@Getter
	@Setter
	private String fechaInspeccion;
	
	@Getter
	@Setter
	private String fechaFin;
	
	@Getter
	@Setter
	private String fecha;
	
	@Getter
	@Setter
	private String interseccionProyecto;
	
	@Getter
	@Setter
	private String superficieTotalProyecto;
	
	@Getter
	@Setter
	private String superficieCobertura;
	
	@Getter
	@Setter
	private String siglas;
	
	@Getter
	@Setter
	private String delegadosInspecion;
	
	@Getter
	@Setter
	private String nombresDelegadoInspeccion;
	
	@Getter
	@Setter
	private String areaDelegado;
	
	@Getter
	@Setter
	private String cargoDelegado;
	
	@Getter
	@Setter
	private String antecedentes;
	
	@Getter
	@Setter
	private String marcoLegal;
	
	@Getter
	@Setter
	private String objetivo;
	
	@Getter
	@Setter
	private String tablaCoordenadasInventarioForestal;
	
	@Getter
	@Setter
	private String caracterizacionCobertura;
	
	@Getter
	@Setter
	private String caracterizacionEcosistemas;
	
	@Getter
	@Setter
	private String caracterizacionAreaImplantacion;
	
	@Getter
	@Setter
	private String conclusiones;
	
	@Getter
	@Setter
	private String recomendaciones;
	
	@Getter
	@Setter
	private String anexos;
	
	@Getter
	@Setter
	private String elaboradoPor;
	
	@Getter
	@Setter
	private String denominacionCargo;
	
	@Getter
	@Setter
	private byte[] archivoInforme;
	
	@Getter
	@Setter
	private String informePath;
	
	@Getter
	@Setter
	private String nombreFichero;

}
