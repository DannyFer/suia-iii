package ec.gob.ambiente.rcoa.digitalizacion.model;

import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;	

@Entity
@Table(name="history_updates", schema = "coa_digitalization_linkage")
@NamedQueries({
@NamedQuery(name="HistorialactualizacionesDigitalizacion.GETFINDALL", query="SELECT c FROM HistorialactualizacionesDigitalizacion c")})

@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "hiup_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "hiup_creation_date")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "hiup_user_update")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "hiup_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "hiup_creator_user")),})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "hiup_status = 'TRUE'")


public class HistorialactualizacionesDigitalizacion extends EntidadAuditable {
    private static final long serialVersionUID = 5016494380218175574L;    
    @Getter
    @Setter
    @Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "hiup_id")
    private Integer id;
    @Getter
    @Setter
    @Column(name = "hiup_update_description", length = 500)
    private String descripcionActualizacion;

	@ManyToOne
	@JoinColumn(name = "enaa_id")
	@ForeignKey(name = "fk_enaa_id")
	@Getter
	@Setter
	private AutorizacionAdministrativaAmbiental autorizacionAdministrativaAmbiental;

	@ManyToOne
	@JoinColumn(name = "user_id")
	@ForeignKey(name = "fk_user_id")
	@Getter
	@Setter
	private Usuario usuario;


	@Getter
	@Setter
	@Transient
	private String areaTecnico;
}
