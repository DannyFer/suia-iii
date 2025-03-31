package ec.gob.ambiente.rcoa.proyecto.controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import org.jfree.util.Log;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.certificado.interseccion.facade.CertificadoInterseccionCoaFacade;
import ec.gob.ambiente.rcoa.certificado.interseccion.model.CertificadoInterseccionOficioCoa;
import ec.gob.ambiente.rcoa.facade.CoordenadasProyectoCoaFacade;
import ec.gob.ambiente.rcoa.facade.CriterioMagnitudFacade;
import ec.gob.ambiente.rcoa.facade.DetalleInterseccionProyectoAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.facade.GestionarProductosQuimicosProyectoAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoCoaCiuuSubActividadesFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaAmbientalCoaCiuuBloquesFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaAmbientalCoaShapeFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaAmbientalConcesionesMinerasFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.facade.SubActividadesFacade;
import ec.gob.ambiente.rcoa.facade.VariableCriterioFacade;
import ec.gob.ambiente.rcoa.model.CatalogoCIUU;
import ec.gob.ambiente.rcoa.model.CoordenadasProyecto;
import ec.gob.ambiente.rcoa.model.CriterioMagnitud;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.GestionarProductosQuimicosProyectoAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoCoaCiuuSubActividades;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaAmbientalCoaCiuuBloques;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaAmbientalCoaShape;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaAmbientalConcesionesMineras;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.model.SubActividades;
import ec.gob.ambiente.rcoa.model.ValorMagnitud;
import ec.gob.ambiente.rcoa.model.VariableCriterio;
import ec.gob.ambiente.rcoa.preliminar.contoller.ResiduosActividadesCiiuBean;
import ec.gob.ambiente.rcoa.preliminar.contoller.SubActividadesCiiuBean;
import ec.gob.ambiente.rcoa.util.CoordendasPoligonos;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.domain.CategoriaFlujo;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.SustanciaQuimicaPeligrosa;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.Tarea;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@SessionScoped
public class VerProyectoRcoaBean implements Serializable {

	private static final long serialVersionUID = -6963992638158983944L;
	
    @Getter
    @Setter
    @ManagedProperty(value = "#{proyectosBean}")
    private ProyectosBean proyectosBean;
    
    @ManagedProperty(value = "#{subActividadesCiiuBean}")
	@Getter
	@Setter
	private SubActividadesCiiuBean subActividadesCiiuBean;
    
    @ManagedProperty(value = "#{residuosActividadesCiiuBean}")
	@Getter
	@Setter
	private ResiduosActividadesCiiuBean residuosActividadesCiiuBean;

	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	@EJB
	private ProyectoLicenciaAmbientalCoaShapeFacade proyectoLicenciaAmbientalCoaShapeFacade;
	@EJB
	private CoordenadasProyectoCoaFacade coordenadasProyectoCoaFacade;
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
	@EJB
	private CriterioMagnitudFacade criterioMagnitudFacade;
	@EJB
	private VariableCriterioFacade variableCriterioFacade;
	@EJB
	private GestionarProductosQuimicosProyectoAmbientalFacade gestionarProductosQuimicosProyectoAmbientalFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private SubActividadesFacade subActividadesFacade;
	@EJB
	private ProyectoLicenciaAmbientalConcesionesMinerasFacade proyectoLicenciaAmbientalConcesionesMinerasFacade;
	@EJB
	private ProyectoLicenciaAmbientalCoaCiuuBloquesFacade proyectoLicenciaAmbientalCoaCiuuBloquesFacade;
	@EJB
	private DocumentosCoaFacade documentosFacade;
	@EJB
	private DetalleInterseccionProyectoAmbientalFacade detalleInterseccionProyectoAmbientalFacade;
	
	@EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
    private AlfrescoServiceBean alfrescoServiceBean;
	
	@EJB
	private ProyectoCoaCiuuSubActividadesFacade proyectoCoaCiuuSubActividadesFacade;
	@EJB
	private CertificadoInterseccionCoaFacade certificadoInterseccionCoaFacade;
	@EJB
	private UsuarioFacade usuarioFacade;

	@Getter
	@Setter
	CatalogoCIUU actividadPrincipal;
	@Getter
	@Setter
	CatalogoCIUU ciiuPrincipal;
	@Getter
	@Setter
	CatalogoCIUU ciiuComplementaria1;
	@Getter
	@Setter
	CatalogoCIUU ciiuComplementaria2;
	@Getter
	@Setter
	ValorMagnitud valorMagnitud1;
	@Getter
	@Setter
	ValorMagnitud valorMagnitud2;
	@Getter
	@Setter
	ValorMagnitud valorMagnitud3;
	@Getter
	@Setter
	List<UbicacionesGeografica> ubicacionesSeleccionadas;
	@Getter
	@Setter
	List<CoordenadasProyecto> coordenadasGeograficas;
	@Getter
	@Setter
	List<CoordendasPoligonos> coordinatesWrappers, coordinatesWrappersGeo;
	@Getter
	ProyectoLicenciaCoa proyectoLicenciaCoa;

	@Getter
	@Setter
	List<CategoriaFlujo> flujos;
	@Getter
	@Setter
	List<Tarea> tareas;
	@Getter
	@Setter
	List<Documento> documentos;
	@Getter
	@Setter
	List<DocumentosCOA> documentosActualizados;
	@Getter
	@Setter
	boolean proyectoFinalizado = false;
	@Getter
	@Setter
	List<SustanciaQuimicaPeligrosa> sustanciaQuimicaSeleccionada; 
	
