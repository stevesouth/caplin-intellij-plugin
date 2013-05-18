package com.caplin.util;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import org.intellij.idea.lang.javascript.psiutil.JSElementFactory;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: stephens
 * Date: 15/05/13
 * Time: 18:04
 * To change this template use File | Settings | File Templates.
 */
public class FileUtil {
    public static boolean isCaplinApp(VirtualFile virtualFile) {
        VirtualFile parent = virtualFile.getParent();

        boolean hasSrc = false;
        boolean hasApps = false;

        while (parent != null) {
            if (parent.getName().equals("src")) hasSrc = true;
            if (parent.getName().equals("apps")) hasApps = true;

            if (hasSrc && hasApps) {
                return true;
            }

            parent = parent.getParent();
        }

        return false;
    }

    public static boolean isCaplinApp(AnActionEvent event) {
        return isCaplinApp(getVirtualFile(event));
    }

    public static String getNameSpace(VirtualFile virtualFile) {
        String namespace = "";
        VirtualFile parent = virtualFile.getParent();

        while (parent != null && !parent.getName().equals("src")) {
            namespace = parent.getName() + "." + namespace;
            parent = parent.getParent();
        }

        return namespace;
    }

    public static VirtualFile getVirtualFile(AnActionEvent e) {
        return e.getData(PlatformDataKeys.VIRTUAL_FILE);
    }

    public static String getFullClass(VirtualFile file) {
        return getNameSpace(file) + file.getNameWithoutExtension();
    }

    public static String getFullClass(AnActionEvent event) {
        return getFullClass(getVirtualFile(event));
    }

    /**
     * Returns the end index of the constructor if one is found. If no constuctor is found then 0 is returned.
     * @param text The JavaScript to scan
     * @return
     */
    public static int getConstructorEndOffsetFromText(String text) {
        int brackets = 0;
        boolean started = false;
        boolean ignoringComments = false;

        for (int i = 0; i < text.length(); i++){
            char character = text.charAt(i);
            if (!ignoringComments && character == '{') {
                brackets++;
                started = true;
            } else if (!ignoringComments && character == '}') {
                brackets--;
                if (started) {
                    if (brackets == 0) {
                        if (i+1 < text.length() && text.charAt(i+1) == ';') {
                            return i+2;
                        } else {
                            return i+1;
                        }
                    }
                }
            }  else if (character == '/' && text.charAt(i+1) == '*') {
                i = i + 1;
                ignoringComments = true;
            } else if (character == '*' && text.charAt(i+1) == '/') {
                i = i + 1;
                ignoringComments = false;
            }
        }
        return 0;
    }

    protected static int getConstructorEndOffsetFromEvent(AnActionEvent e) {
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        String text = editor.getDocument().getText();
        return getConstructorEndOffsetFromText(text);
    }

    public static PsiElement getConstructorFromPsiFile(PsiElement file) {
        PsiElement[] list = file.getChildren();

        for (int i = 0, l = list.length; i < l; i++) {
            if (list[i].getText().indexOf("function") != -1) {
                return list[i];
            }
        }

        if (list.length > 0) {
            return list[0];
        } else {
            return null;
        }

    }

    public static ASTNode createASTNodeFromText(AnActionEvent e, String code) {
        return JSElementFactory.createElementFromText(e.getProject(), code);
    }

    public static VirtualFile getApplicationRoot(VirtualFile file) {
       VirtualFile parent = file.getParent();
       while (parent != null) {
            if (parent.findChild("sdk") != null && parent.findChild("apps") != null) {
                return parent;
            }
            parent = parent.getParent();
       };
       return null;
    }

    static boolean isInterface(VirtualFile file) {
        String contents = null;
        Boolean isInterface = false;
        try {
            contents = new String(file.contentsToByteArray());
            if (contents.indexOf("@interface") != -1) {
                isInterface = true;
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return isInterface;
    }

    static boolean isJSFile(VirtualFile child) {
        return child.getExtension() != null && child.getExtension().equals("js");
    }
}