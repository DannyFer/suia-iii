package ec.gob.ambiente.suia.licenciamiento.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ClasificacionClave;
import ec.gob.ambiente.suia.domain.ClaveOperacion;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.FuenteDesechoPeligroso;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.RequisitosPreviosLicenciamiento;
import ec.gob.ambiente.suia.licenciamiento.service.RequisitosPreviosServiceBean;

@Stateless
public class RequisitosPreviosFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	RequisitosPreviosServiceBean requisitosPreviosServiceBean;

	public void guardar(RequisitosPreviosLicenciamiento requisitos, ProyectoLicenciamientoAmbiental proyecto) {
		requisitos.setProyecto(proyecto);
		crudServiceBean.saveOrUpdate(requisitos);
	}

	public List<FuenteDesechoPeligroso> obtenerFuentesDesechosPeligrososXTipo(int tipoId) throws Exception {
		return requisitosPreviosServiceBean.obtenerFuentesDesechosPeligrosos(tipoId);
	}

	public List<DesechoPeligroso> obtenerDesechosPeligrososXFuente(int fuenteDesechosId) throws Exception {
		return requisitosPreviosServiceBean.obtenerDesechosPeligrosos(fuenteDesechosId);
	}

	public List<ClaveOperacion> obtenerClavesOperacion(int clasificacionId) throws Exception {
		return requisitosPreviosServiceBean.obtenerClavesOperacion(clasificacionId);
	}

	public List<ClasificacionClave> obtenerClasificacionesClave(int numeroClave) throws Exception {
		return requisitosPreviosServiceBean.obtenerClasificacionesClave(numeroClave);
	}

}
