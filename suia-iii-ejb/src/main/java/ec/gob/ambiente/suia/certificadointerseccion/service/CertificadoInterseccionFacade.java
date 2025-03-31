package ec.gob.ambiente.suia.certificadointerseccion.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.autoridadambiental.service.AutoridadAmbientalFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.CertificadoInterseccion;
import ec.gob.ambiente.suia.domain.DetalleInterseccionProyecto;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.DocumentosTareasProceso;
import ec.gob.ambiente.suia.domain.InterseccionProyecto;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.EntityPersona;
import ec.gob.ambiente.suia.entidadResponsable.facade.EntidadResponsableFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.persona.service.PersonaServiceBean;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.UtilPlantilla;
import ec.gob.ambiente.suiamapasws.client.generated.Resultado;
import ec.gob.ambiente.suiamapasws.client.generated.Web_0020Services_0020MAE_0020;
import ec.gob.ambiente.suiamapasws.client.generated.Web_0020Services_0020MAE_0020PortType;

@Stateless
public class CertificadoInterseccionFacade {

	private static final String FIRMA_DIRECTOR_PREVENCION = "firmaDirectorPrevencion.png";

	@EJB
	private CrudServiceBean crudServiceBean = new CrudServiceBean();

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	@EJB
	private OficioCertificadoInterseccionService oficioCertificadoInterseccionService;

	@EJB
	private DocumentosFacade documentosFacade;

	@EJB
	private AutoridadAmbientalFacade autoridadAmbientalService;

	@EJB
	private SecuenciasFacade secuenciasFacade;

	@EJB
	private CapasFacade capasFacade;

	@EJB
	private EntidadResponsableFacade entidadResponsableFacade;

	@EJB
	private UsuarioFacade usuarioFacade;

	@EJB
	private CertificadoInterseccionService certificadoInterseccionService;

	@EJB
	private PersonaServiceBean personaService;
	
	@EJB
	private AreaFacade areaFacade;

	private static final String NOMBRE_MAPA = "MAPA.pdf";
	private static final String NOMBRE_OFICIO = "OFICIO.pdf";

	/**
	 * Método que retorna si interseco con SNAP, BP y PFE
	 * 
	 * @param codigoProyecto
	 * @return
	 */
	public String getCapasInterseccion(String codigoProyecto) {
		return certificadoInterseccionService.getCapasInterseccion(codigoProyecto);
	}

	/**
	 * Método que retorna detalle de interseccion
	 * 
	 * @param codigoProyecto
	 * @return
	 */
	public String getDetalleCapasInterseccion(String codigoProyecto) {
		Map<String, Boolean> resultado = getCapasInterseccionBoolean(codigoProyecto);
		String detalle = "";
		if (resultado.get(Constantes.CAPA_SNAP)) {
			detalle = detalle + capasFacade.getCapas(Integer.valueOf(Constantes.ID_CAPA_SNAP)).getNombre() + ", ";
		}
		if (resultado.get(Constantes.CAPA_BP)) {
			detalle = detalle + capasFacade.getCapas(Integer.valueOf(Constantes.ID_CAPA_BP)).getNombre() + ", ";
		}
		if (resultado.get(Constantes.CAPA_PFE)) {
			detalle = detalle + capasFacade.getCapas(Integer.valueOf(Constantes.ID_CAPA_PFE)).getNombre() + ", ";
		}
		if (resultado.get(Constantes.CAPA_RAMSAR_PUNTO)) {
			detalle = detalle + capasFacade.getCapas(Integer.valueOf(Constantes.ID_CAPA_RAMSAR_PUNTO)).getNombre() + ", ";
		}
		if (resultado.get(Constantes.CAPA_RAMSAR_AREA)) {
			detalle = detalle + capasFacade.getCapas(Integer.valueOf(Constantes.ID_CAPA_RAMSAR_AREA)).getNombre() + ", ";
		}
		
		detalle = detalle.substring(0, detalle.length() - 2);
		return detalle;
	}

