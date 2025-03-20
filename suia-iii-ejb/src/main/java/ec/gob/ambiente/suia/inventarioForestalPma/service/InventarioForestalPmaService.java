package ec.gob.ambiente.suia.inventarioForestalPma.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.InventarioForestalPma;
import ec.gob.ambiente.suia.exceptions.ServiceException;

@Stateless
public class InventarioForestalPmaService {
	@EJB
	private CrudServiceBean crudServiceBean;

	public InventarioForestalPma obtenerInventarioForestalPma(Integer idFicha) throws ServiceException {
		List<InventarioForestalPma> lista = null;
		try {
			lista = crudServiceBean.getEntityManager()
					.createQuery("From InventarioForestalPma m where m.fichaAmbientalPma.id=:idFicha and m.idRegistroOriginal = null")
					.setParameter("idFicha", idFicha).getResultList();
			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				InventarioForestalPma inventarioForestalPma = lista.get(0);
				return inventarioForestalPma;
			}
		} catch (Exception e) {
			throw new ServiceException("Ocurrió un problema al recuperar los datos");
		}
	}

    public InventarioForestalPma obtenerInventarioForestalPmaMineria(Integer idFicha) throws ServiceException {
        List<InventarioForestalPma> lista = null;
        try {
            lista = crudServiceBean.getEntityManager()
                    .createQuery("From InventarioForestalPma m where m.fichaAmbientalMineria.id=:idFicha")
                    .setParameter("idFicha", idFicha).getResultList();
            if (lista == null || lista.isEmpty()) {
                return null;
            } else {
                InventarioForestalPma inventarioForestalPma = lista.get(0);
                return inventarioForestalPma;
            }
        } catch (Exception e) {
            throw new ServiceException("Ocurrió un problema al recuperar los datos");
        }
    }
    
    public InventarioForestalPma obtenerInventarioForestalPmaProyecto(Integer idProyecto) throws ServiceException {
        List<InventarioForestalPma> lista = null;
        try {
            lista = crudServiceBean.getEntityManager()
                    .createQuery("From InventarioForestalPma m where m.proyectoLicenciamientoAmbiental.id=:idProyecto")
                    .setParameter("idProyecto", idProyecto).getResultList();
            if (lista == null || lista.isEmpty()) {
                return null;
            } else {
                InventarioForestalPma inventarioForestalPma = lista.get(0);
                return inventarioForestalPma;
            }
        } catch (Exception e) {
            throw new ServiceException("Ocurrió un problema al recuperar los datos");
        }
    }
    
    public List<InventarioForestalPma> obtenerAllInventarioForestalPma(Integer idFicha) throws ServiceException {
		List<InventarioForestalPma> lista = null;
		try {
			lista = crudServiceBean.getEntityManager()
					.createQuery("From InventarioForestalPma m where m.fichaAmbientalPma.id=:idFicha order by id desc")
					.setParameter("idFicha", idFicha).getResultList();
			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista;
			}
		} catch (Exception e) {
			throw new ServiceException("Ocurrió un problema al recuperar los datos");
		}
	}
    
    public List<InventarioForestalPma> obtenerAllInventarioForestalPmaMineria(Integer idFicha) throws ServiceException {
        List<InventarioForestalPma> lista = null;
        try {
            lista = crudServiceBean.getEntityManager()
                    .createQuery("From InventarioForestalPma m where m.fichaAmbientalMineria.id=:idFicha order by id desc")
                    .setParameter("idFicha", idFicha).getResultList();
            if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista;
			}
        } catch (Exception e) {
            throw new ServiceException("Ocurrió un problema al recuperar los datos");
        }
    }
}
