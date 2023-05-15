package me.panxin.plugin.idea.pojocheck.inspection;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.*;
import me.panxin.plugin.idea.pojocheck.util.POJO;
import org.jetbrains.annotations.NotNull;

/**
 * @author PanXin
 * @version $ Id: ToStringMethodInspection, v 0.1 2023/05/14 22:00 PanXin Exp $
 */
public class ToStringMethodInspection extends LocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new PsiElementVisitor() {
            @Override
            public void visitFile(@NotNull PsiFile file) {
                if (file instanceof PsiJavaFile) {
                    PsiJavaFile javaFile = (PsiJavaFile) file;
                    PsiClass[] classes = javaFile.getClasses();
                    for (PsiClass psiClass : classes) {
                        String className = psiClass.getName();
                        // 判断文件是否是POJO
                        PsiFile containingFile = psiClass.getContainingFile();
                        PsiDirectory containingDirectory = containingFile.getContainingDirectory();
                        String directoryName = containingDirectory.getName();
                        if (POJO.isPOJO(directoryName, className)) {
                            PsiMethod[] methods = psiClass.getMethods();
                            boolean hasToStringMethod = false;
                            for (PsiMethod method : methods) {
                                if ("toString".equals(method.getName())) {
                                    hasToStringMethod = true;
                                    break;
                                }
                            }
                            if (!hasToStringMethod) {
                                holder.registerProblem(psiClass.getNameIdentifier(), "没有重写toString()方法！");
                            }
                        }
                    }
                }
            }
        };
    }
}
