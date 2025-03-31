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
@Table(name="entity_type", schema="environmental_education_campaign")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "enty_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "enty_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "enty_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "enty_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "enty_user_update")) })
@NamedQuery(name="TipoEntidad.findAll", query="SELECT i FROM TipoEntidad i")
public class TipoEntidad  extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="enty_id")
	private Integer id;
	
	@Getter
    @Setter
    @Column(name="enty_desciption")
    private String descripcion;

}