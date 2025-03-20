/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.prevencion.registrogeneradordesechos.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.AlmacenGeneradorDesechoPeligroso;
import ec.gob.ambiente.suia.domain.AlmacenGeneradorDesechos;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.GeneradorDesechosDesechoPeligroso;
import ec.gob.ambiente.suia.domain.GeneradorDesechosPeligrosos;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PuntoRecuperacion;
import ec.gob.ambiente.suia.domain.RegistroGeneradorDesechosAsociado;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.persona.facade.PersonaFacade;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.utils.ConexionBpms;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 *
 * @author carlos.pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: carlos.pupo, Fecha: 11/06/2015]
 *          </p>
 */
@ManagedBean
@SessionScoped
public class VerRegistroGeneradorDesechoBean implements Serializable {

	private static final long serialVersionUID = 5121749090148133118L;

	private static final Logger LOG = Logger.getLogger(VerRegistroGeneradorDesechoBean.class);

	public static final String URL_VER_GENERADOR_DESECHOS = "/prevencion/registrogeneradordesechos/registroGeneradorDesechosVer.jsf";

	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;

	@EJB
	private OrganizacionFacade organizacionFacade;

	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private ConexionBpms conexionBpms ;

//	@Getter
	@Setter
	private GeneradorDesechosPeligrosos generadorDesechosPeligrosos;

	@Getter
	@Setter
	private boolean habilitarObservaciones;

	@Getter
	@Setter
	private boolean observacionesSoloLectura;

	@Getter
	@Setter
	private boolean mostrarComoTarea;

	@Getter
	private Organizacion organizacion;

	@Getter
	private UbicacionesGeografica ubicacionesGeografica;

	@Getter
	private String direccion;

	@Getter
	private String telefono;

	@Getter
	private List<DesechoPeligroso> desechosPeligrosos;

	@Getter
	private List<AlmacenGeneradorDesechos> almacenesGeneradorDesechos;
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private DocumentosFacade documentosFacade;
	
	
	@Getter
	@Setter
	private RegistroGeneradorDesechosAsociado registroGeneradorDesechosAsociado;
	
	@Getter
	@Setter
	private Documento documentoLicencia;

	private UploadedFile file;
	
	
	public void cargarDesechoAsociado(){
	}
	
