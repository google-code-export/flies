package org.fedorahosted.flies.rest;

public class NoSuchEntityException extends RuntimeException
{

   public NoSuchEntityException()
   {
   }

   public NoSuchEntityException(String message)
   {
      super(message);
   }

}
