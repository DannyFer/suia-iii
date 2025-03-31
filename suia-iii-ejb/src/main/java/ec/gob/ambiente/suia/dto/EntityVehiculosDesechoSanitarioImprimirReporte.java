/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author Frank Torres
 */
public class EntityVehiculosDesechoSanitarioImprimirReporte {

    @Getter
    @Setter
    private String placa;
    @Getter
    @Setter
    private String motor;
    @Getter
    @Setter
    private String modelo;
    @Getter
    @Setter
    private String tipo;
    @Getter
    @Setter
    private String cilindraje;
    @Getter
    @Setter
    private String pesoBruto;
    @Getter
    @Setter
    private String pesoVeicular;
    @Getter
    @Setter
    private String pesoTonelaje;
}
