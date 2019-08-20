CREATE TABLE `Profile` (
  `user` varchar(20) NOT NULL,
  `user_clean` varchar(20) NOT NULL,
  `pass` varchar(150) NOT NULL,
  `lastIp` varchar(15) NOT NULL,
  `lastSeen` bigint(11) NOT NULL DEFAULT '0',
  `rights` smallint(3) NOT NULL DEFAULT '0',
  PRIMARY KEY (`user_clean`),
  UNIQUE KEY `user_clean` (`user_clean`) USING HASH,
  KEY `user` (`user`) USING BTREE,
  KEY `lastSeen` (`lastSeen`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `Profile` (user, user_clean, pass, lastIp, lastSeen, rights) VALUES ('admin', 'admin', 'admin', '0.0.0.0', 0, 2);