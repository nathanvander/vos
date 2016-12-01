package vos.db;
import java.util.*;

/**
* A Pair is the basic entry in a table.
*/

public class Pair implements Comparable,Map.Entry<String,Comparable> {
	public String key;
	public Comparable value;

	public Pair() {}
	public Pair(String key,Comparable value) {
		if (key==null || value == null) {
			throw new NullPointerException();
		}
		this.key=key;
		this.value=value;
	}

	public int compareTo(Object o) {
		if (o==null) {return -1;}
		if (o instanceof Pair) {
			Pair p=(Pair)o;
			String k1=value.getClass().getName();
			String k2=p.value.getClass().getName();
			if (!k1.equals(k2)) {
				System.out.println("WARNING: on record# "+key+","+p.key+" trying to compare "+k1+" with "+k2);
			}
			return value.compareTo(p.value);
		} else {
			return -1;
		}
	}

	public String getKey() {return key;}
	public Comparable getValue() {return value;}
	public Comparable setValue(Comparable v) {
		Comparable old=value;
		value=v;
		return old;
	}

	public int hashCode() {
		return key.hashCode()*31+value.hashCode();
	}

	public boolean equals(Object o) {
		if (o==null || !(o instanceof Pair)) {
			return false;
		}
		return hashCode()==o.hashCode();
	}
}