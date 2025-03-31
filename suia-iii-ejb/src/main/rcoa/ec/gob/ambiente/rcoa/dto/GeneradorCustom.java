package ec.gob.ambiente.rcoa.dto;

import lombok.Getter;
import lombok.Setter;

public class GeneradorCustom {

	public static final String SOURCE_TYPE_SUIA_III = "source_type_suia_iii";
	public static final String SOURCE_TYPE_RCOA = "source_type_rcoa";

	@Getter
	@Setter
	private Integer id;
	
	@Getter
	@Setter
	private Integer idGeneradorProyectoRcoa;

	@Getter
	@Setter
	private String sourceType;

	@Getter
	@Setter
	private String codigo;

	@Getter
	@Setter
	private String documento;

	public GeneradorCustom() {
		
	}

	public GeneradorCustom(Integer id, String codigo, String documento, Integer idGeneradorProyectoRcoa) {
		this.id = id;
		this.codigo = codigo;
		this.documento = documento;
		this.idGeneradorProyectoRcoa = idGeneradorProyectoRcoa;
		this.sourceType = SOURCE_TYPE_RCOA;
	}

	public GeneradorCustom(Integer id, String codigo, String documento) {
		this.id = id;
		this.codigo = codigo;
		this.documento = documento;
		this.sourceType = SOURCE_TYPE_SUIA_III;
	}
	
}
