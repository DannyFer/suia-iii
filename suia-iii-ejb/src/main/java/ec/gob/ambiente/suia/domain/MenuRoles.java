/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * @author christian
 */
@Entity
@Table(name = "menu_roles", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "mero_status")),
@AttributeOverride(name = "fechaCreacion", column = @Column(name = "mero_creation_date")),
@AttributeOverride(name = "fechaModificacion", column = @Column(name = "mero_date_update")),
@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "mero_creator_user")),
@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "mero_user_update"))})
@NamedQueries({ @NamedQuery(name = "MenuRoles.findAll", query = "SELECT m FROM MenuRoles m"),
		@NamedQuery(name = "MenuRoles.listarPorRol", query = "SELECT m FROM MenuRoles m WHERE m.roleId = :rol and m.estado=true"),
		@NamedQuery(name = "MenuRoles.listarPorRolMenu", query = "SELECT m FROM MenuRoles m WHERE m.roleId = :rol and m.menuId = :menuId and m.estado=true")})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "mero_status = 'TRUE'")
public class MenuRoles extends EntidadAuditable {

	private static final long serialVersionUID = 1L;
	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "MENU_ROL_ID_GENERATOR", initialValue = 1, sequenceName = "seq_mero_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MENU_ROL_ID_GENERATOR")
	@Basic(optional = false)
	@Column(name = "mero_id", nullable = false)
	private Integer id;
	@Getter
	@Setter
	@JoinColumn(name = "role_id", referencedColumnName = "role_id", nullable = false)
	@ForeignKey(name = "fk_menu_roles_role_id_roles_role_id")
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Rol roleId;
	@Getter
	@Setter
	@JoinColumn(name = "menu_id", referencedColumnName = "menu_id", nullable = false)
	@ForeignKey(name = "fk_menu_roles_menu_id_menu_menu_id")
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Menu menuId;
	@Getter
	@Setter
	@Column(name = "menu_id", insertable = false, updatable = false)
	private Integer idMenu;
	@Getter
	@Setter
	@Column(name = "role_id", insertable = false, updatable = false)
	private Long idRol;

	public MenuRoles() {
	}

	public MenuRoles(Integer id) {
		this.id = id;
	}
}
