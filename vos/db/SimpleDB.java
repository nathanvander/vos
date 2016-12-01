package vos.db;
import java.io.*;
import java.util.*;
import java.lang.reflect.Field;

/**
* Reads and writes the database to file.
* 	k is the classname, except # means an empty object, and T means a simple string
*
*  This has the following types:
*		int
*		float
*		boolean
*		String
*		Date
*
*/
public class SimpleDB implements DataStore {
	PairTable idb;
	BufferedWriter writer;

	public SimpleDB(String fileName) throws DSX {
		//0. create the internal list
		idb=new PairTable(255);

		//1. create a new log file, if needed
		if (fileName==null) {
			fileName="vos.db";
		}
		File log=new File(fileName);
		if (!log.exists()) {
			try {
				log.createNewFile();
			} catch (Exception x) {
				throw new DSX(x,1);
			}
		}

		//2. open log file for reading
		FileInputStream fis=null;
		try {
			fis=new FileInputStream(log);
		} catch (Exception x) {
			//FileNotFoundException - won't happen
			throw new DSX(x,2);
		}

		//3. read log file
		try {
			readFile(fis);
		} catch (Exception x) {
			//IOException - prob won't happen
			throw new DSX(x,3);
		}

		//4. close file
		try {
			fis.close();
		} catch (Exception x) {
			//IOException - prob won't happen
			throw new DSX(x,4);
		}

		//5. reopen file for writing
		try {
			FileWriter fw=new FileWriter(log,true);  //open in append mode
			writer=new BufferedWriter(fw);
		} catch (Exception x) {
			throw new DSX(x,5);
		}
	}

	//=================================================================================
	private void readFile(FileInputStream fis) throws IOException {
		InputStreamReader isr=new InputStreamReader(fis);
		BufferedReader br=new BufferedReader(isr);
		String line;

		while ( (line=br.readLine()) != null) {
			//remove optional trailing semicolon
			if (line.endsWith(";")) {
				line=line.substring(0,line.length()-1);
			}
			parseLine(line);
		}
	}

	private void parseLine(String line) {
		//1.  first-level split, between key and value
		//split on ->
		String[] sa1=line.split("==");
		if (sa1.length!=2) {
			//just give a warning, don't abort
			System.out.println("WARNING: invalid format (1): "+line);
			return;  //skip to the next line
		}

		//int key = Integer.parseInt(sa1[0]);
		String key = sa1[0];
		String[] tuples=extractTuples(sa1[1]);
		if (tuples==null) {
			return;  //silently fail, warning already displayed
		}

		Object o=parseObject(tuples,true);
		Comparable c=(Comparable)o;

		//now add it to the list
		idb.store(key,c);
	}

	private static String[] extractTuples(String s2) {
		if (s2.startsWith("{") && s2.endsWith("}")) {
			s2=s2.substring(1,s2.length()-1);
		} else {
			System.out.println("WARNING: invalid format (2): "+s2);
			return null;
		}

		//2. second-level split, between fields
		//System.out.println("DEBUG s2: "+s2);
		String[] tuples=s2.split("\\|");
		//System.out.println("DEBUG tuples.length: "+tuples.length);
		return tuples;
	}

