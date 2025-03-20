/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author ishmael
 */
public class EntityUsuario implements Serializable {

    private static final long serialVersionUID = 2335071894416421264L;

    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private String nombre;

    @Getter
    @Setter
    private String fechaCreacion;
    
    @Getter
    @Setter
    private String nombreUsuario;
    
    @Getter
    @Setter
    private String estado;

    @Getter
    @Setter
    private String roles;

    @Getter
    @Setter
    private String cedula;
    
    @Getter
    @Setter
    private String provincia;

    public EntityUsuario() {
    }

    public EntityUsuario(String id, String nombre, String fechaCreacion, String roles) {
        this.id = id;
        this.nombre = nombre;
        this.fechaCreacion = fechaCreacion;
        this.roles = roles;
    }

    public EntityUsuario(String id, String nombre, String fechaCreacion, String roles, String nombreUsuario, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.fechaCreacion = fechaCreacion;
        this.roles = roles;
        this.nombreUsuario=nombreUsuario;
        this.estado=estado;
    }
    
    public EntityUsuario(String id, String nombre, String fechaCreacion, String nombrePersona, String nombreOrganizacion) {
        this.id = id;
        this.nombre = nombre;
        this.fechaCreacion = fechaCreacion;
        if (nombreOrganizacion != null && !nombreOrganizacion.isEmpty()) {
            this.roles = nombreOrganizacion;
        } else {
            this.roles = nombrePersona;
        }

    }
    
    public EntityUsuario(String id, String nombre, String fechaCreacion, String nombrePersona, String nombreOrganizacion, String estado, String roles) {
        this.id = id;
        this.nombre = nombre;
        this.roles=roles;
        this.fechaCreacion = fechaCreacion;
        if (nombreOrganizacion != null && !nombreOrganizacion.isEmpty()) {
            this.nombreUsuario = nombreOrganizacion;
        } else {
            this.nombreUsuario = nombrePersona;
        }

    }

    public EntityUsuario(String id, String nombre, String cedula) {
        this.id = id;
        this.nombre = nombre;
        this.cedula = cedula;
    }
    
    public EntityUsuario(String id, String nombre, String fechaCreacion, String nombrePersona, String nombreOrganizacion, String estado, String roles,String provincia) {
        this.id = id;
        this.nombre = nombre;
        this.roles=roles;
        this.fechaCreacion = fechaCreacion;
        if (nombreOrganizacion != null && !nombreOrganizacion.isEmpty()) {
            this.nombreUsuario = nombreOrganizacion;
        } else {
            this.nombreUsuario = nombrePersona;
        }
        this.provincia=provincia;

    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof EntityUsuario)) {
            return false;
        }
        EntityUsuario other = (EntityUsuario) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
