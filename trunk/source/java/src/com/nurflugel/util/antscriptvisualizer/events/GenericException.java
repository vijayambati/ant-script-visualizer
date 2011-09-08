/*
 * Created by IntelliJ IDEA. User: douglasbullard Date: Dec 14, 2004 Time: 7:30:36 PM
 */
package com.nurflugel.util.antscriptvisualizer.events;

/** Copyright 2005, Nurflugel.com. */
public class GenericException extends Exception
{
  /** Use serialVersionUID for interoperability. */
  private static final long serialVersionUID = -4742666104116163474L;
  private Exception         exception;

  /** Creates a new GenericException object. */
  public GenericException(Exception exception)
  {
    this.exception = exception;
  }

  /** Creates a new GenericException object. */
  public GenericException(String s, Exception exception)
  {
    super(s);
    this.exception = exception;
  }

  /** Creates a new GenericException object. */
  public GenericException(Throwable throwable, Exception exception)
  {
    super(throwable);
    this.exception = exception;
  }

  /** Creates a new GenericException object. */
  public GenericException(String s, Throwable throwable, Exception exception)
  {
    super(s, throwable);
    this.exception = exception;
  }
  // --------------------- GETTER / SETTER METHODS ---------------------

  /**  */
  public Exception getException()
  {
    return exception;
  }
}
