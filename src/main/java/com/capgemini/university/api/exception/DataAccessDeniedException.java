/**
 * 
 */
package com.capgemini.university.api.exception;

/**
 * @author zhihuang
 *
 */
public class DataAccessDeniedException extends RuntimeException {
    //~ Constructors ===================================================================================================

    /**
     * Constructs an <code>AccessDeniedException</code> with the specified
     * message.
     *
     * @param msg the detail message
     */
    public DataAccessDeniedException(String msg) {
        super(msg);
    }

    /**
     * Constructs an <code>AccessDeniedException</code> with the specified
     * message and root cause.
     *
     * @param msg the detail message
     * @param t root cause
     */
    public DataAccessDeniedException(String msg, Throwable t) {
        super(msg, t);
    }
}