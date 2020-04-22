package com.chtwm.workflow.mapper;


import com.chtwm.workflow.entity.TaskModelVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author fanyuexing
 * @date 2019/11/8 9:25
 */
@Mapper
public interface XiaoKeMessageProjectMapper {

    /**
     * 获取所有纷享销客待发送和发送失败的消息
     * @return
     */
    List<TaskModelVO> getAllUnSendMessage();

    /**
     * 更新纷享逍客的消息发送的结果
     * @param id 主键
     * @param noticeState  待修改的状态
     * @return
     */
    Integer updateThisMessageNoticeState(@Param("id") Long id,@Param("noticeState") Integer noticeState);

    /**
     * 根据主键集合更新纷享逍客的消息发送的结果
     * @param ids 主键集合
     * @param noticeState 待修改的状态
     * @return
     */
    Integer updateNoticeStateByListKeys(@Param("ids") List<Long> ids,@Param("noticeState") Integer noticeState);

}
