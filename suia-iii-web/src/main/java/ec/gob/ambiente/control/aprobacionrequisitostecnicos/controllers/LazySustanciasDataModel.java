package ec.gob.ambiente.control.aprobacionrequisitostecnicos.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.SustanciasQuimicaPeligrosaTransporteFacade;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.SustanciaQuimicaPeligrosaTransporte;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.BeanLocator;

public class LazySustanciasDataModel extends LazyDataModel<SustanciaQuimicaPeligrosaTransporte> {

	private static final long serialVersionUID = 1L;
	
	private SustanciasQuimicaPeligrosaTransporteFacade sustanciasFacade = BeanLocator
            .getInstance(SustanciasQuimicaPeligrosaTransporteFacade.class);
	
	private List<SustanciaQuimicaPeligrosaTransporte> sustanciasQuimicas;
	
	@Getter
	@Setter
	private Integer idAprobacionRequisitosTecnicos;

	public LazySustanciasDataModel(Integer idArt) {
		idAprobacionRequisitosTecnicos = idArt;
    }

    @Override
    public Object getRowKey(SustanciaQuimicaPeligrosaTransporte usuario) {
        return usuario.getId();
    }

    @Override
    public List<SustanciaQuimicaPeligrosaTransporte> load(int first, int pageSize, String sortField,
                                    SortOrder sortOrder, Map<String, Object> filters) {

        sustanciasQuimicas = new ArrayList<SustanciaQuimicaPeligrosaTransporte>();

        Integer total = sustanciasFacade.contarSustancias(idAprobacionRequisitosTecnicos);

        try {
            sustanciasQuimicas = sustanciasFacade.getListaSustanciaQuimicaPeligrosaTransporteLazy(first, pageSize, idAprobacionRequisitosTecnicos);
        } catch (ServiceException e) {
            sustanciasQuimicas = new ArrayList<SustanciaQuimicaPeligrosaTransporte>();
        }

        this.setRowCount(total);

        return sustanciasQuimicas;

    }
    
}
