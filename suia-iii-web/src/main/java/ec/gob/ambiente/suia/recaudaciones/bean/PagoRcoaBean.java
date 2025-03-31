package ec.gob.ambiente.suia.recaudaciones.bean;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ec.gob.ambiente.rcoa.enums.CatalogoTipoCoaEnum;
import ec.gob.ambiente.rcoa.facade.CatalogoCoaFacade;
import ec.gob.ambiente.rcoa.facade.PagoKushkiJsonFacade;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.model.PagoKushkiJson;
import ec.gob.ambiente.suia.domain.InstitucionFinanciera;
import ec.gob.ambiente.suia.domain.TransaccionFinanciera;
import ec.gob.ambiente.suia.recaudaciones.facade.DocumentosNUTFacade;
import ec.gob.ambiente.suia.recaudaciones.model.DocumentoNUT;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class PagoRcoaBean implements Serializable {

	private static final long serialVersionUID = -2280344496977812691L;
	@EJB
	private DocumentosNUTFacade documentosNUTFacade;
	@EJB
	private PagoKushkiJsonFacade pagoKushkiJsonFacade;
	@Getter
	@Setter
	private List<DocumentoNUT> documentosNUT;
	
	@Getter
	@Setter
	private List<PagoKushkiJson> pagoKushkiJson;

	@Getter
	@Setter
	private TransaccionFinanciera transaccionFinanciera;

	@Getter
	@Setter
	private TransaccionFinanciera transaccionFinancieraCobertura;

	@Getter
	@Setter
	private List<TransaccionFinanciera> transaccionesFinancieras;
	
	@Getter
	@Setter
	private InstitucionFinanciera institucionFinancieraNut;
	
	@Getter
	@Setter
	private List<InstitucionFinanciera> institucionesFinancieras, institucionesFinancierasCobertura, institucionesFinancierasNut;
	
	@Getter
	@Setter
	public double montoTotalProyecto, montoTotalCoberturaVegetal, montoTotalRGD;

	@Getter
	@Setter
	private boolean cumpleMontoProyecto = false, cumpleMontoCobertura=false, cumpleMontoRGD=false;
	
	@Getter
	@Setter
	public Boolean generarNUT, datoFormaspago=true,EsPagoEnLinea=false;
	
	@Getter
	@Setter
	private boolean esEnteAcreditado, cumpleValorRegistro=false, mostrarPnlCobertura,mensajePago=false,validaNutKushki=true,pagaLinea=false,Facilitadores=true, CalcularValorPagar;
	
	@Getter
	@Setter
	private double valorAPagar;
	
	@Getter
	@Setter
	private int categorizacion;

	@Getter
	@Setter
	private String identificadorMotivo, tramite, tipoProyecto, tipoPago, Ciudad, Provincia, Pais, Direccion,TicketNumber,Mensaje_Adicional, Mensaje_Completo;
	@Getter
	@Setter
	private Integer Idproy;
	@Getter
	@Setter
	private double valorTransaccion, valorComision;
	@Getter
	@Setter
	private String Mensaje_Costo_Registro,Mensaje_Costo_Inventario,Mensaje_Facilitadores,montoMinimopago,validacionMontos,montoMaximopago;
	@Getter
	@Setter
	private String TicketPago,EntidadPago,Entidad_Pagada,valproy, vallicamb;
	@Getter
	@Setter
	private String Mensaje_ValidacionT, Mensaje_ValidacionC, url_enlace;
	@Getter
	@Setter
	public Boolean control_salto=false;
	@EJB
	private CatalogoCoaFacade catalogoCoaFacade;

	@PostConstruct
	public void init() {
		Mensaje_Adicional = Mensajes_Carga(21,1);
		Mensaje_Completo =  Mensajes_Carga(22,1);
		Mensaje_Costo_Registro = Mensajes_Carga(24,1);
		Mensaje_Costo_Inventario = Mensajes_Carga(25,1);
		montoMaximopago = Mensajes_Carga(27,2);
		Mensaje_Facilitadores = Mensajes_Carga(28,1);
		montoMinimopago = Mensajes_Carga(29,2);
		Mensaje_ValidacionT = Mensajes_Carga(34,1);
		Mensaje_ValidacionC = Mensajes_Carga(35,1);
		//valproy="0";
		//vallicamb="0";
	}


	
	public String Mensajes_Carga(Integer Orden,Integer Campo)
	{
		 String dato = "";
	     CatalogoGeneralCoa parametro = catalogoCoaFacade.obtenerCatalogoPorTipoYOrden(CatalogoTipoCoaEnum.TIPO_VALOR_KUSHKI, Orden); 
	     if (Campo == 1)
	     {
		     dato = parametro.getDescripcion();	
	     }
	     else if (Campo == 2)
	     {
	    	 dato = parametro.getValor();
	     }
	     return dato;
	}
}
