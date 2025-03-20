package ec.gob.ambiente.control.retce.controllers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.control.retce.beans.DeclaracionGeneradorRetceBean;
import ec.gob.ambiente.retce.model.GeneradorDesechosPeligrososRetce;
import ec.gob.ambiente.retce.model.InformacionProyecto;
import ec.gob.ambiente.retce.services.GeneradorDesechosPeligrososFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.ObservacionesFormularios;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.observaciones.facade.ObservacionesFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class CorreccionesGeneradorRGDController {
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{declaracionGeneradorRetceBean}")
	private DeclaracionGeneradorRetceBean declaracionGeneradorRetceBean;
	
	@EJB
	private GeneradorDesechosPeligrososFacade generadorDesechosPeligrososFacade;
	
	@EJB
	private DocumentosFacade documentosFacade;
	
	@EJB
	private ObservacionesFacade observacionesFacade;
	
	@Getter
	@Setter
	private GeneradorDesechosPeligrososRetce generadorDesechosRetce;
	
	@Getter
	@Setter
	private Documento oficioObservaciones;
	
	@Getter
	@Setter
	private InformacionProyecto informacionProyecto;
	
	@Getter
	@Setter
	private Integer numeroObservaciones;

	@PostConstruct
	public void init() {
		try {
			String codigoGenerador = JsfUtil.getCurrentTask().getVariable("tramite").toString();
			numeroObservaciones = Integer.valueOf(JsfUtil.getCurrentTask().getVariable("numero_observaciones").toString());
			
			generadorDesechosRetce = generadorDesechosPeligrososFacade.getRgdRetcePorCodigo(codigoGenerador);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void iniciarCorreccion() throws ServiceException{
		List<DesechoPeligroso> listaDesechosGenerador = generadorDesechosPeligrososFacade.getDesechosByGenerador(generadorDesechosRetce.getIdGeneradorDesechosPeligrosos());
		
		declaracionGeneradorRetceBean.setGeneradorDesechosRetce(generadorDesechosRetce);
		declaracionGeneradorRetceBean.setListaDesechosGenerador(listaDesechosGenerador);
		declaracionGeneradorRetceBean.setNumeroObservacion(numeroObservaciones);
		
		declaracionGeneradorRetceBean.setEditarGeneracion(false);
		declaracionGeneradorRetceBean.setEditarAutogestion(false);
		declaracionGeneradorRetceBean.setEditarTransporte(false);
		declaracionGeneradorRetceBean.setEditarExportacion(false);
		declaracionGeneradorRetceBean.setEditarEliminacion(false);
		declaracionGeneradorRetceBean.setEditarDisposicion(false);
		
//		getObservaciones(); //se comenta ya no se requere validar q seccion tiene observaciones porque se muestra siempre los formularios de ingreso
		
		JsfUtil.redirectTo("/control/retce/generadorDesechos/generadorDesechosCorrecciones.jsf");
	}
	
	private void getObservaciones() throws ServiceException {
		List<ObservacionesFormularios> observacionesTramite = observacionesFacade.listarPorIdClaseNombreClaseNoCorregidas(
				generadorDesechosRetce.getId(), GeneradorDesechosPeligrososRetce.class.getSimpleName());
		
		LinkedHashMap<String, List<ObservacionesFormularios>> observaciones = new LinkedHashMap<String, List<ObservacionesFormularios>>();

		if (observacionesTramite != null && !observacionesTramite.isEmpty()) {
			for (ObservacionesFormularios observacion : observacionesTramite) {
				if (!observacion.isObservacionCorregida()) {
					if (!observaciones.containsKey(observacion
							.getSeccionFormulario()))
						observaciones.put(observacion.getSeccionFormulario(),
								new ArrayList<ObservacionesFormularios>());
					observaciones.get(observacion.getSeccionFormulario()).add(
							observacion);
				}
			}
		}
		
		Object[] secciones = observaciones.keySet().toArray();
		for (Object seccion : secciones) {
			
			List<ObservacionesFormularios> observacionesSeccion = observaciones.get(seccion);
			if(observacionesSeccion != null && observacionesSeccion.size() > 0){
				switch (seccion.toString()) {
				case "GeneradorRETCE_Generación":
					declaracionGeneradorRetceBean.setEditarGeneracion(true);
					break;
				case "GeneradorRETCE_Autogestión":
					declaracionGeneradorRetceBean.setEditarAutogestion(true);
					break;
				case "GeneradorRETCE_Transporte Medios propios":
					declaracionGeneradorRetceBean.setEditarTransporte(true);
					break;
				case "GeneradorRETCE_Transporte Gestor ambiental":
					declaracionGeneradorRetceBean.setEditarTransporte(true);
					break;
				case "GeneradorRETCE_Exportación":
					declaracionGeneradorRetceBean.setEditarExportacion(true);
					break;
				case "GeneradorRETCE_Eliminación":
					declaracionGeneradorRetceBean.setEditarEliminacion(true);
					break;
				case "GeneradorRETCE_Disposición":
					declaracionGeneradorRetceBean.setEditarDisposicion(true);
					break;
				default:
					break;
				}
			}
		}
		
		
	}
	
}