	public Map<String, Boolean> getCapasInterseccionBoolean(String codigoProyecto) {
		List<InterseccionProyecto> intersecciones = certificadoInterseccionService
				.getListaInterseccionProyectoIntersecaCapas(codigoProyecto);
		Map<String, Boolean> mapaResultado = new HashMap<String, Boolean>();
		mapaResultado.put(Constantes.CAPA_SNAP,
				certificadoInterseccionService.getValorInterseccionBoolean(intersecciones, Constantes.ID_CAPA_SNAP));
		mapaResultado.put(Constantes.CAPA_BP,
				certificadoInterseccionService.getValorInterseccionBoolean(intersecciones, Constantes.ID_CAPA_BP));
		mapaResultado.put(Constantes.CAPA_PFE,
				certificadoInterseccionService.getValorInterseccionBoolean(intersecciones, Constantes.ID_CAPA_PFE));
		mapaResultado.put(Constantes.CAPAS_AMORTIGUAMIENTO,
				getValorInterseccionCapasAmortiguamientoBoolean(intersecciones));
		mapaResultado.put(Constantes.CAPA_RAMSAR_AREA,
				certificadoInterseccionService.getValorInterseccionBoolean(intersecciones, Constantes.ID_CAPA_RAMSAR_AREA));
		mapaResultado.put(Constantes.CAPA_RAMSAR_PUNTO,
				certificadoInterseccionService.getValorInterseccionBoolean(intersecciones, Constantes.ID_CAPA_RAMSAR_PUNTO));
		return mapaResultado;
	}

	private boolean getValorInterseccionCapasAmortiguamientoBoolean(List<InterseccionProyecto> intersecciones) {
		for (InterseccionProyecto interseccionProyecto : intersecciones) {
			if (interseccionProyecto.getIntersectaConCapaAmortiguamiento()) {
				return true;
			}
		}
		return false;
	}

	private CertificadoInterseccion crearCertificadoInterseccion(ProyectoLicenciamientoAmbiental proyecto,
			Area areaResponsable) throws Exception {

		CertificadoInterseccion ci = certificadoInterseccionService.recuperarCertificadoInterseccion(proyecto);
		if (ci == null) {
			ci = new CertificadoInterseccion();
			ci.setCodigo(secuenciasFacade.getSecuenciaResolucionAreaResponsable(proyecto
					.isAreaResponsableEnteAcreditado() ? entidadResponsableFacade
					.buscarAreaDireccionProvincialPorUbicacion(proyecto) : areaResponsable));
			ci.setProyectoLicenciamientoAmbiental(proyecto);
			crudServiceBean.saveOrUpdate(ci);
		}
		return ci;

	}

	public EntityPersona getPersonaRepresentanteProyecto(ProyectoLicenciamientoAmbiental proyecto)
			throws ServiceException {
		EntityPersona personaTemp = new EntityPersona();
		Persona persona = personaService.getRepresentanteProyectoFull(proyecto.getId());
		personaTemp.setNombre(persona.getNombre());
		personaTemp.setTrato(persona.getTipoTratos().getNombre());
		personaTemp.setPin(usuarioFacade.obtenerCedulaNaturalJuridico(proyecto.getUsuario().getId()));
		return personaTemp;
	}

    public void generarInterseccionProyectoCapas(ProyectoLicenciamientoAmbiental proyecto, String userNameLogin) throws Exception {
        invocarWSInterseccion(proyecto);
        ProyectoLicenciamientoAmbiental proyectoFull = null;
        try {
            proyectoFull = proyectoLicenciamientoAmbientalFacade.cargarProyectoFullPorId(proyecto.getId());
        } catch (Exception e) {
            throw new ServiceException("Error cargando los datos del proyecto.", e);
        }
        guardarAreaResponsable(proyectoFull);

	}

	private Resultado invocarWSInterseccion(ProyectoLicenciamientoAmbiental proyecto) throws ServiceException {
		Resultado resultado = null;
		try {
			Web_0020Services_0020MAE_0020 web = new Web_0020Services_0020MAE_0020(
					getUrlWSDLSuiaMapasCertificadoInterseccion());
			Web_0020Services_0020MAE_0020PortType ws = web.getWeb_0020Services_0020MAE_0020Port();
			resultado = ws.listarCapas(proyecto.getCodigo());
		} catch (Exception e) {
			guardarEstadoProcesoInterseccionEnProyecto(proyecto, false);
			throw new ServiceException(e.getCause());
		}
		if (resultado.getCodigo() == 0) {
			guardarEstadoProcesoInterseccionEnProyecto(proyecto, false);
			throw new ServiceException(resultado.getDetalle());
		}
		guardarEstadoProcesoInterseccionEnProyecto(proyecto, true);
		return resultado;

	}