	@Getter
	@Setter
	ProyectoLicenciaCuaCiuu actividad1 = new ProyectoLicenciaCuaCiuu();
	@Getter
	@Setter
	ProyectoLicenciaCuaCiuu actividad2 = new ProyectoLicenciaCuaCiuu();
	@Getter
	@Setter
	ProyectoLicenciaCuaCiuu actividad3 = new ProyectoLicenciaCuaCiuu();
	@Getter
	@Setter
	SubActividades parent1 = new SubActividades();
	@Getter
	@Setter
	SubActividades parent2 = new SubActividades();
	@Getter
	@Setter
	SubActividades parent3 = new SubActividades();
	@Getter
	@Setter
	private boolean subOpciones1,subOpciones2,subOpciones3;
	@Getter
	@Setter
	private Integer tipoOpcion1,tipoOpcion2,tipoOpcion3;
	@Getter
	@Setter
	private SubActividades bancoEstado1=new SubActividades();
	@Getter
	@Setter
	private SubActividades bancoEstado2=new SubActividades();
	@Getter
	@Setter
	private SubActividades bancoEstado3=new SubActividades();	
	@Getter
	@Setter
	private List<ProyectoLicenciaAmbientalConcesionesMineras> listaConcesionesMineras = new ArrayList<ProyectoLicenciaAmbientalConcesionesMineras>();
	@Getter
	@Setter
	private List<ProyectoLicenciaAmbientalCoaCiuuBloques> listaBloques = new ArrayList<ProyectoLicenciaAmbientalCoaCiuuBloques>();
	@Getter
	@Setter
	private Boolean existeHidrocarburos = false;
	@Getter
	@Setter
	private Boolean existeConcesiones = false;
	@Getter
	@Setter
	private Boolean esCiiu1HidrocarburoMineriaElectrico = false, esCiiu2HidrocarburoMineriaElectrico = false, esCiiu3HidrocarburoMineriaElectrico = false;
	@Getter
	@Setter
	private boolean estadoFrontera;
	@Getter
	@Setter
	private ProyectoLicenciaCuaCiuu ciiu1= new ProyectoLicenciaCuaCiuu();
	@Getter
	@Setter
	private ProyectoLicenciaCuaCiuu ciiu2= new ProyectoLicenciaCuaCiuu();
	@Getter
	@Setter
	private ProyectoLicenciaCuaCiuu ciiu3= new ProyectoLicenciaCuaCiuu();
	
	@Getter
	@Setter
	private List<SubActividades> listaSubActividades1 = new ArrayList<SubActividades>();
	@Getter
	@Setter
	private List<SubActividades> listaSubActividades2 = new ArrayList<SubActividades>();
	@Getter
	@Setter
	private List<SubActividades> listaSubActividades3 = new ArrayList<SubActividades>();
	
	@Getter
	@Setter
	private boolean requiereTablaResiduo1,requiereTablaResiduo2,requiereTablaResiduo3;	
	@Getter
	@Setter
	private List<SubActividades> listaSubActividadSeleccionad1 = new ArrayList<SubActividades>();
	@Getter
	@Setter
	private List<SubActividades> listaSubActividadSeleccionad2 = new ArrayList<SubActividades>();
	@Getter
	@Setter
	private List<SubActividades> listaSubActividadSeleccionad3 = new ArrayList<SubActividades>();	
	
	@Getter
	@Setter
	private List<SustanciaQuimicaPeligrosa> sustanciaQuimicaSeleccionadaTransporta = new ArrayList<SustanciaQuimicaPeligrosa>();
	
	@Getter
	@Setter
	private List<SustanciaQuimicaPeligrosa> listaSustanciaQuimicaSeleccionadaOtros = new ArrayList<SustanciaQuimicaPeligrosa>();
	
	@Getter
	@Setter
	private List<SustanciaQuimicaPeligrosa> listaSustanciaQuimicaSeleccionadaOtrosTransporta = new ArrayList<SustanciaQuimicaPeligrosa>();
	
	@Getter
	@Setter
	private boolean esControlSustancia = false;
	
	@Getter
	@Setter
	private boolean estadoSustanciasQuimicas = false;
	
	@Getter
	@Setter
	private boolean tblSustanciaOtros = false;
	
	@Getter
	@Setter
	private boolean tblSustanciaOtrosTransporta = false;
	
	@Getter
	@Setter
	SubActividades subActividadTemp = new SubActividades();
	
