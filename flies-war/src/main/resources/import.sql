INSERT INTO Account (id, creationDate, lastChanged, apiKey, enabled, passwordHash, username) VALUES (1,'2009-01-14 11:39:00','2009-01-14 11:39:00',NULL,'','Eyox7xbNQ09MkIfRyH+rjg==','admin');
INSERT INTO Account (id, creationDate, lastChanged, apiKey, enabled, passwordHash, username) VALUES (2,'2009-01-14 11:39:00','2009-01-14 11:39:00',NULL,'','/9Se/pfHeUH8FJ4asBD6jQ==','demo');
INSERT INTO Account (id, creationDate, lastChanged, apiKey, enabled, passwordHash, username) VALUES (3,'2009-01-14 11:39:00','2009-01-14 11:39:00',NULL,'','pQRgEKG97HuyCfeoOR69Sg==','bob');
	
INSERT INTO AccountRole (id, conditional, name) VALUES (1,'\0','admin');
INSERT INTO AccountRole (id, conditional, name) VALUES (2,'\0','user');

INSERT INTO AccountMembership (accountId, memberOf) VALUES (1,1);
INSERT INTO AccountMembership (accountId, memberOf) VALUES (2,2);
INSERT INTO AccountMembership (accountId, memberOf) VALUES (3,2);
	
INSERT INTO AccountRoleGroup (roleId, memberOf) VALUES  (1,2);

INSERT INTO Person (id, creationDate, lastChanged, email, name, accountId) VALUES (1,'2009-01-14 11:39:00','2009-01-14 11:39:00','asgeirf@localhost','Administrator',1);
INSERT INTO Person (id, creationDate, lastChanged, email, name, accountId) VALUES (2,'2009-01-14 11:39:00','2009-01-14 11:39:00','asgeirf@localhost','Sample User',2);
INSERT INTO Person (id, creationDate, lastChanged, email, name, accountId) VALUES (3,'2009-01-14 11:39:00','2009-01-14 11:39:00','asgeirf@localhost','Bob Translator',3);
	
INSERT INTO Community (id, creationDate, lastChanged, slug, description, homeContent, name, ownerId) VALUES (1,'2009-01-14 11:39:00','2009-01-14 11:39:00','jboss','best community since sliced bread',NULL,'JBoss.org',1);
INSERT INTO Community (id, creationDate, lastChanged, slug, description, homeContent, name, ownerId) VALUES (2,'2009-01-14 11:39:00','2009-01-14 11:39:00','redhat','best community since sliced bread',NULL,'Red Hat',1);
INSERT INTO Community (id, creationDate, lastChanged, slug, description, homeContent, name, ownerId) VALUES (3,'2009-01-14 11:39:00','2009-01-14 11:39:00','hibernate','best community since sliced bread',NULL,'Hibernate.org',1);
INSERT INTO Community (id, creationDate, lastChanged, slug, description, homeContent, name, ownerId) VALUES (4,'2009-01-14 11:39:00','2009-01-14 11:39:00','seam','best community since sliced bread',NULL,'Seam',1);
	
INSERT INTO FliesLocale (id, icuLocaleId, parentId) VALUES ('as-IN','as_IN',NULL);
INSERT INTO FliesLocale (id, icuLocaleId, parentId) VALUES ('bn-IN','bn_IN',NULL);
INSERT INTO FliesLocale (id, icuLocaleId, parentId) VALUES ('de-DE','de_DE',NULL);
INSERT INTO FliesLocale (id, icuLocaleId, parentId) VALUES ('en-US','en_US',NULL);
INSERT INTO FliesLocale (id, icuLocaleId, parentId) VALUES ('es-ES','es_ES',NULL);
INSERT INTO FliesLocale (id, icuLocaleId, parentId) VALUES ('fr-FR','fr_FR',NULL);
INSERT INTO FliesLocale (id, icuLocaleId, parentId) VALUES ('gu-IN','gu_IN',NULL);
INSERT INTO FliesLocale (id, icuLocaleId, parentId) VALUES ('hi-IN','hi_IN',NULL);
INSERT INTO FliesLocale (id, icuLocaleId, parentId) VALUES ('it-IT','it_IT',NULL);
INSERT INTO FliesLocale (id, icuLocaleId, parentId) VALUES ('ja-JP','ja_JP',NULL);
INSERT INTO FliesLocale (id, icuLocaleId, parentId) VALUES ('kn-IN','kn_IN',NULL);
INSERT INTO FliesLocale (id, icuLocaleId, parentId) VALUES ('ko-KR','ko_KR',NULL);
INSERT INTO FliesLocale (id, icuLocaleId, parentId) VALUES ('ml-IN','ml_IN',NULL);
INSERT INTO FliesLocale (id, icuLocaleId, parentId) VALUES ('mr-IN','mr_IN',NULL);
INSERT INTO FliesLocale (id, icuLocaleId, parentId) VALUES ('or-IN','or_IN',NULL);
INSERT INTO FliesLocale (id, icuLocaleId, parentId) VALUES ('pa-IN','pa_IN',NULL);
INSERT INTO FliesLocale (id, icuLocaleId, parentId) VALUES ('pt-BR','pt_BR',NULL);
INSERT INTO FliesLocale (id, icuLocaleId, parentId) VALUES ('ru-RU','ru_RU',NULL);
INSERT INTO FliesLocale (id, icuLocaleId, parentId) VALUES ('si-LK','si_LK',NULL);
INSERT INTO FliesLocale (id, icuLocaleId, parentId) VALUES ('ta-IN','ta-IN',NULL);
INSERT INTO FliesLocale (id, icuLocaleId, parentId) VALUES ('te-IN','te_IN',NULL);
INSERT INTO FliesLocale (id, icuLocaleId, parentId) VALUES ('zh-CN','zh_CN',NULL);
INSERT INTO FliesLocale (id, icuLocaleId, parentId) VALUES ('zh-TW','zh_TW',NULL);
	