	private void guardarEstadoProcesoInterseccionEnProyecto(ProyectoLicenciamientoAmbiental proyecto,
			boolean isProyectoInterseccionExitosa) {
		proyecto.setProyectoInterseccionExitosa(isProyectoInterseccionExitosa);
		crudServiceBean.saveOrUpdate(proyecto);
	}

    private void guardarAreaResponsable(ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental) throws Exception {
        Area areaResponsable = entidadResponsableFacade.obtenerEntidadResponsable(proyectoLicenciamientoAmbiental);
        if (areaResponsable == null) {
            areaResponsable = entidadResponsableFacade.getDireccionNacionalControlAmbiental();
        }
//        if(areaResponsable.getTipoArea().getId()==3 &&(areaResponsable.getId()==584 || areaResponsable.getId()==580 || areaResponsable.getId()==577 || areaResponsable.getId()==576 || areaResponsable.getId()==579)){
        if (areaResponsable.getHabilitarArea()==null){
        	areaResponsable.setHabilitarArea(false);
        }
        	
        if (areaResponsable.getTipoArea().getId()==3  && (areaResponsable.getHabilitarArea()==true)){
			proyectoLicenciamientoAmbiental.setAreaResponsable(areaResponsable);
		}else if (areaResponsable.getTipoArea().getId()==3) {
			proyectoLicenciamientoAmbiental.setAreaResponsable(null);
		}else {
			proyectoLicenciamientoAmbiental.setAreaResponsable(areaResponsable);
		}
        crudServiceBean.saveOrUpdate(proyectoLicenciamientoAmbiental);
    }

	private URL getUrlWSDLSuiaMapasCertificadoInterseccion() throws MalformedURLException {
		URL url = null;
		URL baseUrl;
		baseUrl = ec.gob.ambiente.suiamapasws.client.generated.Web_0020Services_0020MAE_0020.class.getResource(".");
		url = new URL(baseUrl, Constantes.getAppServicesSuiaMapasCertificadoInterseccion());
		return url;
	}

	public void subirMapaGeneradoCIAlfresco(ProyectoLicenciamientoAmbiental proyecto, TipoDocumentoSistema tipo)
			throws ServiceException, CmisAlfrescoException {

		certificadoInterseccionService.isGeneradoPDFCertificadoInterseccion(proyecto.getCodigo());

		File file = certificadoInterseccionService.getFilePDFMapaCertificadoInterseccion(proyecto.getCodigo());
		if (file.exists()) {
			subirFileAlfresco(file, proyecto, tipo);
		} else {
			throw new ServiceException("El Certificado de intersección para el proyecto " + proyecto.getCodigo()
					+ " no se encuentra en la ubicacion");
		}
	}

	public void subirReportesCIAlfresco(File file, ProyectoLicenciamientoAmbiental proyecto) throws ServiceException, CmisAlfrescoException {
		subirFileAlfresco(file, proyecto, TipoDocumentoSistema.TIPO_CERTIFICADO_INTERSECCION_OFICIO);
	}

	public void subirCIMapaAlfresco(ProyectoLicenciamientoAmbiental proyecto) throws ServiceException, CmisAlfrescoException {
		subirMapaGeneradoCIAlfresco(proyecto, TipoDocumentoSistema.TIPO_CERTIFICADO_INTERSECCION_MAPA);

	}

	private void subirFileAlfresco(File file, ProyectoLicenciamientoAmbiental proyecto, TipoDocumentoSistema tipo)
			throws ServiceException, CmisAlfrescoException {

		Documento documento = new Documento();
		try {
			byte[] data = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
			documento.setContenidoDocumento(data);
			documento.setDescripcion("CI pdf con mapa.");
			documento.setExtesion(".pdf");
			documento.setMime("application/pdf");

			// No se que hace esto
			documento.setNombreTabla(ProyectoLicenciamientoAmbiental.class.getSimpleName());
			documento.setIdTable(proyecto.getId());

			documento.setNombre(tipo.getIdTipoDocumento() == 3 ? NOMBRE_MAPA : NOMBRE_OFICIO);
			DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
			documentoTarea.setIdTarea(0L);

			documentoTarea.setProcessInstanceId(0L);
			documentosFacade.guardarDocumentoAlfresco(proyecto.getCodigo(),
					Constantes.CARPETA_CARTIFICADO_INTERSECCION_ALFRESCO, 0L, documento, tipo, documentoTarea);
		} catch (IOException e) {
			throw new ServiceException("Error al recueperar el contenido de file", e.getCause());
		}

	}

