package ec.gob.ambiente.rcoa.digitalizacion.model;


import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;	

@Entity
@Table(name="location", schema = "coa_digitalization_linkage")
@NamedQueries({
@NamedQuery(name="UbicacionDigitalizacion.GETFINDALL", query="SELECT c FROM UbicacionDigitalizacion c")})

@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "loca_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "loca_creation_date")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "loca_user_update")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "loca_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "loca_creator_user")),})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "loca_status = 'TRUE'")


public class UbicacionDigitalizacion extends EntidadAuditable {
    private static final long serialVersionUID = 5016494380218175574L;    
    @Getter
    @Setter
    @Id    
    @SequenceGenerator(name = "LOCATION_DIGITALIZATION_ID_GENERATOR", sequenceName = "location_loca_id_seq", schema = "coa_digitalization_linkage", allocationSize = 1)
   	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LOCATION_DIGITALIZATION_ID_GENERATOR")
    @Column(name = "loca_id")
    private Integer id;
    @Getter
    @Setter
    @Column(name = "loca_observations", length = 255)
    private String observaciones;
    @Getter
    @Setter
    @Column(name = "loca_reference_system", length = 10)
    private String sistemaReferencia;
    @Getter
    @Setter
    @Column(name = "loca_zone", length = 4)
    private String zona;
    @Getter
    @Setter
    @Column(name = "loca_primary")
    private Boolean principal;    
    @Getter
    @Setter
    @Column(name = "loca_update_number")
    private Integer numeroActualizacion;
    @Getter
    @Setter
    @Column(name = "loca_type")
    private Integer tipoIngreso; //1 ingreso del sistema, 2 ingreso del usuario

	@ManyToOne
	@JoinColumn(name = "enaa_id")
	@ForeignKey(name = "fk_enaa_id")
	@Getter
	@Setter
	private AutorizacionAdministrativaAmbiental autorizacionAdministrativaAmbiental;
	@ManyToOne
	@JoinColumn(name = "gelo_id")
	@ForeignKey(name = "fk_gelo_id")
	@Getter
	@Setter
	private UbicacionesGeografica ubicacionesGeografica;

}
