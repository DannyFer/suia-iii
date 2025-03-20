/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

import java.util.Collection;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

/**
 * 
 * @author christian
 */
@Entity
@Table(name = "menu", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "menu_status")) })
@NamedQueries({
		@NamedQuery(name = "Menu.findAll", query = "SELECT m FROM Menu m"),
		@NamedQuery(name = "Menu.listarMenu", query = "SELECT m FROM Menu m WHERE m.padreId IS NULL ORDER BY m.orden"),
		@NamedQuery(name = "Menu.obtenerPorNemonico", query = "SELECT m FROM Menu m WHERE m.nemonico = :nemonico"),
		@NamedQuery(name = "Menu.listarPorMenuPadre", query = "SELECT m FROM Menu m WHERE m.padreId = :menu ORDER BY m.orden"),
		@NamedQuery(name = "Menu.listarNodoFinalFalse", query = "SELECT m FROM Menu m WHERE m.nodoFinal = false ORDER BY m.orden"),
		@NamedQuery(name = "Menu.listarPorRolMuestra", query = "SELECT ac FROM Menu ac, MenuRoles ar WHERE ac.id = ar.idMenu AND ar.idRol in :roles AND ac.estado = true AND ac.muestraMenu = true ORDER BY ac.orden"),
		@NamedQuery(name = "Menu.listarPorRolNoMuestra", query = "SELECT ac FROM Menu ac, MenuRoles ar WHERE ac.id = ar.idMenu AND ar.idRol in :roles AND ac.estado = true AND ac.nodoFinal = true AND ac.muestraMenu = false ORDER BY ac.orden") })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "menu_status = 'TRUE'")
public class Menu extends EntidadBase {

	private static final long serialVersionUID = 1L;
	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "MENU_ID_GENERATOR", initialValue = 1, sequenceName = "seq_menu_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MENU_ID_GENERATOR")
	@Basic(optional = false)
	@Column(name = "menu_id", nullable = false)
	private Integer id;
	@Getter
	@Setter
	@Basic(optional = false)
	@Column(name = "menu_name", nullable = false, length = 200)
	private String nombre;
	@Getter
	@Setter
	@Column(name = "menu_action", length = 200)
	private String accion;
	@Getter
	@Setter
	@Column(name = "menu_url", nullable = true, length = 200)
	private String url;
	@Getter
	@Setter
	@Basic(optional = false)
	@Column(name = "menu_end_node", nullable = false)
	private boolean nodoFinal;
	@Getter
	@Setter
	@Column(name = "menu_sample_menu")
	private boolean muestraMenu;
	@Getter
	@Setter
	@Column(name = "menu_mnemonic", length = 50)
	private String nemonico;
	@Getter
	@Setter
	@Basic(optional = false)
	@Column(name = "menu_order", nullable = false)
	private int orden;
	@Getter
	@Setter
	@OneToMany(mappedBy = "padreId", fetch = FetchType.LAZY)
	private Collection<Menu> menuCollection;
	@Getter
	@Setter
	@JoinColumn(name = "menu_father_id", referencedColumnName = "menu_id")
	@ForeignKey(name = "fk_menu_menu_father_id_menu_menu_id")
	@ManyToOne(fetch = FetchType.EAGER)
	private Menu padreId;
	@Getter
	@Setter
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "menuId", fetch = FetchType.LAZY)
	private Collection<MenuRoles> menuRolesCollection;
	@Getter
	@Setter
	@Column(name = "menu_father_id", insertable = false, updatable = false)
	private Long idMenuPadre;
	@Getter
	@Setter
	@Basic(optional = false)
	@Column(name = "menu_icon", nullable = false, length = 100)
	private String icono;

	public Menu() {
	}

	public Menu(Integer id) {
		this.id = id;
	}
}
