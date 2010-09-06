package net.openl10n.flies.rest.service;

import net.openl10n.flies.FliesJpaTest;
import net.openl10n.flies.common.ContentType;
import net.openl10n.flies.common.LocaleId;
import net.openl10n.flies.common.ResourceType;
import net.openl10n.flies.model.HDocument;
import net.openl10n.flies.rest.StringSet;
import net.openl10n.flies.rest.dto.extensions.PoHeader;
import net.openl10n.flies.rest.dto.extensions.SimpleComment;
import net.openl10n.flies.rest.dto.po.HeaderEntry;
import net.openl10n.flies.rest.dto.resource.ResourceMeta;

import org.testng.annotations.Test;

public class ResourceUtilsJpaTest extends FliesJpaTest
{
   private static ResourceUtils resourceUtils = new ResourceUtils();

   @Test
   void transferFromResourceMetadata()
   {
      ResourceMeta from = new ResourceMeta("resId");
      from.setContentType(ContentType.TextPlain);
      PoHeader poHeader = new PoHeader();
      poHeader.setComment("comment");
      poHeader.getEntries().add(new HeaderEntry("key", "value"));
      from.getExtensions(true).add(poHeader);
      from.setLang(LocaleId.ES);
      from.setName("name");
      from.setType(ResourceType.FILE);

      HDocument to = new HDocument("fullPath", ContentType.PO);

      StringSet commentExt = new StringSet(SimpleComment.ID);
      resourceUtils.transferFromResourceMetadata(from, to, commentExt);
      // TODO check the results in 'to'
   }
}
