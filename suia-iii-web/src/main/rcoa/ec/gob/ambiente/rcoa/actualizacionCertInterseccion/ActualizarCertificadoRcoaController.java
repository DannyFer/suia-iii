package ec.gob.ambiente.rcoa.actualizacionCertInterseccion;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.mapa.webservices.GenerarMapaRcoaWS_Service;
import ec.gob.ambiente.mapa.webservices.ResponseCertificado;
import ec.gob.ambiente.rcoa.certificado.interseccion.CertificadoInterseccionRcoaOficioHtml;
import ec.gob.ambiente.rcoa.certificado.interseccion.facade.CertificadoInterseccionCoaFacade;
import ec.gob.ambiente.rcoa.certificado.interseccion.model.CertificadoInterseccionOficioCoa;
import ec.gob.ambiente.rcoa.enums.CatalogoTipoCoaEnum;
import ec.gob.ambiente.rcoa.facade.CapasCoaFacade;
import ec.gob.ambiente.rcoa.facade.CatalogoCoaFacade;
import ec.gob.ambiente.rcoa.facade.CoordenadasProyectoCoaFacade;
import ec.gob.ambiente.rcoa.facade.DetalleInterseccionProyectoAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.facade.InterseccionProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaAmbientalCoaShapeFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.model.CapasCoa;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.model.CoordenadasProyecto;
import ec.gob.ambiente.rcoa.model.DetalleInterseccionProyectoAmbiental;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.InterseccionProyectoLicenciaAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaAmbientalCoaShape;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoaUbicacion;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.preliminar.contoller.CoordenadasRcoaBean;
import ec.gob.ambiente.rcoa.proyecto.controller.VerProyectoRcoaBean;
import ec.gob.ambiente.rcoa.util.CoordendasPoligonos;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoActualizacionCertificadoInterseccionFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;

@ManagedBean
@ViewScoped
public class ActualizarCertificadoRcoaController {
	
	@Setter
	@Getter
	@ManagedProperty(value = "#{coordenadasRcoaBean}")
	private CoordenadasRcoaBean coordenadasRcoaBean;
	
	@Setter
	@Getter
	@ManagedProperty(value = "#{verProyectoRcoaBean}")
	private VerProyectoRcoaBean verProyectoRcoaBean;
	
	@EJB
	private ProyectoLicenciaAmbientalCoaShapeFacade proyectoLicenciaAmbientalCoaShapeFacade;
	@EJB
	private CoordenadasProyectoCoaFacade coordenadasProyectoCoaFacade;
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	@EJB
	private InterseccionProyectoLicenciaAmbientalFacade interseccionProyectoLicenciaAmbientalFacade;
	@EJB
	private DetalleInterseccionProyectoAmbientalFacade detalleInterseccionProyectoAmbientalFacade;
	@EJB
	private CertificadoInterseccionCoaFacade certificadoInterseccionCoaFacade;
	@EJB
	private CapasCoaFacade capasCoaFacade;
	@EJB
	private DocumentosCoaFacade documentosFacade;
	@EJB
    private OrganizacionFacade organizacionFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
	@EJB
	private ProyectoActualizacionCertificadoInterseccionFacade proyectoActualizacionCIFacade;
	@EJB
	private CatalogoCoaFacade catalogoCoaFacade;
	@EJB
	private PlantillaReporteFacade plantillaReporteFacade;
	@EJB
	private AreaFacade areaFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;
	@Getter
	@Setter
	private DocumentosCOA documentoMapa, documentoCertificado;
	@Getter
	@Setter
	private CertificadoInterseccionOficioCoa oficioCI;
	@Getter
	@Setter
	private Area areaResponsableActualizada, areaUsuarioAutoridad;
	@Getter
	@Setter
	private Usuario usuarioAutoridad;
	
	private List<CapasCoa> capasCoaLista;
	
	@Getter
	@Setter
	private Boolean verCoordenadas, mostrarDetalleInterseccion;
	
	private Integer nroActualizacion;
	
	private GenerarMapaRcoaWS_Service wsMapas;
	
