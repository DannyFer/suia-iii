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
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;


/**
 * The persistent class for the integrated_management_plan database table.
 * 
 */
@Entity
@Table(name="integrated_management_plan", schema="chemical_pesticides")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "inmp_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "inmp_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "inmp_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "inmp_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "inmp_user_update")) })

@NamedQueries({
@NamedQuery(name="NumeroPlanGestionIntegral.findAll", query="SELECT n FROM NumeroPlanGestionIntegral n"),
@NamedQuery(name=NumeroPlanGestionIntegral.GET_NUMEROS_PGI, query="SELECT n FROM NumeroPlanGestionIntegral n WHERE n.estado = true order by 1")
})

@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "inmp_status = 'TRUE'")
public class NumeroPlanGestionIntegral extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAQUETE = "ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.";
	
	public static final String GET_NUMEROS_PGI = PAQUETE + "NumeroPlanGestionIntegral.getNumeroPgi";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="inmp_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="user_id")
	private Usuario operador;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="geca_id_guild")
	private CatalogoGeneralCoa gremio;

	@Getter
	@Setter
	@Column(name="inmp_number_plan")
	private String numeroPlan;

	@Getter
	@Setter
	@Column(name="inmp_user_sga")
	private Boolean esOperadorSga;

	@Getter
	@Setter
	@Transient
	private String nombreOperador;

}