/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.dto.GeneradorCustom;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.comparator.OrdenarTipoSectorPorNombreComparator;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.AlmacenGeneradorDesechoPeligroso;
import ec.gob.ambiente.suia.domain.AlmacenGeneradorDesechos;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.AreaUsuario;
import ec.gob.ambiente.suia.domain.CategoriaDesechoPeligroso;
import ec.gob.ambiente.suia.domain.Coordenada;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.FormaPuntoRecuperacion;
import ec.gob.ambiente.suia.domain.GeneradorDesechosAlmacenMedidaSeguridad;
import ec.gob.ambiente.suia.domain.GeneradorDesechosDesechoPeligroso;
import ec.gob.ambiente.suia.domain.GeneradorDesechosDesechoPeligrosoDatosGenerales;
import ec.gob.ambiente.suia.domain.GeneradorDesechosDesechoPeligrosoEtiquetado;
import ec.gob.ambiente.suia.domain.GeneradorDesechosDesechoPeligrosoPuntoGeneracion;
import ec.gob.ambiente.suia.domain.GeneradorDesechosEliminador;
import ec.gob.ambiente.suia.domain.GeneradorDesechosEliminadorSede;
import ec.gob.ambiente.suia.domain.GeneradorDesechosPeligrosos;
import ec.gob.ambiente.suia.domain.GeneradorDesechosRecolector;
import ec.gob.ambiente.suia.domain.GeneradorDesechosRecolectorSede;
import ec.gob.ambiente.suia.domain.GeneradorDesechosRecoletorUbicacionGeografica;
import ec.gob.ambiente.suia.domain.IncompatibilidadDesechoPeligroso;
import ec.gob.ambiente.suia.domain.IncompatibilidadGeneradorDesechosDesecho;
import ec.gob.ambiente.suia.domain.PoliticaDesecho;
import ec.gob.ambiente.suia.domain.PoliticaDesechoActividad;
import ec.gob.ambiente.suia.domain.PrestadorServiciosDesechoPeligroso;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.PuntoEliminacion;
import ec.gob.ambiente.suia.domain.PuntoEliminacionPrestadorServicioDesechoPeligroso;
import ec.gob.ambiente.suia.domain.PuntoGeneracion;
import ec.gob.ambiente.suia.domain.PuntoRecuperacion;
import ec.gob.ambiente.suia.domain.SedePrestadorServiciosDesechos;
import ec.gob.ambiente.suia.domain.TipoEliminacionDesecho;
import ec.gob.ambiente.suia.domain.TipoEnvase;
import ec.gob.ambiente.suia.domain.TipoEstadoFisico;
import ec.gob.ambiente.suia.domain.TipoEtiquetado;
import ec.gob.ambiente.suia.domain.TipoIluminacion;
import ec.gob.ambiente.suia.domain.TipoLocal;
import ec.gob.ambiente.suia.domain.TipoMaterialConstruccion;
import ec.gob.ambiente.suia.domain.TipoMedidaSeguridad;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.TipoVentilacion;
import ec.gob.ambiente.suia.domain.TransaccionFinanciera;
import ec.gob.ambiente.suia.domain.UnidadMedida;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.entidadResponsable.facade.EntidadResponsableFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.Constantes;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 *
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 16/03/2015]
 *          </p>
 */
@Stateless
public class RegistroGeneradorDesechosFacade {

	public static final String CODE_PREFIX = "SUIA-";

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private SecuenciasFacade secuenciasFacade;

	@EJB
	private DocumentosFacade documentosFacade;	

	@EJB
	private AreaFacade areaFacade;

	@EJB
	private EntidadResponsableFacade entidadResponsableFacade;

	@EJB
	private ProcesoFacade procesoFacade;

	public final String SIGLAS_PLANTA_CENTRAL_CODIGO_GENERADOR = "SCA";

