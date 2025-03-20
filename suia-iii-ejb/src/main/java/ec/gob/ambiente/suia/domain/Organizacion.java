package ec.gob.ambiente.suia.domain;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name = "organizations", schema = "public")
@NamedQueries({
        @NamedQuery(name = Organizacion.FIND_BY_PERSON, query = "SELECT o FROM Organizacion o WHERE o.persona = :persona"),
        @NamedQuery(name = Organizacion.FIND_BY_PERSON_NAME, query = "SELECT o FROM Organizacion o WHERE o.persona = :persona and o.ruc = :nombre"),
		@NamedQuery(name = Organizacion.FIND_BY_RUC, query = "SELECT o FROM Organizacion o WHERE o.ruc = :ruc")

})
//@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "orga_status")) })
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "orga_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "orga_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "orga_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "orga_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "orga_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "orga_status = 'TRUE'")
public class Organizacion extends EntidadAuditable {

	private static final long serialVersionUID = -9134685354365311100L;

    public static final String FIND_BY_PERSON = "ec.com.magmasoft.business.domain.Organizacion.findByPerson";

    public static final String FIND_BY_PERSON_NAME = "ec.com.magmasoft.business.domain.Organizacion.findByPersonName";

	public static final String FIND_BY_RUC = "ec.com.magmasoft.business.domain.Organizacion.findByRuc";

	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "ORGANIZATIONS_GENERATOR", initialValue = 1, sequenceName = "seq_orga_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ORGANIZATIONS_GENERATOR")
	@Column(name = "orga_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "orga_ruc")
	private String ruc;

	@Getter
	@Setter
	@Column(name = "orga_charge_legal_representative")
	private String cargoRepresentante;

	@Getter
	@Setter
	@Column(name = "orga_name_organization")
	private String nombre;
	
	@Getter
	@Setter
	@Column(name = "org_name_comercial")
	private String nombreComercial;
	
	@Getter
	@Setter
	@Column(name = "orga_state_participation")
	private String participacionEstado;

	@Getter
	@Setter
	@Column(name = "gelo_id")
	private Integer idUbicacionGeografica;

	@Getter
	@Setter
	@Column(name = "tyor_id", insertable = false, updatable = false)
	private Integer idTipoOrganizacion;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "peop_id")
	@ForeignKey(name = "fk_organizations_peop_id_people_peop_id")
	private Persona persona;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "tyor_id")
	@ForeignKey(name = "fk_organizations_orty_id_organization_types_orty_id")
	private TipoOrganizacion tipoOrganizacion;

	@Getter
	@Setter
	@OneToMany(cascade = CascadeType.PERSIST, mappedBy = "organizacion")
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<Contacto> contactos;
	
	@Getter
	@Setter
	@Column(name = "org_qualified")
	private Boolean calificado;

	public Contacto obtenerContactoPorId(Integer idContacto) {
		if (contactos != null) {
			for (int i = 0; i < contactos.size(); i++) {
				if (contactos.get(i).getFormasContacto().getId().equals(idContacto) && contactos.get(i).getEstado())
					return contactos.get(i);
			}
		}
		return null;
	}
}
