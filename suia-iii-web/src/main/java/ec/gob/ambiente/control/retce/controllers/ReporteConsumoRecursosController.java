package ec.gob.ambiente.control.retce.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.retce.model.CatalogoSustanciasRetce;
import ec.gob.ambiente.retce.model.ConsumoAgua;
import ec.gob.ambiente.retce.model.ConsumoCombustible;
import ec.gob.ambiente.retce.model.ConsumoEnergia;
import ec.gob.ambiente.retce.model.ConsumoRecursos;
import ec.gob.ambiente.retce.model.DetalleCatalogoGeneral;
import ec.gob.ambiente.retce.model.GeneradorDesechosPeligrososRetce;
import ec.gob.ambiente.retce.model.InformacionProyecto;
import ec.gob.ambiente.retce.model.SubstanciasRetce;
import ec.gob.ambiente.retce.model.TecnicoResponsable;
import ec.gob.ambiente.retce.model.TipoProcesoConsumo;
import ec.gob.ambiente.retce.services.CatalogoSustanciasRetceFacade;
import ec.gob.ambiente.retce.services.ConsumoRecursosFacade;
import ec.gob.ambiente.retce.services.DetalleCatalogoGeneralFacade;
import ec.gob.ambiente.retce.services.InformacionProyectoFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class ReporteConsumoRecursosController implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
	
	@EJB
	private DocumentosFacade documentosFacade;
	
	@EJB
	private InformacionProyectoFacade informacionProyectoFacade;
	
	@EJB
	private ConsumoRecursosFacade consumoRecursosFacade;
	
	@EJB
	private CatalogoSustanciasRetceFacade catalogoSustanciasRetceFacade;
	
	@EJB
	private DetalleCatalogoGeneralFacade detalleCatalogoGeneralFacade;
	
	@Getter
	@Setter
	private List<ConsumoCombustible> listaConsumoCombustibles;
	
	@Getter
	@Setter
	private List<ConsumoEnergia> listaConsumoEnergia;
	
	@Getter
	@Setter
	private List<ConsumoAgua> listaConsumoAgua;
	
	@Getter
	@Setter
	private List<CatalogoSustanciasRetce> listaSustanciasRetce;
	
	@Getter
	@Setter
	private List<ConsumoRecursos> listaConsumosRecursos;
	
	@Getter
	@Setter
	private List<Integer> listaAnios;
	
	@Getter
	@Setter
	private InformacionProyecto informacionProyecto;
	
	@Getter
	@Setter
	private ConsumoRecursos consumoRecursos;
	
	@Getter
	@Setter
	private ConsumoAgua consumoAgua;
	
	@Getter
	@Setter
	private TecnicoResponsable tecnicoResponsable;
	
	@Getter
	@Setter
	private SubstanciasRetce sustanciaRetce;
	
	@Getter
	@Setter
	private Boolean verDetalleConsumo, agregarNuevo, reporteHabilitado;
	
	@Getter
	@Setter
	private String codOtroTipoCombustible, codOtroTipoSuministro, inicioReporte, finReporte;
	
	@Getter
	@Setter
	private Integer ordenMedicionDirecta = 1;

	@PostConstruct
	private void iniciar() {
		JsfUtil.getLoggedUser().getNombre();
		try {
			
			Integer idInformacionBasica =(Integer)(JsfUtil.devolverObjetoSession(InformacionProyecto.class.getSimpleName()));
			
			if (idInformacionBasica != null) {
				informacionProyecto = informacionProyectoFacade.findById(idInformacionBasica);
				JsfUtil.cargarObjetoSession(InformacionProyecto.class.getSimpleName(), null);
			} else {
				return;
			}
			
			DetalleCatalogoGeneral periodoRegistro = detalleCatalogoGeneralFacade.findByCodigo("periodo.consumo");
			
			Calendar fecha = Calendar.getInstance();
			String fechaActualString = fecha.get(Calendar.DAY_OF_MONTH) + "/" + (fecha.get(Calendar.MONTH) + 1)  + "/" + fecha.get(Calendar.YEAR); //mes se suma 1 xq retorna enero 0
			Date fechaActual = new SimpleDateFormat("dd/MM/yyyy").parse(fechaActualString);
			
			Integer anioActual = JsfUtil.getYearFromDate(fechaActual);
			String inicioString = periodoRegistro.getParametro() + "/" + anioActual;
			String finString = periodoRegistro.getParametro2() + "/" + anioActual;
			Date fechaInicio = new SimpleDateFormat("dd/MM/yyyy").parse(inicioString);
			Date fechaFin = new SimpleDateFormat("dd/MM/yyyy").parse(finString);
			
			if(fechaActual.compareTo(fechaInicio) >= 0 && fechaActual.compareTo(fechaFin) <= 0) 
				reporteHabilitado = true;
			else
				reporteHabilitado = false;
			
			inicioReporte = JsfUtil.devuelveDiaMesEnLetras(fechaInicio);
			finReporte = JsfUtil.devuelveDiaMesEnLetras(fechaFin);
			
			cargarInicio();
			cargarInfoConsumos();
			cargarListaAnios();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void cargarInicio() {
		tecnicoResponsable = new TecnicoResponsable();
		consumoRecursos = (consumoRecursos == null)  ? new ConsumoRecursos() : consumoRecursos;

		verDetalleConsumo = false;
		agregarNuevo = false;
		
		listaConsumosRecursos = new ArrayList<>();
		listaConsumoCombustibles = new ArrayList<>();
		listaConsumoEnergia = new ArrayList<>();
		listaConsumoAgua = new ArrayList<>();
		
		codOtroTipoCombustible = "tipocombustible.otro";
		codOtroTipoSuministro = "tiposuministro.otros";
		
	}
	
	private void cargarInfoConsumos() {
		listaConsumosRecursos = consumoRecursosFacade.getConsumosByInformacionProyecto(informacionProyecto.getId());
	}
	
	private void cargarListaAnios() {
		Date nuevaFecha = new Date();
		Integer i= JsfUtil.getYearFromDate(nuevaFecha);
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.YEAR, -1);
		nuevaFecha = cal.getTime();
		listaAnios = new ArrayList<Integer>();

		for ( i=2019; i<= JsfUtil.getYearFromDate(nuevaFecha); i++){
			listaAnios.add(i);
		}
	}
	
	public void agregarConsumo(){
		if (!reporteHabilitado) {
			JsfUtil.addMessageError("Está fuera del periodo de registro.");
			
			return;
		}
		
		consumoRecursos = new ConsumoRecursos();
		
		verDetalleConsumo = false;
		agregarNuevo = true;
	}
	
	public String seleccionarAnioReporte() {
		validarExisteReporte();
		
		return null;
	}
	
	public String iniciarReporte(){
		
		if(consumoRecursos.getId() == null) {
			if(!validarExisteReporte()) {
				consumoRecursos.setCodigoTramite(consumoRecursosFacade.generarCodigoTramite());
				consumoRecursos.setInformacionProyecto(informacionProyecto);
				consumoRecursos.setRegistroFinalizado(false);
					
				consumoRecursosFacade.guardarConsumo(consumoRecursos);
			}
		}
		
		JsfUtil.cargarObjetoSession(ConsumoRecursos.class.getSimpleName(), consumoRecursos.getId());
		return JsfUtil.actionNavigateTo("/control/retce/consumoRecursos/consumoRecursos.jsf");
	}
	
	public Boolean validarExisteReporte() {
		ConsumoRecursos reporteRecursos =  consumoRecursosFacade.getByInformacionProyectoAnio(informacionProyecto.getId(), consumoRecursos.getAnioDeclaracion());
		
		if(reporteRecursos != null) {
			if (reporteRecursos.getRegistroFinalizado()) {
				JsfUtil.addMessageError("Usted ya generó el reporte de Consumo de Recursos para el año seleccionado.");
				
				JsfUtil.cargarObjetoSession(InformacionProyecto.class.getSimpleName(), informacionProyecto.getId());
				JsfUtil.redirectTo("/control/retce/consumoRecursos.jsf");
			} 
			
			consumoRecursos = reporteRecursos;
			
			return true;
		}
		return false;
	}
	
	public String verInfoReporte(ConsumoRecursos consumo, Integer accion){
		if(accion == 0){
			if(consumo.getCodigoTramite() == null) {
				consumo.setCodigoTramite(consumoRecursosFacade.generarCodigoTramite());
				consumoRecursosFacade.guardarConsumo(consumo);
			}
			
			JsfUtil.cargarObjetoSession(ConsumoRecursos.class.getSimpleName(), consumo.getId());
			return JsfUtil.actionNavigateTo("/control/retce/consumoRecursos/consumoRecursos.jsf");
		} else {
			consumoRecursos = consumo;
			tecnicoResponsable = consumo.getTecnicoResponsable();
			cargarCombustibles();
			cargarConsumoEnergia();
			cargarAprovechamientoAgua();
			
			verDetalleConsumo = true;
			agregarNuevo = false;
			return null;
		}
	}
	
	public void eliminarConsumo(ConsumoRecursos consumo) {
		consumo.setEstado(false);
		consumoRecursosFacade.eliminarConsumo(consumo);
		
		listaConsumosRecursos.remove(consumo);
	}
	
	public void cargarCombustibles() {		
		if(consumoRecursos != null && consumoRecursos.getId() != null) {
			listaConsumoCombustibles = consumoRecursosFacade.getConsumoCombustible(consumoRecursos.getId());
			
			if(listaConsumoCombustibles != null){
				for (ConsumoCombustible consumo : listaConsumoCombustibles) {
					List<DetalleCatalogoGeneral> listaProcesos = new ArrayList<>();
					List<TipoProcesoConsumo> tiposProceso = consumo.getListaTipoProcesos();
					for (TipoProcesoConsumo tipoProcesoConsumo : tiposProceso) {
						listaProcesos.add(tipoProcesoConsumo.getTipoProceso());
					}
					
					consumo.setListaProcesos(listaProcesos);

					List<Documento> documentos = documentosFacade
							.documentoXTablaIdXIdDoc(
									consumo.getId(),
									ConsumoCombustible.class.getSimpleName(),
									TipoDocumentoSistema.DOCUMENTO_MEDIO_VERIFICACION);
					if (documentos.size() > 0)
						consumo.setListaMediosVerificacion(documentos);
				}
			} else 
				listaConsumoCombustibles = new ArrayList<>();
		}
	}
	
	public void cargarConsumoEnergia() {
		listaSustanciasRetce = catalogoSustanciasRetceFacade.findAll();
		
		if(consumoRecursos != null && consumoRecursos.getId() != null) {
			listaConsumoEnergia = consumoRecursosFacade.getConsumoEnergia(consumoRecursos.getId());
			
			if(listaConsumoEnergia != null){
				for (ConsumoEnergia consumo : listaConsumoEnergia) {
					List<DetalleCatalogoGeneral> listaProcesos = new ArrayList<>();
					List<TipoProcesoConsumo> tiposProceso = consumo.getListaTipoProcesos();
					for (TipoProcesoConsumo tipoProcesoConsumo : tiposProceso) {
						listaProcesos.add(tipoProcesoConsumo.getTipoProceso());
					}
					
					consumo.setListaProcesos(listaProcesos);

					List<Documento> documentos = documentosFacade
							.documentoXTablaIdXIdDoc(
									consumo.getId(),
									ConsumoEnergia.class.getSimpleName(),
									TipoDocumentoSistema.DOCUMENTO_MEDIO_VERIFICACION);
					if (documentos.size() > 0)
						consumo.setListaMediosVerificacion(documentos);
				}
			} else
				listaConsumoEnergia = new ArrayList<>();
		}
	}
	
	public void cargarAprovechamientoAgua() {
		if(consumoRecursos != null && consumoRecursos.getId() != null) {
			listaConsumoAgua = consumoRecursosFacade.getAprovechamientoAgua(consumoRecursos.getId());
			
			if(listaConsumoAgua != null){
				for (ConsumoAgua consumo : listaConsumoAgua) {
					List<DetalleCatalogoGeneral> listaProcesos = new ArrayList<>();
					List<TipoProcesoConsumo> tiposProceso = consumo.getListaTipoProcesos();
					for (TipoProcesoConsumo tipoProcesoConsumo : tiposProceso) {
						listaProcesos.add(tipoProcesoConsumo.getTipoProceso());
					}
					
					consumo.setListaProcesos(listaProcesos);

					List<Documento> documentos = documentosFacade
							.documentoXTablaIdXIdDoc(
									consumo.getId(),
									ConsumoAgua.class.getSimpleName(),
									TipoDocumentoSistema.DOCUMENTO_MEDIO_VERIFICACION);
					if (documentos.size() > 0)
						consumo.setListaMediosVerificacion(documentos);
					
					if(consumo.getTipoFuente().getParametro().equals("1")) {
						List<Documento> resolucion = documentosFacade
								.documentoXTablaIdXIdDoc(
										consumo.getId(),
										ConsumoAgua.class.getSimpleName(),
										TipoDocumentoSistema.RESOLUCION_APROVECHAMIENTO_AGUA);
						if (resolucion.size() > 0)
							consumo.setResolucionAprovechamiento(resolucion.get(0));
					}
				}
			} else
				listaConsumoAgua = new ArrayList<>();
		}
	}
	
	public StreamedContent descargar(Documento documento) throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			
			if (documento != null && documento.getIdAlfresco() != null) {
				documentoContent = documentosFacade.descargar(documento.getIdAlfresco());
			} else if (documento.getContenidoDocumento() != null) {
				documentoContent = documento.getContenidoDocumento();
			}
			
			if (documento != null && documento.getNombre() != null
					&& documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documento.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public Double getTotalConsumoEnergia(){
		Double total =  0.0;
		if(listaConsumoEnergia != null && listaConsumoEnergia.size() > 0){
			for (ConsumoEnergia consumo : listaConsumoEnergia) {
				total += consumo.getValorAnual();
			}
		}
		
		return total;
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
	
	public void verFuentesAprovechamiento(ConsumoAgua aprovechamientoAgua) {
		consumoAgua = aprovechamientoAgua;
	}
	
	public Double getTotalConsumoAgua(){
		Double total =  0.0;
		if(listaConsumoAgua != null && listaConsumoAgua.size() > 0){
			for (ConsumoAgua consumo : listaConsumoAgua) {
				total += consumo.getConsumoAnual();
			}
		}
		
		return total;
	}
	
	public List<SubstanciasRetce> getListaSustanciasRetceEnergia(){
		List<SubstanciasRetce> listaSustanciasRetceEnergia = new ArrayList<>();
		
		for (ConsumoEnergia consumo : listaConsumoEnergia) {
			listaSustanciasRetceEnergia.addAll(consumo.getListaSustanciasRetce());
		}		
		
		return listaSustanciasRetceEnergia;
	}
	
	public void cerrarConsumo() {
		verDetalleConsumo = false;
	}
	
	public void cerrarNuevoConsumo() {
		agregarNuevo = false;
	}
	
	public void validarData() {
		if (informacionProyecto == null) {
			JsfUtil.redirectTo("/control/retce/informacionBasica.jsf");
		}
	}
}