	public String generarCodigoRegistroGeneradorDesechos(Area area) {
		String abreviaturaArea = area.getAreaAbbreviation();

		try {
			if (area.getId().equals(Area.PNGIDS)
					|| area.getAreaAbbreviation().equals(Constantes.SIGLAS_GRECI)
					|| area.getId().equals(Area.UNIDAD_DE_PRODUCTOS_DESECHOS_PELIGROSOS_Y_NO_PELIGROSOS_SUB_DNCA))
				abreviaturaArea = SIGLAS_PLANTA_CENTRAL_CODIGO_GENERADOR;
			return CODE_PREFIX + secuenciasFacade.getCurrentMonth() + "-" + secuenciasFacade.getCurrentYear() + "-" + Constantes.SIGLAS_INSTITUCION + "-"
					+ abreviaturaArea + "-"
					+ secuenciasFacade.getNextValueDedicateSequence(area.getAreaAbbreviation() + "-RGD", 5);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public String generarNumeroSolicitud() {
		try {
			return Constantes.SIGLAS_INSTITUCION + "-SOL-RGD-" + secuenciasFacade.getCurrentYear() + "-"
					+ secuenciasFacade.getNextValueDedicateSequence("SOLICITUD_REGISTRO_GENERADORES");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public List<GeneradorDesechosPeligrosos> getGeneradoresFinalizadosPorDesecho(DesechoPeligroso desechoPeligroso) {
		return getGeneradoresFinalizadosPorDesecho(null, desechoPeligroso);
	}


	public List<GeneradorDesechosPeligrosos> getGeneradoresPorCodigoPorUsuarioList(String codigo, Integer idregistro, String usuario) {
		return getGeneradoresPorCodigoPorUsuario(codigo, idregistro, usuario);
	}
	
	@SuppressWarnings("unchecked")
	public List<GeneradorDesechosPeligrosos> findRGDByProyectoLicenciaAmbiental(ProyectoLicenciamientoAmbiental proyecto){
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("proyecto", proyecto);
		try {
			return (List<GeneradorDesechosPeligrosos>) crudServiceBean.findByNamedQuery(
					GeneradorDesechosPeligrosos.GET_BY_PROYECTO_LICENCIA_AMBIENTAL, parameters);	
		} catch (NoResultException e) {
			return null;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public List<GeneradorDesechosPeligrosos> findRGDByProyectoLicenciaAmbientalId(Integer proyectoId){
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("proyecto", proyectoId);
		String sqlPproyecto="SELECT g FROM GeneradorDesechosPeligrosos g WHERE g.proyecto.id = :proyecto and estado = true ";
		
		try {

			return (List<GeneradorDesechosPeligrosos>) crudServiceBean.getEntityManager().createQuery(
					sqlPproyecto).setParameter("proyecto", proyectoId).getResultList();
		} catch (NoResultException e) {
			return null;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public List<GeneradorDesechosPeligrosos> getGeneradoresFinalizadosPorDesecho(Usuario usuario,
			DesechoPeligroso desechoPeligroso) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idDesecho", desechoPeligroso.getId());
		parameters.put("nombreUsuario", "%" + (usuario == null ? "" : usuario.getNombre()) + "%");
		return (List<GeneradorDesechosPeligrosos>) crudServiceBean.findByNamedQuery(
				GeneradorDesechosPeligrosos.GET_BY_DESECHO_FINALIZADO, parameters);
	}
	
	@SuppressWarnings("unchecked")
	public List<GeneradorDesechosPeligrosos> getGeneradoresPorCodigoRequest(String codigo, Integer idregistro, String usuario) {
		String sqlPproyecto="SELECT g FROM GeneradorDesechosPeligrosos g WHERE g.estado = true and g.id <> "+idregistro;
		if (!"".equals(codigo)){
			sqlPproyecto += " and g.solicitud = '"+codigo+"'";
		}
		if (!"".equals(usuario)){
			sqlPproyecto += " and g.usuarioCreacion = '"+usuario+"'";
		}
		return (List<GeneradorDesechosPeligrosos>) crudServiceBean.getEntityManager().createQuery(
				sqlPproyecto).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<GeneradorDesechosPeligrosos> getGeneradoresPorCodigoPorUsuario(String codigo, Integer idregistro, String usuario) {
		String sqlPproyecto="SELECT g FROM GeneradorDesechosPeligrosos g WHERE g.proyecto.id is null and g.tipoIngreso = true and g.id <> "+idregistro;
		if (!"".equals(codigo)){
			sqlPproyecto += " and g.codigo = '"+codigo+"'";
		}
		if (!"".equals(usuario)){
			sqlPproyecto += " and g.usuarioCreacion = '"+usuario+"'";
		}
		return (List<GeneradorDesechosPeligrosos>) crudServiceBean.getEntityManager().createQuery(
				sqlPproyecto).getResultList();
	}
	
	public void guardarBorrador(GeneradorDesechosPeligrosos generador, Long processId) throws ServiceException,
			CmisAlfrescoException {
		Documento documentoBorrador = new Documento();
		documentoBorrador.setIdTable(generador.getId());
		documentoBorrador.setNombreTabla(generador.getClass().getSimpleName());
		documentoBorrador.setDescripcion(Constantes.NOMBRE_DOCUMENTO_BORRADOR_REGISTRO_GENERADOR);
		documentoBorrador.setEstado(true);
		documentoBorrador.setNombre(Constantes.NOMBRE_DOCUMENTO_BORRADOR_REGISTRO_GENERADOR + ".pdf");
		documentoBorrador.setContenidoDocumento(generador.getArchivoGenerador());
		documentoBorrador.setMime("application/pdf");
		documentoBorrador.setExtesion(".pdf");

		if (generador.getProyecto() != null) {
			documentoBorrador = documentosFacade.guardarDocumentoAlfresco(generador.getProyecto().getCodigo(),
					Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS, processId, documentoBorrador,
					TipoDocumentoSistema.TIPO_BORRADOR_RGD, null);
		} else {
			documentoBorrador = documentosFacade.guardarDocumentoAlfrescoSinProyecto(generador.getSolicitud(),
					Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS, processId, documentoBorrador,
					TipoDocumentoSistema.TIPO_BORRADOR_RGD, null);
		}

		generador.setDocumentoBorrador(documentoBorrador);
		crudServiceBean.saveOrUpdate(generador);
	}

	public void guardarJustificacionesProponente(GeneradorDesechosPeligrosos generador, Long processId,
			Documento copiaJustificacion) throws ServiceException, CmisAlfrescoException {
		if (copiaJustificacion != null) {
			Documento justificacionProponente = new Documento();
			justificacionProponente.setIdTable(generador.getId());
			justificacionProponente.setNombreTabla(generador.getClass().getSimpleName());
			justificacionProponente
					.setDescripcion(Constantes.NOMBRE_DOCUMENTO_JUSTIFICACION_PROPONENTE_REGISTRO_GENERADOR);
			justificacionProponente.setEstado(true);
			Date fecha = new Date();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			justificacionProponente.setNombre(Constantes.NOMBRE_DOCUMENTO_JUSTIFICACION_PROPONENTE_REGISTRO_GENERADOR
					+ format.format(fecha) + ".pdf");
			justificacionProponente.setContenidoDocumento(copiaJustificacion.getContenidoDocumento());
			justificacionProponente.setMime("application/pdf");
			justificacionProponente.setExtesion(".pdf");

			if (generador.getProyecto() != null) {
				justificacionProponente = documentosFacade.guardarDocumentoAlfresco(
						generador.getProyecto().getCodigo(), Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS, processId,
						justificacionProponente, TipoDocumentoSistema.TIPO_JUSTIFICACION_PROPONENTE_RGD, null);
			} else {
				justificacionProponente = documentosFacade.guardarDocumentoAlfrescoSinProyecto(
						generador.getSolicitud(), Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS, processId,
						justificacionProponente, TipoDocumentoSistema.TIPO_JUSTIFICACION_PROPONENTE_RGD, null);
			}

			generador.setDocumentoJustificacionProponente(justificacionProponente);
			crudServiceBean.saveOrUpdate(generador);
		}
	}

	/**
	 * autor: vear
	 * fecha 26/02/2016
	 * objetivo: este método no es mio,
	 * lo comento como respaldo, abajo este mismo metodo modificado
	 */



	public void guardar(GeneradorDesechosPeligrosos generador, Long processId,
			List<PuntoRecuperacion> puntosRecuperacion, List<DesechoPeligroso> desechosPeligrosos,
			List<GeneradorDesechosDesechoPeligrosoDatosGenerales> datosGeneralesDesechos,
			List<GeneradorDesechosDesechoPeligrosoEtiquetado> desechosEtiquetados,
			Map<DesechoPeligroso, List<IncompatibilidadDesechoPeligroso>> incompatibilidadesDesechos,
			List<AlmacenGeneradorDesechos> almacenesGeneradoresDesechos, List<PuntoEliminacion> puntosEliminacion,
			List<GeneradorDesechosRecolector> recolectoresDesechos,
			List<GeneradorDesechosEliminador> eliminadoresDesechos, boolean guardarDocumento, boolean actualizacion) throws Exception, ServiceException, CmisAlfrescoException {

		//para validar que los puntos tenga asignado la parroquia
		if (puntosRecuperacion != null && !puntosRecuperacion.isEmpty()) {
			for (PuntoRecuperacion punto : puntosRecuperacion) {
				//punto.getUbicacionesGeografica().getUbicacionesGeografica().getUbicacionesGeografica().getCodificacionInec();
				if(punto.getUbicacionesGeografica().getCodificacionInec().length() < 6)
					throw new Exception("El punto de recuperación " + punto.getNombre() + " no tiene definida la ubicación");
			}
		}
		
		Area areaResponsable = null;
		if (generador.getResponsabilidadExtendida()) {
			areaResponsable = generador.getPoliticaDesecho().getAreaResponsable();
		} else {
			/*
			 * if (generador.getProyecto() != null) { areaResponsable =
			 * generador.getProyecto().getAreaResponsable(); } else {
			 */
			if (puntosRecuperacion != null && !puntosRecuperacion.isEmpty()) {
				if(puntosRecuperacion.get(0).getUbicacionesGeografica().getUbicacionesGeografica().getUbicacionesGeografica().getCodificacionInec().equals(Constantes.CODIGO_INEC_GALAPAGOS)) 
				areaResponsable = entidadResponsableFacade.buscarAreaDireccionProvincialPorUbicacion(puntosRecuperacion
						.get(0).getUbicacionesGeografica().getUbicacionesGeografica().getUbicacionesGeografica());
				else {
					if(generador.getAreaResponsable() == null 
							|| !generador.getAreaResponsable().getTipoArea().getSiglas().toUpperCase().equals(Constantes.SIGLAS_TIPO_AREA_OT))
						areaResponsable = entidadResponsableFacade.buscarAreaDireccionZonalPorUbicacion(puntosRecuperacion
								.get(0).getUbicacionesGeografica().getUbicacionesGeografica());
					else
						areaResponsable = generador.getAreaResponsable();
				}
			}
			// }
		}
		
		generador.setAreaResponsable(areaResponsable);

		/*Documento copiaJustificacion = null;
		if (generador.getDocumentoJustificacionProponente() != null) {
			copiaJustificacion = new Documento();
			copiaJustificacion.setContenidoDocumento(generador.getDocumentoJustificacionProponente()
					.getContenidoDocumento());
			generador.setDocumentoJustificacionProponente(null);
		}*/

		//crudServiceBean.saveOrUpdate(generador);

		if (actualizacion){

			Documento copiaJustificacion = null;
			if (generador.getDocumentoJustificacionProponente() != null) {
				copiaJustificacion = new Documento();
				copiaJustificacion.setContenidoDocumento(generador.getDocumentoJustificacionProponente()
						.getContenidoDocumento());
				generador.setDocumentoJustificacionProponente(null);
			}

			crudServiceBean.saveOrUpdate(generador);

			guardarPuntosRecuperacion(generador, puntosRecuperacion, processId, guardarDocumento);
			guardarDesechosSeleccionados(generador, desechosPeligrosos);
			guardarDatosGeneralesDesechos(generador, datosGeneralesDesechos);
			guardarDesechosEtiquetados(generador, desechosEtiquetados, processId);
		}else{//Emisión
			if(generador.getDocumentoJustificacionProponente() != null){

				Documento copiaJustificacion = new Documento();
				copiaJustificacion.setContenidoDocumento(generador.getDocumentoJustificacionProponente()
						.getContenidoDocumento());
				generador.setDocumentoJustificacionProponente(null);

				guardarJustificacionesProponente(generador, processId, copiaJustificacion);
				//crudServiceBean.saveOrUpdate(generador);
				
			}
//			if (generador.getDocumentoJustificacionProponente() != null || generador.getDocumentoJustificacionProponente() == null)//Para evitar que se guarde 2 veces el registro
			crudServiceBean.saveOrUpdate(generador);
				
		}

		/*guardarPuntosRecuperacion(generador, puntosRecuperacion, processId, guardarDocumento);
		guardarDesechosSeleccionados(generador, desechosPeligrosos);
		guardarDatosGeneralesDesechos(generador, datosGeneralesDesechos);
		guardarDesechosEtiquetados(generador, desechosEtiquetados, processId);

		if (!actualizacion){
			guardarIncompatibilidadesDesechos(generador, incompatibilidadesDesechos);
			guardarAlmacenesDesechos(generador, almacenesGeneradoresDesechos);
			guardarPuntosEliminacion(generador, puntosEliminacion);
			guardarRecolectoresDesechos(generador, recolectoresDesechos);
			guardarEliminadoresDesechos(generador, eliminadoresDesechos);
			guardarJustificacionesProponente(generador, processId, copiaJustificacion);
		}
*/
	}

	public void guardarPaso13(GeneradorDesechosPeligrosos generador, Long processId) throws ServiceException, CmisAlfrescoException {
		Documento copiaJustificacion = null;
		if (generador.getDocumentoJustificacionProponente() != null) {
			copiaJustificacion = new Documento();
			copiaJustificacion.setContenidoDocumento(generador.getDocumentoJustificacionProponente()
					.getContenidoDocumento());
			generador.setDocumentoJustificacionProponente(null);
		}

		crudServiceBean.saveOrUpdate(generador);
		guardarJustificacionesProponente(generador, processId, copiaJustificacion);
	}


	public void guardarPaso4(GeneradorDesechosPeligrosos generador,
							 List<DesechoPeligroso> desechosPeligrosos,
							 List<AlmacenGeneradorDesechos> almacenesGeneradoresDesechos) throws ServiceException, CmisAlfrescoException {
		guardarDesechosSeleccionados(generador, desechosPeligrosos);
		guardarAlmacenesDesechos(generador, almacenesGeneradoresDesechos);

	}

	public void guardarPaso7(GeneradorDesechosPeligrosos generador, Long processId,
						Map<DesechoPeligroso, List<IncompatibilidadDesechoPeligroso>> incompatibilidadesDesechos) throws ServiceException, CmisAlfrescoException {
		guardarIncompatibilidadesDesechos(generador, incompatibilidadesDesechos);
	}


	/**
	 * autor: vear
	 * fecha: 26/02/2016
	 * objetivo: modificar este metodo para que no persista todos los pasos
	 * arriba un respaldo del original
	 * @param generador
	 * @param processId
     * @throws ServiceException
     * @throws CmisAlfrescoException
     */
	public void guardarPaso2(GeneradorDesechosPeligrosos generador, Long processId) throws ServiceException, CmisAlfrescoException {
		crudServiceBean.saveOrUpdate(generador);
	}
	public void guardarPaso1(GeneradorDesechosPeligrosos generador, Long processId) throws ServiceException, CmisAlfrescoException {

		Area areaResponsable = null;
		if (generador.getResponsabilidadExtendida()) {
			areaResponsable = generador.getPoliticaDesecho().getAreaResponsable();
		}
		generador.setAreaResponsable(areaResponsable);

		Documento copiaJustificacion = null;
		if (generador.getDocumentoJustificacionProponente() != null) {
			copiaJustificacion = new Documento();
			copiaJustificacion.setContenidoDocumento(generador.getDocumentoJustificacionProponente()
					.getContenidoDocumento());
			generador.setDocumentoJustificacionProponente(null);
		}
		crudServiceBean.saveOrUpdate(generador);
	}

	public boolean datosLlenos(GeneradorDesechosPeligrosos generador, Long processId,String currentStep){
		boolean datos;
		datos=true;
		if (currentStep==null){
			if (generador.getResponsabilidadExtendida()==null){
				datos=false;
			}
		}
		return datos;
	}

	public void guardarPaso6(GeneradorDesechosPeligrosos generador, Long processId,
						GeneradorDesechosDesechoPeligrosoEtiquetado desechosEtiquetados)
			throws ServiceException, CmisAlfrescoException {

		guardarDesechoEtiquetado(generador, desechosEtiquetados, processId);
	}

	/**
	 * autor: vear
	 * fecha: 26/02/2016
	 * Objetivo: persistir solo el paso 5 del wizard, como objeto no como lista
	 *
	 * @param generador
	 * @param datosGeneralesDesechos
     * @throws ServiceException
     * @throws CmisAlfrescoException
     */
	public void guardarPaso5(GeneradorDesechosPeligrosos generador,
						GeneradorDesechosDesechoPeligrosoDatosGenerales datosGeneralesDesechos)
			throws ServiceException, CmisAlfrescoException {
		guardarDatosGeneralesDesecho(generador, datosGeneralesDesechos);
	}


	/**
	 * autor: vear
	 * fecha: 24/02/2016
	 * objetivo: guardar solo el paso 3 del wizar
	 * @param generador
	 * @param processId
	 * @param puntoRecuperacion
	 * @param guardarDocumento
	 * @throws ServiceException
	 * @throws CmisAlfrescoException
	 */
	public void guardarPaso3(GeneradorDesechosPeligrosos generador, Long processId,
							 PuntoRecuperacion puntoRecuperacion, boolean guardarDocumento) throws ServiceException, CmisAlfrescoException {
		Documento copiaJustificacion = null;
		if (generador.getDocumentoJustificacionProponente() != null) {
			copiaJustificacion = new Documento();
			copiaJustificacion.setContenidoDocumento(generador.getDocumentoJustificacionProponente()
					.getContenidoDocumento());
			generador.setDocumentoJustificacionProponente(null);
		}

		crudServiceBean.saveOrUpdate(generador);

		guardarPuntoRecuperacion(generador, puntoRecuperacion, processId, guardarDocumento);
	}

	/**
	 * autor: vear
	 * fecha: 24/02/2016
	 * objetivo: persistir solo el paso 3 del wizard como objeto no como lista
	 */




	public void guardar(GeneradorDesechosPeligrosos generador) throws ServiceException {
		crudServiceBean.saveOrUpdate(generador);
	}
	public void eliminar(GeneradorDesechosPeligrosos generador){
		crudServiceBean.delete(generador);
	}

	/**
	 * autor: vear
	 * fecha: 03/03/2016
	 *
	 * @param punto
     */
   public void eliminarPuntoRecuperacion(PuntoRecuperacion punto){
	   crudServiceBean.delete(punto);
   }

	/**
	 * autor: vear
	 * fecha: 03/03/2016
	 * objetivo: eliminar lógicamente de la bdd
	 */
	public void eliminarDesechoPeligrosoDatosGenerales(GeneradorDesechosDesechoPeligrosoDatosGenerales datosGenerales) {
		crudServiceBean.delete(datosGenerales);
	}

	/**
	 * autor: vear
	 * fecha: 03/03/2016
	 * objetivo: eliminar lógicamente de la bdd
	 * @param desechoEtiquetado
     */
	public void eliminarDesechoEtiquetado(GeneradorDesechosDesechoPeligrosoEtiquetado desechoEtiquetado) {
		crudServiceBean.delete(desechoEtiquetado);
	}


	/*eliminar paso 8*/
	public void eliminarAlmacenGeneradorDesechos(AlmacenGeneradorDesechos almacen) {
		crudServiceBean.delete(almacen);
	}

	/*eliminar paso 10*/
	public void eliminarPuntoEliminacion(PuntoEliminacion punto) {
		crudServiceBean.delete(punto);
	}

	/*eliminar paso 11*/
	public void eliminarGeneradorDesechosRecolector(GeneradorDesechosRecolector generadorDesechosRecolector) {
		crudServiceBean.delete(generadorDesechosRecolector);
	}

	/*eliminar paso 12*/
	public void eliminarGeneradorDesechosEliminador(GeneradorDesechosEliminador eliminador) {
		crudServiceBean.delete(eliminador);
	}


	/**
	 * autor:vear
	 * fecha: 03/03/2016
	 * @param generador
	 * @param punto
	 * @param processId
	 * @param guardarDocumento
	 * @throws ServiceException
	 * @throws CmisAlfrescoException
     */
	public void guardarPuntoRecuperacion(GeneradorDesechosPeligrosos generador,
										  PuntoRecuperacion punto, Long processId, boolean guardarDocumento) throws ServiceException, CmisAlfrescoException {
			punto.setEstado(true);
			punto.setGeneradorDesechosPeligrosos(generador);

			List<FormaPuntoRecuperacion> formasPuntoRecuperacion = new ArrayList<FormaPuntoRecuperacion>();
			if (punto.getFormasPuntoRecuperacion() != null)
				formasPuntoRecuperacion.addAll(punto.getFormasPuntoRecuperacion());
			punto.setFormasPuntoRecuperacion(null);

			Documento certificadoUsoSuelos = punto.getCertificadoUsoSuelos();
			Documento certificadoCompatibilidadUsoSuelos = punto.getCertificadoCompatibilidadUsoSuelos();

			//Seteamos null para poder persistir el punto.
			guardarDocumento = punto.getCertificadoUsoSuelos() != null || punto.getCertificadoCompatibilidadUsoSuelos() != null;
			punto.setCertificadoUsoSuelos(null);
			punto.setCertificadoCompatibilidadUsoSuelos(null);

			crudServiceBean.saveOrUpdate(punto);

			if (punto.getFormasPuntoRecuperacionActuales() != null)
			for (FormaPuntoRecuperacion forma : punto.getFormasPuntoRecuperacionActuales()) {
				List<Coordenada> coordenadas = new ArrayList<Coordenada>();
				coordenadas.addAll(forma.getCoordenadas());
				crudServiceBean.delete(forma);
				forma.setCoordenadas(coordenadas);
			}

			for (FormaPuntoRecuperacion forma : formasPuntoRecuperacion) {
				forma.setEstado(true);
				forma.setPuntoRecuperacion(punto);
				List<Coordenada> coordenadas = new ArrayList<Coordenada>();
				coordenadas.addAll(forma.getCoordenadas());

				crudServiceBean.saveOrUpdate(forma);
				for (Coordenada coordenada : coordenadas) {
					coordenada.setFormasPuntoRecuperacion(forma);
				}
				crudServiceBean.saveOrUpdate(coordenadas);
			}
			if(guardarDocumento){

				Documento documento = certificadoUsoSuelos != null ? certificadoUsoSuelos
						: certificadoCompatibilidadUsoSuelos;

				if (documento != null && ((certificadoUsoSuelos == documento && punto.isCertifUsoSuelosModif()) ||
						(certificadoCompatibilidadUsoSuelos == documento && punto.isCertifCompatibUsoSuelosModif()))) {
					documento.setIdTable(punto.getId());
					documento
							.setDescripcion(certificadoUsoSuelos != null ? Constantes.NOMBRE_DOCUMENTO_CERTIFICADO_USO_SUELOS
									: Constantes.NOMBRE_DOCUMENTO_CERTIFICADO_COMPATIBILIDAD_USO_SUELOS);
					documento.setEstado(true);
					TipoDocumentoSistema tipo = certificadoUsoSuelos != null ? TipoDocumentoSistema.TIPO_CERTIFICADO_USO_SUELOS
							: TipoDocumentoSistema.TIPO_CERTIFICADO_COMPATIBILIDAD_USO_SUELOS;
					if (generador.getProyecto() != null) {
						documento = documentosFacade.guardarDocumentoAlfresco(generador.getProyecto().getCodigo(),
								Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS, processId, documento, tipo, null);
					} else {
						documento = documentosFacade.guardarDocumentoAlfrescoSinProyecto(generador.getSolicitud(),
								Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS, processId, documento, tipo, null);
					}

					if (certificadoUsoSuelos != null)
						punto.setCertificadoUsoSuelos(documento);
					else
						punto.setCertificadoCompatibilidadUsoSuelos(documento);

					crudServiceBean.saveOrUpdate(punto);
				}

			}
	}

	public void guardarPuntosRecuperacion(GeneradorDesechosPeligrosos generador,
			List<PuntoRecuperacion> puntosRecuperacion, Long processId, boolean guardarDocumento) throws ServiceException, CmisAlfrescoException {

		List<PuntoRecuperacion> puntosRecuperacionAnteriores = generador.getPuntosRecuperacionActuales();

		for (PuntoRecuperacion punto : puntosRecuperacion) {
			if (puntosRecuperacionAnteriores != null && puntosRecuperacionAnteriores.contains(punto))
				puntosRecuperacionAnteriores.remove(punto);

			punto.setEstado(true);
			punto.setGeneradorDesechosPeligrosos(generador);

			List<FormaPuntoRecuperacion> formasPuntoRecuperacion = new ArrayList<FormaPuntoRecuperacion>();
			if (punto.getFormasPuntoRecuperacion() != null)
				formasPuntoRecuperacion.addAll(punto.getFormasPuntoRecuperacion());
			punto.setFormasPuntoRecuperacion(null);

			Documento certificadoUsoSuelos = punto.getCertificadoUsoSuelos();
			Documento certificadoCompatibilidadUsoSuelos = punto.getCertificadoCompatibilidadUsoSuelos();

			//Seteamos null para poder persistir el punto.
			guardarDocumento = punto.getCertificadoUsoSuelos() != null || punto.getCertificadoCompatibilidadUsoSuelos() != null;
			punto.setCertificadoUsoSuelos(null);
			punto.setCertificadoCompatibilidadUsoSuelos(null);

			crudServiceBean.saveOrUpdate(punto);

			if (punto.getFormasPuntoRecuperacionActuales() != null)
				for (FormaPuntoRecuperacion forma : punto.getFormasPuntoRecuperacionActuales()) {
					List<Coordenada> coordenadas = new ArrayList<Coordenada>();
					coordenadas.addAll(forma.getCoordenadas());
					crudServiceBean.delete(forma);
					forma.setCoordenadas(coordenadas);
				}

			for (FormaPuntoRecuperacion forma : formasPuntoRecuperacion) {
				forma.setEstado(true);
				forma.setPuntoRecuperacion(punto);
				List<Coordenada> coordenadas = new ArrayList<Coordenada>();
				coordenadas.addAll(forma.getCoordenadas());

				crudServiceBean.saveOrUpdate(forma);
				for (Coordenada coordenada : coordenadas) {
					coordenada.setFormasPuntoRecuperacion(forma);
				}
				crudServiceBean.saveOrUpdate(coordenadas);
			}
			if(guardarDocumento){

			Documento documento = certificadoUsoSuelos != null ? certificadoUsoSuelos
					: certificadoCompatibilidadUsoSuelos;

				if (documento != null && ((certificadoUsoSuelos == documento && punto.isCertifUsoSuelosModif()) ||
						(certificadoCompatibilidadUsoSuelos == documento && punto.isCertifCompatibUsoSuelosModif()))) {
				documento.setIdTable(punto.getId());
				documento
						.setDescripcion(certificadoUsoSuelos != null ? Constantes.NOMBRE_DOCUMENTO_CERTIFICADO_USO_SUELOS
								: Constantes.NOMBRE_DOCUMENTO_CERTIFICADO_COMPATIBILIDAD_USO_SUELOS);
				documento.setEstado(true);
				TipoDocumentoSistema tipo = certificadoUsoSuelos != null ? TipoDocumentoSistema.TIPO_CERTIFICADO_USO_SUELOS
						: TipoDocumentoSistema.TIPO_CERTIFICADO_COMPATIBILIDAD_USO_SUELOS;
				if (generador.getProyecto() != null) {
					documento = documentosFacade.guardarDocumentoAlfresco(generador.getProyecto().getCodigo(),
							Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS, processId, documento, tipo, null);
				} else {
					documento = documentosFacade.guardarDocumentoAlfrescoSinProyecto(generador.getSolicitud(),
							Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS, processId, documento, tipo, null);
				}

				if (certificadoUsoSuelos != null)
					punto.setCertificadoUsoSuelos(documento);
				else
					punto.setCertificadoCompatibilidadUsoSuelos(documento);

				crudServiceBean.saveOrUpdate(punto);
				}

			}
		}

		if (puntosRecuperacionAnteriores != null && !puntosRecuperacionAnteriores.isEmpty())
			crudServiceBean.delete(puntosRecuperacionAnteriores);
	}

	public void guardarDesechosSeleccionados(GeneradorDesechosPeligrosos generador,
			List<DesechoPeligroso> desechosPeligrosos) {

		List<GeneradorDesechosDesechoPeligroso> desechosPeligrososAnteriores = new ArrayList<GeneradorDesechosDesechoPeligroso>();
		if (generador.getGeneradorDesechosDesechoPeligrosos() != null) {
			if (generador.getGeneradorDesechosDesechoPeligrosos() != null && generador.getGeneradorDesechosDesechoPeligrosos().size()>0){
				desechosPeligrososAnteriores.addAll(generador.getGeneradorDesechosDesechoPeligrosos());
				for (GeneradorDesechosDesechoPeligroso generadorDesecho : generador.getGeneradorDesechosDesechoPeligrosos()) {
					if (desechosPeligrosos.contains(generadorDesecho.getDesechoPeligroso()))
						desechosPeligrososAnteriores.remove(generadorDesecho);
				}
			}
		}

		List<GeneradorDesechosDesechoPeligroso> desechos = new ArrayList<GeneradorDesechosDesechoPeligroso>();
		for (DesechoPeligroso desechoPeligroso : desechosPeligrosos) {
			GeneradorDesechosDesechoPeligroso generadorDesechosDesechoPeligroso = generador
					.getFromDesecho(desechoPeligroso);
			if (generadorDesechosDesechoPeligroso != null) {
				desechos.add(generadorDesechosDesechoPeligroso);
				continue;
			} else {
				generadorDesechosDesechoPeligroso = new GeneradorDesechosDesechoPeligroso();
				generadorDesechosDesechoPeligroso.setDesechoPeligroso(desechoPeligroso);
				generadorDesechosDesechoPeligroso.setGeneradorDesechosPeligrosos(generador);
				crudServiceBean.saveOrUpdate(generadorDesechosDesechoPeligroso);
				desechos.add(generadorDesechosDesechoPeligroso);
			}
		}

		if (desechosPeligrososAnteriores != null && !desechosPeligrososAnteriores.isEmpty())
			crudServiceBean.delete(desechosPeligrososAnteriores);

		generador.setGeneradorDesechosDesechoPeligrosos(desechos);
	}


	/*guardarAlmacenesDesechos8 Paso8*/
	public void guardar8(GeneradorDesechosPeligrosos generador,
						 AlmacenGeneradorDesechos almacenGeneradorDesechos, List<AlmacenGeneradorDesechos> almacenesGeneradorDesechos, Long processId) throws ServiceException, CmisAlfrescoException {

		crudServiceBean.saveOrUpdate(generador);

		guardarAlmacenesDesechos8(generador, almacenGeneradorDesechos, processId);

		guardarAlmacenesDesechos(generador, almacenesGeneradorDesechos);

		crudServiceBean.saveOrUpdate(generador);

	}

	public void EliminarPaso8(GeneradorDesechosPeligrosos generador,
							  List<AlmacenGeneradorDesechos> almacenesGeneradorDesechos) throws ServiceException, CmisAlfrescoException{
		guardarAlmacenesDesechos(generador,almacenesGeneradorDesechos);

		crudServiceBean.saveOrUpdate(generador);
	}
/*guardarPuntosEliminacion10 Paso10*/


	public void guardar10(GeneradorDesechosPeligrosos generador, PuntoEliminacion puntoEliminacion) throws ServiceException, CmisAlfrescoException {


		crudServiceBean.saveOrUpdate(generador);

		guardarPuntosEliminacion10(generador, puntoEliminacion);

	}

	/*guardarRecolectoresDesechos11 Paso11*/

	public void guardar11(GeneradorDesechosPeligrosos generador, GeneradorDesechosRecolector generadorDesechosRecolector, Long processId) throws ServiceException, CmisAlfrescoException {

	/*	Area areaResponsable = null;
		if (generador.getResponsabilidadExtendida()) {
			areaResponsable = generador.getPoliticaDesecho().getAreaResponsable();
		}
		generador.setAreaResponsable(areaResponsable);

		Documento copiaJustificacion = null;
		if (generador.getDocumentoJustificacionProponente() != null) {
			copiaJustificacion = new Documento();
			copiaJustificacion.setContenidoDocumento(generador.getDocumentoJustificacionProponente()
					.getContenidoDocumento());
			generador.setDocumentoJustificacionProponente(null);
		}*/

		crudServiceBean.saveOrUpdate(generador);

		guardarRecolectoresDesechos11(generador, generadorDesechosRecolector,  processId);

	}

	/*guardarEliminadoresDesechos12 Paso 12*/

	public void guardar12(GeneradorDesechosPeligrosos generador, GeneradorDesechosEliminador generadorDesechosEliminador, Long processId) throws ServiceException, CmisAlfrescoException {

		/*Area areaResponsable = null;
		if (generador.getResponsabilidadExtendida()) {
			areaResponsable = generador.getPoliticaDesecho().getAreaResponsable();
		}
		generador.setAreaResponsable(areaResponsable);

		Documento copiaJustificacion = null;
		if (generador.getDocumentoJustificacionProponente() != null) {
			copiaJustificacion = new Documento();
			copiaJustificacion.setContenidoDocumento(generador.getDocumentoJustificacionProponente()
					.getContenidoDocumento());
			generador.setDocumentoJustificacionProponente(null);
		}*/
		crudServiceBean.saveOrUpdate(generador);

		guardarEliminadoresDesechos12(generador, generadorDesechosEliminador, processId);
	}


	/*guardarAlmacenesDesechos8 Paso 8*/
	public void guardarAlmacenesDesechos8(GeneradorDesechosPeligrosos generador,
										  AlmacenGeneradorDesechos almacen, Long processId) throws ServiceException, CmisAlfrescoException {
		/**
		 * Byron Burbano
		 * 2019-06-10
		 * para adjuntar el permiso ambiental para almacenamiento temporal
		 * @throws CmisAlfrescoException 
		 * @throws ServiceException 
		 */	
		if (almacen.getPermisoAlmacenamientoTmp() != null && almacen.getPermisoAlmacenamientoTmp().getContenidoDocumento() != null){
			Documento objDocumento = almacen.getPermisoAlmacenamientoTmp();
			almacen.setPermisoAlmacenamientoTmp(null);
			crudServiceBean.saveOrUpdate(almacen);
			objDocumento.setIdTable(almacen.getId());
			objDocumento.setDescripcion(Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS_PERMISO_AMBIENTAL);
			objDocumento.setEstado(true);
			TipoDocumentoSistema tipo = TipoDocumentoSistema.TIPO_PERMISO_AMBIENTAL;
			if (generador.getProyecto() != null) {
				objDocumento = documentosFacade.guardarDocumentoAlfresco(generador.getProyecto().getCodigo(),
						Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS, processId, objDocumento, tipo, null);
			} else {
				objDocumento = documentosFacade.guardarDocumentoAlfrescoSinProyecto(generador.getSolicitud(),
						Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS, processId, objDocumento, tipo, null);
			}
			almacen.setPermisoAlmacenamientoTmp(objDocumento);
		}
		crudServiceBean.saveOrUpdate(almacen);

		for (DesechoPeligroso desechoPeligroso : almacen.getDesechosPeligrosos()) {
			GeneradorDesechosDesechoPeligroso desechosDesechoPeligroso = generador.getFromDesecho(desechoPeligroso);
			boolean save = true;
			if (almacen.getAlmacenGeneradorDesechoPeligrosos() != null) {
				for (AlmacenGeneradorDesechoPeligroso almacenGeneradorDesechoPeligroso : almacen
						.getAlmacenGeneradorDesechoPeligrosos()) {
					if (almacenGeneradorDesechoPeligroso.getGeneradorDesechosDesechoPeligroso() != null
							&& almacenGeneradorDesechoPeligroso.getGeneradorDesechosDesechoPeligroso().equals(
							desechosDesechoPeligroso)) {
						save = false;
						break;
					}
				}
			}

			if (save) {
				AlmacenGeneradorDesechoPeligroso almacenDesecho = new AlmacenGeneradorDesechoPeligroso();
				almacenDesecho.setAlmacenGeneradorDesechos(almacen);
				almacenDesecho.setGeneradorDesechosDesechoPeligroso(desechosDesechoPeligroso);
				crudServiceBean.saveOrUpdate(almacenDesecho);
			}
		}

		List<GeneradorDesechosAlmacenMedidaSeguridad> medidasAnteriores = new ArrayList<GeneradorDesechosAlmacenMedidaSeguridad>();
		if (almacen.getGeneradorDesechosAlmacenMedidasSeguridad() != null)
			medidasAnteriores.addAll(almacen.getGeneradorDesechosAlmacenMedidasSeguridad());

		for (TipoMedidaSeguridad tipoMedidaSeguridad : almacen.getMedidasSeguridad()) {
			boolean save = true;
			for (GeneradorDesechosAlmacenMedidaSeguridad almacenMedida : almacen
					.getGeneradorDesechosAlmacenMedidasSeguridad()) {
				if (almacenMedida.getTipoMedidaSeguridad().equals(tipoMedidaSeguridad)) {
					medidasAnteriores.remove(almacenMedida);
					save = false;

					if (isMedidaSeguridadOpcionOtro(almacenMedida.getTipoMedidaSeguridad())) {
						almacenMedida.setOtro(almacen.getTextoMedidaSeguridaOpcionOtro());
						crudServiceBean.saveOrUpdate(almacenMedida);
					}
					break;
				}
			}

			if (save) {
				GeneradorDesechosAlmacenMedidaSeguridad medidaSeguridad = new GeneradorDesechosAlmacenMedidaSeguridad();
				medidaSeguridad.setGeneradorDesechosAlmacen(almacen);
				medidaSeguridad.setTipoMedidaSeguridad(tipoMedidaSeguridad);
				if (isMedidaSeguridadOpcionOtro(medidaSeguridad.getTipoMedidaSeguridad()))
					medidaSeguridad.setOtro(almacen.getTextoMedidaSeguridaOpcionOtro());
				crudServiceBean.saveOrUpdate(medidaSeguridad);
			}
		}

		if (medidasAnteriores != null && !medidasAnteriores.isEmpty())
			crudServiceBean.delete(medidasAnteriores);
	}


	/*guardarRecolectoresDesechos11 Paso11*/

	public void guardarRecolectoresDesechos11(GeneradorDesechosPeligrosos generador,
											  GeneradorDesechosRecolector recolector, Long processId) throws ServiceException, CmisAlfrescoException {

		recolector.setGeneradorDesechosDesechoPeligroso(generador.getFromDesecho(recolector.getDesechoPeligroso()));

		List<GeneradorDesechosRecoletorUbicacionGeografica> ubicaciones = new ArrayList<GeneradorDesechosRecoletorUbicacionGeografica>();
		List<GeneradorDesechosRecolectorSede> sedes = new ArrayList<GeneradorDesechosRecolectorSede>();

		ubicaciones.addAll(recolector.getGeneradorDesechosRecoletorUbicacionesGeograficas());
		recolector.setGeneradorDesechosRecoletorUbicacionesGeograficas(null);

		sedes.addAll(recolector.getGeneradoresDesechosRecolectoresSedes());
		recolector.setGeneradoresDesechosRecolectoresSedes(null);

		crudServiceBean.saveOrUpdate(recolector);

		List<GeneradorDesechosRecoletorUbicacionGeografica> ubicacionesActuales = new ArrayList<GeneradorDesechosRecoletorUbicacionGeografica>();
		if (recolector.getGeneradorDesechosRecoletorUbicacionesGeograficasActuales() != null)
			ubicacionesActuales.addAll(recolector.getGeneradorDesechosRecoletorUbicacionesGeograficasActuales());

		List<GeneradorDesechosRecolectorSede> sedesAnteriores = new ArrayList<GeneradorDesechosRecolectorSede>();
		if (recolector.getGeneradoresDesechosRecolectoresSedesActuales() != null)
			sedesAnteriores.addAll(recolector.getGeneradoresDesechosRecolectoresSedesActuales());

		for (GeneradorDesechosRecoletorUbicacionGeografica ubicacion : ubicaciones) {
			if (ubicacionesActuales.contains(ubicacion))
				ubicacionesActuales.remove(ubicacion);
			else {
				ubicacion.setGeneradorDesechosRecolector(recolector);
				crudServiceBean.saveOrUpdate(ubicacion);
			}
		}

		if (ubicacionesActuales != null && !ubicacionesActuales.isEmpty())
			crudServiceBean.delete(ubicacionesActuales);

		for (GeneradorDesechosRecolectorSede sede : sedes) {
			if (sedesAnteriores.contains(sede))
				sedesAnteriores.remove(sede);
			else {
				sede.setGeneradorDesechosRecolector(recolector);
				/**
				 * Byron Burbano 2019-06-18
				 * guardao el archivo subido del permios de la empresa
				 * 
				 */
				if (sede.getPermisoAmbiental() != null ){
					Documento objDocumento = sede.getPermisoAmbiental();
					if(sede.getPermisoAmbiental().getContenidoDocumento() != null){
						sede.setPermisoAmbiental(null);
					}
					if(sede.getSedePrestadorServiciosDesechos().getId().equals(0)){
						sede.setSedePrestadorServiciosDesechos(null);
					}
					crudServiceBean.saveOrUpdate(sede);
					if(objDocumento.getContenidoDocumento() != null){
						objDocumento.setIdTable(sede.getId());
						objDocumento.setDescripcion(Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS_PERMISO_AMBIENTAL);
						objDocumento.setEstado(true);
						TipoDocumentoSistema tipo = TipoDocumentoSistema.TIPO_PERMISO_AMBIENTAL;
						if (generador.getProyecto() != null) {
							objDocumento = documentosFacade.guardarDocumentoAlfresco(generador.getProyecto().getCodigo(),
									Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS, processId, objDocumento, tipo, null);
						} else {
							objDocumento = documentosFacade.guardarDocumentoAlfrescoSinProyecto(generador.getSolicitud(),
									Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS, processId, objDocumento, tipo, null);
						}
					}
					sede.setPermisoAmbiental(objDocumento);
				}
				crudServiceBean.saveOrUpdate(sede);
			}
		}

		if (sedesAnteriores != null && !sedesAnteriores.isEmpty())
			crudServiceBean.delete(sedesAnteriores);
	}



	/*guardarEliminadoresDesechos12 Paso 12*/
	public void guardarEliminadoresDesechos12(GeneradorDesechosPeligrosos generador,
											  GeneradorDesechosEliminador eliminador, Long processId) throws ServiceException, CmisAlfrescoException {

		eliminador.setGeneradorDesechosDesechoPeligroso(generador.getFromDesecho(eliminador.getDesechoPeligroso()));

		List<GeneradorDesechosEliminadorSede> sedes = new ArrayList<GeneradorDesechosEliminadorSede>();

		sedes.addAll(eliminador.getGeneradoresDesechosEliminadoresSedes());
		eliminador.setGeneradoresDesechosEliminadoresSedes(null);

		crudServiceBean.saveOrUpdate(eliminador);

		List<GeneradorDesechosEliminadorSede> sedesAnteriores = new ArrayList<GeneradorDesechosEliminadorSede>();
		if (eliminador.getGeneradoresDesechosEliminadoresSedesActuales() != null)
			sedesAnteriores.addAll(eliminador.getGeneradoresDesechosEliminadoresSedesActuales());

		for (GeneradorDesechosEliminadorSede sede : sedes) {
			if (sedesAnteriores.contains(sede))
				sedesAnteriores.remove(sede);
			else {
				sede.setGeneradorDesechosEliminador(eliminador);
				/**
				 * Byron Burbano 2019-06-18
				 * guardao el archivo subido del permios de la empresa
				 * 
				 */
				if (sede.getPermisoAmbiental() != null ){
					Documento objDocumento = sede.getPermisoAmbiental();
					if(sede.getPermisoAmbiental().getContenidoDocumento() != null){
						sede.setPermisoAmbiental(null);
					}
					if(sede.getSedePrestadorServiciosDesechos().getId().equals(0)){
						sede.setSedePrestadorServiciosDesechos(null);
					}
					crudServiceBean.saveOrUpdate(sede);
					if(objDocumento.getContenidoDocumento() != null){
						objDocumento.setIdTable(sede.getId());
						objDocumento.setDescripcion(Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS_PERMISO_AMBIENTAL);
						objDocumento.setEstado(true);
						TipoDocumentoSistema tipo = TipoDocumentoSistema.TIPO_PERMISO_AMBIENTAL;
						if (generador.getProyecto() != null) {
							objDocumento = documentosFacade.guardarDocumentoAlfresco(generador.getProyecto().getCodigo(),
									Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS, processId, objDocumento, tipo, null);
						} else {
							objDocumento = documentosFacade.guardarDocumentoAlfrescoSinProyecto(generador.getSolicitud(),
									Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS, processId, objDocumento, tipo, null);
						}
					}
					sede.setPermisoAmbiental(objDocumento);
				}
				crudServiceBean.saveOrUpdate(sede);
			}
		}

		if (sedesAnteriores != null && !sedesAnteriores.isEmpty())
			crudServiceBean.delete(sedesAnteriores);

	}


	public void guardarDatosGeneralesDesechos(GeneradorDesechosPeligrosos generador,
											  List<GeneradorDesechosDesechoPeligrosoDatosGenerales> datosGeneralesDesechos) {

		List<GeneradorDesechosDesechoPeligrosoDatosGenerales> datosGeneralesDesechosAnteriores = new ArrayList<GeneradorDesechosDesechoPeligrosoDatosGenerales>();
		if (generador.getDatosGenerales() != null)
			datosGeneralesDesechosAnteriores.addAll(generador.getDatosGenerales());

		for (GeneradorDesechosDesechoPeligrosoDatosGenerales datosGenerales : datosGeneralesDesechos) {
			if (datosGeneralesDesechosAnteriores.contains(datosGenerales))
				datosGeneralesDesechosAnteriores.remove(datosGenerales);

			List<GeneradorDesechosDesechoPeligrosoPuntoGeneracion> puntosGeneracion = new ArrayList<GeneradorDesechosDesechoPeligrosoPuntoGeneracion>();
			if (datosGenerales.getGeneradorDesechosDesechoPeligrosoPuntosGeneracion() != null)
				puntosGeneracion.addAll(datosGenerales.getGeneradorDesechosDesechoPeligrosoPuntosGeneracion());
			GeneradorDesechosDesechoPeligroso desechoPeligroso = generador.getFromDesecho(datosGenerales
					.getDesechoPeligroso());
			datosGenerales.setGeneradorDesechosDesechoPeligrosoPuntosGeneracion(null);
			crudServiceBean.saveOrUpdate(datosGenerales);
			desechoPeligroso.setGeneradorDesechosDesechoPeligrosoDatosGenerales(datosGenerales);
			crudServiceBean.saveOrUpdate(desechoPeligroso);
			for (GeneradorDesechosDesechoPeligrosoPuntoGeneracion puntoGeneracion : puntosGeneracion) {
				if (datosGenerales.getGeneradorDesechosDesechoPeligrosoPuntosGeneracionActuales() != null
						&& datosGenerales.getGeneradorDesechosDesechoPeligrosoPuntosGeneracionActuales().contains(
						puntoGeneracion))
					datosGenerales.getGeneradorDesechosDesechoPeligrosoPuntosGeneracionActuales().remove(
							puntoGeneracion);
				puntoGeneracion.setGeneraDesechosDesechoPeligrosoDatosGenerales(datosGenerales);
			}
			crudServiceBean.saveOrUpdate(puntosGeneracion);

			if (datosGenerales.getGeneradorDesechosDesechoPeligrosoPuntosGeneracionActuales() != null
					&& !datosGenerales.getGeneradorDesechosDesechoPeligrosoPuntosGeneracionActuales().isEmpty())
				crudServiceBean.delete(datosGenerales.getGeneradorDesechosDesechoPeligrosoPuntosGeneracionActuales());
		}

		if (datosGeneralesDesechosAnteriores != null && !datosGeneralesDesechosAnteriores.isEmpty())
			crudServiceBean.delete(datosGeneralesDesechosAnteriores);
	}



	public void guardarDatosGeneralesDesecho(GeneradorDesechosPeligrosos generador,
			GeneradorDesechosDesechoPeligrosoDatosGenerales datosGenerales) {
			List<GeneradorDesechosDesechoPeligrosoPuntoGeneracion> puntosGeneracion = new ArrayList<GeneradorDesechosDesechoPeligrosoPuntoGeneracion>();
			if (datosGenerales.getGeneradorDesechosDesechoPeligrosoPuntosGeneracion() != null)
				puntosGeneracion.addAll(datosGenerales.getGeneradorDesechosDesechoPeligrosoPuntosGeneracion());
			GeneradorDesechosDesechoPeligroso desechoPeligroso = generador.getFromDesecho(datosGenerales
					.getDesechoPeligroso());
			datosGenerales.setGeneradorDesechosDesechoPeligrosoPuntosGeneracion(null);
			crudServiceBean.saveOrUpdate(datosGenerales);
			desechoPeligroso.setGeneradorDesechosDesechoPeligrosoDatosGenerales(datosGenerales);
			crudServiceBean.saveOrUpdate(desechoPeligroso);
			for (GeneradorDesechosDesechoPeligrosoPuntoGeneracion puntoGeneracion : puntosGeneracion) {
				if (datosGenerales.getGeneradorDesechosDesechoPeligrosoPuntosGeneracionActuales() != null
						&& datosGenerales.getGeneradorDesechosDesechoPeligrosoPuntosGeneracionActuales().contains(
								puntoGeneracion))
					datosGenerales.getGeneradorDesechosDesechoPeligrosoPuntosGeneracionActuales().remove(
							puntoGeneracion);
				puntoGeneracion.setGeneraDesechosDesechoPeligrosoDatosGenerales(datosGenerales);
			}
			crudServiceBean.saveOrUpdate(puntosGeneracion);

			if (datosGenerales.getGeneradorDesechosDesechoPeligrosoPuntosGeneracionActuales() != null
					&& !datosGenerales.getGeneradorDesechosDesechoPeligrosoPuntosGeneracionActuales().isEmpty())
				crudServiceBean.delete(datosGenerales.getGeneradorDesechosDesechoPeligrosoPuntosGeneracionActuales());
	}

	public void guardarDesechosEtiquetados(GeneradorDesechosPeligrosos generador,
			List<GeneradorDesechosDesechoPeligrosoEtiquetado> desechosEtiquetado, Long processId)
			throws ServiceException, CmisAlfrescoException {

		List<GeneradorDesechosDesechoPeligrosoEtiquetado> desechosEtiquetadosAnteriores = new ArrayList<GeneradorDesechosDesechoPeligrosoEtiquetado>();
		if (generador.getDatosGenerales() != null)
			desechosEtiquetadosAnteriores.addAll(generador.getEnvasadosEtiquetados());

		for (GeneradorDesechosDesechoPeligrosoEtiquetado desechoEtiquetado : desechosEtiquetado) {
			if (desechosEtiquetadosAnteriores.contains(desechoEtiquetado))
				desechosEtiquetadosAnteriores.remove(desechoEtiquetado);

			Documento modeloEtiqueta = desechoEtiquetado.getModeloEtiqueta();
			desechoEtiquetado.setModeloEtiqueta(null);
			GeneradorDesechosDesechoPeligroso desecho = generador.getFromDesecho(desechoEtiquetado
					.getDesechoPeligroso());
			crudServiceBean.saveOrUpdate(desechoEtiquetado);
			desecho.setGeneradorDesechosDesechoPeligrosoEtiquetado(desechoEtiquetado);
			crudServiceBean.saveOrUpdate(desecho);

			if(modeloEtiqueta != null) {
				modeloEtiqueta.setIdTable(desechoEtiquetado.getId());
				modeloEtiqueta.setDescripcion(Constantes.NOMBRE_DOCUMENTO_MODELO_ETIQUETA);
				modeloEtiqueta.setEstado(true);
				TipoDocumentoSistema tipo = TipoDocumentoSistema.TIPO_MODELO_ETIQUETA;
				if (generador.getProyecto() != null) {
					modeloEtiqueta = documentosFacade.guardarDocumentoAlfresco(generador.getProyecto().getCodigo(),
							Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS, processId, modeloEtiqueta, tipo, null);
				} else {
					modeloEtiqueta = documentosFacade.guardarDocumentoAlfrescoSinProyecto(generador.getSolicitud(),
							Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS, processId, modeloEtiqueta, tipo, null);
				}
				desechoEtiquetado.setModeloEtiqueta(modeloEtiqueta);
			}
			crudServiceBean.saveOrUpdate(desechoEtiquetado);
		}

		if (desechosEtiquetadosAnteriores != null && !desechosEtiquetadosAnteriores.isEmpty())
			crudServiceBean.delete(desechosEtiquetadosAnteriores);
	}


	public void guardarDesechoEtiquetado(GeneradorDesechosPeligrosos generador,
										   GeneradorDesechosDesechoPeligrosoEtiquetado desechoEtiquetado, Long processId)
			throws ServiceException, CmisAlfrescoException {

		Documento modeloEtiqueta = desechoEtiquetado.getModeloEtiqueta();
		TipoDocumentoSistema tipo = TipoDocumentoSistema.TIPO_MODELO_ETIQUETA;

		if (desechoEtiquetado.getId() == null){//Si es una inserción:
			desechoEtiquetado.setModeloEtiqueta(null);
			GeneradorDesechosDesechoPeligroso desecho = generador.getFromDesecho(desechoEtiquetado
					.getDesechoPeligroso());

			crudServiceBean.saveOrUpdate(desechoEtiquetado);
			desecho.setGeneradorDesechosDesechoPeligrosoEtiquetado(desechoEtiquetado);
			crudServiceBean.saveOrUpdate(desecho);

			modeloEtiqueta.setIdTable(desechoEtiquetado.getId());
			modeloEtiqueta.setDescripcion(Constantes.NOMBRE_DOCUMENTO_MODELO_ETIQUETA);
			modeloEtiqueta.setEstado(true);

			if (generador.getProyecto() != null) {
				modeloEtiqueta = documentosFacade.guardarDocumentoAlfresco(generador.getProyecto().getCodigo(),
						Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS, processId, modeloEtiqueta, tipo, null);
			} else {
				modeloEtiqueta = documentosFacade.guardarDocumentoAlfrescoSinProyecto(generador.getSolicitud(),
						Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS, processId, modeloEtiqueta, tipo, null);
			}
			}else{//Es una actualización

				String nombreDocu = modeloEtiqueta.getNombre();
				int version;
				try{
					version = Integer.parseInt(nombreDocu.substring(nombreDocu.lastIndexOf("_v")+1));

				}catch (Exception ex){
					version = 0;
				}

				version++;
				nombreDocu= nombreDocu.substring(0,nombreDocu.lastIndexOf("."))+"_v"+version+modeloEtiqueta.getExtesion();

				modeloEtiqueta.setNombre(nombreDocu);//Cambiamos el nombre concatenando la versión.
				//NOTA: El alfresco está dando error cuando intenta crear un documento con el mismo nombre
				//en el mismo directorio (No versiona).

				if (generador.getProyecto() != null) {
					modeloEtiqueta = documentosFacade.guardarDocumentoAlfresco(generador.getProyecto().getCodigo(),
							Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS, processId, modeloEtiqueta, tipo, null);
				} else {
					modeloEtiqueta = documentosFacade.guardarDocumentoAlfrescoSinProyecto(generador.getSolicitud(),
							Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS, processId, modeloEtiqueta, tipo, null);
				}
				crudServiceBean.saveOrUpdate(modeloEtiqueta);

			}

			desechoEtiquetado.setModeloEtiqueta(modeloEtiqueta);
			crudServiceBean.saveOrUpdate(desechoEtiquetado);
	}

	public void guardarIncompatibilidadesDesechos(GeneradorDesechosPeligrosos generador,
			Map<DesechoPeligroso, List<IncompatibilidadDesechoPeligroso>> incompatibilidadesDesechos) {

		for (DesechoPeligroso desechoPeligroso : incompatibilidadesDesechos.keySet()) {
			GeneradorDesechosDesechoPeligroso desecho = generador.getFromDesecho(desechoPeligroso);

			List<IncompatibilidadDesechoPeligroso> incompatibilidades = incompatibilidadesDesechos
					.get(desechoPeligroso);

			List<IncompatibilidadGeneradorDesechosDesecho> incompatibilidadesAnteriores = new ArrayList<IncompatibilidadGeneradorDesechosDesecho>();
			if (desecho.getIncompatibilidades() != null)
				incompatibilidadesAnteriores.addAll(desecho.getIncompatibilidades());

			for (IncompatibilidadDesechoPeligroso incompatibilidadDesecho : incompatibilidades) {
				boolean save = true;
				for (IncompatibilidadGeneradorDesechosDesecho incompatibilidad : desecho.getIncompatibilidades()) {
					if (incompatibilidad.getIncompatibilidadDesechoPeligroso().equals(incompatibilidadDesecho)) {
						incompatibilidadesAnteriores.remove(incompatibilidad);

						if (incompatibilidadDesecho.isOtro()) {
							incompatibilidad.setOtro(incompatibilidadDesecho.getOtroValor());
							crudServiceBean.saveOrUpdate(incompatibilidad);
						}

						save = false;
						break;
					}
				}

				if (save) {
					IncompatibilidadGeneradorDesechosDesecho incompatibilidad = new IncompatibilidadGeneradorDesechosDesecho();
					incompatibilidad.setGeneradorDesechosDesechoPeligroso(desecho);
					incompatibilidad.setIncompatibilidadDesechoPeligroso(incompatibilidadDesecho);
					if (incompatibilidadDesecho.isOtro())
						incompatibilidad.setOtro(incompatibilidadDesecho.getOtroValor());
					crudServiceBean.saveOrUpdate(incompatibilidad);
				}
			}

			if (incompatibilidadesAnteriores != null && !incompatibilidadesAnteriores.isEmpty())
				crudServiceBean.delete(incompatibilidadesAnteriores);
		}
	}

	public void guardarAlmacenesDesechos(GeneradorDesechosPeligrosos generador,
			List<AlmacenGeneradorDesechos> almacenesGeneradoresDesechos) {

		List<AlmacenGeneradorDesechos> almacenesGeneradoresDesechosAnteriores = new ArrayList<AlmacenGeneradorDesechos>();
		if (generador.getAlmacenes() != null)
			almacenesGeneradoresDesechosAnteriores.addAll(generador.getAlmacenes());

		for (AlmacenGeneradorDesechos almacen : almacenesGeneradoresDesechos) {
			if (almacenesGeneradoresDesechosAnteriores.contains(almacen))
				almacenesGeneradoresDesechosAnteriores.remove(almacen);

			List<AlmacenGeneradorDesechoPeligroso> desechosAnteriores = new ArrayList<AlmacenGeneradorDesechoPeligroso>();
			if (almacen.getAlmacenGeneradorDesechoPeligrosos() != null)
				desechosAnteriores.addAll(almacen.getAlmacenGeneradorDesechoPeligrosos());

			crudServiceBean.saveOrUpdate(almacen);

			for (DesechoPeligroso desechoPeligroso : almacen.getDesechosPeligrosos()) {
				GeneradorDesechosDesechoPeligroso desechosDesechoPeligroso = generador.getFromDesecho(desechoPeligroso);

				boolean save = true;
				if (almacen.getAlmacenGeneradorDesechoPeligrosos() != null) {
					for (AlmacenGeneradorDesechoPeligroso almacenGeneradorDesechoPeligroso : almacen
							.getAlmacenGeneradorDesechoPeligrosos()) {
						if (almacenGeneradorDesechoPeligroso.getGeneradorDesechosDesechoPeligroso() != null
								&& almacenGeneradorDesechoPeligroso.getGeneradorDesechosDesechoPeligroso().equals(
										desechosDesechoPeligroso)) {
							desechosAnteriores.remove(almacenGeneradorDesechoPeligroso);
							save = false;
							break;
						}
					}
				}

				if (save) {
					AlmacenGeneradorDesechoPeligroso almacenDesecho = new AlmacenGeneradorDesechoPeligroso();
					almacenDesecho.setAlmacenGeneradorDesechos(almacen);
					almacenDesecho.setGeneradorDesechosDesechoPeligroso(desechosDesechoPeligroso);
					crudServiceBean.saveOrUpdate(almacenDesecho);
				}
			}

			if (desechosAnteriores != null && !desechosAnteriores.isEmpty())
				crudServiceBean.delete(desechosAnteriores);

			List<GeneradorDesechosAlmacenMedidaSeguridad> medidasAnteriores = new ArrayList<GeneradorDesechosAlmacenMedidaSeguridad>();
			if (almacen.getGeneradorDesechosAlmacenMedidasSeguridad() != null)
				medidasAnteriores.addAll(almacen.getGeneradorDesechosAlmacenMedidasSeguridad());

			for (TipoMedidaSeguridad tipoMedidaSeguridad : almacen.getMedidasSeguridad()) {
				boolean save = true;
				for (GeneradorDesechosAlmacenMedidaSeguridad almacenMedida : almacen
						.getGeneradorDesechosAlmacenMedidasSeguridad()) {
					if (almacenMedida.getTipoMedidaSeguridad().equals(tipoMedidaSeguridad)) {
						medidasAnteriores.remove(almacenMedida);
						save = false;

						if (isMedidaSeguridadOpcionOtro(almacenMedida.getTipoMedidaSeguridad())) {
							almacenMedida.setOtro(almacen.getTextoMedidaSeguridaOpcionOtro());
							crudServiceBean.saveOrUpdate(almacenMedida);
						}
						break;
					}
				}

				if (save) {
					GeneradorDesechosAlmacenMedidaSeguridad medidaSeguridad = new GeneradorDesechosAlmacenMedidaSeguridad();
					medidaSeguridad.setGeneradorDesechosAlmacen(almacen);
					medidaSeguridad.setTipoMedidaSeguridad(tipoMedidaSeguridad);
					if (isMedidaSeguridadOpcionOtro(medidaSeguridad.getTipoMedidaSeguridad()))
						medidaSeguridad.setOtro(almacen.getTextoMedidaSeguridaOpcionOtro());
					crudServiceBean.saveOrUpdate(medidaSeguridad);
				}
			}

			if (medidasAnteriores != null && !medidasAnteriores.isEmpty())
				crudServiceBean.delete(medidasAnteriores);
		}

		if (almacenesGeneradoresDesechosAnteriores != null && !almacenesGeneradoresDesechosAnteriores.isEmpty())
			crudServiceBean.delete(almacenesGeneradoresDesechosAnteriores);
	}

	/*guardarPuntosEliminacion10 Paso10 Paso 10*/

	public void guardarPuntosEliminacion10(GeneradorDesechosPeligrosos generador, PuntoEliminacion puntoEliminacion) {

		puntoEliminacion.setGeneradorDesechosDesechoPeligroso(generador.getFromDesecho(puntoEliminacion
				.getDesechoPeligroso()));

		List<PuntoEliminacionPrestadorServicioDesechoPeligroso> prestadores = new ArrayList<PuntoEliminacionPrestadorServicioDesechoPeligroso>();
		prestadores.addAll(puntoEliminacion.getPuntoEliminacionPrestadorServicioDesechoPeligrosos());
		puntoEliminacion.setPuntoEliminacionPrestadorServicioDesechoPeligrosos(null);
		crudServiceBean.saveOrUpdate(puntoEliminacion);
		for (PuntoEliminacionPrestadorServicioDesechoPeligroso prestador : prestadores) {
			if (puntoEliminacion.getPuntoEliminacionPrestadorServicioDesechoPeligrososActuales() != null
					&& puntoEliminacion.getPuntoEliminacionPrestadorServicioDesechoPeligrososActuales().contains(
					prestador)) {
				puntoEliminacion.getPuntoEliminacionPrestadorServicioDesechoPeligrososActuales().remove(prestador);
				continue;
			}
			prestador.setPuntoEliminacion(puntoEliminacion);
			crudServiceBean.saveOrUpdate(prestador);
		}

		if (puntoEliminacion.getPuntoEliminacionPrestadorServicioDesechoPeligrososActuales() != null
				&& !puntoEliminacion.getPuntoEliminacionPrestadorServicioDesechoPeligrososActuales().isEmpty())
			crudServiceBean
					.delete(puntoEliminacion.getPuntoEliminacionPrestadorServicioDesechoPeligrososActuales());
	}


	public void guardarPuntosEliminacion(GeneradorDesechosPeligrosos generador, List<PuntoEliminacion> puntosEliminacion) {
		List<PuntoEliminacion> puntosEliminacionAnteriores = new ArrayList<PuntoEliminacion>();
		if (generador.getPuntosEliminacion() != null)
			puntosEliminacionAnteriores.addAll(generador.getPuntosEliminacion());

		for (PuntoEliminacion puntoEliminacion : puntosEliminacion) {
			if (puntosEliminacionAnteriores.contains(puntoEliminacion))
				puntosEliminacionAnteriores.remove(puntoEliminacion);

			puntoEliminacion.setGeneradorDesechosDesechoPeligroso(generador.getFromDesecho(puntoEliminacion
					.getDesechoPeligroso()));

			List<PuntoEliminacionPrestadorServicioDesechoPeligroso> prestadores = new ArrayList<PuntoEliminacionPrestadorServicioDesechoPeligroso>();
			prestadores.addAll(puntoEliminacion.getPuntoEliminacionPrestadorServicioDesechoPeligrosos());
			puntoEliminacion.setPuntoEliminacionPrestadorServicioDesechoPeligrosos(null);
			crudServiceBean.saveOrUpdate(puntoEliminacion);
			for (PuntoEliminacionPrestadorServicioDesechoPeligroso prestador : prestadores) {
				if (puntoEliminacion.getPuntoEliminacionPrestadorServicioDesechoPeligrososActuales() != null
						&& puntoEliminacion.getPuntoEliminacionPrestadorServicioDesechoPeligrososActuales().contains(
								prestador)) {
					puntoEliminacion.getPuntoEliminacionPrestadorServicioDesechoPeligrososActuales().remove(prestador);
					continue;
				}
				prestador.setPuntoEliminacion(puntoEliminacion);
				crudServiceBean.saveOrUpdate(prestador);
			}

			if (puntoEliminacion.getPuntoEliminacionPrestadorServicioDesechoPeligrososActuales() != null
					&& !puntoEliminacion.getPuntoEliminacionPrestadorServicioDesechoPeligrososActuales().isEmpty())
				crudServiceBean
						.delete(puntoEliminacion.getPuntoEliminacionPrestadorServicioDesechoPeligrososActuales());

		}

		if (puntosEliminacionAnteriores != null && !puntosEliminacionAnteriores.isEmpty())
			crudServiceBean.delete(puntosEliminacionAnteriores);
	}

	public void guardarRecolectoresDesechos(GeneradorDesechosPeligrosos generador,
			List<GeneradorDesechosRecolector> recolectoresDesechos) {

		List<GeneradorDesechosRecolector> recolectoresDesechosAnteriores = new ArrayList<GeneradorDesechosRecolector>();
		if (generador.getGeneradoresDesechosRecolectores() != null)
			recolectoresDesechosAnteriores.addAll(generador.getGeneradoresDesechosRecolectores());

		for (GeneradorDesechosRecolector recolector : recolectoresDesechos) {
			if (recolectoresDesechosAnteriores.contains(recolector))
				recolectoresDesechosAnteriores.remove(recolector);

			recolector.setGeneradorDesechosDesechoPeligroso(generador.getFromDesecho(recolector.getDesechoPeligroso()));

			List<GeneradorDesechosRecoletorUbicacionGeografica> ubicaciones = new ArrayList<GeneradorDesechosRecoletorUbicacionGeografica>();
			List<GeneradorDesechosRecolectorSede> sedes = new ArrayList<GeneradorDesechosRecolectorSede>();

			ubicaciones.addAll(recolector.getGeneradorDesechosRecoletorUbicacionesGeograficas());
			recolector.setGeneradorDesechosRecoletorUbicacionesGeograficas(null);

			sedes.addAll(recolector.getGeneradoresDesechosRecolectoresSedes());
			recolector.setGeneradoresDesechosRecolectoresSedes(null);

			crudServiceBean.saveOrUpdate(recolector);

			List<GeneradorDesechosRecoletorUbicacionGeografica> ubicacionesActuales = new ArrayList<GeneradorDesechosRecoletorUbicacionGeografica>();
			if (recolector.getGeneradorDesechosRecoletorUbicacionesGeograficasActuales() != null)
				ubicacionesActuales.addAll(recolector.getGeneradorDesechosRecoletorUbicacionesGeograficasActuales());

			List<GeneradorDesechosRecolectorSede> sedesAnteriores = new ArrayList<GeneradorDesechosRecolectorSede>();
			if (recolector.getGeneradoresDesechosRecolectoresSedesActuales() != null)
				sedesAnteriores.addAll(recolector.getGeneradoresDesechosRecolectoresSedesActuales());

			for (GeneradorDesechosRecoletorUbicacionGeografica ubicacion : ubicaciones) {
				if (ubicacionesActuales.contains(ubicacion))
					ubicacionesActuales.remove(ubicacion);
				else {
					ubicacion.setGeneradorDesechosRecolector(recolector);
					crudServiceBean.saveOrUpdate(ubicacion);
				}
			}

			if (ubicacionesActuales != null && !ubicacionesActuales.isEmpty())
				crudServiceBean.delete(ubicacionesActuales);

			for (GeneradorDesechosRecolectorSede sede : sedes) {
				if (sedesAnteriores.contains(sede))
					sedesAnteriores.remove(sede);
				else {
					sede.setGeneradorDesechosRecolector(recolector);
					crudServiceBean.saveOrUpdate(sede);
				}
			}

			if (sedesAnteriores != null && !sedesAnteriores.isEmpty())
				crudServiceBean.delete(sedesAnteriores);
		}

		if (recolectoresDesechosAnteriores != null && !recolectoresDesechosAnteriores.isEmpty())
			crudServiceBean.delete(recolectoresDesechosAnteriores);
	}

	public void guardarEliminadoresDesechos(GeneradorDesechosPeligrosos generador,
			List<GeneradorDesechosEliminador> eliminadoresDesechos) {

		List<GeneradorDesechosEliminador> eliminadoresDesechosAnteriores = new ArrayList<GeneradorDesechosEliminador>();
		if (generador.getGeneradoresDesechosEliminadores() != null)
			eliminadoresDesechosAnteriores.addAll(generador.getGeneradoresDesechosEliminadores());

		for (GeneradorDesechosEliminador eliminador : eliminadoresDesechos) {
			if (eliminadoresDesechosAnteriores.contains(eliminador))
				eliminadoresDesechosAnteriores.remove(eliminador);

			eliminador.setGeneradorDesechosDesechoPeligroso(generador.getFromDesecho(eliminador.getDesechoPeligroso()));

			List<GeneradorDesechosEliminadorSede> sedes = new ArrayList<GeneradorDesechosEliminadorSede>();

			sedes.addAll(eliminador.getGeneradoresDesechosEliminadoresSedes());
			eliminador.setGeneradoresDesechosEliminadoresSedes(null);

			crudServiceBean.saveOrUpdate(eliminador);

			List<GeneradorDesechosEliminadorSede> sedesAnteriores = new ArrayList<GeneradorDesechosEliminadorSede>();
			if (eliminador.getGeneradoresDesechosEliminadoresSedesActuales() != null)
				sedesAnteriores.addAll(eliminador.getGeneradoresDesechosEliminadoresSedesActuales());

			for (GeneradorDesechosEliminadorSede sede : sedes) {
				if (sedesAnteriores.contains(sede))
					sedesAnteriores.remove(sede);
				else {
					sede.setGeneradorDesechosEliminador(eliminador);
					crudServiceBean.saveOrUpdate(sede);
				}
			}

			if (sedesAnteriores != null && !sedesAnteriores.isEmpty())
				crudServiceBean.delete(sedesAnteriores);
		}

		if (eliminadoresDesechosAnteriores != null && !eliminadoresDesechosAnteriores.isEmpty())
			crudServiceBean.delete(eliminadoresDesechosAnteriores);
	}

	/**
	 *
	 * @param usuario
	 *            usuario autenticado
	 * @param clazz
	 *            el tramite resolver del registro de generador
	 * @param proyecto
	 *            un proyecto, si no esta asociado, pasar NULL
	 * @return arreglo de objetos, en la primera posicion un Long
	 *         processInstanceId iniciado, en la seguda posicion un Map<String,
	 *         Object> con las variables del proceso iniciado
	 * @throws JbpmException
	 */
	public synchronized Object[] iniciarEmisionProcesoRegistroGenerador(Usuario usuario, Class<?> clazz,
			ProyectoLicenciamientoAmbiental proyecto) throws JbpmException {
		String numeroSolicitud = generarNumeroSolicitud();

		Object[] result = new Object[2];

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("sujetoControl", usuario.getNombre());
		params.put("tipoTramite", true);
		params.put("accion", "emision.jsf");
		params.put(Constantes.VARIABLE_PROCESO_TRAMITE_RESOLVER, clazz.getName());
		params.put(GeneradorDesechosPeligrosos.VARIABLE_NUMERO_SOLICITUD, numeroSolicitud);
		if (proyecto != null) {
			params.put(Constantes.ID_PROYECTO, proyecto.getId());
			params.put(Constantes.CODIGO_PROYECTO, proyecto.getCodigo());
		} else
			params.put(Constantes.USUARIO_VISTA_MIS_PROCESOS, usuario.getNombre());

		Long processInstanceId = procesoFacade.iniciarProceso(usuario, Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS,
				proyecto == null ? numeroSolicitud : proyecto.getCodigo(), params);

		result[0] = processInstanceId;
		result[1] = params;

		return result;
	}

	public void continuarProcesoRegistroGenerador(GeneradorDesechosPeligrosos generador, long processInstanceID,
			long taskId, Usuario usuario) throws JbpmException, ServiceException {
		boolean responsabilidadExtendida = generador.getResponsabilidadExtendida() != null
				&& generador.getResponsabilidadExtendida();
		Usuario subsecretaria = null;
		Usuario director = null;
		Usuario coordinador = null;

		subsecretaria = areaFacade.getSubsecretariaCalidadAmbiental();
		if (responsabilidadExtendida) {
			director = areaFacade.getUsuarioPorRolArea("role.gerente", generador.getAreaResponsable());
			coordinador = areaFacade.getUsuarioPorRolArea("role.pc.coordinador", generador.getAreaResponsable());
		} else {
			if(generador.getAreaResponsable().getArea() != null)
				director = areaFacade.getDirectorProvincial(generador.getAreaResponsable().getArea());
			else
				director = areaFacade.getDirectorProvincial(generador.getAreaResponsable());
			coordinador = areaFacade.getCoordinadorProvincialRegistro(generador.getAreaResponsable());
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(GeneradorDesechosPeligrosos.VARIABLE_ID_GENERADOR, generador.getId());
		params.put("subsecretaria", subsecretaria.getNombre());
		params.put("director", director.getNombre());
		params.put("coordinador", coordinador.getNombre());
		params.put("esResponsabilidadExtendida", generador.getResponsabilidadExtendida());
		params.put("mensajeArchivadoTitulo", "Notificación de proceso archivado");
		params.put("mensajeArchivadoContenido",
				"En cumplimiento de las normas vigentes, teniendo en cuenta que se ha excedido el número "
						+ "de revisiones permitidas sobre el proceso de Registro de generador de desechos especiales "
						+ "y/o peligrosos, el tr&aacute;mite de la solicitud " + generador.getSolicitud()
						+ " ha sido archivado.");
		params.put(Constantes.USUARIO_FUNCIONARIO_VISTA_MIS_PROCESOS, "subsecretaria, director, coordinador, tecnico");

		try {
			params.put("urlAsignacionAutomatica", Constantes.getWorkloadServiceURL());
			boolean plantaCentral = generador.getAreaResponsable().getTipoArea().getSiglas()
					.equals(Constantes.getRoleAreaName("area.plantacentral"));
//			String roleType = plantaCentral ? "role.pc.tecnico.rgd" : "role.area.tecnico.rgd";
			String roleType = plantaCentral ? "role.pc.tecnico" : "role.area.tecnico.registro.generador";
			params.put("requestBody", "rol=" + Constantes.getRoleAreaName(roleType) + "&area=" + generador.getAreaResponsable().getAreaName());
			
			
			//Asigna un técnico predeterminado
			Map<String, Object> variables=procesoFacade.recuperarVariablesProceso(usuario, processInstanceID);
			Object varTecnico = variables.get("tecnico");
			if(varTecnico==null) {
				Usuario usuarioTecnico= areaFacade.getUsuarioPorRolArea(roleType, generador.getAreaResponsable());
				if(usuarioTecnico!=null) {
					params.put("tecnico", usuarioTecnico.getNombre());
					params.put("tecnicoPredeterminado", usuarioTecnico.getNombre());//para historial
				}else {
					return;
				}
			}else {
				Usuario usuarioTecnico= areaFacade.getUsuarioPorRolArea(roleType, generador.getAreaResponsable());
				if(usuarioTecnico!=null) {
					params.put("tecnico", usuarioTecnico.getNombre());
					params.put("tecnicoPredeterminado", usuarioTecnico.getNombre());//para historial
				}else {
					return;
				}
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (generador.getAreaResponsable().getId().equals(Area.PNGIDS)) {
			String equipoMultidisciplinarioSolicitaApoyo = "role.pc.coordinador" + "--"
					+ Area.UNIDAD_DE_PRODUCTOS_DESECHOS_PELIGROSOS_Y_NO_PELIGROSOS_SUB_DNCA;
			params.put("equipoMultidisciplinarioSolicitaApoyo", equipoMultidisciplinarioSolicitaApoyo);
		} else if (generador.getAreaResponsable().getId()
				.equals(Area.UNIDAD_DE_PRODUCTOS_DESECHOS_PELIGROSOS_Y_NO_PELIGROSOS_SUB_DNCA)) {
			String equipoMultidisciplinarioSolicitaApoyo = "role.pc.coordinador" + "--" + Area.PNGIDS;
			params.put("equipoMultidisciplinarioSolicitaApoyo", equipoMultidisciplinarioSolicitaApoyo);
		}

		procesoFacade.modificarVariablesProceso(usuario, processInstanceID, params);
		procesoFacade.aprobarTarea(usuario, taskId, processInstanceID, null);
		procesoFacade.envioSeguimientoRGD(usuario, processInstanceID);
	}

	public void iniciarProcesoActualizacionRegistroGenerador(GeneradorDesechosPeligrosos generador, Usuario usuario,
			Class<?> tramiteResolver,boolean bloquearGuardarNuevo) throws JbpmException, ServiceException {
		Usuario subsecretaria = null;
		Usuario director = null;
		Usuario coordinador = null;

		subsecretaria = areaFacade.getSubsecretariaCalidadAmbiental();
		if (generador.getResponsabilidadExtendida() != null && generador.getResponsabilidadExtendida()) {
			director = areaFacade.getUsuarioPorRolArea("role.gerente", generador.getAreaResponsable());
			coordinador = areaFacade.getUsuarioPorRolArea("role.pc.coordinador", generador.getAreaResponsable());
		} else {
			if(generador.getAreaResponsable().getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)) {
				director = areaFacade.getDirectorProvincial(generador.getAreaResponsable());
        	} else {
        		director = areaFacade.getDirectorProvincial(generador.getAreaResponsable().getArea());
        	}
			
			coordinador = areaFacade.getCoordinadorProvincialRegistro(generador.getAreaResponsable());
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("sujetoControl", usuario.getNombre());
		params.put("tipoTramite", false);
		params.put("accion", "actualizacion.jsf");params.put(Constantes.VARIABLE_PROCESO_TRAMITE_RESOLVER, tramiteResolver.getName());
		params.put(GeneradorDesechosPeligrosos.VARIABLE_NUMERO_SOLICITUD, generador.getSolicitud());

		if (generador.getProyecto() != null) {
			params.put(Constantes.ID_PROYECTO, generador.getProyecto().getId());
			params.put(Constantes.CODIGO_PROYECTO, generador.getProyecto().getCodigo());
		} else
			params.put(Constantes.USUARIO_VISTA_MIS_PROCESOS, usuario.getNombre());

		params.put(GeneradorDesechosPeligrosos.VARIABLE_ID_GENERADOR, generador.getId());
		params.put("subsecretaria", subsecretaria.getNombre());
		params.put("director", director.getNombre());
		params.put("coordinador", coordinador.getNombre());
		params.put("esResponsabilidadExtendida", generador.getResponsabilidadExtendida());
		params.put("mensajeArchivadoTitulo", "Notificación de proceso archivado");
		params.put("mensajeArchivadoContenido",
				"En cumplimiento de las normas vigentes, teniendo en cuenta que se ha excedido el número "
						+ "de revisiones permitidas sobre el proceso de Registro de generador de desechos especiales "
						+ "y/o peligrosos, el trámite de la solicitud " + generador.getSolicitud()
						+ " ha sido archivado.");
		params.put(Constantes.USUARIO_FUNCIONARIO_VISTA_MIS_PROCESOS, "subsecretaria, director, coordinador, tecnico");

		try {
			params.put("urlAsignacionAutomatica", Constantes.getWorkloadServiceURL());
			boolean plantaCentral = generador.getAreaResponsable().getTipoArea().getSiglas()
					.equals(Constantes.getRoleAreaName("area.plantacentral"));
			String roleType = plantaCentral ? "role.pc.tecnico" : "role.area.tecnico.registro.generador";
			String area = generador.getAreaResponsable().getAreaName();
			params.put("requestBody", "rol=" + Constantes.getRoleAreaName(roleType) + "&area=" + area);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (generador.getAreaResponsable().getId().equals(Area.PNGIDS)) {
			String equipoMultidisciplinarioSolicitaApoyo = "role.pc.coordinador" + "--"
					+ Area.UNIDAD_DE_PRODUCTOS_DESECHOS_PELIGROSOS_Y_NO_PELIGROSOS_SUB_DNCA;
			params.put("equipoMultidisciplinarioSolicitaApoyo", equipoMultidisciplinarioSolicitaApoyo);
		} else if (generador.getAreaResponsable().getId()
				.equals(Area.UNIDAD_DE_PRODUCTOS_DESECHOS_PELIGROSOS_Y_NO_PELIGROSOS_SUB_DNCA)) {
			String equipoMultidisciplinarioSolicitaApoyo = "role.pc.coordinador" + "--" + Area.PNGIDS;
			params.put("equipoMultidisciplinarioSolicitaApoyo", equipoMultidisciplinarioSolicitaApoyo);
		}
		procesoFacade.iniciarProceso(usuario, Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS,
				generador.getProyecto() == null ? generador.getSolicitud() : generador.getProyecto().getCodigo(),
				params);
//		 if(bloquearGuardarNuevo){
//			 generador.setCodigo(null);
//         	 guardar(generador);
//		 }
	}

	public GeneradorDesechosPeligrosos get(Integer id) throws Exception {
		try {
			GeneradorDesechosPeligrosos generador = crudServiceBean.find(GeneradorDesechosPeligrosos.class, id);
			generador.getAreaResponsable().getTipoArea().getSiglas();
			return generador;
		} catch (Exception e) {
			throw new Exception("No se puede cargar el registro de generador.", e);
		}
	}
	
	public GeneradorDesechosPeligrosos get(Integer id, Integer area) throws Exception {
		try {
			GeneradorDesechosPeligrosos generador = crudServiceBean.find(GeneradorDesechosPeligrosos.class, id);
			Area areaResponsablepadre= new Area();
			if (generador.getAreaResponsable()==null || generador.getAreaResponsable().getTipoArea().getId()==3){
				Area areaResponsable= areaFacade.getArea(area);
				
				if (areaResponsable.getArea().getId()!=null){
					areaResponsablepadre= areaFacade.getArea(areaResponsable.getArea().getId());
					generador.setAreaResponsable(areaResponsablepadre);										
				}else{
					generador.setAreaResponsable(areaResponsable);		
				}
				crudServiceBean.saveOrUpdate(generador);
			}else{
				generador.getAreaResponsable().getTipoArea().getSiglas();
			}				
			return generador;
		} catch (Exception e) {
			throw new Exception("No se puede cargar el registro de generador.", e);
		}
	}
	

	public GeneradorDesechosPeligrosos get(String solicitud) throws Exception {
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("solicitud", solicitud);
			List<GeneradorDesechosPeligrosos> listaGeneradores = (List<GeneradorDesechosPeligrosos>)crudServiceBean.findByNamedQuery(
					GeneradorDesechosPeligrosos.GET_BY_SOLICITUD, parameters);

			GeneradorDesechosPeligrosos generador = listaGeneradores != null && !listaGeneradores.isEmpty() ? listaGeneradores.get(listaGeneradores.size()-1) : null;

			if (generador != null) {
				if (generador.getAreaResponsable() != null) {
					generador.getAreaResponsable().getAreaAbbreviation();
				} else if (generador.getProyecto() != null){
					generador.setAreaResponsable(generador.getProyecto()
							.getAreaResponsable());
				}
				if (generador.getGeneradorDesechosDesechoPeligrosos()!=null)
					generador.getGeneradorDesechosDesechoPeligrosos().size();
			}
			return generador;
		} catch (Exception e) {
			throw new Exception("No se puede cargar el registro de generador.", e);
		}
	}
	
	public GeneradorDesechosPeligrosos getRGD(String solicitud){
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("solicitud", solicitud);
			@SuppressWarnings("unchecked")
			List<GeneradorDesechosPeligrosos> listaGeneradores = (List<GeneradorDesechosPeligrosos>)crudServiceBean.findByNamedQuery(
					GeneradorDesechosPeligrosos.GET_BY_SOLICITUD, parameters);

			GeneradorDesechosPeligrosos generador = listaGeneradores != null && !listaGeneradores.isEmpty() ? listaGeneradores.get(listaGeneradores.size()-1) : null;

			return generador;
		}catch (NoResultException e) {
			return null;
		}
	}
	
	public GeneradorDesechosPeligrosos get(String solicitud,Integer area) throws Exception {
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("solicitud", solicitud);
			List<GeneradorDesechosPeligrosos> listaGeneradores = (List<GeneradorDesechosPeligrosos>)crudServiceBean.findByNamedQuery(
					GeneradorDesechosPeligrosos.GET_BY_SOLICITUD, parameters);

			GeneradorDesechosPeligrosos generador = listaGeneradores != null && !listaGeneradores.isEmpty() ? listaGeneradores.get(listaGeneradores.size()-1) : null;

			if (generador != null) {
				if (generador.getAreaResponsable() != null) {
					generador.getAreaResponsable().getAreaAbbreviation();
				} else if (generador.getProyecto() != null){
					Area areaResponsable= areaFacade.getArea(area);
					Area areaResponsablepadre= new Area();
					if (areaResponsable.getArea() !=null){
						areaResponsablepadre= areaFacade.getArea(areaResponsable.getArea().getId());
						generador.setAreaResponsable(areaResponsablepadre);										
					}else{
						generador.setAreaResponsable(areaResponsable);		
					}
					crudServiceBean.saveOrUpdate(generador);
				}
				if (generador.getGeneradorDesechosDesechoPeligrosos()!=null)
					generador.getGeneradorDesechosDesechoPeligrosos().size();
			}
			return generador;
		} catch (Exception e) {
			throw new Exception("No se puede cargar el registro de generador.", e);
		}
	}

	public GeneradorDesechosPeligrosos cargarGeneradorParaDocumentoPorId(Integer id) throws Exception {
		try {
			GeneradorDesechosPeligrosos generador = crudServiceBean.find(GeneradorDesechosPeligrosos.class, id);

			if(generador != null){
				if (generador.getAreaResponsable() != null){
					generador.getAreaResponsable().getTipoArea().getSiglas();
					if (generador.getAreaResponsable().getTipoEnteAcreditado()!=null){
						generador.getAreaResponsable().toString();
					}else{
						generador.getAreaResponsable().getUbicacionesGeografica();
					}
					
				}
				/*if(generador.getPuntosRecuperacion()!=null)
					generador.getPuntosRecuperacion().size(); // se elimina carga de puntos para mejorar rendimiento*/
				if(generador.getGeneradorDesechosDesechoPeligrosos()!=null)
					generador.getGeneradorDesechosDesechoPeligrosos().size();
				try {
					generador.getUsuario().getPersona().getUbicacionesGeografica().getUbicacionesGeografica()
							.getUbicacionesGeografica();
				} catch (Exception e) {
				}
				try {
					generador.getUsuario().getPersona().getTipoTratos().getNombre();
				} catch (Exception e) {
				}
			}
			return generador;
		} catch (Exception e) {
			throw new Exception("No se puede cargar el registro de generador.", e);
		}
	}

	public GeneradorDesechosPeligrosos cargarGeneradorFullPorId(Integer id, Boolean cargarPuntos) throws Exception {

		try {
			GeneradorDesechosPeligrosos generador = crudServiceBean.find(GeneradorDesechosPeligrosos.class, id);

			if (generador.getAreaResponsable() != null) {
				if (generador.getAreaResponsable().getTipoArea() != null)
					generador.getAreaResponsable().getTipoArea().getSiglas();
			}

			if (generador.getDocumentoJustificacionProponente() != null) {
				generador.getDocumentoJustificacionProponente().setContenidoDocumento(
						documentosFacade.descargar(generador.getDocumentoJustificacionProponente().getIdAlfresco()));
			}
			
			if(cargarPuntos) {
				for (PuntoRecuperacion puntoRecuperacion : generador.getPuntosRecuperacion()) {
					puntoRecuperacion.getCertificadoUsoSuelos();
					puntoRecuperacion.getCertificadoCompatibilidadUsoSuelos();
					Documento documento = puntoRecuperacion.getCertificadoUsoSuelos() != null ? puntoRecuperacion
							.getCertificadoUsoSuelos() : puntoRecuperacion.getCertificadoCompatibilidadUsoSuelos();
					if (documento != null)
						documento.setContenidoDocumento(documentosFacade.descargar(documento.getIdAlfresco()));
					for (FormaPuntoRecuperacion forma : puntoRecuperacion.getFormasPuntoRecuperacion())
						forma.getCoordenadas().size();
					puntoRecuperacion.setFormasPuntoRecuperacionActuales(new ArrayList<FormaPuntoRecuperacion>());
					puntoRecuperacion.getFormasPuntoRecuperacionActuales().addAll(
							puntoRecuperacion.getFormasPuntoRecuperacion());
				}
				generador.setPuntosRecuperacionActuales(new ArrayList<PuntoRecuperacion>());
				generador.getPuntosRecuperacionActuales().addAll(generador.getPuntosRecuperacion());
			}


			if (generador.getGeneradorDesechosDesechoPeligrosos()!=null && generador.getGeneradorDesechosDesechoPeligrosos().size()>0 )
				for (GeneradorDesechosDesechoPeligroso desechoGenerador : generador.getGeneradorDesechosDesechoPeligrosos()) {
					if (desechoGenerador.getGeneradorDesechosDesechoPeligrosoDatosGenerales() != null) {
						desechoGenerador.getGeneradorDesechosDesechoPeligrosoDatosGenerales()
								.getGeneradorDesechosDesechoPeligrosoPuntosGeneracion().size();
						desechoGenerador.getGeneradorDesechosDesechoPeligrosoDatosGenerales()
								.setGeneradorDesechosDesechoPeligrosoPuntosGeneracionActuales(
										new ArrayList<GeneradorDesechosDesechoPeligrosoPuntoGeneracion>());
						desechoGenerador
								.getGeneradorDesechosDesechoPeligrosoDatosGenerales()
								.getGeneradorDesechosDesechoPeligrosoPuntosGeneracionActuales()
								.addAll(desechoGenerador.getGeneradorDesechosDesechoPeligrosoDatosGenerales()
										.getGeneradorDesechosDesechoPeligrosoPuntosGeneracion());
					}
					if (desechoGenerador.getGeneradorDesechosDesechoPeligrosoEtiquetado() != null) {
						desechoGenerador.getGeneradorDesechosDesechoPeligrosoEtiquetado().getModeloEtiqueta();
						if (desechoGenerador.getGeneradorDesechosDesechoPeligrosoEtiquetado().getModeloEtiqueta() != null) {

							String workspace = desechoGenerador
									.getGeneradorDesechosDesechoPeligrosoEtiquetado().getModeloEtiqueta()
									.getIdAlfresco();
							desechoGenerador
									.getGeneradorDesechosDesechoPeligrosoEtiquetado()
									.getModeloEtiqueta()
									.setContenidoDocumento(
											documentosFacade.descargar(workspace));
						}
					}
					if (desechoGenerador.getIncompatibilidades() != null) {
						desechoGenerador.getIncompatibilidades().size();
						for (IncompatibilidadGeneradorDesechosDesecho incompatibilidad : desechoGenerador
								.getIncompatibilidades()) {
							if (incompatibilidad.getIncompatibilidadDesechoPeligroso() != null
									&& incompatibilidad.getIncompatibilidadDesechoPeligroso().isOtro())
								incompatibilidad.getIncompatibilidadDesechoPeligroso().setOtroValor(
										incompatibilidad.getOtro());
						}
					}

					if (desechoGenerador.getAlmacenGeneradorDesechoPeligrosos() != null) {
						desechoGenerador.getAlmacenGeneradorDesechoPeligrosos().size();
						for (AlmacenGeneradorDesechoPeligroso almacen : desechoGenerador
								.getAlmacenGeneradorDesechoPeligrosos()) {
							if (almacen.getAlmacenGeneradorDesechos().getGeneradorDesechosAlmacenMedidasSeguridad() != null)
								almacen.getAlmacenGeneradorDesechos().getGeneradorDesechosAlmacenMedidasSeguridad().size();
						}
					}

					for (PuntoEliminacion punto : desechoGenerador.getPuntosEliminacion()) {
						if (punto.getPuntoEliminacionPrestadorServicioDesechoPeligrosos() != null) {
							punto.getPuntoEliminacionPrestadorServicioDesechoPeligrosos().size();
							punto.setPuntoEliminacionPrestadorServicioDesechoPeligrososActuales(new ArrayList<PuntoEliminacionPrestadorServicioDesechoPeligroso>());
							punto.getPuntoEliminacionPrestadorServicioDesechoPeligrososActuales().addAll(
									punto.getPuntoEliminacionPrestadorServicioDesechoPeligrosos());
							if (punto.getGeneradorDesechosDesechoPeligroso() != null
									&& punto.getGeneradorDesechosDesechoPeligroso().getDesechoPeligroso() != null)
								punto.setDesechoPeligroso(punto.getGeneradorDesechosDesechoPeligroso()
										.getDesechoPeligroso());
						}
					}

					for (GeneradorDesechosRecolector desechosRecolector : desechoGenerador
							.getGeneradoresDesechosRecolectores()) {
						if (desechosRecolector.getGeneradoresDesechosRecolectoresSedes() != null) {
							desechosRecolector.getGeneradoresDesechosRecolectoresSedes().size();
							desechosRecolector
									.setGeneradoresDesechosRecolectoresSedesActuales(new ArrayList<GeneradorDesechosRecolectorSede>());
							if (desechosRecolector.getGeneradoresDesechosRecolectoresSedes() != null)
								desechosRecolector.getGeneradoresDesechosRecolectoresSedesActuales().addAll(
										desechosRecolector.getGeneradoresDesechosRecolectoresSedes());

							desechosRecolector.getGeneradorDesechosRecoletorUbicacionesGeograficas().size();
							desechosRecolector
									.setGeneradorDesechosRecoletorUbicacionesGeograficasActuales(new ArrayList<GeneradorDesechosRecoletorUbicacionGeografica>());
							desechosRecolector.getGeneradorDesechosRecoletorUbicacionesGeograficasActuales().addAll(
									desechosRecolector.getGeneradorDesechosRecoletorUbicacionesGeograficas());

							if (desechosRecolector.getGeneradorDesechosDesechoPeligroso() != null
									&& desechosRecolector.getGeneradorDesechosDesechoPeligroso().getDesechoPeligroso() != null)
								desechosRecolector.setDesechoPeligroso(desechosRecolector
										.getGeneradorDesechosDesechoPeligroso().getDesechoPeligroso());
						}
					}

					for (GeneradorDesechosEliminador desechosEliminador : desechoGenerador
							.getGeneradoresDesechosEliminadores()) {
						if (desechosEliminador.getGeneradoresDesechosEliminadoresSedes() != null) {
							desechosEliminador.getGeneradoresDesechosEliminadoresSedes().size();
							desechosEliminador
									.setGeneradoresDesechosEliminadoresSedesActuales(new ArrayList<GeneradorDesechosEliminadorSede>());
							if (desechosEliminador.getGeneradoresDesechosEliminadoresSedes() != null)
								desechosEliminador.getGeneradoresDesechosEliminadoresSedesActuales().addAll(
										desechosEliminador.getGeneradoresDesechosEliminadoresSedes());

							if (desechosEliminador.getGeneradorDesechosDesechoPeligroso() != null
									&& desechosEliminador.getGeneradorDesechosDesechoPeligroso().getDesechoPeligroso() != null)
								desechosEliminador.setDesechoPeligroso(desechosEliminador
										.getGeneradorDesechosDesechoPeligroso().getDesechoPeligroso());
						}
					}
				}
			return generador;
		} catch (Exception e) {
			throw new Exception("No se puede cargar el registro de generador.", e);
		}
	}

	public GeneradorDesechosPeligrosos buscarGeneradorPorCodigo(String codigo, String usuario) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("codigo", codigo);
		parameters.put("usuario", usuario);
		List<GeneradorDesechosPeligrosos> listaGgeneradores = (List<GeneradorDesechosPeligrosos>)crudServiceBean.findByNamedQuery(
				GeneradorDesechosPeligrosos.GET_BY_CODE, parameters);

		GeneradorDesechosPeligrosos generador = listaGgeneradores != null && !listaGgeneradores.isEmpty() ? listaGgeneradores.get(listaGgeneradores.size()-1) : null;

		if (generador != null) {
			generador.getProyecto();
			generador.getTipoSector();
			generador.getUsuario().toString();
		}
		return generador;
	}

	@SuppressWarnings("unchecked")
	public List<PoliticaDesecho> getPoliticasDesechos() {
		return (List<PoliticaDesecho>) crudServiceBean.findAll(PoliticaDesecho.class);
	}

	@SuppressWarnings("unchecked")
	public List<TipoEtiquetado> getTiposEtiquetados() {
		return (List<TipoEtiquetado>) crudServiceBean.findAll(TipoEtiquetado.class);
	}

	@SuppressWarnings("unchecked")
	public List<TipoEnvase> getTiposEnvases() {
		return (List<TipoEnvase>) crudServiceBean.findAll(TipoEnvase.class);
	}

	@SuppressWarnings("unchecked")
	public List<TipoLocal> getTiposLocal() {
		return (List<TipoLocal>) crudServiceBean.findAll(TipoLocal.class);
	}

	@SuppressWarnings("unchecked")
	public List<TipoVentilacion> getTiposVentilacion() {
		return (List<TipoVentilacion>) crudServiceBean.findAll(TipoVentilacion.class);
	}

	@SuppressWarnings("unchecked")
	public List<TipoIluminacion> getTiposIluminacion() {
		return (List<TipoIluminacion>) crudServiceBean.findAll(TipoIluminacion.class);
	}

	@SuppressWarnings("unchecked")
	public List<TipoMaterialConstruccion> getTiposMaterialConstruccion() {
		return (List<TipoMaterialConstruccion>) crudServiceBean.findAll(TipoMaterialConstruccion.class);
	}

	@SuppressWarnings("unchecked")
	public List<TipoSector> getTiposSectores() {
		List<TipoSector> result = (List<TipoSector>) crudServiceBean.findAll(TipoSector.class);
		TipoSector licenciamiento = new TipoSector(TipoSector.TIPO_SECTOR_LICENCIAMIENTO);
		if (result.contains(licenciamiento))
			result.remove(licenciamiento);
		Collections.sort(result, new OrdenarTipoSectorPorNombreComparator());
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<TipoEstadoFisico> getTiposEstadosFisicos() {
		return (List<TipoEstadoFisico>) crudServiceBean.findAll(TipoEstadoFisico.class);
	}

	public UnidadMedida getUnidadMedidaTonelada() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("unidad", "t");
		return (UnidadMedida) crudServiceBean.findByNamedQuerySingleResult(UnidadMedida.GET_BY_UNIDAD, params);
	}

	public UnidadMedida getUnidadMedidaUnidad() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("unidad", "u");
		return (UnidadMedida) crudServiceBean.findByNamedQuerySingleResult(UnidadMedida.GET_BY_UNIDAD, params);
	}

	public UnidadMedida getUnidadMedidaTonelada(List<UnidadMedida> unidades) {
		for (UnidadMedida unidadMedida : unidades) {
			if (unidadMedida.getSiglas().toLowerCase().equals("t"))
				return unidadMedida;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<CategoriaDesechoPeligroso> buscarCategoriaDesechoPeligrososPadres(String filtro) {
		List<CategoriaDesechoPeligroso> desechos = new ArrayList<CategoriaDesechoPeligroso>();
		if (filtro != null && !filtro.isEmpty()) {
			filtro = filtro.toLowerCase();
			Query query = crudServiceBean
					.getEntityManager()
					.createQuery(
							"FROM CategoriaDesechoPeligroso c WHERE (lower(c.nombre) like :filtro OR lower(c.clave) like :filtro) ORDER BY c.id");
			query.setParameter("filtro", "%" + filtro + "%");
			desechos = query.getResultList();
		} else {
			Query query = crudServiceBean.getEntityManager().createQuery(
					"FROM CategoriaDesechoPeligroso c WHERE c.categoriaDesechoPeligroso = null ORDER BY c.id");
			desechos = query.getResultList();
		}
		initIsCategoriaDesechoPeligrosoFinal(desechos);
		return desechos;
	}

	@SuppressWarnings("unchecked")
	public List<CategoriaDesechoPeligroso> buscarCategoriaDesechoPeligrososPorPadre(CategoriaDesechoPeligroso padre) {
		List<CategoriaDesechoPeligroso> desechos = (List<CategoriaDesechoPeligroso>) crudServiceBean
				.getEntityManager()
				.createQuery("From CategoriaDesechoPeligroso c where c.categoriaDesechoPeligroso =:padre order by c.id")
				.setParameter("padre", padre).getResultList();
		initIsCategoriaDesechoPeligrosoFinal(desechos);
		return desechos;
	}

	private void initIsCategoriaDesechoPeligrosoFinal(List<CategoriaDesechoPeligroso> desechos) {
		if (desechos != null)
			for (CategoriaDesechoPeligroso categoriaDesechoPeligroso : desechos)
				categoriaDesechoPeligroso.isCategoriaDesechoFinal();
	}

	@SuppressWarnings("unchecked")
	public List<PuntoGeneracion> getPuntosGeneracion(String filter) {
		if (filter == null || filter.trim().isEmpty())
			return (List<PuntoGeneracion>) crudServiceBean.findAll(PuntoGeneracion.class);
		else {
			filter = filter.toLowerCase();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("filter", "%" + filter + "%");
			return (List<PuntoGeneracion>) crudServiceBean.findByNamedQuery(PuntoGeneracion.FILTRAR, params);
		}
	}

	@SuppressWarnings("unchecked")
	public List<TipoMedidaSeguridad> getTiposMedidasSeguridad(String filter) {
		if (filter == null || filter.trim().isEmpty())
			return (List<TipoMedidaSeguridad>) crudServiceBean.findAll(TipoMedidaSeguridad.class);
		else {
			filter = filter.toLowerCase();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("filter", "%" + filter + "%");
			return (List<TipoMedidaSeguridad>) crudServiceBean.findByNamedQuery(TipoMedidaSeguridad.FILTRAR, params);
		}
	}

	@SuppressWarnings("unchecked")
	public List<SedePrestadorServiciosDesechos> getSedesPrestadorServiciosDesechos(List<Integer> idFasesGestion,
			String ruc, int idDesecho, int idTipoEliminacion, String filter) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("idFasesGestion", idFasesGestion);
		params.put("ruc", ruc);
		params.put("idDesecho", idDesecho);
		params.put("idTipoEliminacion", idTipoEliminacion);

		if (filter == null || filter.trim().isEmpty())
			return (List<SedePrestadorServiciosDesechos>) crudServiceBean.findByNamedQuery(
					SedePrestadorServiciosDesechos.FILTRAR_POR_FASE_GESTION_DESECHO, params);
		else {
			filter = filter.toLowerCase();
			params.put("filter", "%" + filter + "%");
			return (List<SedePrestadorServiciosDesechos>) crudServiceBean.findByNamedQuery(
					SedePrestadorServiciosDesechos.FILTRAR_POR_FASE_GESTION_DESECHO_Y_FILTRO, params);
		}
	}

	@SuppressWarnings("unchecked")
	public List<PrestadorServiciosDesechoPeligroso> getPrestadoresServiciosDesechosPorFaseDesechoRucTipoUbicacion(
			List<Integer> idFasesGestion, int idDesecho, String ruc, List<Integer> idUbicaciones,
			TipoEliminacionDesecho tipoEliminacion, String filter) {

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("idFasesGestion", idFasesGestion);
		params.put("idDesecho", idDesecho);
		params.put("ruc", ruc);
		params.put("idUbicaciones", idUbicaciones);

		if (tipoEliminacion != null) {
			params.put("idTipoEliminacion", tipoEliminacion.getId());
			return (List<PrestadorServiciosDesechoPeligroso>) crudServiceBean.findByNamedQuery(
					PrestadorServiciosDesechoPeligroso.FILTRAR_POR_FASE_TIPO_DESECHO_RUC_UBICACION_TIPO_ELIMINACION,
					params);
		} else {
			return (List<PrestadorServiciosDesechoPeligroso>) crudServiceBean.findByNamedQuery(
					PrestadorServiciosDesechoPeligroso.FILTRAR_POR_FASE_TIPO_DESECHO_RUC_UBICACION, params);
		}
	}

	@SuppressWarnings("unchecked")
	public List<TipoEliminacionDesecho> buscarTipoEliminacionDesechosPadres(String filtro) {
		List<TipoEliminacionDesecho> desechos = new ArrayList<TipoEliminacionDesecho>();
		if (filtro != null && !filtro.isEmpty()) {
			filtro = filtro.toLowerCase();
			Query query = crudServiceBean
					.getEntityManager()
					.createQuery(
							"FROM TipoEliminacionDesecho t WHERE (lower(t.nombre) like :filtro OR lower(t.clave) like :filtro) ORDER BY t.id");
			query.setParameter("filtro", "%" + filtro + "%");
			desechos = query.getResultList();
		} else {
			Query query = crudServiceBean.getEntityManager().createQuery(
					"FROM TipoEliminacionDesecho t WHERE t.tipoEliminacionDesecho = null ORDER BY t.id");
			desechos = query.getResultList();
		}
		initIsTipoEliminacionFinal(desechos);
		return desechos;
	}

	private void initIsTipoEliminacionFinal(List<TipoEliminacionDesecho> tipoEliminacionDesechos) {
		if (tipoEliminacionDesechos != null)
			for (TipoEliminacionDesecho tipoEliminacionDesecho : tipoEliminacionDesechos)
				tipoEliminacionDesecho.isTipoEliminacionFinal();
	}

	@SuppressWarnings("unchecked")
	public List<TipoEliminacionDesecho> buscarTipoEliminacionDesechoPorPadre(TipoEliminacionDesecho padre) {
		List<TipoEliminacionDesecho> desechos = (List<TipoEliminacionDesecho>) crudServiceBean.getEntityManager()
				.createQuery("From TipoEliminacionDesecho t where t.tipoEliminacionDesecho =:padre order by t.id")
				.setParameter("padre", padre).getResultList();
		initIsTipoEliminacionFinal(desechos);
		return desechos;
	}

	@SuppressWarnings("unchecked")
	public List<PoliticaDesechoActividad> getPoliticasDesechoActividad(PoliticaDesecho politicaDesecho) {
		return (List<PoliticaDesechoActividad>) crudServiceBean.getEntityManager()
				.createQuery("From PoliticaDesechoActividad p where p.politicaDesecho =:politicaDesecho")
				.setParameter("politicaDesecho", politicaDesecho).getResultList();
	}

	public boolean isMedidaSeguridadOpcionOtro(TipoMedidaSeguridad medida) {
		if (medida.getId().equals(TipoMedidaSeguridad.TIPO_MEDIDA_SEGURIDAD_OTRO))
			return true;
		return false;
	}

	public void guardarListaGeneradorDesechosPeligrosos(List<GeneradorDesechosDesechoPeligroso> generadorDesechosDesechoPeligrosos) {

		this.crudServiceBean.saveOrUpdate(generadorDesechosDesechoPeligrosos);

	}

	
	//Cris F: aumento de metodos para vinculación  rgd
	public List<GeneradorDesechosPeligrosos> buscarRegistroGeneradorDesechosSinProyecto(Area area) {
		try{
			
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT g FROM GeneradorDesechosPeligrosos g where "
					+ "g.estado = true and g.finalizado = false");
			
			
			List<GeneradorDesechosPeligrosos> desechos = query.getResultList();
			if(desechos != null && !desechos.isEmpty())
				return desechos;			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}	
	
	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;
	
	public boolean actualizarRGD(String procesoRGD, String procesoAmbiental){
		
		try {
			//obtengo la id del MAE-SOL
			String sql="select * from dblink('"+Constantes.getDblinkBpmsSuiaiii()+"', "
	            	+ "'select processinstanceid "
	            	+ "from variableinstancelog where processinstanceid in ("
	            	+ "select processinstanceid from variableinstancelog where value = " 
	            	+ "''" + procesoRGD +"'' limit 1)') as (processinstanceid text)"; 
			
			Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
			
			//se lo coloca en un hashmap para poder tener acceso a la variable mediante la key processintanceid
			Map<String, Object> variablesRGD = new HashMap<String, Object>();
        	List<String> resultListRGD = (List<String>) query.getResultList();
    		if (resultListRGD.size() > 0) {
    			for (int i = 0; i < resultListRGD.size(); i++) {
    				variablesRGD.put("processinstanceid", resultListRGD.get(i));
    				break;
    			}
    		}			
			
    		//aqui obtengo todos los registros de la tabla variableinstancelog que tengan MAE-SOL en el value para modificar
			String sql_proyecto = "select * from dblink('"+Constantes.getDblinkBpmsSuiaiii()+"', "
	            	+ "'select id, value "
	            	+ "from variableinstancelog where processinstanceid in ("
	            	+ "select processinstanceid from variableinstancelog where value = " 
	            	+ "''" + procesoAmbiental +"'' and processid = ''mae-procesos.GeneradorDesechos''"
	            	+ " ) "
	            	+ "order by 1') as (id text,value text)";
			
			Query q = crudServiceBean.getEntityManager().createNativeQuery(sql_proyecto);		
    		
        	List<Object[]> resultList = (List<Object[]>) q.getResultList();
    		if (resultList.size() > 0) {
    			for (int i = 0; i < resultList.size(); i++) {
    				Object[] dataProject = (Object[]) resultList.get(i);
    				if(dataProject[1].toString().contains("MAE-SOL") || dataProject[1].toString().contains("MAAE-SOL")
    					|| dataProject[1].toString().contains("MAATE-SOL")){
    					
    					String sqlUpdate="select dblink_exec('"+Constantes.getDblinkBpmsSuiaiii()
      							 +"','update variableinstancelog set value=''"+procesoRGD+"'',"
      							 + " processinstanceid ="+ Integer.parseInt(variablesRGD.get("processinstanceid").toString())
      							 +" where id="+ Integer.parseInt(dataProject[0].toString()) +"') as result";
      					 
      					 Query queryUpdate = crudServiceBean.getEntityManager().createNativeQuery(sqlUpdate);
      			         if(queryUpdate.getResultList().size()>0)
      			        	 queryUpdate.getSingleResult();    								         
    				}else{
    					String sqlUpdate="select dblink_exec('"+Constantes.getDblinkBpmsSuiaiii()
   							 +"','update variableinstancelog set "
   							 + " processinstanceid ="+ Integer.parseInt(variablesRGD.get("processinstanceid").toString())
   							 +" where id="+ Integer.parseInt(dataProject[0].toString()) +"') as result";
   					 
   					 Query queryUpdate = crudServiceBean.getEntityManager().createNativeQuery(sqlUpdate);
   			         if(queryUpdate.getResultList().size()>0)
   			        	 queryUpdate.getSingleResult();
    				}
    			}
    			
    			//Cambiar el tramite
    			String sql_tramite = "select * from dblink('"+Constantes.getDblinkBpmsSuiaiii()+"', "
    	            	+ "'select id, value, variableid "
    	            	+ "from variableinstancelog where processinstanceid in ("
    	            	+ "select processinstanceid from variableinstancelog where value = " 
    	            	+ "''" + procesoAmbiental +"'' and processid = ''mae-procesos.GeneradorDesechos''"
    	            	+ " ) "
    	            	+ "order by 1') as (id text,value text, variableid text)";
    			
    			Query queryTramite = crudServiceBean.getEntityManager().createNativeQuery(sql_tramite);		
        		
            	List<Object[]> resultTramiteList = (List<Object[]>) queryTramite.getResultList();
        		if (resultList.size() > 0) {
        			for (int i = 0; i < resultTramiteList.size(); i++) {
        				Object[] dataProject = (Object[]) resultTramiteList.get(i);
        				if((dataProject[1].toString().contains("MAE-SOL") || dataProject[1].toString().contains("MAAE-SOL")
        				|| dataProject[1].toString().contains("MAATE-SOL") )&& dataProject[2].toString().equals("tramite")){
        					
        					String sqlUpdate="select dblink_exec('"+Constantes.getDblinkBpmsSuiaiii()
       							 +"','update variableinstancelog set value=''"+procesoAmbiental+"'',"
       							 + " processinstanceid ="+ Integer.parseInt(variablesRGD.get("processinstanceid").toString())
       							 +" where id="+ Integer.parseInt(dataProject[0].toString()) +"') as result";
       					 
       					 Query queryUpdate = crudServiceBean.getEntityManager().createNativeQuery(sqlUpdate);
       			         if(queryUpdate.getResultList().size()>0)
       			        	 queryUpdate.getSingleResult();
        				}        				
        			}
        		}
    			
    			//actualizando pren_id en la tabla de proyectos
        		ProyectoLicenciamientoAmbiental proyecto = proyectoLicenciamientoAmbientalFacade.getProyectoPorCodigo(procesoAmbiental);
        		
        		if(proyecto != null){
        			GeneradorDesechosPeligrosos registro = registroGeneradorDesechosFacade.getRGD(procesoRGD);    	
        			if(registro != null){
        				registro.setProyecto(proyecto);
        			}
        		}
        		
        		//actualizando pago
        		
        		String sql_pago = "select * from dblink('"+Constantes.getDblinkSuiaVerde()+"', "
    	            	+ "'select id_online_payment_historical "
    	            	+ "from online_payment.online_payments_historical where project_id = "
    	            	+ "''" + procesoRGD +"'' "	            	
    	            	+ "order by 1') as (id_online_payment_historical text)";
        		
        		Query queryPago = crudServiceBean.getEntityManager().createNativeQuery(sql_pago);
    			
        		//Como solo es una variable solo se pone en una lista de String
    			//se lo coloca en un hashmap para poder tener acceso a la variable mediante la key processintanceid
    			Map<String, Object> variablesPago = new HashMap<String, Object>();
            	List<String> resultListPago = (List<String>) queryPago.getResultList();
        		if (resultListPago.size() > 0) {
        			for (int i = 0; i < resultListPago.size(); i++) {
        				variablesPago.put("idPago", resultListPago.get(i));
        				break;
        			}
        		}	
        		
        		if(variablesPago.size() >0){
        			String sqlUpdatePago="select dblink_exec('"+Constantes.getDblinkSuiaVerde()
       					 +"','update online_payment.online_payments_historical set project_id=''"+procesoAmbiental
       					 +"'' where id_online_payment_historical="+ Integer.parseInt(variablesPago.get("idPago").toString()) +"') as result";
       			 
       			 Query queryUpdate = crudServiceBean.getEntityManager().createNativeQuery(sqlUpdatePago);
       	         if(queryUpdate.getResultList().size()>0)
       	        	 queryUpdate.getSingleResult();  
        		}
        		
//        		actualizar el financial_transaction - pren Id a asociar -WR
        		String sqlUpdateRGD="update suia_iii.financial_transaction set pren_id="+proyecto.getId()+" where fitr_transaction_motive_id='"+procesoRGD+"'";					 
				 Query queryUpdateRGD = crudServiceBean.getEntityManager().createNativeQuery(sqlUpdateRGD);
				 queryUpdateRGD.executeUpdate();
				 
    	         return true;
    		}		
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}
	
	public Map<String, Object> validacionRGD(String codigo){
		try {
			String sql="select * from dblink('"+Constantes.getDblinkBpmsSuiaiii()+"', "
	            	+ "'select variableid, value "
	            	+ "from variableinstancelog where processinstanceid in ("
	            	+ "select processinstanceid from variableinstancelog where value = " 
	            	+ "''" + codigo +"'' and processid = ''mae-procesos.GeneradorDesechos'' limit 1)') as (variableid text, value text)"; 
			
			Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
			
			//se lo coloca en un hashmap para poder tener acceso a la variable mediante la key processintanceid    		
			Map<String, Object> variables = new HashMap<String, Object>();
        	List<Object[]> resultList = (List<Object[]>) query.getResultList();
    		if (resultList.size() > 0) {
    			for (int i = 0; i < resultList.size(); i++) {
    				Object[] dataProject = (Object[]) resultList.get(i);
    				variables.put(dataProject[0].toString(), dataProject[1].toString());				
    			}
    		}
    		
    		if(variables.isEmpty()){
    			return null;
    		}else{
    			return variables;
    		}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Map<String, Object> buscarPago(String procesoRGD){
		try {
						
			String sql_pago = "select * from dblink('"+Constantes.getDblinkSuiaVerde()+"', "
	            	+ "'select tramit_number "
	            	+ "from online_payment.online_payments_historical where project_id = "
	            	+ "''" + procesoRGD +"'' "	            	
	            	+ "order by 1') as (tramit_number text)";
    		
    		Query queryPago = crudServiceBean.getEntityManager().createNativeQuery(sql_pago);
    		
    		Map<String, Object> variablesPago = new HashMap<String, Object>();
        	List<String> resultListPago = (List<String>) queryPago.getResultList();
    		if (resultListPago.size() > 0) {
    			for (int i = 0; i < resultListPago.size(); i++) {
    				variablesPago.put("referenciaPago", resultListPago.get(i));
    				break;
    			}
    		}
    		
    		if(variablesPago.isEmpty()){
    			return null;
    		}else{
    			return variablesPago;
    		}
    		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean verificarRGDAsociado(String codigoRGD){
		try {
			String sql="select * from dblink('"+Constantes.getDblinkBpmsSuiaiii()+"', "
	            	+ "'select variableid, value "
	            	+ "from variableinstancelog where processinstanceid in ("
	            	+ "select processinstanceid from variableinstancelog where value = " 
	            	+ "''" + codigoRGD +"'' and processid = ''mae-procesos.GeneradorDesechos'' limit 1)') as (variableid text, value text)"; 
			
			Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
			
			//se lo coloca en un hashmap para poder tener acceso a la variable mediante la key processintanceid    		
			Map<String, Object> variables = new HashMap<String, Object>();
        	List<Object[]> resultList = (List<Object[]>) query.getResultList();
    		if (resultList.size() > 0) {
    			for (int i = 0; i < resultList.size(); i++) {
    				Object[] dataProject = (Object[]) resultList.get(i);
    				if(dataProject[1].toString().contains("MAE-RA") || dataProject[1].toString().contains("MAAE-RA")
    					|| dataProject[1].toString().contains("MAATE-RA")){	
    					variables.put(dataProject[0].toString(), dataProject[1].toString());	
    				}
    			}
    		}
    		
    		if(variables.isEmpty()){
    			return false;
    		}else{
    			return true;
    		}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean desvincularRGDAsociado(String codigoRGD){
		try {
			String sql="select * from dblink('"+Constantes.getDblinkBpmsSuiaiii()+"', "
	            	+ "'select variableid, value "
	            	+ "from variableinstancelog where processinstanceid in ("
	            	+ "select processinstanceid from variableinstancelog where value = " 
	            	+ "''" + codigoRGD +"'' and processid = ''mae-procesos.GeneradorDesechos'' limit 1)') as (variableid text, value text)"; 
			
			Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
			
			//se lo coloca en un hashmap para poder tener acceso a la variable mediante la key processintanceid    		
			Map<String, Object> variables = new HashMap<String, Object>();
        	List<Object[]> resultList = (List<Object[]>) query.getResultList();
    		if (resultList.size() > 0) {
    			for (int i = 0; i < resultList.size(); i++) {
    				Object[] dataProject = (Object[]) resultList.get(i);
    				if(dataProject[1].toString().contains("MAE-RA") || dataProject[1].toString().contains("MAAE-RA")
    					|| dataProject[1].toString().contains("MAATE-RA")){	
    					variables.put(dataProject[0].toString(), dataProject[1].toString());	
    				}
    			}
    		}
    		
    		if(variables.isEmpty()){
    			return false;
    		}else{
    			return true;
    		}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public Integer contarRegistrosGeneradorDesechos(Usuario usuario,boolean admin) {
		String strAreaId = "";
		Map<String, Object> params = new HashMap<String, Object>();
		boolean gerente = (Usuario.isUserInRole(usuario, "GERENTE"));
		if(!admin && !gerente){
			
			List<AreaUsuario> lista = usuario.getListaAreaUsuario();
			String areasB = "";
			
			for(AreaUsuario au : lista){
				areasB += (areasB.equals("")) ? au.getArea().getId() :  "," + au.getArea().getId();
			}
					
			
			params.put("areaId",areasB);
//			strAreaId += " and p.area_id=:areaId ";
			strAreaId += " and p.area_id IN (:areaId) ";
		}
		String like="";
		if(gerente){
		    like +=" AND hwge_extended_responsibility=true ";
		}
		String sql="select count (*) "
				+ "from suia_iii.hazardous_wastes_generators d "
				+ "inner join areas a on(a.area_id=d.area_id) "
				+ "left join suia_iii.projects_environmental_licensing p on(p.pren_id=d.pren_id) "
				+ "where hwge_status=true" + strAreaId+ like;
				//+ "where hwge_status="+estado+" "+(area!=null?" and d.area_id="+area.getId():"")+" order by 1 desc ";

		
		List<Object> result = (List<Object>)crudServiceBean.findByNativeQuery(sql, params);
		for (Object object : result) {
			return (((BigInteger) object).intValue());
		}
		
		return 0;
		
		
		
	}
		
	public List<GeneradorDesechosPeligrosos> buscarRegistrosGeneradorDesechos(Integer inicio,Integer total, Usuario usuario,boolean admin ,
			String codigo,String proyecto,String usuarioCreacion,String responsableSiglas) {
		String strAreaId = "";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("total", total);
		params.put("inicio", inicio);
		
		boolean gerente = (Usuario.isUserInRole(usuario, "GERENTE"));
		
		if(!admin && !gerente){
			List<AreaUsuario> lista = usuario.getListaAreaUsuario();
			String areasB = "";
			
			for(AreaUsuario au : lista){
				areasB += (areasB.equals("")) ? au.getArea().getId() :  "," + au.getArea().getId();
			}			
			
			params.put("areaId",areasB);
			strAreaId += " and p.area_id IN (:areaId) ";
			
//			params.put("areaId",usuario.getIdArea());
//			strAreaId += " and a.area_id=:areaId ";
		}
		String like = "";
		
		if (!codigo.isEmpty()) {
			params.put("codigo", "%" + codigo + "%");
			like += " AND LOWER(hwge_request) like LOWER(:codigo)";
		}
		if (!proyecto.isEmpty()) {
			params.put("proyecto", "%" + proyecto + "%");
			like += " AND  LOWER(pren_code) like LOWER(:proyecto) ";
		}
		if (!usuarioCreacion.isEmpty()) {
			params.put("usuarioCreacion", "%" + usuarioCreacion + "%");
			like += " AND  LOWER(hwge_creator_user) like LOWER(:usuarioCreacion) ";
		}
		if (!responsableSiglas.isEmpty()) {
			params.put("responsableSiglas", "%" + responsableSiglas + "%");
			like += " AND  LOWER(area_abbreviation) like LOWER(:responsableSiglas) ";
		}
		
		if(gerente){
			like +=" AND hwge_extended_responsibility=true ";
		}
		
		String sql="select hwge_id,hwge_request,hwge_creator_user,hwge_creation_date,pren_code,a.area_id,area_abbreviation, "
				+"(select gelo_name from geographical_locations where gelo_id = "
				+"(select gelo_parent_id from geographical_locations  where gelo_id in (select gelo_parent_id from geographical_locations  where gelo_id in" 
				+"( select  gelo_id from suia_iii.recovery_points pl where pl.hwge_id = d.hwge_id and repo_status is true order by repo_id limit 1)) limit 1) )  provincia_proyecto "
				+ "from suia_iii.hazardous_wastes_generators d "
				+ "inner join areas a on(a.area_id=d.area_id) "
				+ "left join suia_iii.projects_environmental_licensing p on(p.pren_id=d.pren_id) "
				+ "where hwge_status=true "+strAreaId+ like +" LIMIT :total OFFSET :inicio";
		
		List<Object> result = (List<Object>)crudServiceBean.findByNativeQuery(sql, params);
		
		List<GeneradorDesechosPeligrosos> listaGeneradores =new ArrayList<GeneradorDesechosPeligrosos>();
		if(result.size()>0)
		{
			for (Object object : result) {
				Object[] obj=(Object[]) object;
				GeneradorDesechosPeligrosos rgd=new GeneradorDesechosPeligrosos();
				rgd.setId((Integer)obj[0]);
				rgd.setSolicitud((String)obj[1]);
				rgd.setUsuarioCreacion((String)obj[2]);
				rgd.setFechaCreacion((Date)obj[3]);
				rgd.setCodigo((String)obj[4]);
				rgd.setAreaAbreviacion((String)obj[6]);
				rgd.setProvincia((String)obj[7]);
				
				Area areaResponsable =new Area();
				areaResponsable.setId((Integer)obj[5]);
				areaResponsable.setAreaAbbreviation((String)obj[6]);
				rgd.setAreaResponsable(areaResponsable);
				listaGeneradores.add(rgd);
			}
		}
		return listaGeneradores;
	}
	
	@SuppressWarnings("unchecked")
	public List<PuntoRecuperacion> buscarPuntoRecuperacion(GeneradorDesechosPeligrosos generadorDesechosPeligrosos) {
		return (List<PuntoRecuperacion>) crudServiceBean.getEntityManager()
				.createQuery("From PuntoRecuperacion p where p.generadorDesechosPeligrosos.id =:hwgeId")
				.setParameter("hwgeId", generadorDesechosPeligrosos.getId()).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<GeneradorDesechosPeligrosos> buscarRegistrosGeneradorDesechos(Area area, boolean estado) {
		String sql="select hwge_id,hwge_request,hwge_creator_user,hwge_creation_date,pren_code,a.area_id,area_abbreviation "
				+ "from suia_iii.hazardous_wastes_generators d "
				+ "inner join areas a on(a.area_id=d.area_id) "
				+ "left join suia_iii.projects_environmental_licensing p on(p.pren_id=d.pren_id) "
				+ "where hwge_status="+estado+" order by 1 limit 12";
				//+ "where hwge_status="+estado+" "+(area!=null?" and d.area_id="+area.getId():"")+" order by 1 desc ";
		List<Object[]> result = (List<Object[]>)crudServiceBean.getEntityManager().createNativeQuery(sql).getResultList();
		List<GeneradorDesechosPeligrosos> listaGeneradores =new ArrayList<GeneradorDesechosPeligrosos>();
		if(result.size()>0)
		{
			for (Object[] obj : result) {
				GeneradorDesechosPeligrosos rgd=new GeneradorDesechosPeligrosos();
				rgd.setId((Integer)obj[0]);
				rgd.setSolicitud((String)obj[1]);
				rgd.setUsuarioCreacion((String)obj[2]);
				rgd.setFechaCreacion((Date)obj[3]);
				rgd.setCodigo((String)obj[4]);
				
				Area areaResponsable =new Area();
				areaResponsable.setId((Integer)obj[5]);
				areaResponsable.setAreaAbbreviation((String)obj[6]);
				rgd.setAreaResponsable(areaResponsable);
				listaGeneradores.add(rgd);
			}
		}
		return listaGeneradores;
	}
	
	@SuppressWarnings("unchecked")
	public List<SedePrestadorServiciosDesechos> getSedesPrestadorServicios(
			List<Integer> idFasesGestion) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("idFasesGestion", idFasesGestion);

		return (List<SedePrestadorServiciosDesechos>) crudServiceBean
				.findByNamedQuery(
						SedePrestadorServiciosDesechos.FILTRAR_POR_FASE_GESTION,
						params);

	}
	
	@SuppressWarnings("unchecked")
	public List<SedePrestadorServiciosDesechos> getTodasSedesPrestadorServicios() {
		Map<String, Object> params = new HashMap<String, Object>();

		return (List<SedePrestadorServiciosDesechos>) crudServiceBean
				.findByNamedQuery(
						SedePrestadorServiciosDesechos.LISTAR_TODAS_SEDES,
						params);

	}
	
	public List<Integer> getRGDIdByRequest(String solicitud){
		try {
			String sql="select hwge_id from suia_iii.hazardous_wastes_generators where hwge_request='"+solicitud+"' order by 1 desc";
			
			@SuppressWarnings("unchecked")
			List<Integer> listaGeneradores = (List<Integer>)crudServiceBean.getEntityManager().createNativeQuery(sql).getResultList();
			return listaGeneradores;
		}catch (NoResultException e) {
			return new ArrayList<Integer>();
		}
	}
	
	public GeneradorDesechosPeligrosos getUltimoPorSolicitud(String solicitud) throws Exception {
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("solicitud", solicitud);
			List<GeneradorDesechosPeligrosos> listaGeneradores = (List<GeneradorDesechosPeligrosos>)crudServiceBean.findByNamedQuery(
					GeneradorDesechosPeligrosos.GET_BY_SOLICITUD, parameters);

			GeneradorDesechosPeligrosos generador = listaGeneradores != null && !listaGeneradores.isEmpty() ? listaGeneradores.get(0) : null;

			if (generador != null) {
				if (generador.getAreaResponsable() != null) {
					generador.getAreaResponsable().getAreaAbbreviation();
				} else if (generador.getProyecto() != null){
					generador.setAreaResponsable(generador.getProyecto()
							.getAreaResponsable());
				}
				if (generador.getGeneradorDesechosDesechoPeligrosos()!=null)
					generador.getGeneradorDesechosDesechoPeligrosos().size();
			}
			return generador;
		} catch (Exception e) {
			throw new Exception("No se puede cargar el registro de generador.", e);
		}
	}

	public List<Integer> getIdsPuntosGenerador(Integer idGenerador){
		try {
			String sql="select repo_id from suia_iii.recovery_points where hwge_id="+idGenerador+" and repo_status = true order by 1 desc";
			
			@SuppressWarnings("unchecked")
			List<Integer> listaIdsPuntos = (List<Integer>)crudServiceBean.getEntityManager().createNativeQuery(sql).getResultList();
			return listaIdsPuntos;
		}catch (NoResultException e) {
			return new ArrayList<Integer>();
		}
	}
	
	public List<PuntoRecuperacion> cargarPuntosPorIdGenerador(Integer id) throws Exception {

		try {

			List<Integer> listaIdsPuntos = getIdsPuntosGenerador(id);
			List<PuntoRecuperacion> listaPuntos = new ArrayList<PuntoRecuperacion>();

			int cont = 0;
			for (Integer idPunto : listaIdsPuntos) {
				PuntoRecuperacion puntoRecuperacion = crudServiceBean.find(PuntoRecuperacion.class, idPunto);
				puntoRecuperacion.getCertificadoUsoSuelos();
				puntoRecuperacion.getCertificadoCompatibilidadUsoSuelos();
				Documento documento = puntoRecuperacion.getCertificadoUsoSuelos() != null ? puntoRecuperacion
						.getCertificadoUsoSuelos() : puntoRecuperacion.getCertificadoCompatibilidadUsoSuelos();
				if (documento != null)
					documento.setContenidoDocumento(documentosFacade.descargar(documento.getIdAlfresco()));
				for (FormaPuntoRecuperacion forma : puntoRecuperacion.getFormasPuntoRecuperacion())
					forma.getCoordenadas().size();
				puntoRecuperacion.setFormasPuntoRecuperacionActuales(new ArrayList<FormaPuntoRecuperacion>());
				puntoRecuperacion.getFormasPuntoRecuperacionActuales().addAll(
						puntoRecuperacion.getFormasPuntoRecuperacion());

				listaPuntos.add(puntoRecuperacion);
				System.out.println(cont++);
			}
			
			return listaPuntos;
		} catch (Exception e) {
			throw new Exception("No se puede cargar el registro de generador.", e);
		}
	}

	public List<PuntoRecuperacion> listarPuntosPorIdGenerador(Integer id) throws Exception {
		try {

			List<Integer> listaIdsPuntos = getIdsPuntosGenerador(id);
			List<PuntoRecuperacion> listaPuntos = new ArrayList<PuntoRecuperacion>();

			int cont = 0;
			for (Integer idPunto : listaIdsPuntos) {
				PuntoRecuperacion puntoRecuperacion = crudServiceBean.find(PuntoRecuperacion.class, idPunto);
				
				for (FormaPuntoRecuperacion forma : puntoRecuperacion.getFormasPuntoRecuperacion())
					forma.getCoordenadas().size();

				listaPuntos.add(puntoRecuperacion);
			}
			
			return listaPuntos;
		} catch (Exception e) {
			throw new Exception("No se puede cargar el registro de generador.", e);
		}
	}

	public List<GeneradorCustom> listarGeneradoresActivosNoVinculados (Integer idUsuario) {
		String queryString="select distinct on (hwge_request) hwge_request, hwge_id, hwge_code "
				+ "from suia_iii.hazardous_wastes_generators d "
				+ "where hwge_status = true "
				+ "and user_id = " + idUsuario + " "
				+ "and hwge_finalized = TRUE "
				+ "and pren_id is NULL "
				+ "and (hwge_linkage is null or hwge_linkage is false) "
				+ "group by 1, 2, 3 "
				+ "order by 1, 2 desc "; //recupera los RGDs mas actuales finalizados y que no esten vinculados

		List<Object> result = crudServiceBean.findByNativeQuery(queryString, null);

		List<GeneradorCustom> projects = new ArrayList<GeneradorCustom>();
		for (Object object : result) {
			Object[] data = (Object[]) object;
			Integer id = (Integer) data[1];
			String codigo = (String) data[0];
			String documento = (String) data[2];
			
			String queryVinculado="select hwge_id,hwge_request, hwge_code "
					+ "from suia_iii.hazardous_wastes_generators d "
					+ "where hwge_status = true "
					+ "and hwge_code =  '" + documento + "' "
					+ "and hwge_linkage is true "
					+ "order by 1"; //valida si el codigo RGD ya se encuentra vinculado, 
									//debido a que puede tener varias actualizaciones y una de ellas ya puede estar vinculada

			List<Object> resultVinculados = crudServiceBean.findByNativeQuery(queryVinculado, null);
			if(resultVinculados == null || resultVinculados.size() == 0) {
				projects.add(new GeneradorCustom(id, codigo, documento));
			}
		}

		return projects;
	}
}
