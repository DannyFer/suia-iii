package ec.gob.ambiente.rcoa.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="environmental_specialist", schema="coa_mae")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "ensp_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "ensp_creation_date")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "ensp_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "ensp_creator_user")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "ensp_user_update")) })
@NamedQueries({
	@NamedQuery(name=EspecialistaAmbiental.GET_POR_NUMEROREGISTRO_POR_IDENTIFICACION, query="SELECT m FROM EspecialistaAmbiental m where m.estado = true and m.identificacion = :identificacion and m.numeroRegistro = :numeroregistro")
})
public class EspecialistaAmbiental extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String GET_POR_NUMEROREGISTRO_POR_IDENTIFICACION = "ec.gob.ambiente.rcoa.model.EspecialistaAmbiental.getNumeroPorIdentificacion";
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="ensp_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="ensp_identificacion")
	private String identificacion;

	@Getter
	@Setter
	@Column(name="ensp_nombres")
	private String nombre;

	@Getter
	@Setter
	@Column(name="ensp_titulo")
	private String titulo;

	@Getter
	@Setter
	@Column(name="ensp_numero_registro")
	private String numeroRegistro;

}