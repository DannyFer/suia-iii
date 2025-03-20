package ec.gob.ambiente.rcoa.model;

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
import javax.persistence.Transient;

import org.hibernate.annotations.Filter;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;


/**
 * The persistent class for the province_areas_snap database table.
 * 
 */
@Entity
@Table(name="province_areas_snap", schema = "coa_mae")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "prar_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "prar_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "prar_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "prar_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "prar_user_update")) })

@NamedQueries({ 
@NamedQuery(name="AreasSnapProvincia.findAll", query="SELECT a FROM AreasSnapProvincia a") })
public class AreasSnapProvincia extends EntidadAuditable implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "prar_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="gelo_id")
	private Integer geloIdProvincia;

	@Getter
	@Setter
	@Column(name="prar_gid_snap")
	private Integer gidSnap;

	@Getter
	@Setter
	@Column(name="prar_inec_province")
	private String inecProvincia;

	@Getter
	@Setter
	@Column(name="prar_protected_area")
	private String nombreAreaProtegida;

	@Getter
	@Setter
	@Column(name="prar_province")
	private String nombreProvincia;

	@Getter
	@Setter
	@Column(name="prar_suap_snap")
	private String tipoSnap;

	@Getter
	@Setter
	@Column(name="prar_subsytem_type")
	private Integer tipoSubsitema; //1MAE   2DELEGADA

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "user_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "user_status = 'TRUE'")
	private Usuario usuario;
	
	@Getter
	@Setter
	@Column(name="prar_abbreviation")
	private String abreviacion;
	
	@Getter
	@Setter
	@Column(name="prar_csnap")
	private String codigoSnap;

	@Getter
	@Setter
	@Column(name="prar_zone")
	private String zona;
	
	@Getter
	@Setter
	@Column(name="prar_qualifies_zone")
	private Integer valorZonificacionArea;
	
	@Getter
	@Setter
	@Transient
	private Usuario tecnicoResponsable;

	public String getNombreAreaCompleto() {
		if(zona != null && zona.equals("NO APLICA")) {
			return nombreAreaProtegida;
		} else {
			return nombreAreaProtegida + " " + zona;
		}
	}

}