	/**
	 * Método que retorna si interseco con SNAP, BP y PFE
	 * 
	 * @param codigoProyecto
	 * @return
	 */

	public String getComentarioInterseccion(String codigoProyecto, String comentarioNoProyectoInterseca,
			String comentarioSiProyectoInterseca) {
		if (certificadoInterseccionService.isProyectoIntersecaCapas(codigoProyecto)) {
			String detalle = getDetalleInterseccion(codigoProyecto);
			String[] parametros = { detalle };
			return UtilPlantilla.getPlantillaConParametros(comentarioSiProyectoInterseca, parametros);
		}
		return comentarioNoProyectoInterseca;

	}

	public String getDetalleInterseccion(String codigoProyecto) {
		String resultado = "<ul> ";
		List<InterseccionProyecto> intersecciones = certificadoInterseccionService
				.getListaInterseccionProyectoIntersecaCapas(codigoProyecto);
		for (InterseccionProyecto interseccionProyecto : intersecciones) {
			String nombreCapa = interseccionProyecto.getDescripcion();
			List<DetalleInterseccionProyecto> detallesAux = certificadoInterseccionService
					.getDetallesInterseccion(interseccionProyecto.getId());
			String nombreDetalles = getStringDetalle(detallesAux);
			resultado = resultado + "<li>" + nombreCapa + ": " + nombreDetalles + "</li>";

		}
		return resultado + "</ul>";
	}

    private String getStringDetalle(List<DetalleInterseccionProyecto> detallesAux) {
        StringBuilder resultadoDetalle = new StringBuilder();
        for (DetalleInterseccionProyecto detalleInterseccionProyecto : detallesAux) {
            resultadoDetalle.append(detalleInterseccionProyecto.getNombre() +(detalleInterseccionProyecto.getCapaSubsistema()!=null?" ("+detalleInterseccionProyecto.getCapaSubsistema()+")":""));
            resultadoDetalle.append(" ,");
        }
        if (resultadoDetalle.toString().length() > 2) {
            return resultadoDetalle.substring(0, resultadoDetalle.length() - 2);
        } else {
            return "";
        }
    }

	public boolean getValueInterseccionProyectoIntersecaCapas(
			ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental) {
		return certificadoInterseccionService.isProyectoIntersecaCapas(proyectoLicenciamientoAmbiental.getCodigo());
	}

	public boolean isCIMapaGenerado(ProyectoLicenciamientoAmbiental proyecto) throws CmisAlfrescoException {
		byte[] archivo = recuperarCIAlfresco(proyecto, TipoDocumentoSistema.TIPO_CERTIFICADO_INTERSECCION_MAPA);
		return archivo == null ? false : true;
	}

	public byte[] recuperarCIAlfresco(ProyectoLicenciamientoAmbiental proyecto, TipoDocumentoSistema idTipo) throws CmisAlfrescoException {
		return documentosFacade.descargarDocumentoAlfrescoQueryDocumentos(
				ProyectoLicenciamientoAmbiental.class.getSimpleName(), proyecto.getId(), idTipo);

	}

	public Persona getPersonaAutoridadAmbiental() throws ServiceException {
		return autoridadAmbientalService.getDirectorPrevencionContaminacionAmbiental();
	}

	public byte[] getFirmaAutoridadPrevencion() throws ServiceException, CmisAlfrescoException {
		try {
			return documentosFacade.descargarDocumentoPorNombre(FIRMA_DIRECTOR_PREVENCION);
		} catch (RuntimeException e) {
			throw new ServiceException("Error al recuperar la firma para el certificado", e);
		}
	}

	public boolean isGeneradoCorrectamenteDocumentosCertificadoInterseccion(Integer idProyecto) {
		return isGeneradoCorrectamenteDocumentoCertificadoInterseccionMapa(idProyecto)
				&& isGeneradoCorrectamenteDocumentoCertificadoInterseccionOficio(idProyecto);
	}

