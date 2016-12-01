package vos.db;
/**
* Used to mark an object as deleted
*/

public class DeletedObject implements Comparable {
	public int compareTo(Object o) {
		return 1;
	}
}