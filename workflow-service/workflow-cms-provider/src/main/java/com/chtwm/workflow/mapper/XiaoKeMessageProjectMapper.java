package com.chtwm.workflow.mapper;


import com.chtwm.workflow.entity.TaskNoicePO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author fanyuexing
 * @date 2019/11/8 9:25
 */
public interface XiaoKeMessageProjectMapper {

    //查询所有未发送以及已发送的信息
    List<TaskNoicePO> getAllUnSetMessage();

    //更新纷享逍客的消息发送的结果
    Integer updateThisMessageNoticeState(@Param("id") String id,@Param("noticeState") Integer noticeState);


}
