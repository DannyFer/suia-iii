package ec.gob.ambiente.suia.campanias.bajalealplastico.domain;

import java.io.Serializable;

import javax.persistence.*;

import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the registration_information database table.
 * 
 */
@Entity
@Table(name="registration_information", schema="environmental_education_campaign")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "rein_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "rein_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "rein_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "rein_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "rein_user_update")) })
@NamedQuery(name="Informacion.findAll", query="SELECT i FROM Informacion i")
public class Informacion  extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="rein_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "rein_accept_terms")
	private Boolean aceptarTerminos;
	
	@Getter
    @Setter
    @Column(name="rein_cell_phone")
    private String celular;
	
	@Getter
    @Setter
    @Column(name="rein_telephone")
    private String telefono;
	
	@Getter
    @Setter
    @Column(name="rein_mail")
    private String correo;
	
	@Getter
    @Setter
    @Column(name="rein_responsable")
    private String cedulaResponsable;
	
	@Getter
    @Setter
    @Column(name="rein_name_responsable")
    private String nombreResponsable;
	
	@Getter
	@Setter
	@Column(name = "rein_students_number")
	private Integer numeroEstudiantes;
	
	@Getter
	@Setter
	@Column(name = "rein_institution_id")
	private String idInstitucion;
	
	@Getter
	@Setter
	@Column(name = "id_entity_type")
	private Integer idTipoEntidad;

	@Getter
	@Setter
	@Transient
	private Institucion institucion;
}