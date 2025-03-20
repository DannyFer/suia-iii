package ec.gob.ambiente.suia.facilitador.facade;

import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.facilitador.service.FacilitadorServiceBean;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.webservice.facade.AsignarTareaFacade;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.ArrayList;
import java.util.List;

/**
 * *
 * <p/>
 * <b> Facade para las operaciones con los Facilitadores</b>
 *
 * @author frank torres rodriguez
 * @version Revision: 1.0
 *          <p>
 *          [Autor: frank torres rodriguez, Fecha: 09/03/2015]
 *          </p>
 */
@Stateless
public class FacilitadorFacade {

    /**
     * <b> Lista los facilitadores registrados. </b>
     *
     * @return
     * @author frank torres rodriguez
     * @version Revision: 1.0
     * <p>
     * [Autor: frank torres rodriguez, Fecha: 09/03/2015]
     * </p>
     * NO SE UTILIZA ESTE METODO
     */
    public List<Usuario> listarFacilitadores() {

        FacilitadorServiceBean facilitadorServiceBean = (FacilitadorServiceBean) BeanLocator.getInstance(FacilitadorServiceBean.class);
        return facilitadorServiceBean.listarFacilitadores();
    }

    /**
     * Lista los facilitadores que no aparecen en la lista a excluir
     *
     * @param excluir lista de usuarios que no se desean incorporar
     * @return
     */
    public List<Usuario> buscarFacilitadoresNoExcluidos(List<Usuario> excluir) {
        FacilitadorServiceBean facilitadorServiceBean = (FacilitadorServiceBean) BeanLocator.getInstance(FacilitadorServiceBean.class);
        return facilitadorServiceBean.buscarFacilitadores(excluir);
    }
    
    public List<Usuario> buscarFacilitadoresNoExcluidos(List<Usuario> excluir,String nombreArea) {
        FacilitadorServiceBean facilitadorServiceBean = (FacilitadorServiceBean) BeanLocator.getInstance(FacilitadorServiceBean.class);     
        return facilitadorServiceBean.buscarFacilitadores(excluir,nombreArea);
    }

    /**
     * Caga laboral del listado de usuario ordenado
     *
     * @param usuarios
     * @return
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Usuario> cargaLaboralFacilitadores(List<Usuario> usuarios) {
        AsignarTareaFacade asignarTareaFacade = (AsignarTareaFacade) BeanLocator.getInstance(AsignarTareaFacade.class);
        return asignarTareaFacade.obtenerCargaPorUsuariosV2(usuarios);
    }

    /**
     * Carga laboral de un usuario
     *
     * @param usuario
     * @return
     */
    public Usuario cargaLaboralFacilitador(Usuario usuario) {
        usuario.setCarga(Float.parseFloat(Integer.toString(usuario.getNombre()
                .length())));
        return usuario;
    }

