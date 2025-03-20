package ec.gob.ambiente.suia.recaudaciones.controllers;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.DocumentosRgdRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.PermisoRegistroGeneradorDesechosFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosProyectosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.DocumentosRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.PermisoRegistroGeneradorDesechos;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosProyectosRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosRcoa;
import ec.gob.ambiente.retce.services.GeneradorDesechosPeligrososFacade;
import ec.gob.ambiente.suia.domain.GeneradorDesechosPeligrosos;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.dto.ProyectoCustom;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ViewScoped
@ManagedBean
public class LiberarPagosController implements Serializable{

	private static final long serialVersionUID = 4689463316622237704L;
		
	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@Getter
	@Setter
	private String codigoTramite;
	
	@Getter
	@Setter
	private String motivo, asociado, codigoRGD, codigoPago, tramite;
	
	@Getter
	@Setter
	private Boolean deshabilitado = true;
	
	@Getter
	@Setter
	private Boolean deshabilitado1 = false;
	
	@Getter
	@Setter
	private String motivoLiberacion;
	
	@Getter
	@Setter
	private String fechaLiberacion;
	
	@Getter
	@Setter
	private String fechaRegistro, asociadoPro, proyecto, resultado;
	
	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoFacade;
	
	
	@PostConstruct
	public void init(){
		
	}
	
	public void validarPago(){
		try{
			motivoLiberacion = "";
			fechaLiberacion = "";
			fechaRegistro = "";
			
			String proyecto = proyectoLicenciaCoaFacade.buscarPago(codigoPago, tramite);
			String resultado = validacion (proyecto);
			if(!proyecto.equals("i") && resultado.equals("true")) {
				JsfUtil.addMessageInfo("El Pago cumple con las validaciones de liberación de pago");
				deshabilitado = false;
			}else{
				JsfUtil.addMessageError("El Pago no existe o no cumple con las validaciones de liberación de pago");		
				deshabilitado = true;
			}			
		}catch(Exception e){
			e.printStackTrace();			
		}
	}
	
	public void liberar() {
		try {		
			
			String pago = proyectoLicenciaCoaFacade.buscarPago(codigoPago, tramite);
			if(pago != null) {
				String ip;
				ip = InetAddress.getLocalHost().toString();
				String resultadopago=proyectoLicenciaCoaFacade.liberarpago(codigoPago, tramite, ip, motivo);
				if (resultadopago=="1") {
					
					JsfUtil.addMessageInfo("Se ha liberado el pago: "+codigoPago);
				}else {
					JsfUtil.addMessageError("No se puede liberar el pago, el proyecto esta activo");
				}
			}
		} catch (Exception e) {
		
			
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_CARGAR_DATOS);
		}
		deshabilitado = true;
		codigoPago = "";
		motivo="";
		asociado="";
	}	
	
	public String validacion(String codigoPago){
		try{
		ProyectoCustom proyectoRcoa = new ProyectoCustom();	
		proyectoRcoa = proyectoLicenciamientoFacade.buscaProyectoRcoa(codigoPago);
		if (proyectoRcoa !=null){
		if ((proyectoRcoa.getCategoria().equals("2") && proyectoRcoa.getFinalizado().equals("false"))&&proyectoRcoa.getEstado().equals("false")){
			resultado = "true";	
		}else{
			if (proyectoRcoa.getEstado().equals("false") && proyectoRcoa.getFinalizado().equals("false")){			
				resultado = "true";
			}	
			return resultado = "false";
		}
	}else{
		ProyectoCustom proyectoCoa = new ProyectoCustom();
		proyectoCoa = proyectoLicenciamientoFacade.buscarProyectoCoa(codigoPago);
		if (proyectoCoa !=null){
		if ((proyectoCoa.getCategoria().equals("2") && proyectoCoa.getFinalizado().equals("false"))&&proyectoCoa.getEstado().equals("false"))
		{
			resultado = "true";	
		}else{
			if (proyectoRcoa.getEstado().equals("false") && proyectoRcoa.getFinalizado().equals("false")){			
				resultado = "true";
			}	
			return resultado = "false";
		}
	}
		resultado = "false";
	}
		}catch(Exception e){
			e.printStackTrace();			
		}
		return resultado;
	}

	public void cancelar(){
		deshabilitado = true;
		motivo = "";
		tramite = "";
		codigoPago = "";
	}
	

}