	public boolean isGeneradoCorrectamenteDocumentoCertificadoInterseccionMapa(Integer idProyecto) {
		return isDocumentoTipo(idProyecto, TipoDocumentoSistema.TIPO_CERTIFICADO_INTERSECCION_MAPA);
	}

	public boolean isGeneradoCorrectamenteDocumentoCertificadoInterseccionOficio(Integer idProyecto) {
		return isDocumentoTipo(idProyecto, TipoDocumentoSistema.TIPO_CERTIFICADO_INTERSECCION_OFICIO);
	}

	public boolean isDocumentoTipo(Integer idProyecto, TipoDocumentoSistema tipoDocumento) {
		List<Documento> documentos = documentosFacade.getDocumentosCertificadoInterseccion(idProyecto);
		for (Documento documento : documentos) {
			if (documento.getTipoDocumento().getId().equals(tipoDocumento.getIdTipoDocumento())) {
				return true;
			}
		}
		return false;
	}

	public boolean verificarCreacionMapa(String codigoProyecto) {
		return certificadoInterseccionService.verificarCreacionMapa(codigoProyecto);
	}

	public boolean isProyectoIntersecaCapas(String codigoProyecto) {
		return certificadoInterseccionService.isProyectoIntersecaCapas(codigoProyecto);
	}

	public Boolean isGeneradoPDFCertificadoInterseccionEnServidorMapas(String codigoProyecto) {
		try {
			return certificadoInterseccionService.isGeneradoPDFCertificadoInterseccion(codigoProyecto);
		} catch (ServiceException e) {
			return false;
		}

	}

	public Boolean isGeneradoPDFMapaCertifiacdoInterseccionServidor(String codigoProyecto) {
		try {
			certificadoInterseccionService.isGeneradoPDFCertificadoInterseccion(codigoProyecto);
			File file = certificadoInterseccionService.getFilePDFMapaCertificadoInterseccion(codigoProyecto);
			return file.exists();
		} catch (ServiceException e) {
			return false;
		}
	}

	public void eliminarPDFMapaCertificadoInterseccion(String codigoProyecto) throws ServiceException {
		if (isGeneradoPDFMapaCertifiacdoInterseccionServidor(codigoProyecto)) {
			File file = certificadoInterseccionService.getFilePDFMapaCertificadoInterseccion(codigoProyecto);
			file.delete();
		}
	}

	public byte[] getAlfrescoMapaCertificadoInterseccion(ProyectoLicenciamientoAmbiental proyecto)
			throws ServiceException, CmisAlfrescoException {

		if (isGeneradoCorrectamenteDocumentoCertificadoInterseccionMapa(proyecto.getId())) {
			return recuperarCIAlfresco(proyecto, TipoDocumentoSistema.TIPO_CERTIFICADO_INTERSECCION_MAPA);
		} else {
			throw new ServiceException("El mapa del certificado de interseccion aún no ha sido generado.");
		}

	}

	public byte[] getAlfrescoOficioCertificadoInterseccion(ProyectoLicenciamientoAmbiental proyecto)
			throws ServiceException, CmisAlfrescoException {

		if (isGeneradoCorrectamenteDocumentoCertificadoInterseccionOficio(proyecto.getId())) {
			return recuperarCIAlfresco(proyecto, TipoDocumentoSistema.TIPO_CERTIFICADO_INTERSECCION_OFICIO);
		} else {
			throw new ServiceException("El oficio del certificado de interseccion aún no ha sido generado.");
		}

	}

	public byte[] descargarCoordenadasCertificadoInterseccion(ProyectoLicenciamientoAmbiental proyecto) throws CmisAlfrescoException {
		return recuperarCIAlfresco(proyecto, TipoDocumentoSistema.TIPO_COORDENADAS);

	}
	
	public byte [] getAlfrescoOficioCertificadoViabilidad (ProyectoLicenciamientoAmbiental proyecto)
			throws ServiceException, CmisAlfrescoException{
			
			if (isGeneradoCorrectamenteDocumentoCertificadoInterseccionOficio(proyecto.getId())){
				return recuperarCIAlfresco(proyecto, TipoDocumentoSistema.TIPO_CERTIFICADO_VIABILIDAD);
			}else{
				throw new ServiceException("El oficio del certificado de viabilidad aún no ha sido generado.");
			}
		}