    /**
     * Obtiene el listado de facilitadores con menos carga
     *
     * @param limite              límite de facilitadores a mostrar
     * @param usuariosConfirmados
     * @param usuariosDescartados
     * @return
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Usuario> seleccionarFacilitadores(Integer limite,
                                                  List<Usuario> usuariosConfirmados, List<Usuario> usuariosDescartados) {
        List<Usuario> usuariosExcluir = new ArrayList<Usuario>();
        // formar lista con todos los usuarios que no forman parte de la
        // selección
        usuariosExcluir.addAll(usuariosConfirmados);
        usuariosExcluir.addAll(usuariosDescartados);
        List<Usuario> usuarios = seleccionarFacilitadores(limite,
                usuariosExcluir);
        if (usuarios.size() >= limite) {
            return usuarios;
        } else {// si no se cumple con el tamaño deseado se incluyen los ya
            // rechazados.
            usuariosExcluir = new ArrayList<Usuario>();
            usuariosExcluir.addAll(usuariosConfirmados);
            usuariosExcluir.addAll(usuarios);

            List<Usuario> usuarios_completado = seleccionarFacilitadores(limite
                    - usuarios.size(), usuariosExcluir);
            usuarios.addAll(usuarios_completado);
            return usuarios;
            // throw new Exception("No hay suficientes facilitadores.");
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Usuario> seleccionarFacilitadores(Integer limite,
                                                  List<Usuario> usuariosConfirmados, List<Usuario> usuariosDescartados,String nombreArea) {
    	//,String nombreArea
        List<Usuario> usuariosExcluir = new ArrayList<Usuario>();
        // formar lista con todos los usuarios que no forman parte de la 
        // selección
        usuariosExcluir.addAll(usuariosConfirmados);
        usuariosExcluir.addAll(usuariosDescartados);
        List<Usuario> usuarios = seleccionarFacilitadores(limite,usuariosExcluir,nombreArea);
        //,nombreArea
        if (usuarios.size() >= limite) {
            return usuarios;
        } else {// si no se cumple con el tamaño deseado se incluyen los ya
            // rechazados.
            usuariosExcluir = new ArrayList<Usuario>();
            usuariosExcluir.addAll(usuariosConfirmados);
            usuariosExcluir.addAll(usuarios);

            List<Usuario> usuarios_completado = seleccionarFacilitadores(limite- usuarios.size(), usuariosExcluir);
            usuarios.addAll(usuarios_completado);
            return usuarios;
            // throw new Exception("No hay suficientes facilitadores.");
        }
    }

    /**
     * Obtiene el listado de facilitadores con menos carga
     *
     * @param limite          límite de facilitadores a mostrar
     * @param usuariosExcluir
     * @return
     */
    private List<Usuario> seleccionarFacilitadores(Integer limite,
                                                   List<Usuario> usuariosExcluir) {

        List<Usuario> usuarios = buscarFacilitadoresNoExcluidos(usuariosExcluir);
        if (usuarios.size() >= limite) {
            usuarios = cargaLaboralFacilitadores(usuarios);
            return usuarios.subList(0, limite);
        } else {
            return cargaLaboralFacilitadores(usuarios);
        }
    }
    
    private List<Usuario> seleccionarFacilitadores(Integer limite,
			List<Usuario> usuariosExcluir,String nombreArea) {
		List<Usuario> usuarios = buscarFacilitadoresNoExcluidos(usuariosExcluir,nombreArea);
		if (usuarios.size() >= limite) {
			usuarios = cargaLaboralFacilitadores(usuarios);
			return usuarios.subList(0, limite);
		} else {
			return cargaLaboralFacilitadores(usuarios);
		}
	}
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Usuario> seleccionarFacilitadoresBypass(Integer limite,
                                                  List<Usuario> usuariosConfirmados, List<Usuario> usuariosDescartados,String nombreArea) {
        List<Usuario> usuariosExcluir = new ArrayList<Usuario>();
        // formar lista con todos los usuarios que no forman parte de la 
        // selección
        usuariosExcluir.addAll(usuariosConfirmados);
        usuariosExcluir.addAll(usuariosDescartados);
        List<Usuario> usuarios = seleccionarFacilitadoresBypass(limite,usuariosExcluir);
        //,nombreArea
        if (usuarios.size() >= limite) {
            return usuarios;
        } else {
        	// si no se cumple con el tamaño deseado se incluyen los ya rechazados.
            usuariosExcluir = new ArrayList<Usuario>();
            usuariosExcluir.addAll(usuariosConfirmados);
            usuariosExcluir.addAll(usuarios);

            List<Usuario> usuarios_completado = seleccionarFacilitadoresBypass(limite- usuarios.size(), usuariosExcluir);
            usuarios.addAll(usuarios_completado);
            return usuarios;
            // throw new Exception("No hay suficientes facilitadores.");
        }
    }
    
    private List<Usuario> seleccionarFacilitadoresBypass(Integer limite, List<Usuario> usuariosExcluir) {
    	FacilitadorServiceBean facilitadorServiceBean = (FacilitadorServiceBean) BeanLocator.getInstance(FacilitadorServiceBean.class);
		List<Usuario> usuarios = facilitadorServiceBean.buscarFacilitadoresAleatorio(usuariosExcluir);
		
		if (usuarios.size() >= limite) {
		usuarios = cargaLaboralFacilitadoresBypass(usuarios);
		return usuarios.subList(0, limite);
		} else {
		return cargaLaboralFacilitadoresBypass(usuarios);
		}
    }
    
    public List<Usuario> cargaLaboralFacilitadoresBypass(List<Usuario> usuarios) {
        AsignarTareaFacade asignarTareaFacade = (AsignarTareaFacade) BeanLocator.getInstance(AsignarTareaFacade.class);
        return asignarTareaFacade.obtenerCargaFacilitadoresByPass(usuarios);
    }
}
