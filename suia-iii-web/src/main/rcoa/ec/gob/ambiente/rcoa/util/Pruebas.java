package ec.gob.ambiente.rcoa.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Pruebas {

	public static void main(String[] args) {
		ArrayList<Integer> lista=new ArrayList<Integer>();
		lista.add(1);
		lista.add(1);
		lista.add(1);		 
		lista.add(3);		
		Set<Integer> miSet = new HashSet<Integer>(lista);
		int x=0;
		for(int s: miSet){
		 System.out.println(s + " -> " +Collections.frequency(lista,s));
		 x++;
		}
		System.out.println("total numeros diferentes:" +x);

	}

}
