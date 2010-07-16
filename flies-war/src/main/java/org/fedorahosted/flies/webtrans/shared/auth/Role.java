package org.fedorahosted.flies.webtrans.shared.auth;

public enum Role
{

   Administrator("admin"), User("user"), Anonymous("anon");

   private final String id;

   private Role(String role)
   {
      this.id = role;
   }

   public String getId()
   {
      return id;
   }

}
