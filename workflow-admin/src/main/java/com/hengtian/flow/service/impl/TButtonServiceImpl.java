package com.hengtian.flow.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.hengtian.flow.dao.TButtonDao;
import com.hengtian.flow.dao.TTaskButtonDao;
import com.hengtian.flow.model.TButton;
import com.hengtian.flow.model.TTaskButton;
import com.hengtian.flow.service.TButtonService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by ma on 2018/5/8.
 */
@Service
public class TButtonServiceImpl extends ServiceImpl<TButtonDao, TButton> implements TButtonService {

    @Autowired
    private TButtonDao tButtonDao;
    @Autowired
    private TTaskButtonDao tTaskButtonDao;

    /**
     * 获取流程配置的权限按钮
     * @param procDefKey 流程定于key
     * @return
     * @author houjinrong@chtwm.com
     * date 2018/5/9 14:25
     */
    @Override
    public JSONObject getConfigButton(String procDefKey){
        EntityWrapper<TTaskButton> wrapper = new EntityWrapper<>();
        wrapper.where("proc_def_key={0}", procDefKey);
        List<TTaskButton> tTaskButtons = tTaskButtonDao.selectList(wrapper);
        if(CollectionUtils.isNotEmpty(tTaskButtons)){
            EntityWrapper<TButton> wrapper1 = new EntityWrapper<>();
            wrapper.where("status={0}",1);
            List<TButton> tButtons = tButtonDao.selectList(wrapper1);

            Map<String, String> map = Maps.newHashMap();
            for(TButton b : tButtons){
                map.put(b.getButtonKey(), b.getName());
            }

            JSONObject result = new JSONObject();
            for(TTaskButton  tb : tTaskButtons){
                if(result.containsKey(tb.getTaskDefKey())){
                    result.getJSONObject(tb.getTaskDefKey()).put("buttonKey", (result.getJSONObject(tb.getTaskDefKey()).getString("buttonKey")+","+tb.getButtonKey()));
                    result.getJSONObject(tb.getTaskDefKey()).put("buttonName", (result.getJSONObject(tb.getTaskDefKey()).getString("buttonName")+","+map.get(tb.getButtonKey())));
                }else {
                    JSONObject buttonJson = new JSONObject();

                    buttonJson.put("buttonKey", tb.getButtonKey());
                    buttonJson.put("buttonName", map.get(tb.getButtonKey()));

                    result.put(tb.getTaskDefKey(),buttonJson);
                }
            }

            return result;
        }

        return null;
    }
}
