package com.hengtian.flow.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hengtian.common.param.WorkDetailParam;
import com.hengtian.common.utils.PageInfo;
import com.hengtian.common.utils.StringUtils;
import com.hengtian.flow.dao.TWorkDetailDao;
import com.hengtian.flow.model.TWorkDetail;
import com.hengtian.flow.service.TWorkDetailService;
import com.user.entity.emp.Emp;
import com.user.service.emp.EmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author chenzhangyan  on 2018/4/24.
 */
@Service
public class TWorkDetailServiceImpl extends ServiceImpl<TWorkDetailDao, TWorkDetail> implements TWorkDetailService {
    @Autowired
    private TWorkDetailDao tWorkDetailDao;
    @Autowired
    EmpService empService;

    /**
     * 操作流程详细信息
     *
     * @param processInstanceId 流程实例
     * @param operator 操作人
     * @return
     */
    @Override
    public List<TWorkDetail> operateDetailInfo(String processInstanceId, String operator,String businessKey) {
        EntityWrapper<TWorkDetail> wrapper = new EntityWrapper();
        if(StringUtils.isNotBlank(processInstanceId)){
         wrapper.where("proc_inst_id={0}", processInstanceId);

        }
        if(StringUtils.isNotBlank(operator)){
            wrapper.and("operator={0}", operator);
        }
        if(StringUtils.isNotBlank(businessKey)){
            wrapper.where("business_key={0}",businessKey);
        }
        List<TWorkDetail> list=tWorkDetailDao.selectList(wrapper);
        for(TWorkDetail tWorkDetail: list){
            Emp emp=empService.selectByCode(tWorkDetail.getOperator());
            if(emp!=null){
                tWorkDetail.setOperator(emp.getName());
            }
        }
        return list;
    }
}
