package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.NumeroPlanGestionIntegral;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;

@Stateless
public class NumeroPlanGestionIntegralFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB
	private UsuarioFacade usuarioFacade;
	
	public NumeroPlanGestionIntegral guardar(NumeroPlanGestionIntegral mumeroPgi){
		return crudServiceBean.saveOrUpdate(mumeroPgi);
	}
	
	@SuppressWarnings("unchecked")
	public List<NumeroPlanGestionIntegral> listaNumeroPlan() {
		List<NumeroPlanGestionIntegral> lista = new ArrayList<>();

		lista = (ArrayList<NumeroPlanGestionIntegral>) crudServiceBean
				.findByNamedQuery(NumeroPlanGestionIntegral.GET_NUMEROS_PGI, null);

		for (NumeroPlanGestionIntegral pgi : lista) {
			List<String> infoOperador = usuarioFacade.recuperarNombreOperador(pgi.getOperador());
			pgi.setNombreOperador(infoOperador.get(0));
		}

		return lista;
	}

	@SuppressWarnings("unchecked")
	public List<NumeroPlanGestionIntegral> listaOperadoresSga() {
		List<NumeroPlanGestionIntegral> listaPlanes = new ArrayList<>();

		String queryString = "SELECT DISTINCT user_name "
				+ " FROM chemical_pesticides.pesticide_project p "
				+ " inner join public.users u on u.user_name = p.chpe_creator_user "
				+ " left join chemical_pesticides.integrated_management_plan r on r.user_id = u.user_id "
				+ " where p.chpe_status = true and u.user_status = true and inmp_id is null";

		Query query = crudServiceBean.getEntityManager().createNativeQuery(
				queryString);

		List<String> listaUsuarios = query.getResultList();

		if (listaUsuarios != null && !listaUsuarios.isEmpty()) {
			for (String cedulaOperador : listaUsuarios) {
				Usuario operador = usuarioFacade.buscarUsuario(cedulaOperador.toString());
				if (operador != null && operador.getId() != null) {

					List<String> infoOperador = usuarioFacade.recuperarNombreOperador(operador);

					NumeroPlanGestionIntegral numeroPgi = new NumeroPlanGestionIntegral();
					numeroPgi.setOperador(operador);
					numeroPgi.setEsOperadorSga(true);
					numeroPgi.setNombreOperador(infoOperador.get(0));

					listaPlanes.add(numeroPgi);
				}
			}
		}

		return listaPlanes;
	}
	
}
