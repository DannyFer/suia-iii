package ec.gob.ambiente.suia.recaudaciones.bean;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@ToString
public class ResponseData implements Serializable{
	
	private static final long serialVersionUID = 1L;
	@Getter
	@Setter
	private String kushkiToken;
	@Getter
	@Setter
	private String kushkiPaymentMethod;
	@Getter
	@Setter
	private String valor_pago;
	@Getter
	@Setter
    private String PrivateKey;
	@Getter
	@Setter
    private String Json_Token;	
	@Getter
	@Setter	
	private String usuario_token;
	@Getter
	@Setter
	private String tramite_nombre;
	@Getter
	@Setter	
	private String Email;
	@Getter
	@Setter	
	private String Telefono;
	@Getter
	@Setter	
	private String CI;
	@Getter
	@Setter		
	private String amount;	
	@Getter
	@Setter		
	private String keytoken;
	@Getter
	@Setter		
	private String methodpay;
	@Getter
	@Setter		
	private String metadata;
	@Getter
	@Setter		
	private String usuario;
	@Getter
	@Setter		
	private String tramite;
	@Getter
	@Setter		
	private String valor_p_t;
	@Getter
	@Setter		
	private String usuario_t;
	@Getter
	@Setter		
	private String tramite_t;
	@Getter
	@Setter		
	private String valor_pagar_original;
	@Getter
	@Setter		
	private String valor_transaccion;
	@Getter
	@Setter		
	private String comision_servicio;
	@Getter
	@Setter		
	private String codigo_financiero;
	@Getter
	@Setter		
	private String tipo_proyecto;
	@Getter
	@Setter		
	private String codigo_proyecto;	
	@Getter
	@Setter		
	private String numero_celular;
	///////////////////////////////////////////////
	@Getter
	@Setter		
	private String price;
	@Getter
	@Setter		
	private String tipo_documento;
	@Getter
	@Setter		
	private String iva;
	@Getter
	@Setter		
	private String ice;
	@Getter
	@Setter		
	private String tipo_moneda;
	@Getter
	@Setter		
	private String pagina_web;
	@Getter
	@Setter		
	private String direccion2;
	@Getter
	@Setter		
	private String cantidad;
	@Getter
	@Setter		
	private String Estado_Response;
	@Getter
	@Setter		
	private String urlK;
	
	//////////////////////////
	@Getter
	@Setter	
	private String usuarioPago;
	@Getter
	@Setter		
	private String contrasena;
	@Getter
	@Setter		
	private String nutCodigo;
	@Getter
	@Setter		
	private String valorPago;
	@Getter
	@Setter		
	private String numeroTramite;
	@Getter
	@Setter		
	private String fechaPago;
	@Getter
	@Setter		
	private String codigoRespuesta;
	@Getter
	@Setter		
	private String mensaje;	
}

