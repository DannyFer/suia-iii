/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * @author ishmael
 */
@Entity
@Table(name = "contacts")
@NamedQueries({
		@NamedQuery(name = Contacto.FIND_BY_PERSON, query = "SELECT c FROM Contacto c WHERE c.persona = :persona AND c.estado=true ORDER BY c.id ASC"),
		@NamedQuery(name = Contacto.FIND_BY_EMAIL, query = "SELECT c FROM Contacto c WHERE c.estado=true and c.formasContacto.id = :codigoEmail and c.valor = :valor"),
        @NamedQuery(name = Contacto.FIND_BY_ORGANIZATION, query = "SELECT c FROM Contacto c WHERE c.organizacion = :organizacion AND c.estado=true ORDER BY c.id ASC") ,
        @NamedQuery(name = Contacto.FIND_BY_ORGANIZATION_NOMBRE, query = "SELECT c FROM Contacto c WHERE c.organizacion = :organizacion AND c.estado=true ORDER BY c.id ASC"),
        @NamedQuery(name = Contacto.FIND_BY_ORGANIZATION_PERSONA, query = "SELECT c FROM Contacto c WHERE c.organizacion = :organizacion and c.persona = :persona AND c.estado=true ORDER BY c.id DESC"),})
//@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "cont_status")) })
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "cont_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "cont_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "cont_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "cont_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "cont_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "cont_status = 'TRUE'")
public class Contacto extends EntidadAuditable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4520557389449170919L;
	public static final String FIND_BY_PERSON = "ec.com.magmasoft.business.domain.Contacto.findByPerson";
    public static final String FIND_BY_ORGANIZATION = "ec.com.magmasoft.business.domain.Contacto.findByOrganization";
    public static final String FIND_BY_ORGANIZATION_NOMBRE = "ec.com.magmasoft.business.domain.Contacto.findByOrganizationName";
    public static final String FIND_BY_ORGANIZATION_PERSONA = "ec.com.magmasoft.business.domain.Contacto.findByOrganizationPersona";
	public static final String FIND_BY_EMAIL = "ec.com.magmasoft.business.domain.Contacto.findByEmail";

	@Id
	@Column(name = "cont_id")
	@SequenceGenerator(name = "CONTACTS_GENERATOR", initialValue = 1, sequenceName = "seq_cont_id", schema = "public", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CONTACTS_GENERATOR")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@Column(name = "cont_value")
	private String valor;

	@Getter
	@Setter
	@JoinColumn(name = "pers_id", referencedColumnName = "peop_id")
	@ForeignKey(name = "fk_contacts_pers_id_person_person_id")
    @Filter(name = EntidadBase.FILTER_ACTIVE, condition = "peop_status = TRUE")
	@ManyToOne
	private Persona persona;

	@Getter
	@Setter
	@JoinColumn(name = "orga_id", referencedColumnName = "orga_id")
	@ForeignKey(name = "fk_contacts_orga_id_organizations_orga_id")
    @Filter(name = EntidadBase.FILTER_ACTIVE, condition = "orga_status = TRUE")
	@ManyToOne
	private Organizacion organizacion;
	
	@Getter
	@Setter
	@JoinColumn(name = "hwpl_id", referencedColumnName = "hwpl_id")
	@ForeignKey(name = "fk_contacts_cont_id_providers_hwpl_id")
	@ManyToOne
	private SedePrestadorServiciosDesechos sedePrestadorServiciosDesechos;

	@Getter
	@Setter
	@JoinColumn(name = "cofo_id", referencedColumnName = "cofo_id")
	@ForeignKey(name = "fk_contacts_cont_id_contacts_forms_cofo_id")
    @Filter(name = EntidadBase.FILTER_ACTIVE, condition = "cont_status = TRUE")
	@ManyToOne
	private FormasContacto formasContacto;

	@Getter
	@Setter
	@Transient
	private int indice;

    public Contacto() {
    }

    public Contacto(String valor, Integer idFormaContacto) {
        this.valor = valor;
        this.formasContacto = new FormasContacto(idFormaContacto);
    }


}
