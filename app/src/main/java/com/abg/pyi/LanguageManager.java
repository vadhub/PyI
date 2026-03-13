package com.abg.pyi;

import android.content.Context;

import com.amrdeveloper.codeview.Code;
import com.amrdeveloper.codeview.CodeView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class LanguageManager {

    public enum LanguageName {
        JAVA,
        PYTHON,
        GO_LANG
    }

    public enum ThemeName {
        MONOKAI,
        NOCTIS_WHITE,
        FIVE_COLOR,
        ORANGE_BOX
    }

    private final Context context;
    private final CodeView codeView;

    public LanguageManager(Context context, CodeView codeView) {
        this.context = context;
        this.codeView = codeView;
    }

    public void applyTheme(LanguageName language, ThemeName theme) {
        switch (theme) {
            case MONOKAI:
                applyMonokaiTheme(language);
                break;
            case NOCTIS_WHITE:
                applyNoctisWhiteTheme(language);
                break;
            case FIVE_COLOR:
                applyFiveColorsDarkTheme(language);
                break;
            case ORANGE_BOX:
                applyOrangeBoxTheme(language);
                break;
        }
    }

    public String[] getLanguageKeywords(LanguageName language) {
        if (Objects.requireNonNull(language) == LanguageName.PYTHON) {
            return PythonLanguage.getKeywords(context);
        }
        return new String[]{};
    }

    public List<Code> getLanguageCodeList(LanguageName language) {
        if (Objects.requireNonNull(language) == LanguageName.PYTHON) {
            return PythonLanguage.getCodeList(context);
        }
        return new ArrayList<>();
    }

    public Set<Character> getLanguageIndentationStarts(LanguageName language) {
        if (Objects.requireNonNull(language) == LanguageName.PYTHON) {
            return PythonLanguage.getIndentationStarts();
        }
        return new HashSet<>();
    }

    public Set<Character> getLanguageIndentationEnds(LanguageName language) {
        if (Objects.requireNonNull(language) == LanguageName.PYTHON) {
            return PythonLanguage.getIndentationEnds();
        }
        return new HashSet<>();
    }

    public String getCommentStart(LanguageName language) {
        if (Objects.requireNonNull(language) == LanguageName.PYTHON) {
            return PythonLanguage.getCommentStart();
        }
        return "";
    }

    public String getCommentEnd(LanguageName language) {
        if (Objects.requireNonNull(language) == LanguageName.PYTHON) {
            return PythonLanguage.getCommentEnd();
        }
        return "";
    }

    private void applyMonokaiTheme(LanguageName language) {
        if (Objects.requireNonNull(language) == LanguageName.PYTHON) {
            PythonLanguage.applyMonokaiTheme(context, codeView);
        }
    }

    private void applyNoctisWhiteTheme(LanguageName language) {
        if (Objects.requireNonNull(language) == LanguageName.PYTHON) {
            PythonLanguage.applyNoctisWhiteTheme(context, codeView);
        }
    }

    private void applyFiveColorsDarkTheme(LanguageName language) {
        if (Objects.requireNonNull(language) == LanguageName.PYTHON) {
            PythonLanguage.applyFiveColorsDarkTheme(context, codeView);
        }
    }

    private void applyOrangeBoxTheme(LanguageName language) {
        if (Objects.requireNonNull(language) == LanguageName.PYTHON) {
            PythonLanguage.applyOrangeBoxTheme(context, codeView);
        }
    }

}