    public String[] getParametrosOficioCertificadoInterseccion(Integer idProyecto, String etiquetaRepresentanteLegal,
                                                               String tituloTratamientoPersonaJuridica, String notaEnteAcreditado, String comentarioNoProyectoInterseca,
                                                               String comentarioSiProyectoInterseca,String userNameLogin, String notaMdq, Integer marcaAgua) throws Exception {
        ProyectoLicenciamientoAmbiental proyecto = proyectoLicenciamientoAmbientalFacade
                .cargarProyectoFullPorId(idProyecto);
        guardarAreaResponsable(proyecto);
        Persona representante = personaService.getRepresentanteProyectoFull(idProyecto);
        Area areaResponsable = proyecto.getAreaResponsable();
        CertificadoInterseccion certificadoInterseccion = crearCertificadoInterseccion(proyecto, areaResponsable);
        return oficioCertificadoInterseccionService.getParametrosCertificadoInterseccion(
                proyecto,
                etiquetaRepresentanteLegal,
                tituloTratamientoPersonaJuridica,
                notaEnteAcreditado,
                getComentarioInterseccion(proyecto.getCodigo(), comentarioNoProyectoInterseca,
                        comentarioSiProyectoInterseca), representante, areaResponsable, certificadoInterseccion, notaMdq,marcaAgua);

	}

    public CertificadoInterseccion recuperarCertificadoInterseccion(ProyectoLicenciamientoAmbiental proyecto) {
       return certificadoInterseccionService.recuperarCertificadoInterseccion(proyecto);

    }

    /**
     * Retornar el detalle de interseción formateado
     *
     * @param codigoProyecto Incluir siempre delante de cada elemento
     * @param prefijo        Incluir siempre al final de cada elemento
     * @param sufijo         Si el sufijo se incluye en el último elemento
     * @param incluirFinal
     * @return
     */
    public String getDetalleInterseccion(String codigoProyecto, String prefijo, String sufijo, Boolean incluirFinal) {
        String resultado = "";
        List<InterseccionProyecto> intersecciones = certificadoInterseccionService
                .getListaInterseccionProyectoIntersecaCapas(codigoProyecto);
        Integer contador = intersecciones.size();
        for (InterseccionProyecto interseccionProyecto : intersecciones) {
            contador--;
            String sufijoF =  (incluirFinal || contador > 0) ? sufijo: "";
            String nombreCapa = interseccionProyecto.getDescripcion();
            List<DetalleInterseccionProyecto> detallesAux = certificadoInterseccionService
                    .getDetallesInterseccion(interseccionProyecto.getId());
            String nombreDetalles = getStringDetalle(detallesAux);
            resultado = resultado + prefijo + nombreCapa + ": " + nombreDetalles + sufijoF;

        }
        return resultado;
    }
    
	public int getDetalleInterseccionlista(String codigoProyecto) {
		Integer valor = 0;
		List<InterseccionProyecto> intersecciones = certificadoInterseccionService
				.getListaInterseccionProyectoIntersecaCapas(codigoProyecto);
		if (intersecciones.size() == 0) {
			valor = 0;
		} else {
			for (InterseccionProyecto interseccionProyecto : intersecciones) {
				String nombreCapa = interseccionProyecto.getDescripcion();
				if (nombreCapa.equals("Quebradas Vivas")) {
					valor = 2;
//					break;
				}
				else{
					valor = 3;
					break;
				}
			}
		}
		return valor;
	}
	
	public List<InterseccionProyecto> getListaInterseccionProyectoIntersecaCapas(String codigoProyecto) {		
		List<InterseccionProyecto> intersecciones = certificadoInterseccionService.getListaInterseccionProyectoIntersecaCapas(codigoProyecto);		
		return intersecciones;
	}
	
	
	public List<DetalleInterseccionProyecto> getDetallesInterseccion(Integer idInterseccionProyecto) {
		List<DetalleInterseccionProyecto> detalles =certificadoInterseccionService.getDetallesInterseccion(idInterseccionProyecto);				
		return detalles;
	}
	
	public boolean valorInterseccionProyecto(String codigoProyecto){
		List<InterseccionProyecto> intersecciones = certificadoInterseccionService.getListaInterseccionProyectoIntersecaCapas(codigoProyecto);	
		boolean valor=false;
		if(intersecciones.size()>0){
			valor= true;
		}
		return valor;
	}

