package top.aexp.mybatishelper.ui;


import lombok.extern.slf4j.Slf4j;
import top.aexp.mybatishelper.core.data.DataSourceHolder;
import top.aexp.mybatishelper.core.model.*;
import top.aexp.mybatishelper.core.start.MyBatisHelperStarter;
import top.aexp.mybatishelper.core.utils.CollectionUtils;
import top.aexp.mybatishelper.core.utils.StringUtils;
import top.aexp.mybatishelper.ui.view.BaseConfigForm;
import top.aexp.mybatishelper.ui.view.BuildConfigTable;
import top.aexp.mybatishelper.ui.view.ModelConfigTable;

import javax.swing.*;
import javax.swing.plaf.InsetsUIResource;
import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author LILONGTAO
 */
@Slf4j
public class MainSwing {

    public JPanel initCenter() {
         JPanel center = new JPanel();

        //定义表单的主体部分，放置到IDEA会话框的中央位置

        //一个简单的3行2列的表格布局
        GridBagLayout gridLayout = new GridBagLayout();
        GridBagConstraints leftGrid = new GridBagConstraints();
        GridBagConstraints rightGrid = new GridBagConstraints();
        GridBagConstraints topGrid = new GridBagConstraints();
        GridBagConstraints midGrid = new GridBagConstraints();
        GridBagConstraints bottomGrid = new GridBagConstraints();
        GridBagConstraints submitGrid = new GridBagConstraints();
        leftGrid.gridx = 0;
        leftGrid.gridy = 0;
        leftGrid.weightx = 0.5;
        leftGrid.weighty = 0.5;
        leftGrid.gridwidth = 1;
        leftGrid.gridheight = 2;
        leftGrid.insets = new InsetsUIResource(0, 0, 5, 5);

        topGrid.gridx = 1;
        topGrid.gridy = 0;
        topGrid.weightx = 0.5;
        topGrid.weighty = 0.5;
        topGrid.gridwidth = 1;
        topGrid.gridheight = 1;
        topGrid.insets = new InsetsUIResource(10, 5, 0, 0);

        rightGrid.gridx = 1;
        rightGrid.gridy = 1;
        rightGrid.weightx = 0.5;
        rightGrid.weighty = 0.5;
        rightGrid.gridwidth = 1;
        rightGrid.gridheight = 1;
        rightGrid.insets = new InsetsUIResource(0, 5, 5, 0);

        midGrid.gridx = 0;
        midGrid.gridy = 3;
        midGrid.weightx = 0.5;
        midGrid.weighty = 0.5;
        midGrid.gridwidth = 2;
        midGrid.gridheight = 1;
        midGrid.insets = new InsetsUIResource(5, 0, 0, 0);


        bottomGrid.gridx = 0;
        bottomGrid.gridy = 4;
        bottomGrid.weightx = 1;
        bottomGrid.weighty = 2;
        bottomGrid.gridwidth = 2;
        bottomGrid.gridheight = 1;
        bottomGrid.insets = new InsetsUIResource(0, 30, 0, 30);

        submitGrid.gridx = 0;
        submitGrid.gridy = 5;
        submitGrid.weightx = 1;
        submitGrid.weighty = 1;
        submitGrid.gridwidth = 2;
        submitGrid.gridheight = 1;
        submitGrid.insets = new InsetsUIResource(0, 0, 10, 0);
        center.setLayout(gridLayout);

        JPanel left = new BaseConfigForm().getForm();

        left.setMinimumSize(new Dimension(200, 150));
        left.setPreferredSize(new Dimension(200, 150));
        center.add(left, leftGrid);



        JScrollPane right = new JScrollPane(new ModelConfigTable().getModelConfigTable());
        right.setMinimumSize(new Dimension(550, 150));
        right.setPreferredSize(new Dimension(550, 150));
        JLabel commonTitle = new JLabel("公共属性配置");
        //commonTitle.setFont(new Font("微软雅黑", Font.PLAIN, 20));
        commonTitle.setHorizontalAlignment(SwingConstants.CENTER);
        center.add(commonTitle,topGrid);
        center.add(right, rightGrid);

        JLabel genTitle = new JLabel("生成器配置");
        //genTitle.setFont(new Font("微软雅黑", Font.PLAIN, 20));
        genTitle.setHorizontalAlignment(SwingConstants.CENTER);
        center.add(genTitle,midGrid);

        JScrollPane jScrollPane = new JScrollPane(new BuildConfigTable().getBuildConfigTable());
        jScrollPane.setMinimumSize(new Dimension(750, 200));
        jScrollPane.setPreferredSize(new Dimension(750, 200));
        center.add(jScrollPane, bottomGrid);
        center.add(submitButton(),submitGrid);
        return center;
    }