INSERT INTO Tribe (id, creationDate, lastChanged, chiefId, localeId) VALUES (1,'2009-01-14 11:39:00','2009-01-14 11:39:00',3,'as-IN');
INSERT INTO Tribe (id, creationDate, lastChanged, chiefId, localeId) VALUES (2,'2009-01-14 11:39:00','2009-01-14 11:39:00',3,'bn-IN');
INSERT INTO Tribe (id, creationDate, lastChanged, chiefId, localeId) VALUES (3,'2009-01-14 11:39:00','2009-01-14 11:39:00',3,'de-DE');
INSERT INTO Tribe (id, creationDate, lastChanged, chiefId, localeId) VALUES (4,'2009-01-14 11:39:00','2009-01-14 11:39:00',3,'en-US');
INSERT INTO Tribe (id, creationDate, lastChanged, chiefId, localeId) VALUES (5,'2009-01-14 11:39:00','2009-01-14 11:39:00',3,'es-ES');
INSERT INTO Tribe (id, creationDate, lastChanged, chiefId, localeId) VALUES (6,'2009-01-14 11:39:00','2009-01-14 11:39:00',3,'fr-FR');
INSERT INTO Tribe (id, creationDate, lastChanged, chiefId, localeId) VALUES (7,'2009-01-14 11:39:00','2009-01-14 11:39:00',3,'gu-IN');
INSERT INTO Tribe (id, creationDate, lastChanged, chiefId, localeId) VALUES (8,'2009-01-14 11:39:00','2009-01-14 11:39:00',3,'hi-IN');
INSERT INTO Tribe (id, creationDate, lastChanged, chiefId, localeId) VALUES (9,'2009-01-14 11:39:00','2009-01-14 11:39:00',3,'it-IT');
INSERT INTO Tribe (id, creationDate, lastChanged, chiefId, localeId) VALUES (10,'2009-01-14 11:39:00','2009-01-14 11:39:00',3,'ja-JP');
INSERT INTO Tribe (id, creationDate, lastChanged, chiefId, localeId) VALUES (12,'2009-01-14 11:39:00','2009-01-14 11:39:00',3,'ko-KR');
INSERT INTO Tribe (id, creationDate, lastChanged, chiefId, localeId) VALUES (13,'2009-01-14 11:39:00','2009-01-14 11:39:00',3,'ml-IN');
INSERT INTO Tribe (id, creationDate, lastChanged, chiefId, localeId) VALUES (14,'2009-01-14 11:39:00','2009-01-14 11:39:00',3,'mr-IN');
INSERT INTO Tribe (id, creationDate, lastChanged, chiefId, localeId) VALUES (15,'2009-01-14 11:39:00','2009-01-14 11:39:00',3,'or-IN');
INSERT INTO Tribe (id, creationDate, lastChanged, chiefId, localeId) VALUES (16,'2009-01-14 11:39:00','2009-01-14 11:39:00',3,'pa-IN');
INSERT INTO Tribe (id, creationDate, lastChanged, chiefId, localeId) VALUES (17,'2009-01-14 11:39:00','2009-01-14 11:39:00',3,'pt-BR');
INSERT INTO Tribe (id, creationDate, lastChanged, chiefId, localeId) VALUES (18,'2009-01-14 11:39:00','2009-01-14 11:39:00',3,'ru-RU');
INSERT INTO Tribe (id, creationDate, lastChanged, chiefId, localeId) VALUES (19,'2009-01-14 11:39:00','2009-01-14 11:39:00',3,'si-LK');
INSERT INTO Tribe (id, creationDate, lastChanged, chiefId, localeId) VALUES (20,'2009-01-14 11:39:00','2009-01-14 11:39:00',3,'ta-IN');
INSERT INTO Tribe (id, creationDate, lastChanged, chiefId, localeId) VALUES (21,'2009-01-14 11:39:00','2009-01-14 11:39:00',3,'te-IN');
INSERT INTO Tribe (id, creationDate, lastChanged, chiefId, localeId) VALUES (22,'2009-01-14 11:39:00','2009-01-14 11:39:00',3,'zh-CN');
INSERT INTO Tribe (id, creationDate, lastChanged, chiefId, localeId) VALUES (23,'2009-01-14 11:39:00','2009-01-14 11:39:00',3,'zh-TW');
