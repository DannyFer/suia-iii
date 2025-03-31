package ec.gob.ambiente.rcoa.dto;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;

import lombok.Getter;
import lombok.Setter;

@Stateless
public class EntityRegistroGeneradorDesecho {
	
	@Getter
	@Setter
	private String provicional, fechaEmisionRgd, fechaActualizacionRgd, nombreOperador, responsableEmpresa, 
					cargoEmpresa, codigoRegistroPreliminar, actividadCIIU, autorizacionAdministrativaAmbiental,
					direccionOperador, tabla, numeroActualizacion, displayUbicacion;
	
	@Getter
	@Setter
	private String nombreProyecto, codigoRegistroGenerador, desechos, direccionProyectoRgd, puntosGeneracion, ubicacionPuntosGeneracion, literalkProvicional;
	
	@Getter
	@Setter
	private String nombreAutoridad, cargoAutoridad, entidadAutoridad;
	
	@Getter
	@Setter
	private String gestionPolitica, acuerdoMinisterial, politicaNeumaticos, tipoDeclaracion, mostrarTipoNeumatico, mostrarPoliticas;
	
	@Getter
	@Setter
	private String informacionRgd, cedulaOperador, representanteLegal, codigoProyecto, codigoCiiu, nombreCiiu, nroResolucion, mostrarCargo, institucion;
	
	String literal = "k) Finalizar el proceso de regularización ambiental para obtener el documento definitivo del Registro Generador de Residuos y Desechos Peligrosos y/o Especiales. En caso de no culminar el proceso de regularización ambiental en los plazos establecidos en la normativa ambiental, se procederá a la cancelación del Registro de Generador Provisional, sin perjuicio de las acciones a las que haya lugar.";
	
	@PostConstruct
	public void init(){
		setLiteralkProvicional(literal);
	}
	

}
