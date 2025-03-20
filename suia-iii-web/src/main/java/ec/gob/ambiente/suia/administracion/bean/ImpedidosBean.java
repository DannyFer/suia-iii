/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.bean;

import ec.gob.ambiente.suia.domain.Impedido;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author christian
 */
public class ImpedidosBean implements Serializable {

	private static final long serialVersionUID = 1413259353847640641L;

	@Getter
    @Setter
    private Impedido impedido;
    @Getter
    @Setter
    private List<Impedido> listaImpedidos;
    @Getter
    @Setter
    private String idTipoImpedido;
    @Getter
    @Setter
    private boolean apareceTabla;
    @Getter
    @Setter
    private boolean soloLectura;

    public void iniciarDatos() {
        setIdTipoImpedido(null);
        setImpedido(null);
        setListaImpedidos(null);
        setApareceTabla(true);
        setSoloLectura(false);
    }

}