	public String actionVerGeneradorDesechosPeligrosos(Integer id,	boolean habilitarObservaciones, boolean observacionesSoloLectura,	boolean mostrarComoTarea) {

		try {
			if (!isTecnicoAutenticado()) {
				this.observacionesSoloLectura = true;
				return this.actionVerGeneradorDesechosPeligrosos(registroGeneradorDesechosFacade.cargarGeneradorFullPorId(id, true), true, true,false);
			} else {
				this.observacionesSoloLectura = false;
				return this.actionVerGeneradorDesechosPeligrosos(registroGeneradorDesechosFacade.cargarGeneradorFullPorId(id, false),	habilitarObservaciones, observacionesSoloLectura,mostrarComoTarea);
			}
		} catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			LOG.error("Error al cargar el registro de generador de desechos", e);
			return "";
		}
	}

	public String actionVerGeneradorDesechosPeligrosos(GeneradorDesechosPeligrosos generador,
													   boolean habilitarObservaciones, boolean observacionesSoloLectura, boolean mostrarComoTarea) {
		this.executeView(generador, habilitarObservaciones, observacionesSoloLectura, mostrarComoTarea, false);
		cargarDesechoPermisoAmbiental(generador.getSolicitud());
		return JsfUtil.actionNavigateTo(URL_VER_GENERADOR_DESECHOS);
	}

	public void redirectVerGeneradorDesechosPeligrosos(Integer id, boolean habilitarObservaciones,
													   boolean observacionesSoloLectura, boolean mostrarComoTarea) {
		try {
			this.redirectVerGeneradorDesechosPeligrosos(registroGeneradorDesechosFacade.cargarGeneradorFullPorId(id, true),
					habilitarObservaciones, observacionesSoloLectura, mostrarComoTarea);
		} catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			LOG.error("Error al cargar el registro de generador de desechos", e);
		}
	}

	public void redirectVerGeneradorDesechosPeligrosos(GeneradorDesechosPeligrosos generador,
													   boolean habilitarObservaciones, boolean observacionesSoloLectura, boolean mostrarComoTarea) {
		this.executeView(generador, habilitarObservaciones, observacionesSoloLectura, mostrarComoTarea, true);
	}

	public void executeView(GeneradorDesechosPeligrosos generador, boolean habilitarObservaciones,
							boolean observacionesSoloLectura, boolean mostrarComoTarea, boolean redirect) {
		this.generadorDesechosPeligrosos = generador;
		this.habilitarObservaciones = habilitarObservaciones;
		this.observacionesSoloLectura = observacionesSoloLectura;
		this.mostrarComoTarea = mostrarComoTarea;
		this.desechosPeligrosos = new ArrayList<DesechoPeligroso>();
		this.almacenesGeneradorDesechos = new ArrayList<AlmacenGeneradorDesechos>();

		for (GeneradorDesechosDesechoPeligroso desecho : generador.getGeneradorDesechosDesechoPeligrosos()) {
			this.desechosPeligrosos.add(desecho.getDesechoPeligroso());
			for (AlmacenGeneradorDesechoPeligroso almacen : desecho.getAlmacenGeneradorDesechoPeligrosos()) {
				if(almacen.getAlmacenGeneradorDesechos().getEstado()) {
					if (!this.almacenesGeneradorDesechos.contains(almacen.getAlmacenGeneradorDesechos()))
						this.almacenesGeneradorDesechos.add(almacen.getAlmacenGeneradorDesechos());
						almacenesGeneradorDesechos.size();
						almacenesGeneradorDesechos.get(0).getId();
				}
			}
		}

		cargarDatosUsuario();

		if (redirect)
			JsfUtil.redirectTo(URL_VER_GENERADOR_DESECHOS);
	}

	public String getClassName() {
		return GeneradorDesechosPeligrosos.class.getSimpleName();
	}

	public void cargarDatosUsuario() {
		try {
			organizacion = organizacionFacade.buscarPorPersona(getGeneradorDesechosPeligrosos().getUsuario()
					.getPersona());
			ubicacionesGeografica= null;
			if (organizacion != null) {
				if(!organizacion.getRuc().equals(getGeneradorDesechosPeligrosos().getUsuario().getNombre())){
					organizacion=organizacionFacade.buscarPorPersona(getGeneradorDesechosPeligrosos().getUsuario()
						.getPersona(), getGeneradorDesechosPeligrosos().getUsuario().getNombre());
				}
				try {
					ubicacionesGeografica = ubicacionGeograficaFacade.buscarPorId(organizacion.getIdUbicacionGeografica());	
				} catch (Exception e) {
				}
				if(ubicacionesGeografica!=null){
					try {
						direccion = organizacion.obtenerContactoPorId(2).getValor();
						telefono = organizacion.obtenerContactoPorId(6).getValor();
					} catch (Exception e) {
					}
				}else {
					ubicacionesGeografica = ubicacionGeograficaFacade.buscarPorId(JsfUtil.getLoggedUser().getPersona()
							.getIdUbicacionGeografica());
					try {
						direccion = getGeneradorDesechosPeligrosos().getUsuario().getPersona().obtenerContactoPorId(2)
								.getValor();
						telefono = getGeneradorDesechosPeligrosos().getUsuario().getPersona().obtenerContactoPorId(6)
								.getValor();
					}catch (Exception e) {
					}
				}
			} else {
				if (JsfUtil.getLoggedUser().getId().equals(getGeneradorDesechosPeligrosos().getUsuario().getId())){
				ubicacionesGeografica = ubicacionGeograficaFacade.buscarPorId(JsfUtil.getLoggedUser().getPersona()
						.getIdUbicacionGeografica());
				}else{
				ubicacionesGeografica = ubicacionGeograficaFacade.buscarPorId(getGeneradorDesechosPeligrosos()
						.getUsuario().getPersona().getUbicacionesGeografica().getId());
				}
				try {
					direccion = getGeneradorDesechosPeligrosos().getUsuario().getPersona().obtenerContactoPorId(2)
							.getValor();
					telefono = getGeneradorDesechosPeligrosos().getUsuario().getPersona().obtenerContactoPorId(6)
							.getValor();
				} catch (Exception e) {
				}
			}
		} catch (ServiceException exception) {
		}
	}

	public boolean isTecnicoAutenticado() {
		boolean isTecnico = false;
		boolean isTecnicoActivo = false;
		
		if (JsfUtil.getCurrentTask().getVariable("tecnico") != null) {
			String pinTecnico = JsfUtil.getCurrentTask().getVariable("tecnico").toString();
			if (pinTecnico != null && JsfUtil.getLoggedUser().getPin() != null && JsfUtil.getLoggedUser().getPin().equals(pinTecnico))
				isTecnico = true;
			else			
				isTecnicoActivo=conexionBpms.verificarroltecnicoRGD(pinTecnico);			
		}
		
		if(!isTecnico && !isTecnicoActivo && conexionBpms.verificarroltecnicoRGD(JsfUtil.getLoggedUser().getPin()))
		{
			Map<String, Object> params = new ConcurrentHashMap<String, Object>();
			params.put("tecnico", JsfUtil.getLoggedUser().getPin());
	    	try {
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), JsfUtil.getCurrentTask().getProcessInstanceId(), params);
				isTecnico = true;
			} catch (JbpmException e) {
				e.printStackTrace();
			}
		}		
		
		return isTecnico;
	}
	
	public void cargarDesechoPermisoAmbiental(String codigoDesecho)
    {
        registroGeneradorDesechosAsociado = new RegistroGeneradorDesechosAsociado();
        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put("codigoDesecho", codigoDesecho);
        List<RegistroGeneradorDesechosAsociado> listaDesechos = crudServiceBean.findByNamedQueryGeneric(RegistroGeneradorDesechosAsociado.LISTAR_POR_CODIGO_DESECHO, parametros);
        if(listaDesechos.size() > 0)
            registroGeneradorDesechosAsociado = (RegistroGeneradorDesechosAsociado)listaDesechos.get(0);
    }

    public void descargarDocumentoPermisoAmbiental(RegistroGeneradorDesechosAsociado desechosAsociado)
        throws CmisAlfrescoException
    {
        byte archivo[] = documentosFacade.descargar(desechosAsociado.getIdAlfresco(), desechosAsociado.getFechaCreacion());
        UtilDocumento.descargarFile(archivo, registroGeneradorDesechosAsociado.getNombreDocumento());
    }

    public void handleFileUpload(FileUploadEvent event)
    {
        file = event.getFile();
        registroGeneradorDesechosAsociado.setNombreDocumento(file.getFileName());
        setDocumentoLicencia(UtilDocumento.generateDocumentPDFFromUpload(file.getContents(), file.getFileName()));
    }

    public GeneradorDesechosPeligrosos getGeneradorDesechosPeligrosos()
    {
        return generadorDesechosPeligrosos;
    }
    
    public void cargarInfoRgd() {
    	try {
    		if(this.generadorDesechosPeligrosos != null 
    				&& this.generadorDesechosPeligrosos.getPuntosRecuperacionActuales() == null) {
	    		List<PuntoRecuperacion> listaPuntos = registroGeneradorDesechosFacade.cargarPuntosPorIdGenerador(this.generadorDesechosPeligrosos.getId());
		    	
	    		this.generadorDesechosPeligrosos.setPuntosRecuperacionActuales(new ArrayList<PuntoRecuperacion>());
	    		this.generadorDesechosPeligrosos.getPuntosRecuperacionActuales().addAll(listaPuntos);
	    		this.generadorDesechosPeligrosos.setPuntosRecuperacion(new ArrayList<PuntoRecuperacion>());
	    		this.generadorDesechosPeligrosos.getPuntosRecuperacion().addAll(listaPuntos);
    		}
    	} catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			LOG.error("Error al cargar el registro de generador de desechos", e);
		}
    }

}
