package ec.gob.ambiente.control.retce.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.control.retce.beans.DisposicionDesechosRetceBean;
import ec.gob.ambiente.control.retce.beans.EliminacionDesechosRetceBean;
import ec.gob.ambiente.control.retce.beans.ExportacionDesechosBean;
import ec.gob.ambiente.control.retce.beans.TransporteDesechosBean;
import ec.gob.ambiente.retce.model.DatosLaboratorio;
import ec.gob.ambiente.retce.model.DesechoAutogestion;
import ec.gob.ambiente.retce.model.DesechoEliminacionAutogestion;
import ec.gob.ambiente.retce.model.DesechoGeneradoEliminacion;
import ec.gob.ambiente.retce.model.GeneradorDesechosPeligrososRetce;
import ec.gob.ambiente.retce.model.IdentificacionDesecho;
import ec.gob.ambiente.retce.model.InformacionProyecto;
import ec.gob.ambiente.retce.model.SubstanciasRetce;
import ec.gob.ambiente.retce.model.TecnicoResponsable;
import ec.gob.ambiente.retce.services.DatosLaboratorioFacade;
import ec.gob.ambiente.retce.services.DesechoAutogestionFacade;
import ec.gob.ambiente.retce.services.GeneradorDesechosPeligrososFacade;
import ec.gob.ambiente.retce.services.IdentificacionDesechosFacade;
import ec.gob.ambiente.retce.services.InformacionProyectoFacade;
import ec.gob.ambiente.retce.services.SubstanciasRetceFacade;
import ec.gob.ambiente.retce.services.TecnicoResponsableFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class GeneradorDesechosPeligrososVerController {
	
	private final Logger LOG = Logger.getLogger(GeneradorDesechosPeligrososVerController.class);

	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;
	
	@EJB
	private GeneradorDesechosPeligrososFacade generadorDesechosPeligrososFacade;
	
	@EJB
	private DocumentosFacade documentosFacade;
	
	@EJB
	private InformacionProyectoFacade informacionProyectoFacade;
	
	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;
	
	@EJB
	private IdentificacionDesechosFacade identificacionDesechosFacade;
	
	@EJB
	private DesechoAutogestionFacade desechoAutogestionFacade;
	
	@EJB
	private SubstanciasRetceFacade substanciasRetceFacade;
	
	@EJB
	private DatosLaboratorioFacade datosLaboratorioFacade;
	
	@EJB
    private TecnicoResponsableFacade tecnicoResponsableFacade;
	
	@Getter
	@Setter
	private List<IdentificacionDesecho> listaIdentificacionDesechos, listaIdentificacionDesechosHistorial;
	
	@Getter
	@Setter
	private List<DesechoAutogestion> listaDesechosAutogestion, listaHistorialDesechosEliminados;
	
	@Getter
	@Setter
	private List<DesechoGeneradoEliminacion> listaDesechosGeneradosPorEliminacion, listaDesechosGeneradosHistorial;
	
	@Getter
	@Setter
	private List<GeneradorDesechosPeligrososRetce> listaAutogestionHistorial, listaRealizaAutogestionHistorial;
	
	@Getter
	@Setter
	private List<Documento> listaDocumentosHistorial, listaDocumentosAutorizacionHistorial;
	
	@Getter
	@Setter
	private List<DesechoEliminacionAutogestion> listaDesechoEliminacionHistorial;
	
	@Getter
	@Setter
	private List<TecnicoResponsable> listaTecnicoHistorial;
	
	@Getter
	@Setter
	private InformacionProyecto informacionProyecto;
	
	@Getter
	@Setter
	private GeneradorDesechosPeligrososRetce generadorDesechosRetce;
	
	@Getter
	@Setter
	private TecnicoResponsable tecnicoResponsable;
	
	@Getter
	@Setter
	private DesechoEliminacionAutogestion desechoEliminadoPorAutogestion, desechoEliminadoSeleccionado;
	
	@Getter
	@Setter
	private SubstanciasRetce sustanciaRetce, sustanciaRetceSeleccionada;
	
	@Getter
	@Setter
	private Documento documentoObservaciones, documentoAutorizacion, informeMonitoreo, documentoAclaraciones;
	
	@Getter
	@Setter
	private String tipoDesechoPeligroso = "tipodesecho.peligroso";
	
	@Getter
	@Setter
	private Boolean existeReporteUnidades, esDesechoEspecialUnidades;
	
	@Getter
	@Setter
	private Boolean habilitarObservaciones, observacionesSoloLectura, isTecnicoAutenticado, verHistorial, mostrarColumnaHistorial;
	
	@Getter
	@Setter
	private Integer numeroObservaciones;
	
	@Getter
	@Setter
	private Integer ordenMedicionDirecta = 1;

	@PostConstruct
	public void init() {
		try {
			Boolean verObservaciones = true;
			isTecnicoAutenticado = false;
			verHistorial = false;
			mostrarColumnaHistorial = false;
			numeroObservaciones = 0;
			
			if(JsfUtil.getCurrentTask() != null) {
				String codigoGenerador = JsfUtil.getCurrentTask().getVariable("tramite").toString();
				generadorDesechosRetce = generadorDesechosPeligrososFacade.getRgdRetcePorCodigo(codigoGenerador);
				
				if(JsfUtil.getCurrentTask().getVariable("numero_observaciones") != null)
					numeroObservaciones = Integer.valueOf(JsfUtil.getCurrentTask().getVariable("numero_observaciones").toString());					
				
			} else {
				Integer idGenerador =(Integer)(JsfUtil.devolverObjetoSession(GeneradorDesechosPeligrososRetce.class.getSimpleName()));
				JsfUtil.cargarObjetoSession(GeneradorDesechosPeligrososRetce.class.getSimpleName(), null);
				verObservaciones = false;
				if(idGenerador != null)
					generadorDesechosRetce = generadorDesechosPeligrososFacade.getRgdRetcePorID(idGenerador);
			}			
			
			if(generadorDesechosRetce == null){
				JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
			} else{
				//para observaciones
				if(verObservaciones) {
					if (!JsfUtil.getCurrentTask().getTaskName().contains("Ingresar Informacion")) {
						verHistorial = true;
					}
					
					String tecnico = JsfUtil.getCurrentTask().getVariable("usuario_tecnico").toString();
					if(tecnico != null && loginBean.getUsuario().getNombre().equals(tecnico))
						isTecnicoAutenticado = true;
					
					if (!isTecnicoAutenticado) {
						habilitarObservaciones = true;
						observacionesSoloLectura = true;
					} else {
						habilitarObservaciones = true;
						observacionesSoloLectura = false;
					}
				} else {
					habilitarObservaciones = false;
					observacionesSoloLectura = false;
				}
				
				informacionProyecto = generadorDesechosRetce.getInformacionProyecto();
				
				tecnicoResponsable = generadorDesechosRetce.getTecnicoResponsable();
				
				if(verHistorial) {
					tecnicoResponsable = tecnicoResponsableFacade.findById(tecnicoResponsable.getId());
					listaTecnicoHistorial = tecnicoResponsable.getHistorialLista();
				}
				
				List<Documento> documentos = documentosFacade.documentoXTablaIdXIdDoc(
						generadorDesechosRetce.getId(),
						GeneradorDesechosPeligrososRetce.class.getSimpleName(),
						TipoDocumentoSistema.DOCUMENTO_OBSERVACIONES_GENERADOR);
				if (documentos.size() > 0)
					documentoAclaraciones = documentos.get(0);
				
				cargarIdentificacion();
				cargarDatosAutogestion();
//				cargarTransporte();
//				cargarExportacion();
//				cargarEliminacion();
//				cargarDisposicion();				
			}

		} catch (Exception e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos de la declaración de generador.");
		}
	}
	
	public void onTabChange(TabChangeEvent event) {
		String currentStep = event.getTab().getId();
		
		if (currentStep == null || currentStep.equals("paso2")){
//        	cargarDatosAutogestion(); //se elimina xq los datos de autogestion se cargan al inicio
        } else if (currentStep.equals("paso3")){
        	cargarTransporte();
        } else if (currentStep.equals("paso4")){
        	cargarExportacion();
        } else if (currentStep.equals("paso5")){
        	cargarEliminacion();
        } else if (currentStep.equals("paso6")){
        	cargarDisposicion();
        }
    }
	
	public StreamedContent descargar(Documento documento) throws IOException {
		DefaultStreamedContent content = null;
		try {
			if (documento.getIdAlfresco() != null) {
				byte[] documentoContent = documentosFacade.descargar(documento.getIdAlfresco());
				
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documento.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void cargarIdentificacion(){
		listaIdentificacionDesechos = identificacionDesechosFacade.getIdentificacionDesechosPorRgdRetce(generadorDesechosRetce.getId());
		
		existeReporteUnidades = false;
		for (IdentificacionDesecho desecho : listaIdentificacionDesechos) {
			if(desecho.getDesechoPeligroso().isDesechoES_04() ||
					desecho.getDesechoPeligroso().isDesechoES_06()){
				existeReporteUnidades = true;
				break;
			}
		}
		
		if(verHistorial)
			cargarHistorialIdentificacion();
	}
	
	public void cargarDatosAutogestion() {
		if (generadorDesechosRetce != null && generadorDesechosRetce.getId() != null && generadorDesechosRetce.getRealizaAutogestion() != null) {
			List<Documento> documentosAutorizacion = null;
			if (generadorDesechosRetce.getRealizaAutogestion()) {
				listaDesechosAutogestion = desechoAutogestionFacade.getListaDesechosAutogestion(generadorDesechosRetce.getId());

				documentosAutorizacion = documentosFacade.documentoXTablaIdXIdDoc(
								generadorDesechosRetce.getId(),
								GeneradorDesechosPeligrososRetce.class.getSimpleName(),
								TipoDocumentoSistema.DOCUMENTO_AUTORIZACION_AUTOGESTION);
				if (documentosAutorizacion.size() > 0)
					documentoAutorizacion = documentosAutorizacion.get(0);
			}
			
			if(verHistorial)
				cargarHistorialAutogestion(documentosAutorizacion);
		}
	}
	
	public void verDesechosGenerados(DesechoEliminacionAutogestion desecho){
		listaDesechosGeneradosPorEliminacion = new ArrayList<>();
		
		desechoEliminadoPorAutogestion = desecho;
		listaDesechosGeneradosPorEliminacion = desecho.getListaDesechosGeneradosPorEliminacion();
	}
	
	public void verDatosLaboratorio(SubstanciasRetce sustancia){
		sustanciaRetce = sustancia;
		
		List<Documento> documentosManifiesto = documentosFacade.documentoXTablaIdXIdDoc(
				sustancia.getDatosLaboratorio().getId(),
				GeneradorDesechosPeligrososRetce.class.getSimpleName(),
				TipoDocumentoSistema.TIPO_DOCUMENTO_INFORME_LABORATORIO);
		if (documentosManifiesto.size() > 0)
			sustanciaRetce.getDatosLaboratorio().setDocumentoLaboratorio(documentosManifiesto.get(0));
	}
	
	public void cargarTransporte() {
		if (generadorDesechosRetce != null && generadorDesechosRetce.getId() != null) {
			JsfUtil.getBean(TransporteDesechosBean.class).setGeneradorDesechosRetce(generadorDesechosRetce);
			JsfUtil.getBean(TransporteDesechosBean.class).cargarDatos();
			
			if(verHistorial)
				JsfUtil.getBean(TransporteDesechosBean.class).cargarDatosHistorial(numeroObservaciones);
		}
	}
	
	public void cargarExportacion() {
		if (generadorDesechosRetce != null && generadorDesechosRetce.getId() != null) {
			JsfUtil.getBean(ExportacionDesechosBean.class).setGeneradorDesechosRetce(generadorDesechosRetce);
			JsfUtil.getBean(ExportacionDesechosBean.class).cargarDatos();
			
			if(verHistorial)
				JsfUtil.getBean(ExportacionDesechosBean.class).cargarDatosHistorial(numeroObservaciones);
		}
	}
	
	public void cargarEliminacion() {
		if (generadorDesechosRetce != null && generadorDesechosRetce.getId() != null) {
			JsfUtil.getBean(EliminacionDesechosRetceBean.class).setGeneradorDesechosRetce(generadorDesechosRetce);
			JsfUtil.getBean(EliminacionDesechosRetceBean.class).cargarDatos();
			
			if(verHistorial)
				JsfUtil.getBean(EliminacionDesechosRetceBean.class).cargarDatosHistorial(numeroObservaciones);
		}
	}
	
	public void cargarDisposicion() {
		if (generadorDesechosRetce != null && generadorDesechosRetce.getId() != null) {
			JsfUtil.getBean(DisposicionDesechosRetceBean.class).setGeneradorDesechosRetce(generadorDesechosRetce);
			JsfUtil.getBean(DisposicionDesechosRetceBean.class).cargarDatos();
			
			if(verHistorial)
				JsfUtil.getBean(DisposicionDesechosRetceBean.class).cargarDatosHistorial(numeroObservaciones);
		}
	}
	
	//para observaciones
	public String getClassName() {
		return GeneradorDesechosPeligrososRetce.class.getSimpleName();
	}
	
	
	//para historial
	public void cargarHistorialIdentificacion() {
		for (IdentificacionDesecho desecho : listaIdentificacionDesechos) {
			List<IdentificacionDesecho> historiales = identificacionDesechosFacade
					.getIdentificacionDesechosHistorialPorIdIdentificacion(desecho.getId());
			if(historiales != null)
				mostrarColumnaHistorial = true;
			
			desecho.setListaHistorial(historiales);
		}
	}
	
	public void verHistorialIdentificacion (IdentificacionDesecho desecho) {
		if(desecho.getDesechoPeligroso().isDesechoES_04() || desecho.getDesechoPeligroso().isDesechoES_06())
			esDesechoEspecialUnidades = true;
		else 
			esDesechoEspecialUnidades = false;
		
		listaIdentificacionDesechosHistorial = desecho.getListaHistorial();
	}
	
	public void cargarHistorialAutogestion(List<Documento> documentosAutorizacion) {
		List<GeneradorDesechosPeligrososRetce> historiales = generadorDesechosPeligrososFacade
				.getRgdRetceHistorialPorID(generadorDesechosRetce.getId());
		
		if(historiales != null) {
			listaAutogestionHistorial = new ArrayList<>();
			listaRealizaAutogestionHistorial = new ArrayList<>();
			
			for (GeneradorDesechosPeligrososRetce generadorRetce : historiales) {
				if(!generadorRetce.getNumeroAutorizacion().equals(generadorDesechosRetce.getNumeroAutorizacion()))
					listaAutogestionHistorial.add(generadorRetce);
				if(!generadorRetce.getRealizaAutogestion().equals(generadorDesechosRetce.getRealizaAutogestion()))
					listaRealizaAutogestionHistorial.add(generadorRetce);
			}
		}
		
		listaDocumentosAutorizacionHistorial = new ArrayList<>();
		if (documentosAutorizacion != null) {
			for (Documento documento : documentosAutorizacion) {
				if(documento.getIdHistorico() != null)
					listaDocumentosAutorizacionHistorial.add(documento);
			}
		}
		
		//historial desechos
		if(listaDesechosAutogestion != null) {
			for (DesechoAutogestion desechoAutogestion : listaDesechosAutogestion) {
				Boolean mostrarColumnaHistorial = false;
				for (DesechoEliminacionAutogestion desechoEliminado : desechoAutogestion.getListaDesechosEliminacionAutogestion()) {
					Boolean mostrarHistorialDesechoEliminacion = false;
					
					if(desechoEliminado.getNumeroObservacion() != null && 
							desechoEliminado.getNumeroObservacion().equals(numeroObservaciones)) {
						desechoEliminado.setNuevoEnModificacion(true);
						mostrarColumnaHistorial = true;
					} else {
						List<DesechoEliminacionAutogestion> historialDesechoEliminado = desechoAutogestionFacade.getDesechoEliminacionHistorial(desechoEliminado.getId());
						if(historialDesechoEliminado != null) {
							mostrarHistorialDesechoEliminacion = true;
							mostrarColumnaHistorial = true;
						}
						
						List<DesechoGeneradoEliminacion> historialDesechoGenerado = new ArrayList<DesechoGeneradoEliminacion>();
						
						if(desechoEliminado.getListaDesechosGeneradosPorEliminacion() != null && 
								desechoEliminado.getListaDesechosGeneradosPorEliminacion().size() > 0) {
							for (DesechoGeneradoEliminacion desechoGenerado : desechoEliminado.getListaDesechosGeneradosPorEliminacion()) {
								if(desechoGenerado.getNumeroObservacion() == numeroObservaciones) {
									desechoGenerado.setNuevoEnModificacion(true);
									historialDesechoGenerado.add(desechoGenerado);
								} else {
									List<DesechoGeneradoEliminacion> historialPorDesechoGenerado = desechoAutogestionFacade.getHistorialDesechoGenerado(desechoGenerado.getId());
									if(historialPorDesechoGenerado != null){
										historialDesechoGenerado.addAll(historialPorDesechoGenerado);
										mostrarHistorialDesechoEliminacion = true;
										mostrarColumnaHistorial = true;
									}
								}
							}
						}
						
						desechoEliminado.setMostrarHistorial(mostrarHistorialDesechoEliminacion);
						desechoEliminado.setListaHistorialDesechoGenerado(historialDesechoGenerado);
						desechoEliminado.setListaHistorial(historialDesechoEliminado);
					}
				}
				
				desechoAutogestion.setMostrarHistorialDesecho(mostrarColumnaHistorial);
				
				//historial sustancias RETCE
				Boolean mostrarColumnaHistorialSustancia = false;
				for (SubstanciasRetce substanciasRetce : desechoAutogestion.getListaSustanciasRetce()) {
					Boolean mostrarHistorialSustancia = false;
					if(substanciasRetce.getNumeroObservacion() != null && 
							substanciasRetce.getNumeroObservacion().equals(numeroObservaciones)) {
						substanciasRetce.setNuevoEnModificacion(true);
						mostrarColumnaHistorialSustancia = true;
					} else {
						List<SubstanciasRetce> historialSustancia = substanciasRetceFacade.getHistorialSustanciaPorID(substanciasRetce.getId());
						if(historialSustancia != null) {
							mostrarColumnaHistorialSustancia = true;
							mostrarHistorialSustancia = true;
						}
						
						substanciasRetce.setMostrarHistorial(mostrarHistorialSustancia);
						substanciasRetce.setListaHistorial(historialSustancia);
					}
					
					//historial laboratorios
					if(substanciasRetce.getDatosLaboratorio() != null && substanciasRetce.getDatosLaboratorio().getId() != null) {
						List<DatosLaboratorio> historialLaboratorio = datosLaboratorioFacade.getHistorialLaboratorioPorID(substanciasRetce.getDatosLaboratorio().getId());
						
						if(historialLaboratorio != null){
							substanciasRetce.setListaHistorialLaboratorio(historialLaboratorio);
						}
					}
				}
				
				desechoAutogestion.setMostrarHistorialSustancias(mostrarColumnaHistorialSustancia);
			}
		}
		
		listaHistorialDesechosEliminados = desechoAutogestionFacade.getListaDesechosAutogestionEliminados(generadorDesechosRetce.getId());
		if(listaHistorialDesechosEliminados != null) {
			for (DesechoAutogestion desecho : listaHistorialDesechosEliminados) {
				List<DesechoEliminacionAutogestion> desechosEliminacionAutogestion = desechoAutogestionFacade.getDesechosEliminacionPorDesecho(desecho.getIdRegistroOriginal());
				if(desechosEliminacionAutogestion != null) {
					for (DesechoEliminacionAutogestion desechoEliminacion : desechosEliminacionAutogestion) {
						if(desechoEliminacion.getGeneraDesecho()) {
							List<DesechoGeneradoEliminacion> desechosGenerados = desechoAutogestionFacade.getDesechoGeneradoPorEliminacion(desechoEliminacion.getIdRegistroOriginal());
							desechoEliminacion.setListaDesechosGeneradosPorEliminacion(desechosGenerados);
						}
					}
				}
				
				desecho.setListaDesechosEliminacionAutogestion(desechosEliminacionAutogestion);
				
				//sustancias RETCE
				List<SubstanciasRetce> listaSustancias = substanciasRetceFacade.getSustanciasEliminadasPorIdAutogestion(desecho.getIdRegistroOriginal());
				desecho.setListaSustanciasRetce(listaSustancias);
			}
		}
	}
	
	public void verHistorialDocumentos(Integer fuenteDocumentos) {
		switch (fuenteDocumentos) {
		case 1:
			listaDocumentosHistorial = listaDocumentosAutorizacionHistorial;
			break;

		default:
			listaDocumentosHistorial = null;
			break;
		}
		
	}
	
	public void verHistorialDesechoEliminado(DesechoEliminacionAutogestion desechoEliminado) {
		desechoEliminadoSeleccionado = desechoEliminado;
	}
	
	public void verHistorialSustancias(SubstanciasRetce sustancia) {
		sustanciaRetceSeleccionada = sustancia;
	}
}
