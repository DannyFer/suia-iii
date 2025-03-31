package ec.gob.ambiente.suia.recaudaciones.model;

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
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * <b> Entity que representa la tabla Tarifas. </b>
 * 
 * @author Santiago Flores
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Santiago Flores, Fecha: 29/05/2019]
 *          </p>
 */
@Entity
@Table(name = "rates", schema = "payments")
@NamedQueries({
		@NamedQuery(name = Tarifas.LISTAR_TODOS, query = "SELECT c FROM Tarifas c WHERE c.estado= true") })
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "rate_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "rate_date_create")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "rate_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "rate_user_create")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "rate_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "rate_status = 'TRUE'")
public class Tarifas extends EntidadAuditable {

	private static final long serialVersionUID = -1756612644167548934L;

	public static final String LISTAR_TODOS = "ec.com.magmasoft.business.domain.Tarifas.listarTodos";

	@Getter
	@Setter
	@Id
	@Column(name = "rate_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "rate_code")
	private String tasasCodigo;
	
	@Getter
	@Setter
	@Column(name = "rate_name")
	private String tasasNombre;

	@Getter
	@Setter
	@Column(name = "rate_description")
	private String tasasDescripcion;

	@Getter
	@Setter
	@Column(name = "rate_value")
	private Double tasasValor;

	@Getter
    @Setter
    @JoinColumn(name = "acco_id", referencedColumnName = "acco_id")
    @ManyToOne
    @ForeignKey(name = "fk_account_acco_id_rates_acco_id")
    private Cuentas cuentas;
	
	@Getter
    @Setter
    @JoinColumn(name = "raty_id", referencedColumnName = "raty_id")
    @ManyToOne
    @ForeignKey(name = "fk_rates_raty_id_rates_type_raty_id")
    private TipoTarifas tipoTarifas;
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Tarifas)) {
			return false;
		}
		Tarifas other = (Tarifas) obj;
		if (((this.id == null) && (other.id != null)) || ((this.id != null) && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return  getTasasDescripcion()+" - "+getCuentas().getCuentaNumero();
	}
	
	public Tarifas(){
		
	}
	
	public Tarifas(Integer id){
		this.id=id;
	}

}
