package me.panxin.plugin.idea.pojocheck.util;

/**
 * @author PanXin
 * @version $ Id: POJO, v 0.1 2023/05/15 11:24 PanXin Exp $
 */
public class POJO {

    public static boolean isPOJO(String directoryName, String className) {
        // 判断文件是否在 modal、vo、dto 包路径下
        if(directoryName.equals("model") || directoryName.equals("vo") || directoryName.equals("po") || directoryName.equals("dto")){
            return true;
        }
        if (className != null && (className.endsWith("VO") || className.endsWith("DTO") || className.endsWith("PO"))){
            return true;
        }
        return false;

    }
}