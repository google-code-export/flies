package org.zanata.service;

import java.util.List;

import org.zanata.exception.ZanataServiceException;
import org.zanata.model.HLocale;


public interface LanguageTeamService
{
   final static int MAX_NUMBER_MEMBERSHIP = Integer.MAX_VALUE;

   List<HLocale> getLanguageMemberships(String userName);

   boolean joinLanguageTeam(String locale, Long personId) throws ZanataServiceException;

   boolean leaveLanguageTeam(String locale, Long personId);
}
