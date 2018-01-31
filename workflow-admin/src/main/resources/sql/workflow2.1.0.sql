ALTER TABLE t_user_task add user_count_total TINYINT(4) DEFAULT null COMMENT '任务节点总的审批人数';
ALTER TABLE t_user_task add user_count_need TINYINT(4) DEFAULT null COMMENT '任务节点需要的审批人数';