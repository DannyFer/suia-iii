package ec.gob.ambiente.rcoa.preliminar.contoller;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.prevencion.registro.proyecto.bean.BloquesBean;
import ec.gob.ambiente.rcoa.dto.EntityInformePreliminar;
import ec.gob.ambiente.rcoa.facade.CertificadoAmbientalMaeFacade;
import ec.gob.ambiente.rcoa.facade.CoordenadasProyectoCoaFacade;
import ec.gob.ambiente.rcoa.facade.CriterioMagnitudFacade;
import ec.gob.ambiente.rcoa.facade.DocumentosCertificadoAmbientalFacade;
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
import ec.gob.ambiente.rcoa.facade.ValorMagnitudFacade;
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
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoaCiiuResiduos;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.model.SubActividades;
import ec.gob.ambiente.rcoa.model.ValorMagnitud;
import ec.gob.ambiente.rcoa.model.VariableCriterio;
import ec.gob.ambiente.rcoa.util.CoordendasPoligonos;
import ec.gob.ambiente.suia.administracion.bean.PromotorBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.InformeProvincialGADFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Bloque;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.SustanciaQuimicaPeligrosa;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;

@ManagedBean
@ViewScoped
public class ReporteInformacionPreliminarController {
	
	private static final Logger LOG = Logger.getLogger(ReporteInformacionPreliminarController.class);
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private ProyectoLicenciaAmbientalCoaShapeFacade proyectoLicenciaAmbientalShapeFacade;
	@EJB
	private CoordenadasProyectoCoaFacade coordenadasProyectoCoaFacade;
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaUbicacionFacade;
	@EJB
	private DocumentosCertificadoAmbientalFacade documentosCertificadoAmbientalFacade;
	@EJB
	private GestionarProductosQuimicosProyectoAmbientalFacade gestionarProductosQuimicosProyectoAmbientalFacade;	
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
	@EJB
	private CriterioMagnitudFacade criterioMagnitudFacade;
	@EJB
	private VariableCriterioFacade variableCriterioFacade;
	@EJB
	private ValorMagnitudFacade valorMagnitudFacade;
	@EJB
	private ProcesoFacade procesoFacade;	
	@EJB
	private AreaFacade areaFacade;
	@EJB
	private CertificadoAmbientalMaeFacade certificadoAmbientalInterseccionMaeFacade;
	@EJB
	private InformeProvincialGADFacade informeProvincialGADFacade;
	@EJB
	private DocumentosCoaFacade documentosFacade;	
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private ProyectoLicenciaAmbientalConcesionesMinerasFacade proyectoLicenciaAmbientalConcesionesMinerasFacade;
	@EJB
	private ProyectoLicenciaAmbientalCoaCiuuBloquesFacade proyectoLicenciaAmbientalCoaCiuuBloquesFacade;
	@EJB
	private SubActividadesFacade subActividadesFacade;
	@ManagedProperty(value = "#{subActividadesCiiuBean}")
	@Getter
	@Setter
	private SubActividadesCiiuBean subActividadesCiiuBean;
	@EJB
	private ProyectoCoaCiuuSubActividadesFacade proyectoCoaCiuuSubActividadesFacade;
	@ManagedProperty(value = "#{residuosActividadesCiiuBean}")
	@Getter
	@Setter
	private ResiduosActividadesCiiuBean residuosActividadesCiiuBean;
	

	@Setter
	@ManagedProperty(value = "#{bloquesBean}")
	private BloquesBean bloquesBean;
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
	@Getter
	@Setter
	private BandejaTareasBean bandejaTareasBean;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;
	
	@Getter
	@Setter
	private List<ProyectoLicenciaAmbientalCoaShape> formas;
	@Getter
	@Setter
	private List<UbicacionesGeografica> ubicacionesSeleccionadas;
	@Getter
	@Setter
	private List<CoordenadasProyecto> coordenadasGeograficas;
	@Getter
	@Setter
    private List<CoordendasPoligonos> coordinatesWrappers, coordinatesWrappersGeo;
	@Getter
	@Setter
	private List<SustanciaQuimicaPeligrosa> sustanciaQuimicaSeleccionada = new ArrayList<SustanciaQuimicaPeligrosa>();	
	
	private String tramite;
	
	private Area area;
	
	@Getter
	@Setter
	private CatalogoCIUU ciiuPrincipal= new CatalogoCIUU();
	@Getter
	@Setter
	private CatalogoCIUU ciiuComplementaria1= new CatalogoCIUU();
	@Getter
	@Setter
	private CatalogoCIUU ciiuComplementaria2= new CatalogoCIUU();
	
	@Getter
	@Setter
	private List<ProyectoLicenciaCuaCiuu> listaActividadesCiuu;
	
	@Getter
	@Setter
	private List<CriterioMagnitud> listaCriterioMagnitud;
	
	@Getter
	@Setter
	private CriterioMagnitud criterioMagnitud;
	
	@Getter
	@Setter
	private VariableCriterio variableCriterio;
	
	@Getter
	@Setter
	private ValorMagnitud valorMagnitud1 = new ValorMagnitud();
	@Getter
	@Setter
	private ValorMagnitud valorMagnitud2 = new ValorMagnitud();
	@Getter
	@Setter
	private ValorMagnitud valorMagnitud3 = new ValorMagnitud();	
	@Getter
	@Setter
	private ValorMagnitud valorMagnitudCalculo = new ValorMagnitud();
	
	@Getter
	@Setter
	private EntityInformePreliminar informePreliminar;
	
	@Getter
    @Setter
    private File informePdf;
	
	@Getter
    @Setter
    private String nombreReporte;
	
	@EJB
    private ContactoFacade contactoFacade;
	
	@Getter
	@Setter
	private String correoTemporal;
	
	 @Getter
	 @Setter
	 @ManagedProperty(value = "#{loginBean}")
	 private LoginBean loginBean;
	 
	 @Getter
	 @Setter
	 private PromotorBean promotorBean;
	
	byte[] archivoInforme;
	
			
		
