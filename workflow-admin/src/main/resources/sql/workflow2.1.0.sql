ALTER TABLE t_user_task add user_count_total TINYINT(4) DEFAULT null COMMENT '任务节点总的审批人数';
ALTER TABLE t_user_task add user_count_need TINYINT(4) DEFAULT null COMMENT '任务节点需要的审批人数';
update sys_user_role set user_id='admin' where user_id='1';
update sys_user set id='admin' where id='1';