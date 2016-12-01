package vos.db;
/**
* This will be used for all exceptions that prevent data from being stored or retrieved as expected.
* These are the error codes:
*	1 = can't create database file
*	2 = can't open database file
*	3 = error reading database file
*	4 = can't close database file
*	5 = can't reopen file for logging
*
*/
public class DSX extends Exception {
	int errcode;

	public DSX(Throwable cause,int errcode) {
			super(cause);
			this.errcode=errcode;
	}

	public int getErrCode() {
		return errcode;
	}

	public String toString() {
		return getCause().getClass().getName()+": "+getMessage()+" (error code: "+errcode+")";
	}
}