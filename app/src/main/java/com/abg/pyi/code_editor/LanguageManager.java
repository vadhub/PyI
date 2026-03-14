package com.abg.pyi.code_editor;

import android.content.Context;

import com.amrdeveloper.codeview.Code;
import com.amrdeveloper.codeview.CodeView;

import java.util.List;
import java.util.Set;

public class LanguageManager {
    public enum ThemeName {
        MONOKAI, NOCTIS_WHITE, FIVE_COLOR, ORANGE_BOX
    }

    private final Context context;
    private final CodeView codeView;

    public LanguageManager(Context context, CodeView codeView) {
        this.context = context;
        this.codeView = codeView;
    }

    public void applyTheme(ThemeName theme) {
        switch (theme) {
            case MONOKAI:
                applyMonokaiTheme();
                break;
            case NOCTIS_WHITE:
                applyNoctisWhiteTheme();
                break;
            case FIVE_COLOR:
                applyFiveColorsDarkTheme();
                break;
            case ORANGE_BOX:
                applyOrangeBoxTheme();
                break;
        }
    }

    public String[] getLanguageKeywords() {
        return PythonLanguage.getKeywords(context);

    }

    public List<Code> getLanguageCodeList() {

        return PythonLanguage.getCodeList(context);
    }

    public Set<Character> getLanguageIndentationStarts() {
        return PythonLanguage.getIndentationStarts();

    }

    public Set<Character> getLanguageIndentationEnds() {
        return PythonLanguage.getIndentationEnds();

    }

    public String getCommentStart() {
        return PythonLanguage.getCommentStart();

    }

    public String getCommentEnd() {
        return PythonLanguage.getCommentEnd();
    }

    private void applyMonokaiTheme() {
        PythonLanguage.applyMonokaiTheme(context, codeView);
    }

    private void applyNoctisWhiteTheme() {
        PythonLanguage.applyNoctisWhiteTheme(context, codeView);

    }

    private void applyFiveColorsDarkTheme() {
        PythonLanguage.applyFiveColorsDarkTheme(context, codeView);
    }

    private void applyOrangeBoxTheme() {
        PythonLanguage.applyOrangeBoxTheme(context, codeView);
    }

}
