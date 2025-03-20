package ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.PlantillaReporte;

@Stateless
public class InformeProvincialGADFacade {
	@EJB
	CrudServiceBean crudServiceBean;
	@EJB
	private DocumentosFacade documentosFacade;
	

	private static final String CARPETA_INFORME_OFICIO_REQUISITOS_MODALIDAD_DISPOSICION_FINAL = "informe_oficio_requisitos_modalidad_disposicion_final";

	public PlantillaReporte obtenerPlantillaReporte(Integer tipoDocumentoId) {
		try {
			Map<String, Object> parametros = new HashMap();
			parametros.put("p_tipoDocumentoId", tipoDocumentoId);

			List<PlantillaReporte> lista = this.crudServiceBean.findByNamedQueryGeneric(
					PlantillaReporte.OBTENER_PLANTILLA_POR_INFORME, parametros);
			if ((lista != null) && (!lista.isEmpty())) {
				return (PlantillaReporte) lista.get(0);
			}
		} catch (RuntimeException e) {
			throw new RuntimeException(e);
		}
		return null;
	}
	
	public BigInteger obtenerNumeroInforme(String sequenceName, String schema) {
		try {
			return (BigInteger) this.crudServiceBean.getSecuenceNextValue(sequenceName, schema);
		} catch (RuntimeException e) {
			throw new RuntimeException(e);
		}
	}

	public byte[] descargarFile(Documento documento) throws CmisAlfrescoException {
		return documentosFacade.descargar(documento.getIdAlfresco());
	}

}
