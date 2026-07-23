package br.com.nischor.ledgerxbackend.shared.infrastructure.validation;

final class BrazilianDocumentValidation {

    private static final int[] CNPJ_FIRST_DIGIT_WEIGHTS = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] CNPJ_SECOND_DIGIT_WEIGHTS = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    private BrazilianDocumentValidation() {
    }

    static boolean isValidCpf(String value) {
        var digits = value.replaceAll("\\D", "");
        if (digits.length() != 11 || digits.chars().distinct().count() == 1) {
            return false;
        }

        int firstCheckDigit = checkDigit(digits.substring(0, 9), 10);
        int secondCheckDigit = checkDigit(digits.substring(0, 9) + firstCheckDigit, 11);

        return digits.equals(digits.substring(0, 9) + firstCheckDigit + secondCheckDigit);
    }

    static boolean isValidCnpj(String value) {
        var digits = value.replaceAll("\\D", "");
        if (digits.length() != 14 || digits.chars().distinct().count() == 1) {
            return false;
        }

        int firstCheckDigit = weightedCheckDigit(digits.substring(0, 12), CNPJ_FIRST_DIGIT_WEIGHTS);
        int secondCheckDigit = weightedCheckDigit(digits.substring(0, 12) + firstCheckDigit,
                CNPJ_SECOND_DIGIT_WEIGHTS);

        return digits.equals(digits.substring(0, 12) + firstCheckDigit + secondCheckDigit);
    }

    private static int checkDigit(String base, int startingWeight) {
        int sum = 0;
        int weight = startingWeight;
        for (int i = 0; i < base.length(); i++) {
            sum += Character.digit(base.charAt(i), 10) * weight--;
        }
        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }

    private static int weightedCheckDigit(String base, int[] weights) {
        int sum = 0;
        for (int i = 0; i < weights.length; i++) {
            sum += Character.digit(base.charAt(i), 10) * weights[i];
        }
        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }
}
