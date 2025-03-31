package ec.gob.ambiente.core.datamodel;

import ec.gob.ambiente.suia.dto.EntityUsuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LazyUserCustomDataModel extends LazyDataModel<EntityUsuario> {

    /**
     *
     */
    private static final long serialVersionUID = -3479713500589851595L;

    private UsuarioFacade usuarioFacade = BeanLocator
            .getInstance(UsuarioFacade.class);


    private List<EntityUsuario> usuarios;


    public LazyUserCustomDataModel() {//AUTORIDAD AMBIENTAL MAE  TÉCNICO ANALISTA MAE

    }

    @Override
    public Object getRowKey(EntityUsuario usuario) {
        return usuario.getId();
    }

    @Override
    public List<EntityUsuario> load(int first, int pageSize, String sortField,
                                    SortOrder sortOrder, Map<String, Object> filters) {

        usuarios = new ArrayList<EntityUsuario>();

        String nombreCompleto = "";
        String nombre = "";

        if (filters != null) {

            if (filters.containsKey("roles")) {
                nombreCompleto = (String) filters.get("roles");
            }
            if (filters.containsKey("nombre")) {
                nombre = (String) filters.get("nombre");
            }
        }
        Integer total = usuarioFacade.contarUsuariosCompletoEntity(nombre, nombreCompleto);


        try {
            usuarios = usuarioFacade.listarUsuariosCompletoEntity(first, pageSize, nombre, nombreCompleto);
        } catch (ServiceException e) {
            usuarios = new ArrayList<EntityUsuario>();
        }

        this.setRowCount(total);

        return usuarios;

    }

}
