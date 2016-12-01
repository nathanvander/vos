package vos.db;
import java.util.*;

/**
*  This is the simplest possible database, map up of one object array and a hashtable of indexes,
* one per table
*
*/
public class PairTable extends AbstractMap<String,Comparable> implements Map<String,Comparable> {
	Pair[] data;
	Hashtable indici;  //map of tablename to arraylist

	int nextId=1;

	//STATIC METHODS
	//---------------------------------------
	//convert to base-15
	public static String toKey(int i) {
		return Integer.toString(i,15);
	}

	public static int fromKey(String s) throws NumberFormatException {
		return Integer.parseInt(s,15);
	}
	//--------------------------------------


	//CONSTRUCTOR
	public PairTable(int initCapacity) {
		data = new Pair[initCapacity];
		indici = new Hashtable();
	}

	//--------------------------------------------
	//this assigns new keys for inserts
	private String assignKey() {
		String key=toKey(nextId);
		nextId++;
		return key;
	}

	//used to sync nextId when loading from log
	private void checkNextId(String key) {
		int n=fromKey(key);
		if (n>=nextId) {
			nextId=n+1;
		}
	}

	//===================================================
	//map methods

	//this is the size of the array, not the number of elements in it
	public int size() {
		return data.length;
	}

	//-----------------------------------------------
	//insert

	private List getIndex(String className) {
		List index=(List)indici.get(className);
		if (index==null) {
			index=new ArrayList(63);
			indici.put(className,index);
		}
		return index;

	}

	//====================================
	//database commands

	public String insert(Comparable c) {
		if (c==null) {return null;}
		String key=assignKey();
		put(key,c);
		return key;
	}

	//use when loading from file
	public void store(String key,Comparable c) {
		if (c==null || key==null) {return;}
		checkNextId(key);

		String k=c.getClass().getName();
		if (k.equals("vos.db.DeletedObject") ) {
			//we really are deleting it
			delete(key);
		} else {
			put(key,c);
		}
	}

	//used when updating the object
	public void update(String key,Comparable c) {
		if (c==null || key==null) {return;}
		put(key,c);
	}

	public void delete(String key) {
		remove(key);
	}

	public Iterator selectAll(String tableName) {
		return sorted(tableName);
	}

	//========================================

	public Comparable put(String k,Comparable v) {
		//we do not accept null values
		if (k==null || v==null) {
			return null;
		}

		//make sure the array is big enough
		int n = fromKey(k);
		ensureCapacity(n);

		//get index
		String klass=v.getClass().getName();
		List index=getIndex(klass);

		//see if the data already exists
		boolean replace=false;
		Pair p=data[n];

		if (p==null) {
			//add or insert
			p=new Pair(k,v);
			data[n]=p;

			//add index
			index.add(p);
			return null;
		} else {
			//update or replace
			int j=index.indexOf(p);

			Comparable oldvalue=p.value;
			p.value=v;
			data[n]=p;
			index.set(j,p);
			return oldvalue;
		}
	}

	//doesn't affect the index
	public Comparable get(String k) {
		int n = fromKey(k);
		Pair p=data[n];
		if (p==null) {
			return null;
		} else {
			return p.value;
		}
	}

	public Comparable remove(String k){
		int n = fromKey(k);
		Pair p=data[n];
		if (p==null) {
			return null;
		} else {
			String klass=p.value.getClass().getName();
			List index=getIndex(klass);

			//delete data
			data[n]=null;
			//remove from index
			index.remove(p);
			return p.value;
		}
	}

	public Set entrySet() {
		//not implemented
		return null;
	}

	//====================================================

	//get the iterator to the raw data
	public Iterator pairs() {
		return new Iter(this);
	}

	//get the iterator to the sorted data
	public Iterator sorted(String className) {
		List index=getIndex(className);
		Collections.sort(index);
		return index.iterator();
	}

	//===========================================

	//the array is zero-based. Therefore the capacity must always be at least 1 more than the entry number
	public void ensureCapacity(int i) {
		if (i>data.length-1) {
			resize(i+1);
		}
	}

	//every time this is run it will double the existing size
	private void resize(int n) {
		int oldlength=data.length;
		int newsize=oldlength*2+1;
		if (n>newsize) {
			newsize=n;
		}
		System.out.println("resizing to "+newsize);
		Pair[] old=data;
		data=new Pair[newsize];
		System.arraycopy(old,0,data,0,oldlength);
	}

	//======================================================
	class Iter implements Iterator {
		PairTable ptable;
		int index=0;  //location of current element
		int next=0;	  //location of next available element, -1 if none

		public Iter(PairTable pt) {
			ptable=pt;
		}

		private boolean findNext() {
			next=index+1;
			while (next<ptable.size()) {
				Pair p=ptable.data[next];
				if (p==null) {
					next++;
				} else {
					return true;
				}
			}
			//not found
			next=-1;
			return false;
		}

		public boolean hasNext() {
			if (next>index) {
				return true;
			} else {
				return findNext();
			}
		}

		//return the next key
		public Object next() {
			if (next==-1) {
				return null;
			} else {
				index=next;
				return ptable.data[next];
			}
		}

		//not incremented
		public void remove() {}
	}

	//==============================
	public static void main(String[] args) {
		PairTable pt=new PairTable(2);  //set it to size 2 to test
		pt.put("1","Akron");
		pt.put("2","Buffalo");
		pt.put("3","Cleveland");
		pt.put("4","Detroit");
		pt.put("5","Erie");
		pt.put("6","Flint");

		//do some odd stuff
		pt.put("a","Denver");
		pt.remove("1");

		//sanity check
		String s2=(String)pt.get("2");
		System.out.println(s2); //should be Buffalo

		//view list in key order
		System.out.println("------------------------------");
		System.out.println("key order");
		for (Iterator it=pt.pairs();it.hasNext();) {
			Pair p=(Pair)it.next();
			System.out.println(p.key+"="+p.value);
		}

		System.out.println("------------------------------");
		System.out.println("sort order");

		//iterate unsorted
		Iterator it=pt.selectAll("java.lang.String");
		while (it.hasNext()) {
			Pair p=(Pair)it.next();
			System.out.println(p.key+"="+p.value);
		}

	}
}