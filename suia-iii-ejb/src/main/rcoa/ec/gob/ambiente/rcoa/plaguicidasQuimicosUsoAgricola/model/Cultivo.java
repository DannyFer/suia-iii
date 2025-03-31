package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model;

import java.io.Serializable;

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
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;


/**
 * The persistent class for the crops database table.
 * 
 */
@Entity
@Table(name="crops", schema="chemical_pesticides")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "crop_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "crop_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "crop_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "crop_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "crop_user_update")) })

@NamedQueries({
@NamedQuery(name="Cultivo.findAll", query="SELECT c FROM Cultivo c"),
@NamedQuery(name=Cultivo.GET_CULTIVOS, query="SELECT c FROM Cultivo c WHERE c.estado = true order by 1")
})

@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "crop_status = 'TRUE'")
public class Cultivo extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAQUETE = "ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.";
	
	public static final String GET_CULTIVOS = PAQUETE + "Cultivo.getCultivos";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="crop_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="crop_common_name")
	private String nombreComun;
	
	@Getter
	@Setter	
	@Column(name="crop_scientific_name")
	private String nombreCientifico;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="geca_id_crop_type")
	private CatalogoGeneralCoa tipoCultivo;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="geca_id_family_type")
	private CatalogoGeneralCoa familiaCultivo;
	
	
	public String getNombreCientificoSplit() {
		return this.nombreCientifico.replace(";", "<br>");
	}

}