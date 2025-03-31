/*
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.comun.bean;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.suia.comun.base.ComunBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.EliminacionDesechoFacade;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.dto.ProyectoCustom;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: 23/12/2014]
 *          </p>
 */
@ManagedBean
@SessionScoped
public class IdentificarProyectoComunBean extends ComunBean {

	private static final long serialVersionUID = -1497754436497689766L;

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	private List<ProyectoCustom> proyectos;

	private ProyectosQueryResolver proyectosQueryResolver;

	@EJB
	ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade;

	@EJB
	private EliminacionDesechoFacade eliminacionDesechoFacade;

	@Getter
	@Setter
	private ProyectoCustom proyecto;

	public void initFunction(String nextURL, CompleteOperation completeOperation,
			ProyectosQueryResolver proyectosQueryResolver) {
		super.initFunction(nextURL, completeOperation);
		this.proyectosQueryResolver = proyectosQueryResolver;
		this.previousURL = JsfUtil.getRelativeCurrentPage();
	}

	public void initFunction(String nextURL, boolean needNavigateToFunction, CompleteOperation completeOperation,
			ProyectosQueryResolver proyectosQueryResolver) {
		super.initFunction(nextURL, needNavigateToFunction, completeOperation);
		this.proyectosQueryResolver = proyectosQueryResolver;
	}

	@Override
	public String getFunctionURL() {
		return "/comun/identificarProyecto.jsf";
	}

	@Override
	public void cleanData() {
		proyecto = null;
		proyectos = null;
		proyectosQueryResolver = null;
		previousURL = null;
	}

	@Override
	public boolean executeBusinessLogic(Object object) {
		proyecto = (ProyectoCustom) object;
		Object result = executeOperation(proyecto);
		if (result != null)
			if (result instanceof Boolean && !(Boolean) result)
				return false;
		return true;
	}

	public List<ProyectoCustom> getProyectos() {
		if (proyectos == null) {
			if (proyectosQueryResolver != null && proyectosQueryResolver.getProyectos() != null)
				proyectos = proyectosQueryResolver.getProyectos();
			else
				proyectos = proyectoLicenciamientoAmbientalFacade.getAllProjectsByUser(JsfUtil.getLoggedUser());
			if (proyectosQueryResolver != null && proyectos != null) {
				String tiposSector = "";
				String categorias = "";
				if (proyectosQueryResolver.getTiposSector() != null)
					for (String value : proyectosQueryResolver.getTiposSector()) {
						tiposSector += value + ";;";
					}
				if (proyectosQueryResolver.getCategorias() != null)
					for (String value : proyectosQueryResolver.getCategorias()) {
						categorias += value + ";;";
					}
				if (!tiposSector.isEmpty() || !categorias.isEmpty()) {
					List<ProyectoCustom> proyectosFiltrados = new ArrayList<>();
					for (ProyectoCustom proyectoCustom : proyectos) {
						if (!tiposSector.isEmpty()) {
							if (!tiposSector.toLowerCase().contains(proyectoCustom.getSector().toLowerCase()))
								continue;
						}
						if (!categorias.isEmpty())
							if (!categorias.toLowerCase()
									.contains(proyectoCustom.getCategoriaNombrePublico().toLowerCase()))
								continue;
						proyectosFiltrados.add(proyectoCustom);
					}
					proyectos.clear();
					proyectos.addAll(proyectosFiltrados);
				}
			}
		}
		return proyectos;
	}

	/***
	 * Método que realiza las validaciones pertinentes y, como resultado, permite o no
	 * al usuario iniciar un nuevo RGD.
	 * @Autor Denis Linares
	 * @param proyecto
	 */
	public String validarProyecto(ProyectoCustom proyecto){

		String id = proyecto.getId();
		if(StringUtils.isNumeric(id)){//Validamos si es un proyecto del SUIA-III. NOTA: El WS devuelve los ids de los proyectos del SUIA-Verde como cadenas alfanuméricas.

			boolean tienePermiso = true;

			if(proyecto.getCategoriaNombrePublico().compareTo("Registro Ambiental") == 0 || proyecto.getCategoriaNombrePublico().compareTo("Licencia Ambiental") == 0){
				try {
					//Validamos que tenga permiso ambiental (Licencia o Registro).
					tienePermiso = this.proyectoLicenciaAmbientalFacade.validarPermisoPorIdProyecto(Integer.parseInt(id), proyecto.getCategoriaNombrePublico(), proyecto.getSector());

					ProyectoLicenciamientoAmbiental proy = this.proyectoLicenciaAmbientalFacade.getProyectoPorId(Integer.parseInt(id));

					if (tienePermiso){
						if(proy.getGeneraDesechos()){

							//Validamos que no tenga un generador de desechos en curso asociado.
							//boolean existeGeneradorEnCurso = eliminacionDesechoFacade.proyectoTieneGeneradorEnCurso(Integer.parseInt(id));
							boolean existeGeneradorEnCurso = proyectoLicenciaAmbientalFacade.tienenRgdEnCurso(Integer.parseInt(id));
							if(existeGeneradorEnCurso){
								//Mostramos un msj que direcciona al menú  de procesos.
								RequestContext.getCurrentInstance().execute(
										"PF('conGeneradorWdgt').show();");
								return "";
							}
						}
							return executeOperationAction(proyecto);

					}
					else{
						//Mostramos un Msj informatativo
						RequestContext.getCurrentInstance().execute(
								"PF('sinPermisoWdgt').show();");
						return "";
					}

				} catch (ServiceException e) {
					e.printStackTrace();
				}
			}
			else{
				return executeOperationAction(proyecto);
			}

		}
		else{
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_PROYECTO_AJENO);
		}

		return "";
	}

	public String redirectTo(String url){
		return JsfUtil.actionNavigateTo(url);
	}

	public interface ProyectosQueryResolver {

		List<ProyectoCustom> getProyectos();

		String[] getTiposSector();

		String[] getCategorias();
	}
}
