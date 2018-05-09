package com.hengtian.flow.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.service.IService;
import com.hengtian.flow.model.TButton;

/**
 * Created by ma on 2018/5/8.
 */
public interface TButtonService  extends IService<TButton> {

    /**
     * 获取流程配置的权限按钮
     * @param procDefKey 流程定于key
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/5/9 14:25
     */
    JSONObject getConfigButton(String procDefKey);
}
