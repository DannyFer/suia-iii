package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model;

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

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;


/**
 * The persistent class for the pests database table.
 * 
 */
@Entity
@Table(name="pests", schema="chemical_pesticides")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "pest_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "pest_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "pest_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "pest_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "pest_user_update")) })

@NamedQueries({
@NamedQuery(name="Plaga.findAll", query="SELECT p FROM Plaga p"),
@NamedQuery(name=Plaga.GET_PLAGAS, query="SELECT p FROM Plaga p where p.estado = true order by 1")
})

@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "pest_status = 'TRUE'")
public class Plaga extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAQUETE = "ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.";
	
	public static final String GET_PLAGAS = PAQUETE + "Plaga.getpLAGASs";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="pest_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name="pest_scientific_name")
	private String nombreCientifico;

	@Getter
	@Setter
	@Column(name="pest_common_name")
	private String nombreComun;

	public String getNombreComunSplit() {
		return this.nombreComun.replace(";", "<br>");
	}

}