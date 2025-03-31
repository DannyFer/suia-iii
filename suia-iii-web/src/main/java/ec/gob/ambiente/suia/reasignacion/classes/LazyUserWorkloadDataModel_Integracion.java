package ec.gob.ambiente.suia.reasignacion.classes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import ec.fugu.ambiente.consultoring.retasking.UsuarioCarga;
import ec.gob.ambiente.integracion.facade.IntegracionFacade;
import ec.gob.ambiente.suia.builders.UserWorkloadBuilder;
import ec.gob.ambiente.suia.carga.facade.CargaLaboralFacade;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.dto.UserWorkload;
import ec.gob.ambiente.suia.reasignacion.bean.AsignacionMasivaBean;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

public class LazyUserWorkloadDataModel_Integracion extends LazyDataModel<UserWorkload> {

	private static final long serialVersionUID = 8687505309399028129L;

	private CargaLaboralFacade cargaLaboralFacade = BeanLocator.getInstance(CargaLaboralFacade.class);
	private IntegracionFacade integracionFacade = BeanLocator.getInstance(IntegracionFacade.class);

	private Usuario usuarioTemplate;
	private List<UserWorkload> usuarios;

	public LazyUserWorkloadDataModel_Integracion(Usuario usuarioTemplate) {
		this.usuarioTemplate = usuarioTemplate;
	}

	@Override
	public Object getRowKey(UserWorkload user) {
		return user.getId();
	}

	@Override
	public List<UserWorkload> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {
		try {

			usuarios = new ArrayList<UserWorkload>();

			int totalTareasExternas = 0;
			int offsetSize = 0;

			List<UserWorkload> usuariosExternos = new ArrayList<UserWorkload>();
			try {
				List<UsuarioCarga> usuariosSuiaII = integracionFacade.getCandidateUsersToRetasking(usuarioTemplate
						.getNombre());
				if (usuariosSuiaII != null) {
					offsetSize = usuariosSuiaII.size();
					Collections.sort(usuariosSuiaII, new Comparator<UsuarioCarga>() {

						@Override
						public int compare(UsuarioCarga o1, UsuarioCarga o2) {
							return o1.getCarga().compareTo(o2.getCarga());
						}
					});
					for (UsuarioCarga usuarioCarga : usuariosSuiaII) {
						totalTareasExternas += usuarioCarga.getCarga();
						usuariosExternos.add(new UserWorkloadBuilder().fromSuiaII(usuarioCarga).build());
					}
					for (UserWorkload user : usuariosExternos) {
						user.setCarga(cargaLaboralFacade.calcularPorcentaje(user.getTramites(), totalTareasExternas));
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			boolean queryInternal = false;
			if (usuariosExternos.size() < first + pageSize) {
				queryInternal = true;
				first = first - usuariosExternos.size();
				first = first < 0 ? 0 : first;
			}

			int totalTareas = JsfUtil.getBean(AsignacionMasivaBean.class).getTasksToAssign().size();
			if (queryInternal) {
				usuarios = cargaLaboralFacade.listarUsuariosContenganRol(usuarioTemplate, null, pageSize, first);
				for (UserWorkload user : usuarios) {
					user.setTramites(cargaLaboralFacade.tareasPorUsuario(user.getUserName(), JsfUtil.getLoggedUser()
							.getNombre(), JsfUtil.getLoggedUser().getPassword(), Constantes.getDeploymentId()));
					totalTareas += user.getTramites();
				}

				for (UserWorkload user : usuarios) {
					user.setCarga(cargaLaboralFacade.calcularPorcentaje(user.getTramites(), totalTareas));
				}

				Collections.sort(usuarios, new Comparator<UserWorkload>() {

					@Override
					public int compare(UserWorkload o1, UserWorkload o2) {
						return o1.getTramites().compareTo(o2.getTramites());
					}
				});
			} else {
				if (usuariosExternos.size() >= first + pageSize)
					usuariosExternos = usuariosExternos.subList(first, first + pageSize);
			}

			this.setRowCount(cargaLaboralFacade.listarUsuariosContenganRolCount(usuarioTemplate, null) + offsetSize);

			usuarios.addAll(0, usuariosExternos);

			return usuarios;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return usuarios;
	}

	@Override
	public UserWorkload getRowData(String rowKey) {
		for (UserWorkload user : usuarios) {
			if (user.getId().equals(rowKey))
				return user;
		}
		return null;
	}

}