	@PostConstruct
	public void init() {
		verCoordenadas = false;
		
		proyecto = verProyectoRcoaBean.getProyectoLicenciaCoa();
		
		coordenadasRcoaBean.setProyecto(proyecto);
		
		capasCoaLista = capasCoaFacade.listaCapas();
		
	}
	
	public void subirCoordenadas() {
		verCoordenadas = true;
	}
	
	public void handleFileUpload(final FileUploadEvent event) {
		coordenadasRcoaBean.handleFileUpload(event);
		coordenadasRcoaBean.procesarIntersecciones();
	}
	
	public void handleFileUploadImple(final FileUploadEvent event) {
		Boolean implementacion = coordenadasRcoaBean.handleFileUploadImple(event);
		
		if(implementacion)
			actualizarInformacion();
	}
	
	public void actualizarInformacion() {
		try{
			try {			
				wsMapas = new GenerarMapaRcoaWS_Service(new URL(Constantes.getActualizarMapaRcoaWS()));
			} catch (Exception e) {
				System.out.println("Servicio no disponible ---> "+Constantes.getActualizarMapaRcoaWS());
				JsfUtil.addMessageError("Ocurrió un error al generar los documentos.");
				return;
			} 
			
			//oficina tecnica con coordenadas cargadas
			UbicacionesGeografica provincia = coordenadasRcoaBean.getUbicacionPrincipal().getUbicacionesGeografica().getUbicacionesGeografica();
			if(provincia.getCodificacionInec().equals(Constantes.CODIGO_INEC_GALAPAGOS) 
					|| coordenadasRcoaBean.getUbicacionPrincipal().getNombre().toUpperCase().contains("INSULAR"))
				areaResponsableActualizada = areaFacade.getArea(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS);
			else {
				if(coordenadasRcoaBean.getUbicacionOficinaTecnica() != null && coordenadasRcoaBean.getUbicacionOficinaTecnica().getId() != null)
					areaResponsableActualizada = areaFacade.getAreaCoordinacionZonal(coordenadasRcoaBean.getUbicacionOficinaTecnica());
			}
			
			if(areaResponsableActualizada==null || areaResponsableActualizada.getId() == null)			
			{
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
				return;
			}
			
			//buscar usuario responsable certificado
			areaUsuarioAutoridad = areaResponsableActualizada;
			String tipoRol = "role.ci.cz.autoridad";
			if(proyecto.getAreaResponsable().getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
				areaUsuarioAutoridad = areaFacade.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL);
				tipoRol = "role.ci.pc.autoridad";
			} else {
				if(areaResponsableActualizada.getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)) {
					areaUsuarioAutoridad = proyecto.getAreaResponsable();
					tipoRol = "role.ci.galapagos.autoridad";
				} 
				if(areaResponsableActualizada.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_OT))
					areaUsuarioAutoridad = areaResponsableActualizada.getArea();
				else
					areaUsuarioAutoridad = proyecto.getAreaResponsable();
			}
			
			String rol = Constantes.getRoleAreaName(tipoRol);
			List<Usuario>uList = usuarioFacade.buscarUsuariosPorRolYArea(rol, areaUsuarioAutoridad);
			if(uList==null || uList.size()==0)			
			{
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
				return;
			}else{
				usuarioAutoridad=uList.get(0);
			}
			
			//guardar coordenadas para generar el mapa
			if(nroActualizacion == null || nroActualizacion <= 0) {
				//buscar nro actualizacion
				nroActualizacion = proyectoLicenciaCoaUbicacionFacade.buscarNroUltimaActualizacion(proyecto);
				nroActualizacion++;
				
				//info geográfica
				ProyectoLicenciaAmbientalCoaShape shape= new ProyectoLicenciaAmbientalCoaShape();
				List<CoordendasPoligonos> coordinatesWrappersGeo = coordenadasRcoaBean.getCoordinatesWrappersGeo();
				for (int i = 0; i <= coordinatesWrappersGeo.size() - 1; i++) {
					shape= new ProyectoLicenciaAmbientalCoaShape();
					shape.setTipoForma(coordenadasRcoaBean.getPoligono());
					shape.setProyectoLicenciaCoa(proyecto);
					shape.setTipo(2);//2=coordenadas geograficas 1=coordenadas implantacion
					shape.setNumeroActualizaciones(nroActualizacion);
					shape.setSuperficie(coordinatesWrappersGeo.get(i).getSuperficie());
					shape=proyectoLicenciaAmbientalCoaShapeFacade.guardar(shape);
					
					CoordenadasProyecto coor= new CoordenadasProyecto();
					for (int j = 0; j <=coordinatesWrappersGeo.get(i).getCoordenadas().size()-1; j++) {
						coor= new CoordenadasProyecto();
						coor.setProyectoLicenciaAmbientalCoaShape(shape);
						coor.setOrdenCoordenada(coordinatesWrappersGeo.get(i).getCoordenadas().get(j).getOrdenCoordenada());
						coor.setX(coordinatesWrappersGeo.get(i).getCoordenadas().get(j).getX());
						coor.setY(coordinatesWrappersGeo.get(i).getCoordenadas().get(j).getY());
						coor.setAreaGeografica(coordinatesWrappersGeo.get(i).getCoordenadas().get(j).getAreaGeografica());
						coor.setTipoCoordenada(2);//2=coordenadas geograficas 1=coordenadas implantacion 
						coor.setNumeroActualizaciones(nroActualizacion);
						coordenadasProyectoCoaFacade.guardar(coor);
					}
				}
				
				//info implantación
				List<CoordendasPoligonos> coordinatesWrappers = coordenadasRcoaBean.getCoordinatesWrappers();
				
				for (int i = 0; i <= coordinatesWrappers.size() - 1; i++) {
					shape = new ProyectoLicenciaAmbientalCoaShape();
					shape.setTipoForma(coordenadasRcoaBean.getPoligono());
					shape.setProyectoLicenciaCoa(proyecto);
					shape.setTipo(1);// 2=coordenadas geograficas 1=coordendas implantacion
					shape.setNumeroActualizaciones(nroActualizacion);
					shape.setSuperficie(coordinatesWrappers.get(i).getSuperficie());
					shape = proyectoLicenciaAmbientalCoaShapeFacade.guardar(shape);
					
					CoordenadasProyecto coorImpl = new CoordenadasProyecto();
					for (int j = 0; j <= coordinatesWrappers.get(i).getCoordenadas().size() - 1; j++) {
						coorImpl = new CoordenadasProyecto();
						coorImpl.setProyectoLicenciaAmbientalCoaShape(shape);
						coorImpl.setOrdenCoordenada(coordinatesWrappers.get(i).getCoordenadas().get(j).getOrdenCoordenada());
						coorImpl.setX(coordinatesWrappers.get(i).getCoordenadas().get(j).getX());
						coorImpl.setY(coordinatesWrappers.get(i).getCoordenadas().get(j).getY());
						coorImpl.setAreaGeografica(coordinatesWrappers.get(i).getCoordenadas().get(j).getAreaGeografica());
						coorImpl.setTipoCoordenada(1);// 2=coordenadas geograficas 1=coordenadas implantacion
						coorImpl.setNumeroActualizaciones(nroActualizacion);
						coordenadasProyectoCoaFacade.guardar(coorImpl);
					}
				}

				//info ubicaciones
				for(UbicacionesGeografica ubi: coordenadasRcoaBean.getUbicacionesSeleccionadas())
				{
					ProyectoLicenciaCoaUbicacion pro = new ProyectoLicenciaCoaUbicacion();
					pro.setProyectoLicenciaCoa(proyecto);
					pro.setUbicacionesGeografica(ubi);
					pro.setNroActualizacion(nroActualizacion);
					if (coordenadasRcoaBean.getArea().getInec().equals(ubi.getCodificacionInec()))
						pro.setPrimario(true);
					else
						pro.setPrimario(false);
					proyectoLicenciaCoaUbicacionFacade.guardar(pro);
				}
				
				//info intersecciones
				for (InterseccionProyectoLicenciaAmbiental i : coordenadasRcoaBean.getCapasIntersecciones().keySet()) {
					i.setProyectoLicenciaCoa(proyecto);
					i.setFechaproceso(new Date());
					i.setNroActualizacion(nroActualizacion);
					i = interseccionProyectoLicenciaAmbientalFacade.guardar(i);
					for (DetalleInterseccionProyectoAmbiental j : coordenadasRcoaBean.getCapasIntersecciones().get(i)) {
						j.setInterseccionProyectoLicenciaAmbiental(i);
						detalleInterseccionProyectoAmbientalFacade.guardar(j);
					}
				}
				
				guardarCertificado();
			}
			
			if(nroActualizacion != null) {				
				//generar el nuevo certificado de intersección
				documentoCertificado = generarCertificadoInterseccion(true);
				if (documentoCertificado == null || documentoCertificado.getContenidoDocumento() == null)
					return;
				
				documentoMapa = new DocumentosCOA();
				Boolean resultMapa = generarMapa();
				if(!resultMapa) {
					JsfUtil.addMessageError("Ocurrió un error al generar el mapa.");
					return;
				}
				
				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('adjuntarCoordenadas').hide();");
				context.execute("PF('docActualizados').show();");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al generar los documentos.");
		}
	}
	
	public void guardarCertificado() throws Exception {
		oficioCI = certificadoInterseccionCoaFacade.obtenerPorProyectoNroActualizacion(proyecto.getCodigoUnicoAmbiental(), nroActualizacion);
		
		if (oficioCI == null) {
			oficioCI = new CertificadoInterseccionOficioCoa();				
			oficioCI.setProyectoLicenciaCoa(proyecto);
			oficioCI.setNroActualizacion(nroActualizacion);
		}
		
		List<UbicacionesGeografica> ubicacionProyectoLista = coordenadasRcoaBean.getUbicacionesSeleccionadas();			
					
		capasCoaLista=capasCoaFacade.listaCapasCertificadoInterseccion();
		
		//generacion Ubicacion
		String strTableUbicacion = "<center><table border=\"1\" cellpadding=\"5\" cellspacing=\"0\" class=\"w600Table\" style=\"width: 100%;border-collapse:collapse;font-size:12px;font-family: arial,helvetica,sans-serif;\">"
				+ "<tbody><tr BGCOLOR=\"#B2B2B2\">"			
				+ "<td><strong>Provincia</strong></td>"
				+ "<td><strong>Cantón</strong></td>"
				+ "<td><strong>Parroquia</strong></td>"
				+ "</tr>";
		
		for (UbicacionesGeografica ubicacion : ubicacionProyectoLista) {
			strTableUbicacion += "<tr>";
			strTableUbicacion += "<td>" + ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre() + "</td>";
			strTableUbicacion += "<td>" + ubicacion.getUbicacionesGeografica().getNombre() + "</td>";
			strTableUbicacion += "<td>" + ubicacion.getNombre() + "</td>";
			strTableUbicacion += "</tr>";
		}		
		strTableUbicacion += "</tbody></table></center>";
		
		//generacion Capas
		String strTableCapas = "";		
		for (CapasCoa capa : capasCoaLista) {			
			String fecha = "ND";
			if(capa.getFechaActualizacionCapa() != null)
				fecha = JsfUtil.getSimpleDateFormat(capa.getFechaActualizacionCapa());
			strTableCapas += capa.getNombre() + " (" + fecha + ")<br/>";			
		}
		
		//generacion interseccion
		List<String> detalleIntersecaCapasViabilidad=new ArrayList<String>();
		List<String> detalleIntersecaOtrasCapas=new ArrayList<String>();
		for (InterseccionProyectoLicenciaAmbiental i : coordenadasRcoaBean.getCapasIntersecciones().keySet()) {
			for (DetalleInterseccionProyectoAmbiental detalleInterseccion : coordenadasRcoaBean.getCapasIntersecciones().get(i)) {
				String capaDetalle=detalleInterseccion.getInterseccionProyectoLicenciaAmbiental().getCapa().getNombre()+": "+detalleInterseccion.getNombreGeometria();
				if(detalleInterseccion.getCodigoConvenio() != null)
					capaDetalle=detalleInterseccion.getInterseccionProyectoLicenciaAmbiental().getCapa().getNombre()+": "+detalleInterseccion.getNombreGeometria() + " (" + detalleInterseccion.getCodigoConvenio() + ")";
				
				if(detalleInterseccion.getInterseccionProyectoLicenciaAmbiental().getCapa().getViabilidad()){
					if(!detalleIntersecaCapasViabilidad.contains(capaDetalle))
						detalleIntersecaCapasViabilidad.add(capaDetalle);
				}else{				
					if(!detalleIntersecaOtrasCapas.contains(capaDetalle))
						detalleIntersecaOtrasCapas.add(capaDetalle);
				}
			}
		}
		
		String strTableIntersecaViabilidad = "";		
		for (String detalleInterseca : detalleIntersecaCapasViabilidad) {			
			strTableIntersecaViabilidad += detalleInterseca+"<br/>";			
		}
		
		String strTableOtrasIntersecciones = "";		
		for (String detalleInterseca : detalleIntersecaOtrasCapas) {			
			strTableOtrasIntersecciones += detalleInterseca+"<br/>";			
		}
		
		oficioCI.setUbicacion(strTableUbicacion);
		oficioCI.setCapas(strTableCapas);
		oficioCI.setInterseccionViabilidad(strTableIntersecaViabilidad);
		oficioCI.setOtraInterseccion(strTableOtrasIntersecciones);
		oficioCI.setAreaUsuarioFirma(areaUsuarioAutoridad.getId());
    	oficioCI.setUsuarioFirma(usuarioAutoridad.getNombre());
		oficioCI = certificadoInterseccionCoaFacade.guardarActualizacion(oficioCI);
		
	}
	
	public Boolean generarMapa()
	{
		
		ResponseCertificado resCer = new ResponseCertificado();
		resCer=wsMapas.getGenerarMapaRcoaWSPort().generarCertificadoInterseccionRcoa(proyecto.getCodigoUnicoAmbiental(), nroActualizacion);
		if (resCer.getWorkspaceAlfresco() == null) {
			return false;
		} else {
			TipoDocumento tipoDocumento = new TipoDocumento();
			tipoDocumento.setId(TipoDocumentoSistema.RCOA_CERTIFICADO_INTERSECCION_MAPA_ACTUALIZADO.getIdTipoDocumento());
			documentoMapa = new DocumentosCOA();
			documentoMapa.setIdAlfresco(resCer.getWorkspaceAlfresco());
			documentoMapa.setExtencionDocumento(".pdf");		
			documentoMapa.setTipo("application/pdf");
			documentoMapa.setTipoDocumento(tipoDocumento);
			documentoMapa.setNombreTabla(ProyectoLicenciaCoa.class.getSimpleName());
			documentoMapa.setNombreDocumento("Mapa_Certificado_intersección_actualizado.pdf");
			documentoMapa.setIdTabla(proyecto.getId());
			documentoMapa.setProyectoLicenciaCoa(proyecto);
		}
		
		return true;
	}
	
	public DocumentosCOA generarCertificadoInterseccion(Boolean marcaAgua) {			
		FileOutputStream fileOutputStream;
		try {
			String nombreReporte= "Certificado_Intersección";
			
			
			Usuario uOperador = proyecto.getUsuario();
			Organizacion orga = organizacionFacade.buscarPorRuc(proyecto.getUsuarioCreacion());
			//busco la actividad  ciiu principal
			ProyectoLicenciaCuaCiuu actividadPrincipal = proyectoLicenciaCuaCiuuFacade.actividadPrincipal(proyecto);
			String codigoCiiu = actividadPrincipal.getCatalogoCIUU() != null ? actividadPrincipal.getCatalogoCIUU().getCodigo(): "";
			// obtengo la informacion geografica externa CONALI
			List<CatalogoGeneralCoa> listaCapas =catalogoCoaFacade.obtenerCatalogo(CatalogoTipoCoaEnum.COA_CAPAS_EXTERNAS_CONALI);
			String strTableCapaCONALI = "";
			for (CatalogoGeneralCoa catalogoCapas : listaCapas) {		
				strTableCapaCONALI += catalogoCapas.getDescripcion() + "<br/>";
			}
			String nombreOperador = uOperador.getPersona().getNombre();
			String cedulaOperador = uOperador.getPersona().getPin();
			String razonSocial = orga == null ? " " : orga.getNombre();
			
			PlantillaReporte plantillaReporte = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.TIPO_CI_OFICIO_ACTUALIZACION);
			File fileAux = UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(), nombreReporte, true,new CertificadoInterseccionRcoaOficioHtml(oficioCI,nombreOperador,cedulaOperador,razonSocial,codigoCiiu,strTableCapaCONALI,usuarioAutoridad, !marcaAgua),null);
			File file = JsfUtil.fileMarcaAgua(fileAux, marcaAgua ? " - - BORRADOR - - " : " ", BaseColor.GRAY);
	        Path path = Paths.get(file.getAbsolutePath());
	        byte[] byteArchivo = Files.readAllBytes(path);
	        File fileArchivo = new File(JsfUtil.devolverPathReportesHtml(file.getName()));
	        fileOutputStream = new FileOutputStream(fileArchivo);
	        fileOutputStream.write(byteArchivo);
	        fileOutputStream.close();
	        
	        Path pathQr = Paths.get(proyecto.getCodigoUnicoAmbiental().replace("/", "-") + "-qr-firma.png");
			Files.delete(pathQr);
	        
	        DocumentosCOA documento = new DocumentosCOA();	        	
        	documento.setContenidoDocumento(Files.readAllBytes(path));
        	documento.setExtencionDocumento(".pdf");		
        	documento.setTipo("application/pdf");
        	documento.setIdTabla(oficioCI.getId());	       		
        	documento.setNombreTabla(CertificadoInterseccionOficioCoa.class.getSimpleName());
        	documento.setNombreDocumento(nombreReporte+"_"+oficioCI.getCodigo()+".pdf");
        	
        	oficioCI.setFechaOficio(new Date());
        	certificadoInterseccionCoaFacade.guardar(oficioCI);
        	
	        return documento;
	       
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Error al generar el documento.");
		}
		return null;
	}

	public StreamedContent descargarMapa() throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
				
			if (documentoMapa != null && documentoMapa.getIdAlfresco() != null) {				
				File fileAux = documentosFacade.descargarFile(documentoMapa);
				File fileDoc = JsfUtil.fileMarcaAguaOverH(fileAux, " - - BORRADOR - - ", BaseColor.GRAY);
				Path path = Paths.get(fileDoc.getAbsolutePath());
				documentoContent = Files.readAllBytes(path);
			}
			
			if (documentoMapa != null && documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documentoMapa.getNombreDocumento());
			} 
			else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
			
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public StreamedContent descargarCertificadoInterseccion() throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			
			if (documentoCertificado != null && documentoCertificado.getContenidoDocumento() != null) {
				documentoContent = documentoCertificado.getContenidoDocumento();
			} else if (documentoCertificado != null && documentoCertificado.getIdAlfresco() != null) {
				documentoContent = documentosFacade.descargar(documentoCertificado.getIdAlfresco());
			}
			
			if (documentoCertificado != null && documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documentoCertificado.getNombreDocumento());
			} 
			else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
			
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void guardarDocumentos() {
		try {
			
			//generar el nuevo certificado de intersección
			documentoCertificado = generarCertificadoInterseccion(false);
			
			documentoMapa = new DocumentosCOA();
			Boolean resultMapa = generarMapa();
			if(!resultMapa) {
				JsfUtil.addMessageError("Ocurrió un guadar el documento del mapa.");
				return;
			}
			
			if (documentoCertificado != null && documentoCertificado.getContenidoDocumento() != null) {
				documentoCertificado = documentosFacade.guardarDocumentoAlfresco(proyecto.getCodigoUnicoAmbiental(),
						"Certificado_Interseccion_Actualizado", 0L, documentoCertificado, TipoDocumentoSistema.RCOA_CERTIFICADO_INTERSECCION_OFICIO_ACTUALIZADO);
				
				documentoCertificado.setNroActualizacion(nroActualizacion);
				documentosFacade.guardar(documentoCertificado);
			}
			

				DocumentosCOA documento = new DocumentosCOA();	        	
				documento.setContenidoDocumento(coordenadasRcoaBean.getUploadedFileGeo().getContents());
				documento.setExtencionDocumento(".xls");		
				documento.setTipo("application/vnd.ms-excel");
				documento.setIdTabla(proyecto.getId());	       		
				documento.setNombreTabla(ProyectoLicenciaCoa.class.getSimpleName());
				documento.setNombreDocumento("Coordenadas área geográfica actualizadas.xls");
				documento.setProyectoLicenciaCoa(proyecto);
				documento = documentosFacade.guardarDocumentoAlfresco(proyecto.getCodigoUnicoAmbiental(), "Coordenadas_Geográficas_Actualizadas", 1L, documento, TipoDocumentoSistema.RCOA_COORDENADA_GEOGRAFICA_ACTUALIZADA);
				documento.setNroActualizacion(nroActualizacion);
				documentosFacade.guardar(documento);
				
				DocumentosCOA documentoCoordenadasImpl = new DocumentosCOA();	        	
				documentoCoordenadasImpl.setContenidoDocumento(coordenadasRcoaBean.getUploadedFileImpl().getContents());
				documentoCoordenadasImpl.setExtencionDocumento(".xls");		
				documentoCoordenadasImpl.setTipo("application/vnd.ms-excel");
				documentoCoordenadasImpl.setIdTabla(proyecto.getId());	       		
				documentoCoordenadasImpl.setNombreTabla(ProyectoLicenciaCoa.class.getSimpleName());
				documentoCoordenadasImpl.setNombreDocumento("Coordenadas área de implantación actualizadas.xls");
				documentoCoordenadasImpl.setProyectoLicenciaCoa(proyecto);

				documentoCoordenadasImpl = documentosFacade.guardarDocumentoAlfresco(proyecto.getCodigoUnicoAmbiental(), "Coordenadas_Implantación_Actualizadas", 1L, documentoCoordenadasImpl, TipoDocumentoSistema.RCOA_COORDENADA_IMPLANTACION_ACTUALIZADA);
				documentoCoordenadasImpl.setNroActualizacion(nroActualizacion);
				documentosFacade.guardar(documentoCoordenadasImpl);
				
				if (documentoMapa != null && documentoMapa.getIdAlfresco() != null) {
					documentoMapa.setNroActualizacion(nroActualizacion);
					documentoMapa = documentosFacade.guardar(documentoMapa);
				}
					
				proyecto.setEstadoActualizacionCertInterseccion(3); //3 modificado por el operador
				proyectoLicenciaCoaFacade.guardar(proyecto);
				
				proyectoActualizacionCIFacade.guardarHistorialActualizacionCertificado(proyecto.getCodigoUnicoAmbiental(), 3, null);
				
				JsfUtil.addMessageInfo("La actualización se realizó con éxito");
				
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			
		} catch (Exception ex) {
			JsfUtil.addMessageError("Ocurrió un error al guardar los documentos.");
		}
	}
	
	public void limpiarActualizacion() {
		try {
			proyectoLicenciaAmbientalCoaShapeFacade.eliminar(proyecto, 2, nroActualizacion);
			
			proyectoLicenciaCoaUbicacionFacade.eliminar(proyecto, nroActualizacion);
			
			interseccionProyectoLicenciaAmbientalFacade.eliminar(proyecto, JsfUtil.getLoggedUser().getNombre(), nroActualizacion);
			
			oficioCI.setEstado(false);
			certificadoInterseccionCoaFacade.guardar(oficioCI);
			
			JsfUtil.redirectTo("/prevencion/actualizacionCertInterseccion/proyectosPendientes.jsf");
		} catch (Exception ex) {
			ex.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al ejecutar la operación.");
		}
	}
}