	public void generarReporte(ProyectoLicenciaCoa proyecto, Boolean isDocProceso, Boolean mostrarOperador){
		try {
			List<DocumentosCOA> listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(proyecto.getId(), TipoDocumentoSistema.REPORTE_INFORMACION_PRELIMINAR,"ProyectoLicenciaCoa");
			if(listaDocumentos != null && listaDocumentos.size() > 0) {
				DocumentosCOA documentoReporte = listaDocumentos.get(0);
				if(!documentoReporte.getUsuarioCreacion().equals(JsfUtil.getLoggedUser().getNombre()) 
						|| !JsfUtil.getSimpleDateFormat(documentoReporte.getFechaCreacion()).equals(JsfUtil.getSimpleDateFormat(new Date()))) {
					documentoReporte.setEstado(false);
					documentosFacade.guardar(documentoReporte);
				} else {
					return;
				}
			}
			
			this.proyecto = proyecto;
			
			area = proyecto.getAreaResponsable();
			
			ubicacionesSeleccionadas = new ArrayList<UbicacionesGeografica>();
			coordenadasGeograficas = new ArrayList<CoordenadasProyecto>();
			coordinatesWrappersGeo = new ArrayList<CoordendasPoligonos>();
			coordinatesWrappers = new ArrayList<CoordendasPoligonos>();
			
			if(proyecto != null && proyecto.getId() != null){
				formas = proyectoLicenciaAmbientalShapeFacade.buscarFormaGeograficaPorProyecto(proyecto, 2, 0); //coordenadas geograficas
				
				if(formas == null){
					formas = new ArrayList<ProyectoLicenciaAmbientalCoaShape>();				
				}else{
					for(ProyectoLicenciaAmbientalCoaShape forma : formas){
						coordenadasGeograficas = coordenadasProyectoCoaFacade.buscarPorForma(forma);
						
						CoordendasPoligonos poligono = new CoordendasPoligonos();
						poligono.setCoordenadas(coordenadasGeograficas);
						poligono.setTipoForma(forma.getTipoForma());
						
						coordinatesWrappersGeo.add(poligono);
					}
				}
				
				List<ProyectoLicenciaAmbientalCoaShape> formasImplantacion = proyectoLicenciaAmbientalShapeFacade.buscarFormaGeograficaPorProyecto(proyecto, 1, 0); //coordenadas implantacion
				
				if(formasImplantacion == null){
					formasImplantacion = new ArrayList<ProyectoLicenciaAmbientalCoaShape>();				
				}else{
					for(ProyectoLicenciaAmbientalCoaShape forma : formasImplantacion){
						List<CoordenadasProyecto> coordenadasGeograficasImplantacion = coordenadasProyectoCoaFacade.buscarPorForma(forma);
						
						CoordendasPoligonos poligono = new CoordendasPoligonos();
						poligono.setCoordenadas(coordenadasGeograficasImplantacion);
						poligono.setTipoForma(forma.getTipoForma());
						
						coordinatesWrappers.add(poligono);
					}
				}
				
				listaActividadesCiuu = proyectoLicenciaCuaCiuuFacade.actividadesPorProyecto(proyecto);
				
				for(ProyectoLicenciaCuaCiuu actividad : listaActividadesCiuu){
					if(actividad.getOrderJerarquia() == 1){
						ciiuPrincipal = actividad.getCatalogoCIUU();
					}else if(actividad.getOrderJerarquia() == 2){
						ciiuComplementaria1 = actividad.getCatalogoCIUU();
					}else if(actividad.getOrderJerarquia() == 3){
						ciiuComplementaria2 = actividad.getCatalogoCIUU();
					}
				}
				
				listaCriterioMagnitud = criterioMagnitudFacade.buscarCriterioMagnitudPorProyecto(proyecto);
				
				if(listaCriterioMagnitud != null && !listaCriterioMagnitud.isEmpty()){
					for(CriterioMagnitud criterioMag : listaCriterioMagnitud){
						variableCriterio = new VariableCriterio();
						variableCriterio = variableCriterioFacade.buscarVariableCriterioPorId(criterioMag.getVariableCriterio());
					
											
						if(variableCriterio.getNivelMagnitud().getId() == 1){							
							valorMagnitud1 = criterioMag.getValorMagnitud();							
						}else if(variableCriterio.getNivelMagnitud().getId() == 2){
							valorMagnitud2 = criterioMag.getValorMagnitud();
						}else{
							valorMagnitud3 = criterioMag.getValorMagnitud();
						}
					}					
				}				
			}	
			sustanciaQuimicaSeleccionada.clear();
			for(GestionarProductosQuimicosProyectoAmbiental lista:gestionarProductosQuimicosProyectoAmbientalFacade.listaSustanciasQuimicas(proyecto))
			{
				sustanciaQuimicaSeleccionada.add(lista.getSustanciaquimica());
			}
			
			ubicacionesSeleccionadas = proyectoLicenciaUbicacionFacade.buscarPorProyecto(proyecto);	
			
			visualizarOficio(isDocProceso, mostrarOperador);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private String getDateFormat(Date date) {
		SimpleDateFormat formateador = new SimpleDateFormat(
				"dd 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
		return formateador.format(date);
	}
	
	 public String visualizarOficio(Boolean isDocProceso, Boolean mostrarOperador) {
	        String html = null;

	        try {	            
			String cantonP = "";
			String provinciaP = "";
			String ubicacionCompleta = "<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 80%; border-collapse:collapse;font-size:12px;\" ";

			ubicacionCompleta += "<tr><td style=\"text-align: center;\" ><strong>PROVINCIA</strong></td>"
					+ "<td style=\"text-align: center;\" ><strong>CANTÓN</strong></td>"
					+ "<td style=\"text-align: center;\" ><strong>PARROQUIA</strong></td></tr>";
			for (UbicacionesGeografica ubicacionActual : ubicacionesSeleccionadas) {
				UbicacionesGeografica cantonU = ubicacionActual
						.getUbicacionesGeografica();
				cantonP = cantonU.getNombre();
				if (cantonU.getUbicacionesGeografica() != null) {
					UbicacionesGeografica provinciaU = cantonU
							.getUbicacionesGeografica();
					provinciaP = provinciaU.getNombre();
				}

				ubicacionCompleta += "<tr><td>" + provinciaP + "</td><td>"
						+ cantonP + "</td><td>" + ubicacionActual.getNombre()
						+ "</td></tr>";
			}

			ubicacionCompleta += "</table>";
			
			String colorCeldaHeader = "bgcolor=\"#C5C5C5\" ";
			String actividades = "<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:12px;\" ";
			
			ProyectoLicenciaCuaCiuu actividad1 = new ProyectoLicenciaCuaCiuu();
			ProyectoLicenciaCuaCiuu actividad2 = new ProyectoLicenciaCuaCiuu();
			ProyectoLicenciaCuaCiuu actividad3 = new ProyectoLicenciaCuaCiuu();
			
			for(ProyectoLicenciaCuaCiuu actividad : listaActividadesCiuu){
				if(actividad.getOrderJerarquia() == 1){
					ciiuPrincipal = actividad.getCatalogoCIUU();
					actividad1=actividad;
				}else if(actividad.getOrderJerarquia() == 2){
					ciiuComplementaria1 = actividad.getCatalogoCIUU();
					actividad2=actividad;
				}else if(actividad.getOrderJerarquia() == 3){
					ciiuComplementaria2 = actividad.getCatalogoCIUU();
					actividad3=actividad;
				}
			}
			
			if(ciiuPrincipal.getId() != null){
				actividades += "<tr><td " + colorCeldaHeader + " width=\"30%\">Actividad principal CIIU </td>";
				actividades += "<td>" + ciiuPrincipal.getNombre();
				//informacion de las sub actividades----------------
				String htmlSubCategorias="";
				if(subActividadesCiiuBean.esActividadRecupercionMateriales(ciiuPrincipal)) {

				}
				else
				{
					List<ProyectoCoaCiuuSubActividades> cargarSubActividades=proyectoCoaCiuuSubActividadesFacade.cargarSubActividades(actividad1);
					boolean requiereTablaResiduo1=false;
					if(cargarSubActividades.size()>0)
					{	
						htmlSubCategorias+="<br /><table cellspacing=\"0\" border=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:12px;\">";
						for(ProyectoCoaCiuuSubActividades subSelecionada: cargarSubActividades)
						{
							if(subSelecionada.getRespuesta()!=null && !subSelecionada.getRespuesta())
							{	
								String pregunta=subSelecionada.getSubActividad().getNombre();
								String respuesta=subSelecionada.getRespuesta()?"Si":"No";
								htmlSubCategorias+="<tr>"
										+ "<td " + colorCeldaHeader + " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
										+ pregunta +"</td>"
										+ "<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
										+ respuesta+"</td>"
										+ "</tr>";
							}
							else if(subSelecionada.getRespuesta()!=null && subSelecionada.getRespuesta())
							{
								String pregunta=subSelecionada.getSubActividad().getNombre();
								String respuesta=subSelecionada.getRespuesta()?"Si":"No";
								htmlSubCategorias+="<tr>"
										+ "<td " + colorCeldaHeader + " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
										+ pregunta +"</td>"
										+ "<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
										+ respuesta+"</td>"
										+ "</tr>";

							}
							else
							{	
								String pregunta="Pregunta seleccionada";
								String respuesta=subSelecionada.getSubActividad().getNombre();
								htmlSubCategorias+="<tr>"
										+ "<td " + colorCeldaHeader + " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
										+ pregunta +"</td>"
										+ "<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
										+ respuesta+"</td>"
										+ "</tr>";
							}
							if(!requiereTablaResiduo1)
								requiereTablaResiduo1=subSelecionada.getSubActividad().getRequiereIngresoResiduos()==null?false:subSelecionada.getSubActividad().getRequiereIngresoResiduos();
						}
						if(requiereTablaResiduo1)
						{
							htmlSubCategorias+="<tr>";
							residuosActividadesCiiuBean.buscarResiduosActividades(actividad1, 1);
							htmlSubCategorias+="<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:12px;text-align: center;\">"
									+"<tr>"
									+ "<td bgcolor=\"#C5C5C5\" colspan=\"3\">Identificación de residuos o desechos no peligrosos o especiales no peligrosos</td>"
									+ "</tr>"											
									+"<tr>"
									+"<td bgcolor=\"#C5C5C5\">Código</td>"
									+ "<td bgcolor=\"#C5C5C5\">Nombre del Residuo o desecho no peligroso especial no peligroso</td>"
									+"<td bgcolor=\"#C5C5C5\">Cantidad anual estimada en kg</td>"
									+ "	</tr>";
							for(ProyectoLicenciaCoaCiiuResiduos residuos: residuosActividadesCiiuBean.getListaCiiuResiduos1())
							{
								htmlSubCategorias+="<tr>"
										+"<td>"+residuos.getDesechoPeligroso().getClave()+"</td>"
										+"<td>"+residuos.getDesechoPeligroso().getDescripcion()+"</td>"			
										+"<td>"+residuos.getCantidadAnual()+"</td>"
										+"</tr>";
							}
							htmlSubCategorias+="</table>";
							htmlSubCategorias+="</tr>";
						}
						htmlSubCategorias+="</table>";
						actividades+=htmlSubCategorias;	
					}	
					else if(actividad1.getSubActividad()!=null)
					{
						if(ciiuPrincipal.getTipoPregunta() != null) {
							htmlSubCategorias += cargarDatosConsideraciones(ciiuPrincipal.getTipoPregunta(), actividad1);
							actividades += htmlSubCategorias;
						} else {
						htmlSubCategorias+="<br /><table cellspacing=\"0\" border=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:12px;\">";

						boolean doblePregunta=false;
						String actividadesDoblePregunta= Constantes.getActividadesDoblePregunta();				
						String[] actividadesDoblePreguntaArray = actividadesDoblePregunta.split(",");				
						for (String acti : actividadesDoblePreguntaArray) {							
							if(actividad1.getCatalogoCIUU().getCodigo().equals(acti))
							{
								doblePregunta=true;
							}
						}
						boolean alcantarillado=false;
						String actividadAlcantarillado= Constantes.getActividadesZonaRural();				
						String[] actividadesAlcantarilladoArray = actividadAlcantarillado.split(",");				
						for (String actividad : actividadesAlcantarilladoArray) {
							if(actividad1.getCatalogoCIUU().getCodigo().equals(actividad))
							{
								alcantarillado=true;
							}					
						}
						if(doblePregunta)
						{
							if(actividad1.getFinanciadoBancoDesarrollo()!=null && actividad1.getFinanciadoBancoDesarrollo())
							{
								SubActividades bancoEstado=subActividadesFacade.actividadParent(actividad1.getIdActividadFinanciadoBancoEstado());
								bancoEstado.setValorOpcion(actividad1.getFinanciadoBancoDesarrollo());
								String pregunta=bancoEstado.getNombre();
								String respuesta=bancoEstado.getValorOpcion()?"Si":"No";
								htmlSubCategorias+="<tr>"
										+ "<td " + colorCeldaHeader + " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
										+ pregunta +"</td>"
										+ "<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
										+ respuesta+"</td>"
										+ "</tr>";
							}			
							SubActividades parent = subActividadesFacade.actividadParent(actividad1.getSubActividad().getId());
							SubActividades parent1=parent;
							parent1.setValorOpcion(actividad1.getPotabilizacionAgua());
							String pregunta=parent1.getNombre();
							String respuesta=parent1.getValorOpcion()?"Si":"No";
							htmlSubCategorias+="<tr>"
									+ "<td " + colorCeldaHeader + " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
									+ pregunta +"</td>"
									+ "<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
									+ respuesta+"</td>"
									+ "</tr>";

							if(parent1.getValorOpcion())
							{
								htmlSubCategorias+="<tr>" + 
										"<td " + colorCeldaHeader + " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">Opción seleccionada</td>\n" + 
										"<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
										+actividad1.getSubActividad().getNombre()+"</td>" + 
										"</tr>";
							}

						}
						else if(alcantarillado)
						{
							SubActividades parent = subActividadesFacade.actividadParent(actividad1.getSubActividad().getSubActividades()==null?actividad1.getSubActividad().getId():actividad1.getSubActividad().getSubActividades().getId());
							SubActividades parent1=parent;
							parent1.setValorOpcion(actividad1.getAlcantarilladoMunicipio());
							String pregunta=parent1.getNombre();
							String respuesta=parent1.getValorOpcion()?"Si":"No";
							htmlSubCategorias+="<tr>"
									+ "<td " + colorCeldaHeader + " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
									+ pregunta +"</td>"
									+ "<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
									+ respuesta+"</td>"
									+ "</tr>";

							if(!parent1.getValorOpcion())
							{
								htmlSubCategorias+="<tr>" + 
										"<td " + colorCeldaHeader + " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">Opción seleccionada</td>\n" + 
										"<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
										+actividad1.getSubActividad().getNombre()+"</td>" + 
										"</tr>";
							}						
						}
						else
						{
							if(!actividad1.getSubActividad().getEsMultiple())
							{
								if(actividad1.getValorOpcion()!=null) {
									String pregunta=actividad1.getSubActividad().getNombre();
									String respuesta=actividad1.getValorOpcion()?"Si":"No";
									htmlSubCategorias+="<tr>"
											+ "<td " + colorCeldaHeader + " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
											+ pregunta +"</td>"
											+ "<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
											+ respuesta+"</td>"
											+ "</tr>";
								}else {
									htmlSubCategorias+="<tr>" + 
											"<td " + colorCeldaHeader + " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">Opción seleccionada</td>\n" + 
											"<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
											+actividad1.getSubActividad().getNombre()+"</td>" + 
											"</tr>";
								}
							}
							else
							{
								//TODO:IVAN
								if(actividad1.getSubActividad().getSubActividades()!=null) {
									htmlSubCategorias+="<tr>" + 
											"<td " + colorCeldaHeader + " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">Opción seleccionada</td>\n" + 
											"<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
											+actividad1.getSubActividad().getSubActividades().getNombre()+" - "+actividad1.getSubActividad().getNombre()+"</td>" + 
											"</tr>";
									
								}else
								htmlSubCategorias+="<tr>" + 
										"<td " + colorCeldaHeader + " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">Opción seleccionada</td>\n" + 
										"<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
										+actividad1.getSubActividad().getNombre()+"</td>" + 
										"</tr>";
							}
						}
						htmlSubCategorias+="</table>";
						actividades+=htmlSubCategorias;
					}
					} else {
						if(actividad1.getFinanciadoBancoDesarrollo() != null && actividad1.getIdActividadFinanciadoBancoEstado() != null) {
							SubActividades bancoEstado = subActividadesFacade.actividadParent(actividad1.getIdActividadFinanciadoBancoEstado());
							String valorOpcion = (actividad1.getFinanciadoBancoDesarrollo()) ? "SI" : "NO";
							htmlSubCategorias+="<br /><table cellspacing=\"0\" border=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:12px;\">";
							htmlSubCategorias += "<tr>"
									+ "<td "
									+ colorCeldaHeader
									+ " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\"> "
									+ bancoEstado.getNombre()
									+ "</td>\n"
									+ "<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
									+ valorOpcion
									+ "</td>" + "</tr>";
							
							htmlSubCategorias+="</table>";
							actividades+=htmlSubCategorias;
						}
					}
				}
				//--------------------------------------------------
				actividades += "</td></tr>";
			}
			Boolean existenComplementarias = false;
			if(ciiuComplementaria1.getId() != null){
				existenComplementarias = true;
				String actividad = (ciiuComplementaria1.getId() == null) ? "" : ciiuComplementaria1.getNombre();
				actividades += "<tr><td " + colorCeldaHeader + ">Actividad complementaria 1 CIIU </td>";
				actividades += "<td>" + actividad;
				//informacion de las sub actividades----------------
				String htmlSubCategorias="";
				if(subActividadesCiiuBean.esActividadRecupercionMateriales(ciiuComplementaria1)) {

				}
				else
				{
					List<ProyectoCoaCiuuSubActividades> cargarSubActividades=proyectoCoaCiuuSubActividadesFacade.cargarSubActividades(actividad2);
					boolean requiereTablaResiduo2=false;
					if(cargarSubActividades.size()>0)
					{	
						htmlSubCategorias+="<br /><table cellspacing=\"0\" border=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:12px;\">";
						for(ProyectoCoaCiuuSubActividades subSelecionada: cargarSubActividades)
						{
							if(subSelecionada.getRespuesta()!=null && !subSelecionada.getRespuesta())
							{	
								String pregunta=subSelecionada.getSubActividad().getNombre();
								String respuesta=subSelecionada.getRespuesta()?"Si":"No";
								htmlSubCategorias+="<tr>"
										+ "<td " + colorCeldaHeader + " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
										+ pregunta +"</td>"
										+ "<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
										+ respuesta+"</td>"
										+ "</tr>";
							}
							else if(subSelecionada.getRespuesta()!=null && subSelecionada.getRespuesta())
							{
								String pregunta=subSelecionada.getSubActividad().getNombre();
								String respuesta=subSelecionada.getRespuesta()?"Si":"No";
								htmlSubCategorias+="<tr>"
										+ "<td " + colorCeldaHeader + " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
										+ pregunta +"</td>"
										+ "<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
										+ respuesta+"</td>"
										+ "</tr>";

							}
							else
							{	
								String pregunta="Pregunta seleccionada";
								String respuesta=subSelecionada.getSubActividad().getNombre();
								htmlSubCategorias+="<tr>"
										+ "<td " + colorCeldaHeader + " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
										+ pregunta +"</td>"
										+ "<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
										+ respuesta+"</td>"
										+ "</tr>";
							}
							if(!requiereTablaResiduo2)
								requiereTablaResiduo2=subSelecionada.getSubActividad().getRequiereIngresoResiduos()==null?false:subSelecionada.getSubActividad().getRequiereIngresoResiduos();
						}
						if(requiereTablaResiduo2)
						{
							htmlSubCategorias+="<tr>";
							residuosActividadesCiiuBean.buscarResiduosActividades(actividad2, 2);
							htmlSubCategorias+="<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:12px;text-align: center;\">"
									+"<tr>"
									+ "<td bgcolor=\"#C5C5C5\" colspan=\"3\">Identificación de residuos o desechos no peligrosos o especiales no peligrosos</td>"
									+ "</tr>"											
									+"<tr>"
									+"<td bgcolor=\"#C5C5C5\">Código</td>"
									+ "<td bgcolor=\"#C5C5C5\">Nombre del Residuo o desecho no peligroso especial no peligroso</td>"
									+"<td bgcolor=\"#C5C5C5\">Cantidad anual estimada en kg</td>"
									+ "	</tr>";
							for(ProyectoLicenciaCoaCiiuResiduos residuos: residuosActividadesCiiuBean.getListaCiiuResiduos2())
							{
								htmlSubCategorias+="<tr>"
										+"<td>"+residuos.getDesechoPeligroso().getClave()+"</td>"
										+"<td>"+residuos.getDesechoPeligroso().getDescripcion()+"</td>"			
										+"<td>"+residuos.getCantidadAnual()+"</td>"
										+"</tr>";
							}
							htmlSubCategorias+="</table>";
							htmlSubCategorias+="</tr>";
						}
						htmlSubCategorias+="</table>";
						actividades+=htmlSubCategorias;	
					}
					else if(actividad2.getSubActividad()!=null)
					{
						if(ciiuComplementaria1.getTipoPregunta() != null) {
							htmlSubCategorias += cargarDatosConsideraciones(ciiuComplementaria1.getTipoPregunta(), actividad2);
							actividades += htmlSubCategorias;
						} else {
						htmlSubCategorias+="<br /><table cellspacing=\"0\" border=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:12px;\">";

						boolean doblePregunta=false;
						String actividadesDoblePregunta= Constantes.getActividadesDoblePregunta();				
						String[] actividadesDoblePreguntaArray = actividadesDoblePregunta.split(",");				
						for (String acti : actividadesDoblePreguntaArray) {							
							if(actividad2.getCatalogoCIUU().getCodigo().equals(acti))
							{
								doblePregunta=true;
							}
						}
						boolean alcantarillado=false;
						String actividadAlcantarillado= Constantes.getActividadesZonaRural();				
						String[] actividadesAlcantarilladoArray = actividadAlcantarillado.split(",");				
						for (String activid2 : actividadesAlcantarilladoArray) {
							if(actividad2.getCatalogoCIUU().getCodigo().equals(activid2))
							{
								alcantarillado=true;
							}					
						}
						if(doblePregunta)
						{
							if(actividad2.getFinanciadoBancoDesarrollo()!=null && actividad2.getFinanciadoBancoDesarrollo())
							{
								SubActividades bancoEstado=subActividadesFacade.actividadParent(actividad2.getIdActividadFinanciadoBancoEstado());
								bancoEstado.setValorOpcion(actividad2.getFinanciadoBancoDesarrollo());
								String pregunta=bancoEstado.getNombre();
								String respuesta=bancoEstado.getValorOpcion()?"Si":"No";
								htmlSubCategorias+="<tr>"
										+ "<td " + colorCeldaHeader + " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
										+ pregunta +"</td>"
										+ "<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
										+ respuesta+"</td>"
										+ "</tr>";
							}
							SubActividades parent = subActividadesFacade.actividadParent(actividad2.getSubActividad().getId());
							SubActividades parent2=parent;
							parent2.setValorOpcion(actividad2.getPotabilizacionAgua());
							String pregunta=parent2.getNombre();
							String respuesta=parent2.getValorOpcion()?"Si":"No";
							htmlSubCategorias+="<tr>"
									+ "<td " + colorCeldaHeader + " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
									+ pregunta +"</td>"
									+ "<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
									+ respuesta+"</td>"
									+ "</tr>";

							if(parent2.getValorOpcion())
							{
								htmlSubCategorias+="<tr>" + 
										"<td " + colorCeldaHeader + " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">Opción seleccionada</td>\n" + 
										"<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
										+actividad2.getSubActividad().getNombre()+"</td>" + 
										"</tr>";
							}

						}
						else if(alcantarillado)
						{
							SubActividades parent = subActividadesFacade.actividadParent(actividad2.getSubActividad().getSubActividades()==null?actividad2.getSubActividad().getId():actividad2.getSubActividad().getSubActividades().getId());
							SubActividades parent2=parent;
							parent2.setValorOpcion(actividad2.getAlcantarilladoMunicipio());
							String pregunta=parent2.getNombre();
							String respuesta=parent2.getValorOpcion()?"Si":"No";
							htmlSubCategorias+="<tr>"
									+ "<td " + colorCeldaHeader + " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
									+ pregunta +"</td>"
									+ "<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
									+ respuesta+"</td>"
									+ "</tr>";

							if(!parent2.getValorOpcion())
							{
								htmlSubCategorias+="<tr>" + 
										"<td " + colorCeldaHeader + " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">Opción seleccionada</td>\n" + 
										"<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
										+actividad2.getSubActividad().getNombre()+"</td>" + 
										"</tr>";
							}						
						}
						else
						{
							if(!actividad2.getSubActividad().getEsMultiple())
							{
								String pregunta=actividad2.getSubActividad().getNombre();
								if(actividad2.getValorOpcion()!=null) {
								String respuesta=actividad2.getValorOpcion()?"Si":"No";
								htmlSubCategorias+="<tr>"
										+ "<td " + colorCeldaHeader + " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
										+ pregunta +"</td>"
										+ "<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
										+ respuesta+"</td>"
										+ "</tr>";
								}else 
									htmlSubCategorias+="<tr>" + 
											"<td " + colorCeldaHeader + " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">Opción seleccionada</td>\n" + 
											"<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
											+actividad2.getSubActividad().getNombre()+"</td>" + 
											"</tr>";
							}
							else
							{
								
								if(actividad2.getSubActividad().getSubActividades()!=null) {
									htmlSubCategorias+="<tr>" + 
											"<td " + colorCeldaHeader + " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">Opción seleccionada</td>\n" + 
											"<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
											+actividad2.getSubActividad().getSubActividades().getNombre()+" - "+actividad2.getSubActividad().getNombre()+"</td>" + 
											"</tr>";
									
								}else
									htmlSubCategorias+="<tr>" + 
										"<td " + colorCeldaHeader + " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">Opción seleccionada</td>\n" + 
										"<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
										+actividad2.getSubActividad().getNombre()+"</td>" + 
										"</tr>";
							}
						}
						htmlSubCategorias+="</table>";
						actividades+=htmlSubCategorias;
					}
					} else {
						if(actividad2.getFinanciadoBancoDesarrollo() != null && actividad2.getIdActividadFinanciadoBancoEstado() != null) {
							SubActividades bancoEstado = subActividadesFacade.actividadParent(actividad2.getIdActividadFinanciadoBancoEstado());
							String valorOpcion = (actividad2.getFinanciadoBancoDesarrollo()) ? "SI" : "NO";
							htmlSubCategorias+="<br /><table cellspacing=\"0\" border=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:12px;\">";
							htmlSubCategorias += "<tr>"
									+ "<td "
									+ colorCeldaHeader
									+ " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\"> "
									+ bancoEstado.getNombre()
									+ "</td>\n"
									+ "<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
									+ valorOpcion
									+ "</td>" + "</tr>";
							
							htmlSubCategorias+="</table>";
							actividades+=htmlSubCategorias;
						}
					}
				}
				//--------------------------------------------------
				actividades += "</td></tr>";
			}
			
			if(ciiuComplementaria2.getId() != null){
				existenComplementarias = true;
				String actividad = (ciiuComplementaria2.getId() == null) ? "" : ciiuComplementaria2.getNombre();
				actividades += "<tr><td " + colorCeldaHeader + ">Actividad complementaria 2 CIIU </td>";
				actividades += "<td>" + actividad;
				//informacion de las sub actividades----------------
				String htmlSubCategorias="";
				if(subActividadesCiiuBean.esActividadRecupercionMateriales(ciiuComplementaria2)) {

				}
				else
				{
					List<ProyectoCoaCiuuSubActividades> cargarSubActividades=proyectoCoaCiuuSubActividadesFacade.cargarSubActividades(actividad3);
					boolean requiereTablaResiduo3=false;
					if(cargarSubActividades.size()>0)
					{	
						htmlSubCategorias+="<br /><table cellspacing=\"0\" border=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:12px;\">";
						for(ProyectoCoaCiuuSubActividades subSelecionada: cargarSubActividades)
						{
							if(subSelecionada.getRespuesta()!=null && !subSelecionada.getRespuesta())
							{	
								String pregunta=subSelecionada.getSubActividad().getNombre();
								String respuesta=subSelecionada.getRespuesta()?"Si":"No";
								htmlSubCategorias+="<tr>"
										+ "<td " + colorCeldaHeader + " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
										+ pregunta +"</td>"
										+ "<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
										+ respuesta+"</td>"
										+ "</tr>";
							}
							else if(subSelecionada.getRespuesta()!=null && subSelecionada.getRespuesta())
							{
								String pregunta=subSelecionada.getSubActividad().getNombre();
								String respuesta=subSelecionada.getRespuesta()?"Si":"No";
								htmlSubCategorias+="<tr>"
										+ "<td " + colorCeldaHeader + " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
										+ pregunta +"</td>"
										+ "<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
										+ respuesta+"</td>"
										+ "</tr>";

							}
							else
							{	
								String pregunta="Pregunta seleccionada";
								String respuesta=subSelecionada.getSubActividad().getNombre();
								htmlSubCategorias+="<tr>"
										+ "<td " + colorCeldaHeader + " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
										+ pregunta +"</td>"
										+ "<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
										+ respuesta+"</td>"
										+ "</tr>";
							}
							if(!requiereTablaResiduo3)
								requiereTablaResiduo3=subSelecionada.getSubActividad().getRequiereIngresoResiduos()==null?false:subSelecionada.getSubActividad().getRequiereIngresoResiduos();
						}
						if(requiereTablaResiduo3)
						{
							htmlSubCategorias+="<tr>";
							residuosActividadesCiiuBean.buscarResiduosActividades(actividad3, 3);
							htmlSubCategorias+="<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:12px;text-align: center;\">"
									+"<tr>"
									+ "<td bgcolor=\"#C5C5C5\" colspan=\"3\">Identificación de residuos o desechos no peligrosos o especiales no peligrosos</td>"
									+ "</tr>"											
									+"<tr>"
									+"<td bgcolor=\"#C5C5C5\">Código</td>"
									+ "<td bgcolor=\"#C5C5C5\">Nombre del Residuo o desecho no peligroso especial no peligroso</td>"
									+"<td bgcolor=\"#C5C5C5\">Cantidad anual estimada en kg</td>"
									+ "	</tr>";
							for(ProyectoLicenciaCoaCiiuResiduos residuos: residuosActividadesCiiuBean.getListaCiiuResiduos3())
							{
								htmlSubCategorias+="<tr>"
										+"<td>"+residuos.getDesechoPeligroso().getClave()+"</td>"
										+"<td>"+residuos.getDesechoPeligroso().getDescripcion()+"</td>"			
										+"<td>"+residuos.getCantidadAnual()+"</td>"
										+"</tr>";
							}
							htmlSubCategorias+="</table>";
							htmlSubCategorias+="</tr>";
						}
						htmlSubCategorias+="</table>";
						actividades+=htmlSubCategorias;	
					}
					else if(actividad3.getSubActividad()!=null)
					{
						if(ciiuComplementaria2.getTipoPregunta() != null) {
							htmlSubCategorias += cargarDatosConsideraciones(ciiuComplementaria2.getTipoPregunta(), actividad3);
							actividades += htmlSubCategorias;
						} else {
						htmlSubCategorias+="<br /><table cellspacing=\"0\" border=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:12px;\">";

						boolean doblePregunta=false;
						String actividadesDoblePregunta= Constantes.getActividadesDoblePregunta();				
						String[] actividadesDoblePreguntaArray = actividadesDoblePregunta.split(",");				
						for (String acti : actividadesDoblePreguntaArray) {							
							if(actividad3.getCatalogoCIUU().getCodigo().equals(acti))
							{
								doblePregunta=true;
							}
						}
						boolean alcantarillado=false;
						String actividadAlcantarillado= Constantes.getActividadesZonaRural();				
						String[] actividadesAlcantarilladoArray = actividadAlcantarillado.split(",");				
						for (String activid3 : actividadesAlcantarilladoArray) {
							if(actividad3.getCatalogoCIUU().getCodigo().equals(activid3))
							{
								alcantarillado=true;
							}					
						}
						if(doblePregunta)
						{
							if(actividad3.getFinanciadoBancoDesarrollo()!=null && actividad3.getFinanciadoBancoDesarrollo())
							{
								SubActividades bancoEstado=subActividadesFacade.actividadParent(actividad3.getIdActividadFinanciadoBancoEstado());
								bancoEstado.setValorOpcion(actividad3.getFinanciadoBancoDesarrollo());
								String pregunta=bancoEstado.getNombre();
								String respuesta=bancoEstado.getValorOpcion()?"Si":"No";
								htmlSubCategorias+="<tr>"
										+ "<td " + colorCeldaHeader + " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
										+ pregunta +"</td>"
										+ "<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
										+ respuesta+"</td>"
										+ "</tr>";
							}
							SubActividades parent = subActividadesFacade.actividadParent(actividad3.getSubActividad().getId());
							SubActividades parent3=parent;
							parent3.setValorOpcion(actividad3.getPotabilizacionAgua());
							String pregunta=parent3.getNombre();
							String respuesta=parent3.getValorOpcion()?"Si":"No";
							htmlSubCategorias+="<tr>"
									+ "<td " + colorCeldaHeader + " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
									+ pregunta +"</td>"
									+ "<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
									+ respuesta+"</td>"
									+ "</tr>";

							if(parent3.getValorOpcion())
							{
								htmlSubCategorias+="<tr>" + 
										"<td " + colorCeldaHeader + " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">Opción seleccionada</td>\n" + 
										"<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
										+actividad3.getSubActividad().getNombre()+"</td>" + 
										"</tr>";
							}						
						}
						else if(alcantarillado)
						{
							SubActividades parent = subActividadesFacade.actividadParent(actividad3.getSubActividad().getSubActividades()==null?actividad3.getSubActividad().getId():actividad3.getSubActividad().getSubActividades().getId());
							SubActividades parent3=parent;
							parent3.setValorOpcion(actividad3.getAlcantarilladoMunicipio());
							String pregunta=parent3.getNombre();
							String respuesta=parent3.getValorOpcion()?"Si":"No";
							htmlSubCategorias+="<tr>"
									+ "<td " + colorCeldaHeader + " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
									+ pregunta +"</td>"
									+ "<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
									+ respuesta+"</td>"
									+ "</tr>";

							if(!parent3.getValorOpcion())
							{
								htmlSubCategorias+="<tr>" + 
										"<td " + colorCeldaHeader + " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">Opción seleccionada</td>\n" + 
										"<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
										+actividad3.getSubActividad().getNombre()+"</td>" + 
										"</tr>";
							}						
						}
						else
						{
							if(!actividad3.getSubActividad().getEsMultiple())
							{
								String pregunta=actividad3.getSubActividad().getNombre();
								if(actividad3.getValorOpcion()!=null) {
									String respuesta=actividad3.getValorOpcion()?"Si":"No";
									htmlSubCategorias+="<tr>"
											+ "<td " + colorCeldaHeader + " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
											+ pregunta +"</td>"
											+ "<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
											+ respuesta+"</td>"
											+ "</tr>";
								}else
									htmlSubCategorias+="<tr>" + 
											"<td " + colorCeldaHeader + " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">Opción seleccionada</td>\n" + 
											"<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
											+actividad3.getSubActividad().getNombre()+"</td>" + 
											"</tr>";
							}
							else
							{
								if(actividad3.getSubActividad().getSubActividades()!=null) {
									htmlSubCategorias+="<tr>" + 
											"<td " + colorCeldaHeader + " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">Opción seleccionada</td>\n" + 
											"<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
											+actividad3.getSubActividad().getSubActividades().getNombre()+" - "+actividad3.getSubActividad().getNombre()+"</td>" + 
											"</tr>";
									
								}else
								htmlSubCategorias+="<tr>" + 
										"<td " + colorCeldaHeader + " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">Opción seleccionada</td>\n" + 
										"<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
										+actividad3.getSubActividad().getNombre()+"</td>" + 
										"</tr>";
							}
						}
						htmlSubCategorias+="</table>";
						actividades+=htmlSubCategorias;
					}
					} else {
						if(actividad3.getFinanciadoBancoDesarrollo() != null && actividad3.getIdActividadFinanciadoBancoEstado() != null) {
							SubActividades bancoEstado = subActividadesFacade.actividadParent(actividad3.getIdActividadFinanciadoBancoEstado());
							String valorOpcion = (actividad3.getFinanciadoBancoDesarrollo()) ? "SI" : "NO";
							htmlSubCategorias+="<br /><table cellspacing=\"0\" border=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:12px;\">";
							htmlSubCategorias += "<tr>"
									+ "<td "
									+ colorCeldaHeader
									+ " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\"> "
									+ bancoEstado.getNombre()
									+ "</td>\n"
									+ "<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
									+ valorOpcion
									+ "</td>" + "</tr>";
							
							htmlSubCategorias+="</table>";
							actividades+=htmlSubCategorias;
						}
					}
				}
				//--------------------------------------------------
				actividades += "</td></tr>";
			}
			
			if(!existenComplementarias) {
				actividades += "<tr>";
				actividades += "<td " + colorCeldaHeader + ">Actividad complementaria</td>";
				actividades += "<td>Operador no ha seleccionado las actividades complementarias" ;
				actividades += "</td></tr>";
			}
			//
			actividades += "</table>";
			
			//Magnitud actividad 
			String magnitud = "<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:12px;\" ";
			if(valorMagnitud1.getId() != null){
				magnitud += "<tr>";
				magnitud += "<td " + colorCeldaHeader + " width=\"20%\">Por consumo / ingresos</td>";
				magnitud += "<td width=\"50%\">";
				magnitud += valorMagnitud1.getCriterioMagnitud().getNombre();
				magnitud += "</td>";
				magnitud += "<td " + colorCeldaHeader + " width=\"10%\">Rango</td>";
				magnitud += "<td width=\"20%\">";
				magnitud += valorMagnitud1.getRango();
				magnitud += "</td>";
				magnitud += "</tr>";
			}
			if(valorMagnitud2.getId() != null){
				magnitud += "<tr>";
				magnitud += "<td " + colorCeldaHeader + " width=\"20%\">Por dimensionamiento</td>";
				magnitud += "<td width=\"50%\">";
				magnitud += valorMagnitud2.getCriterioMagnitud().getNombre();
				magnitud += "</td>";
				magnitud += "<td " + colorCeldaHeader + " width=\"10%\">Rango</td>";
				magnitud += "<td width=\"20%\">";
				magnitud += valorMagnitud2.getRango();
				magnitud += "</td>";
				magnitud += "</tr>";
			}
			
			if(valorMagnitud3.getId() != null){
				magnitud += "<tr>";
				magnitud += "<td " + colorCeldaHeader + " width=\"20%\">Por capacidad</td>";
				magnitud += "<td width=\"50%\">";
				magnitud += valorMagnitud3.getCriterioMagnitud().getNombre();
				magnitud += "</td>";
				magnitud += "<td " + colorCeldaHeader + " width=\"10%\">Rango</td>";
				magnitud += "<td width=\"20%\">";
				magnitud += valorMagnitud3.getRango();
				magnitud += "</td>";
				magnitud += "</tr>";
			}
			
			magnitud += "</table>";	
			
			String tipoTramite = "";
			String impactoAmbiental = "";
			
			if(proyecto.getCategorizacion() == 1) {
				tipoTramite = "Certificado Ambiental";
				impactoAmbiental = "Impacto NO SIGNIFICATIVO";
			} else if(proyecto.getCategorizacion() == 2) {
				tipoTramite = "Registro Ambiental";
				impactoAmbiental = "Impacto BAJO";
			} else if(proyecto.getCategorizacion() == 3 || proyecto.getCategorizacion() == 4) {
				tipoTramite = "Licencia Ambiental";
				impactoAmbiental = (proyecto.getCategorizacion() == 3) ? "Impacto MEDIO" : "Impacto ALTO";
			}
			
			//Coordenadas geográficas
			String tableCoordenadas = "<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 80%; border-collapse:collapse;font-size:12px;\" ";
			String headerTableCoordenadas = "<tr><td style=\"text-align: center;\">Área Geográfica</td>"
					+"<td style=\"text-align: center;\">Shape</td>"
					+ "<td style=\"text-align: center;\">X</td>"
					+ "<td style=\"text-align: center;\">Y</td></tr>";
			
			String coordenadaGeografica = "";
			for(CoordendasPoligonos coordenadasPol : coordinatesWrappersGeo){
				coordenadaGeografica += tableCoordenadas;
				coordenadaGeografica += headerTableCoordenadas;
				
				if(coordenadasPol != null){
					for(CoordenadasProyecto coordenada : coordenadasPol.getCoordenadas()){
						String nroArea = (coordenada.getAreaGeografica() == null) ? "1" : coordenada.getAreaGeografica().toString();
						coordenadaGeografica += "<tr><td>" + nroArea + "</td><td>" + coordenada.getOrdenCoordenada() + "</td><td>" + coordenada.getX().toString() + "</td><td>" + coordenada.getY().toString() + "</td></tr>";
					}						
				}	
				coordenadaGeografica += "</table>";
			}
			
			//Coordenadas implantacion
			
			String coordenadasImplantacion = "";
			if(coordinatesWrappers != null){
				for(CoordendasPoligonos coordenadasPol : coordinatesWrappers){
					coordenadasImplantacion += tableCoordenadas;
					coordenadasImplantacion += headerTableCoordenadas;
					
					if(coordenadasPol != null){
						for(CoordenadasProyecto coordenada : coordenadasPol.getCoordenadas()){
							String nroArea = (coordenada.getAreaGeografica() == null) ? "1" : coordenada.getAreaGeografica().toString();
							coordenadasImplantacion += "<tr><td>" + nroArea + "</td><td>"  + coordenada.getOrdenCoordenada() + "</td><td>" + coordenada.getX().toString() + "</td><td>" + coordenada.getY().toString() + "</td></tr>";
						}						
					}	
					coordenadasImplantacion += "</table>";
				}
			}
			
			
			//informacion proyecto
			String informacionProyecto="";
			List<ProyectoLicenciaAmbientalConcesionesMineras> listaConcesionesMineras=proyectoLicenciaAmbientalConcesionesMinerasFacade.cargarConcesiones(proyecto);			
			Iterator<ProyectoLicenciaAmbientalCoaCiuuBloques> iteratorPB = proyectoLicenciaAmbientalCoaCiuuBloquesFacade.cargarBloques(proyecto).iterator();
			while (iteratorPB.hasNext()) {
				bloquesBean.setBloqueSeleccionado(iteratorPB.next().getBloque());
			}
			
			String tablesConcesiones="";
			if(listaConcesionesMineras.size()>0)
			{
				tablesConcesiones="<table cellspacing=\"0\" border=\"0\" style=\"width: 80%; border-collapse:collapse;font-size:12px;\">\n" + 
						"	<colgroup  span=\"4\"></colgroup>\n" + 
						"	<tbody>\n" + 
						"	<tr bgcolor=\"#C5C5C5\">\n" + 
						"		<td style=\"border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" colspan=\"4\" valign=\"middle\"  align=\"center\">CONCESIONES MINERAS</td>\n" + 
						"	</tr>";				
				for(ProyectoLicenciaAmbientalConcesionesMineras concesiones : listaConcesionesMineras)
				{
					tablesConcesiones+="<tr>\n" + 
							"		<td style=\"width: 25%; border-left: 1px solid #000000\"  align=\"left\">Nombre</td>\n" + 
							"		<td style=\"width: 25%;\" align=\"left\">"+concesiones.getNombre()+"</td>\n" + 
							"		<td style=\"width: 25%;\" align=\"left\">Régimen</td>\n" + 
							"		<td style=\"width: 25%; border-right: 1px solid #000000\" align=\"left\">"+concesiones.getRegimen()+"</td>\n" + 
							"	</tr>\n" + 
							"	<tr>\n" + 
							"		<td style=\"width: 25%; border-left: 1px solid #000000\" align=\"left\">Código</td>\n" + 
							"		<td style=\"width: 25%;\" align=\"left\">"+concesiones.getCodigo()+"</td>\n" + 
							"		<td style=\"width: 25%;\" align=\"left\">Área</td>\n" + 
							"		<td style=\"width: 25%; border-right: 1px solid #000000\" align=\"left\">"+concesiones.getArea()+" ha</td>\n" + 
							"	</tr>\n" + 
							"	<tr>\n" + 
							"		<td style=\"width: 25%; border-bottom: 1px solid #000000; border-left: 1px solid #000000\"  align=\"left\">Material</td>\n" + 
							"		<td style=\"width: 75%; border-bottom: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\" colspan=\"3\">"+concesiones.getMaterial()+"</td>\n" + 
							"	</tr>";
				}
				
				tablesConcesiones+="</tbody></table><br/>";
			}
			informacionProyecto+=tablesConcesiones;
			
			String tablaBloques="";
			if(bloquesBean.getBloquesSeleccionados().size()>0)
			{		
				tablaBloques+="<table cellspacing=\"0\" border=\"0\" style=\"width: 80%; border-collapse:collapse;font-size:12px;\">\n" + 
						"	<colgroup span=\"2\"></colgroup>\n" + 
						"	<tbody><tr bgcolor=\"#C5C5C5\">\n" + 
						"		<td style=\"border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" colspan=\"2\" valign=\"middle\" align=\"center\">BLOQUES</td>\n" + 
						"		</tr>\n" + 
						"	<tr>\n" + 
						"		<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">No. Bloque</td>\n" + 
						"		<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">Denominación del área</td>\n" + 
						"	</tr>";
				for(Bloque bloques: bloquesBean.getBloquesSeleccionados())
				{
					tablaBloques+="<tr>" + 
							"		<td style=\"border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"+bloques.getNombre()+"</td>\n" + 
							"		<td style=\"border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"+bloques.getDenominacionArea()+"</td>\n" + 
							"	</tr>";					
				}
				tablaBloques+="</tbody></table><br/>";
			}
			informacionProyecto+=tablaBloques;
			
			String generaDesechos = proyecto.getGeneraDesechos() ? "Si" : "No";			
			String gestionResiduos = proyecto.getGestionDesechos() ? "Si" : "No";
			String remocionCobertura = proyecto.getRenocionCobertura() ? "Si" : "No";
			String transporteSustancias = proyecto.getTransportaSustanciasQuimicas() ? "Si":"No";
			String altoImpacto = proyecto.getAltoImpacto() ? "Si" : "No";
			String empleoSustancias = proyecto.getSustanciasQuimicas() ? "Si": "No";
			
			String formatoCeldaSiNo = "width=\"15%\" style=\"text-align: center;\" ";
			informacionProyecto +=  "<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 80%; border-collapse:collapse;font-size:12px;\" ";
			informacionProyecto += "<tr><td>Generación de residuos o desechos peligrosos y/o especiales</td><td " + formatoCeldaSiNo + ">" + generaDesechos + "</td></tr>";
			informacionProyecto += "<tr><td>Gestión de residuos o desechos peligrosos y/o especiales</td><td " + formatoCeldaSiNo + ">" + gestionResiduos + "</td></tr>";
			informacionProyecto += "<tr><td>Remoción de cobertura vegetal nativa</td><td " + formatoCeldaSiNo + ">" + remocionCobertura + "</td></tr>";
			informacionProyecto += "<tr><td>Transporte de sustancias químicas</td><td " + formatoCeldaSiNo + ">" + transporteSustancias + "</td></tr>";
			informacionProyecto += "<tr><td>Proyecto declarado de alto impacto ambiental o interés nacional</td><td " + formatoCeldaSiNo + ">" + altoImpacto + "</td></tr>";
			informacionProyecto += "<tr><td>Fabrica, usa o almacena sustancia químicas</td><td " + formatoCeldaSiNo + ">" + empleoSustancias + "</td></tr>";
			informacionProyecto += "</table>";
			
			//sustanciasQumicas
			String sustanciasQuimicas = "";
			if(proyecto.getSustanciasQuimicas()){
				sustanciasQuimicas += "<table><tr><td>N°</td><td>Sustancias químicas</td></tr>";
				int j = 0;
				for(SustanciaQuimicaPeligrosa sustancia : sustanciaQuimicaSeleccionada){
					sustanciasQuimicas += "<tr><td>" + j +1 +"</td><td>" + sustancia.getDescripcion() +"</td></tr>";
				}
				sustanciasQuimicas += "</table>";
			}
			
			List<Contacto> contactos = null;
			cargarDatosUsuario();
			promotorBean.setOrganizacion(organizacionFacade.buscarPorPersona(promotorBean.getPersona(), promotorBean.getUsuario().getNombre()));
			
			if (promotorBean.getOrganizacion() != null && promotorBean.getOrganizacion().getRuc().trim().equals(promotorBean.getUsuario().getNombre())) {
	            
	            contactos = contactoFacade.buscarPorOrganizacion(promotorBean
	                    .getOrganizacion());
	        } else {
	           
	            contactos = contactoFacade.buscarPorPersona(promotorBean
	                    .getPersona());
	        }
			boolean multiplesCorreos = false;
			  for (int i = 0; i < contactos.size(); i++) {
		            switch (contactos.get(i).getFormasContacto().getNombre()) {
		                case "*TELÉFONO":
		                    promotorBean.setTelefono(contactos.get(i).getValor());
		                    contactos.remove(i);
		                    i--;
		                    break;
		                case "*TEL├ëFONO":
		                    promotorBean.setTelefono(contactos.get(i).getValor());
		                    contactos.remove(i);
		                    i--;
		                    break;
		                case "*DIRECCI├ôN":
		                    promotorBean.setDireccion(contactos.get(i).getValor());
		                    contactos.remove(i);
		                    i--;
		                    break;
		                case "*DIRECCIÓN":
		                    promotorBean.setDireccion(contactos.get(i).getValor());
		                    contactos.remove(i);
		                    i--;
		                    break;
		                case "*EMAIL":
		                    if (!multiplesCorreos) {
		                        correoTemporal = contactos.get(i).getValor();
		                        promotorBean.setEmail(contactos.get(i).getValor());
		                        contactos.remove(i);
		                        multiplesCorreos = true;
		                        i--;
		                    }
		                    break;
		                case "*CELULAR":
		                	String celular=(contactos.get(i).getValor()!=null)?contactos.get(i).getValor():"";
		                    promotorBean.setCelular(celular);
		                    contactos.remove(i);
		                    i--;
		                    break;
		                default:
		                    break;
		            }
		        }
			  
			List<String> datosOperador = usuarioFacade.recuperarNombreOperador(proyecto.getUsuario());
			String nombreOperador = datosOperador.get(0);
			String representanteLegal = datosOperador.get(1);
				
						
			PlantillaReporte plantillaReporte = this.informeProvincialGADFacade.obtenerPlantillaReporte(TipoDocumentoSistema.REPORTE_INFORMACION_PRELIMINAR.getIdTipoDocumento());
			EntityInformePreliminar entityInforme = new EntityInformePreliminar();

			entityInforme.setCodigo(proyecto.getCodigoUnicoAmbiental());
			entityInforme.setFechaRegistro(proyecto.getFechaGeneracionCua() != null ? getDateFormat(proyecto.getFechaGeneracionCua()) : "");
			entityInforme.setOperador(nombreOperador);
			entityInforme.setEnteResponsable(proyecto.getAreaResponsable().getAreaName());
			entityInforme.setSector("");
			entityInforme.setTelefonoFijo(promotorBean.getTelefono());
			entityInforme.setCelular(promotorBean.getCelular());
			entityInforme.setEmail(promotorBean.getEmail());
			entityInforme.setSuperficie(proyecto.getSuperficie().toString());
			entityInforme.setNombreProyecto(proyecto.getNombreProyecto());
			entityInforme.setResumenProyecto(proyecto.getDescripcionProyecto());
			entityInforme.setActividades(actividades);
	        entityInforme.setMagnitud(magnitud);
	        entityInforme.setTipoTramite(tipoTramite);
	        entityInforme.setUbicacionGeografica(ubicacionCompleta);
	        entityInforme.setTipoZona(proyecto.getTipoPoblacion().getNombre());
	        entityInforme.setCoordenadasGeograficas(coordenadaGeografica);
	        entityInforme.setCoordenadasImplantacion(coordenadasImplantacion);
	        entityInforme.setInformacionProyecto(informacionProyecto);
	        entityInforme.setSustanciasQuimicas(sustanciasQuimicas);
	        entityInforme.setDisplaySustancias((sustanciasQuimicas == "") ? "none" : "inline");
	        entityInforme.setRepresentanteLegal(representanteLegal);
	        entityInforme.setImpactoAmbiental(impactoAmbiental);
	        
	        if(proyecto.getDireccionProyecto() != null && !proyecto.getDireccionProyecto().isEmpty()) {
	        	entityInforme.setDireccionProyecto(proyecto.getDireccionProyecto());
	        	entityInforme.setDisplayDireccion("inline");
	        } else {
	        	entityInforme.setDireccionProyecto("");
	        	entityInforme.setDisplayDireccion("none");
	        }
	        
	        if(mostrarOperador)
	        	entityInforme.setDisplayOperador("inline");
	        else
	        	entityInforme.setDisplayOperador("none");
	            
	        nombreReporte = "ReporteInformacionPreliminar.pdf";				

			setInformePdf(informePdf);
			
			 
			File informePdf = UtilGenerarInforme.generarFichero(
					plantillaReporte.getHtmlPlantilla(),
					nombreReporte, true, entityInforme);
					

			Path path = Paths.get(informePdf.getAbsolutePath());
			String reporteHtmlfinal = nombreReporte;
			archivoInforme = Files.readAllBytes(path);
			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(Files.readAllBytes(path));
			file.close();
			
			TipoDocumento tipoDoc = new TipoDocumento();
			tipoDoc.setId(TipoDocumentoSistema.REPORTE_INFORMACION_PRELIMINAR.getIdTipoDocumento());
			
			DocumentosCOA documento = new DocumentosCOA();
			documento.setNombreDocumento("ReporteInformacionPreliminar.pdf");
			documento.setExtencionDocumento(".pdf");		
			documento.setTipo("application/pdf");
			documento.setContenidoDocumento(archivoInforme);
			documento.setNombreTabla(ProyectoLicenciaCoa.class.getSimpleName());
			documento.setTipoDocumento(tipoDoc);
			documento.setIdTabla(proyecto.getId());
			documento.setProyectoLicenciaCoa(proyecto);
			if(isDocProceso)
				documento.setIdProceso(JsfUtil.getCurrentProcessInstanceId());

			documento = documentosFacade.guardarDocumentoAlfresco(proyecto.getCodigoUnicoAmbiental(), "INFORMACION_PRELIMINAR", 0L, documento, TipoDocumentoSistema.REPORTE_INFORMACION_PRELIMINAR);
			
								
	        } catch (Exception e) {
	            this.LOG.error("Error al visualizar el reporte de información preliminar", e);
	            JsfUtil.addMessageError("Error al visualizar el informe técnico");
	        }
	        return html;
	    }
	 
	 private void cargarDatosUsuario() {
	        try {
	        	promotorBean = new PromotorBean();
	            promotorBean.setUsuario(usuarioFacade.buscarUsuarioPorId(loginBean
	                    .getUsuario().getId()));
	            promotorBean.setPersona(promotorBean.getUsuario().getPersona());	            
	        } catch (Exception e) {
	            LOG.error(e, e);
	        }
	    }

	/**
	 * Recupera la información de las consideraciones seleccionadas en formato HTML
	 * @param tipoPregunta indica el tipo de pregunta del catalogo
	 * @param actividadCua
	 * @return
	 */
	public String cargarDatosConsideraciones(Integer tipoPregunta, ProyectoLicenciaCuaCiuu actividadCua) {
		String colorCeldaHeader = "bgcolor=\"#C5C5C5\" ";
		String htmlSubActividades = "<br /><table cellspacing=\"0\" border=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:12px;\">";

		switch (tipoPregunta) {
		case 1:
			String tituloSeleccion = (actividadCua.getSubActividad().getTituloHijos() != null) ? actividadCua.getSubActividad().getTituloHijos() : "Opción seleccionada";
			
			htmlSubActividades += "<tr>"
					+ "<td "
					+ colorCeldaHeader
					+ " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
					+ tituloSeleccion
					+ "</td>\n"
					+ "<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
					+ actividadCua.getSubActividad().getNombre()
					+ "</td>" + "</tr>";
			break;
		case 2:
			String titulo = "Opción seleccionada";			
			
			if(actividadCua.getSubActividad().getSubActividades() != null  
				&& actividadCua.getSubActividad().getSubActividades().getRequiereValidacionCoordenadas().equals(1)) {
				
				if(actividadCua.getSubActividad().getSubActividades() != null  
						&& actividadCua.getSubActividad().getSubActividades().getId() != null) {
					htmlSubActividades += "<tr>"
							+ "<td "
							+ colorCeldaHeader
							+ " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\"> "
							+ "Actividad:"
							+ "</td>\n"
							+ "<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
							+ actividadCua.getSubActividad().getSubActividades().getNombre()
							+ "</td>" + "</tr>";
				}
				
				htmlSubActividades += "<tr>"
						+ "<td "
						+ colorCeldaHeader
						+ " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
						+ "Rango de operación"
						+ "</td>\n"
						+ "<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
						+ actividadCua.getSubActividad().getNombre()
						+ "</td>" + "</tr>";
				
				if(proyecto.getZona_camaronera()!= null){
					String tipo = "";
					if(proyecto.getZona_camaronera().equals("ALTA")){
						tipo = "Tierras Privadas o Zonas Altas";
					}else if(proyecto.getZona_camaronera().equals("PLAYA")){
						tipo = "Zona de Playa y Bahía";
					}else if(proyecto.getZona_camaronera().equals("MIXTA")){
						tipo = "Zona Mixta(Tierras Privadas o Zonas Altas y Zona de Playa y Bahía)";
					}
					
					htmlSubActividades += "<tr>"
							+ "<td "
							+ colorCeldaHeader 
							+ " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
							+ "El proyecto se encuentra en:"
							+ "</td>\n"
							+ "<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
							+ tipo
							+ "</td>" + "</tr>";					
				}
			}else{
				
				if(actividadCua.getSubActividad().getSubActividades() != null  
						&& actividadCua.getSubActividad().getSubActividades().getId() != null) {
					htmlSubActividades += "<tr>"
							+ "<td "
							+ colorCeldaHeader
							+ " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\"> "
							+ titulo
							+ "</td>\n"
							+ "<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
							+ actividadCua.getSubActividad().getSubActividades().getNombre()
							+ "</td>" + "</tr>";
				}
				
				htmlSubActividades += "<tr>"
						+ "<td "
						+ colorCeldaHeader
						+ " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
						+ titulo
						+ "</td>\n"
						+ "<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
						+ actividadCua.getSubActividad().getNombre()
						+ "</td>" + "</tr>";
			}
			
			break;
		case 4:
			String titulo1 = "Su proyecto, obra o actividad corresponde a";
			String titulo2 = "Opción seleccionada";
			
			if(actividadCua.getFinanciadoBancoDesarrollo() != null) {
				SubActividades bancoEstado = subActividadesFacade.actividadParent(actividadCua.getIdActividadFinanciadoBancoEstado());
				String valorOpcion = (actividadCua.getFinanciadoBancoDesarrollo()) ? "SI" : "NO";
				htmlSubActividades += "<tr>"
						+ "<td "
						+ colorCeldaHeader
						+ " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\"> "
						+ bancoEstado.getNombre()
						+ "</td>\n"
						+ "<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
						+ valorOpcion
						+ "</td>" + "</tr>";
			}
			
			if(actividadCua.getSubActividad().getSubActividades() != null  
					&& actividadCua.getSubActividad().getSubActividades().getId() != null) {
				htmlSubActividades += "<tr>"
						+ "<td "
						+ colorCeldaHeader
						+ " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\"> "
						+ titulo1
						+ "</td>\n"
						+ "<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
						+ actividadCua.getSubActividad().getSubActividades().getNombre()
						+ "</td>" + "</tr>";
			}
			
			htmlSubActividades += "<tr>"
					+ "<td "
					+ colorCeldaHeader
					+ " style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
					+ titulo2
					+ "</td>\n"
					+ "<td style=\"width: 50%; border-top: 1px solid #000000; border-bottom: 1px solid #000000; border-left: 1px solid #000000; border-right: 1px solid #000000\" align=\"left\">"
					+ actividadCua.getSubActividad().getNombre()
					+ "</td>" + "</tr>";
			break;
		default:
			break;
		}
		
		htmlSubActividades += "</table>";
		
		return htmlSubActividades;
	}
	
}