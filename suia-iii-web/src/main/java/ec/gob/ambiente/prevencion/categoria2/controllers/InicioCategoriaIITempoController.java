package ec.gob.ambiente.prevencion.categoria2.controllers;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.prevencion.registro.proyecto.bean.VerProyectoBean;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.CategoriaIIFacade;

@RequestScoped
@ManagedBean
public class InicioCategoriaIITempoController implements Serializable {
	private static final long serialVersionUID = -357779968891L;
	
	@EJB
	private CategoriaIIFacade categoriaIIFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{verProyectoBean}")
	private VerProyectoBean verProyectoBean;

	public void iniciarProceso() {
//		try {
//			categoriaIIFacade.inicarProcesoCategoriaII(loginBean.getNombreUsuario(), loginBean.getPassword(),
//					13799, "PROY");
//		} catch (JbpmException e) {
//			LOGGER.error("Error al iniciar el proceso de Categoria II", e);
//			JsfUtil.addMessageError("Ocurrió un error al intentar iniciar el proceso categoría II");
//		}		

	}
}
