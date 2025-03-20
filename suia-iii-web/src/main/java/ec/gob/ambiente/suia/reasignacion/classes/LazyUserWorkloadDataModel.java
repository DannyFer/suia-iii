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

public class LazyUserWorkloadDataModel extends LazyDataModel<UserWorkload> {

	private static final long serialVersionUID = 8687505309399028129L;

	private CargaLaboralFacade cargaLaboralFacade = BeanLocator.getInstance(CargaLaboralFacade.class);
	private IntegracionFacade integracionFacade = BeanLocator.getInstance(IntegracionFacade.class);

	private Usuario usuarioTemplate;
	private List<UserWorkload> usuarios;

	private boolean loadExternal;

	private int totalTareas;

	public LazyUserWorkloadDataModel(Usuario usuarioTemplate) {
		this.usuarioTemplate = usuarioTemplate;
		loadExternal = Constantes.getAppIntegrationSuiaEnabled();

		usuarios = new ArrayList<UserWorkload>();

		if (loadExternal)
			usuarios.addAll(fillExternalData());
		usuarios.addAll(cargaLaboralFacade.listarUsuariosContenganRol(usuarioTemplate, null));

		totalTareas = JsfUtil.getBean(AsignacionMasivaBean.class).getTasksToAssign().size();
	}
	
	public LazyUserWorkloadDataModel(Usuario usuarioTemplate, List<String> rol) {
		this.usuarioTemplate = usuarioTemplate;
		loadExternal = Constantes.getAppIntegrationSuiaEnabled();

		usuarios = new ArrayList<UserWorkload>();

		if (loadExternal)
			usuarios.addAll(fillExternalData());
		
		usuarios.addAll(cargaLaboralFacade.listarUsuariosContenganRolP(usuarioTemplate, rol));

		totalTareas = JsfUtil.getBean(AsignacionMasivaBean.class).getTasksToAssign().size();
	}
	
	public LazyUserWorkloadDataModel(UserWorkload userWorkload) {
		usuarios = new ArrayList<UserWorkload>();
		usuarios.add(userWorkload);
		totalTareas = JsfUtil.getBean(AsignacionMasivaBean.class).getTasksToAssign().size();
	}
	
	@Override
	public Object getRowKey(UserWorkload user) {
		return user.getId();
	}

	@Override
	public List<UserWorkload> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {
		try {
			List<UserWorkload> usuariosToShow = usuarios.subList(first, usuarios.size() > first + pageSize ? first
					+ pageSize : usuarios.size());

			for (UserWorkload user : usuariosToShow) {
				if (user.isInternal()) {
					user.setTramites(cargaLaboralFacade.tareasPorUsuario(user.getUserName(), JsfUtil.getLoggedUser()
							.getNombre(), JsfUtil.getLoggedUser().getPassword(), Constantes.getDeploymentId()));
					totalTareas += user.getTramites();
				}
			}

			for (UserWorkload user : usuariosToShow) {
				if (user.isInternal())
					user.setCarga(cargaLaboralFacade.calcularPorcentaje(user.getTramites(), totalTareas));
			}

			Collections.sort(usuariosToShow, new Comparator<UserWorkload>() {

				@Override
				public int compare(UserWorkload o1, UserWorkload o2) {
					return o1.getTramites().compareTo(o2.getTramites());
				}
			});

			this.setRowCount(usuarios.size());

			return usuariosToShow;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public UserWorkload getRowData(String rowKey) {
		for (UserWorkload user : usuarios) {
			if (user.getId().equals(rowKey))
				return user;
		}
		return null;
	}

	private List<UserWorkload> fillExternalData() {
		int totalTareasExternas = 0;
		List<UserWorkload> usuariosExternos = new ArrayList<UserWorkload>();
		try {
			if (usuarioTemplate.getListaAreaUsuario().get(0).getArea().getTipoArea().getId()!=3 && usuarioTemplate.getListaAreaUsuario().get(0).getArea().getTipoArea().getId()!=2){							
			List<UsuarioCarga> usuariosSuiaII = integracionFacade.getCandidateUsersToRetasking(usuarioTemplate
					.getNombre());
			if (usuariosSuiaII != null) {
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
		}
		} catch (Exception e) {
			usuariosExternos.clear();
			e.printStackTrace();
		}
		return usuariosExternos;
	}

}
