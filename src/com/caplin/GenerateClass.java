package com.caplin;

import com.caplin.util.DocEditor;
import com.caplin.util.FileUtil;
import com.caplin.util.Runner;
import com.caplin.util.Template;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: stephens
 * Date: 11/05/13
 * Time: 10:45
 * To change this template use File | Settings | File Templates.
 */
public class GenerateClass extends CaplinAction {

    public void actionPerformed(final AnActionEvent e) {
        Runner.runWriteCommand(e.getProject(), new Runnable() {
            public void run() {
                writeClass(e);
            }
        });
    }

    private void writeClass(AnActionEvent e) {
        HashMap data = new HashMap();
        data.put("fullClass", FileUtil.getFullClass(FileUtil.getVirtualFile(e)));
        writeTemplate(e, "class.ftl", data);
    }

}