	public CertificadoInterseccion getCertificadoInterseccion(Integer idProyecto) {
		return certificadoInterseccionService.getCertificadoInterseccion(idProyecto);
	}
	
	public CertificadoInterseccion getCertificadoInterseccionActualizacion(Integer idProyecto, Integer nroActualizacion) {
		return certificadoInterseccionService.getCertificadoInterseccionActualizacion(idProyecto, nroActualizacion);
	}

	public CertificadoInterseccion getCertificadoInterseccionActualizacionSuiaVerde(String idProyecto, Integer nroActualizacion) {
		return certificadoInterseccionService.getCertificadoInterseccionActualizacionSuiaVerde(idProyecto, nroActualizacion);
	}
	
	public CertificadoInterseccion getCertificadoInterseccionCodigo(String codigo) {
		return certificadoInterseccionService.getCertificadoInterseccionCodigo(codigo);
	}
	
	public String generarCodigo(String siglasArea) throws Exception {
		String nombreSecuencia = "ACTUALIZACION_CERTIFICADO_INTERSECCION_" + siglasArea + "_" + secuenciasFacade.getCurrentYear();
		if(siglasArea.equals(Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL))
			nombreSecuencia = "ACTUALIZACION_CERTIFICADO_INTERSECCION_" + secuenciasFacade.getCurrentYear();
		
		String secuencia = Constantes.SIGLAS_INSTITUCION + "-SUIA-RA-"+siglasArea+"-"+secuenciasFacade.getCurrentYear();
				
		String codigo = secuencia + "-" + secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia, 5) + "-A";
		
		return codigo;
	}
	
	public CertificadoInterseccion guardarActualizacionCertificadoInterseccion(CertificadoInterseccion ci) throws Exception {		
		Area areaFirma = areaFacade.getArea(ci.getAreaUsuarioFirma());
		String siglasArea = areaFirma.getAreaAbbreviation();
		if(areaFirma.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_PC))
			siglasArea = Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL;
		else if(areaFirma.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_ZONALES))
			siglasArea = areaFirma.getAreaAbbreviation();
		else if(areaFirma.getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_OT))
			siglasArea = areaFirma.getArea().getAreaAbbreviation();
		
		if(ci.getCodigo()==null)
			ci.setCodigo(generarCodigo(siglasArea));
		else if(!ci.getCodigo().contains(siglasArea))
			ci.setCodigo(generarCodigo(siglasArea));
		
		return crudServiceBean.saveOrUpdate(ci);
	}
	
	public CertificadoInterseccion guardarCertificadoInterseccion(CertificadoInterseccion ci) throws Exception {
		crudServiceBean.saveOrUpdate(ci);
		return ci;
	}
	
	public String getComentarioInterseccionActualizacion(String codigoProyecto, String comentarioNoProyectoInterseca,
			String comentarioSiProyectoInterseca, Integer nroActualizacion) {
		if (certificadoInterseccionService.isProyectoIntersecaCapasActualizacion(codigoProyecto, nroActualizacion)) {
			String detalle = getDetalleInterseccionActualizacion(codigoProyecto, nroActualizacion);
			String[] parametros = { detalle };
			return UtilPlantilla.getPlantillaConParametros(comentarioSiProyectoInterseca, parametros);
		}
		return comentarioNoProyectoInterseca;

	}
	
	public String getDetalleInterseccionActualizacion(String codigoProyecto, Integer nroActualizacion) {
		String resultado = "<ul> ";
		List<InterseccionProyecto> intersecciones = certificadoInterseccionService
				.getListaInterseccionProyectoIntersecaCapasActualizacion(codigoProyecto, nroActualizacion);
		for (InterseccionProyecto i : intersecciones) {
			if(i.getCapaCoa() != null) {
				List<DetalleInterseccionProyecto> detallesAux = certificadoInterseccionService
						.getDetallesInterseccion(i.getId());
				for(DetalleInterseccionProyecto detalleInterseccion : detallesAux)
				{
					resultado = resultado + "<li>" +i.getCapaCoa().getNombre()+": "+detalleInterseccion.getNombre() + "</li>";
				}
			}
		}
		
		return resultado + "</ul>";
	}
	
}