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
 * <b> Entity que representa la tabla TarifasNUT. </b>
 * 
 * @author Santiago Flores
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Santiago Flores, Fecha: 29/05/2019]
 *          </p>
 */
@Entity
@Table(name = "rates_nut", schema = "payments")
@NamedQueries({
		@NamedQuery(name = TarifasNUT.LISTAR_TODOS, query = "SELECT c FROM TarifasNUT c WHERE c.estado= true"),
		@NamedQuery(name = TarifasNUT.LISTAR_TARIFAS_NUT_POR_NUT, query = "SELECT c FROM TarifasNUT c WHERE c.estado= true and c.numeroUnicoTransaccional.nutCodigo=:nutCodigo")})
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "ranu_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "ranu_date_create")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "ranu_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "ranu_user_create")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "ranu_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "ranu_status = 'TRUE'")
public class TarifasNUT extends EntidadAuditable {

	private static final long serialVersionUID = -1756612644167548934L;

	private static final String PAQUETE_CLASE = "ec.gob.ambiente.suia.recaudaciones.model.TarifasNUT";
	public static final String LISTAR_TODOS = PAQUETE_CLASE + "listarTodos";
	public static final String LISTAR_TARIFAS_NUT_POR_NUT = PAQUETE_CLASE + "listarTarifasNutPorNut";

	@Getter
	@Setter
	@Id
	@Column(name = "ranu_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "ranu_amount")
	private Integer cantidad;

	@Getter
	@Setter
	@Column(name = "ranu_unit_value")
	private Double valorUnitario;

	@Getter
    @Setter
    @JoinColumn(name = "nut_id", referencedColumnName = "nut_id")
    @ManyToOne
    @ForeignKey(name = "fk_rates_nut_nut_id_unique_transaction_number_nut_id")
    private NumeroUnicoTransaccional numeroUnicoTransaccional;
	
	@Getter
    @Setter
    @JoinColumn(name = "rate_id", referencedColumnName = "rate_id")
    @ManyToOne
    @ForeignKey(name = "fk_rates_nut_rate_id_rates_rate_id")
    private Tarifas tarifas;
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TarifasNUT)) {
			return false;
		}
		TarifasNUT other = (TarifasNUT) obj;
		if (((this.id == null) && (other.id != null)) || ((this.id != null) && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

}
