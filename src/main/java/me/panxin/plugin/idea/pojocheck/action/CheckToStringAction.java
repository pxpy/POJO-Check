package me.panxin.plugin.idea.pojocheck.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import me.panxin.plugin.idea.pojocheck.util.POJO;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CheckToStringAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        if (project != null) {
            PsiManager psiManager = PsiManager.getInstance(project);
            GlobalSearchScope searchScope = GlobalSearchScope.projectScope(project);
            PsiPackage rootPackage = JavaPsiFacade.getInstance(psiManager.getProject()).findPackage("");

            List<PsiClass> classesToCheck = new ArrayList<>();
            processPackage(rootPackage, searchScope, classesToCheck);

            List<PsiClass> classesWithoutToString = new ArrayList<>();
            for (PsiClass psiClass : classesToCheck) {
                boolean hasToString = false;

                // 判断文件是否是POJO
                PsiFile containingFile = psiClass.getContainingFile();
                PsiDirectory containingDirectory = containingFile.getContainingDirectory();
                String directoryName = containingDirectory.getName();
                if (POJO.isPOJO(directoryName, psiClass.getName())) {
                    PsiMethod[] methods = psiClass.getMethods();
                    for (PsiMethod method : methods) {
                        if ("toString".equals(method.getName()) && method.getParameterList().isEmpty()) {
                            hasToString = true;
                            break;
                        }
                    }

                    if (!hasToString) {
                        classesWithoutToString.add(psiClass);
                    }
                }
            }

            if (classesWithoutToString.isEmpty()) {
                Messages.showInfoMessage("所有类都已重写了 toString 方法！", "检查结果");
            } else {
                int choice = Messages.showYesNoDialog(project, "以下类未重写 toString 方法:\n\n" +
                                getClassListAsString(classesWithoutToString) +
                                "\n\n是否跳转到类进行修改？\n\n选择“否”将生成报告。",
                        "检查结果", "跳转修改", "生成报告", Messages.getQuestionIcon());
                if (choice == Messages.YES) {
                    // 跳转到类进行修改
                    navigateToClasses(classesWithoutToString);
                } else if (choice == Messages.NO) {
                    // 生成报告
                    generateReport(classesWithoutToString,project);
                }
            }
        }
    }

    private void processPackage(PsiPackage psiPackage, GlobalSearchScope searchScope, List<PsiClass> classesToCheck) {
        for (PsiClass psiClass : psiPackage.getClasses()) {
            classesToCheck.add(psiClass);
        }

        for (PsiPackage subPackage : psiPackage.getSubPackages(searchScope)) {
            processPackage(subPackage, searchScope, classesToCheck);
        }
    }

    private String getClassListAsString(List<PsiClass> classes) {
        StringBuilder sb = new StringBuilder();
        for (PsiClass psiClass : classes) {
            sb.append(psiClass.getQualifiedName()).append("\n");
        }
        return sb.toString();
    }

    private void navigateToClasses(List<PsiClass> classes) {
        for (PsiClass psiClass : classes) {
            psiClass.navigate(true);
        }
    }

    private void generateReport(List<PsiClass> classes, Project project) {
        StringBuilder sb = new StringBuilder();
        sb.append("以下类未重写 toString 方法:\n\n");
        sb.append(getClassListAsString(classes));

        // 将报告输出到控制台
        System.out.println(sb.toString());
        // 输出报告到文件
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(project.getBasePath() + "/POJO-check-summary.txt"))) {
            writer.write(sb.toString());
            Messages.showInfoMessage("报告已生成：" + project.getBasePath() + "/POJO-check-summary.txt", "生成报告");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
