package ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.CoordenadaRgdCoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.FormaPuntoRecuperacionRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.PuntoRecuperacionRgdRcoa;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class PuntoRecuperacionRgdRcoaFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB
	private FormaPuntoRecuperacionRgdRcoaFacade formaPuntoRecuperacionRgdRcoaFacade;
	@EJB
	private CoordenadaRgdCoaFacade coordenadaRgdCoaFacade;

	public PuntoRecuperacionRgdRcoa findById(Integer id) {
		try {
			return (PuntoRecuperacionRgdRcoa) crudServiceBean.getEntityManager()
					.createQuery("SELECT o FROM PuntoRecuperacionRgdRcoa o where o.id = :id").setParameter("id", id)
					.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void save(PuntoRecuperacionRgdRcoa obj, Usuario usuario) {
		if (obj.getId() == null) {
			obj.setUsuarioCreacion(usuario.getNombre());
			obj.setFechaCreacion(new Date());
		} else {
			obj.setUsuarioModificacion(usuario.getNombre());
			obj.setFechaModificacion(new Date());
		}
		crudServiceBean.saveOrUpdate(obj);
	}

	public void savePuntoRecuperacion(PuntoRecuperacionRgdRcoa puntoRecuperacion, Usuario usuario) {
		try {

			if (puntoRecuperacion.getId() != null) {
				PuntoRecuperacionRgdRcoa puntoAnterior = new PuntoRecuperacionRgdRcoa();
				puntoAnterior = buscarPuntoPorId(puntoRecuperacion.getId());

				for (FormaPuntoRecuperacionRgdRcoa formaAnt : puntoAnterior.getFormasPuntoRecuperacionRgdRcoa()) {
					formaAnt.setEstado(false);
					formaPuntoRecuperacionRgdRcoaFacade.save(formaAnt, usuario);
				}
			}

			List<FormaPuntoRecuperacionRgdRcoa> listaFormas = puntoRecuperacion.getFormasPuntoRecuperacionRgdRcoa();

			save(puntoRecuperacion, usuario);

			for (FormaPuntoRecuperacionRgdRcoa forma : listaFormas) {

				List<CoordenadaRgdCoa> listaCoordenadas = forma.getCoordenadas();

				forma.setPuntoRecuperacionRgdRcoa(puntoRecuperacion);
				formaPuntoRecuperacionRgdRcoaFacade.save(forma, usuario);

				for (CoordenadaRgdCoa coordenada : listaCoordenadas) {
					coordenada.setFormasPuntoRecuperacionRgdRcoa(forma);
					coordenadaRgdCoaFacade.save(coordenada, usuario);
				}

				forma.setCoordenadas(listaCoordenadas);
			}
			puntoRecuperacion.setFormasPuntoRecuperacionRgdRcoa(listaFormas);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public List<PuntoRecuperacionRgdRcoa> buscarPorRgd(Integer id) {

		List<PuntoRecuperacionRgdRcoa> lista = new ArrayList<PuntoRecuperacionRgdRcoa>();
		try {

			lista = (List<PuntoRecuperacionRgdRcoa>) crudServiceBean.getEntityManager().createQuery(
					"SELECT o FROM PuntoRecuperacionRgdRcoa o where o.registroGeneradorDesechosRcoa.id = :id and o.estado = true")
					.setParameter("id", id).getResultList();

			for (PuntoRecuperacionRgdRcoa punto : lista) {
				List<FormaPuntoRecuperacionRgdRcoa> formas = formaPuntoRecuperacionRgdRcoaFacade
						.buscarPorPunto(punto.getId());
				punto.setFormasPuntoRecuperacionRgdRcoa(formas);

				for (FormaPuntoRecuperacionRgdRcoa forma : formas) {
					List<CoordenadaRgdCoa> coordenadas = coordenadaRgdCoaFacade.buscarPorForma(forma.getId());
					forma.setCoordenadas(coordenadas);
				}
			}

			return lista;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}

	public PuntoRecuperacionRgdRcoa buscarPuntoPorId(Integer id) {

		try {

			PuntoRecuperacionRgdRcoa punto = (PuntoRecuperacionRgdRcoa) crudServiceBean.getEntityManager()
					.createQuery("SELECT o FROM PuntoRecuperacionRgdRcoa o where o.id = :id and o.estado = true")
					.setParameter("id", id).getSingleResult();

			if (punto != null) {
				List<FormaPuntoRecuperacionRgdRcoa> formas = formaPuntoRecuperacionRgdRcoaFacade
						.buscarPorPunto(punto.getId());
				punto.setFormasPuntoRecuperacionRgdRcoa(formas);

				for (FormaPuntoRecuperacionRgdRcoa forma : formas) {
					List<CoordenadaRgdCoa> coordenadas = coordenadaRgdCoaFacade.buscarPorForma(forma.getId());
					forma.setCoordenadas(coordenadas);
				}
			}

			return punto;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void eliminarPuntoRecuperacion(List<PuntoRecuperacionRgdRcoa> puntosRecuperacion, Usuario usuario) {
		try {

			for (PuntoRecuperacionRgdRcoa puntoBdd : puntosRecuperacion) {
				puntoBdd.setEstado(false);
				save(puntoBdd, usuario);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
