package vos.db;
import java.util.*;

/** This is the public interface to the database.
*  Keys are in base-15, just to be different.
*/
public interface DataStore {
	//insert an object into the datastore and return the key
	public String insert(Comparable c) throws DSX;

	public void update(String key,Comparable o) throws DSX;

	public void delete(String key) throws DSX;

	public Object get(String key);

	//iterates over sorted Pair items
	public Iterator selectAll(String tableName);
}