-- 将旧的行政区地点统一为校园公寓地点格式
-- 映射：东城区 -> 1号公寓，西城区 -> 2号公寓，朝阳区 -> 3号公寓，
-- 海淀区 -> 4号公寓，丰台区 -> 5号公寓，通州区 -> 6号公寓

START TRANSACTION;

UPDATE animal
SET area = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(area,
    '东城区', '1号公寓'), '西城区', '2号公寓'), '朝阳区', '3号公寓'),
    '海淀区', '4号公寓'), '丰台区', '5号公寓'), '通州区', '6号公寓')
WHERE area LIKE '%东城区%' OR area LIKE '%西城区%' OR area LIKE '%朝阳区%'
   OR area LIKE '%海淀区%' OR area LIKE '%丰台区%' OR area LIKE '%通州区%';

UPDATE rescue_clue
SET location = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(location,
    '东城区', '1号公寓'), '西城区', '2号公寓'), '朝阳区', '3号公寓'),
    '海淀区', '4号公寓'), '丰台区', '5号公寓'), '通州区', '6号公寓')
WHERE location LIKE '%东城区%' OR location LIKE '%西城区%' OR location LIKE '%朝阳区%'
   OR location LIKE '%海淀区%' OR location LIKE '%丰台区%' OR location LIKE '%通州区%';

UPDATE rescue_order
SET title = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(title,
    '东城区', '1号公寓'), '西城区', '2号公寓'), '朝阳区', '3号公寓'),
    '海淀区', '4号公寓'), '丰台区', '5号公寓'), '通州区', '6号公寓'),
    description = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(description,
    '东城区', '1号公寓'), '西城区', '2号公寓'), '朝阳区', '3号公寓'),
    '海淀区', '4号公寓'), '丰台区', '5号公寓'), '通州区', '6号公寓'),
    location = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(location,
    '东城区', '1号公寓'), '西城区', '2号公寓'), '朝阳区', '3号公寓'),
    '海淀区', '4号公寓'), '丰台区', '5号公寓'), '通州区', '6号公寓')
WHERE title LIKE '%区%' OR description LIKE '%区%' OR location LIKE '%区%';

UPDATE volunteer_task
SET title = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(title,
    '东城区', '1号公寓'), '西城区', '2号公寓'), '朝阳区', '3号公寓'),
    '海淀区', '4号公寓'), '丰台区', '5号公寓'), '通州区', '6号公寓'),
    description = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(description,
    '东城区', '1号公寓'), '西城区', '2号公寓'), '朝阳区', '3号公寓'),
    '海淀区', '4号公寓'), '丰台区', '5号公寓'), '通州区', '6号公寓'),
    location = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(location,
    '东城区', '1号公寓'), '西城区', '2号公寓'), '朝阳区', '3号公寓'),
    '海淀区', '4号公寓'), '丰台区', '5号公寓'), '通州区', '6号公寓')
WHERE title LIKE '%区%' OR description LIKE '%区%' OR location LIKE '%区%';

UPDATE adoption_application
SET address = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(address,
    '东城区', '1号公寓'), '西城区', '2号公寓'), '朝阳区', '3号公寓'),
    '海淀区', '4号公寓'), '丰台区', '5号公寓'), '通州区', '6号公寓')
WHERE address LIKE '%东城区%' OR address LIKE '%西城区%' OR address LIKE '%朝阳区%'
   OR address LIKE '%海淀区%' OR address LIKE '%丰台区%' OR address LIKE '%通州区%';

UPDATE animal_health_record
SET hospital = '校医院'
WHERE hospital IN ('社区合作宠物医院', '安心宠物医院');

-- 将旧快照中的道路、社区和公园点位改为校园内具体点位
UPDATE rescue_clue
SET location = REPLACE(REPLACE(REPLACE(REPLACE(location,
    '1号公寓和平路小区门口', '1号公寓南门'),
    '4号公寓学院路公交站', '4号公寓北侧'),
    '3号公寓公园北门', '3号公寓北门'),
    '2号公寓社区投喂点', '2号公寓楼下投喂点');

UPDATE rescue_order
SET title = REPLACE(title, '公交站流浪犬救助', '4号公寓流浪犬救助'),
    description = REPLACE(description, '根据线索前往公交站处理流浪犬。', '根据线索前往4号公寓北侧处理流浪犬。'),
    location = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(location,
    '4号公寓学院路公交站', '4号公寓北侧'),
    '3号公寓河边桥下', '3号公寓东侧花坛'),
    '1号公寓和平路小区门口', '1号公寓南门'),
    '2号公寓社区投喂点', '2号公寓楼下投喂点'),
    '1号公寓和平路小区门口', '1号公寓南门');

UPDATE volunteer_task
SET title = REPLACE(title, '临时救助协助任务', '3号公寓临时救助协助任务'),
    location = REPLACE(REPLACE(REPLACE(REPLACE(location,
    '1号公寓和平路投喂点', '1号公寓楼下投喂点'),
    '2号公寓社区猫屋', '2号公寓楼下猫屋'),
    '3号公寓公园北门', '3号公寓北门'),
    '3号公寓公园北门', '3号公寓北门');

UPDATE adoption_application
SET address = REPLACE(address, '1号公寓和平路 1 号', '1号公寓');

COMMIT;