	@Getter
	@Setter
	String nombreTemp="";

//	@PostConstruct
	public void cargarDatos() throws ServiceException {
		
		proyectoLicenciaCoa = proyectosBean.getProyectoRcoa();

		if (proyectoLicenciaCoa != null && proyectoLicenciaCoa.getId() != null) {
			ciiuPrincipal = new CatalogoCIUU();
			ciiuComplementaria1 = new CatalogoCIUU();
			ciiuComplementaria2 = new CatalogoCIUU();
			valorMagnitud1 = new ValorMagnitud();
			valorMagnitud2 = new ValorMagnitud();
			valorMagnitud3 = new ValorMagnitud();
			coordenadasGeograficas = new ArrayList<>();
			coordinatesWrappersGeo = new ArrayList<>();
			coordinatesWrappers = new ArrayList<>();
			
			sustanciaQuimicaSeleccionada = new ArrayList<>();
			sustanciaQuimicaSeleccionadaTransporta = new ArrayList<SustanciaQuimicaPeligrosa>();
			listaSustanciaQuimicaSeleccionadaOtros = new ArrayList<SustanciaQuimicaPeligrosa>();
			listaSustanciaQuimicaSeleccionadaOtrosTransporta = new ArrayList<SustanciaQuimicaPeligrosa>();
			documentosActualizados = new ArrayList<>();
			
			actividad1 = new ProyectoLicenciaCuaCiuu();
			actividad2 = new ProyectoLicenciaCuaCiuu();
			actividad3 = new ProyectoLicenciaCuaCiuu();
			parent1 = new SubActividades();
			parent2 = new SubActividades();
			parent3 = new SubActividades();
			subOpciones1 = false;
			subOpciones2 = false;
			subOpciones3 = false;
			tipoOpcion1 = null;
			tipoOpcion2 = null;
			tipoOpcion3 = null;
			esCiiu1HidrocarburoMineriaElectrico = false;
			esCiiu2HidrocarburoMineriaElectrico = false;
			esCiiu3HidrocarburoMineriaElectrico = false;
			existeConcesiones = false;
			existeHidrocarburos = false;

			cargarUbicacionProyecto();

			List<ProyectoLicenciaAmbientalCoaShape> formas = new ArrayList<ProyectoLicenciaAmbientalCoaShape>();
			formas = proyectoLicenciaAmbientalCoaShapeFacade
					.buscarFormaGeograficaPorProyecto(proyectoLicenciaCoa, 2, 0);
			if (formas == null) {
				formas = new ArrayList<ProyectoLicenciaAmbientalCoaShape>();
			} else {
				coordenadasGeograficas = new ArrayList<>();
				for (ProyectoLicenciaAmbientalCoaShape forma : formas) {
					List<CoordenadasProyecto> coordenadasGeograficasGeo = coordenadasProyectoCoaFacade.buscarPorForma(forma);
					coordenadasGeograficas.addAll(coordenadasGeograficasGeo);
					
					CoordendasPoligonos poligono = new CoordendasPoligonos();
					poligono.setCoordenadas(coordenadasGeograficasGeo);
					poligono.setTipoForma(forma.getTipoForma());
					coordinatesWrappersGeo.add(poligono);
				}
			}
			List<ProyectoLicenciaAmbientalCoaShape> formasImplantacion = proyectoLicenciaAmbientalCoaShapeFacade
					.buscarFormaGeograficaPorProyecto(proyectoLicenciaCoa, 1, 0);
			if (formasImplantacion == null) {
				formasImplantacion = new ArrayList<ProyectoLicenciaAmbientalCoaShape>();
			} else {
				for (ProyectoLicenciaAmbientalCoaShape forma : formasImplantacion) {
					List<CoordenadasProyecto> coordenadasGeograficasImplantacion = coordenadasProyectoCoaFacade
							.buscarPorForma(forma);
					CoordendasPoligonos poligono = new CoordendasPoligonos();
					poligono.setCoordenadas(coordenadasGeograficasImplantacion);
					poligono.setTipoForma(forma.getTipoForma());
					coordinatesWrappers.add(poligono);
				}
			}

			List<ProyectoLicenciaCuaCiuu> listaActividadesCiuu = new ArrayList<ProyectoLicenciaCuaCiuu>();
			listaActividadesCiuu = proyectoLicenciaCuaCiuuFacade
					.actividadesPorProyecto(proyectoLicenciaCoa);

			for (ProyectoLicenciaCuaCiuu actividad : listaActividadesCiuu) {
				if (actividad.getOrderJerarquia() == 1) {
					ciiuPrincipal = actividad.getCatalogoCIUU();
					ciiu1=actividad;
					//informacion de las sub actividades----------------
					List<ProyectoCoaCiuuSubActividades> cargarSubActividades=proyectoCoaCiuuSubActividadesFacade.cargarSubActividades(actividad);
					requiereTablaResiduo1=false;
					if(cargarSubActividades.size()>0)
					{
						subOpciones1=true;
						listaSubActividades1= subActividadesFacade.listaXactividad(actividad.getCatalogoCIUU());
						if (listaSubActividades1.size() > 0) {
							tipoOpcion1=listaSubActividades1.get(0).getTipoPregunta().intValue();
						}
						// tipoOpcion1=listaSubActividades1.get(0).getTipoPregunta().intValue();	
						listaSubActividadSeleccionad1 = new ArrayList<SubActividades>();						
						for(ProyectoCoaCiuuSubActividades subSelecionada: cargarSubActividades)
						{
							if(subSelecionada.getRespuesta()!=null && !subSelecionada.getRespuesta())
							{
								parent1=subSelecionada.getSubActividad();
								parent1.setValorOpcion(subSelecionada.getRespuesta());								
							}
							else if(subSelecionada.getRespuesta()!=null && subSelecionada.getRespuesta())
							{
								parent1=subSelecionada.getSubActividad();
								parent1.setValorOpcion(subSelecionada.getRespuesta());
							}
							else
							{								
								listaSubActividadSeleccionad1.add(subSelecionada.getSubActividad());
							}
							if(!requiereTablaResiduo1)
								requiereTablaResiduo1=subSelecionada.getSubActividad().getRequiereIngresoResiduos()==null?false:subSelecionada.getSubActividad().getRequiereIngresoResiduos();
						}
						residuosActividadesCiiuBean.buscarResiduosActividades(actividad, 1);
					}
					else if(actividad.getSubActividad()!=null)
					{
						subOpciones1=true;
						
						if(ciiuPrincipal.getTipoPregunta() != null) {
							actividad1 = actividad;
							cargarDatosConsideraciones(1);
						} else {
						boolean doblePregunta=false;
						String actividadesDoblePregunta= Constantes.getActividadesDoblePregunta();				
						String[] actividadesDoblePreguntaArray = actividadesDoblePregunta.split(",");				
						for (String actividades : actividadesDoblePreguntaArray) {							
							if(actividad.getCatalogoCIUU().getCodigo().equals(actividades))
							{
								doblePregunta=true;
							}
						}
						if(doblePregunta)
						{
							tipoOpcion1=3;
							if(actividad.getFinanciadoBancoDesarrollo()!=null)
							{
								bancoEstado1=subActividadesFacade.actividadParent(actividad.getIdActividadFinanciadoBancoEstado());
								bancoEstado1.setValorOpcion(actividad.getFinanciadoBancoDesarrollo());
							}
							
//							SubActividades parent = subActividadesFacade.actividadParent(actividad.getSubActividad().getSubActividades().getId());
							SubActividades parent = subActividadesFacade.actividadParent(actividad.getSubActividad().getSubActividades()==null?actividad.getSubActividad().getId():actividad.getSubActividad().getSubActividades().getId());
							parent1=parent;
							parent1.setValorOpcion(actividad.getPotabilizacionAgua());
							if(actividad.getPotabilizacionAgua())
								actividad1=actividad;
								
						} else if (actividad.getFinanciadoBancoDesarrollo() != null) {
							parent1 = subActividadesFacade.actividadParent(actividad.getIdActividadFinanciadoBancoEstado());
							parent1.setValorOpcion(actividad.getFinanciadoBancoDesarrollo());

							tipoOpcion1 = 5;

							if (actividad.getSubActividad() != null) {
								if (actividad.getSubActividad().getEsMultiple()) {
									actividad1 = actividad;
								} else {
									actividad1 = actividad;
									actividad1.setValorOpcion(actividad.getValorOpcion());
								}
							}
						}
						else if(!actividad.getSubActividad().getEsMultiple())
						{
							tipoOpcion1=1;
							actividad1=actividad;
 						    actividad1.setValorOpcion(actividad.getValorOpcion());
							if(actividad.getCatalogoCIUU().getCodigo().equals(Constantes.ACTIVIDAD_SUBESTACION_ELECTRICA_PADRE) || actividad.getCatalogoCIUU().getCodigo().equals(Constantes.ACTIVIDAD_SUBESTACION_ELECTRICA_HIJO) ) {
								tipoOpcion1=11;
								nombreTemp=actividad.getSubActividad().getNombre();
							}
						}
						else
						{
							tipoOpcion1=2;
							actividad1=actividad;
							if(actividad.getSubActividad().getSubActividades()!=null)
								if(actividad.getSubActividad().getSubActividades().getNombre().equals(Constantes.ACTIVIDAD_SUBESTACION_SUBTRANSMISION)) {
									tipoOpcion1=11;
									nombreTemp=Constantes.ACTIVIDAD_SUBESTACION_SUBTRANSMISION+" - "+actividad.getSubActividad().getNombre();
								}
						}
						}
					} else {
						if (actividad.getFinanciadoBancoDesarrollo() != null)
						{
							subOpciones1 = true;

							actividad.setSubActividad(subActividadesFacade.actividadParent(actividad.getIdActividadFinanciadoBancoEstado()));
							tipoOpcion1 = 1;
							actividad1 = actividad;
							actividad1.setValorOpcion(actividad.getFinanciadoBancoDesarrollo());
						}
					}
					
					//--------------------------------------------------
					esCiiu1HidrocarburoMineriaElectrico = false;
					if(ciiuPrincipal != null && ciiuPrincipal.getTipoSector() != null) {
						String nombreSector = ciiuPrincipal.getTipoSector().getNombre().toUpperCase();
						if(nombreSector.equals("MINERÍA") || nombreSector.equals("HIDROCARBUROS")){// || nombreSector.equals("ELÉCTRICO")
							esCiiu1HidrocarburoMineriaElectrico = true;
						}
					}
					
					if(subActividadesCiiuBean.esActividadRecupercionMateriales(ciiuPrincipal)) {
						tipoOpcion1 = 6;
						subOpciones1=true;
						buscarSubACtividadesSeleccionadas(actividad, 1);
					}
					
					if(ciiuPrincipal.getLibreAprovechamiento()) {
						SubActividades parent = subActividadesFacade.actividadParent(actividad.getSubActividad().getSubActividades()==null?actividad.getSubActividad().getId():actividad.getSubActividad().getSubActividades().getId());
						if (parent != null) {
							tipoOpcion1 = 5;
							actividad1 = actividad;						
							parent1 = parent;
							parent1.setValorOpcion(actividad.getSubActividad().getPadreVerdadero());
							
							if(!actividad.getSubActividad().getEsMultiple())
								actividad1.getSubActividad().setValorOpcion(actividad.getValorOpcion());
						}
					}
				} else if (actividad.getOrderJerarquia() == 2) {
					ciiuComplementaria1 = actividad.getCatalogoCIUU();
					ciiu2=actividad;
					//informacion de las sub actividades----------------
					List<ProyectoCoaCiuuSubActividades> cargarSubActividades=proyectoCoaCiuuSubActividadesFacade.cargarSubActividades(actividad);
					requiereTablaResiduo2=false;
					if(cargarSubActividades.size()>0)
					{
						subOpciones2=true;
						listaSubActividades2= subActividadesFacade.listaXactividad(actividad.getCatalogoCIUU());
						tipoOpcion2=listaSubActividades2.get(0).getTipoPregunta().intValue();	
						listaSubActividadSeleccionad2 = new ArrayList<SubActividades>();						
						for(ProyectoCoaCiuuSubActividades subSelecionada: cargarSubActividades)
						{
							if(subSelecionada.getRespuesta()!=null && !subSelecionada.getRespuesta())
							{
								parent2=subSelecionada.getSubActividad();
								parent2.setValorOpcion(subSelecionada.getRespuesta());								
							}
							else if(subSelecionada.getRespuesta()!=null && subSelecionada.getRespuesta())
							{
								parent2=subSelecionada.getSubActividad();
								parent2.setValorOpcion(subSelecionada.getRespuesta());
							}
							else
							{								
								listaSubActividadSeleccionad2.add(subSelecionada.getSubActividad());
							}
							if(!requiereTablaResiduo2)
								requiereTablaResiduo2=subSelecionada.getSubActividad().getRequiereIngresoResiduos()==null?false:subSelecionada.getSubActividad().getRequiereIngresoResiduos();
						}
						residuosActividadesCiiuBean.buscarResiduosActividades(actividad, 2);
					}
					else if(actividad.getSubActividad()!=null)
					{
						subOpciones2=true;
						
						if(ciiuComplementaria1.getTipoPregunta() != null) {
							actividad2 = actividad;
							cargarDatosConsideraciones(2);
						} else {
						boolean doblePregunta=false;
						String actividadesDoblePregunta= Constantes.getActividadesDoblePregunta();				
						String[] actividadesDoblePreguntaArray = actividadesDoblePregunta.split(",");				
						for (String actividades : actividadesDoblePreguntaArray) {							
							if(actividad.getCatalogoCIUU().getCodigo().equals(actividades))
							{
								doblePregunta=true;
							}
						}
						if(doblePregunta)
						{
							tipoOpcion2=3;
							if(actividad.getFinanciadoBancoDesarrollo()!=null)
							{
								bancoEstado2=subActividadesFacade.actividadParent(actividad.getIdActividadFinanciadoBancoEstado());
								bancoEstado2.setValorOpcion(actividad.getFinanciadoBancoDesarrollo());
							}
							
//							SubActividades parent = subActividadesFacade.actividadParent(actividad.getSubActividad().getSubActividades().getId());
							SubActividades parent = subActividadesFacade.actividadParent(actividad.getSubActividad().getSubActividades()==null?actividad.getSubActividad().getId():actividad.getSubActividad().getSubActividades().getId());
							parent2=parent;
							parent2.setValorOpcion(actividad.getPotabilizacionAgua());
							if(actividad.getPotabilizacionAgua())
								actividad2=actividad;
								
						} else if (actividad.getFinanciadoBancoDesarrollo() != null) {
							parent2 = subActividadesFacade.actividadParent(actividad.getIdActividadFinanciadoBancoEstado());
							parent2.setValorOpcion(actividad.getFinanciadoBancoDesarrollo());

							tipoOpcion2 = 5;

							if (actividad.getSubActividad() != null) {
								if (actividad.getSubActividad().getEsMultiple()) {
									actividad2 = actividad;
								} else {
									actividad2 = actividad;
									actividad2.setValorOpcion(actividad.getValorOpcion());
								}
							}
						}
						else if(!actividad.getSubActividad().getEsMultiple())
						{
							tipoOpcion2=1;
							actividad2=actividad;
							actividad2.setValorOpcion(actividad.getValorOpcion());
							if(actividad.getCatalogoCIUU().getCodigo().equals(Constantes.ACTIVIDAD_SUBESTACION_ELECTRICA_PADRE) || actividad.getCatalogoCIUU().getCodigo().equals(Constantes.ACTIVIDAD_SUBESTACION_ELECTRICA_HIJO) ) {
								tipoOpcion2=11;
								nombreTemp=actividad.getSubActividad().getNombre();
							}
						}
						else
						{
							tipoOpcion2=2;
							actividad2=actividad;
							if(actividad.getSubActividad().getSubActividades()!=null)
								if(actividad.getSubActividad().getSubActividades().getNombre().equals(Constantes.ACTIVIDAD_SUBESTACION_SUBTRANSMISION)) {
									tipoOpcion2=11;
									nombreTemp=Constantes.ACTIVIDAD_SUBESTACION_SUBTRANSMISION+" - "+actividad.getSubActividad().getNombre();
								}
						}
						}
					} else {
						if (actividad.getFinanciadoBancoDesarrollo() != null)
						{
							subOpciones2 = true;
							actividad.setSubActividad(subActividadesFacade.actividadParent(actividad.getIdActividadFinanciadoBancoEstado()));
							tipoOpcion2 = 1;
							actividad2 = actividad;
							actividad2.setValorOpcion(actividad.getFinanciadoBancoDesarrollo());
						}
					}
					//--------------------------------------------------
					esCiiu2HidrocarburoMineriaElectrico = false;
					if(ciiuComplementaria1 != null && ciiuComplementaria1.getTipoSector() != null) {
						String nombreSector = ciiuComplementaria1.getTipoSector().getNombre().toUpperCase();
						if(nombreSector.equals("MINERÍA") || nombreSector.equals("HIDROCARBUROS")){ // || nombreSector.equals("ELÉCTRICO")
							esCiiu2HidrocarburoMineriaElectrico = true;
						}
					}
					
					if(subActividadesCiiuBean.esActividadRecupercionMateriales(ciiuComplementaria1)) {
						tipoOpcion2 = 6;
						subOpciones2=true;
						buscarSubACtividadesSeleccionadas(actividad, 2);
					}
					
					if(ciiuComplementaria1.getLibreAprovechamiento()) {
						SubActividades parent = subActividadesFacade.actividadParent(actividad.getSubActividad().getSubActividades()==null?actividad.getSubActividad().getId():actividad.getSubActividad().getSubActividades().getId());
						if (parent != null) {
							tipoOpcion2 = 5;
							actividad2=actividad;
							parent2=parent;
							parent2.setValorOpcion(actividad.getSubActividad().getPadreVerdadero());
							
							if(!actividad.getSubActividad().getEsMultiple())
								actividad2.getSubActividad().setValorOpcion(actividad.getValorOpcion());
						}
					}
				} else if (actividad.getOrderJerarquia() == 3) {
					ciiuComplementaria2 = actividad.getCatalogoCIUU();
					ciiu3=actividad;
					//informacion de las sub actividades----------------
					List<ProyectoCoaCiuuSubActividades> cargarSubActividades=proyectoCoaCiuuSubActividadesFacade.cargarSubActividades(actividad);
					requiereTablaResiduo3=false;
					if(cargarSubActividades.size()>0)
					{
						subOpciones3=true;
						listaSubActividades3= subActividadesFacade.listaXactividad(actividad.getCatalogoCIUU());
						tipoOpcion3=listaSubActividades3.get(0).getTipoPregunta()!=null?listaSubActividades3.get(0).getTipoPregunta().intValue():0;	
						listaSubActividadSeleccionad3 = new ArrayList<SubActividades>();						
						for(ProyectoCoaCiuuSubActividades subSelecionada: cargarSubActividades)
						{
							if(subSelecionada.getRespuesta()!=null && !subSelecionada.getRespuesta())
							{
								parent3=subSelecionada.getSubActividad();
								parent3.setValorOpcion(subSelecionada.getRespuesta());								
							}
							else if(subSelecionada.getRespuesta()!=null && subSelecionada.getRespuesta())
							{
								parent3=subSelecionada.getSubActividad();
								parent3.setValorOpcion(subSelecionada.getRespuesta());
							}
							else
							{								
								listaSubActividadSeleccionad3.add(subSelecionada.getSubActividad());
							}
							if(!requiereTablaResiduo3)
								requiereTablaResiduo3=subSelecionada.getSubActividad().getRequiereIngresoResiduos()==null?false:subSelecionada.getSubActividad().getRequiereIngresoResiduos();
						}
						residuosActividadesCiiuBean.buscarResiduosActividades(actividad, 3);
					}
					else if(actividad.getSubActividad()!=null)
					{
						subOpciones3=true;
						
						if(ciiuComplementaria2.getTipoPregunta() != null) {
							actividad3 = actividad;
							cargarDatosConsideraciones(3);
						} else {
						boolean doblePregunta=false;
						String actividadesDoblePregunta= Constantes.getActividadesDoblePregunta();				
						String[] actividadesDoblePreguntaArray = actividadesDoblePregunta.split(",");				
						for (String actividades : actividadesDoblePreguntaArray) {							
							if(actividad.getCatalogoCIUU().getCodigo().equals(actividades))
							{
								doblePregunta=true;
							}
						}
						if(doblePregunta)
						{
							tipoOpcion3=3;
							if(actividad.getFinanciadoBancoDesarrollo()!=null)
							{
								bancoEstado3=subActividadesFacade.actividadParent(actividad.getIdActividadFinanciadoBancoEstado());
								bancoEstado3.setValorOpcion(actividad.getFinanciadoBancoDesarrollo());
							}
							
//							SubActividades parent = subActividadesFacade.actividadParent(actividad.getSubActividad().getSubActividades().getId());
							SubActividades parent = subActividadesFacade.actividadParent(actividad.getSubActividad().getSubActividades()==null?actividad.getSubActividad().getId():actividad.getSubActividad().getSubActividades().getId());
							parent3=parent;
							parent3.setValorOpcion(actividad.getPotabilizacionAgua());
							if(actividad.getPotabilizacionAgua())
								actividad3=actividad;
								
						} else if (actividad.getFinanciadoBancoDesarrollo() != null) {
							parent3 = subActividadesFacade.actividadParent(actividad.getIdActividadFinanciadoBancoEstado());
							parent3.setValorOpcion(actividad.getFinanciadoBancoDesarrollo());

							tipoOpcion3 = 5;

							if (actividad.getSubActividad() != null) {
								if (actividad.getSubActividad().getEsMultiple()) {
									actividad3 = actividad;
								} else {
									actividad3 = actividad;
									actividad3.setValorOpcion(actividad.getValorOpcion());
								}
							}
						}
						else if(!actividad.getSubActividad().getEsMultiple())
						{
							tipoOpcion3=1;
							actividad3=actividad;
							actividad3.setValorOpcion(actividad.getValorOpcion());
							if(actividad.getCatalogoCIUU().getCodigo().equals(Constantes.ACTIVIDAD_SUBESTACION_ELECTRICA_PADRE) || actividad.getCatalogoCIUU().getCodigo().equals(Constantes.ACTIVIDAD_SUBESTACION_ELECTRICA_HIJO) ) {
								tipoOpcion3=11;
								nombreTemp=actividad.getSubActividad().getNombre();
							}
						}
						else
						{
							tipoOpcion3=2;
							actividad3=actividad;
							if(actividad.getSubActividad().getSubActividades()!=null)
								if(actividad.getSubActividad().getSubActividades().getNombre().equals(Constantes.ACTIVIDAD_SUBESTACION_SUBTRANSMISION)) {
									tipoOpcion3=11;
									nombreTemp=Constantes.ACTIVIDAD_SUBESTACION_SUBTRANSMISION+" - "+actividad.getSubActividad().getNombre();
								}
						}
						}
					} else {
						if (actividad.getFinanciadoBancoDesarrollo() != null)
						{
							subOpciones3 = true;
							actividad.setSubActividad(subActividadesFacade.actividadParent(actividad.getIdActividadFinanciadoBancoEstado()));
							tipoOpcion3 = 1;
							actividad3 = actividad;
							actividad3.setValorOpcion(actividad.getFinanciadoBancoDesarrollo());
						}
					}

					//--------------------------------------------------
					esCiiu3HidrocarburoMineriaElectrico = false;
					if(ciiuComplementaria2 != null && ciiuComplementaria2.getTipoSector() != null) {
						String nombreSector = ciiuComplementaria2.getTipoSector().getNombre().toUpperCase();
						if(nombreSector.equals("MINERÍA") || nombreSector.equals("HIDROCARBUROS")){ // || nombreSector.equals("ELÉCTRICO")
							esCiiu3HidrocarburoMineriaElectrico = true;
						}
					}
					
					if(subActividadesCiiuBean.esActividadRecupercionMateriales(ciiuComplementaria2)) {
						tipoOpcion3 = 6;
						subOpciones3=true;
						buscarSubACtividadesSeleccionadas(actividad, 3);
					}
					
					if(ciiuComplementaria2.getLibreAprovechamiento()) {
						SubActividades parent = subActividadesFacade.actividadParent(actividad.getSubActividad().getSubActividades()==null?actividad.getSubActividad().getId():actividad.getSubActividad().getSubActividades().getId());
						if (parent != null) {
							tipoOpcion3 = 5;
							actividad3=actividad;
							parent3=parent;
							parent3.setValorOpcion(actividad.getSubActividad().getPadreVerdadero());
							
							if(!actividad.getSubActividad().getEsMultiple())
								actividad3.getSubActividad().setValorOpcion(actividad.getValorOpcion());
						}
					}
				}
			}
			
			ProyectoLicenciaCuaCiuu proyectoCiuuPrincipal = proyectoLicenciaCuaCiuuFacade.actividadPrincipal(proyectoLicenciaCoa);
			actividadPrincipal = proyectoCiuuPrincipal.getCatalogoCIUU();

			List<CriterioMagnitud> listaCriterioMagnitud = new ArrayList<CriterioMagnitud>();
			listaCriterioMagnitud = criterioMagnitudFacade
					.buscarCriterioMagnitudPorProyecto(proyectoLicenciaCoa);

			if (listaCriterioMagnitud != null
					&& !listaCriterioMagnitud.isEmpty()) {
				for (CriterioMagnitud criterioMag : listaCriterioMagnitud) {
					VariableCriterio variableCriterio = new VariableCriterio();
					variableCriterio = variableCriterioFacade
							.buscarVariableCriterioPorId(criterioMag
									.getVariableCriterio());
					if (variableCriterio.getNivelMagnitud().getId() == 1) {
						valorMagnitud1 = criterioMag.getValorMagnitud();
					} else if (variableCriterio.getNivelMagnitud().getId() == 2) {
						valorMagnitud2 = criterioMag.getValorMagnitud();
					} else {
						valorMagnitud3 = criterioMag.getValorMagnitud();
					}
				}
			}
			//sustancias quimicas
			for (GestionarProductosQuimicosProyectoAmbiental lista : gestionarProductosQuimicosProyectoAmbientalFacade
					.listaSustanciasQuimicas(proyectoLicenciaCoa)) {
				sustanciaQuimicaSeleccionada.add(lista.getSustanciaquimica());
				if(lista.getSustanciaquimica().getControlSustancia()!=null && lista.getSustanciaquimica().getControlSustancia()) {
					esControlSustancia=true;
				}
			}
			
			for(GestionarProductosQuimicosProyectoAmbiental listat:gestionarProductosQuimicosProyectoAmbientalFacade.listaSustanciasQuimicasTransporta(proyectoLicenciaCoa))
			{
				sustanciaQuimicaSeleccionadaTransporta.add(listat.getSustanciaquimica());
				if(listat.getSustanciaquimica().getControlSustancia()!=null && listat.getSustanciaquimica().getControlSustancia()) {
					esControlSustancia=true;
				}
						
			}
			/**
			 * Buscar sustancias ingresadas (otros), fabrica y transporte
			 * */
	
			for(GestionarProductosQuimicosProyectoAmbiental listaO:gestionarProductosQuimicosProyectoAmbientalFacade.listaSustanciasQuimicasOtros(proyectoLicenciaCoa) ) {
				tblSustanciaOtros=true;
				SustanciaQuimicaPeligrosa sustanciaQuimicaSeleccionadaOtros = new SustanciaQuimicaPeligrosa();
				sustanciaQuimicaSeleccionadaOtros.setDescripcion(listaO.getOtraSustancia());
				sustanciaQuimicaSeleccionadaOtros.setId(Constantes.ID_SUSTANCIA_OTROS);
				listaSustanciaQuimicaSeleccionadaOtros.add(sustanciaQuimicaSeleccionadaOtros);	
			}
				
					
			for(GestionarProductosQuimicosProyectoAmbiental listaOt:gestionarProductosQuimicosProyectoAmbientalFacade.listaSustanciasQuimicasOtrosTransporta(proyectoLicenciaCoa)) {
				tblSustanciaOtrosTransporta=true;
				SustanciaQuimicaPeligrosa sustanciaQuimicaSeleccionadaOtrosTransporta = new SustanciaQuimicaPeligrosa();
				sustanciaQuimicaSeleccionadaOtrosTransporta.setDescripcion(listaOt.getOtraSustancia());
				sustanciaQuimicaSeleccionadaOtrosTransporta.setId(Constantes.ID_SUSTANCIA_OTROS);
				listaSustanciaQuimicaSeleccionadaOtrosTransporta.add(sustanciaQuimicaSeleccionadaOtrosTransporta);
				
			}
			
			/****fin busqueda sustancias (otros)****/
			
			listaConcesionesMineras=proyectoLicenciaAmbientalConcesionesMinerasFacade.cargarConcesiones(proyectoLicenciaCoa);
			if(listaConcesionesMineras.size()>0)
				existeConcesiones=true;
			
			listaBloques = proyectoLicenciaAmbientalCoaCiuuBloquesFacade.cargarBloques(proyectoLicenciaCoa);
			if(listaBloques.size()>0)
				existeHidrocarburos=true;

			estadoFrontera=false;
			if(detalleInterseccionProyectoAmbientalFacade.zonaFrontera(proyectoLicenciaCoa))
				estadoFrontera=true;
			
			try {
				buscarDocumentosActualizados();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} else {
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		}
	}

	public void cargarUbicacionProyecto() {
		ubicacionesSeleccionadas = proyectoLicenciaCoaUbicacionFacade
				.buscarPorProyecto(proyectoLicenciaCoa);
	}

	public String getNombreProponente() {

		String nombreOperador = usuarioFacade.recuperarNombreOperador(proyectoLicenciaCoa.getUsuario()).get(0);

		return nombreOperador;
	}
	
	public void descargarAutorizacionPorSector(Integer tipoDocumento)
	{
		try {
			Integer idTabla = 0;
			if(tipoDocumento == 1)
				idTabla = ciiu1.getId();
			else if(tipoDocumento == 2)
				idTabla = ciiu2.getId();
			else if(tipoDocumento == 3)
				idTabla = ciiu3.getId();
			
			List<DocumentosCOA> listaDocumentos;
			listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(idTabla, TipoDocumentoSistema.RCOA_DOCUMENTO_AUTORIZACION_SECTORES,"ProyectoLicenciaCuaCiuu");
			if (listaDocumentos.size() > 0) {				
				UtilDocumento.descargarPDF(alfrescoServiceBean.downloadDocumentById(listaDocumentos.get(0).getIdAlfresco()),"Documento autorización");
			}
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
	}
	
	public void buscarSubACtividadesSeleccionadas(ProyectoLicenciaCuaCiuu actividad, Integer nroActividad) {
		List<SubActividades> listaSubActividades = subActividadesFacade.actividadPrincipalConHijos(actividad.getCatalogoCIUU());
		
		switch (nroActividad) {
		case 1:
			listaSubActividades1 = listaSubActividades;
			subActividadesCiiuBean.buscarSubActividadesCiiu(actividad, listaSubActividades1, nroActividad);
			break;
		case 2:
			listaSubActividades2 = listaSubActividades;
			subActividadesCiiuBean.buscarSubActividadesCiiu(actividad, listaSubActividades2, nroActividad);
			break;
		case 3:
			listaSubActividades3 = listaSubActividades;
			subActividadesCiiuBean.buscarSubActividadesCiiu(actividad, listaSubActividades3, nroActividad);
			break;
		default:
			break;
		}
		
		//buscar residuos
		residuosActividadesCiiuBean.buscarResiduosActividades(actividad, nroActividad);
	}
	
	public void buscarDocumentosActualizados() throws Exception {
		documentosActualizados= new ArrayList<DocumentosCOA>();

		List<CertificadoInterseccionOficioCoa> oficios = certificadoInterseccionCoaFacade.obtenerActualizadosPorCodigoProyecto(proyectoLicenciaCoa.getCodigoUnicoAmbiental());
		for(CertificadoInterseccionOficioCoa oficioCI : oficios){
			List<DocumentosCOA> listaDocumentosInt = documentosFacade.documentoXTablaIdXIdDocXNroActualizacion(oficioCI.getId(), TipoDocumentoSistema.RCOA_CERTIFICADO_INTERSECCION_OFICIO_ACTUALIZADO,"CertificadoInterseccionOficioCoa", oficioCI.getNroActualizacion());
			if (listaDocumentosInt.size() > 0) 
				documentosActualizados.add(listaDocumentosInt.get(0));
			
			List<Integer> listaTipos = new ArrayList<Integer>();
			listaTipos.add(TipoDocumentoSistema.RCOA_CERTIFICADO_INTERSECCION_MAPA_ACTUALIZADO.getIdTipoDocumento());
			listaTipos.add(TipoDocumentoSistema.RCOA_COORDENADA_GEOGRAFICA_ACTUALIZADA.getIdTipoDocumento());
			listaTipos.add(TipoDocumentoSistema.RCOA_COORDENADA_IMPLANTACION_ACTUALIZADA.getIdTipoDocumento());
			List<DocumentosCOA> listaDocumentos = documentosFacade.recuperarDocumentosPorTipoActualizacion(proyectoLicenciaCoa.getId(), "ProyectoLicenciaCoa", listaTipos, oficioCI.getNroActualizacion());
			documentosActualizados.addAll(listaDocumentos);
		}		
	}
	
	public DocumentosCOA descargarDocumentoActualizado(DocumentosCOA documento) throws CmisAlfrescoException {
        byte[] documentoContenido = null;
        if (documento != null && documento.getIdAlfresco() != null)
        	documentoContenido = documentosFacade.descargar(documento.getIdAlfresco());
        
        if (documentoContenido != null)
            documento.setContenidoDocumento(documentoContenido);
        
        try {
        	if(documento.getNombreDocumento().contains("pdf"))
        		JsfUtil.descargarPdf(documentoContenido, documento.getNombreDocumento().replace(".pdf", ""));
        	else 
        		UtilDocumento.descargarExcel(documentoContenido, documento.getNombreDocumento().replace(".xls", ""));
        } catch (IOException e) {
        	Log.debug(e.toString());
        }
        return documento;
    }
	
	/**
     * Recupera la información de las consideraciones seleccionadas
     * @param tipoActividad define el tipo de actividad principal, complementaria 1, complementaria 2 para asignación de datos a los objetos correspondientes
     */
    public void cargarDatosConsideraciones(Integer tipoActividad) {
    	switch (tipoActividad) {
		case 1:
			if(ciiuPrincipal.getTipoPregunta() != null) {
				Integer tipoPregunta = ciiuPrincipal.getTipoPregunta();
				
				switch (tipoPregunta) {
				case 1:
					parent1 = actividad1.getSubActividad();
					actividad1 = new ProyectoLicenciaCuaCiuu();
					break;
				case 2:
					if(actividad1.getSubActividad().getSubActividades() != null  
					&& actividad1.getSubActividad().getSubActividades().getId() != null) {
						parent1 = actividad1.getSubActividad().getSubActividades();
					} else {
						parent1 = actividad1.getSubActividad();
						actividad1 = new ProyectoLicenciaCuaCiuu();
					}
					break;
				case 4: 
					if (ciiu1.getFinanciadoBancoDesarrollo() != null) {
						bancoEstado1 = subActividadesFacade.actividadParent(actividad1.getIdActividadFinanciadoBancoEstado());
						bancoEstado1.setValorOpcion(actividad1.getFinanciadoBancoDesarrollo());
					}

					if (actividad1.getSubActividad().getSubActividades() != null
							&& actividad1.getSubActividad().getSubActividades().getId() != null) {
						parent1 = actividad1.getSubActividad().getSubActividades();
					} else {
						parent1 = actividad1.getSubActividad();
						actividad1 = new ProyectoLicenciaCuaCiuu();
					}
					break;
				default:
					break;
				}
			}
			break;
		case 2:
			if(ciiuComplementaria1.getTipoPregunta() != null) {
				Integer tipoPregunta = ciiuComplementaria1.getTipoPregunta();
				
				switch (tipoPregunta) {
				case 1:
					parent2 = actividad2.getSubActividad();
					actividad2 = new ProyectoLicenciaCuaCiuu();
					break;
				case 2:
					if(actividad2.getSubActividad().getSubActividades() != null  
					&& actividad2.getSubActividad().getSubActividades().getId() != null) {
						parent2 = actividad2.getSubActividad().getSubActividades();
					} else {
						parent2 = actividad2.getSubActividad();
						actividad2 = new ProyectoLicenciaCuaCiuu();
					}
					break;
				case 4:
					if (ciiu2.getFinanciadoBancoDesarrollo() != null) {
						bancoEstado2 = subActividadesFacade.actividadParent(actividad2.getIdActividadFinanciadoBancoEstado());
						bancoEstado2.setValorOpcion(actividad2.getFinanciadoBancoDesarrollo());
					}
					
					if (actividad2.getSubActividad().getSubActividades() != null
							&& actividad2.getSubActividad().getSubActividades().getId() != null) {
						parent2 = actividad2.getSubActividad().getSubActividades();
					} else {
						parent2 = actividad2.getSubActividad();
						actividad2 = new ProyectoLicenciaCuaCiuu();
					}
					break;
				default:
					break;
				}
			}
			break;
		case 3:
			if(ciiuComplementaria2.getTipoPregunta() != null) {
				Integer tipoPregunta = ciiuComplementaria2.getTipoPregunta();
				
				switch (tipoPregunta) {
				case 1:
					parent3 = actividad3.getSubActividad();
					actividad3 = new ProyectoLicenciaCuaCiuu();
					break;
				case 2:
					if(actividad3.getSubActividad().getSubActividades() != null  
					&& actividad3.getSubActividad().getSubActividades().getId() != null) {
						parent3 = actividad3.getSubActividad().getSubActividades();
					} else {
						parent3 = actividad3.getSubActividad();
						actividad3 = new ProyectoLicenciaCuaCiuu();
					}
					break;
				case 4:
					if (ciiu3.getFinanciadoBancoDesarrollo() != null) {
						bancoEstado3 = subActividadesFacade.actividadParent(actividad3.getIdActividadFinanciadoBancoEstado());
						bancoEstado3.setValorOpcion(actividad3.getFinanciadoBancoDesarrollo());
					}
					
					if (actividad3.getSubActividad().getSubActividades() != null
							&& actividad3.getSubActividad().getSubActividades().getId() != null) {
						parent3 = actividad3.getSubActividad().getSubActividades();
					} else {
						parent3 = actividad3.getSubActividad();
						actividad3 = new ProyectoLicenciaCuaCiuu();
					}
					break;
				default:
					break;
				}
			}
			break;
		default:
			break;
		}
    }

}