	private static Object parseObject(String[] tuples,boolean fromFile) {
		Object o=null;
		Class k=null;

		//3. third-level split
		for (int i=0;i<tuples.length;i++) {
			//System.out.println("DEBUG tuples: "+tuples[i]);
			String[] sa4=tuples[i].split("=");
			if (sa4.length!=2) {
				System.out.println("WARNING: invalid format (3) '"+tuples[i]+"'");
			}
			String field=sa4[0];
			String value=sa4[1];
			if (value.startsWith("\"") && value.endsWith("\"")) {
				value=value.substring(1,value.length()-1);
			}

			//the first value must be timestamp
			if (fromFile) {
				if (i==0) {
					//we actually don't use this but need to enforce the presence
					if (!field.equals("ts")) {
						System.out.println("WARNING: invalid format (4): "+tuples[i]+", expecting ts");
						return null;
					}
					continue;
				}
			}

			//the second value must be class
			if ( (fromFile&&(i==1)) || (i==0)) {
				if (field.equals("k")) {
					//create a new empty object of the given class
					try {
						//special case for String
						if (value.equals("T")) {
							//don't do anything for now
							continue;
						}

						//special case for object
						if(value.equals("#")) {
							value="vos.db.DeletedObject";
						}
						k=Class.forName(value);
						o=k.newInstance();
					} catch (Exception x) {
						System.out.println("WARNING: "+x.getMessage());
						x.printStackTrace();
						return null;
					}
				} else {
					System.out.println("WARNING: invalid format (5): "+tuples[i]+", expecting k");
					return null;
				}
				continue;
			}

			//else try to set the value for the given field
			try {
				Field f=null;
				//special case where field="t"
				if (field.equals("t")) {
					return value;
				}

				try {
					f=k.getField(field);
				} catch (Exception x) {
					System.out.println("WARNING: no such field "+field);
					continue;
				}
				Class type=f.getType();
				String ft=type.getName();

				//only works with a few types
				if (ft.equals("java.lang.String")) {
					f.set(o,value);
				} else if (ft.equals("int")) {
					int iv=Integer.parseInt(value);
					f.setInt(o,iv);
				} else if (ft.equals("boolean")) {
					boolean bv=Boolean.parseBoolean(value);
					f.setBoolean(o,bv);
				} else if (ft.equals("float")) {
					float fv=Float.parseFloat(value);
					f.setFloat(o,fv);
				} else if (ft.equals("java.util.Date")) {
					f.set(o,new java.util.Date(value));
				} else {
					//unknown type
					System.out.println("WARNING: unimplemented type "+ft);
					return null;
				}
			} catch (Exception x) {
				System.out.println("WARNING: "+x.getMessage());
				x.printStackTrace();
				return null;
			}
		}
		return o;
	}

	//===========================================================================
	//public methods that satisfy the interface

	public String insert(Comparable o) throws DSX {
		if (o==null) {
			return null;
		}

		//insert it
		String key=idb.insert(o);

		//add to log file
		try {
			log(key,o);
		} catch (IOException x) {
			throw new DSX(x,6);
		}

		//return key
		return key;
	}

	//-------------------------------------------------------------------
	private void log(String key,Object value) throws IOException {
		writer.write(key+"==");
		writer.write("{ts="+System.currentTimeMillis()+"|");
		writer.write(stringify(value)+"};");
		writer.newLine();
		writer.flush();
	}

	//convert an object to a String.  Without surrounding brackets
	//k (class) is always first
	private String stringify(Object o) {
		if (o==null) {
			return "k=#";
		}

		Class klass=o.getClass();
		String k=klass.getName();

		//----------------------------------
		//special case where o is a blank object
		if (k.equals("vos.db.DeletedObject")) {
			return "k=#";
		}

		//--------------------------------------
		//special case where o is a String
		if (k.equals("java.lang.String")) {
			return "k=T|t=\""+o+"\"";
		}

		StringBuffer sb=new StringBuffer();
		sb.append("k="+k);

		Field[] fa=klass.getFields();
		for (int i=0;i<fa.length;i++) {
			Field f=fa[i];
			String ft=f.getType().getName();
			try {
			if (ft.equals("java.lang.String")) {
				String val=(String)f.get(o);
				if (val!=null && val.length()>0 ) {
					sb.append("|"+f.getName()+"=\""+val+"\"");
				}
			} else if (ft.equals("int")) {
				int iv=f.getInt(o);
				sb.append("|"+f.getName()+"="+iv);
			} else if (ft.equals("boolean")) {
				//System.out.println("storing boolean value for "+f.getName());
				boolean bv=f.getBoolean(o);
				sb.append("|"+		f.getName()+"="+bv);
			} else if (ft.equals("float")) {
				float fv=f.getFloat(o);
				sb.append("|"+f.getName()+"="+fv);
			} else {
				System.out.println("WARNING: unimplemented type "+ft);
			}
			} catch (Exception x) {
				System.out.println("WARNING: "+x.getMessage());
			}
		}
		return sb.toString();
	}
	//---------------------------------------------------------

	public void update(String key,Comparable o) throws DSX {
		if (o==null) {return;}
		idb.update(key,o);

		//add to log file
		try {
			log(key,o);
		} catch (IOException x) {
			throw new DSX(x,6);
		}
	}

	public void delete(String key) throws DSX {
		idb.delete(key);
		//add to log file
		try {
			//logging a deleted object is the same as a delete
			log(key,new vos.db.DeletedObject());
		} catch (IOException x) {
			throw new DSX(x,6);
		}
	}

	public Iterator selectAll(String className) {
		return idb.selectAll(className);
	}

	//no need to log this
	public Object get(String key) {
		return idb.get(key);
	}

}