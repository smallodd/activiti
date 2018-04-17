package com.hengtian.common.zcode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.hengtian.common.generator.AutoGenerator;
import com.hengtian.common.generator.InjectionConfig;
import com.hengtian.common.generator.config.DataSourceConfig;
import com.hengtian.common.generator.config.GlobalConfig;
import com.hengtian.common.generator.config.PackageConfig;
import com.hengtian.common.generator.config.StrategyConfig;
import com.hengtian.common.generator.config.rules.DbType;
import com.hengtian.common.generator.config.rules.NamingStrategy;

/**
 * <p>
 * 代码生成器
 * </p>
 */
@Deprecated
public class GeneratorCode {
    public static void main(String[] args) {
        /* 获取 JDBC 配置文件 */
        Properties props = getProperties();
        
        AutoGenerator mpg = new AutoGenerator();
        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        gc.setOutputDir("F:/code");//-----------------------------1.配置你生成代码的位置-----------------------------------------------
        
        gc.setFileOverride(true);
        gc.setActiveRecord(true);
        gc.setEnableCache(false);// XML 二级缓存
        gc.setBaseResultMap(false);// XML ResultMap
        gc.setBaseColumnList(false);// XML columList
        gc.setAuthor("junyang.liu");//-----------------------------2.配置你的svn用户名-----------------------------------------------
//         自定义文件命名，注意 %s 会自动填充表实体属性！
        gc.setMapperName("%sDao");
        gc.setXmlName("%sMapper");
        gc.setServiceName("%sService");
        gc.setServiceImplName("%sServiceImpl");
        gc.setControllerName("%sController");
        
        gc.setListName("%s");
        gc.setAddName("%sAdd");
        gc.setEditName("%sEdit");
        
        mpg.setGlobalConfig(gc);
        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setDbType(DbType.MYSQL);
        dsc.setDriverName(props.getProperty("jdbc.driverClassName"));
        dsc.setUsername(props.getProperty("jdbc.username"));
        dsc.setPassword(props.getProperty("jdbc.password"));
        dsc.setUrl(props.getProperty("jdbc.url"));
        mpg.setDataSource(dsc);
        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
//        strategy.setTablePrefix("bmd_");// 此处可以修改为您的表前缀
        strategy.setNaming(NamingStrategy.underline_to_camel);// 表名生成策略
        //多个表的话 strategy.setInclude(new String[] {"sys_oper_log","sys_user"});
        strategy.setInclude(new String[] {"t_mail_log"}); // 需要生成的表//-----------------------------3.配置你要生成的表的表名-----------------------------------------------
        // strategy.setExclude(new String[]{"test"}); // 排除生成的表
        // 字段名生成策略
        strategy.setFieldNaming(NamingStrategy.underline_to_camel);
        // 自定义实体父类
        //strategy.setSuperEntityClass("java.io.Serializable");
        // 自定义实体，公共字段
        // strategy.setSuperEntityColumns(new String[] { "test_id", "age" });
        // 自定义 mapper 父类
        // strategy.setSuperMapperClass("com.baomidou.demo.TestMapper");
        // 自定义 service 父类
        // strategy.setSuperServiceClass("com.baomidou.demo.TestService");
        // 自定义 service 实现类父类
        // strategy.setSuperServiceImplClass("com.baomidou.demo.TestServiceImpl");
        // 自定义 controller 父类
        strategy.setSuperControllerClass("com.hengtian.common.base.BaseController");
        // 【实体】是否生成字段常量（默认 false）
        // public static final String ID = "test_id";
        // strategy.setEntityColumnConstant(true);
        // 【实体】是否为构建者模型（默认 false）
        // public User setName(String name) {this.name = name; return this;}
        // strategy.setEntityBuliderModel(true);
        mpg.setStrategy(strategy);
        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setParent("com.hengtian");
        pc.setModuleName("system");  //所属模块 例如  system系统模块下配置 为  pc.setModuleName("system");//-----------------------------4.配置你生成代码所在的模块-----------------------------------------------
        pc.setController("controller");
        pc.setEntity("model");
        pc.setXml("mapper");
        pc.setMapper("dao");
        
        mpg.setPackageInfo(pc);
        // 注入自定义配置，可以在 VM 中使用 cfg.abc 设置的值
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("abc", this.getConfig().getGlobalConfig().getAuthor());
                this.setMap(map);
            }
        };
        mpg.setCfg(cfg);
        // 自定义模板配置，可以 copy 源码 mybatis-plus/src/main/resources/template 下面内容修改，
        // 放置自己项目的 src/main/resources/template 目录下, 默认名称一下可以不配置，也可以自定义模板名称
//        TemplateConfig tc = new TemplateConfig();
//        tc.setController("D:/generator/controller.java.vm");
//        tc.setEntity("D:/generator/entity.java.vm");
//        tc.setMapper("D:/generator/mapper.java.vm");
//        tc.setXml("/src/main/resources/template/mapper.xml.vm");
//        tc.setService("/src/main/resources/template/service.java.vm");
//        tc.setServiceImpl("/src/main/resources/template/serviceImpl.java.vm");
//        mpg.setTemplate(tc);
        // 执行生成
        mpg.execute();
        // 打印注入设置
        System.err.println(mpg.getCfg().getMap().get("abc"));
    }
    
    /**
     * 获取配置文件
     *
     * @return 配置Props
     */
    private static Properties getProperties() {
        // 读取配置文件
        Resource resource = new ClassPathResource("jdbc.properties");
        Properties props = new Properties();
        try {
            props = PropertiesLoaderUtils.loadProperties(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props;
    }
}