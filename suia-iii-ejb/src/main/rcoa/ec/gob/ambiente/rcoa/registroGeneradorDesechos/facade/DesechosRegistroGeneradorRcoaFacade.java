package ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.DesechosRegistroGeneradorRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.DocumentosRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.PuntoGeneracionDesecho;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.PuntoGeneracionRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosRcoa;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import javax.persistence.Query;

@Stateless
public class DesechosRegistroGeneradorRcoaFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB
	private PuntoGeneracionDesechoFacade puntoGeneracionDesechoFacade;
	@EJB
	private PuntoGeneracionRgdRcoaFacade puntoGeneracionRgdRcoaFacade;
	@EJB
	private DocumentosRgdRcoaFacade documentosRgdRcoaFacade;

	public DesechosRegistroGeneradorRcoa findById(Integer id) {
		try {
			return (DesechosRegistroGeneradorRcoa) crudServiceBean.getEntityManager()
					.createQuery("SELECT o FROM DesechosRegistroGeneradorRcoa o where o.id = :id")
					.setParameter("id", id).getSingleResult();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void save(DesechosRegistroGeneradorRcoa obj, Usuario usuario) {
		try {

			List<String> listaIds = obj.getPuntoGeneracionIdList();

			if (obj.getId() == null) {
				obj.setUsuarioCreacion(usuario.getNombre());
				obj.setFechaCreacion(new Date());
			} else {
				obj.setUsuarioModificacion(usuario.getNombre());
				obj.setFechaModificacion(new Date());
			}
			crudServiceBean.saveOrUpdate(obj);

			List<PuntoGeneracionDesecho> listaBdd = puntoGeneracionDesechoFacade.buscarPorDesechoRcoa(obj.getId());

			List<PuntoGeneracionDesecho> listaEliminados = new ArrayList<PuntoGeneracionDesecho>();
			if (listaBdd != null && !listaBdd.isEmpty()) {
				for (PuntoGeneracionDesecho desechoBdd : listaBdd) {
					String id = String.valueOf(desechoBdd.getPuntoGeneracionRgdRcoa().getId());
					if (!listaIds.contains(id)) {
						listaEliminados.add(desechoBdd);
					}
				}
			}
			for (String id : listaIds) {
				List<PuntoGeneracionDesecho> listaPuntoGeneracion = puntoGeneracionDesechoFacade
						.buscarPorDesechoRcoaPuntoGeneracion(obj.getId(), Integer.valueOf(id));
				if (listaPuntoGeneracion == null || listaPuntoGeneracion.isEmpty()) {
					PuntoGeneracionRgdRcoa puntoGen = new PuntoGeneracionRgdRcoa();
					puntoGen = puntoGeneracionRgdRcoaFacade.findById(Integer.valueOf(id));
					PuntoGeneracionDesecho punto = new PuntoGeneracionDesecho();
					punto.setDesechoRegistroGeneradorRcoa(obj);
					punto.setPuntoGeneracionRgdRcoa(puntoGen);
					puntoGeneracionDesechoFacade.save(punto, usuario);
				}
			}

			if (listaEliminados != null && !listaEliminados.isEmpty()) {
				for (PuntoGeneracionDesecho punto : listaEliminados) {
					punto.setEstado(false);
					puntoGeneracionDesechoFacade.save(punto, usuario);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveDesecho(DesechosRegistroGeneradorRcoa obj, Usuario usuario) {
		try {
			if (obj.getId() == null) {
				obj.setUsuarioCreacion(usuario.getNombre());
				obj.setFechaCreacion(new Date());
			} else {
				obj.setUsuarioModificacion(usuario.getNombre());
				obj.setFechaModificacion(new Date());
			}
			crudServiceBean.saveOrUpdate(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public List<DesechosRegistroGeneradorRcoa> buscarPorRegistroGenerador(
			RegistroGeneradorDesechosRcoa registroGenerador) {

		List<DesechosRegistroGeneradorRcoa> lista = new ArrayList<DesechosRegistroGeneradorRcoa>();
		try {
			lista = (List<DesechosRegistroGeneradorRcoa>) crudServiceBean
					.getEntityManager().createQuery(
					"SELECT d FROM DesechosRegistroGeneradorRcoa d where d.registroGeneradorDesechosRcoa.id = :id and d.estado = true")
					.setParameter("id", registroGenerador.getId()).getResultList();
			for (DesechosRegistroGeneradorRcoa desecho : lista) {
				DocumentosRgdRcoa documento = documentosRgdRcoaFacade.descargarDocumentoUnicoRgd(desecho.getId(),
						RegistroGeneradorDesechosRcoa.class.toString(), TipoDocumentoSistema.RGD_NO_GENERA_DESECHOS);
				if (documento != null) {
					documento.setSubido(true);
					desecho.setDocumentoGenera(documento);
				}

				if(desecho.getGestionInterna() != null &&  desecho.getGestionInterna()){
					List<DocumentosRgdRcoa> listadocumento = documentosRgdRcoaFacade.descargarDocumentoRgd(desecho.getId(), RegistroGeneradorDesechosRcoa.class.toString(), TipoDocumentoSistema.RGD_GESTION_INTERNA);
					if(listadocumento  != null && listadocumento.size() > 0){
						desecho.setDocumentosGestion(listadocumento);
					}
				}
				List<PuntoGeneracionDesecho> listaGeneracion = puntoGeneracionDesechoFacade
						.buscarPorDesechoRcoa(desecho.getId());
				List<String> listaIds = new ArrayList<String>();
				String nombres = "";
				for (PuntoGeneracionDesecho punto : listaGeneracion) {
					listaIds.add(String.valueOf(punto.getPuntoGeneracionRgdRcoa().getId()));
					if (punto.getPuntoGeneracionRgdRcoa().getClave().equals("OT")) {
						desecho.setOtroGeneracionVer(true);
						nombres += punto.getPuntoGeneracionRgdRcoa().getClave() + "-"
								+ punto.getPuntoGeneracionRgdRcoa().getNombre() + "-" + desecho.getOtroGeneracion()
								+ "<br />";
					} else {
						desecho.setOtroGeneracionVer(false);
						nombres += punto.getPuntoGeneracionRgdRcoa().getClave() + "-"
								+ punto.getPuntoGeneracionRgdRcoa().getNombre() + "<br />";
					}
				}
				desecho.setNombresGeneracion(nombres);
				desecho.setPuntoGeneracionIdList(listaIds);
			}
			return lista;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}

	@SuppressWarnings("unchecked")
	public List<DesechosRegistroGeneradorRcoa> buscarPorRegistroGeneradorGenerados(
			RegistroGeneradorDesechosRcoa registroGenerador) {
		List<DesechosRegistroGeneradorRcoa> lista = new ArrayList<DesechosRegistroGeneradorRcoa>();
		try {
			lista = (List<DesechosRegistroGeneradorRcoa>) crudServiceBean.getEntityManager().createQuery(
					"SELECT d FROM DesechosRegistroGeneradorRcoa d where d.registroGeneradorDesechosRcoa.id = :id and d.estado = true and d.generaDesecho = false order by 1")
					.setParameter("id", registroGenerador.getId()).getResultList();
			for (DesechosRegistroGeneradorRcoa desecho : lista) {
				List<PuntoGeneracionDesecho> listaGeneracion = puntoGeneracionDesechoFacade
						.buscarPorDesechoRcoa(desecho.getId());
				List<String> listaIds = new ArrayList<String>();
				for (PuntoGeneracionDesecho punto : listaGeneracion) {
					listaIds.add(String.valueOf(punto.getPuntoGeneracionRgdRcoa().getId()));
					if (desecho.getNombreOrigen() == null || !desecho.getNombreOrigen().isEmpty()) {
						desecho.setNombreOrigen(punto.getPuntoGeneracionRgdRcoa().getNombre());
					}
					if (punto.getPuntoGeneracionRgdRcoa().getClave().equals("OT")) {
						desecho.setOtroGeneracionVer(true);
					}
				}
				desecho.setPuntoGeneracionIdList(listaIds);
			}
			return lista;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<DesechosRegistroGeneradorRcoa> obtenerDesechosporRGD(String RGD) {
		List<DesechosRegistroGeneradorRcoa> result = new ArrayList<DesechosRegistroGeneradorRcoa>();
		Query sql = crudServiceBean.getEntityManager()
				.createNativeQuery("select wwgp.* from coa_waste_generator_record.waste_generator_record_coa wgrc inner join " + 
						"coa_waste_generator_record.waste_waste_generation_points wwgp on wgrc.ware_id = wwgp.ware_id and wgrc.ware_status = true and wgrc.ware_code =:idRGD ", DesechosRegistroGeneradorRcoa.class)
				.setParameter("idRGD", RGD);
		if (sql.getResultList().size() > 0)
		{
			result = (List<DesechosRegistroGeneradorRcoa>) sql.getResultList();
		}
		else
		{
			result = null;
		}
		return result;
	}

}
