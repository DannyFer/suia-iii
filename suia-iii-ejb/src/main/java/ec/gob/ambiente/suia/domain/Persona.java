/*
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.domain;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
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
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * <b> Entity Bean. </b>
 * 
 * @author Rene Enriquez
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Rene Enriquez, Fecha: Oct 28, 2014]
 *          </p>
 */
@NamedQueries({ @NamedQuery(name = Persona.FIND_ALL, query = "SELECT p FROM Persona p"),
@NamedQuery(name = Persona.FIND_BY_PIN, query = "SELECT u FROM Persona u WHERE pin = :pin")})
@Entity
@Table(name = "people", schema = "public")
//@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "peop_status")) })
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "peop_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "peop_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "peop_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "peop_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "peop_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "peop_status = 'TRUE'")
public class Persona extends EntidadAuditable {

	private static final long serialVersionUID = -8848052297710885892L;

	public static final String FIND_ALL = "ec.com.magmasoft.business.domain.Persona.findAll";
	public static final String FIND_BY_PIN = "ec.com.magmasoft.business.domain.Persona.findByPin";

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "PEOPLE_GENERATOR", initialValue = 1, sequenceName = "seq_peop_id", schema = "public", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PEOPLE_GENERATOR")
	@Column(name = "peop_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "peop_name")
	private String nombre;

	@Getter
	@Setter
	@Column(name = "peop_genre")
	private String genero;

	@Getter
	@Setter
	@Column(name = "peop_id_document")
	private String documento;

	@Getter
	@Setter
	@Column(name = "peop_title")
	private String titulo;

	@Getter
	@Setter
	@Column(name = "peop_pin")
	private String pin;

	@Getter
	@Setter
	@Column(name = "peop_position")
	private String posicion;

	@Getter
	@Setter
	@OneToMany(mappedBy = "persona")
	private List<Usuario> usuarios;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "nati_id")
	@ForeignKey(name = "fk_people_peop_nati_id_nationalities_nati_id")
	private Nacionalidad nacionalidad;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "gelo_id")
	@ForeignKey(name = "fk_people_gelo_id_geografical_locations_gelo_id")
	private UbicacionesGeografica ubicacionesGeografica;

	@Getter
	@Setter
	@Column(name = "gelo_id", insertable = false, updatable = false)
	private Integer idUbicacionGeografica;

	@Getter
	@Setter
	@Column(name = "nati_id", insertable = false, updatable = false)
	private Integer idNacionalidad;

	@Getter
	@Setter
	@Column(name = "trty_id", insertable = false, updatable = false)
	private Integer idTipoTratos;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "trty_id")
	@ForeignKey(name = "fk_people_trty_id_treatments_types_trty_id")
	private TipoTratos tipoTratos;

	@Getter
	@Setter
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "persona")
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<Organizacion> organizaciones;

	@Getter
	@Setter
	@OneToMany(cascade = CascadeType.PERSIST, mappedBy = "persona")
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<Contacto> contactos;

    public Contacto obtenerContactoPorId(Integer idContacto) {
        if (contactos != null) {
            for (int i = 0; i < contactos.size(); i++) {
                if (contactos.get(i).getEstado() && contactos.get(i).getFormasContacto().getId()
                        .equals(idContacto))
                    return contactos.get(i);
            }
        }
        return null;
    }

	@Getter
	@Setter
	@Transient
	private String direccion;

	@Getter
	@Setter
	@Transient
	private String telefono;

	@Getter
	@Setter
	@Transient
	private String correoElectronico;

	@Override
	public String toString() {
		return nombre;
	}

}
