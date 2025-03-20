/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.bean;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import org.primefaces.model.TreeNode;

import ec.gob.ambiente.rcoa.model.AreasSnapProvincia;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.Usuario;

/**
 * 
 * @author ishmael
 */
@Data
public class PromotorBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7687114018058910027L;

	private Usuario usuario;

	private Persona persona;

	private Organizacion organizacion;

	private String tipoPersona;

	private String idNacionalidad;

	private String idTipoOrganizacion;

	private String idFormaContacto;

	private String idTipoTrato;

	private Contacto contacto;

	private List<Contacto> listaContacto;

	private List<Contacto> listaContactoObligatorios;

	private List<Contacto> listaContactoOpcionales;

	private List<Contacto> listaContactoRemover;

	private String idParroquia;

	private boolean aceptaTerminos;

	private List<String> causas;

	private boolean deshabilitarRegistro;

	private TreeNode root;
	
	private TreeNode rootOt;
	
	private TreeNode rootAP;

	private List<Area> listaMenu;

	private TreeNode selectedNode;
	
	@Getter
    @Setter
	private TreeNode[] selectedNodeAP;
	
	@Getter
    @Setter
    private TreeNode[] selectedNodes;

	private String passwordAnterior;

	private String password;

	private String scriptNumeros;

	private String scriptTamanio;
	
	private String maskValores;

	private String scriptNumerosDocumento;

	private String scriptTamanioDocumento;

	private boolean wsEncontrado;

	private String telefono;

	private String celular;

	private String email;

	private String direccion;
	
	@Getter
	@Setter
	private List<Area> areasGuardar;
	
	private List<AreasSnapProvincia> areasProtegidasGuardar;
}
