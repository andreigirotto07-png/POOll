package com.locadora.app.util;

import java.util.regex.Pattern;

public class ValidationUtils {

    // -----------------------------------------------------------------------
    //  REGEX
    // -----------------------------------------------------------------------
    private static final Pattern EMAIL =
            Pattern.compile("^[\\w._%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern TELEFONE =
            Pattern.compile("^\\(?\\d{2}\\)? ?9?\\d{4}-?\\d{4}$");

    // placas no padrão Mercosul e antigo
    private static final Pattern PLACA =
            Pattern.compile("^[A-Z]{3}\\d[A-Z]\\d{2}$|^[A-Z]{3}-?\\d{4}$");

    // moeda brasileira (12.500,90 ou 12500.90 ou 12500)
    private static final Pattern MOEDA =
            Pattern.compile("^\\d{1,3}(\\.\\d{3})*(,\\d{2})?$|^\\d+(\\.\\d{2})?$");

    // -----------------------------------------------------------------------
    //  MÉTODOS PÚBLICOS
    // -----------------------------------------------------------------------

    public static boolean emailValido(String s) {
        return s != null && EMAIL.matcher(s).matches();
    }

    public static boolean telefoneValido(String s) {
        return s != null && TELEFONE.matcher(s).matches();
    }

    public static boolean placaValida(String s) {
        if (s == null) return false;
        s = s.replace("-", "").toUpperCase();
        return PLACA.matcher(s).matches();
    }

    public static boolean moedaValida(String s) {
        return s != null && MOEDA.matcher(s).matches();
    }

}