    public JButton submitButton() {

        //定义表单的提交按钮，放置到IDEA会话框的底部位置

        JButton submit = new JButton("提交");
        //水平居中
        submit.setHorizontalAlignment(SwingConstants.CENTER);
        //垂直居中
        submit.setVerticalAlignment(SwingConstants.CENTER);

        //按钮事件绑定
        submit.addActionListener(e -> {
            submit.setEnabled(false);
            try {
                Config config = ConfigDataHolder.getData();
                if (Objects.equals(config.getDropTable(),true)&& Objects.equals(config.getUseDb(),true)) {

                    int i = JOptionPane.showConfirmDialog(null, "重建表会清除所有数据,确认执行?");
                    if (0 != i) {
                        submit.setEnabled(true);
                        return;
                    }
                }

                checkStartConfigAndConfigDataSource(config);
                BuildResult run = MyBatisHelperStarter.db(config.getDbType()).run(config);
                List<Log> logs = run.getLogs();
                StringBuilder stringBuilder = new StringBuilder();
                logs.forEach(item->stringBuilder.append(item).append("\r\n"));

                if (run.isSucceed()) {
                    if (run.getTotal() == 0) {
                        JOptionPane.showMessageDialog(null,"未找到可生成的实体类,请在实体类javaDoc注释增加.auto标识");
                    }else {
                        JOptionPane.showMessageDialog(null,"已生成"+run.getTotal()+"条记录,请查看指定目录下base文件夹\r\n\n"+stringBuilder);
                    }
                }else {
                    log.warn("生成异常", run.getE());
                    JOptionPane.showMessageDialog(null,run.getE().getMessage()+"\r\n"+stringBuilder);
                }

            } catch (Exception ex) {
                log.warn("生成异常", ex);
                JOptionPane.showMessageDialog(null,ex.getMessage());
            }
            DataSourceHolder.clear();
            submit.setEnabled(true);

            log.info("SUCCESS");

        });
        return submit;
    }

    private void checkStartConfigAndConfigDataSource(Config config) {
        boolean useDb = Objects.equals(config.getUseDb(), true);
        List<BuildConfig> buildConfigList = config.getBuildConfigList();
        List<EntityField> baseEntityFieldList = config.getBaseEntityFieldList();
        if (CollectionUtils.isEmpty(buildConfigList)) {
            throw new IllegalArgumentException("配置信息不能为空");
        }
        boolean useBase = false;
        for (BuildConfig buildConfig1 : buildConfigList) {
            if (StringUtils.isEmpty(buildConfig1.getMapperFolder()) ||
                    StringUtils.isEmpty(buildConfig1.getEntityFolder()) ||
                    StringUtils.isEmpty(buildConfig1.getXmlFolder())
            ) {
                throw new IllegalArgumentException("配置文件夹信息不能为空");
            }
            if (!Objects.equals(buildConfig1.getIgnoreBaseField(),true)) {
                useBase = true;
            }
        }
        if (useBase) {
            for (EntityField entityField : baseEntityFieldList) {
                if (StringUtils.isEmpty(entityField.getColumnName())) {
                    throw new IllegalArgumentException("基类属性列名不能为空");
                }
                if (StringUtils.isEmpty(entityField.getType())) {
                    throw new IllegalArgumentException("基类属性java类型不能为空");
                }
            }
        }


        if (useDb) {
            if (StringUtils.isEmpty(config.getBaseDbUrl()) ||
                    StringUtils.isEmpty(config.getBaseDbPassword()) ||
                    StringUtils.isEmpty(config.getBaseDbUsername()) ||
                    StringUtils.isEmpty(config.getDbType())
            ) {
                throw new IllegalArgumentException("若生成表结构则数据库信息必填");
            }
            boolean matches = Pattern.matches("^(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5]):([0-9]|[1-9]\\d{1,3}|[1-5]\\d{4}|6[0-5]{2}[0-3][0-5])$"
                    , config.getBaseDbUrl());
            if (!matches) {
                throw new RuntimeException("数据库地址应以 ip:port 的形式填写");
            }

        }
    }
}
