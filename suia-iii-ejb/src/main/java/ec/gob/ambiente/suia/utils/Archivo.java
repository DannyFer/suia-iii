package ec.gob.ambiente.suia.utils;

import lombok.Getter;
import lombok.Setter;

public class Archivo {

	@Getter @Setter
	private byte[] file;
	@Getter @Setter
	private String name;
	@Getter @Setter
	private String contentTye;
	@Getter @Setter
	private boolean editado;
	
	public Archivo(byte[] file, String name, String contentTye) {
		super();
		this.file = file;
		this.name = name;
		this.contentTye = contentTye;
	}
	
	public Archivo() {
	}
}
