delete from `ad_platform`;
alter table `ad_platform` AUTO_INCREMENT = 1;
INSERT INTO `ad_platform`(`id`, `name`, `app_id`, `api_version`, `sort`, `adslot_ids`, `url`, `deleted`, `creator`, `create_time`) VALUES (10000, '翼猫平台', '11', '1.0.0', 1, '', NULL, 0, '系统', NOW());
INSERT INTO `ad_platform`(`id`, `name`, `app_id`, `api_version`, `sort`, `adslot_ids`, `url`, `deleted`, `creator`, `create_time`) VALUES (1, '百度聚屏', 'J3DeDHl', '1.0.0', 2, 'JQMOfoKzd,JnPJYJxfD,JC2l5Mzx3,JL715Rhxw', 'http://jpad.baidu.com/api_6/', 0, '系统', NOW());
INSERT INTO `ad_platform`(`id`, `name`, `app_id`, `api_version`, `sort`, `adslot_ids`, `url`, `deleted`, `creator`, `create_time`) VALUES (2, '京东钼媒', '8931', '1.0.0', 3, '1111', 'https://api.kuaifa.tv/', 0, '系统', NOW());
INSERT INTO `ad_platform`(`id`, `name`, `app_id`, `api_version`, `sort`, `adslot_ids`, `url`, `deleted`, `creator`, `create_time`) VALUES (4, '简视', '8ab9ed22', '1.0.0', 4, '1111', 'http://app.betvis.cn/', 0, '系统', NOW());
update `ad_platform` set id=0 where id = 10000;
alter table `ad_platform` AUTO_INCREMENT = 5;